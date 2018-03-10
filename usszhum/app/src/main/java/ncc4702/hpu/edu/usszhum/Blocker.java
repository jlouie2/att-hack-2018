package ncc4702.hpu.edu.usszhum;

/**
 * Created by Daniel Gallardo on 2/25/2018.
 */

public class Blocker extends GameElement {
    private int missPenalty; //the miss penalty for this Blocker

    //Constructor
    public Blocker(CannonView view, int color, int missPenalty, int x, int y, int width, int length, float velocityY){
        super(view, color, CannonView.BLOCKER_SOUND_ID, x, y, width, length, velocityY);
        this.missPenalty = missPenalty;
    }

    //returns the miss penalty for this Blocker
    public int getMissPenalty(){
        return missPenalty;
    }

}
