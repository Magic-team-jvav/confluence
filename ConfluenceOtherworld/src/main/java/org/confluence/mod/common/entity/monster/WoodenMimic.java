package org.confluence.mod.common.entity.monster;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/// 普通宝箱怪实体。
///
/// 木、金、冰和暗影注册项共享此类型；具体数值由注册表属性配置决定，开合与跳跃
/// 行为统一由 {@link BaseMimic} 提供，避免每个外观变体复制一份状态机。
public class WoodenMimic extends BaseMimic {
    public WoodenMimic(EntityType<? extends WoodenMimic> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseMonster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0).add(Attributes.ATTACK_DAMAGE, 6.0);
    }

    /// 普通宝箱怪不使用困难模式宝箱怪的特殊攻击轮次。
    @Override
    protected boolean isHardmodeVariant() {
        return false;
    }
}
