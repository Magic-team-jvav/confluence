package org.confluence.mod.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.worldgen.structure.*;

public final class ModStructures {
    public static final DeferredRegister<StructureType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.STRUCTURE_TYPE, Confluence.MODID);

    public static final RegistryObject,StructureType<LivingTreeStructure>>LIVING_TREE =TYPES.register("living_tree",()->()->LivingTreeStructure.CODEC);
    public static final RegistryObject,StructureType<LivingMahoganyTreeStructure>>LIVING_MAHOGANY_TREE =TYPES.register("living_mahogany_tree",()->()->LivingMahoganyTreeStructure.CODEC);
    public static final RegistryObject,StructureType<SmallLivingMahoganyTreeStructure>>SMALL_LIVING_MAHOGANY_TREE =TYPES.register("small_living_mahogany_tree",()->()->SmallLivingMahoganyTreeStructure.CODEC);
    public static final RegistryObject,StructureType<CrimsonCaveStructure>>CRIMSON_CAVE =TYPES.register("crimson_cave",()->()->CrimsonCaveStructure.CODEC);
    public static final RegistryObject,StructureType<QueenBeeHiveStructure>>QUEEN_BEE_HIVE =TYPES.register("queen_bee_hive",()->()->QueenBeeHiveStructure.CODEC);
    public static final RegistryObject,StructureType<ShimmerLakeStructure>>SHIMMER_LAKE =TYPES.register("shimmer_lake",()->()->ShimmerLakeStructure.CODEC);
    public static final RegistryObject,StructureType<DungeonStructure>>DUNGEON =TYPES.register("dungeon",()->()->DungeonStructure.CODEC);
    public static final RegistryObject,StructureType<HeavenIslandsStructure>>HEAVEN_ISLANDS =TYPES.register("heaven_islands",()->()->HeavenIslandsStructure.CODEC);
    public static final RegistryObject,StructureType<IceThornStructure>>ICE_THORN =TYPES.register("ice_thorn",()->()->IceThornStructure.CODEC);
    public static final RegistryObject,StructureType<MineTunnelsStructure>>MINE_TUNNELS =TYPES.register("mine_tunnels",()->()->MineTunnelsStructure.CODEC);
    public static final RegistryObject,StructureType<PyramidStructure>>PYRAMID =TYPES.register("pyramid",()->()->PyramidStructure.CODEC);
    public static final RegistryObject,StructureType<OasisStructure>>OASIS =TYPES.register("oasis",()->()->OasisStructure.CODEC);
    public static final RegistryObject,StructureType<MarbleCaveStructure>>MARBLE_CAVE =TYPES.register("marble_cave",()->()->MarbleCaveStructure.CODEC);
    public static final RegistryObject,StructureType<GraniteCaveStructure>>GRANITE_CAVE =TYPES.register("granite_cave",()->()->GraniteCaveStructure.CODEC);
    public static final RegistryObject,StructureType<ObsidianPillarStructure>>OBSIDIAN_PILLAR =TYPES.register("obsidian_pillar",()->()->ObsidianPillarStructure.CODEC);

    public static class Keys {
        public static final ResourceKey<Structure> AIR = key("air");
        public static final ResourceKey<Structure> CRIMSON_CAVE = key("crimson_cave");
        public static final ResourceKey<Structure> CRIMSON_FOSSIL = key("crimson_fossil");
        public static final ResourceKey<Structure> GRANITE_CAVE = key("granite_cave");
        public static final ResourceKey<Structure> MARBLE_CAVE = key("marble_cave");
        public static final ResourceKey<Structure> DESERT_UNDERGROUND_CABINS = key("desert_underground_cabins");
        public static final ResourceKey<Structure> DUNGEON = key("dungeon");
        public static final ResourceKey<Structure> DUNGEON_ALTAR = key("dungeon_altar");
        public static final ResourceKey<Structure> EBONY_STONE_THORN = key("ebony_stone_thorn");
        public static final ResourceKey<Structure> SHIMMER_LAKE = key("shimmer_lake");

        private static ResourceKey<Structure> key(String path) {
            return Confluence.asResourceKey(Registries.STRUCTURE, path);
        }
    }
}
