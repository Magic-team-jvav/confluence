package org.confluence.mod.common.item.bow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.entity.projectile.arrow.WindArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/// 风弦：把普通木箭替换成风矢，并且对具有击退抗性的敌怪额外增伤。
///
/// 增伤区间按目标击退抗性 0.1~1 线性映射到 10%~20%。
public class WindStringBowItem extends BaseTerraBowItem {
    private static final double KNOCKBACK_RESISTANCE_START = 0.1;
    private static final float BONUS_MIN = 0.10F;
    private static final float BONUS_MAX = 0.20F;

    public WindStringBowItem(float baseDamage, int durability) {
        super(baseDamage, new Item.Properties().durability(durability));
    }

    /// 木箭（以及其它未指定的普通箭）在射出时替换为风矢。
    @Override
    public @Nullable BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        // 只替换普通木箭，其它箭矢保持原样。
        if (!ammo.is(Items.ARROW)) return null;
        WindArrowEntity arrow = new WindArrowEntity(ModEntities.WIND_ARROW.get(), shooter, ammo.copyWithCount(1), weapon);
        arrow.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
        arrow.setOwner(shooter);
        return arrow;
    }

    @Override
    public void modifyArrowEntity(BaseArrowEntity entity) {
        entity.addDamageModifier(WindStringBowItem::applyKnockbackBonus);
    }

    /// 击退抗性 0.1 -> +10%，1.0 -> +20%。
    public static float applyKnockbackBonus(LivingEntity target, float damage) {
        double resistance = target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        if (resistance < KNOCKBACK_RESISTANCE_START) return damage;
        double ratio = Math.min(1.0, (resistance - KNOCKBACK_RESISTANCE_START) / (1.0 - KNOCKBACK_RESISTANCE_START));
        float bonus = BONUS_MIN + (float) ratio * (BONUS_MAX - BONUS_MIN);
        return damage * (1.0F + bonus);
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable net.minecraft.world.level.Level level, List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(net.minecraft.network.chat.Component.translatable("tooltip.item.confluence.wind_string.0")
                .withStyle(net.minecraft.ChatFormatting.GRAY));
    }
}
