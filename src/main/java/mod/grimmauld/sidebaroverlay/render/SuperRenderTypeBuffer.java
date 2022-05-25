package mod.grimmauld.sidebaroverlay.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.util.LazyValue;
import net.minecraft.util.Util;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.SortedMap;

import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SuperRenderTypeBuffer implements IRenderTypeBuffer {
	static final LazyValue<SuperRenderTypeBuffer> instance = new LazyValue<>(SuperRenderTypeBuffer::new);
	final SuperRenderTypeBufferPhase earlyBuffer = new SuperRenderTypeBufferPhase();
	final SuperRenderTypeBufferPhase defaultBuffer = new SuperRenderTypeBufferPhase();
	final SuperRenderTypeBufferPhase lateBuffer = new SuperRenderTypeBufferPhase();

	public SuperRenderTypeBuffer() {
	}

	public static SuperRenderTypeBuffer getInstance() {
		return instance.get();
	}

	public IVertexBuilder getBuffer(RenderType type) {
		return this.defaultBuffer.getBuffer(type);
	}

	public IVertexBuilder getLateBuffer(RenderType type) {
		return this.lateBuffer.getBuffer(type);
	}

	public void draw() {
		RenderSystem.disableCull();
		this.earlyBuffer.endBatch();
		this.defaultBuffer.endBatch();
		this.lateBuffer.endBatch();
	}

	private static class SuperRenderTypeBufferPhase extends Impl {
		static final RegionRenderCacheBuilder blockBuilders = new RegionRenderCacheBuilder();

		protected SuperRenderTypeBufferPhase() {
			super(new BufferBuilder(256), createEntityBuilders());
		}

		static SortedMap<RenderType, BufferBuilder> createEntityBuilders() {
			return Util.make(new Object2ObjectLinkedOpenHashMap<>(), (map) -> {
				map.put(Atlases.solidBlockSheet(), blockBuilders.builder(RenderType.solid()));
				assign(map, RenderTypes.getOutlineSolid());
				map.put(Atlases.cutoutBlockSheet(), blockBuilders.builder(RenderType.cutout()));
				map.put(Atlases.bannerSheet(), blockBuilders.builder(RenderType.cutoutMipped()));
				map.put(Atlases.translucentCullBlockSheet(), blockBuilders.builder(RenderType.translucent()));
				assign(map, Atlases.shieldSheet());
				assign(map, Atlases.bedSheet());
				assign(map, Atlases.shulkerBoxSheet());
				assign(map, Atlases.signSheet());
				assign(map, Atlases.chestSheet());
				assign(map, RenderType.translucentNoCrumbling());
				assign(map, RenderType.glint());
				assign(map, RenderType.entityGlint());
				assign(map, RenderType.waterMask());
				ModelBakery.DESTROY_TYPES.forEach((p_228488_1_) -> assign(map, p_228488_1_));
			});
		}

		private static void assign(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> map, RenderType type) {
			map.put(type, new BufferBuilder(type.bufferSize()));
		}
	}
}
