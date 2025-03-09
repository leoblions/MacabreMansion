package main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Random;

import javax.imageio.ImageIO;

import main.Item.ItemType;
import main.Item.ItemUnit;

//import main.Tile.TileType;

public class Decor {
	GamePanel gp;
	BufferedImage[] decorImages;
	Random random;
	public DecorItem[][] worldDecorGrid ;
	public final int maxDecorOnScreen = 200;
	public final int defaultDecorSizePx = 25;
	public final int defaultDecorSizePxX = 25;
	public final int defaultDecorSizePxY = 25;
	public final int minTilesDrawX = 16;
	public final int minTilesDrawY = 12;
	public final int RANDOM_ITEM_DENSITY = 50;
	public final int MINIMUM_RANDOM_GRIDX = 300;
	public Dictionary<DecorType,Integer> kindMap;
	int drawableRange;
	public final int WALL_TILE_TYPE = 1;
	
	public Decor(GamePanel gp) {
		this.gp=gp;
		
		worldDecorGrid = new DecorItem[gp.WT_SIZE_Y][gp.WT_SIZE_X];
		BufferedImage[] decorImages= new BufferedImage[10];
		try {
			initDecorImages();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//drawRectOfDecorStretch(13, 3, 5, 5, DecorType.BARREL);
		//drawRectOfDecor(15, 5, 5, 5, DecorType.BARREL);
		//drawRectOfDecor(32, 42, 6, 6, DecorType.CHAIR2);
		//drawRectOfDecor(17, 33, 1, 4, DecorType.CHAIR2);
		//drawRectOfDecorStretch(33, 3, 5, 5, DecorType.LOLLIPOP_TREE);
		//drawRectOfDecor(3, 33, 4, 1, DecorType.OLDPC);
		//drawRectOfDecor(3, 35, 4, 1, DecorType.FOUNTAIN);
		//drawRectOfDecor(23, 3, 4, 4, DecorType.TOMBSTONE);
		//drawRectOfDecor(42, 2, 6, 6, DecorType.TOMBSTONE);
		randomPlaceDecor(100,DecorType.BARREL,0);
		randomPlaceDecor(50,DecorType.CHAIR2,2);
		randomPlaceDecor(50,DecorType.CHAIR2,3);
		randomPlaceDecor(50,DecorType.CHAIR2,4);
		randomPlaceDecor(50,DecorType.CHAIR2,6);
		randomPlaceDecor(50,DecorType.TABLE,6);
		randomPlaceDecor(10,DecorType.SKELETON,5);
		randomPlaceDecor(10,DecorType.COBWEB,5);
		randomPlaceDecor(10,DecorType.TABLE,8);
		randomPlaceDecor(10,DecorType.TABLE,2);
		randomPlaceDecor(10,DecorType.HOUSEPLANT,2);
		drawWallShadow();
		

		
		

	}
	
	public void randomPlaceDecor(int amount, DecorType dtype, int tileKind) {
		int itemsPlaced = 0;
		this.random = new Random();
		int tmp=0;
		int height,width;
		boolean keepTrying = false;
		// loop through tileGrid
		// check if tile is colliding or open
		// use random number to decide place item or not
		do {
			for (int y = 1; y< gp.tileGrid.length;y++) {
				for (int x = 1; x< gp.tileGrid[0].length;x++) {
					tmp = random.nextInt(RANDOM_ITEM_DENSITY);
//					if(gp.tileGrid[y][x].kind!=tileKind || tmp!=10 ||
//							(x <  MINIMUM_RANDOM_GRIDX &&
//							y < MINIMUM_RANDOM_GRIDX)
//							
//							) {
//						continue;
//					}
					if(gp.tileGrid[y][x].kind==tileKind && tmp==10) {
						try {
							//get height and width of image
							height = decorImages[kindMap.get( dtype)].getHeight();
							width = decorImages[kindMap.get( dtype)].getWidth();
							//place new decor object on the matrix
							worldDecorGrid[y][x] = new DecorItem(
									gp.TILE_SIZE_PX*x,
									gp.TILE_SIZE_PX*y,
									width,
									height,
									dtype);
						}catch(Exception e) {
							System.out.println(" randomPlaceDecor failed"+itemsPlaced);
							
						}
						itemsPlaced++;
					}else {
						
						
					}
					
					
					
					if (itemsPlaced>=amount)break;
				}if (itemsPlaced>=amount)break;
			}
		}while(keepTrying);
		
		
	}
	
	public void drawWallShadow() {
		int kind;
		for(int y = 0;y<gp.tileGrid.length-1;y++) {
			for(int x = 0;x<gp.tileGrid[0].length;x++) {
				//place shadows on wall tiles
				kind = gp.tileGrid[y][x].kind;
				if(kind==WALL_TILE_TYPE&&
						gp.tileGrid[y+1][x].kind!=WALL_TILE_TYPE) {
					worldDecorGrid[y][x] = new DecorItem(
							gp.TILE_SIZE_PX*x,
							gp.TILE_SIZE_PX*y,
							gp.TILE_SIZE_PX,
							gp.TILE_SIZE_PX,
							DecorType.WALLSHADOW);
				}
				
				
			}
		}
		
	}
	
	public void putDecorOnTileType(int kind, DecorType dtype) {
		//int kind;
		for(int y = 0;y<gp.tileGrid.length-1;y++) {
			for(int x = 0;x<gp.tileGrid[0].length;x++) {
				//place shadows on wall tiles
				kind = gp.tileGrid[y][x].kind;
				if(kind==WALL_TILE_TYPE&&
						gp.tileGrid[y+1][x].kind!=WALL_TILE_TYPE) {
					
					worldDecorGrid[y][x] = new DecorItem(
							gp.TILE_SIZE_PX*x,
							gp.TILE_SIZE_PX*y,
							gp.TILE_SIZE_PX,
							gp.TILE_SIZE_PX,
							DecorType.WALLSHADOW);
				}
				
				
			}
		}
		
	}
	
	
	public int clamp(int minval, int maxval, int test) {
		if (test<minval) return minval;
		if (test> maxval) return maxval;
		return test;
	}
	
	
	/**
	 * xmin, ymin, xmax, ymax
	 * visible on decor gred
	 * @return
	 */
	public int[] gridRange() {
		int[] range = new int[4];
		range[0] = gp.wpScreenLocX / gp.TILE_SIZE_PX;
		range[1] = gp.wpScreenLocY / gp.TILE_SIZE_PX;
		range[2] = (gp.WIDTH+gp.wpScreenLocX) / gp.TILE_SIZE_PX;
		range[3] = (gp.HEIGHT+ gp.wpScreenLocY )/ gp.TILE_SIZE_PX;
		return range;
	}
	
	
	
	public void draw() {
		
		
		//subtract worldCoord of top left screen corner from worldCord of image
		int TopLeftCornerX = gp.wpScreenLocX;
		int TopLeftCornerY = gp.wpScreenLocY;
		int screenX, screenY;
		int[] drawableRange = gridRange();
		
		//sprite culling distances
		int xstart = drawableRange[0]-4;
		int ystart = drawableRange[1]-4;
		int xend = drawableRange[2]+4;
		int yend = drawableRange[3]+4;
		
		if (ystart<0) ystart=0;
		if (xstart<0) xstart=0;
		if (xend>gp.WT_SIZE_X) xend=gp.WT_SIZE_X;
		if (yend>gp.WT_SIZE_Y) yend=gp.WT_SIZE_Y;

		
		//prevent array out of bounds/null pointer errors
		clamp(0,gp.WT_SIZE_X,xend);
		clamp(0,gp.WT_SIZE_Y,yend);
		
		

		
		for (int x = xstart; x < xend; x++ ) {
			for (int y = ystart; y < yend; y++ ) {
				DecorItem currentDecor = worldDecorGrid[y][x];
				if (currentDecor!= null) {
					//System.out.println("tree");
					screenX = currentDecor.worldX - TopLeftCornerX;
					screenY = currentDecor.worldY - TopLeftCornerY;
					gp.g2.drawImage(
							decorImages[kindMap.get(currentDecor.dtype)],
							screenX,
							screenY,
							currentDecor.width,
							currentDecor.height,
							null);
					
				}
				
			}
		}
		//draw cross
		gp.g2.setColor(Color.red);
		gp.g2.fillRect(22, 22, 7, 20); //up
		gp.g2.fillRect(16, 28, 20, 7); //right
		
	}
	
	public void addTree(int tileX, int tileY) {
		DecorItem tree = new DecorItem(
				gp.TILE_SIZE_PX*tileX+8,
				gp.TILE_SIZE_PX*tileY+8,
				defaultDecorSizePx,
				defaultDecorSizePx+16,
				DecorType.LOLLIPOP_TREE);
		worldDecorGrid[tileY][tileX] = tree;
	}
	
	public void drawRectOfDecor(
			
			int startx, 
			int starty, 
			int width, 
			int height,
			DecorType dtype) {
		if (startx<0)startx=0;
		if (starty<0)starty=0;
		
		int endx = startx + width;
		int endy = starty + height;
		for (int x = startx; x<endx ; x++ ) {
			for (int y = starty; y<endy; y++ ) {
				//addTree( x,  y);
				DecorItem dec = new DecorItem(
						gp.TILE_SIZE_PX*x,
						gp.TILE_SIZE_PX*y,
						defaultDecorSizePx,
						defaultDecorSizePx,
						dtype);
				worldDecorGrid[y][x] = dec;
			}
				
			}
			
		}
	private void drawRectOfDecorStretch(
			int startx, 
			int starty, 
			int width, 
			int height,
			DecorType dtype) {
		if (startx<0)startx=0;
		if (starty<0)starty=0;
		
		int endx = startx + width;
		int endy = starty + height;
		for (int x = startx; x<endx ; x++ ) {
			for (int y = starty; y<endy; y++ ) {
				//addTree( x,  y);
				DecorItem dec = new DecorItem(
						gp.TILE_SIZE_PX*x+8,
						gp.TILE_SIZE_PX*y,
						defaultDecorSizePx,
						defaultDecorSizePx+16,
						dtype);
				worldDecorGrid[y][x] = dec;
			}
				
			}
			
		}
	
	
	public void update() {
		
		
	}
	
	public enum DecorType{
		LOLLIPOP_TREE,
		BARREL,
		CHAIR2,
		OLDPC,
		FOUNTAIN,
		TOMBSTONE,
		WALLSHADOW,
		BOX,
		TABLE, SKELETON, COBWEB,HOUSEPLANT
	}
	
	public class DecorItem{
		public int worldX,worldY, height, width;
		public DecorType dtype;
		DecorItem(int worldX,
				int worldY,
				int width,
				int height,
				DecorType dtype){
			this.worldX = worldX;
			this.worldY = worldY;
			this.dtype=dtype;
			this.height=height;
			this.width=width;
		}
		
	}
	
	public boolean visibleOnScreen(int worldX, int worldY) {
		int buffer = 100;
		int swx = gp.wpScreenLocX;
		int swy = gp.wpScreenLocY;
		if (
				worldX> swx -buffer &&
				worldY> swy -buffer &&
				worldX< swx + buffer + gp.WIDTH &&
				worldX< swy + buffer + gp.HEIGHT 
				) {
			return true;
		}else {
			return false;
		}
				
		
		
		
	}
	
	
	
	
	
	private void initDecorImages() throws IOException {
		this.decorImages = new BufferedImage[20];
		decorImages[0]=(ImageIO.read(
				getClass().getResourceAsStream("/decor/lollipop_tree.png")));
		decorImages[1]=(ImageIO.read(
				getClass().getResourceAsStream("/decor/barrel2.png")));
		decorImages[2]=(ImageIO.read(
				getClass().getResourceAsStream("/decor/chair2.png")));
		decorImages[3]=(ImageIO.read(
				getClass().getResourceAsStream("/decor/oldpc2.png")));
		decorImages[4]=(ImageIO.read(
				getClass().getResourceAsStream("/decor/fountain.png")));
		decorImages[5]=(ImageIO.read(
				getClass().getResourceAsStream("/decor/tombstone.png")));
		decorImages[6]=(ImageIO.read(
				getClass().getResourceAsStream("/decor/wallshadow50.png")));
		decorImages[7]=(ImageIO.read(
				getClass().getResourceAsStream("/decor/lollipop_tree.png")));
		decorImages[8]=(ImageIO.read(
				getClass().getResourceAsStream("/decor/table.png")));
		decorImages[9]=(ImageIO.read(
				getClass().getResourceAsStream("/decor/skeleton.png")));
		decorImages[10]=(ImageIO.read(
				getClass().getResourceAsStream("/decor/cobweb.png")));
		decorImages[11]=(ImageIO.read(
				getClass().getResourceAsStream("/decor/houseplant.png")));
		
		kindMap= new Hashtable<>();
		kindMap.put(DecorType.LOLLIPOP_TREE, 0);
		kindMap.put(DecorType.BARREL, 1);
		kindMap.put(DecorType.CHAIR2, 2);
		kindMap.put(DecorType.OLDPC, 3);
		kindMap.put(DecorType.FOUNTAIN, 4);
		kindMap.put(DecorType.TOMBSTONE, 5);
		kindMap.put(DecorType.WALLSHADOW, 6);
		kindMap.put(DecorType.BOX, 7);
		kindMap.put(DecorType.TABLE, 8);
		kindMap.put(DecorType.SKELETON, 9);
		kindMap.put(DecorType.COBWEB, 10);
		kindMap.put(DecorType.HOUSEPLANT, 11);
//		kindMap.put(DecorType.SLIME, 10);
		
	}

}
