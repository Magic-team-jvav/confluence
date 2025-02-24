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

    public static final Supplier<SoundEvent> TRANSMISSION = register("transmission"); // 泰拉传送
    public static final Supplier<SoundEvent> WAVING = register("waving"); // 泰拉挥动
    public static final Supplier<SoundEvent> LIGHTSABER_OPEN = register("lightsaber_open"); // 光剑
    public static final Supplier<SoundEvent> REGULAR_STAFF_SHOOT = register("regular_staff_shoot"); // 法杖1
    public static final Supplier<SoundEvent> REGULAR_STAFF_SHOOT_2 = register("regular_staff_shoot_2"); // 法杖2
    public static final Supplier<SoundEvent> LIFE_CRYSTAL_USE = register("life_crystal_use");// 生命水晶
    public static final Supplier<SoundEvent> MANA_STAR_USE = register("mana_star_use"); // 魔力水晶
    public static final Supplier<SoundEvent> COINS = register("coins"); // 币
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
    // boss音乐包其一
    public static final Supplier<SoundEvent> ETERNAL_STARE_OF_THE_EYE_OF_CHAOS = register("eternal_stare_of_the_eye_of_chaos"); // 克眼
    public static final Supplier<SoundEvent> GEL_CROWN_SOVEREIGN = register("gel_crown_sovereign"); // 史王
    public static final Supplier<SoundEvent> BRAIN_ABYSS_FLESH_ILLUSION= register("brain_abyss_flesh_illusion"); // 克脑
    public static final Supplier<SoundEvent> WORLD_WORM_ABYSS = register("world_worm_abyss"); // 世吞
    public static final Supplier<SoundEvent> CELESTIAL_HAMLET_MELODY = register("celestial_hamlet_melody");  // 空岛村
    public static final Supplier<SoundEvent> INFERNAL_SPIRE_MARCH = register("infernal_spire_march"); // 地狱尖塔

    // 若干音乐包，都带上other_world
    public static final Supplier<SoundEvent> OTHER_WORLD = register("other_world"); // 汇流常规主题曲

    // 泰拉原版音乐包
    public static final Supplier<SoundEvent> BOSS_1 = register("boss_1"); // 克眼,史王，世吞
    public static final Supplier<SoundEvent> BOSS_2 = register("boss_2"); // 双子魔眼，血肉墙（暂不用）
    public static final Supplier<SoundEvent> BOSS_3 = register("boss_3"); // 克脑 毁灭者 火把神
    public static final Supplier<SoundEvent> QUEEN_BEE = register("queen_bee"); // 蜂王

    public static final Supplier<SoundEvent> OVERWORLD_DAY = register("overworld_day"); // 地表白天
    public static final Supplier<SoundEvent> ALTERNATE_DAY= register("alternate_day"); // 地表白天
    public static final Supplier<SoundEvent> EERIE = register("eerie"); // 血月事件使用
    public static final Supplier<SoundEvent> OVERWORLD_NIGHT = register("overworld_night"); // 地表夜晚
    public static final Supplier<SoundEvent> UNDERGROUND = register("underground"); // 地下 y40——y64可以触发
    public static final Supplier<SoundEvent> ALTERNATE_UNDERGROUND= register("alternate_underground"); // 地下 y40——y64可以触发
    public static final Supplier<SoundEvent> JUNGLE = register("jungle"); // 丛林白天
    public static final Supplier<SoundEvent> UNDERGROUND_JUNGLE = register("underground_jungle"); // 丛林地下 y40——y64可以触发
    public static final Supplier<SoundEvent> JUNGLE_NIGHT = register("jungle_night"); // 丛林夜晚
    public static final Supplier<SoundEvent> CORRUPTION = register("corruption"); // 腐化地表
    public static final Supplier<SoundEvent> UNDERGROUND_CORRUPTION = register("underground_corruption"); // 腐化地下 y40——y64可以触发
    public static final Supplier<SoundEvent> THE_HALLOW = register("the_hallow"); // 神圣地表
    public static final Supplier<SoundEvent> UNDERGROUND_HALLOW = register("underground_hallow"); // 神圣地下 y40——y64可以触发
    public static final Supplier<SoundEvent> OCEAN = register("ocean"); // 海洋以及海边
    public static final Supplier<SoundEvent> OCEAN_NIGHT = register("ocean_night"); // 海洋以及海边夜晚
    public static final Supplier<SoundEvent> HIGH_WIND = register("high_wind"); // 大风
    public static final Supplier<SoundEvent> RAIN = register("rain"); // 下雨
    public static final Supplier<SoundEvent> MORNING_RAIN = register("morning_rain"); // 晨雨
    public static final Supplier<SoundEvent> STORM = register("storm"); // 暴风雨
    public static final Supplier<SoundEvent> SPACE = register("space"); // 太空 超过y320
    public static final Supplier<SoundEvent> SPACE_DAY = register("space_day"); // 太空白天 超过y320
    public static final Supplier<SoundEvent> MUSHROOMS = register("mushrooms"); // 发光蘑菇群系
    public static final Supplier<SoundEvent> CRIMSON = register("crimson"); // 猩红地表
    public static final Supplier<SoundEvent> UNDERGROUND_CRIMSON = register("underground_crimson"); // 猩红地下 y40——y64可以触发
    public static final Supplier<SoundEvent> ICE = register("ice"); // 冰雪群系地表
    public static final Supplier<SoundEvent> UNDERGROUND_ICE = register("underground_ice"); // 冰雪群系地表
    public static final Supplier<SoundEvent> UNDERWORLD = register("underworld"); // 灰烬群系，白蜡木林
    public static final Supplier<SoundEvent> SLIME_RAIN = register("slime_rain"); // 史莱姆雨事件使用
    public static final Supplier<SoundEvent> DESERT = register("desert"); // 沙漠
    public static final Supplier<SoundEvent> UNDERGROUND_DESERT = register("underground_desert"); // 沙漠地下 y40——y64可以触发
    public static final Supplier<SoundEvent> TOWN_DAY = register("town_day"); // 村庄，城镇白天
    public static final Supplier<SoundEvent> TOWN_NIGHT = register("town_night"); // 村庄，城镇夜晚
    public static final Supplier<SoundEvent> AETHER = register("aether"); // 以太

    // 泰拉异界音乐包
    public static final Supplier<SoundEvent> SINKHOLES_OF_DARKNESS= register("sinkholes_of_darkness");
    // 独眼巨鹿 猪龙鱼公爵 光之女皇 石巨人 拜月教邪教徒 史莱姆皇后 机械骷髅王 毁灭者 双子魔眼
    public static final Supplier<SoundEvent> BEHOLD_THE_OCTOEYE= register("behold_the_octoeye");
    // 克苏鲁之脑 世界吞噬怪 克苏鲁之眼 史莱姆王 机械美杜莎 蜂王 骷髅王

    public static final Supplier<SoundEvent> PRELUDE = register("prelude"); // 下雨，晨雨，雷雨
    public static final Supplier<SoundEvent> EVERY_ADVENTURE_HAS_A_BEGINNING = register("every_adventure_has_a_beginning");
    // 白天↑
    public static final Supplier<SoundEvent> NIGHT_FALLS_DARKNESS_EMERGES = register("night_falls_darkness_emerges");
    // 晚上，下雨的晚上，神圣之地的晚上↑
    public static final Supplier<SoundEvent> BELOW_THE_SURFACE = register("below_the_surface");
    // 地下↑
    public static final Supplier<SoundEvent> SECRET_OF_THE_SANDS= register("secret_of_the_sands");
    // 沙漠，地下沙漠
    public static final Supplier<SoundEvent> ENCHANTED_BLUE= register("enchanted_blue");
    // 海边，海洋
    public static final Supplier<SoundEvent> CELESTIAL_CAVERNS= register("celestial_caverns");
    // 发光蘑菇群系
    public static final Supplier<SoundEvent> THE_ENDLESS_VOID= register("the_endless_void");
    // 太空
    public static final Supplier<SoundEvent> JOURNEY_TO_THE_CORE= register("journey_to_the_core");
    // 汇集群系，白蜡木林
    public static final Supplier<SoundEvent> GLIMMERS_OF_VIBRANCE= register("glimmers_of_vibrance");
    // 冰雪，以太
    public static final Supplier<SoundEvent> CORRUPTION_OTHER_WORLD= register("corruption_other_world");
    // 腐化地表
    public static final Supplier<SoundEvent> DECAY_AND_CORROSION= register("decay_and_corrosion");
    // 腐化地下
    public static final Supplier<SoundEvent> BLOOD_CRAWLERS= register("blood_crawlers");
    // 猩红地表
    public static final Supplier<SoundEvent> CRIMSON_CHASM= register("crimson_chasm");
    // 猩红地下
    public static final Supplier<SoundEvent> SWEET_MENACE= register("sweet_menace");
    // 冰雪
    public static final Supplier<SoundEvent> TWISTED_VIRTUE= register("twisted_virtue");
    // 神圣地下
    public static final Supplier<SoundEvent> ENTER_DARKNESS= register("enter_darkness");
    // 血月，日食，墓地，陨石

    public static final Supplier<SoundEvent> SKY_GUARDIAN= register("sky_guardian");
    // 丛林 地下丛林
    public static final Supplier<SoundEvent> WALL_OF_FLESH= register("wall_of_flesh");
    // 火把神，血肉墙
    public static final Supplier<SoundEvent> POSTLUDE_CREDITS= register("postlude_credits");
    // 神圣白天


    private static Supplier<SoundEvent> register(String name) {
        return EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(Confluence.asResource(name)));
    }

    public static class Types {
        public static final SoundType COIN = new DeferredSoundType(1.0F, 1.0F, COINS, COINS, COINS, COINS, COINS);
    }
}
