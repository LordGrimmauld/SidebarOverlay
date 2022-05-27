package mod.grimmauld.sidebaroverlay.util;

import net.minecraft.world.phys.Vec3;

public class ColorHelper {
	public static Vec3 getRGB(int color) {
		int r = color >> 16;
		int g = color >> 8 & 255;
		int b = color & 255;
		return (new Vec3(r, g, b)).scale(0.00390625D);
	}
}
