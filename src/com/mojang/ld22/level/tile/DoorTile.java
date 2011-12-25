package com.mojang.ld22.level.tile;

import com.mojang.ld22.entity.Entity;
import com.mojang.ld22.entity.ItemEntity;
import com.mojang.ld22.entity.Player;
import com.mojang.ld22.gfx.Color;
import com.mojang.ld22.gfx.Screen;
import com.mojang.ld22.item.Item;
import com.mojang.ld22.item.ResourceItem;
import com.mojang.ld22.item.ToolItem;
import com.mojang.ld22.item.ToolType;
import com.mojang.ld22.item.resource.Resource;
import com.mojang.ld22.level.Level;

public class DoorTile extends Tile {
	private Tile onType;
	private boolean opened = false;
	private boolean locked = false;

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
		int o = opened ? 2 : 0;
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
		// open with hands
		if (item == null) {
			this.opened = !this.opened;
			return true;
		}
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
	
	public void hurt(Level level, int x, int y, Entity source, int dmg, int attackDir) {
		System.out.println("Hurting with dmg " + dmg);
		level.setTile(x, y, onType, 0);
	}
	
	@Override
	public boolean mayPass(Level level, int x, int y, Entity e)
	{
		return opened;
	}
}