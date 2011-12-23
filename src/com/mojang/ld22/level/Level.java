package com.mojang.ld22.level;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.mojang.ld22.entity.AirWizard;
import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.LivingEntity;
import com.mojang.ld22.entity.Mob;
import com.mojang.ld22.entity.Npc;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.entity.Slime;
import com.mojang.ld22.entity.Wanderer;
import com.mojang.ld22.entity.Zombie;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.level.levelgen.LevelGen;
import com.mojang.ld22.level.tile.Tile;

public class Level implements Externalizable {
	private Random random = new Random();

	public int w, h;

	public byte[] tiles;
	public byte[] data;
	public List<Entity>[] entitiesInTiles;

	public int grassColor = 141;
	public int dirtColor = 322;
	public int sandColor = 550;
	private int depth;
	public int monsterDensity = 8;

	private int randomFog = 0;
	
	public List<Entity> entities = new ArrayList<Entity>();
	private Comparator<Entity> spriteSorter = new Comparator<Entity>() {
		public int compare(Entity e0, Entity e1) {
			if (e1.y < e0.y) return +1;
			if (e1.y > e0.y) return -1;
			return 0;
		}

	};

	public Level() {
	}
	
	@SuppressWarnings("unchecked")
	public Level(int w, int h, int level, Level parentLevel) {
		if (level < 0) {
			dirtColor = 222;
		}
		this.depth = level;
		this.w = w;
		this.h = h;
		byte[][] maps;

		if (level == 1) {
			dirtColor = 444;
		}
		if (level == 0)
			maps = LevelGen.createAndValidateTopMap(w, h);
		else if (level < 0) {
			maps = LevelGen.createAndValidateUndergroundMap(w, h, -level);
			monsterDensity = 4;
		} else {
			maps = LevelGen.createAndValidateSkyMap(w, h); // Sky level
			monsterDensity = 4;
		}

		tiles = maps[0];
		data = maps[1];

		if (parentLevel != null) {
			for (int y = 0; y < h; y++)
				for (int x = 0; x < w; x++) {
					if (parentLevel.getTile(x, y) == Tile.stairsDown) {

						setTile(x, y, Tile.stairsUp, 0);
						if (level == 0) {
							setTile(x - 1, y, Tile.hardRock, 0);
							setTile(x + 1, y, Tile.hardRock, 0);
							setTile(x, y - 1, Tile.hardRock, 0);
							setTile(x, y + 1, Tile.hardRock, 0);
							setTile(x - 1, y - 1, Tile.hardRock, 0);
							setTile(x - 1, y + 1, Tile.hardRock, 0);
							setTile(x + 1, y - 1, Tile.hardRock, 0);
							setTile(x + 1, y + 1, Tile.hardRock, 0);
						} else {
							setTile(x - 1, y, Tile.dirt, 0);
							setTile(x + 1, y, Tile.dirt, 0);
							setTile(x, y - 1, Tile.dirt, 0);
							setTile(x, y + 1, Tile.dirt, 0);
							setTile(x - 1, y - 1, Tile.dirt, 0);
							setTile(x - 1, y + 1, Tile.dirt, 0);
							setTile(x + 1, y - 1, Tile.dirt, 0);
							setTile(x + 1, y + 1, Tile.dirt, 0);
						}
					}

				}
		}

		entitiesInTiles = new ArrayList[w * h];
		for (int i = 0; i < w * h; i++) {
			entitiesInTiles[i] = new ArrayList<Entity>();
		}
		
		if (level==1) {
			AirWizard aw = new AirWizard();
			aw.x = w*8;
			aw.y = h*8;
			add(aw);
		}
	}

