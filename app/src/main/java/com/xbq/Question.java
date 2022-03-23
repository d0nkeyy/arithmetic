package com.xbq;
//题目
public class Question {
    private String detail; // 题目详情，包含完整的算式
    private String RightAnswer;//该题的正确答案

    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getRightAnswer() {
        return RightAnswer;
    }
    public void setRightAnswer(String answer) {
        RightAnswer = answer;
    }
}
