package org.confluence.mod.common.entity.projectile;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.ModEffects;

public final class SpiderWebSpit extends StraightMonsterProjectile implements ItemSupplier {
    public SpiderWebSpit(EntityType<? extends SpiderWebSpit> type, Level level) {
        super(type, level);
    }

    @Override
    protected Vec3 modifyVelocity(Vec3 velocity) {
        return velocity.add(0.0, -0.025, 0.0);
    }

    @Override
    protected void onSuccessfulHit(Mob owner, LivingEntity target) {
        target.addEffect(new MobEffectInstance(ModEffects.WEBBED.get(), 10 + random.nextInt(41)), owner);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Items.COBWEB);
    }
}
