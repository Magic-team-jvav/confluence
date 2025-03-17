package org.confluence.mod.common.item.bow;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.mod.common.entity.projectile.BaseArrowEntity;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;
import org.confluence.terraentity.entity.proj.hit_effect.EffectStrategy;

import javax.annotation.Nullable;
import java.util.List;

public class BaseArrowItem extends ArrowItem {
    private final BaseArrowEntity.Tuple modifier;
    BaseArrowEntity.Builder attributes;
    public BaseArrowItem(ModRarity rarity) {
        this(rarity,null);
    }
    public BaseArrowItem(ModRarity rarity,@Nullable BaseArrowEntity.Tuple modifier) {
        super(new Properties().component(TCDataComponentTypes.MOD_RARITY, rarity));
        this.modifier=modifier;
        if(modifier!=null)
            attributes = modifier.attr.get();
    }

    public BaseArrowEntity.Tuple getModifier() {
        return modifier;
    }

    public AbstractArrow createArrow(Level pLevel, ItemStack pStack, LivingEntity pShooter,ItemStack weapon) {
        // todo 到时候做自定义箭矢的时候再改
        if(pStack.getItem() instanceof BaseArrowItem arrowItem && arrowItem.modifier != null){
            BaseArrowEntity arrow;
            if(weapon.getItem() instanceof TerraBowItem item){
                arrow= new BaseArrowEntity(pShooter, this.getDefaultInstance(), weapon, this, item.modifyArrowBuilder);
            }else{
                arrow= new BaseArrowEntity(pShooter, this.getDefaultInstance(), weapon, this);
            }
            //arrow.setEffectsFromItem(pStack);
            return arrow;
        }
        return super.createArrow(pLevel, pStack, pShooter,weapon);
    }

    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        if(attributes!=null){
            tooltipComponents.add(Component.translatable("tooltip.item.confluence.additional_attack_damage").append(": +").append(String.format("%.1f", attributes.base_damage)).withColor(0x00FF00));

            EffectStrategy.appendDescription(tooltipComponents, attributes.onHitEffect.stream().map(DeferredHolder::get).toList(),
                    Component.translatable("tooltip.item.confluence.on_hit_effects").append(": ").withColor(0xFF00FF)
            );

            if((attributes.getType() & BaseArrowEntity.Tag.low_gravity) != 0){
                tooltipComponents.add(Component.translatable("tooltip.item.confluence.no_gravity"));
            }
            if((attributes.getType() & BaseArrowEntity.Tag.cause_fire) != 0){
                tooltipComponents.add(Component.translatable("tooltip.item.confluence.cause_fire"));
            }
            if((attributes.getType() & BaseArrowEntity.Tag.penetration) != 0){
                tooltipComponents.add(Component.translatable("tooltip.item.confluence.can_penetrate"));
            }
        }

    }
}
