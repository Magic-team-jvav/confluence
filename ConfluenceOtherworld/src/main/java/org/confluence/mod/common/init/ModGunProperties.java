package org.confluence.mod.common.init;

import org.confluence.mod.common.init.item.GunItems;
import org.confluence.mod.common.init.item.ManaWeaponItems;
import org.confluence.terra_guns.common.init.TGGunSounds;
import org.confluence.terra_guns.common.init.TGSoundEvents;
import org.confluence.terra_guns.common.init.TGTrailColors;

public final class ModGunProperties {
    public static void init() {
        TGGunSounds.putSound(ManaWeaponItems.BEE_GUN, TGSoundEvents.GUN_GENERIC);
        TGGunSounds.putSound(ManaWeaponItems.SPACE_GUN, TGSoundEvents.GUN_SPACE);
        TGGunSounds.putSound(GunItems.STAR_CANNON, ModSoundEvents.STAR);

        TGTrailColors.putColor("space_gun", 0xFF2CFD03);
    }
}
