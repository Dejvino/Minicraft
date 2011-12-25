package com.mojang.ld22.entity;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.mojang.ld22.entity.particle.TextParticle;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.level.Level;
import com.mojang.ld22.level.tile.Tile;
import com.mojang.ld22.sound.Sound;

public class LivingEntity extends Entity
{
	private static final long serialVersionUID = 923792273628L;

	protected static final int HEARING_DISTANCE = 2 * 8 * 16;
	
	protected int walkDist = 0;
	protected int dir = 0;
	public int hurtTime = 0;
	protected int xKnockback;
	protected int yKnockback;
	public int maxHealth = 10;
	public int health = maxHealth;
	public int swimTimer = 0;
	public int tickTime = 0;
	protected int lvl;
	public int karma = 0;

	public LivingEntity()
	{
		super();
	}

	public void tick()
	{
		tickTime++;
		if (level.getTile(x >> 4, y >> 4) == Tile.lava) {
			hurt(this, 4, dir ^ 1);
		}
	
		if (health <= 0) {
			die();
		}
		if (hurtTime > 0) hurtTime--;
	}

	protected void die()
	{
		remove();
	}

	public boolean move(int xa, int ya) {
		if (isSwimming()) {
			if (swimTimer++ % 2 == 0) return true;
		}
		if (xKnockback < 0) {
			move2(-1, 0);
			xKnockback++;
		}
		if (xKnockback > 0) {
			move2(1, 0);
			xKnockback--;
		}
		if (yKnockback < 0) {
			move2(0, -1);
			yKnockback++;
		}
		if (yKnockback > 0) {
			move2(0, 1);
			yKnockback--;
		}
		if (hurtTime > 0) return true;
		if (xa != 0 || ya != 0) {
			walkDist++;
			if (xa < 0) dir = 2;
			if (xa > 0) dir = 3;
			if (ya < 0) dir = 1;
			if (ya > 0) dir = 0;
		}
		return super.move(xa, ya);
	}

	protected boolean isSwimming()
	{
		Tile tile = level.getTile(x >> 4, y >> 4);
		return Tile.water.equals(tile) || Tile.lava.equals(tile);
	}

	public void hurt(Tile tile, int x, int y, int damage)
	{
		int attackDir = dir ^ 1;
		doHurt(damage, attackDir);
	}

	public void hurt(Entity ent, int damage, int attackDir) {
		doHurt(damage, attackDir);
		// change attackers karma
		if (ent instanceof LivingEntity) {
			LivingEntity livingEnt = (LivingEntity)ent;
			int minKarma = this.karma > 0 ? 1 : (this.karma < 0 ? -1 : 0);
			livingEnt.karma -= (this.karma * damage / 1000 ) + minKarma;
		}
	}
	
	public void heal(int heal)
	{
		if (hurtTime > 0) return;
	
		level.add(new TextParticle("" + heal, x, y, Color.get(-1, 50, 50, 50)));
		health += heal;
		if (health > maxHealth) health = maxHealth;
	}

	protected void doHurt(int damage, int attackDir)
	{
		if (hurtTime > 0) return;
	
		if (level.player != null) {
			int xd = level.player.x - x;
			int yd = level.player.y - y;
			if (xd * xd + yd * yd < 80 * 80) {
				Sound.monsterHurt.play();
			}
		}
		level.add(new TextParticle("" + damage, x, y, Color.get(-1, 500, 500, 500)));
		health -= damage;
		if (attackDir == 0) yKnockback = +6;
		if (attackDir == 1) yKnockback = -6;
		if (attackDir == 2) xKnockback = -6;
		if (attackDir == 3) xKnockback = +6;
		hurtTime = 10;
	}

	public boolean findStartPos(Level level)
	{
		int x = random.nextInt(level.w);
		int y = random.nextInt(level.h);
		int xx = x * 16 + 8;
		int yy = y * 16 + 8;
	
		if (level.player != null) {
			int xd = level.player.x - xx;
			int yd = level.player.y - yy;
			if (xd * xd + yd * yd < 80 * 80) return false;
		}
	
		int r = level.monsterDensity * 16;
		if (level.getEntities(xx - r, yy - r, xx + r, yy + r).size() > 0) return false;
	
		if (level.getTile(x, y).mayPass(level, x, y, this)) {
			this.x = xx;
			this.y = yy;
			return true;
		}
	
		return false;
	}
	
	/**
	 * Returns the X position of the tile this entity is facing.
	 * 
	 * @return Tile pos X
	 */
	public int getFacingTileX()
	{
		int xa = 0;
		if (dir == 2) {
			xa = -1;
		}
		if (dir == 3) {
			xa = 1;
		}
		return (x >> 4) + xa;
	}
	
	/**
	 * Returns the Y position of the tile this entity is facing.
	 * 
	 * @return Tile pos Y
	 */
	public int getFacingTileY()
	{
		int ya = 0;
		if (dir == 1) {
			ya = -1;
		}
		if (dir == 0) {
			ya = 1;
		}
		return (y >> 4) + ya;
	}
	
	/**
	 * Returns the tile that this entity is facing.
	 * This is based on direction and current position.
	 * 
	 * @return Tile
	 */
	public Tile getFacingTile()
	{
		int xt = this.getFacingTileX();
		int yt = this.getFacingTileY();
		return level.getTile(xt, yt);
	}
	
	/**
	 * Returns entity's karma:
	 *  > 0 is good
	 *  0 is neutral
	 *  < 0 evil
	 *  
	 * @return
	 */
	public int getKarma()
	{
		return this.karma;
	}
	
	public boolean isGood()
	{
		return this.karma > 100;
	}
	
	public boolean isEvil()
	{
		return this.karma < -100;
	}
	
	public boolean isNeutral()
	{
		return !isGood() && !isEvil();
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException
	{
		super.readExternal(in);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		super.writeExternal(out);
		// TODO
	}

}