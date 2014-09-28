package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.uchicago.cs.java.finalproject.controller.Game;

import javax.imageio.ImageIO;


public class NewShipFloater extends Sprite {


    private BufferedImage image;
    private int type;

	public NewShipFloater() {

		super();
        int judge = Game.R.nextInt(10);
        if(judge<4) type=2;else if(judge>=4&&judge<7)type=1;else if(judge>=7)type=0;
        try
        {
            if(type==0)
            {
                image = ImageIO.read(new File("image/lanternfish.png")) ;
            }
            else if(type==1)
            {
                image = ImageIO.read(new File("image/bigfish.jpg")) ;
            }
            else if(type==2)
            {
                image = ImageIO.read(new File("image/takifugu.jpg"));
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
		setExpire(250);
		setRadius(40);


		int nX = Game.R.nextInt(10);
		int nY = Game.R.nextInt(10);
		
		//set random DeltaX
		if (nX % 2 == 0)
			setDeltaX(nX);
		else
			setDeltaX(-nX);

		//set rnadom DeltaY
		if (nY % 2 == 0)
			setDeltaY(nY);
		else
			setDeltaY(-nY);
		
		//set random spin
	/*	if (nS % 2 == 0)
			setSpin(nS);
		else
			setSpin(-nS);   */

		//random point on the screen
		setCenter(new Point(Game.R.nextInt(Game.DIM.width),
				Game.R.nextInt(Game.DIM.height)));

		//random orientation 
		// setOrientation(Game.R.nextInt(360));

	}



	//override the expire method - once an object expires, then remove it from the arrayList.
	@Override
	public void expire() {
		if (getExpire() == 0)
			CommandCenter.movFloaters.remove(this);
		else
			setExpire(getExpire() - 1);
	}

    public int getType()
    {
        return type;
    }

	@Override
	public void draw(Graphics g) {
        g.drawImage(image,getCenter().x-getRadius(),getCenter().y-getRadius(),2*getRadius()+20,2*getRadius(),null);
	}

}
