package org.confluence.mod.client.gui.wiki;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.util.GuiUtils;

public class TypeWikiScreen extends BaseWikiScreen {

    private GroupWikiScreen wiki;
    private DeferredRegister<?> reg;
    private WikiRenderType type;

    public TypeWikiScreen(GroupWikiScreen wiki, DeferredRegister<?> reg,
                          WikiRenderType type) {
        super(wiki.getName());
        this.wiki = wiki;
        this.reg = reg;
        this.type = type;
    }

    public DeferredRegister<?> getReg() {
        return reg;
    }

    @Override
    protected void init() {
        int z = 0;
        for (int i = 0; i < reg.getEntries().size(); i++) {
            DeferredItem<?> d = (DeferredItem<?>) reg.getEntries().toArray()[i];
            Item item = d.asItem();
            if (item instanceof BlockItem) continue;
            Button g = Button.builder(Component.empty(), (e) -> this.minecraft.setScreen(
                            new ObjectWikiScreen(item, this, item.getDescriptionId())))
                    .bounds(31 + (z % 14) * 30, height - 245 + (z / 14 * 30), 20,
                            20).tooltip(Tooltip.create(item.getName(item.getDefaultInstance()))).build();
            this.addRenderableWidget(g);
            z++;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (type.equals(WikiRenderType.ITEM)){
            renderItemButton(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    private void renderItemButton(GuiGraphics guiGraphics, int mouseX, int mouseY,
                                  float partialTick){
        int z = 0;
        for (int i = 0; i < reg.getEntries().size(); i++) {
            DeferredItem<?> d = (DeferredItem<?>) reg.getEntries().toArray()[i];
            Item item = d.asItem();
            if (item instanceof BlockItem){
                continue;
            }
            GuiUtils.drawItemWithoutTooltip(guiGraphics, item,minecraft,
                    33 + (z % 14) * 30,
                    height - 243 + (z / 14 * 30),
                    20,
                    20,
                    1
               );
            z++;
        }
    }

    @Override
    public Screen getParentScreen() {
        return wiki;
    }

    public enum WikiRenderType{
        ITEM, ENTITY, BLOCK
    }
}
