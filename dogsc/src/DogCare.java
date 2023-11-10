// 보상으로 먹이, 목욕, 장난감 강아지에게 제공하거나 강아지를 쓰다듬은 후 친밀도 및 레벨 상승시키는 강아지 케어 클래스
public class DogCare {
    private ControlReward controlReward;
    private DogLevel dogLevel;

    public DogCare() {
        this.controlReward = new ControlReward();
        this.dogLevel = new DogLevel();
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
    }
}