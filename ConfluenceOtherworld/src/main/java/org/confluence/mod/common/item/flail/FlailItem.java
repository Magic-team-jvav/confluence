package org.confluence.mod.common.item.flail;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.FlailBall;
import org.confluence.mod.mixed.IPlayer;
import org.jetbrains.annotations.NotNull;

// 原作者：@viola
public class FlailItem extends Item {
    public final float damage;
    public final ParticleOptions particle;
    public final MobEffect effect;
    public final double chance;

    public FlailItem(float damage, ParticleOptions particle, MobEffect effect, double chance){
        super(new Properties().stacksTo(1));
        this.damage = damage;
        this.particle = particle;
        this.effect = effect;
        this.chance = chance;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity){
        return 12000;
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand){
        pPlayer.startUsingItem(pUsedHand);
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        IPlayer fp = (IPlayer) pPlayer;
        FlailBall flail = fp.confluence$getFlailBall();
        if(flail == null){
            flail = new FlailBall(pPlayer.level(), pPlayer, pUsedHand, this); // TODO: 应该传ItemStack
            pPlayer.level().addFreshEntity(flail);
            fp.confluence$setFlailBall(flail);
        }else if(flail.getPhase() != FlailBall.PHASE_FORCE_RETRACT){
            flail.setPhase(FlailBall.PHASE_STAY);
        }
        return InteractionResultHolder.success(itemstack);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull LivingEntity pLivingEntity, int pTimeCharged){
//        Confluence.LOGGER.info("releaseUsing {}",pLivingEntity);
        super.releaseUsing(pStack, pLevel, pLivingEntity, pTimeCharged);
        if(!(pLivingEntity instanceof IPlayer player)) return;
        FlailBall flail = player.confluence$getFlailBall();
        if(flail == null) return;
        int phase = flail.getPhase();
        Player owner = (Player) player;
        if(phase == FlailBall.PHASE_SPIN){
            flail.setPhase(FlailBall.PHASE_THROWN);
            flail.setPos(flail.position().add(0, 1, 0));
            Vec3 direction = Vec3.directionFromRotation(owner.getXRot() - 3, owner.getYRot() - 3).scale(3); // TODO: 发射速度，近战速度加成
            flail.setDeltaMovement(direction.add(owner.getDeltaMovement()));
        }else{
            flail.setPhase(FlailBall.PHASE_FORCE_RETRACT);
        }
    }

    @Override
    public boolean useOnRelease(@NotNull ItemStack pStack){

        return super.useOnRelease(pStack);

    }
    public boolean isEdible(){return false;}
}