package org.confluence.terra_curio.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.init.TCBlocks;
import org.confluence.terra_curio.common.init.TCTags;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, @Nullable ExistingFileHelper helper) {
        super(output, lookup, TerraCurio.MODID, helper);
    }

    @Override
    public void addTags(HolderLookup.Provider provider) {
        tag(TCTags.FLOWER_BOOTS_AVAILABLE).add(
            Blocks.GRASS_BLOCK
        );
        tag(BlockTags.MINEABLE_WITH_AXE).add(
                TCBlocks.WORKSHOP.get()
        );
    }
}
