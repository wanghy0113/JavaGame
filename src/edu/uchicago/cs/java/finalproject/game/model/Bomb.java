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

public class Bomb extends Sprite {

    private final double FIRE_POWER = 15.0;
    private final int MAX_EXPIRE = 25;
    private BufferedImage image;

    //for drawing alternative shapes
    //you could have more than one of these sets so that your sprite morphs into various shapes
    //throughout its life
    public double[] dLengthsAlts;
    public double[] dDegreesAlts;

    public Bomb(Falcon fal) {

        super();
        try
        {
            image = ImageIO.read(new File("image/takifuguBone.jpg")) ;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        //defined the points on a cartesean grid


        //a cruis missile expires after 25 frames
        setExpire(MAX_EXPIRE);
        setRadius(40);

        //everything is relative to the falcon ship that fired the bullet
        setDeltaX(fal.getDeltaX()
                + Math.cos(Math.toRadians(fal.getOrientation())) * FIRE_POWER);
        setDeltaY(fal.getDeltaY()
                + Math.sin(Math.toRadians(fal.getOrientation())) * FIRE_POWER);
        setCenter(new Point(fal.getCenter().x+30,fal.getCenter().y ));

        //set the bullet orientation to the falcon (ship) orientation
    }

    @Override
    public void move() {

        super.move();

        if (getExpire() < MAX_EXPIRE -5){
            setDeltaX(getDeltaX() * 1.07);
            setDeltaY(getDeltaY() * 1.07);
        }



    }

    @Override
    public void draw(Graphics g){

        g.drawImage(image,getCenter().x-getRadius(),getCenter().y-getRadius(),2*getRadius(),2*getRadius(),null);

    }




    //override the expire method - once an object expires, then remove it from the arrayList.
}
