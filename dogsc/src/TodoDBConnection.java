import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TodoDBConnection {
    private Connection connection;
    String todoDB = "jdbc:sqlite:todoDB.sqlite";

    // DB 연결
    public Connection getConnection() {
        try{
            connection = DriverManager.getConnection(todoDB);
            System.out.println("Todo 데이터 베이스에 연결 중");
        } catch(SQLException e){
            System.out.println("Todo 데이터 베이스에 연결 안됨");
            e.printStackTrace();
        }
        return connection;
    }

    //투두 추가
    public void addTodoDB(String todoText, Date todoDate) {
        try {
            String insertQuery = "INSERT INTO todos (todoDate, todoText, is_completed) VALUES (?, ?, 0)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(todoDate)); // 형식에 맞게 날짜를 문자열로 변환
                preparedStatement.setString(2, todoText);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //체크 박스 누를 때
    public void updateTodoChecked(String todoText, int is_completed) {
        try {
            String updateQuery = "UPDATE todos SET is_completed = ? WHERE todoText = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setInt(1, is_completed);
                preparedStatement.setString(2, todoText);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}