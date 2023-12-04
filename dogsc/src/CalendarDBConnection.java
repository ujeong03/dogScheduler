import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CalendarDBConnection 클래스는 캘린더 애플리케이션과 SQLite 데이터베이스 간의 연결을 관리합니다.
 */
public class CalendarDBConnection {
    private static final Logger logger = Logger.getLogger(CalendarDBConnection.class.getName());
    private static final String calendarDB = "jdbc:sqlite:src/database.sqlite";
    private static Connection connection;

    /**
     * CalendarDBConnection 클래스의 생성자입니다.
     */
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

    /**
     * 데이터베이스 연결을 반환합니다.
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
     * 지정한 날짜의 일정을 가져오는 메서드입니다.
     *
     * @param date 조회할 날짜
     * @return 해당 날짜의 일정 목록
     */
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

    /**
     * 지정한 날짜의 상세 일정 목록을 가져오는 메서드입니다.
     *
     * @param date 조회할 날짜
     * @return 해당 날짜의 상세 일정 목록
     */
    public List<Schedule> getSchedulesDetailsForDate(Date date) {
        List<Schedule> schedules = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(date);
        String selectSQL = "SELECT id, schedule, reminder, homework FROM calendarDB WHERE calendardate = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(selectSQL)) {
            statement.setString(1, formattedDate);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String scheduleText = resultSet.getString("schedule");
                    boolean isReminder = resultSet.getBoolean("reminder");
                    boolean isHomework = resultSet.getBoolean("homework");

                    Schedule schedule = new Schedule(id, scheduleText, date, isReminder, isHomework);
                    schedules.add(schedule);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "일정 상세 조회 실패", e);
        }
        return schedules;
    }

    /**
     * 일정을 업데이트하는 메서드입니다.
     *
     * @param schedule 업데이트할 일정
     */
    public void updateSchedule(Schedule schedule) {
        String updateSQL = "UPDATE calendarDB SET schedule = ?, reminder = ?, homework = ? WHERE id = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(updateSQL)) {
            statement.setString(1, schedule.getText());
            statement.setBoolean(2, schedule.isReminder());
            statement.setBoolean(3, schedule.isHomework());
            statement.setInt(4, schedule.getId());
            statement.executeUpdate();
            connection.commit();

            // 로그에 체크박스 상태 기록
            logger.info("일정 업데이트됨: " + schedule.getText());
            logger.info("리마인더 상태: " + (schedule.isReminder() ? "활성화" : "비활성화"));
            logger.info("과제 상태: " + (schedule.isHomework() ? "활성화" : "비활성화"));

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "일정 업데이트 중 오류 발생", e);
            rollbackConnection();
        }
    }

    /**
     * 새로운 일정을 추가하는 메서드입니다.
     *
     * @param date         일정 날짜
     * @param newSchedule  추가할 일정 내용
     * @param isReminder   리마인더 체크 여부
     * @param isHomework   과제 체크 여부
     * @return 추가된 일정의 ID
     */
    public int addSchedule(String date, String newSchedule, boolean isReminder, boolean isHomework) {
        String insertSQL = "INSERT INTO calendarDB (calendardate, schedule, reminder, homework) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = getConnection().prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setObject(1, date);
            statement.setString(2, newSchedule);
            statement.setBoolean(3, isReminder);
            statement.setBoolean(4, isHomework);
            statement.executeUpdate();
            connection.commit();

            // 로그에 일정 정보와 체크박스 상태를 기록
            logger.info("새로운 일정 추가됨: " + newSchedule);
            logger.info("리마인더 상태: " + (isReminder ? "활성화" : "비활성화"));
            logger.info("과제 상태: " + (isHomework ? "활성화" : "비활성화"));

            // 추가된 일정의 ID를 반환
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("일정 생성 실패, ID를 얻을 수 없음.");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "일정 추가 중 오류 발생", e);
            rollbackConnection();
            return -1; // 오류 발생 시 -1을 반환하거나 예외를 던질 수 있습니다.
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

    /**
     * 일정을 삭제하는 메서드입니다.
     *
     * @param scheduleId 삭제할 일정의 ID
     */
    public void deleteSchedule(int scheduleId) {
        String deleteSQL = "DELETE FROM calendarDB WHERE id = ?";

        try (PreparedStatement statement = getConnection().prepareStatement(deleteSQL)) {
            statement.setInt(1, scheduleId);
            statement.executeUpdate();
            connection.commit();
            logger.info("일정 삭제됨: ID = " + scheduleId);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "일정 삭제 중 오류 발생", e);
            rollbackConnection();
        }
    }

    /**
     * 데이터베이스에 대한 PreparedStatement를 생성하는 메서드입니다.
     *
     * @param query SQL 쿼리 문자열
     * @return PreparedStatement 객체
     * @throws SQLException SQL 예외 발생 시
     */
    public PreparedStatement prepareStatement(String query) throws SQLException {
        return getConnection().prepareStatement(query);
    }
}


