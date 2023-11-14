import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

// 보상으로 먹이, 목욕, 장난감 강아지에게 제공하거나 강아지를 쓰다듬은 후 친밀도 및 레벨 상승시키는 강아지 케어 클래스
public class DogCare {
    private ControlReward controlReward;
    private DogLevel dogLevel;
    private JOptionPane optionPane;
    private JFrame frame;
    private JDialog dialog;
    private int touchCount;
    private String touchPath;

    public DogCare() {
        this.controlReward = new ControlReward();
        this.dogLevel = new DogLevel();
        this.touchPath = "dog_txt/touch.txt";
        this.touchCount = this.getTouchCount();
    }

    // 먹이, 목욕, 장난감 버튼 클릭 시 보상 -1, 친밀도 +10. 친밀도 100 달성 시 레벨 +1
    public void careDog() {
        if (controlReward.getReward() > 0){
            controlReward.useReward();
            dogLevel.increaseCloseness(10);
            dogLevel.increaseLevel();
        }
    }

    // 강아지 버튼 클릭 시 친밀도 +1. 친밀도 100 달성 시 레벨 +1
    public void touchDog() {
        dogLevel.increaseCloseness(1);
        dogLevel.increaseLevel();
        this.addTouchCount();
    }

    public int getTouchCount() {
        try {
            File touchfile = new File(touchPath);
            Scanner scanner = new Scanner(touchfile);
            while (scanner.hasNext())
                this.touchCount = scanner.nextInt();
            scanner.close();
            return this.touchCount;
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            return -1;
        }
    }

    public void addTouchCount(){
        try {
            this.touchCount++;

            FileWriter touchFileWriter = new FileWriter(touchPath, false);
            BufferedWriter bw = new BufferedWriter(touchFileWriter);
            bw.write(Integer.toString(this.touchCount));
            bw.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void showTouchLimitDialog() {
        optionPane.showMessageDialog(frame, "오늘은 이미 10번을 쓰다듬었습니다.", "경고", JOptionPane.INFORMATION_MESSAGE);
    }
}