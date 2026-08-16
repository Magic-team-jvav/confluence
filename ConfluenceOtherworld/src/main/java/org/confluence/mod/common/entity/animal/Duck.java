package org.confluence.mod.common.entity.animal;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import PortLib.extensions.net.minecraft.world.entity.ai.attributes.Attributes.PortAttributesExtension;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.init.entity.CritterEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import javax.annotation.Nullable;
import java.util.Locale;

/// 同时支持陆地行走和水面活动的鸭子。
///
/// <p>鸭子的外观、食物、后代工厂、落水表现和动画选择均由实体自身负责；
/// 通用小动物基类只提供行为树生命周期和默认的不可繁殖契约。</p>
public class Duck extends BaseCritter
        implements VariantHolder<Duck.Variant> {
    public static final String VARIANT_KEY = "Variant";
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(
                    Duck.class,
                    EntityDataSerializers.INT);
    private int eggLayTime = random.nextInt(6000) + 6000;

    public Duck(EntityType<? extends Duck> type, Level level) {
        super(type, level);
        getAttribute(PortAttributesExtension.waterMovementEfficiency().get()).setBaseValue(1.0);
        setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Chicken.createAttributes()
                .add(PortAttributesExtension.waterMovementEfficiency().get(), 1.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return withPassivePanic(
                        createGroundCritterRoutine(
                                1.0,
                                new VanillaGoalAction(
                                        new BreedGoal(Duck.this, 1.0)),
                                new VanillaGoalAction(
                                        new TemptGoal(
                                                Duck.this,
                                                1.0,
                                                Ingredient.of(
                                                        Items.WHEAT_SEEDS,
                                                        Items.MELON_SEEDS,
                                                        Items.PUMPKIN_SEEDS,
                                                        Items.BEETROOT_SEEDS,
                                                        Items.TORCHFLOWER_SEEDS,
                                                        Items.PITCHER_POD),
                                                false)),
                                new VanillaGoalAction(
                                        new FollowParentGoal(
                                                Duck.this,
                                                1.1))),
                        1.4);
            }
        };
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_VARIANT, Variant.MALLARD.ordinal());
    }

    @Override
    public Variant getVariant() {
        return CritterVariantUtil.byId(
                Variant.values(),
                entityData.get(DATA_VARIANT),
                Variant.MALLARD);
    }

    @Override
    public void setVariant(Variant variant) {
        entityData.set(DATA_VARIANT, variant.ordinal());
    }

    @Override
    protected String variantSaveKey() {
        return VARIANT_KEY;
    }

    @Override
    protected void initializeSpawnVariant() {
        Variant[] variants = Variant.values();
        setVariant(variants[random.nextInt(variants.length)]);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        getVariant().serialize(tag);
        tag.putInt("EggLayTime", eggLayTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (!tag.contains(VARIANT_KEY)) {
            setVariant(Variant.MALLARD);
        } else {
            PortDataResultExtension.ifSuccess(
                    Variant.CODEC.parse(
                            NbtOps.INSTANCE,
                            tag.get(VARIANT_KEY)),
                    this::setVariant);
        }
        if (tag.contains("EggLayTime")) {
            eggLayTime = tag.getInt("EggLayTime");
        }
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ItemTags.FISHES);
    }

    @Nullable
    @Override
    public Duck getBreedOffspring(
            ServerLevel level,
            net.minecraft.world.entity.AgeableMob otherParent) {
        return CritterEntities.DUCK.get().create(level);
    }

    @Override
    public boolean causeFallDamage(
            float fallDistance,
            float multiplier,
            DamageSource source) {
        return false;
    }

    /// 鸭子保留 1.21 中从鸡继承的声音音量。
    @Override
    protected float getSoundVolume() {
        return 0.2F;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && isInWater() && tickCount % 16 == 0) {
            level().addParticle(
                    ParticleTypes.BUBBLE_POP,
                    getX(),
                    getY(),
                    getZ(),
                    0.0,
                    0.0,
                    0.0);
        }
    }

    /// 还原 1.21 中通过鸡基类继承的空中缓降与成年产蛋行为。
    @Override
    public void aiStep() {
        super.aiStep();
        Vec3 movement = getDeltaMovement();
        if (!onGround() && movement.y < 0.0) {
            setDeltaMovement(movement.multiply(1.0, 0.6, 1.0));
        }
        if (!level().isClientSide
                && isAlive()
                && !isBaby()
                && --eggLayTime <= 0) {
            playSound(
                    SoundEvents.CHICKEN_EGG,
                    1.0F,
                    (random.nextFloat() - random.nextFloat())
                            * 0.2F + 1.0F);
            spawnAtLocation(Items.EGG);
            gameEvent(GameEvent.ENTITY_PLACE);
            eggLayTime = random.nextInt(6000) + 6000;
        }
    }

    @Override
    public ResourceLocation getModelPath() {
        return getVariant().modelPath();
    }

    @Override
    public ResourceLocation getTexturePath() {
        return getVariant().texturePath();
    }

    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "Swim/Idle/Move",
                5,
                state -> {
                    if (isInWater()) {
                        return state.setAndContinue(SWIM);
                    }
                    return state.setAndContinue(
                            state.isMoving()
                                    ? WALK
                                    : IDLE);
                }));
    }

    /// 鸭子的两种基础外观。枚举同时承担同步值、持久化值和纹理路径的解析。
    public enum Variant implements IVariant {
        MALLARD("duck_1"),
        COMMON("duck_2");

        public static final Codec<Variant> CODEC =
                StringRepresentable.fromEnum(Variant::values);
        private final String textureName;

        Variant(String textureName) {
            this.textureName = textureName;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public ResourceLocation modelPath() {
            return IVariant.resource("animal/duck");
        }

        @Override
        public ResourceLocation texturePath() {
            return IVariant.resource(
                    "textures/entity/animal/duck/"
                            + textureName
                            + ".png");
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
