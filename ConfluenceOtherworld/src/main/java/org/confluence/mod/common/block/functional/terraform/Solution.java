package org.confluence.mod.common.block.functional.terraform;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.item.MaterialItems;

import java.util.function.Supplier;

// 溶液的物品、显示颜色和普通机器兼容性集中定义，顺序与改造表一致。
public enum Solution {
    GREEN(0x17b71b, false, MaterialItems.GREEN_SOLUTION),
    BLUE(0x3dc1dc, false, MaterialItems.BLUE_SOLUTION),
    PURPLE(0x7516b0, false, MaterialItems.PURPLE_SOLUTION),
    DARK_BLUE(0x3d67dc, false, MaterialItems.DARK_BLUE_SOLUTION),
    RED(0xc30909, false, MaterialItems.RED_SOLUTION),
    YELLOW(0xb79217, true, MaterialItems.YELLOW_SOLUTION),
    WHITE(0xbffffe, true, MaterialItems.WHITE_SOLUTION),
    BROWN(0x925923, true, MaterialItems.BROWN_SOLUTION);

    public final int color;
    public final boolean advanced;
    private final Supplier<Item> item;

    Solution(int color, boolean advanced, Supplier<Item> item) {
        this.color = color;
        this.advanced = advanced;
        this.item = item;
    }

    public Item item() {return item.get();}

    public String id() {return name().toLowerCase(java.util.Locale.ROOT);}

    // 非溶液物品返回空值，禁止靠名称相似或颜色推断燃料。
    public static Solution of(ItemStack stack) {
        for (Solution solution : values()) if (stack.is(solution.item())) return solution;
        return null;
    }
}
