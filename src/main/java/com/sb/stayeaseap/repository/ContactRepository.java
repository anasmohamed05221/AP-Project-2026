package com.sb.stayeaseap.repository;

import com.sb.stayeaseap.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {
}