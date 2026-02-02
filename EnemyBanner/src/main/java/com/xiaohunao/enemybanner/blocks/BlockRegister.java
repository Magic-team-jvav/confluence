package com.xiaohunao.enemybanner.blocks;

import com.mojang.datafixers.DSL;
import com.xiaohunao.enemybanner.EnemyBanner;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegister {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EnemyBanner.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EnemyBanner.MODID);

    public static final DeferredBlock<EnemyBannerBlock> ENEMY_BANNER = BLOCKS.register(
            "enemy_banner", () -> new EnemyBannerBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.WOOD)
                            .forceSolidOn()
                            .instrument(NoteBlockInstrument.BASS)
                            .noCollission()
                            .strength(1.0F)
                            .sound(SoundType.WOOD)
                            .ignitedByLava()
            )
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnemyBannerBlockEntity>> ENEMY_BANNER_ENTITY = BLOCK_ENTITY_TYPE.register(
            "enemy_banner", () -> BlockEntityType.Builder.of(EnemyBannerBlockEntity::new, ENEMY_BANNER.get()).build(DSL.remainderType())
    );

    public static final DeferredBlock<BannerBoxBlock> BANNER_BOX = BLOCKS.register(
            "banner_box", () -> new BannerBoxBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.5F)
                    .sound(SoundType.WOOD)
            )
    );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BannerBoxBlockEntity>> BANNER_BOX_ENTITY = BLOCK_ENTITY_TYPE.register(
            "banner_box", () -> BlockEntityType.Builder.of(BannerBoxBlockEntity::new, BANNER_BOX.get()).build(DSL.remainderType())
    );

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ENTITY_TYPE.register(bus);
    }
}
