package com.cuny.coursespotter;

import com.vaadin.flow.component.grid.Grid;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static com.cuny.coursespotter.Parser.*;
import static org.junit.jupiter.api.Assertions.*;

public class ParserTests {
    private static String icsID;
    private static String icstateNum;
    private static String institution;
    private static String term;
    private static String classID;

    private static LinkedHashMap<String, String> sessionDetails;

    @BeforeClass
    public static void setUp() {
        sessionDetails = acquireSessionInfo();
        icsID = sessionDetails.get("ICSID");
        icstateNum = sessionDetails.get("ICStateNum");
        institution = "QNS01";
        term = "1202";
        classID = "50466";
    }

    @Test
    public void acquireSessionInfoTestA() {
        LinkedHashMap<String, String> map = acquireSessionInfo();
        assertFalse(map.isEmpty());

        assertTrue(map.containsKey("ICSID"));
        assertTrue(map.containsKey("ICStateNum"));
    }

    @Test
    public void acquireSessionInfoTestB() {
        // In the chance of empty session key, fetchSubjects should not attempt to send requests

        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        List<String> subjects = fetchSubjects(map.get("ICSID"), Integer.parseInt(map.get("ICStateNum")), institution, term);
        assertTrue(subjects.isEmpty());

    }

    @Test
    public void fetchSubjectsTestA() {
        List<String> subjects = fetchSubjects(icsID, Integer.parseInt(icstateNum), institution, term);
        assertFalse(subjects.isEmpty());
    }

    @Test
    public void fetchSubjectsTestB() {
        // In the chance of empty subjects list, fetchByClassNumber and fetchCourseInfo should not attempt to send requests
        // This is because the CUNY system will issue an error if it is in the wrong state

        CourseInfo latestInfo = Parser.fetchByClassNumber(institution, "1199", String.valueOf(classID));
        assertNotNull(latestInfo);

        LinkedHashMap<String, String> map = acquireSessionInfo();

        List<CourseInfo> result = fetchCourseInfo(map.get("ICSID"), Integer.parseInt(map.get("ICStateNum")), "QNS01", "1199", "CSCI", 211);
        assertTrue(result.isEmpty());

    }

    @Test
    public void fetchCourseInfoTestA() {
        LinkedHashMap<String, String> map = acquireSessionInfo();

        List<CourseInfo> result = fetchCourseInfo(map.get("ICSID"), Integer.parseInt(map.get("ICStateNum")), "QNS01", "1199", "CSCI", 211);
        assertFalse(result.isEmpty());
    }

    @Test
    public void fetchCourseInfoTestB() {
        LinkedHashMap<String, String> map = acquireSessionInfo();
        Student student = new Student();
        Grid<CourseInfo> courses = new Grid<>(CourseInfo.class);

        List<CourseInfo> fetchedCourses = new ArrayList<>();

        // Safeguard in place to check if the list is empty first before trying to present the items within.
        if (!fetchedCourses.isEmpty()) courses.setItems(fetchedCourses);
    }

    @Test
    public void fetchByClassNumberTestA() {
        CourseInfo latestInfo = Parser.fetchByClassNumber(institution, "1199", String.valueOf(classID));

        assertNotNull(latestInfo);
    }

    @Test
    public void fetchByClassNumberTestB() {
        CourseInfo latestInfo = Parser.fetchByClassNumber(institution, "1202", String.valueOf(classID));

        assertEquals("", latestInfo.getId());
        assertEquals("", latestInfo.getInstructor());
    }
}
