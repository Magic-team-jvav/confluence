package org.confluence.mod.client.summon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.core.BlockPos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.client.model.entity.summon.TerraprismaModel;
import org.confluence.mod.common.summon.SummonAnimation;
import org.confluence.mod.network.s2c.SummonSyncPacketS2C;
import org.mesdag.portlib.event.client.PortRenderLevelStageEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 保存服务端同步的召唤物状态，并在世界渲染阶段绘制对应的客户端表现。
 * 召唤物逻辑仍由服务端驱动；客户端只负责插值、拖尾、待机编队和模型动画。
 * 跟随玩家的召唤剑会直接使用玩家的渲染插值坐标，避免服务端同步间隔导致奔跑时模型和玩家拉开。
 */
public final class ClientSummonManager {
    private static final ResourceLocation TERRAPRISMA = Confluence.asResource("terraprisma");
    private static final ResourceLocation TERRAPRISMA_TEXTURE = Confluence.asResource("textures/entity/model/terraprisma_gray.png");
    private static final float BACK_SWORD_TILT = 25.0F;
    private static final float BACK_SWORD_SWING_DEGREES = 4.0F;
    private static final float TERRAPRISMA_BACK_TILT = 25.0F;
    private static final float TERRAPRISMA_BACK_SWING_DEGREES = 7.0F;
    private static final Map<UUID, State> STATES = new HashMap<>();
    private static final Map<UUID, ClientSummonVisual> GEO_VISUALS = new HashMap<>();
    private static final Map<UUID, ClientStardustDragonVisual> STARDUST_DRAGON_VISUALS = new HashMap<>();
    private static final Map<ResourceLocation, ClientSummonGeoRenderer> GEO_RENDERERS = new HashMap<>();
    private static final ClientStardustDragonRenderer STARDUST_DRAGON_RENDERER = new ClientStardustDragonRenderer();
    private static TerraprismaModel terraprismaModel;
    private static ClientIronGolemRenderer ironGolemRenderer;

    private ClientSummonManager() {}

