package org.confluence.lib.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import org.confluence.lib.ConfluenceMagicLib;

public final class LibTags {
    public static class Items {
        public static final TagKey<Item> WIP = register("wip");
        public static final TagKey<Item> SKIP_USING_SLOWDOWN = register("skip_using_slowdown"); // 使用时不影响玩家移动速度
        public static final TagKey<Item> SKIP_RESET_STRENGTH = register("skip_reset_strength"); // 使用时不重置玩家攻击冷却

        private static TagKey<Item> register(String id) {
            return ItemTags.create(ConfluenceMagicLib.asResource(id));
        }
    }

    public static class DamageTypes {
        public static final TagKey<DamageType> AS_MELEE_ATTACK = register("as_melee_attack"); // 用于判断是否是近战伤害

        private static TagKey<DamageType> register(String id) {
            return TagKey.create(Registries.DAMAGE_TYPE, ConfluenceMagicLib.asResource(id));
        }
    }
}
