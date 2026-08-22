package org.confluence.mod.client.summon;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.client.model.entity.projectile.HornetStingerProjectileModel;
import org.confluence.mod.client.model.entity.summon.TerraprismaModel;
import org.confluence.mod.common.summon.SummonAnimation;
import org.confluence.mod.common.summon.projectile.SummonProjectileTypes;
import org.confluence.mod.network.s2c.SummonSyncPacketS2C;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.mesdag.portlib.event.client.PortRenderLevelStageEvent;
import org.mesdag.portlib.event.client.PortModelEvent;

import java.util.*;

/// 保存服务端同步的召唤物状态，并在世界渲染阶段绘制对应的客户端表现。
/// 召唤物逻辑仍由服务端驱动；客户端只负责插值、拖尾、待机编队和模型动画。
public final class ClientSummonManager {
    private static final ResourceLocation TERRAPRISMA = Confluence.asResource("terraprisma");
    private static final ResourceLocation STARDUST_DRAGON = Confluence.asResource("stardust_dragon");
    private static final ResourceLocation IRON_GOLEM = Confluence.asResource("i_32_iron_golem");
    private static final ResourceLocation TERRAPRISMA_TEXTURE = Confluence.asResource("textures/entity/model/terraprisma_gray.png");
    private static final ResourceLocation HORNET_STINGER = SummonProjectileTypes.HORNET_STINGER.id();
    private static final ResourceLocation IMP_FIREBALL = SummonProjectileTypes.IMP_FIREBALL.id();
    private static final ModelResourceLocation FINCH_STAFF_EMPTY_MODEL = new ModelResourceLocation(Confluence.asResource("finch_staff_empty"), "inventory");
    private static final ResourceLocation STINGER_TEXTURE = Confluence.asResource("textures/entity/model/stinger.png");
    private static final int BACK_TRANSITION_TICKS = 20;
    private static final double INTERPOLATION_TICKS = 1.0;
    private static final double TELEPORT_DISTANCE_SQR = 16.0 * 16.0;
    private static final Map<UUID, State> STATES = new HashMap<>();
    private static final Map<UUID, ClientSummonVisual> GEO_VISUALS = new HashMap<>();
    private static final Map<UUID, ClientStardustDragonVisual> STARDUST_DRAGON_VISUALS = new HashMap<>();
    private static final Map<UUID, Integer> STARDUST_DRAGON_LAST_PARTS = new HashMap<>();
    private static final Map<ResourceLocation, ClientSummonGeoRenderer> GEO_RENDERERS = new HashMap<>();
    private static final Map<ResourceLocation, ItemStack> SUMMON_SWORD_ITEMS = Map.of(
            Confluence.asResource("summon_wooden_sword"), new ItemStack(Items.WOODEN_SWORD),
            Confluence.asResource("summon_stone_sword"), new ItemStack(Items.STONE_SWORD),
            Confluence.asResource("summon_iron_sword"), new ItemStack(Items.IRON_SWORD),
            Confluence.asResource("summon_golden_sword"), new ItemStack(Items.GOLDEN_SWORD),
            Confluence.asResource("summon_diamond_sword"), new ItemStack(Items.DIAMOND_SWORD),
            Confluence.asResource("summon_netherite_sword"), new ItemStack(Items.NETHERITE_SWORD));
    private static final ClientStardustDragonRenderer STARDUST_DRAGON_RENDERER = new ClientStardustDragonRenderer();
    private static boolean externalShaderPipeline;
    private static long synchronizationSequence;
    private static TerraprismaModel terraprismaModel;
    private static HornetStingerProjectileModel hornetStingerModel;
    private static ClientIronGolemRenderer ironGolemRenderer;

    private ClientSummonManager() {}

    public static void reset() {
        STATES.clear();
        GEO_VISUALS.clear();
        STARDUST_DRAGON_VISUALS.clear();
        STARDUST_DRAGON_LAST_PARTS.clear();
        synchronizationSequence = 0L;
    }

    public static void registerAdditionalModels(PortModelEvent.RegisterAdditional event) {
        event.register(FINCH_STAFF_EMPTY_MODEL);
    }

