package com.example.wanderearth;

public class UserDataType {
    private String fullName, email, gender, memberType, password;

    public UserDataType(String fullName, String email, String gender, String memberType, String password) {
        this.fullName = fullName;
        this.email = email;
        this.gender = gender;
        this.memberType = memberType;
        this.password = password;
    }

    public UserDataType() {
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getMemberType() {
        return memberType;
    }

    public String getPassword() {
        return password;
    }


}
