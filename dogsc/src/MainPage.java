import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * {@code MainPage} 클래스는 애플리케이션의 메인 페이지를 나타냅니다.
 * 캘린더, 리마인더, 할 일 목록, 그리고 강아지 돌보기를 위한 패널을 포함하고 있습니다.
 */
public class MainPage {

    /**
     * 새로운 {@code MainPage}를 생성하고 그래픽 사용자 인터페이스를 초기화합니다.
     */
    public MainPage() {
        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("멍멍 ! 🐶 오늘도 화이팅 ");

            // 프레임을 전체 화면 크기로 설정
            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel contentPanel = new JPanel(new GridLayout(2, 2));

            // 각 패널을 콘텐츠 패널에 추가
            contentPanel.add(new MainCalendar());
            contentPanel.add(new Reminder());
            contentPanel.add(new TodoList());
            contentPanel.add(new MainDogCare());


            // 콘텐츠 패널을 메인 프레임에 추가하고 화면에 표시
            mainFrame.add(contentPanel);
            mainFrame.setVisible(true);
        });
    }
}

