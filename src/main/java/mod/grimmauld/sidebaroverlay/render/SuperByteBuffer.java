package mod.grimmauld.sidebaroverlay.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector4f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SuperByteBuffer {
	protected final ByteBuffer template;
	protected final int formatSize;
	final Vector4f pos = new Vector4f();
	private MatrixStack transforms;

	public SuperByteBuffer(BufferBuilder buf) {
		Pair<BufferBuilder.DrawState, ByteBuffer> state = buf.popNextBuffer();
		ByteBuffer rendered = state.getSecond();
		rendered.order(ByteOrder.nativeOrder());
		this.formatSize = buf.getVertexFormat().getVertexSize();
		int size = state.getFirst().vertexCount() * this.formatSize;
		this.template = GLAllocation.createByteBuffer(size);
		this.template.order(rendered.order());
		this.template.limit(rendered.limit());
		this.template.put(rendered);
		this.template.rewind();
		this.transforms = new MatrixStack();
	}

	public void renderInto(MatrixStack input, IVertexBuilder builder) {
		ByteBuffer buffer = this.template;
		if (buffer.limit() != 0) {
			buffer.rewind();
			Matrix4f t = input.last().pose().copy();
			Matrix4f localTransforms = this.transforms.last().pose();
			t.multiply(localTransforms);

			int vertexCount = this.vertexCount(buffer);

			for (int i = 0; i < vertexCount; ++i) {
				float x = this.getX(buffer, i);
				float y = this.getY(buffer, i);
				float z = this.getZ(buffer, i);
				byte r = this.getR(buffer, i);
				byte g = this.getG(buffer, i);
				byte b = this.getB(buffer, i);
				byte a = this.getA(buffer, i);
				this.pos.set(x, y, z, 1.0F);
				this.pos.transform(t);
				builder.vertex(this.pos.x(), this.pos.y(), this.pos.z());
				builder.color(r, g, b, a);

				float u = this.getU(buffer, i);
				float v = this.getV(buffer, i);
				builder.uv(u, v);

				builder.uv2(this.getLight(buffer, i));

				builder.normal(this.getNX(buffer, i), this.getNY(buffer, i), this.getNZ(buffer, i)).endVertex();
			}

			this.transforms = new MatrixStack();
		}
	}

	protected int vertexCount(ByteBuffer buffer) {
		return buffer.limit() / this.formatSize;
	}

	protected int getBufferPosition(int vertexIndex) {
		return vertexIndex * this.formatSize;
	}

	protected float getX(ByteBuffer buffer, int index) {
		return buffer.getFloat(this.getBufferPosition(index));
	}

	protected float getY(ByteBuffer buffer, int index) {
		return buffer.getFloat(this.getBufferPosition(index) + 4);
	}

	protected float getZ(ByteBuffer buffer, int index) {
		return buffer.getFloat(this.getBufferPosition(index) + 8);
	}

	protected byte getR(ByteBuffer buffer, int index) {
		return buffer.get(this.getBufferPosition(index) + 12);
	}

	protected byte getG(ByteBuffer buffer, int index) {
		return buffer.get(this.getBufferPosition(index) + 13);
	}

	protected byte getB(ByteBuffer buffer, int index) {
		return buffer.get(this.getBufferPosition(index) + 14);
	}

	protected byte getA(ByteBuffer buffer, int index) {
		return buffer.get(this.getBufferPosition(index) + 15);
	}

	protected float getU(ByteBuffer buffer, int index) {
		return buffer.getFloat(this.getBufferPosition(index) + 16);
	}

	protected float getV(ByteBuffer buffer, int index) {
		return buffer.getFloat(this.getBufferPosition(index) + 20);
	}

	protected int getLight(ByteBuffer buffer, int index) {
		return buffer.getInt(this.getBufferPosition(index) + 24);
	}

	protected byte getNX(ByteBuffer buffer, int index) {
		return buffer.get(this.getBufferPosition(index) + 28);
	}

	protected byte getNY(ByteBuffer buffer, int index) {
		return buffer.get(this.getBufferPosition(index) + 29);
	}

	protected byte getNZ(ByteBuffer buffer, int index) {
		return buffer.get(this.getBufferPosition(index) + 30);
	}
}