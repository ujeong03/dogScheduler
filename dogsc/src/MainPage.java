import javax.swing.*;
import java.awt.*;

public class MainPage {
    private JPanel calendarPaner;
    private JPanel reminderPanel;
    private JPanel todoPanel;
    private JPanel dogCarePanel;
    private JPanel mainFrame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("메인 페이지");

            // 프레임을 전체 화면 크기로 만들기
            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel contentPanel = new JPanel(new GridLayout(2, 2));

            JPanel calendarPanel = new JPanel();
            // 캘린더 컴포넌트들을 calendarPanel에 추가
            calendarPanel.add(new JLabel("캘린더"));

            JPanel reminderPanel = new JPanel();
            // 리마인더 관련 컴포넌트들을 reminderPanel에 추가
            reminderPanel.add(new JLabel("리마인더"));


            JPanel dogCarePanel = new JPanel();
            // 강아지 키우기 관련 컴포넌트들을 dogCarePanel에 추가
            dogCarePanel.add(new JLabel("강아지 키우기"));

            contentPanel.add(calendarPanel);
            contentPanel.add(reminderPanel);
            contentPanel.add(new TodoList());
            contentPanel.add(dogCarePanel);

            mainFrame.add(contentPanel);

            mainFrame.setVisible(true);
        });
    }
}