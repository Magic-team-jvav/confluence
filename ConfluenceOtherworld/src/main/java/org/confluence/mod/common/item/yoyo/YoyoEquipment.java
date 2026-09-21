package org.confluence.mod.common.item.yoyo;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.item.AccessoryItems;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.confluence.terra_curio.util.TCUtils;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

/// 一次发射的饰品配置；组合饰品与其材料只提供一次效果。
public record YoyoEquipment(boolean string, boolean glove, boolean offstring, int counterweight) {
    public static final int RAINBOW_COLOR = 0x1000000;
    public static final int MAGIC_COLOR = 0x1000001;

    public record Appearance(int stringColor, int counterweight) {}

    /// 外观槽只改变颜色，不赋予射程、手套或脱手能力；平衡锤以较低外观槽优先。
    public static Appearance appearance(LivingEntity owner, int defaultColor, int counterweight) {
        ICuriosItemHandler inventory = CuriosApi.getCuriosInventory(owner).resolve().orElse(null);
        if (inventory == null) return new Appearance(defaultColor, counterweight);
        int color = defaultColor;
        boolean magic = false;
        for (ICurioStacksHandler group : inventory.getCurios().values()) {
            for (int slot = 0; slot < group.getStacks().getSlots(); slot++) {
                PrimitiveValueComponent component = TCUtils.getAccessoriesComponent(group.getStacks().getStackInSlot(slot));
                if (component == null) continue;
                boolean visible = group.getRenders().get(slot);
                if (component.contains(AccessoryItems.YOYO$STRING))
                    color = visible && component.contains(AccessoryItems.YOYO$COUNTERWEIGHT) && component.get(AccessoryItems.YOYO$COUNTERWEIGHT).get() == 7 ? 0xFFE2E48E : 0xFFFFFFFF;
                if (component.contains(AccessoryItems.YOYO$STRING_COLOR))
                    color = visible ? component.get(AccessoryItems.YOYO$STRING_COLOR).get() : 0xFFFFFFFF;
                if (visible && component.contains(AccessoryItems.YOYO$OFFSTRING)) magic = true;
            }
        }
        if (magic) color = MAGIC_COLOR;
        int lineSlot = Integer.MAX_VALUE;
        int weightSlot = Integer.MAX_VALUE;
        for (ICurioStacksHandler group : inventory.getCurios().values()) {
            for (int slot = 0; slot < group.getCosmeticStacks().getSlots(); slot++) {
                ItemStack stack = group.getCosmeticStacks().getStackInSlot(slot);
                PrimitiveValueComponent component = TCUtils.getAccessoriesComponent(stack);
                if (component == null) continue;
                if (slot < lineSlot && (component.contains(AccessoryItems.YOYO$STRING) || component.contains(AccessoryItems.YOYO$OFFSTRING))) {
                    color = component.contains(AccessoryItems.YOYO$OFFSTRING) ? MAGIC_COLOR
                            : component.contains(AccessoryItems.YOYO$STRING_COLOR) ? component.get(AccessoryItems.YOYO$STRING_COLOR).get()
                            : component.contains(AccessoryItems.YOYO$COUNTERWEIGHT) && component.get(AccessoryItems.YOYO$COUNTERWEIGHT).get() == 7 ? 0xFFE2E48E : 0xFFFFFFFF;
                    lineSlot = slot;
                }
                if (slot < weightSlot && component.contains(AccessoryItems.YOYO$COUNTERWEIGHT)) {
                    int candidate = component.get(AccessoryItems.YOYO$COUNTERWEIGHT).get();
                    if (candidate >= 1 && candidate <= 6) {
                        counterweight = candidate;
                        weightSlot = slot;
                    }
                }
            }
        }
        return new Appearance(color, counterweight);
    }

    public static int animatedColor(int color, float ticks) {
        if (color == RAINBOW_COLOR || color == MAGIC_COLOR)
            return 0xFF000000 | Mth.hsvToRgb((ticks % 120) / 120, color == MAGIC_COLOR ? 0.5F : 1, 1);
        return 0xFF000000 | color;
    }

    public static YoyoEquipment of(LivingEntity owner) {
        return new YoyoEquipment(TCUtils.hasType(owner, AccessoryItems.YOYO$STRING),
                TCUtils.hasType(owner, AccessoryItems.YOYO$GLOVE),
                TCUtils.hasType(owner, AccessoryItems.YOYO$OFFSTRING),
                TCUtils.getValue(owner, AccessoryItems.YOYO$COUNTERWEIGHT));
    }

    public float range(float base) {
        return string ? base * 1.25F + 2 : base;
    }

    public int lifetime(int base) {
        return base == 0 ? 0 : string ? base * 3 / 2 : base;
    }
}
