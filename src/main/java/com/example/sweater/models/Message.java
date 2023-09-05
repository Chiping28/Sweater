package com.example.sweater.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "text")
    private String text;

    @Column(name = "tag")
    private String tag;

    @Column(name = "file")
    private String file;

    @ManyToOne
    @JoinColumn(name = "person_id", columnDefinition = "id")
    private Person owner;

    @ManyToMany
    @JoinTable(name = "message_likes",
            joinColumns = {@JoinColumn(name = "message_id")},
            inverseJoinColumns = {@JoinColumn(name = "person_id")}
    )
    private Set<Person> likes = new HashSet<>();

    public Message(String text, String tag, String file, Person owner) {
        this.text = text;
        this.tag = tag;
        this.file = file;
        this.owner = owner;
    }

    public Message() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Set<Person> getLikes() {
        return likes;
    }

    public void setLikes(Set<Person> likes) {
        this.likes = likes;
    }
}
