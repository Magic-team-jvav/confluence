package org.confluence.mod.common.entity.monster;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
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

public class DemonEye extends BaseFlyingMonster implements VariantHolder<DemonEye.Variant> {
    public static final String VARIANT_KEY = "Variant";
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(DemonEye.class, EntityDataSerializers.INT);
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");

    public DemonEye(EntityType<? extends DemonEye> type, Level level) {
        this(type, level, Variant.NORMAL);
    }

    public DemonEye(EntityType<? extends DemonEye> type, Level level, Variant variant) {
        super(type, level);
        setVariant(variant);
    }

    private void applyVariantStats(Variant v) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(v.health);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(v.damage);
        this.getAttribute(Attributes.ARMOR).setBaseValue(v.armor);
        this.setHealth(this.getMaxHealth());
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
    public Variant getVariant() {
        return Variant.values()[this.entityData.get(DATA_VARIANT)];
    }

    @Override
    public void setVariant(Variant variant) {
        this.entityData.set(DATA_VARIANT, variant.ordinal());
        applyVariantStats(variant);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, Variant.NORMAL.ordinal());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        PortDataResultExtension.ifSuccess(Variant.CODEC.encodeStart(NbtOps.INSTANCE, getVariant()), t -> tag.put(VARIANT_KEY, t));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        PortDataResultExtension.ifSuccess(Variant.CODEC.parse(NbtOps.INSTANCE, tag.get(VARIANT_KEY)), this::setVariant);
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

    public enum Variant implements StringRepresentable {
        NORMAL("normal", 15.0, 3.5, 1, 1.0F),
        NORMAL_BIG("normal_big", 12.0, 4.0, 2, 1.3F),
        CATARACT("cataract", 11.5, 3.5, 2, 1.0F),
        CATARACT_BIG("cataract_big", 14.0, 4.0, 2, 1.3F),
        SLEEPY("sleepy", 15.0, 3.0, 1, 1.0F),
        SLEEPY_BIG("sleepy_big", 16.0, 3.5, 1, 1.3F),
        DILATED("dilated", 12.0, 3.5, 1, 1.0F),
        DILATED_SMALL("dilated_small", 11.5, 3.0, 0, 0.7F),
        GREEN("green", 15.0, 4.0, 0, 1.0F),
        GREEN_SMALL("green_small", 12.5, 3.0, 0, 0.7F),
        PURPLE("purple", 15.0, 3.0, 2, 1.0F),
        PURPLE_BIG("purple_big", 16.0, 3.0, 2, 1.3F),
        OWL("owl", 18.5, 3.0, 3, 1.0F),
        SPACESHIP("spaceship", 15.0, 3.0, 2, 1.0F);

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        private final String name;
        public final double health;
        public final double damage;
        public final int armor;
        private final float scale;

        Variant(String name, double health, double damage, int armor, float scale) {
            this.name = name;
            this.health = health;
            this.damage = damage;
            this.armor = armor;
            this.scale = scale;
        }

        @Override
        public String getSerializedName() { return name; }

        public float scale() { return scale; }

        public int textureIndex() { return ordinal() / 2; }

        public ResourceLocation modelPath() { return Confluence.asResource("monster/demon_eye"); }

        public ResourceLocation texturePath() {
            String[] names = {"normal", "cataract", "sleepy", "dilated", "green", "purple", "owl", "spaceship"};
            return Confluence.asResource("textures/entity/demon_eye/" + names[textureIndex()] + ".png");
        }

        public ResourceLocation animationPath() { return Confluence.asResource("animations/entity/demon_eye"); }

        public static Variant random(RandomSource random) {
            if (random.nextInt(50) == 0) return random.nextBoolean() ? OWL : SPACESHIP;
            return values()[random.nextInt(12)];
        }
    }
}
