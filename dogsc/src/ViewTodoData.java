import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewTodoData {
    public static void main(String[] args) {
        Connection connection = null;
        try {
            // 데이터베이스 연결 설정
            String dbUrl = "jdbc:sqlite:src/todoDB.sqlite"; // 데이터베이스 URL
            Class.forName("org.sqlite.JDBC"); // SQLite JDBC 드라이버 로드
            connection = DriverManager.getConnection(dbUrl);
            System.out.println("데이터베이스에 연결되었습니다.");

            // SQL 쿼리 작성
            String selectQuery = "SELECT * FROM todoDB";

            // SQL 쿼리 실행
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                // 결과 집합 반복 및 데이터 출력
                while (resultSet.next()) {
                    String todoDate = resultSet.getString("todoDate");
                    String todoText = resultSet.getString("todoText");
                    int isCompleted = resultSet.getInt("is_completed");
                    System.out.println("Date: " + todoDate + ", Todo: " + todoText + ", Completed: " + isCompleted);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
