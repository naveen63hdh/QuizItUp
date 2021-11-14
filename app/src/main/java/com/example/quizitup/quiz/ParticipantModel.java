package com.example.quizitup.quiz;

public class ParticipantModel {
    int sno;
    String name,url;

    ParticipantModel(int sno,String name, String url) {
        this.sno = sno;
        this.name = name;
        this.url = url;
    }

    public int getSno() {
        return sno;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
