package org.confluence.mod.common.soulcerer.artifact;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils.TargetPolicy;
import org.confluence.lib.util.LibEntityUtils.WeaponTargeting;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.mesdag.particlestorm.data.molang.MolangExp;
import org.mesdag.particlestorm.network.EmitterCreationPacketS2C;

/// 法器效果的公共结算：限时增益、冲刺、烟花火箭替换标记与泰拉闪电。
///
/// 这里只放“效果本身”，灵魂支付、冷却、1/2/3 快捷触发分别由 {@link org.confluence.mod.util.SoulcererUtils}
/// 与客户端 {@link org.confluence.mod.client.handler.soulcerer.ClientSoulHandler} 负责。
public final class ArtifactEffects {
    /// 迅捷之靴的移速修饰符 ID 与到期时刻（世界时间，写在玩家持久数据里）。
    private static final ResourceLocation SWIFT_BOOTS_MODIFIER = Confluence.asResource("artifact_swift_boots");
    private static final String SWIFT_BOOTS_UNTIL = "confluence.artifact.swift_boots_until";
    /// 玩家身上“下一支箭替换成烟花火箭射弹”的待触发标记。
    private static final String FIREWORK_PAYLOAD = "confluence.artifact.fireworks_arrow_payload";
    /// 雷霆之杖的预警倒计时与落雷点。
    private static final String LIGHTNING_COUNTDOWN = "confluence.artifact.lightning_countdown";
    private static final String LIGHTNING_TARGET_X = "confluence.artifact.lightning_x";
    private static final String LIGHTNING_TARGET_Y = "confluence.artifact.lightning_y";
    private static final String LIGHTNING_TARGET_Z = "confluence.artifact.lightning_z";
    private static final String LIGHTNING_DIMENSION = "confluence.artifact.lightning_dimension";
    /// 泰拉闪电的预警粒子（本模组新增，样式参照蓄能闪电）。
    private static final ResourceLocation LIGHTNING_CHARGE_PARTICLE = Confluence.asResource("lightning_rod_charge");
    /// 落地闪光沿用现有雷击特效。
    private static final ResourceLocation LIGHTNING_IMPACT_PARTICLE = Confluence.asResource("thunderstrike_impact");
    /// 索敌规则与星怒一致：只抓敌对阵营。
    private static final WeaponTargeting TARGETING = new WeaponTargeting(TargetPolicy.ENEMY_FACTION);

    private ArtifactEffects() {}

    // region 迅捷之靴

    /// 施加（或刷新）迅捷之靴的移速增益，到期由 {@link #tick(ServerPlayer)} 摘除。
    public static void applySwiftBoots(ServerPlayer player) {
        long until = player.level().getGameTime() + SoulSkillParameters.SWIFT_BOOTS_DURATION_TICKS;
        player.getPersistentData().putLong(SWIFT_BOOTS_UNTIL, until);
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }
        speed.removeModifier(SWIFT_BOOTS_MODIFIER);
        speed.addTransientModifier(new AttributeModifier(SWIFT_BOOTS_MODIFIER,
                SoulSkillParameters.SWIFT_BOOTS_SPEED_BONUS, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    // endregion

    // region 浮流之羽

    /// 冲刺：与克苏鲁之盾同一套写法——只看身体朝向、不看俯仰角，所以抬头也冲不上天；
    /// 区别是速度更小（距离短于克苏鲁之盾），并且保留一点固定爬升，比克苏鲁之盾的 0 抬高。
    public static void dash(ServerPlayer player) {
        float yaw = player.getYRot() * Mth.DEG_TO_RAD;
        double factor = player.onGround() ? SoulSkillParameters.LIGHT_FEATHER_DASH_SPEED_GROUND
                : SoulSkillParameters.LIGHT_FEATHER_DASH_SPEED_AIR;
        Vec3 delta = player.getDeltaMovement();
        double climb = Math.max(delta.y, SoulSkillParameters.LIGHT_FEATHER_CLIMB);
        player.setDeltaMovement(delta.x - Mth.sin(yaw) * factor, climb, delta.z + Mth.cos(yaw) * factor);
        player.hasImpulse = true;
        player.hurtMarked = true;
        player.resetFallDistance();
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 0.4F, 1.6F);
    }

    // endregion

    // region 烟火之箭

    /// 标记“下一支箭整支替换成烟花火箭射弹”。
    public static void markFireworkPayload(ServerPlayer player) {
        player.getPersistentData().putBoolean(FIREWORK_PAYLOAD, true);
    }

    /// 取走待触发标记：射出一支箭只消耗一次。
    public static boolean takeFireworkPayload(Player player) {
        if (!player.getPersistentData().getBoolean(FIREWORK_PAYLOAD)) {
            return false;
        }
        player.getPersistentData().remove(FIREWORK_PAYLOAD);
        return true;
    }

    // endregion

    // region 雷霆之杖

    /// 锁定准星附近的敌怪（与星怒共用同一套索敌逻辑），没有敌怪时退回视线命中点。
    public static Vec3 findLightningTarget(ServerPlayer player) {
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getLookAngle().normalize().scale(SoulSkillParameters.LIGHTNING_ROD_RANGE));
        Entity target = TARGETING.angleHitTarget(eye, end, player.level(), player, player,
                SoulSkillParameters.LIGHTNING_ROD_RANGE, SoulSkillParameters.LIGHTNING_ROD_MAX_ANGLE, entity -> true);
        if (target != null) {
            return target.position();
        }
        HitResult hit = player.level().clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        return hit.getType() == HitResult.Type.MISS ? end : hit.getLocation();
    }

