import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;

/**
 * 멍멍 사용법을 위한 클래스
 * @author ujeong
 */
public class Guide extends JFrame {


    /**버튼 조작을 위한 필드*/
    private RoundButton nextButton;
    /**버튼 조작을 위한 필드*/
    private RoundButton prevButton;

    /**폰트 설정 위한 필드*/
    InputStream inputStream = getClass().getResourceAsStream("font/IM_Hyemin-Bold.ttf");
    /**폰트 설정 위한 필드*/
    Font guidefont;
    {
        try {
            guidefont = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(Font.BOLD,20);
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**이미지 조정을 위한 필드*/
    private JLabel imageLabel;
    /**이미지 조정을 위한 필드*/
    private int currentImageIndex = 0;
    /**이미지 조정을 위한 필드*/
    private String[] imagePaths = new String[]{
            "image/guide_img/guide1.jpg",
            "image/guide_img/guide2.jpg",
            "image/guide_img/guide3.jpg",
            "image/guide_img/guide4.jpg",
            "image/guide_img/guide5.jpg",
            "image/guide_img/guide6.jpg",
            "image/guide_img/guide7.jpg",
            "image/guide_img/guide8.jpg",
            "image/guide_img/guide9.jpg",
            "image/guide_img/guide10.jpg"
    };

    /**
     * Guide 클래스의 생성자
     * 이미지, 이전 및 다음 버튼
     */
    public Guide() {
        setTitle("멍멍 ! 🐶 사용자 가이드");

        //프레임을 전체 화면 크기로 만들기
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        prevButton = new RoundButton("이전");
        prevButton.setFont(guidefont);
        nextButton = new RoundButton("다음");
        nextButton.setFont(guidefont);

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


        //상단 패널에 이전 버튼과 다음 버튼 추가
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 700, 0, 0));
        buttonPanel.add(prevButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(40, 0)));
        buttonPanel.add(nextButton);


        // 전체 프레임에 버튼 패널과 중앙 패널 추가
        setLayout(new BorderLayout());
        add(imageLabel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.PAGE_START); // 상단에 버튼 패널 배치

        showImage(currentImageIndex);
        setVisible(true);
    }

    /**
     * 현재 인덱스에 해당하는 이미지 표시
     *
     * @param index 표시할 이미지의 인덱스
     */
    private void showImage(int index) {
        String imagePath = imagePaths[index];
        ImageIcon imageIcon = new ImageIcon(imagePath);
        imageLabel.setIcon(imageIcon);
    }

    /**
     * 다음 이미지 표시
     */
    private void showNextImage() {
        if (currentImageIndex < imagePaths.length - 1) {
            currentImageIndex++;
            showImage(currentImageIndex);
        }
    }

    /**
     * 이전 이미지 표시
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
