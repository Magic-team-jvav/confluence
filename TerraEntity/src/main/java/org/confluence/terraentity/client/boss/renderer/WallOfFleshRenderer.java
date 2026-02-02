package org.confluence.terraentity.client.boss.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.client.boss.model.GeoBossModel;
import org.confluence.terraentity.client.entity.renderer.GeoNormalRenderer;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFlesh;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFleshEye;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFleshMouth;
import org.confluence.terraentity.entity.boss.wallofflesh.WallOfFleshPart;
import net.minecraft.util.Tuple;
import javax.annotation.Nonnull;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import java.util.*;

public class WallOfFleshRenderer extends GeoNormalRenderer<WallOfFlesh> {

    private static final String[] VARIANT_BONES = {"bone0", "bone1", "bone2", "bone3", "bone4"};
    private static final int[] MODEL_WEIGHTS = {1, 1, 1, 1, 5};

    private static final float CELL_SIZE = 240f;
    private static final float CELL_HALF = CELL_SIZE / 2.0f;
    private final Set<GeoBone> gridBones = Collections.newSetFromMap(new IdentityHashMap<>());
    private final Map<String, Integer> cellVariantCache = new HashMap<>();
    private int cachedPartCount = -1; //记录缓存 part 数量
    private boolean modelMerged = false;

    // 肉墙裁剪距离平方
    public static final int LOD_DIST_SQ_B = 100 * 100;
    public static final int LOD_DIST_SQ_C = 200 * 200;
    public static final int LOD_DIST_SQ_D = 400 * 400;
    public static final int LOD_DIST_SQ_OFF = 1200 * 1200; //完全不渲染的距离
    public static final String LOD_SUFFIX_B = "_b";
    public static final String LOD_SUFFIX_C = "_c";
    public static final String LOD_SUFFIX_D = "_d";
    private Vec3 playerEyePos;      //存储提前计算的玩家位置

    // debug 相关
    private static final int LOG_INTERVAL = 100; // 每100帧记录一次
    private int frameCulledCount = 0;    // 当前帧剔除数
    private int frameRenderedCount = 0;  // 当前帧渲染数
    private int framesInInterval = 0; // 周期内实际经过的帧数
    private int logTimer = 0;            // 用于计时的帧计数器
    private final boolean isPrintLog = false;     // 是否打印调试日志
    private final boolean isDrawDebugBox = false; //是否绘制视锥调试框
    private long lastLogTime = System.currentTimeMillis();
    private static final Logger LOGGER = LoggerFactory.getLogger("TerraEntity-WallOfFleshRenderer");

