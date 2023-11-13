import javax.xml.transform.Result;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TodoDBConnection {
    private Connection connection;
    String todoDB = "jdbc:sqlite:src/todoDB.sqlite";

    // 데이터베이스 연결 초기화
    public TodoDBConnection() {
        initializeDatabaseConnection();
    }

    private void initializeDatabaseConnection(){
        try {
            Class.forName("org.sqlite.JDBC"); // SQLite JDBC 드라이버를 로드
            connection = DriverManager.getConnection(todoDB);
            connection.setAutoCommit(false); // AutoCommit 모드를 해제
            System.out.println("Todo 데이터베이스에 연결 중");

            // 테이블 생성 SQL 실행
            String createTableSQL = "CREATE TABLE IF NOT EXISTS todoDB ("+
                    "todoDate TEXT, " +
                    "todoText TEXT, " +
                    "is_completed INTEGER)";

            try (Statement statement = connection.createStatement()) {
                statement.execute(createTableSQL);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Todo 데이터베이스에 연결 안됨");
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
                System.out.println("Todo 데이터베이스 연결 닫힘");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //투두 추가
    public void addTodoDB(String todoText, Date todoDate)  {
        try {
            initializeDatabaseConnection(); //직접호출하기

            String insertQuery = "INSERT INTO todoDB (todoDate, todoText, is_completed)"+"VALUES (?, ?, 0);";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(todoDate)); // 형식에 맞게 날짜를 문자열로 변환
                preparedStatement.setString(2, todoText);
                preparedStatement.executeUpdate();
                connection.commit(); // 변경 사항 커밋
                System.out.println("성공");

            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Todo 데이터베이스 추가 중 오류 발생");
            try{
                connection.rollback();;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    //체크박스누를떼
    public void updateTodoChecked(String todoText, int is_completed) {
        try {
            initializeDatabaseConnection();

            String updateQuery = "UPDATE todoDB SET is_completed = ? WHERE todoText = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(2, todoText);
                preparedStatement.setInt(1, is_completed);
                preparedStatement.executeUpdate();
                connection.commit(); // 변경 사항 커밋

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    
    

    //해당 날짜의 투두 가져오기
    public List<String> getTodosForDate(Date date){
        List<String> todos = new ArrayList<>();
        try{
            initializeDatabaseConnection();
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            String selectSQL = "SELECT * FROM todoDB WHERE todoDate = ?";


            PreparedStatement statement = connection.prepareStatement(selectSQL);
            statement.setString(1,formattedDate);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                String todoText = resultSet.getString("todoText");
                todos.add(todoText);
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
        return todos;
    }
    //체크박스 상태
    public int getTodoCompletedStatus(String todo){
        int isCompleted =0 ; //기본적으로 0으로 초기화

        try{
            String selectSQL = "SELECT is_completed FROM todoDB WHERE todoText=?";

            try(PreparedStatement statement = connection.prepareStatement(selectSQL)) {
                statement.setString(1, todo);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    //결과가 있따면 is_completed 값을 가져옴
                    isCompleted = resultSet.getInt("is_completed");
                }
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return isCompleted;
    }


}
    
    
