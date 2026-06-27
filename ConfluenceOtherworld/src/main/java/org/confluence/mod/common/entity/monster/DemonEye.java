package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.condition.IsDaytimeCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.ChargeAttackAction;
import org.confluence.mod.common.entity.ai.bt.leaf.CircleAroundTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Locale;

public class DemonEye extends BaseFlyingMonster implements VariantHolder<DemonEye.Variant> {

    public enum Variant implements StringRepresentable {
        NORMAL, NORMAL_BIG,
        CATARACT, CATARACT_BIG,
        SLEEPY, SLEEPY_BIG,
        DILATED, DILATED_SMALL,
        GREEN, GREEN_SMALL,
        PURPLE, PURPLE_BIG,
        OWL, SPACESHIP;

        @Override
        public String getSerializedName() { return name().toLowerCase(Locale.ROOT); }

        public boolean isBig() {
            return name().endsWith("_BIG");
        }

        public boolean isSmall() {
            return name().endsWith("_SMALL");
        }

        public float scale() {
            return isBig() ? 1.3F : isSmall() ? 0.7F : 1.0F;
        }

        public int textureIndex() {
            return ordinal() / 2;
        }

        public ResourceLocation modelPath() {
            return Confluence.asResource("monster/demon_eye");
        }

        public ResourceLocation texturePath() {
            String[] names = {"normal", "cataract", "sleepy", "dilated", "green", "purple", "owl", "spaceship"};
            return Confluence.asResource("textures/entity/demon_eye/" + names[textureIndex()] + ".png");
        }

        public ResourceLocation animationPath() {
            return Confluence.asResource("animations/entity/demon_eye");
        }
    }

    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(DemonEye.class, EntityDataSerializers.INT);
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");

    public DemonEye(EntityType<? extends DemonEye> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes()
                .add(Attributes.ATTACK_DAMAGE, 18.0);
    }

    @Override
    protected BTRoot createBT() {
        BTNode combat = SelectorNode.of(
                SequenceNode.of(new CircleAroundTargetAction(this, 0.3, 4), new WaitAction(20)),
                SequenceNode.of(new ChargeAttackAction(this, 0.5)));

        BTNode night = SelectorNode.of(
                SequenceNode.of(new HasTargetCondition(this), combat),
                new FlyWanderAction(this, 0.15, 10));

        BTNode root = SelectorNode.of(
                SequenceNode.of(new IsDaytimeCondition(this), new FlyWanderAction(this, 0.3, 20)),
                night);

        return new BTRoot() {
            @Override
            protected BTNode createTree() { return root; }
        };
    }

    @Override
    public Variant getVariant() { return Variant.values()[this.entityData.get(DATA_VARIANT)]; }

    @Override
    public void setVariant(Variant variant) { this.entityData.set(DATA_VARIANT, variant.ordinal()); }

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
        controllers.add(new AnimationController<>(this, "Fly", 0, state -> state.setAndContinue(FLY)));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && getTarget() == null && tickCount % 40 == 0) {
            setTarget(level().getNearestPlayer(this, 40));
        }
    }
}
