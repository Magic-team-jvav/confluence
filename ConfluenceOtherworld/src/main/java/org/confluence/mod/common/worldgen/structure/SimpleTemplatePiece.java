package org.confluence.mod.common.worldgen.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModStructures;

public class SimpleTemplatePiece extends TemplateStructurePiece {
    public SimpleTemplatePiece(StructureTemplateManager structureTemplateManager, String name, BlockPos startPos, boolean overwrite, boolean ignoreEntities, Rotation rotation) {
        super(ModStructures.SIMPLE_TEMPLATE_PIECE.get(), 0, structureTemplateManager, Confluence.asResource("simple_template/" + name), name, makeSettings(overwrite, ignoreEntities, rotation), startPos);
    }

    public SimpleTemplatePiece(StructureTemplateManager structureTemplateManager, CompoundTag tag) {
        super(ModStructures.SIMPLE_TEMPLATE_PIECE.get(), tag, structureTemplateManager, location -> makeSettings(tag.getBoolean("OW"), tag.getBoolean("IE"), Rotation.valueOf(tag.getString("Rot"))));
    }

    private static StructurePlaceSettings makeSettings(boolean overwrite, boolean ignoreEntities, Rotation rotation) {
        BlockIgnoreProcessor blockignoreprocessor = overwrite ? BlockIgnoreProcessor.STRUCTURE_BLOCK : BlockIgnoreProcessor.STRUCTURE_AND_AIR;
        return new StructurePlaceSettings().setIgnoreEntities(true).addProcessor(blockignoreprocessor).setRotation(rotation);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putBoolean("OW", placeSettings.getProcessors().getFirst() == BlockIgnoreProcessor.STRUCTURE_BLOCK);
        tag.putBoolean("IE", placeSettings.isIgnoreEntities());
        tag.putString("Rot", placeSettings.getRotation().name());
    }

    @Override
    protected void handleDataMarker(String name, BlockPos pos, ServerLevelAccessor level, RandomSource random, BoundingBox box) {}
}
