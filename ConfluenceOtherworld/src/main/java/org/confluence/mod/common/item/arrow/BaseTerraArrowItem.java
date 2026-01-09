package org.confluence.mod.common.item.arrow;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.projectile.range.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.item.bow.BaseTerraBowItem;
import org.confluence.terraentity.registries.hit_effect.IEffectStrategy;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BaseTerraArrowItem extends ArrowItem {
    protected final @Nullable BaseArrowEntity.Factory modifier;
    protected BaseArrowEntity.Builder attributes;

    public BaseTerraArrowItem(ModRarity rarity) {
        this(rarity, null);
    }

    public BaseTerraArrowItem(ModRarity rarity, @Nullable BaseArrowEntity.Factory modifier) {
        super(new Properties().component(ConfluenceMagicLib.MOD_RARITY, rarity));
        this.modifier = modifier;
        if (modifier != null) {
            attributes = modifier.attr.get();
        }
    }

    public BaseArrowEntity.@Nullable Factory getModifier() {
        return modifier;
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack stack, LivingEntity shooter, @Nullable ItemStack weapon) {
        if (stack.getItem() instanceof BaseTerraArrowItem arrowItem && arrowItem.modifier != null) {
            BaseArrowEntity arrow;
            if (weapon != null && weapon.getItem() instanceof BaseTerraBowItem item) {
                arrow = new BaseArrowEntity(ModEntities.ARROW_PROJECTILE.get(), shooter, this.getDefaultInstance(), weapon, this, item.modifyArrowBuilder);
            } else {
                arrow = new BaseArrowEntity(ModEntities.ARROW_PROJECTILE.get(), shooter, this.getDefaultInstance(), weapon, this);
            }
            //arrow.setEffectsFromItem(stack);
            return arrow;
        }
        return super.createArrow(level, stack, shooter, weapon);
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        if (stack.getItem() instanceof BaseTerraArrowItem arrowItem && arrowItem.modifier != null) {
            //arrow.setEffectsFromItem(stack); -- 暂时简化代码，需要时再拆回来
            return new BaseArrowEntity(ModEntities.ARROW_PROJECTILE.get(), pos.x(), pos.y(), pos.z(), level, stack.copyWithCount(1), null, this);
        }
        return super.asProjectile(level, pos, stack, direction);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (attributes == null) {
            return;
        }

        tooltipComponents.add(Component.translatable("tooltip.item.confluence.additional_attack_damage").append(": +").append(String.format("%.1f", attributes.base_damage)).withColor(0x00FF00));

        if (attributes.onHitEffects != null) {
            IEffectStrategy.appendDescriptions(tooltipComponents, attributes.onHitEffects,
                    Component.translatable("tooltip.item.confluence.on_hit_effects").append(": ").withColor(0xFF00FF)
            );
        }

        if ((attributes.getType() & BaseArrowEntity.Tag.no_gravity) != 0) {
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.no_gravity"));
        }
        if ((attributes.getType() & BaseArrowEntity.Tag.cause_fire) != 0) {
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.cause_fire"));
        }
        if ((attributes.getType() & BaseArrowEntity.Tag.penetration) != 0) {
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.can_penetrate"));
        }
    }
}
