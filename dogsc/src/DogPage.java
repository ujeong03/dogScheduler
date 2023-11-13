import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

// 강아지 키우기 페이지 메인 클래스
public class DogPage {
    private JPanel contentPane;
    private JFrame frame;
    private DogCare dogCare;
    private JButton dogButton, feedButton, bathButton, playButton;
    private JLabel title, rewardLabel, levelLabel;
    private JProgressBar closenessProgressBar;
    private ControlReward controlReward;
    private DogLevel dogLevel;

    public DogPage() {
        this.controlReward = new ControlReward();
        this.dogCare = new DogCare();
        this.dogLevel = new DogLevel();

        // 강아지 페이지 frame 생성
        frame = new JFrame("강아지 키우기");
        frame.setSize(1200,800);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        // contentpane 생성
        contentPane = new JPanel();
        contentPane.setBackground(Color.GREEN);
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
        bathButton = new JButton(new ImageIcon("dog_image/soap.png"));
        bathButton.setContentAreaFilled(false);
        bathButton.setBorderPainted(false);

        // 장난감 버튼 생성
        playButton = new JButton(new ImageIcon("dog_image/ball.png"));
        playButton.setContentAreaFilled(false);
        playButton.setBorderPainted(false);

        // 레벨 라벨 생성
        levelLabel = new JLabel("level " + dogLevel.getLevel());
        levelLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        // 친밀도 프로그레스바 생성
        closenessProgressBar = new JProgressBar(0, 100);
        closenessProgressBar.setValue(dogLevel.getCloseness());
        closenessProgressBar.setStringPainted(true);
        closenessProgressBar.setBackground(Color.WHITE);
        feedButton.addActionListener((ActionEvent e) -> {
            dogCare.careDog();
            updateUI();
        });

        // 목욕 버튼 클릭 시 이벤트 발생
        bathButton.addActionListener(e -> {
            dogCare.careDog();
            updateUI();
        });

        // 장난감 버튼 클릭 시 이벤트 발생
        playButton.addActionListener(e -> {
            dogCare.careDog();
            updateUI();
        });

        // 강아지 버튼 클릭 시 이벤트 발생
        dogButton.addActionListener(e -> {
            dogCare.touchDog();
            updateUI();
        });

        // 제목 라벨 위치 설정 및 패널에 추가
        title.setBounds(400,70, 600,50);
        contentPane.add(title);

        // 강아지 버튼 위치 설정 및 패널에 추가
        dogButton.setBounds(260,150,480,480);
        contentPane.add(dogButton);

        // 보상 라벨 위치 설정 및 패널에 추가
        rewardLabel.setBounds(900, 100, 200, 50);
        contentPane.add(rewardLabel);

        // 먹이 버튼 위치 설정 및 패널에 추가
        feedButton.setBounds(900, 170, 155, 155);
        contentPane.add(feedButton);

        // 목욕 버튼 위치 설정 및 패널에 추가
        bathButton.setBounds(900, 330, 165, 165);
        contentPane.add(bathButton);

        // 장난감 버튼 위치 설정 및 패널에 추가
        playButton.setBounds(900, 520, 150,150);
        contentPane.add(playButton);

        // 레벨 라벨 위치 설정 및 패널에 추가
        levelLabel.setBounds(200, 680, 100, 30);
        contentPane.add(levelLabel);

        // 친밀도 프로그레스바 위치 설정 및 패널에 추가
        closenessProgressBar.setBounds(300, 680, 500, 30);
        contentPane.add(closenessProgressBar);
    }

    // 각 버튼 클릭 시 보상, 레벨, 친밀도의 변화 등의 UI 업데이트
    public void updateUI() {
        dogButton.setIcon(new ImageIcon(dogLevel.growUp(dogLevel.getLevel())));
        rewardLabel.setText("보상 : " + controlReward.getReward() + "개");
        levelLabel.setText("level " + dogLevel.getLevel());
        closenessProgressBar.setValue(dogLevel.getCloseness());
    }
}