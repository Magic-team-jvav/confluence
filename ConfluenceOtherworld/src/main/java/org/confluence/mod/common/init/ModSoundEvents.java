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
    // 若干音乐包，都带上other_world
   public static final Supplier<SoundEvent> OTHER_WORLD = register("other_world"); // 汇流常规主题曲

    // 音乐包
    public static final Supplier<SoundEvent> KING_SLIME_COMBAT = register("king_slime_combat"); // 史王，
    public static final Supplier<SoundEvent> EYE_OF_CTHULHU_COMBAT = register("eye_of_cthulhu_combat"); // 克眼
    public static final Supplier<SoundEvent> EATER_OF_WORLDS_COMBAT = register("eater_of_worlds_combat"); // 世吞
    public static final Supplier<SoundEvent> BRAIN_OF_CTHULHU_COMBAT = register("brain_of_cthulhu_combat"); // 克脑
    public static final Supplier<SoundEvent> QUEEN_BEE_COMBAT = register("queen_bee_combat"); // 蜂王

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

    private static Supplier<SoundEvent> register(String name) {
        return EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(Confluence.asResource(name)));
    }

    public static class Types {
        public static final SoundType COIN = new DeferredSoundType(1.0F, 1.0F, COINS, COINS, COINS, COINS, COINS);
    }
}
