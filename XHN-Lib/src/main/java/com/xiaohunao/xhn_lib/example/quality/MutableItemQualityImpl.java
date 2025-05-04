package com.xiaohunao.xhn_lib.example.quality;

import net.minecraft.ChatFormatting;

/**
 * 可变品质实现类
 * 提供了一个可以动态更新的品质对象实现
 */
public class MutableItemQualityImpl extends ItemQuality implements MutableItemQuality {
    
    // 这些字段用于存储可变的数据
    private String name;
    private int level;
    private ChatFormatting color;
    private float statBonus;
    
    /**
     * 创建一个新的可变物品品质
     * 
     * @param id 品质ID（不可变）
     * @param name 品质名称
     * @param level 品质等级
     * @param color 品质颜色
     * @param statBonus 属性加成百分比
     */
    public MutableItemQualityImpl(String id, String name, int level, ChatFormatting color, float statBonus) {
        super(id, name, level, color, statBonus);
        // 初始化可变字段
        this.name = name;
        this.level = level;
        this.color = color;
        this.statBonus = statBonus;
    }
    
    /**
     * 从另一个品质对象更新当前品质的数据
     * 
     * @param source 源品质对象，包含新的数据
     */
    @Override
    public void updateFrom(ItemQuality source) {
        if (source == null) {
            return;
        }
        
        // 更新可变字段
        this.name = source.getName();
        this.level = source.getLevel();
        this.color = source.getColor();
        this.statBonus = source.getStatBonus();
    }
    
    // 覆盖父类的getter方法，使其返回可变字段的值
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public int getLevel() {
        return this.level;
    }
    
    @Override
    public ChatFormatting getColor() {
        return this.color;
    }
    
    @Override
    public float getStatBonus() {
        return this.statBonus;
    }
}
