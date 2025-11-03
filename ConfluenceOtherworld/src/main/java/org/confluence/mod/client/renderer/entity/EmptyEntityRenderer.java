package org.confluence.mod.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.entity.EmptyEntity;
import org.joml.Vector2d;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.confluence.lib.util.VectorUtils.lightningPathList;

public class EmptyEntityRenderer extends EntityRenderer<EmptyEntity> {
	public EmptyEntityRenderer(final EntityRendererProvider.Context context) {
		super(context);
	}

	private final Map<Vector2d, Vector2d> POINTS_MAP = new HashMap<>();
	private final Map<Vector3d, Integer> LIGHTNING = new HashMap<>();
	private final List<Double> POINTS_SIDE = new ArrayList<>();
	private long TIME_BEFORE = 0;
	private double ROTATE0 = 0;
	private double ROTATE1 = 0;

	@Override
	public void render(final EmptyEntity entity, final float entityYaw, final float partialTick, final PoseStack poseStack, final MultiBufferSource bufferSource, final int packedLight) {

        ROTATE0 += 0.03;
        ROTATE1 += 0.03;

		int maxCount = 5;

		double moveDis = 0.5;
		ClientLevel level = Minecraft.getInstance().level;
		final long timeVariable = (System.currentTimeMillis() / 35) % 10000;
		boolean updata = (TIME_BEFORE != timeVariable);
		TIME_BEFORE = timeVariable;
		RandomSource random = entity.getRandom();
		RandomSource random1 = entity.getRandom();
		RandomSource random2 = entity.getRandom();
		RandomSource random3 = entity.getRandom();
		random1.setSeed(timeVariable);

		random.setSeed(random1.nextInt());
		random2.setSeed(random1.nextInt());

		while (POINTS_MAP.size() < maxCount) {
			double rotate = random2.nextDouble() * 2 * Math.PI;
			double length0 = random2.nextDouble() * 5;
			double length1 = random2.nextDouble() * 35 + 20;
			Vector2d pointFirst = new Vector2d(length1 * Math.cos(rotate), length1 * Math.sin(rotate));
			Vector2d pointLast = new Vector2d(length0 * Math.cos(rotate), length0 * Math.sin(rotate));
			POINTS_MAP.put(pointFirst, pointLast);
		}

		while (POINTS_SIDE.size() < maxCount) {
			POINTS_SIDE.add(0.6 + random2.nextDouble());
		}

		Vec3 entityPos = new Vec3(0, 0, 0);
		Vector3d entityMainPos = toVector3d(entity.getPosition(partialTick));

		List<Vector2d> removePoints = new ArrayList<>();
		int sideGet = 0;

		for (Map.Entry<Vector2d, Vector2d> doublePoints : POINTS_MAP.entrySet()){
			int yMax = 0;
			boolean showTheLightning = true;
			double mainSide = POINTS_SIDE.get(sideGet);
			Vector2d pointFirst = doublePoints.getKey();
			Vector2d pointLast = doublePoints.getValue();
			if (updata) {
				double pointLastLength = pointLast.length();
				pointLast = new Vector2d(pointLast.x + (moveDis / pointLastLength * pointLast.x), pointLast.y + (moveDis / pointLastLength * pointLast.y));
				if (pointLast.length() > pointFirst.length()) {
					removePoints.add(pointFirst);
					POINTS_SIDE.remove(sideGet);
					POINTS_SIDE.add(sideGet, 0.6 + random2.nextDouble());
				} else POINTS_MAP.put(pointFirst, pointLast);
			}
			BlockPos checkPos = new BlockPos((int) (pointLast.x + entityMainPos.x), (int) entityMainPos.y, (int) (pointLast.y + entityMainPos.z));
            if (level != null) {
                while (level.getBlockState(checkPos).canBeReplaced() || !level.getBlockState(checkPos.offset(0, 1, 0)).canBeReplaced()) {
                    if (level.getBlockState(checkPos).canBeReplaced() && level.getBlockState(checkPos.offset(0, 1, 0)).canBeReplaced()) checkPos = checkPos.offset(0, -1, 0);
                    if (!level.getBlockState(checkPos).canBeReplaced() && !level.getBlockState(checkPos.offset(0, 1, 0)).canBeReplaced()) checkPos = checkPos.offset(0, 1, 0);
                    int y = checkPos.getY();
                    yMax++;
                    if ((y > level.getMaxBuildHeight()) || (y < level.getMinBuildHeight()) || (yMax > 40)) {
						showTheLightning = false;
                    }
                    if (!showTheLightning) break;
                }
            }
            if (!showTheLightning) continue;
			Vec3 targetPos = entityPos.add(pointLast.x, checkPos.getY() - entityMainPos.y, pointLast.y());
			Vector3d mPos = new Vector3d(targetPos.x / 2 + random.nextDouble() * 6 - 3, -random.nextDouble() * 6 - 2, targetPos.z / 2 + random.nextDouble() * 6 - 3);

			List<Vector3d> pathPoints = new ArrayList<>();
			pathPoints.add(toVector3d(entityPos));
			if (mPos.y > targetPos.y) pathPoints.add(mPos);
			pathPoints.add(toVector3d(targetPos));

			lightningPathList(
					pathPoints,
					mainSide + 1.5,
					(float) (0.1 * mainSide),
					random
			);
			Vector3d point0 = pathPoints.getFirst();
			Vector3d point1 = pathPoints.get(1);
			sideGet++;

			Vector3d pointF0 = pathPoints.getLast();
			Vector3d pointF1 = pathPoints.get(pathPoints.size() - 2);
			Vector3d pointN1 = new Vector3d(2 * point0.x - point1.x, 2 * point0.y - point1.y, 2 * point0.z - point1.z);
			Vector3d pointNF1 = new Vector3d(2 * pointF0.x - pointF1.x, 2 * pointF0.y - pointF1.y, 2 * pointF0.z - pointF1.z);
			pathPoints.addFirst(pointN1);
			pathPoints.add(pointNF1);
			renderLightningPath(poseStack, bufferSource, pathPoints, 180, 0, 255, 255, mainSide + 0.3, entityMainPos, false);
			renderLightningPath(poseStack, bufferSource, pathPoints, 128, 0, 255, 127, mainSide + 0.7, entityMainPos, false);
			renderLightningPath(poseStack, bufferSource, pathPoints, 255, 255, 255, 255, mainSide + 0.1, entityMainPos, false);
			renderLightningPath(poseStack, bufferSource, pathPoints, 0, 0, 0, 255, mainSide, entityMainPos, true);
		}

		for (Vector2d removePoint : removePoints) {
			POINTS_MAP.remove(removePoint);
		}
		drawCube(poseStack, bufferSource, 10, 0, 0, 0, 255, entityMainPos, true);
		drawCube(poseStack, bufferSource, 10.1, 255, 255, 255, 255, entityMainPos, false);
		drawCube(poseStack, bufferSource, 10.3, 180, 0, 255, 255, entityMainPos, false);
		drawCube(poseStack, bufferSource, 11.5, 128, 0, 255, 127, entityMainPos, false);

		while ((LIGHTNING.size() < 10) && (random2.nextDouble() < 0.6)) {
			Vector3d facing = new Vector3d(random2.nextDouble() * 2 - 1, random2.nextDouble() * 2 - 1, random2.nextDouble() * 2 - 1);
			while (facing.length() > 1) facing = new Vector3d(random2.nextDouble() * 2 - 1, random2.nextDouble() * 2 - 1, random2.nextDouble() * 2 - 1);
			facing.normalize().mul(random2.nextDouble() * 8 + 20);
			LIGHTNING.put(facing, 0);
		}

		List<Vector3d> willRemove = new ArrayList<>();

		int maxTime = 70;
		int debugTime = 25;

		for (Map.Entry<Vector3d, Integer> entry : LIGHTNING.entrySet()) {
			random3.setSeed((long) entry.getKey().length());
			List<Vector3d> debugList = new ArrayList<>();
			Vector3d debugVct0 = new Vector3d(new Vector3d(entry.getKey()).normalize().mul(5));
			Vector3d debugVct1 = new Vector3d(entry.getKey());
			debugList.add(debugVct0);
			debugList.add(debugVct1);
			List<List<Vector3d>> newLightning = lightningPathList(debugList, 0.2, 0.1F, random3, 3, 0.5F);
			int alpha;
			int time = entry.getValue();
			alpha = switch (time) {
				case 5, 10, 15, 20 -> 0;
				case 6, 11, 16, 21 -> 100;
				case 7, 12, 17, 22 -> 200;
				default -> 255;
			};
			if (time > debugTime) alpha = (int) (((double) (maxTime - time) / (double) (maxTime - debugTime)) * 255);

			for (List<Vector3d> listVct : newLightning) {
				Vector3d beforeVct = new Vector3d(0, 0, 0);
				int i = 0;
				for (Vector3d lightningVct : listVct) {
					if (i != 0) {
						PoseStack.Pose pose = poseStack.last();
						VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
						poseStack.pushPose();
						{
							consumer.addVertex(pose, (float) beforeVct.x, (float) beforeVct.y, (float) beforeVct.z)
									.setColor(180, 0, 255, alpha)
									.setNormal(pose, 0, 1, 0);
							consumer.addVertex(pose, (float) lightningVct.x, (float) lightningVct.y, (float) lightningVct.z)
									.setColor(180, 0, 255, alpha)
									.setNormal(pose, 0, 1, 0);
						}
						poseStack.popPose();
					}
					beforeVct = lightningVct;
					i++;
				}
			}
			if (updata) time++;
			if (time >= maxTime) willRemove.add(entry.getKey());
			else LIGHTNING.put(entry.getKey(), time);
		}

		for (Vector3d removeKey : willRemove) {
			LIGHTNING.remove(removeKey);
		}
	}

