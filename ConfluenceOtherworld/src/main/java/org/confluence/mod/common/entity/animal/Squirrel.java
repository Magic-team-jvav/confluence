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
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.common.init.entity.CritterEntities;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.Locale;

public class Squirrel extends BaseCritter implements VariantHolder<Squirrel.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Squirrel.class, EntityDataSerializers.INT);
    public static final String VARIANT_KEY = "Variant";
    private static final Variant[] COMMON_SPAWN_VARIANTS = {Variant.NORMAL, Variant.RED};

    public Squirrel(EntityType<? extends Squirrel> type, Level level) {
        super(type, level);
        getAttribute(PortAttributesExtension.safeFallDistance().get()).setBaseValue(6.0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(PortAttributesExtension.safeFallDistance().get(), 6.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return withPassivePanic(createGroundCritterRoutine(1.0, new VanillaGoalAction(new BreedGoal(Squirrel.this, 1.0)), new VanillaGoalAction(new FollowParentGoal(Squirrel.this, 1.25))), 2.0);
            }
        };
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, Variant.NORMAL.ordinal());
    }

    @Override
    public Variant getVariant() {
        return CritterVariantUtil.byId(Variant.values(), this.entityData.get(DATA_VARIANT), Variant.NORMAL);
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
            setVariant(Variant.NORMAL);
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
        setVariant(CritterVariantUtil.withRareVariant(random, COMMON_SPAWN_VARIANTS, Variant.GOLD));
    }

    @Override
    public ResourceLocation getModelPath() {return getVariant().modelPath();}

    @Override
    public ResourceLocation getTexturePath() {return getVariant().texturePath();}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController(this));
    }

    /// 松鼠当前没有可触发求偶的食物，但仍保留后代工厂，供命令、事件和附属模组调用。
    @Override
    public Squirrel getBreedOffspring(ServerLevel level, net.minecraft.world.entity.AgeableMob otherParent) {
        return CritterEntities.SQUIRREL.get().create(level);
    }

    public enum Variant implements IVariant {
        NORMAL, RED, GOLD,
        AMETHYST, TOPAZ, SAPPHIRE, EMERALD, RUBY, AMBER, DIAMOND;

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public ResourceLocation modelPath() {
            return IVariant.resource("animal/squirrel");
        }

        @Override
        public ResourceLocation texturePath() {
            String name = switch (this) {
                case NORMAL -> "squirrel";
                case RED -> "red_squirrel";
                case GOLD -> "golden_squirrel";
                default -> getSerializedName() + "_squirrel";
            };
            return IVariant.resource("textures/entity/animal/squirrel/" + name + ".png");
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
