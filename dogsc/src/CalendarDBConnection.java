import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalendarDBConnection {
    private static final Logger logger = Logger.getLogger(CalendarDBConnection.class.getName());
    private static final String calendarDB = "jdbc:sqlite:src/database.sqlite";
    private static Connection connection;

    public CalendarDBConnection() {
        initializeDatabaseConnection();
    }

    private void initializeDatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(calendarDB);
                connection.setAutoCommit(false);
                logger.info("Calendar 데이터베이스에 연결 중");

                String createTableSQL = "CREATE TABLE IF NOT EXISTS calendarDB (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "calendardate TEXT, " +
                        "schedule TEXT, " +
                        "reminder BOOLEAN, " +
                        "homework BOOLEAN)";

                try (Statement statement = connection.createStatement()) {
                    statement.execute(createTableSQL);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            logger.log(Level.SEVERE, "Calendar 데이터베이스 연결 실패", e);
        }
    }

    public static synchronized Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                logger.info("Calendar 데이터베이스 연결 닫힘");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Calendar 데이터베이스 연결 닫기 실패", e);
        }
    }

    public List<String> getSchedulesForDate(Date date) {
        List<String> schedules = new ArrayList<>();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        String selectSQL = "SELECT * FROM calendarDB WHERE calendardate = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(selectSQL)) {
            statement.setString(1, formattedDate);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String schedule = resultSet.getString("schedule");
                    schedules.add(schedule);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "일정 조회 실패", e);
        }
        return schedules;
    }

    public List<String> getSchedulesForDateRange(Date startDate, Date endDate) {
        List<String> schedules = new ArrayList<>();
        String formattedStartDate = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
        String formattedEndDate = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
        String selectSQL = "SELECT * FROM calendarDB WHERE calendardate BETWEEN ? AND ?";

        try (PreparedStatement statement = getConnection().prepareStatement(selectSQL)) {
            statement.setString(1, formattedStartDate);
            statement.setString(2, formattedEndDate);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String schedule = resultSet.getString("schedule");
                    schedules.add(schedule);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "일정 조회 실패", e);
        }
        return schedules;
    }

    public void updateSchedule(String schedule, boolean isReminder, boolean isHomework) {
        String updateQuery = "UPDATE calendarDB SET reminder = ?, homework = ? WHERE schedule = ?";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(updateQuery)) {
            preparedStatement.setBoolean(1, isReminder);
            preparedStatement.setBoolean(2, isHomework);
            preparedStatement.setString(3, schedule);
            preparedStatement.executeUpdate();
            connection.commit();
            logger.info("일정 업데이트됨: " + schedule);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "일정 업데이트 중 오류 발생", e);
            rollbackConnection();
        }
    }

    private void rollbackConnection() {
        try {
            if (connection != null) {
                connection.rollback();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "데이터베이스 롤백 중 오류 발생", e);
        }
    }

    public PreparedStatement prepareStatement(String query) throws SQLException {
        Connection connection = getConnection();
        return connection.prepareStatement(query);
    }

    public void addSchedule(Date date, String newSchedule) {
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());

        String insertSQL = "INSERT INTO calendarDB (calendardate, schedule, reminder, homework) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = getConnection().prepareStatement(insertSQL)) {
            statement.setObject(1, sqlDate);
            statement.setString(2, newSchedule);
            statement.setBoolean(3, false);
            statement.setBoolean(4, false);
            statement.executeUpdate();
            connection.commit();
            logger.info("새로운 일정 추가됨: " + newSchedule);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "일정 추가 중 오류 발생", e);
            rollbackConnection();
        }
    }
    public boolean hasSchedulesForDate(Date date) {
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        String selectSQL = "SELECT COUNT(*) FROM calendarDB WHERE calendardate = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(selectSQL)) {
            statement.setString(1, formattedDate);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "일정 조회 실패", e);
        }
        return false;
    }





}
