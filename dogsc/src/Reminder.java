import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Reminder extends JPanel {

    private JPanel reminderPanel;

    public Reminder() {
        reminderPanel = new JPanel();


        JScrollPane scrollPane = new JScrollPane(reminderPanel);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        loadRemindersFromDatabase();
    }

    private void loadRemindersFromDatabase() {
        try {
            CalendarDBConnection calendarDBConnection = new CalendarDBConnection();
            calendarDBConnection.getConnection();

            // 현재 날짜 이후의 리마인더를 불러오기
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedCurrentDate = dateFormat.format(currentDate);

            String query = "SELECT * FROM calendardb WHERE (reminder = 1 OR homework = 1) AND calendardate >= ?";
            PreparedStatement preparedStatement = calendarDBConnection.prepareStatement(query);
            preparedStatement.setString(1, formattedCurrentDate);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String eventTitle = resultSet.getString("schedule");
                String eventDateText = resultSet.getString("calendardate");

                // Parse the eventDateText into a Date object
                Date eventDate = dateFormat.parse(eventDateText);

                // 남은 일 수 계산
                long daysRemaining = daysBetween(currentDate, eventDate);

                // 리마인더 패널에 추가
                addReminderItem(eventTitle, daysRemaining);
            }

            calendarDBConnection.closeConnection();

        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
    }


    private void addReminderItem(String eventTitle, long daysRemaining) {
        JPanel reminderItemPanel = new JPanel();
        JLabel titleLabel = new JLabel(eventTitle);
        JLabel daysRemainingLabel = new JLabel("D - " + daysRemaining);

        reminderItemPanel.add(daysRemainingLabel);
        reminderItemPanel.add(titleLabel);

        reminderPanel.add(reminderItemPanel);
        reminderPanel.revalidate();
        reminderPanel.repaint();
    }

    public static long daysBetween(Date currentDate, Date eventDate) {
        // Calendar 객체 생성
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(currentDate);

        Calendar eventCal = Calendar.getInstance();
        eventCal.setTime(eventDate);

        // 시간 정보 초기화
        currentCal.set(Calendar.HOUR_OF_DAY, 0);
        currentCal.set(Calendar.MINUTE, 0);
        currentCal.set(Calendar.SECOND, 0);
        currentCal.set(Calendar.MILLISECOND, 0);

        eventCal.set(Calendar.HOUR_OF_DAY, 0);
        eventCal.set(Calendar.MINUTE, 0);
        eventCal.set(Calendar.SECOND, 0);
        eventCal.set(Calendar.MILLISECOND, 0);

        // 날짜 차이 계산
        long difference = eventCal.getTimeInMillis() - currentCal.getTimeInMillis();

        // 차이를 일 수로 변환
        return difference / (24 * 60 * 60 * 1000);
    }


}
