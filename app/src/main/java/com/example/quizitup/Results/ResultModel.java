package com.example.quizitup.Results;

public class ResultModel {
    String quiz;
    Double score;

    public ResultModel(String quiz, Double score) {
        this.quiz = quiz;
        this.score = score;
    }

    public String getQuiz() {
        return quiz;
    }

    public void setQuiz(String quiz) {
        this.quiz = quiz;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}

