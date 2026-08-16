package org.confluence.mod.common.entity.fishing;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.entity.ModEntities;

/// 血腥鱼竿的专用浮标实体。
/// <p>
/// 泰拉瑞亚中鱼饵桶会提高血月钓起敌怪的概率；当前 1.20 与 1.21 侧都还没有独立的“钓起敌怪”基础流程，
/// 因此这里暂时只保留专用实体类型。等血月钓怪池接入后，应当在统一的鱼获结果处理处根据该实体类型做概率加权，
/// 不要把敌怪生成逻辑散落到鱼竿物品或渲染实体里。
public class BloodyFishingHook extends AbstractFishingHook {
    public BloodyFishingHook(EntityType<BloodyFishingHook> entityType, Level level) {
        super(entityType, level);
    }

    public BloodyFishingHook(Player player, Level level, int luck, int lureSpeed) {
        super(ModEntities.BLOODY_FISHING_HOOK.get(), level, luck, lureSpeed);
        setup(player);
    }
}
