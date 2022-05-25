package mod.grimmauld.sidebaroverlay.util.outline;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import mod.grimmauld.sidebaroverlay.render.ExtraTextures;
import mod.grimmauld.sidebaroverlay.render.RenderTypes;
import mod.grimmauld.sidebaroverlay.render.SuperRenderTypeBuffer;
import mod.grimmauld.sidebaroverlay.util.AngleHelper;
import mod.grimmauld.sidebaroverlay.util.ColorHelper;
import mod.grimmauld.sidebaroverlay.util.VecHelper;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class Outline {
	final protected OutlineParams params = new OutlineParams();
	protected Matrix3f transformNormals;

	public Outline() {
	}

	public abstract void render(MatrixStack var1, SuperRenderTypeBuffer var2);

	public void renderCuboidLine(MatrixStack ms, SuperRenderTypeBuffer buffer, Vector3d start, Vector3d end) {
		Vector3d diff = end.subtract(start);
		float hAngle = AngleHelper.deg(MathHelper.atan2(diff.x, diff.z));
		float hDistance = (float) diff.multiply(1.0D, 0.0D, 1.0D).length();
		float vAngle = AngleHelper.deg(MathHelper.atan2(hDistance, diff.y)) - 90.0F;
		ms.pushPose();
		ms.translate(start.x, start.y, start.z);
		ms.mulPose(Vector3f.YP.rotationDegrees(hAngle));
		ms.mulPose(Vector3f.XP.rotationDegrees(vAngle));
		this.renderAACuboidLine(ms, buffer, Vector3d.ZERO, new Vector3d(0.0D, 0.0D, diff.length()));
		ms.popPose();
	}

	public void renderAACuboidLine(MatrixStack ms, SuperRenderTypeBuffer buffer, Vector3d start, Vector3d end) {
		float lineWidth = this.params.getLineWidth();
		if (lineWidth != 0.0F) {
			IVertexBuilder builder = buffer.getBuffer(RenderTypes.getOutlineSolid());
			Vector3d diff = end.subtract(start);
			Vector3d extension;
			if (diff.x + diff.y + diff.z < 0.0D) {
				extension = start;
				start = end;
				end = extension;
				diff = diff.scale(-1.0D);
			}

			extension = diff.normalize().scale(lineWidth / 2.0F);
			Vector3d plane = VecHelper.axisAlingedPlaneOf(diff);
			Direction face = Direction.getNearest(diff.x, diff.y, diff.z);
			Direction.Axis axis = face.getAxis();
			start = start.subtract(extension);
			end = end.add(extension);
			plane = plane.scale(lineWidth / 2.0F);
			Vector3d a1 = plane.add(start);
			Vector3d b1 = plane.add(end);
			plane = VecHelper.rotate(plane, -90.0D, axis);
			Vector3d a2 = plane.add(start);
			Vector3d b2 = plane.add(end);
			plane = VecHelper.rotate(plane, -90.0D, axis);
			Vector3d a3 = plane.add(start);
			Vector3d b3 = plane.add(end);
			plane = VecHelper.rotate(plane, -90.0D, axis);
			Vector3d a4 = plane.add(start);
			Vector3d b4 = plane.add(end);
			if (this.params.disableNormals) {
				face = Direction.UP;
				this.putQuad(ms, builder, b4, b3, b2, b1, face);
				this.putQuad(ms, builder, a1, a2, a3, a4, face);
				this.putQuad(ms, builder, a1, b1, b2, a2, face);
				this.putQuad(ms, builder, a2, b2, b3, a3, face);
				this.putQuad(ms, builder, a3, b3, b4, a4, face);
			} else {
				this.putQuad(ms, builder, b4, b3, b2, b1, face);
				this.putQuad(ms, builder, a1, a2, a3, a4, face.getOpposite());
				Vector3d vec = a1.subtract(a4);
				face = Direction.getNearest(vec.x, vec.y, vec.z);
				this.putQuad(ms, builder, a1, b1, b2, a2, face);
				vec = VecHelper.rotate(vec, -90.0D, axis);
				face = Direction.getNearest(vec.x, vec.y, vec.z);
				this.putQuad(ms, builder, a2, b2, b3, a3, face);
				vec = VecHelper.rotate(vec, -90.0D, axis);
				face = Direction.getNearest(vec.x, vec.y, vec.z);
				this.putQuad(ms, builder, a3, b3, b4, a4, face);
				vec = VecHelper.rotate(vec, -90.0D, axis);
				face = Direction.getNearest(vec.x, vec.y, vec.z);
			}
			this.putQuad(ms, builder, a4, b4, b1, a1, face);
		}
	}

	public void putQuad(MatrixStack ms, IVertexBuilder builder, Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4, Direction normal) {
		this.putQuadUV(ms, builder, v1, v2, v3, v4, 0.0F, 0.0F, 1.0F, 1.0F, normal);
	}

	public void putQuadUV(MatrixStack ms, IVertexBuilder builder, Vector3d v1, Vector3d v2, Vector3d v3, Vector3d v4, float minU, float minV, float maxU, float maxV, Direction normal) {
		this.putVertex(ms, builder, v1, minU, minV, normal);
		this.putVertex(ms, builder, v2, maxU, minV, normal);
		this.putVertex(ms, builder, v3, maxU, maxV, normal);
		this.putVertex(ms, builder, v4, minU, maxV, normal);
	}

	protected void putVertex(MatrixStack ms, IVertexBuilder builder, Vector3d pos, float u, float v, Direction normal) {
		int i = 15728880;
		int j = i >> 16 & '\uffff';
		int k = i & '\uffff';
		MatrixStack.Entry peek = ms.last();
		Vector3d rgb = this.params.rgb;
		if (this.transformNormals == null) {
			this.transformNormals = peek.normal();
		}

		int xOffset = 0;
		int yOffset = 0;
		int zOffset = 0;
		if (normal != null) {
			xOffset = normal.getStepX();
			yOffset = normal.getStepY();
			zOffset = normal.getStepZ();
		}

		builder.vertex(peek.pose(), (float) pos.x, (float) pos.y, (float) pos.z).color((float) rgb.x, (float) rgb.y, (float) rgb.z, this.params.alpha).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(j, k).normal(peek.normal(), (float) xOffset, (float) yOffset, (float) zOffset).endVertex();
		this.transformNormals = null;
	}

	public OutlineParams getParams() {
		return this.params;
	}

	public Outline extendedUpwards(int value) {
		return this;
	}

	public static class OutlineParams {
		protected final boolean fadeLineWidth;
		protected Optional<ExtraTextures> faceTexture;
		protected Optional<ExtraTextures> hightlightedFaceTexture;
		protected Direction highlightedFace;
		protected boolean disableCull;
		protected boolean disableNormals;
		protected float alpha;
		protected Vector3d rgb;
		private float lineWidth;

		public OutlineParams() {
			this.faceTexture = this.hightlightedFaceTexture = Optional.empty();
			this.alpha = 1.0F;
			this.lineWidth = 0.03125F;
			this.fadeLineWidth = true;
			this.rgb = ColorHelper.getRGB(16777215);
			int i = 15728880;
		}

		public OutlineParams colored(int color) {
			this.rgb = ColorHelper.getRGB(color);
			return this;
		}

		public OutlineParams lineWidth(float width) {
			this.lineWidth = width;
			return this;
		}

		public OutlineParams withFaceTexture(ExtraTextures texture) {
			this.faceTexture = Optional.ofNullable(texture);
			return this;
		}

		public void clearTextures() {
			this.withFaceTextures(null, null);
		}

		public void withFaceTextures(ExtraTextures texture, ExtraTextures highlightTexture) {
			this.faceTexture = Optional.ofNullable(texture);
			this.hightlightedFaceTexture = Optional.ofNullable(highlightTexture);
		}

		public OutlineParams highlightFace(@Nullable Direction face) {
			this.highlightedFace = face;
			return this;
		}

		public OutlineParams disableNormals() {
			this.disableNormals = true;
			return this;
		}

		public OutlineParams disableCull() {
			this.disableCull = true;
			return this;
		}

		public float getLineWidth() {
			return this.fadeLineWidth ? this.alpha * this.lineWidth : this.lineWidth;
		}

		public Direction getHighlightedFace() {
			return this.highlightedFace;
		}
	}
}