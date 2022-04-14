package com.example.quizitup.home;

public class QuizHomeModel {
    String quizName, quizCode, startTime, endTime, qDate, qStatus;
    boolean isStudent;
    int statusCode;

    public QuizHomeModel(boolean isStudent,String quizName,String quizCode, String startTime, String endTime, String qDate,String qStatus,int statusCode) {
        this.quizName = quizName;
        this.quizCode = quizCode;
        this.startTime = startTime;
        this.isStudent = isStudent;
        this.endTime = endTime;
        this.qDate = qDate;
        this.qStatus = qStatus;
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isStudent() {
        return isStudent;
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
