import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DateDetailDialog extends JDialog {
    private JLabel dateLabel;
    private JPanel schedulesPanel;
    private JButton addButton;
    private JButton saveButton;
    private Calendar calendar;
    private List<SchedulePanel> schedulePanels;
    private JButton calendarButton; // 캘린더 버튼 추가

    public DateDetailDialog(JFrame parent, String title, boolean modal, int day, JButton calendarButton) {
        super(parent, title, modal);
        setSize(800, 600);
        setLayout(new BorderLayout());

        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);

        dateLabel = new JLabel("", JLabel.CENTER);
        dateLabel.setFont(new Font("Arial", Font.BOLD, 24));
        updateDateLabel(day);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(dateLabel, BorderLayout.NORTH);

        schedulesPanel = new JPanel();
        schedulesPanel.setLayout(new BoxLayout(schedulesPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(schedulesPanel);
        add(scrollPane, BorderLayout.CENTER);

        schedulePanels = new ArrayList<>();

        addButton = new JButton("+");
        addButton.addActionListener(e -> addSchedulePanel());
        topPanel.add(addButton, BorderLayout.SOUTH);

        // 캘린더 버튼 설정
        this.calendarButton = calendarButton;
        updateCalendarButtonText(); // 캘린더 버튼 텍스트 업데이트

        add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        saveButton = new JButton("저장");
        saveButton.addActionListener(e -> saveSchedules());
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

        revalidate();
        repaint();
    }

    private void saveSchedules() {
        // 저장 버튼을 눌렀을 때 실행되는 메서드
        // 각 SchedulePanel에서 일정 정보를 가져와 데이터베이스에 저장하도록 구현해야 함
        // 여기에서는 저장된 일정 정보를 출력하는 메시지만 표시
        StringBuilder scheduleText = new StringBuilder();
        for (SchedulePanel schedulePanel : schedulePanels) {
            String title = schedulePanel.getTitle();
            boolean isReminder = schedulePanel.isReminderSelected();
            boolean isHomework = schedulePanel.isHomeworkSelected();

            scheduleText.append("* ").append(title).append(" (리마인더: ").append(isReminder).append(", 과제: ").append(isHomework).append(")\n");
        }

        JOptionPane.showMessageDialog(this, "일정이 저장되었습니다:\n" + scheduleText.toString());

        // 저장 후 캘린더 버튼 업데이트
        updateCalendarButtonText();
    }

    private void updateCalendarButtonText() {
        // 캘린더 버튼 텍스트 업데이트
        StringBuilder buttonText = new StringBuilder("일정\n");
        for (SchedulePanel schedulePanel : schedulePanels) {
            buttonText.append("* ").append(schedulePanel.getTitle()).append("\n");
        }
        calendarButton.setText(buttonText.toString());
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

            add(new JLabel("일정 추가:"));
            add(titleField);
            add(reminderCheckBox);
            add(homeworkCheckBox);

            JButton deleteButton = new JButton("X");
            deleteButton.addActionListener(e -> deleteSchedulePanel()); // X 버튼 클릭 시 삭제 메서드 호출
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
            schedulePanels.remove(this);
            schedulesPanel.remove(this);

            revalidate();
            repaint();

            // 삭제 후 캘린더 버튼 업데이트
            updateCalendarButtonText();
        }
    }
}

