import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;

/**
 * 일정 알림을 표시하는 패널입니다.
 */
public class Reminder extends JPanel {

    private JPanel reminderPanel;
    private List<ReminderItem> reminderItems;

    // 폰트 파일 경로 설정
    InputStream inputStream = getClass().getResourceAsStream("font/BMJUA_ttf.ttf");

    // 폰트 로드
    Font reminderfont;

    {
        try {
            reminderfont = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(Font.BOLD,20);
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    ImageIcon reminderBGIcon = new ImageIcon("image/reminderBG.png");
    Image reminderBG = reminderBGIcon.getImage();

    class ReminderBG extends JPanel{
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(reminderBG,0,0,getWidth(),getHeight(),this);
            setBackground(Color.WHITE);

        }
    }

    /**
     * Reminder 클래스의 생성자입니다.
     */
    public Reminder() {
        reminderPanel = new ReminderBG();
        reminderItems = new ArrayList<>();

        JScrollPane scrollPane = new JScrollPane(reminderPanel);
        setLayout(new BorderLayout());
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        reminderPanel.setLayout(new BoxLayout(reminderPanel, BoxLayout.Y_AXIS));
        reminderPanel.setBorder(BorderFactory.createEmptyBorder(40,0,0,0));
        loadRemindersFromDatabase();
    }

    /**
     * 데이터베이스에서 일정을 로드하여 표시하는 메서드입니다.
     */
    private void loadRemindersFromDatabase() {
        try {
            CalendarDBConnection calendarDBConnection = new CalendarDBConnection();
            calendarDBConnection.getConnection();

            // 현재 날짜 이후의 리마인더를 불러오기
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedCurrentDate = dateFormat.format(currentDate);

            String query = "SELECT * FROM calendardb WHERE (reminder = 1 OR homework = 1) AND calendardate >= ? ORDER BY calendardate ASC";
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
                int homework = resultSet.getInt("homework");
                ReminderItem reminderItem = new ReminderItem(eventTitle, daysRemaining, homework);
                reminderItems.add(reminderItem);
                addReminderItem(reminderItem);
            }

            calendarDBConnection.closeConnection();

        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * ReminderItem을 패널에 추가하는 메서드입니다.
     *
     * @param reminderItem 추가할 ReminderItem 객체
     */
    private void addReminderItem(ReminderItem reminderItem) {
        reminderPanel.setFont(reminderfont);
        reminderPanel.add(reminderItem);
        reminderPanel.revalidate();
        reminderPanel.repaint();
    }

    /**
     * 두 날짜 사이의 일 수를 계산하는 메서드입니다.
     *
     * @param currentDate 현재 날짜
     * @param eventDate    이벤트 날짜
     * @return 두 날짜 사이의 일 수
     */
    public static long daysBetween(Date currentDate, Date eventDate) {
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(currentDate);

        Calendar eventCal = Calendar.getInstance();
        eventCal.setTime(eventDate);

        currentCal.set(Calendar.HOUR_OF_DAY, 0);
        currentCal.set(Calendar.MINUTE, 0);
        currentCal.set(Calendar.SECOND, 0);
        currentCal.set(Calendar.MILLISECOND, 0);

        eventCal.set(Calendar.HOUR_OF_DAY, 0);
        eventCal.set(Calendar.MINUTE, 0);
        eventCal.set(Calendar.SECOND, 0);
        eventCal.set(Calendar.MILLISECOND, 0);

        long difference = eventCal.getTimeInMillis() - currentCal.getTimeInMillis();
        return difference / (24 * 60 * 60 * 1000);
    }

    /**
     * 일정 알림 항목을 표시하는 내부 클래스입니다.
     */
    private class ReminderItem extends JPanel {
        private JLabel titleLabel;
        private JLabel daysRemainingLabel;
        private String eventTitle;

        /**
         * ReminderItem 클래스의 생성자입니다.
         *
         * @param eventTitle     일정 제목
         * @param daysRemaining  남은 일 수
         * @param homework       과제 여부
         */
        ReminderItem(String eventTitle, long daysRemaining, int homework) {
            this.eventTitle = eventTitle;

            titleLabel = new JLabel(eventTitle+"\n");
            if (daysRemaining != 0) {daysRemainingLabel = new JLabel("D - " + daysRemaining);}
            else {daysRemainingLabel = new JLabel("D - Day");}

            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            daysRemainingLabel.setHorizontalAlignment(SwingConstants.CENTER);

            titleLabel.setFont(reminderfont);
            daysRemainingLabel.setFont(reminderfont);

            // 과제는 다른 색으로 표시
            if (homework == 1) {
                Color homeworkCR = new Color(233,75,72);
                titleLabel.setForeground(homeworkCR);
            } else {
                titleLabel.setForeground(Color.BLACK);
            }
            add(daysRemainingLabel,BorderLayout.WEST); // 남은 일 수를 서쪽(왼쪽)에 배치
            add(titleLabel, BorderLayout.CENTER);
            setOpaque(false);

        }
    }
}