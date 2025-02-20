package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;

import java.util.function.Supplier;

public final class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, Confluence.MODID);

    public static final Supplier<SoundEvent> TRANSMISSION = register("transmission");
    public static final Supplier<SoundEvent> WAVING = register("waving");
    public static final Supplier<SoundEvent> LASER = register("laser");
    public static final Supplier<SoundEvent> LIGHTSABER_QUICK = register("lightsaber_quick");
    public static final Supplier<SoundEvent> LIGHTSABER_SLOW = register("lightsaber_slow");
    public static final Supplier<SoundEvent> LIGHTSABER_OPEN = register("lightsaber_open");
    public static final Supplier<SoundEvent> REGULAR_STAFF_SHOOT = register("regular_staff_shoot");
    public static final Supplier<SoundEvent> SHOES_FLY = register("shoes_fly");
    public static final Supplier<SoundEvent> SHOES_FLY_JET = register("shoes_fly_jet");
    public static final Supplier<SoundEvent> SHOES_WALK = register("shoes_walk");
    public static final Supplier<SoundEvent> SHOOT = register("shoot"); // 枪射击
    public static final Supplier<SoundEvent> SPARKLE_SHOOT = register("sparkle_shoot"); // 法杖发射1
    public static final Supplier<SoundEvent> LIFE_CRYSTAL_USE = register("life_crystal_use");
    public static final Supplier<SoundEvent> MANA_STAR_USE = register("mana_star_use");
    public static final Supplier<SoundEvent> COINS = register("coins");
    public static final Supplier<SoundEvent> ROUTINE_HURT = register("routine_hurt");
    public static final Supplier<SoundEvent> ROUTINE_DEATH = register("routine_death");
    public static final Supplier<SoundEvent> COOLDOWN_RECOVERY = register("cooldown_recovery"); // CD冷却
    public static final Supplier<SoundEvent> FROZEN_ARROW = register("frozen_arrow"); // 冰雪射弹
    public static final Supplier<SoundEvent> FROZEN_BROKEN = register("frozen_broken"); // 冰雪射弹消失
    public static final Supplier<SoundEvent> SHIMMER_DETACHMENT = register("shimmer_detachment"); // 脱离微光
    public static final Supplier<SoundEvent> SHIMMER_EVOLUTION = register("shimmer_evolution"); // 嬗变
    public static final Supplier<SoundEvent> SHIMMER_IMMERSION = register("shimmer_immersion"); // 生物入微光
    public static final Supplier<SoundEvent> SHIMMER_ITEM_INTERACTIONS = register("shimmer_item_interactions"); // 物品入微光
    public static final Supplier<SoundEvent> STAR = register("star"); // 坠星
    public static final Supplier<SoundEvent> STAR_LANDS = register("star_lands"); // 星星落地
    public static final Supplier<SoundEvent> TERRA_OPERATION = register("terra_operation"); // 操作音效
    public static final Supplier<SoundEvent> USE_MOUNTS = register("use_mounts"); // 召唤坐骑
    public static final Supplier<SoundEvent> DECOUPLING = register("decoupling"); // 脱钩
    public static final Supplier<SoundEvent> ACHIEVEMENTS = register("achievements"); // 成就音效
    public static final Supplier<SoundEvent> TRANSMUTATION_USE = register("transmutation_use"); // 高级增益使用
    //空音效
    public static final Supplier<SoundEvent> ETERNAL_STARE_OF_THE_EYE_OF_CHAOS = register("eternal_stare_of_the_eye_of_chaos");
    public static final Supplier<SoundEvent> GEL_CROWN_SOVEREIGN = register("gel_crown_sovereign");
    public static final Supplier<SoundEvent> BRAIN_ABYSS_FLESH_ILLUSION= register("brain_abyss_flesh_illusion");
    public static final Supplier<SoundEvent> WORLD_WORM_ABYSS = register("world_worm_abyss");
    public static final Supplier<SoundEvent> CELESTIAL_HAMLET_MELODY = register("celestial_hamlet_melody");
    public static final Supplier<SoundEvent> INFERNAL_SPIRE_MARCH = register("infernal_spire_march");

    private static Supplier<SoundEvent> register(String name) {
        return EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(Confluence.asResource(name)));
    }

    public static class Types {
        public static final SoundType COIN = new DeferredSoundType(1.0F, 1.0F, COINS, COINS, COINS, COINS, COINS);
    }
}
