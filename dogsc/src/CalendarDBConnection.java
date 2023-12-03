import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarDBConnection {
    private Connection connection;
    String calendarDB = "jdbc:sqlite:src/database.sqlite";

    public CalendarDBConnection() {
        initializeDatabaseConnection();
    }

    private void initializeDatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC"); // SQLite JDBC 드라이버를 로드
            connection = DriverManager.getConnection(calendarDB);
            connection.setAutoCommit(false); // AutoCommit 모드를 해제
           // System.out.println("Calendar 데이터베이스에 연결 중");

            // 테이블 생성 SQL 실행
            String createTableSQL = "CREATE TABLE IF NOT EXISTS calendarDB (" +
                    "id INTEGER," +
                    "calendardate TEXT, " +
                    "schedule TEXT, " +
                    "reminder INTEGER, " +
                    "homework INTEGER)";

            try (Statement statement = connection.createStatement()) {
                statement.execute(createTableSQL);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
           // System.out.println("Calendar 데이터베이스에 연결 안됨");
        }
    }


    public static synchronized Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {

        try {
            if (connection != null) {
                connection.close();
               // System.out.println("Calendar 데이터베이스 연결 닫힘");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getSchedulesForDate(Date date) {
        List<String> schedules = new ArrayList<>();
        try {
            initializeDatabaseConnection();
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            String selectSQL = "SELECT * FROM calendarDB WHERE calendardate = ?";

            PreparedStatement statement = connection.prepareStatement(selectSQL);
            statement.setString(1, formattedDate);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String schedule = resultSet.getString("schedule");
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
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
