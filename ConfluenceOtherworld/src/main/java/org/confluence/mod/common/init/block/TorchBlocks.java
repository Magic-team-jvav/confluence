package org.confluence.mod.common.init.block;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.ModItems;

/// 泰拉瑞亚的火把。全部由注册信息推导资源：直立与墙挂共用一张 `block/torch/<名字>` 纹理，
/// 彩光颜色在 {@code ModOpalDataProvider} 中登记。
public class TorchBlocks {
    public static void init() {}

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Confluence.MODID);

    // 宝石火把
    public static final DeferredBlock<TorchBlock> RED_TORCH = register("red", MapColor.COLOR_RED);
    public static final DeferredBlock<TorchBlock> ORANGE_TORCH = register("orange", MapColor.COLOR_ORANGE);
    public static final DeferredBlock<TorchBlock> YELLOW_TORCH = register("yellow", MapColor.COLOR_YELLOW);
    public static final DeferredBlock<TorchBlock> GREEN_TORCH = register("green", MapColor.COLOR_GREEN);
    public static final DeferredBlock<TorchBlock> BLUE_TORCH = register("blue", MapColor.COLOR_BLUE);
    public static final DeferredBlock<TorchBlock> WHITE_TORCH = register("white", MapColor.SNOW);
    public static final DeferredBlock<TorchBlock> PURPLE_TORCH = register("purple", MapColor.COLOR_PURPLE);
    public static final DeferredBlock<TorchBlock> PINK_TORCH = register("pink", MapColor.COLOR_PINK);

    // 材料与特殊火把
    public static final DeferredBlock<TorchBlock> ICE_TORCH = register("ice", MapColor.ICE);
    public static final DeferredBlock<TorchBlock> BONE_TORCH = register("bone", MapColor.TERRACOTTA_WHITE);
    public static final DeferredBlock<TorchBlock> ULTRABRIGHT_TORCH = register("ultrabright", MapColor.COLOR_LIGHT_BLUE);
    public static final DeferredBlock<TorchBlock> DEMON_TORCH = register("demon", MapColor.COLOR_MAGENTA);
    public static final DeferredBlock<TorchBlock> CURSED_TORCH = register("cursed", MapColor.COLOR_LIGHT_GREEN);
    public static final DeferredBlock<TorchBlock> ICHOR_TORCH = register("ichor", MapColor.COLOR_YELLOW);
    public static final DeferredBlock<TorchBlock> RAINBOW_TORCH = register("rainbow", MapColor.COLOR_MAGENTA);

    // 生物群系火把
    public static final DeferredBlock<TorchBlock> DESERT_TORCH = register("desert", MapColor.SAND);
    public static final DeferredBlock<TorchBlock> CORAL_TORCH = register("coral", MapColor.COLOR_PINK);
    public static final DeferredBlock<TorchBlock> CORRUPT_TORCH = register("corrupt", MapColor.COLOR_PURPLE);
    public static final DeferredBlock<TorchBlock> CRIMSON_TORCH = register("crimson", MapColor.COLOR_RED);
    public static final DeferredBlock<TorchBlock> HALLOWED_TORCH = register("hallowed", MapColor.COLOR_PINK);
    public static final DeferredBlock<TorchBlock> JUNGLE_TORCH = register("jungle", MapColor.PLANT);
    public static final DeferredBlock<TorchBlock> MUSHROOM_TORCH = register("mushroom", MapColor.COLOR_LIGHT_BLUE);
    public static final DeferredBlock<TorchBlock> AETHER_TORCH = register("aether", MapColor.COLOR_PURPLE);

    private static DeferredBlock<TorchBlock> register(String prefix, MapColor mapColor) {
        DeferredBlock<TorchBlock> torch = BLOCKS.register(prefix + "_torch", () -> new TorchBlock(ParticleTypes.FLAME, BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel(state -> 14).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY).mapColor(mapColor)));
        DeferredBlock<WallTorchBlock> wallTorch = BLOCKS.register(prefix + "_wall_torch", () -> new WallTorchBlock(ParticleTypes.FLAME, BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel(state -> 14).sound(SoundType.WOOD).lootFrom(torch).pushReaction(PushReaction.DESTROY)));
        ModItems.BLOCK_ITEMS.register(prefix + "_torch", () -> new StandingAndWallBlockItem(torch.get(), wallTorch.get(), new Item.Properties(), Direction.DOWN));
        return torch;
    }
}
