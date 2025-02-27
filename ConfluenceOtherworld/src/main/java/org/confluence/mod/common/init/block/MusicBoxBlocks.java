package org.confluence.mod.common.init.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.functional.MusicBoxBlock;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.mod.common.item.accessory.MusicBoxItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.confluence.mod.common.init.block.ModBlocks.BLOCK_ENTITIES;

public class MusicBoxBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    private static List<Supplier<? extends Block>> MUSIC_BOXES = new ArrayList<>();

    public static final Supplier<MusicBoxBlock> MUSIC_BOX = register("music_box", AccessoryItems.MUSIC_BOX);

    public static final Supplier<BlockEntityType<MusicBoxBlock.Entity>> MUSIC_BOX_ENTITY = BLOCK_ENTITIES.register("music_box_entity", () -> {
        Block[] validBlocks = MUSIC_BOXES.stream().map(Supplier::get).toArray(Block[]::new);
        MUSIC_BOXES = null;
        return BlockEntityType.Builder.of(MusicBoxBlock.Entity::new, validBlocks).build(null);
    });

    private static Supplier<MusicBoxBlock> register(String id, Supplier<MusicBoxItem> item) {
        DeferredBlock<MusicBoxBlock> object = BLOCKS.register(id, () -> new MusicBoxBlock(item));
        MUSIC_BOXES.add(object);
        return object;
    }
}
