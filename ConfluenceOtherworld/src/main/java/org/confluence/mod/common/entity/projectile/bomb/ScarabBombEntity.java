package org.confluence.mod.common.entity.projectile.bomb;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.MultiplyExplosionDamageCalculator;
import org.confluence.mod.common.init.ModEntities;
import org.confluence.mod.common.init.block.ModBlocks;
import org.jetbrains.annotations.Nullable;

public class ScarabBombEntity extends StickyBombEntity {
    private static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.defineId(ScarabBombEntity.class, EntityDataSerializers.INT);
    private Vec3 facingDir = new Vec3(0, -1, 0);
    private Entity owner;

    public ScarabBombEntity(EntityType<? extends ScarabBombEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.blastPower = 5;
    }

    public ScarabBombEntity(LivingEntity pShooter) {
        super(ModEntities.SCARAB_BOMB_ENTITY.get(), pShooter);
        this.blastPower = 5;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNER_ID, -1);
    }

    // todo 新粒子
    @Override
    protected void explodeFunction(ServerLevel level) {
        Vec3 blastPos = getEyePosition();
        Vec3 step = facingDir.normalize().scale(-3);
        float upperLimit = ModBlocks.getObsidianBasedExplosionResistance(50);
        ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList = new ObjectArrayList<>();
        DamageSource damageSource = Explosion.getDefaultDamageSource(level, this);
        MultiplyExplosionDamageCalculator damageCalculator = new MultiplyExplosionDamageCalculator(0.2F);
        for (int i = 0; i < 24; i++) {
            if (i % 3 == 0) {
                Explosion explosion = level.explode(this, damageSource, damageCalculator, blastPos.x(), blastPos.y(), blastPos.z(), blastPower, false, Level.ExplosionInteraction.MOB);
                Vec3 end = blastPos.add(step);
                BlockPos.betweenClosedStream(new AABB(blastPos, end)).forEach(blockPos -> {
                    if (!level.isLoaded(blockPos)) return;
                    BlockState blockState = level.getBlockState(blockPos);
                    if (blockState.getExplosionResistance(level, blockPos, explosion) < upperLimit) {
                        level.getProfiler().push("explosion_blocks");
                        if (blockState.canDropFromExplosion(level, blockPos, explosion)) {
                            BlockEntity blockentity = blockState.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
                            LootParams.Builder lootparams$builder = new LootParams.Builder(level)
                                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos))
                                    .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockentity)
                                    .withOptionalParameter(LootContextParams.THIS_ENTITY, this);
                            blockState.spawnAfterBreak(level, blockPos, ItemStack.EMPTY, getOwner() instanceof Player);
                            blockState.getDrops(lootparams$builder).forEach((itemStack) -> addBlockDrops(objectArrayList, itemStack, blockPos));
                        }
                        blockState.onBlockExploded(level, blockPos, explosion);
                        level.getProfiler().pop();
                    }
                });
                blastPos = end;
            }
        }
        for (Pair<ItemStack, BlockPos> pair : objectArrayList) {
            Block.popResource(level, pair.getSecond(), pair.getFirst());
        }
    }

    private static void addBlockDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> pDropPositionArray, ItemStack pStack, BlockPos pPos) {
        int i = pDropPositionArray.size();
        for (int j = 0; j < i; ++j) {
            Pair<ItemStack, BlockPos> pair = pDropPositionArray.get(j);
            ItemStack itemstack = pair.getFirst();
            if (ItemEntity.areMergable(itemstack, pStack)) {
                ItemStack itemStack = ItemEntity.merge(itemstack, pStack, 16);
                pDropPositionArray.set(j, Pair.of(itemStack, pair.getSecond()));
                if (pStack.isEmpty()) return;
            }
        }
        pDropPositionArray.add(Pair.of(pStack, pPos));
    }

    @Override
    public void tick() {
        super.tick();
        if (getOwner() != null && stickBlock != null) {
            this.facingDir = getOwner().getEyePosition().subtract(position());
            Vec3 vec3 = facingDir.normalize();
            float f = (float) vec3.horizontalDistance();
            setYRot(90 - (float) Math.atan2(vec3.z, vec3.x) * Mth.RAD_TO_DEG);
            this.rotate = (float) Math.atan2(f, vec3.y);
        }
    }

    @Override
    public void setOwner(@Nullable Entity owner) {
        super.setOwner(owner);
        entityData.set(DATA_OWNER_ID, owner == null ? 0 : owner.getId());
    }

    @Override
    public @Nullable Entity getOwner() {
        if (owner == null) {
            if (level().isClientSide) {
                int id = entityData.get(DATA_OWNER_ID);
                if (id != -1) {
                    this.owner = level().getEntity(id);
                }
            } else {
                this.owner = super.getOwner();
            }
        }
        return owner;
    }
}
