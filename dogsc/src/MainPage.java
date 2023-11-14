import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainPage {
    private JPanel calendarPanel;
    private JPanel reminderPanel;
    private JPanel todoPanel;
    private JPanel dogCarePanel;
    private JPanel mainFrame;

    public static void main() {
        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("메인 페이지");

            // 프레임을 전체 화면 크기로 만들기
            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel contentPanel = new JPanel(new GridLayout(2, 2));



//            JPanel reminderPanel = new JPanel();
//            // 리마인더 관련 컴포넌트들을 reminderPanel에 추가
//            reminderPanel.add(new JLabel("리마인더"));



            JPanel dogCarePanel = new JPanel();
            ImageIcon dogIcon = new ImageIcon("dog_image/dog_big_costume.png");
            Image scaledImage = dogIcon.getImage().getScaledInstance(300,300,Image.SCALE_SMOOTH);
            dogIcon = new ImageIcon(scaledImage);
            JLabel dogLabel = new JLabel(dogIcon);
            dogCarePanel.add(dogLabel, BorderLayout.CENTER);


            dogLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    new DogPage();
                }
            });


            contentPanel.add(new MainCalendar());
            contentPanel.add(new Reminder());
            contentPanel.add(new TodoList());
            contentPanel.add(dogCarePanel);


            mainFrame.add(contentPanel);

            mainFrame.setVisible(true);
        });
    }
}