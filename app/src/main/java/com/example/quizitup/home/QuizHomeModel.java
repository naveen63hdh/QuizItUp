package com.example.quizitup.home;

public class QuizHomeModel {
    String quizName, quizCode, startTime, endTime, qDate, qStatus,mode;

    QuizHomeModel(String mode,String quizName,String quizCode, String startTime, String endTime, String qDate,String qStatus) {
        this.quizName = quizName;
        this.quizCode = quizCode;
        this.startTime = startTime;
        this.mode = mode;
        this.endTime = endTime;
        this.qDate = qDate;
        this.qStatus = qStatus;
    }

    public String getMode() {
        return mode;
    }

    public String getQuizCode() {
        return quizCode;
    }

    public String getQuizName() {
        return quizName;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getqDate() {
        return qDate;
    }

    public String getqStatus() {
        return qStatus;
    }
}
