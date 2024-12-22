package org.confluence.mod.client.gui.wiki;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.item.food.BaseFoodItem;
import org.confluence.mod.common.item.sword.BaseSwordItem;
import org.confluence.mod.util.GuiUtils;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ObjectWikiScreen extends BaseWikiScreen{
    private Item item;
    private BaseWikiScreen screen;
    private String id;
    private static Map<String, Component> descriptionMap = new HashMap<>();

    protected ObjectWikiScreen(Item item, BaseWikiScreen screen, String id) {
        super(Component.literal(item.getDescriptionId()));
        this.id = id;
        this.item = item;
        this.screen = screen;
    }

    public static void putDescription(String id, Component description){
        String eid = "item." + id.split(":")[0] + "." + id.split(":")[1];
        descriptionMap.put(eid, description);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);


        GuiUtils.drawItem(guiGraphics, item,minecraft,
                367, 35, 80, 80, 5 ,
                true, mouseX, mouseY);

        if (item instanceof TieredItem ti){
            if (ti.getTier() instanceof ModTiers.PoweredTier pt){
                guiGraphics.drawString(minecraft.font, Component.translatable("wiki.confluence.power")
                        .append(String.valueOf(pt.getPower())) , 10, 60, Color.WHITE.getRGB());
                if (item instanceof BaseSwordItem sword){
                    guiGraphics.drawString(minecraft.font, Component.translatable("wiki.confluence.setDamage").append(new DecimalFormat("0.00").format(getItemDamage(sword))), 10, 10, Color.WHITE.getRGB());
                    guiGraphics.drawString(minecraft.font, Component.translatable("wiki.confluence.speed").append(new DecimalFormat("0.00").format(getItemSpeed(sword))), 10, 30, Color.WHITE.getRGB());
                }
                guiGraphics.drawString(minecraft.font, Component.translatable("wiki.confluence.enchantment").append(String.valueOf(pt.getEnchantmentValue())), 10, 40, Color.WHITE.getRGB());
                guiGraphics.drawString(minecraft.font, Component.translatable("wiki.confluence.use").append(pt.getUses() == 0 ? Component.translatable("item.unbreakable") : Component.literal(String.valueOf(ti.getTier().getUses()))), 10, 20, Color.WHITE.getRGB());
                guiGraphics.drawString(minecraft.font, Component.translatable("wiki.confluence.ingredient").append(Arrays.toString(getRepairIngredient(pt
                                        .getRepairIngredient().getItems()))
                                .replace("[", "").replace("]", "")),
                        10, 50, Color.WHITE.getRGB());
            } else {
                guiGraphics.drawString(minecraft.font, Component.translatable("wiki.confluence.setDamage").append(String.valueOf(ti.getTier().getAttackDamageBonus())), 10, 10, Color.WHITE.getRGB());
                guiGraphics.drawString(minecraft.font, Component.translatable("wiki.confluence.use").append(ti.getTier().getUses() == 0 ? Component.translatable("item.unbreakable") : Component.literal(String.valueOf(ti.getTier().getUses()))), 10, 20, Color.WHITE.getRGB());
                guiGraphics.drawString(minecraft.font, Component.translatable("wiki.confluence.speed").append(String.valueOf(ti.getTier().getSpeed())), 10, 30, Color.WHITE.getRGB());
                guiGraphics.drawString(minecraft.font, Component.translatable("wiki.confluence.enchantment").append(String.valueOf(ti.getTier().getEnchantmentValue())), 10, 40, Color.WHITE.getRGB());
                guiGraphics.drawString(minecraft.font, Component.translatable("wiki.confluence.ingredient").append(Arrays.toString(getRepairIngredient(ti.getTier()
                                        .getRepairIngredient().getItems()))
                                .replace("[", "").replace("]", "")),
                        10, 50, Color.WHITE.getRGB());
            }
        } else if (item instanceof BaseFoodItem fi){
            guiGraphics.drawString(minecraft.font, Component.translatable("wiki.confluence.nutrition")
                    .append(String.valueOf(fi.getFoodProperties(fi.getDefaultInstance(), null).nutrition())),
                    10, 10, Color.WHITE.getRGB());
            guiGraphics.drawString(minecraft.font, Component.translatable("wiki.confluence.saturation")
                            .append(String.valueOf(fi.getFoodProperties(fi.getDefaultInstance(), null).saturation())),
                    10, 20, Color.WHITE.getRGB());
        }

        if (descriptionMap.containsKey(id)){
            drawDescription(guiGraphics, minecraft.font, descriptionMap.get(id), 10, 70);
        }
    }

    private double getItemDamage(BaseSwordItem item) {
        return item.modifier.damage;
    }

    private double getItemSpeed(BaseSwordItem item) {
        return item.modifier.speed;
    }

    private static void drawDescription(GuiGraphics guiGraphics, Font font,
                                        Component description, int x, int y){
        String st = description.getString();
        String[] des = st.split("\n");
        for (int i = 0; i < des.length; i++) {
            guiGraphics.drawString(font, des[i],
                    x, y + i * 10, Color.WHITE.getRGB());
        }
    }

    private String[] getRepairIngredient(ItemStack[] stacks){
        ArrayList<String> items = new ArrayList<>();
        for (ItemStack stack : stacks){
            items.add(stack.getItem().getName(stack).getString());
        }
        if (!items.isEmpty()){
            return items.toArray(new String[0]);
        } else {
            return new String[]{"Null"};
        }
    }

    @Override
    public Screen getParentScreen() {
        return screen;
    }
}
