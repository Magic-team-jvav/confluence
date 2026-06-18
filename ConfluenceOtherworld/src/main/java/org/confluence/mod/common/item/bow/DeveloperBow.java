package org.confluence.mod.common.item.bow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModEntities;
import org.mesdag.portlib.wrapper.world.item.PortItem;

import static org.confluence.lib.common.component.ModRarity.MASTER;

public class DeveloperBow extends BaseTerraBowItem {
    public DeveloperBow() {
        super(9999F, new PortItem.PortProperties().component(ConfluenceMagicLib.MOD_RARITY, MASTER));
    }

    @Override
    public BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        return new BaseArrowEntity(ModEntities.ARROW_PROJECTILE.get(), shooter, ammo, weapon) {
            @Override public double getBaseDamage() { return 9999; }
            @Override protected void onHit(LivingEntity owner, LivingEntity target, boolean fullPull) { target.setRemainingFireTicks(target.getRemainingFireTicks() + 200 - tickCount); }
            @Override protected float getSpeedFactor() { return 2.0; }
            @Override protected int getPenetrationCount() { return 9999; }
            @Override public double getDefaultGravity() { return 0; }
        };
    }
}
