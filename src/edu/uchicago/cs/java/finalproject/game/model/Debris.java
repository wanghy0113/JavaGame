package edu.uchicago.cs.java.finalproject.game.model;

import edu.uchicago.cs.java.finalproject.controller.Game;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: ag
 * Date: 11/20/13
 * Time: 11:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class Debris extends Sprite {
    private Movable movable;

    public Debris(Movable spr) {

        movable = spr;
        setCenter(spr.getCenter());
        setExpire(10);
    }


    public void draw(Graphics g)
    {
        if(movable instanceof NewShipFloater)
        {
            g.setColor(Color.white);
            g.fillOval(getCenter().x-800,getCenter().y-800,1600,1600);
        }
        else if(movable instanceof Bomb)
        {
            g.setColor(Color.red);
            g.fillOval(getCenter().x-100,getCenter().y-100,200,200);
        }

    }

    public void expire(){
        System.out.println("expire:"+getExpire());
        if (getExpire() == 0)
            CommandCenter.movDebris.remove(this);
        else
            setExpire(getExpire() - 1);
    }

}
