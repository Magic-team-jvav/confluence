package org.confluence.terraentity.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import org.confluence.lib.util.ScheduledForMove;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.integration.curios.CuriosHelper;

public class TETags {

    public static class Items {
        public static final TagKey<Item> WHIP_ENCHANTABLE = registerItem("whip_enchantable");
        public static final TagKey<Item> BOOMERANG_ENCHANTABLE = registerItem("boomerang_enchantable");
        public static final TagKey<Item> CURIOS_MOUNT = registerCuriosItem(CuriosHelper.MOUNT_KEY);
        public static final TagKey<Item> CURIOS_PET = registerCuriosItem(CuriosHelper.PET_KEY);
        public static final TagKey<Item> CURIOS_LIGHT_PET = registerCuriosItem(CuriosHelper.LIGHT_PET_KEY);

        public static final TagKey<Item> WEAPONS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "weapons"));
    }

    public static class Blocks {
        public static final TagKey<Block> HONEY = registerBlock("honey");
        public static final TagKey<Block> NPC_HOUSE_CONSTITUTE = registerBlock("house_constitute");
        public static final TagKey<Block> NPC_HOUSE_CHAIR = registerBlock("house_chair");
        public static final TagKey<Block> NPC_HOUSE_TABLE = registerBlock("house_table");


    }

    public static class EntityTypes {
        public static final TagKey<EntityType<?>> SLIME = registerEntityType("slime");
        public static final TagKey<EntityType<?>> CORRUPT = registerEntityType("corrupt");
        public static final TagKey<EntityType<?>> FLESH_ALLIANCE = registerEntityType("flesh_alliance");
    }

    public static class Biomes{
        public static final TagKey<Biome> IS_EVER_WHERE = registerBiome("is_ever_where");

    }

    @ScheduledForMove(since = "1.2.0", inVersion = "2.0.0")
    public static class DamageTypes {
        // 玩家召唤伤害 如鞭子
        public static final ResourceKey<DamageType> SUMMON = registerDamageType("summon");
        // 召唤物召唤伤害 用于标记伤害增伤
        public static final ResourceKey<DamageType> SUMMONER = registerDamageType("summoner");
        public static final ResourceKey<DamageType> FROST_BURN = registerDamageType("frost_burn");
        public static final ResourceKey<DamageType> PASS_ARMOR = registerDamageType("pass_armor");



        public static DamageSource of(Level level, ResourceKey<DamageType> key) {
            return of(level, key, null, null);
        }

        public static DamageSource of(Level level, ResourceKey<DamageType> key, Entity causing) {
            return of(level, key, causing, causing);
        }

        public static DamageSource of(Level level, ResourceKey<DamageType> key, Entity causing, Entity direct) {
            return new DamageSource(level.registryAccess().registry(Registries.DAMAGE_TYPE).orElseThrow().getHolderOrThrow(key), causing, direct);
        }

        public static void createDamageTypes(BootstrapContext<DamageType> context) {
            context.register(SUMMON, new DamageType("summon_damage_type", 0.1F));
            context.register(SUMMONER, new DamageType("summoner_damage_type", 0.1F));
            context.register(FROST_BURN, new DamageType("frost_burn_damage_type", 0.1F));
            context.register(PASS_ARMOR, new DamageType("pass_armor_damage_type", 0.1F));
        }
    }

    private static TagKey<Item> registerItem(String id) {
        return ItemTags.create(TerraEntity.space(id));
    }
    private static TagKey<Item> registerCuriosItem(String id) {
        return ItemTags.create(TerraEntity.fromSpaceAndPath("curios", id));
    }
    private static TagKey<Block> registerBlock(String id) {
        return BlockTags.create(TerraEntity.space(id));
    }
    private static TagKey<EntityType<?>> registerEntityType(String id) {
        return TagKey.create(Registries.ENTITY_TYPE, TerraEntity.space(id));
    }
    private static TagKey<Biome> registerBiome(String id) {
        return TagKey.create(Registries.BIOME, TerraEntity.space(id));
    }
    private static ResourceKey<DamageType> registerDamageType(String id) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, TerraEntity.space(id));
    }
}
