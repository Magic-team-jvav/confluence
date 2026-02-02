package org.confluence.terraentity.api.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import org.confluence.terraentity.api.event.SummonEvent;
import org.confluence.terraentity.entity.ai.goal.summon.SummonAttackPartEntityGoal;
import org.confluence.terraentity.entity.ai.goal.summon.SummonFollowOwnerGoal;
import org.confluence.terraentity.entity.ai.goal.summon.SummonOwnerHurtByTargetGoal;
import org.confluence.terraentity.entity.ai.goal.summon.SummonOwnerHurtTargetGoal;
import org.confluence.terraentity.entity.ai.goal.summon.SummonPriorAttackGoal;
import org.confluence.terraentity.init.TEAttachments;
import org.confluence.terraentity.init.TEAttributes;
import org.confluence.terraentity.init.TETags;
import org.confluence.terraentity.item.SummonItem;
import org.confluence.terraentity.utils.AdapterUtils;
import org.confluence.terraentity.utils.TEUtils;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * 召唤物接口，必须由Mob实现
 */
@SuppressWarnings({"unchecked", "rawtypes", "unused"})
public interface ISummonMob extends OwnableEntity {
    int getCost();

    void setCost(int cost);

    default Mob asEntity() {
        return (Mob) this;
    }

    /**
     * 区分是否为宠物，这样不需要再进行一次类型检查
     */
    default boolean isPet(){
        return false;
    }

    /*Tamed Animals**/

    EntityDataAccessor<Optional<UUID>> get_DATA_OWNERUUID_ID();

    default LivingEntity summon_getOwner() {
        UUID uuid = this.summon_getOwnerUUID();
        return uuid == null ? null : asEntity().level().getPlayerByUUID(uuid);
    }

    default void summon_setOwnerUUID(@Nullable UUID uuid) {
        asEntity().getEntityData().set(get_DATA_OWNERUUID_ID(), Optional.ofNullable(uuid));
    }

    @Nullable
    default UUID getOwnerUUID() {
        return summon_getOwnerUUID();
    }

    EntityGetter level();

    default UUID summon_getOwnerUUID() {
        return (UUID) ((Optional) asEntity().getEntityData().get(get_DATA_OWNERUUID_ID())).orElse(null);
    }

    default boolean summon_unableToMoveToOwner() {
        return this.summon_getOwner() != null && this.summon_getOwner().isSpectator();
    }

    default boolean summon_shouldTryTeleportToOwner() {
        LivingEntity livingentity = summon_getOwner();
        return livingentity != null && asEntity().distanceToSqr(summon_getOwner()) >= summon_getDistanceToTeleportToOwner();
    }

    default void summon_tryToTeleportToOwner() {
        LivingEntity livingentity = summon_getOwner();
        if (livingentity != null) {
            this.summon_teleportToAroundBlockPos(livingentity.blockPosition());
        }
    }

    default boolean summon_wantsToAttack(LivingEntity ownerLastHurtBy, LivingEntity livingentity) {
        return true;
    }

    default boolean summon_isTame() {
        return true;
    }

    default void summon_setTame(boolean tame, boolean applyTamingSideEffects) {}

    default void summon_addData(CompoundTag compound) {
        if (this.summon_getOwnerUUID() != null) {
            compound.putUUID("Owner", this.summon_getOwnerUUID());
        }
        compound.putInt("cost", getCost());

    }

    default void summon_readData(CompoundTag compound) {
        UUID uuid = null;
        if (compound.hasUUID("Owner")) {
            uuid = compound.getUUID("Owner");
        } else {
            String s = compound.getString("Owner");
            MinecraftServer server = asEntity().getServer();
            if (server != null) {
                uuid = OldUsersConverter.convertMobOwnerIfNecessary(server, s);
            }
        }
        if (uuid != null) {
            this.summon_setOwnerUUID(uuid);
        }
        setCost(compound.getInt("cost"));
    }

    /* Teleport API */

    /**
     * 当距离平方超过这个数时，会尝试传送到owner附近
     */
    default float summon_getDistanceToTeleportToOwner() {
        return 40 * 40;
    }

    /**
     * 当距离平方超过这个数时，会尝试移动到owner附近
     */
    default float summon_getStartDistanceToOwner() {
        return 32 * 32;
    }

