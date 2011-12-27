package com.mojang.ld22.entity;

import java.util.List;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.level.tile.Tile;

/**
 * 		Fire!
 * 
 * This entity represents a freely burning flame. The default version (without
 * changing the burn parameters) is capable of burning down a wooden house
 * or a part of a forest. It should NOT be powerful enough to burn the whole
 * map to the ground! On the other hand it shouldn't die after half a second.
 * 
 * Burn parameters explained:
 * Power - the number of hit-points / damage it does every time it consumes fuel.
 * 			It also has a direct impact on the light radius.
 * Cycle - how often the fuel is consumed.
 * Both Power and Cycle determine the likelihood of spreading. Brighter and
 * stronger flames (large Power) spread faster, slowly burning flames (large
 * Cycle) are less likely to spread.
 * To make a torch: Power = 1, Cycle = 1000.
 * To make napalm: Power = 100, Cycle = 1 (it could also burn the CPU). 
 * 
 * @author Dejvino
 *
 */
public class Fire extends Entity {
	private int time;
	protected LivingEntity owner;
	protected int burnPower = 1;
	protected int burnCycle = 100;
	
	protected int renderFlip;
	protected int renderImg;
	
	public Fire() {
		
	}
	
	public Fire(LivingEntity owner, int x, int y) {
		this.x = x;
		this.y = y;
		this.owner = owner;
	}
	
	public Fire(LivingEntity owner, int x, int y, int burnPower, int burnCycle) {
		this.x = x;
		this.y = y;
		this.owner = owner;
		this.burnPower = burnPower;
		this.burnCycle = burnCycle;
	}

	public void tick() {
		time++;

		// eat some fuel
		if (time % burnCycle == 0) {
			this.burnFuel();
		}
		
		// spread all around
		this.trySpreading();
		
		// change the graphics
		if (time % 5 == 0) {
			this.renderFlip = Screen.BIT_MIRROR_X * random.nextInt(2);
			this.renderImg = random.nextInt(3);
		}
		
		// harm entities all around
		this.harmNearbyEntities();
	}

	public boolean isBlockableBy(Mob mob) {
		return false;
	}

	public void render(Screen screen) {
		int xt = 30;
		int yt = renderImg*2;
		int flip = renderFlip;
		int fx = flip == 0 ? 1 : -1;
		int col = Color.get(-1, 530, 541, 553);
		screen.render(x-4*fx, y-8, xt + yt * 32, col, flip);
		screen.render(x+4*fx, y-8, xt+1 + yt * 32, col, flip);
		screen.render(x-4*fx, y, xt + (yt+1) * 32, col, flip);
		screen.render(x+4*fx, y, xt+1 + (yt+1) * 32, col, flip);
	}
	
	@Override
	public int getLightRadius()
	{
		return burnPower * 5;
	}
	
	/**
	 * Burns fuel. The default implementation burns fuel from the tile on which
	 * this entity is located.
	 * 
	 * Ran in the {@see tick()} method.
	 * The purpose of this method is to allow fine-grained functionality change
	 * by class extension.
	 */
	protected void burnFuel()
	{
		int xt = x >> 4;
		int yt = y >> 4;
		Tile onTile = level.getTile(xt, yt);
		if (onTile.isFlammable(level, xt, yt)) {
			// burn!
			onTile.burnFireFuel(level, xt, yt, burnPower, this);
		} else {
			// out of fuel :(
			this.remove();
		}
	}
	
	/**
	 * Randomly tries to spread fire.
	 * 
	 * Ran in the {@see tick()} method.
	 * The purpose of this method is to allow fine-grained functionality change
	 * by class extension.
	 */
	protected void trySpreading()
	{
		if (random.nextInt(burnCycle * 2 / burnPower) == 0) {
			int fx = this.x + ((random.nextInt(8)+8) * (random.nextInt(2)*2-1));
			int fy = this.y + ((random.nextInt(8)+8) * (random.nextInt(2)*2-1));
			if (level.getTile(fx >> 4, fy >> 4).isFlammable(level, fx >> 4, fy >> 4)) {
				level.add(new Fire(this.owner, fx, fy));
			}
		}
	}
	
	/**
	 * Harms nearby entities.
	 * 
	 * Ran in the {@see tick()} method.
	 * The purpose of this method is to allow fine-grained functionality change
	 * by class extension.
	 */
	protected void harmNearbyEntities()
	{
		List<Entity> toHit = level.getEntities(x-1, y-1, x+1, y+1);
		for (int i = 0; i < toHit.size(); i++) {
			Entity e = toHit.get(i);
			if (e instanceof LivingEntity) {
				e.hurt(owner, this.burnPower, ((LivingEntity) e).dir ^ 1);
			}
		}
	}
}
