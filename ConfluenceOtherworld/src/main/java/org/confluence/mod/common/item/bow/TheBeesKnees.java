package org.confluence.mod.common.item.bow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.entity.projectile.arrow.BeeArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.item.arrow.BaseTerraArrowItem;
import org.mesdag.portlib.wrapper.world.item.PortItem;

import static org.confluence.lib.common.component.ModRarity.YELLOW;

public class TheBeesKnees extends BaseTerraBowItem {
    public TheBeesKnees() {
        super(6.7F, new PortItem.PortProperties().component(ConfluenceMagicLib.MOD_RARITY, YELLOW));
    }

    @Override
    protected int getMultiShootCount() { return 3; }

    @Override
    protected Vec3 getMultiShootOffset(int shootingIndex, int shootingTotality) {
        return new Vec3(-shootingIndex * 0.25f, 0, 0);
    }

    @Override
    protected boolean canMultiShoot(ItemStack ammo) {
        return !(ammo.getItem() instanceof BaseTerraArrowItem);
    }

    @Override
    public BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        return new BeeArrowEntity(ModEntities.BEE_ARROW.get(), shooter, ammo.copyWithCount(1), weapon);
    }
}
