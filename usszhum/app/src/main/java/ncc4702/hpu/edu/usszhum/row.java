package ncc4702.hpu.edu.usszhum;

import android.graphics.Canvas;

/**
 * Created by ReadUnpingco on 3/10/18.
 */

public class row extends GameElement{
    private int[] row;
    private float velocityY;
    private boolean onScreen;

    //constructor
    public row(GameView view, int color, int soundId, int x,
               int y, int width, int height, float velocityX, float velocityY){
        super(view, color, soundId, x, y, width, height,1);
        this.velocityY = velocityY;

    }



    @Override
    public void draw(Canvas canvas){
//        canvas.drawCircle(shape.left + getRadius(),
//                mShape.top + getRadius(), getRadius(), mPaint);
    }
}