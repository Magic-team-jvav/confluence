package org.confluence.mod.common.entity.monster;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.confluence.lib.common.LibEffects;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.entity.ModEntities;

public final class SpittingPlant extends Snatcher {
    private final Profile species;
    private int spitCooldown;

    public SpittingPlant(EntityType<? extends Snatcher> type, Level level, Profile species) {
        super(type, level, species);
        this.species = species;
        spitCooldown = species == Profile.CLINGER ? 40 : 50;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide || !isAlive() || isNoAi() || hasEffect(LibEffects.CONFUSED.get()))
            return;
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive() || !canAttack(target) || --spitCooldown > 0) return;
        if (level().clip(new ClipContext(getEyePosition(), target.getEyePosition(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS) {
            spitCooldown = 7;
            return;
        }
        boolean clinger = species == Profile.CLINGER;
        spitCooldown = clinger ? 40 : 50;
        var projectile = (clinger ? ModEntities.CLINGER_FLAME : ModEntities.FUNGI_SPORE).get().create(level());
        if (projectile == null) return;
        float damage = clinger ? LibUtils.isMaster(level(), blockPosition()) ? 102 : LibUtils.isAtLeastExpert(level(), blockPosition()) ? 68 : 44
                : LibUtils.isMaster(level(), blockPosition()) ? 240 : LibUtils.isAtLeastExpert(level(), blockPosition()) ? 160 : 80;
        damage *= (float) getAttributeValue(Attributes.ATTACK_DAMAGE) / 70.0F;
        projectile.configure(this, target, damage, clinger ? 0.4F : 0.12F, 0.0F, 200);
        if (level().addFreshEntity(projectile)) playSound(SoundEvents.BLAZE_SHOOT, 0.7F, 1.0F);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean accepted = super.hurt(source, amount);
        if (accepted && !level().isClientSide) spitCooldown = species == Profile.CLINGER ? 40 : 50;
        return accepted;
    }
}
