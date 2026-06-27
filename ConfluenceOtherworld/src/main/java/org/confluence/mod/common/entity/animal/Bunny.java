package org.confluence.mod.common.entity.animal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.PlayerCloseCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.PanicFleeAction;
import org.confluence.mod.common.entity.ai.bt.leaf.RandomStrollAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Locale;

public class Bunny extends BaseCritter implements VariantHolder<Bunny.Variant> {

    public enum Variant implements StringRepresentable {
        NORMAL, GOLD, PARTY, SLIMED, XMAS,
        AMETHYST, TOPAZ, SAPPHIRE, EMERALD, RUBY, AMBER, DIAMOND,
        CORRUPT, VICIOUS, EXPLOSIVE;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public ResourceLocation modelPath() {
            String id = this == EXPLOSIVE ? "explosive_bunny" : "bunny";
            return Confluence.asResource("animal/" + id);
        }

        public ResourceLocation texturePath() {
            String name = this == NORMAL ? "bunny" : getSerializedName() + "_bunny";
            return Confluence.asResource("textures/entity/animal/bunny/" + name);
        }
    }

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(Bunny.class, EntityDataSerializers.INT);
    private static final RawAnimation WATCH_1 = RawAnimation.begin().thenPlay("watch_1");
    private static final RawAnimation WATCH_2 = RawAnimation.begin().thenPlay("watch_2");

    public Bunny(EntityType<? extends Bunny> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseCritter.createCritterAttributes().add(Attributes.JUMP_STRENGTH, 0.6);
    }

    @Override
    protected BTRoot createBT() {
        BTNode tree = SelectorNode.of(
                SequenceNode.of(new PlayerCloseCondition(this, 5.0), new PanicFleeAction(this, 1.5)),
                SequenceNode.of(new WaitAction(20 + random.nextInt(60)), new RandomStrollAction(this, 0.8, 7))
        );
        return new BTRoot() {
            @Override
            protected BTNode createTree() { return tree; }
        };
    }

    @Override
    public Variant getVariant() {
        return Variant.values()[this.entityData.get(DATA_VARIANT)];
    }

    @Override
    public void setVariant(Variant variant) {
        this.entityData.set(DATA_VARIANT, variant.ordinal());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, Variant.NORMAL.ordinal());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Variant", getVariant().getSerializedName());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(parseVariant(tag.getString("Variant")));
    }

    private static Variant parseVariant(String name) {
        for (Variant v : Variant.values()) {
            if (v.getSerializedName().equals(name)) return v;
        }
        return Variant.NORMAL;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle/Move", 5, state -> {
            if (state.isMoving()) return state.setAndContinue(DefaultAnimations.WALK);
            return state.setAndContinue(state.getAnimatable().getRandom().nextBoolean() ? WATCH_1 : WATCH_2);
        }));
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide && getVariant() == Variant.EXPLOSIVE) {
            level().explode(this, getX(), getY(), getZ(), 3.0F, Level.ExplosionInteraction.NONE);
        }
        super.die(source);
    }
}
