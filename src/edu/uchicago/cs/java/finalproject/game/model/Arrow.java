package edu.uchicago.cs.java.finalproject.game.model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: wanghenry
 * Date: 11/30/13
 * Time: 8:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class Arrow {
    private int attribute;
    private boolean opposite;
    private BufferedImage imageRight;
    private BufferedImage imageLeft;
    private BufferedImage imageUp;
    private BufferedImage imageDown;
    private BufferedImage imageoRight;
    private BufferedImage imageoLeft;
    private BufferedImage imageoUp;
    private BufferedImage imageoDown;
    public Arrow(int attribute, boolean opposite)
    {
        this.attribute = attribute;
        this.opposite = opposite;
        try {
            imageRight = ImageIO.read(new File("image/Right.jpg")) ;
            imageLeft = ImageIO.read(new File("image/Left.jpg")) ;
            imageUp = ImageIO.read(new File("image/Up.jpg")) ;
            imageDown = ImageIO.read(new File("image/Down.jpg")) ;
            imageoRight = ImageIO.read(new File("image/oRight.jpg")) ;
            imageoLeft = ImageIO.read(new File("image/oLeft.jpg")) ;
            imageoUp = ImageIO.read(new File("image/oUp.jpg")) ;
            imageoDown = ImageIO.read(new File("image/oDown.jpg")) ;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public int getAttribute()
    {
        return  attribute;
    }

    public void draw(Graphics g, Point center,int radius)
    {

        if(!opposite)
        {
            if(attribute==0)
            {
                g.drawImage(imageRight,center.x-radius,center.y-radius,2*radius+10,2*radius,null);
            }
            else if (attribute==1)
            {
                g.drawImage(imageDown,center.x-radius,center.y-radius,2*radius+10,2*radius,null);
            }
            else if(attribute==2)
            {
                g.drawImage(imageLeft,center.x-radius,center.y-radius,2*radius+10,2*radius,null);
            }
            else if (attribute==3)
            {
                g.drawImage(imageUp,center.x-radius,center.y-radius,2*radius+10,2*radius,null);
            }
        }
        else
        {

            if(attribute==0)
            {
                g.drawImage(imageoLeft,center.x-radius,center.y-radius,2*radius+10,2*radius,null);
            }
            else if (attribute==1)
            {
                g.drawImage(imageoUp,center.x-radius,center.y-radius,2*radius+10,2*radius,null);
            }
            else if(attribute==2)
            {
                g.drawImage(imageoRight,center.x-radius,center.y-radius,2*radius+10,2*radius,null);
            }
            else if (attribute==3)
            {
                g.drawImage(imageoDown,center.x-radius,center.y-radius,2*radius+10,2*radius,null);
            }
        }
    }
}
