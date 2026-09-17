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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.effect.RenderStateShardAccessor;
import org.confluence.mod.client.model.entity.projectile.HornetStingerProjectileModel;
import org.confluence.mod.client.model.entity.summon.TerraprismaModel;
import org.confluence.mod.common.summon.flying.FinchSummon;
import org.confluence.mod.common.summon.projectile.SummonProjectileTypes;
import org.confluence.mod.network.s2c.SummonSyncPacketS2C;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.mesdag.portlib.event.client.PortRenderLevelStageEvent;

import java.util.*;

/// 保存服务端同步的召唤物状态，并在世界渲染阶段绘制对应的客户端表现。
/// 召唤物逻辑仍由服务端驱动；客户端只负责插值、拖尾、待机编队和模型动画。
public final class ClientSummonManager {
    private static final ResourceLocation TERRAPRISMA = Confluence.asResource("terraprisma");
    private static final ResourceLocation STARDUST_DRAGON = Confluence.asResource("stardust_dragon");
    private static final ResourceLocation IRON_GOLEM = Confluence.asResource("i_32_iron_golem");
    private static final ResourceLocation FINCH = Confluence.asResource("finch_baby");
    private static final ResourceLocation TERRAPRISMA_TEXTURE = Confluence.asResource("textures/entity/model/terraprisma_gray.png");
    private static final ResourceLocation HORNET_STINGER = SummonProjectileTypes.HORNET_STINGER.id();
    private static final ResourceLocation IMP_FIREBALL = SummonProjectileTypes.IMP_FIREBALL.id();
    public static final ModelResourceLocation FINCH_STAFF_EMPTY_MODEL = new ModelResourceLocation(Confluence.asResource("finch_staff_empty"), "inventory");
    private static final ResourceLocation STINGER_TEXTURE = Confluence.asResource("textures/entity/model/stinger.png");
    private static final int BACK_TRANSITION_TICKS = 20;
    private static final double INTERPOLATION_TICKS = 2.0;
    private static final double TELEPORT_DISTANCE_SQR = 16.0 * 16.0;
    private static final Map<UUID, State> STATES = new HashMap<>();
    private static final Map<UUID, Long> CLOCK_OFFSETS = new HashMap<>();
    private static final Map<UUID, ClientSummonVisual> GEO_VISUALS = new HashMap<>();
    private static final Map<ResourceLocation, ClientSummonGeoRenderer> GEO_RENDERERS = new HashMap<>();
    private static boolean externalShaderPipeline;
    private static long synchronizationSequence;
    private static TerraprismaModel terraprismaModel;
    private static HornetStingerProjectileModel hornetStingerModel;

    private ClientSummonManager() {}

    public static void reset() {
        STATES.clear();
        CLOCK_OFFSETS.clear();
        GEO_VISUALS.clear();
        synchronizationSequence = 0L;
    }

    public static BakedModel finchStaffEmptyModel() {
        return Minecraft.getInstance().getModelManager().getModel(FINCH_STAFF_EMPTY_MODEL);
    }

    public static boolean hasSummon(UUID ownerId, ResourceLocation type) {
        for (State state : STATES.values()) {
            if (state.ownerId.equals(ownerId) && state.current.type().equals(type)) return true;
        }
        return false;
    }