	public void renderBackground(Screen screen, int xScroll, int yScroll) {
		int xo = xScroll >> 4;
		int yo = yScroll >> 4;
		int w = (screen.w + 15) >> 4;
		int h = (screen.h + 15) >> 4;
		screen.setOffset(xScroll, yScroll);
		for (int y = yo; y <= h + yo; y++) {
			for (int x = xo; x <= w + xo; x++) {
				getTile(x, y).render(screen, this, x, y);
			}
		}
		screen.setOffset(0, 0);
	}

	private List<Entity> rowSprites = new ArrayList<Entity>();

	public Player player;

	public void renderSprites(Screen screen, int xScroll, int yScroll) {
		int xo = xScroll >> 4;
		int yo = yScroll >> 4;
		int w = (screen.w + 15) >> 4;
		int h = (screen.h + 15) >> 4;

		screen.setOffset(xScroll, yScroll);
		for (int y = yo; y <= h + yo; y++) {
			for (int x = xo; x <= w + xo; x++) {
				if (x < 0 || y < 0 || x >= this.w || y >= this.h) continue;
				rowSprites.addAll(entitiesInTiles[x + y * this.w]);
			}
			if (rowSprites.size() > 0) {
				sortAndRender(screen, rowSprites);
			}
			rowSprites.clear();
		}
		screen.setOffset(0, 0);
	}

	public void renderLight(Screen screen, int xScroll, int yScroll) {
		int xo = xScroll >> 4;
		int yo = yScroll >> 4;
		int w = (screen.w + 15) >> 4;
		int h = (screen.h + 15) >> 4;

		screen.setOffset(xScroll, yScroll);
		int r = 4;
		for (int y = yo - r; y <= h + yo + r; y++) {
			for (int x = xo - r; x <= w + xo + r; x++) {
				if (x < 0 || y < 0 || x >= this.w || y >= this.h) continue;
				List<Entity> entities = entitiesInTiles[x + y * this.w];
				for (int i = 0; i < entities.size(); i++) {
					Entity e = entities.get(i);
					// e.render(screen);
					int lr = e.getLightRadius();
					if (lr > 0) screen.renderLight(e.x - 1, e.y - 4, lr * 8);
				}
				int lr = getTile(x, y).getLightRadius(this, x, y);
				if (lr > 0) screen.renderLight(x * 16 + 8, y * 16 + 8, lr * 8);
			}
		}
		screen.setOffset(0, 0);
	}
	
