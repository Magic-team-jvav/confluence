package com.xiaohunao.mine_team.common.config;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.regex.Pattern;

public class MineTeamConfig {
    public static final ModConfigSpec CONFIG;

    private static final ModConfigSpec.ConfigValue<List<? extends String>> tamingMaterials;
    private static final BiMap<EntityType<?>, Ingredient> tamingMaterialMap = HashBiMap.create();


    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        tamingMaterials = builder
                .comment("List of materials that can be used to tame entities")
                .comment("Format: entity-ingredient,'minecraft:wolf-{\"item\":\"minecraft:bone\"}'")
                .defineList("tamingMaterials", List.of(), str -> {
                    if (!(str instanceof String)) {
                        return false;
                    }
                    return Pattern.matches("\\w+:\\w+-\\{\"\\w+\":\"\\w+:\\w+\"}", (String) str);
                });
        CONFIG = builder.build();
    }

    public static void loadTamingMaterials() {
        tamingMaterialMap.clear();
        for (String material : tamingMaterials.get()) {
            String[] split = material.split("-");
            BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.parse(split[0])).ifPresent(entityType -> {
                Ingredient.CODEC_NONEMPTY.parse(JsonOps.INSTANCE,new JsonPrimitive(split[1])).result().ifPresent(ingredient -> {
                    tamingMaterialMap.put(entityType, ingredient);
                });
            });
        }
    }

    public static BiMap<EntityType<?>, Ingredient> getTamingMaterialMap() {
        return tamingMaterialMap;
    }

    public static Ingredient getTamingMaterial(EntityType<?> entityType) {
        return tamingMaterialMap.getOrDefault(entityType, Ingredient.of(Items.GOLDEN_APPLE));
    }
}