package org.confluence.mod.common.block.functional.terraform;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.mod.common.block.conversion.ConversionTable;
import org.confluence.mod.common.block.conversion.machine.*;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.MaterialItems;

import java.util.Locale;
import java.util.function.Supplier;

// 溶液的物品、显示颜色和普通机器兼容性集中定义，顺序与改造表一致。
public enum Solution {
    GREEN(0x17b71b, false, MaterialItems.GREEN_SOLUTION, new PureMachineConversionTable()),
    BLUE(0x3dc1dc, false, MaterialItems.BLUE_SOLUTION, new HallowMachineConversionTable()),
    PURPLE(0x7516b0, false, MaterialItems.PURPLE_SOLUTION, new CorruptionMachineConversionTable()),
    DARK_BLUE(0x3d67dc, false, MaterialItems.DARK_BLUE_SOLUTION, new MushroomMachineConversionTable()),
    RED(0xc30909, false, MaterialItems.RED_SOLUTION, new CrimsonMachineConversionTable()),
    YELLOW(0xb79217, true, MaterialItems.YELLOW_SOLUTION, new DesertConversionTable()),
    WHITE(0xbffffe, true, MaterialItems.WHITE_SOLUTION, new SnowConversionTable()),
    BROWN(0x925923, true, MaterialItems.BROWN_SOLUTION, new ForestConversionTable());

    public final int color;
    public final boolean advanced;
    private final Supplier<Item> item;
    private final ConversionTable conversionTable;

    Solution(int color, boolean advanced, Supplier<Item> item, ConversionTable conversionTable) {
        this.color = color;
        this.advanced = advanced;
        this.item = item;
        this.conversionTable = conversionTable;
    }

    // 每种溶液持有独立的转换表，显式改造不受自然蔓延的困难模式门槛影响。
    public BlockState convert(LevelReader level, BlockPos pos, BlockState source) {
        BlockState target = conversionTable.get(source, true, level, pos);
        return target == null ? source : target;
    }

    // 事故只使用对应群系的地表方块，纯净与森林共用普通草方块。
    public BlockState failureBlock() {
        return switch (this) {
            case GREEN, BROWN -> Blocks.GRASS_BLOCK.defaultBlockState();
            case BLUE -> NatureBlocks.HALLOW_GRASS_BLOCK.get().defaultBlockState();
            case PURPLE -> NatureBlocks.CORRUPT_GRASS_BLOCK.get().defaultBlockState();
            case DARK_BLUE -> NatureBlocks.MUSHROOM_GRASS_BLOCK.get().defaultBlockState();
            case RED -> NatureBlocks.CRIMSON_GRASS_BLOCK.get().defaultBlockState();
            case YELLOW -> Blocks.SAND.defaultBlockState();
            case WHITE -> Blocks.SNOW_BLOCK.defaultBlockState();
        };
    }

    public Item item() {return item.get();}

    public String id() {return name().toLowerCase(Locale.ROOT);}

    // 非溶液物品返回空值，禁止靠名称相似或颜色推断燃料。
    public static Solution of(ItemStack stack) {
        for (Solution solution : values()) if (stack.is(solution.item())) return solution;
        return null;
    }
}
