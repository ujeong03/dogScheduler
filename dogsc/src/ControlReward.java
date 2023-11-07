import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ControlReward {
    public int rewardCount;
    public String rewardPath;

    public ControlReward() {
        this.rewardPath = "dog_txt/reward.txt";
    }

    // 현재 보상 개수 반환
    public int getReward() {
        try {
            File rewardfile = new File(rewardPath);
            Scanner scanner = new Scanner(rewardfile);
            while (scanner.hasNext())
                rewardCount = scanner.nextInt();
            scanner.close();
            return rewardCount;
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            return -1;
        }
    }

    //todo 투두 달성하고 보상 받은 만큼 개수 증가 후 파일에 저장
    public void addReward(int num){
        rewardCount = getReward();
        rewardCount += num;

    }

    // todo 보상 사용 시 개수 감소 후 파일에 저장
    public void useReward() {
        rewardCount = getReward();

        if (rewardCount > 0) {
            rewardCount--;

        }
    }
}