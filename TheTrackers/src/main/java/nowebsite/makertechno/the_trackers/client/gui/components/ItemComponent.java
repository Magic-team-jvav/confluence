package nowebsite.makertechno.the_trackers.client.gui.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemComponent implements IRenderElement{
    private final ItemStack itemStack;
    public ItemComponent(ItemStack itemStack) {
        this.itemStack = itemStack.copy();
    }
    @Override
    public void render(@NotNull GuiGraphics graphics, float partialTick) {
        graphics.renderItem(itemStack, 0, 0);
    }

    @Override
    public IRenderElement flush() {
        return this;
    }

    @Override
    public int width() {
        return 16;
    }

    @Override
    public int height() {
        return 16;
    }

    @Override
    public float rescale() {
        return 0.8f;
    }

    // 2026/1/10-20:54 TODO: Interface, Render, and others.
}
