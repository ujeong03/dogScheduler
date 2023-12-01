import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * 강아지 키우기 페이지의 메인 클래스입니다.
 */
public class DogPage {
    private JPanel contentPane;
    private JFrame frame;
    private JButton dogButton, feedButton, soapButton, ballButton;
    private JLabel title, rewardLabel, levelLabel, heart, play, bath, eat;
    private JProgressBar closenessProgressBar;
    private DogCare dogCare;
    private ControlReward controlReward;
    private DogLevel dogLevel;

    /**
     * DogPage 클래스의 생성자입니다.
     * 강아지 키우기 페이지의 UI를 초기화하고 보여줍니다.
     */
    public DogPage() {
        this.controlReward = new ControlReward();
        this.dogCare = new DogCare();
        this.dogLevel = new DogLevel();
        dogCare.resetTouchCount();

        // 강아지 페이지 frame 생성
        frame = new JFrame("강아지 키우기");
        frame.setSize(1200,800);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        // contentpane 생성
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(5,5,5,5));
        frame.setContentPane(contentPane);
        contentPane.setLayout(null);

        // 제목 라벨 생성
        title = new JLabel("MY PUPPY");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 50));

        // 강아지 버튼 생성
        dogButton = new JButton(new ImageIcon(dogLevel.growUp(dogLevel.getLevel())));
        dogButton.setContentAreaFilled(false);
        dogButton.setBorderPainted(false);

        // 보상 라벨 생성
        rewardLabel = new JLabel("보상 : " + controlReward.getReward() + "개");
        rewardLabel.setFont(new Font("맑은 고딕", Font.BOLD, 27));

        // 먹이 버튼 생성
        feedButton = new JButton(new ImageIcon("dog_image/feed.png"));
        feedButton.setContentAreaFilled(false);
        feedButton.setBorderPainted(false);

        // 목욕 버튼 생성
        soapButton = new JButton(new ImageIcon("dog_image/soap.png"));
        soapButton.setContentAreaFilled(false);
        soapButton.setBorderPainted(false);

        // 장난감 버튼 생성
        ballButton = new JButton(new ImageIcon("dog_image/ball.png"));
        ballButton.setContentAreaFilled(false);
        ballButton.setBorderPainted(false);

        // 레벨 라벨 생성
        levelLabel = new JLabel("level " + dogLevel.getLevel());
        levelLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        // 친밀도 프로그레스바 생성
        closenessProgressBar = new JProgressBar(0, 100);
        closenessProgressBar.setValue(dogLevel.getCloseness());
        closenessProgressBar.setStringPainted(true);
        closenessProgressBar.setBackground(Color.WHITE);

        // 하트 이미지 라벨 생성
        heart = new JLabel(new ImageIcon("dog_image/heart.png"));

        // 먹기 이미지 라벨 생성
        eat = new JLabel(new ImageIcon("dog_image/eat.png"));

        // 목욕 이미지 라벨 생성
        bath = new JLabel(new ImageIcon("dog_image/bath.png"));

        // 놀기 이미지 라벨 생성
        play = new JLabel(new ImageIcon("dog_image/play.png"));

        // 먹이 버튼 클릭 시 이벤트 발생
        feedButton.addActionListener((ActionEvent e) -> {
            if (controlReward.getReward() > 0) {
                dogCare.careDog();
                showImagePopup(eat);
                updateUI(eat);
            } else {
                // 보상 0개일때 경고창 띄움
                controlReward.showRewardLimitDialog();
            }
        });

        // 목욕 버튼 클릭 시 이벤트 발생
        soapButton.addActionListener(e -> {
            if (controlReward.getReward() > 0) {
                dogCare.careDog();
                showImagePopup(bath);
                updateUI(bath);
            } else {
                // 보상 0개일 때 경고창 띄움
                controlReward.showRewardLimitDialog();
            }
        });

        // 장난감 버튼 클릭 시 이벤트 발생
        ballButton.addActionListener(e -> {
            if (controlReward.getReward() > 0) {
                dogCare.careDog();
                showImagePopup(play);
                updateUI(play);
            } else {
                // 보상 0개일 때 경고창 띄움
                controlReward.showRewardLimitDialog();
            }
        });

        // 강아지 버튼 클릭 시 이벤트 발생
        dogButton.addActionListener(e -> {
            if (dogCare.getTouchCount() < 10) {
                dogCare.touchDog();
                showImagePopup(heart);
                updateUI(heart);
            } else {
                // 10번 초과 클릭 시 경고창 띄움
                dogCare.showTouchLimitDialog();
            }
        });

        // 제목 라벨 위치 설정 및 패널에 추가
        title.setBounds(400,70, 600,50);
        contentPane.add(title);

        // 강아지 버튼 위치 설정 및 패널에 추가
        dogButton.setBounds(260,150,480,480);
        contentPane.add(dogButton);

        // 보상 라벨 위치 설정 및 패널에 추가
        rewardLabel.setBounds(910, 100, 200, 50);
        contentPane.add(rewardLabel);

        // 먹이 버튼 위치 설정 및 패널에 추가
        feedButton.setBounds(910, 170, 155, 155);
        contentPane.add(feedButton);

        // 목욕 버튼 위치 설정 및 패널에 추가
        soapButton.setBounds(910, 330, 165, 165);
        contentPane.add(soapButton);

        // 장난감 버튼 위치 설정 및 패널에 추가
        ballButton.setBounds(910, 520, 150,150);
        contentPane.add(ballButton);

        // 레벨 라벨 위치 설정 및 패널에 추가
        levelLabel.setBounds(200, 680, 100, 30);
        contentPane.add(levelLabel);

        // 친밀도 프로그레스바 위치 설정 및 패널에 추가
        closenessProgressBar.setBounds(300, 680, 500, 30);
        contentPane.add(closenessProgressBar);

        // 하트 이미지 라벨 위치 설정 및 패널에 추가
        heart.setBounds(250, 160, 150,150);
        contentPane.add(heart);
        heart.setVisible(false);

        // 먹기 이미지 라벨 위치 설정 및 패널에 추가
        eat.setBounds(700, 500, 150,150);
        contentPane.add(eat);
        eat.setVisible(false);

        // 목욕 이미지 라벨 위치 설정 및 패널에 추가
        bath.setBounds(700, 350, 150,150);
        contentPane.add(bath);
        bath.setVisible(false);

        // 놀기 이미지 라벨 위치 설정 및 패널에 추가
        play.setBounds(750, 250, 150,150);
        contentPane.add(play);
        play.setVisible(false);
    }

    // 먹이, 비누, 장난감 버튼을 눌렀을 때 해당 이미지 팝업
    private void showImagePopup(JLabel rewardImageLabel) {
        Timer timer = new Timer(1000, e -> rewardImageLabel.setVisible(false)); // 1초 뒤 이미지 숨김
        timer.setRepeats(false); // 한 번만 실행
        timer.start();
    }

    // 각 버튼 클릭 시 보상, 레벨, 친밀도의 변화 등의 UI 업데이트
    private void updateUI(JLabel rewardImageLabel) {
        dogButton.setIcon(new ImageIcon(dogLevel.growUp(dogLevel.getLevel())));
        rewardLabel.setText("보상 : " + controlReward.getReward() + "개");
        levelLabel.setText("level " + dogLevel.getLevel());
        closenessProgressBar.setValue(dogLevel.getCloseness());
        rewardImageLabel.setVisible(true); // 보상 사용 시 이미지 보이게 함
    }
}

