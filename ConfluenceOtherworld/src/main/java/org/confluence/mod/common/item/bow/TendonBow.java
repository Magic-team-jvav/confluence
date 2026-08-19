package org.confluence.mod.common.item.bow;

import net.minecraft.world.effect.MobEffectInstance;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.entity.projectile.arrow.BaseArrowEntity;
import org.confluence.mod.common.init.ModEffects;

import static org.confluence.lib.common.component.ModRarity.BLUE;

public class TendonBow extends BaseTerraBowItem {
    public TendonBow() {
        super(5.2F, new Properties().component(ConfluenceMagicLib.MOD_RARITY, BLUE));
    }

    @Override
    public void modifyArrowEntity(BaseArrowEntity entity) {
        entity.addWeaponHitEffect((owner, target, fullPull) -> {
            if (fullPull)
                target.addEffect(new MobEffectInstance(ModEffects.BLOOD_BUTCHERED.get(), 180, 4));
        });
    }
}
