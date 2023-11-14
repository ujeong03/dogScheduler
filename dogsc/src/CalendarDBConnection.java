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
            System.out.println("Calendar 데이터베이스에 연결 중");

            // 테이블 생성 SQL 실행
            String createTableSQL = "CREATE TABLE IF NOT EXISTS calendarDB (" +
                    "id INTEGER,"+
                    "calendardate TEXT, " +
                    "schedule TEXT, " +
                    "reminder INTEGER, " +
                    "homework INTEGER)";

            try (Statement statement = connection.createStatement()) {
                statement.execute(createTableSQL);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Calendar 데이터베이스에 연결 안됨");
        }
    }

    // DB 연결
    public Connection getConnection() {
        return connection;
    }

    // 데이터베이스 연결 닫기
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Calendar 데이터베이스 연결 닫힘");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 해당 날짜의 일정을 가져오기
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

    // 일정 추가
    public void addSchedule(Date date, String schedule) {
        try {
            initializeDatabaseConnection();

            String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);

            String insertQuery = "INSERT INTO calendarDB (date, schedule, reminder, homework) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, formattedDate);
                preparedStatement.setString(2, schedule);
                preparedStatement.setString(3, ""); // 미리 알림 필드 초기화
                preparedStatement.setString(4, ""); // 숙제 필드 초기화
                preparedStatement.executeUpdate();
                connection.commit(); // 변경 사항 커밋
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Calendar 데이터베이스 추가 중 오류 발생");
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            closeConnection();
        }
    }

    public PreparedStatement prepareStatement(String query) throws SQLException {
        Connection connection = getConnection();
        return connection.prepareStatement(query);
    }

}


