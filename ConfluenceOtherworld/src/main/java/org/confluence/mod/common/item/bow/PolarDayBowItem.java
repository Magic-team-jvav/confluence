package org.confluence.mod.common.item.bow;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.common.effect.harmful.PolarMarkEffect;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.tooltipcomponent.AltImageComponent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/// 极昼：命中给敌怪挂上「极昼灼烧」。
///
/// 1 级：以自身为中心、跟随自身的 3x3 光环，每秒造成 10% 武器攻击力伤害。
/// 2 级：在 1 级基础上每秒迸发一发日光弹幕（50% 武器攻击力），索敌附近的其它敌怪。
/// 再次命中会升级印记（最高 2 级）并延长 5 秒；对 2 级印记累计命中足够多次可获得「极昼环」，
/// 期间射出的箭矢自带 5x5 光环，接触到的敌怪直接获得极昼灼烧。
public class PolarDayBowItem extends BaseTerraBowItem {
    /// 首次命中施加的极昼灼烧时长（tick）
    public static final int MARK_DURATION = 120;
    /// 再次命中时延长的时长（5 秒）
    public static final int MARK_EXTEND = 100;
    /// 击中 2 级印记累计超过该次数后给予极昼环
    public static final int RING_HIT_THRESHOLD = 10;
    /// 极昼环持续时长（3 分钟）
    public static final int RING_DURATION = 3600;
    /// 极昼环箭矢的光环半径（5x5）
    private static final double RING_AURA_RADIUS = 2.5;
    private TooltipComponent tooltipImage;

    /// 无耐久：不掉耐久，也不需要修复。
    public PolarDayBowItem(float baseDamage) {
        super(baseDamage, new Item.Properties());
    }

    /// 与裂天剑一致，在 tooltip 里额外画出大图预览（弓只取静态那一张，不需要蓄力多态）
    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (tooltipImage == null) {
            this.tooltipImage = AltImageComponent.of(stack.getItem());
        }
        return Optional.of(tooltipImage);
    }

    @Override
    public void modifyArrowEntity(BaseArrowEntity entity) {
        entity.addWeaponHitEffect((owner, target, fullPull) -> markOnHit(owner, target));
        entity.addTickEffect(this::ringAura);
    }

    /// 命中既有的印记会升级并延长；打满 2 级印记的命中次数会给予极昼环
    private void markOnHit(LivingEntity owner, LivingEntity target) {
        if (owner.level().isClientSide) return;
        MobEffectInstance current = target.getEffect(ModEffects.POLAR_MARK.get());
        boolean wasLevelTwo = current != null && current.getAmplifier() >= 1;
        int duration = current == null ? MARK_DURATION : current.getDuration() + MARK_EXTEND;
        int amplifier = current == null ? 0 : Math.min(current.getAmplifier() + 1, 1);
        PolarMarkEffect.apply(target, owner, getFullDrawDamage(), duration, amplifier);
        if (!wasLevelTwo) return;
        CompoundTag data = target.getPersistentData();
        int hits = data.getInt(PolarMarkEffect.HITS_KEY) + 1;
        if (hits > RING_HIT_THRESHOLD) {
            hits = 0;
            owner.addEffect(new MobEffectInstance(ModEffects.POLAR_RING.get(), RING_DURATION, 0), owner);
        }
        data.putInt(PolarMarkEffect.HITS_KEY, hits);
    }

    /// 极昼环：箭矢周围 5x5 内的敌怪被赋予 1 级极昼灼烧（只赋予，不叠加）
    private void ringAura(BaseArrowEntity arrow) {
        if (!(arrow.getOwner() instanceof LivingEntity owner) || !owner.hasEffect(ModEffects.POLAR_RING.get())) return;
        Level level = arrow.level();
        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, new AABB(arrow.position(), arrow.position()).inflate(RING_AURA_RADIUS))) {
            if (!victim.isAlive() || victim.isSpectator() || !LibEntityUtils.canHit(owner, victim)) continue;
            if (victim.hasEffect(ModEffects.POLAR_MARK.get())) continue;
            PolarMarkEffect.apply(victim, owner, getFullDrawDamage(), MARK_DURATION, 0);
        }
    }

    /// 原版箭会被建造成普通 Arrow，接不上模组箭矢的命中钩子（极昼灼烧就不会生效）；
    /// 这里把原版箭换成通用箭实体，模组箭矢本身带钩子，保持原样。
    @Override
    public @Nullable BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        if (!ammo.is(Items.ARROW)) return null;
        BaseArrowEntity arrow = new BaseArrowEntity(ModEntities.BASE_ARROW.get(), shooter, ammo.copyWithCount(1), weapon);
        arrow.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
        arrow.setOwner(shooter);
        return arrow;
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.item.confluence.polar_day.0").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.item.confluence.polar_day.1").withStyle(ChatFormatting.GRAY));
    }
}
