package org.confluence.mod.common.init;

import org.confluence.mod.common.init.gun.GunSounds;
import org.confluence.mod.common.init.gun.GunTrailColors;
import org.confluence.mod.common.init.item.GunItems;
import org.confluence.mod.common.init.item.ManaWeaponItems;

public final class ModGunProperties {
    public static void init() {
        GunSounds.putSound(ManaWeaponItems.BEE_GUN, ModSoundEvents.GUN_GENERIC);
        GunSounds.putSound(ManaWeaponItems.SPACE_GUN, ModSoundEvents.GUN_SPACE);
        GunSounds.putSound(GunItems.STAR_CANNON, ModSoundEvents.STAR);

        GunTrailColors.putColor("space_gun", 0xFF2CFD03);
    }
}
