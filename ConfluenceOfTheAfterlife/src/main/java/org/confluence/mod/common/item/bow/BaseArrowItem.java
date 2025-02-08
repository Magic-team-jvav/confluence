package org.confluence.mod.common.item.bow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.projectile.BaseArrowEntity;
import org.confluence.terra_curio.common.component.ModRarity;
import org.confluence.terra_curio.common.init.TCDataComponentTypes;

public class BaseArrowItem extends ArrowItem {
    public BaseArrowItem(ModRarity rarity) {
        super(new Properties().component(TCDataComponentTypes.MOD_RARITY, rarity));
    }

    public AbstractArrow createArrow(Level pLevel, ItemStack pStack, LivingEntity pShooter,ItemStack weapon) {
        // todo 到时候做自定义箭矢的时候再改
        if(pStack.getItem() instanceof BaseArrowItem && BaseArrowEntity.selectArrowFromItemMap.containsKey(pStack.getItem())){
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

}
