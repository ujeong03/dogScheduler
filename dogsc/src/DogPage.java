import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

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

        frame = new JFrame("강아지 키우기");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(5,5,5,5));
        frame.setContentPane(contentPane);
        contentPane.setLayout(null);

        dogCare = new DogCare();

        title = new JLabel("MY PUPPY");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 50));
        dogButton = new JButton(new ImageIcon("dog_image/dog_big.png"));
        dogButton.setContentAreaFilled(false);
        dogButton.setBorderPainted(false);

        // Add buttons and labels to the buttonPanel
        rewardLabel = new JLabel("보상 : " + controlReward.getReward() + "개");
        rewardLabel.setFont(new Font("맑은 고딕", Font.BOLD, 27));

        feedButton = new JButton(new ImageIcon("dog_image/feed.png"));
        feedButton.setContentAreaFilled(false);
        feedButton.setBorderPainted(false);

        bathButton = new JButton(new ImageIcon("dog_image/soap.png"));
        bathButton.setContentAreaFilled(false);
        bathButton.setBorderPainted(false);

        playButton = new JButton(new ImageIcon("dog_image/ball.png"));
        playButton.setContentAreaFilled(false);
        playButton.setBorderPainted(false);

        levelLabel = new JLabel("level " + dogLevel.getLevel());
        levelLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        closenessProgressBar = new JProgressBar(0, 100);
        closenessProgressBar.setValue(dogLevel.getCloseness());
        closenessProgressBar.setStringPainted(true);
        closenessProgressBar.setBackground(Color.WHITE);
        closenessProgressBar.setVisible(true);


        feedButton.addActionListener(e -> {
            dogCare.feedDog();
            updateUI();
        });

        bathButton.addActionListener(e -> {
            dogCare.bathDog();
            updateUI();
        });

        playButton.addActionListener(e -> {
            dogCare.playDog();
            updateUI();
        });

        dogButton.addActionListener(e -> {
            dogCare.touchDog();
            updateUI();
        });

        JPanel panel = new JPanel();
        panel.setBackground(Color.GREEN);
        panel.setBounds(0,0, 1200,800);
        contentPane.add(panel);
        panel.setLayout(null);

        title.setBounds(400,40, 600,50);
        panel.add(title);
        dogButton.setBounds(230,100,570,570);
        panel.add(dogButton);
        rewardLabel.setBounds(925, 100, 200, 50);
        panel.add(rewardLabel);
        feedButton.setBounds(900, 150, 160, 160);
        panel.add(feedButton);
        bathButton.setBounds(900, 320, 160, 160);
        panel.add(bathButton);
        playButton.setBounds(900, 500, 160,160);
        panel.add(playButton);
        levelLabel.setBounds(200, 700, 100, 30);
        panel.add(levelLabel);
        closenessProgressBar.setBounds(300, 700, 500, 30);
        panel.add(closenessProgressBar);

        frame.setVisible(true);
    }


    public void updateUI() {
        rewardLabel.setText("보상 : " + controlReward.getReward() + "개");
        levelLabel.setText("level " + dogLevel.getLevel());
        closenessProgressBar.setValue(dogLevel.getCloseness());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DogPage());
    }
}