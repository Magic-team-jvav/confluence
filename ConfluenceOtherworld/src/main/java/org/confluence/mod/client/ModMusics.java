package org.confluence.mod.client;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import org.confluence.mod.common.init.ModSoundEvents;

public final class ModMusics {
    public static final CachedLocationMusic KING_SLIME = register(ModSoundEvents.KING_SLIME_COMBAT);
    public static final CachedLocationMusic EYE_OF_CTHULHU = register(ModSoundEvents.EYE_OF_CTHULHU_COMBAT);
    public static final CachedLocationMusic EATER_OF_WORLDS = register(ModSoundEvents.EATER_OF_WORLDS_COMBAT);
    public static final CachedLocationMusic BRAIN_OF_CTHULHU = register(ModSoundEvents.BRAIN_OF_CTHULHU_COMBAT);
    public static final CachedLocationMusic QUEEN_BEE = register(ModSoundEvents.QUEEN_BEE_COMBAT);

    public static final CachedLocationMusic OVERWORLD_DAY = register(ModSoundEvents.OVERWORLD_DAY);
    public static final CachedLocationMusic OVERWORLD_NIGHT = register(ModSoundEvents.OVERWORLD_NIGHT);
    public static final CachedLocationMusic UNDERGROUND = register(ModSoundEvents.UNDERGROUND);
    public static final CachedLocationMusic ALTERNATE_DAY = register(ModSoundEvents.ALTERNATE_DAY); // todo
    public static final CachedLocationMusic ALTERNATE_UNDERGROUND = register(ModSoundEvents.UNDERGROUND); // todo
    public static final CachedLocationMusic DESERT = register(ModSoundEvents.DESERT);
    public static final CachedLocationMusic ICE = register(ModSoundEvents.ICE);
    public static final CachedLocationMusic UNDERGROUND_ICE = register(ModSoundEvents.UNDERGROUND_ICE);
    public static final CachedLocationMusic CORRUPTION = register(ModSoundEvents.CORRUPTION);
    public static final CachedLocationMusic UNDERGROUND_CORRUPTION = register(ModSoundEvents.UNDERGROUND_CORRUPTION);
    public static final CachedLocationMusic CRIMSON = register(ModSoundEvents.CRIMSON);
    public static final CachedLocationMusic UNDERGROUND_CRIMSON = register(ModSoundEvents.UNDERGROUND_CRIMSON);
    public static final CachedLocationMusic HALLOW = register(ModSoundEvents.THE_HALLOW);
    public static final CachedLocationMusic UNDERGROUND_HALLOW = register(ModSoundEvents.UNDERGROUND_HALLOW);
    public static final CachedLocationMusic JUNGLE = register(ModSoundEvents.JUNGLE);
    public static final CachedLocationMusic JUNGLE_NIGHT = register(ModSoundEvents.JUNGLE_NIGHT);
    public static final CachedLocationMusic UNDERGROUND_JUNGLE = register(ModSoundEvents.UNDERGROUND_JUNGLE);
    public static final CachedLocationMusic OCEAN = register(ModSoundEvents.OCEAN);
    public static final CachedLocationMusic OCEAN_NIGHT = register(ModSoundEvents.OCEAN_NIGHT);
    public static final CachedLocationMusic RAIN = register(ModSoundEvents.RAIN);
    public static final CachedLocationMusic MORNING_RAIN = register(ModSoundEvents.MORNING_RAIN);
    public static final CachedLocationMusic STORM = register(ModSoundEvents.STORM);
    public static final CachedLocationMusic SPACE = register(ModSoundEvents.SPACE);
    public static final CachedLocationMusic MUSHROOMS = register(ModSoundEvents.MUSHROOMS);
    public static final CachedLocationMusic UNDERWORLD = register(ModSoundEvents.UNDERWORLD);

    private static CachedLocationMusic register(Holder<SoundEvent> event) {
        return new CachedLocationMusic(event, 0, 0, true);
    }

    public static class CachedLocationMusic extends Music {
        private ResourceLocation location;

        public CachedLocationMusic(Holder<SoundEvent> event, int minDelay, int maxDelay, boolean replaceCurrentMusic) {
            super(event, minDelay, maxDelay, replaceCurrentMusic);
        }

        public ResourceLocation getLocation() {
            if (location == null) {
                this.location = getEvent().value().getLocation();
            }
            return location;
        }
    }
}
