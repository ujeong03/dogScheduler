import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DateDetailDialog extends JDialog {
    private Date selectedDate;
    private CalendarDBConnection dbConnection;
    private JPanel schedulesPanel;
    private JButton addButton; // '일정추가' 버튼
    private JTextField inputTextField; // 일정 내용을 입력하는 텍스트 상자
    private JCheckBox reminderCheckbox; // 리마인더 체크박스
    private JCheckBox homeworkCheckbox; // 과제 체크박스
    private JButton saveButton; // '저장' 버튼
    private Schedule selectedSchedule; // 선택된 일정

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
        setSize(800, 300);

        // 상단 패널: '+' 버튼
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel dateInfoLabel = new JLabel("날짜: " + new SimpleDateFormat("yyyy-MM-dd").format(selectedDate));
        topPanel.add(dateInfoLabel);
        addButton = new JButton("일정추가");
        topPanel.add(addButton);

        add(topPanel, BorderLayout.NORTH);

        // 중앙 패널: 일정 목록
        schedulesPanel = new JPanel();
        schedulesPanel.setLayout(new BoxLayout(schedulesPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(schedulesPanel), BorderLayout.CENTER);

        // 하단 패널: 일정 입력, 체크박스, 저장 버튼 추가
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputTextField = new JTextField(20);
        reminderCheckbox = new JCheckBox("리마인더");
        homeworkCheckbox = new JCheckBox("과제");
        saveButton = new JButton("저장");
        saveButton.setEnabled(false);

        // 일정 입력 필드 내용이 변경될 때만 저장 버튼 활성화
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                enableSaveButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                enableSaveButton();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                enableSaveButton();
            }
        };

        inputTextField.getDocument().addDocumentListener(documentListener);

        saveButton.addActionListener(e -> saveSchedule());

        bottomPanel.add(inputTextField);
        bottomPanel.add(reminderCheckbox);
        bottomPanel.add(homeworkCheckbox);
        bottomPanel.add(saveButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void enableSaveButton() {
        // 일정 입력 필드에 내용이 있을 때만 저장 버튼 활성화
        String text = inputTextField.getText();
        saveButton.setEnabled(!text.isEmpty());
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

    private void addSchedulePanel(Schedule schedule) {
        JPanel schedulePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField scheduleTextField = new JTextField(20);
        JCheckBox reminder = new JCheckBox("리마인더");
        JCheckBox homework = new JCheckBox("과제");
        JButton updateButton = new JButton("수정"); // 버튼 이름을 "수정"으로 변경

        // 선택된 일정이 있을 경우, 해당 일정 정보를 텍스트 필드 및 체크박스에 설정
        if (schedule != null) {
            scheduleTextField.setText(schedule.getText());
            reminder.setSelected(schedule.isReminder());
            homework.setSelected(schedule.isHomework());
        }

        updateButton.addActionListener(e -> {
            // 새로운 Schedule 객체를 생성
            assert schedule != null;
            Schedule updatedSchedule = new Schedule(schedule.getId(), scheduleTextField.getText(),
                    schedule.getDate(), // 여기에는 실제 날짜 정보가 들어가야 합니다.
                    reminder.isSelected(), homework.isSelected());
            // updateSchedule 메소드를 호출하여 데이터베이스에 변경 사항을 저장
            dbConnection.updateSchedule(updatedSchedule);
        });


        schedulePanel.add(scheduleTextField);
        schedulePanel.add(reminder);
        schedulePanel.add(homework);
        schedulePanel.add(updateButton);

        // UI 갱신
        schedulesPanel.add(schedulePanel);
        schedulesPanel.revalidate();
        schedulesPanel.repaint();
    }

    private void saveSchedule() {
        String text = inputTextField.getText();
        boolean isReminder = reminderCheckbox.isSelected();
        boolean isHomework = homeworkCheckbox.isSelected();

        if (!text.isEmpty()) {
            // 데이터베이스에 일정 추가하고 id 받아오기
            int scheduleId = dbConnection.addSchedule(selectedDate, text, isReminder, isHomework);

            // 입력된 텍스트 정보를 사용하여 스케줄 패널에 일정 추가
            addSchedulePanel(new Schedule(scheduleId, text, text, isReminder, isHomework));

            // 입력 필드 및 체크박스 초기화
            inputTextField.setText("");
            reminderCheckbox.setSelected(false);
            homeworkCheckbox.setSelected(false);
            enableSaveButton();
        }
    }






}
