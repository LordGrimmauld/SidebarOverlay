package mod.grimmauld.sidebaroverlay.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.*;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.Util;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.SortedMap;

import net.minecraft.client.renderer.MultiBufferSource.BufferSource;

import com.mojang.blaze3d.vertex.BufferBuilder;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SuperRenderTypeBuffer implements MultiBufferSource {
	static final LazyLoadedValue<SuperRenderTypeBuffer> instance = new LazyLoadedValue<>(SuperRenderTypeBuffer::new);
	final SuperRenderTypeBufferPhase earlyBuffer = new SuperRenderTypeBufferPhase();
	final SuperRenderTypeBufferPhase defaultBuffer = new SuperRenderTypeBufferPhase();
	final SuperRenderTypeBufferPhase lateBuffer = new SuperRenderTypeBufferPhase();

	public SuperRenderTypeBuffer() {
	}

	public static SuperRenderTypeBuffer getInstance() {
		return instance.get();
	}

	public VertexConsumer getBuffer(RenderType type) {
		return this.defaultBuffer.getBuffer(type);
	}

	public VertexConsumer getLateBuffer(RenderType type) {
		return this.lateBuffer.getBuffer(type);
	}

	public void draw() {
		RenderSystem.disableCull();
		this.earlyBuffer.endBatch();
		this.defaultBuffer.endBatch();
		this.lateBuffer.endBatch();
	}

	private static class SuperRenderTypeBufferPhase extends BufferSource {
		static final ChunkBufferBuilderPack blockBuilders = new ChunkBufferBuilderPack();

		protected SuperRenderTypeBufferPhase() {
			super(new BufferBuilder(256), createEntityBuilders());
		}

		static SortedMap<RenderType, BufferBuilder> createEntityBuilders() {
			return Util.make(new Object2ObjectLinkedOpenHashMap<>(), (map) -> {
				map.put(Sheets.solidBlockSheet(), blockBuilders.builder(RenderType.solid()));
				assign(map, RenderTypes.getOutlineSolid());
				map.put(Sheets.cutoutBlockSheet(), blockBuilders.builder(RenderType.cutout()));
				map.put(Sheets.bannerSheet(), blockBuilders.builder(RenderType.cutoutMipped()));
				map.put(Sheets.translucentCullBlockSheet(), blockBuilders.builder(RenderType.translucent()));
				assign(map, Sheets.shieldSheet());
				assign(map, Sheets.bedSheet());
				assign(map, Sheets.shulkerBoxSheet());
				assign(map, Sheets.signSheet());
				assign(map, Sheets.chestSheet());
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
