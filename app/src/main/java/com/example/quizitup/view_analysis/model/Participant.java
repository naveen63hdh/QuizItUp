package com.example.quizitup.view_analysis.model;

import java.util.ArrayList;

public class Participant {
    String uid, name;
    ArrayList<String> choice;

    public Participant(String uid, String name, ArrayList<String> choice) {
        this.uid = uid;
        this.name = name;
        this.choice = choice;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getChoice() {
        return choice;
    }

    public void setChoice(ArrayList<String> choice) {
        this.choice = choice;
    }
}
