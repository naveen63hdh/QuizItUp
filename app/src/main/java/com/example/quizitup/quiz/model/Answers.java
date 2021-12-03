package com.example.quizitup.quiz.model;

public class Answers {
    String qno, answers;

    public Answers(String qno, String answers) {
        this.qno = qno;
        this.answers = answers;
    }

    public String getQno() {
        return qno;
    }

    public void setQno(String qno) {
        this.qno = qno;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }
}
