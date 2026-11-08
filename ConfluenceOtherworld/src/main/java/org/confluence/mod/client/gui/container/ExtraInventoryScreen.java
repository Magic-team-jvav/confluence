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
import org.confluence.mod.client.gui.hud.HouseSelectHud;
import org.confluence.mod.client.handler.bestiary.ClientBestiary;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.attachment.PlayerSpecialData;
import org.confluence.mod.common.data.Team;
import org.confluence.mod.common.menu.ExtraInventoryMenu;
import org.confluence.mod.common.menu.TerraInventoryActions;
import org.confluence.mod.common.menu.StationCrafting;
import org.confluence.mod.network.c2s.CraftRecipePacketC2S;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.confluence.mod.client.gui.hud.TerraHotbarHud;
import org.confluence.mod.common.menu.TerraInventoryLayout;
import org.confluence.mod.network.c2s.FavoriteSlotPacketC2S;
import org.confluence.mod.network.c2s.SortInventoryPacketC2S;
import net.minecraft.client.gui.components.Tooltip;
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
    private static final ResourceLocation SLOTS = Confluence.asResource("textures/gui/container/terra_inventory.png");
    private static final int SLOTS_WIDTH = 256;
    private static final int SLOTS_HEIGHT = 64;
    private static final WidgetSprites SORT_BUTTON = new WidgetSprites(Confluence.asResource("widget/sort_button"), Confluence.asResource("widget/sort_button_highlighted"));
    private static final WidgetSprites QUICK_STACK_BUTTON = new WidgetSprites(Confluence.asResource("widget/quick_stack_button"), Confluence.asResource("widget/quick_stack_button_highlighted"));    private static final WidgetSprites BESTIARY_BUTTON = new WidgetSprites(Confluence.asResource("widget/bestiary_button"), Confluence.asResource("widget/bestiary_button_highlighted"));
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
    public static int teamCooldown; // 本地全局冷却

    public ExtraInventoryScreen(ExtraInventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.player = playerInventory.player;
        this.imageWidth = TerraInventoryLayout.WIDTH;
        this.imageHeight = TerraInventoryLayout.HEIGHT;
    }

    /// 下一次打开原版背包时不要转到泰拉背包（由“原版背包”按钮设置）
    private static boolean allowVanillaOnce = false;

    public static boolean consumeAllowVanilla() {
        boolean allow = allowVanillaOnce;
        allowVanillaOnce = false;
        return allow;
    }

    @Override
    protected void init() {
        super.init();
        // 泰拉的背包贴在屏幕左上角
        this.leftPos = TerraInventoryLayout.SCREEN_MARGIN;
        this.topPos = TerraInventoryLayout.SCREEN_TOP;
        // 切换到原版界面并保存（创造模式下原版界面就是创造物品栏；泰拉背包里有自己的“物品”页）
        ImageButton vanillaButton = new ImageButton(leftPos + TerraInventoryLayout.VANILLA_BUTTON_X, topPos + TerraInventoryLayout.VANILLA_BUTTON_Y, 16, 16, ModClientSetups.EXTRA_INVENTORY_BUTTON, button -> openVanillaInventory());
        vanillaButton.setTooltip(Tooltip.create(Component.translatable("gui.confluence.vanilla_interface")));
        addRenderableWidget(vanillaButton);
        ImageButton sortButton = new ImageButton(leftPos + TerraInventoryLayout.SORT_BUTTON_X, topPos + TerraInventoryLayout.SORT_BUTTON_Y, 16, 16, SORT_BUTTON, button -> {
            if (menu.getCarried().isEmpty()) SortInventoryPacketC2S.sendToServer();
        });
        sortButton.setTooltip(Tooltip.create(Component.translatable("gui.confluence.sort_inventory")));
        addRenderableWidget(sortButton);
        // 泰拉：“快速堆叠到附近的箱子”，在整理按钮左边
        ImageButton quickStackButton = new ImageButton(leftPos + TerraInventoryLayout.SORT_BUTTON_X - TerraInventoryLayout.CELL, topPos + TerraInventoryLayout.SORT_BUTTON_Y, 16, 16, QUICK_STACK_BUTTON, button -> {
            if (menu.getCarried().isEmpty()) {
                org.confluence.mod.network.c2s.TerraSlotActionPacketC2S.sendToServer(org.confluence.mod.common.menu.TerraInventoryActions.QUICK_STACK_NEARBY, -1);
            }
        });
        quickStackButton.setTooltip(Tooltip.create(Component.translatable("gui.confluence.quick_stack_nearby")));
        addRenderableWidget(quickStackButton);
        addRenderableWidget(new ImageButton(leftPos + TerraInventoryLayout.HOUSE_BUTTON_X, topPos + TerraInventoryLayout.BUTTONS_Y, 16, 16, HOUSE_BUTTON, button -> {
            if (menu.getCarried().isEmpty()) {
                super.onClose();
                HouseSelectHud.inSelectHUD = true;
            }
        }));
        addRenderableWidget(this.bestiaryButton = new ImageButton(leftPos + TerraInventoryLayout.BESTIARY_BUTTON_X, topPos + TerraInventoryLayout.BESTIARY_BUTTON_Y, 16, 16, BESTIARY_BUTTON, button -> {
            if (menu.getCarried().isEmpty()) {
                super.onClose();
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
                int rightPos = leftPos + TerraInventoryLayout.TEAM_X;
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
        // 创造模式：合成列表的位置可切换成“物品”页（全部物品 + 筛选 + 搜索）
        this.creativePanel = new CreativeItemPanel(this, (net.minecraft.client.player.LocalPlayer) player, font);
        creativePanel.init(height, !org.confluence.mod.client.gui.hud.TerraBuffHud.visibleEffects(player).isEmpty());
        addRenderableWidget(creativePanel.searchBox());
        // better experience mixin here
    }

    private CreativeItemPanel creativePanel;
    /// 这次按下由物品页处理了，松开也不交给原版（否则会当成拖拽/扔出）
    private boolean panelPressed;

    private boolean creativeItems() {
        return creativePanel != null && creativePanel.active();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (panelPressed && creativePanel != null && creativePanel.mouseDragged(mouseX, mouseY)) return true; // 拖动物品页的滚动条
        if (panelPressed) return true;
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    void onCreativeTabChanged() {
        this.craftRefreshTicks = 0;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (creativeItems() && creativePanel.keyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (creativePanel != null) creativePanel.mouseReleased();
        if (panelPressed) {
            panelPressed = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (ClientUtils.shouldDisplayTeam()) {
            renderTeam(guiGraphics, mouseX, mouseY);
        }
        if (creativeItems()) {
            creativePanel.renderTabs(guiGraphics, mouseX, mouseY);
            creativePanel.render(guiGraphics, mouseX, mouseY, SLOTS, SLOTS_WIDTH, SLOTS_HEIGHT);
        } else {
            renderCrafting(guiGraphics, mouseX, mouseY);
        }
        renderDefense(guiGraphics, mouseX, mouseY);
        renderBuffs(guiGraphics, mouseX, mouseY);
        renderAccessoryEyes(guiGraphics);
        renderLoadouts(guiGraphics, mouseX, mouseY);
        renderTooltip(guiGraphics, mouseX, mouseY);
        if (creativeItems()) creativePanel.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    /// 鼠标在钱币栏上时，提示里加上身上钱币的总数（泰拉把钱币单独放一列，总数一眼可见）
    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        int eye = eyeAt(x, y);
        if (eye >= 0 && menu.getCarried().isEmpty()) {
            boolean visible = accessoryVisible(eye);
            guiGraphics.renderTooltip(font, Component.translatable(visible ? "gui.confluence.accessory.visible" : "gui.confluence.accessory.hidden"), x, y);
            return;
        }
        if (hoveredSlot instanceof org.confluence.mod.common.menu.ToggleCurioSlot curioSlot && curioSlot.isCosmetic() && !hoveredSlot.hasItem() && menu.getCarried().isEmpty()) {
            guiGraphics.renderTooltip(font, Component.translatable("gui.confluence.accessory.vanity"), x, y);
            return;
        }
        if (hoveredSlot != null && menu.getCarried().isEmpty()
                && hoveredSlot.index >= org.confluence.mod.common.attachment.ExtraInventory.COINS_START
                && hoveredSlot.index < org.confluence.mod.common.attachment.ExtraInventory.AMMO_START) {
            long money = org.confluence.mod.util.PlayerUtils.getMoney(player, false);
            List<Component> lines = new ArrayList<>(hoveredSlot.hasItem() ? getTooltipFromContainerItem(hoveredSlot.getItem()) : List.of());
            lines.add(Component.translatable("gui.confluence.coins_total", money > 0 ? org.confluence.mod.util.ClientUtils.formatPrice(money) : Component.literal("0")));
            guiGraphics.renderTooltip(font, lines, hoveredSlot.hasItem() ? hoveredSlot.getItem().getTooltipImage() : java.util.Optional.empty(), x, y);
            return;
        }
        super.renderTooltip(guiGraphics, x, y);
    }

    // ---------- 泰拉式饰品“眼睛”：显示/隐藏饰品外观（属性照常生效），用 Curios 自己的渲染开关 ----------
    private static final int EYE = 8;

    private top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler accessoryHandler() {
        return top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player)
                .map(handler -> handler.getCurios().get(TerraCurio.CURIO_SLOT)).orElse(null);
    }

    private boolean accessoryVisible(int index) {
        var handler = accessoryHandler();
        return handler == null || index >= handler.getRenders().size() || handler.getRenders().get(index);
    }

    /// 饰品槽左上角的眼睛：返回饰品序号，没有返回 -1
    private int eyeAt(double mouseX, double mouseY) {
        for (int i = menu.extraInventory.getContainerSize(); i < menu.invStart; i++) {
            Slot slot = menu.getSlot(i);
            if (!(slot instanceof org.confluence.mod.common.menu.ToggleCurioSlot curioSlot) || curioSlot.isCosmetic() || !slot.isActive()) continue;
            int x = leftPos + slot.x - 1;
            int y = topPos + slot.y - 1;
            if (mouseX >= x && mouseX < x + EYE && mouseY >= y && mouseY < y + EYE) return slot.getContainerSlot();
        }
        return -1;
    }

    private void renderAccessoryEyes(GuiGraphics guiGraphics) {
        var handler = accessoryHandler();
        if (handler == null || !handler.canToggleRendering()) return;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 300);
        RenderSystem.enableBlend();
        for (int i = menu.extraInventory.getContainerSize(); i < menu.invStart; i++) {
            Slot slot = menu.getSlot(i);
            if (!(slot instanceof org.confluence.mod.common.menu.ToggleCurioSlot curioSlot) || curioSlot.isCosmetic() || !slot.isActive()) continue;
            boolean visible = accessoryVisible(slot.getContainerSlot());
            guiGraphics.blit(SLOTS, leftPos + slot.x - 1, topPos + slot.y - 1, visible ? 44 : 52, 18, EYE, EYE, SLOTS_WIDTH, SLOTS_HEIGHT);
        }
        RenderSystem.disableBlend();
        guiGraphics.pose().popPose();
    }

    // ---------- 泰拉的装备栏位（三套装备）：防御盾牌下面三个彩色方块，当前的高亮 ----------
    /// 3 个 11 像素方块 + 2 个间隔 = 37，比 40 宽的这一列窄，右边不碰染料格的边框
    private static final int LOADOUT_SIZE = 11;
    private static final int LOADOUT_GAP = 2;
    private static final int[] LOADOUT_COLORS = {0xFF3FA34D, 0xFF3F6FD0, 0xFF9A4FD0};

    private int loadoutX(int index) {
        return leftPos + TerraInventoryLayout.TEAM_X + 1 + index * (LOADOUT_SIZE + LOADOUT_GAP);
    }

    private int loadoutY() {
        return defenseY() + 29;
    }

    private int loadoutAt(double mouseX, double mouseY) {
        int y = loadoutY();
        if (mouseY < y || mouseY >= y + LOADOUT_SIZE) return -1;
        for (int i = 0; i < org.confluence.mod.common.menu.TerraLoadouts.COUNT; i++) {
            int x = loadoutX(i);
            if (mouseX >= x && mouseX < x + LOADOUT_SIZE) return i;
        }
        return -1;
    }

    private void renderLoadouts(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int current = org.confluence.mod.common.menu.TerraLoadouts.clientIndex;
        int hovered = loadoutAt(mouseX, mouseY);
        int y = loadoutY();
        for (int i = 0; i < org.confluence.mod.common.menu.TerraLoadouts.COUNT; i++) {
            int x = loadoutX(i);
            int color = LOADOUT_COLORS[i];
            if (i != current) color = (color & 0x00FFFFFF) | 0x90000000; // 不是当前的：半透明
            guiGraphics.fill(x, y, x + LOADOUT_SIZE, y + LOADOUT_SIZE, color);
            guiGraphics.renderOutline(x, y, LOADOUT_SIZE, LOADOUT_SIZE, i == current ? 0xFFFFFFFF : hovered == i ? 0xFFF0C030 : 0xFF202020);
            String number = String.valueOf(i + 1);
            // 数字居中：字形宽 = font.width - 1（最后一列是字距），高 7
            guiGraphics.drawString(font, number, x + (LOADOUT_SIZE - (font.width(number) - 1) + 1) / 2, y + (LOADOUT_SIZE - 7 + 1) / 2, i == current ? 0xFFFFFF : 0xC0C0C0, true);
        }
        if (hovered >= 0 && menu.getCarried().isEmpty()) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.translatable("gui.confluence.loadout", hovered + 1));
            lines.add(Component.translatable(hovered == current ? "gui.confluence.loadout.current" : "gui.confluence.loadout.switch").withStyle(ChatFormatting.GRAY));
            guiGraphics.renderComponentTooltip(font, lines, mouseX, mouseY);
        }
    }

    // ---------- 泰拉式防御与增益 ----------
    private int defenseX() {
        return leftPos + TerraInventoryLayout.TEAM_X + (2 * TerraInventoryLayout.CELL + TerraInventoryLayout.GAP - 23) / 2;
    }

    private int defenseY() {
        return topPos + TerraInventoryLayout.BESTIARY_BUTTON_Y + TerraInventoryLayout.CELL + 4;
    }

    /// 泰拉：防御值只在背包里显示，盾牌在饰品栏左侧
    private void renderDefense(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x = defenseX();
        int y = defenseY();
        RenderSystem.enableBlend();
        org.confluence.mod.client.gui.hud.TerraStyleArmorHud.Armor.drawShield(guiGraphics, getMinecraft(), x, y);
        RenderSystem.disableBlend();
        if (mouseX >= x && mouseX < x + 23 && mouseY >= y && mouseY < y + 25 && menu.getCarried().isEmpty()) {
            guiGraphics.renderTooltip(font, Component.translatable("gui.confluence.defense", player.getArmorValue()), mouseX, mouseY);
        }
    }

    private int buffsLeft() {
        return leftPos + TerraInventoryLayout.CRAFT_X;
    }

    /// 合成列表下方
    private int buffsTop() {
        if (creativeItems()) return creativePanel.bottom() + 6;
        return topPos + TerraInventoryLayout.CRAFT_Y + TerraInventoryLayout.CRAFT_ROWS * TerraInventoryLayout.CELL + 6;
    }

    private void renderBuffs(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        List<net.minecraft.world.effect.MobEffectInstance> effects = org.confluence.mod.client.gui.hud.TerraBuffHud.visibleEffects(player);
        if (effects.isEmpty()) return;
        org.confluence.mod.client.gui.hud.TerraBuffHud.render(guiGraphics, font, effects, buffsLeft(), buffsTop());
        int hovered = org.confluence.mod.client.gui.hud.TerraBuffHud.indexAt(effects.size(), buffsLeft(), buffsTop(), mouseX, mouseY);
        if (hovered >= 0 && menu.getCarried().isEmpty()) {
            guiGraphics.renderComponentTooltip(font, org.confluence.mod.client.gui.hud.TerraBuffHud.tooltip(effects.get(hovered)), mouseX, mouseY);
        }
    }

    // ---------- 泰拉式合成列表 ----------
    private List<RecipeHolder<?>> craftingCandidates;
    private List<RecipeHolder<?>> craftable = List.of();
    private int craftScroll;
    private int craftRefreshTicks;

    @Override
    protected void containerTick() {
        super.containerTick();
        if (craftRefreshTicks-- <= 0) {
            refreshCraftable();
            this.craftRefreshTicks = 10;
        }
    }

    /// 附近制作站 + 背包材料 → 当前能做的配方（客户端预览，真正合成由服务端校验）
    private void refreshCraftable() {
        if (craftingCandidates == null) craftingCandidates = StationCrafting.candidates(player.level());
        Set<RecipeType<?>> stations = StationCrafting.nearbyStations(player);
        List<RecipeHolder<?>> list = new ArrayList<>();
        for (RecipeHolder<?> holder : craftingCandidates) {
            Recipe<?> recipe = holder.value();
            if (StationCrafting.isAvailable(player, recipe, stations) && StationCrafting.hasMaterials(player.getInventory(), StationCrafting.requirements(recipe))) {
                list.add(holder);
            }
        }
        this.craftable = list;
        int maxScroll = Math.max(0, (craftable.size() + TerraInventoryLayout.CRAFT_COLUMNS - 1) / TerraInventoryLayout.CRAFT_COLUMNS - TerraInventoryLayout.CRAFT_ROWS);
        this.craftScroll = Math.min(craftScroll, maxScroll);
    }

    private int craftingIndexAt(double mouseX, double mouseY) {
        if (creativeItems()) return -1;
        int x = (int) mouseX - leftPos - TerraInventoryLayout.CRAFT_X + 1;
        int y = (int) mouseY - topPos - TerraInventoryLayout.CRAFT_Y + 1;
        if (x < 0 || y < 0) return -1;
        int column = x / TerraInventoryLayout.CELL;
        int row = y / TerraInventoryLayout.CELL;
        if (column >= TerraInventoryLayout.CRAFT_COLUMNS || row >= TerraInventoryLayout.CRAFT_ROWS) return -1;
        int index = (row + craftScroll) * TerraInventoryLayout.CRAFT_COLUMNS + column;
        return index < craftable.size() ? index : -1;
    }

    private void renderCrafting(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int baseX = leftPos + TerraInventoryLayout.CRAFT_X;
        int baseY = topPos + TerraInventoryLayout.CRAFT_Y;
        Component label = Component.translatable("gui.confluence.crafting");
        if (player.isCreative() && creativePanel != null) {
            creativePanel.renderTabs(guiGraphics, mouseX, mouseY); // 创造模式：“合成 | 物品”页签
        } else {
            guiGraphics.drawString(font, label, baseX, topPos + TerraInventoryLayout.CRAFT_LABEL_Y, 0xFFFFFF, true);
        }
        if (craftable.isEmpty() && !player.isCreative()) {
            guiGraphics.drawString(font, Component.translatable("gui.confluence.crafting.none"), baseX + font.width(label) + 6, topPos + TerraInventoryLayout.CRAFT_LABEL_Y, 0xA0A0A0, true);
        }
        RenderSystem.enableBlend();
        int start = craftScroll * TerraInventoryLayout.CRAFT_COLUMNS;
        int visible = TerraInventoryLayout.CRAFT_COLUMNS * TerraInventoryLayout.CRAFT_ROWS;
        for (int i = 0; i < visible && start + i < craftable.size(); i++) {
            int x = baseX + (i % TerraInventoryLayout.CRAFT_COLUMNS) * TerraInventoryLayout.CELL;
            int y = baseY + (i / TerraInventoryLayout.CRAFT_COLUMNS) * TerraInventoryLayout.CELL;
            guiGraphics.blit(SLOTS, x - 1, y - 1, 18, 0, 18, 18, SLOTS_WIDTH, SLOTS_HEIGHT);
            ItemStack result = craftable.get(start + i).value().getResultItem(player.level().registryAccess());
            guiGraphics.renderItem(result, x, y);
            guiGraphics.renderItemDecorations(font, result, x, y);
        }
        RenderSystem.disableBlend();
        int hovered = craftingIndexAt(mouseX, mouseY);
        if (hovered >= 0 && menu.getCarried().isEmpty()) {
            int x = baseX + ((hovered - start) % TerraInventoryLayout.CRAFT_COLUMNS) * TerraInventoryLayout.CELL;
            int y = baseY + ((hovered - start) / TerraInventoryLayout.CRAFT_COLUMNS) * TerraInventoryLayout.CELL;
            AbstractContainerScreen.renderSlotHighlight(guiGraphics, x, y, 0);
            Recipe<?> recipe = craftable.get(hovered).value();
            ItemStack result = recipe.getResultItem(player.level().registryAccess());
            List<Component> lines = new ArrayList<>(getTooltipFromContainerItem(result));
            lines.add(Component.translatable("gui.confluence.crafting.requires").withStyle(ChatFormatting.GRAY));
            for (StationCrafting.Requirement requirement : StationCrafting.requirements(recipe)) {
                ItemStack[] items = requirement.ingredient().getItems();
                Component name = items.length > 0 ? items[0].getHoverName() : Component.literal("?");
                lines.add(Component.literal("  " + requirement.count() + " × ").append(name).withStyle(ChatFormatting.WHITE));
            }
            lines.add(Component.translatable("gui.confluence.crafting.hint").withStyle(ChatFormatting.DARK_GRAY));
            guiGraphics.renderComponentTooltip(font, lines, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (creativeItems()) {
            if (creativePanel.mouseScrolled(mouseX, mouseY, scrollY)) return true;
            return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }
        int x = (int) mouseX - leftPos - TerraInventoryLayout.CRAFT_X;
        int y = (int) mouseY - topPos - TerraInventoryLayout.CRAFT_Y;
        if (x >= 0 && y >= 0 && x < TerraInventoryLayout.CRAFT_COLUMNS * TerraInventoryLayout.CELL && y < TerraInventoryLayout.CRAFT_ROWS * TerraInventoryLayout.CELL) {
            int maxScroll = Math.max(0, (craftable.size() + TerraInventoryLayout.CRAFT_COLUMNS - 1) / TerraInventoryLayout.CRAFT_COLUMNS - TerraInventoryLayout.CRAFT_ROWS);
            this.craftScroll = Math.max(0, Math.min(maxScroll, craftScroll - (int) Math.signum(scrollY)));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {}

    /// 泰拉风格：不压暗、不模糊世界（原版这里先画半透明背景再调用 renderBg，这里只保留后者）
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBg(guiGraphics, partialTick, mouseX, mouseY);
    }

    private int infoIconsX() {
        return leftPos + TerraInventoryLayout.TEAM_X + (ClientUtils.shouldDisplayTeam() ? 24 : 0);
    }

    private void openVanillaInventory() {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return;
        // 切换到原版界面并保存（再次切回：原版背包右上方的按钮）
        ClientConfigs.setTerrariaInterface(false);
        org.confluence.mod.network.c2s.TerraUiModePacketC2S.sendToServer(localPlayer, false);
        ItemStack stack = localPlayer.containerMenu.getCarried();
        localPlayer.containerMenu.setCarried(ItemStack.EMPTY);
        // 不再设置 allowVanillaOnce：模式已切成原版，screen$Opening 直接放行；
        // 之前留下的标记没人消费，切回泰拉后第一次按 E 会错误地打开原版背包。
        super.onClose();
        Minecraft.getInstance().setScreen(new InventoryScreen(localPlayer));
        localPlayer.containerMenu.setCarried(stack);
        PacketDistributor.sendToServer(new CPacketOpenVanilla(stack));
    }

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
        ExtraInventory extraInventory = menu.extraInventory;
        int containerSize = extraInventory.getContainerSize();
        int sizeAccessoryDye = extraInventory.getSizeAccessoryDye();
        int size = containerSize - sizeAccessoryDye;
        int selectedHotbar = player.getInventory().selected;

        RenderSystem.enableBlend();
        // 每个槽位一个泰拉风格的格子
        for (int i = 0; i < menu.slots.size(); i++) {
            Slot slot = menu.getSlot(i);
            if (!slot.isActive()) continue;
            int frameU = TerraInventoryActions.isFavorite(slot.getItem()) ? 72 : slotFrameU(i, size);
            guiGraphics.blit(SLOTS, leftPos + slot.x - 1, topPos + slot.y - 1, frameU, 0, 18, 18, SLOTS_WIDTH, SLOTS_HEIGHT);
        }
        // 当前选中的快捷栏格子与快捷栏编号
        Slot selected = menu.getSlot(menu.invStart + 27 + selectedHotbar);
        guiGraphics.blit(SLOTS, leftPos + selected.x - 1, topPos + selected.y - 1, 0, 18, 18, 18, SLOTS_WIDTH, SLOTS_HEIGHT);

        // 空槽位图标（透明底，图集第 36 行：时装 0-3、宠物等 4-7、坐骑 8、弹药 9、钱币 10、垃圾桶 11）
        for (int i = VANITY_ARMOR_START; i < size; i++) {
            Slot slot = menu.getSlot(i);
            if (!slot.isActive() || slot.hasItem()) continue;
            int icon;
            if (i < VANITY_ARMOR_DYE_START) {
                icon = i - VANITY_ARMOR_START;
            } else if (i < COINS_START) {
                continue; // 染料槽没有图标
            } else if (i < AMMO_START) {
                icon = 10;
            } else if (i < EQUIPMENT_START) {
                icon = 9;
            } else if (i < EQUIPMENT_DYE_START) {
                int j = i - EQUIPMENT_START;
                icon = j == MOUNT_INDEX ? 8 : 4 + j;
            } else if (i == TRASH_START) {
                icon = 11;
            } else {
                continue;
            }
            guiGraphics.blit(SLOTS, leftPos + slot.x, topPos + slot.y, icon * 16, 36, 16, 16, SLOTS_WIDTH, SLOTS_HEIGHT);
        }
        for (int i = 0; i < sizeAccessoryDye; i++) {
            Slot slot = menu.getSlot(containerSize + i);
            if (slot.isActive() && !slot.hasItem()) {
                guiGraphics.blit(ACCESSORY, leftPos + slot.x, topPos + slot.y, 0, 0, 16, 16, 16, 16);
            }
        }

        int x = infoIconsX();
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

    /// 0 普通背包格，18 特殊格（钱币/弹药/垃圾桶/合成），36 时装格，54 染料格
    private int slotFrameU(int index, int sizeExceptAccessoryDye) {
        if (index >= menu.cosmeticStart) return 36; // 时装饰品
        if (index < sizeExceptAccessoryDye) {
            if (index < VANITY_ARMOR_DYE_START) return 36;
            if (index < COINS_START) return 54;
            if (index < EQUIPMENT_START) return 18;
            if (index < EQUIPMENT_DYE_START) return 0;
            if (index < TRASH_START) return 54;
            return 18; // 垃圾桶
        }
        if (index < menu.extraInventory.getContainerSize()) return 54; // 饰品染料
        return 0;
    }

    /// 收藏的物品在左上角画一颗星
    @Override
    protected void renderSlot(GuiGraphics guiGraphics, Slot slot) {
        super.renderSlot(guiGraphics, slot);
        renderSlotOverlay(guiGraphics, slot, slot.container == player.getInventory() && slot.getContainerSlot() < 9);
    }

    /// 物品之上：快捷栏编号（左上）与收藏星（右上）
    public static void renderSlotOverlay(GuiGraphics guiGraphics, Slot slot, boolean hotbar) {
        if (hotbar) {
            TerraHotbarHud.renderSlotNumber(guiGraphics, Minecraft.getInstance().font, slot.getContainerSlot(), slot.x, slot.y);
        }
        if (TerraInventoryActions.isFavorite(slot.getItem())) {
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 300);
            guiGraphics.blit(SLOTS, slot.x + 9, slot.y - 1, 36, 18, 8, 8, SLOTS_WIDTH, SLOTS_HEIGHT);
            guiGraphics.pose().popPose();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 创造模式的“合成 | 物品”页签与物品页
        if (creativePanel != null && creativePanel.clickTabs(mouseX, mouseY)) {
            panelPressed = true;
            return true;
        }
        if (creativeItems() && creativePanel.mouseClicked(mouseX, mouseY, button)) {
            panelPressed = true;
            return true;
        }
        // 装备栏位：切换整套装备（手里拿着东西时不换）
        int loadout = loadoutAt(mouseX, mouseY);
        if (loadout >= 0) {
            if (button == 0 && menu.getCarried().isEmpty() && loadout != org.confluence.mod.common.menu.TerraLoadouts.clientIndex) {
                org.confluence.mod.network.c2s.SwitchLoadoutPacketC2S.sendToServer(loadout);
                getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1.0F));
            }
            panelPressed = true;
            return true;
        }
        // 饰品的眼睛：显示/隐藏外观
        int eye = eyeAt(mouseX, mouseY);
        if (eye >= 0 && button == 0) {
            PacketDistributor.sendToServer(new top.theillusivec4.curios.common.network.client.CPacketToggleRender(TerraCurio.CURIO_SLOT, eye));
            getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.3F));
            panelPressed = true;
            return true;
        }
        // Alt+点击：收藏 / 取消收藏（泰拉）
        if (button == 0 && hasAltDown() && hoveredSlot != null && hoveredSlot.hasItem() && hoveredSlot.container == player.getInventory() && menu.getCarried().isEmpty()) {
            FavoriteSlotPacketC2S.sendToServer(hoveredSlot.index);
            getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.4F));
            return true;
        }
        // Ctrl+点击：进垃圾桶（泰拉）；右键盔甲/饰品/装备：直接装备（泰拉）
        if (hoveredSlot != null && hoveredSlot.hasItem() && menu.getCarried().isEmpty() && menu.isPlayerInventorySlot(hoveredSlot.index)) {
            if (button == 0 && hasControlDown() && !hasShiftDown() && !TerraInventoryActions.isFavorite(hoveredSlot.getItem())) {
                org.confluence.mod.network.c2s.TerraSlotActionPacketC2S.sendToServer(org.confluence.mod.common.menu.TerraInventoryActions.SLOT_TRASH, hoveredSlot.index);
                getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 0.7F));
                return true;
            }
            if (button == 1 && !hasShiftDown() && menu.equipTarget(player, hoveredSlot.getItem()) >= 0) {
                org.confluence.mod.network.c2s.TerraSlotActionPacketC2S.sendToServer(org.confluence.mod.common.menu.TerraInventoryActions.SLOT_EQUIP, hoveredSlot.index);
                getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1.0F));
                return true;
            }
        }
        // 右键增益图标：取消增益（减益不行，服务端也会校验）
        if (button == 1) {
            List<net.minecraft.world.effect.MobEffectInstance> effects = org.confluence.mod.client.gui.hud.TerraBuffHud.visibleEffects(player);
            int buff = org.confluence.mod.client.gui.hud.TerraBuffHud.indexAt(effects.size(), buffsLeft(), buffsTop(), mouseX, mouseY);
            if (buff >= 0) {
                var holder = effects.get(buff).getEffect();
                if (holder.value().getCategory() != net.minecraft.world.effect.MobEffectCategory.HARMFUL) {
                    holder.unwrapKey().ifPresent(key -> org.confluence.mod.network.c2s.CancelBuffPacketC2S.sendToServer(key.location()));
                    getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 0.8F));
                }
                return true;
            }
        }
        // 合成列表：点击做 1 个放到鼠标上（同种物品继续叠加，泰拉做法），Shift+点击尽量多做进背包
        int craftIndex = craftingIndexAt(mouseX, mouseY);
        if (craftIndex >= 0 && button == 0 && (hasShiftDown() ? menu.getCarried().isEmpty()
                : org.confluence.mod.common.menu.StationCrafting.fitsOnCursor(player, craftable.get(craftIndex).value().getResultItem(player.level().registryAccess())))) {
            CraftRecipePacketC2S.sendToServer(craftable.get(craftIndex).id(), hasShiftDown());
            getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            this.craftRefreshTicks = 2; // 服务端扣完材料后尽快刷新
            return true;
        }
        int x = infoIconsX();
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
                teamCooldown = 100;
                return true;
            }
            for (int i = 0; i < TEAM_SPRITES.length; i++) {
                if (TEAM_SPRITES[i][TEAM_ICON_INDEX].isHovered(mouseX, mouseY)) {
                    PlayerSpecialData data = PlayerSpecialData.of(player);
                    Team team = Team.TEAMS[i];
                    if (data.getTeam() != team) {
                        data.setTeam(team);
                        TeamPacket.sendToServer(player);
                        teamCooldown = 100;
                        return true;
                    }
                    return false;
                }
            }
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
                // 与泰拉背包里的同一个按钮成对：从原版背包按下即切回泰拉界面（并保存）
                if (!ClientConfigs.terrariaInterface) {
                    ClientConfigs.setTerrariaInterface(true);
                    org.confluence.mod.network.c2s.TerraUiModePacketC2S.sendToServer(player, true);
                }
                ItemStack stack = player.containerMenu.getCarried();
                player.containerMenu.setCarried(ItemStack.EMPTY);
                OpenMenuPacketC2S.sendToServer(OpenMenuPacketC2S.EXTRA_INVENTORY, stack);
            }
        });
        extraInventoryButton.setTooltip(Tooltip.create(Component.translatable("gui.confluence.terraria_interface")));
        if (isInventoryScreen) {
            IInventoryScreen.of((InventoryScreen) screen).confluence$setExtraButton(extraInventoryButton);
        }
        return extraInventoryButton;
    }
}
