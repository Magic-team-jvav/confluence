package org.confluence.mod.client.gui.container;

import com.google.common.collect.EvictingQueue;
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
import org.confluence.mod.common.init.item.ModItems;
import org.confluence.mod.common.menu.NPCReforgeMenu;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
import org.confluence.terraentity.entity.ai.keyframe.animation.KeyframeAnimation;

public class NPCReforgeScreen extends AbstractContainerScreen<NPCReforgeMenu> {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/container/reforge.png");
    private boolean buttonClicked = false;
    private final EvictingQueue<Component> prefixBefore;
    int clickTime = 500;
    KeyframeAnimation interpolator;


    public NPCReforgeScreen(NPCReforgeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.prefixBefore = EvictingQueue.create(5);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos + 19, 0, 0, imageWidth, imageHeight);
        int cost = menu.getCost();
        if (cost < 0x3F3F3F3F) {
            int[] coins = PlayerUtils.decodeCoin(cost);
            int x = leftPos + 52;
            int y = topPos + 1;
            if (coins[3] > 0) {
                ItemStack stack = new ItemStack(ModItems.PLATINUM_COIN.get(), coins[3]);
                guiGraphics.renderItem(stack, x, y);
                guiGraphics.renderItemDecorations(font, stack, x, y);
                x += 18;
            }
            if (coins[2] > 0) {
                ItemStack stack = new ItemStack(ModItems.GOLDEN_COIN.get(), coins[2]);
                guiGraphics.renderItem(stack, x, y);
                guiGraphics.renderItemDecorations(font, stack, x, y);
                x += 18;
            }
            if (coins[1] > 0) {
                ItemStack stack = new ItemStack(ModItems.SILVER_COIN.get(), coins[1]);
                guiGraphics.renderItem(stack, x, y);
                guiGraphics.renderItemDecorations(font, stack, x, y);
                x += 18;
            }
            if (coins[0] > 0) {
                ItemStack stack = new ItemStack(ModItems.COPPER_COIN.get(), coins[0]);
                guiGraphics.renderItem(stack, x, y);
                guiGraphics.renderItemDecorations(font, stack, x, y);
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

    protected void containerTick() {
        clickTime++;
    }

    protected void init() {
        super.init();
        this.interpolator = KeyframeAnimation.builder()
                .addKeyframe(0,0)
                .addKeyframe(15, 55)
                .addKeyframe(20, 60)
                .addKeyframe(40, 60)
                .addKeyframe(45, 65)
                .addKeyframe(60, 100)
                .build();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);

        ItemStack itemStack = menu.getReforgeItem();
        if (!itemStack.isEmpty()) {
            PrefixComponent prefix = PrefixUtils.getPrefix(itemStack);

            if (prefix != null && clickTime > 0 && clickTime < 60) {
                double v = interpolator.cal(clickTime + pPartialTick);
                pGuiGraphics.pose().pushPose();

                MutableComponent component = prefix.getName().append(" ").append(Component.translatable(itemStack.getItem().getDescriptionId()));
                ModRarity rarity = ModRarity.getRarity(itemStack);
                if (rarity != null) {
                    component.withColor(rarity.color());
                }
                pGuiGraphics.pose().translate(0, -v * 0.5f, 0);
                float f = (clickTime / 60.0f);
                f = f * (1 - f) * 4f;
                f = Math.min(1, f);
                pGuiGraphics.setColor(1, 1, 1, f);
                pGuiGraphics.drawString(font, component, leftPos + 12, topPos + 40, -1);
                pGuiGraphics.setColor(1, 1, 1, 1);
                pGuiGraphics.pose().popPose();
            }
        }
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
            this.clickTime = 0;
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
