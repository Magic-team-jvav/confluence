package org.confluence.mod.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.worldgen.structure.LivingTreeStructure;

import java.util.function.Supplier;

public final class ModStructures {
    public static final DeferredRegister<StructureType<?>> TYPES = DeferredRegister.create(BuiltInRegistries.STRUCTURE_TYPE, Confluence.MODID);
    public static final DeferredRegister<StructurePieceType> PIECE_TYPES = DeferredRegister.create(BuiltInRegistries.STRUCTURE_PIECE, Confluence.MODID);

    public static final Supplier<StructureType<LivingTreeStructure>> LIVING_TREE = TYPES.register("living_tree", () -> () -> LivingTreeStructure.CODEC);
    public static final Supplier<StructurePieceType> LIVING_TREE_TRUNK = PIECE_TYPES.register("living_tree_trunk", () -> LivingTreeStructure.TrunkPiece::new);

    public static void register(IEventBus bus) {
        TYPES.register(bus);
        PIECE_TYPES.register(bus);
    }
}
