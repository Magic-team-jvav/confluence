package org.confluence.mod.mixin.entity;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.confluence.mod.api.event.ShimmerItemTransmutationEvent;
import org.confluence.mod.common.advancement.ModTriggers;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.mixed.IEntity;
import org.confluence.mod.mixed.IItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin implements IItemEntity {
    @Shadow
    public abstract ItemStack getItem();

    @Unique
    private static final Vec3 ANTI_GRAVITY = new Vec3(0.0, -5.0E-4F, 0.0);
    @Unique
    private int confluence$item_coolDown = 0;
    @Unique
    private int confluence$item_transforming = 0;

    @Override
    public void confluence$item_setCoolDown(int ticks) {
        this.confluence$item_coolDown = ticks;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void endTick(CallbackInfo ci) {
        ItemEntity self = (ItemEntity) (Object) this;
        Level level = self.level();
        if (level.isClientSide || self.isRemoved()) return;
        if (confluence$item_coolDown < 0) this.confluence$item_coolDown = 0;

        if (confluence$item_coolDown == 0 && ((IEntity) self).confluence$isInShimmer()) {
            ShimmerItemTransmutationEvent.Pre pre = new ShimmerItemTransmutationEvent.Pre(self);
            if (NeoForge.EVENT_BUS.post(pre).isCanceled()) {
                self.getItem().shrink(pre.getShrink());
                confluence$setup(self, pre.getCoolDown(), pre.getSpeedY());
            } else if (confluence$item_transforming < pre.getTransformTime()) {
                this.confluence$item_transforming++;
                self.addDeltaMovement(ANTI_GRAVITY);
            } else {
                ShimmerItemTransmutationEvent.Post post = new ShimmerItemTransmutationEvent.Post(self);
                NeoForge.EVENT_BUS.post(post);
                List<ItemStack> targets = post.getTargets();
                self.getItem().shrink(post.getShrink());
                if (targets == null) {
                    confluence$setup(self, post.getCoolDown(), post.getSpeedY());
                } else {
                    for (ItemStack target : targets) {
                        //if (PrefixProvider.canInit(target)) PrefixProvider.unknown(target); todo 词缀
                        ItemEntity itemEntity = new ItemEntity(level, self.getX(), self.getY(), self.getZ(), target);
                        confluence$setup(itemEntity, post.getCoolDown(), post.getSpeedY());
                        level.addFreshEntity(itemEntity);
                        level.playSound(null, self.getX(), self.getY(), self.getZ(), ModSoundEvents.SHIMMER_EVOLUTION.get(), SoundSource.AMBIENT, 1.0F, 1.0F);
                    }
                    if (self.getOwner() instanceof ServerPlayer serverPlayer) {
                        ModTriggers.SHIMMER_TRANSMUTATION.get().trigger(serverPlayer, self);
                    }
                }
            }
        } else if (confluence$item_coolDown > 0) {
            this.confluence$item_coolDown--;
        }
    }

    @Unique
    private static void confluence$setup(ItemEntity entity, int coolDown, double y) {
        entity.setNoGravity(true);
        Vec3 motion = entity.getDeltaMovement();
        entity.setDeltaMovement(motion.x, y, motion.z);
        ((IItemEntity) entity).confluence$item_setCoolDown(coolDown);
        entity.setGlowingTag(true);
    }
}
