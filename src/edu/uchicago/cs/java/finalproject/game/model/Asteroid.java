package edu.uchicago.cs.java.finalproject.game.model;


import java.awt.*;
import java.util.Arrays;

import edu.uchicago.cs.java.finalproject.controller.Game;
import java.util.ArrayList;

public class Asteroid extends Sprite {


	private ArrayList<Arrow> arrows = new ArrayList<Arrow>();
    private int arrowAmount = Game.R.nextInt(4)+1;
	//radius of a large asteroid
	private final int RAD = 100;
    private static int speedLevel =100;
    private int arrowScale = 30;
    private int nCount;
    private boolean bPause;
	
	//nSize determines if the Asteroid is Large (0), Medium (1), or Small (2)
	//when you explode a Large asteroid, you should spawn 2 or 3 medium asteroids
	//same for medium asteroid, you should spawn small asteroids
	//small asteroids get blasted into debris
	public Asteroid(){
		
		//call Sprite constructor
		super();
        for(int i =0;i<arrowAmount;i++)
        {
            arrows.add(new Arrow(Game.R.nextInt(4),Game.R.nextBoolean()));
        }
		initialize();
		int nDY = Game.R.nextInt(speedLevel);
		setDeltaY((double)nDY*0.03*CommandCenter.getLevel()+1);


		

	}
    public Asteroid(int level,double Speed) {

        //you can override this and many more in the subclasses
        setCenter(new Point(Game.R.nextInt(Game.DIM.width),
                -level*100));
        for(int i =0;i<arrowAmount;i++)
        {
            arrows.add(new Arrow(Game.R.nextInt(4),Game.R.nextBoolean()));
        }
        initialize();
        int nDY = Game.R.nextInt(speedLevel);
        setDeltaY(Speed);


    }
    public void initialize()
    {


        ArrayList<Point> pntCs = new ArrayList<Point>();
        setRadius((int)(Math.pow((arrowScale*arrowScale+arrowScale*arrowAmount*arrowScale*arrowAmount),0.5)));

        // top of ship
        //pntCs.add(new Point(-getRadius(), 0));

        //right points
        //pntCs.add(new Point(-20*arrowAmount, 20));
        //pntCs.add(new Point(20*arrowAmount,20));
        //pntCs.add(new Point(getRadius(), 0));
        //pntCs.add(new Point(20*arrowAmount, -20));
        //pntCs.add(new Point(-20*arrowAmount, -20));
        //  pntCs.add(new Point(-35, 15));


        //assignPolarPoints(pntCs);

        //setColor(Color.BLACK);
        //setOrientation(90);
    }
	
	public int getRemain()
    {
        System.out.println(arrows.size());
        return arrows.size();
    }
    public int getFirstAttribute()
    {
        return arrows.get(0).getAttribute();
    }


    public void weak()
    {
        if(arrows.size()>0)
        {
            arrows.remove(0);
            arrowAmount--;

        }
        initialize();

    }

	public int getSize(){
		
		int nReturn = 0;
		
		switch (getRadius()) {
			case 100:
				nReturn= 0;
				break;
			case 50:
				nReturn= 1;
				break;
			case 25:
				nReturn= 2;
				break;
		}
		return nReturn;
		
	}

	//overridden
	public void move(){
        Point pnt = getCenter();
        double dX = pnt.x ;
        double dY = pnt.y ;
        if(bPause)
        {
            nCount=nCount+1;
            if(nCount>=100)
            {
                bPause=false;
                nCount=0;
            }
        }
        else
        {
            if(nCount++>1)
            {
                dX = pnt.x + getDeltaX();
                dY = pnt.y + getDeltaY();
                nCount=0;
            }
        }


        //this just keeps the sprite inside the bounds of the frame
        if (pnt.x > (getDim().width-getRadius())) {
            setCenter(new Point(getRadius(), pnt.y));

        } else if (pnt.x < getRadius()) {
            setCenter(new Point(getDim().width - getRadius(), pnt.y));
        } else if (pnt.y > getDim().height) {
            setCenter(new Point(pnt.x, 1));

        }
        else {

            setCenter(new Point((int) dX, (int) dY));
        }
		
		//an asteroid spins, so you need to adjust the orientation at each move()
	//	setOrientation(getOrientation() + getSpin());
		
	}

	/*public int getSpin() {
		return this.nSpin;
	} */
	

	/*public void setSpin(int nSpin) {
		this.nSpin = nSpin;
	} */
    public void draw(Graphics g)
    {
       // super.draw(g);
        for(int i =0;i<arrows.size();i++)
        {
            arrows.get(i).draw(g,new Point(getCenter().x+(-arrowScale*arrowAmount+arrowScale+i*2*arrowScale),getCenter().y),arrowScale-5);
        }
      //  g.drawImage()


    }


    public void pause()
    {
        bPause =true;
    }
	//this is for an asteroid only

}
