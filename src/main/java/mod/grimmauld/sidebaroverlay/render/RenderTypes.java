package mod.grimmauld.sidebaroverlay.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import net.minecraft.client.renderer.RenderState.CullState;
import net.minecraft.client.renderer.RenderState.TextureState;

public class RenderTypes extends RenderState {
	protected static final CullState DISABLE_CULLING = new NoCullState();
	private static final RenderType OUTLINE_SOLID;

	static {
		OUTLINE_SOLID = RenderType.create("outline_solid", DefaultVertexFormats.NEW_ENTITY, 7, 256, true, false, RenderType.State.builder().setTextureState(new TextureState(ExtraTextures.BLANK.getLocation(), false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true));
	}

	public RenderTypes() {
		super("", () -> {
		}, () -> {
		});
	}

	public static RenderType getOutlineTranslucent(ResourceLocation texture, boolean cull) {
		RenderType.State rendertype$state = RenderType.State.builder().setTextureState(new TextureState(texture, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(cull ? CULL : DISABLE_CULLING).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
		return RenderType.create("outline_translucent" + (cull ? "_cull" : ""), DefaultVertexFormats.NEW_ENTITY, 7, 256, true, true, rendertype$state);
	}

	public static RenderType getOutlineSolid() {
		return OUTLINE_SOLID;
	}

	protected static class NoCullState extends CullState {
		public NoCullState() {
			super(false);
		}

		public void setupRenderState() {
			RenderSystem.disableCull();
		}
	}
}
