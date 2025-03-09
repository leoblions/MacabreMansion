package main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TileManager {
	
	GamePanel gp;
	public Tile[][] tileGrid;
	BufferedImage[] bufferedImages;
	TilePlacer tilePlacer;

	public TileManager(GamePanel gp) {
		this.gp=gp;
		newTileGrid();
		try { initImages(); }catch(Exception e){e.printStackTrace();}
		
		//place tile patterns with tilePlacer
		tilePlacer = new TilePlacer(gp, this.tileGrid);
		tilePlacer.placeTiles();
	}
	
	public void newTileGrid() {
		this.tileGrid = new Tile[GamePanel.WT_SIZE_Y][GamePanel.WT_SIZE_X];
		for (int y = 0; y < GamePanel.WT_SIZE_Y;y++) {
			for (int x = 0; x < GamePanel.WT_SIZE_X;x++) {
				tileGrid[y][x]= new Tile(x,y,0);
			}
		}
	}
	
	public void initImages() throws IOException {
		this.bufferedImages = new BufferedImage[20];
		this.bufferedImages[0] = ImageIO.read(getClass().getResourceAsStream("/tiles/metaltileD.png"));
		this.bufferedImages[1] = ImageIO.read(getClass().getResourceAsStream("/tiles/metal.png"));
		this.bufferedImages[2] = ImageIO.read(getClass().getResourceAsStream("/tiles/diamondtile.png"));
		this.bufferedImages[3] = ImageIO.read(getClass().getResourceAsStream("/tiles/yellowcarpet.png"));
		this.bufferedImages[4] = ImageIO.read(getClass().getResourceAsStream("/tiles/oldsubwaytile.png"));
		this.bufferedImages[5] = ImageIO.read(getClass().getResourceAsStream("/tiles/cobblestonem.png"));
		this.bufferedImages[6] = ImageIO.read(getClass().getResourceAsStream("/tiles/grass50a.png"));
		this.bufferedImages[7] = ImageIO.read(getClass().getResourceAsStream("/tiles/wood50b.png"));
		this.bufferedImages[8] = ImageIO.read(getClass().getResourceAsStream("/tiles/diamondtile2.png"));
		this.bufferedImages[9] = ImageIO.read(getClass().getResourceAsStream("/tiles/wood50a.png"));
		this.bufferedImages[10] = ImageIO.read(getClass().getResourceAsStream("/tiles/tilesbrown.png"));
		this.bufferedImages[11] = ImageIO.read(getClass().getResourceAsStream("/tiles/tilesRB.png"));
	}
	/**
	 * 
	 * Which tiles are visible on screen
	 * 
	 * [top left corner x,
	 * top left corner y,
	 * bottom right corner x,
	 * bottom right corner y
	 * ]
	 * 
	 * @return
	 */
	public int[] getDrawableRange() {
		int[] ranges = {0,0,0,0};
		ranges[0] = GamePanel.wpScreenLocX / GamePanel.TILE_SIZE_PX;
		ranges[1] = GamePanel.wpScreenLocY / GamePanel.TILE_SIZE_PX;
		ranges[2] = (GamePanel.wpScreenLocX+GamePanel.WIDTH) / GamePanel.TILE_SIZE_PX;
		ranges[3] = (GamePanel.wpScreenLocY+GamePanel.HEIGHT) / GamePanel.TILE_SIZE_PX;
		return ranges;
		
	}
	
	private void renderTile(Tile tile) {
		int screenX = tile.worldX - GamePanel.wpScreenLocX;
		int screenY = tile.worldY - GamePanel.wpScreenLocY;
		gp.g2.drawImage(bufferedImages[tile.kind], screenX, screenY, GamePanel.TILE_SIZE_PX, GamePanel.TILE_SIZE_PX,null);
		
	}
	
	private int clamp(int min, int max, int test) {
		if(test>max)return max;
		if(test<min)return min;
		return test;
	}
	
	public void draw() {
		
		int[] ranges = getDrawableRange();
		int startx = clamp(0,tileGrid[0].length,ranges[0]);
		int starty = clamp(0,tileGrid.length,ranges[1]);
		int endx = clamp(0,tileGrid[0].length,ranges[2]+1);
		int endy = clamp(0,tileGrid.length,ranges[3]+1);
		for (int y = starty; y < endy;y++) {
			for (int x = startx; x < endx;x++) {
				renderTile(tileGrid[y][x]);
			}
		}
		
	}
	
	public void highlightTile(int tileX, int tileY, Color color) {
		
		if(gp.g2==null)return;
		int screenX = tileX*gp.TILE_SIZE_PX - GamePanel.wpScreenLocX;
		int screenY = tileY*gp.TILE_SIZE_PX - GamePanel.wpScreenLocY;
		gp.g2.setColor(color);
		gp.g2.fillRect(
				screenX, 
				screenY, 
				gp.TILE_SIZE_PX , 
				gp.TILE_SIZE_PX );
		
	}
	
	public void update() {
		
	}

}
