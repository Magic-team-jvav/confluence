package org.confluence.mod.common.item.paint;

import net.minecraft.world.item.Item;

public class PaintItem extends Item {
    public final int color;

    public PaintItem(int color) {
        super(new Properties().stacksTo(99));
        this.color = color;
    }
}
