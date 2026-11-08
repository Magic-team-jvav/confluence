package org.confluence.mod.client.gui.container;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.item.GroupItem;
import net.neoforged.neoforge.common.CreativeModeTabRegistry;
import org.confluence.mod.common.menu.TerraInventoryActions;
import org.confluence.mod.common.menu.TerraInventoryLayout;
import org.confluence.mod.network.c2s.CreativeItemPacketC2S;
import org.confluence.terra_curio.common.init.TCItems;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.IntPredicate;

/// 创造模式下泰拉背包里的“物品”页（类似泰拉旅途模式的复制菜单），占用合成列表的位置：
/// 页签“合成 | 物品” → 分类转盘（可滚动，可拖动下面的细滚动条；右键加入/移出收藏，收藏的排在最前）
/// → 搜索框 + 搜索方式（名称 / 分类）+ 来源（全部 / 原版以外 / 某一个模组）→ 物品网格（滚轮或拖动滚动条）。
/// Confluence 标签里的“物品组”（图标带 +，原版物品栏里点开才展开）在这里直接展开成里面的物品；物品组本身不能发给服务端。
/// 分类 = 泰拉式的物品种类 + 所有模组的创造物品栏标签（包括 Confluence 的“开发者”标签）。
/// 左键拿一组，右键拿一个（手里是同种物品时再加），Shift+左键整组进背包；拿着别的东西点网格就删掉手里的东西。
final class CreativeItemPanel {
    private static final int VISIBLE_CATEGORIES = 9;
    private static final int BUTTON = 13;
    private static final int BAR = 3;
    private static final Path SETTINGS = FMLPaths.CONFIGDIR.get().resolve("confluence-creative-items.properties");

    // ---------- 跨次打开保留的状态 ----------
    static boolean showItems = true;
    private static String selectedKey = "terra:all";
    private static String query = "";
    private static int scroll = 0;
    private static int categoryScroll = 0;
    /// 0 名称，1 分类
    private static int searchMode = 0;
    /// 来源："all" 全部，"not_vanilla" 原版以外，"mod:<命名空间>" 只有该模组
    private static String source = "all";
    private static List<String> sources = List.of("all", "not_vanilla");
    private static final Map<String, ItemStack> SOURCE_ICONS = new HashMap<>();
    private static final LinkedHashSet<String> FAVORITES = new LinkedHashSet<>();
    private static boolean settingsLoaded;

    // ---------- 物品与分类（创造物品栏重建时一起重建） ----------
    private static List<ItemStack> allItems = List.of();
    private static final IdentityHashMap<ItemStack, String> NAMES = new IdentityHashMap<>();
    private static List<Category> categories = List.of();
    /// 收藏排在前面后的显示顺序
    private static List<Category> ordered = List.of();

    private record Category(String key, Component name, ItemStack icon, List<ItemStack> items) {}

    private static final String[] TERRA_KEYS = {"all", "weapons", "tools", "armor", "accessories", "ammo", "consumables", "blocks", "misc"};

    private final ExtraInventoryScreen screen;
    private final LocalPlayer player;
    private final Font font;
    private EditBox searchBox;
    private List<ItemStack> visible = List.of();
    private int rows = 4;
    /// 0 不拖，1 拖物品滚动条，2 拖分类滚动条
    private int dragging;

    CreativeItemPanel(ExtraInventoryScreen screen, LocalPlayer player, Font font) {
        this.screen = screen;
        this.player = player;
        this.font = font;
    }

    /// 页签只在创造模式出现；生存模式（或中途切了模式）一律是合成页
    boolean active() {
        return showItems && player.isCreative();
    }

    // ---------- 坐标 ----------
    private int left() {
        return screen.getGuiLeft() + TerraInventoryLayout.CRAFT_X;
    }

    private int tabsY() {
        return screen.getGuiTop() + TerraInventoryLayout.CRAFT_LABEL_Y;
    }

    private int categoriesY() {
        return screen.getGuiTop() + TerraInventoryLayout.CRAFT_Y;
    }

    private int categoryBarY() {
        return categoriesY() + TerraInventoryLayout.CELL;
    }

    private int searchY() {
        return categoryBarY() + BAR + 3;
    }

    private int gridY() {
        return searchY() + BUTTON + 3;
    }

