import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

public class CalendarWindow extends JFrame {
    private Calendar calendar;   // 캘린더 객체
    private JLabel monthLabel;   // 월 표시 레이블
    private JPanel calendarPanel; // 캘린더 패널
    private JButton prevButton, nextButton; // 이전/다음 월 버튼
    private final String[] dayNames = {"일", "월", "화", "수", "목", "금", "토"}; // 요일 배열

    // DateDetailDialog 객체 선언
    private DateDetailDialog dateDetailDialog;

    public CalendarWindow() {
        calendar = Calendar.getInstance(Locale.getDefault()); // 기본 캘린더 인스턴스 생성
        setTitle("캘린더");  // 윈도우 타이틀 설정
        setSize(800, 1000); // 윈도우 크기 설정
        setLayout(new BorderLayout()); // 레이아웃 매니저 설정
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 윈도우 닫기 버튼 설정

        JPanel monthPanel = new JPanel(); // 월 표시 패널 생성
        monthPanel.setBorder(BorderFactory.createLineBorder(Color.white, 1)); // 테두리 설정
        monthPanel.setLayout(new BoxLayout(monthPanel, BoxLayout.X_AXIS)); // 가로 정렬을 위한 레이아웃 설정

        prevButton = new JButton("<"); // 이전 달로 이동 버튼 생성
        prevButton.addActionListener(e -> navigateMonths(-1)); // 액션 리스너 추가
        nextButton = new JButton(">"); // 다음 달로 이동 버튼 생성
        nextButton.addActionListener(e -> navigateMonths(1)); // 액션 리스너 추가

        monthLabel = new JLabel("", JLabel.CENTER); // 월과 연도 표시 레이블 생성
        monthLabel.setFont(new Font("Arial", Font.BOLD, 24)); // 폰트 설정

        monthPanel.add(Box.createHorizontalGlue()); // 가로 방향으로 공간 확보
        monthPanel.add(prevButton); // 이전 달 버튼 추가
        monthPanel.add(Box.createHorizontalStrut(10)); // 간격 추가
        monthPanel.add(monthLabel); // 월과 연도 표시 레이블 추가
        monthPanel.add(Box.createHorizontalStrut(10)); // 간격 추가
        monthPanel.add(nextButton); // 다음 달 버튼 추가
        monthPanel.add(Box.createHorizontalGlue()); // 가로 방향으로 공간 확보

        add(monthPanel, BorderLayout.NORTH); // 월 표시 패널을 윈도우 상단에 추가

        calendarPanel = new JPanel(); // 캘린더 패널 생성
        calendarPanel.setLayout(new BorderLayout()); // 레이아웃 설정
        add(calendarPanel, BorderLayout.CENTER); // 캘린더 패널을 윈도우 중앙에 추가

        JPanel headerPanel = new JPanel(new GridLayout(1, 7, 5, 5)); // 요일 표시 헤더 패널 생성
        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, JLabel.CENTER); // 요일 레이블 생성
            if ("일".equals(dayName)) {
                dayLabel.setForeground(Color.RED); // 일요일은 빨간색으로 설정
            } else if ("토".equals(dayName)) {
                dayLabel.setForeground(Color.BLUE); // 토요일은 파란색으로 설정
            }
            headerPanel.add(dayLabel); // 요일 레이블을 헤더 패널에 추가
        }
        calendarPanel.add(headerPanel, BorderLayout.NORTH); // 헤더 패널을 캘린더 패널 상단에 추가

        JPanel daysPanel = new JPanel(new GridLayout(0, 7, 5, 5)); // 날짜 버튼을 포함할 패널 생성
        daysPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 패널 테두리 설정
        calendarPanel.add(daysPanel, BorderLayout.CENTER); // 날짜 버튼 패널을 캘린더 패널 중앙에 추가

        // DateDetailDialog 객체 초기화
        JButton someButton = new JButton("Calendar Button"); // 예시로 버튼 생성
        dateDetailDialog = new DateDetailDialog(this, "날짜 정보", true, 0, someButton);

        updateCalendar(); // 캘린더 업데이트
        setVisible(true); // 윈도우 표시
    }

    private void navigateMonths(int delta) {
        calendar.add(Calendar.MONTH, delta); // 이전 또는 다음 달로 이동
        updateCalendar(); // 캘린더 업데이트
    }

    private void updateCalendar() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MMMM", Locale.KOREA); // 월과 연도 표시 포맷 설정
        monthLabel.setText(sdf.format(calendar.getTime())); // 월 표시 레이블 업데이트

        JPanel daysPanel = (JPanel) calendarPanel.getComponent(1); // 날짜 버튼 패널 가져오기
        daysPanel.removeAll(); // 기존 버튼 삭제

        calendar.set(Calendar.DAY_OF_MONTH, 1); // 현재 월의 첫 날로 설정
        int startDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 현재 월의 첫 날의 요일 구하기
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // 현재 월의 마지막 날 구하기

        // 달력의 시작 요일 전까지 빈 칸으로 채우기
        for (int i = 1; i < startDayOfWeek; i++) {
            daysPanel.add(new JLabel(""));
        }

        // 실제 날짜 버튼 추가
        for (int day = 1; day <= maxDay; day++) {
            JButton dayButton = new JButton(String.valueOf(day));
            dayButton.setHorizontalAlignment(SwingConstants.LEFT); // 텍스트 정렬 설정
            dayButton.setVerticalAlignment(SwingConstants.TOP);

            final int selectedDay = day;
            dayButton.addActionListener(e -> showDateWindow(selectedDay)); // 날짜 클릭 시 이벤트 처리

            // 일요일과 토요일의 텍스트 색상 설정
            if ((day + startDayOfWeek - 1) % 7 == 0) {
                dayButton.setForeground(Color.BLUE); // 일요일 파란색
            } else if ((day + startDayOfWeek - 1) % 7 == 1) {
                dayButton.setForeground(Color.RED); // 토요일 빨간색
            }

            daysPanel.add(dayButton); // 날짜 버튼 추가
        }

        daysPanel.revalidate(); // 패널의 변경 사항 적용
        daysPanel.repaint();    // 패널 다시 그리기
    }

    // DateDetailDialog 열기
    private void showDateWindow(int day) {
        // DateDetailDialog의 날짜 정보 업데이트
        dateDetailDialog.updateDateLabel(day);

        // DateDetailDialog를 보이도록 설정
        dateDetailDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalendarWindow());
    }
}




