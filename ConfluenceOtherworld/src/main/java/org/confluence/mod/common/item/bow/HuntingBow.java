package org.confluence.mod.common.item.bow;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;

import static org.confluence.lib.common.component.ModRarity.BLUE;

public class HuntingBow extends BaseTerraBowItem {
    public HuntingBow() {
        super(3.5F, new Properties().component(ConfluenceMagicLib.MOD_RARITY, BLUE));
    }

    @Override
    public void modifyArrowEntity(BaseArrowEntity entity) {
        entity.addWeaponHitEffect((owner, target, fullPull) -> target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 160, 0)));
    }
}
