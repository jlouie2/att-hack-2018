package ncc4702.hpu.edu.usszhum;

/**
 * Created by Daniel Gallardo on 3/9/2018.
 */

public class ThreeBlock extends Obstacle {//1x3 obstacle

    public ThreeBlock(GameView view, int color, int hitPenalty, int y, int width) {
        super(view, color, hitPenalty, y, width, 1, 2);
    }
}
