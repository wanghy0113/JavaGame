package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.uchicago.cs.java.finalproject.controller.Game;

import javax.imageio.ImageIO;


public class Bullet extends Sprite {

	  private final double FIRE_POWER = 25.0;
      private int attribute;
      private BufferedImage imageRight;
      private BufferedImage imageLeft;
      private BufferedImage imageUp;
      private BufferedImage imageDown;
	 
	
public Bullet(Falcon fal,int direction){
		
		super();
		attribute = direction;
        try
        {
            imageUp = ImageIO.read(new File("image/upfish.jpg")) ;
            imageDown = ImageIO.read(new File("image/downfish.jpg")) ;
            imageRight = ImageIO.read(new File("image/rightfish.jpg")) ;
            imageLeft = ImageIO.read(new File("image/leftfish.jpg")) ;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
		//a bullet expires after 20 frames
	    setExpire( 30 );
	    setRadius(25);
	    

	    //everything is relative to the falcon ship that fired the bullet
	    setDeltaX( fal.getDeltaX() +
	               Math.cos( Math.toRadians( fal.getOrientation() ) ) * FIRE_POWER );
	    setDeltaY( fal.getDeltaY() +
	               Math.sin( Math.toRadians( fal.getOrientation() ) ) * FIRE_POWER );
	    setCenter( new Point(fal.getCenter().x+30,fal.getCenter().y ));

	    //set the bullet orientation to the falcon (ship) orientation


	}

    //override the expire method - once an object expires, then remove it from the arrayList. 
	public void expire(){
 		if (getExpire() == 0)
 			CommandCenter.movFriends.remove(this);
		 else 
			setExpire(getExpire() - 1);
	}

    public int getAttribute()
    {
        return attribute;
    }

    public void draw(Graphics g)
    {
        if(attribute==0)
        {
            g.drawImage(imageRight,getCenter().x-getRadius(),getCenter().y-getRadius(),2*getRadius(),getRadius(),null);
        }
        else if (attribute==1)
        {
            g.drawImage(imageDown,getCenter().x-getRadius(),getCenter().y-getRadius(),getRadius(),2*getRadius(),null);
        }
        else if(attribute==2)
        {
            g.drawImage(imageLeft,getCenter().x-getRadius(),getCenter().y-getRadius(),2*getRadius()+10,getRadius(),null);
        }
        else if (attribute==3)
        {
            g.drawImage(imageUp,getCenter().x-getRadius(),getCenter().y-getRadius(),getRadius(),2*getRadius(),null);
        }
    }

}
