package com.example.contact_management.contacts.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.contact_management.contacts.models.Contact;
import com.example.contact_management.auth.models.User;


@Repository
public interface ContactRepository extends JpaRepository<Contact,Long>{
   
    Page<Contact> findByUser(User user,Pageable pageable);
    Page<Contact> findByUserAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrTitleContainingIgnoreCase(
            User user, String firstName, String lastName, String title, Pageable pageable);



}
