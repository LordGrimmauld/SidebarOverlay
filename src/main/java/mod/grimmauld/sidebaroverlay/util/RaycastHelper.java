package mod.grimmauld.sidebaroverlay.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.util.math.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class RaycastHelper {
	private static final Minecraft MC = Minecraft.getInstance();

	public static BlockHitResult rayTraceRange(Level worldIn, Player playerIn, double range) {
		Vec3 origin = getTraceOrigin(playerIn);
		Vec3 target = getTraceTarget(playerIn, range, origin);
		ClipContext context = new ClipContext(origin, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, playerIn);
		return worldIn.clip(context);
	}

	public static Vec3 getTraceTarget(Player playerIn, double range, Vec3 origin) {
		float f = playerIn.xRot;
		float f1 = playerIn.yRot;
		float f2 = Mth.cos(-f1 * 0.017453292F - 3.1415927F);
		float f3 = Mth.sin(-f1 * 0.017453292F - 3.1415927F);
		float f4 = -Mth.cos(-f * 0.017453292F);
		float f5 = Mth.sin(-f * 0.017453292F);
		float f6 = f3 * f4;
		float f7 = f2 * f4;
		return origin.add((double) f6 * range, (double) f5 * range, (double) f7 * range);
	}

	public static Vec3 getTraceOrigin(Player playerIn) {
		double d0 = playerIn.getX();
		double d1 = playerIn.getY() + (double) playerIn.getEyeHeight();
		double d2 = playerIn.getZ();
		return new Vec3(d0, d1, d2);
	}

	public static PredicateTraceResult rayTraceUntil(Vec3 start, Vec3 end, Predicate<BlockPos> predicate) {
		if (!Double.isNaN(start.x) && !Double.isNaN(start.y) && !Double.isNaN(start.z)) {
			if (!Double.isNaN(end.x) && !Double.isNaN(end.y) && !Double.isNaN(end.z)) {
				int dx = Mth.floor(end.x);
				int dy = Mth.floor(end.y);
				int dz = Mth.floor(end.z);
				int x = Mth.floor(start.x);
				int y = Mth.floor(start.y);
				int z = Mth.floor(start.z);
				BlockPos currentPos = new BlockPos(x, y, z);
				if (predicate.test(currentPos)) {
					return new PredicateTraceResult(currentPos, Direction.getNearest((float) (dx - x), (float) (dy - y), (float) (dz - z)));
				} else {
					int var10 = 200;

					Direction enumfacing;
					do {
						if (var10-- < 0) {
							return new PredicateTraceResult();
						}

						if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z)) {
							return null;
						}

						if (x == dx && y == dy && z == dz) {
							return new PredicateTraceResult();
						}

						boolean flag2 = true;
						boolean flag = true;
						boolean flag1 = true;
						double d0 = 999.0D;
						double d1 = 999.0D;
						double d2 = 999.0D;
						if (dx > x) {
							d0 = (double) x + 1.0D;
						} else if (dx < x) {
							d0 = (double) x + 0.0D;
						} else {
							flag2 = false;
						}

						if (dy > y) {
							d1 = (double) y + 1.0D;
						} else if (dy < y) {
							d1 = (double) y + 0.0D;
						} else {
							flag = false;
						}

						if (dz > z) {
							d2 = (double) z + 1.0D;
						} else if (dz < z) {
							d2 = (double) z + 0.0D;
						} else {
							flag1 = false;
						}

						double d3 = 999.0D;
						double d4 = 999.0D;
						double d5 = 999.0D;
						double d6 = end.x - start.x;
						double d7 = end.y - start.y;
						double d8 = end.z - start.z;
						if (flag2) {
							d3 = (d0 - start.x) / d6;
						}

						if (flag) {
							d4 = (d1 - start.y) / d7;
						}

						if (flag1) {
							d5 = (d2 - start.z) / d8;
						}

						if (d3 == -0.0D) {
							d3 = -1.0E-4D;
						}

						if (d4 == -0.0D) {
							d4 = -1.0E-4D;
						}

						if (d5 == -0.0D) {
							d5 = -1.0E-4D;
						}

						if (d3 < d4 && d3 < d5) {
							enumfacing = dx > x ? Direction.WEST : Direction.EAST;
							start = new Vec3(d0, start.y + d7 * d3, start.z + d8 * d3);
						} else if (d4 < d5) {
							enumfacing = dy > y ? Direction.DOWN : Direction.UP;
							start = new Vec3(start.x + d6 * d4, d1, start.z + d8 * d4);
						} else {
							enumfacing = dz > z ? Direction.NORTH : Direction.SOUTH;
							start = new Vec3(start.x + d6 * d5, start.y + d7 * d5, d2);
						}

						x = Mth.floor(start.x) - (enumfacing == Direction.EAST ? 1 : 0);
						y = Mth.floor(start.y) - (enumfacing == Direction.UP ? 1 : 0);
						z = Mth.floor(start.z) - (enumfacing == Direction.SOUTH ? 1 : 0);
						currentPos = new BlockPos(x, y, z);
					} while (!predicate.test(currentPos));

					return new PredicateTraceResult(currentPos, enumfacing);
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Nullable
	public static BlockPos getFocusedPosition() {
		if (MC.player == null || MC.level == null)
			return null;

		BlockHitResult trace = RaycastHelper.rayTraceRange(MC.level, MC.player, 75);
		if (trace.getType() != HitResult.Type.BLOCK)
			return null;

		BlockPos hit = new BlockPos(trace.getLocation());
		if (MC.level.getBlockState(hit).getMaterial().isReplaceable())
			hit = hit.relative(trace.getDirection().getOpposite());
		return hit;
	}

	public static class PredicateTraceResult {
		private BlockPos pos;
		private Direction facing;

		public PredicateTraceResult(BlockPos pos, Direction facing) {
			this.pos = pos;
			this.facing = facing;
		}

		public PredicateTraceResult() {
		}

		public Direction getFacing() {
			return this.facing;
		}

		public boolean missed() {
			return this.pos == null;
		}
	}
}
