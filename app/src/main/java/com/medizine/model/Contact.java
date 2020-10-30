package com.medizine.model;

import java.util.HashSet;
import java.util.Set;

public class Contact {
    private String name;
    private Set<String> phones;
    private Set<String> emails;

    public Contact() {
        emails = new HashSet<>();
        phones = new HashSet<>();
        name = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getPhones() {
        return phones;
    }

    public void setPhones(Set<String> phones) {
        this.phones = phones;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }

    public void addEmail(String email) {
        emails.add(email);
    }

    public void addPhone(String phone) {
        phones.add(phone);
    }
}
