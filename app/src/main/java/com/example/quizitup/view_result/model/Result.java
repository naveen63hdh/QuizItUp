package com.example.quizitup.view_result.model;

public class Result {
    int qno;
    String QType,question,op1,op2,op3,op4,op5,op6,ans,exp,choice;

    public Result(int qno, String QType, String question, String op1, String op2, String op3, String op4, String op5, String op6, String ans, String exp, String choice) {
        this.qno = qno;
        this.QType = QType;
        this.question = question;
        this.op1 = op1;
        this.op2 = op2;
        this.op3 = op3;
        this.op4 = op4;
        this.op5 = op5;
        this.op6 = op6;
        this.ans = ans;
        this.exp = exp;
        this.choice = choice;
    }

    public int getQno() {
        return qno;
    }

    public void setQno(int qno) {
        this.qno = qno;
    }

    public String getQType() {
        return QType;
    }

    public void setQType(String QType) {
        this.QType = QType;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOp1() {
        return op1;
    }

    public void setOp1(String op1) {
        this.op1 = op1;
    }

    public String getOp2() {
        return op2;
    }

    public void setOp2(String op2) {
        this.op2 = op2;
    }

    public String getOp3() {
        return op3;
    }

    public void setOp3(String op3) {
        this.op3 = op3;
    }

    public String getOp4() {
        return op4;
    }

    public void setOp4(String op4) {
        this.op4 = op4;
    }

    public String getOp5() {
        return op5;
    }

    public void setOp5(String op5) {
        this.op5 = op5;
    }

    public String getOp6() {
        return op6;
    }

    public void setOp6(String op6) {
        this.op6 = op6;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    @Override
    public String toString() {
        return "Result{" +
                "qno=" + qno +
                ", QType='" + QType + '\'' +
                ", question='" + question + '\'' +
                ", op1='" + op1 + '\'' +
                ", op2='" + op2 + '\'' +
                ", op3='" + op3 + '\'' +
                ", op4='" + op4 + '\'' +
                ", op5='" + op5 + '\'' +
                ", op6='" + op6 + '\'' +
                ", ans='" + ans + '\'' +
                ", exp='" + exp + '\'' +
                ", choice='" + choice + '\'' +
                '}'+"\n"+"\n";
    }
}
