package com.mojang.ld22.level.levelgen;

import java.util.Random;

import com.mojang.ld22.level.tile.Tile;

/**
 * 		History generator
 * 
 * Housing the overly-complex logic of map alternation to simulate human
 * presence on the map. It should appear that the map has been inhabited by
 * someone before the player entered this realm.
 * 
 * @author Dejvino
 *
 */
public class HistoryGen
{
	/**
	 * This is the main method that does all the work and enhances a given map.
	 * 
	 * @param originalMap
	 * @param w
	 * @param h
	 * @return Altered map.
	 */
	public static byte[][] addHistoryToMap(byte[][] originalMap, int w, int h)
	{
		HistoryGen inst = new HistoryGen(originalMap, w, h);
		return inst.addHistory();
	}

	private byte[][] originalMap;
	private int w;
	private int h;
	private Random rand = new Random();
	
	public static byte[] freeSpaceIds = new byte[] {
		Tile.grass.id, Tile.flower.id, Tile.dirt.id, Tile.sand.id
	};

	public static byte[] plainsIds = new byte[] {
		Tile.grass.id, Tile.flower.id
	};
	public static byte[] forestIds = new byte[] {
		Tile.tree.id, Tile.grass.id, Tile.dirt.id
	};
	
	public HistoryGen(byte[][] originalMap, int w, int h)
	{
		this.originalMap = originalMap;
		this.w = w;
		this.h = h;
	}
	
	/**
	 * The actual worker-method to be used from the context of an instance.
	 * Called by {@see addHistoryToMap()}.
	 * 
	 * @return Altered map.
	 */
	public byte[][] addHistory()
	{
		byte[] map = new byte[w * h];
		byte[] data = new byte[w * h];
		for (int i = 0; i < w*h; i++) {
			map[i] = originalMap[0][i];
			data[i] = originalMap[1][i];
		}

		int places;
		
		// generate houses on plains (cities / villages)
		places = rand.nextInt(200) + 2;
		for (int i = 0; i < places; i++) {
			addRandomSceneryItem(map, HistoryGenPattern.buildings, 2, plainsIds, 0.95);
		}
		
		// generate forest scenery
		places = rand.nextInt(50);
		for (int i = 0; i < places; i++) {
			addRandomSceneryItem(map, HistoryGenPattern.sceneryForest, 8, forestIds, 0.9);
		}
		
		return new byte[][] { map, data };
	}
	
	/**
	 * Picks a random scenery item, finds a suitable place for it and places it.
	 *  
	 * @param map
	 * @param scenery
	 * @param margin
	 * @param tileIds
	 * @param threshold
	 * @return Were we successful?
	 */
	public boolean addRandomSceneryItem(byte[] map, byte[][][] scenery, int margin, byte[] tileIds, double threshold)
	{
		// pick random pattern
		byte[][] pattern = scenery[rand.nextInt(scenery.length)];
		
		// find a tile cluster
		int radius = (pattern.length > pattern[0].length)
						? pattern.length
						: pattern[0].length;
		radius += margin;
		int pos = this.findTileCluster(map, tileIds, radius, threshold);
		if (pos == -1) {
			// could not find
			return false;
		}
		
		// place pattern randomly
		int x = pos % w;
		int y = pos / w;
		this.applyPattern(map, pattern, x, y, rand.nextInt(8));
		
		return true;
	}
	
	/**
	 * Returns a map index (position) of an area that satisfies the given criteria.
	 * It is used to find an area with at least threshold% of given tile type.
	 * 
	 * The area which is inspected is rectangular.
	 * 
	 * @param tileId Tile to look for.
	 * @param radius Radius of tiles in which to search.
	 * @param threshold What is the minimal amount of the tiles in this area.
	 * 			Range is from 0 (no tiles) to 1 (everything is this tile)
	 * @return The position of the cluster or -1 which indicates that no such
	 * 			area could be found.
	 */
	public int findTileCluster(byte[] map, byte[] tileIds, int radius, double threshold)
	{
		int attempt = 0;
		while (attempt < 100) {
			attempt++;
			
			// find random position
			int position = rand.nextInt(w*h);
			
			// inspect the area
			int tilesOk = 0;
			for (int x = 0; x < radius; x++) {
				for (int y = 0; y < radius; y++) {
					int xr = (position % w) + x - radius/2;
					int yr = (position / w) + y - radius/2;
					int i = xr + yr*w;
					if (xr <= 0 || xr >= w || yr <= 0 || yr >= h) {
						continue;
					}
					// check for any of the valid tiles
					for (int t = 0; t < tileIds.length; t++) {
						if (map[i] == tileIds[t]) {
							tilesOk++;
							break;
						}
					}
				}
			}
			
			// check the result
			double share = tilesOk / (radius*radius);
			if (share >= threshold) {
				return position;
			}
		}
		return -1;
	}
	
	/**
	 * Replaces tiles in the map by a given pattern of tiles.
	 * 
	 * @param map
	 * @param pattern
	 * @param x
	 * @param y
	 * @param dir
	 */
	public void applyPattern(byte[] map, byte[][] pattern, int x, int y, int dir)
	{
		// prepare pattern
		byte[][] p;
		// transpose it if necessary
		if (dir / 4 == 0) {
			p = pattern;
		} else {
			p = HistoryGenPattern.transpose(pattern);
		}
		
		for (int ya = 0; ya < p.length; ya++) {
			for (int xa = 0; xa < p[ya].length; xa++) {
				int yp = (dir / 2 == 0) ? ya : p.length - (ya+1);
				int xp = (dir % 2 == 0) ? xa : p[ya].length - (xa+1);
				if (p[yp][xp] <= 0) {
					continue; // transparent tile
				}
				if (x + xa < 0 || x + xa >= w || y + ya < 0 || y + ya >= h) {
					continue; // not visible, out of range
				}
				map[(x + xa) + (y + ya)*w] = p[yp][xp];
			}
		}
	}
}
