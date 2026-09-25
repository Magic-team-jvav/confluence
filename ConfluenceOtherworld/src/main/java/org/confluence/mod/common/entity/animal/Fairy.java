package org.confluence.mod.common.entity.animal;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.block.natural.LifeCrystalBlock;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.util.AchievementUtils;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.EnumSet;
import java.util.Locale;
import java.util.function.IntFunction;

public class Fairy extends BaseFlyingCritter implements VariantHolder<Fairy.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Fairy.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_GUIDING = SynchedEntityData.defineId(Fairy.class, EntityDataSerializers.BOOLEAN);
    public static final String VARIANT_KEY = "Variant";
    private static final String GUIDING_KEY = "Guiding";
    private static final String GUIDING_TICKS_KEY = "GuidingTicks";
    private static final int MAX_GUIDING_TICKS = 5 * 60 * 20;
    private static final VariantSpawnProfile<Variant> SPAWN_VARIANTS = VariantSpawnProfile.<Variant>builder()
            .add(Variant.BLUE, 1)
            .add(Variant.GREEN, 1)
            .add(Variant.PINK, 1)
            .build();

    public Fairy(EntityType<? extends Fairy> type, Level level) {
        super(type, level);
        /// 仙灵需要跨越普通方块把玩家引向宝箱。该标记只改变实体碰撞，
        /// 导航目标、脱离距离和移动节奏仍由引导目标负责。
        setGuiding(true);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        new VanillaGoalAction(new SurfaceFleeGoal(Fairy.this)),
                        new VanillaGoalAction(new FairyGuideAction(Fairy.this)),
                        createFlyingRoutine()
                );
            }
        };
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, data, tag);
        if (tag == null || !tag.contains(GUIDING_KEY)) {
            setGuiding(spawnType != MobSpawnType.NATURAL || !level.canSeeSky(blockPosition()));
        }
        return result;
    }

    public boolean isGuiding() {
        return entityData.get(DATA_GUIDING);
    }

    private void setGuiding(boolean guiding) {
        entityData.set(DATA_GUIDING, guiding);
        noPhysics = guiding;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide && isGuiding()) {
            int guidingTicks = getPersistentData().getInt(GUIDING_TICKS_KEY) + 1;
            getPersistentData().putInt(GUIDING_TICKS_KEY, guidingTicks);
            if (guidingTicks >= MAX_GUIDING_TICKS) discard();
        }
    }

    private static final class SurfaceFleeGoal extends AvoidEntityGoal<Player> {
        private final Fairy fairy;

        private SurfaceFleeGoal(Fairy fairy) {
            super(fairy, Player.class, 6.0F, 1.0D, 1.3D);
            this.fairy = fairy;
        }

        @Override
        public boolean canUse() {
            return !fairy.isGuiding() && super.canUse();
        }
    }

    /// 实现仙灵“发现玩家、建立跟随、寻找宝物并带路”的完整状态机。
    ///
    /// 动作以十格为首次发现范围；玩家靠近到三格内后，仙灵进入持续引导状态，
    /// 此后允许双方拉开到三十格。容器类宝物在仙灵所在区块周围一圈区块内搜索，
    /// 生命水晶、生命果及矿物则在有限方块范围内搜索。若宝物距离玩家超过十格，
    /// 当前导航点会限制在玩家前方十格，从而让仙灵逐段带路，而不是直接飞走。
    private static final class FairyGuideAction extends Goal {
        private static final double ACQUIRE_RANGE = 10.0;
        private static final double ABANDON_RANGE = 30.0;
        private static final double FOLLOW_DISTANCE = 3.0;
        private static final double GUIDE_STEP = 10.0;
        private static final double ORBIT_RADIUS = 3.0;
        private static final double ORBIT_HEIGHT = 3.0;
        private static final int CHEST_CHUNK_RADIUS = 1;
        private static final int BLOCK_TREASURE_HORIZONTAL_RANGE = 24;
        private static final int BLOCK_TREASURE_VERTICAL_RANGE = 16;
        private static final int TREASURE_SEARCH_INTERVAL = 100;

        private final Fairy fairy;
        private Player target;
        private BlockPos guidePos;
        private boolean following;
        private float angle;
        private int arrivalTicks;
        private int treasureSearchCooldown;

        private FairyGuideAction(Fairy fairy) {
            this.fairy = fairy;
            setFlags(EnumSet.of(Flag.MOVE));
        }

        /// 供实时分支每 tick 判断是否需要占用移动控制。
        ///
        /// 首次进入时只接纳十格内玩家；一旦完成近距离接触，则沿用同一玩家，
        /// 直到玩家死亡或离开三十格，避免引导途中在多个玩家之间来回切换。
        public boolean canUse() {
            if (!fairy.isGuiding()) return false;
            if (target != null) {
                if (target.isAlive() && !target.isSpectator() && fairy.distanceTo(target) <= ABANDON_RANGE) {
                    return true;
                }
                clearState();
            }

            target = fairy.level().getNearestPlayer(fairy, ACQUIRE_RANGE);
            if (target == null || target.isSpectator()) {
                target = null;
                return false;
            }
            if (target instanceof ServerPlayer player) {
                AchievementUtils.awardAchievement(player, "hey_listen");
            }
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void tick() {
            if (target == null || !target.isAlive()) {
                return;
            }

            angle += fairy.getRandom1211().nextFloat() * 0.05F + 0.05F;
            Vec3 playerPos = target.position();
            if (!following) {
                moveAround(playerPos);
                return;
            }

            if (fairy.distanceTo(target) > ABANDON_RANGE) {
                clearState();
                return;
            }

            if (!isTreasureStillPresent()) {
                guidePos = null;
                if (treasureSearchCooldown > 0) {
                    --treasureSearchCooldown;
                } else {
                    guidePos = findNearestTreasure();
                    treasureSearchCooldown = TREASURE_SEARCH_INTERVAL;
                }
                /// 发现宝物的这一 tick 只更新目标，下一 tick 再开始移动。
                if (guidePos != null) {
                    return;
                }
            }

            Vec3 destination = guidePos == null
                    ? playerPos
                    : createGuideDestination(playerPos);
            moveAround(destination);
            if (guidePos != null && fairy.position().closerThan(Vec3.atCenterOf(guidePos), 3.0D)) {
                if (++arrivalTicks >= 40) fairy.discard();
            } else {
                arrivalTicks = 0;
            }
        }

        @Override
        public void stop() {
            fairy.getNavigation().stop();
            clearState();
        }

        private Vec3 createGuideDestination(Vec3 playerPos) {
            Vec3 chestCenter = Vec3.atCenterOf(guidePos);
            Vec3 delta = chestCenter.subtract(playerPos);
            double distance = delta.length();
            if (distance > ABANDON_RANGE) {
                guidePos = null;
            }
            if (distance > GUIDE_STEP) {
                return playerPos.add(delta.normalize().scale(GUIDE_STEP));
            }
            return chestCenter;
        }

        private void moveAround(Vec3 destination) {
            double distance = fairy.position().distanceTo(destination);
            Vec3 orbitPosition = destination.add(Math.sin(angle) * ORBIT_RADIUS, ORBIT_HEIGHT, Math.cos(angle) * ORBIT_RADIUS);
            fairy.getNavigation().moveTo(orbitPosition.x, orbitPosition.y, orbitPosition.z, 1.0 + distance);
            if (distance < FOLLOW_DISTANCE) {
                following = true;
            }
        }

        /// 只检查已经加载的区块，避免一只小动物在巡游时主动生成新区块。
        /// 在已加载范围内，按距离选择最近的宝箱，使多人或多宝箱场景结果稳定。
        private BlockPos findNearestTreasure() {
            BlockPos origin = fairy.blockPosition();
            int centerChunkX = origin.getX() >> 4;
            int centerChunkZ = origin.getZ() >> 4;
            BlockPos nearest = null;
            double nearestDistance = Double.MAX_VALUE;

            for (int offsetX = -CHEST_CHUNK_RADIUS; offsetX <= CHEST_CHUNK_RADIUS; offsetX++) {
                for (int offsetZ = -CHEST_CHUNK_RADIUS; offsetZ <= CHEST_CHUNK_RADIUS; offsetZ++) {
                    int chunkX = centerChunkX + offsetX;
                    int chunkZ = centerChunkZ + offsetZ;
                    BlockPos chunkProbe = new BlockPos(chunkX << 4, origin.getY(), chunkZ << 4);
                    if (!fairy.level().hasChunkAt(chunkProbe)) {
                        continue;
                    }
                    for (BlockEntity blockEntity : fairy.level()
                            .getChunk(chunkX, chunkZ)
                            .getBlockEntities()
                            .values()) {
                        if (!(blockEntity instanceof ChestBlockEntity)
                                && !(blockEntity instanceof LifeCrystalBlock.BEntity)) {
                            continue;
                        }
                        double distance = blockEntity.getBlockPos().distSqr(origin);
                        if (distance < nearestDistance) {
                            nearestDistance = distance;
                            nearest = blockEntity.getBlockPos().immutable();
                        }
                    }
                }
            }

            BlockPos blockTreasure = BlockPos.findClosestMatch(
                    origin,
                    BLOCK_TREASURE_HORIZONTAL_RANGE,
                    BLOCK_TREASURE_VERTICAL_RANGE,
                    pos -> isTaggedTreasure(fairy.level().getBlockState(pos))
            ).orElse(null);
            if (blockTreasure != null) {
                double distance = blockTreasure.distSqr(origin);
                if (distance < nearestDistance) nearest = blockTreasure.immutable();
            }
            return nearest;
        }

        private boolean isTreasureStillPresent() {
            if (guidePos == null) return false;
            BlockEntity blockEntity = fairy.level().getBlockEntity(guidePos);
            return blockEntity instanceof ChestBlockEntity
                    || blockEntity instanceof LifeCrystalBlock.BEntity
                    || isTaggedTreasure(fairy.level().getBlockState(guidePos));
        }

        private static boolean isTaggedTreasure(BlockState state) {
            return state.is(NatureBlocks.LIFE_FRUIT.get())
                    || state.is(NatureBlocks.LIFE_CRYSTAL_BLOCK.get())
                    || state.is(PortTags.Blocks.ORES_GOLD)
                    || state.is(BlockTags.GOLD_ORES)
                    || state.is(ModTags.Blocks.ORES_PLATINUM)
                    || state.is(ModTags.Blocks.ORES_COBALT)
                    || state.is(ModTags.Blocks.ORES_PALLADIUM)
                    || state.is(ModTags.Blocks.ORES_MYTHRIL)
                    || state.is(ModTags.Blocks.ORES_ORICHALCUM)
                    || state.is(ModTags.Blocks.ORES_ADAMANTITE)
                    || state.is(ModTags.Blocks.ORES_TITANIUM)
                    || state.is(OreBlocks.CHLOROPHYTE_ORE.get());
        }

        private void clearState() {
            target = null;
            guidePos = null;
            following = false;
            arrivalTicks = 0;
            treasureSearchCooldown = 0;
        }
    }

    /// 仙灵是引导实体而不是可被普通攻击清除的小动物。
    ///
    /// 仅放行带有“绕过无敌”标签的伤害；同时保留强制清除伤害的显式判断，
    /// 确保管理命令和世界清理流程仍能移除实体。
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!isGuiding()) return super.hurt(source, amount);
        boolean bypassesInvulnerability = source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) || source == damageSources().genericKill();
        return bypassesInvulnerability && super.hurt(source, amount);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, Variant.BLUE.ordinal());
        this.entityData.define(DATA_GUIDING, true);
    }

    @Override
    public Variant getVariant() {
        return Variant.BY_ID.apply(entityData.get(DATA_VARIANT));
    }

    @Override
    public void setVariant(Variant v) {this.entityData.set(DATA_VARIANT, v.ordinal());}

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        getVariant().serialize(tag);
        tag.putBoolean(GUIDING_KEY, isGuiding());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setGuiding(!tag.contains(GUIDING_KEY) || tag.getBoolean(GUIDING_KEY));
        if (!tag.contains(VARIANT_KEY)) {
            setVariant(Variant.BLUE);
            return;
        }
        PortDataResultExtension.ifSuccess(Variant.CODEC.parse(NbtOps.INSTANCE, tag.get(VARIANT_KEY)), this::setVariant);
    }

    @Override
    protected String variantSaveKey() {
        return VARIANT_KEY;
    }

    @Override
    protected void initializeSpawnVariant() {
        setVariant(SPAWN_VARIANTS.select(random));
    }

    @Override
    public ResourceLocation getModelPath() {return getVariant().modelPath();}

    @Override
    public ResourceLocation getTexturePath() {return getVariant().texturePath();}

    public enum Variant implements IVariant {
        BLUE, GREEN, PINK;

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);
        private static final IntFunction<Variant> BY_ID = ByIdMap.sparse(Variant::ordinal, values(), BLUE);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public ResourceLocation modelPath() {
            return IVariant.resource("animal/fairy");
        }

        @Override
        public ResourceLocation texturePath() {
            return IVariant.resource("textures/entity/animal/fairy/" + getSerializedName() + "_fairy.png");
        }

        @Override
        public Codec<Variant> codec() {
            return CODEC;
        }

        @Override
        public String serializeKey() {
            return VARIANT_KEY;
        }
    }
}
