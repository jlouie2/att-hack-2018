package ncc4702.hpu.edu.usszhum;

/**
 * Created by Daniel Gallardo on 2/25/2018.
 */

import android.graphics.Canvas;
import android.graphics.Rect;

public class Player extends GameElement{
    private float velocityX;
    //private boolean onScreen;

    //constructor
    //MODIFIED TO GET RID OF VELOCITY Y
    public Player(GameView view, int color, int soundId, int x, int y, int radius, float velocityX){
        super(view, color, soundId,x,y,1,2);
        this.velocityX = velocityX;
        //onScreen = true;POSSIBLY NOT NECESSARY
    }

    //get Player's radius KEEP THIS AS IT DRAWS THE PERSON
    private int getRadius(){
        return (shape.right-shape.left)/2;
    }

    //test whether Player collides with the given Gameelement
    public boolean collidesWith(GameElement element){
        return (Rect.intersects(shape, element.shape)&& velocityX > 0);
    }

//    //returns true if this Player is on the screen
//    public boolean isOnScreen(){
//        return onScreen;
//    }

//    //reverses the Player's horizontal velocity
//    public void reverseVelocityX(){
//        velocityX *=-1;
//    }

    //updates the Player's position
    //MAY NOT NEED THIS
    @Override
    public void update(double interval){
        super.update(interval);

        //update horizontal position
        shape.offset((int) (velocityX*interval), 0);

        //if Player goes off the screen
        //if(shape.top <0 || shape.left < 0 || shape.bottom > view.getScreenHeight() || shape.right > view.getScreenWidth())  onScreen = false;//set it to be removed
    }

    //draws the Player on the given canvas
    @Override
    public void draw(Canvas canvas){
        canvas.drawCircle(shape.left+getRadius(), shape.top+getRadius(),getRadius(),paint);
    }

}
