package org.confluence.mod.common.item.sword;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.lib.util.LibUtils;
import org.jetbrains.annotations.Nullable;

/// 大马林鱼刺剑。
///
/// 普通攻击永远是「1 段」伤害。二段跳或提升一次移动速度档位（静止→行走→奔跑）会额外积攒 1 段，
/// 每段折算 +3 伤害，最多 8 段（共 +24）。命中时：
/// - 未满 8 段：积攒的加成一并算进同一次伤害结算（基础值 + 3 × 段数），段数在命中后清空；
/// - 已满 8 段：主命中带 1 段加成，随后逐段独立补刀，总加成依旧是 +24。
///
/// 段数只存在于物品数据里，不额外显示进度条。
public class MarlinSpikeSwordItem extends BaseSwordItem {
    public static final int MAX_SEGMENTS = 8;
    /// 每段积攒折算的额外伤害。
    public static final float DAMAGE_PER_SEGMENT = 3.0F;
    private static final String SEGMENTS_KEY = "confluence:marlin_spike_segments";
    /// 玩家上一次被记录的移动档位，0 = 静止，1 = 行走，2 = 奔跑。
    private static final String MOVE_TIER_KEY = "confluence:marlin_spike_move_tier";

    public MarlinSpikeSwordItem(Tier tier, ModRarity rarity, int rawDamage, float rawSpeed, SwordDefinition.Builder builder) {
        super(tier, rarity, rawDamage, rawSpeed, builder);
    }

    public static int getSegments(ItemStack stack) {
        CompoundTag tag = LibUtils.getItemStackNbtNoCopy(stack);
        return tag.getInt(SEGMENTS_KEY);
    }

    public static void setSegments(ItemStack stack, int segments) {
        int clamped = Mth.clamp(segments, 0, MAX_SEGMENTS);
        LibUtils.updateItemStackNbt(stack, tag -> {
            if (clamped <= 0) {
                tag.remove(SEGMENTS_KEY);
            } else {
                tag.putInt(SEGMENTS_KEY, clamped);
            }
        });
    }

    /// 二段跳与提升移动档位共用的积攒入口，满段后不再累加。
    public static void addSegment(ItemStack stack) {
        int segments = getSegments(stack);
        if (segments < MAX_SEGMENTS) {
            setSegments(stack, segments + 1);
        }
    }

    /// 二段跳的触发入口，由 terra_curio 的跳跃协议转发。
    public static void onMultiJump(Player player) {
        if (player.level().isClientSide) return;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof MarlinSpikeSwordItem) {
                addSegment(stack);
            }
        }
        ItemStack offhand = player.getOffhandItem();
        if (offhand.getItem() instanceof MarlinSpikeSwordItem) {
            addSegment(offhand);
        }
    }

    @Override
    protected void onInventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof Player player)) return;
        int tier = movementTier(player);
        CompoundTag data = LibEntityUtils.getOrCreatePersistedData(player);
        int last = data.getInt(MOVE_TIER_KEY);
        if (tier > last) {
            addSegment(stack);
        }
        data.putInt(MOVE_TIER_KEY, tier);
    }

    /// 0 = 几乎静止，1 = 在地面上行走，2 = 疾跑。飞行、骑乘与游泳不计档位变化。
    private static int movementTier(Player player) {
        if (player.isPassenger() || player.getAbilities().flying || player.isFallFlying()) return 0;
        if (player.isSprinting()) return 2;
        Vec3 motion = player.getDeltaMovement();
        double horizontalSqr = motion.x * motion.x + motion.z * motion.z;
        return horizontalSqr > 0.0025 ? 1 : 0;
    }

    @Override
    public float modifyDamage(ItemStack stack, DamageSource source, @Nullable Entity attacker, LivingEntity victim, float amount) {
        int segments = getSegments(stack);
        if (segments >= MAX_SEGMENTS) return amount + DAMAGE_PER_SEGMENT; // 满段：主命中只带 1 段，其余由补刀补齐
        return amount + DAMAGE_PER_SEGMENT * segments;
    }

    @Override
    protected void afterHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        int segments = getSegments(stack);
        if (segments <= 0) return;
        // 先清空再补刀：补刀会重新走一遍近战伤害结算，段数不清零就会被重复放大。
        setSegments(stack, 0);
        if (segments < MAX_SEGMENTS) return;
        if (!(attacker instanceof Player player) || player.level().isClientSide) return;
        DamageSource source = player.damageSources().playerAttack(player);
        // 第一段已经算在主命中里，这里补剩下的 7 段，合计 +24。
        for (int i = 1; i < MAX_SEGMENTS; i++) {
            target.invulnerableTime = 0;
            target.hurt(source, DAMAGE_PER_SEGMENT);
        }
    }
}
