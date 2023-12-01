import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DateDetailDialog 클래스는 특정 날짜의 일정을 상세하게 보여주는 다이얼로그입니다.
 */
public class DateDetailDialog extends JDialog {
    private JLabel dateLabel;
    private JPanel schedulesPanel;
    private JButton addButton;
    private JButton saveButton;
    private Calendar calendar;
    private List<SchedulePanel> schedulePanels;
    private JButton calendarButton;
    private int day;
    private JButton dayButton;

    /**
     * DateDetailDialog 클래스의 생성자입니다.
     *
     * @param parent     부모 프레임
     * @param title      다이얼로그 타이틀
     * @param modal      모달 여부
     * @param day        날짜
     * @param dayButton  날짜를 나타내는 버튼
     */

    public DateDetailDialog(JFrame parent, String title, boolean modal, int day, JButton dayButton) {
        super(parent, title, modal);
        setSize(800, 600);
        setLayout(new BorderLayout());

        this.day = day;
        this.dayButton = dayButton;

        calendar = Calendar.getInstance();
        schedulePanels = new ArrayList<>();
        this.calendarButton = calendarButton;

        updateCalendarButtonText();

        dateLabel = new JLabel("", JLabel.CENTER);
        dateLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(dateLabel, BorderLayout.NORTH);

        schedulesPanel = new JPanel();
        schedulesPanel.setLayout(new BoxLayout(schedulesPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(schedulesPanel);
        add(scrollPane, BorderLayout.CENTER);

        addButton = new JButton("+");
        addButton.addActionListener(e -> addSchedulePanel());
        topPanel.add(addButton, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        saveButton = new JButton("저장");
        saveButton.addActionListener(e -> {
            try {
                saveSchedules();
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        bottomPanel.add(saveButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void updateDateLabel(int day) {
        String dayOfWeek = getDayOfWeek(day);
        dateLabel.setText(day + "일 (" + dayOfWeek + ")");
    }

    private String getDayOfWeek(int day) {
        String[] daysOfWeek = {"일", "월", "화", "수", "목", "금", "토"};
        int dayOfWeekIndex = (day + calendar.get(Calendar.DAY_OF_WEEK) - 2) % 7;
        return daysOfWeek[dayOfWeekIndex];
    }

    private void addSchedulePanel() {
        SchedulePanel schedulePanel = new SchedulePanel();
        schedulePanels.add(schedulePanel);
        schedulesPanel.add(schedulePanel);

        SwingUtilities.invokeLater(() -> {
            revalidate();
            repaint();
            updateCalendarButtonText();
        });
    }



    private void saveSchedules() throws ClassNotFoundException {
        String url = "jdbc:sqlite:src/todoDB.sqlite";
        Class.forName("org.sqlite.JDBC");
        createTableIfNotExists();
        try (Connection conn = DriverManager.getConnection(url)) {
            for (SchedulePanel schedulePanel : schedulePanels) {
                String title = schedulePanel.getTitle();
                boolean isReminder = schedulePanel.isReminderSelected();
                boolean isHomework = schedulePanel.isHomeworkSelected();

                String sql = "INSERT INTO schedules (title, isReminder, isHomework) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, title);
                    pstmt.setBoolean(2, isReminder);
                    pstmt.setBoolean(3, isHomework);
                    pstmt.executeUpdate();
                }
            }
            JOptionPane.showMessageDialog(this, "일정이 저장되었습니다.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(this, "데이터베이스 저장 중 오류가 발생했습니다.");
        }
        List<String> schedulesForDay = schedulePanels.stream()
                .map(SchedulePanel::getTitle)
                .collect(Collectors.toList());
        ((CalendarWindow) getParent()).updateDayButton(day, schedulesForDay);

        updateCalendarButtonText();
    }

    private void createTableIfNotExists() {
        String url = "jdbc:sqlite:src/todoDB.sqlite";

        String sql = "CREATE TABLE IF NOT EXISTS schedules ("
                + "id INTEGER PRIMARY KEY,"
                + "title TEXT NOT NULL,"
                + "isReminder BOOLEAN NOT NULL,"
                + "isHomework BOOLEAN NOT NULL);";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * 일정 패널을 제거하고 화면을 업데이트합니다.
     *
     * @param schedulePanel 제거할 일정 패널
     */
    public void removeSchedulePanel(SchedulePanel schedulePanel) {
        schedulePanels.remove(schedulePanel);
        schedulesPanel.remove(schedulePanel);

        SwingUtilities.invokeLater(() -> {
            revalidate();
            repaint();
            updateCalendarButtonText();
        });
    }

    /**
     * 캘린더 버튼 텍스트를 업데이트합니다.
     */
    private void updateCalendarButtonText() {
        StringBuilder buttonText = new StringBuilder("일정\n");
        for (SchedulePanel schedulePanel : schedulePanels) {
            buttonText.append("* ").append(schedulePanel.getTitle()).append("\n");
        }
        //calendarButton.setText(buttonText.toString());
    }

    /**
     * 일정을 나타내는 패널 클래스입니다.
     */
    private class SchedulePanel extends JPanel {
        private JTextField titleField;
        private JCheckBox reminderCheckBox;
        private JCheckBox homeworkCheckBox;

        public SchedulePanel() {
            setLayout(new FlowLayout());

            titleField = new JTextField(30);
            reminderCheckBox = new JCheckBox("리마인더");
            homeworkCheckBox = new JCheckBox("과제");

            add(new JLabel("일정 추가:"));
            add(titleField);
            add(reminderCheckBox);
            add(homeworkCheckBox);

            JButton deleteButton = new JButton("X");
            deleteButton.addActionListener(e -> deleteSchedulePanel());
            add(deleteButton);
        }

        public String getTitle() {
            return titleField.getText();
        }

        public boolean isReminderSelected() {
            return reminderCheckBox.isSelected();
        }

        public boolean isHomeworkSelected() {
            return homeworkCheckBox.isSelected();
        }

        private void deleteSchedulePanel() {
            DateDetailDialog parentDialog = (DateDetailDialog) SwingUtilities.getWindowAncestor(this);
            parentDialog.removeSchedulePanel(this);
        }
    }
}
