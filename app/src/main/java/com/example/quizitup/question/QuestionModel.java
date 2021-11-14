package com.example.quizitup.question;

public class QuestionModel {
    Double qno,marks;
    String QType,question,op1,op2,op3,op4,op5,op6,ans,exp,shuffle;

    QuestionModel(Double qno,String QType,String question,String op1,String op2,String op3,String op4,String op5,String op6,String ans,String exp,String shuffle,Double marks) {
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
        this.shuffle = shuffle;
        this.marks = marks;
    }

    public Double getMarks() {
        return marks;
    }

    public Double getQno() {
        return qno;
    }

    public String getQType() {
        return QType;
    }

    public String getQuestion() {
        return question;
    }

    public String getOp1() {
        return op1;
    }

    public String getOp2() {
        return op2;
    }

    public String getOp3() {
        return op3;
    }

    public String getOp4() {
        return op4;
    }

    public String getOp5() {
        return op5;
    }

    public String getOp6() {
        return op6;
    }

    public String getAns() {
        return ans;
    }

    public String getExp() {
        return exp;
    }

    public String getShuffle() {
        return shuffle;
    }
}

