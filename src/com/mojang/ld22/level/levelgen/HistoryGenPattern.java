package com.mojang.ld22.level.levelgen;

import com.mojang.ld22.level.tile.Tile;

public class HistoryGenPattern
{
	private static final byte O = -1; // zerO (transparent)
	private static final byte G = Tile.grass.id; // Grass
	private static final byte D = Tile.dirt.id; // Dirt
	private static final byte W = Tile.woodenWall.id; // Wooden wall
	private static final byte F = Tile.fence.id; // Fence
	private static final byte A = Tile.farmland.id; // fArmland
	private static final byte S = Tile.rockWall.id; // Stone
	private static final byte P = Tile.rockFloor.id; // Paved Path
	private static final byte R = Tile.door.id; // dooR
	private static final byte I = Tile.window.id; // wIndow
	
	//=========================================================================
	//		BUILDINGS
	//=========================================================================
	
	public static byte[][] hut1 = new byte[][] {
		{ W, W, W },
		{ W, D, W }
	};
	public static byte[][] hut2 = new byte[][] {
		{ W, W, W },
		{ W, D, W },
		{ W, R, W }
	};

	public static byte[][] cottage1 = new byte[][] {
		{ W, W, W, W },
		{ W, D, D, W },
		{ W, R, I, W }
	};
	public static byte[][] cottage2 = new byte[][] {
		{ W, W, I, W },
		{ R, D, D, I },
		{ W, W, I, W }
	};
	public static byte[][] cottage3 = new byte[][] {
		{ W, I, W, I, W },
		{ R, D, D, D, I },
		{ W, I, W, I, W }
	};
	public static byte[][] cottage4 = new byte[][] {
		{ W, W, I, W, W },
		{ I, D, D, D, I },
		{ W, I, R, I, W }
	};
	public static byte[][] cottage5 = new byte[][] {
		{ W, I, W, I, W },
		{ W, D, D, D, W },
		{ I, D, D, D, I },
		{ W, W, R, W, W }
	};

	public static byte[][] stoneHouse = new byte[][] {
		{ S, I, S, I, S },
		{ I, P, P, P, I },
		{ S, P, P, P, S },
		{ I, P, P, P, I },
		{ S, S, P, S, S }
	};
	
	public static byte[][] woodenWall1 = new byte[][] {
		{ W, W, W, W, W, W, W },
	};
	public static byte[][] woodenWall2 = new byte[][] {
		{ W, W, W, W, W, W, W },
		{ W, W, W, W, W, W, O },
	};
	public static byte[][] woodenWall3 = new byte[][] {
		{ W, W, W, W, W, W, W, W, W, W, W, W, W },
		{ W, W, W, W, W, W, W, W, W, W, W, W, W },
	};
	
	public static byte[][] woodenWallCorner1 = new byte[][] {
		{ W, W, W, W, W,},
		{ O, O, O, O, W,},
		{ O, O, O, O, W,},
		{ O, O, O, O, W,},
		{ O, O, O, O, W,},
	};
	public static byte[][] woodenWallCorner2 = new byte[][] {
		{ W, W, W, W, W, W, W, W,},
		{ O, W, W, W, W, W, W, W,},
		{ O, O, O, O, O, O, W, W,},
		{ O, O, O, O, O, O, W, W,},
		{ O, O, O, O, O, O, W, W,},
		{ O, O, O, O, O, O, W, W,},
		{ O, O, O, O, O, O, W, W,},
		{ O, O, O, O, O, O, W, W,}
	};
	
	public static byte[][] stoneWall1 = new byte[][] {
		{ S, S, S, S, S, S, S },
	};
	public static byte[][] stoneWall2 = new byte[][] {
		{ S, S, S, S, S, S, S },
		{ S, S, S, S, S, S, O },
	};
	public static byte[][] stoneWall3 = new byte[][] {
		{ S, S, S, S, S, S, S, S, S, S, S, S, S },
		{ S, S, S, S, S, S, S, S, S, S, S, S, S },
	};
	
