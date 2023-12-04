import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * 투두리스트를 보기 위한 패널
 * @author ujeong
 */
public class TodoList extends JPanel {

    /**날짜 라벨*/
    private JLabel dateLabel;
    /**이전 날짜 버튼*/
    private RoundButton prevDayButton;
    /**다음 날짜 버튼*/
    private RoundButton nextDayButton;
    /**투두를 적는 필드*/
    private JTextField todoTextField;
    /**투두 리스트 배경 패널*/
    private TodoListBG todoListPanel; //배경화면을 넣은 패널
    /**보상받기 버튼*/
    private RoundButton rewardButton;

    /**폰트*/
    InputStream inputStream1 = getClass().getResourceAsStream("font/BMJUA_ttf.ttf");
    /**폰트*/
    InputStream inputStream2 = getClass().getResourceAsStream("font/IM_Hyemin-Bold.ttf");
    /**날짜 폰트*/
    Font datefont;
    {
        try {
            datefont = Font.createFont(Font.TRUETYPE_FONT, inputStream1).deriveFont(Font.BOLD,15);
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**투두 폰트*/
    Font todofont;
    {
        try {
            todofont = Font.createFont(Font.TRUETYPE_FONT, inputStream2).deriveFont(Font.BOLD,17);
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**현재 날짜*/
    private Date currentDate;
    /**보상 받을 날짜*/
    private Date rewardDate;
    /**투두 디비 커넥션*/
    private TodoDBConnection todoDBConnection;
    /**전날 버튼 클릭 횟수*/
    private int prevDayButtonClickCount = 0;
    /**다음날 버튼 클릭 횟수*/
    private int nextDayButtonClickCount = 0;

    /**보상 컨트롤을 위한 객체*/
    private ControlReward controlReward;
    /**보상관련 파일 내용을 읽기 위한 스캐너*/
    Scanner scanner;
    /**보상 여부*/
    private int reward;
    /**보상 날짜와 여부가 적힌 파일*/
    File rewardDateFile;
    /**파일을 작성하기 위한 변수*/
    FileWriter fw;

    /**순서 인덱스*/
    private int orderIndex;
    /**투두 데이터 관련 정보를 담고 있는 객체*/
    private TodoData todoData;
    /**투두의 날짜*/
    private String todoDate;
    /**투두의 달성 여부*/
    private int is_completed;
    /**보상 받을 날짜와 오늘의 날짜를 비교하기 위한 멤버*/
    String compareDate; //날짜 비교해서 제한하기

    /**투두 배경화면 이미지*/
    private ImageIcon todoBGIcon = new ImageIcon("image/todolistBG.png");
    /**투두 배경화면 이미지*/
    private Image todoBG = todoBGIcon.getImage();
    /**투두 배경화면을 설정하기 위한 패널*/
    class TodoListBG extends JPanel {
        /**배경화면 설정하기*/
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(todoBG,0,0,getWidth(),getHeight(),this);

        }
    }

    /**
     * TodoList 생성자
     * 날짜, 버튼, 투두, 투두 입력, 보상 받기 버튼 등 gui 를 초기화
     */
    public TodoList() {
        todoDBConnection = new TodoDBConnection(); // TodoDBConnection 객체 생성
        controlReward = new ControlReward();

        setLayout(new BorderLayout());
        currentDate = new Date(); // currentDate를 현재 날짜로 초기화

        // 날짜 표시 레이블
        dateLabel = new JLabel();
        dateLabel.setFont(datefont);
        updateDateLabel();
        add(dateLabel, BorderLayout.NORTH);

        // 날짜 변경 버튼
        JPanel dateControlPanel = new JPanel();
        prevDayButton = new RoundButton("←");
        nextDayButton = new RoundButton("→");

        //이전 날짜로 이동. 오늘을 기준으로 3일 이동 가능
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

        //다음 날짜로 이동. 오늘을 기준으로 3일 이동 가능
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
        dateControlPanel.setBackground(Color.WHITE);
        dateControlPanel.setBorder(BorderFactory.createEmptyBorder(10,0,4,0));
        add(dateControlPanel, BorderLayout.NORTH);


        // 투두 입력 필드와 추가 버튼
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 75, 5));
        inputPanel.setPreferredSize(new Dimension(100, 50));
        todoTextField = new JTextField(20); //투두 입력
        rewardButton = new RoundButton("어제의 보상받기"); //보상받기 버튼
        rewardButton.setFont(todofont);

        todoTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // 엔터 키를 눌러 투두 추가
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
        rewardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rewardDate = new Date(); //오늘 날짜
                String formattedRewardDate = new SimpleDateFormat("yyyy-MM-dd").format(rewardDate);

                try {
                    rewardDateFile = new File("src/rewardDate.txt"); //리워드를 컨트롤 할 텍스트 파일
                    scanner = new Scanner(rewardDateFile);
                    compareDate = scanner.next(); //보상 받을 날짜
                    reward = scanner.nextInt(); //보상 받은 적 있는지
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }

                if (formattedRewardDate.equals(compareDate) && (reward==1)){ //만약 보상 받을 날짜와 적혀 있는 날짜가 같고, 보상 받은 적이 있으면
                    rewardButton.setEnabled(false); //버튼 비활성화
                }

                //보상을 받아야 하는 경우
                if ((formattedRewardDate.equals(compareDate) && (reward == 0))||!formattedRewardDate.equals(compareDate)){
                    int n = todoDBConnection.getDoneTodoCount(rewardDate); //투두가 달성된 개수를 세고
                    controlReward.addReward(n); // 그 개수를 강아지 보상에 추가
                    try {
                        fw = new FileWriter(rewardDateFile);
                        BufferedWriter writer = new BufferedWriter(fw);
                        writer.write(formattedRewardDate+" 1"); //그리고 보상을 받았다는 정보를 리워드를 컨트롤하는 파일에 저장
                        writer.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }


            }
        });

