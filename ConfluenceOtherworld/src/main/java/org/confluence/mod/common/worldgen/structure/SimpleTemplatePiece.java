package org.confluence.mod.common.worldgen.structure;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModStructures;

public class SimpleTemplatePiece extends TemplateStructurePiece {
    public SimpleTemplatePiece(StructureTemplateManager structureTemplateManager, String name, BlockPos startPos, boolean overwrite, boolean ignoreEntities, Rotation rotation) {
        super(ModStructures.SIMPLE_TEMPLATE_PIECE.get(), 0, structureTemplateManager, Confluence.asResource("simple_template/" + name), name, makeSettings(overwrite, ignoreEntities, rotation), startPos);
        makeBigBox();
    }

    public SimpleTemplatePiece(StructureTemplateManager structureTemplateManager, CompoundTag tag) {
        super(ModStructures.SIMPLE_TEMPLATE_PIECE.get(), tag, structureTemplateManager, location -> makeSettings(tag.getBoolean("OW"), tag.getBoolean("IE"), Rotation.valueOf(tag.getString("Rot"))));
        makeBigBox();
    }

    private void makeBigBox() {
        int i = templatePosition.getX() / 16 * 16;
        int j = templatePosition.getZ() / 16 * 16;
        this.boundingBox = new BoundingBox(
                i - 16, templatePosition.getY() - 12, j - 16,
                i + 31, templatePosition.getY() + 11, j + 31
        );
        placeSettings.setBoundingBox(boundingBox);
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
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
        if (this.template.placeInWorld(level, this.templatePosition, pos, this.placeSettings, random, 2)) {
            for (StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : this.template
                    .filterBlocks(this.templatePosition, this.placeSettings, Blocks.STRUCTURE_BLOCK)) {
                if (structuretemplate$structureblockinfo.nbt() != null) {
                    StructureMode structuremode = StructureMode.valueOf(structuretemplate$structureblockinfo.nbt().getString("mode"));
                    if (structuremode == StructureMode.DATA) {
                        this.handleDataMarker(
                                structuretemplate$structureblockinfo.nbt().getString("metadata"),
                                structuretemplate$structureblockinfo.pos(),
                                level,
                                random,
                                box
                        );
                    }
                }
            }

            for (StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo1 : this.template
                    .filterBlocks(this.templatePosition, this.placeSettings, Blocks.JIGSAW)) {
                if (structuretemplate$structureblockinfo1.nbt() != null) {
                    String s = structuretemplate$structureblockinfo1.nbt().getString("final_state");
                    BlockState blockstate = Blocks.AIR.defaultBlockState();

                    try {
                        blockstate = BlockStateParser.parseForBlock(level.holderLookup(Registries.BLOCK), s, true).blockState();
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        Confluence.LOGGER.error("Error while parsing blockstate {} in jigsaw block @ {}", s, structuretemplate$structureblockinfo1.pos());
                    }

                    level.setBlock(structuretemplate$structureblockinfo1.pos(), blockstate, 3);
                }
            }
        }
    }

    @Override
    protected void handleDataMarker(String name, BlockPos pos, ServerLevelAccessor level, RandomSource random, BoundingBox box) {}
}
