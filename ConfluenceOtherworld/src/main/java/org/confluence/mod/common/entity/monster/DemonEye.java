package org.confluence.mod.common.entity.monster;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.ConditionalSwitchNode;
import org.confluence.mod.common.entity.ai.bt.leaf.DemonEyeLeaveAction;
import org.confluence.mod.common.entity.ai.bt.leaf.DemonEyeSurroundAction;
import org.confluence.mod.common.entity.ai.bt.leaf.DemonEyeWanderAction;
import org.confluence.mod.util.DateUtils;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class DemonEye extends ReboundingFlyingMonster implements VariantHolder<DemonEye.Variant> {
    public static final String VARIANT_KEY = "Variant";
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(DemonEye.class, EntityDataSerializers.INT);
    private static final RawAnimation FLY = RawAnimation.begin().thenLoop("fly");
    private DemonEyeSurroundAction surroundAction;

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
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(v.isLarge() ? 0.1 : 0.2);
        this.setHealth(this.getMaxHealth());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return BaseFlyingMonster.createFlyingAttributes().add(Attributes.ATTACK_DAMAGE, 18.0).add(Attributes.KNOCKBACK_RESISTANCE, 0.0);
    }

    /// 恶魔眼始终使用飞行物理。这里直接返回无重力语义，与 1.21 侧一致，避免命令生成或
    /// NBT 读取覆盖实体标志后先坠落到地面，再由飞行行为勉强拉回目标高度。
    @Override
    public boolean isNoGravity() {
        return true;
    }

    /// 恶魔眼体型决定受击后的位移幅度。
    ///
    /// 普通体型使用两倍击退，大型体型使用一点五倍击退；倍率先作用于原始强度，
    /// 再交给原版击退公式处理，保持伤害来源、附魔和其他模组施加的击退语义。
    @Override
    public void knockback(double strength, double x, double z) {
        super.knockback(strength * (getVariant().isLarge() ? 1.5 : 2.0),
                x, z);
    }

    @Override
    protected Vec3 reboundVelocity(Vec3 requested, Vec3 allowed) {
        Vec3 rebound = requested;
        if (allowed.x != requested.x) {
            rebound = new Vec3(requested.x < 0.0 ? 0.22 : -0.22, rebound.y, rebound.z);
        }
        if (allowed.y != requested.y) {
            boolean movingDown = requested.y < 0.0;
            if (surroundAction != null) {
                surroundAction.adjustTargetAfterVerticalCollision(movingDown);
            }
            rebound = new Vec3(rebound.x, movingDown ? Mth.clamp(-requested.y, 0.1, 0.22) : Mth.clamp(-requested.y, -0.22, -0.1), rebound.z);
        }
        if (allowed.z != requested.z) {
            rebound = new Vec3(rebound.x, rebound.y, requested.z < 0.0 ? 0.3 : -0.3);
        }
        return rebound;
    }

    @Override
    protected BTRoot createBT() {
        surroundAction = new DemonEyeSurroundAction(this);
        BTNode night = new ConditionalSwitchNode(() -> getTarget() != null && getTarget().isAlive(), surroundAction, new DemonEyeWanderAction(this));

        BTNode root = new ConditionalSwitchNode(() -> level().isDay(), new DemonEyeLeaveAction(this), night);

        return new BTRoot() {
            @Override
            protected BTNode createTree() { return root; }
        };
    }

    @Override
    public Variant getVariant() {
        return Variant.byId(this.entityData.get(DATA_VARIANT));
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

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, data, tag);
        if (tag == null || !tag.contains(VARIANT_KEY)) {
            setVariant(Variant.random(random));
        }
        return result;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Fly", 0, state -> state.setAndContinue(FLY)));
    }

    @Override
    public void tick() {
        if (!level().isClientSide) {
            setTarget(level().getNearestPlayer(this, 40.0));
        }
        super.tick();
        Vec3 movement = getDeltaMovement();
        if (movement.lengthSqr() > 1.0E-8) {
            setYRot((float) Math.toDegrees(Mth.atan2(-movement.x, movement.z)));
            setXRot((float) Math.toDegrees(Mth.atan2(movement.y, movement.horizontalDistance())));
        }
    }

    public enum Variant implements IVariant {
        NORMAL("normal", false, 15.0, 3.5, 1, 1.0F),
        NORMAL_BIG("normal_big", true, 12.0, 4.0, 2, 1.3F),
        CATARACT("cataract", false, 11.5, 3.5, 2, 1.0F),
        CATARACT_BIG("cataract_big", true, 14.0, 4.0, 2, 1.3F),
        SLEEPY("sleepy", false, 15.0, 3.0, 1, 1.0F),
        SLEEPY_BIG("sleepy_big", true, 16.0, 3.5, 1, 1.3F),
        DILATED("dilated", true, 12.0, 3.5, 1, 1.0F),
        DILATED_SMALL("dilated_small", false, 11.5, 3.0, 0, 0.7F),
        GREEN("green", true, 15.0, 4.0, 0, 1.0F),
        GREEN_SMALL("green_small", false, 12.5, 3.0, 0, 0.7F),
        PURPLE("purple", false, 15.0, 3.0, 2, 1.0F),
        PURPLE_BIG("purple_big", true, 16.0, 3.0, 2, 1.3F),
        OWL("owl", false, 18.5, 3.0, 3, 1.0F),
        SPACESHIP("spaceship", false, 15.0, 3.0, 2, 1.0F);

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        private final String name;
        private final boolean large;
        public final double health;
        public final double damage;
        public final int armor;
        private final float scale;

        Variant(String name, boolean large, double health, double damage, int armor, float scale) {
            this.name = name;
            this.large = large;
            this.health = health;
            this.damage = damage;
            this.armor = armor;
            this.scale = scale;
        }

        @Override
        public String getSerializedName() { return name; }

        public float scale() { return scale; }

        public boolean isLarge() {
            return large;
        }

        public int textureIndex() { return ordinal() / 2; }

        @Override
        public Codec<Variant> codec() {
            return CODEC;
        }

        @Override
        public String serializeKey() {
            return VARIANT_KEY;
        }

        @Override
        public ResourceLocation modelPath() {
            return Confluence.asResource("geo/entity/demon_eye.geo.json");
        }

        @Override
        public ResourceLocation texturePath() {
            String[] names = {"normal", "cataract", "sleepy", "dilated", "green", "purple", "owl", "spaceship"};
            return Confluence.asResource("textures/entity/demon_eye/" + names[textureIndex()] + ".png");
        }

        public ResourceLocation animationPath() {
            return Confluence.asResource("animations/entity/demon_eye.animation.json");
        }

        public static Variant byId(int id) {
            Variant[] variants = values();
            return id >= 0 && id < variants.length ? variants[id] : NORMAL;
        }

        public static Variant random(RandomSource random) {
            return random(random, DateUtils.isHalloween(DateUtils.getCalendar()));
        }

        /// 按节日状态选择自然生成变种。
        ///
        /// 万圣节期间只生成猫头鹰和太空船外观，其余日期只从十二种常规恶魔眼中选择。
        static Variant random(RandomSource random, boolean halloween) {
            if (halloween) {
                return random.nextBoolean() ? OWL : SPACESHIP;
            }
            return values()[random.nextInt(12)];
        }
    }
}
