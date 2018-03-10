package ncc4702.hpu.edu.usszhum;

/**
 * Created by Daniel Gallardo on 2/25/2018.
 */

import android.graphics.Canvas;
import android.graphics.Rect;

public class Cannonball extends GameElement{
    private float velocityX;
    private boolean onScreen;

    //constructor
    public Cannonball(CannonView view, int color, int soundId, int x, int y, int radius, float velocityX, float velocityY){
        super(view, color, soundId,x,y, 2*radius, 2*radius,velocityY);
        this.velocityX = velocityX;
        onScreen = true;
    }

    //get Cannonball's radius
    private int getRadius(){
        return (shape.right-shape.left)/2;
    }

    //test whether Cannonball collides with the given Gameelement
    public boolean collidesWith(GameElement element){
        return (Rect.intersects(shape, element.shape)&& velocityX > 0);
    }

    //returns true if this Cannonball is on the screen
    public boolean isOnScreen(){
        return onScreen;
    }

    //reverses the Cannonball's horizontal velocity
    public void reverseVelocityX(){
        velocityX *=-1;
    }

    //updates the Cannonball's position
    @Override
    public void update(double interval){
        super.update(interval);

        //update horizontal position
        shape.offset((int) (velocityX*interval), 0);

        //if Cannonball goes off the screen
        if(shape.top <0 || shape.left < 0 || shape.bottom > view.getScreenHeight() || shape.right > view.getScreenWidth())  onScreen = false;//set it to be removed
    }

    //draws the Cannonball on the given canvas
    @Override
    public void draw(Canvas canvas){
        canvas.drawCircle(shape.left+getRadius(), shape.top+getRadius(),getRadius(),paint);
    }

}
