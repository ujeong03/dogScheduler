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
 * 리마인더 표시를 하기 위한 클래스
 * @author ujeong
 */
public class Reminder extends JPanel {

    /**리마인더 패널*/
    private JPanel reminderPanel;
    /**리마인더 아이템을 담기 위한 객체*/
    private List<ReminderItem> reminderItems;

    /**폰트*/
    InputStream inputStream = getClass().getResourceAsStream("font/BMJUA_ttf.ttf");
    /**폰트*/
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


    /**리마인더 배경화면*/
    ImageIcon reminderBGIcon = new ImageIcon("image/reminderBG.png");
    /**리마인더 배경화면 이미지*/
    Image reminderBG = reminderBGIcon.getImage();
    /**배경화면을 담은 패널*/
    class ReminderBG extends JPanel{
        /**배경화면 그리기*/
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(reminderBG,0,0,getWidth(),getHeight(),this);
            setBackground(Color.WHITE);

        }
    }

    /**
     * Reminder 생성자
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
     * 데이터베이스에서 리마인더 , 과제 정보를 로드
     */
    private void loadRemindersFromDatabase() {
        try {
            CalendarDBConnection calendarDBConnection = new CalendarDBConnection();
            calendarDBConnection.getConnection();

            // 현재 날짜 이후의 리마인더를 불러오기
            Date currentDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedCurrentDate = dateFormat.format(currentDate);

            //리마인더나 과제가 1이면 calendardate 오름차순 정렬로 디비에서 가져옴
            String query = "SELECT * FROM calendardb WHERE (reminder = 1 OR homework = 1) AND calendardate >= ? ORDER BY calendardate ASC";
            PreparedStatement preparedStatement = calendarDBConnection.prepareStatement(query);
            preparedStatement.setString(1, formattedCurrentDate);

            ResultSet resultSet = preparedStatement.executeQuery();

            //resultSet에서 schedule과 calendardate 정보를 가지고 남은 일 수 계산, 패널에 추가
            while (resultSet.next()) {
                String eventTitle = resultSet.getString("schedule"); // 일정 내용
                String eventDateText = resultSet.getString("calendardate"); //일정 날짜

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
     * remdiner 정보를 패널에 추가
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
     * 두 날짜 사이의 일 수를 계산
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
     *  rminder item을 저장하기 위한 객체
     */
    private class ReminderItem extends JPanel {
        private JLabel titleLabel;
        private JLabel daysRemainingLabel;
        private String eventTitle;

        /**
         * ReminderItem 클래스의 생성자입니다.
         *
         * @param eventTitle     일정 내용
         * @param daysRemaining  남은 일 수
         * @param homework       과제 여부
         */
        ReminderItem(String eventTitle, long daysRemaining, int homework) {
            this.eventTitle = eventTitle;

            if (daysRemaining != 0) {daysRemainingLabel = new JLabel("D - " + daysRemaining);}
            else {daysRemainingLabel = new JLabel("D - Day");}

            titleLabel = new JLabel(eventTitle);

            daysRemainingLabel.setHorizontalAlignment(SwingConstants.CENTER);
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

            daysRemainingLabel.setFont(reminderfont);
            titleLabel.setFont(reminderfont);

            // 과제는 다른 색으로 표시
            if (homework == 1) {
                Color homeworkCR = new Color(233,75,72);
                titleLabel.setForeground(homeworkCR);
            } else {
                titleLabel.setForeground(Color.BLACK);
            }

            //D - 남은 일수 | 일정
            add(daysRemainingLabel,BorderLayout.WEST); // 남은 일 수를 서쪽(왼쪽)에 배치
            add(titleLabel, BorderLayout.CENTER);
            setOpaque(false);

        }
    }
}