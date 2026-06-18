package org.confluence.mod.common.item.bow;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModEntities;
import org.mesdag.portlib.wrapper.world.item.PortItem;

import static org.confluence.lib.common.component.ModRarity.BLUE;

public class HuntingBow extends BaseTerraBowItem {
    public HuntingBow() {
        super(3.5F, new PortItem.PortProperties().component(ConfluenceMagicLib.MOD_RARITY, BLUE));
    }

    @Override
    public BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        return new BaseArrowEntity(ModEntities.ARROW_PROJECTILE.get(), shooter, ammo, weapon) {
            @Override
            protected void onHit(LivingEntity owner, LivingEntity target, boolean fullPull) {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 160, 0));
            }
        };
    }
}
