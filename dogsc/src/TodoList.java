import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * GUI를 사용한 TodoList 애플리케이션을 나타내는 클래스입니다.
 */
public class TodoList extends JPanel {

    // GUI 구성 요소
    private JLabel dateLabel;
    private JButton prevDayButton;
    private JButton nextDayButton;
    private JTextField todoTextField;
    private JButton addTodoButton;
    private JPanel todoListPanel;

    // 날짜 관련 필드
    private Date currentDate;
    private TodoDBConnection todoDBConnection;

    // 버튼 클릭 횟수
    private int prevDayButtonClickCount = 0;
    private int nextDayButtonClickCount = 0;

    /**
     * TodoList 객체를 생성합니다.
     * TodoDBConnection을 초기화하고 GUI 구성 요소를 설정하며, 데이터베이스에서 투두를 로드합니다.
     */
    public TodoList() {
        todoDBConnection = new TodoDBConnection(); // TodoDBConnection 객체 생성

        setLayout(new BorderLayout());
        currentDate = new Date(); // currentDate를 현재 날짜로 초기화

        // 날짜 표시 레이블
        dateLabel = new JLabel();
        updateDateLabel();
        add(dateLabel, BorderLayout.NORTH);

        // 날짜 변경 버튼
        JPanel dateControlPanel = new JPanel();
        prevDayButton = new JButton("←");
        nextDayButton = new JButton("→");

        prevDayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 이전 날짜 버튼 클릭 처리
                if (prevDayButtonClickCount < 3) {
                    currentDate = getPreviousDate(currentDate);
                    updateDateLabel();
                    loadTodosFromDatabase();
                    prevDayButtonClickCount++;
                    nextDayButtonClickCount--;
                }
                if (prevDayButtonClickCount == 4) {
                    prevDayButton.setEnabled(false);
                    currentDate = getPreviousDate(currentDate);
                    updateDateLabel();
                    loadTodosFromDatabase();
                }
            }
        });

        nextDayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 다음 날짜 버튼 클릭 처리
                if (nextDayButtonClickCount < 3) {
                    nextDayButton.setEnabled(true);
                    currentDate = getNextDate(currentDate);
                    updateDateLabel();
                    loadTodosFromDatabase();
                    nextDayButtonClickCount++;
                    prevDayButtonClickCount--;
                }
                if (nextDayButtonClickCount == 4) {
                    nextDayButton.setEnabled(false);
                    currentDate = getNextDate(currentDate);
                    updateDateLabel();
                    loadTodosFromDatabase();
                }
            }
        });

        dateControlPanel.add(prevDayButton);
        dateControlPanel.add(dateLabel);
        dateControlPanel.add(nextDayButton);

        add(dateControlPanel, BorderLayout.NORTH);

        // 투두 입력 필드와 추가 버튼
        JPanel inputPanel = new JPanel();
        inputPanel.setPreferredSize(new Dimension(100, 50));
        todoTextField = new JTextField(20);

        todoTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // 엔터 키를 누르면 투두를 추가합니다.
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    todoDBConnection.getConnection();
                    todoDBConnection.addTodoDB(todoTextField.getText(), currentDate);
                    todoTextField.setText("");
                    addTodoItem(todoTextField.getText(), 0);
                    todoDBConnection.closeConnection();
                    loadTodosFromDatabase();
                }
            }
        });

        inputPanel.add(todoTextField);
        add(inputPanel, BorderLayout.SOUTH);

        // 투두 항목 표시 패널
        todoListPanel = new JPanel();
        todoListPanel.setLayout(new BoxLayout(todoListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(todoListPanel);
        add(scrollPane, BorderLayout.CENTER);
        loadTodosFromDatabase();
    }

    /**
     * 날짜 레이블을 현재 날짜로 업데이트합니다.
     */
    private void updateDateLabel() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateLabel.setText(dateFormat.format(currentDate));
    }

    /**
     * 주어진 날짜의 이전 날짜를 가져옵니다.
     *
     * @param date 참조 날짜.
     * @return 이전 날짜.
     */
    private Date getPreviousDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

    /**
     * 주어진 날짜의 다음 날짜를 가져옵니다.
     *
     * @param date 참조 날짜.
     * @return 다음 날짜.
     */
    private Date getNextDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    /**
     * 데이터베이스에서 투두를 로드하고 UI를 업데이트합니다.
     */
    private void loadTodosFromDatabase() {
        try {
            todoDBConnection.getConnection();

            // 현재 날짜에 해당하는 투두 가져오기
            List<String> todos = todoDBConnection.getTodosForDate(currentDate);

            // 디버깅 출력문
            System.out.println("데이터베이스에서 가져온 Todos: " + todos);

            // 기존의 투두 항목을 모두 제거
            todoListPanel.removeAll();

            for (String todo : todos) {
                int isCompleted = todoDBConnection.getTodoCompletedStatus(todo);
                addTodoItem(todo, isCompleted);
            }

            // UI를 다시 그리기
            todoListPanel.revalidate();
            todoListPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            todoDBConnection.closeConnection();
        }
    }

    /**
     * UI에 투두 항목을 추가합니다.
     *
     * @param todoText    투두 항목의 텍스트.
     * @param isCompleted 투두 항목의 완료 상태.
     */
    private void addTodoItem(String todoText, int isCompleted) {
        // 새로운 투두 아이템 패널 생성
        JPanel todoItemPanel = new JPanel();
        JCheckBox checkBox = new JCheckBox();
        JTextField todotextField = new JTextField(todoText);
        JButton deleteButton = new JButton("삭제");

        // 체크박스 설정
        checkBox.setSelected(isCompleted == 1);

        checkBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int isCompleted = checkBox.isSelected() ? 1 : 0;
                todoDBConnection.getConnection();
                todoDBConnection.updateTodoChecked(todoText, isCompleted);
                todoDBConnection.closeConnection();
                loadTodosFromDatabase();
            }
        });

        // 수정하기
        todotextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String modifiedTodoText = todotextField.getText();
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    todoDBConnection.getConnection();
                    todoDBConnection.modTodoDB(todoText, modifiedTodoText);
                    todoDBConnection.closeConnection();
                    loadTodosFromDatabase();
                }
            }
        });

        // 삭제 버튼 설정
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 데이터베이스에서 투두 삭제
                todoDBConnection.getConnection();
                todoDBConnection.delTodoDB(todoText);
                todoDBConnection.closeConnection();

                // 데이터베이스에서 다시 로드
                loadTodosFromDatabase();
            }
        });

        todoItemPanel.add(checkBox);
        todoItemPanel.add(todotextField);
        todoItemPanel.add(deleteButton);

        todoListPanel.add(todoItemPanel);
        todoListPanel.revalidate();
    }
}