    /// 雷霆之杖：先在落点召唤泰拉闪电的预警粒子，`LIGHTNING_ROD_WINDUP_TICKS` 刻后才真正落雷。
    public static void beginLightningStrike(ServerPlayer player) {
        Vec3 target = findLightningTarget(player);
        CompoundTag data = player.getPersistentData();
        data.putDouble(LIGHTNING_TARGET_X, target.x);
        data.putDouble(LIGHTNING_TARGET_Y, target.y);
        data.putDouble(LIGHTNING_TARGET_Z, target.z);
        data.putString(LIGHTNING_DIMENSION, player.level().dimension().location().toString());
        data.putInt(LIGHTNING_COUNTDOWN, SoulSkillParameters.LIGHTNING_ROD_WINDUP_TICKS);
        if (player.level() instanceof ServerLevel level) {
            EmitterCreationPacketS2C.sendToAll(LIGHTNING_CHARGE_PARTICLE, target.toVector3f(), MolangExp.EMPTY, null);
            level.playSound(null, target.x, target.y, target.z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.4F, 1.8F);
        }
    }

    /// 落雷结算：生成真正的泰拉闪电（MC 闪电束）并广播落地闪光。
    private static void strikeLightning(ServerPlayer player, Vec3 target) {
        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt == null) {
            return;
        }
        bolt.moveTo(target);
        bolt.setCause(player);
        bolt.setDamage(SoulSkillParameters.LIGHTNING_ROD_DAMAGE);
        level.addFreshEntity(bolt);
        level.playSound(null, target.x, target.y, target.z, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.0F, 1.0F);
        EmitterCreationPacketS2C.sendToAll(LIGHTNING_IMPACT_PARTICLE, target.toVector3f(), MolangExp.EMPTY, null);
    }

    // endregion

    /// 每刻结算：迅捷之靴到期摘除、雷霆之杖预警倒计时归零后落雷。
    public static void tick(ServerPlayer player) {
        tickSwiftBoots(player);
        tickPendingLightning(player);
    }

    private static void tickSwiftBoots(ServerPlayer player) {
        long until = player.getPersistentData().getLong(SWIFT_BOOTS_UNTIL);
        if (until <= 0L || player.level().getGameTime() < until) {
            return;
        }
        player.getPersistentData().remove(SWIFT_BOOTS_UNTIL);
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(SWIFT_BOOTS_MODIFIER);
        }
    }

    /// 预警倒计时归零的那一帧落雷；玩家中途退出存档也会保留，重新进服后继续结算。
    private static void tickPendingLightning(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if (!data.contains(LIGHTNING_COUNTDOWN)) {
            return;
        }
        int countdown = data.getInt(LIGHTNING_COUNTDOWN) - 1;
        if (countdown > 0) {
            data.putInt(LIGHTNING_COUNTDOWN, countdown);
            return;
        }
        Vec3 target = new Vec3(data.getDouble(LIGHTNING_TARGET_X), data.getDouble(LIGHTNING_TARGET_Y), data.getDouble(LIGHTNING_TARGET_Z));
        /// 换维度后旧落点失效，直接丢弃这次预警，避免在别的维度炸出闪电。
        boolean sameDimension = data.getString(LIGHTNING_DIMENSION).equals(player.level().dimension().location().toString());
        data.remove(LIGHTNING_COUNTDOWN);
        data.remove(LIGHTNING_TARGET_X);
        data.remove(LIGHTNING_TARGET_Y);
        data.remove(LIGHTNING_TARGET_Z);
        strikeLightning(player, target);
    }
}
