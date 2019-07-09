package com.theapp.zeathon;

public class User {
    private int id;
    private String email, firstName, secondName, gender, maritalstatus;

    public User(int id, String email, String firstName, String secondName, String gender, String maritalstatus) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.secondName = secondName;
        this.gender = gender;
        this.maritalstatus = maritalstatus;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getGender() {
        return gender;
    }

    public String getMaritalstatus() {
        return maritalstatus;
    }
}
