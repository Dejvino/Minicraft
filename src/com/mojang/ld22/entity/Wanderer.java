package com.mojang.ld22.entity;

import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.resource.Resource;

/**
 * Simple lonely NPC.
 * 
 * Does not do much, just wanders around, fights mobs, cuts down trees from time
 * to time, builds small shelters.
 * 
 * @author Dejvino
 */
public class Wanderer extends Npc
{
	private int xa, ya;
	private int randomWalkTime = 0;
	
	public Wanderer()
	{
	}
	
	public Wanderer(int lvl)
	{
		this.lvl = lvl;
		x = random.nextInt(64 * 16);
		y = random.nextInt(64 * 16);
		health = maxHealth = lvl * lvl * 10;
	}

	public void tick() {
		super.tick();

		int speed = tickTime & 1;
		if (!move(xa * speed, ya * speed) || random.nextInt(200) == 0) {
			randomWalkTime = 60;
			xa = (random.nextInt(3) - 1) * random.nextInt(2);
			ya = (random.nextInt(3) - 1) * random.nextInt(2);
		}
		if (randomWalkTime > 0) randomWalkTime--;
	}

	public void render(Screen screen) {
		int xt = 0;
		int yt = 14;

		int flip1 = (walkDist >> 3) & 1;
		int flip2 = (walkDist >> 3) & 1;

		if (dir == 1) {
			xt += 2;
		}
		if (dir > 1) {

			flip1 = 0;
			flip2 = ((walkDist >> 4) & 1);
			if (dir == 2) {
				flip1 = 1;
			}
			xt += 4 + ((walkDist >> 3) & 1) * 2;
		}

		int xo = x - 8;
		int yo = y - 11;

		int col = Color.get(-1, 000, 100, 532);
		/*if (lvl == 2) col = Color.get(-1, 100, 522, 050);
		if (lvl == 3) col = Color.get(-1, 111, 444, 050);
		if (lvl == 4) col = Color.get(-1, 000, 111, 020);*/
		if (hurtTime > 0) {
			col = Color.get(-1, 555, 555, 555);
		}

		screen.render(xo + 8 * flip1, yo + 0, xt + yt * 32, col, flip1);
		screen.render(xo + 8 - 8 * flip1, yo + 0, xt + 1 + yt * 32, col, flip1);
		screen.render(xo + 8 * flip2, yo + 8, xt + (yt + 1) * 32, col, flip2);
		screen.render(xo + 8 - 8 * flip2, yo + 8, xt + 1 + (yt + 1) * 32, col, flip2);
	}

	protected void touchedBy(Entity entity) {
		if (entity instanceof Mob) {
			Mob mob = (Mob)entity;
			this.hurt(mob, mob.lvl + 1, mob.dir);
		}
	}

	protected void die() {
		super.die();

		int count;
		
		// some clothes
		count = random.nextInt(2) + 1;
		for (int i = 0; i < count; i++) {
			level.add(new ItemEntity(new ResourceItem(Resource.cloth), x + random.nextInt(11) - 5, y + random.nextInt(11) - 5));
		}
		
		// maybe food
		count = random.nextInt(3);
		for (int i = 0; i < count; i++) {
			level.add(new ItemEntity(new ResourceItem(Resource.apple), x + random.nextInt(11) - 5, y + random.nextInt(11) - 5));
		}
		count = random.nextInt(2);
		for (int i = 0; i < count; i++) {
			level.add(new ItemEntity(new ResourceItem(Resource.bread), x + random.nextInt(11) - 5, y + random.nextInt(11) - 5));
		}

		if (level.player != null) {
			level.player.score += 100 * lvl;
		}

	}
}