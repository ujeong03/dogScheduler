import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
public class TodoList extends JPanel {

    private JLabel dateLabel;
    private JButton prevDayButton;
    private JButton nextDayButton;
    private JTextField todoTextField;
    private JButton addTodoButton;
    private JPanel todoListPanel;
    private Date currentDate;
    private TodoDBConnection todoDBConnection;

    public TodoList() {
        //TodoList 클래스의 생성자에서 TodoDBConnection 객체 생성
        todoDBConnection = new TodoDBConnection();

        // 날짜 관련 코드
        //currentDate 변수를 현재 날짜로 초기화
        setLayout(new BorderLayout());
        currentDate = new Date();

        //날짜 표시 레이블
        dateLabel = new JLabel();
        updateDateLabel();
        add(dateLabel, BorderLayout.NORTH);

        //날짜 변경버튼
        JPanel dateControlPanel = new JPanel();
        prevDayButton = new JButton("←");
        nextDayButton = new JButton("→");

        //전날
        prevDayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentDate = getPreviousDate(currentDate);
                updateDateLabel();
                loadTodosFromDatabase();
            }
        });

        //다음날
        nextDayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentDate = getNextDate(currentDate);
                updateDateLabel();
                loadTodosFromDatabase();
            }
        });

        //날짜 이동 패널 만들기
        dateControlPanel.add(prevDayButton);
        dateControlPanel.add(dateLabel);
        dateControlPanel.add(nextDayButton);

        add(dateControlPanel, BorderLayout.NORTH);



        // 투두 입력 필드와 추가 버튼
        JPanel inputPanel = new JPanel();
        inputPanel.setPreferredSize(new Dimension(100,50));
        todoTextField = new JTextField(20);
        addTodoButton = new JButton("추가");

        addTodoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //todoDBConnection.getConnection();

                todoDBConnection.addTodoDB(todoTextField.getText(),currentDate);
                todoTextField.setText(""); // 입력 필드 비우기
                //todoDBConnection.closeConnection();

                addTodoItem(todoTextField.getText(), false);
            }
        });

        inputPanel.add(todoTextField);
        inputPanel.add(addTodoButton);
        add(inputPanel, BorderLayout.SOUTH);

        // 투두 항목 표시 패널
        todoListPanel = new JPanel();
        todoListPanel.setLayout(new BoxLayout(todoListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(todoListPanel);
        add(scrollPane, BorderLayout.CENTER);
        loadTodosFromDatabase();


    }


    // todo 메서드 모음
    // todo 날짜 메서드
    //날짜 업데이트 메서드
    private void updateDateLabel() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateLabel.setText(dateFormat.format(currentDate));
    }

    //현재 날짜 가져오는 코드
    private Date getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    //전날의 날짜 정보를 가져오는 메서드
    private Date getPreviousDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -1); //1일전으로 이동
        return calendar.getTime();
    }

    //다음날의 날짜 정보를 가져오는 메서드
    private Date getNextDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1); // 1일 후로 이동
        return calendar.getTime();
    }

    //todo 투두 메서드

    //데이터베이스에서 투두 가져오기
    private void loadTodosFromDatabase() {
        try {
        todoDBConnection.getConnection();

        // 현재 날짜에 해당하는 투두 가져오기
        List<String> todos = todoDBConnection.getTodosForDate(currentDate);

        //디버깅 출력문
        System.out.println("Todos from database: " + todos);

        //기존의 투두 항목을 모두 제거
        todoListPanel.removeAll();

        for (String todo : todos) {
            addTodoItem(todo, false);
        }
        //UI를 다시 그리기
        todoListPanel.revalidate();
        todoListPanel.repaint();
    } catch (Exception e)

    {
        e.printStackTrace();
    }finally{
        todoDBConnection.closeConnection();
        }}


    private void addTodoItem(String todoText, boolean isCompleted) {
        // 새로운 투두 아이템 패널 생성
        JPanel todoItemPanel = new JPanel();
        JCheckBox checkBox = new JCheckBox();
        JLabel todoTextLabel = new JLabel(todoText);

        checkBox.setSelected(isCompleted); // 투두 항목의 상태를 설정

        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int isCompleted = checkBox.isSelected() ? 1 : 0; // 체크 박스 상태에 따라 1 또는 0 설정
                todoDBConnection.getConnection();
                todoDBConnection.updateTodoChecked(todoText, isCompleted);
                todoDBConnection.closeConnection();
            }
        });

        todoItemPanel.add(checkBox);
        todoItemPanel.add(todoTextLabel);

        todoListPanel.add(todoItemPanel);
        todoListPanel.revalidate();
    }
}

