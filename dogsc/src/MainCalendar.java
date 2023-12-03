import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.spi.CalendarNameProvider;

/**
 * 메인 페이지의 캘린더를 위한 클래스
 */
public class MainCalendar extends JPanel {
   //날짜 조정
    private Date currentDate;
    private Date currentStartDate;

   // 버튼
    private JPanel calendarControlPanel;
    private RoundButton prevButton;
    private RoundButton nextButton;
    private ImageIcon openCalendarButton;

   //캘린더 패널
    private JPanel calendarPanel;

    //데이터베이스
    private CalendarDBConnection calendarDB;

    //폰트
    InputStream inputStream = getClass().getResourceAsStream("font/IM_Hyemin-Bold.ttf");
    Font calendarfont;

    {
        try {
            calendarfont = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(Font.BOLD,10);
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    /**
     * MainCalendar 클래스의 생성자
     * 기본적인 틀을 조정합니다.
     * 버튼과 캘린더 프레임이 만들어집니다.
     */
    public MainCalendar() {

        setLayout(new BorderLayout());

        calendarDB = new CalendarDBConnection();

        // 이번주의 첫날과 끝날을 가지고 초기화하기
        Calendar calendar = Calendar.getInstance();
        currentStartDate = getStartOfWeek(calendar.getTime());
        currentDate = currentStartDate;

        //메인 캘린더 버튼 컨트롤러
        calendarControlPanel = new JPanel();
        calendarControlPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0)); // Add padding around the control panel

        // 캘린더 버튼 생성
        openCalendarButton = new ImageIcon("image/Rectangle 1.png");
        Image scaledImage = openCalendarButton.getImage().getScaledInstance(200,60, Image.SCALE_SMOOTH);
        openCalendarButton = new ImageIcon(scaledImage);
        JLabel openCalendarLabel = new JLabel(openCalendarButton);
        // 캘린더 버튼 작동
        openCalendarLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //new CalendarWindow();
            }
        });


        // 이전 주 버튼
        prevButton = new RoundButton("<");
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPreviousWeek();
            }
        });


        // 다음 주 버튼
        nextButton = new RoundButton(">");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNextWeek();
            }
        });

        calendarControlPanel.add(prevButton);
        calendarControlPanel.add(openCalendarLabel);
        calendarControlPanel.add(nextButton);


        //요일
        JPanel dayOfWeekPanel = new JPanel();
        dayOfWeekPanel.setBackground(Color.WHITE);
        ImageIcon dayOfWeek = new ImageIcon("image/dayofweek.png");
        Image scaledDayOfWeekImage = dayOfWeek.getImage().getScaledInstance(800,50,Image.SCALE_SMOOTH);
        dayOfWeek = new ImageIcon(scaledDayOfWeekImage);
        JLabel dayOfWeekLabel = new JLabel(dayOfWeek);
        dayOfWeekPanel.add(dayOfWeekLabel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE); // Set background color
        topPanel.add(calendarControlPanel, BorderLayout.NORTH);
        topPanel.add(dayOfWeekPanel, BorderLayout.CENTER);


        // 캘린더 패널
        calendarPanel = new JPanel();
        calendarPanel.setOpaque(false);
        calendarPanel.setLayout(new GridLayout(2, 7));
        calendarPanel.setBounds(100, 200, 400, 500);
        add(calendarPanel, BorderLayout.CENTER);

        updateCalendar(); // 캘린더 업데이트

        // 버튼과 캘린더 패널을 프레임에 추가
        setBackground(Color.WHITE);
        calendarControlPanel.setBackground(Color.WHITE);
        calendarPanel.setBackground(Color.WHITE);
        add(topPanel, BorderLayout.NORTH);
        add(calendarPanel, BorderLayout.CENTER);

    }



    /**
     * 초기화를 위해 이번 주의 첫날을 가져옴
     */
    private Date getStartOfWeek(Date startDate) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        return calendar.getTime();
    }

    /**
     * 이전 주를 보여주기 위한 메서드
     */
    private void showPreviousWeek() {
        currentDate = getPreviousWeekStart(currentDate);
        currentStartDate = getPreviousWeekStart(currentStartDate);
        updateCalendar();
    }

    /**
     * 다음 주를 보여주기 위한 메서드
     */
    private void showNextWeek() {
        currentDate = getNextWeekStart(currentDate);
        currentStartDate = getNextWeekStart(currentStartDate);
        updateCalendar();
    }

    /**
     * 버튼의 클릭 등이 있을 때마다 캘린더의 내용을 업데이트 해야할 때
     */
    private void updateCalendar() {
        calendarPanel.removeAll();

        List<Date> currentWeekDates = getWeekDates(currentDate);
        List<Date> nextWeekDates = getWeekDates(getNextWeekStart(currentStartDate));

        displayWeek(currentWeekDates);
        displayWeek(nextWeekDates);

        // UI 업데이트
        revalidate();
        repaint();
    }


   /**
    * 해당하는 주의 일정을 보여줌
    *
    * 오늘에 해당하는 날짜는 분홍색으로 색칠
    * 일정의 텍스트 길이가 길 경우 스크롤하여 전체 내용을 볼 수 있게 함
    *
    * @param weekDates 해당하는 주의 리스트
    */

    private void displayWeek(List<Date> weekDates) {
        for (Date date : weekDates) {
            JPanel datePanel = new JPanel(new BorderLayout());

            datePanel.setBackground(Color.WHITE);
            JLabel dateLabel = new JLabel(new SimpleDateFormat("MM-dd").format(date), JLabel.LEFT);
            dateLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add border for better visibility

            if (isToday(date)) {
                dateLabel.setBackground(Color.PINK); // 오늘 날짜에 PINK로 하이라이트 하기
                dateLabel.setOpaque(true);
            }

            datePanel.add(dateLabel, BorderLayout.NORTH);

            // 일정을 추가하는 부분
            List<String> schedules = calendarDB.getSchedulesForDate(date);
            JPanel schedulesPanel = new JPanel(new GridLayout(schedules.size(), 1)); // 일정을 세로로 표시하기 위한 패널
            schedulesPanel.setBackground(new Color(252,247,244));


            for (String schedule : schedules) {
                JLabel scheduleLabel = new JLabel("V " + schedule);
                scheduleLabel.setFont(calendarfont);

                schedulesPanel.add(scheduleLabel);
            }

            JScrollPane scrollPane = new JScrollPane(schedulesPanel); // 스크롤 가능하도록 스크롤 패널에 추가
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // 수직 스크롤바 필요시 활성화
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // 수평 스크롤바 필요시 활성화
            datePanel.add(scrollPane, BorderLayout.CENTER);

            // 스크롤바를 숨김
            scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
            scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
            scrollPane.getVerticalScrollBar().setVisible(false);
            scrollPane.getHorizontalScrollBar().setVisible(false);

            calendarPanel.add(datePanel);
        }
    }


    /**
     * 일주일의 날짜 정보를 가져오기 위한 메서드
     *
     * @param startDate 시작 날짜
     * @return weekDates
     */
    private List<Date> getWeekDates(Date startDate) {
        // 주의 시작일을 기준으로 7일간의 일자를 가져옴
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 주의 시작일로 설정
        Date current = calendar.getTime();

        // 7일간의 일자를 리스트에 추가
        List<Date> weekDates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weekDates.add(current);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            current = calendar.getTime();
        }
        return weekDates;
    }

    /**
     * 이전 주의 시작 날짜를 가져오기 위한 메서드
     *
     * @param startDate
     * @return calendar.getTime()
     */
    private Date getPreviousWeekStart(Date startDate) {
        // 이전 주의 시작일을 가져옴
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_MONTH, -7); // 7일 전으로 이동
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 주의 시작일로 설정
        return calendar.getTime();
    }

    /**
     * 다음 주의 시작 날짜를 가져오기 위한 메서드
     * @param startDate
     * @return calendar.getTime()
     */
    private Date getNextWeekStart(Date startDate) {
        // 다음 주의 시작일을 가져옴
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DAY_OF_MONTH, 7); // 7일 후로 이동
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 주의 시작일로 설정
        return calendar.getTime();
    }

    /**
     * 오늘이 며칠인지 확인하기 위한 메서드
     *
     * @param date
     * @return
     */
    private boolean isToday(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                today.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH);
    }
}


