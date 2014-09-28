package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import edu.uchicago.cs.java.finalproject.controller.Game;

import javax.imageio.ImageIO;


public class Falcon extends Sprite {

	// ==============================================================
	// FIELDS 
	// ==============================================================
	private int moveSpeed = 15;
	
	private boolean bShield = false;
	private boolean bFlame = false;
	private boolean bProtected; //for fade in and out
    private BufferedImage image;
	
	private boolean bMoveUp = false;
	private boolean bMoveRight = false;
	private boolean bMoveLeft = false;
    private boolean bMoveDown = false;
	
	private int nShield;
			
	private final double[] FLAME = { 23 * Math.PI / 24 + Math.PI / 2,
			Math.PI + Math.PI / 2, 25 * Math.PI / 24 + Math.PI / 2 };

	private int[] nXFlames = new int[FLAME.length];
	private int[] nYFlames = new int[FLAME.length];

	private Point[] pntFlames = new Point[FLAME.length];

	
	// ==============================================================
	// CONSTRUCTOR 
	// ==============================================================
	
	public Falcon() {
		super();
        try{
            image = ImageIO.read(new File("image/cat.jpg")) ;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
		ArrayList<Point> pntCs = new ArrayList<Point>();
		
		// top of ship
		pntCs.add(new Point(0, 18));
		
		//right points
		pntCs.add(new Point(3, 3)); 
		pntCs.add(new Point(12, 0)); 
		pntCs.add(new Point(13, -2)); 
		pntCs.add(new Point(13, -4)); 
		pntCs.add(new Point(11, -2)); 
		pntCs.add(new Point(4, -3)); 
		pntCs.add(new Point(2, -10)); 
		pntCs.add(new Point(4, -12)); 
		pntCs.add(new Point(2, -13)); 

		//left points
		pntCs.add(new Point(-2, -13)); 
		pntCs.add(new Point(-4, -12));
		pntCs.add(new Point(-2, -10)); 
		pntCs.add(new Point(-4, -3)); 
		pntCs.add(new Point(-11, -2));
		pntCs.add(new Point(-13, -4));
		pntCs.add(new Point(-13, -2)); 
		pntCs.add(new Point(-12, 0)); 
		pntCs.add(new Point(-3, 3)); 
		

		assignPolarPoints(pntCs);

		setColor(Color.BLACK);
		
		//put falcon in the middle.
		setCenter(new Point(Game.DIM.width / 2, Game.DIM.height-100));
		
		//with random orientation
		setOrientation(270);
		
		//this is the size of the falcon
		setRadius(40);

		//these are falcon specific
		setProtected(true);
		setFadeValue(0);
	}
	
	
	// ==============================================================
	// METHODS 
	// ==============================================================

    public void moveUp()
    {
        setDeltaY(-moveSpeed);
    }

    public void moveDown()
    {
        setDeltaY(moveSpeed);
    }

    public void moveLeft()
    {
        setDeltaX(-moveSpeed);
    }

    public void moveRight()
    {
        setDeltaX(moveSpeed);
    }

	public void stopMoving(String orientation) {
		if(orientation.equals("UP"))
        {
            setDeltaY(0);
        }
        else if(orientation.equals("DOWN"))
        {
            setDeltaY(0);
        }
        else if(orientation.equals("LEFT"))
        {
            setDeltaX(0);
        }
        else if(orientation.equals("RIGHT"))
        {
            setDeltaX(0);
        }
	}

	private int adjustColor(int nCol, int nAdj) {
		if (nCol - nAdj <= 0) {
			return 0;
		} else {
			return nCol - nAdj;
		}
	}

	public void draw(Graphics g) {

		//does the fading at the beginning or after hyperspace
		Color colShip;
		if (getFadeValue() == 255) {
			colShip = Color.white;
		} else {
			colShip = new Color(adjustColor(getFadeValue(), 200), adjustColor(
					getFadeValue(), 175), getFadeValue());
		}

//		//shield on
//		if (bShield && nShield > 0) {
//
//			setShield(getShield() - 1);
//
//			g.setColor(Color.cyan);
//			g.drawOval(getCenter().x - getRadius(),
//					getCenter().y - getRadius(), getRadius() * 2,
//					getRadius() * 2);
//
//		} //end if shield

		//thrusting
		if (bFlame) {
			g.setColor(colShip);
			//the flame
			for (int nC = 0; nC < FLAME.length; nC++) {
				if (nC % 2 != 0) //odd
				{
					pntFlames[nC] = new Point((int) (getCenter().x + 2
							* getRadius()
							* Math.sin(Math.toRadians(getOrientation())
									+ FLAME[nC])), (int) (getCenter().y - 2
							* getRadius()
							* Math.cos(Math.toRadians(getOrientation())
									+ FLAME[nC])));

				} else //even
				{
					pntFlames[nC] = new Point((int) (getCenter().x + getRadius()
							* 1.1
							* Math.sin(Math.toRadians(getOrientation())
									+ FLAME[nC])),
							(int) (getCenter().y - getRadius()
									* 1.1
									* Math.cos(Math.toRadians(getOrientation())
											+ FLAME[nC])));

				} //end even/odd else

			} //end for loop

			for (int nC = 0; nC < FLAME.length; nC++) {
				nXFlames[nC] = pntFlames[nC].x;
				nYFlames[nC] = pntFlames[nC].y;

			} //end assign flame points

			//g.setColor( Color.white );
			g.fillPolygon(nXFlames, nYFlames, FLAME.length);

		} //end if flame

		drawShipWithColor(g, colShip);
        g.drawImage(image,getCenter().x-getRadius(),getCenter().y-getRadius(),2*getRadius(),2*getRadius(),null);

	} //end draw()

	public void drawShipWithColor(Graphics g, Color col) {
		super.draw(g);
		g.setColor(col);
		g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
	}

	public void fadeInOut() {
		if (getProtected()) {
			setFadeValue(getFadeValue() + 3);
		}
		if (getFadeValue() == 255) {
			setProtected(false);
		}
	}
	
	public void setProtected(boolean bParam) {
		if (bParam) {
			setFadeValue(0);
		}
		bProtected = bParam;
	}

	public void setProtected(boolean bParam, int n) {
		if (bParam && n % 3 == 0) {
			setFadeValue(n);
		} else if (bParam) {
			setFadeValue(0);
		}
		bProtected = bParam;
	}	

	public boolean getProtected() {return bProtected;}
	public void setShield(int n) {nShield = n;}
	public int getShield() {return nShield;}

    public void setMoveSpeed(int speed)
    {
        moveSpeed = speed;
    }

    public int getMoveSpeed()
    {
        return moveSpeed;
    }
	
} //end class