	public void renderFog(Screen screen, int xScroll, int yScroll)
	{
		int visMax = 1000;
		
		// get sizes and positions (in tiles)
		int xo = xScroll >> 4;
		int yo = yScroll >> 4;
		int w = (screen.w + 15) >> 4;
		int h = (screen.h + 15) >> 4;
		int pX = this.player.x >> 4;
		int pY = this.player.y >> 4;
		
		// resolution of raytracing
		float res = 8;
		
		// prepare visibility grid
		int[][] visibility = new int[h+1][w+1];
		
		// for every point on the edge of the screen
		int edgeMax = (int)((w+h)*2*res);
		for (int edge = 0; edge < edgeMax; edge++) {
			// determine the destination point (we do a loop)
			int dstX;
			int dstY;
			if (edge < w*res) {
				dstX = edge;
				dstY = 0;
			} else if (edge < (w+h)*res) {
				dstX = (int)(w*res);
				dstY = (int)((edge-(w*res)));
			} else if (edge < (w+w+h)*res) {
				dstX = (int)(edge - (w+h)*res);
				dstY = (int)(h*res);
			} else {
				dstX = 0;
				dstY = (int)(edgeMax-edge);
			}
			
			// determine the ray properties
			int rayPower = (int)(visMax / res);
			int rayFall = (int)(0.4*rayPower);
			int dist = (int)Math.sqrt(
								Math.pow(xo + dstX/res - pX, 2)
									+
								Math.pow(yo + dstY/res - pY, 2));
			int maxStep = dist*2;
			
			// perform step-calculations on the line |Player --> destination|
			for (int step = 0; step <= maxStep; step++) {
				float progress = step / (float)maxStep;
				int curX = pX + (int)(((xo + dstX/res) - pX) * progress);
				int curY = pY + (int)(((yo + dstY/res) - pY) * progress);
			
				if (curY - yo < 0 || curX - xo < 0) {
					continue;
				}
				
				// compute new visibility
				int curVis = visibility[curY - yo][curX - xo];
				int newVis = (int) (curVis + rayPower);
				if (curVis < newVis) {
					visibility[curY - yo][curX - xo] = newVis;
				}
				
				// lower the strength of the ray if this tile is blocking the view
				int visBlock = this.getTile(curX, curY).getVisibilityBlocking(this, curX, curY, player);
				if (visBlock > 0) {
					int tileRayFall = (int)(rayFall * (visBlock / 100.0));
					rayPower -= tileRayFall + randomFog;
					if (rayPower <= 0) {
						// this ray is dead
						break;
					}
				}
			}
		}
		
		// normalize visibility
		for (int y = 0; y <= h; y++) {
			for (int x = 0; x <= w; x++) {
				int vis = visibility[y][x] / 10;
				if (vis > 100) {
					vis = 100;
				}
				if (vis < 0) {
					vis = 0;
				}
				visibility[y][x] = vis;
			}
		}
		
		// reset screen for rendering
		screen.clear(Color.get(999));
		screen.setOffset(xScroll, yScroll);
		
		// render blocks of fog (4x4)
		for (int y = 0; y <= h; y++) {
			for (int x = 0; x <= w; x++) {
				int vis = visibility[y][x];
				int visBlend = vis;
				int xr = (xo+x)*16;
				int yr = (yo+y)*16;
				for (int z = 0; z < 4; z++) {
					int zx = (z % 2)-1;
					int zy = (z / 2)-1;
					for (int s = 0; s < 4; s++) {
						int sx = (s % 2);
						int sy = (s / 2);
						if (y+sy+zy < 0 || y+sy+zy >= h || x+sx+zx < 0 || x+sx+zx >= w) {
							visBlend += vis;
						} else {
							visBlend += visibility[y+sy+zy][x+sx+zx];
						}
					}
					visBlend /= 5;
					int color = visBlend * 2;					
					screen.renderPoint(xr + zx*8, yr + zy*8, 8, color);
				}
			}
		}
		screen.setOffset(0, 0);
	}

	private void sortAndRender(Screen screen, List<Entity> list) {
		Collections.sort(list, spriteSorter);
		for (int i = 0; i < list.size(); i++) {
			list.get(i).render(screen);
		}
	}

	public Tile getTile(int x, int y) {
		if (x < 0 || y < 0 || x >= w || y >= h) return Tile.rock;
		return Tile.tiles[tiles[x + y * w]];
	}

	public void setTile(int x, int y, Tile t, int dataVal) {
		if (x < 0 || y < 0 || x >= w || y >= h) return;
		tiles[x + y * w] = t.id;
		data[x + y * w] = (byte) dataVal;
	}

	public int getData(int x, int y) {
		if (x < 0 || y < 0 || x >= w || y >= h) return 0;
		return data[x + y * w] & 0xff;
	}

	public void setData(int x, int y, int val) {
		if (x < 0 || y < 0 || x >= w || y >= h) return;
		data[x + y * w] = (byte) val;
	}

	public void add(Entity entity) {
		if (entity instanceof Player) {
			player = (Player) entity;
		}
		entity.removed = false;
		entities.add(entity);
		entity.init(this);

		insertEntity(entity.x >> 4, entity.y >> 4, entity);
	}

	public void remove(Entity e) {
		entities.remove(e);
		int xto = e.x >> 4;
		int yto = e.y >> 4;
		removeEntity(xto, yto, e);
	}

	private void insertEntity(int x, int y, Entity e) {
		if (x < 0 || y < 0 || x >= w || y >= h) return;
		entitiesInTiles[x + y * w].add(e);
	}

