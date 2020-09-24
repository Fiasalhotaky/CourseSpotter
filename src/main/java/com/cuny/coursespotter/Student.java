package com.cuny.coursespotter;

public class Student {

    private String email;

    private String phoneNumber;

    private String college;

    private String courseName;

    private int courseNumber;

    private int classID;

    public Student(String email, String phoneNumber, String college, String courseName, int courseNumber, int classID) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.college = college;
        this.courseName = courseName;
        this.courseNumber = courseNumber;
        this.classID = classID;
    }

    public Student() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(int courseNumber) {
        this.courseNumber = courseNumber;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

}
