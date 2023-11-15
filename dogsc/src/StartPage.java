import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 프로그램 시작 시 나타나는 시작 페이지를 나타내는 클래스입니다.
 */
public class StartPage extends JFrame {

    /**
     * StartPage 클래스의 생성자입니다.
     */
    public StartPage() {
        // 창을 전체 화면으로 설정합니다.
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // 창의 제목을 설정합니다.
        setTitle("시작 페이지");

        // 창의 content pane 배경색을 흰색으로 설정합니다.
        getContentPane().setBackground(Color.WHITE);

        // 레이아웃을 BorderLayout으로 설정합니다. (동, 서, 남, 북, 중앙)
        setLayout(new FlowLayout());

        // 이미지 아이콘을 생성
        ImageIcon imageIcon = new ImageIcon("image/maindog.jpg"); // 상대 경로로 이미지 위치를 지정합니다.
        JLabel imageLabel = new JLabel(imageIcon){
            // 이미지의 크기에 맞게 레이블의 크기를 지정합니다.
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight());
            }
        };
        imageLabel.setLayout(null);

        // 버튼 패널을 생성하고 버튼들을 추가합니다.
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // 오른쪽 정렬로 버튼을 배치합니다.

        // '시작' 버튼을 생성하고 액션 리스너를 추가합니다.
        JButton startButton = new JButton("시작");
        startButton.setBounds(imageIcon.getIconWidth() - 200, 20, 160, 40);
        startButton.setPreferredSize(new Dimension(200, 50)); // 버튼의 크기를 설정합니다.
        startButton.setOpaque(true);
        startButton.setContentAreaFilled(true);
        startButton.setBorderPainted(false); // 필요에 따라 버튼의 테두리를 제거합니다.
        startButton.setBackground(Color.BLUE);
        startButton.setForeground(Color.WHITE);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // '시작' 버튼 클릭 시, 메인 페이지로 이동하는 코드를 작성하세요.
                dispose();
                new MainPage();
            }
        });

        // '사용자 가이드' 버튼을 생성하고 액션 리스너를 추가합니다.
        JButton guideButton = new JButton("사용자 가이드");
        guideButton.setPreferredSize(new Dimension(200, 50)); // 버튼의 크기를 설정합니다.
        guideButton.setOpaque(true);
        guideButton.setContentAreaFilled(true);
        guideButton.setBorderPainted(false); // 필요에 따라 버튼의 테두리를 제거합니다.
        guideButton.setBackground(Color.BLUE);
        guideButton.setForeground(Color.WHITE);

        guideButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // '사용자 가이드' 버튼 클릭 시, 사용자 가이드 페이지로 이동하는 코드를 작성하세요.
                new Guide();
            }
        });

        // 버튼 패널에 버튼들을 추가합니다.
        buttonPanel.add(startButton);
        buttonPanel.add(guideButton);

        // 창에 이미지 레이블과 버튼 패널을 추가합니다.
        add(imageLabel, BorderLayout.WEST); // 이미지 레이블을 창의 왼쪽에 배치합니다.

        // 버튼 패널을 남동쪽에 배치합니다.
        // 패딩을 추가하기 위해 추가적인 패널을 사용합니다.
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.EAST); // 이 패널을 창의 오른쪽에 배치합니다.

        // 기본 종료 작업을 설정합니다.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 창을 화면에 표시합니다.
        setVisible(true);
    }

    /**
     * 프로그램을 실행하는 메인 메서드입니다.
     *
     * @param args 명령행 인수
     */
    public static void main(String[] args) {
        // Swing 유틸리티를 사용하여 이벤트 디스패치 스레드에서 GUI를 시작합니다.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new StartPage(); // 시작 페이지 인스턴스를 생성합니다.
            }
        });
    }
}
