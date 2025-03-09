package main;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.GamePanel.InputAction;

public class Player implements IEntity, IInputListener{

	GamePanel gp;
	public Tile[][] tileGrid;
	BufferedImage[] bufferedImages;
	
	//worldX and worldY are the TLC of the hitbox in world
	public int gridX, gridY, worldX, worldY;
	public int collision; 
	//public int wpSolidAreaX,wpSolidAreaY,wpSolidAreaWidth,wpSolidAreaHeight;
	public Rectangle wpSolidArea;
	public Rectangle spriteRect;
	private Rectangle proposedMove;
	public int spriteHitboxOffsetX=0;
	public int spriteHitboxOffsetY=0;
	public int velocity =5;
	public int defaultVelocity =5;
	private boolean run = false;
	public int velX, velY;
	public int playerScreenX,playerScreenY;
	public int health=80;
	public PlayerState state;
	private final int CYCLE_IMAGE_FREQ = 30;
	
	private int currentImageIndex = 0;
	
	//up down left right
	public boolean[] movesRequested;
	public boolean[] stopRequested;

	public Player(GamePanel gp) {
		this.gp=gp;
		this.wpSolidArea = new Rectangle();
		this.proposedMove = new Rectangle();
		this.wpSolidArea.width = 25;
		this.wpSolidArea.height = 25;
		this.proposedMove.width = wpSolidArea.width ;
		this.proposedMove.height = wpSolidArea.height ;
		this.worldX = 200;
		this.worldY = 200;
		this.movesRequested = new boolean[] {false,false,false,false};
		this.stopRequested = new boolean[] {false,false,false,false};
		
		try { initImages(); }catch(Exception e){e.printStackTrace();}
	}
	
	public enum PlayerState{
		WALK,
		STAND,
		DEAD,
		ZOMBIE,
		POD
	}
	
	int walkCycleCounter=0;
	public void cycleSprite() {
		
		if(state==PlayerState.STAND) {
			currentImageIndex=0;
			return;
		}else if(state==PlayerState.WALK) {
			walkCycleCounter++;
			if(walkCycleCounter>CYCLE_IMAGE_FREQ) {
				walkCycleCounter=0;
				if(currentImageIndex==1) {
					currentImageIndex=2;
				}else {
					currentImageIndex=1;
				}
			}
			
		}else {
			currentImageIndex=0;
		}
		
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		gp.g2.setColor(Color.orange);
		if(gp.drawCollRect)gp.g2.drawRect(
				worldX-gp.wpScreenLocX, 
				worldY-gp.wpScreenLocY, 
				wpSolidArea.width, 
				wpSolidArea.height);
		gp.g2.drawImage(bufferedImages[currentImageIndex],
				worldX-gp.wpScreenLocX+spriteHitboxOffsetX,
				worldY-gp.wpScreenLocY+spriteHitboxOffsetY,
				25, 
				35,
				null);
		
	}

	@Override
	public void update() {
		movePlayer();
		cycleSprite();
		this.wpSolidArea.x = worldX;
		this.wpSolidArea.y = worldY;
		
	}
	
	public void toggleRun() {
		if (velocity==defaultVelocity) {
			velocity*=1.5;
		}else {
			velocity=defaultVelocity;
		}
	}
	
	private int decay(int number) {
		if (number <0){
			number++;
			return number;
		}else if (number > 0) {
			number --;
			return number;
		}else {
			return number;
		}
	}
	
	private void bumpPlayer(boolean[] colls) {
		
		if (colls[0]) {
			worldY+=1;
		}
		if (colls[1]) {
			worldY-=1;
		}
		if (colls[2]) {
			worldX+=1;
		}
		if (colls[3]) {
			worldX-=1;
		}
	}
	
	/**
	 * return false if a move is blocked by tile
	 * @return
	 */
	private boolean moveAllowed() {
		
		//this.proposedMove = new Rectangle();
		
		int pvelX = decay(velX);
		int pvelY = decay(velY);
		
		if(this.movesRequested[0]) {
			pvelY = -velocity;
			//this.movesRequested[0]=false;
		}
		if(this.movesRequested[1]) {
			pvelY= velocity;
			//this.movesRequested[1]=false;
		}
		if(this.movesRequested[2]) {
			pvelX = -velocity;
			//this.movesRequested[2]=false;
		}
		if(this.movesRequested[3]) {
			pvelX = velocity;
			//this.movesRequested[3]=false;
		}
		
		this.proposedMove.x = worldX+pvelX;
		this.proposedMove.y = worldY+pvelY;
		
		boolean[] colls = null;
		try {
			 colls = gp.collision.collideTileRect(this.proposedMove);
		}catch(Exception e) {
			
			return false;
		}
		//bump the player away from wall
		bumpPlayer( colls);
		
		for(int i=0;i<colls.length;i++) {
			if (colls[i]==true){
				
				 
				//stop player if collision function goes OOB
				
				return false;
			}
			
		}
		return true;
		
	}
	int walkingCounter =0;
	private void movePlayer() {
		
		if(!moveAllowed()) {
			gp.sound.clipPlayFlags[1]=true;
			
			return;
		}
		
		velX = decay(velX);
		velY = decay(velY);
		
		if(this.movesRequested[0]) {
			this.velY = -velocity;
			//this.movesRequested[0]=false;
		}
		if(this.movesRequested[1]) {
			this.velY= velocity;
			//this.movesRequested[1]=false;
		}
		if(this.movesRequested[2]) {
			this.velX = -velocity;
			//this.movesRequested[2]=false;
		}
		if(this.movesRequested[3]) {
			this.velX = velocity;
			//this.movesRequested[3]=false;
		}
		
		this.worldX+=velX;
		this.worldY+=velY;
		
		//change state to walking if recent movement
		
		if(velX!=0||velY!=0) {
			
			walkingCounter=60;
			this.state=PlayerState.WALK;
			
		}else if (walkingCounter>0){
			walkingCounter--;
			this.state=PlayerState.WALK;
			
		}else {
			this.state=PlayerState.STAND;
		}
		//System.out.println(state.name());
		
	}

	@Override
	public void initImages() throws IOException {
		bufferedImages = new BufferedImage[20];
		bufferedImages[0] = ImageIO.read(getClass().getResourceAsStream("/characters/hegray2.png"));
		bufferedImages[1] = ImageIO.read(getClass().getResourceAsStream("/characters/hegraDO1.png"));
		bufferedImages[2] = ImageIO.read(getClass().getResourceAsStream("/characters/hegraDO2.png"));
		
	}

	@Override
	public void inputListenerAction(InputAction action) {
		// TODO Auto-generated method stub
		switch(action) {
		case UP:
			this.movesRequested[0] = true;
			break;
		case DOWN:
			this.movesRequested[1] = true;
			break;
		case LEFT:
			this.movesRequested[2] = true;
			break;
		case RIGHT:
			this.movesRequested[3] = true;
			break;
		case UPSTOP:
			//System.out.println("up");
			this.movesRequested[0] = false;
			break;
		case DOWNSTOP:
			this.movesRequested[1] = false;
			break;
		case LEFTSTOP:
			this.movesRequested[2] = false;
			break;
		case RIGHTSTOP:
			this.movesRequested[3] = false;
			break;
			
		case RUN:
			toggleRun();
			break;
		default:
			break;
		}
		
	}

}
