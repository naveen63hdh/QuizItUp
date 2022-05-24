package com.example.quizitup.quiz;

public class ParticipantModel {

    String name,url;

    public ParticipantModel(String name, String url) {

        this.name = name;
        this.url = url;
    }


    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
