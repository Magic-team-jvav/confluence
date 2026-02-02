package org.confluence.terraentity.data.gen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.terraentity.TerraEntity;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class TELootModifyProvider extends GlobalLootModifierProvider {

    public TELootModifyProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, String modId) {
        super(output, registries, modId);
    }

    @Override
    public void start() {

        // 丛林府邸箱子
        this.addLootModifier("chest/spawn_wooden_sword_staff", BuiltInLootTables.WOODLAND_MANSION, TESubLoot.SPAWN_WOODEN_SWORD_STAFF);
        // 金字塔箱子
        this.addLootModifier("chest/spawn_stone_sword_staff", BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY, TESubLoot.SPAWN_STONE_SWORD_STAFF);
        // 铁匠村民箱子
        this.addLootModifier("chest/spawn_iron_sword_staff", BuiltInLootTables.VILLAGE_WEAPONSMITH, TESubLoot.SPAWN_IRON_SWORD_STAFF);
        // 猪灵交易
        this.addLootModifier("gameplay/spawn_golden_sword_staff", BuiltInLootTables.PIGLIN_BARTERING, TESubLoot.SPAWN_GOLDEN_SWORD_STAFF);
        // 远古守卫者
        this.addLootModifier("entities/spawn_diamond_sword_staff", EntityType.ELDER_GUARDIAN.getDefaultLootTable(), TESubLoot.SPAWN_DIAMOND_SWORD_STAFF);
        // 堡垒遗迹珍宝
        this.addLootModifier("chest/spawn_netherite_sword_staff", BuiltInLootTables.BASTION_TREASURE, TESubLoot.SPAWN_NETHERITE_SWORD_STAFF);
        // 远古城市
        this.addLootModifier("chest/spawn_sculk_wisp_staff", BuiltInLootTables.ANCIENT_CITY, TESubLoot.SPAWN_SCULK_WISP_STAFF);

    }

    private void addLootModifier(String name, @Nullable ResourceKey<LootTable> lootTableId, ResourceKey<LootTable> lootTableAdd) {
        LootItemCondition condition;
        if (lootTableId != null) {
            condition = LootTableIdCondition.builder(lootTableId.location()).build();
            this.add(name, new AddTableLootModifier(new LootItemCondition[]{condition}, lootTableAdd), new NotCondition(new ModLoadedCondition(ConfluenceMagicLib.CONFLUENCE_ID)));
        }else{
            TerraEntity.LOGGER.warn("Loot table id is null for {}", name);
        }
    }

}