package org.confluence.mod.common.item.flail;

import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.component.FlailComponent;
import org.jetbrains.annotations.NotNull;

/**
 * <h1>铁链血滴子物品</h1>
 * 自动挥舞的投射型连枷：按住攻击键时按挥舞速度连续射出连在链条上的锋利刀刃，
 * 每枚刀刃飞至最大射程（32 图格）或撞到方块后收回。
 * <p>
 * 时序关系（基础挥舞间隔 S = {@link FlailComponent#autoSwingInterval} = 13 tick）：
 * <ul>
 *   <li>飞出耗时 F = maxDistance / throwSpeed ≈ 25 tick ≈ 2S</li>
 *   <li>收回耗时 R = maxDistance / retractSpeed ≈ 12 tick ≈ S</li>
 *   <li>F + R ≈ 37 &lt; 3S = 39</li>
 * </ul>
 * 因此极限状态下可同时存在 2 枚向前的 THROWN 射弹和 1 枚向后的 RETRACT 射弹；
 * 提高近战速度会缩短 S，同时存在的刀刃数量随之增加。
 *
 * @see BaseFlailItem#tryAutoSwing
 * @see FlailComponent#getAutoSwingInterval
 */
public class ChainGuillotinesItem extends BaseFlailItem {
    public ChainGuillotinesItem(@NotNull FlailComponent component, @NotNull ModRarity rarity) {
        super(component, rarity);
    }

    /** 按住攻击键即持续发射，无需反复点击 */
    @Override
    public boolean isAutoSwing() {
        return true;
    }
}
