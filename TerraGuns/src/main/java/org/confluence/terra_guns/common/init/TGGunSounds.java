package org.confluence.terra_guns.common.init;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import org.confluence.terra_guns.common.item.gun.BaseGun;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.confluence.terra_guns.common.init.TGItems.*;
import static org.confluence.terra_guns.common.init.TGSoundEvents.*;

public class TGGunSounds {
    protected static Map<BaseGun, SoundEvent> soundMap = new HashMap<>();
    public static void init() {
        putSound(HAND_GUN, GUN_AUTO);
        putSound(PHOENIX_BLASTER, GUN_AUTO);
        putSound(SHOTGUN, SHOTGUN_MULTI);
        putSound(FLINTLOCK_PISTOL);
        putSound(BOOMSTICK, SHOTGUN_MULTI);
        putSound(THE_UNDERTAKER);
        putSound(MUSKET);
        putSound(MINISHARK);
        putSound(BLOWGUN, BLOWPIPE_SHOT);
        putSound(SNOWBALL_CANNON);
        putSound(TACTICAL_SHOTGUN, SHOTGUN_TACTICAL);
    }

    public static void putSound(DeferredItem<? extends BaseGun> item){
        putSound(item, GUN_GENERIC);
    }

    public static void putSound(DeferredItem<? extends BaseGun> item, Supplier<SoundEvent> soundEvent){
        putSound(item.get(), soundEvent.get());
    }

    public static void putSound(BaseGun item, SoundEvent soundEvent){
        soundMap.put(item, soundEvent);
    }

    public static SoundEvent getSound(ItemStack itemStack) {
        return getSound((BaseGun) itemStack.getItem());
    }
    public static SoundEvent getSound(BaseGun item) {
        return soundMap.get(item);
    }
}