    default void summon_teleportToAroundBlockPos(BlockPos pos) {
        for (int i = 0; i < 10; ++i) {
            int j = asEntity().getRandom().nextIntBetweenInclusive(-3, 3);
            int k = asEntity().getRandom().nextIntBetweenInclusive(-3, 3);
            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
                int l = asEntity().getRandom().nextIntBetweenInclusive(-1, 1);
                if (this.summon_maybeTeleportTo(pos.getX() + j, pos.getY() + l, pos.getZ() + k)) {
                    return;
                }
            }
        }
    }

    default boolean summon_maybeTeleportTo(int x, int y, int z) {
        if (!this.summon_canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            asEntity().moveTo((double) x + 0.5, y, (double) z + 0.5, asEntity().getYRot(), asEntity().getXRot());
            asEntity().getNavigation().stop();
            return true;
        }
    }

    default boolean summon_canTeleportTo(BlockPos pos) {
        PathType pathtype = WalkNodeEvaluator.getPathTypeStatic(asEntity(), pos);
        if (pathtype != PathType.WALKABLE && !summon_canFlyToOwner()) {
            return false;
        } else {
            BlockState blockstate = asEntity().level().getBlockState(pos.below());
            if (!this.summon_canFlyToOwner() && blockstate.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockpos = pos.subtract(asEntity().blockPosition());
                return asEntity().level().noCollision(asEntity(), asEntity().getBoundingBox().move(blockpos));
            }
        }
    }

    default boolean summon_canFlyToOwner() {
        return asEntity() instanceof FlyingAnimal;
    }

    /* Summoning API */

    default void summon(Player player, ItemStack stack) {
        summon_setOwnerUUID(player.getUUID());
        summon_setTame(true, true);
        if (stack.getItem() instanceof SummonItem<?> summonItem)
            asEntity().getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(summonItem.baseAttackDamage);
        AdapterUtils.postGameEvent(new SummonEvent(player, stack, this));
    }

    /* Attack API */

    default float summon_getAttackDamage(Entity entity, ServerLevel serverLevel, DamageSource damageSource) {
        float f = (float) asEntity().getAttributeValue(Attributes.ATTACK_DAMAGE);
        f = EnchantmentHelper.modifyDamage(serverLevel, asEntity().getWeaponItem(), entity, damageSource, f);
        if(getOwner() != null){
            f *= TEUtils.getAttributePercent(TEAttributes.SUMMON_DAMAGE, getOwner());
        }
        return f;
    }

    default DamageSource summon_getDamageSource() {
        return asEntity().damageSources().source(TETags.DamageTypes.SUMMONER, summon_getOwner());
    }

    /**
     * 简单攻击
     */
    default boolean summon_doHurtTarget(Entity entity) {
        float f = 0;
        DamageSource damagesource = summon_getDamageSource();
        Level var5 = asEntity().level();
        if (var5 instanceof ServerLevel serverlevel) {
            f = summon_getAttackDamage(entity, serverlevel, damagesource);
        }
        // 事件统一处理
//        f += (float) summon_getOwner().getAttributeValue(TEAttributes.MARK_DAMAGE);
        boolean flag = entity.hurt(damagesource, f);
        if (flag) {
            float f1 = summon_getKnockback(entity, damagesource);
            if (f1 > 0.0F && entity instanceof LivingEntity livingentity) {
                livingentity.knockback(f1 * 0.5F, Mth.sin(asEntity().getYRot() * 0.017453292F), -Mth.cos(asEntity().getYRot() * 0.017453292F));
                asEntity().setDeltaMovement(asEntity().getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            if (asEntity().level() instanceof ServerLevel level) {
                EnchantmentHelper.doPostAttackEffects(level, entity, damagesource);
            }

            asEntity().setLastHurtMob(entity);
//            asEntity().playAttackSound();
        }
        return flag;
    }

    default float summon_getKnockback(Entity attacker, DamageSource damageSource) {
        float f = (float) asEntity().getAttributeValue(Attributes.ATTACK_KNOCKBACK);
        Level var5 = asEntity().level();
        float var10000;
        if (var5 instanceof ServerLevel serverlevel) {
            var10000 = EnchantmentHelper.modifyKnockback(serverlevel, asEntity().getWeaponItem(), attacker, damageSource, f);
        } else {
            var10000 = f;
        }

        return var10000;
    }

    /* 以下方法需要被写入对应重写方法 */

    default void summon_registerCommonGoals() {
        summon_registerMoveGoal();
        asEntity().goalSelector.addGoal(10, new LookAtPlayerGoal(asEntity(), Player.class, 8.0F));
        asEntity().goalSelector.addGoal(10, new RandomLookAroundGoal(asEntity()));
        summon_registerTargetGoals();
    }

    default void summon_registerTargetGoals() {
        asEntity().targetSelector.addGoal(1, new SummonPriorAttackGoal<>(asEntity(), false));
        asEntity().targetSelector.addGoal(2, new SummonOwnerHurtByTargetGoal(asEntity()));
        asEntity().targetSelector.addGoal(3, new SummonOwnerHurtTargetGoal(asEntity()));
        asEntity().targetSelector.addGoal(4, new SummonAttackPartEntityGoal(asEntity()));
        asEntity().targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(asEntity(), Monster.class, 10, true, true, living -> (living instanceof Enemy && !(living instanceof NeutralMob))));
        asEntity().targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(asEntity(), Slime.class, 10, true, true, living -> (living instanceof Enemy && !(living instanceof NeutralMob))));
    }

    default void summon_registerMoveGoal() {
        asEntity().goalSelector.addGoal(6, new SummonFollowOwnerGoal(asEntity(), 1.0, 2.0F));
    }

    default void summon_onAddedToLevel() {
        if (!asEntity().level().isClientSide) {
            if (summon_getOwner() == null) {
                asEntity().discard();
                return;
            }
            var data = summon_getOwner().getData(TEAttachments.SUMMONER_STORAGE.get());
            data.getIds().add(asEntity().getId());
        }
    }

    default void summon_onRemovedFromLevel() {
        if (summon_getOwner() instanceof ServerPlayer owner) {
            var data = summon_getOwner().getData(TEAttachments.SUMMONER_STORAGE.get());
            if (data.canRemove(getCost())) {
                data.remove(owner, getCost(), asEntity().getId());
                data.sync(owner);
            }
        }
    }

    default boolean summon_discardWhenOwnerDie() {
        if (asEntity().level().isClientSide) return false;
        if (summon_getOwner() != null) {
            Entity entity = asEntity().level().getEntity(summon_getOwner().getId());
            if (entity == null || !entity.isAlive()) {
                asEntity().discard();
                return true;
            }
        }
        return false;
    }
}
