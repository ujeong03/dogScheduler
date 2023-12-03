import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarWindow extends JFrame {
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
        prevButton.addActionListener(e -> navigateMonths(-1));

        // 다음 월 버튼 설정
        nextButton = new JButton(">");
        nextButton.addActionListener(e -> navigateMonths(1));

        // 월 표시 레이블 설정
        monthLabel = new JLabel("", JLabel.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 24));

        monthPanel.add(Box.createHorizontalGlue());
        monthPanel.add(prevButton);
        monthPanel.add(Box.createHorizontalStrut(10));
        monthPanel.add(monthLabel);
        monthPanel.add(Box.createHorizontalStrut(10));
        monthPanel.add(nextButton);
        monthPanel.add(Box.createHorizontalGlue());

        add(monthPanel, BorderLayout.NORTH); // 월 헤더 패널을 윈도우의 상단에 추가

        calendarPanel = new JPanel();
        calendarPanel.setLayout(new BorderLayout());
        add(calendarPanel, BorderLayout.CENTER); // 캘린더 패널을 윈도우의 가운데에 추가

        // 요일 헤더 패널 설정
        JPanel headerPanel = new JPanel(new GridLayout(1, 7, 5, 5)); // 그리드 레이아웃을 사용해 1행 7열로 설정
        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, JLabel.CENTER); // 중앙 정렬된 요일 레이블
            if ("일".equals(dayName)) {
                dayLabel.setForeground(Color.RED); // 일요일은 빨간색으로 표시
            } else if ("토".equals(dayName)) {
                dayLabel.setForeground(Color.BLUE); // 토요일은 파란색으로 표시
            }
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

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MMMM", Locale.KOREA); // 날짜 형식 지정
        monthLabel.setText(sdf.format(calendar.getTime())); // 월 레이블에 현재 월 표시

        calendar.set(Calendar.DAY_OF_MONTH, 1); // 현재 월의 첫 날로 설정
        int startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 현재 월의 첫 날의 요일
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 현재 월의 최대 일자

        for (int i = 1; i < startDayOfWeek; i++) {
            daysPanel.add(new JLabel("")); // 시작 요일 전까지 공백 레이블 추가
        }

        for (int day = 1; day <= maxDay; day++) {
            JButton dayButton = new JButton(String.valueOf(day)); // 일자를 나타내는 버튼 생성
            dayButton.setHorizontalAlignment(SwingConstants.LEFT);
            dayButton.setVerticalAlignment(SwingConstants.TOP);

            if ((day + startDayOfWeek - 1) % 7 == 0) {
                dayButton.setForeground(Color.BLUE); // 토요일 파란색으로 표시
            } else if ((day + startDayOfWeek - 1) % 7 == 1) {
                dayButton.setForeground(Color.RED); // 일요일 빨간색으로 표시
            }

            final int dayFinal = day;
            dayButton.addActionListener(e -> openDateDetailDialog(dayFinal)); // 날짜 버튼에 액션 리스너 추가

            daysPanel.add(dayButton); // 일자 버튼을 일자 패널에 추가
        }

        calendarPanel.revalidate(); // 일자 패널을 다시 그리기
        calendarPanel.repaint(); // 일자 패널을 다시 그리기
    }

    // DateDetailDialog를 여는 메서드
    private void openDateDetailDialog(int day) {
        Calendar selectedDate = (Calendar) calendar.clone();
        selectedDate.set(Calendar.DAY_OF_MONTH, day);
        Date sqlDate = new Date(selectedDate.getTimeInMillis());

        DateDetailDialog detailDialog = new DateDetailDialog(this, sqlDate, calendarDB);
        detailDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalendarWindow()); // Swing 스레드에서 윈도우 생성
    }
}
