package com.example.exercise3;

import android.graphics.Bitmap;

public class FileSystemModel {
    private String title;
    private String path;
    private boolean isFolder;
    private Bitmap albumImage;
    private String albumName;

    public FileSystemModel() {};

    public FileSystemModel(String title, String path, boolean isFolder, Bitmap albumImage, String albumName) {
        this.title = title;
        this.path = path;
        this.isFolder = isFolder;
        this.albumImage = albumImage;
        this.albumName = albumName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
    }

    public Bitmap getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(Bitmap albumImage) {
        this.albumImage = albumImage;
    }
    public String getAlbumName() {
        return albumName;
    }
    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
}
