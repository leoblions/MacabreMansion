package main;


import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Random;

import javax.imageio.ImageIO;

public class Item {
	public final int ITEM_SCALE_PX = 25;
	public final int ITEM_TLC_OFFSET= GamePanel.TILE_SIZE_PX/2;
	public final float ITEM_DRAWSIZE_FACTOR = 0.5f;
	
	public final int MINIMUM_RANDOM_GRIDX = 10;
	public final int MINIMUM_RANDOM_GRIDY = 10;
	public final int RANDOM_ITEM_DENSITY = 50;
	
	BufferedImage[] itemImages;
	GamePanel gp;
	ItemUnit[][] itemsGrid;
	LinkedList<ItemUnit> itemsOnScreen;
	LinkedList<ItemUnit> itemsTouchedByPlayer;
	Random random;
	public Dictionary<ItemType,Integer> kindMap;
	
	public Item(GamePanel gp) {
		this.gp=gp;;
		this.itemsGrid = new ItemUnit[gp.WT_SIZE_Y][gp.WT_SIZE_X];
		kindMap = new Hashtable<>();
		
		try {
			initImages() ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		itemsOnScreen = new LinkedList<>();
		itemsTouchedByPlayer = new LinkedList<>();
		//itemsGrid[11][11] = new ItemUnit(15,15,ItemType.MEDKIT);
		//itemsGrid[25][25] = new ItemUnit(25,25,ItemType.MEDKIT);
		addItem(11,11,ItemType.MEDKIT);
		addItem(25,25,ItemType.MEDKIT);
		addItem(11,45,ItemType.MEDKIT);
		addItem(31,55,ItemType.BRIEFCASEBL);
		addItem(35,35,ItemType.NOTE);
		addItem(35,45,ItemType.NOTE);
		addItem(35,55,ItemType.NOTE);
		addItem(35,65,ItemType.NOTE);
		addItem(35,75,ItemType.NOTE);
		
		randomPlaceItem(11,ItemType.MEDKIT);
		randomPlaceItem(25,ItemType.NOTE);
		randomPlaceItem(80,ItemType.SAPPHIRE);
		randomPlaceItem(80,ItemType.RUBY);
		randomPlaceItem(80,ItemType.EMERALD);
		
	}
	
	public enum ItemType{
		NONE,
		MEDKIT,
		KEY1,
		KEY2,
		NOTE,
		NOTE2,
		BRIEFCASEBL,
		EMERALD,
		SAPPHIRE,
		RUBY
	}
	
	public void randomPlaceItem(int amount, ItemType type) {
		int itemsPlaced = 0;
		this.random = new Random();
		int tmp=0;
		// loop through tileGrid
		// check if tile is colliding or open
		// use random number to decide place item or not
		do {
			for (int y = 1; y< gp.tileGrid.length;y++) {
				for (int x = 1; x< gp.tileGrid[0].length;x++) {
					tmp = random.nextInt(RANDOM_ITEM_DENSITY);
					if(gp.tileGrid[y][x].kind==1 || tmp!=10 ||
							(x <  MINIMUM_RANDOM_GRIDX &&
							y < MINIMUM_RANDOM_GRIDX)
							
							) {
						continue;
					}
					try {
						itemsGrid[y][x] = new ItemUnit(x,y,type);
					}catch(Exception e) {
						
					}
					itemsPlaced++;
					if (itemsPlaced>=amount)break;
				}if (itemsPlaced>=amount)break;
			}
		}while(itemsPlaced<amount);
		
		
	}
	
	class ItemUnit{
		
		public int tileX, tileY, worldX, worldY, height, width;
		public boolean closed, flagForRemoval;
		public String state = "normal";
		public ItemType itemType;
		//tilex/y is the worldx/y divided by tilesize
		public ItemUnit(int tileX, int tileY, ItemType itemType) {
			this.tileX=tileX;
			this.tileY=tileY;
			this.worldX = tileX * gp.TILE_SIZE_PX+ ITEM_TLC_OFFSET;
			this.worldY = tileY * gp.TILE_SIZE_PX+ ITEM_TLC_OFFSET;
			this.height= (int) ((float)gp.TILE_SIZE_PX*ITEM_DRAWSIZE_FACTOR);
			this.width= (int) ((float)gp.TILE_SIZE_PX*ITEM_DRAWSIZE_FACTOR);
			this.itemType = itemType;
			this.flagForRemoval=false;
			
		}
		
		
		
	}
	
	public void pickupItem(ItemUnit item) {
		System.out.println("Picked up item "+ item.itemType.name());
		
		if (item.itemType==ItemType.MEDKIT) {
			gp.player.health=100;
		}else if(item.itemType==ItemType.EMERALD||
				item.itemType==ItemType.RUBY||
				item.itemType==ItemType.SAPPHIRE) {
			gp.hud.gemCount+=1;
		}
		gp.sound.clipPlayFlags[2]=true;
		item.flagForRemoval=true;
		
	}
	
	
	public void update() {
		try {
			itemsTouchedByPlayer();
		}catch(Exception e) {
			
		}
		
//		for (ItemUnit item: itemsTouchedByPlayer) {
//			if(item!=null) {
//				
//			}
//			
//		}
		
	}
	
	private void initImages() throws IOException {
		this.itemImages = new BufferedImage[20];
		itemImages[0]=(ImageIO.read(
				getClass().getResourceAsStream("/items/medkit.png")));
		itemImages[1]=(ImageIO.read(
				getClass().getResourceAsStream("/items/key_old.png")));
		itemImages[2]=(ImageIO.read(
				getClass().getResourceAsStream("/items/key_old_blue.png")));
		itemImages[3]=(ImageIO.read(
				getClass().getResourceAsStream("/items/briefcase_bl.png")));
		itemImages[4]=(ImageIO.read(
				getClass().getResourceAsStream("/items/note.png")));
		itemImages[5]=(ImageIO.read(
				getClass().getResourceAsStream("/items/note2.png")));
		itemImages[6]=(ImageIO.read(
				getClass().getResourceAsStream("/items/emerald3.png")));
		itemImages[7]=(ImageIO.read(
				getClass().getResourceAsStream("/items/ruby.png")));
		itemImages[8]=(ImageIO.read(
				getClass().getResourceAsStream("/items/sapphire2.png")));
		
		if(kindMap==null)kindMap= new Hashtable<>();
		
		//resize scale images
		Image tmp_image;
		BufferedImage tmp_bimage;
		for(int i = 0; i< itemImages.length;i++) {
			if (itemImages[i]==null)continue;
			tmp_image = itemImages[i].getScaledInstance(ITEM_SCALE_PX,ITEM_SCALE_PX,Image.SCALE_SMOOTH);
			tmp_bimage = new BufferedImage(ITEM_SCALE_PX,ITEM_SCALE_PX,BufferedImage.TYPE_INT_ARGB);
			tmp_bimage.getGraphics().drawImage(tmp_image, 0, 0, null);
			itemImages[i] = tmp_bimage;
		}
		
		kindMap.put(ItemType.MEDKIT, 0);
		kindMap.put(ItemType.KEY1, 1);
		kindMap.put(ItemType.KEY2, 2);
		kindMap.put(ItemType.BRIEFCASEBL, 3);
		kindMap.put(ItemType.NOTE, 4);
		kindMap.put(ItemType.NOTE2, 5);
		kindMap.put(ItemType.EMERALD, 6);
		kindMap.put(ItemType.RUBY, 7);
		kindMap.put(ItemType.SAPPHIRE, 8);
		
	}
	/** 
	 * return visible screen area in world tiles X,Y,X,Y
	 * @return
	 */
	public int[] getVisibleArea() {
		int ULCX = gp.wpScreenLocX / gp.TILE_SIZE_PX;
		int ULCY = gp.wpScreenLocY / gp.TILE_SIZE_PX;
		int BRCX = (gp.wpScreenLocX+gp.WIDTH) / gp.TILE_SIZE_PX;
		int BRCY = (gp.wpScreenLocY+gp.HEIGHT) / gp.TILE_SIZE_PX;
		return new int[] {ULCX,ULCY,BRCX,BRCY};
		
	}
	
	public int clamp(int min, int max, int test) {
		if(test>max) {
			return max;
		}else if (test< min) {
			return min;
		}else {
			return test;
		}
	}
	/**
	 * adds items that collide with player to a list
	 */
	public void itemsTouchedByPlayer() {
		
		Rectangle itemRect;
		itemsTouchedByPlayer.clear();
		
		Rectangle playerRect = gp.player.wpSolidArea;
		
		for (ItemUnit item: this.itemsOnScreen) {
			if(item==null||item.flagForRemoval)continue;
			itemRect = new Rectangle(
					item.worldX,
					item.worldY,
					item.width,
					item.height);
			if (itemRect.intersects(playerRect)) {
				//itemsTouchedByPlayer.add(item);
				try {
					pickupItem(item);
				}catch(Exception e){
					
				}
				
			}
			
		}
		
		
				
	}
	/**
	 * draws the items on screen, also adds onscreen items to a list
	 */
	public void draw() {
		itemsTouchedByPlayer.clear();
		itemsOnScreen = new LinkedList<>();
		int[] visible = getVisibleArea();
		int TopLeftCornerX = gp.wpScreenLocX;
		int TopLeftCornerY = gp.wpScreenLocY;
		int maxy = itemsGrid.length;
		int maxx = itemsGrid[0].length;
		int startx = clamp(0,maxx,visible[0]-50);
		int starty = clamp(0,maxy,visible[1]-50);
		int endx = clamp(0,maxx,visible[2]+50);
		int endy = clamp(0,maxy,visible[3]+50);
		int screenX,screenY;
		ItemUnit tmp;
		
		
		
		for (int y = starty; y < endy; y++) {
			
			for (int x = startx; x < endy; x++) {
				if (itemsGrid[y][x]==null) {
					
					continue;
				}else if(itemsGrid[y][x].flagForRemoval==true){
					itemsGrid[y][x]=null;
					continue;
				}
				
				else {
					tmp=itemsGrid[y][x];
					//itemsOnScreen.clear();
					itemsOnScreen.add(tmp);
					screenX = tmp.worldX - TopLeftCornerX;
					screenY = tmp.worldY - TopLeftCornerY;
					
					gp.g2.drawImage(
							itemImages[kindMap.get(tmp.itemType)],
							screenX,
							screenY,
							tmp.width,
							tmp.height,
							null);
					//gp.g2.fillRect(screenX, screenY, 5, 5);
				}
			}
			
		}
	}
	
	public void addItem(int tileGridX, int tileGridY, ItemType it) {
		try {
			itemsGrid[tileGridY][tileGridX] = new ItemUnit(tileGridX,tileGridY,it);
		}catch(Exception e) {
			
		}
		
		 
		
	}
	
	
	
		
		
	}


