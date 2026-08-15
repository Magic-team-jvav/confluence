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
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.advancement.AchievementAwardService;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.ConditionalSwitchNode;
import org.confluence.mod.common.init.entity.CritterEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.Locale;

public class Fairy extends Bird implements VariantHolder<Fairy.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Fairy.class, EntityDataSerializers.INT);
    public static final String VARIANT_KEY = "Variant";
    private static final Variant[] SPAWN_VARIANTS = Variant.values();

    public Fairy(EntityType<? extends Fairy> type, Level level) {
        super(type, level);
        /*
         * 仙灵需要跨越普通方块把玩家引向宝箱。该标记只改变实体碰撞，
         * 导航目标、脱离距离和移动节奏仍由行为树负责。
         */
        this.noPhysics = true;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingCritter.createFlyingCritterAttributes();
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                FairyGuideAction guide = new FairyGuideAction(Fairy.this);
                /*
                 * 引导玩家是妖精的最高优先级行为。实时分支可以在玩家进入十格范围的
                 * 当个行为 tick 中打断巡游，也会在玩家死亡或离开三十格后立即恢复巡游。
                 */
                return withPassivePanic(
                        new ConditionalSwitchNode(
                                guide::canGuidePlayer,
                                guide,
                                createBirdDailyRoutine()),
                        1.25);
            }
        };
    }

    /**
     * 实现妖精“发现玩家、建立跟随、寻找宝箱并带路”的完整状态机。
     *
     * <p>动作以十格为首次发现范围；玩家靠近到三格内后，妖精进入持续引导状态，
     * 此后允许双方拉开到三十格。宝箱搜索范围与 1.21 实现一致，为妖精所在区块
     * 周围一圈区块。若宝箱距离玩家超过十格，当前导航点会限制在玩家前方十格，
     * 从而让妖精逐段带路，而不是直接飞走。</p>
     */
    private static final class FairyGuideAction extends BTNode {
        private static final double ACQUIRE_RANGE = 10.0;
        private static final double ABANDON_RANGE = 30.0;
        private static final double FOLLOW_DISTANCE = 3.0;
        private static final double GUIDE_STEP = 10.0;
        private static final double ORBIT_RADIUS = 3.0;
        private static final double ORBIT_HEIGHT = 3.0;
        private static final int CHEST_CHUNK_RADIUS = 1;

        private final Fairy fairy;
        private Player target;
        private BlockPos guidePos;
        private boolean following;
        private float angle;

        private FairyGuideAction(Fairy fairy) {
            this.fairy = fairy;
        }

        /**
         * 供实时分支每 tick 判断是否需要占用移动控制。
         *
         * <p>首次进入时只接纳十格内玩家；一旦完成近距离接触，则沿用同一玩家，
         * 直到玩家死亡或离开三十格，避免引导途中在多个玩家之间来回切换。</p>
         */
        private boolean canGuidePlayer() {
            if (target != null) {
                if (target.isAlive()
                        && !target.isSpectator()
                        && fairy.distanceTo(target) <= ABANDON_RANGE) {
                    return true;
                }
                clearState();
            }

            target = fairy.level().getNearestPlayer(fairy, ACQUIRE_RANGE);
            if (target == null || target.isSpectator()) {
                target = null;
                return false;
            }
            if (fairy.getType() == CritterEntities.FAIRY.get()
                    && target instanceof ServerPlayer serverPlayer) {
                awardEncounterAchievement(serverPlayer);
            }
            return true;
        }

        @Override
        public BTStatus execute() {
            if (target == null || !target.isAlive()) {
                return BTStatus.FAILURE;
            }

            angle += fairy.getRandom().nextFloat() * 0.05F + 0.05F;
            Vec3 playerPos = target.position();
            if (!following) {
                moveAround(playerPos);
                return BTStatus.RUNNING;
            }

            if (fairy.distanceTo(target) > ABANDON_RANGE) {
                clearState();
                return BTStatus.FAILURE;
            }

            if (!isChestStillPresent()) {
                guidePos = findNearestChest();
                /*
                 * 发现宝箱的这一 tick 只更新目标，下一 tick 再开始移动。
                 * 这样可以保持两侧实现相同的状态切换节奏。
                 */
                if (guidePos != null) {
                    return BTStatus.RUNNING;
                }
            }

            Vec3 destination = guidePos == null
                    ? playerPos
                    : createGuideDestination(playerPos);
            moveAround(destination);
            return BTStatus.RUNNING;
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
            Vec3 orbitPosition = destination.add(
                    Math.sin(angle) * ORBIT_RADIUS,
                    ORBIT_HEIGHT,
                    Math.cos(angle) * ORBIT_RADIUS);
            fairy.getNavigation().moveTo(
                    orbitPosition.x,
                    orbitPosition.y,
                    orbitPosition.z,
                    1.0 + distance);
            if (distance < FOLLOW_DISTANCE) {
                following = true;
            }
        }

        /**
         * 只检查已经加载的区块，避免一只小动物在巡游时主动生成新区块。
         * 在已加载范围内，按距离选择最近的宝箱，使多人或多宝箱场景结果稳定。
         */
        private BlockPos findNearestChest() {
            BlockPos origin = fairy.blockPosition();
            int centerChunkX = origin.getX() >> 4;
            int centerChunkZ = origin.getZ() >> 4;
            BlockPos nearest = null;
            double nearestDistance = Double.MAX_VALUE;

            for (int offsetX = -CHEST_CHUNK_RADIUS;
                 offsetX <= CHEST_CHUNK_RADIUS;
                 offsetX++) {
                for (int offsetZ = -CHEST_CHUNK_RADIUS;
                     offsetZ <= CHEST_CHUNK_RADIUS;
                     offsetZ++) {
                    int chunkX = centerChunkX + offsetX;
                    int chunkZ = centerChunkZ + offsetZ;
                    BlockPos chunkProbe = new BlockPos(
                            chunkX << 4, origin.getY(), chunkZ << 4);
                    if (!fairy.level().hasChunkAt(chunkProbe)) {
                        continue;
                    }
                    for (BlockEntity blockEntity : fairy.level()
                            .getChunk(chunkX, chunkZ)
                            .getBlockEntities()
                            .values()) {
                        if (!(blockEntity instanceof ChestBlockEntity)) {
                            continue;
                        }
                        double distance = blockEntity.getBlockPos()
                                .distSqr(origin);
                        if (distance < nearestDistance) {
                            nearestDistance = distance;
                            nearest = blockEntity.getBlockPos().immutable();
                        }
                    }
                }
            }
            return nearest;
        }

        private boolean isChestStillPresent() {
            return guidePos != null
                    && fairy.level().getBlockEntity(guidePos)
                    instanceof ChestBlockEntity;
        }

        private void clearState() {
            target = null;
            guidePos = null;
            following = false;
        }
    }

    /**
     * 妖精在服务端确认首次发现玩家后结算相遇成就。
     */
    static AchievementAwardService.Result awardEncounterAchievement(
            ServerPlayer player
    ) {
        return AchievementAwardService.award(player, "hey_listen");
    }

    /**
     * 仙灵是引导实体而不是可被普通攻击清除的小动物。
     *
     * <p>仅放行带有“绕过无敌”标签的伤害；同时保留强制清除伤害的显式判断，
     * 确保管理命令和世界清理流程仍能移除实体。</p>
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean bypassesInvulnerability =
                source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                        || source == damageSources().genericKill();
        return bypassesInvulnerability && super.hurt(source, amount);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, Variant.BLUE.ordinal());
    }

    @Override
    public Variant getVariant() {
        return CritterVariantUtil.byId(
                Variant.values(), this.entityData.get(DATA_VARIANT), Variant.BLUE);
    }

    @Override
    public void setVariant(Variant v) {this.entityData.set(DATA_VARIANT, v.ordinal());}

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        getVariant().serialize(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
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
        setVariant(CritterVariantUtil.uniform(random, SPAWN_VARIANTS));
    }

    @Override
    public ResourceLocation getModelPath() {return getVariant().modelPath();}

    @Override
    public ResourceLocation getTexturePath() {return getVariant().texturePath();}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        registerFlyOnlyController(controllers);
    }

    public enum Variant implements IVariant {
        BLUE, GREEN, PINK;

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

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
            return IVariant.resource("textures/entity/animal/fairy/"
                    + getSerializedName() + "_fairy.png");
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
