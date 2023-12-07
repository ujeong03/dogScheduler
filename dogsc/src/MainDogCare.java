import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 강아지 키우기 페이지로 넘어가기 위한 클래스
 * @author ujeong
 */
public class MainDogCare extends JPanel {

    /**강아지 케어 패널*/
    private DogBG dogCarePanel;
    /**강아지 이미지*/
    ImageIcon dogBGIcon = new ImageIcon("image/dogBG.png");
    /**강아지 이미지*/
    Image dogBG = dogBGIcon.getImage();
    /**강아지 패널 배경화면을 위한 클래스*/
    public class DogBG extends JPanel{
        /**배경화면 채우기*/
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(dogBG,0,0,getWidth(),getHeight(),this);


        }
    }

    /**
     * MainDogCare 생성자
     * 이미지를 클릭했을 때 DogPage 클래스로 이동
     */
    public MainDogCare(){
        dogCarePanel = new DogBG();
        setLayout(new BorderLayout());

        ImageIcon dogIcon = new ImageIcon("dog_image/dog_big_costume.png");
        Image scaledImage = dogIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        dogIcon = new ImageIcon(scaledImage);
        JLabel dogLabel = new JLabel(dogIcon);
        dogCarePanel.setBorder(BorderFactory.createEmptyBorder(0,225,30,0));
        dogCarePanel.add(dogLabel, BorderLayout.CENTER);

        //강아지 이미지를 클릭했을 때
        dogLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new DogPage();
            }
        });

        dogCarePanel.setLayout(new BoxLayout(dogCarePanel,BoxLayout.X_AXIS));

        add(dogCarePanel);

    }

}
