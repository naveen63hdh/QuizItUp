package com.example.quizitup.quiz.model;

import java.util.ArrayList;

public class ParticipantsAns {
    String name,email,uid,score;
    ArrayList<Answers> answersList;

    public ParticipantsAns(String name, String email, String uid, String score, ArrayList<Answers> answersList) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.score = score;
        this.answersList = answersList;
    }

    @Override
    public String toString() {
        return "ParticipantsAns{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", uid='" + uid + '\'' +
                ", score='" + score + '\'' +
                ", answersList=" + answersList +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public ArrayList<Answers> getAnswersList() {
        return answersList;
    }

    public void setAnswersList(ArrayList<Answers> answersList) {
        this.answersList = answersList;
    }
}