    public static void accept(UUID ownerId, List<SummonSyncPacketS2C.Entry> entries) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            STATES.clear();
            GEO_VISUALS.clear();
            STARDUST_DRAGON_VISUALS.clear();
            return;
        }
        Set<UUID> retained = new HashSet<>();
        for (SummonSyncPacketS2C.Entry entry : entries) {
            retained.add(entry.id());
            STATES.compute(entry.id(), (id, state) -> state == null
                    ? new State(ownerId, entry, level.getGameTime())
                    : state.update(ownerId, entry, level.getGameTime()));
        }
        STATES.entrySet().removeIf(state -> state.getValue().ownerId.equals(ownerId) && !retained.contains(state.getKey()));
        GEO_VISUALS.keySet().removeIf(id -> !STATES.containsKey(id));
        STARDUST_DRAGON_VISUALS.keySet().removeIf(id -> !STATES.containsKey(id));
    }

    public static void render(PortRenderLevelStageEvent event) {
        if (event.getStage() != PortRenderLevelStageEvent.Stage.AFTER_PARTICLES || STATES.isEmpty()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            STATES.clear();
            GEO_VISUALS.clear();
            STARDUST_DRAGON_VISUALS.clear();
            return;
        }
        STATES.entrySet().removeIf(entry -> level.getGameTime() - entry.getValue().lastUpdate > 5L);
        GEO_VISUALS.keySet().removeIf(id -> !STATES.containsKey(id));
        STARDUST_DRAGON_VISUALS.keySet().removeIf(id -> !STATES.containsKey(id));
        if (STATES.isEmpty()) {
            return;
        }
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        RenderType terraprismaRenderType = RenderType.energySwirl(TERRAPRISMA_TEXTURE, 0.0F, 0.0F);
        for (State state : STATES.values()) {
            if (state.current.type().equals(TERRAPRISMA)) {
                renderTerraprisma(state, event, buffers, terraprismaRenderType);
                if (!state.current.followingOwner())
                    renderTrail(state, event, buffers, 0.25F, state.rgb());
            } else if (isSummonSword(state.current.type())) {
                renderSummonSword(state, event, buffers);
                if (!state.current.followingOwner())
                    renderTrail(state, event, buffers, 0.15F, summonSwordColor(state.current.type()));
            } else if (state.current.type().equals(Confluence.asResource("stardust_dragon"))) {
                renderStardustDragonPart(state, event, buffers);
            } else if (usesGeoVisual(state.current.type())) {
                renderGeoVisual(state, event, buffers);
            } else if (state.current.type().equals(Confluence.asResource("i_32_iron_golem"))) {
                renderIronGolemVisual(state, event, buffers);
            }
        }
        buffers.endBatch(terraprismaRenderType);
        buffers.endBatch(RenderStateShardAccessor.TRAIL_RENDER_TYPE);
    }

    private static void renderGeoVisual(State state, PortRenderLevelStageEvent event,
                                        MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 position = state.interpolatedPosition(partialTick);
        ClientSummonVisual visual = GEO_VISUALS.computeIfAbsent(state.current.id(), id ->
                new ClientSummonVisual(id, state.current.type()));
        visual.update(state.current.animation(), state.current.position().distanceToSqr(state.previous.position()) > 1.0E-5,
                state.lastUpdate + partialTick);
        Vec3 camera = event.getCamera().getPosition();
        int packedLight = LevelRenderer.getLightColor(level, BlockPos.containing(position));
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(position.x - camera.x, position.y - camera.y, position.z - camera.z);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.interpolatedYaw(partialTick)));
        poseStack.mulPose(Axis.XP.rotationDegrees(state.interpolatedPitch(partialTick)));
        poseStack.scale(state.current.scale(), state.current.scaleY(), state.current.scale());
        GEO_RENDERERS.computeIfAbsent(state.current.type(), ClientSummonGeoRenderer::new)
                .render(poseStack, visual, bufferSource, null, null, packedLight);
        poseStack.popPose();
    }

    private static boolean usesGeoVisual(ResourceLocation type) {
        return type.getNamespace().equals(Confluence.MODID) && switch (type.getPath()) {
            case "finch_baby", "slime_baby", "hornet_baby", "sculk_wisp", "summon_imp",
                 "summon_snow_flinx" -> true;
            default -> false;
        };
    }

    private static void renderIronGolemVisual(State state, PortRenderLevelStageEvent event,
                                              MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 position = state.interpolatedPosition(partialTick);
        Vec3 camera = event.getCamera().getPosition();
        int packedLight = LevelRenderer.getLightColor(level, BlockPos.containing(position));
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(position.x - camera.x, position.y - camera.y, position.z - camera.z);
        ironGolemRenderer(minecraft.getEntityModels()).render(poseStack, bufferSource, packedLight,
                state.interpolatedYaw(partialTick), state.interpolatedPitch(partialTick),
                state.walkAnimation.position(partialTick), state.walkAnimation.speed(partialTick),
                state.current.animation() == SummonAnimation.MELEE_ATTACK
                        ? Math.max(0, state.current.animationDuration() - state.current.animationTicks()) : 0,
                partialTick);
        poseStack.popPose();
    }

    private static void renderSummonSword(State state, PortRenderLevelStageEvent event,
                                          MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        Player owner = minecraft.level.getPlayerByUUID(state.ownerId);
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 position = state.interpolatedPosition(partialTick);
        if (state.current.followingOwner() && owner != null) {
            position = summonSwordBackAnchor(owner, state.current.order(), partialTick, state.idlePhase(partialTick));
        }
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        Vec3 camera = event.getCamera().getPosition();
        poseStack.translate(position.x - camera.x, position.y - camera.y, position.z - camera.z);
        if (state.current.followingOwner() && owner != null) {
            applySummonSwordBackPose(poseStack, state, owner, partialTick);
        } else {
            poseStack.mulPose(Axis.YN.rotationDegrees(state.interpolatedYaw(partialTick) - 90.0F));
            poseStack.mulPose(Axis.ZN.rotationDegrees(-state.interpolatedPitch(partialTick)));
            poseStack.mulPose(Axis.XN.rotationDegrees(state.interpolatedRoll(partialTick)));
            poseStack.mulPose(Axis.ZN.rotationDegrees(-45.0F));
        }
        if (!state.current.followingOwner()) {
            applyAnimation(state, partialTick, poseStack);
        }
        int packedLight = LevelRenderer.getLightColor(minecraft.level, BlockPos.containing(position));
        minecraft.getItemRenderer().renderStatic(swordItem(state.current.type()), ItemDisplayContext.FIXED,
                packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, minecraft.level,
                state.current.id().hashCode());
        poseStack.popPose();
    }

    private static boolean isSummonSword(ResourceLocation type) {
        return type.getNamespace().equals(Confluence.MODID) && switch (type.getPath()) {
            case "summon_wooden_sword", "summon_stone_sword", "summon_iron_sword",
                 "summon_golden_sword",
                 "summon_diamond_sword", "summon_netherite_sword" -> true;
            default -> false;
        };
    }

    private static void renderStardustDragonPart(State state, PortRenderLevelStageEvent event,
                                                 MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }
        int lastPart = lastStardustDragonPart(state);
        ClientStardustDragonVisual.Part part = state.current.order() == 0
                ? ClientStardustDragonVisual.Part.HEAD
                : state.current.order() == lastPart
                ? ClientStardustDragonVisual.Part.TAIL : ClientStardustDragonVisual.Part.BODY;
        ClientStardustDragonVisual visual = STARDUST_DRAGON_VISUALS.computeIfAbsent(state.current.id(),
                id -> new ClientStardustDragonVisual(id, part));
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        visual.update(part, state.lastUpdate + partialTick);
        Vec3 position = state.interpolatedPosition(partialTick);
        Vec3 camera = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(position.x - camera.x, position.y - camera.y, position.z - camera.z);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - state.interpolatedYaw(partialTick)));
        poseStack.mulPose(Axis.XP.rotationDegrees(state.interpolatedPitch(partialTick)));
        poseStack.scale(state.current.scale(), state.current.scaleY(), state.current.scale());
        int packedLight = LevelRenderer.getLightColor(level, BlockPos.containing(position));
        STARDUST_DRAGON_RENDERER.render(poseStack, visual, bufferSource, null, null, packedLight);
        poseStack.popPose();
    }

    private static int lastStardustDragonPart(State state) {
        UUID headId = stardustDragonHeadId(state.current.id(), state.current.order());
        return STATES.values().stream().filter(candidate -> candidate.ownerId.equals(state.ownerId)
                        && candidate.current.type().equals(state.current.type())
                        && stardustDragonHeadId(candidate.current.id(), candidate.current.order()).equals(headId))
                .mapToInt(candidate -> candidate.current.order()).max().orElse(0);
    }

    private static UUID stardustDragonHeadId(UUID partId, int order) {
        return order == 0 ? partId : new UUID(partId.getMostSignificantBits(), partId.getLeastSignificantBits() ^ order);
    }

    private static ItemStack swordItem(ResourceLocation type) {
        return new ItemStack(switch (type.getPath()) {
            case "summon_wooden_sword" -> Items.WOODEN_SWORD;
            case "summon_stone_sword" -> Items.STONE_SWORD;
            case "summon_iron_sword" -> Items.IRON_SWORD;
            case "summon_golden_sword" -> Items.GOLDEN_SWORD;
            case "summon_diamond_sword" -> Items.DIAMOND_SWORD;
            case "summon_netherite_sword" -> Items.NETHERITE_SWORD;
            default -> throw new IllegalArgumentException("Unknown summon sword type: " + type);
        });
    }

    private static int summonSwordColor(ResourceLocation type) {
        return switch (type.getPath()) {
            case "summon_wooden_sword" -> 0x714C11;
            case "summon_stone_sword" -> 0x8E9797;
            case "summon_iron_sword" -> 0xE6F0F3;
            case "summon_golden_sword" -> 0xE3D529;
            case "summon_diamond_sword" -> 0x17CFC1;
            case "summon_netherite_sword" -> 0x8136D2;
            default -> throw new IllegalArgumentException("Unknown summon sword type: " + type);
        };
    }

    private static void renderTerraprisma(State state, PortRenderLevelStageEvent event,
                                          MultiBufferSource bufferSource, RenderType renderType) {
        Minecraft minecraft = Minecraft.getInstance();
        Player owner = minecraft.level == null ? null : minecraft.level.getPlayerByUUID(state.ownerId);
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 position = state.interpolatedPosition(partialTick);
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        if (state.current.followingOwner() && owner != null) {
            position = terraprismaBackAnchor(owner, state.current.order(), partialTick, state.idlePhase(partialTick));
        }
        Vec3 camera = event.getCamera().getPosition();
        poseStack.translate(position.x - camera.x, position.y - camera.y, position.z - camera.z);
        if (state.current.followingOwner() && owner != null) {
            applyTerraprismaBackPose(poseStack, state, owner, partialTick);
        } else {
            poseStack.mulPose(Axis.YN.rotationDegrees(state.interpolatedYaw(partialTick)));
            poseStack.mulPose(Axis.XN.rotationDegrees(-state.interpolatedPitch(partialTick) + 180.0F));
            poseStack.mulPose(Axis.ZN.rotationDegrees(state.interpolatedRoll(partialTick)));
        }
        if (!state.current.followingOwner()) {
            applyAnimation(state, partialTick, poseStack);
        }
        poseStack.scale(state.current.scale(), state.current.scaleY(), state.current.scale());
        int rgb = state.rgb();
        int packedLight = minecraft.level == null ? LightTexture.FULL_BRIGHT
                : LevelRenderer.getLightColor(minecraft.level, BlockPos.containing(position));
        float red = (rgb >> 16 & 255) / 255.0F;
        float green = (rgb >> 8 & 255) / 255.0F;
        float blue = (rgb & 255) / 255.0F;
        model(minecraft.getEntityModels()).renderToBuffer(poseStack, bufferSource.getBuffer(renderType),
                packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
        poseStack.popPose();
    }

    /**
     * 将本地长剑模型固定到玩家背部平面。
     * 模型本地 Z 轴是剑身方向，X 轴是剑面法线；这里让剑面法线贴住玩家背部方向，
     * 再把剑身摆成“剑柄在右上、剑刃在左下”，避免沿用追踪目标时的俯仰角。
     */
    private static void applySummonSwordBackPose(PoseStack poseStack, State state, Player owner, float partialTick) {
        applyBackSwordPose(poseStack, state, owner, partialTick, BACK_SWORD_TILT, BACK_SWORD_SWING_DEGREES, 15.0F);
    }

    private static Vec3 summonSwordBackAnchor(Player owner, int order, float partialTick, float idlePhase) {
        int sequence = order + 1;
        float bodyYaw = Mth.rotLerp(partialTick, owner.yBodyRotO, owner.yBodyRot);
        Vec3 forward = Vec3.directionFromRotation(0.0F, bodyYaw).multiply(1.0, 0.0, 1.0).normalize();
        Vec3 right = forward.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
        Vec3 ownerPosition = new Vec3(Mth.lerp(partialTick, owner.xo, owner.getX()),
                Mth.lerp(partialTick, owner.yo, owner.getY()), Mth.lerp(partialTick, owner.zo, owner.getZ()));
        double backDistance = Math.max(0.22, 0.5F - 0.04F * (sequence - 1));
        return ownerPosition.subtract(forward.scale(backDistance))
                .add(0.0, 1.0 + Mth.cos(idlePhase * 0.8F) * 0.04, 0.0)
                .add(right.scale(0.24F * (sequence / 2) * ((sequence & 1) == 0 ? 1.0F : -1.0F)
                        + Mth.sin(idlePhase) * 0.07F));
    }

    private static Vec3 terraprismaBackAnchor(Player owner, int order, float partialTick, float idlePhase) {
        int sequence = order + 1;
        float bodyYaw = Mth.rotLerp(partialTick, owner.yBodyRotO, owner.yBodyRot);
        Vec3 forward = Vec3.directionFromRotation(0.0F, bodyYaw).multiply(1.0, 0.0, 1.0).normalize();
        Vec3 right = forward.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
        Vec3 ownerPosition = new Vec3(Mth.lerp(partialTick, owner.xo, owner.getX()),
                Mth.lerp(partialTick, owner.yo, owner.getY()), Mth.lerp(partialTick, owner.zo, owner.getZ()));
        int layer = sequence / 2;
        float side = (sequence & 1) == 0 ? 1.0F : -1.0F;
        double backDistance = Math.max(0.16, 0.24F - 0.012F * (sequence - 1));
        return ownerPosition.subtract(forward.scale(backDistance))
                .add(0.0, 1.0 + layer * 0.08F + Mth.cos(idlePhase * 0.8F) * 0.035F, 0.0)
                .add(right.scale(0.32F * layer * side + Mth.sin(idlePhase) * 0.12F));
    }

    /**
     * 将泰拉棱镜贴到玩家背部平面。
     * 这里复用召唤剑的背负姿态，保证剑柄位于右上、剑刃位于左下；动态染色仍然由模型渲染阶段处理。
     */
    private static void applyTerraprismaBackPose(PoseStack poseStack, State state, Player owner, float partialTick) {
        applyBackSwordPose(poseStack, state, owner, partialTick, TERRAPRISMA_BACK_TILT, TERRAPRISMA_BACK_SWING_DEGREES, 12.0F);
    }

    private static void applyBackSwordPose(PoseStack poseStack, State state, Player owner, float partialTick, float baseTilt, float swingDegrees, float stackSpread) {
        int sequence = state.current.order() + 1;
        int layer = sequence / 2;
        float side = (sequence & 1) == 0 ? -1.0F : 1.0F;
        float bodyYaw = Mth.rotLerp(partialTick, owner.yBodyRotO, owner.yBodyRot);
        Vec3 forward = Vec3.directionFromRotation(0.0F, bodyYaw).multiply(1.0, 0.0, 1.0).normalize();
        Vec3 right = forward.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
        float tilt = baseTilt + layer * side * stackSpread + Mth.sin(state.idlePhase(partialTick)) * swingDegrees;
        double radians = tilt * Mth.DEG_TO_RAD;
        Vector3f localX = new Vector3f((float) -forward.x, 0.0F, (float) -forward.z).normalize();
        Vector3f localZ = new Vector3f((float) (right.x * Math.sin(radians)), (float) Math.cos(radians),
                (float) (right.z * Math.sin(radians))).normalize();
        Vector3f localY = new Vector3f(localZ).cross(localX).normalize();
        poseStack.mulPose(new Quaternionf().setFromNormalized(new Matrix3f(
                localX.x, localY.x, localZ.x,
                localX.y, localY.y, localZ.y,
                localX.z, localY.z, localZ.z)));
    }

    private static void renderTrail(State state, PortRenderLevelStageEvent event, MultiBufferSource bufferSource,
                                    float width, int rgb) {
        if (state.trailSamples.size() < 2) {
            return;
        }
        Vec3 camera = event.getCamera().getPosition();
        VertexConsumer consumer = bufferSource.getBuffer(RenderStateShardAccessor.TRAIL_RENDER_TYPE);
        Matrix4f matrix = event.getPoseStack().last().pose();
        TrailSample previous = null;
        Vec3 previousLeft = null;
        Vec3 previousRight = null;
        int sampleCount = state.trailSamples.size();
        int index = 0;
        for (TrailSample current : state.trailSamples) {
            if (previous == null) {
                previous = current;
                continue;
            }
            float progress = index++ / (float) sampleCount * 0.6F + 0.4F;
            Vec3 side = trailSide(current).scale(width * progress);
            Vec3 previousPosition = previous.position.subtract(camera);
            Vec3 currentPosition = current.position.subtract(camera);
            Vec3 currentLeft = currentPosition.add(side);
            Vec3 currentRight = currentPosition.subtract(side);
            Vec3 startLeft = previousLeft == null ? previousPosition.add(side) : previousLeft;
            Vec3 startRight = previousRight == null ? previousPosition.subtract(side) : previousRight;
            int alpha = current == state.trailSamples.peekLast() ? 20 : Mth.floor(200.0F * progress);
            int color = alpha << 24 | rgb;
            vertex(consumer, matrix, startLeft, color);
            vertex(consumer, matrix, startRight, color & 0x00FFFFFF);
            vertex(consumer, matrix, currentRight, color & 0x00FFFFFF);
            vertex(consumer, matrix, currentLeft, color);
            previous = current;
            previousLeft = currentLeft;
            previousRight = currentRight;
        }
    }

    private static Vec3 trailSide(TrailSample sample) {
        Quaternionf rotation = new Quaternionf().rotateY(-sample.yaw * Mth.DEG_TO_RAD)
                .rotateX(sample.pitch * Mth.DEG_TO_RAD).rotateZ(sample.roll * Mth.DEG_TO_RAD);
        Vector3f normal = rotation.transform(new Vector3f(0.0F, 1.0F, 0.0F));
        return normal.lengthSquared() < 1.0E-8F ? new Vec3(0.0, 1.0, 0.0)
                : new Vec3(normal.x, normal.y, normal.z).normalize();
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, Vec3 point, int argb) {
        consumer.vertex(matrix, (float) point.x, (float) point.y, (float) point.z).color(argb).endVertex();
    }

    private static void applyAnimation(State state, float partialTick, PoseStack poseStack) {
        int duration = state.current.animationDuration();
        if (duration <= 0) {
            return;
        }
        float progress = Mth.clamp((state.current.animationTicks() + partialTick) / duration, 0.0F, 1.0F);
        float degrees = state.current.animationDegrees() * progress;
        switch (state.current.animation()) {
            case SPIN_X -> poseStack.mulPose(Axis.XN.rotationDegrees(degrees));
            case ROTATE_Z -> poseStack.mulPose(Axis.ZN.rotationDegrees(degrees));
            case SPIN_Y -> {
                poseStack.mulPose(Axis.YN.rotationDegrees(degrees));
                float tilt = progress < 1.0F / 6.0F ? progress * 6.0F * 90.0F
                        : progress > 5.0F / 6.0F ? (1.0F - progress) * 6.0F * 90.0F : 90.0F;
                poseStack.mulPose(Axis.ZN.rotationDegrees(tilt));
            }
            default -> {
            }
        }
    }

    private static TerraprismaModel model(EntityModelSet models) {
        if (terraprismaModel == null) {
            terraprismaModel = new TerraprismaModel(models.bakeLayer(TerraprismaModel.LAYER_LOCATION));
        }
        return terraprismaModel;
    }

    private static ClientIronGolemRenderer ironGolemRenderer(EntityModelSet models) {
        if (ironGolemRenderer == null) ironGolemRenderer = new ClientIronGolemRenderer(models);
        return ironGolemRenderer;
    }

    private static final class State {
        private final UUID ownerId;
        private final RandomSource random;
        private SummonSyncPacketS2C.Entry previous;
        private SummonSyncPacketS2C.Entry current;
        private long lastUpdate;
        private float colorProgress;
        private float sliderProgress;
        private final WalkAnimationState walkAnimation = new WalkAnimationState();
        private final Deque<TrailSample> trailSamples = new ArrayDeque<>(8);

        private State(UUID ownerId, SummonSyncPacketS2C.Entry entry, long lastUpdate) {
            this.ownerId = ownerId;
            this.previous = entry;
            this.current = entry;
            this.lastUpdate = lastUpdate;
            this.random = RandomSource.create(entry.id().getMostSignificantBits() ^ entry.id().getLeastSignificantBits());
            this.colorProgress = random.nextFloat();
            updateTrail(entry);
        }

        private State update(UUID ownerId, SummonSyncPacketS2C.Entry entry, long lastUpdate) {
            if (!this.ownerId.equals(ownerId)) {
                throw new IllegalStateException("Summon owner changed without replacing its runtime id");
            }
            previous = current;
            current = entry;
            this.lastUpdate = lastUpdate;
            walkAnimation.update((float) Math.min(1.0, current.position().subtract(previous.position()).horizontalDistance() * 4.0), 0.4F);
            float change = (random.nextFloat() - 0.5F) * 0.05F;
            colorProgress = Mth.clamp(colorProgress + change + sliderProgress, 0.0F, 1.0F);
            if (colorProgress >= 1.0F) {
                sliderProgress = -0.01F;
            } else if (colorProgress <= 0.0F) {
                sliderProgress = 0.01F;
            }
            updateTrail(entry);
            return this;
        }

        private void updateTrail(SummonSyncPacketS2C.Entry entry) {
            if (entry.followingOwner()) {
                trailSamples.clear();
                return;
            }
            trailSamples.addLast(new TrailSample(entry.position(), entry.yaw(), entry.pitch(), entry.roll()));
            while (trailSamples.size() > 8) {
                trailSamples.removeFirst();
            }
        }

        private Vec3 interpolatedPosition(float partialTick) {return previous.position().lerp(current.position(), partialTick);}

        private float interpolatedYaw(float partialTick) {return Mth.rotLerp(partialTick, previous.yaw(), current.yaw());}

        private float interpolatedPitch(float partialTick) {return Mth.rotLerp(partialTick, previous.pitch(), current.pitch());}

        private float interpolatedRoll(float partialTick) {return Mth.rotLerp(partialTick, previous.roll(), current.roll());}

        private float idlePhase(float partialTick) {return (lastUpdate + partialTick) * 0.075F + current.order() * 0.65F;}

        private int rgb() {
            int from = 0x1FE6C0;
            int to = 0xC67C28;
            int red = (int) ((from >> 16 & 255) + ((to >> 16 & 255) - (from >> 16 & 255)) * colorProgress);
            int green = (int) ((from >> 8 & 255) + ((to >> 8 & 255) - (from >> 8 & 255)) * colorProgress);
            int blue = (int) ((from & 255) + ((to & 255) - (from & 255)) * colorProgress);
            return red << 16 | green << 8 | blue;
        }
    }

    private record TrailSample(Vec3 position, float yaw, float pitch, float roll) {}
}
