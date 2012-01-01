package com.mojang.ld22.level;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.mojang.ld22.Game;
import com.mojang.ld22.GameContainer;
import com.mojang.ld22.entity.AirWizard;
import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.LivingEntity;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.entity.Slime;
import com.mojang.ld22.entity.Wanderer;
import com.mojang.ld22.entity.Zombie;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.level.levelgen.LevelGen;
import com.mojang.ld22.level.tile.Tile;

public class Level implements Externalizable
{
	private static final long serialVersionUID = -7288529757348475493L;
	
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

	private int dayFog = 0;
	public static final int MAX_FOG = 40;
	
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
					if (lr > 0) {
						screen.renderLight(e.x - 1, e.y - 4, lr * 8);
					}
				}
				int lr = getTile(x, y).getLightRadius(this, x, y);
				if (lr > 0) screen.renderLight(x * 16 + 8, y * 16 + 8, lr * 8);
			}
		}
		screen.setOffset(0, 0);
	}
	
	public void renderFog(Screen screen, Screen light, int xScroll, int yScroll)
	{
		int visMax = 1000;
		
		// get sizes and positions (in tiles)
		int xo = xScroll >> 4;
		int yo = yScroll >> 4;
		int w = (screen.w >> 4) + 1;
		int h = (screen.h >> 4) + 1;
		
		// change to a rectangle to make better raytracing
		if (w > h) {
			yo -= (w-h) / 2;
			h = w;
		} else {
			xo -= (h-w) / 2;
			w = h;
		}
		
		int pX = ((xScroll) >> 4) + w / 2;//this.player.x >> 4;
		int pY = yo + h / 2;//this.player.y >> 4;
		
		// resolution of raytracing
		float res = 10;
		
		// prepare visibility grid
		int[][] visibility = new int[h+1][w+1];
		
		// for every point on the edge of the screen
		int edgeMax = (int)((w+h)*2*res);
		for (int edge = 0; edge < edgeMax; edge++) {
			// determine the destination point (we do a loop)
			double dstX;
			double dstY;
			if (edge < w*res) {
				dstX = edge;
				dstY = 0;
			} else if (edge < (w+h)*res) {
				dstX = (w*res);
				dstY = ((edge-(w*res)));
			} else if (edge < (w+w+h)*res) {
				dstX = (edge - (w+h)*res);
				dstY = (h*res);
			} else if (edge < (w+w+h+h)*res) {
				dstX = 0;
				dstY = (edgeMax-edge);
			} else {
				throw new RuntimeException("raytracing edge value");
			}
			
			// determine the ray properties
			double dist = Math.sqrt(
								Math.pow(xo + dstX/res - pX, 2)
									+
								Math.pow(yo + dstY/res - pY, 2));
			double maxStepModif = 11;
			int maxStep = (int)(dist*maxStepModif);
			double rayPower = (visMax);
			double rayFall = (rayPower / (dist * maxStepModif * 0.1));
			
			
			// perform step-calculations on the line |Player --> destination|
			for (int step = 0; step <= maxStep; step++) {
				double progress = step / (float)maxStep;
				int curX = pX + (int)(((xo + dstX/res) - pX) * progress);
				int curY = pY + (int)(((yo + dstY/res) - pY) * progress);
			
				if (curY - yo < 0 || curX - xo < 0) {
					continue;
				}
				
				// compute new visibility
				int curVis = visibility[curY - yo][curX - xo];
				double lightLevel = 0.5 * light.getPixel(((curX) << 4) - xScroll, ((curY) << 4) - yScroll);
				int newVis = (int)(curVis + rayPower);
				if (curVis < newVis) {
					visibility[curY - yo][curX - xo] = newVis;
				}
				
				// lower the strength of the ray if this tile is blocking the view
				int visBlock = this.getTile(curX, curY).getVisibilityBlocking(this, curX, curY, player);
				double visBlockCoef = (visBlock / 100.0);
				double tileRayFall = (rayFall * visBlockCoef);
				double lightBlock = (dayFog/maxStepModif*res) - (lightLevel);
				if (lightBlock < 0) {
					lightBlock = 0;
				}
				rayPower -= tileRayFall + lightBlock;
				if (rayPower <= 0) {
					// this ray is dead, but we must keep it because of light
					//break;
				}
			}
		}
		
		// normalize visibility
		for (int y = 0; y <= h; y++) {
			for (int x = 0; x <= w; x++) {
				int vis = visibility[y][x];
				if (vis > visMax) {
					vis = visMax;
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
		
		// render blocks of fog (8x8)
		for (int y = 0; y <= h; y++) {
			for (int x = 0; x <= w; x++) {
				int vis = visibility[y][x];
				int xr = (xo+x)*16;
				int yr = (yo+y)*16;
				for (int z = 0; z < 4; z++) {
					int zx = (z % 2);
					int zy = (z / 2);
					int visBlend = vis;
					// blend levels
					for (int s = 0; s < 4; s++) {
						int sx = (s % 2)-1;
						int sy = (s / 2)-1;
						if (y+sy+zy < 0 || y+sy+zy >= h || x+sx+zx < 0 || x+sx+zx >= w) {
							visBlend += vis;
						} else {
							visBlend += visibility[y+sy+zy][x+sx+zx];
						}
					}
					visBlend /= 5;
					// create normal color for overlay
					int color = (int)visBlend / 6;			
					// render one 8x8 patch 
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
			int type = random.nextInt(5);
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
	
	public void tryDespawn(int count) {
		for (int i = 0; i < count; i++) {
			int x = random.nextInt(this.w);
			int y = random.nextInt(this.h);
			int xx = x * 16 + 8;
			int yy = y * 16 + 8;
		
			// check distance from player
			if (this.player != null) {
				int xd = this.player.x - xx;
				int yd = this.player.y - yy;
				if (xd * xd + yd * yd < 80 * 80) {
					continue;
				}
			}
		
			// kill redundant monsters
			int r = this.monsterDensity * 16;
			int remaining = 1;
			List<Entity> entities = getEntities(xx - r, yy - r, xx + r, yy + r);
			for (Entity ent : entities) {
				if (ent instanceof LivingEntity) {
					LivingEntity entity = (LivingEntity)ent;
					if (entity.isEvil()) {
						if (remaining <= 0) {
							entity.remove();
						} else {
							remaining--;
						}
					}
				}
			}
		}
	}

	public void tick() {
		trySpawn(5);
		tryDespawn(5);

		// update fog value
		if (depth >= 0) {
			// above ground, day and night cycles
			Game game = GameContainer.getInstance().getGame();
			dayFog = (int)((Math.cos(game.getDayCycle() * 2*Math.PI) * MAX_FOG) + MAX_FOG) / 2;
			if (dayFog > 15) {
				grassColor = 121;
				dirtColor = 211;
				sandColor = 330;
			} else if (dayFog > 8) {
				grassColor = 131;
				dirtColor = 322;
				sandColor = 440;
			} else {
				grassColor = 141;
				dirtColor = 322;
				sandColor = 550;
			}
		} else {
			// underground, full darkness
			dayFog = (int)(MAX_FOG * 3.5);
		}
		// update monsters density based on darkness
		monsterDensity = 6 * MAX_FOG / (dayFog+1);
		
		// tick all tiles
		for (int i = 0; i < w * h / 50; i++) {
			int xt = random.nextInt(w);
			int yt = random.nextInt(w);
			getTile(xt, yt).tick(this, xt, yt);
		}
		
		// tick all entities
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