package org.confluence.mod.client.util;

/**
 * 灵魂技能快速切换HUD工具类
 * 提供与轮盘式技能选择器相关的通用计算方法
 */
public final class SoulQuickSkillHudUtils {
    
    // 私有构造函数防止实例化
    private SoulQuickSkillHudUtils() {}
    
    /**
     * 默认显示的技能框数量
     */
    public static final int DEFAULT_DISPLAY_COUNT = 8;
    
    /**
     * 每个技能框的角度间隔（度）
     */
    public static final float DEFAULT_INTERVAL = 360f / DEFAULT_DISPLAY_COUNT;
    
    /**
     * 每个技能框的角度间隔（弧度）
     */
    public static final float DEFAULT_INTERVAL_RADIANS = (float) Math.toRadians(DEFAULT_INTERVAL);
    
    /**
     * 显示数量的一半
     */
    public static final int DEFAULT_HALF_DISPLAY = DEFAULT_DISPLAY_COUNT / 2;
    
    /**
     * 获取技能索引
     * 
     * @param slotIdx        槽位索引（0-based）
     * @param rotationOffset 旋转偏移量
     * @param currentIdx     当前技能索引
     * @param totalSkills    技能总数
     * @param displayCount   显示的技能框数量
     * @param halfDisplay    显示数量的一半
     * @return 计算后的技能索引
     */
    public static int getSkillIndex(int slotIdx, int rotationOffset, int currentIdx, 
                                   int totalSkills, int displayCount, int halfDisplay) {
        if (totalSkills <= 0) {
            return -1;
        }
        
        int skillIdx;
        // 计算当前槽位相对于旋转中心的逻辑偏移
        int slotOffset = slotIdx - rotationOffset;
        
        if (slotIdx == rotationOffset) {
            // 当前选中槽位，直接使用当前技能索引
            skillIdx = currentIdx;
        } else {
            // 处理循环偏移，将范围限制在 [-displayCount, displayCount]
            if (slotOffset > halfDisplay) {
                slotOffset -= displayCount;
            } else if (slotOffset < -halfDisplay) {
                slotOffset += displayCount;
            }
            // 根据逻辑偏移计算实际技能索引
            skillIdx = calculateSkillIndex(slotOffset, currentIdx, totalSkills);
        }
        
        // 确保索引在有效范围内
        return Math.clamp(skillIdx, 0, totalSkills - 1);
    }
    
    /**
     * 根据偏移量计算实际技能索引（处理循环边界）
     *
     * @param offset       索引偏移量（可正可负）
     * @param currentIndex 当前技能索引
     * @param skillTotalNumber 技能总数
     * @return 计算后的实际技能索引
     */
    public static int calculateSkillIndex(int offset, int currentIndex, int skillTotalNumber) {
        if (skillTotalNumber <= 0) {
            return 0;
        }
        return (currentIndex + offset + skillTotalNumber) % skillTotalNumber;
    }
    
    /**
     * 计算旋转偏移量
     * 
     * @param targetAngle 目标角度
     * @param interval 角度间隔
     * @param displayCount 显示数量
     * @return 旋转偏移量
     */
    public static int calculateRotationOffset(float targetAngle, float interval, int displayCount) {
        int rotationOffset = (int) (-targetAngle / interval) % displayCount;
        if (rotationOffset < 0) {
            rotationOffset += displayCount;
        }
        return rotationOffset;
    }
    
    /**
     * 计算默认旋转偏移量（使用默认参数）
     * 
     * @param targetAngle 目标角度
     * @return 旋转偏移量
     */
    public static int calculateDefaultRotationOffset(float targetAngle) {
        return calculateRotationOffset(targetAngle, DEFAULT_INTERVAL, DEFAULT_DISPLAY_COUNT);
    }
    
    /**
     * 获取默认的技能索引（使用默认参数）
     * 
     * @param slotIdx    槽位索引
     * @param currentIdx 当前技能索引
     * @param totalSkills 技能总数
     * @return 技能索引
     */
    public static int getDefaultSkillIndex(int slotIdx, int currentIdx, int totalSkills) {
        int rotationOffset = calculateDefaultRotationOffset(0); // 这里可以根据实际需要调整
        return getSkillIndex(slotIdx, rotationOffset, currentIdx, totalSkills, 
                           DEFAULT_DISPLAY_COUNT, DEFAULT_HALF_DISPLAY);
    }
}
