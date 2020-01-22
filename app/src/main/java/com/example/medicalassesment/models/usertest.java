package com.example.medicalassesment.models;

import java.util.List;

public class usertest {
    private  List<Data> data;

    public usertest(List<Data> data) {
        this.data = data;
    }

    public List<Data> getData() {
        return data;
    }

    public static class Data {
        private final int id;

        private final String firstname;

        private final String surname;

        private final String email;

        public Data(int id, String firstname, String surname, String email) {
            this.id = id;
            this.firstname = firstname;
            this.surname = surname;
            this.email = email;
        }

        public int getId() {
            return id;
        }

        public String getFirstname() {
            return firstname;
        }

        public String getSurname() {
            return surname;
        }

        public String getEmail() {
            return email;
        }
    }
}
