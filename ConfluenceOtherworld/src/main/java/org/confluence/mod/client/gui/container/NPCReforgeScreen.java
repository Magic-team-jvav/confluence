package org.confluence.mod.client.gui.container;

import com.google.common.collect.EvictingQueue;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.item.common.CoinItem;
import org.confluence.mod.common.menu.NPCReforgeMenu;
import org.confluence.mod.util.Coins;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;

@SuppressWarnings("UnstableApiUsage")
public class NPCReforgeScreen extends AbstractContainerScreen<NPCReforgeMenu> {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/container/reforge.png");
    private boolean buttonClicked = false;
    private final EvictingQueue<Component> prefixBefore;

    public NPCReforgeScreen(NPCReforgeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.prefixBefore = EvictingQueue.create(5);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos + 19, 0, 0, imageWidth, imageHeight);
        int cost = menu.getCost();
        if (cost < 0x3F3F3F3F) {
            Coins coins = PlayerUtils.decodeCoin(cost);
            int x = leftPos + 52;
            int y = topPos + 1;
            for (Object2IntMap.Entry<CoinItem> entry : coins.platinum2CopperEntries()) {
                ItemStack stack = new ItemStack(entry.getKey(), entry.getIntValue());
                guiGraphics.renderItem(stack, x, y);
                guiGraphics.renderItemDecorations(font, stack, x, y);
                x += 18;
            }
        }
        if (buttonClicked) {
            guiGraphics.blit(BACKGROUND, leftPos + 120, topPos + 43, 177, 0, 18, 18);
        }
        int x = leftPos + 61;
        int y = topPos + 21;
        for (Component component : prefixBefore) {
            guiGraphics.drawString(font, component, x, y += font.lineHeight, -1);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX >= leftPos + 120 && mouseX <= leftPos + 138 && mouseY >= topPos + 43 && mouseY <= topPos + 62 && menu.clickMenuButton(minecraft.player, 0)) {
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.SMITHING_TABLE_USE, minecraft.level.random.nextFloat() * 0.1F + 0.9F));
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
            ItemStack itemStack = menu.getReforgeItem();
            PrefixComponent prefix = PrefixUtils.getPrefix(itemStack);
            if (prefix != null && prefix.type() != PrefixType.UNKNOWN) {
                MutableComponent component = prefix.getName();
                ModRarity rarity = ModRarity.getRarity(itemStack);
                if (rarity != null) {
                    component.withColor(rarity.color());
                }
                prefixBefore.add(component);
            }
            this.buttonClicked = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.buttonClicked = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