    public static BakedModel finchStaffEmptyModel() {
        return Minecraft.getInstance().getModelManager().getModel(FINCH_STAFF_EMPTY_MODEL);
    }

    public static boolean hasAvailableSlots(UUID ownerId, int requestedSlots, int capacity) {
        int occupiedSlots = 0;
        for (State state : STATES.values()) {
            if (!state.ownerId.equals(ownerId) || state.current.type().equals(HORNET_STINGER)
                    || state.current.type().equals(IMP_FIREBALL)) continue;
            if (!state.current.type().equals(STARDUST_DRAGON) || state.current.order() > 0)
                occupiedSlots++;
        }
        return occupiedSlots + requestedSlots <= capacity;
    }

    public static void accept(UUID ownerId, List<SummonSyncPacketS2C.Entry> entries) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            STATES.clear();
            GEO_VISUALS.clear();
            STARDUST_DRAGON_VISUALS.clear();
            return;
        }
        long sequence = ++synchronizationSequence;
        for (SummonSyncPacketS2C.Entry entry : entries) {
            State state = STATES.get(entry.id());
            if (state == null) {
                state = new State(ownerId, entry, level.getGameTime());
                STATES.put(entry.id(), state);
            } else {
                state.update(ownerId, entry, level.getGameTime());
            }
            state.lastSynchronization = sequence;
        }
        Iterator<Map.Entry<UUID, State>> iterator = STATES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, State> state = iterator.next();
            if (state.getValue().ownerId.equals(ownerId) && state.getValue().lastSynchronization != sequence) {
                removeVisuals(state.getKey());
                iterator.remove();
            }
        }
    }

    public static void render(PortRenderLevelStageEvent event) {
        if (event.getStage() == PortRenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            externalShaderPipeline = RenderSystem.getShader() != null
                    && RenderSystem.getShader().getClass().getSimpleName().equals("ExtendedShader");
        }
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
        Iterator<Map.Entry<UUID, State>> iterator = STATES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, State> state = iterator.next();
            if (level.getGameTime() - state.getValue().lastUpdate > 5L) {
                removeVisuals(state.getKey());
                iterator.remove();
            }
        }
        if (STATES.isEmpty()) {
            return;
        }
        STARDUST_DRAGON_LAST_PARTS.clear();
        for (State state : STATES.values()) {
            if (state.current.type().equals(STARDUST_DRAGON)) {
                STARDUST_DRAGON_LAST_PARTS.merge(state.visualGroupId, state.current.order(), Math::max);
            }
        }
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        for (State state : STATES.values()) {
            if (state.current.type().equals(HORNET_STINGER)) {
                renderHornetStinger(state, event, buffers);
            } else if (state.current.type().equals(IMP_FIREBALL)) {
                continue;
            } else if (state.current.type().equals(TERRAPRISMA)) {
                renderTerraprisma(state, event, buffers);
                if (!state.current.followingOwner())
                    renderTrail(state, event, buffers, 0.25F, state.rgb());
            } else if (isSummonSword(state.current.type())) {
                renderSummonSword(state, event, buffers);
                if (!state.current.followingOwner())
                    renderTrail(state, event, buffers, 0.15F, summonSwordColor(state.current.type()));
            } else if (state.current.type().equals(STARDUST_DRAGON)) {
                renderStardustDragonPart(state, event, buffers);
            } else if (usesGeoVisual(state.current.type())) {
                renderGeoVisual(state, event, buffers);
            } else if (state.current.type().equals(IRON_GOLEM)) {
                renderIronGolemVisual(state, event, buffers);
            }
        }
        buffers.endBatch();
    }

    private static void removeVisuals(UUID id) {
        GEO_VISUALS.remove(id);
        STARDUST_DRAGON_VISUALS.remove(id);
    }

    private static void renderGeoVisual(State state, PortRenderLevelStageEvent event, MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 position = state.interpolatedPosition(partialTick);
        ClientSummonVisual visual = GEO_VISUALS.computeIfAbsent(state.current.id(), id -> new ClientSummonVisual(id, state.current.type()));
        visual.update(state.current.animation(), state.current.position().distanceToSqr(state.previous.position()) > 1.0E-5, state.lastUpdate + partialTick);
        Vec3 camera = event.getCamera().getPosition();
        int packedLight = LevelRenderer.getLightColor(level, BlockPos.containing(position));
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(position.x - camera.x, position.y - camera.y, position.z - camera.z);
        float yaw = state.interpolatedYaw(partialTick);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        if (rotatesWithPitch(state.current.type())) {
            double radians = yaw * Mth.DEG_TO_RAD;
            poseStack.mulPose(Axis.of(new Vector3f((float) Math.cos(radians), 0.0F, (float) Math.sin(radians)))
                    .rotationDegrees(state.interpolatedPitch(partialTick)));
        }
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

    private static void renderHornetStinger(State state, PortRenderLevelStageEvent event, MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 position = state.interpolatedPosition(partialTick);
        Vec3 camera = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(position.x - camera.x, position.y - camera.y, position.z - camera.z);
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.interpolatedYaw(partialTick)));
        poseStack.mulPose(Axis.ZP.rotationDegrees(state.interpolatedPitch(partialTick)));
        int packedLight = LevelRenderer.getLightColor(minecraft.level, BlockPos.containing(position));
        HornetStingerProjectileModel model = hornetStingerModel(minecraft.getEntityModels());
        model.renderToBuffer(poseStack, bufferSource.getBuffer(model.renderType(STINGER_TEXTURE)),
                packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    private static boolean rotatesWithPitch(ResourceLocation type) {
        return switch (type.getPath()) {
            case "finch_baby", "hornet_baby", "sculk_wisp", "summon_imp" -> true;
            default -> false;
        };
    }

    private static void renderIronGolemVisual(State state, PortRenderLevelStageEvent event, MultiBufferSource bufferSource) {
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
                        ? Math.max(0, state.current.animationDuration() - state.current.animationTicks()) : 0, partialTick);
        poseStack.popPose();
    }

    private static void renderSummonSword(State state, PortRenderLevelStageEvent event, MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 position = state.interpolatedPosition(partialTick);
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        Vec3 camera = event.getCamera().getPosition();
        poseStack.translate(position.x - camera.x, position.y - camera.y, position.z - camera.z);
        poseStack.mulPose(Axis.YN.rotationDegrees(state.interpolatedYaw(partialTick) - 90.0F));
        poseStack.mulPose(Axis.ZN.rotationDegrees(-state.interpolatedPitch(partialTick)));
        poseStack.mulPose(Axis.XN.rotationDegrees(state.interpolatedRoll(partialTick)));
        float backProgress = state.backProgress(partialTick);
        int sequence = state.current.order() + 1;
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F * backProgress));
        poseStack.mulPose(Axis.ZP.rotationDegrees(sequence / 2 * ((sequence & 1) == 0 ? -1.0F : 1.0F) * 15.0F * backProgress));
        applySummonSwordAnimation(state, partialTick, poseStack);
        poseStack.mulPose(Axis.ZN.rotationDegrees(-45.0F));
        int packedLight = LevelRenderer.getLightColor(minecraft.level, BlockPos.containing(position));
        minecraft.getItemRenderer().renderStatic(swordItem(state.current.type()), ItemDisplayContext.FIXED,
                packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource, minecraft.level, state.current.id().hashCode());
        poseStack.popPose();
    }

    private static boolean isSummonSword(ResourceLocation type) {
        return SUMMON_SWORD_ITEMS.containsKey(type);
    }

    private static void renderStardustDragonPart(State state, PortRenderLevelStageEvent event, MultiBufferSource bufferSource) {
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
        ClientStardustDragonVisual visual = STARDUST_DRAGON_VISUALS.computeIfAbsent(state.current.id(), id -> new ClientStardustDragonVisual(id, part));
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
        return STARDUST_DRAGON_LAST_PARTS.getOrDefault(state.visualGroupId, 0);
    }

    private static ItemStack swordItem(ResourceLocation type) {
        ItemStack stack = SUMMON_SWORD_ITEMS.get(type);
        if (stack == null) throw new IllegalArgumentException("Unknown summon sword type: " + type);
        return stack;
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

    private static void renderTerraprisma(State state, PortRenderLevelStageEvent event, MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 position = state.interpolatedPosition(partialTick);
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        Vec3 camera = event.getCamera().getPosition();
        poseStack.translate(position.x - camera.x, position.y - camera.y, position.z - camera.z);
        poseStack.mulPose(Axis.YN.rotationDegrees(state.interpolatedYaw(partialTick)));
        poseStack.mulPose(Axis.XN.rotationDegrees(-state.interpolatedPitch(partialTick) + 180.0F));
        poseStack.mulPose(Axis.ZN.rotationDegrees(state.interpolatedRoll(partialTick)));
        float backProgress = state.backProgress(partialTick);
        int sequence = state.current.order() + 1;
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F * backProgress));
        poseStack.mulPose(Axis.XP.rotationDegrees(sequence / 2 * ((sequence & 1) == 0 ? -1.0F : 1.0F) * 15.0F * backProgress));
        applyAnimation(state, partialTick, poseStack);
        poseStack.scale(state.current.scale(), state.current.scaleY(), state.current.scale());
        int rgb = state.rgb();
        int packedLight = minecraft.level == null ? LightTexture.FULL_BRIGHT
                : LevelRenderer.getLightColor(minecraft.level, BlockPos.containing(position));
        float red = (rgb >> 16 & 255) / 255.0F;
        float green = (rgb >> 8 & 255) / 255.0F;
        float blue = (rgb & 255) / 255.0F;
        TerraprismaModel model = model(minecraft.getEntityModels());
        if (externalShaderPipeline) {
            poseStack.pushPose();
            poseStack.scale(0.9F, 0.9F, 0.9F);
            model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityCutoutNoCull(TERRAPRISMA_TEXTURE)),
                    packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, 47.0F / 255.0F);
            poseStack.popPose();
            model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TERRAPRISMA_TEXTURE)),
                    packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
        } else {
            model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.energySwirl(TERRAPRISMA_TEXTURE, 0.0F, 0.0F)),
                    packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, 1.0F);
        }
        poseStack.popPose();
    }

    private static void renderTrail(State state, PortRenderLevelStageEvent event, MultiBufferSource bufferSource, float width, int rgb) {
        if (state.trailSamples.size() < 2) {
            return;
        }
        Vec3 camera = event.getCamera().getPosition();
        VertexConsumer consumer = bufferSource.getBuffer(RenderStateShardAccessor.TRAIL_RENDER_TYPE);
        Matrix4f matrix = event.getPoseStack().last().pose();
        TrailSample previous = null;
        Vec3 previousLeft = null;
        Vec3 previousRight = null;
        Vec3 previousFadedLeft = null;
        int sampleCount = state.trailSamples.size();
        boolean terraprisma = state.current.type().equals(TERRAPRISMA);
        TrailSample first = state.trailSamples.peekFirst();
        int fadeRed = first.color >> 16 & 255;
        int fadeGreen = first.color >> 8 & 255;
        int fadeBlue = first.color & 255;
        int red = rgb >> 16 & 255;
        int green = rgb >> 8 & 255;
        int blue = rgb & 255;
        int lastColor = rgb;
        int index = 0;
        for (TrailSample current : state.trailSamples) {
            if (previous == null) {
                previous = current;
                continue;
            }
            float progress = index++ / (float) sampleCount * 0.6F + 0.4F;
            float currentWidth = width * progress;
            Vec3 side = trailSide(current, terraprisma);
            Vec3 previousPosition = previous.position.subtract(camera);
            Vec3 currentPosition = current.position.subtract(camera);
            Vec3 currentFadedLeft = currentPosition.add(side.scale(currentWidth * 5.0F));
            Vec3 currentLeft = currentPosition.add(side.scale(currentWidth * 5.25F));
            Vec3 currentRight = currentPosition.subtract(side.scale(currentWidth));
            Vec3 startLeft = previousLeft == null ? previousPosition.add(side.scale(currentWidth)) : previousLeft;
            Vec3 startRight = previousRight == null ? previousPosition.subtract(side.scale(currentWidth)) : previousRight;
            Vec3 startFadedLeft = previousFadedLeft == null ? startLeft : previousFadedLeft;
            int alpha = current == state.trailSamples.peekLast() ? 20 : Mth.floor(200.0F * progress);
            int color = alpha << 24 | Mth.floor(Mth.lerp(progress, red, fadeRed)) << 16
                    | Mth.floor(Mth.lerp(progress, green, fadeGreen)) << 8
                    | Mth.floor(Mth.lerp(progress, blue, fadeBlue));
            vertex(consumer, matrix, startLeft, lastColor);
            vertex(consumer, matrix, startFadedLeft, lastColor);
            vertex(consumer, matrix, currentFadedLeft, color);
            vertex(consumer, matrix, currentLeft, color);
            vertex(consumer, matrix, startFadedLeft, lastColor & 0xA0FFFFFF);
            vertex(consumer, matrix, startRight, lastColor & 0x00FFFFFF);
            vertex(consumer, matrix, currentRight, color & 0x00FFFFFF);
            vertex(consumer, matrix, currentFadedLeft, color & 0xA0FFFFFF);
            previous = current;
            previousLeft = currentLeft;
            previousRight = currentRight;
            previousFadedLeft = currentFadedLeft;
            lastColor = color;
            current.color = color;
        }
    }

    private static Vec3 trailSide(TrailSample sample, boolean terraprisma) {
        if (terraprisma && sample.side != null) return sample.side;
        Quaternionf rotation = new Quaternionf().rotateY(-sample.yaw * Mth.DEG_TO_RAD)
                .rotateX((terraprisma ? sample.pitch - 180.0F : sample.pitch) * Mth.DEG_TO_RAD);
        Vector3f normal = rotation.transform(new Vector3f(0.0F, 0.0F, terraprisma ? -1.0F : 1.0F));
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
            default -> {}
        }
    }

    private static void applySummonSwordAnimation(State state, float partialTick, PoseStack poseStack) {
        int duration = state.current.animationDuration();
        if (duration <= 0 || state.current.animation() != SummonAnimation.SPIN_X) return;
        float progress = Mth.clamp((state.current.animationTicks() + partialTick) / duration, 0.0F, 1.0F);
        poseStack.mulPose(Axis.ZN.rotationDegrees(state.current.animationDegrees() * progress));
    }

    private static TerraprismaModel model(EntityModelSet models) {
        if (terraprismaModel == null) {
            terraprismaModel = new TerraprismaModel(models.bakeLayer(TerraprismaModel.LAYER_LOCATION));
        }
        return terraprismaModel;
    }

    private static HornetStingerProjectileModel hornetStingerModel(EntityModelSet models) {
        if (hornetStingerModel == null) {
            hornetStingerModel = new HornetStingerProjectileModel(models.bakeLayer(HornetStingerProjectileModel.LAYER_LOCATION));
        }
        return hornetStingerModel;
    }

    private static ClientIronGolemRenderer ironGolemRenderer(EntityModelSet models) {
        if (ironGolemRenderer == null) ironGolemRenderer = new ClientIronGolemRenderer(models);
        return ironGolemRenderer;
    }

    private static final class State {
        private final UUID ownerId;
        private final UUID visualGroupId;
        private final RandomSource random;
        private SummonSyncPacketS2C.Entry previous;
        private SummonSyncPacketS2C.Entry current;
        private long lastUpdate;
        private long lastSynchronization;
        private Vec3 interpolationStartPosition;
        private float interpolationStartYaw;
        private float interpolationStartPitch;
        private float interpolationStartRoll;
        private double interpolationStartTime;
        private float colorProgress;
        private float sliderProgress;
        private int backTicks;
        private final WalkAnimationState walkAnimation = new WalkAnimationState();
        private final Deque<TrailSample> trailSamples = new ArrayDeque<>(8);

        private State(UUID ownerId, SummonSyncPacketS2C.Entry entry, long lastUpdate) {
            this.ownerId = ownerId;
            this.visualGroupId = !entry.type().equals(STARDUST_DRAGON) || entry.order() == 0 ? entry.id()
                    : new UUID(entry.id().getMostSignificantBits(), entry.id().getLeastSignificantBits() ^ entry.order());
            this.previous = entry;
            this.current = entry;
            this.lastUpdate = lastUpdate;
            this.interpolationStartPosition = entry.position();
            this.interpolationStartYaw = entry.yaw();
            this.interpolationStartPitch = entry.pitch();
            this.interpolationStartRoll = entry.roll();
            this.interpolationStartTime = lastUpdate;
            this.random = RandomSource.create(entry.id().getMostSignificantBits() ^ entry.id().getLeastSignificantBits());
            this.colorProgress = random.nextFloat();
            this.backTicks = 0;
            updateTrail(entry);
            emitProjectileParticles(entry, entry.position());
        }

        private State update(UUID ownerId, SummonSyncPacketS2C.Entry entry, long lastUpdate) {
            if (!this.ownerId.equals(ownerId)) {
                throw new IllegalStateException("Summon owner changed without replacing its runtime id");
            }
            double updateTime = lastUpdate + Minecraft.getInstance().getFrameTime();
            Vec3 displayedPosition = interpolatedPosition(updateTime);
            float displayedYaw = interpolatedYaw(updateTime);
            float displayedPitch = interpolatedPitch(updateTime);
            float displayedRoll = interpolatedRoll(updateTime);
            previous = current;
            current = entry;
            if (previous.followingOwner() != current.followingOwner()) {
                backTicks = current.followingOwner() ? 0 : BACK_TRANSITION_TICKS;
            } else if (current.followingOwner()) {
                backTicks = Math.min(BACK_TRANSITION_TICKS, backTicks + 1);
            } else {
                backTicks = Math.max(0, backTicks - 1);
            }
            this.lastUpdate = lastUpdate;
            if (displayedPosition.distanceToSqr(entry.position()) > TELEPORT_DISTANCE_SQR) {
                interpolationStartPosition = entry.position();
                interpolationStartYaw = entry.yaw();
                interpolationStartPitch = entry.pitch();
                interpolationStartRoll = entry.roll();
            } else {
                interpolationStartPosition = displayedPosition;
                interpolationStartYaw = displayedYaw;
                interpolationStartPitch = displayedPitch;
                interpolationStartRoll = displayedRoll;
            }
            interpolationStartTime = updateTime;
            walkAnimation.update((float) Math.min(1.0, current.position().subtract(previous.position()).horizontalDistance() * 4.0), 0.4F);
            float change = (random.nextFloat() - 0.5F) * 0.05F;
            colorProgress = Mth.clamp(colorProgress + change + sliderProgress, 0.0F, 1.0F);
            if (colorProgress >= 1.0F) {
                sliderProgress = -0.01F;
            } else if (colorProgress <= 0.0F) {
                sliderProgress = 0.01F;
            }
            updateTrail(entry);
            emitProjectileParticles(entry, previous.position());
            return this;
        }

        private static void emitProjectileParticles(SummonSyncPacketS2C.Entry entry, Vec3 previousPosition) {
            if (!entry.type().equals(IMP_FIREBALL)) return;
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return;
            Vec3 movement = entry.position().subtract(previousPosition);
            Vec3 velocity = movement.scale(0.55);
            RandomSource random = level.random;
            for (int index = 0; index < 3; index++) {
                double x = entry.position().x + (random.nextDouble() - 0.5) * 0.5;
                double y = entry.position().y + (random.nextDouble() - 0.5) * 0.5;
                double z = entry.position().z + (random.nextDouble() - 0.5) * 0.5;
                level.addParticle(ParticleTypes.FLAME, x, y, z, velocity.x, velocity.y, velocity.z);
            }
        }

        private void updateTrail(SummonSyncPacketS2C.Entry entry) {
            if (entry.followingOwner()) {
                trailSamples.pollFirst();
                return;
            }
            TrailSample sample = new TrailSample(entry.position(), entry.yaw(), entry.pitch(), entry.roll());
            if (entry.type().equals(TERRAPRISMA))
                sample.side = terraprismaTrailSide(entry, backProgress(0.0F));
            trailSamples.addLast(sample);
            while (trailSamples.size() > 8) {
                trailSamples.removeFirst();
            }
        }

        private static Vec3 terraprismaTrailSide(SummonSyncPacketS2C.Entry entry, float backProgress) {
            int sequence = entry.order() + 1;
            float tilt = sequence / 2 * ((sequence & 1) == 0 ? -1.0F : 1.0F) * 15.0F * backProgress;
            Quaternionf rotation = new Quaternionf().rotateY(-entry.yaw() * Mth.DEG_TO_RAD)
                    .rotateX((entry.pitch() - 180.0F) * Mth.DEG_TO_RAD)
                    .rotateZ(90.0F * backProgress * Mth.DEG_TO_RAD).rotateX(tilt * Mth.DEG_TO_RAD);
            int duration = entry.animationDuration();
            float progress = duration <= 0 ? 0.0F : Mth.clamp(entry.animationTicks() / (float) duration, 0.0F, 1.0F);
            float degrees = entry.animationDegrees() * progress;
            switch (entry.animation()) {
                case SPIN_X -> rotation.rotateX(-degrees * Mth.DEG_TO_RAD);
                case ROTATE_Z -> rotation.rotateZ(-degrees * Mth.DEG_TO_RAD);
                case SPIN_Y -> {
                    rotation.rotateY(-degrees * Mth.DEG_TO_RAD);
                    float zTilt = progress < 1.0F / 6.0F ? progress * 540.0F
                            : progress > 5.0F / 6.0F ? (1.0F - progress) * 540.0F : 90.0F;
                    rotation.rotateZ(-zTilt * Mth.DEG_TO_RAD);
                }
                default -> {}
            }
            Vector3f side = rotation.transform(new Vector3f(0.0F, 0.0F, -1.0F)).normalize();
            return new Vec3(side.x, side.y, side.z);
        }

        private Vec3 interpolatedPosition(float partialTick) {
            return interpolatedPosition(renderTime(partialTick));
        }

        private Vec3 interpolatedPosition(double time) {
            return interpolationStartPosition.lerp(current.position(), interpolationProgress(time));
        }

        private float interpolatedYaw(float partialTick) {
            return interpolatedYaw(renderTime(partialTick));
        }

        private float interpolatedYaw(double time) {
            return Mth.rotLerp(interpolationProgress(time), interpolationStartYaw, current.yaw());
        }

        private float interpolatedPitch(float partialTick) {
            return interpolatedPitch(renderTime(partialTick));
        }

        private float interpolatedPitch(double time) {
            return Mth.rotLerp(interpolationProgress(time), interpolationStartPitch, current.pitch());
        }

        private float interpolatedRoll(float partialTick) {
            return interpolatedRoll(renderTime(partialTick));
        }

        private float interpolatedRoll(double time) {
            return Mth.rotLerp(interpolationProgress(time), interpolationStartRoll, current.roll());
        }

        private float backProgress(float partialTick) {
            float ticks = current.followingOwner() ? backTicks + partialTick : backTicks - partialTick;
            float progress = Mth.clamp(ticks / BACK_TRANSITION_TICKS, 0.0F, 1.0F);
            return progress < 0.5F ? 2.0F * progress * progress : 1.0F - Mth.square(-2.0F * progress + 2.0F) * 0.5F;
        }

        private double renderTime(float partialTick) {
            ClientLevel level = Minecraft.getInstance().level;
            return (level == null ? lastUpdate : level.getGameTime()) + partialTick;
        }

        private float interpolationProgress(double time) {
            return Mth.clamp((float) ((time - interpolationStartTime) / INTERPOLATION_TICKS), 0.0F, 1.0F);
        }

        private int rgb() {
            int from = 0x1FE6C0;
            int to = 0xC67C28;
            int red = (int) ((from >> 16 & 255) + ((to >> 16 & 255) - (from >> 16 & 255)) * colorProgress);
            int green = (int) ((from >> 8 & 255) + ((to >> 8 & 255) - (from >> 8 & 255)) * colorProgress);
            int blue = (int) ((from & 255) + ((to & 255) - (from & 255)) * colorProgress);
            return red << 16 | green << 8 | blue;
        }
    }

    private static final class TrailSample {
        private final Vec3 position;
        private final float yaw;
        private final float pitch;
        private final float roll;
        private Vec3 side;
        private int color;

        private TrailSample(Vec3 position, float yaw, float pitch, float roll) {
            this.position = position;
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
        }
    }
}
