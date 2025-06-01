package org.confluence.mod.mixin.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.fluids.FluidType;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.mod.api.event.ShimmerEntityTransmutationEvent;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.data.saved.NPCSpawner;
import org.confluence.mod.common.init.ModFluids;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.item.ArmorItems;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.confluence.mod.api.event.ShimmerEntityTransmutationEvent.ENTITY_TRANSMUTATION;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntity, SelfGetter<Entity> {
    @Unique
    private static final Vec3 ANTI_GRAVITY = new Vec3(0.0, -5.0E-4F, 0.0);

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

        boolean isItem = self instanceof ItemEntity;
        if (getEyeInFluidType() == ModFluids.SHIMMER.type().get()) {
            if (!confluence$isInShimmer) { // 入微光
                this.confluence$isInShimmer = true;
                self.level().playSound(null, self.getX(), self.getY(), self.getZ(), isItem ? ModSoundEvents.SHIMMER_ITEM_INTERACTIONS.get() : ModSoundEvents.SHIMMER_IMMERSION.get(), SoundSource.AMBIENT, 0.5F, 1.0F);
            }
        } else {
            if (confluence$isInShimmer) { // 出微光
                this.confluence$isInShimmer = false;
                self.level().playSound(null, self.getX(), self.getY(), self.getZ(), ModSoundEvents.SHIMMER_DETACHMENT.get(), SoundSource.AMBIENT, 0.5F, 1.0F);
            }
        }

        if (confluence$isInShimmer) {
            if (!self.level().isClientSide && confluence$entity_coolDown == 0 && !isItem) {
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
            if (confluence$entity_coolDown > 0) this.confluence$entity_coolDown--;
            if (confluence$entity_coolDown == 0 && confluence$transformData != HAD_SETUP && !isItem) {
                setGlowingTag(false);
                if (confluence$transformData == HAS_GRAVITY) {
                    setNoGravity(false);
                }
                this.confluence$transformData = HAD_SETUP;
            }
            this.confluence$wasInShimmer = false;
        }
    }

    @Inject(method = "getName", at = @At("RETURN"), cancellable = true)
    private void zombieName(CallbackInfoReturnable<Component> cir) {
        Component returnValue = cir.getReturnValue();
        Entity self = confluence$self();
        if (self instanceof Zombie zombie) {
            ItemStack chestItem = zombie.getItemBySlot(EquipmentSlot.CHEST);
            if (chestItem.is(ArmorItems.RAINCOAT.get())) {
                returnValue = Component.translatable("entity.confluence.raincoat_zombie");
            } else if (chestItem.is(ArmorItems.SNOW_SUITS.get()) || chestItem.is(ArmorItems.PINK_SNOW_SUITS.get())) {
                returnValue = Component.translatable("entity.confluence.frozen_zombie");
            }
        } else if (self instanceof Skeleton skeleton) {
            if (skeleton.getItemBySlot(EquipmentSlot.CHEST).is(ArmorItems.MINING_CHESTPLATE.get())) {
                returnValue = Component.translatable("entity.confluence.undead_miner");
            }
        }
        cir.setReturnValue(returnValue);
    }

    @Unique
    private static void confluence$setup(Entity entity, int coolDown, double y) {
        IEntity iEntity = (IEntity) entity;
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
                    targetNpc.setHouse(sourceNpc.house);
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
}
