package com.cuny.coursespotter;

public class CourseInfo {

    private String id;

    private String times;

    private String room;

    private String instructor;

    private String status;

    public CourseInfo(String id, String times, String room, String instructor, String status) {
        this.id = id;
        this.times = times;
        this.room = room;
        this.instructor = instructor;
        this.status = status;
    }

    public CourseInfo() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CourseInfo{" +
                "id='" + id + '\'' +
                ", times='" + times + '\'' +
                ", room='" + room + '\'' +
                ", instructor='" + instructor + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}