package org.confluence.mod.common.item.bow;

import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.entity.ModEntities;

import static org.confluence.lib.common.component.ModRarity.BLUE;

public class DemonBow extends BaseTerraBowItem {
    public DemonBow() {
        super(4.9F, new Properties().component(ConfluenceMagicLib.MOD_RARITY, BLUE));
    }

    @Override
    protected boolean hasFullPullHitEffect() {
        return true;
    }

    @Override
    public void modifyArrowEntity(BaseArrowEntity entity) {
        entity.addWeaponHitEffect((owner, target, fullPull) -> {
            if (!fullPull) return;
            var projectile = ModEntities.LIGHTS_BANE.get().create(owner.level()).addAttackDamage(7.0F);
            projectile.setOwner(owner);
            projectile.setPos(target.position().add(target.getRandom().nextFloat() * 0.2F, target.getEyeHeight() * 0.5F, target.getRandom().nextFloat() * 0.2F));
            owner.level().addFreshEntity(projectile);
        });
    }
}
