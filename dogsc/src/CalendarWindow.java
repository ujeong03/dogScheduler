import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CancellationException;

public class CalendarWindow extends JFrame {

    InputStream inputStream = getClass().getResourceAsStream("font/BMJUA_ttf.ttf");
    Font reminderfont;

    {
        try {
            reminderfont = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(Font.BOLD, 20);
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private Calendar selectedCalendar; // 선택된 날짜를 저장하는 변수
    private Calendar calendar; // 캘린더 객체
    private Date currentDate; // 날짜 정보
    private JLabel monthLabel; // 월 표시 레이블
    private JPanel calendarPanel; // 캘린더 패널
    private JButton prevButton, nextButton; // 이전 월, 다음 월 이동 버튼
    private final String[] dayNames = {"일", "월", "화", "수", "목", "금", "토"}; // 요일 이름 배열

    private CalendarDBConnection calendarDB; // 데이터베이스 연결 객체

    private List<JScrollPane> dayScrollPaneList; // JScrollPane 목록을 저장하는 리스트
    private JTextArea[][] dayTextAreaArray; // JTextArea 배열로 각 일자의 일정을 표시

    public CalendarWindow() {
        calendar = Calendar.getInstance(Locale.getDefault()); // 로케일에 따른 현재 날짜와 시간을 가지는 캘린더 객체 생성
        setTitle("캘린더");
        setSize(800, 1000); // 윈도우 크기 설정
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        calendarDB = new CalendarDBConnection(); // 데이터베이스 연결 객체 초기화

        // 월 헤더 패널 설정
        JPanel monthPanel = new JPanel();
        monthPanel.setBorder(BorderFactory.createLineBorder(Color.white, 2)); // 테두리 설정
        monthPanel.setLayout(new BoxLayout(monthPanel, BoxLayout.X_AXIS));

        // 이전 월 버튼 설정
        prevButton = new JButton("<");
        prevButton.addActionListener(e -> navigateMonths(-1));

        // 다음 월 버튼 설정
        nextButton = new JButton(">");
        nextButton.addActionListener(e -> navigateMonths(1));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월"); // 날짜 형식 지정
        calendar = Calendar.getInstance();
        String month = sdf.format(calendar.getTime());
        System.out.println("tadsf"+sdf.format(calendar.getTime()));
        System.out.println(month);

        monthLabel = new JLabel(month, JLabel.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 24));

        monthPanel.add(Box.createHorizontalGlue());
        monthPanel.add(prevButton);
        monthPanel.add(Box.createHorizontalStrut(10));
        monthPanel.add(monthLabel);
        monthPanel.add(Box.createHorizontalStrut(10));
        monthPanel.add(nextButton);
        monthPanel.add(Box.createHorizontalGlue());
        add(monthPanel,BorderLayout.NORTH);

        calendarPanel = new JPanel();
        calendarPanel.setLayout(new BorderLayout());
        add(calendarPanel, BorderLayout.CENTER); // 캘린더 패널을 윈도우의 가운데에 추가


        // 요일 헤더 패널 설정
        JPanel headerPanel = new JPanel(new GridLayout(1, 7, 5, 5)); // 그리드 레이아웃을 사용해 1행 7열로 설정
        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, JLabel.CENTER); // 중앙 정렬된 요일 레이블
            dayLabel.setFont(new Font("Arial", Font.BOLD, 16));
            dayLabel.setForeground(Color.BLACK); // 글자색을 검은색으로 설정
            headerPanel.add(dayLabel); // 요일 레이블을 요일 헤더 패널에 추가
        }

        calendarPanel.add(headerPanel, BorderLayout.NORTH); // 요일 헤더 패널을 캘린더 패널의 상단에 추가


        updateCalendar(); // 캘린더 업데이트
        setVisible(true); // 윈도우 표시
    }

    // 월 이동 메서드
    private void navigateMonths(int delta) {
        calendar.add(Calendar.MONTH, delta); // 현재 월에 delta를 더해 다음 또는 이전 월로 이동
        updateCalendar(); // 캘린더 업데이트
    }

    // 캘린더 업데이트 메서드
    protected void updateCalendar() {
        calendarPanel.removeAll();
        JPanel daysPanel = new JPanel(new GridLayout(0, 7, 5, 5)); // 그리드 레이아웃을 사용해 일자를 표시할 패널 생성
        daysPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 테두리와 여백 설정
        calendarPanel.add(daysPanel, BorderLayout.CENTER); // 일자 패널을 캘린더 패널의 가운데에 추가



        calendar.set(Calendar.DAY_OF_MONTH, 1); // 현재 월의 첫 날로 설정
        int startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 현재 월의 첫 날의 요일
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 현재 월의 최대 일자

        daysPanel.removeAll(); // 일자 패널 초기화

        for (int i = 1; i < startDayOfWeek; i++) {
            daysPanel.add(new JLabel("")); // 시작 요일 전까지 공백 레이블 추가
        }

        for (int day = 1; day <= maxDay; day++) {
            // 여기에서 날짜별로 일정을 표시할 패널과 스크롤 패널을 생성하고 설정
            JPanel datePanel = new JPanel(new BorderLayout());

            JLabel dateLabel = new JLabel(String.valueOf(day), JLabel.LEFT);

            datePanel.add(dateLabel, BorderLayout.NORTH);

            // 일정을 추가하는 부분
            List<String> schedules = calendarDB.getSchedulesForDate(calendar.getTime());
            JPanel schedulesPanel = new JPanel(new GridLayout(schedules.size(), 1)); // 일정을 세로로 표시하기 위한 패널

            for (String schedule : schedules) {
                JLabel scheduleLabel = new JLabel("V " + schedule);
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

            final int selectedDay = day; // 선택된 일자를 저장
            System.out.println(selectedDay+"skfWk");
            // 스크롤 패널에 마우스 클릭 이벤트를 추가
            scrollPane.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    openDateDetailDialog(selectedDay); // 선택된 일자를 전달하여 Detail 창을 엽니다.
                }
            });

            daysPanel.add(datePanel);
            calendar.add(Calendar.DAY_OF_MONTH, 1); // 다음 날로 이동
        }

        calendarPanel.revalidate(); // 일자 패널을 다시 그리기
        calendarPanel.repaint(); // 일자 패널을 다시 그리기
    }


    // DateDetailDialog를 여는 메서드
    private void openDateDetailDialog(int selectedDay) {
        // 현재 선택된 월과 선택된 일자를 사용하여 날짜 생성
        calendar = Calendar.getInstance();
        Calendar selectedCalendar = Calendar.getInstance(Locale.getDefault());
        selectedCalendar.setTime(calendar.getTime());
        selectedCalendar.set(Calendar.DAY_OF_MONTH, selectedDay);

        // 선택된 날짜 가져오기
        Date selectedDate = new Date(selectedCalendar.getTimeInMillis());
        System.out.println(selectedDate+"선택날짜");


        // DateDetailDialog 열기
        DateDetailDialog detailDialog = new DateDetailDialog(this, selectedDate, calendarDB);
        detailDialog.setVisible(true);
    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalendarWindow()); // Swing 스레드에서 윈도우 생성
    }
}

