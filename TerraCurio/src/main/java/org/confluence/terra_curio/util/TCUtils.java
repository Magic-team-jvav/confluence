package org.confluence.terra_curio.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.common.LibTags;
import org.confluence.lib.util.LibUtils;
import org.confluence.lib.util.VectorUtils;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;
import org.confluence.terra_curio.api.primitive.UnitValue;
import org.confluence.terra_curio.api.primitive.ValueType;
import org.confluence.terra_curio.client.handler.TCClientPacketHandler;
import org.confluence.terra_curio.common.attachment.AccessoriesAttachment;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.confluence.terra_curio.common.entity.BeeProjectile;
import org.confluence.terra_curio.common.entity.StarCloakEntity;
import org.confluence.terra_curio.common.init.*;
import org.confluence.terra_curio.mixed.IEntity;
import org.confluence.terra_curio.mixed.ILivingEntity;
import org.confluence.terra_curio.network.InfoDisablePacket;
import org.confluence.terra_curio.network.c2s.PlayerSprintPacketC2S;
import org.confluence.terra_curio.network.s2c.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class TCUtils {
    public static final AttributeModifier ICE_SPEED_MODIFIER = new AttributeModifier(TerraCurio.asResource("ice_speed"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    public static void applyFireAttack(DamageSource damageSource, Entity victim) {
        if (damageSource.is(LibTags.DamageTypes.AS_MELEE_ATTACK) &&
                damageSource.getEntity() instanceof LivingEntity living &&
                hasType(living, TCItems.FIRE$ATTACK)
        ) {
            float f = living.getRandom().nextFloat();
            int time;
            if (f < 0.25F) {
                time = 120;
            } else if (f < 0.375F) {
                time = 80;
            } else {
                time = 40;
            }
            victim.igniteForTicks(time);
        }
    }

    public static boolean isInvulnerableTo(Entity self, DamageSource damageSource) {
        if (!(self instanceof LivingEntity living)) return false;
        AccessoriesAttachment attachment = AccessoriesAttachment.of(living);
        Entity attacker = damageSource.getEntity();
        if (attacker != null && attacker.getType().is(attachment.getValue(TCItems.MOB$IGNORE))) {
            return true;
        }
        if (IEntity.of(living).terra_curio$getCthulhuSprintingTime() > 10 && attachment.contains(TCItems.SHIELD$OF$CTHULHU)) {
            return true;
        }
        if (attachment.contains(TCItems.FIRE$IMMUNE) && isFire(damageSource)) {
            living.clearFire();
            return true;
        }
        return TCAttributes.applyDodge(living, living.getRandom());
    }

    /**
     * 不包括熔岩
     */
    public static boolean isFire(DamageSource damageSource) {
        return damageSource.is(DamageTypes.IN_FIRE) ||
                damageSource.is(DamageTypes.ON_FIRE) ||
                damageSource.is(DamageTypes.HOT_FLOOR) ||
                damageSource.is(DamageTypes.UNATTRIBUTED_FIREBALL) ||
                damageSource.is(DamageTypes.FIREBALL);
    }

    public static float applyInjuryFree(LivingEntity living, float amount) {
        return amount * (1.0F - getValue(living, TCItems.INJURY$FREE));
    }

    public static void applyStarClock(LivingEntity living, RandomSource random) {
        AccessoriesAttachment attachment = AccessoriesAttachment.of(living);
        boolean starClock = attachment.contains(TCItems.STAR$CLOCK);
        if (starClock) {
            Level level = living.level();
            List<Entity> list = level.getEntities(living, new AABB(living.blockPosition()).inflate(4.0, 3.0, 4.0), entity -> entity instanceof Enemy);
            for (int i = 0; i < 3; i++) {
                Entity target;
                if (list.isEmpty()) {
                    target = living;
                } else {
                    target = list.get(random.nextInt(list.size()));
                }
                StarCloakEntity entity = new StarCloakEntity(level, living, target, attachment.getValue(TCItems.STAR$CLOCK));
                level.addFreshEntity(entity);
            }
        }
    }

    public static void applyHoneyComb(LivingEntity living, RandomSource random) {
        AccessoriesAttachment attachment = AccessoriesAttachment.of(living);
        if (attachment.contains(TCItems.HONEY$COMB)) {
            boolean hasHivePack = attachment.contains(TCItems.HIVE$PACK);
            int summon = random.nextInt(1, hasHivePack ? 5 : 4);
            for (int i = 0; i < summon; i++) {
                BeeProjectile projectile = new BeeProjectile(living.level(), living, hasHivePack && random.nextBoolean());
                projectile.setPos(living.position().add(random.nextInt(3) - 1.0, 2.0, random.nextInt(3) - 1.0));
                living.level().addFreshEntity(projectile);
            }
            living.addEffect(new MobEffectInstance(TCEffects.HONEY, 100));
        }
    }

    public static void applyIgniteArrow(LivingEntity living, AbstractArrow arrow) {
        if (hasType(living, TCItems.IGNITE$ARROW)) {
            arrow.igniteForTicks(2000);
        }
    }

    public static float applyFrozenTurtleShell(LivingEntity living, float amount) {
        if (living.getHealth() / living.getMaxHealth() < 0.5F && hasType(living, TCItems.FROZEN$TURTLE$SHELL)) {
            return amount * 0.75F;
        }
        return amount;
    }

    public static float applyBrainOfConfusion(LivingEntity living, RandomSource randomSource, DamageSource damageSource, float amount) {
        if (damageSource.is(TCTags.HARMFUL_EFFECT)) return amount;
        if (!AccessoriesAttachment.of(living).contains(TCItems.BRAIN$OF$CONFUSION)) {
            return amount;
        }
        if (randomSource.nextFloat() < 0.6F + amount * 0.02F) {
            float rangeMin, rangeMax;
            if (amount <= 120) rangeMin = amount * 0.5F + 200;
            else if (amount <= 266.6F) rangeMin = amount * 0.375F + 275;
            else if (amount <= 440) rangeMin = amount * 0.1875F + 487.5F;
            else rangeMin = amount * 0.046875F + 796.875F;
            if (amount <= 20) rangeMax = amount * 2 + 300;
            else if (amount <= 46.6F) rangeMax = amount * 1.5F + 350;
            else if (amount <= 100) rangeMax = amount * 0.75F + 525;
            else rangeMax = amount * 0.1875F + 806.25F;
            float range = Mth.nextFloat(randomSource, rangeMin, rangeMax) / 24;
            int duration = randomSource.nextInt((int) (90 + amount / 3), (int) (300 + amount / 2));
            living.level().getEntities(living, new AABB(living.blockPosition()).inflate(range), entity -> entity instanceof Enemy).forEach(enemy -> {
                if (enemy instanceof LivingEntity living1) {
                    living1.addEffect(new MobEffectInstance(TCEffects.CONFUSED, duration));
                }
            });
        }
        if (randomSource.nextFloat() < 0.1667F && !living.hasEffect(TCEffects.CEREBRAL_MINDTRICK)) {
            living.addEffect(new MobEffectInstance(TCEffects.CEREBRAL_MINDTRICK, 80));
            return 0.0F;
        }
        return amount;
    }

    public static boolean magicQuiver$shouldConsume(LivingEntity living) {
        return !AccessoriesAttachment.of(living).contains(TCItems.MAGIC$QUIVER) || living.getRandom().nextFloat() >= 0.2F;
    }

    public static void resetClientPacket(ServerPlayer serverPlayer) {
        InfoCurioCheckPacketS2C.sendToClient(serverPlayer, serverPlayer.getInventory());
        InfoDisablePacket.sendToClient(serverPlayer);
        CurioExistsPacketS2C.sendToClient(serverPlayer);
        PlayerClimbPacketS2C.sendToClient(serverPlayer);
        PlayerJumpPacketS2C.sendToClient(serverPlayer);
        PlayerFlyPacketS2C.sendToClient(serverPlayer);
        RightClickSubtractorPacketS2C.sendToClient(serverPlayer);
        InfiniteFlightPacketS2C.sendToClient(serverPlayer);
        BroadcastRenderPacketS2C.sendToPlayersTrackingTarget(serverPlayer);
        FluidWalkUpdatePacketS2C.sendToClient(serverPlayer);
    }

    // confluence mixin here
    public static float applyLavaHurtReduce(LivingEntity living, DamageSource damageSource, float amount) {
        if (damageSource.is(DamageTypes.LAVA)) {
            return amount * (1.0F - getValue(living, TCItems.LAVA$HURT$REDUCE));
        }
        return amount;
    }

    public static void onChangedBlock(LivingEntity living, ServerLevel level) {
        if (!living.onGround()) return;
        AccessoriesAttachment attachment = AccessoriesAttachment.of(living);
        BlockPos onPos = living.getOnPos();
        if (attachment.contains(TCItems.FLOWER$BOOTS) && level.getBlockState(onPos).is(TCTags.FLOWER_BOOTS_AVAILABLE)) {
            BlockPos abovePos = onPos.above();
            RandomSource random = level.random;
            for (BlockPos aroundPos : BlockPos.betweenClosed(abovePos.offset(-1, 0, -1), abovePos.offset(1, 0, 1))) {
                if (!level.getBlockState(aroundPos.below()).is(TCTags.FLOWER_BOOTS_AVAILABLE))
                    continue;
                if (level.getBlockState(aroundPos).isCollisionShapeFullBlock(level, aroundPos))
                    continue;
                if (random.nextFloat() < 0.3F && level.getBlockState(aroundPos).isAir()) {
                    List<ConfiguredFeature<?, ?>> list = level.getBiome(aroundPos).value().getGenerationSettings().getFlowerFeatures();
                    if (list.isEmpty()) continue;
                    ((RandomPatchConfiguration) list.getFirst().config()).feature().value().place(level, level.getChunkSource().getGenerator(), random, aroundPos);
                }
            }
        }
        if (attachment.contains(TCItems.ICE$SPEED)) {
            AttributeInstance instance = living.getAttribute(Attributes.MOVEMENT_SPEED);
            assert instance != null;
            if (level.getBlockState(onPos).is(BlockTags.ICE)) {
                if (!instance.hasModifier(ICE_SPEED_MODIFIER.id())) {
                    instance.addTransientModifier(ICE_SPEED_MODIFIER);
                }
            } else {
                instance.removeModifier(ICE_SPEED_MODIFIER);
            }
        }
    }

    public static void updateWalkableFluidStates(Player player) {
        Set<FluidState> walkableFluidStates = new HashSet<>();
        Set<TagKey<Fluid>> tagKeys = CuriosUtils.calculateValue(player, TCItems.FLUID$WALK);
        BuiltInRegistries.FLUID.stream().flatMap(fluid -> fluid.getStateDefinition().getPossibleStates().stream()).forEach(state -> {
            if (tagKeys.stream().anyMatch(state::is)) {
                walkableFluidStates.add(state);
            }
        });
        ((ILivingEntity) player).terra_curio$resetLastWalkedFluidState(walkableFluidStates);
    }

    public static void applyFluidWalk(Player player) {
        if (player.getEyeInFluidType() == NeoForgeMod.EMPTY_TYPE.value()) {
            BlockPos pos = player.blockPosition();
            Level level = player.level();
            FluidState fluidState;
            if (level.getFluidState(pos.above()).isEmpty() && player.canStandOnFluid((fluidState = level.getFluidState(pos)))) {
                Vec3 motion = player.getDeltaMovement();
                double deltaY = player.getY() - Mth.floor(player.getY());
                double maxY = Math.max(0.0, motion.y);
                double fluidHeight = fluidState.getHeight(level, pos);
                if (deltaY < fluidHeight) {
                    maxY += fluidHeight - deltaY;
                }
                player.setDeltaMovement(motion.x, maxY, motion.z);
                player.setOnGround(true);
                float f = Math.min(0.1F, (float) motion.horizontalDistance());
                player.bob = player.bob + (f - player.bob) * 0.8F;
            }
        }
    }

    public static boolean isFluidWalkable(LivingEntity living, FluidState fluidState) {
        if (fluidState.isEmpty() || living.isCrouching() || !IEntity.of(living).terra_curio$isPlayer()) {
            return false;
        }
        ILivingEntity iLiving = (ILivingEntity) living;
        if (iLiving.terra_curio$getLastWalkedFluidState() == fluidState) {
            return true;
        } else if (iLiving.terra_curio$isFluidWalkable(fluidState)) {
            iLiving.terra_curio$setLastWalkedFluidState(fluidState);
            return true;
        }
        return false; // confluence mixin here
    }

    public static boolean applyTotemAbility(LivingEntity living) {
        ILivingEntity iLiving = (ILivingEntity) living;
        if (iLiving.terra_curio$getTotemCooldown() == 0) {
            int cooldown = getValue(living, TCItems.TOTEM$WITH$COOLDOWN);
            if (cooldown > 0) {
                living.setHealth(1.0F);
                living.removeEffectsCuredBy(EffectCures.PROTECTED_BY_TOTEM);
                living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                living.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                living.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
                living.level().broadcastEntityEvent(living, EntityEvent.TALISMAN_ACTIVATE);
                iLiving.terra_curio$setTotemCooldown(cooldown);
                return true;
            } else {
                iLiving.terra_curio$setTotemCooldown(-1);
            }
        }
        return false;
    }

    public static void applyCthulhuTouch(Player player, Entity touched) {
        if (player == touched || IEntity.of(player).terra_curio$getCthulhuSprintingTime() <= 20)
            return;
        if (LibUtils.getOwner(touched) instanceof LivingEntity target && player != target) {
            Vec3 vector = player.getDeltaMovement();
            VectorUtils.knockBack(player, touched, new Vec3(vector.x * 1.2, 0.2, vector.z * 1.2));
            touched.hurt(player.damageSources().playerAttack(player), 7.8F);
            player.setDeltaMovement(vector.scale(-0.9));
            IEntity.of(player).terra_curio$setCthulhuSprintingTime(20);
        }
    }

    private static boolean sprintKeyDown = false;

    public static void applyCthulhuSprinting(boolean down, Player player) {
        if (IEntity.of(player).terra_curio$getCthulhuSprintingTime() > 0 || player.isFallFlying()) {
            return;
        }
        boolean sprint = false;
        if (player.isLocalPlayer()) {
            if (down) {
                if (!sprintKeyDown && TCClientPacketHandler.isHasCthulhu()) {
                    PacketDistributor.sendToServer(PlayerSprintPacketC2S.INSTANCE);
                    sprintKeyDown = true;
                    sprint = true;
                }
            } else {
                sprintKeyDown = false;
            }
        } else if (hasType(player, TCItems.SHIELD$OF$CTHULHU)) {
            sprint = true;
        }
        if (sprint) {
            float f = player.getYRot() * Mth.DEG_TO_RAD;
            double factor = player.onGround() ? 1.6 : 1.2;
            player.setDeltaMovement(player.getDeltaMovement().add(-Mth.sin(f) * factor, 0.0D, Mth.cos(f) * factor));
            IEntity.of(player).terra_curio$setCthulhuSprintingTime(32);
        }
    }

    public static boolean applyLavaImmune(boolean original, Entity self) {
        if (!self.level().isClientSide) {
            if (original) {
                if (AccessoriesAttachment.of(self).decreaseLavaImmuneTicks()) {
                    return false;
                }
            } else {
                AccessoriesAttachment.of(self).increaseLavaImmuneTicks();
            }
        }
        return original;
    }

    public static boolean hasType(LivingEntity living, ValueType<Unit, UnitValue> type) {
        return AccessoriesAttachment.of(living).contains(type);
    }

    public static <T, V extends PrimitiveValue<T>> T getValue(LivingEntity living, ValueType<T, V> type) {
        return AccessoriesAttachment.of(living).getValue(type);
    }

    public static <T, V extends PrimitiveValue<T>> @Nullable V getPrimitiveValue(LivingEntity living, ValueType<T, V> type) {
        return AccessoriesAttachment.of(living).getPrimitiveValue(type);
    }

    public static @Nullable PrimitiveValueComponent getAccessoriesComponent(ItemStack itemStack) {
        PrimitiveValueComponent component = itemStack.getItemHolder().getData(TCDataMaps.ACCESSORIES);
        if (component != null || (component = itemStack.get(TCDataComponentTypes.ACCESSORIES)) != null) {
            return component;
        }
        return null;
    }

    public static boolean isIceSafe(LivingEntity self) {
        if (IEntity.of(self).terra_curio$isPlayer() && ((Player) self).isLocalPlayer()) {
            return TCClientPacketHandler.isIceSafe();
        }
        return TCUtils.hasType(self, TCItems.ICE$SAFE);
    }

    // confluence mixin here
    public static boolean applyFrozenImmune(LivingEntity living, boolean original) {
        if (original && TCUtils.hasType(living, TCItems.FROZEN$IMMUNE)) {
            return false;
        }
        return original;
    }
}
