package org.confluence.mod.common.init.gun;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.item.GunItems;
import org.confluence.mod.common.item.gun.BaseGun;
import org.mesdag.portlib.registries.PortDeferredItem;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GunSounds {
    protected static Map<BaseGun, SoundEvent> soundMap = new HashMap<>();

    public static void init() {
        putSound(GunItems.HAND_GUN, ModSoundEvents.GUN_AUTO);
        putSound(GunItems.PHOENIX_BLASTER, ModSoundEvents.GUN_AUTO);
        putSound(GunItems.SHOTGUN, ModSoundEvents.SHOTGUN_MULTI);
        putSound(GunItems.FLINTLOCK_PISTOL);
        putSound(GunItems.BOOMSTICK, ModSoundEvents.SHOTGUN_MULTI);
        putSound(GunItems.THE_UNDERTAKER);
        putSound(GunItems.MUSKET);
        putSound(GunItems.MINISHARK);
        putSound(GunItems.BLOWGUN, ModSoundEvents.BLOWPIPE_SHOT);
        putSound(GunItems.SNOWBALL_CANNON);
        putSound(GunItems.TACTICAL_SHOTGUN, ModSoundEvents.SHOTGUN_TACTICAL);
    }

    public static void putSound(PortDeferredItem<? extends BaseGun> item) {
        putSound(item, ModSoundEvents.GUN_GENERIC);
    }

    public static void putSound(PortDeferredItem<? extends BaseGun> item, Supplier<SoundEvent> soundEvent) {
        putSound(item.get(), soundEvent.get());
    }

    public static void putSound(BaseGun item, SoundEvent soundEvent) {
        soundMap.put(item, soundEvent);
    }

    public static SoundEvent getSound(ItemStack itemStack) {
        return getSound((BaseGun) itemStack.getItem());
    }

    public static SoundEvent getSound(BaseGun item) {
        return soundMap.get(item);
    }
}