    /// 网格下边（增益图标画在下面）
    int bottom() {
        return gridY() + rows * TerraInventoryLayout.CELL;
    }

    private int gridWidth() {
        return TerraInventoryLayout.CRAFT_COLUMNS * TerraInventoryLayout.CELL;
    }

    private int modeButtonX() {
        return left() + gridWidth() - 2 * BUTTON - 1;
    }

    private int sourceButtonX() {
        return left() + gridWidth() - BUTTON;
    }

    private int gridBarX() {
        return left() + gridWidth() + 1;
    }

    private Component craftingTab() {
        return Component.translatable("gui.confluence.crafting");
    }

    private Component itemsTab() {
        return Component.translatable("gui.confluence.creative.tab_items");
    }

    private int itemsTabX() {
        return left() + font.width(craftingTab()) + 10;
    }

    // ---------- 初始化 ----------
    void init(int screenHeight, boolean hasEffects) {
        loadSettings();
        rebuild();
        // 网格尽量往下铺满，留出增益一行
        int available = screenHeight - gridY() - 6 - (hasEffects ? 30 : 0);
        this.rows = Mth.clamp(available / TerraInventoryLayout.CELL, 3, 24);
        this.searchBox = new EditBox(font, left() + 1, searchY() + 1, modeButtonX() - left() - 3, BUTTON - 2, Component.translatable("gui.confluence.creative.search"));
        searchBox.setMaxLength(50);
        searchBox.setBordered(true);
        updateHint();
        searchBox.setValue(query);
        searchBox.setResponder(text -> {
            query = text;
            scroll = 0;
            refilter();
        });
        searchBox.visible = active();
        refilter();
    }

    EditBox searchBox() {
        return searchBox;
    }

    private void updateHint() {
        searchBox.setHint(Component.translatable(searchMode == 1 ? "gui.confluence.creative.search.category" : "gui.confluence.creative.search").withStyle(ChatFormatting.GRAY));
    }

