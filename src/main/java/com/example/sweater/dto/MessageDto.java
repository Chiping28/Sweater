package com.example.sweater.dto;

import com.example.sweater.models.Message;
import com.example.sweater.models.Person;

public class MessageDto {
    private int id;

    private String text;
    private String tag;
    private String file;

    private Person owner;
    private long likes;
    private boolean meLiked;

    public MessageDto(Message message, long likes, boolean meLiked) {
        this.id = message.getId();
        this.text = message.getText();
        this.tag = message.getTag();
        this.file = message.getFile();
        this.owner = message.getOwner();
        this.likes = likes;
        this.meLiked = meLiked;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getTag() {
        return tag;
    }

    public String getFile() {
        return file;
    }

    public Person getOwner() {
        return owner;
    }

    public long getLikes() {
        return likes;
    }

    public boolean isMeLiked() {
        return meLiked;
    }
}
