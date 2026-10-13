package org.confluence.mod.common.enchantment;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEnchantments;
import org.jetbrains.annotations.Nullable;

/// 风暴锤：连枷命中实体时生成风暴并击飞命中点周围的生物。
///
/// 1.20.1 没有附魔效果组件系统，因此粒子、音效和风弹式击退全部由
/// [org.confluence.mod.util.EnchantmentUtils#processFlailWindBurst] 手动驱动。
/// 仅实体命中触发，方块命中不执行；与 [TurbineEnchantment]（flail_turbine）互斥。
public final class WindBurstEnchantment extends Enchantment {
    /// 击退半径
    public static final double KNOCKBACK_RADIUS = 3.0;
    /// 水平击退力度
    public static final double KNOCKBACK_HORIZONTAL = 1.2;
    /// 垂直击退力度
    public static final double KNOCKBACK_VERTICAL = 0.6;

    public WindBurstEnchantment() {
        super(Rarity.VERY_RARE, ModEnchantments.Categories.FLAIL, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 25 + 25 * (level - 1);
    }

    @Override
    public int getMaxCost(int level) {
        return 75 + 25 * (level - 1);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        return !(other instanceof TurbineEnchantment) && super.checkCompatibility(other);
    }

    /// 读取玩家手持或副手物品上的风暴锤附魔等级。
    ///
    /// @param player 连枷持有者
    /// @return 附魔等级（0 表示无此附魔）
    public static int getLevel(Player player) {
        int level = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.FLAIL_WIND_BURST.get(), player.getMainHandItem());
        if (level <= 0) {
            level = EnchantmentHelper.getTagEnchantmentLevel(ModEnchantments.FLAIL_WIND_BURST.get(), player.getOffhandItem());
        }
        return level;
    }

    /// 在命中点释放风暴：粒子 + 音效 + 风弹式击退。
    ///
    /// @param level            服务端世界
    /// @param enchantmentLevel 附魔等级，决定击退力度
    /// @param owner           连枷持有者，不参与击退
    /// @param victim          被命中的实体，其位置即命中点
    public static void applyWindBurst(ServerLevel level, int enchantmentLevel, @Nullable Player owner, LivingEntity victim) {
        Vec3 origin = victim.position();

        // ── 粒子 ──
        // 1.20.1 没有风弹粒子（GUST_EMITTER 系是 1.21 随风弹加入的），改为一团白云模拟气流爆开，
        // 避免用 EXPLOSION_EMITTER 让每次命中看起来像爆炸
        level.sendParticles(ParticleTypes.CLOUD,
                origin.x, origin.y + victim.getBbHeight() * 0.5, origin.z,
                12, 0.35, 0.35, 0.35, 0.05);

        // ── 音效 ──
        // 1.20.1 没有 WIND_CHARGE_BURST，使用同为空气爆鸣的横扫音效代替
        level.playSound(null, origin.x, origin.y, origin.z,
                SoundEvents.PLAYER_ATTACK_SWEEP, victim.getSoundSource(),
                1.5F, 0.8F + level.getRandom().nextFloat() * 0.4F);

        // ── 风弹式击退 ──
        double power = 1.0 + enchantmentLevel * 0.6;
        AABB area = new AABB(
                origin.x - KNOCKBACK_RADIUS, origin.y - KNOCKBACK_RADIUS, origin.z - KNOCKBACK_RADIUS,
                origin.x + KNOCKBACK_RADIUS, origin.y + KNOCKBACK_RADIUS, origin.z + KNOCKBACK_RADIUS
        );

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area, e -> e != owner && e.isAlive())) {
            Vec3 entityCenter = entity.position().add(0, entity.getBbHeight() / 2.0, 0);
            double dist = entityCenter.distanceTo(origin);
            if (dist < 0.01 || dist > KNOCKBACK_RADIUS) continue;

            double decay = 1.0 - dist / KNOCKBACK_RADIUS;
            Vec3 dir = entityCenter.subtract(origin).normalize();
            double knock = decay * power;

            entity.push(
                    dir.x * KNOCKBACK_HORIZONTAL * knock,
                    KNOCKBACK_VERTICAL * knock,
                    dir.z * KNOCKBACK_HORIZONTAL * knock
            );
            entity.hurtMarked = true;
            entity.fallDistance = 0.0F;
        }
    }
}
