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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.common.menu.IToggleSlot;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;
import org.confluence.mod.client.event.ModClientSetups;
import org.confluence.mod.client.gui.BestiaryScreen;
import org.confluence.mod.client.gui.GuiSprite;
import org.confluence.mod.client.gui.hud.HouseSelectHUD;
import org.confluence.mod.client.handler.bestiary.ClientBestiary;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.data.saved.Team;
import org.confluence.mod.common.menu.ExtraInventoryMenu;
import org.confluence.mod.mixed.IInventoryScreen;
import org.confluence.mod.network.TeamPacket;
import org.confluence.mod.network.c2s.OpenMenuPacketC2S;
import org.confluence.mod.util.ClientUtils;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.api.primitive.TooltipComponentsValue;
import org.confluence.terra_curio.client.handler.InformationHandler;
import org.confluence.terra_curio.client.renderer.tooltip.MultiFunctionTooltip;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.network.InfoDisablePacket;
import top.theillusivec4.curios.common.network.client.CPacketOpenVanilla;

import static org.confluence.mod.common.attachment.ExtraInventory.*;

public class ExtraInventoryScreen extends AbstractContainerScreen<ExtraInventoryMenu> {
    private static final ResourceLocation BACKGROUND = Confluence.asResource("textures/gui/container/extra_inventory.png");
    private static final ResourceLocation ACCESSORY = TerraCurio.asResource("textures/slot/accessory.png");
    private static final WidgetSprites BESTIARY_BUTTON = new WidgetSprites(Confluence.asResource("widget/bestiary_button"), Confluence.asResource("widget/bestiary_button_highlighted"));
    private static final WidgetSprites HOUSE_BUTTON = new WidgetSprites(Confluence.asResource("widget/house_button"), Confluence.asResource("widget/house_button_highlighted"));
    //    private static final TooltipComponentsValue.Storage MECHANICAL$LENS = new TooltipComponentsValue.Storage(Confluence.asResource("textures/gui/information/mechanical_lens.png"), Component.translatable("tooltip.confluence.mechanical_lens"));
    private static final int TEAM_PVPF_INDEX = 0;
    private static final int TEAM_PVPT_INDEX = 1;
    private static final int TEAM_ICON_INDEX = 2;
    private static final GuiSprite[][] TEAM_SPRITES;

    static {
        int length = Team.TEAMS.length;
        TEAM_SPRITES = new GuiSprite[length][3];
        ResourceLocation teamIcon = Confluence.asResource("team/icon");
        ResourceLocation teamPvp = Confluence.asResource("team/pvp");
        GuiSprite pvpFB = new GuiSprite(teamPvp, 36, 306, 0, 288, 18, 18);
        GuiSprite pvpTB = new GuiSprite(teamPvp, 36, 306, 18, 288, 18, 18);
        for (int i = 0; i < length; i++) {
            int j = i * 18;
            TEAM_SPRITES[i] = new GuiSprite[]{
                    new GuiSprite(teamPvp, 36, 306, 0, j, 18, 18).setHovered(pvpFB),
                    new GuiSprite(teamPvp, 36, 306, 18, j, 18, 18).setHovered(pvpTB),
                    new GuiSprite(teamIcon, 10, 170, 0, i * 10, 10, 10)
                            .setHovered(new GuiSprite(teamIcon, 10, 170, 0, 160, 10, 10))
            };
        }
    }

    private final Player player;
    private boolean dyeButtonPressed;
    private ImageButton bestiaryButton;
    private boolean teamPvPT;
    private static int teamCooldown; // 本地全局冷却

    public ExtraInventoryScreen(ExtraInventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.player = playerInventory.player;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new ImageButton(leftPos + 109, topPos + 166, 16, 16, HOUSE_BUTTON, button -> {
            if (menu.getCarried().isEmpty()) {
                getMinecraft().setScreen(null);
                HouseSelectHUD.inSelectHUD = true;
            }
        }));
        addRenderableWidget(this.bestiaryButton = new ImageButton(leftPos + 125, topPos + 166, 16, 16, BESTIARY_BUTTON, button -> {
            if (menu.getCarried().isEmpty()) {
                getMinecraft().pushGuiLayer(this);
                getMinecraft().setScreen(new BestiaryScreen());
            }
        }));
        if (ClientUtils.shouldDisplayTeam()) {
            this.teamPvPT = PlayerSpecialData.of(player).isPvP();
            for (int i = 0; i < TEAM_SPRITES.length; i++) {
                int x = (i % 2) * 10;
                int y = (i / 2) * 10;
                GuiSprite[] sprites = TEAM_SPRITES[i];
                int rightPos = leftPos + imageWidth;
                GuiSprite hovered = sprites[TEAM_PVPF_INDEX].setPos(rightPos, topPos).getHovered();
                if (hovered != null) {
                    hovered.setPos(rightPos, topPos);
                }
                hovered = sprites[TEAM_PVPT_INDEX].setPos(rightPos, topPos).getHovered();
                if (hovered != null) {
                    hovered.setPos(rightPos, topPos);
                }
                x = rightPos + x;
                y = topPos + y + 18;
                hovered = sprites[TEAM_ICON_INDEX].setPos(x, y).getHovered();
                if (hovered != null) {
                    hovered.setPos(x, y);
                }
            }
        }
        // better experience mixin here
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (ClientUtils.shouldDisplayTeam()) {
            renderTeam(guiGraphics, mouseX, mouseY);
        }
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

