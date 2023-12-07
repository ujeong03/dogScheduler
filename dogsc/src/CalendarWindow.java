import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CancellationException;
/**
 * CalendarWindow 클래스는 캘린더 창을 나타내는 Swing 프레임입니다.
 */
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

    private Calendar calendar; // 캘린더 객체
    private JLabel monthLabel; // 월 표시 레이블
    private JPanel calendarPanel; // 캘린더 패널
    private JButton prevButton, nextButton; // 이전 월, 다음 월 이동 버튼
    private final String[] dayNames = {"일", "월", "화", "수", "목", "금", "토"}; // 요일 이름 배열

    private CalendarDBConnection calendarDB; // 데이터베이스 연결 객체


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
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calendar.add(Calendar.MONTH, -1); // 캘린더의 월을 하나 감소시킵니다.
                updateCalendar(); // 캘린더를 업데이트합니다.
            }
        });

        // 다음 월 버튼 설정
        nextButton = new JButton(">");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calendar.add(Calendar.MONTH, 1); // 캘린더의 월을 하나 증가시킵니다.
                updateCalendar(); // 캘린더를 업데이트합니다.
            }
        });

        // 월 표시 레이블 설정
        monthLabel = new JLabel("", JLabel.CENTER);
        monthLabel.setFont(reminderfont);

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
            dayLabel.setFont(reminderfont);
            dayLabel.setForeground(Color.BLACK); // 글자색을 검은색으로 설정
            headerPanel.add(dayLabel); // 요일 레이블을 요일 헤더 패널에 추가
        }

        calendarPanel.add(headerPanel, BorderLayout.NORTH); // 요일 헤더 패널을 캘린더 패널의 상단에 추가


        updateCalendar(); // 캘린더 업데이트
        setVisible(true); // 윈도우 표시
    }


    // 캘린더 업데이트 메서드
    // 캘린더 업데이트 메서드
    protected void updateCalendar() {
        calendarPanel.removeAll();
        JPanel daysPanel = new JPanel(new GridLayout(0, 7, 5, 5)); // 그리드 레이아웃을 사용해 일자를 표시할 패널 생성
        daysPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 테두리와 여백 설정
        calendarPanel.add(daysPanel, BorderLayout.CENTER); // 일자 패널을 캘린더 패널의 가운데에 추가

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월"); // 날짜 형식 지정
        monthLabel.setText(sdf.format(calendar.getTime())); // 월 레이블에 현재 월 표시

        Calendar tempCalendar = (Calendar)calendar.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1); // 현재 월의 첫 날로 설정
        int startDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK); // 현재 월의 첫 날의 요일
        int maxDay = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 현재 월의 최대 일자

        for (int i = 1; i < startDayOfWeek; i++) {
            daysPanel.add(new JLabel("")); // 시작 요일 전까지 공백 레이블 추가
        }

        for (int day = 1; day <= maxDay; day++) {
            JPanel datePanel = new JPanel(new BorderLayout());
            JLabel dateLabel = new JLabel(String.valueOf(day), JLabel.LEFT);
            datePanel.add(dateLabel, BorderLayout.NORTH);

            // 일정을 추가하는 부분
            tempCalendar.set(Calendar.DAY_OF_MONTH, day);
            List<String> schedules = calendarDB.getSchedulesForDate(tempCalendar.getTime());
            JPanel schedulesPanel = new JPanel(new GridLayout(schedules.size(), 1)); // 일정을 세로로 표시하기 위한 패널

            for (String schedule : schedules) {
                JLabel scheduleLabel = new JLabel(" " + schedule);
                schedulesPanel.add(scheduleLabel);
            }

            JScrollPane scrollPane = new JScrollPane(schedulesPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            datePanel.add(scrollPane, BorderLayout.CENTER);


            final int selectedDay = day; // 선택된 일자를 저장

            // 스크롤 패널에 마우스 클릭 이벤트를 추가
            scrollPane.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    openDateDetailDialog(selectedDay); // 선택된 일자를 전달하여 Detail 창을 엽니다.
                }
            });

            daysPanel.add(datePanel);
        }


        calendarPanel.revalidate();
        calendarPanel.repaint();
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



        // DateDetailDialog 열기
        DateDetailDialog detailDialog = new DateDetailDialog(this, selectedDate, calendarDB);
        detailDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalendarWindow()); // Swing 스레드에서 윈도우 생성
    }
}
