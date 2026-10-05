package org.confluence.mod.common.effect.beneficial;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.init.ModEffects;

import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE;

public final class DryadsBlessingEffect extends MobEffect {
    public DryadsBlessingEffect(ResourceLocation id) {
        super(MobEffectCategory.BENEFICIAL, 0x70B94D);
        addAttributeModifier(Attributes.ARMOR, id, 8, ADD_VALUE);
    }

    /// 结界持续刷新效果，因此每 tick 都按自然恢复方式补充生命。
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    /// 玩家每秒恢复 3 点；普通城镇 NPC 每秒额外恢复 3.33 点，老人不恢复。
    @Override
    public boolean applyEffectTick(LivingEntity living, int amplifier) {
        if (living.level().isClientSide || living.getHealth() >= living.getMaxHealth()) return true;
        if (living instanceof BaseNPC npc && npc.getCombatProfile().healthRegeneration(npc) <= 0)
            return true;
        float regeneration = living instanceof BaseNPC ? 10.0F / 3.0F : 3.0F;
        living.setHealth(Math.min(living.getMaxHealth(), living.getHealth() + regeneration / 20.0F));
        return true;
    }

    /// 按玩家 50%、城镇 NPC 33.33% 的比例反伤，并使用各自的结界击退强度。
    public static void reflectDamage(LivingEntity self, DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (attacker == null || attacker == self || !self.hasEffect(ModEffects.DRYADS_BLESSING)
                || self.hasEffect(ModEffects.THORNS) || source.is(DamageTypes.THORNS)) return;
        float ratio = self instanceof BaseNPC ? 1.0F / 3.0F : 0.5F;
        if (attacker.hurt(attacker.damageSources().thorns(self), Math.min(1000, amount * ratio))
                && attacker instanceof LivingEntity living) {
            double strength = self instanceof BaseNPC ? 0.6 : 1.0;
            living.knockback(strength, self.getX() - attacker.getX(), self.getZ() - attacker.getZ());
        }
    }
}
