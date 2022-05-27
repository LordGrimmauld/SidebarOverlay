package mod.grimmauld.sidebaroverlay.util.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.grimmauld.sidebaroverlay.render.RenderTypes;
import mod.grimmauld.sidebaroverlay.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class AABBOutline extends Outline {
	protected AABB bb;

	public AABBOutline(AABB bb) {
		this.setBounds(bb);
	}

	public void render(PoseStack ms, SuperRenderTypeBuffer buffer) {
		this.renderBB(ms, buffer, this.bb);
	}

	public void renderBB(PoseStack ms, SuperRenderTypeBuffer buffer, AABB bb) {
		Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
		boolean noCull = bb.contains(projectedView);
		bb = bb.inflate(noCull ? -0.0078125D : 0.0078125D);
		noCull |= this.params.disableCull;
		Vec3 xyz = new Vec3(bb.minX, bb.minY, bb.minZ);
		Vec3 Xyz = new Vec3(bb.maxX, bb.minY, bb.minZ);
		Vec3 xYz = new Vec3(bb.minX, bb.maxY, bb.minZ);
		Vec3 XYz = new Vec3(bb.maxX, bb.maxY, bb.minZ);
		Vec3 xyZ = new Vec3(bb.minX, bb.minY, bb.maxZ);
		Vec3 XyZ = new Vec3(bb.maxX, bb.minY, bb.maxZ);
		Vec3 xYZ = new Vec3(bb.minX, bb.maxY, bb.maxZ);
		Vec3 XYZ = new Vec3(bb.maxX, bb.maxY, bb.maxZ);
		renderPartial(ms, buffer, xYz, Xyz, xyz, XYZ, XyZ, xyZ);
		renderPartial(ms, buffer, Xyz, xYz, XYz, xyZ, xYZ, XYZ);
		this.renderFace(ms, buffer, Direction.NORTH, xYz, XYz, Xyz, xyz, noCull);
		this.renderFace(ms, buffer, Direction.SOUTH, XYZ, xYZ, xyZ, XyZ, noCull);
		this.renderFace(ms, buffer, Direction.EAST, XYz, XYZ, XyZ, Xyz, noCull);
		this.renderFace(ms, buffer, Direction.WEST, xYZ, xYz, xyz, xyZ, noCull);
		this.renderFace(ms, buffer, Direction.UP, xYZ, XYZ, XYz, xYz, noCull);
		this.renderFace(ms, buffer, Direction.DOWN, xyz, Xyz, XyZ, xyZ, noCull);
	}

	private void renderPartial(PoseStack ms, SuperRenderTypeBuffer buffer, Vec3 xyz, Vec3 xYz, Vec3 XYz, Vec3 xyZ, Vec3 xYZ, Vec3 XYZ) {
		this.renderAACuboidLine(ms, buffer, XYz, xYz);
		this.renderAACuboidLine(ms, buffer, XYz, xyz);
		this.renderAACuboidLine(ms, buffer, XYz, XYZ);
		this.renderAACuboidLine(ms, buffer, xYZ, XYZ);
		this.renderAACuboidLine(ms, buffer, xYZ, xyZ);
		this.renderAACuboidLine(ms, buffer, xYZ, xYz);
	}

	protected void renderFace(PoseStack ms, SuperRenderTypeBuffer buffer, Direction direction, Vec3 p1, Vec3 p2, Vec3 p3, Vec3 p4, boolean noCull) {
		if (this.params.faceTexture.isPresent()) {
			ResourceLocation faceTexture = this.params.faceTexture.get().getLocation();
			float alphaBefore = this.params.alpha;
			this.params.alpha = direction == this.params.getHighlightedFace() && this.params.hightlightedFaceTexture.isPresent() ? 1.0F : 0.5F;
			RenderType translucentType = RenderTypes.getOutlineTranslucent(faceTexture, !noCull);
			VertexConsumer builder = buffer.getLateBuffer(translucentType);
			Direction.Axis axis = direction.getAxis();
			Vec3 uDiff = p2.subtract(p1);
			Vec3 vDiff = p4.subtract(p1);
			float maxU = (float) Math.abs(axis == Direction.Axis.X ? uDiff.z : uDiff.x);
			float maxV = (float) Math.abs(axis == Direction.Axis.Y ? vDiff.z : vDiff.y);
			this.putQuadUV(ms, builder, p1, p2, p3, p4, 0.0F, 0.0F, maxU, maxV, Direction.UP);
			this.params.alpha = alphaBefore;
		}
	}

	public void setBounds(AABB bb) {
		this.bb = bb;
	}
}
