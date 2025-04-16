package org.confluence.mod.common.init.block;

import com.mojang.datafixers.DSL;
import net.minecraft.sounds.Music;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ModMusics;
import org.confluence.mod.common.block.functional.MusicBoxBlock;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.confluence.mod.common.init.block.ModBlocks.BLOCK_ENTITIES;

public class MusicBoxBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);
    private static List<Supplier<? extends Block>> MUSIC_BOXES = new ArrayList<>();

    public static final Supplier<MusicBoxBlock> MUSIC_BOX = register("music_box", null);
    public static final Supplier<MusicBoxBlock> MUSIC_BOX_OTHERWORLD_DAY = register("music_box_otherworld_day", ModMusics.OVERWORLD_DAY);
    // todo eerie
    public static final Supplier<MusicBoxBlock> MUSIC_BOX_NIGHT = register("music_box_night", ModMusics.OVERWORLD_NIGHT);
    // todo title
    public static final Supplier<MusicBoxBlock> MUSIC_BOX_UNDERGROUND = register("music_box_underground", ModMusics.UNDERGROUND);
    // todo boss1
    public static final Supplier<MusicBoxBlock> MUSIC_BOX_JUNGLE = register("music_box_jungle", ModMusics.JUNGLE);
    public static final Supplier<MusicBoxBlock> MUSIC_BOX_CORRUPTION = register("music_box_corruption", ModMusics.CORRUPTION);
    public static final Supplier<MusicBoxBlock> MUSIC_BOX_UNDERGROUND_CORRUPTION = register("music_box_underground_corruption", ModMusics.UNDERGROUND_CORRUPTION);
    public static final Supplier<MusicBoxBlock> MUSIC_BOX_THE_HALLOW = register("music_box_the_hallow", ModMusics.HALLOW);
    // todo boss2
    public static final Supplier<MusicBoxBlock> MUSIC_BOX_UNDERGROUND_HALLOW = register("music_box_underground_hallow", ModMusics.UNDERGROUND_HALLOW);
    // todo boss3

    public static final Supplier<BlockEntityType<MusicBoxBlock.Entity>> MUSIC_BOX_ENTITY = BLOCK_ENTITIES.register("music_box_entity", () -> {
        Block[] validBlocks = MUSIC_BOXES.stream().map(Supplier::get).toArray(Block[]::new);
        MUSIC_BOXES = null;
        return BlockEntityType.Builder.of(MusicBoxBlock.Entity::new, validBlocks).build(DSL.remainderType());
    });

    private static Supplier<MusicBoxBlock> register(String id, @Nullable Music music) {
        DeferredBlock<MusicBoxBlock> object = BLOCKS.register(id, () -> new MusicBoxBlock(music));
        MUSIC_BOXES.add(object);
        return object;
    }
}
