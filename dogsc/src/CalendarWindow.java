import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

class CalendarWindow extends JFrame {
    private Calendar calendar; // 캘린더 객체를 저장할 변수입니다.
    private JLabel monthLabel; // 현재 월을 표시할 레이블입니다.
    private JButton prevButton, nextButton; // 이전, 다음 달로 이동하는 버튼입니다.

    public CalendarWindow() {
        calendar = Calendar.getInstance(Locale.getDefault()); // 현재 로케일에 맞는 캘린더 인스턴스를 가져옵니다.
        setTitle("캘린더"); // 윈도우 타이틀을 설정합니다.
        setSize(300, 400); // 윈도우 크기를 설정합니다.
        setLayout(new BorderLayout()); // 레이아웃 매니저를 BorderLayout으로 설정합니다.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 윈도우 닫기 버튼을 눌렀을 때 동작을 설정합니다.

        // 월을 표시하는 패널을 설정합니다.
        JPanel monthPanel = new JPanel();
        prevButton = new JButton("<"); // 이전 달로 이동하는 버튼을 생성합니다.
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calendar.add(Calendar.MONTH, -1); // 캘린더의 월을 하나 감소시킵니다.
                updateCalendar(); // 캘린더를 업데이트합니다.
            }
        });
        nextButton = new JButton(">"); // 다음 달로 이동하는 버튼을 생성합니다.
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calendar.add(Calendar.MONTH, 1); // 캘린더의 월을 하나 증가시킵니다.
                updateCalendar(); // 캘린더를 업데이트합니다.
            }
        });

        monthLabel = new JLabel("", JLabel.CENTER); // 현재 월을 표시할 레이블을 생성합니다.
        monthPanel.add(prevButton); // 이전 버튼을 패널에 추가합니다.
        monthPanel.add(monthLabel); // 월 레이블을 패널에 추가합니다.
        monthPanel.add(nextButton); // 다음 버튼을 패널에 추가합니다.
        add(monthPanel, BorderLayout.NORTH); // 만든 패널을 윈도우의 상단에 배치합니다.

        updateCalendar(); // 캘린더를 처음 업데이트합니다.
        setVisible(true); // 윈도우를 화면에 표시합니다.
    }

    private void updateCalendar() {
        // 현재 캘린더의 월과 연도를 표시합니다.
        monthLabel.setText(new SimpleDateFormat("MMMM yyyy").format(calendar.getTime()));
        // 실제 캘린더 뷰를 업데이트하는 코드를 여기에 추가합니다.
    }

    // 특정 날짜를 클릭했을 때 호출되어 작은 정보 창을 표시하는 메서드입니다.
    private void showDateWindow(int day) {
        JDialog dateDialog = new JDialog(this, "날짜 정보", true); // 다이얼로그를 생성합니다.
        dateDialog.setSize(200, 100); // 다이얼로그 크기를 설정합니다.
        dateDialog.setLayout(new FlowLayout()); // 레이아웃 매니저를 FlowLayout으로 설정합니다.
        dateDialog.add(new JLabel("선택한 날짜: " + day)); // 선택한 날짜를 표시하는 레이블을 추가합니다.
        // 필요한 추가 정보나 컴포넌트를 다이얼로그에 추가할 수 있습니다.
        dateDialog.setVisible(true); // 다이얼로그를 화면에 표시합니다.
    }

    public static void main(String[] args) {
        // 이벤트 디스패치 스레드(EDT)에서 GUI를 실행하기 위한 코드입니다.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CalendarWindow(); // 캘린더 윈도우 인스턴스를 생성하고 보여줍니다.
            }
        });
    }
}


