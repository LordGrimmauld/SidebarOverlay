package mod.grimmauld.sidebaroverlay.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.client.renderer.RenderStateShard.CullStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;

public class RenderTypes extends RenderStateShard {
	protected static final CullStateShard DISABLE_CULLING = new NoCullState();
	private static final RenderType OUTLINE_SOLID;

	static {
		OUTLINE_SOLID = RenderType.create("outline_solid", DefaultVertexFormat.NEW_ENTITY, 7, 256, true, false, RenderType.CompositeState.builder().setTextureState(new TextureStateShard(ExtraTextures.BLANK.getLocation(), false, false)).setTransparencyState(NO_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true));
	}

	public RenderTypes() {
		super("", () -> {
		}, () -> {
		});
	}

	public static RenderType getOutlineTranslucent(ResourceLocation texture, boolean cull) {
		RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder().setTextureState(new TextureStateShard(texture, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setDiffuseLightingState(DIFFUSE_LIGHTING).setAlphaState(DEFAULT_ALPHA).setCullState(cull ? CULL : DISABLE_CULLING).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true);
		return RenderType.create("outline_translucent" + (cull ? "_cull" : ""), DefaultVertexFormat.NEW_ENTITY, 7, 256, true, true, rendertype$state);
	}

	public static RenderType getOutlineSolid() {
		return OUTLINE_SOLID;
	}

	protected static class NoCullState extends CullStateShard {
		public NoCullState() {
			super(false);
		}

		public void setupRenderState() {
			RenderSystem.disableCull();
		}
	}
}
