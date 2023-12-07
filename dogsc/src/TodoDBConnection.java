import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * TodoList 어플리케이션의 데이터베이스 연동을 담당
 * @author ujeong
 */
public class TodoDBConnection {
    private Connection connection;
    private String todoDB = "jdbc:sqlite:src/database.sqlite";

    /**
     * TodoDBConnection 생성자
     * 데이터베이스 연결 초기화
     */
    public TodoDBConnection() {
        initializeDatabaseConnection();
    }

    /**
     * 데이터베이스 연결 초기화.
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
                        "order_index INTEGER , "+
                        "todoDate TEXT, " +
                        "todoText TEXT, " +
                        "is_completed INTEGER)";

                try (Statement statement = connection.createStatement()) {
                    statement.execute(createTableSQL);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 데이터베이스 연결 반환
     *
     * @return 데이터베이스 연결 객체
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * 데이터베이스 연결 닫기
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 새로운 투두를 데이터베이스에 추가
     *
     * @param todoText 추가할 투두의 텍스트
     * @param todoDate 투두의 날짜
     */
    public void addTodoDB(String todoText, Date todoDate) {
        try {
            initializeDatabaseConnection();//초기화
            // 처음 투두를 추가하면 달성여부는 0으로 초기화하여 데이터베이스에 추가
            String insertQuery = "INSERT INTO todoDB (todoDate, todoText, is_completed) VALUES (?, ?, 0);";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(todoDate));//날짜 형식에 주의
                preparedStatement.setString(2, todoText);
                preparedStatement.executeUpdate();
                connection.commit(); //커밋
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * 투두 수정
     *
     * @param oldText 수정 전 투두의 텍스트
     * @param newText 수정 후 투두의 텍스트
     */
    public void modTodoDB(String oldText, String newText,String todoDate) {
        try {
            initializeDatabaseConnection();
            String updateQuery = "UPDATE todoDB SET todoText = ? WHERE todoText = ? and todoDate =?";
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
     * 투두 삭제
     *
     * @param todoText 삭제할 투두의 텍스트
     */
    public void delTodoDB(String todoText,String todoDate) {
        try {
            initializeDatabaseConnection();
            String deleteQuery = "DELETE FROM todoDB WHERE todoText = ? and todoDate =?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                preparedStatement.setString(1, todoText);
                preparedStatement.setString(2,todoDate);
                preparedStatement.executeUpdate();
                connection.commit();
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }

    /**
     * 투두 체크박스 상태 업데이트
     *
     * @param todoText     업데이트할 투두의 텍스트
     * @param is_completed 체크박스 상태 (1: 체크, 0: 언체크)
     */
    public void updateTodoChecked(String todoText, String todoDate, int is_completed) {
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
     * 특정 날짜의 투두 리스트를 가져옴
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
     * 투두의 체크박스 상태를 가져옴
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
     * SQLException을 처리하는 메서드
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
     * 특정 날짜의 완료된 투두의 수를 가져옴
     *
     * @param currentDate 기준이 되는 날짜
     * @return 완료된 투두의 수
     */
    public int getDoneTodoCount(Date currentDate) {
        int totalCount = 0;
        try {
            initializeDatabaseConnection();
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


    /**
     * 투두 내용으로 투두의 데이터 전부를 가져옴
     *
     * @param todoText
     * @return
     */
    public TodoData getTodoDataFromText(String todoText) {
        TodoData todoData = new TodoData();

        try {
            initializeDatabaseConnection();
            String selectSQL = "SELECT order_index, todoDate, todoText, is_completed FROM todoDB WHERE todoText = ?";
            try (PreparedStatement statement = connection.prepareStatement(selectSQL)) {
                statement.setString(1, todoText);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        todoData.setOrderIndex(resultSet.getInt("order_index"));
                        todoData.setTodoDate(resultSet.getString("todoDate"));
                        todoData.setTodoText(resultSet.getString("todoText"));
                        todoData.setCompleted(resultSet.getInt("is_completed"));
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException(e);
        }

        return todoData;
    }

    /**
     * 투두의 순서를 바꾸기 위해 orderIndex를 업데이트 하는 메서드
     *
     * orderIndex는 primary key이므로 해당 데이터를 삭제하고 추가하는 과정 필요
     *
     * @param todoText
     * @param newOrderIndex 새롭게 바꿀 인덱스
     * @param todoDate
     * @param is_completed
     * @throws SQLException
     */
    public void updateOrderIndex(String todoText, int newOrderIndex, String todoDate, int is_completed) throws SQLException {
        initializeDatabaseConnection();

        // 순서가 변경된 항목 삭제
        String deleteQuery = "DELETE FROM todoDB WHERE order_index = ?";
        PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
        deleteStatement.setInt(1, newOrderIndex);
        deleteStatement.executeUpdate();

        // 삭제된 항목 대신 새로운 값 삽입
        String insertQuery = "INSERT INTO todoDB (order_index, todoDate, todoText, is_completed) VALUES (?, ?, ?, ?)";
        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.setInt(1, newOrderIndex); // order_index 값 설정
        insertStatement.setString(2, todoDate); // todoDate 값 설정
        insertStatement.setString(3, todoText); // todoText 값 설정
        insertStatement.setInt(4, is_completed); // is_completed 값 설정
        insertStatement.executeUpdate();
        connection.commit();
    }

    /**
     * 똑같은 orderIndex를 가진 데이터를 찾아 해당 인덱스를 증가 시키는 메서드
     * @param orderIndex
     * @throws SQLException
     */
    public void increaseOrderIndexIfDuplicate(int orderIndex) throws SQLException {
        initializeDatabaseConnection();

        // 동일한 orderIndex를 가진 다른 todoText 찾기
        String findQuery = "SELECT * FROM todoDB WHERE order_index = ?";
        PreparedStatement findStatement = connection.prepareStatement(findQuery);
        findStatement.setInt(1, orderIndex);
        ResultSet resultSet = findStatement.executeQuery();

        // 동일한 orderIndex를 가진 todoText가 있다면 해당 todoText의 orderIndex를 +1하여 업데이트
        while (resultSet.next()) {
            int currentOrderIndex = resultSet.getInt("order_index");
            String foundTodoDate = resultSet.getString("todoDate");
            String foundTodoText = resultSet.getString("todoText");
            int foundCompleted = resultSet.getInt("is_completed");

            // 해당 todoText의 orderIndex를 +1하여 업데이트
            updateOrderIndex(foundTodoText, currentOrderIndex + 1,foundTodoDate,foundCompleted);
        }
    }

    /**
     * 똑같은 orderIndex를 가진 데이터를 찾아 index를 감소
     * @param orderIndex
     * @throws SQLException
     */
    public void decreaseOrderIndexIfDuplicate(int orderIndex) throws SQLException {
        initializeDatabaseConnection();

        // 동일한 orderIndex를 가진 다른 todoText 찾기
        String findQuery = "SELECT * FROM todoDB WHERE order_index = ?";
        PreparedStatement findStatement = connection.prepareStatement(findQuery);
        findStatement.setInt(1, orderIndex);
        ResultSet resultSet = findStatement.executeQuery();

        // 동일한 orderIndex를 가진 todoText가 있다면 해당 todoText의 orderIndex를 -1하여 업데이트
        while (resultSet.next()) {
            int currentOrderIndex = resultSet.getInt("order_index");
            String foundTodoDate = resultSet.getString("todoDate");
            String foundTodoText = resultSet.getString("todoText");
            int foundCompleted = resultSet.getInt("is_completed");

            // 해당 todoText의 orderIndex를 -1하여 업데이트
            updateOrderIndex(foundTodoText, currentOrderIndex - 1,foundTodoDate,foundCompleted);
        }
    }
}
