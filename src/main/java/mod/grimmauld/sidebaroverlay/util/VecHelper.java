package mod.grimmauld.sidebaroverlay.util;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;

public class VecHelper {
	public static final Vec3 CENTER_OF_ORIGIN = new Vec3(0.5D, 0.5D, 0.5D);

	public static Vec3 rotate(Vec3 vec, double deg, Direction.Axis axis) {
		if (deg == 0.0D) {
			return vec;
		} else if (vec == Vec3.ZERO) {
			return vec;
		} else {
			float angle = (float) (deg / 180.0D * 3.141592653589793D);
			double sin = Mth.sin(angle);
			double cos = Mth.cos(angle);
			double x = vec.x;
			double y = vec.y;
			double z = vec.z;
			if (axis == Direction.Axis.X) {
				return new Vec3(x, y * cos - z * sin, z * cos + y * sin);
			} else if (axis == Direction.Axis.Y) {
				return new Vec3(x * cos + z * sin, y, z * cos - x * sin);
			} else {
				return axis == Direction.Axis.Z ? new Vec3(x * cos - y * sin, y * cos + x * sin, z) : vec;
			}
		}
	}

	public static Vec3 getCenterOf(Vec3i pos) {
		return pos.equals(Vec3i.ZERO) ? CENTER_OF_ORIGIN : Vec3.atLowerCornerOf(pos).add(0.5D, 0.5D, 0.5D);
	}

	public static Vec3 axisAlingedPlaneOf(Vec3 vec) {
		vec = vec.normalize();
		return (new Vec3(1.0D, 1.0D, 1.0D)).subtract(Math.abs(vec.x), Math.abs(vec.y), Math.abs(vec.z));
	}
}
