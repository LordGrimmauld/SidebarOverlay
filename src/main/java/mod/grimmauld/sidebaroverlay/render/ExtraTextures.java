package mod.grimmauld.sidebaroverlay.render;

import mod.grimmauld.sidebaroverlay.SidebarOverlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber({Dist.CLIENT})
public enum ExtraTextures {
	BLANK("blank.png"),
	GRAY("background.png"),
	CHECKERED("checkerboard.png"),
	HIGHLIGHT_CHECKERED("highlighted_checkerboard.png");

	public static final String ASSET_PATH = "textures/special/";
	private final ResourceLocation location;

	ExtraTextures(String filename) {
		this.location = new ResourceLocation(SidebarOverlay.MODID, ASSET_PATH + filename);
	}

	public ResourceLocation getLocation() {
		return this.location;
	}
}
