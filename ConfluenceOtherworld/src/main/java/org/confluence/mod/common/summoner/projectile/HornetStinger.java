package org.confluence.mod.common.summoner.projectile;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.confluence.mod.common.summoner.attachmentEntity.IBlockCollision;
import org.confluence.mod.common.summoner.attachmentEntity.IEntityCollision;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class HornetStinger extends Projectile implements IEntityCollision<HornetStinger>, IBlockCollision<HornetStinger>, GeoAnimatable {

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public HornetStinger() {
        super(SummonerAttachmentEntityTypes.HORNET_STINGER);
        setMaxTickCount(200);
        setDrag(1.0F);
        setGravity(0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    @Override
    public double getTick(Object object) {
        return tickCount;
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        HitContext context = hitContexts.get(0);
        LivingEntity target = context.entity();
        attack(target, getDamage(), 0);
        Player owner = getOwner();
        int amplifier = owner != null && TCUtils.hasType(owner, TCItems.HIVE$PACK) ? 1 : 0;
        target.addEffect(new MobEffectInstance(MobEffects.POISON, 80 + getRandom().nextInt(60), amplifier), owner);
        setCurrentPathNode(getCurrentPathNode().modifyPos(context.hitPoint()));
        setRemove();
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.075, -0.075, -0.3, 0.075, 0.075, 0.1);
    }

    @Override
    public @NotNull AABB getBlockCollisionBox() {
        return new AABB(-0.075, -0.075, -0.075, 0.075, 0.075, 0.075);
    }

    @Override
    public void onBlockCollision(CollisionContext context) {
        setRemove();
    }
}
