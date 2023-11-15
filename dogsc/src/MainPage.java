import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * {@code MainPage} 클래스는 애플리케이션의 메인 페이지를 나타냅니다.
 * 캘린더, 리마인더, 할 일 목록, 그리고 강아지 돌보기를 위한 패널을 포함하고 있습니다.
 */
public class MainPage {
    private JPanel calendarPanel;
    private JPanel reminderPanel;
    private JPanel todoPanel;
    private JPanel dogCarePanel;
    private JPanel mainFrame;

    /**
     * 새로운 {@code MainPage}를 생성하고 그래픽 사용자 인터페이스를 초기화합니다.
     */
    public MainPage() {
        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("메인 페이지");

            // 프레임을 전체 화면 크기로 설정
            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel contentPanel = new JPanel(new GridLayout(2, 2));

            // 리마인더 패널 생성 및 컴포넌트 추가
            // JPanel reminderPanel = new JPanel();
            // reminderPanel.add(new JLabel("리마인더"));

            JPanel dogCarePanel = new JPanel();
            // 강아지 이미지 아이콘 생성 및 크기 조정
            ImageIcon dogIcon = new ImageIcon("dog_image/dog_big_costume.png");
            Image scaledImage = dogIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
            dogIcon = new ImageIcon(scaledImage);
            JLabel dogLabel = new JLabel(dogIcon);
            dogCarePanel.add(dogLabel, BorderLayout.CENTER);

            // 강아지 이미지에 마우스 이벤트 리스너 추가
            dogLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    new DogPage();
                }
            });

            // 각 패널을 콘텐츠 패널에 추가
            contentPanel.add(new MainCalendar());
            contentPanel.add(new Reminder());
            contentPanel.add(new TodoList());
            contentPanel.add(dogCarePanel);

            // 콘텐츠 패널을 메인 프레임에 추가하고 화면에 표시
            mainFrame.add(contentPanel);
            mainFrame.setVisible(true);
        });
    }
}
