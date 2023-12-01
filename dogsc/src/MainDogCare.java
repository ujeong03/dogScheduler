import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainDogCare extends JPanel {

    private DogBG dogCarePanel;
    ImageIcon dogBGIcon = new ImageIcon("image/dogBG.png");
    Image dogBG = dogBGIcon.getImage();
    public class DogBG extends JPanel{
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(dogBG,0,0,getWidth(),getHeight(),this);


        }
    }

    public MainDogCare(){
        dogCarePanel = new DogBG();
        setLayout(new BorderLayout());


        ImageIcon dogIcon = new ImageIcon("dog_image/dog_big_costume.png");
        Image scaledImage = dogIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        dogIcon = new ImageIcon(scaledImage);
        JLabel dogLabel = new JLabel(dogIcon);
        dogCarePanel.setBorder(BorderFactory.createEmptyBorder(0,225,30,0));

        dogCarePanel.add(dogLabel, BorderLayout.CENTER);

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
