package com.example.quizitup.classroom.Models;

public class Classroom {

    private String key;
    private String name;
    private String createdBy, creator, hint, desc;
    private boolean isOpen;

    public Classroom(String key, String name, String creator) {
        this.key = key;
        this.name = name;
        this.creator = creator;
    }

    public Classroom(String key, String name, String createdBy, String hint, String desc, boolean isOpen) {
        this.key = key;
        this.name = name;
        this.createdBy = createdBy;
        this.creator = creator;
        this.hint = hint;
        this.desc = desc;
        this.isOpen = isOpen;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
