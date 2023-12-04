import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DateDetailDialog extends JDialog {
    private Date selectedDate;
    private CalendarDBConnection dbConnection;
    private JPanel schedulesPanel;
    private JButton addButton; // '+' 버튼

    public DateDetailDialog(JFrame parent, Date date, CalendarDBConnection db) {
        super(parent, "일정 세부사항", true);
        this.selectedDate = date;
        this.dbConnection = db;

        getContentPane().setBackground(Color.WHITE);

        setupUI();
        loadSchedules();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setSize(600, 300);

        // 상단 패널: '+' 버튼
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel dateInfoLabel = new JLabel("날짜: " + new SimpleDateFormat("yyyy-MM-dd").format(selectedDate));
        topPanel.add(dateInfoLabel);
        addButton = new JButton("일정추가");
        addButton.addActionListener(e -> addNewSchedulePanel());
        topPanel.add(addButton);

        add(topPanel, BorderLayout.NORTH);

        // 중앙 패널: 일정 목록
        schedulesPanel = new JPanel();
        schedulesPanel.setLayout(new BoxLayout(schedulesPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(schedulesPanel), BorderLayout.CENTER);
    }

    private void loadSchedules() {
        schedulesPanel.removeAll();
        List<Schedule> schedules = dbConnection.getSchedulesDetailsForDate(selectedDate);
        for (Schedule schedule : schedules) {
            addSchedulePanel(schedule);
        }
        schedulesPanel.revalidate();
        schedulesPanel.repaint();
    }

    private void addNewSchedulePanel() {
        // 새로운 일정 객체를 생성하고, 이를 패널에 추가
        Schedule schedule = new Schedule(-1, "", selectedDate.toString(), false, false);
        addSchedulePanel(schedule);
        schedulesPanel.revalidate();
        schedulesPanel.repaint();
    }

    private void addSchedulePanel(Schedule schedule) {
        JPanel schedulePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField scheduleTextField = new JTextField(schedule.getText(), 20);
        JCheckBox reminder = new JCheckBox("리마인더", schedule.isReminder());
        JCheckBox homework = new JCheckBox("과제", schedule.isHomework());
        JButton saveButton = new JButton("저장");

        saveButton.addActionListener(e -> saveSchedule(schedule.getId(), scheduleTextField, reminder, homework));

        schedulePanel.add(scheduleTextField);
        schedulePanel.add(reminder);
        schedulePanel.add(homework);
        schedulePanel.add(saveButton);
        schedulesPanel.add(schedulePanel);

        // UI 갱신
        schedulesPanel.revalidate();
        schedulesPanel.repaint();
    }

    private void saveSchedule(int id, JTextField scheduleTextField, JCheckBox reminder, JCheckBox homework) {
        String text = scheduleTextField.getText();
        boolean isReminder = reminder.isSelected();
        boolean isHomework = homework.isSelected();

        if (id == -1) {
            // 새로운 일정 추가
            dbConnection.addSchedule(selectedDate, text, isReminder, isHomework);
        } else {
            dbConnection.updateSchedule(new Schedule(id, text, selectedDate.toString(), isReminder, isHomework));
        }
        loadSchedules();
    }
}





