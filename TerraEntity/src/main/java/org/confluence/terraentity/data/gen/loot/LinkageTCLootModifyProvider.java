package org.confluence.terraentity.data.gen.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.conditions.AndCondition;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.integration.ModChecker;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class LinkageTCLootModifyProvider extends GlobalLootModifierProvider {

    public LinkageTCLootModifyProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, ModChecker.terraCurio.getId());
    }

    @Override
    protected void start() {
        this.addLootModifier(TEBossEntities.KING_SLIME, TESubLoot.SPAWN_royal_gel);
        this.addLootModifier(TEBossEntities.EYE_OF_CTHULHU, TESubLoot.SPAWN_shield_of_cthulhu);
        this.addLootModifier(TEBossEntities.QUEEN_BEE, TESubLoot.SPAWN_hive_pack);
        this.addLootModifier(TEBossEntities.BRAIN_OF_CTHULHU, TESubLoot.SPAWN_brain_of_confusion);
        this.addLootModifier(TEBossEntities.EATER_OF_WORLDS, TESubLoot.SPAWN_worm_scarf);
        this.addLootModifier(TEBossEntities.SKELETRON, TESubLoot.SPAWN_bone_glove);


        this.addLootModifier(TEMonsterEntities.NYMPH, TESubLoot.SPAWN_metal_detector);
        this.addLootModifier(TEMonsterEntities.CURSED_SKULL, TESubLoot.SPAWN_tally_counter);
        this.addLootModifier(TEMonsterEntities.HELL_BAT, TESubLoot.SPAWN_magma_stone);
        this.addLootModifier(TEMonsterEntities.FIRE_IMP, TESubLoot.SPAWN_obsidian_rose);
        this.addLootModifier(TEMonsterEntities.DARK_CASTER, TESubLoot.SPAWN_tally_counter);
        this.addLootModifier(TEMonsterEntities.GIANT_SHELLY, TESubLoot.SPAWN_giant_shelly);
        this.addLootModifier(TEMonsterEntities.PIRANHA, TESubLoot.SPAWN_compass);
        this.addLootModifier(TEMonsterEntities.PIXIE, TESubLoot.SPAWN_fast_clock);
        this.addLootModifier(TEMonsterEntities.SNOW_FLINX, TESubLoot.SPAWN_compass);
        Stream.of(TEMonsterEntities.CAVE_BAT,TEMonsterEntities.ICE_BAT, TEMonsterEntities.JUNGLE_BAT, TEMonsterEntities.SPORE_BAT).forEach(e->
                this.addLootModifier(e, TESubLoot.SPAWN_depth_meter)
        );
        this.addLootModifier(TEMonsterEntities.HORNET, TESubLoot.SPAWN_bezoar);


    }

    private void addLootModifier(DeferredHolder<EntityType<?>, ?> entityType, ResourceKey<LootTable> lootTableAdd) {
        LootItemCondition condition;
        ResourceKey<LootTable> lootTableId = entityType.get().getDefaultLootTable();
        condition = LootTableIdCondition.builder(lootTableId.location()).build();
        this.add("entities/add_" + entityType.getId().getPath(), new AddTableLootModifier(new LootItemCondition[]{condition}, lootTableAdd), new AndCondition(List.of(
                new NotCondition(new ModLoadedCondition(ConfluenceMagicLib.CONFLUENCE_ID))
        )));
    }

}