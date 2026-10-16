package org.confluence.mod.common.data.gen.language;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.confluence.lib.util.LibUtils;

@FunctionalInterface
public interface TranslationConsumer {
    /// 添加中英文翻译
    ///
    /// @param key     翻译键
    /// @param english 英文名称或文本
    /// @param chinese 中文名称或文本
    void add(String key, String english, String chinese);

    default void add(Item item, String chinese) {
        add(item.getDescriptionId(), LibUtils.toTitleCase(BuiltInRegistries.ITEM.getKey(item).getPath()), chinese);
    }

    default void add(Block block, String chinese) {
        add(block.getDescriptionId(), LibUtils.toTitleCase(BuiltInRegistries.BLOCK.getKey(block).getPath()), chinese);
    }

    default void add(EntityType<?> entity, String chinese) {
        add(entity.getDescriptionId(), LibUtils.toTitleCase(BuiltInRegistries.ENTITY_TYPE.getKey(entity).getPath()), chinese);
    }

    default void add(MobEffect effect, String chinese) {
        add(effect.getDescriptionId(), LibUtils.toTitleCase(BuiltInRegistries.MOB_EFFECT.getKey(effect).getPath()), chinese);
    }

    default void add(Item item, String english, String chinese) {
        add(item.getDescriptionId(), english, chinese);
    }

    default void add(Block block, String english, String chinese) {
        add(block.getDescriptionId(), english, chinese);
    }

    default void add(EntityType<?> entity, String english, String chinese) {
        add(entity.getDescriptionId(), english, chinese);
    }

    default void add(MobEffect effect, String english, String chinese) {
        add(effect.getDescriptionId(), english, chinese);
    }

    default void addTooltip(Item item, String english, String chinese) {
        add("tooltip." + item.getDescriptionId() + ".0", english, chinese);
    }

    default void addTooltip(MobEffect effect, String english, String chinese) {
        add("tooltip." + effect.getDescriptionId() + ".0", english, chinese);
    }
}
