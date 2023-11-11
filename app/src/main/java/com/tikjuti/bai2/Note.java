package com.tikjuti.bai2;



public class Note {
    private int id;
    private String title;
    private String content;
    private byte[] imagePath;
    private String dateTime;

    public Note(int id, String title, String content, byte[] imagePath, String dateTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.dateTime = dateTime;
    }

    public Note() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getImagePath() {
        return imagePath;
    }

    public void setImagePath(byte[] imagePath) {
        this.imagePath = imagePath;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
