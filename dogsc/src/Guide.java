import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * 사용자 가이드를 보여주는 프레임 클래스입니다.
 */
public class Guide extends JFrame {
    private JLabel imageLabel;
    private JButton nextButton;
    private JButton prevButton;

    private int currentImageIndex = 0;
    private String[] imagePaths = new String[]{
            "guide_page_img/guide_img1.png",
            "guide_page_img/test1.png"
    };

    /**
     * Guide 클래스의 생성자입니다.
     * 프레임의 기본 설정 및 컴포넌트 초기화를 수행합니다.
     */
    public Guide() {
        setTitle("사용법");

        // 프레임을 전체 화면 크기로 만들기
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        imageLabel = new JLabel();
        prevButton = new JButton("이전");
        nextButton = new JButton("다음");

        // 이전 버튼 클릭 시
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPreviousImage();
            }
        });

        // 다음 버튼 클릭 시
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNextImage();
            }
        });

        // 상단 패널에 이전 버튼과 다음 버튼 추가
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0))); // 왼쪽 여백 추가
        buttonPanel.add(prevButton);
        buttonPanel.add(Box.createHorizontalGlue()); // 가운데 여백 추가
        buttonPanel.add(nextButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0))); // 오른쪽 여백 추가

        // 전체 프레임에 버튼 패널과 중앙 패널 추가
        setLayout(new BorderLayout());
        add(imageLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.PAGE_START); // 상단에 버튼 패널 배치

        showImage(currentImageIndex);
        setVisible(true);
    }

    /**
     * 현재 인덱스에 해당하는 이미지를 화면에 표시합니다.
     *
     * @param index 표시할 이미지의 인덱스
     */
    private void showImage(int index) {
        String imagePath = imagePaths[index];
        ImageIcon imageIcon = new ImageIcon(imagePath);
        imageLabel.setIcon(imageIcon);
    }

    /**
     * 다음 이미지를 표시합니다.
     */
    private void showNextImage() {
        if (currentImageIndex < imagePaths.length - 1) {
            currentImageIndex++;
            showImage(currentImageIndex);
        }
    }

    /**
     * 이전 이미지를 표시합니다.
     */
    private void showPreviousImage() {
        if (currentImageIndex > 0) {
            currentImageIndex--;
            showImage(currentImageIndex);
        }
    }

    /**
     * Guide 클래스를 실행하는 메인 메서드입니다.
     *
     * @param args 명령행 인수 (사용하지 않음)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Guide();
        });
    }
}