        //투두를 입력하는 필드와 버튼을 추가
        inputPanel.add(todoTextField);
        inputPanel.add(rewardButton);
        Color textFieldColor = new Color(237, 232, 224);
        inputPanel.setBackground(textFieldColor);
        add(inputPanel, BorderLayout.SOUTH);


        // 투두 항목 표시 패널
        todoListPanel = new TodoListBG();
        todoListPanel.setLayout(new BoxLayout(todoListPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(todoListPanel);
        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);
        loadTodosFromDatabase();
    }

    /**
     * 날짜 레이블을 현재 날짜로 업데이트
     */
    private void updateDateLabel() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateLabel.setText(dateFormat.format(currentDate));
    }

    /**
     * 주어진 날짜의 이전 날짜를 가져옴
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
     * 주어진 날짜의 다음 날짜를 가져옴
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
     * 데이터베이스에서 투두를 로드하고 UI 업데이트
     */
    private void loadTodosFromDatabase() {
        try {
            todoDBConnection.getConnection();

            // 현재 날짜에 해당하는 투두 가져오기
            List<String> todos = todoDBConnection.getTodosForDate(currentDate);


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
     * 투두 추가 / 수정 / 사제
     *
     * @param todoText    투두 항목의 텍스트.
     * @param isCompleted 투두 항목의 완료 상태.
     */
    private void addTodoItem(String todoText, int isCompleted) {
        // 새로운 투두 아이템 패널 생성
        JPanel todoItemPanel = new JPanel();
        JCheckBox checkBox = new JCheckBox(); //체크박스
        JTextField todotextField = new JTextField(todoText); //투두 패널에 올라온 투두
        JButton deleteButton = new RoundButton("삭제"); //삭제버튼

        todotextField.setFont(todofont);
        deleteButton.setFont(todofont);

        // 체크박스 설정
        checkBox.setSelected(isCompleted == 1); //isCompleted 가 1이면 체크박스가 체크된 상태로 나타남

        checkBox.addActionListener(new ActionListener() { //체크박스를 누를 때
            @Override
            public void actionPerformed(ActionEvent e) {
                int isCompleted = checkBox.isSelected() ? 1 : 0;
                todoDBConnection.getConnection();
                todoDBConnection.updateTodoChecked(todoText, todoDate, isCompleted);
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
                    todoData = todoDBConnection.getTodoDataFromText(todoText);
                    todoDate = todoData.getTodoDate();
                    todoDBConnection.getConnection();
                    todoDBConnection.modTodoDB(todoText, modifiedTodoText,todoDate);
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
                todoData = todoDBConnection.getTodoDataFromText(todoText);
                todoDate = todoData.getTodoDate();

                todoDBConnection.getConnection();
                todoDBConnection.delTodoDB(todoText,todoDate);
                todoDBConnection.closeConnection();

                // 데이터베이스에서 다시 로드
                loadTodosFromDatabase();
            }
        });

        todoItemPanel.setFocusable(true); // 키 이벤트를 받을 수 있도록 패널에 포커스 설정

        //투두 순서를 바꾸기 위한 코드
        todotextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                todoData = todoDBConnection.getTodoDataFromText(todoText);
                orderIndex = todoData.getOrderIndex();
                todoDate = todoData.getTodoDate();
                is_completed = todoData.isCompleted();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                compareDate = dateFormat.format(currentDate);
            }
        });

        todotextField.addKeyListener(new KeyAdapter() {


            @Override
            public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_UP) {
                    // 방향키를 눌렀을 때, 현재 선택된 투두 아이템의 순서를 변경

                    if ((todoText != null) && (orderIndex >1)) {
                        // 방향키를 누를 때마다 orderIndex를 변경하여 데이터베이스에 순서 업데이트
                        orderIndex -=1; // 현재 선택된 투두 아이템의 순서를 감소시킴
                        try {
                            todoDBConnection.increaseOrderIndexIfDuplicate(orderIndex);
                            todoDBConnection.updateOrderIndex(todoText, orderIndex,todoDate,is_completed); // 데이터베이스에서 순서 업데이트
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }

                        // 변경된 순서로 데이터 다시 로드하여 UI 업데이트
                        loadTodosFromDatabase();
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {

                    if (todoDate.equals(compareDate) && todoText != null) {

                        orderIndex +=1;
                        try {
                            todoDBConnection.decreaseOrderIndexIfDuplicate(orderIndex);
                            todoDBConnection.updateOrderIndex(todoText, orderIndex,todoDate,is_completed); // 데이터베이스에서 순서 업데이트
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }

                        loadTodosFromDatabase();
                    }
                }
            }

        });

        todoItemPanel.add(checkBox);
        todoItemPanel.add(todotextField);
        todoItemPanel.add(deleteButton);
        todoItemPanel.setOpaque(false);

        todoListPanel.add(todoItemPanel);
        todoListPanel.revalidate();
    }

}