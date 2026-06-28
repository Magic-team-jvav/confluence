package org.confluence.mod.common.entity.monster.humanoid;

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
import org.confluence.mod.common.entity.ai.bt.leaf.MeleeAttackAction;
import org.confluence.mod.common.entity.ai.bt.leaf.MoveToTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.RandomStrollAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class Zombie extends BaseHumanoidMonster implements VariantHolder<Zombie.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.INT);
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");

    public Zombie(EntityType<? extends Zombie> type, Level level) {
        this(type, level, Variant.NORMAL);
    }

    public Zombie(EntityType<? extends Zombie> type, Level level, Variant variant) {
        super(type, level);
        setVariant(variant);
    }

    private void applyVariantStats(Variant v) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(v.health);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(v.damage);
        this.getAttribute(Attributes.ARMOR).setBaseValue((double) v.armor);
        this.setHealth(this.getMaxHealth());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createHumanoidAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        SequenceNode.of(
                                new HasTargetCondition(Zombie.this),
                                new MoveToTargetAction(Zombie.this, 1.0, 2.0),
                                new MeleeAttackAction(Zombie.this, 2.5)
                        ),
                        SequenceNode.of(
                                new WaitAction(20 + random.nextInt(40)),
                                new RandomStrollAction(Zombie.this, 0.8, 10)
                        )
                );
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
        return Variant.values()[this.entityData.get(DATA_VARIANT)];
    }

    @Override
    public void setVariant(Variant variant) {
        this.entityData.set(DATA_VARIANT, variant.ordinal());
        applyVariantStats(variant);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        PortDataResultExtension.ifSuccess(
                Variant.CODEC.encodeStart(NbtOps.INSTANCE, getVariant()),
                t -> tag.put("Variant", t));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        PortDataResultExtension.ifSuccess(
                Variant.CODEC.parse(NbtOps.INSTANCE, tag.get("Variant")),
                this::setVariant);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk", 0, state -> state.setAndContinue(WALK)));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide && !isPersistenceRequired()
                && level().isDay() && level().canSeeSky(blockPosition())) {
            discard();
        }
    }

    public enum Variant implements StringRepresentable {
        NORMAL("normal", 20.0, 4.0, 2),
        BALD("bald", 18.0, 4.5, 1),
        PINCUSHION("pincushion", 22.0, 5.0, 3),
        SLIMED("slimed", 18.0, 3.5, 2),
        SWAMP("swamp", 20.0, 3.5, 3),
        TWIGGY("twiggy", 20.0, 5.0, 1);

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        private final String name;
        public final double health;
        public final double damage;
        public final int armor;

        Variant(String name, double health, double damage, int armor) {
            this.name = name;
            this.health = health;
            this.damage = damage;
            this.armor = armor;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public ResourceLocation modelPath() {
            return Confluence.asResource("monster/zombie");
        }

        public ResourceLocation texturePath() {
            return Confluence.asResource("textures/entity/zombie/" + name + ".png");
        }

        public ResourceLocation animationPath() {
            return Confluence.asResource("animations/entity/zombie");
        }

        public static Variant random(RandomSource random) {
            return values()[random.nextInt(values().length)];
        }
    }
}
