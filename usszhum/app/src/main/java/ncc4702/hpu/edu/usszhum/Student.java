package ncc4702.hpu.edu.usszhum;

/**
 * Created by Daniel Gallardo on 3/9/2018.
 */

public class Student extends Obstacle {//THIS IS A STUDENT OBSTACLE 1x1

    public Student(GameView view, int color, int hitPenalty, int y, int width, float velocityY) {
        super(view, color, 1,2, hitPenalty, y, width, velocityY);
    }
}
