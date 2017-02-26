package com.catandroidfirebaseapp.model;

import android.nfc.cardemulation.CardEmulation;

/**
 * Created by mac on 26.02.17.
 */

public class Cat {

    private String name;

    private long age;

    private String breed;

    private String imgName;

    private String image;

    private String uid;

    public Cat(){

    }

    public Cat(String name, long age, String breed, String imgName, String image, String uid) {
        this.name = name;
        this.age = age;
        this.breed = breed;
        this.imgName = imgName;
        this.image = image;
        this.uid = uid;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