    public WallOfFleshRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GeoBossModel<>(TEBossEntities.WALL_OF_FLESH), false, 1.0f, 0.5f);
    }

    @Override
    public void preRender(PoseStack poseStack, WallOfFlesh animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        this.entityRenderTranslations = new Matrix4f(poseStack.last().pose());
        GeoModel<WallOfFlesh> geoModel = super.getGeoModel();
        getOrBuildMergedRoot(animatable, geoModel);
        GeoBone mergedRoot = geoModel.getBone("All").orElse(null);
        if (mergedRoot != null && !model.topLevelBones().contains(mergedRoot)) {
            model.topLevelBones().clear();
            model.topLevelBones().add(mergedRoot);
        }
        scaleModelForRender(this.scaleWidth, this.scaleHeight, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    @Override
    public void render(WallOfFlesh wall, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        // 在开始渲染前，重置这一帧的计数器 用于 debug日志打印
        this.frameCulledCount = 0;
        this.frameRenderedCount = 0;
        // 提前计算玩家位置
        this.playerEyePos = Minecraft.getInstance().player.getEyePosition(partialTick);

        poseStack.pushPose();
        poseStack.pushPose();
        super.render(wall, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()
                && !wall.isInvisible()
                && !Minecraft.getInstance().showOnlyReducedInfo()
                && !wall.isRemoved()) {
            AABB aabb = wall.getOutsideBox().move(-wall.getX(), -wall.getY(), -wall.getZ());
            LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), aabb, 1, 0, 0, 1.0F);
            AABB bbaa = wall.getInsideBox().move(-wall.getX(), -wall.getY(), -wall.getZ());
            LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), bbaa, 0, 0, 1, 1.0F);
        }

        poseStack.popPose();

        printDebugLog(); //视锥渲染调试日志打印
    }

    // 打印视锥剔除统计日志 (代码稳定后可移除)
    private void printDebugLog() {
        if (!isPrintLog) {
            return;
        }

        // 每一帧增加计数
        this.framesInInterval++;
        this.logTimer++;

        if (this.logTimer >= LOG_INTERVAL) {
            long currentTime = System.currentTimeMillis();
            long timeElapsed = currentTime - lastLogTime; // 毫秒

            // 计算平均 FPS: 帧数 / 秒数
            double avgFps = framesInInterval / (timeElapsed / 1000.0);

            int total = frameCulledCount + frameRenderedCount;
            if (total > 0) {
                LOGGER.info("[肉山性能分析] 平均FPS: {} | 渲染: {} | 剔除: {} | 剔除率: {}%",
                        String.format("%.1f", avgFps),
                        frameRenderedCount,
                        frameCulledCount,
                        String.format("%.1f", (frameCulledCount / (float)total) * 100));
            }

            // 重置所有计数器
            this.logTimer = 0;
            this.framesInInterval = 0;
            this.lastLogTime = currentTime;
        }
    }
    @Override
    public void renderRecursively(PoseStack poseStack, WallOfFlesh animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                                  int packedOverlay, int colour) {

        // LOD 距离裁剪
        String name = bone.getName();
        if (name != null && isBoneFiltered(name, getDistSq(poseStack))) {
            return;
        }

        // 视锥体剔除
        Frustum frustum = Minecraft.getInstance().levelRenderer.getFrustum();
        if (isOutsideFrustum(poseStack, bone, frustum, bufferSource, animatable)) {
            return;
        }

        if ("Head_eye".equals(bone.getName())) {
            handleEyeTracking(bone, animatable, partialTick);
        }
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    /**
     * 判定当前骨骼是否应基于距离被过滤（LOD 裁剪）
     */
    private boolean isBoneFiltered(String name, double distSq) {
        // 1. 全局最大距离裁剪：如果超过最大渲染距离，直接剔除所有骨骼
        if (distSq > LOD_DIST_SQ_OFF) return true;

        // 2. 基于后缀的分级裁剪
        if (name.endsWith(LOD_SUFFIX_B)) return distSq > LOD_DIST_SQ_B;
        if (name.endsWith(LOD_SUFFIX_C)) return distSq > LOD_DIST_SQ_C;
        if (name.endsWith(LOD_SUFFIX_D)) return distSq > LOD_DIST_SQ_D;

        return false;
    }

    /**
     * 判定骨骼是否在视锥体之外
     */
    private boolean isOutsideFrustum(PoseStack poseStack, GeoBone bone, Frustum frustum, MultiBufferSource bufferSource, WallOfFlesh animatable) {
        if (!isGridBone(bone)) {
            return false;
        }

        // 1.获取骨骼在模型空间中的最终位置
        Matrix4f matrix = poseStack.last().pose();
        float modelX = - (bone.getPivotX() + bone.getPosX()) / 32f; //因为 geo lib 的原因 x 这里要取反
        float modelY = (bone.getPivotY() + bone.getPosY()) / 32f;
        float modelZ = (bone.getPivotZ() + bone.getPosZ()) / 32f;

        // 2.提取变换后的矩阵点
        Vector4f bonePos = new Vector4f(modelX, modelY, modelZ, 1.0f);
        matrix.transform(bonePos);

        float camX = bonePos.x();
        float camY = bonePos.y();
        float camZ = bonePos.z();

        // 3. 计算盒子半径
        double radius = CELL_SIZE / 32f;

        // 4. 调试绘制
        drawDebugBox(bufferSource, camX, camY, camZ, radius);

        // 5. 视锥判定
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        AABB boneAabb = new AABB(
                camX + cameraPos.x - radius, camY + cameraPos.y, camZ + cameraPos.z - radius,
                camX + cameraPos.x + radius, camY + cameraPos.y + 2 * radius, camZ + cameraPos.z + radius
        );

        boolean result = !frustum.isVisible(boneAabb);

        // 6. 地狱上层基岩剔除
        if (!result){
            result = shouldCullByHeight(boneAabb, animatable, this.playerEyePos);
        }

        // debug 日志记录
        if(isPrintLog && result){
            this.frameCulledCount++; // 记录这一帧中被剔除的一个骨骼
        }else {
            this.frameRenderedCount++; // 记录这一帧中被渲染的一个骨骼
        }

        return result;
    }

    /**
     * 地狱上基岩层优化渲染
     */
    private boolean shouldCullByHeight(AABB aabb, WallOfFlesh entity, Vec3 playerEyePos) {
        Level level = entity.level();

        // 1. 高度判定
        if (aabb.maxY < level.getMinBuildHeight()) return true;

        // 2. 地狱基岩层逻辑
        if (level.dimension() == Level.NETHER) {
            double playerY = playerEyePos.y;
            double ceilingY = 127.0;
            if (playerY > ceilingY + 0.5) {
                return aabb.maxY <= ceilingY;
            } else if (playerY < 123.0) {
                return aabb.minY >= ceilingY;
            }
        }

        return false;
    }

    /**
     * 绘制视锥剔除调试框(代码稳定后可移除)
     */
    private void drawDebugBox(MultiBufferSource bufferSource, double camX, double camY, double camZ, double radius) {
        if(!isDrawDebugBox){
            return;
        }
        // 获取专门用于绘制线条的 VertexConsumer
        VertexConsumer builder = bufferSource.getBuffer(RenderType.lines());

        // 创建一个空的 PoseStack（因为 camX/Y/Z 已经是相对于摄像机的坐标了）
        PoseStack debugStack = new PoseStack();

        // 绘制线框盒
        LevelRenderer.renderLineBox(
                debugStack, builder,
                camX - radius, camY, camZ - radius,
                camX + radius, camY + 2 * radius, camZ + radius,
                1.0f, 1.0f, 0.0f, 1.0f // 颜色：黄色 (R, G, B, A)
        );
    }

    /**
     *  眼球追踪
     */
    private void handleEyeTracking(GeoBone bone, WallOfFlesh animatable, float partialTick) {
        GeoBone parent = bone.getParent();
        if (parent == null) return;

        for (WallOfFleshPart part : animatable.subEntities) {
            if (part instanceof WallOfFleshEye eyePart && parent.getName().equals(part.name)) {
                adjustEyePose(bone, eyePart, animatable, partialTick);
                break;
            }
        }
    }

    /**
     * 获取骨骼距离摄像机距离平方
     */
    private double getDistSq(PoseStack poseStack) {
        // 1. 从当前的矩阵栈中提取变换矩阵
        // Matrix4f 包含了当前骨骼的所有平移、旋转和缩放信息
        Matrix4f matrix = poseStack.last().pose();

        // 2. 提取平移分量 (m30, m31, m32)
        float x = matrix.m30();
        float y = matrix.m31();
        float z = matrix.m32();

        // 3. 计算该骨骼距离摄像机的平方距离
        return x * x + y * y + z * z;
    }

    private void adjustEyePose(GeoBone bone, WallOfFleshEye eyePart, WallOfFlesh parentMob, float partialTick) {

        if (parentMob == null || !parentMob.isAlive() || parentMob.deathTime > 0) {
            return;
        }

        LivingEntity target = eyePart.target;
        if (target != null && target.isAlive() && !target.isRemoved()) {
            Vec3 targetEyePos = new Vec3(target.xo, target.yo, target.zo)
                    .lerp(target.position(), partialTick)
                    .add(0.0, target.getEyeHeight(), 0.0);
            Vec3 eyePos = new Vec3(eyePart.xo, eyePart.yo, eyePart.zo)
                    .lerp(eyePart.position(), partialTick)
                    .add(0.0, eyePart.getBbHeight() * 0.5, 0.0);
            Vec3 dist = targetEyePos.subtract(eyePos);

            Vec3 forward = parentMob.getForward().normalize();
            Vec3 toTargetHorizontal = new Vec3(dist.x, 0, dist.z);
            double hLenSqr = toTargetHorizontal.lengthSqr();

            if (hLenSqr > 1.0E-6 && forward.dot(toTargetHorizontal.normalize()) >= 0.0) {
                double horizontalDist = Math.sqrt(dist.x * dist.x + dist.z * dist.z);
                float pitch = (float) Math.atan2(dist.y, horizontalDist);

                float yawOffset = (float) Math.atan2(
                        forward.x * dist.z - forward.z * dist.x,
                        forward.x * dist.x + forward.z * dist.z
                );

                eyePart.stareYaw = -yawOffset;
                eyePart.starePitch = pitch;
            }
        } else {
            float backSpeed = 0.15f;
            eyePart.stareYaw = Mth.lerp(backSpeed, eyePart.stareYaw, 0.0f);
            eyePart.starePitch = Mth.lerp(backSpeed, eyePart.starePitch, 0.0f);
        }

        float lerpYaw = eyePart.lerpYaw(partialTick);
        float lerpPitch = eyePart.lerpPitch(partialTick);

        float clampedYaw = Mth.clamp(lerpYaw, (float) Math.toRadians(-60), (float) Math.toRadians(60));
        float clampedPitch = Mth.clamp(lerpPitch, (float) Math.toRadians(-45), (float) Math.toRadians(45));

        bone.setRotY(clampedYaw);
        bone.setRotX(clampedPitch);
    }

    private int pickVariantIndex(WallOfFlesh wall, int x, int y) {
        String key = wall.getId() + ":" + x + ":" + y;
        Integer cached = cellVariantCache.get(key);
        if (cached != null) {
            return cached;
        }
        int totalWeight = 0;
        for (int weight : MODEL_WEIGHTS) {
            totalWeight += weight;
        }
        int randomWeight = wall.getRandom().nextInt(Math.max(totalWeight, 1));
        int selectedIndex = 0;
        int cumulativeWeight = MODEL_WEIGHTS[0];

        while (randomWeight >= cumulativeWeight && selectedIndex < MODEL_WEIGHTS.length - 1) {
            selectedIndex++;
            cumulativeWeight += MODEL_WEIGHTS[selectedIndex];
        }
        cellVariantCache.put(key, selectedIndex);
        return selectedIndex;
    }

    private void getOrBuildMergedRoot(WallOfFlesh animatable, GeoModel<WallOfFlesh> model) {
        // 检查缓存是否有效
        List<Tuple<Integer, Vec3>> localOffsets = animatable.getLocalOffsets();

        // 数据未同步则跳过
        if (localOffsets.isEmpty()||animatable.subEntities.isEmpty()) return;

        // 数量未变化则不重构
        int currentOffsetSize = localOffsets.size();
        if (modelMerged && cachedPartCount == currentOffsetSize) {
            return;
        }

        GeoBone baseRoot = model.getBone("All").orElse(null);
        if (baseRoot == null) {
            return;
        }

        // 清理旧骨骼
        baseRoot.getChildBones().clear();
        gridBones.clear();

        GeoBone[] variants = new GeoBone[VARIANT_BONES.length];
        int validVariantCount = 0;
        for (int i = 0; i < VARIANT_BONES.length; i++) {
            variants[i] = model.getBone(VARIANT_BONES[i]).orElse(null);
            if (variants[i] != null) {
                validVariantCount++;
            }
        }
        GeoBone eyeBone = model.getBone("bone_eye").orElse(null);
        GeoBone mouthBone = model.getBone("bone_mouth").orElse(null);

        final float geckoScale = 16.0f;
        final float spacing = animatable.gridSpacing;
        final double halfSpacingScale = spacing * geckoScale;

        final int collisionWidth = Mth.floor(300 / spacing); // 150*2
        final int collisionHeight = Mth.floor(150 / spacing);

        int gridX = animatable.getGridSizeX() + collisionWidth;
        int gridY = animatable.getGridSizeY() + collisionHeight;

        double gridXHalf = gridX / 2.0;
        double gridYHalf = gridY / 2.0;

        if (validVariantCount > 0) {
            // 绘制背景墙网格
            List<GeoBone> baseRootChildBones = baseRoot.getChildBones();
            for (int ix = 0; ix < gridX; ix++) {
                final double lxBase = (ix - gridXHalf) * halfSpacingScale;

                for (int iy = 0; iy < gridY; iy++) {
                    int vIdx = pickVariantIndex(animatable, ix, iy);

                    if (vIdx >= 0 && vIdx < VARIANT_BONES.length) {
                        GeoBone variant = variants[vIdx];
                        if (variant != null) {
                            // 对齐中心点
                            double ly = (iy - gridYHalf) * halfSpacingScale;

                            // X轴取反 和 缩放
                            Vec3 renderPos = new Vec3(-lxBase, ly, 0);
                            String boneName = "bone" + ix + "_" + iy;
                            GeoBone gridBone = copyBone(variant, renderPos, boneName, baseRoot);
                            baseRootChildBones.add(gridBone);
                            gridBones.add(gridBone);
                        }
                    }
                }
            }
        }

        // 直接遍历 localOffsets，确保位置与部件对应
        List<WallOfFleshPart> subEntities = animatable.subEntities;
        int subEntitySize = subEntities.size();

        List<GeoBone> baseRootChildBones = baseRoot.getChildBones();

        boolean hasEyeBone = eyeBone != null;
        boolean hasMouthBone = mouthBone != null;

        for (Tuple<Integer, Vec3> entry : localOffsets) {
            int partIndex = entry.getA();

            if (partIndex < 0 || partIndex >= subEntitySize) {
                continue;
            }

            Vec3 rawOffset = entry.getB();
            WallOfFleshPart part = subEntities.get(partIndex);

            if (part == null || !part.isAlive()) {
                continue;
            }

            // X轴取反 和 缩放
            Vec3 renderOffset = new Vec3(-rawOffset.x * geckoScale, rawOffset.y * geckoScale, rawOffset.z * geckoScale);

            if (hasEyeBone && part instanceof WallOfFleshEye) {
                baseRootChildBones.add(copyBone(eyeBone, renderOffset, part.name, baseRoot));
            } else if (hasMouthBone && part instanceof WallOfFleshMouth) {
                baseRootChildBones.add(copyBone(mouthBone, renderOffset, part.name, baseRoot));
            }
        }

        this.cachedPartCount = currentOffsetSize;
        modelMerged = true;
    }
    /**
     * geckolib 1.21.1 的构造器手动拷贝骨骼（递归拷贝子骨骼），使用指定的新名称。
     */
    private GeoBone copyBone(GeoBone template, Vec3 offset, String newName, GeoBone parent) {
        GeoBone copy = new GeoBone(parent,
                newName,
                template.getMirror(),
                template.getInflate(),
                template.shouldNeverRender(),
                template.getReset());

        // 只有传入了有效 offset 的顶层骨骼（嘴巴根部）才使用 offset
        // 递归产生的子骨骼（牙齿等）必须保留 template 原始的 Pos 和 Pivot
        if (offset != Vec3.ZERO) {
            copy.setPosX((float) offset.x);
            copy.setPosY((float) offset.y);
            copy.setPosZ((float) offset.z);
            copy.setPivotX((float) offset.x);
            copy.setPivotY((float) offset.y);
            copy.setPivotZ((float) offset.z);
        } else {
            // 保留子骨骼在 Blockbench 里定义的相对位置
            copy.setPosX(template.getPosX());
            copy.setPosY(template.getPosY());
            copy.setPosZ(template.getPosZ());
            copy.setPivotX(template.getPivotX());
            copy.setPivotY(template.getPivotY());
            copy.setPivotZ(template.getPivotZ());
        }

        copy.setRotX(template.getRotX());
        copy.setRotY(template.getRotY());
        copy.setRotZ(template.getRotZ());
        copy.updateScale(template.getScaleX(), template.getScaleY(), template.getScaleZ());

        copy.getCubes().addAll(template.getCubes());

        if (!template.getChildBones().isEmpty()) {
            for (GeoBone child : template.getChildBones()) {
                // 递归时保持 Vec3.ZERO，这样子骨骼就会走上面的 else 分支，保留原始坐标
                GeoBone childCopy = copyBone(child, Vec3.ZERO, child.getName(), copy);
                copy.getChildBones().add(childCopy);
            }
        }

        return copy;
    }

    private boolean isGridBone(GeoBone bone) {
        return gridBones.contains(bone);
    }

    @Override
    public GeoModel<WallOfFlesh> getGeoModel() {
        return super.getGeoModel();
    }

    @Override
    public boolean shouldRender(@Nonnull WallOfFlesh wall, @Nonnull Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    protected void applyRotations(WallOfFlesh animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        if (isShaking(animatable))
            rotationYaw += (float)(Math.cos(animatable.tickCount * 3.25d) * Math.PI * 0.4d);

        if (!animatable.hasPose(Pose.SLEEPING))
            poseStack.mulPose(Axis.YP.rotationDegrees(180f - rotationYaw));

        if (animatable.deathTime <= 0 && animatable.isAutoSpinAttack()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(-90f - animatable.getXRot()));
            poseStack.mulPose(Axis.YP.rotationDegrees((animatable.tickCount + partialTick) * -75f));
        }
    }

    @Override
    protected int getSkyLightLevel(@Nonnull WallOfFlesh entity, @Nonnull BlockPos pos) {
        Vec3 potionPos = new Vec3(pos.getX(), entity.level().getMaxBuildHeight()+1, pos.getZ());
        return super.getSkyLightLevel(entity, BlockPos.containing(potionPos));
    }

    @Override
    protected int getBlockLightLevel(@Nonnull WallOfFlesh entity, @Nonnull BlockPos pos) {
        Vec3 potionPos = new Vec3(pos.getX(), entity.level().getMaxBuildHeight()+1, pos.getZ());
        return super.getBlockLightLevel(entity, BlockPos.containing(potionPos));
    }

}
