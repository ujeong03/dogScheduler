import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Guide extends JFrame{
    private JLabel imageLabel;
    private JButton nextButton;
    private JButton prevButton;

    private int currentImageIndex = 0 ;
    private String[] imagePaths = new String[]{
            "guide_page_img/guide_img1.png",
            "guide_page_img/test1.png"
    };

    public Guide() {
        setTitle("사용법");

        // 프레임을 전체 화면 크기로 만들기
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        imageLabel = new JLabel();
        prevButton = new JButton("이전");
        nextButton = new JButton("다음");

        //이전 버튼 클릭 시
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPreviousImage();
            }
        });

        //다음 버튼 클릭 시

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNextImage();
            }
        });



        // 상단 패널에 이전 버튼과 다음 버튼 추가
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createRigidArea(new Dimension(10,0))); //왼쪽 여백 추가
        buttonPanel.add(prevButton);
        buttonPanel.add(Box.createHorizontalGlue()); //가운데 여백 추가
        buttonPanel.add(nextButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10,0))); //오른쪽 여백 추가


        //전체 프레임에 버튼 패널과 중앙 패널 추가
        setLayout(new BorderLayout());
        add(imageLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.PAGE_START); // 상단에 버튼 패널 배치

        showImage(currentImageIndex);
        setVisible(true);


    }

    private void showImage(int index) {
        String imagePath = imagePaths[index];
        ImageIcon imageIcon = new ImageIcon(imagePath);
        imageLabel.setIcon(imageIcon);
    }

    private void showNextImage() {
        if (currentImageIndex < imagePaths.length - 1) {
            currentImageIndex++;
            showImage(currentImageIndex);
        }
    }

    private void showPreviousImage() {
        if (currentImageIndex > 0) {
            currentImageIndex--;
            showImage(currentImageIndex);
        }
    }

    public static void main() {
        SwingUtilities.invokeLater(() -> {
            new Guide();
        });
    }
}