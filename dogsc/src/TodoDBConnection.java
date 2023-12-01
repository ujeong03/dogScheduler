import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TodoList 어플리케이션의 데이터베이스 연동을 담당하는 클래스입니다.
 */
public class TodoDBConnection {
    private Connection connection;
    private String todoDB = "jdbc:sqlite:src/database.sqlite";

    /**
     * 데이터베이스 연결을 초기화합니다.
     */
    public TodoDBConnection() {
        initializeDatabaseConnection();
    }

    /**
     * 데이터베이스 연결을 초기화합니다.
     */
    private void initializeDatabaseConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(todoDB);
                connection.setAutoCommit(false);
                System.out.println("Todo 데이터베이스에 연결 중");

                // 테이블 생성 SQL 실행
                String createTableSQL = "CREATE TABLE IF NOT EXISTS todoDB (" +
                        "order_index INTEGER PRIMARY KEY, "+
                        "todoDate TEXT, " +
                        "todoText TEXT, " +
                        "is_completed INTEGER)";

                try (Statement statement = connection.createStatement()) {
                    statement.execute(createTableSQL);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Todo 데이터베이스에 연결 안됨");
        }
    }

    /**
     * 데이터베이스 연결을 반환합니다.
     *
     * @return 데이터베이스 연결 객체
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * 데이터베이스 연결을 닫습니다.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Todo 데이터베이스 연결 닫힘");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 새로운 투두를 데이터베이스에 추가합니다.
     *
     * @param todoText 추가할 투두의 텍스트
     * @param todoDate 투두의 날짜
     */
    public void addTodoDB(String todoText, Date todoDate) {
        try {
            initializeDatabaseConnection();
            String insertQuery = "INSERT INTO todoDB (todoDate, todoText, is_completed) VALUES (?, ?, 0);";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(todoDate));
                preparedStatement.setString(2, todoText);
                preparedStatement.executeUpdate();
                connection.commit();
                System.out.println("성공");
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * 투두를 수정합니다.
     *
     * @param oldText 수정 전 투두의 텍스트
     * @param newText 수정 후 투두의 텍스트
     */
    public void modTodoDB(String oldText, String newText) {
        try {
            initializeDatabaseConnection();
            String updateQuery = "UPDATE todoDB SET todoText = ? WHERE todoText = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, newText);
                preparedStatement.setString(2, oldText);
                preparedStatement.executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * 투두를 삭제합니다.
     *
     * @param todoText 삭제할 투두의 텍스트
     */
    public void delTodoDB(String todoText) {
        try {
            initializeDatabaseConnection();
            String deleteQuery = "DELETE FROM todoDB WHERE todoText = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, todoText);
                preparedStatement.executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * 투두의 체크박스 상태를 업데이트합니다.
     *
     * @param todoText     업데이트할 투두의 텍스트
     * @param is_completed 체크박스 상태 (1: 체크, 0: 언체크)
     */
    public void updateTodoChecked(String todoText, int is_completed) {
        try {
            initializeDatabaseConnection();
            String updateQuery = "UPDATE todoDB SET is_completed = ? WHERE todoText = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setInt(1, is_completed);
                preparedStatement.setString(2, todoText);
                preparedStatement.executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * 특정 날짜의 투두 리스트를 가져옵니다.
     *
     * @param date 가져올 투두의 날짜
     * @return 해당 날짜의 투두 리스트
     */
    public List<String> getTodosForDate(Date date) {
        List<String> todos = new ArrayList<>();
        try {
            initializeDatabaseConnection();
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            String selectSQL = "SELECT * FROM todoDB WHERE todoDate = ? ORDER BY order_index ASC";

            try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
                statement.setString(1, formattedDate);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String todoText = resultSet.getString("todoText");
                        todos.add(todoText);
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return todos;
    }

    /**
     * 투두의 체크박스 상태를 가져옵니다.
     *
     * @param todo 투두의 텍스트
     * @return 체크박스 상태 (1: 체크, 0: 언체크)
     */
    public int getTodoCompletedStatus(String todo) {
        int isCompleted = 0;
        try {
            initializeDatabaseConnection();
            String selectSQL = "SELECT is_completed FROM todoDB WHERE todoText=?";

            try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
                statement.setString(1, todo);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        isCompleted = resultSet.getInt("is_completed");
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return isCompleted;
    }

    /**
     * SQLException을 처리하는 메서드입니다.
     *
     * @param e SQLException 객체
     */
    private void handleSQLException(SQLException e) {
        e.printStackTrace();
        System.out.println("Todo 데이터베이스 작업 중 오류 발생");
        try {
            connection.rollback();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }




    /**
     * 특정 날짜의 완료된 투두의 수를 가져옵니다.
     *
     * @param currentDate 기준이 되는 날짜
     * @return 완료된 투두의 수
     */
    public int getDoneTodoCount(Date currentDate) {
        int totalCount = 0;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date yesterday = new Date(currentDate.getTime() - (24 * 60 * 60 * 1000));
            String formattedYesterday = dateFormat.format(yesterday);

            // SQL 쿼리 작성
            String query = "SELECT SUM(is_completed) FROM todoDB WHERE todoDate = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, formattedYesterday);

                // 쿼리 실행
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // 결과 처리
                    if (resultSet.next()) {
                        totalCount = resultSet.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalCount;
    }
}

