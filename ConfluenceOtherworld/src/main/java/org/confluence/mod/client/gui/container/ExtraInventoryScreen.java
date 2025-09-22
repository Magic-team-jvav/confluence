package org.confluence.mod.client.gui.container;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.common.menu.IToggleSlot;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.event.ModClientSetups;
import org.confluence.mod.client.gui.BestiaryScreen;
import org.confluence.mod.client.gui.hud.HouseSelectHUD;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.menu.ExtraInventoryMenu;
import org.confluence.mod.integration.mine_team.ExtraTeamRender;
import org.confluence.mod.mixed.IInventoryScreen;
import org.confluence.mod.network.c2s.OpenMenuPacketC2S;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.api.primitive.TooltipComponentsValue;
import org.confluence.terra_curio.client.handler.InformationHandler;
import org.confluence.terra_curio.client.renderer.tooltip.MultiFunctionTooltip;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.network.InfoDisablePacket;
import top.theillusivec4.curios.common.network.client.CPacketOpenVanilla;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventoryFollowsMouse;
import static org.confluence.mod.common.attachment.ExtraInventory.*;

public class ExtraInventoryScreen extends AbstractContainerScreen<ExtraInventoryMenu> {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/container/extra_inventory.png");
    private static final ResourceLocation ACCESSORY = TerraCurio.asResource("textures/slot/accessory.png");
    private static final WidgetSprites BESTIARY_BUTTON = new WidgetSprites(Confluence.asResource("widget/bestiary_button"), Confluence.asResource("widget/bestiary_button_highlighted"));
    private static final WidgetSprites HOUSE_BUTTON = new WidgetSprites(Confluence.asResource("widget/house_button"), Confluence.asResource("widget/house_button_highlighted"));

    private boolean buttonPressed = false;
    private final ExtraTeamRender teamRender = new ExtraTeamRender(this);