    public static void accept(UUID ownerId, long gameTime, List<SummonSyncPacketS2C.Entry> entries) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            reset();
            return;
        }
        long offset = CLOCK_OFFSETS.computeIfAbsent(ownerId, id -> level.getGameTime() - gameTime);
        long sampleTime = gameTime + offset;
        long sequence = ++synchronizationSequence;
        for (SummonSyncPacketS2C.Entry entry : entries) {
            State state = STATES.get(entry.id());
            if (state == null) {
                state = new State(ownerId, entry, level.getGameTime(), sampleTime);
                STATES.put(entry.id(), state);
            } else {
                state.update(ownerId, entry, level.getGameTime(), sampleTime);
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
        if (entries.isEmpty()) CLOCK_OFFSETS.remove(ownerId);
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
            reset();
            return;
        }
        Iterator<Map.Entry<UUID, State>> iterator = STATES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, State> state = iterator.next();
            if (level.getGameTime() - state.getValue().lastUpdate > 100L) {
                removeVisuals(state.getKey());
                iterator.remove();
            }
        }
        if (STATES.isEmpty()) {
            return;
        }
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        for (State state : STATES.values()) {
            ClientSummonModels.Binding binding = ClientSummonModels.MODELS.get(state.current.type());
            if (state.current.type().equals(HORNET_STINGER)) {
                renderHornetStinger(state, event, buffers);
            } else if (state.current.type().equals(IMP_FIREBALL)) {
                continue;
            } else if (state.current.type().equals(TERRAPRISMA)) {
                renderTerraprisma(state, event, buffers);
                if (!state.current.followingOwner())
                    renderTrail(state, event, buffers, 0.25F, state.rgb());
            } else if (state.current.type().equals(STARDUST_DRAGON)) {
                renderStardustDragonPart(state, event, buffers);
            } else if (state.current.type().equals(IRON_GOLEM)) {
                // 铁傀儡由正常实体渲染器绘制；这里仅保留状态同步，不能再叠加一份客户端外观。
                continue;
            } else if (binding != null) {
                renderGeoVisual(state, binding, event, buffers);
            } else {
                renderMissingSummon(state, event, buffers);
            }
        }
        buffers.endBatch();
    }

    private static void removeVisuals(UUID id) {
        GEO_VISUALS.remove(id);
    }

    private static void renderGeoVisual(State state, ClientSummonModels.Binding binding, PortRenderLevelStageEvent event, MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 position = state.renderPosition(partialTick);
        ClientSummonVisual visual = GEO_VISUALS.compute(state.current.id(), (id, existing) -> existing != null && existing.type().equals(state.current.type())
                ? existing : new ClientSummonVisual(id, state.current.type(), binding.animations()));
        State.PoseFrame frame = state.poseFrame(state.renderTime(partialTick));
        visual.update(frame.from().animation(), frame.from().position().distanceToSqr(frame.to().position()) > 1.0E-5, state.renderTime(partialTick));
        Vec3 camera = event.getCamera().getPosition();
        int packedLight = LevelRenderer.getLightColor(level, BlockPos.containing(position));
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(position.x - camera.x, position.y - camera.y, position.z - camera.z);
        float yaw = state.interpolatedYaw(partialTick);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
        if (binding.rotatesWithPitch()) {
            double radians = yaw * Mth.DEG_TO_RAD;
            poseStack.mulPose(Axis.of(new Vector3f((float) Math.cos(radians), 0.0F, (float) Math.sin(radians))).rotationDegrees(state.interpolatedPitch(partialTick)));
        }
        poseStack.scale(state.current.scale(), state.current.scaleY(), state.current.scale());
        GEO_RENDERERS.computeIfAbsent(state.current.type(), type -> new ClientSummonGeoRenderer(binding)).render(poseStack, visual, bufferSource, null, null, packedLight);
        poseStack.popPose();
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
        model.renderToBuffer(poseStack, bufferSource.getBuffer(model.renderType(STINGER_TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }


    private static void renderStardustDragonPart(State state, PortRenderLevelStageEvent event, MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 position = state.interpolatedPosition(partialTick);
        Vec3 camera = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(position.x - camera.x, position.y - camera.y, position.z - camera.z);
        LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()), -0.25, 0.0, -0.25, 0.25, 0.5, 0.25, 1.0F, 0.65F, 0.1F, 1.0F);
        if (state.current.order() == 0) {
            var label = net.minecraft.network.chat.Component.translatable("entity.confluence.model_pending",
                    net.minecraft.network.chat.Component.translatable("entity.confluence.stardust_dragon"));
            poseStack.translate(0.0, 0.75, 0.0);
            poseStack.mulPose(event.getCamera().rotation());
            poseStack.scale(-0.025F, -0.025F, 0.025F);
            minecraft.font.drawInBatch(label, -minecraft.font.width(label) * 0.5F, 0.0F, 0xFFFFA619, false,
                    poseStack.last().pose(), bufferSource, net.minecraft.client.gui.Font.DisplayMode.NORMAL,
                    0, LightTexture.FULL_BRIGHT);
        }
        poseStack.popPose();
    }

    private static void renderMissingSummon(State state, PortRenderLevelStageEvent event, MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 position = state.interpolatedPosition(partialTick);
        Vec3 camera = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(position.x - camera.x, position.y - camera.y, position.z - camera.z);
        // 没有专用资源时仅显示逻辑尺寸，绝不借用其他生物模型。
        LevelRenderer.renderLineBox(poseStack, bufferSource.getBuffer(RenderType.lines()),
                -0.5, 0.0, -0.5, 0.5, 0.8, 0.5, 1.0F, 0.65F, 0.1F, 1.0F);
        var type = state.current.type();
        var label = net.minecraft.network.chat.Component.translatable("entity.confluence.model_pending",
                net.minecraft.network.chat.Component.translatable("entity." + type.getNamespace() + "." + type.getPath()));
        poseStack.translate(0.0, 1.05, 0.0);
        poseStack.mulPose(event.getCamera().rotation());
        poseStack.scale(-0.025F, -0.025F, 0.025F);
        minecraft.font.drawInBatch(label, -minecraft.font.width(label) * 0.5F, 0.0F, 0xFFFFA619, false,
                poseStack.last().pose(), bufferSource, net.minecraft.client.gui.Font.DisplayMode.NORMAL,
                0, LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }



    private static void renderTerraprisma(State state, PortRenderLevelStageEvent event, MultiBufferSource bufferSource) {
        Minecraft minecraft = Minecraft.getInstance();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 position = state.renderPosition(partialTick);
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

    private static final class State {
        private final UUID ownerId;
        private final UUID visualGroupId;
        private final RandomSource random;
        private SummonSyncPacketS2C.Entry previous;
        private SummonSyncPacketS2C.Entry current;
        private long lastUpdate;
        private long lastSynchronization;
        private final Deque<PoseSample> poseSamples = new ArrayDeque<>(8);
        private float colorProgress;
        private float sliderProgress;
        private int backTicks;
        private final WalkAnimationState walkAnimation = new WalkAnimationState();
        private final Deque<TrailSample> trailSamples = new ArrayDeque<>(8);

        private State(UUID ownerId, SummonSyncPacketS2C.Entry entry, long lastUpdate, long sampleTime) {
            this.ownerId = ownerId;
            this.visualGroupId = !entry.type().equals(STARDUST_DRAGON) || entry.order() == 0 ? entry.id()
                    : new UUID(entry.id().getMostSignificantBits(), entry.id().getLeastSignificantBits() ^ entry.order());
            this.previous = entry;
            this.current = entry;
            this.lastUpdate = lastUpdate;
            poseSamples.addLast(new PoseSample(sampleTime, entry));
            this.random = RandomSource.create(entry.id().getMostSignificantBits() ^ entry.id().getLeastSignificantBits());
            this.colorProgress = random.nextFloat();
            this.backTicks = 0;
            updateTrail(entry);
            emitProjectileParticles(entry, entry.position());
        }

        private State update(UUID ownerId, SummonSyncPacketS2C.Entry entry, long lastUpdate, long sampleTime) {
            if (!this.ownerId.equals(ownerId)) {
                throw new IllegalStateException("Summon owner changed without replacing its runtime id");
            }
            if (sampleTime < poseSamples.getLast().time()) return this;
            boolean teleport = current.position().distanceToSqr(entry.position()) > TELEPORT_DISTANCE_SQR;
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
            if (teleport) {
                poseSamples.clear();
                trailSamples.clear();
            } else if (poseSamples.getLast().time() == sampleTime) {
                poseSamples.removeLast();
            }
            poseSamples.addLast(new PoseSample(sampleTime, entry));
            while (poseSamples.size() > 8) poseSamples.removeFirst();
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

        private Vec3 renderPosition(float partialTick) {
            Vec3 synchronizedPosition = interpolatedPosition(partialTick);
            if (!current.followingOwner() || !current.type().equals(FINCH))
                return synchronizedPosition;
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) return synchronizedPosition;
            Player owner = level.getPlayerByUUID(ownerId);
            if (owner == null) return synchronizedPosition;
            Vec3 ownerPosition = owner.getPosition(partialTick);
            float bodyYaw = Mth.rotLerp(partialTick, owner.yBodyRotO, owner.yBodyRot);
            Vec3 formationPosition = FinchSummon.perchPosition(ownerPosition, bodyYaw, current.order());
            return synchronizedPosition.lerp(formationPosition, backProgress(partialTick));
        }

        private Vec3 interpolatedPosition(double time) {
            PoseFrame frame = poseFrame(time);
            return frame.from().position().lerp(frame.to().position(), frame.progress());
        }

        private float interpolatedYaw(float partialTick) {
            return interpolatedYaw(renderTime(partialTick));
        }

        private float interpolatedYaw(double time) {
            PoseFrame frame = poseFrame(time);
            return Mth.rotLerp(frame.progress(), frame.from().yaw(), frame.to().yaw());
        }

        private float interpolatedPitch(float partialTick) {
            return interpolatedPitch(renderTime(partialTick));
        }

        private float interpolatedPitch(double time) {
            PoseFrame frame = poseFrame(time);
            return Mth.rotLerp(frame.progress(), frame.from().pitch(), frame.to().pitch());
        }

        private float interpolatedRoll(float partialTick) {
            return interpolatedRoll(renderTime(partialTick));
        }

        private float interpolatedRoll(double time) {
            PoseFrame frame = poseFrame(time);
            return Mth.rotLerp(frame.progress(), frame.from().roll(), frame.to().roll());
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

        private PoseFrame poseFrame(double time) {
            // 按服务端采样间隔播放，收包不会重新启动插值；短时抖动由两 tick 缓冲吸收。
            double playbackTime = time - INTERPOLATION_TICKS;
            PoseSample from = poseSamples.getFirst();
            for (PoseSample to : poseSamples) {
                if (to.time() > playbackTime) {
                    float progress = to.time() == from.time() ? 0.0F
                            : Mth.clamp((float) ((playbackTime - from.time()) / (to.time() - from.time())), 0.0F, 1.0F);
                    return new PoseFrame(from.entry(), to.entry(), progress);
                }
                from = to;
            }
            return new PoseFrame(from.entry(), from.entry(), 0.0F);
        }

        private record PoseSample(long time, SummonSyncPacketS2C.Entry entry) {}

        private record PoseFrame(SummonSyncPacketS2C.Entry from, SummonSyncPacketS2C.Entry to,
                                 float progress) {}

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
