import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.sql.DriverManager.getConnection;

public class DateDetailDialog extends JDialog {
    private Date selectedDate;
    private CalendarDBConnection dbConnection;
    private JTextField scheduleTextField;
    private JCheckBox reminderCheckBox, homeworkCheckBox;
    private JButton saveButton, editButton;
    private JPanel schedulesPanel;


    public DateDetailDialog(JFrame parent, Date date, CalendarDBConnection db) {
        super(parent, "일정 세부사항", true);
        this.selectedDate = date;
        this.dbConnection = db;

        setupUI();
        loadSchedules();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setSize(400, 300);

        // 상단 패널: 날짜 제목, 일정 입력 필드, 체크박스
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        JLabel dateLabel = new JLabel(new SimpleDateFormat("yyyy-MM-dd").format(selectedDate));
        scheduleTextField = new JTextField(20);
        reminderCheckBox = new JCheckBox("리마인더");
        homeworkCheckBox = new JCheckBox("과제");

        topPanel.add(dateLabel);
        topPanel.add(scheduleTextField);
        topPanel.add(reminderCheckBox);
        topPanel.add(homeworkCheckBox);

        add(topPanel, BorderLayout.NORTH);

        // 중앙 패널: 기존 일정 목록
        schedulesPanel = new JPanel();
        schedulesPanel.setLayout(new BoxLayout(schedulesPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(schedulesPanel), BorderLayout.CENTER);

        // 하단 패널: 저장 및 수정 버튼
        JPanel bottomPanel = new JPanel();
        saveButton = new JButton("저장");
        editButton = new JButton("수정");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSchedule();
            }
        });

//        editButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                editSchedule();
//            }
//        });

        bottomPanel.add(saveButton);
        bottomPanel.add(editButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadSchedules() {
        schedulesPanel.removeAll();
        List<Schedule> schedules = dbConnection.getSchedulesDetailsForDate(selectedDate);
        for (Schedule schedule : schedules) {
            JButton scheduleButton = new JButton(schedule.getText());
            scheduleButton.addActionListener(e -> selectSchedule(schedule));
            schedulesPanel.add(scheduleButton);
        }
        schedulesPanel.revalidate();
        schedulesPanel.repaint();
    }

    private void selectSchedule(Schedule schedule) {
        // 현재 선택된 일정
        scheduleTextField.setText(schedule.getText());
        reminderCheckBox.setSelected(schedule.isReminder());
        homeworkCheckBox.setSelected(schedule.isHomework());
    }

    private void saveSchedule() {
        String newSchedule = scheduleTextField.getText();
        boolean isReminder = reminderCheckBox.isSelected();
        boolean isHomework = homeworkCheckBox.isSelected();

        dbConnection.addSchedule(selectedDate, newSchedule, isReminder, isHomework);
        loadSchedules();
    }

//    public void editSchedule(Schedule schedule) {
//        java.sql.Date sqlDate = new java.sql.Date(schedule.getDate().getTime());
//
//        String updateSQL = "UPDATE calendarDB SET schedule = ?, reminder = ?, homework = ? WHERE id = ?";
//
//        try (PreparedStatement statement = getConnection().prepareStatement(updateSQL)) {
//            statement.setString(1, schedule.getText());
//            statement.setBoolean(2, schedule.isReminder());
//            statement.setBoolean(3, schedule.isHomework());
//            statement.setInt(4, schedule.getId());
//            statement.executeUpdate();
//            connection.commit();
//            logger.info("일정 업데이트됨: " + schedule.getText());
//        } catch (SQLException e) {
//            CalendarDBConnection.logger.log(Level.SEVERE, "일정 업데이트 중 오류 발생", e);
//            rollbackConnection();
//        }
//    }

//
//    // 수정하기
//        todotextField.addKeyListener(new KeyAdapter() {
//        @Override
//        public void keyPressed(KeyEvent e) {
//            String modifiedTodoText = todotextField.getText();
//            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
//                todoDBConnection.getConnection();
//                todoDBConnection.modTodoDB(todoText, modifiedTodoText);
//                todoDBConnection.closeConnection();
//                loadTodosFromDatabase();
//            }
//        }
//    });


}
