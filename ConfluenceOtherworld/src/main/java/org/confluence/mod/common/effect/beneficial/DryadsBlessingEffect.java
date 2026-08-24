package org.confluence.mod.common.effect.beneficial;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.init.ModEffects;
import org.mesdag.portlib.wrapper.world.effect.PortMobEffect;

import static org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier.Operation.ADD_VALUE;

/// 树妖结界给予玩家和城镇 NPC 的短时守护效果。
public final class DryadsBlessingEffect extends PortMobEffect {
    public DryadsBlessingEffect(ResourceLocation id) {
        super(MobEffectCategory.BENEFICIAL, 0x70B94D);
        addAttributeModifier(Attributes.ARMOR, id, 8, ADD_VALUE);
    }

    /// 结界持续刷新效果，因此每 tick 都按自然恢复方式补充生命。
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    /// 玩家每秒恢复 3 点；普通城镇 NPC 每秒额外恢复 3.33 点，老人不恢复。
    @Override
    public void applyEffectTick(LivingEntity living, int amplifier) {
        if (living.level().isClientSide || living.getHealth() >= living.getMaxHealth()) return;
        if (living instanceof BaseNPC npc && npc.getCombatProfile().healthRegeneration(npc) <= 0)
            return;
        float regeneration = living instanceof BaseNPC ? 10.0F / 3.0F : 3.0F;
        living.setHealth(Math.min(living.getMaxHealth(), living.getHealth() + regeneration / 20.0F));
    }

    /// 按玩家 50%、城镇 NPC 33.33% 的比例反伤，并使用各自的结界击退强度。
    public static void reflectDamage(LivingEntity self, DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (attacker == null || attacker == self || !self.hasEffect(ModEffects.DRYADS_BLESSING.get())
                || self.hasEffect(ModEffects.THORNS.get()) || source.is(DamageTypes.THORNS)) return;
        float ratio = self instanceof BaseNPC ? 1.0F / 3.0F : 0.5F;
        if (attacker.hurt(attacker.damageSources().thorns(self), Math.min(1000, amount * ratio))
                && attacker instanceof LivingEntity living) {
            double strength = self instanceof BaseNPC ? 0.6 : 1.0;
            living.knockback(strength, self.getX() - attacker.getX(), self.getZ() - attacker.getZ());
        }
    }
}
