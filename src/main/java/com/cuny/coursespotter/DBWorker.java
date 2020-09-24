package com.cuny.coursespotter;

import com.sendgrid.helpers.mail.objects.Content;

import java.sql.*;

public class DBWorker {

    private static final String insertStatement = "INSERT INTO CourseSpotter (Email, Phone, College, CourseName, CourseNumber, ClassID) VALUES (?,?,?,?,?,?)";

    private static final String deleteStatement = "DELETE FROM CourseSpotter WHERE Email=? AND Phone=? AND ClassID=?";

    private static String connectionString = "jdbc:sqlserver://PC;instanceName=DEVELOPMENT;";

    private static String username = "sa";

    private static String password = "your_password";

    private static MessageService messageService = new MessageService();

    /**
     * Insert the specified data into the database.
     * @param email
     * @param phone
     * @param college
     * @param courseName
     * @param courseNumber
     * @param classID
     * @return
     */
    public static int insertData(String email, String phone, String college, String courseName, int courseNumber, int classID) {

        try (Connection conn = DriverManager.getConnection(
                connectionString, username, password);
             PreparedStatement preparedStatement = conn.prepareStatement(insertStatement)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, phone);
            preparedStatement.setString(3, college);
            preparedStatement.setString(4, courseName);
            preparedStatement.setInt(5, courseNumber);
            preparedStatement.setInt(6, classID);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Check the database, delete entries when a course opens and use the message service to notify user.
     * @return
     */
    public static int checkData() {
        Connection connection;
        try {
            connection = DriverManager.getConnection(
                    connectionString, username, password);

            Statement connectionStatement = connection.createStatement();
            ResultSet resultSet = connectionStatement.executeQuery(
                    "SELECT * FROM CourseSpotter");
            while (resultSet.next()) {
                String email = resultSet.getString("Email");
                String phone = resultSet.getString("Phone");
                String college = resultSet.getString("College");
                String courseName = resultSet.getString("CourseName");
                int courseNumber = resultSet.getInt("CourseNumber");
                int classID = resultSet.getInt("ClassID");

                CourseInfo latestInfo = Parser.fetchByClassNumber(college, "1202", String.valueOf(classID));

                if (latestInfo.getStatus().equals("")) continue;
                if (latestInfo.getStatus().equals("Open")) {
                    if (!email.isEmpty()) {
                        Content content = new Content("text/plain", "Your class " + courseName + " " + courseNumber + " with Professor " + latestInfo.getInstructor() + " is now open.");
                        messageService.sendEmail("admin@coursespotter", "CourseSpotter Notification", email, content);
                    }
                    if (!phone.isEmpty()) {
                        messageService.sendText(phone, "+16466939744", "Your class " + courseName + " " + courseNumber + " with Professor " + latestInfo.getInstructor() + " is now open.");
                    }
                    PreparedStatement preparedStatement = connection.prepareStatement(deleteStatement);

                    preparedStatement.setString(1, email);
                    preparedStatement.setString(2, phone);
                    preparedStatement.setInt(3, classID);

                    return preparedStatement.executeUpdate();
                }
            }
            connection.close();
        } catch (Exception e) {
            System.out.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}
