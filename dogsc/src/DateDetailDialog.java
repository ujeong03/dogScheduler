import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DateDetailDialog extends JDialog {
    private JLabel dateLabel;
    private JPanel schedulesPanel;
    private JButton addButton;
    private JButton saveButton;
    private Calendar calendar;
    private List<SchedulePanel> schedulePanels;
    private JPanel dayPanel;
    private int day;

    // CalendarDBConnection 인스턴스 추가
    private CalendarDBConnection dbConnection;

    public DateDetailDialog(JFrame parent, String title, boolean modal, int day, JPanel dayPanel, CalendarDBConnection dbConnection) {


        super(parent, title, modal);
        setSize(800, 600);
        setLayout(new BorderLayout());

        this.day = day;
        this.dayPanel = dayPanel;

        calendar = Calendar.getInstance();
        schedulePanels = new ArrayList<>();

        updateDateLabel(day);

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
        saveButton.addActionListener(e -> saveSchedules());
        bottomPanel.add(saveButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // CalendarDBConnection 초기화
        dbConnection = new CalendarDBConnection();

        // 기존 일정 불러오기
        loadSchedules();
    }

    private void updateDateLabel(int day) {
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
        revalidate();
        repaint();
    }

    private void saveSchedules() {
        List<String> schedulesForDay = schedulePanels.stream()
                .map(SchedulePanel::getTitle)
                .collect(Collectors.toList());

        // 데이터베이스에 저장
        dbConnection.clearSchedulesForDate(new Date());
        for (String schedule : schedulesForDay) {
            dbConnection.addSchedule(new Date(), schedule);
        }

        ((CalendarWindow) getParent()).updateDayButton(day, schedulesForDay);
        dispose();
    }

    // 기존 일정 불러와서 UI에 표시
    private void loadSchedules() {
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = sdf.format(currentDate);

        List<String> schedules = dbConnection.getSchedulesForDate(currentDate);

        for (String schedule : schedules) {
            SchedulePanel schedulePanel = new SchedulePanel(schedule);
            schedulePanels.add(schedulePanel);
            schedulesPanel.add(schedulePanel);
        }

        revalidate();
        repaint();
    }

    private class SchedulePanel extends JPanel {
        private JTextField titleField;
        private JCheckBox reminderCheckBox;
        private JCheckBox homeworkCheckBox;

        public SchedulePanel() {
            setLayout(new FlowLayout());

            titleField = new JTextField(30);
            reminderCheckBox = new JCheckBox("리마인더");
            homeworkCheckBox = new JCheckBox("과제");

            add(new JLabel("일정:"));
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

        public void setTitle(String title) {
            titleField.setText(title);
        }

        public boolean isReminderSelected() {
            return reminderCheckBox.isSelected();
        }

        public boolean isHomeworkSelected() {
            return homeworkCheckBox.isSelected();
        }

        private void deleteSchedulePanel() {
            schedulePanels.remove(this);
            schedulesPanel.remove(this);
            revalidate();
            repaint();
        }
    }
}
