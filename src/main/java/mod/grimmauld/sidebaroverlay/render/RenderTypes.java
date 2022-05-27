package mod.grimmauld.sidebaroverlay.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import mod.grimmauld.sidebaroverlay.SidebarOverlay;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.client.renderer.RenderStateShard.CullStateShard;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;

import static mod.grimmauld.sidebaroverlay.render.ExtraTextures.BLANK;

public class RenderTypes extends RenderStateShard {
	protected static final CullStateShard DISABLE_CULLING = new NoCullState();

	private static final RenderType DEFAULT_OUTLINE_SOLID =
			getOutlineSolid(BLANK.getLocation());

	public RenderTypes() {
		super("", () -> {
		}, () -> {
		});
	}

	public static RenderType getOutlineTranslucent(ResourceLocation texture, boolean cull) {
		return RenderType.create(createLayerName("outline_translucent" + (cull ? "_cull" : "")),
				DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder()
						.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
						.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
						.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
						.setCullState(cull ? CULL : NO_CULL)
						.setLightmapState(LIGHTMAP)
						.setOverlayState(OVERLAY)
						.setWriteMaskState(RenderStateShard.COLOR_WRITE)
						.createCompositeState(true));	}

	public static RenderType getOutlineSolid(ResourceLocation texture) {
		return RenderType.create(createLayerName("outline_solid"), DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true,
				false, RenderType.CompositeState.builder()
						.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_CUTOUT_SHADER)
						.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
						.setLightmapState(LIGHTMAP)
						.setOverlayState(OVERLAY)
						.createCompositeState(true));
	}

	public static RenderType getDefaultOutlineSolid() {
		return DEFAULT_OUTLINE_SOLID;
	}

	protected static class NoCullState extends CullStateShard {
		public NoCullState() {
			super(false);
		}

		public void setupRenderState() {
			RenderSystem.disableCull();
		}
	}

	private static String createLayerName(String name) {
		return SidebarOverlay.MODID + ":" + name;
	}
}
