package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;

public final class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, Confluence.MODID);

    public static final RegistryObject<SoundEvent> TRANSMISSION = register("transmission"); // 泰拉传送
    public static final RegistryObject<SoundEvent> LIGHTSABER_OPEN = register("lightsaber_open"); // 光剑
    public static final RegistryObject<SoundEvent> REGULAR_STAFF_SHOOT = register("regular_staff_shoot"); // 法杖1
    public static final RegistryObject<SoundEvent> REGULAR_STAFF_SHOOT_2 = register("regular_staff_shoot_2"); // 法杖2
    public static final RegistryObject<SoundEvent> REGULAR_STAFF_SHOOT_3 = register("regular_staff_shoot_3"); // 法杖3 滋水专属
    public static final RegistryObject<SoundEvent> LIFE_CRYSTAL_USE = register("life_crystal_use");// 生命水晶
    public static final RegistryObject<SoundEvent> MANA_STAR_USE = register("mana_star_use"); // 魔力水晶
    public static final RegistryObject<SoundEvent> COINS = register("coins"); // 币
    public static final RegistryObject<SoundEvent> COINS_LARGE = register("coins_large"); // 捡起一堆钱
    public static final RegistryObject<SoundEvent> COINS_MEDIUM = register("coins_medium"); // 捡起适量钱
    public static final RegistryObject<SoundEvent> COINS_SMALL = register("coins_small"); // 捡起一点钱
    public static final RegistryObject<SoundEvent> COOLDOWN_RECOVERY = register("cooldown_recovery"); // CD冷却
    public static final RegistryObject<SoundEvent> BOW_COOLDOWN_RECOVERY = register("bow_cooldown_recovery"); // CD冷却-弓箭
    public static final RegistryObject<SoundEvent> FROZEN_ARROW = register("frozen_arrow"); // 冰雪射弹
    public static final RegistryObject<SoundEvent> FROZEN_BROKEN = register("frozen_broken"); // 冰雪射弹消失
    public static final RegistryObject<SoundEvent> SHIMMER_DETACHMENT = register("shimmer_detachment"); // 脱离微光
    public static final RegistryObject<SoundEvent> SHIMMER_EVOLUTION = register("shimmer_evolution"); // 嬗变
    public static final RegistryObject<SoundEvent> SHIMMER_IMMERSION = register("shimmer_immersion"); // 生物入微光
    public static final RegistryObject<SoundEvent> SHIMMER_ITEM_INTERACTIONS = register("shimmer_item_interactions"); // 物品入微光
    public static final RegistryObject<SoundEvent> STAR = register("star"); // 坠星
    public static final RegistryObject<SoundEvent> STAR_LANDS = register("star_lands"); // 星星落地
    public static final RegistryObject<SoundEvent> TERRA_OPERATION = register("terra_operation"); // 操作音效
    public static final RegistryObject<SoundEvent> DECOUPLING = register("decoupling"); // 脱钩
    public static final RegistryObject<SoundEvent> ACHIEVEMENTS = register("achievements"); // 成就音效
    public static final RegistryObject<SoundEvent> TRANSMUTATION_USE = register("transmutation_use"); // 高级增益使用
    public static final RegistryObject<SoundEvent> HOOK_SHOOT = register("hook_shoot");
    public static final RegistryObject<SoundEvent> HOOK_ATTACH = register("hook_attach");
    public static final RegistryObject<SoundEvent> LUCYAXE_TALK = register("lucyaxe_talk");
    public static final RegistryObject<SoundEvent> REPEATER_ITEM_AERIAL_SHOOTING = register("repeater_item_aerial_shooting");
    public static final RegistryObject<SoundEvent> CRYSTAL_VILE_SHARD_SHOOT = register("crystal_vile_shard_shoot"); // 魔晶碎块

    private static RegistryObject<SoundEvent> register(String name) {
        return EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(Confluence.asResource(name)));
    }

    public static class Types {
        public static final SoundType COIN = new ForgeSoundType(1.0F, 1.0F, COINS, COINS, COINS, COINS, COINS);
    }
}
