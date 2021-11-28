package com.example.quizitup.view_analysis.model;

import com.example.quizitup.question.QuestionModel;

public class Analysis {
    double p1,p2,p3,p4,p5,p6,pnone;
    QuestionModel questionModel;

    public Analysis(double p1, double p2, double p3, double p4, double p5, double p6, double pnone, QuestionModel questionModel) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        this.p5 = p5;
        this.p6 = p6;
        this.pnone = pnone;
        this.questionModel = questionModel;
    }

    public double getP1() {
        return p1;
    }

    public void setP1(double p1) {
        this.p1 = p1;
    }

    public double getP2() {
        return p2;
    }

    public void setP2(double p2) {
        this.p2 = p2;
    }

    public double getP3() {
        return p3;
    }

    public void setP3(double p3) {
        this.p3 = p3;
    }

    public double getP4() {
        return p4;
    }

    public void setP4(double p4) {
        this.p4 = p4;
    }

    public double getP5() {
        return p5;
    }

    public void setP5(double p5) {
        this.p5 = p5;
    }

    public double getP6() {
        return p6;
    }

    public void setP6(double p6) {
        this.p6 = p6;
    }

    public double getPnone() {
        return pnone;
    }

    public void setPnone(double pnone) {
        this.pnone = pnone;
    }

    public QuestionModel getQuestionModel() {
        return questionModel;
    }

    public void setQuestionModel(QuestionModel questionModel) {
        this.questionModel = questionModel;
    }
}