    private void renderTeam(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Team playerTeam = PlayerSpecialData.of(player).getTeam();
        GuiSprite sprite = TEAM_SPRITES[playerTeam.ordinal()][teamPvPT ? TEAM_PVPT_INDEX : TEAM_PVPF_INDEX];
        if (teamCooldown <= 0) {
            sprite.renderHoveredAndSelf(guiGraphics, mouseX, mouseY);
            if (sprite.isHovered(mouseX, mouseY)) {
                guiGraphics.renderTooltip(font, Component.translatable(
                        teamPvPT ? "message.confluence.disable_pvp.button" : "message.confluence.enable_pvp.button"
                ), mouseX, mouseY);
            }
        } else {
            sprite.render(guiGraphics);
        }
        for (int i = 0; i < TEAM_SPRITES.length; i++) {
            sprite = TEAM_SPRITES[i][TEAM_ICON_INDEX];
            Team team = Team.TEAMS[i];
            if (teamCooldown <= 0) {
                if (playerTeam == team) {
                    GuiSprite hovered = sprite.getHovered();
                    if (hovered != null) {
                        hovered.render(guiGraphics);
                        sprite.render(guiGraphics);
                    }
                } else {
                    sprite.renderHoveredAndSelf(guiGraphics, mouseX, mouseY);
                }
                if (sprite.isHovered(mouseX, mouseY)) {
                    if (playerTeam == team) {
                        if (team == Team.WHITE) {
                            guiGraphics.renderTooltip(font, Component.translatable(
                                    "message.confluence.no_team"
                            ), mouseX, mouseY);
                        } else {
                            guiGraphics.renderTooltip(font, Component.translatable(
                                    "message.confluence.on_team", team.getTitleCaseName()
                            ), mouseX, mouseY);
                        }
                    } else {
                        if (team == Team.WHITE) {
                            guiGraphics.renderTooltip(font, Component.translatable(
                                    "message.confluence.leave_team.button"
                            ), mouseX, mouseY);
                        } else {
                            guiGraphics.renderTooltip(font, Component.translatable(
                                    "message.confluence.join_team.button", team.getTitleCaseName()
                            ), mouseX, mouseY);
                        }
                    }
                }
            } else {
                if (playerTeam == team) {
                    GuiSprite hovered = sprite.getHovered();
                    if (hovered != null) {
                        hovered.render(guiGraphics);
                        sprite.render(guiGraphics);
                    }
                } else {
                    sprite.render(guiGraphics);
                }
            }
        }
    }

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
                if (dyeButtonPressed ? menu.getSlot(SIZE_EXCEPT_ACCESSORY_DYE + i).getItem().isEmpty() : menu.getSlot(containerSize + i).getItem().isEmpty()) {
                    guiGraphics.blit(ACCESSORY, x + 8, y, 0, 0, 16, 16, 16, 16);
                }
                y += 18;
            }
            guiGraphics.blit(BACKGROUND, x, y, 224, 27, 32, 7);
        }
        if (dyeButtonPressed) {
            guiGraphics.blit(BACKGROUND, leftPos + 147, topPos + 33, 194, 0, 18, 20);
        }
        InventoryScreen.renderEntityInInventoryFollowsMouse(guiGraphics, leftPos + 26, topPos + 8, leftPos + 75, topPos + 78, 30, 0.0625F, mouseX, mouseY, minecraft.player);

        RenderSystem.enableBlend();
        int x = leftPos + 178;
        if (ClientUtils.shouldDisplayTeam()) {
            x += 18;
        }
        int y = topPos + 1;
        for (int i = 0; i < 12; i++) { // 跳过机械棱镜的
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

        if (bestiaryButton.isHovered()) {
            guiGraphics.renderTooltip(font, Component.translatable("bestiary.unlocked_count", ClientBestiary.getInstance().getUnlockedCount()), mouseX, mouseY);
        }
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
            this.dyeButtonPressed = !dyeButtonPressed;
            toggleAllSlot();
            getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, dyeButtonPressed ? 1.0F : 0.8F));
            return true;
        }

        int x = leftPos + 178;
        if (ClientUtils.shouldDisplayTeam()) {
            x += 18;
        }
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

        if (teamCooldown <= 0 && ClientUtils.shouldDisplayTeam()) {
            if ((teamPvPT ? TEAM_SPRITES[0][TEAM_PVPT_INDEX] : TEAM_SPRITES[0][TEAM_PVPF_INDEX]).isHovered(mouseX, mouseY)) {
                this.teamPvPT = !teamPvPT;
                PlayerSpecialData.of(player).setPvP(teamPvPT);
                TeamPacket.sendToServer(player);
                this.teamCooldown = 100;
                return true;
            }
            for (int i = 0; i < TEAM_SPRITES.length; i++) {
                if (TEAM_SPRITES[i][TEAM_ICON_INDEX].isHovered(mouseX, mouseY)) {
                    PlayerSpecialData data = PlayerSpecialData.of(player);
                    Team team = Team.TEAMS[i];
                    if (data.getTeam() != team) {
                        data.setTeam(team);
                        TeamPacket.sendToServer(player);
                        this.teamCooldown = 100;
                        return true;
                    }
                    return false;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void containerTick() {
        if (teamCooldown > 0) {
            --this.teamCooldown;
        }
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
