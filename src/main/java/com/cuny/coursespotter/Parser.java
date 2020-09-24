package com.cuny.coursespotter;

import jodd.jerry.Jerry;
import kong.unirest.Unirest;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;

public class Parser {

    private static final String url = "https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/GUEST/HRMS/c/COMMUNITY_ACCESS.CLASS_SEARCH.GBL";

    private static Map<String, Object> fields = new HashMap<String, Object>() {{
        put("ICAJAX", "1");
        put("ICNAVTYPEDROPDOWN", "0");
        put("ICType", "Panel");
        put("ICElementNum", "0");
        put("ICXPos", "0");
        put("ICYPos", "0");
        put("ResponsetoDiffFrame", "-1");
        put("TargetFrameName", "None");
        put("FacetPath", "None");
        put("ICFocus", "");
        put("ICSaveWarningFilter", "0");
        put("ICChanged", "-1");
        put("ICAutoSave", "0");
        put("ICResubmit", "0");
        put("ICActionPrompt", "false");
        put("ICBcDomData", "undefined");
        put("ICFind", "");
        put("ICAddCount", "");
        put("ICAPPCLSDATA", "");
    }};

    public static Map<String, String> colleges = new HashMap<String, String>() {{
        put("Baruch College", "BAR01");
        put("Borough of Manhattan CC", "BMC01");
        put("Bronx CC", "BCC01");
        put("Brooklyn College", "BKL01");
        put("City College", "CTY01");
        put("College of Staten Island", "CSI01");
        put("Graduate Center", "GRD01");
        put("Guttman CC", "NCC01");
        put("Hostos CC", "HOS01");
        put("Hunter College", "HTR01");
        put("John Jay College", "JJC01");
        put("Kingsborough CC", "KCC01");
        put("LaGuardia CC", "LAG01");
        put("Lehman College", "LEH01");
        put("Macaulay Honors College", "MHC01");
        put("Medgar Evers College", "MEC01");
        put("NYC College of Technology", "NYT01");
        put("Queens College", "QNS01");
        put("Queensborough CC", "QCC01");
        put("School of Journalism", "SOJ01");
        put("School of Labor & Urban Studies", "SLU01");
        put("School of Law", "LAW01");
        put("School of Medicine", "MED01");
        put("School of Professional Studies", "SPS01");
        put("School of Public Health", "SPH01");
        put("University Admissions", "UAPC1");
        put("York College", "YRK01");
    }};

    /**
     * Obtains Subjects from CUNYfirst
     * @param icsID
     * @param icstateNum
     * @param institution
     * @param term
     * @return
     */
    public static List<String> fetchSubjects(String icsID, int icstateNum, String institution, String term) {
        List<String> subjects = new ArrayList<>();
        if (icsID != null) {
            String response = Unirest.post(url)
                    .fields(fields)
                    .field("ICStateNum", String.valueOf(icstateNum))
                    .field("ICAction", "CLASS_SRCH_WRK2_STRM$35$")
                    .field("ICSID", icsID)
                    .field("CLASS_SRCH_WRK2_INSTITUTION$31$", institution)
                    .field("CLASS_SRCH_WRK2_STRM$35$", term)
                    .asString().getBody();
            Jerry doc = Jerry.jerry(response);
            Jerry selectBox = doc.$("#SSR_CLSRCH_WRK_SUBJECT_SRCH\\$0");
            for (Jerry j : selectBox.children()) {
                subjects.add(j.text());
            }
        }
        return subjects;
    }

