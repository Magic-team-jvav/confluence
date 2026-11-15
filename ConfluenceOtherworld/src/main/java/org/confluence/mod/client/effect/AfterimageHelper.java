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
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.init.armor.ArmorSetBonusKey;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.mixin.client.renderer.entity.LivingEntityRendererAccessor;

import java.util.*;

/// 套装效果：穿戴指定套装的玩家移动时，身后留下短暂的黑白残影
public final class AfterimageHelper {
    /// 每 2 tick 记录一次快照（拖尾更短）
    private static final int RECORD_INTERVAL = 2;
    /// 最多同时保留的残影数量
    private static final int MAX_GHOSTS = 4;
    /// 低于该速度（格/tick 的平方）视为静止，不再记录
    private static final double MIN_SPEED_SQR = 0.0025D;

    private static final Map<UUID, Deque<Ghost>> TRAILS = new HashMap<>();
    private static ArmorSetBonusKey[] cachedKeys;

    private AfterimageHelper() {}

    /// 每 tick 记录一次移动快照，实现见 [org.confluence.mod.client.event.GameClientEvents#playerTick$Post]
    public static void tick(Player player) {
        if (!player.level().isClientSide) return;

        UUID uuid = player.getUUID();
        if (player.isSpectator() || player.isInvisible() || player.isDeadOrDying() || !isWearingAfterimageSet(player)) {
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

    /// 渲染残影，实现见 [org.confluence.mod.client.event.GameClientEvents#renderPlayer$Pre]
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

        // 相邻快照之间插值补帧，把离散的残影连成动态模糊
        int lastSegment = (ghosts.length - 1) * AfterimageStyle.BLUR_STEPS;
        int sample = 0;
        for (int i = 0; i < ghosts.length - 1; i++) {
            Ghost from = ghosts[i];
            Ghost to = ghosts[i + 1];
            for (int step = 0; step < AfterimageStyle.BLUR_STEPS; step++, sample++) {
                // step == 0 即记录下来的快照，保持原透明度；其余补帧残影压暗作为模糊过渡
                float factor = sample / (float) lastSegment;
                float alpha = step > 0
                        ? AfterimageStyle.interpolatedAlphaAt(factor)
                        : AfterimageStyle.alphaAt(factor);
                renderer.render(from.lerp(step / (float) AfterimageStyle.BLUR_STEPS, to), alpha);
            }
        }
        renderer.render(ghosts[ghosts.length - 1], AfterimageStyle.alphaAt(1.0F));
    }

    /// 单帧的残影渲染上下文，复用玩家模型把每个插值残影画成纯黑剪影
    private static final class TrailRenderer {
        private final PlayerModel<AbstractClientPlayer> model;
        private final PoseStack poseStack;
        private final VertexConsumer consumer;
        private final LivingEntityRendererAccessor accessor;
        private final AbstractClientPlayer player;
        private final int packedLight;
        private final float partialTick;
        private final float bob;
        private final float scale;
        private final Vec3 current;

        private TrailRenderer(RenderPlayerEvent.Pre event, AbstractClientPlayer player) {
            PlayerRenderer renderer = event.getRenderer();
            float partialTick = event.getPartialTick();
            this.model = renderer.getModel();
            this.poseStack = event.getPoseStack();
            this.accessor = (LivingEntityRendererAccessor) renderer;
            this.player = player;
            this.packedLight = event.getPackedLight();
            this.partialTick = partialTick;
            this.consumer = event.getMultiBufferSource().getBuffer(RenderType.entityTranslucent(renderer.getTextureLocation(player)));
            this.bob = this.accessor.callGetBob(player, partialTick);
            this.scale = player.getScale();
            this.current = player.getPosition(partialTick);
        }

        private void render(Ghost ghost, float alpha) {
            int color = AfterimageStyle.colorAt(alpha);

            model.setupAnim(player, ghost.limbSwing(), ghost.limbSwingAmount(), ghost.ageInTicks(),
                    Mth.wrapDegrees(ghost.headYaw() - ghost.bodyYaw()), ghost.headPitch());

            poseStack.pushPose();
            poseStack.translate(ghost.x() - current.x, ghost.y() - current.y, ghost.z() - current.z);
            accessor.callSetupRotations(player, poseStack, bob, ghost.bodyYaw(), partialTick, scale);
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            accessor.callScale(player, poseStack, partialTick);
            poseStack.translate(0.0F, -1.501F, 0.0F);

            model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, color);
            poseStack.popPose();
        }
    }

    /// 登出时清空
    public static void reset() {
        TRAILS.clear();
    }

    /// 会触发残影的套装，取自 [ModArmorBonus] 注册时的返回值，后续可继续追加
    private static ArmorSetBonusKey[] afterimageSets() {
        ArmorSetBonusKey[] keys = cachedKeys;
        if (keys == null) keys = cachedKeys = new ArmorSetBonusKey[]{ModArmorBonus.NINJA_SET};
        return keys;
    }

    /// 玩家当前的套装加成键由 [PlayerSpecialData] 跟随装备变更维护，这里只需比对是否命中
    private static boolean isWearingAfterimageSet(Player player) {
        ArmorSetBonusKey current = PlayerSpecialData.of(player).getArmorSetBonusKey();
        if (current == ArmorSetBonusKey.NONE) return false;

        for (ArmorSetBonusKey key : afterimageSets()) {
            if (current.equals(key)) return true;
        }
        return false;
    }

    private record Ghost(
            double x, double y, double z,
            float bodyYaw, float headYaw, float headPitch,
            float limbSwing, float limbSwingAmount, float ageInTicks
    ) {
        /// 在两个快照之间线性插值，角度用 [Mth#rotLerp] 处理绕圈
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
