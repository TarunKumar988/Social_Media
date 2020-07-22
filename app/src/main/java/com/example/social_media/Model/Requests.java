package com.example.social_media.Model;

public class Requests {
    private Boolean Request;
    private String id;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRequest() {
        return Request;
    }

    public void setRequest(Boolean request) {
        Request = request;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
