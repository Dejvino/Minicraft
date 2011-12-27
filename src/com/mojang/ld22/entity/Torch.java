package com.mojang.ld22.entity;

import java.util.List;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.tile.Tile;

/**
 * 		Torch
 * 
 * Slowly burning flame eating its own fuel.
 * There is a veeeery low probability that the surroundings will catch fire.
 * 
 * @author Dejvino
 *
 */
public class Torch extends Fire {
	private int burnCapacity = 10;
	
	public Torch() {
		
	}
	
	public Torch(LivingEntity owner, int x, int y) {
		this(owner, x, y, 10);
	}
	
	public Torch(LivingEntity owner, int x, int y, int burnCapacity) {
		this(owner, x, y, burnCapacity, 1, 1000);
	}
	
	public Torch(LivingEntity owner, int x, int y, int burnCapacity, int burnPower, int burnCycle) {
		super(owner, x, y, burnPower, burnCycle);
		this.burnPower = burnPower;
		this.burnCycle = burnCycle;
		this.burnCapacity = burnCapacity;
	}

	public void render(Screen screen) {
		int xt = 12;
		int yt = renderImg*2+8;
		int col = Color.get(-1, 410, 540, 553);
		screen.render(x-8, y-10, xt + yt * 32, col, 0);
		screen.render(x, y-10, xt+1 + yt * 32, col, 0);
		screen.render(x-8, y-2, xt + (yt+1) * 32, col, 0);
		screen.render(x, y-2, xt+1 + (yt+1) * 32, col, 0);
	}
	
	@Override
	public void hurt(Entity entity, int attackDamage, int attackDir)
	{
		this.remove();
	}
	
	@Override
	public boolean use(Player player, int attackDir)
	{
		this.remove();
		player.inventory.add(new ResourceItem(Resource.torch));
		return true;
	}
	
	@Override
	public int getLightRadius()
	{
		return burnPower * 8;
	}
	
	@Override
	protected void trySpreading()
	{
		// torches are less likely to spread fire
		if (random.nextInt(10) == 0) {
			super.trySpreading();
		}
	}
	
	@Override
	protected void harmNearbyEntities()
	{
		// torch is not that dangerous, but it should burn sometimes
		if (random.nextInt(10) == 0) {
			List<Entity> toHit = level.getEntities(x, y, x, y);
			for (int i = 0; i < toHit.size(); i++) {
				Entity e = toHit.get(i);
				if (e instanceof LivingEntity && (x >> 4 == e.x >> 4) && (y >> 4 == e.y >> 4)) {
					e.hurt(owner, this.burnPower, ((LivingEntity) e).dir ^ 1);
				}
			}
		}
	}
	
	@Override
	protected void burnFuel()
	{
		// torch has its own fuel supply and doesn't burn the tile underneath
		this.burnCapacity -= this.burnPower;
		if (this.burnCapacity <= 0) {
			// torch went out
			this.remove();
		}
	}
}
