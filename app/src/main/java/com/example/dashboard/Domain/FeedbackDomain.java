package com.example.dashboard.Domain;

public class FeedbackDomain {
    private String name;
    private String nickname;
    private String feedback;
    private String pic;

    public FeedbackDomain() {
    }

    public FeedbackDomain(String name, String nickname, String feedback, String pic) {
        this.name = name;
        this.nickname = nickname;
        this.feedback = feedback;
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
