package edu.uchicago.cs.java.finalproject.controller;

import sun.audio.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.File;
import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;

import edu.uchicago.cs.java.finalproject.game.model.*;
import edu.uchicago.cs.java.finalproject.game.view.*;
import edu.uchicago.cs.java.finalproject.sounds.Sound;

// ===============================================
// == This Game class is the CONTROLLER
// ===============================================

public class Game implements Runnable, KeyListener {

	// ===============================================
	// FIELDS
	// ===============================================

    private String strDisplay = "";

	public static final Dimension DIM = new Dimension(1100, 900); //the dimension of the game.
	private GamePanel gmpPanel;
	public static Random R = new Random();
	public final static int ANI_DELAY = 45; // milliseconds between screen
											// updates (animation)
	private Thread thrAnim;
	private int nTick = 0;
	private ArrayList<Tuple> tupMarkForRemovals;
	private ArrayList<Tuple> tupMarkForAdds;
	private boolean bMuted = true;
    private OffScreenImage offScreenImage;
    private boolean bInstruction = true;
    private int nBigFish = 3;
    private int nBomb = 3 ;
    private Bomb bomb;
    private boolean isBombEjected;

    private BufferedImage image;
    private BufferedImage imageHead;
    private BufferedImage imageInstruction;
    private BufferedImage imagePaused;
    private BufferedImage imageFish;
    private BufferedImage imageBomb;

	private final int PAUSE = 80, // p key
			QUIT = 81, // q key
			LEFT = 65, // a key
			RIGHT = 68, // d key
			UP = 87, // w key
            DOWN = 83, //s key
			START = 10, // enter key
			FIREUP = 38, // up key
            FIRELEFT = 37, //left key
            FIREDOWN = 40, //down key
            FIRERIGHT = 39, //right key
			MUTE = 77, // m-key mute
            INSTRUCTION = 73, // i-key instruction
            BUYITEM1 = 49,
            BUYITEM2 = 50,
            BUYITEM3 = 52,
            BUYITEM4 = 51,
            BOMB = 32,


	// for possible future use
	// HYPER = 68, 					// d key
	// SHIELD = 65, 				// a key arrow
	// NUM_ENTER = 10, 				// hyp
	 SPECIAL = 70; 					// fire special weapon;  F key

	private Clip clpThrust;
	private Clip clpMusicBackground;

	private static final int SPAWN_NEW_SHIP_FLOATER = 1200;



	// ===============================================
	// ==CONSTRUCTOR
	// ===============================================

