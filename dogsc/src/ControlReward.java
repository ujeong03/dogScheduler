import javax.swing.*;
import java.io.*;
import java.util.Scanner;

/**
 * ControlReward 클래스는 보상의 반환, 추가, 사용 등을 관리하는 클래스입니다.
 */
public class ControlReward {
    private int rewardCount;
    private String rewardPath;
    private JOptionPane optionPane;
    private JFrame frame;

    /**
     * ControlReward 클래스의 생성자입니다.
     * 파일 경로를 초기화합니다.
     */
    public ControlReward() {
        this.rewardPath = "dog_txt/reward.txt";
    }

    /**
     * 파일에서 현재 보상 개수를 읽고 반환합니다.
     *
     * @return 현재 보상 개수
     */
    public int getReward() {
        try {
            File rewardfile = new File(rewardPath);
            Scanner scanner = new Scanner(rewardfile);
            while (scanner.hasNext())
                this.rewardCount = scanner.nextInt();
            scanner.close();
            return this.rewardCount;
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            return -1;
        }
    }

    /**
     * 투두를 달성한 후 받은 보상을 추가하고 파일에 저장합니다.
     *
     * @param num 추가할 보상 개수
     */
    public void addReward(int num){
        try {
            rewardCount = getReward();
            rewardCount += num;

            System.out.println(num);
            FileWriter rewardFileWriter = new FileWriter(rewardPath, false);
            BufferedWriter bw = new BufferedWriter(rewardFileWriter);
            bw.write(Integer.toString(rewardCount));
            bw.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 사용한 보상만큼 감소시키고 파일에 저장합니다.
     */
    public void useReward() {
        try {
            FileWriter rewardFileWriter = new FileWriter(rewardPath, false);
            BufferedWriter bw = new BufferedWriter(rewardFileWriter);
            bw.write(Integer.toString(--this.rewardCount));
            bw.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 보상이 0개일 때 보상 사용 시 경고창을 띄웁니다.
     */
    public void showRewardLimitDialog() {
        optionPane.showMessageDialog(frame, "보상이 부족합니다. \nTodo를 달성하여 보상을 얻어보세요!", "안내", JOptionPane.WARNING_MESSAGE);
    }
}
