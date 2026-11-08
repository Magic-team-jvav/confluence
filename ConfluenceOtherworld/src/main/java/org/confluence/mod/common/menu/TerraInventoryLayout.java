package org.confluence.mod.common.menu;

/// 泰拉风格背包的布局（槽位左上角坐标，相对 leftPos/topPos，槽位格子为 18px）。
///
/// 左侧：9×4 背包（第一行为快捷栏）、钱币、弹药、垃圾桶、2×2 合成。
/// 右侧：染料 / 时装 / 盔甲三列，下方为饰品；再右侧为装备栏（宠物、钩爪等）与副手。
public final class TerraInventoryLayout {
    public static final int CELL = 18;
    public static final int GAP = 4;

    public static final int INV_X = 1;
    public static final int INV_Y = 1;
    public static final int COINS_X = INV_X + 9 * CELL + GAP;
    public static final int AMMO_X = COINS_X + CELL;
    public static final int TRASH_X = AMMO_X;
    public static final int TRASH_Y = INV_Y + 4 * CELL + GAP;
    public static final int VANILLA_BUTTON_X = COINS_X;
    public static final int VANILLA_BUTTON_Y = TRASH_Y;
    public static final int SORT_BUTTON_X = COINS_X - CELL;
    public static final int SORT_BUTTON_Y = TRASH_Y;

    /// 泰拉式合成列表（附近制作站的全部可做配方），在背包下方
    public static final int CRAFT_X = INV_X;
    /// 在“整理 / 原版背包 / 垃圾桶”这一行下面，避免列表第 9 列压住按钮
    public static final int CRAFT_LABEL_Y = TRASH_Y + CELL + 2;
    public static final int CRAFT_Y = CRAFT_LABEL_Y + 10;
    public static final int CRAFT_COLUMNS = 9;
    public static final int CRAFT_ROWS = 4;

    public static final int TEAM_X = AMMO_X + CELL + GAP;
    public static final int HOUSE_BUTTON_X = TEAM_X;
    public static final int BESTIARY_BUTTON_X = TEAM_X;
    public static final int BUTTONS_Y = INV_Y + 4 * CELL + GAP;
    public static final int BESTIARY_BUTTON_Y = BUTTONS_Y + CELL;
    public static final int TEAM_Y = 0;
    public static final int RIGHT_X = TEAM_X + 2 * CELL + GAP;
    public static final int DYE_X = RIGHT_X;
    public static final int VANITY_X = DYE_X + CELL;
    public static final int ARMOR_X = VANITY_X + CELL;
    public static final int ARMOR_Y = INV_Y;
    public static final int ACCESSORY_Y = ARMOR_Y + 4 * CELL + GAP;

    public static final int EQUIPMENT_DYE_X = ARMOR_X + CELL + GAP + 2;
    public static final int EQUIPMENT_X = EQUIPMENT_DYE_X + CELL;
    public static final int EQUIPMENT_Y = INV_Y;
    public static final int OFFHAND_X = EQUIPMENT_X;
    public static final int OFFHAND_Y = EQUIPMENT_Y + 5 * CELL + GAP;

    public static final int WIDTH = EQUIPMENT_X + CELL;
    public static final int HEIGHT = ACCESSORY_Y + 8 * CELL;
    /// 屏幕左上角留白（泰拉的背包贴在左上角，不居中）
    public static final int SCREEN_MARGIN = 4;
    /// 上方留一行给选中物品的名字；HUD 快捷栏与背包第一行在同一位置
    public static final int SCREEN_TOP = 14;

    public static int inventoryX(int column) {
        return INV_X + column * CELL;
    }

    /// row 0 为快捷栏，1..3 为主背包
    public static int inventoryY(int row) {
        return INV_Y + row * CELL;
    }

    private TerraInventoryLayout() {}
}
