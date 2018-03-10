package ncc4702.hpu.edu.usszhum;

/**
 * Created by Daniel Gallardo on 2/25/2018.
 */

public class Target extends GameElement {
    private int hitReward; //the hit reward for this target

    //constructor
    public Target(GameView view, int color, int hitReward, int x, int y, int width, int length, float velocityY){
        super(view, color, GameView.TARGET_SOUND_ID, x, y, width, length, velocityY);
        this.hitReward = hitReward;
    }

    //returns the hit reward for this Target
    public int getHitReward(){
        return hitReward;
    }
}
