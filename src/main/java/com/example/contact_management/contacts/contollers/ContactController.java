package com.example.contact_management.contacts.contollers;

import com.example.contact_management.contacts.dto.UpdateContactDTO;
import com.example.contact_management.contacts.models.EmailAddress;
import com.example.contact_management.contacts.models.PhoneNumber;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.property.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.contact_management.auth.models.User;
import com.example.contact_management.contacts.dto.ContactResponseDTO;
import com.example.contact_management.contacts.dto.CreateContactDTO;
import com.example.contact_management.contacts.dto.PaginatedContactListDTO;
import com.example.contact_management.contacts.services.ContactService;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private static final Logger log = LoggerFactory.getLogger(ContactController.class);
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("")
    public ResponseEntity<ContactResponseDTO> createContact(@RequestBody CreateContactDTO createContactDTO, @AuthenticationPrincipal User user) {
        ContactResponseDTO createdContact = contactService.createContact(createContactDTO, user);
        return new ResponseEntity<>(createdContact, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponseDTO> getContactById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        ContactResponseDTO cDto = contactService.getContactById(id, user);
        return new ResponseEntity<>(cDto, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<PaginatedContactListDTO> getContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        PaginatedContactListDTO pDto = contactService.getPaginatedContacts(page, size, user);
        return new ResponseEntity<>(pDto, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactResponseDTO> updateContactById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody UpdateContactDTO updateContactDTO) {

        ContactResponseDTO contactResponseDTO = contactService.updateContactById(id,user,updateContactDTO);
        return new ResponseEntity<>(contactResponseDTO,HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteContactById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        contactService.deleteContactById(id,user);
        return new ResponseEntity<>("Contact deleted successfully",HttpStatus.OK);

    }
    @GetMapping("/search")
    public PaginatedContactListDTO searchContacts(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String query,
            @AuthenticationPrincipal User user
    ) {
        return contactService.search(page, size, query, user);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportContacts(@AuthenticationPrincipal User user){



        try {
            PaginatedContactListDTO paginatedContactListDTO = contactService.getPaginatedContacts(0,Integer.MAX_VALUE,user);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            for (ContactResponseDTO contact : paginatedContactListDTO.contacts()) {
                VCard vCard = new VCard();
                vCard.addFormattedName(new FormattedName(contact.firstName() + " " + contact.lastName()));
                vCard.addProperty(new Title(contact.title()));
                for (EmailAddress emailAddress : contact.emailAddresses()) {
                    vCard.addEmail(emailAddress.getEmail());
                }
                for (PhoneNumber phoneNumber : contact.phoneNumbers()) {
                    vCard.addTelephoneNumber(phoneNumber.getNumber());
                }

                Ezvcard.write(vCard).go(outputStream);
            }

            byte[] outputData = outputStream.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contacts.vcf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(outputData.length)
                    .contentType(MediaType.parseMediaType("text/vcard"))
                    .body(outputData);
        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/import")
    public ResponseEntity<String> importContacts(@RequestBody MultipartFile file, @AuthenticationPrincipal User user){
        try {
            List<VCard> vCardsList = Ezvcard.parse(file.getInputStream()).all();
            for (VCard vCard:vCardsList){

                FormattedName name = vCard.getFormattedName();
                String firstName = "";
                String lastName = "";
                String title = "";
                if (name!= null){
                    String[] arr = name.getValue().split("[.\\s]+");
                    firstName = arr[0];
                    lastName = arr[1];
                }
                if (!vCard.getTitles().isEmpty()){
                    title = vCard.getTitles().getFirst().getValue();
                }

                List<Telephone> telephoneList = vCard.getTelephoneNumbers();
                List<PhoneNumber> numbers = new ArrayList<>();
                if (!telephoneList.isEmpty()) {

                    for (Telephone telephone : telephoneList) {
                        String type = telephone.getParameter("TYPE");
                        String number = telephone.getText();

                        PhoneNumber phoneNumber = new PhoneNumber();
                        phoneNumber.setNumber(number);
                        phoneNumber.setLabel(type);
                        numbers.add(phoneNumber);
                    }
                }



                List<Email> emailList = vCard.getEmails();
                List<EmailAddress> emails = new ArrayList<>();
                if (!emailList.isEmpty()) {
                    emails = getEmail(emailList);
                }

                CreateContactDTO createContactDTO = new CreateContactDTO(
                        firstName,
                        lastName,
                        title,
                        emails,
                        numbers
                );

                contactService.createContact(createContactDTO,user);
            }

            return new ResponseEntity<>("Imported successfully",HttpStatus.OK);

        }
        catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private List<EmailAddress> getEmail(List<Email> emailList){
        List<EmailAddress> emails = new ArrayList<>();
        for (Email email : emailList) {
            String type = email.getParameter("TYPE");
            String emailAddress = email.getValue();

            EmailAddress emailAddress1 = new EmailAddress();

            emailAddress1.setEmail(emailAddress);
            emailAddress1.setLabel(type);

            emails.add(emailAddress1);

        }
        return emails;
    }



}
