package com.pengjinfei.bean;

/**
 * Author: EX-PENGJINFEI001
 * Date: 2016-03-15
 * Description:
 */
public class Child {
    private String id;
    private String name;
    private String sex;
    private String birthday;
    private String height;
    private String dipearDay;
    private String location;
    private String dispearLocation;
    private String discreption;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getDipearDay() {
        return dipearDay;
    }

    public void setDipearDay(String dipearDay) {
        this.dipearDay = dipearDay;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDispearLocation() {
        return dispearLocation;
    }

    public void setDispearLocation(String dispearLocation) {
        this.dispearLocation = dispearLocation;
    }

    public String getDiscreption() {
        return discreption;
    }

    public void setDiscreption(String discreption) {
        this.discreption = discreption;
    }

    @Override
    public String toString() {
        return "Child{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday='" + birthday + '\'' +
                ", height='" + height + '\'' +
                ", dipearDay='" + dipearDay + '\'' +
                ", location='" + location + '\'' +
                ", dispearLocation='" + dispearLocation + '\'' +
                ", discreption='" + discreption + '\'' +
                '}';
    }
}
