package org.confluence.mod.common.item.bow;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;
import org.mesdag.portlib.wrapper.world.item.PortItem;

import static org.confluence.lib.common.component.ModRarity.BLUE;

public class DemonBow extends BaseTerraBowItem {
    public DemonBow() {
        super(4.9F, new PortItem.PortProperties().component(ConfluenceMagicLib.MOD_RARITY, BLUE));
    }

    @Override
    public BaseArrowEntity createCustomArrow(LivingEntity shooter, ItemStack ammo, ItemStack weapon) {
        return new BaseArrowEntity(ModEntities.BASE_ARROW.get(), shooter, ammo, weapon) {
            @Override
            protected void onHit(LivingEntity owner, LivingEntity target, boolean fullPull) {
                if (!fullPull) return;
                var projectile = ModEntities.LIGHTS_BANE.get().create(owner.level()).addAttackDamage(7f);
                projectile.setOwner(owner);
                projectile.setPos(target.position().add(target.getRandom().nextFloat() * 0.2f, target.getEyeHeight() * 0.5f, target.getRandom().nextFloat() * 0.2f));
                owner.level().addFreshEntity(projectile);
            }
        };
    }
}
