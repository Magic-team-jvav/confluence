package org.confluence.mod.client.effect;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.init.armor.ArmorSetBonusKey;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.mixin.client.renderer.entity.LivingEntityRendererAccessor;

import java.util.*;

/// 忍者套装效果：移动时在玩家身后留下短暂的黑色残影。
public final class AfterimageHelper {
    private static final int RECORD_INTERVAL = 2;
    private static final int MAX_GHOSTS = 4;
    private static final double MIN_SPEED_SQR = 0.0025D;
    private static final Map<UUID, Deque<Ghost>> TRAILS = new HashMap<>();

    private AfterimageHelper() {}

    public static void tick(Player player) {
        if (!player.level().isClientSide) return;

        UUID uuid = player.getUUID();
        ArmorSetBonusKey armorSet = PlayerSpecialData.of(player).getArmorSetBonusKey();
        if (player.isSpectator() || player.isInvisible() || player.isDeadOrDying()
                || ModArmorBonus.NINJA_SET == null || !ModArmorBonus.NINJA_SET.equals(armorSet)) {
            Deque<Ghost> trail = TRAILS.get(uuid);
            if (trail != null) trail.clear();
            return;
        }

        Deque<Ghost> trail = TRAILS.computeIfAbsent(uuid, key -> new ArrayDeque<>());
        if (player.getDeltaMovement().horizontalDistanceSqr() < MIN_SPEED_SQR) {
            trail.pollFirst();
            return;
        }
        if (player.tickCount % RECORD_INTERVAL != 0) return;

        if (trail.size() >= MAX_GHOSTS) trail.pollFirst();
        trail.addLast(new Ghost(
                player.getX(), player.getY(), player.getZ(),
                player.yBodyRot, player.getYHeadRot(), player.getXRot(),
                player.walkAnimation.position(1.0F), player.walkAnimation.speed(1.0F),
                player.tickCount + 1.0F
        ));
    }

    public static void render(RenderPlayerEvent.Pre event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;

        Deque<Ghost> trail = TRAILS.get(player.getUUID());
        if (trail == null || trail.isEmpty()) return;

        TrailRenderer renderer = new TrailRenderer(event, player);
        Ghost[] ghosts = trail.toArray(Ghost[]::new);
        if (ghosts.length == 1) {
            renderer.render(ghosts[0], AfterimageStyle.alphaAt(1.0F));
            return;
        }

        int lastSegment = (ghosts.length - 1) * AfterimageStyle.BLUR_STEPS;
        int sample = 0;
        for (int i = 0; i < ghosts.length - 1; i++) {
            Ghost from = ghosts[i];
            Ghost to = ghosts[i + 1];
            for (int step = 0; step < AfterimageStyle.BLUR_STEPS; step++, sample++) {
                float factor = sample / (float) lastSegment;
                float alpha = step > 0
                        ? AfterimageStyle.interpolatedAlphaAt(factor)
                        : AfterimageStyle.alphaAt(factor);
                renderer.render(from.lerp(step / (float) AfterimageStyle.BLUR_STEPS, to), alpha);
            }
        }
        renderer.render(ghosts[ghosts.length - 1], AfterimageStyle.alphaAt(1.0F));
    }

    public static void reset() {
        TRAILS.clear();
    }

    private static final class TrailRenderer {
        private final PlayerModel<AbstractClientPlayer> model;
        private final PoseStack poseStack;
        private final VertexConsumer consumer;
        private final LivingEntityRendererAccessor accessor;
        private final AbstractClientPlayer player;
        private final int packedLight;
        private final float partialTick;
        private final float bob;
        private final Vec3 current;

        private TrailRenderer(RenderPlayerEvent.Pre event, AbstractClientPlayer player) {
            PlayerRenderer renderer = event.getRenderer();
            this.model = renderer.getModel();
            this.poseStack = event.getPoseStack();
            this.accessor = (LivingEntityRendererAccessor) renderer;
            this.player = player;
            this.packedLight = event.getPackedLight();
            this.partialTick = event.getPartialTick();
            this.consumer = event.getMultiBufferSource().getBuffer(RenderType.entityTranslucent(renderer.getTextureLocation(player)));
            this.bob = accessor.callGetBob(player, partialTick);
            this.current = player.getPosition(partialTick);
        }

        private void render(Ghost ghost, float alpha) {
            model.setupAnim(player, ghost.limbSwing(), ghost.limbSwingAmount(), ghost.ageInTicks(),
                    Mth.wrapDegrees(ghost.headYaw() - ghost.bodyYaw()), ghost.headPitch());

            poseStack.pushPose();
            poseStack.translate(ghost.x() - current.x, ghost.y() - current.y, ghost.z() - current.z);
            accessor.callSetupRotations(player, poseStack, bob, ghost.bodyYaw(), partialTick);
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            accessor.callScale(player, poseStack, partialTick);
            poseStack.translate(0.0F, -1.501F, 0.0F);
            model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 0.0F, 0.0F, 0.0F, alpha);
            poseStack.popPose();
        }
    }

    private record Ghost(
            double x, double y, double z,
            float bodyYaw, float headYaw, float headPitch,
            float limbSwing, float limbSwingAmount, float ageInTicks
    ) {
        private Ghost lerp(float delta, Ghost next) {
            return new Ghost(
                    Mth.lerp(delta, x, next.x),
                    Mth.lerp(delta, y, next.y),
                    Mth.lerp(delta, z, next.z),
                    Mth.rotLerp(delta, bodyYaw, next.bodyYaw),
                    Mth.rotLerp(delta, headYaw, next.headYaw),
                    Mth.lerp(delta, headPitch, next.headPitch),
                    Mth.lerp(delta, limbSwing, next.limbSwing),
                    Mth.lerp(delta, limbSwingAmount, next.limbSwingAmount),
                    Mth.lerp(delta, ageInTicks, next.ageInTicks)
            );
        }
    }
}
