package org.confluence.mod.common.entity.animal;

import com.mojang.serialization.Codec;
import java.util.Locale;
import java.util.function.IntFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.ConditionalSwitchNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.constant.DefaultAnimations;



public class Ladybug extends BaseFlyingCritter implements VariantHolder<Ladybug.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Ladybug.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_FLYING = SynchedEntityData.defineId(Ladybug.class, EntityDataSerializers.BOOLEAN);
    public static final String VARIANT_KEY = "Variant";
    private static final String FLYING_KEY = "Flying";
    private static final String PHASE_TICKS_KEY = "PhaseTicks";
    private static final VariantSpawnProfile<Variant> SPAWN_VARIANTS = VariantSpawnProfile.<Variant>builder()
            .add(Variant.RED, 399)
            .add(Variant.GOLD, 1)
            .build();
    private int phaseTicks = 40;

    public Ladybug(EntityType<? extends Ladybug> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, Variant.RED.ordinal());
        builder.define(DATA_FLYING, true);
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
        tag.putBoolean(FLYING_KEY, isFlyingPhase());
        tag.putInt(PHASE_TICKS_KEY, phaseTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (!tag.contains(VARIANT_KEY)) {
            setVariant(Variant.RED);
        } else
            Variant.CODEC.parse(NbtOps.INSTANCE, tag.get(VARIANT_KEY)).ifSuccess(this::setVariant);
        setFlyingPhase(!tag.contains(FLYING_KEY) || tag.getBoolean(FLYING_KEY));
        phaseTicks = tag.contains(PHASE_TICKS_KEY) ? Math.max(1, tag.getInt(PHASE_TICKS_KEY)) : 40;
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
    protected BTRoot createBT() {
        BTNode routine = new ConditionalSwitchNode(
                this::isFlyingPhase,
                new VanillaGoalAction(new WaterAvoidingRandomFlyingGoal(this, 0.8D)),
                new VanillaGoalAction(new WaterAvoidingRandomStrollGoal(this, 0.55D))
        );
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return withPassivePanic(routine, 1.0D);
            }
        };
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (isFlyingPhase()) {
            if (--phaseTicks <= 0) {
                setFlyingPhase(false);
                phaseTicks = 40 + random.nextInt(61);
            }
        } else if (onGround() && --phaseTicks <= 0) {
            setFlyingPhase(true);
            phaseTicks = 30 + random.nextInt(51);
            setDeltaMovement(getDeltaMovement().add(0.0, 0.18, 0.0));
        }
    }

    private boolean isFlyingPhase() {
        return entityData.get(DATA_FLYING);
    }

    private void setFlyingPhase(boolean flying) {
        entityData.set(DATA_FLYING, flying);
        setNoGravity(flying);
        if (!flying) getNavigation().stop();
    }

    @Override
    public ResourceLocation getModelPath() {return getVariant().modelPath();}

    @Override
    public ResourceLocation getTexturePath() {return getVariant().texturePath();}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Fly", 0, state -> state.setAndContinue(DefaultAnimations.FLY)));
    }

    public enum Variant implements IVariant {
        RED, GOLD;

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);
        private static final IntFunction<Variant> BY_ID = ByIdMap.sparse(Variant::ordinal, values(), RED);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public ResourceLocation modelPath() {
            return IVariant.resource("animal/ladybug");
        }

        @Override
        public ResourceLocation texturePath() {
            String name = this == GOLD ? "gold_ladybug" : "ladybug";
            return IVariant.resource("textures/entity/animal/ladybug/" + name + ".png");
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
