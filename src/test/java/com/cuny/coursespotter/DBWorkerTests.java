package com.cuny.coursespotter;

import org.junit.jupiter.api.Test;

import static com.cuny.coursespotter.DBWorker.insertData;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DBWorkerTests {
    String email = "test@example.com";
    String phone = "1112223333";
    String college = "QNS01";
    String courseName = "CSCI";
    int courseNumber = 211;
    int classID = 50460;

    @Test
    public void insertDataTestA() {

        int result = insertData(email, phone, college, courseName, courseNumber, classID);

        // 1 signifies that one row was successfully inserted
        assertEquals(1, result);
    }

    @Test
    public void insertDataTestB() {
        // Attempt to insert data when the SQL Server is Offline
        int result = insertData(email, phone, college, courseName, courseNumber, classID);

        // We receive an exit code of 0.
        assertEquals(0, result);
    }


}
