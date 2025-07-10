package org.confluence.mod.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.worldgen.structure.*;

public final class ModStructures {
    public static final DeferredRegister<StructureType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.STRUCTURE_TYPE, Confluence.MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<LivingTreeStructure>> LIVING_TREE = TYPES.register("living_tree", () -> () -> LivingTreeStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<LivingMahoganyTreeStructure>> LIVING_MAHOGANY_TREE = TYPES.register("living_mahogany_tree", () -> () -> LivingMahoganyTreeStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<SmallLivingMahoganyTreeStructure>> SMALL_LIVING_MAHOGANY_TREE = TYPES.register("small_living_mahogany_tree", () -> () -> SmallLivingMahoganyTreeStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<CrimsonCaveStructure>> CRIMSON_CAVE = TYPES.register("crimson_cave", () -> () -> CrimsonCaveStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<QueenBeeHiveStructure>> QUEEN_BEE_HIVE = TYPES.register("queen_bee_hive", () -> () -> QueenBeeHiveStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<ShimmerLakeStructure>> SHIMMER_LAKE = TYPES.register("shimmer_lake", () -> () -> ShimmerLakeStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<DungeonStructure>> DUNGEON = TYPES.register("dungeon", () -> () -> DungeonStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<HeavenIslandsStructure>> HEAVEN_ISLANDS = TYPES.register("heaven_islands", () -> () -> HeavenIslandsStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<IceThornStructure>> ICE_THORN = TYPES.register("ice_thorn", () -> () -> IceThornStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<MineTunnelsStructure>> MINE_TUNNELS = TYPES.register("mine_tunnels", () -> () -> MineTunnelsStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<PyramidStructure>> PYRAMID = TYPES.register("pyramid", () -> () -> PyramidStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<OasisStructure>> OASIS = TYPES.register("oasis", () -> () -> OasisStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<MarbleCaveStructure>> MARBLE_CAVE = TYPES.register("marble_cave", () -> () -> MarbleCaveStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<GraniteCaveStructure>> GRANITE_CAVE = TYPES.register("granite_cave", () -> () -> GraniteCaveStructure.CODEC);

    public static final ResourceKey<Structure> DUNGEON_KEY = Confluence.asResourceKey(Registries.STRUCTURE, "dungeon");
}