    private void rebuild() {
        Minecraft minecraft = Minecraft.getInstance();
        boolean op = player.canUseGameMasterBlocks() && minecraft.options.operatorItemsTab().get();
        boolean rebuilt = CreativeModeTabs.tryRebuildTabContents(player.connection.enabledFeatures(), op, player.level().registryAccess());
        if (!rebuilt && !allItems.isEmpty()) return;
        NAMES.clear();
        allItems = new ArrayList<>(CreativeModeTabs.searchTab().getDisplayItems());
        List<Category> list = new ArrayList<>();
        // 泰拉式物品种类（TerraInventoryActions.category：0 武器 1 工具 2 盔甲 3 饰品 4 弹药 5 消耗品 6 方块 7/8 杂项）
        int[] kinds = new int[allItems.size()];
        for (int i = 0; i < kinds.length; i++) kinds[i] = TerraInventoryActions.category(allItems.get(i));
        ItemStack[] icons = {new ItemStack(Items.CHEST), new ItemStack(Items.IRON_SWORD), new ItemStack(Items.IRON_PICKAXE),
                new ItemStack(Items.IRON_CHESTPLATE), TCItems.BAND_OF_REGENERATION.get().getDefaultInstance(), new ItemStack(Items.ARROW),
                new ItemStack(Items.GOLDEN_APPLE), new ItemStack(Items.GRASS_BLOCK), new ItemStack(Items.IRON_INGOT)};
        for (int f = 0; f < TERRA_KEYS.length; f++) {
            final int filter = f;
            IntPredicate predicate = switch (filter) {
                case 0 -> kind -> true;
                case 8 -> kind -> kind >= 7;
                default -> kind -> kind == filter - 1;
            };
            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < kinds.length; i++) if (predicate.test(kinds[i])) items.add(allItems.get(i));
            list.add(new Category("terra:" + TERRA_KEYS[f], Component.translatable("gui.confluence.creative.filter." + TERRA_KEYS[f]), icons[f], items));
        }
        // 所有模组的创造物品栏标签
        for (CreativeModeTab tab : CreativeModeTabRegistry.getSortedCreativeModeTabs()) {
            if (tab.getType() != CreativeModeTab.Type.CATEGORY || !tab.shouldDisplay()) continue;
            List<ItemStack> items = flattenGroups(tab.getDisplayItems());
            if (items.isEmpty()) continue;
            ResourceLocation key = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab);
            if (key == null) continue;
            list.add(new Category("tab:" + key, tab.getDisplayName(), tab.getIconItem(), items));
        }
        buildSources();
        categories = list;
        reorder();
    }

    /// 物品组（Confluence 的折叠组，图标带 +）换成组里的物品，去重
    private static List<ItemStack> flattenGroups(Collection<ItemStack> display) {
        Set<ItemStack> set = ItemStackLinkedSet.createTypeAndComponentsSet();
        for (ItemStack stack : display) {
            if (stack.getItem() instanceof GroupItem) {
                GroupItem.Stacks group = stack.get(ConfluenceMagicLib.GROUP_STACKS.get());
                if (group != null) {
                    for (ItemStack value : group.getValues()) {
                        if (!value.isEmpty() && !(value.getItem() instanceof GroupItem)) set.add(value);
                    }
                }
            } else if (!stack.isEmpty()) {
                set.add(stack);
            }
        }
        return new ArrayList<>(set);
    }

    /// 来源列表：全部、原版以外，然后每个有物品的模组（原版、Confluence 系列在前，其余按名称）
    private static void buildSources() {
        LinkedHashMap<String, ItemStack> firstItem = new LinkedHashMap<>();
        for (ItemStack stack : allItems) {
            firstItem.putIfAbsent(BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace(), stack);
        }
        List<String> namespaces = new ArrayList<>(firstItem.keySet());
        namespaces.sort(Comparator.comparingInt(CreativeItemPanel::sourceOrder).thenComparing(CreativeItemPanel::modName));
        List<String> list = new ArrayList<>(List.of("all", "not_vanilla"));
        SOURCE_ICONS.clear();
        SOURCE_ICONS.put("all", new ItemStack(Items.NETHER_STAR));
        SOURCE_ICONS.put("not_vanilla", new ItemStack(Items.BARRIER));
        for (String namespace : namespaces) {
            list.add("mod:" + namespace);
            SOURCE_ICONS.put("mod:" + namespace, firstItem.get(namespace));
        }
        SOURCE_ICONS.put("mod:minecraft", new ItemStack(Items.GRASS_BLOCK));
        sources = list;
        if (!sources.contains(source)) source = "all";
    }

    private static int sourceOrder(String namespace) {
        return switch (namespace) {
            case "minecraft" -> 0;
            case "confluence" -> 1;
            case "terra_curio" -> 2;
            case "terra_furniture" -> 3;
            case "confluence_magic_lib" -> 4;
            default -> 5;
        };
    }

    private static String modName(String namespace) {
        return ModList.get().getModContainerById(namespace).map(container -> container.getModInfo().getDisplayName()).orElse(namespace);
    }

    /// 每个分类在当前来源下有多少物品（提示里显示这个数，而不是分类的总数）
    private static final Map<String, Integer> COUNTS = new HashMap<>();

    /// 收藏的分类按收藏顺序排在最前，其余保持原顺序；当前来源下没有物品的分类不显示
    private static void reorder() {
        COUNTS.clear();
        for (Category category : categories) {
            int count = 0;
            for (ItemStack stack : category.items) if (sourceAllows(stack)) count++;
            COUNTS.put(category.key, count);
        }
        List<Category> list = new ArrayList<>(categories.size());
        for (String key : FAVORITES) {
            for (Category category : categories) if (category.key.equals(key) && COUNTS.get(key) > 0) list.add(category);
        }
        for (Category category : categories) {
            if (!FAVORITES.contains(category.key) && COUNTS.get(category.key) > 0) list.add(category);
        }
        ordered = list;
        categoryScroll = Mth.clamp(categoryScroll, 0, Math.max(0, ordered.size() - VISIBLE_CATEGORIES));
    }

    /// 选中的分类被当前来源隐藏时，暂时显示“全部”（切回来源后恢复原来的选择）
    private static Category selected() {
        for (Category category : ordered) if (category.key.equals(selectedKey)) return category;
        for (Category category : ordered) if (category.key.equals("terra:all")) return category;
        return ordered.isEmpty() ? null : ordered.getFirst();
    }

    private static String name(ItemStack stack) {
        return NAMES.computeIfAbsent(stack, s -> s.getHoverName().getString().toLowerCase(Locale.ROOT));
    }

    private static String id(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    private static boolean sourceAllows(ItemStack stack) {
        if (source.equals("all")) return true;
        String namespace = BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace();
        if (source.equals("not_vanilla")) return !namespace.equals("minecraft");
        return source.equals("mod:" + namespace);
    }

    /// 名称方式：名称或 ID 包含输入（“@模组名”只看该模组）；分类方式（或以 # 开头）：属于名称包含输入的分类的物品，例如“яйца”→刷怪蛋
    private void refilter() {
        Category category = selected();
        List<ItemStack> base = category == null ? allItems : category.items;
        String text = query.trim().toLowerCase(Locale.ROOT);
        boolean byCategory = searchMode == 1;
        if (text.startsWith("#")) {
            byCategory = true;
            text = text.substring(1).trim();
        }
        String mod = null;
        if (text.startsWith("@")) {
            int space = text.indexOf(' ');
            mod = space < 0 ? text.substring(1) : text.substring(1, space);
            text = space < 0 ? "" : text.substring(space + 1).trim();
        }
        Set<ItemStack> inCategories = null;
        if (byCategory && !text.isEmpty()) {
            inCategories = ItemStackLinkedSet.createTypeAndComponentsSet();
            for (Category candidate : categories) {
                if (candidate.key.equals("terra:all")) continue;
                if (candidate.name.getString().toLowerCase(Locale.ROOT).contains(text)) inCategories.addAll(candidate.items);
            }
        }
        List<ItemStack> list = new ArrayList<>();
        if (inCategories != null && (category == null || category.key.equals("terra:all"))) {
            base = new ArrayList<>(inCategories); // 全部物品里按分类找：直接按分类里的顺序
            inCategories = null;
            text = "";
        }
        for (ItemStack stack : base) {
            if (!sourceAllows(stack)) continue;
            if (mod != null && !id(stack).startsWith(mod)) continue;
            if (inCategories != null) {
                if (!inCategories.contains(stack)) continue;
            } else if (!byCategory && !text.isEmpty() && !name(stack).contains(text) && !id(stack).contains(text)) {
                continue;
            }
            list.add(stack);
        }
        this.visible = list;
        scroll = Mth.clamp(scroll, 0, maxScroll());
    }

    private int maxScroll() {
        int totalRows = (visible.size() + TerraInventoryLayout.CRAFT_COLUMNS - 1) / TerraInventoryLayout.CRAFT_COLUMNS;
        return Math.max(0, totalRows - rows);
    }

    private static int maxCategoryScroll() {
        return Math.max(0, ordered.size() - VISIBLE_CATEGORIES);
    }

    // ---------- 设置文件（收藏、搜索方式、来源） ----------
    private static void loadSettings() {
        if (settingsLoaded) return;
        settingsLoaded = true;
        if (!Files.exists(SETTINGS)) return;
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(SETTINGS)) {
            properties.load(reader);
        } catch (IOException ignored) {
            return;
        }
        String favorites = properties.getProperty("favorites", "");
        for (String key : favorites.split(",")) if (!key.isBlank()) FAVORITES.add(key.trim());
        searchMode = Mth.clamp(parse(properties.getProperty("searchMode")), 0, 1);
        source = properties.getProperty("sourceFilter", "all");
    }

    private static int parse(String value) {
        try {
            return value == null ? 0 : Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static void saveSettings() {
        Properties properties = new Properties();
        properties.setProperty("favorites", String.join(",", FAVORITES));
        properties.setProperty("searchMode", String.valueOf(searchMode));
        properties.setProperty("sourceFilter", source);
        try (Writer writer = Files.newBufferedWriter(SETTINGS)) {
            properties.store(writer, "Confluence: creative items panel of the Terraria inventory");
        } catch (IOException ignored) {
        }
    }

    // ---------- 绘制 ----------
    /// 页签（合成页也画，只要是创造模式）
    void renderTabs(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int y = tabsY();
        Component crafting = craftingTab();
        Component items = itemsTab();
        boolean craftingHovered = hoverText(mouseX, mouseY, left(), y, crafting);
        boolean itemsHovered = hoverText(mouseX, mouseY, itemsTabX(), y, items);
        guiGraphics.drawString(font, crafting, left(), y, tabColor(!showItems, craftingHovered), true);
        guiGraphics.drawString(font, "|", itemsTabX() - 7, y, 0x808080, true);
        guiGraphics.drawString(font, items, itemsTabX(), y, tabColor(showItems, itemsHovered), true);
        if (showItems) {
            String count = String.valueOf(visible.size());
            guiGraphics.drawString(font, count, left() + gridWidth() - font.width(count), y, 0xA0A0A0, true);
        }
    }

    private static int tabColor(boolean selected, boolean hovered) {
        if (hovered) return 0xF0C030;
        return selected ? 0xFFFFFF : 0x909090;
    }

    private boolean hoverText(double mouseX, double mouseY, int x, int y, Component text) {
        return mouseX >= x && mouseX < x + font.width(text) && mouseY >= y - 1 && mouseY < y + 9;
    }

    void render(GuiGraphics guiGraphics, int mouseX, int mouseY, ResourceLocation slots, int slotsWidth, int slotsHeight) {
        RenderSystem.enableBlend();
        // 分类转盘
        int hoveredCategory = categoryAt(mouseX, mouseY);
        Category current = selected();
        for (int i = 0; i < VISIBLE_CATEGORIES && categoryScroll + i < ordered.size(); i++) {
            Category category = ordered.get(categoryScroll + i);
            int x = left() + i * TerraInventoryLayout.CELL;
            int y = categoriesY();
            guiGraphics.blit(slots, x - 1, y - 1, 18, 0, 18, 18, slotsWidth, slotsHeight);
            if (category == current) guiGraphics.blit(slots, x - 1, y - 1, 0, 18, 18, 18, slotsWidth, slotsHeight);
            guiGraphics.renderItem(category.icon, x, y);
            if (FAVORITES.contains(category.key)) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(0, 0, 200);
                guiGraphics.drawString(font, "★", x + 10, y - 1, 0xFFD040, true);
                guiGraphics.pose().popPose();
            }
        }
        // 分类转盘下面的细滚动条
        int maxCategoryScroll = maxCategoryScroll();
        if (maxCategoryScroll > 0) {
            int width = gridWidth();
            int barY = categoryBarY();
            guiGraphics.fill(left() - 1, barY, left() - 1 + width, barY + BAR, 0x80202020);
            int thumb = Math.max(10, width * VISIBLE_CATEGORIES / ordered.size());
            int thumbX = left() - 1 + (width - thumb) * categoryScroll / maxCategoryScroll;
            guiGraphics.fill(thumbX, barY, thumbX + thumb, barY + BAR, dragging == 2 || overCategoryBar(mouseX, mouseY) ? 0xFFFFFFFF : 0xFFB0B0B0);
        }
        // 搜索方式与来源按钮
        renderButton(guiGraphics, modeButtonX(), searchY(), searchMode == 1 ? new ItemStack(Items.BOOKSHELF) : new ItemStack(Items.NAME_TAG), over(mouseX, mouseY, modeButtonX(), searchY()));
        ItemStack sourceIcon = SOURCE_ICONS.getOrDefault(source, new ItemStack(Items.NETHER_STAR));
        renderButton(guiGraphics, sourceButtonX(), searchY(), sourceIcon, over(mouseX, mouseY, sourceButtonX(), searchY()));
        // 网格
        int start = scroll * TerraInventoryLayout.CRAFT_COLUMNS;
        int count = rows * TerraInventoryLayout.CRAFT_COLUMNS;
        for (int i = 0; i < count; i++) {
            int x = left() + (i % TerraInventoryLayout.CRAFT_COLUMNS) * TerraInventoryLayout.CELL;
            int y = gridY() + (i / TerraInventoryLayout.CRAFT_COLUMNS) * TerraInventoryLayout.CELL;
            guiGraphics.blit(slots, x - 1, y - 1, 0, 0, 18, 18, slotsWidth, slotsHeight);
            if (start + i < visible.size()) {
                ItemStack stack = visible.get(start + i);
                guiGraphics.renderItem(stack, x, y);
                guiGraphics.renderItemDecorations(font, stack, x, y);
            }
        }
        RenderSystem.disableBlend();
        // 网格右边的滚动条（可拖动）
        int maxScroll = maxScroll();
        if (maxScroll > 0) {
            int barX = gridBarX();
            int top = gridY() - 1;
            int height = rows * TerraInventoryLayout.CELL;
            guiGraphics.fill(barX, top, barX + 4, top + height, 0x80202020);
            int thumb = thumbSize(height, rows, rows + maxScroll);
            int thumbY = top + (height - thumb) * scroll / maxScroll;
            guiGraphics.fill(barX, thumbY, barX + 4, thumbY + thumb, dragging == 1 || overGridBar(mouseX, mouseY) ? 0xFFFFFFFF : 0xFFB0B0B0);
        }
        // 悬停高亮
        int hovered = indexAt(mouseX, mouseY);
        if (hovered >= 0) {
            int i = hovered - start;
            AbstractContainerScreen.renderSlotHighlight(guiGraphics, left() + (i % TerraInventoryLayout.CRAFT_COLUMNS) * TerraInventoryLayout.CELL,
                    gridY() + (i / TerraInventoryLayout.CRAFT_COLUMNS) * TerraInventoryLayout.CELL, 0);
        }
        if (hoveredCategory >= 0) {
            AbstractContainerScreen.renderSlotHighlight(guiGraphics, left() + (hoveredCategory - categoryScroll) * TerraInventoryLayout.CELL, categoriesY(), 0);
        }
    }

    private static int thumbSize(int length, int shown, int total) {
        return Math.max(8, length * shown / Math.max(1, total));
    }

    private void renderButton(GuiGraphics guiGraphics, int x, int y, ItemStack icon, boolean hovered) {
        guiGraphics.fill(x, y, x + BUTTON, y + BUTTON, hovered ? 0xC0606080 : 0xC0303048);
        guiGraphics.renderOutline(x, y, BUTTON, BUTTON, 0xFF101018);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x + 0.5F, y + 0.5F, 0);
        guiGraphics.pose().scale(0.75F, 0.75F, 1.0F);
        guiGraphics.renderItem(icon, 0, 0);
        guiGraphics.pose().popPose();
    }

    private static boolean over(double mouseX, double mouseY, int x, int y) {
        return mouseX >= x && mouseX < x + BUTTON && mouseY >= y && mouseY < y + BUTTON;
    }

    /// 提示放在最后画（盖在一切之上）
    void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (dragging != 0) return;
        int hoveredCategory = categoryAt(mouseX, mouseY);
        if (hoveredCategory >= 0) {
            Category category = ordered.get(hoveredCategory);
            List<Component> lines = new ArrayList<>();
            lines.add(category.name);
            lines.add(Component.translatable("gui.confluence.creative.category_count", COUNTS.getOrDefault(category.key, category.items.size())).withStyle(ChatFormatting.GRAY));
            lines.add(Component.translatable(FAVORITES.contains(category.key) ? "gui.confluence.creative.unfavorite" : "gui.confluence.creative.favorite").withStyle(ChatFormatting.DARK_GRAY));
            guiGraphics.renderComponentTooltip(font, lines, mouseX, mouseY);
            return;
        }
        if (over(mouseX, mouseY, modeButtonX(), searchY())) {
            guiGraphics.renderComponentTooltip(font, List.of(
                    Component.translatable("gui.confluence.creative.mode." + (searchMode == 1 ? "category" : "name")),
                    Component.translatable("gui.confluence.creative.mode.hint").withStyle(ChatFormatting.DARK_GRAY)), mouseX, mouseY);
            return;
        }
        if (over(mouseX, mouseY, sourceButtonX(), searchY())) {
            guiGraphics.renderComponentTooltip(font, List.of(
                    sourceName(),
                    Component.translatable("gui.confluence.creative.source.hint").withStyle(ChatFormatting.DARK_GRAY)), mouseX, mouseY);
            return;
        }
        int hovered = indexAt(mouseX, mouseY);
        if (hovered < 0) return;
        if (!screen.getMenu().getCarried().isEmpty()) {
            ItemStack carried = screen.getMenu().getCarried();
            if (!ItemStack.isSameItemSameComponents(carried, visible.get(hovered))) {
                guiGraphics.renderTooltip(font, Component.translatable("gui.confluence.creative.delete").withStyle(ChatFormatting.RED), mouseX, mouseY);
            }
            return;
        }
        ItemStack stack = visible.get(hovered);
        List<Component> lines = new ArrayList<>(Screen.getTooltipFromItem(Minecraft.getInstance(), stack));
        lines.add(Component.translatable("gui.confluence.creative.hint").withStyle(ChatFormatting.DARK_GRAY));
        guiGraphics.renderTooltip(font, lines, stack.getTooltipImage(), mouseX, mouseY);
    }

    private static Component sourceName() {
        if (source.startsWith("mod:")) return Component.translatable("gui.confluence.creative.source.mod", modName(source.substring(4)));
        return Component.translatable("gui.confluence.creative.source." + source);
    }

    // ---------- 命中测试 ----------
    boolean isOverPanel(double mouseX, double mouseY) {
        return mouseX >= left() - 1 && mouseX < gridBarX() + 5 && mouseY >= categoriesY() - 1 && mouseY < bottom();
    }

    private int categoryAt(double mouseX, double mouseY) {
        int x = (int) mouseX - left() + 1;
        int y = (int) mouseY - categoriesY() + 1;
        if (x < 0 || y < 0 || y >= TerraInventoryLayout.CELL) return -1;
        int column = x / TerraInventoryLayout.CELL;
        if (column >= VISIBLE_CATEGORIES) return -1;
        int index = categoryScroll + column;
        return index < ordered.size() ? index : -1;
    }

    private boolean overCategoryBar(double mouseX, double mouseY) {
        return maxCategoryScroll() > 0 && mouseX >= left() - 1 && mouseX < left() - 1 + gridWidth() && mouseY >= categoryBarY() - 1 && mouseY < categoryBarY() + BAR + 1;
    }

    private boolean overGridBar(double mouseX, double mouseY) {
        return maxScroll() > 0 && mouseX >= gridBarX() - 1 && mouseX < gridBarX() + 5 && mouseY >= gridY() - 1 && mouseY < bottom();
    }

    private int indexAt(double mouseX, double mouseY) {
        int x = (int) mouseX - left() + 1;
        int y = (int) mouseY - gridY() + 1;
        if (x < 0 || y < 0) return -1;
        int column = x / TerraInventoryLayout.CELL;
        int row = y / TerraInventoryLayout.CELL;
        if (column >= TerraInventoryLayout.CRAFT_COLUMNS || row >= rows) return -1;
        int index = (row + scroll) * TerraInventoryLayout.CRAFT_COLUMNS + column;
        return index < visible.size() ? index : -1;
    }

    // ---------- 输入 ----------
    /// 页签点击（创造模式下合成页也要能切过来）
    boolean clickTabs(double mouseX, double mouseY) {
        if (!player.isCreative()) return false;
        int y = tabsY();
        boolean toItems = hoverText(mouseX, mouseY, itemsTabX(), y, itemsTab());
        boolean toCrafting = hoverText(mouseX, mouseY, left(), y, craftingTab());
        if (!toItems && !toCrafting) return false;
        if (showItems != toItems) {
            showItems = toItems;
            searchBox.visible = showItems;
            if (!showItems) searchBox.setFocused(false);
            screen.onCreativeTabChanged();
            click(1.2F);
        }
        return true;
    }

    boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!searchBox.isMouseOver(mouseX, mouseY)) {
            searchBox.setFocused(false);
            if (screen.getFocused() == searchBox) screen.setFocused(null);
        } else if (button == 1) { // 右键搜索框：清空（与原版一致）
            searchBox.setValue("");
            return true;
        }
        // 滚动条：按下就跳到该位置并开始拖动
        if (button == 0 && overGridBar(mouseX, mouseY)) {
            dragging = 1;
            mouseDragged(mouseX, mouseY);
            return true;
        }
        if (button == 0 && overCategoryBar(mouseX, mouseY)) {
            dragging = 2;
            mouseDragged(mouseX, mouseY);
            return true;
        }
        int categoryIndex = categoryAt(mouseX, mouseY);
        if (categoryIndex >= 0) {
            Category category = ordered.get(categoryIndex);
            if (button == 1) { // 右键：收藏 / 取消收藏
                if (!FAVORITES.remove(category.key)) FAVORITES.add(category.key);
                reorder();
                saveSettings();
                click(1.5F);
            } else if (button == 0) {
                selectedKey = category.key;
                scroll = 0;
                refilter();
                click(1.0F);
            }
            return true;
        }
        if (over(mouseX, mouseY, modeButtonX(), searchY())) {
            searchMode = 1 - searchMode;
            updateHint();
            saveSettings();
            scroll = 0;
            refilter();
            click(1.1F);
            return true;
        }
        if (over(mouseX, mouseY, sourceButtonX(), searchY())) {
            int index = Math.max(0, sources.indexOf(source));
            source = sources.get(Math.floorMod(index + (button == 1 ? -1 : 1), sources.size()));
            reorder(); // 隐藏这个来源下的空分类，数字也按来源重算
            saveSettings();
            scroll = 0;
            refilter();
            click(1.1F);
            return true;
        }
        int index = indexAt(mouseX, mouseY);
        if (index < 0) return isOverPanel(mouseX, mouseY) && !searchBox.isMouseOver(mouseX, mouseY);
        ItemStack carried = screen.getMenu().getCarried();
        ItemStack stack = visible.get(index);
        if (!carried.isEmpty()) {
            if (ItemStack.isSameItemSameComponents(carried, stack)) {
                // 手里是同种物品：左键补满，右键加一个
                if (carried.getCount() < carried.getMaxStackSize()) {
                    setCursor(carried.copyWithCount(button == 1 ? carried.getCount() + 1 : carried.getMaxStackSize()));
                }
            } else {
                screen.getMenu().setCarried(ItemStack.EMPTY);
                CreativeItemPacketC2S.sendToServer(CreativeItemPacketC2S.DELETE_CURSOR, ItemStack.EMPTY);
                click(0.6F);
            }
            return true;
        }
        if (button == 0 && Screen.hasShiftDown()) {
            CreativeItemPacketC2S.sendToServer(CreativeItemPacketC2S.TO_INVENTORY, stack.copyWithCount(stack.getMaxStackSize()));
            click(1.0F);
            return true;
        }
        setCursor(stack.copyWithCount(button == 1 ? 1 : stack.getMaxStackSize()));
        return true;
    }

    /// 拖动滚动条
    boolean mouseDragged(double mouseX, double mouseY) {
        if (dragging == 1) {
            int maxScroll = maxScroll();
            int height = rows * TerraInventoryLayout.CELL;
            int thumb = thumbSize(height, rows, rows + maxScroll);
            double progress = (mouseY - (gridY() - 1) - thumb / 2.0) / Math.max(1, height - thumb);
            scroll = Mth.clamp((int) Math.round(progress * maxScroll), 0, maxScroll);
            return true;
        }
        if (dragging == 2) {
            int maxCategoryScroll = maxCategoryScroll();
            int width = gridWidth();
            int thumb = Math.max(10, width * VISIBLE_CATEGORIES / Math.max(1, ordered.size()));
            double progress = (mouseX - (left() - 1) - thumb / 2.0) / Math.max(1, width - thumb);
            categoryScroll = Mth.clamp((int) Math.round(progress * maxCategoryScroll), 0, maxCategoryScroll);
            return true;
        }
        return false;
    }

    void mouseReleased() {
        dragging = 0;
    }

    private void setCursor(ItemStack stack) {
        if (stack.getItem() instanceof GroupItem) return; // 物品组只是物品栏里的折叠夹，不能拿
        screen.getMenu().setCarried(stack);
        CreativeItemPacketC2S.sendToServer(CreativeItemPacketC2S.TO_CURSOR, stack);
        click(1.0F);
    }

    boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        if (!isOverPanel(mouseX, mouseY)) return false;
        int step = -(int) Math.signum(scrollY);
        if (categoryAt(mouseX, mouseY) >= 0 || overCategoryBar(mouseX, mouseY)) { // 分类转盘：横向滚动
            int amount = Screen.hasShiftDown() ? VISIBLE_CATEGORIES : 1;
            categoryScroll = Mth.clamp(categoryScroll + step * amount, 0, maxCategoryScroll());
            return true;
        }
        int amount = Screen.hasShiftDown() ? rows : 1;
        scroll = Mth.clamp(scroll + step * amount, 0, maxScroll());
        return true;
    }

    /// 搜索框有焦点时吞掉按键（否则 E、数字键会关背包/换快捷栏）。Ctrl+F 聚焦搜索框。
    boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchBox.isFocused()) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
                searchBox.setFocused(false);
                screen.setFocused(null);
                return true;
            }
            searchBox.keyPressed(keyCode, scanCode, modifiers);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_F && Screen.hasControlDown()) {
            screen.setFocused(searchBox);
            searchBox.setFocused(true);
            return true;
        }
        return false;
    }

    private static void click(float pitch) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, pitch));
    }
}