    /**
     * Obtains the course data from CUNYfirst
     * @param icsID
     * @param icstateNum
     * @param institution
     * @param term
     * @param subject
     * @param course
     * @return
     */
    public static List<CourseInfo> fetchCourseInfo(String icsID, int icstateNum, String institution, String term, String subject, int course) {
        List<String> subjects = fetchSubjects(icsID, icstateNum, institution, term);
        List<CourseInfo> courseData = new ArrayList<>();

        if (!subjects.isEmpty()) {

            String response = Unirest.post(url)
                    .fields(fields)
                    .field("ICStateNum", String.valueOf((icstateNum + 1)))
                    .field("ICAction", "CLASS_SRCH_WRK2_SSR_PB_CLASS_SRCH")
                    .field("ICSID", icsID)
                    .field("CLASS_SRCH_WRK2_STRM$35$", term)
                    .field("SSR_CLSRCH_WRK_SUBJECT_SRCH$0", subject)
                    .field("SSR_CLSRCH_WRK_CATALOG_NBR$1", String.valueOf(course))
                    .field("SSR_CLSRCH_WRK_SSR_OPEN_ONLY$chk$5", "N")
                    .asString().getBody();

            Jerry doc = Jerry.jerry(response);

            Jerry table = doc.$("#ACE_\\$ICField48\\$0");

            int row = 0;
            for (int i = 0; i < table.children().length() / 2; i++) {
                String courseNumber = table.$("#MTG_CLASS_NBR\\$" + row).text();
                String times = table.$("#MTG_DAYTIME\\$" + row).text();
                String rooms = table.$("#MTG_ROOM\\$" + row).text();
                String instructor = table.$("#MTG_INSTR\\$" + row).text();
                String status = table.$("#win0divDERIVED_CLSRCH_SSR_STATUS_LONG\\$" + row + " > div > img").attr("alt");

                CourseInfo courseInfo = new CourseInfo(courseNumber, times, rooms, instructor, status);
                courseData.add(courseInfo);
                row++;
            }
        }

        return courseData;
    }

    /**
     * Obtains the course data for a single course according to class number
     * @param institution
     * @param term
     * @param classNumber
     * @return
     */
    public static CourseInfo fetchByClassNumber(String institution, String term, String classNumber) {
        LinkedHashMap<String, String> map = acquireSessionInfo();
        CourseInfo courseData = new CourseInfo();

        int ICStateNum = NumberUtils.toInt(map.get("ICStateNum"), 0);
        List<String> subjects = fetchSubjects(map.get("ICSID"), ICStateNum, institution, term);

        if (!subjects.isEmpty()) {
            String response = Unirest.post(url)
                    .fields(fields)
                    .field("ICStateNum", String.valueOf((Integer.parseInt(map.get("ICStateNum")) + 1)))
                    .field("ICAction", "CLASS_SRCH_WRK2_SSR_PB_CLASS_SRCH")
                    .field("ICSID", map.get("ICSID"))
                    .field("CLASS_SRCH_WRK2_STRM$35$", term)
                    .field("SSR_CLSRCH_WRK_CLASS_NBR$10", classNumber)
                    .asString().getBody();

            Jerry doc = Jerry.jerry(response);
            Jerry table = doc.$("#trSSR_CLSRCH_MTG1\\$0_row1");

            courseData.setId(table.$("#MTG_CLASS_NBR\\$0").text());
            courseData.setTimes(table.$("#MTG_DAYTIME\\$0").text());
            courseData.setRoom(table.$("#MTG_ROOM\\$0").text());
            courseData.setInstructor(table.$("#MTG_INSTR\\$0").text());
            courseData.setStatus(table.$("#win0divDERIVED_CLSRCH_SSR_STATUS_LONG\\$0 > div > img").attr("alt"));
        }
        return courseData;
    }

    /**
     * Retrieves the session details including session key
     * @return
     */
    public static LinkedHashMap<String, String> acquireSessionInfo() {
        String html = Unirest.get(url).asString().getBody();

        Jerry document = Jerry.jerry(html);
        Jerry info = document.$("#win0divPSHIDDENFIELDS").first();

        LinkedHashMap<String, String> icMap = new LinkedHashMap<>();

        for (Jerry e : info.children()) {
            icMap.put(e.attr("name"), e.attr("value"));
        }
        //System.out.println(icMap.get("ICSID"));
        //System.out.println(icMap.get("ICStateNum"));

        return icMap;
    }
}