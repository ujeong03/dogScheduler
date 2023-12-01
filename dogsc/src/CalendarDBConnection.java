import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CalendarDBConnection 클래스는 SQLite 데이터베이스와의 연결 및 상호 작용을 담당합니다.
 */
public class CalendarDBConnection {
    private static final Logger logger = Logger.getLogger(CalendarDBConnection.class.getName());
    private static final String calendarDB = "jdbc:sqlite:src/database.sqlite";
    private static Connection connection;

    /**
     * CalendarDBConnection 클래스의 생성자입니다.
     * 데이터베이스 연결을 초기화하고 테이블이 존재하지 않으면 생성합니다.
     */
    public CalendarDBConnection() {
        initializeDatabaseConnection();
    }

    /**
     * 데이터베이스 연결을 초기화하고 테이블이 존재하지 않으면 생성합니다.
     */
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

    /**
     * 데이터베이스 연결 객체를 반환합니다.
     *
     * @return 데이터베이스 연결 객체
     */
    public static synchronized Connection getConnection() {
        return connection;
    }

    /**
     * 데이터베이스 연결을 닫습니다.
     */
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

    /**
     * 특정 날짜에 대한 일정 목록을 가져옵니다.
     *
     * @param date 가져올 날짜
     * @return 해당 날짜의 일정 목록
     */
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

    /**
     * 특정 날짜에 새로운 일정을 추가합니다.
     *
     * @param date     일정을 추가할 날짜
     * @param schedule 추가할 일정 내용
     */
    public void addSchedule(Date date, String schedule) {
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        String insertQuery = "INSERT INTO calendarDB (calendardate, schedule, reminder, homework) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(insertQuery)) {
            preparedStatement.setString(1, formattedDate);
            preparedStatement.setString(2, schedule);
            preparedStatement.setBoolean(3, false); // 미리 알림 필드 초기화
            preparedStatement.setBoolean(4, false); // 숙제 필드 초기화
            preparedStatement.executeUpdate();
            connection.commit(); // 변경 사항 커밋
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Calendar 데이터베이스 추가 중 오류 발생", e);
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Calendar 데이터베이스 롤백 실패", ex);
            }
        }
    }

    /**
     * 특정 날짜의 모든 일정을 데이터베이스에서 삭제합니다.
     *
     * @param date 삭제할 일정의 날짜
     */
    public void clearSchedulesForDate(Date date) {
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        String deleteQuery = "DELETE FROM calendarDB WHERE calendardate = ?";

        try (PreparedStatement preparedStatement = getConnection().prepareStatement(deleteQuery)) {
            preparedStatement.setString(1, formattedDate);
            preparedStatement.executeUpdate();
            connection.commit(); // 변경 사항 커밋
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Calendar 데이터베이스에서 일정 삭제 중 오류 발생", e);
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Calendar 데이터베이스 롤백 실패", ex);
            }
        }
    }

    /**
     * 지정된 기간 내의 모든 일정을 가져옵니다.
     *
     * @param startDate 조회 시작 날짜
     * @param endDate   조회 종료 날짜
     * @return 기간 내의 모든 일정 목록
     */
    public List<String> getSchedulesForDateRange(Date startDate, Date endDate) {
        List<String> schedules = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedStartDate = sdf.format(startDate);
        String formattedEndDate = sdf.format(endDate);
        String selectSQL = "SELECT schedule FROM calendarDB WHERE calendardate BETWEEN ? AND ?";

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
            logger.log(Level.SEVERE, "기간 내 일정 조회 실패", e);
        }
        return schedules;
    }
}