	private void removeEntity(int x, int y, Entity e) {
		if (x < 0 || y < 0 || x >= w || y >= h) return;
		entitiesInTiles[x + y * w].remove(e);
	}

	public void trySpawn(int count) {
		for (int i = 0; i < count; i++) {
			LivingEntity ent;

			int minLevel = 1;
			int maxLevel = 1;
			if (depth < 0) {
				maxLevel = (-depth) + 1;
			}
			if (depth > 0) {
				minLevel = maxLevel = 4;
			}

			int lvl = random.nextInt(maxLevel - minLevel + 1) + minLevel;
			int type = random.nextInt(3);
			switch (type) {
				default:
				case 0:
					ent = new Slime(lvl);
					break;
				case 1:
					ent = new Zombie(lvl);
					break;
				case 2:
					ent = new Wanderer(lvl);
					break;
			}
				
			if (ent.findStartPos(this)) {
				this.add(ent);
			}
		}
	}

	public void tick() {
		trySpawn(1);

		/*if (random.nextInt(100) == 0) {
			randomFog += random.nextInt(3)-1;
			if (randomFog < -2) {
				randomFog = -2;
			}
			if (randomFog > 2) {
				randomFog = 2;
			}
		}*/
		
		for (int i = 0; i < w * h / 50; i++) {
			int xt = random.nextInt(w);
			int yt = random.nextInt(w);
			getTile(xt, yt).tick(this, xt, yt);
		}
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			int xto = e.x >> 4;
			int yto = e.y >> 4;

			e.tick();

			if (e.removed) {
				entities.remove(i--);
				removeEntity(xto, yto, e);
			} else {
				int xt = e.x >> 4;
				int yt = e.y >> 4;

				if (xto != xt || yto != yt) {
					removeEntity(xto, yto, e);
					insertEntity(xt, yt, e);
				}
			}
		}
	}

	public List<Entity> getEntities(int x0, int y0, int x1, int y1) {
		List<Entity> result = new ArrayList<Entity>();
		int xt0 = (x0 >> 4) - 1;
		int yt0 = (y0 >> 4) - 1;
		int xt1 = (x1 >> 4) + 1;
		int yt1 = (y1 >> 4) + 1;
		for (int y = yt0; y <= yt1; y++) {
			for (int x = xt0; x <= xt1; x++) {
				if (x < 0 || y < 0 || x >= w || y >= h) continue;
				List<Entity> entities = entitiesInTiles[x + y * this.w];
				for (int i = 0; i < entities.size(); i++) {
					Entity e = entities.get(i);
					if (e.intersects(x0, y0, x1, y1)) result.add(e);
				}
			}
		}
		return result;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		// config
		this.data = (byte[])in.readObject();
		this.depth = in.readInt();
		this.dirtColor = in.readInt();
		this.grassColor = in.readInt();
		this.h = in.readInt();
		this.monsterDensity = in.readInt();
		this.sandColor = in.readInt();
		this.tiles = (byte[])in.readObject();
		this.w = in.readInt();
		
		this.entitiesInTiles = new ArrayList[this.w * this.h];
		for (int i = 0; i < w * h; i++) {
			this.entitiesInTiles[i] = new ArrayList<Entity>();
		}
		
		// entities
		int entCount = in.readInt();
		this.entities.clear();
		for (int i = 0; i < entCount; i++) {
			Entity e = (Entity)in.readObject();
			add(e);
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		// config
		out.writeObject(this.data);
		out.writeInt(this.depth);
		out.writeInt(this.dirtColor);
		out.writeInt(this.grassColor);
		out.writeInt(this.h);
		out.writeInt(this.monsterDensity);
		out.writeInt(this.sandColor);
		out.writeObject(this.tiles);
		out.writeInt(this.w);
		
		// entities
		out.writeInt(this.entities.size());
		for (Entity e : this.entities) {
			out.writeObject(e);
		}
	}
}