	private Vector3d toVector3d(Vec3 vec3) {
		return new Vector3d(vec3.x, vec3.y, vec3.z);
	}

	private void renderLightningPath(PoseStack poseStack, MultiBufferSource bufferSource, List<Vector3d> pathPoints, int red, int green, int blue, int alpha, double side, Vector3d entityPos, boolean face) {

		List<Vector3d> points = new ArrayList<>();
		List<Vector3d> points0 = new ArrayList<>();
		List<Vector3d> points1 = new ArrayList<>();
		List<Vector3d> points2 = new ArrayList<>();
		List<Vector3d> points3 = new ArrayList<>();
		Vector3d before = new Vector3d(0, 0, 0);
		Vector3d point = new Vector3d(0, 0, 0);

		int j = 0;
		for (Vector3d after : pathPoints) {
			if (j >= 2) {
				points.clear();
				VectorUtils.findVerticalPlane(point, before, after, side, points);
				points0.add(points.get(0));
				points1.add(points.get(1));
				points2.add(points.get(2));
				points3.add(points.get(3));
			}
			before = point;
			point = after;
			j++;
		}

		PoseStack.Pose pose = poseStack.last();
		poseStack.pushPose();
		Vector3d cameraPos = toVector3d(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());
		cameraPos = new Vector3d(cameraPos.x - entityPos.x, cameraPos.y - entityPos.y, cameraPos.z - entityPos.z);

		for (int i = 0; i < points0.size() - 1; i++) {
			Vector3d startPoint0 = points0.get(i);
			Vector3d endPoint0 = points0.get(i + 1);
			Vector3d startPoint1 = points1.get(i);
			Vector3d endPoint1 = points1.get(i + 1);
			Vector3d startPoint2 = points2.get(i);
			Vector3d endPoint2 = points2.get(i + 1);
			Vector3d startPoint3 = points3.get(i);
			Vector3d endPoint3 = points3.get(i + 1);

			VertexConsumer consumer = bufferSource.getBuffer(RenderType.debugQuads());

			poseStack.pushPose();
			{
				if (face || calculateNormal(startPoint0, endPoint0, endPoint1, cameraPos)) {
					consumer.addVertex(pose, (float) startPoint0.x, (float) startPoint0.y, (float) startPoint0.z)
							.setColor(red, green, blue, alpha);
					consumer.addVertex(pose, (float) endPoint0.x, (float) endPoint0.y, (float) endPoint0.z)
							.setColor(red, green, blue, alpha);
					consumer.addVertex(pose, (float) endPoint1.x, (float) endPoint1.y, (float) endPoint1.z)
							.setColor(red, green, blue, alpha);
					consumer.addVertex(pose, (float) startPoint1.x, (float) startPoint1.y, (float) startPoint1.z)
							.setColor(red, green, blue, alpha);
				}

				if (face || calculateNormal(startPoint1, endPoint1, endPoint2, cameraPos)) {
					consumer.addVertex(pose, (float) startPoint1.x, (float) startPoint1.y, (float) startPoint1.z)
							.setColor(red, green, blue, alpha);
					consumer.addVertex(pose, (float) endPoint1.x, (float) endPoint1.y, (float) endPoint1.z)
							.setColor(red, green, blue, alpha);
					consumer.addVertex(pose, (float) endPoint2.x, (float) endPoint2.y, (float) endPoint2.z)
							.setColor(red, green, blue, alpha);
					consumer.addVertex(pose, (float) startPoint2.x, (float) startPoint2.y, (float) startPoint2.z)
							.setColor(red, green, blue, alpha);
				}

				if (face || calculateNormal(startPoint2, endPoint2, endPoint3, cameraPos)) {
					consumer.addVertex(pose, (float) startPoint2.x, (float) startPoint2.y, (float) startPoint2.z)
							.setColor(red, green, blue, alpha);
					consumer.addVertex(pose, (float) endPoint2.x, (float) endPoint2.y, (float) endPoint2.z)
							.setColor(red, green, blue, alpha);
					consumer.addVertex(pose, (float) endPoint3.x, (float) endPoint3.y, (float) endPoint3.z)
							.setColor(red, green, blue, alpha);
					consumer.addVertex(pose, (float) startPoint3.x, (float) startPoint3.y, (float) startPoint3.z)
							.setColor(red, green, blue, alpha);
				}

				if (face || calculateNormal(startPoint3, endPoint3, endPoint0, cameraPos)) {
					consumer.addVertex(pose, (float) startPoint3.x, (float) startPoint3.y, (float) startPoint3.z)
							.setColor(red, green, blue, alpha);
					consumer.addVertex(pose, (float) endPoint3.x, (float) endPoint3.y, (float) endPoint3.z)
							.setColor(red, green, blue, alpha);
					consumer.addVertex(pose, (float) endPoint0.x, (float) endPoint0.y, (float) endPoint0.z)
							.setColor(red, green, blue, alpha);
					consumer.addVertex(pose, (float) startPoint0.x, (float) startPoint0.y, (float) startPoint0.z)
							.setColor(red, green, blue, alpha);
				}
			}
			poseStack.popPose();
		}
		poseStack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(final EmptyEntity entity) {
		return ResourceLocation.withDefaultNamespace("textures/block/white_concrete.png");
	}

	public static boolean calculateNormal(Vector3d p0, Vector3d p1, Vector3d p2, Vector3d cameraPos) {

		Vector3d edge1 = new Vector3d(p1).sub(p0);
		Vector3d edge2 = new Vector3d(p2).sub(p0);

		Vector3d normal = new Vector3d(edge1).cross(edge2);

		normal.normalize();

		Vector3d cameraV = new Vector3d(cameraPos.x - p0.x, cameraPos.y - p0.y, cameraPos.z - p0.z);

		cameraV.normalize();

		double cosCamera = (cameraV.x * normal.x + cameraV.y * normal.y + cameraV.z * normal.z) / (cameraV.length() * normal.length());

		return (cosCamera > 0);
	}

	private void drawCube(PoseStack poseStack, MultiBufferSource bufferSource, double ballSide, int red, int green, int blue, int alpha, Vector3d entityPos, boolean face) {

		PoseStack.Pose pose = poseStack.last();

		VertexConsumer consumer = bufferSource.getBuffer(RenderType.debugQuads());

		double ballSide1 = ballSide * Math.sqrt(2);

		double x0_y = ballSide1 * Math.cos(ROTATE0) / 2;
		double x1_y = -ballSide1 * Math.sin(ROTATE0) / 2;
		double x2_y = -x0_y;
		double x3_y = -x1_y;
		double z0_y = ballSide1 * Math.sin(ROTATE0) / 2;
		double z1_y = ballSide1 * Math.cos(ROTATE0) / 2;
		double z2_y = -z0_y;
		double z3_y = -z1_y;

		Vector3d point0_y = new Vector3d(x0_y, ballSide / 2, z0_y);
		Vector3d point1_y = new Vector3d(x1_y, ballSide / 2, z1_y);
		Vector3d point2_y = new Vector3d(x2_y, ballSide / 2, z2_y);
		Vector3d point3_y = new Vector3d(x3_y, ballSide / 2, z3_y);

		double cosZ = Math.cos(ROTATE1);
		double sinZ = Math.sin(ROTATE1);

		Vector3d ballPoint0 = new Vector3d(
				point0_y.x * cosZ - point0_y.y * sinZ,
				point0_y.x * sinZ + point0_y.y * cosZ,
				point0_y.z
		);
		Vector3d ballPoint1 = new Vector3d(
				point1_y.x * cosZ - point1_y.y * sinZ,
				point1_y.x * sinZ + point1_y.y * cosZ,
				point1_y.z
		);
		Vector3d ballPoint2 = new Vector3d(
				point2_y.x * cosZ - point2_y.y * sinZ,
				point2_y.x * sinZ + point2_y.y * cosZ,
				point2_y.z
		);
		Vector3d ballPoint3 = new Vector3d(
				point3_y.x * cosZ - point3_y.y * sinZ,
				point3_y.x * sinZ + point3_y.y * cosZ,
				point3_y.z
		);
		Vector3d cameraPos = toVector3d(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition());
		cameraPos = new Vector3d(cameraPos.x - entityPos.x, cameraPos.y - entityPos.y, cameraPos.z - entityPos.z);

		poseStack.pushPose();
		if (face || calculateNormal(ballPoint0, new Vector3d(ballPoint2).negate(), new Vector3d(ballPoint3).negate(), cameraPos)) {
			consumer.addVertex(pose, (float) ballPoint0.x, (float) ballPoint0.y, (float) ballPoint0.z)
					.setColor(red, green, blue, alpha);
			consumer.addVertex(pose, (float) -ballPoint2.x, (float) -ballPoint2.y, (float) -ballPoint2.z)
					.setColor(red, green, blue, alpha);
			consumer.addVertex(pose, (float) -ballPoint3.x, (float) -ballPoint3.y, (float) -ballPoint3.z)
					.setColor(red, green, blue, alpha);
			consumer.addVertex(pose, (float) ballPoint1.x, (float) ballPoint1.y, (float) ballPoint1.z)
					.setColor(red, green, blue, alpha);
		}
		if (face || calculateNormal(ballPoint1, new Vector3d(ballPoint3).negate(), new Vector3d(ballPoint0).negate(), cameraPos)) {
			consumer.addVertex(pose, (float) ballPoint1.x, (float) ballPoint1.y, (float) ballPoint1.z)
					.setColor(red, green, blue, alpha);
			consumer.addVertex(pose, (float) -ballPoint3.x, (float) -ballPoint3.y, (float) -ballPoint3.z)
					.setColor(red, green, blue, alpha);
			consumer.addVertex(pose, (float) -ballPoint0.x, (float) -ballPoint0.y, (float) -ballPoint0.z)
					.setColor(red, green, blue, alpha);
			consumer.addVertex(pose, (float) ballPoint2.x, (float) ballPoint2.y, (float) ballPoint2.z)
					.setColor(red, green, blue, alpha);
		}
		if (face || calculateNormal(ballPoint2, new Vector3d(ballPoint0).negate(), new Vector3d(ballPoint1).negate(), cameraPos)) {
			consumer.addVertex(pose, (float) ballPoint2.x, (float) ballPoint2.y, (float) ballPoint2.z)
					.setColor(red, green, blue, alpha);
			consumer.addVertex(pose, (float) -ballPoint0.x, (float) -ballPoint0.y, (float) -ballPoint0.z)
					.setColor(red, green, blue, alpha);
			consumer.addVertex(pose, (float) -ballPoint1.x, (float) -ballPoint1.y, (float) -ballPoint1.z)
					.setColor(red, green, blue, alpha);
			consumer.addVertex(pose, (float) ballPoint3.x, (float) ballPoint3.y, (float) ballPoint3.z)
					.setColor(red, green, blue, alpha);
		}
		if (face || calculateNormal(ballPoint3, new Vector3d(ballPoint1).negate(), new Vector3d(ballPoint2).negate(), cameraPos)) {
			consumer.addVertex(pose, (float) ballPoint3.x, (float) ballPoint3.y, (float) ballPoint3.z)
					.setColor(red, green, blue, alpha);
			consumer.addVertex(pose, (float) -ballPoint1.x, (float) -ballPoint1.y, (float) -ballPoint1.z)
					.setColor(red, green, blue, alpha);
			consumer.addVertex(pose, (float) -ballPoint2.x, (float) -ballPoint2.y, (float) -ballPoint2.z)
					.setColor(red, green, blue, alpha);
			consumer.addVertex(pose, (float) ballPoint0.x, (float) ballPoint0.y, (float) ballPoint0.z)
					.setColor(red, green, blue, alpha);
		}
		if (face || calculateNormal(ballPoint0, ballPoint1, ballPoint2, cameraPos)) {
			consumer.addVertex(pose, (float) ballPoint0.x, (float) ballPoint0.y, (float) ballPoint0.z)
					.setColor(red, green, blue, alpha);
			consumer.addVertex(pose, (float) ballPoint1.x, (float) ballPoint1.y, (float) ballPoint1.z)
					.setColor(red, green, blue, alpha);
			consumer.addVertex(pose, (float) ballPoint2.x, (float) ballPoint2.y, (float) ballPoint2.z)
					.setColor(red, green, blue, alpha);
			consumer.addVertex(pose, (float) ballPoint3.x, (float) ballPoint3.y, (float) ballPoint3.z)
					.setColor(red, green, blue, alpha);
		}
		if (face || calculateNormal(new Vector3d(ballPoint2).negate(), new Vector3d(ballPoint1).negate(), new Vector3d(ballPoint0).negate(), cameraPos)) {
			consumer.addVertex(pose, (float) -ballPoint0.x, (float) -ballPoint0.y, (float) -ballPoint0.z)
					.setColor(red, green, blue, alpha);
			consumer.addVertex(pose, (float) -ballPoint1.x, (float) -ballPoint1.y, (float) -ballPoint1.z)
					.setColor(red, green, blue, alpha);
			consumer.addVertex(pose, (float) -ballPoint2.x, (float) -ballPoint2.y, (float) -ballPoint2.z)
					.setColor(red, green, blue, alpha);
			consumer.addVertex(pose, (float) -ballPoint3.x, (float) -ballPoint3.y, (float) -ballPoint3.z)
					.setColor(red, green, blue, alpha);
		}
		poseStack.popPose();
	}

    @Override
    public boolean shouldRender(EmptyEntity entity, Frustum frustum, double camX, double camY, double camZ) {
        if (super.shouldRender(entity, frustum, camX, camY, camZ)) {
            return true;
        }

        double maxLightningDistance = 55.0;
        AABB extendedBox = entity.getBoundingBox().inflate(maxLightningDistance);

        return frustum.isVisible(extendedBox);
    }

}
