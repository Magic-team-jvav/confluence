package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.ModTags;

/**
 * 血肉史莱姆 —— 免疫火焰/熔岩/摔伤，不攻击血肉同盟生物。
 */
public class FleshSlime extends BaseSlime {

    public FleshSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xFF0000, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(14.0f, 6, 50.0f);
    }

    @Override
    protected boolean isFireImmune() {
        return true;
    }

    /// 1.21 侧除关闭摔落结算外，还直接屏蔽所有带摔落标签的伤害来源。
    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypeTags.IS_FIRE)
                || source.is(DamageTypeTags.IS_FALL)
                || super.isInvulnerableTo(source);
    }

    /// 按血肉山当前阶段配置召唤体型。
    public void configureSummonedSize(int size) {
        setSlimeSize(size);
        setHealth(getMaxHealth());
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return !target.getType().is(ModTags.EntityTypes.FLESH_ALLIANCE)
                && super.canAttack(target);
    }
}