    public ExtraInventoryScreen(ExtraInventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        teamRender.renderTeamIcon(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void init() {
        super.init();
        teamRender.initButton();
        addRenderableWidget(new ImageButton(leftPos + 109, topPos + 166, 16, 16, HOUSE_BUTTON, button -> {
            if (menu.getCarried().isEmpty()) {
                getMinecraft().setScreen(null);
                HouseSelectHUD.inSelectHUD = true;
            }
        }));
        addRenderableWidget(new ImageButton(leftPos + 125, topPos + 166, 16, 16, BESTIARY_BUTTON, button -> {
            if (menu.getCarried().isEmpty()) {
                getMinecraft().pushGuiLayer(this);
                getMinecraft().setScreen(new BestiaryScreen());
            }
        }));
        // better experience mixin here
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight + 24);
        ExtraInventory extraInventory = menu.extraInventory;
        int containerSize = extraInventory.getContainerSize();
        int sizeAccessoryDye = extraInventory.getSizeAccessoryDye();
        int size = containerSize - sizeAccessoryDye;
        for (int i = VANITY_ARMOR_START; i < size; i++) {
            Slot slot = menu.getSlot(i);
            if (!slot.isActive() || slot.hasItem()) continue;
            if (i < COINS_START) {
                renderVanityArmor(guiGraphics, i);
            } else if (i < AMMO_START) {
                guiGraphics.blit(BACKGROUND, leftPos + 81, topPos + (i - COINS_START) * 18 + 8, 177, 153, 16, 16);
            } else if (i < EQUIPMENT_START) {
                guiGraphics.blit(BACKGROUND, leftPos + 99, topPos + (i - AMMO_START) * 18 + 8, 177, 136, 16, 16);
            } else if (i < TRASH_START) {
                renderEquipment(guiGraphics, i - EQUIPMENT_START);
            } else if (i < ACCESSORY_DYE_START) {
                guiGraphics.blit(BACKGROUND, leftPos + 152, topPos + 166, 177, 170, 16, 16);
            }
        }
        if (sizeAccessoryDye > 0) {
            int x = leftPos - 33;
            int y = topPos + 7;
            guiGraphics.blit(BACKGROUND, x, topPos, 224, 0, 32, 7);
            for (int i = 0; i < sizeAccessoryDye; i++) {
                guiGraphics.blit(BACKGROUND, x, y, 224, 8, 32, 18);
                if (buttonPressed ? menu.getSlot(SIZE_EXCEPT_ACCESSORY_DYE + i).getItem().isEmpty() : menu.getSlot(containerSize + i).getItem().isEmpty()) {
                    guiGraphics.blit(ACCESSORY, x + 8, y, 0, 0, 16, 16, 16, 16);
                }
                y += 18;
            }
            guiGraphics.blit(BACKGROUND, x, y, 224, 27, 32, 7);
        }
        if (buttonPressed) {
            guiGraphics.blit(BACKGROUND, leftPos + 147, topPos + 33, 194, 0, 18, 20);
        }
        renderEntityInInventoryFollowsMouse(guiGraphics, leftPos + 26, topPos + 8, leftPos + 75, topPos + 78, 30, 0.0625F, mouseX, mouseY, minecraft.player);

        RenderSystem.enableBlend();
        int x = leftPos + 196;
        int y = topPos + 1;
        for (int i = 0; i < InformationHandler.DISABLE.length; i++) {
            if (!InformationHandler.hasInfoData(i)) continue;
            TooltipComponentsValue.Storage storage = TCItems.FULL_INFO.get(i);

            boolean disable = InformationHandler.DISABLE[i];
            if (disable) RenderSystem.setShaderColor(1, 1, 1, 0.5F);
            guiGraphics.blit(storage.texture(), x, y, 0, 0, 7, 7, 7, 7);
            if (disable) RenderSystem.setShaderColor(1, 1, 1, 1);

            if (mouseX >= x && mouseX < x + 7 && mouseY >= y && mouseY < y + 7) {
                RenderSystem.setShaderColor(1, 1, 0, 1);
                guiGraphics.blit(MultiFunctionTooltip.HIGHLIGHT, x - 1, y - 1, 0, 0, 9, 9, 9, 9);
                RenderSystem.setShaderColor(1, 1, 1, 1);
                guiGraphics.renderTooltip(font, storage.text(), mouseX, mouseY);
            }

            y += 8;
        }
        RenderSystem.disableBlend();
    }

    private void renderEquipment(GuiGraphics guiGraphics, int i) {
        if (i >= SIZE_EQUIPMENT) i -= SIZE_EQUIPMENT;
        boolean isMount = i == MOUNT_INDEX;
        guiGraphics.blit(BACKGROUND,
                leftPos + (isMount ? 148 : 121), topPos + (isMount ? 8 : i * 18 + 8),
                isMount ? 194 : 177, isMount ? 68 : 68 + i * 17,
                16, 16
        );
    }

    private void renderVanityArmor(GuiGraphics guiGraphics, int i) {
        if (i >= SIZE_VANITY_ARMOR) i -= SIZE_VANITY_ARMOR;
        guiGraphics.blit(BACKGROUND, leftPos + 8, topPos + i * 18 + 8, 177, i * 17, 16, 16);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX >= leftPos + 147 && mouseX <= leftPos + 165 && mouseY >= topPos + 33 && mouseY <= topPos + 53) {
            this.buttonPressed = !buttonPressed;
            toggleAllSlot();
            getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, buttonPressed ? 1.0F : 0.8F));
            return true;
        }

        int x = leftPos + 196;
        int y = topPos + 1;
        boolean[] disable = InformationHandler.DISABLE;
        for (int i = 0; i < disable.length; i++) {
            if (!InformationHandler.hasInfoData(i)) continue;
            if (mouseX >= x && mouseX < x + 7 && mouseY >= y && mouseY < y + 7) {
                disable[i] = !disable[i];
                InfoDisablePacket.sendToServer(disable);
                return true;
            }
            y += 8;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void toggleAllSlot() {
        ExtraInventory extraInventory = menu.extraInventory;
        int size = extraInventory.getContainerSize() + extraInventory.getSizeAccessoryDye();
        for (int i = 0; i < size; i++) {
            if (menu.getSlot(i) instanceof IToggleSlot toggleSlot) {
                toggleSlot.setEnable(!toggleSlot.isEnabled());
            }
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) return;
        ItemStack stack = player.containerMenu.getCarried();
        player.containerMenu.setCarried(ItemStack.EMPTY);
        InventoryScreen inventory = new InventoryScreen(player);
        minecraft.setScreen(inventory);
        player.containerMenu.setCarried(stack);
        PacketDistributor.sendToServer(new CPacketOpenVanilla(stack));
    }

    @Override
    public <T extends GuiEventListener & Renderable & NarratableEntry> T addRenderableWidget(T widget) {
        return super.addRenderableWidget(widget);
    }

    public static ImageButton getExtraInventoryButton(EffectRenderingInventoryScreen<?> screen, boolean isInventoryScreen) {
        int x = screen.getGuiLeft() - 16 + ClientConfigs.extraInventoryButtonOffsetX;
        if (!isInventoryScreen && ClientConfigs.extraInventoryButtonOffsetX >= 192) x += 19;
        int y = screen.getGuiTop() + 2 + ClientConfigs.extraInventoryButtonOffsetY;
        ImageButton extraInventoryButton = new ImageButton(x, y, 16, 16, ModClientSetups.EXTRA_INVENTORY_BUTTON, button -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                ItemStack stack = player.containerMenu.getCarried();
                player.containerMenu.setCarried(ItemStack.EMPTY);
                OpenMenuPacketC2S.sendToServer(OpenMenuPacketC2S.EXTRA_INVENTORY, stack);
            }
        });
        if (isInventoryScreen) {
            IInventoryScreen.of((InventoryScreen) screen).confluence$setExtraButton(extraInventoryButton);
        }
        return extraInventoryButton;
    }
}
