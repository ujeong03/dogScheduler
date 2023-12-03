import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DateDetailDialog extends JDialog {
    private JLabel dateLabel;
    private JPanel schedulesPanel;
    private JTextField newScheduleField;
    private Calendar calendar;
    private int day;
    private CalendarDBConnection dbConnection;
    private List<SchedulePanel> schedulePanels;
    private CalendarWindow calendarWindow; // CalendarWindow 참조 추가

    public DateDetailDialog(JFrame parent, String title, boolean modal, int day, CalendarDBConnection dbConnection, CalendarWindow calendarWindow) {
        super(parent, title, modal);
        setSize(800, 600);
        setLayout(new BorderLayout());

        this.dbConnection = dbConnection;
        this.day = day;
        this.schedulePanels = new ArrayList<>();
        this.calendarWindow = calendarWindow; // CalendarWindow 초기화

        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        updateDateLabel();

        // Top panel with date label
        JPanel topPanel = new JPanel(new BorderLayout());
        dateLabel = new JLabel("", JLabel.CENTER);
        dateLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(dateLabel, BorderLayout.NORTH);

        // Schedules panel
        schedulesPanel = new JPanel();
        schedulesPanel.setLayout(new BoxLayout(schedulesPanel, BoxLayout.Y_AXIS));
        loadSchedules();

        // Scroll pane for schedules
        JScrollPane scrollPane = new JScrollPane(schedulesPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with new schedule field
        JPanel bottomPanel = new JPanel(new FlowLayout());
        newScheduleField = new JTextField(30);
        newScheduleField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    addNewSchedule();
                }
            }
        });
        bottomPanel.add(newScheduleField);

        add(bottomPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
    }

    private void updateDateLabel() {
        dateLabel.setText(new SimpleDateFormat("yyyy년 MM월 dd일").format(new Date(calendar.getTimeInMillis())));
    }

    private void loadSchedules() {
        List<String> schedules = dbConnection.getSchedulesForDate(new Date(calendar.getTimeInMillis()));
        for (String schedule : schedules) {
            SchedulePanel schedulePanel = new SchedulePanel(schedule);
            schedulePanels.add(schedulePanel);
            schedulesPanel.add(schedulePanel);
        }
    }

    private void addNewSchedule() {
        String newSchedule = newScheduleField.getText().trim();
        if (!newSchedule.isEmpty()) {
            dbConnection.addSchedule(new Date(calendar.getTimeInMillis()), newSchedule);

            SchedulePanel schedulePanel = new SchedulePanel(newSchedule);
            schedulePanels.add(schedulePanel);
            schedulesPanel.add(schedulePanel);
            schedulesPanel.revalidate();
            schedulesPanel.repaint();

            newScheduleField.setText(""); // Clear the input field
            calendarWindow.updateDayButton(day, dbConnection.getSchedulesForDate(new Date(calendar.getTimeInMillis())));
        }
    }

    private class SchedulePanel extends JPanel {
        private JLabel scheduleLabel;
        private JCheckBox reminderCheckBox;
        private JCheckBox homeworkCheckBox;

        public SchedulePanel(String schedule) {
            setLayout(new FlowLayout());
            scheduleLabel = new JLabel(schedule);
            reminderCheckBox = new JCheckBox("Reminder");
            homeworkCheckBox = new JCheckBox("Homework");

            reminderCheckBox.addActionListener(e -> updateScheduleInDatabase(scheduleLabel.getText(), reminderCheckBox.isSelected(), homeworkCheckBox.isSelected()));
            homeworkCheckBox.addActionListener(e -> updateScheduleInDatabase(scheduleLabel.getText(), reminderCheckBox.isSelected(), homeworkCheckBox.isSelected()));

            add(scheduleLabel);
            add(reminderCheckBox);
            add(homeworkCheckBox);
        }

        private void updateScheduleInDatabase(String schedule, boolean isReminder, boolean isHomework) {
            dbConnection.updateSchedule(schedule, isReminder, isHomework);
            calendarWindow.updateDayButton(day, dbConnection.getSchedulesForDate(new Date(calendar.getTimeInMillis())));
        }
    }
}
