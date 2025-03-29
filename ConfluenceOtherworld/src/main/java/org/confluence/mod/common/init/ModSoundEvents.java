package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;

public final class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, Confluence.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> TRANSMISSION = register("transmission"); // 泰拉传送
    public static final DeferredHolder<SoundEvent, SoundEvent> LIGHTSABER_OPEN = register("lightsaber_open"); // 光剑
    public static final DeferredHolder<SoundEvent, SoundEvent> REGULAR_STAFF_SHOOT = register("regular_staff_shoot"); // 法杖1
    public static final DeferredHolder<SoundEvent, SoundEvent> REGULAR_STAFF_SHOOT_2 = register("regular_staff_shoot_2"); // 法杖2
    public static final DeferredHolder<SoundEvent, SoundEvent> LIFE_CRYSTAL_USE = register("life_crystal_use");// 生命水晶
    public static final DeferredHolder<SoundEvent, SoundEvent> MANA_STAR_USE = register("mana_star_use"); // 魔力水晶
    public static final DeferredHolder<SoundEvent, SoundEvent> COINS = register("coins"); // 币
    public static final DeferredHolder<SoundEvent, SoundEvent> COOLDOWN_RECOVERY = register("cooldown_recovery"); // CD冷却
    public static final DeferredHolder<SoundEvent, SoundEvent> BOW_COOLDOWN_RECOVERY = register("bow_cooldown_recovery"); // CD冷却-弓箭
    public static final DeferredHolder<SoundEvent, SoundEvent> FROZEN_ARROW = register("frozen_arrow"); // 冰雪射弹
    public static final DeferredHolder<SoundEvent, SoundEvent> FROZEN_BROKEN = register("frozen_broken"); // 冰雪射弹消失
    public static final DeferredHolder<SoundEvent, SoundEvent> SHIMMER_DETACHMENT = register("shimmer_detachment"); // 脱离微光
    public static final DeferredHolder<SoundEvent, SoundEvent> SHIMMER_EVOLUTION = register("shimmer_evolution"); // 嬗变
    public static final DeferredHolder<SoundEvent, SoundEvent> SHIMMER_IMMERSION = register("shimmer_immersion"); // 生物入微光
    public static final DeferredHolder<SoundEvent, SoundEvent> SHIMMER_ITEM_INTERACTIONS = register("shimmer_item_interactions"); // 物品入微光
    public static final DeferredHolder<SoundEvent, SoundEvent> STAR = register("star"); // 坠星
    public static final DeferredHolder<SoundEvent, SoundEvent> STAR_LANDS = register("star_lands"); // 星星落地
    public static final DeferredHolder<SoundEvent, SoundEvent> TERRA_OPERATION = register("terra_operation"); // 操作音效
    public static final DeferredHolder<SoundEvent, SoundEvent> USE_MOUNTS = register("use_mounts"); // 召唤坐骑
    public static final DeferredHolder<SoundEvent, SoundEvent> DECOUPLING = register("decoupling"); // 脱钩
    public static final DeferredHolder<SoundEvent, SoundEvent> ACHIEVEMENTS = register("achievements"); // 成就音效
    public static final DeferredHolder<SoundEvent, SoundEvent> TRANSMUTATION_USE = register("transmutation_use"); // 高级增益使用

    // 若干音乐包，都带上otherworld
    public static final DeferredHolder<SoundEvent, SoundEvent> OTHERWORLD = register("otherworld"); // 汇流常规主题曲

    // 音乐包
    public static final DeferredHolder<SoundEvent, SoundEvent> KING_SLIME_COMBAT = register("king_slime_combat"); // 史王，
    public static final DeferredHolder<SoundEvent, SoundEvent> EYE_OF_CTHULHU_COMBAT = register("eye_of_cthulhu_combat"); // 克眼
    public static final DeferredHolder<SoundEvent, SoundEvent> EATER_OF_WORLDS_COMBAT = register("eater_of_worlds_combat"); // 世吞
    public static final DeferredHolder<SoundEvent, SoundEvent> BRAIN_OF_CTHULHU_COMBAT = register("brain_of_cthulhu_combat"); // 克脑
    public static final DeferredHolder<SoundEvent, SoundEvent> QUEEN_BEE_COMBAT = register("queen_bee_combat"); // 蜂王

    public static final DeferredHolder<SoundEvent, SoundEvent> OVERWORLD_DAY = register("overworld_day"); // 地表白天
    public static final DeferredHolder<SoundEvent, SoundEvent> ALTERNATE_DAY = register("alternate_day"); // 地表白天
    public static final DeferredHolder<SoundEvent, SoundEvent> EERIE = register("eerie"); // 血月事件使用
    public static final DeferredHolder<SoundEvent, SoundEvent> OVERWORLD_NIGHT = register("overworld_night"); // 地表夜晚
    public static final DeferredHolder<SoundEvent, SoundEvent> UNDERGROUND = register("underground"); // 地下 y40——y64可以触发
    public static final DeferredHolder<SoundEvent, SoundEvent> ALTERNATE_UNDERGROUND = register("alternate_underground"); // 地下 y40——y64可以触发
    public static final DeferredHolder<SoundEvent, SoundEvent> JUNGLE = register("jungle"); // 丛林白天
    public static final DeferredHolder<SoundEvent, SoundEvent> UNDERGROUND_JUNGLE = register("underground_jungle"); // 丛林地下 y40——y64可以触发
    public static final DeferredHolder<SoundEvent, SoundEvent> JUNGLE_NIGHT = register("jungle_night"); // 丛林夜晚
    public static final DeferredHolder<SoundEvent, SoundEvent> CORRUPTION = register("corruption"); // 腐化地表
    public static final DeferredHolder<SoundEvent, SoundEvent> UNDERGROUND_CORRUPTION = register("underground_corruption"); // 腐化地下 y40——y64可以触发
    public static final DeferredHolder<SoundEvent, SoundEvent> THE_HALLOW = register("the_hallow"); // 神圣地表
    public static final DeferredHolder<SoundEvent, SoundEvent> UNDERGROUND_HALLOW = register("underground_hallow"); // 神圣地下 y40——y64可以触发
    public static final DeferredHolder<SoundEvent, SoundEvent> OCEAN = register("ocean"); // 海洋以及海边
    public static final DeferredHolder<SoundEvent, SoundEvent> OCEAN_NIGHT = register("ocean_night"); // 海洋以及海边夜晚
    public static final DeferredHolder<SoundEvent, SoundEvent> HIGH_WIND = register("high_wind"); // 大风
    public static final DeferredHolder<SoundEvent, SoundEvent> RAIN = register("rain"); // 下雨
    public static final DeferredHolder<SoundEvent, SoundEvent> MORNING_RAIN = register("morning_rain"); // 晨雨
    public static final DeferredHolder<SoundEvent, SoundEvent> STORM = register("storm"); // 暴风雨
    public static final DeferredHolder<SoundEvent, SoundEvent> SPACE = register("space"); // 太空 超过y320
    public static final DeferredHolder<SoundEvent, SoundEvent> SPACE_DAY = register("space_day"); // 太空白天 超过y320
    public static final DeferredHolder<SoundEvent, SoundEvent> MUSHROOMS = register("mushrooms"); // 发光蘑菇群系
    public static final DeferredHolder<SoundEvent, SoundEvent> CRIMSON = register("crimson"); // 猩红地表
    public static final DeferredHolder<SoundEvent, SoundEvent> UNDERGROUND_CRIMSON = register("underground_crimson"); // 猩红地下 y40——y64可以触发
    public static final DeferredHolder<SoundEvent, SoundEvent> ICE = register("ice"); // 冰雪群系地表
    public static final DeferredHolder<SoundEvent, SoundEvent> UNDERGROUND_ICE = register("underground_ice"); // 冰雪群系地下
    public static final DeferredHolder<SoundEvent, SoundEvent> UNDERWORLD = register("underworld"); // 灰烬群系，白蜡木林
    public static final DeferredHolder<SoundEvent, SoundEvent> SLIME_RAIN = register("slime_rain"); // 史莱姆雨事件使用
    public static final DeferredHolder<SoundEvent, SoundEvent> DESERT = register("desert"); // 沙漠
    public static final DeferredHolder<SoundEvent, SoundEvent> UNDERGROUND_DESERT = register("underground_desert"); // 沙漠地下 y40——y64可以触发
    public static final DeferredHolder<SoundEvent, SoundEvent> TOWN_DAY = register("town_day"); // 村庄，城镇白天
    public static final DeferredHolder<SoundEvent, SoundEvent> TOWN_NIGHT = register("town_night"); // 村庄，城镇夜晚
    public static final DeferredHolder<SoundEvent, SoundEvent> AETHER = register("aether"); // 以太

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(Confluence.asResource(name)));
    }

    public static class Types {
        public static final SoundType COIN = new DeferredSoundType(1.0F, 1.0F, COINS, COINS, COINS, COINS, COINS);
    }
}
