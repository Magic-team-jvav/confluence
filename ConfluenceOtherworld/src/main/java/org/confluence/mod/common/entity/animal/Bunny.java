package org.confluence.mod.common.entity.animal;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.init.entity.CritterEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Locale;

public class Bunny extends BaseCritter implements VariantHolder<Bunny.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Bunny.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_WATCH_STATE =
            SynchedEntityData.defineId(
                    Bunny.class, EntityDataSerializers.INT);
    public static final String VARIANT_KEY = "Variant";
    private static final RawAnimation WATCH_1 = RawAnimation.begin().thenPlay("watch_1");
    private static final RawAnimation WATCH_2 = RawAnimation.begin().thenPlay("watch_2");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final Variant[] COMMON_SPAWN_VARIANTS = {Variant.NORMAL};
    private int idleTicks;
    private int nextWatchTick;
    private int watchTicksRemaining = 20;
    private int watchAnimationType;

    public Bunny(EntityType<? extends Bunny> type, Level level) {
        super(type, level);
        getAttribute(PortAttributesExtension
                .jumpStrength()
                .value()).setBaseValue(0.6);
        getAttribute(PortAttributesExtension
                .safeFallDistance()
                .value()).setBaseValue(6.0);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.moveControl = new BunnyHopMoveControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Rabbit.createAttributes()
                .add(PortAttributesExtension
                        .jumpStrength()
                        .value(), 0.6)
                .add(PortAttributesExtension
                        .safeFallDistance()
                        .value(), 6.0);
    }

    @Override
    protected BTRoot createBT() {
        BTNode tree = withPassivePanic(
                SelectorNode.of(
                        new VanillaGoalAction(
                                new FloatGoal(this)),
                        new VanillaGoalAction(
                                new ClimbOnTopOfPowderSnowGoal(
                                        this, level())),
                        new VanillaGoalAction(
                                new BreedGoal(this, 0.8)),
                        new VanillaGoalAction(
                                new TemptGoal(
                                        this,
                                        1.0,
                                        Ingredient.of(
                                                Items.CARROT,
                                                Items.GOLDEN_CARROT,
                                                Items.DANDELION),
                                        false)),
                        new VanillaGoalAction(
                                new AvoidEntityGoal<>(
                                        this,
                                        Player.class,
                                        8.0F,
                                        2.2,
                                        2.2)),
                        new VanillaGoalAction(
                                new AvoidEntityGoal<>(
                                        this,
                                        Wolf.class,
                                        10.0F,
                                        2.2,
                                        2.2)),
                        new VanillaGoalAction(
                                new AvoidEntityGoal<>(
                                        this,
                                        Monster.class,
                                        4.0F,
                                        2.2,
                                        2.2)),
                        new VanillaGoalAction(
                                new WaterAvoidingRandomStrollGoal(
                                        this,
                                        0.6)),
                        new VanillaGoalAction(
                                new LookAtPlayerGoal(
                                        this,
                                        Player.class,
                                        10.0F))),
                2.2);
        return new BTRoot() {
            @Override
            protected BTNode createTree() { return tree; }
        };
    }

    @Override
    public Variant getVariant() {
        return CritterVariantUtil.byId(Variant.values(), this.entityData.get(DATA_VARIANT), Variant.NORMAL);
    }

    @Override
    public void setVariant(Variant variant) {
        this.entityData.set(DATA_VARIANT, variant.ordinal());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, Variant.NORMAL.ordinal());
        this.entityData.define(DATA_WATCH_STATE, 20);
    }

    /**
     * 保持 1.21 的兔子待机节奏：导航结束后累计空闲时间，每一百刻安排一次短暂观察。
     *
     * <p>动画类型和剩余时长由服务端一次性确定，再通过同步数据发送给客户端。客户端只
     * 递减本地剩余时间，不会在每一帧重新随机，因此同一次观察动作始终使用同一动画。</p>
     */
    @Override
    public void tick() {
        super.tick();
        --watchTicksRemaining;
        if (level().isClientSide) {
            return;
        }

        if (!navigation.isDone()) {
            idleTicks = 0;
        } else {
            ++idleTicks;
            if (idleTicks % 100 == 99) {
                nextWatchTick = tickCount + random.nextInt(20);
            }
            if (tickCount == nextWatchTick) {
                beginWatchCycle(50, random.nextInt(2));
            }
        }
        if (watchTicksRemaining >= 0) {
            navigation.stop();
        }
    }

    /**
     * 以一个整数同步观察动画类型和持续时间，避免维护两份可能不同步的实体数据。
     */
    void beginWatchCycle(int duration, int animationType) {
        int normalizedDuration = Math.max(0, Math.min(duration, 999));
        int normalizedType = Mth.clamp(animationType, 0, 1);
        entityData.set(
                DATA_WATCH_STATE,
                normalizedType * 1000 + normalizedDuration);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (DATA_WATCH_STATE.equals(key)) {
            int packedState = entityData.get(DATA_WATCH_STATE);
            watchAnimationType = packedState / 1000;
            watchTicksRemaining = packedState % 1000;
        }
    }

    public int getWatchTicksRemaining() {
        return watchTicksRemaining;
    }

    public int getWatchAnimationType() {
        return watchAnimationType;
    }

    /**
     * 将普通地面导航转换为兔子式间歇跳跃；空中继续沿当前目标推进，落地后短暂停顿。
     */
    static final class BunnyHopMoveControl extends MoveControl {
        private final Bunny bunny;
        private int jumpDelay;

        BunnyHopMoveControl(Bunny bunny) {
            super(bunny);
            this.bunny = bunny;
        }

        @Override
        public void tick() {
            double deltaX = wantedX - bunny.getX();
            double deltaY = wantedY - bunny.getY();
            double deltaZ = wantedZ - bunny.getZ();
            double distanceSquared =
                    deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
            if (operation != Operation.MOVE_TO
                    || distanceSquared < MIN_SPEED_SQR) {
                bunny.setXxa(0.0F);
                bunny.setZza(0.0F);
                bunny.setSpeed(0.0F);
                return;
            }

            float targetYaw = (float) (Mth.atan2(deltaZ, deltaX)
                    * Mth.RAD_TO_DEG) - 90.0F;
            bunny.setYRot(rotlerp(
                    bunny.getYRot(), targetYaw, 90.0F));
            bunny.yHeadRot = bunny.getYRot();
            bunny.yBodyRot = bunny.getYRot();
            bunny.setSpeed((float) (speedModifier
                    * bunny.getAttributeValue(
                    Attributes.MOVEMENT_SPEED)));

            if (!bunny.onGround()) {
                return;
            }
            bunny.setJumping(false);
            if (jumpDelay-- > 0) {
                bunny.setXxa(0.0F);
                bunny.setZza(0.0F);
                bunny.setSpeed(0.0F);
                return;
            }

            jumpDelay = 10;
            bunny.getJumpControl().jump();
            double horizontalDistance =
                    Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            if (horizontalDistance > 1.0E-5) {
                bunny.addDeltaMovement(
                        new net.minecraft.world.phys.Vec3(
                                deltaX / horizontalDistance * 0.1,
                                0.0,
                                deltaZ / horizontalDistance * 0.1));
            }
            bunny.setJumping(true);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        getVariant().serialize(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (!tag.contains(VARIANT_KEY)) {
            setVariant(Variant.NORMAL);
            return;
        }
        PortDataResultExtension.ifSuccess(Variant.CODEC.decode(NbtOps.INSTANCE, tag.get(VARIANT_KEY)), p -> setVariant(p.getFirst()));
    }

    @Override
    protected String variantSaveKey() {
        return VARIANT_KEY;
    }

    @Override
    protected void initializeSpawnVariant() {
        setVariant(CritterVariantUtil.withRareVariant(
                random, COMMON_SPAWN_VARIANTS, Variant.GOLD));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle/Move", 5, state -> {
            if (watchTicksRemaining > 0) {
                return state.setAndContinue(
                        watchAnimationType == 0 ? WATCH_1 : WATCH_2);
            }
            return state.setAndContinue(
                    state.isMoving()
                            ? WALK
                            : IDLE);
        }));
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.CARROT)
                || stack.is(Items.GOLDEN_CARROT)
                || stack.is(Items.DANDELION);
    }

    @Override
    public Bunny getBreedOffspring(
            ServerLevel level,
            net.minecraft.world.entity.AgeableMob otherParent) {
        return CritterEntities.BUNNY.get().create(level);
    }

    public enum Variant implements IVariant {
        NORMAL, GOLD, PARTY, SLIMED, XMAS,
        AMETHYST, TOPAZ, SAPPHIRE, EMERALD, RUBY, AMBER, DIAMOND,
        CORRUPT, VICIOUS, EXPLOSIVE;

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public ResourceLocation modelPath() {
            String id = this == EXPLOSIVE ? "explosive_bunny" : "bunny";
            return IVariant.resource("animal/" + id);
        }

        @Override
        public ResourceLocation texturePath() {
            String name = switch (this) {
                case NORMAL, PARTY, SLIMED, XMAS, CORRUPT, VICIOUS -> "bunny";
                case GOLD -> "golden_bunny";
                case EXPLOSIVE -> "explosive_bunny";
                default -> getSerializedName() + "_bunny";
            };
            return IVariant.resource("textures/entity/animal/bunny/" + name + ".png");
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