	public static byte[][] stoneWallGate1 = new byte[][] {
		{ O, O, O, O, O, P, P, O, O, O, O, O },
		{ S, S, S, S, S, P, P, S, S, S, S, S },
		{ S, S, S, S, S, P, P, S, S, S, S, S },
		{ O, O, O, O, O, P, P, O, O, O, O, O },
	};
	public static byte[][] stoneWallGate2 = new byte[][] {
		{ O, O, O, O, S, S, S, P, P, S, S, S, O, O, O, O },
		{ S, S, S, S, S, P, I, P, P, R, P, S, S, S, S, S },
		{ S, S, S, S, S, P, R, P, P, I, P, S, S, S, S, S },
		{ O, O, O, O, S, S, S, P, P, S, S, S, O, O, O, O },
	};
	
	public static byte[][] stoneWallCorner1 = new byte[][] {
		{ S, S, S, S, S,},
		{ O, O, O, O, S,},
		{ O, O, O, O, S,},
		{ O, O, O, O, S,},
		{ O, O, O, O, S,},
	};
	public static byte[][] stoneWallCorner2 = new byte[][] {
		{ S, S, S, S, S, S, S, S,},
		{ O, S, S, S, S, S, S, S,},
		{ O, O, O, O, O, O, S, S,},
		{ O, O, O, O, O, O, S, S,},
		{ O, O, O, O, O, O, S, S,},
		{ O, O, O, O, O, O, S, S,},
		{ O, O, O, O, O, O, S, S,},
		{ O, O, O, O, O, O, S, S,}
	};
	
	public static byte[][] ruin1 = new byte[][] {
		{ W, W, W, D, O },
		{ W, D, D, D, W },
		{ W, G, D, D, W },
		{ O, G, D, W, W }
	};
	
	public static byte[][] ruin2 = new byte[][] {
		{ O, W, W, D, O },
		{ W, D, D, D, W },
		{ W, G, D, D, O },
		{ W, D, D, D, W },
		{ O, G, D, W, W }
	};
	
	public static byte[][] ruin3 = new byte[][] {
		{ O, S, S, D, O },
		{ S, D, D, D, S },
		{ S, G, S, D, O },
		{ S, D, D, D, W },
		{ O, G, D, W, W }
	};
	
	public static byte[][] farm1 = new byte[][] {
		{ F, F, F, F, F },
		{ F, A, D, A, F },
		{ F, A, D, A, F },
		{ F, A, D, A, F },
		{ F, F, D, F, F }
	};
	
	public static byte[][] farm2 = new byte[][] {
		{ F, F, F, F, F },
		{ F, A, A, A, F },
		{ F, A, A, A, F },
		{ F, A, A, A, F },
		{ F, F, F, F, F }
	};
	
	public static byte[][] farm3 = new byte[][] {
		{ A, A, A, A, A },
		{ A, A, A, A, A },
		{ D, D, D, D, D },
		{ A, A, A, A, A },
		{ A, A, A, A, A }
	};
	
	public static byte[][] farm4 = new byte[][] {
		{ A, A, A, A, A },
		{ A, A, A, A, A },
		{ A, A, A, A, A },
	};
	
	//=========================================================================
	//		LISTS
	//=========================================================================
		
	public static byte[][][] buildings = new byte[][][] {
		hut1, hut2, cottage1, cottage2, cottage3, cottage4, cottage5,
		stoneHouse,
		woodenWall1, woodenWall2, woodenWall3, woodenWallCorner1, woodenWallCorner2,
		stoneWall1, stoneWall2, stoneWall3, stoneWallCorner1, stoneWallCorner2,
		stoneWallGate1, stoneWallGate2,
		ruin1, ruin2, ruin3,
		farm1, farm2, farm3, farm4
	};
	
	public static byte[][][] sceneryForest = new byte[][][] {
		hut1, hut2, cottage1, cottage2, cottage3, cottage4, cottage5,
		stoneHouse,
		woodenWall1, woodenWall2, woodenWall3, woodenWallCorner1, woodenWallCorner2,
		ruin1, ruin2, ruin3,
		farm4
	};
	
	/**
	 * Transposes a pattern.
	 * 
	 * @param x
	 * @return
	 */
	public static byte[][] transpose(byte[][] x)
	{
		byte[][] r = new byte[x[0].length][x.length];
		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < x[i].length; j++)
				r[j][i] = x[i][j];
		return r;
	}
}