	public Game() {
        try {
            image = ImageIO.read(new File("image/background.jpg"));
            imageHead = ImageIO.read(new File("image/head.jpg"));
            imageInstruction = ImageIO.read(new File("image/instruction.jpg"));
            imagePaused = ImageIO.read(new File("image/GamePaused.jpg"));
            imageFish = ImageIO.read(new File("image/Shake.jpg"));
            imageBomb = ImageIO.read(new File("image/takifuguBone.jpg"));

        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        offScreenImage = new OffScreenImage();
		gmpPanel = new GamePanel(DIM,offScreenImage);
		gmpPanel.addKeyListener(this);

		clpThrust = Sound.clipForLoopFactory("whitenoise.wav");
		clpMusicBackground = Sound.clipForLoopFactory("district.wav");
	

	}

	// ===============================================
	// ==METHODS
	// ===============================================

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() { // uses the Event dispatch thread from Java 5 (refactored)
					public void run() {
						try {
							Game game = new Game(); // construct itself
							game.fireUpAnimThread();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void fireUpAnimThread() { // called initially
		if (thrAnim == null) {
			thrAnim = new Thread(this); // pass the thread a runnable object (this)
			thrAnim.start();
		}
	}

	// implements runnable - must have run method
	public void run() {

		// lower this thread's priority; let the "main" aka 'Event Dispatch'
		// thread do what it needs to do first
		thrAnim.setPriority(Thread.MIN_PRIORITY);

		// and get the current time
		long lStartTime = System.currentTimeMillis();

		// this thread animates the scene
		while (Thread.currentThread() == thrAnim) {
			tick();
		//	spawnNewShipFloater();

            drawOffScreen();

			//gmpPanel.update(gmpPanel.getGraphics()); // update takes the graphics context we must
														// surround the sleep() in a try/catch block
														// this simply controls delay time between 
														// the frames of the animation

			//this might be a good place to check for collisions
			checkCollisions();
			//this might be a god place to check if the level is clear (no more foes)
			//if the level is clear then spawn some big asteroids -- the number of asteroids 
			//should increase with the level.
            if(CommandCenter.isPlaying())
		    	checkNewLevel();

			try {
				// The total amount of time is guaranteed to be at least ANI_DELAY long.  If processing (update) 
				// between frames takes longer than ANI_DELAY, then the difference between lStartTime - 
				// System.currentTimeMillis() will be negative, then zero will be the sleep time
				lStartTime += ANI_DELAY;
				Thread.sleep(Math.max(0,
						lStartTime - System.currentTimeMillis()));
			} catch (InterruptedException e) {
				// just skip this frame -- no big deal
				continue;
			}
		} // end while
	} // end run

	private void checkCollisions() {

		
		//@formatter:off
		//for each friend in movFriends
			//for each foe in movFoes
				//if the distance between the two centers is less than the sum of their radii
					//mark it for removal
		
		//for each mark-for-removal
			//remove it
		//for each mark-for-add
			//add it
		//@formatter:on
		
		//we use this ArrayList to keep pairs of movMovables/movTarget for either
		//removal or insertion into our arrayLists later on
		tupMarkForRemovals = new ArrayList<Tuple>();
		tupMarkForAdds = new ArrayList<Tuple>();

		Point pntFriendCenter, pntFoeCenter;
		int nFriendRadiux, nFoeRadiux;
        for(Movable movFoe : CommandCenter.movFoes)
        {
            if(movFoe.getCenter().getY()>Game.DIM.height)
            {
                tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
                CommandCenter.decEscape();
            }
        }
		for (Movable movFriend : CommandCenter.movFriends) {
			for (Movable movFoe : CommandCenter.movFoes) {

				pntFriendCenter = movFriend.getCenter();
				pntFoeCenter = movFoe.getCenter();
				nFriendRadiux = movFriend.getRadius();
				nFoeRadiux = movFoe.getRadius();

				//detect collision
				if ((Math.abs(pntFriendCenter.getX()-pntFoeCenter.getX())<(nFriendRadiux + nFoeRadiux)
                    &&(Math.abs(pntFriendCenter.getY()-pntFoeCenter.getY())<(nFriendRadiux+30)))) {     //pntFriendCenter.distance(pntFoeCenter) < (nFriendRadiux + nFoeRadiux

					//falcon
					if ((movFriend instanceof Falcon) ){
						if (!CommandCenter.getFalcon().getProtected()){
							tupMarkForRemovals.add(new Tuple(CommandCenter.movFriends, movFriend));
							CommandCenter.spawnFalcon(false);

							killFoe(movFoe);
						}
					}
					//not the falcon
					else if(movFoe instanceof Asteroid)
                        {
                            Asteroid collisionAsteroid = (Asteroid)movFoe;
                            if(movFriend instanceof Bullet)
                            {
                                 if(movFriend.getAttribute()==collisionAsteroid.getFirstAttribute())
                                 {
                                     tupMarkForRemovals.add(new Tuple(CommandCenter.movFriends, movFriend));
//                                     createDebris((Sprite)movFoe, tupMarkForAdds);
                                     killFoe(movFoe);
                                 }
                            }
                            else if(movFriend instanceof Cruise)
                            {
                                tupMarkForRemovals.add(new Tuple(CommandCenter.movFriends, movFriend));
                                tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
 //                               createDebris((Sprite)movFoe, tupMarkForAdds);
                            }
                        }



					//end else

					//explode/remove foe
					
					
				
				}//end if 
			}//end inner for
		}//end outer for


		//check for collisions between falcon and floaters
		if (CommandCenter.getFalcon() != null){
			Point pntFalCenter = CommandCenter.getFalcon().getCenter();
			int nFalRadiux = CommandCenter.getFalcon().getRadius();
			Point pntFloaterCenter;
			int nFloaterRadiux;
			
			for (Movable movFloater : CommandCenter.movFloaters) {
				pntFloaterCenter = movFloater.getCenter();
				nFloaterRadiux = movFloater.getRadius();
	
				//detect collision
				if (pntFalCenter.distance(pntFloaterCenter) < (nFalRadiux + nFloaterRadiux)) {
	
					
					tupMarkForRemovals.add(new Tuple(CommandCenter.movFloaters, movFloater));
                    NewShipFloater floater = (NewShipFloater)movFloater;
                    if(floater.getType()==0)
                    {
                         tupMarkForAdds.add(new Tuple(CommandCenter.movDebris,new Debris(movFloater)));
                         for(Movable mov : CommandCenter.getMovFoes())
                        {
                            Asteroid asteroid = (Asteroid)mov;
                            asteroid.pause();
                         }
                    }
                    else if(floater.getType()==1)
                    {
                        nBigFish++;
                    }
                    else if(floater.getType()==2)
                    {
                        nBomb++;
                    }
					Sound.playSound("pacman_eatghost.wav");
	
				}//end if 
			}//end inner for
		}//end if not null
		
		//remove these objects from their appropriate ArrayLists
		//this happens after the above iterations are done
		for (Tuple tup : tupMarkForRemovals) 
			tup.removeMovable();
		
		//add these objects to their appropriate ArrayLists
		//this happens after the above iterations are done
		for (Tuple tup : tupMarkForAdds) 
			tup.addMovable();

		//call garbage collection
		System.gc();
		
	}//end meth


	private void killFoe(Movable movFoe) {
		
		if (movFoe instanceof Asteroid){

			//we know this is an Asteroid, so we can cast without threat of ClassCastException
			Asteroid astExploded = (Asteroid)movFoe;
			//big asteroid 
			if(astExploded.getRemain() == 1){
				//spawn two medium Asteroids
                CommandCenter.setScore(CommandCenter.getScore()+1000);
                tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
                spawnNewShipFloater();
				
			}
            else
            {
                astExploded.weak();
                CommandCenter.setScore(CommandCenter.getScore()+100);
            }
			//medium size aseroid exploded
			//remove the original Foe	

		} 
		//not an asteroid
		else {
			//remove the original Foe
			tupMarkForRemovals.add(new Tuple(CommandCenter.movFoes, movFoe));
		}
		
		
		

		
		
		
		
	}

	//some methods for timing events in the game,
	//such as the appearance of UFOs, floaters (power-ups), etc. 
	public void tick() {
		if (nTick == Integer.MAX_VALUE)
			nTick = 0;
		else
			nTick++;
	}

	public int getTick() {
		return nTick;
	}

	private void spawnNewShipFloater() {
		//make the appearance of power-up dependent upon ticks and levels
		//the higher the level the more frequent the appearance
		if (Game.R.nextInt(10)<2) {
			CommandCenter.movFloaters.add(new NewShipFloater());
		}
	}

	// Called when user presses 's'
	private void startGame() {
		CommandCenter.clearAll();
		CommandCenter.initGame();
		//CommandCenter.setLevel(1);
        CommandCenter.setnEscape(10);
		CommandCenter.setPlaying(true);
		CommandCenter.setPaused(false);
        nBigFish = 3;
		//if (!bMuted)
		    clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
	}

	//this method spawns new asteroids
	private void spawnAsteroids(int nNum) {
		for (int nC = 0; nC < nNum; nC++) {
			//Asteroids with size of zero are big
			CommandCenter.movFoes.add(new Asteroid(nC,1+CommandCenter.getLevel()/3));
            CommandCenter.movFoes.add(new Asteroid(nC,2+CommandCenter.getLevel()/3));
            CommandCenter.movFoes.add(new Asteroid(nC,3+CommandCenter.getLevel()/3));
            CommandCenter.movFoes.add(new Asteroid(nC,4+CommandCenter.getLevel()/3));
		}
	}
	
	
	private boolean isLevelClear(){
		//if there are no more Asteroids on the screen
		
		boolean bAsteroidFree = true;
		for (Movable movFoe : CommandCenter.movFoes) {
			if (movFoe instanceof Asteroid){
				bAsteroidFree = false;
				break;
			}
		}
		
		return bAsteroidFree;

		
	}
	
	private void checkNewLevel(){
		
		if (isLevelClear() ){
			if (CommandCenter.getFalcon() !=null)
				CommandCenter.getFalcon().setProtected(true);
			
			spawnAsteroids(CommandCenter.getLevel());
			CommandCenter.setLevel(CommandCenter.getLevel() + 1);
            Sound.playSound("crow.wav");

		}
	}


    public void drawOffScreen() {
        if (offScreenImage.getGrpOff() == null) {
            offScreenImage.reset();
        }


        Graphics grpOff = offScreenImage.getGrpOff();
        // Fill in background with black.
      //  grpOff.setColor(Color.white);
      //  grpOff.fillRect(0, 0, Game.DIM.width, Game.DIM.height);


        grpOff.drawImage(image,0,0,Game.DIM.width,Game.DIM.height,null);
        drawStatus(grpOff);

        if (!CommandCenter.isPlaying()) {
            if(bInstruction)
                grpOff.drawImage(imageHead,0,0,Game.DIM.width,Game.DIM.height,null);
            else
                grpOff.drawImage(imageInstruction,0,0,Game.DIM.width,Game.DIM.height,null);
        }
        else if (CommandCenter.isPaused()) {
            grpOff.drawImage(imagePaused,0,0,Game.DIM.width,Game.DIM.height,null);
            drawStatus(grpOff);
            grpOff.drawString(strDisplay,
                    (Game.DIM.width - offScreenImage.getFmt().stringWidth(strDisplay)) / 2, Game.DIM.height / 4);
        }

        //playing and not paused!
        else {

            //draw them in decreasing level of importance
            //friends will be on top layer and debris on the bottom
            iterateMovables(grpOff,
                    CommandCenter.movDebris,
                    CommandCenter.movFloaters,
                    CommandCenter.movFoes,
                    CommandCenter.movFriends);


            drawNumberShipsLeft(grpOff);
            if (CommandCenter.isGameOver()) {
                CommandCenter.setPlaying(false);
                CommandCenter.clearAll();
                stopLoopingSounds(clpMusicBackground);
                //bPlaying = false;
            }
        }

        //when we call repaint, repaint calls update(g)
        gmpPanel.repaint();
    }





    //for each movable array, process it.
    private void iterateMovables(Graphics g, CopyOnWriteArrayList<Movable>...movMovz){

        for (CopyOnWriteArrayList<Movable> movMovs : movMovz) {
            for (Movable mov : movMovs) {

                mov.move();
                mov.draw(g);
                mov.fadeInOut();
                mov.expire();
            }
        }

    }


    //offscreen
    // Draw the number of falcons left on the bottom-right of the screen.
    private void drawNumberShipsLeft(Graphics g) {
        Falcon fal = CommandCenter.getFalcon();
        double[] dLens = fal.getLengths();
        int nLen = fal.getDegrees().length;
        Point[] pntMs = new Point[nLen];
        int[] nXs = new int[nLen];
        int[] nYs = new int[nLen];

        //convert to cartesean points
        for (int nC = 0; nC < nLen; nC++) {
            pntMs[nC] = new Point((int) (10 * dLens[nC] * Math.sin(Math
                    .toRadians(90) + fal.getDegrees()[nC])),
                    (int) (10 * dLens[nC] * Math.cos(Math.toRadians(90)
                            + fal.getDegrees()[nC])));
        }

        //set the color to white
        g.setColor(Color.white);
        //for each falcon left (not including the one that is playing)
        for (int nD = 1; nD < CommandCenter.getNumFalcons(); nD++) {
            //create x and y values for the objects to the bottom right using cartesean points again
            for (int nC = 0; nC < fal.getDegrees().length; nC++) {
                nXs[nC] = pntMs[nC].x + Game.DIM.width - (20 * nD);
                nYs[nC] = pntMs[nC].y + Game.DIM.height - 40;
            }
            g.drawPolygon(nXs, nYs, nLen);
        }
    }


    private void drawStatus(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(offScreenImage.getFnt());
        g.drawString("SCORE :  " + CommandCenter.getScore()+"       Level:  "+(CommandCenter.getLevel()-1 )
                        +"       Life:  "+CommandCenter.getNumFalcons()+"    Fish:  "+CommandCenter.getnEscape(), offScreenImage.getFontWidth(), offScreenImage.getFontHeight());
        for(int i=0;i<nBigFish;i++)
        g.drawImage(imageFish,Game.DIM.width-30*(i+1),0,30,50,null);
        for(int i=0;i<nBomb;i++)
        g.drawImage(imageBomb,Game.DIM.width-30*(i+1),60,30,50,null);

    }
	
	

	// Varargs for stopping looping-music-clips
	private static void stopLoopingSounds(Clip... clpClips) {
		for (Clip clp : clpClips) {
			clp.stop();
		}
	}

	// ===============================================
	// KEYLISTENER METHODS
	// ===============================================

	@Override
	public void keyPressed(KeyEvent e) {
		Falcon fal = CommandCenter.getFalcon();
		int nKey = e.getKeyCode();
		// System.out.println(nKey);

		if (nKey == START && !CommandCenter.isPlaying())
			startGame();
        if(!bInstruction)
        {
            if(nKey == INSTRUCTION&&!CommandCenter.isPlaying())
            {
                bInstruction = true;
            }
        }
        else
        {
            if(nKey == INSTRUCTION&&!CommandCenter.isPlaying())
            {
                bInstruction = false;
            }
        }



		if (fal != null) {

			switch (nKey) {
			case PAUSE:
				CommandCenter.setPaused(!CommandCenter.isPaused());
				if (CommandCenter.isPaused())
					stopLoopingSounds(clpMusicBackground, clpThrust);
				else
					clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
				break;
			case QUIT:
				System.exit(0);
				break;
			case UP:
				fal.moveUp();
	//			if (!CommandCenter.isPaused())
	//				clpThrust.loop(Clip.LOOP_CONTINUOUSLY);
				break;
			case LEFT:
				fal.moveLeft();
				break;
			case RIGHT:
				fal.moveRight();
				break;
            case DOWN:
                fal.moveDown();
                break;
            case BOMB:
                if(nBomb>0&&isBombEjected==false)
                {
                    bomb = new Bomb(fal);
                    CommandCenter.movFriends.add(bomb);
                    isBombEjected =true;
                }
                break;



			// possible future use
			// case KILL:
			// case SHIELD:
			// case NUM_ENTER:

			default:
				break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Falcon fal = CommandCenter.getFalcon();
		int nKey = e.getKeyCode();
		 System.out.println(nKey);

		if (fal != null) {
			switch (nKey) {
			case FIRERIGHT:
				CommandCenter.movFriends.add(new Bullet(fal,0));
				Sound.playSound("laser.wav");
				break;
            case FIREUP:
                CommandCenter.movFriends.add(new Bullet(fal,3));
                Sound.playSound("laser.wav");
                break;
            case FIRELEFT:
                CommandCenter.movFriends.add(new Bullet(fal,2));
                Sound.playSound("laser.wav");
                break;
             case FIREDOWN:
                CommandCenter.movFriends.add(new Bullet(fal,1));
                Sound.playSound("laser.wav");
                break;
				
			//special is a special weapon, current it just fires the cruise missile. 
			case SPECIAL:
                if(nBigFish>0)
                {
                    nBigFish = nBigFish - 1;
                    CommandCenter.movFriends.add(new Cruise(fal));
                }
                break;
            case BOMB:
                if(isBombEjected=true&&nBomb>0)
                {
                    for(Movable foe : CommandCenter.getMovFoes())
                    {
                        if(Math.abs(foe.getCenter().x-bomb.getCenter().x)<150&&Math.abs(foe.getCenter().y-bomb.getCenter().y)<150)
                        {
                            CommandCenter.getMovFoes().remove(foe);
                        }
                    }

                    CommandCenter.movDebris.add(new Debris(bomb));
                    CommandCenter.movFriends.remove(bomb);
                    nBomb--;
                    isBombEjected = false;
                }

				break;
				
			case LEFT:
				fal.stopMoving("LEFT");
				break;
			case RIGHT:
				fal.stopMoving("RIGHT");
				break;
			case UP:
				fal.stopMoving("UP");
                break;
            case DOWN:
                fal.stopMoving("DOWN");
				break;
			case MUTE:
				if (!bMuted){
					stopLoopingSounds(clpMusicBackground);
					bMuted = !bMuted;
				} 
				else {
					clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
					bMuted = !bMuted;
				}
				break;
            case BUYITEM1:
                if(CommandCenter.getScore()>=4000)
                {
                CommandCenter.getFalcon().setMoveSpeed(CommandCenter.getFalcon().getMoveSpeed()+1);
                CommandCenter.setScore(CommandCenter.getScore()-4000);
                }
                break;
            case BUYITEM2:
                if(CommandCenter.getScore()>=5000)
                {
                nBigFish++;
                CommandCenter.setScore(CommandCenter.getScore()-5000);
                }
                break;
             case BUYITEM3:
                 if(CommandCenter.getScore()>=10000)
                 {
                 CommandCenter.incEscape();
                 CommandCenter.setScore(CommandCenter.getScore()-10000);
                 }
                 break;
             case BUYITEM4:
                  if(CommandCenter.getScore()>=7000)
                  {
                      nBomb++;
                      CommandCenter.setScore(CommandCenter.getScore()-7000);
                  }
                  break;
				
			default:
				break;
			}
		}
	}

	@Override
	// Just need it b/c of KeyListener implementation
	public void keyTyped(KeyEvent e) {
	}
	

	
}

// ===============================================
// ==A tuple takes a reference to an ArrayList and a reference to a Movable
//This class is used in the collision detection method, to avoid mutating the array list while we are iterating
// it has two public methods that either remove or add the movable from the appropriate ArrayList 
// ===============================================

class Tuple{
	//this can be any one of several CopyOnWriteArrayList<Movable>
	private CopyOnWriteArrayList<Movable> movMovs;
	//this is the target movable object to remove
	private Movable movTarget;
	
	public Tuple(CopyOnWriteArrayList<Movable> movMovs, Movable movTarget) {
		this.movMovs = movMovs;
		this.movTarget = movTarget;
	}
	
	public void removeMovable(){
		movMovs.remove(movTarget);
	}
	
	public void addMovable(){
		movMovs.add(movTarget);
	}

}
