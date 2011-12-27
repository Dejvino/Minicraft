package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.entity.particle.SmashParticle;
import com.mojang.ld22.entity.particle.TextParticle;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.Level;

public class DoorTile extends Tile {
	
	private static final long serialVersionUID = 1558626257008145034L;

	private Tile onType;

	public static final int MAX_DAMAGE = 10;
	public static final int OPENED_FLAG = 1 << 4;
	public static final int LOCKED_FLAG = 1 << 5;
	public static final int HEALTH_MASK = OPENED_FLAG - 1;

	public DoorTile(int id) {
		this(id, Tile.dirt);
	}
	
	public DoorTile(int id, Tile onType) {
		super(id);
		this.onType = onType;
		connectsToSand = onType.connectsToSand;
		connectsToGrass = onType.connectsToGrass;
		connectsToWater = onType.connectsToWater;
		connectsToLava = onType.connectsToLava;
		connectsToPavement = onType.connectsToPavement;
	}

	public void setOnType(Tile onType)
	{
		this.onType = onType;
	}
	
	public Tile getOnType()
	{
		return this.onType;
	}
	
	public void render(Screen screen, Level level, int x, int y) {
		onType.render(screen, level, x, y);
		int color = Color.get(100, 421, 532, 553);
		int o = (level.getData(x, y) & OPENED_FLAG) > 0 ? 2 : 0;
		screen.render(x * 16 + 0, y * 16 + 0, 19 + (1+o) * 32, color, 0);
		screen.render(x * 16 + 8, y * 16 + 0, 20 + (1+o) * 32, color, 0);
		screen.render(x * 16 + 0, y * 16 + 8, 19 + (2+o) * 32, color, 0);
		screen.render(x * 16 + 8, y * 16 + 8, 20 + (2+o) * 32, color, 0);
	}

	public void tick(Level level, int x, int y) {
		
	}

	@Override
	public boolean interact(Level level, int xt, int yt, Player player,
			Item item, int attackDir)
	{
		// deconstruct with axe
		if (item instanceof ToolItem) {
			ToolItem tool = (ToolItem)item;
			if (tool.type.equals(ToolType.axe)) {
				level.setTile(xt, yt, this.onType, 0);
				level.add(new ItemEntity(new ResourceItem(Resource.door),
						(xt << 4) + random.nextInt(11) - 5, (yt << 4) + random.nextInt(11) - 5));
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean use(Level level, int xt, int yt, Player player, int attackDir)
	{
		// open / close
		level.setData(xt, yt, (level.getData(xt, yt) ^ OPENED_FLAG));
		return true;
	}
	
	public void hurt(Level level, int x, int y, Entity source, int dmg, int attackDir) {
		int damage = (level.getData(x, y) & HEALTH_MASK) + dmg;
		level.add(new SmashParticle(x * 16 + 8, y * 16 + 8));
		level.add(new TextParticle("" + dmg, x * 16 + 8, y * 16 + 8, Color.get(-1, 500, 500, 500)));
		if (damage >= MAX_DAMAGE) {
			int count = random.nextInt(2) + 1;
			for (int i = 0; i < count; i++) {
				level.add(new ItemEntity(new ResourceItem(Resource.wood), x * 16 + random.nextInt(10) + 3, y * 16 + random.nextInt(10) + 3));
			}
			level.setTile(x, y, Tile.dirt, 0);
		} else {
			level.setData(x, y, level.getData(x, y)^(level.getData(x,y) & HEALTH_MASK) + damage);
		}
	}
	
	@Override
	public boolean mayPass(Level level, int x, int y, Entity e)
	{
		return (level.getData(x, y) & OPENED_FLAG) > 0;
	}
	
	@Override
	public int getFireFuelAmount(Level level, int xt, int yt)
	{
		return MAX_DAMAGE - (level.getData(xt, yt) & HEALTH_MASK);
	}

	@Override
	public void burnFireFuel(Level level, int xt, int yt, int burnPower,
			Entity ent)
	{
		int damage = (level.getData(xt, yt) & HEALTH_MASK) + burnPower;
		if (damage >= MAX_DAMAGE) {
			level.setTile(xt, yt, Tile.dirt, 0);
		} else {
			level.setData(xt, yt, (level.getData(xt, yt)^(level.getData(xt,yt) & HEALTH_MASK)) + damage);
		}
	}
}