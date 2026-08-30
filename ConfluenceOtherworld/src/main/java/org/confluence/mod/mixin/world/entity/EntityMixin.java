package org.confluence.mod.mixin.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.fluids.FluidType;
import org.confluence.mod.api.event.ShimmerEntityTransmutationEvent;
import org.confluence.mod.common.block.common.AetheriumCauldronBlock;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.integration.terra_entity.IAbstractTerraNPC;
import org.confluence.mod.mixed.IEntity;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.AnglerNPC;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.confluence.mod.api.event.ShimmerEntityTransmutationEvent.ENTITY_TRANSMUTATION;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntity {
    @Shadow
    public abstract DamageSources damageSources();

    @Shadow(remap = false)
    public abstract FluidType getEyeInFluidType();

    @Shadow
    public abstract void discard();

    @Shadow
    public abstract void setNoGravity(boolean pNoGravity);

    @Shadow
    public abstract void setGlowingTag(boolean pHasGlowingTag);

    @Shadow
    public abstract Vec3 getDeltaMovement();

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    public abstract boolean is(Entity pEntity);

    @Shadow
    public abstract BlockState getInBlockState();

    @Shadow
    public abstract BlockPos blockPosition();

    @Unique
    private boolean confluence$isInShimmer = false;
    @Unique
    private boolean confluence$wasInShimmer = false;
    @Unique
    private int confluence$entity_coolDown = 0;
    @Unique
    private int confluence$entity_transforming = 0;
    @Unique
    private byte confluence$transformData = HAD_SETUP;

    @Override
    public void confluence$entity_setCoolDown(int ticks) {
        this.confluence$entity_coolDown = ticks;
    }

    @Override
    public void confluence$setOriginalNoGravity(boolean bool) {
        this.confluence$transformData = bool ? NO_GRAVITY : HAS_GRAVITY;
    }

    @Override
    public boolean confluence$isInShimmer() {
        return confluence$isInShimmer;
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"))
    private void shimmerTick(CallbackInfo ci) {
        Entity self = confluence$self();
        if (self instanceof PartEntity<?>) return;

        if (confluence$entity_coolDown < 0) this.confluence$entity_coolDown = 0;

        if (confluence$checkInShimmer()) {
            if (!confluence$isInShimmer) { // 入微光
                this.confluence$isInShimmer = true;
                self.level().playSound(null, self.getX(), self.getY(), self.getZ(), self instanceof ItemEntity ? ModSoundEvents.SHIMMER_ITEM_INTERACTIONS.get() : ModSoundEvents.SHIMMER_IMMERSION.get(), SoundSource.AMBIENT, 0.5F, 1.0F);
            }
        } else {
            if (confluence$isInShimmer) { // 出微光
                this.confluence$isInShimmer = false;
                self.level().playSound(null, self.getX(), self.getY(), self.getZ(), ModSoundEvents.SHIMMER_DETACHMENT.get(), SoundSource.AMBIENT, 0.5F, 1.0F);
            }
        }

        if (confluence$isInShimmer) {
            if (confluence$entity_coolDown == 0 && !self.level().isClientSide && !(self instanceof ItemEntity)) {
                ShimmerEntityTransmutationEvent.Pre pre = new ShimmerEntityTransmutationEvent.Pre(self);
                if (NeoForge.EVENT_BUS.post(pre).isCanceled()) {
                    confluence$setup(self, pre.getCoolDown(), pre.getSpeedY());
                } else if (confluence$entity_transforming < pre.getTransformTime()) {
                    this.confluence$entity_transforming++;
                    self.addDeltaMovement(ANTI_GRAVITY);
                } else {
                    ShimmerEntityTransmutationEvent.Post post = new ShimmerEntityTransmutationEvent.Post(self);
                    confluence$initTarget(post);
                    NeoForge.EVENT_BUS.post(post);
                    Entity target = post.getTarget();
                    if (target != null) {
                        discard();
                        confluence$setup(target, post.getCoolDown(), post.getSpeedY());
                        self.level().addFreshEntity(target);
                        self.level().playSound(null, self.getX(), self.getY(), self.getZ(), ModSoundEvents.SHIMMER_EVOLUTION.get(), SoundSource.AMBIENT, 0.5F, 1.0F);
                        return;
                    }
                }
            }

            if (!confluence$wasInShimmer && self instanceof Projectile projectile) {
                Vec3 motion = projectile.getDeltaMovement();
                projectile.setDeltaMovement(motion.x, -motion.y, motion.z);
                this.confluence$wasInShimmer = true;
            }
        } else {
            this.confluence$entity_transforming = 0;
            if (--this.confluence$entity_coolDown == 0 && confluence$transformData != HAD_SETUP && !(self instanceof ItemEntity)) {
                setGlowingTag(false);
                if (confluence$transformData == HAS_GRAVITY) {
                    setNoGravity(false);
                }
                this.confluence$transformData = HAD_SETUP;
            }
            this.confluence$wasInShimmer = false;
        }
    }

    @Unique
    private static void confluence$setup(Entity entity, int coolDown, double y) {
        IEntity iEntity = IEntity.of(entity);
        iEntity.confluence$setOriginalNoGravity(entity.isNoGravity());
        iEntity.confluence$entity_setCoolDown(coolDown);
        entity.setNoGravity(true);
        Vec3 motion = entity.getDeltaMovement();
        entity.setDeltaMovement(motion.x, y, motion.z);
        entity.setGlowingTag(true);
    }

    @Unique
    private static void confluence$initTarget(ShimmerEntityTransmutationEvent.Post event) {
        Entity sourceEntity = event.getSource();
        GamePhase gamePhase = KillBoard.INSTANCE.getGamePhase();
        for (ShimmerEntityTransmutationEvent.EntityTransmutation transmutation : ENTITY_TRANSMUTATION) {
            if (transmutation.gamePhase().isAboveThan(gamePhase)) continue;
            if (transmutation.source().test(sourceEntity)) {
                Entity target = transmutation.target().create(sourceEntity.level());
                if (target == null) continue;
                target.setPos(sourceEntity.position());
                target.setXRot(sourceEntity.getXRot());
                target.setYRot(sourceEntity.getYRot());
                target.setYHeadRot(sourceEntity.getYHeadRot());
                if (target instanceof LivingEntity livingTarget && sourceEntity instanceof LivingEntity livingSource) {
                    float ratio = livingSource.getHealth() / livingSource.getMaxHealth();
                    livingTarget.setHealth(livingTarget.getMaxHealth() * ratio);
                }
                event.setTarget(target);
                if (sourceEntity instanceof AbstractTerraNPC sourceNpc && target instanceof AbstractTerraNPC targetNpc) {
                    targetNpc.setHouse(sourceNpc.getHouse());
                    event.setSpeedY(0.7);
                    NPCSpawner.INSTANCE.setNPCAlive(((IAbstractTerraNPC) sourceNpc).confluence$getRegion(), sourceNpc.getType(), false);
                    if (target instanceof AnglerNPC anglerNPC) {
                        anglerNPC.setWakeUp(true);
                        anglerNPC.initName();
                        anglerNPC.refreshBrain((ServerLevel) sourceEntity.level());
                        anglerNPC.refreshDimensions();
                    }
                }
                return;
            }
        }
    }

    @Unique
    private boolean confluence$checkInShimmer() {
        if (getEyeInFluidType() == ModFluids.SHIMMER.type().get()) return true;
        BlockState state = getInBlockState();
        Block block = state.getBlock();
        return block == ModBlocks.AETHERIUM_CAULDRON.get() && ((AetheriumCauldronBlock) block).isEntityInsideContent(state, blockPosition(), confluence$self());
    }
}
