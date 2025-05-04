package com.xiaohunao.xhn_lib.example.quality;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

/**
 * 物品品质类 - 定义物品的品质等级和相关属性
 */
public class ItemQuality {
    public static final Codec<ItemQuality> QUALITY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(ItemQuality::getId),
            Codec.STRING.fieldOf("name").forGetter(ItemQuality::getName),
            Codec.INT.fieldOf("level").forGetter(ItemQuality::getLevel),
            Codec.STRING.fieldOf("color").forGetter(quality -> quality.getColor().getName()),
            Codec.FLOAT.fieldOf("stat_bonus").forGetter(ItemQuality::getStatBonus)
    ).apply(instance, (id, name, level, colorName, statBonus) -> {
        ChatFormatting color = ChatFormatting.getByName(colorName);
        if (color == null) {
            color = ChatFormatting.WHITE;
        }
        return new ItemQuality(id, name, level, color, statBonus);
    }));


    // 品质ID
    private final String id;
    
    // 品质名称
    private final String name;
    
    // 品质等级 (数值越高品质越好)
    private final int level;
    
    // 品质颜色
    private final ChatFormatting color;
    
    // 属性加成百分比 (例如: 0.1 表示增加10%的属性)
    private final float statBonus;
    
    /**
     * 创建一个新的物品品质
     * 
     * @param id 品质ID
     * @param name 品质名称
     * @param level 品质等级
     * @param color 品质颜色
     * @param statBonus 属性加成百分比
     */
    public ItemQuality(String id, String name, int level, ChatFormatting color, float statBonus) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.color = color;
        this.statBonus = statBonus;
    }
    
    /**
     * 获取品质ID
     * 
     * @return 品质ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * 获取品质名称
     * 
     * @return 品质名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 获取品质等级
     * 
     * @return 品质等级
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * 获取品质颜色
     * 
     * @return 品质颜色
     */
    public ChatFormatting getColor() {
        return color;
    }
    
    /**
     * 获取属性加成百分比
     * 
     * @return 属性加成百分比
     */
    public float getStatBonus() {
        return statBonus;
    }
    
    /**
     * 获取带有颜色的品质文本组件
     * 
     * @return 带颜色的品质文本组件
     */
    public Component getDisplayName() {
        return Component.literal(name).setStyle(Style.EMPTY.withColor(color));
    }
    
    /**
     * 应用品质加成到基础值
     * 
     * @param baseValue 基础值
     * @return 应用品质加成后的值
     */
    public float applyBonus(float baseValue) {
        return baseValue * (1 + statBonus);
    }
    
    /**
     * 应用品质加成到基础值
     * 
     * @param baseValue 基础值
     * @return 应用品质加成后的值
     */
    public int applyBonus(int baseValue) {
        return Math.round(baseValue * (1 + statBonus));
    }
    
    @Override
    public String toString() {
        return name + " (Level " + level + ", Bonus: +" + (statBonus * 100) + "%)";
    }
}
