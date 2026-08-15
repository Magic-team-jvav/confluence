package org.confluence.mod.common.entity.monster.humanoid;

import PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
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
import org.confluence.lib.util.LibDateUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.SpawnPlacementChecks;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.MeleeAttackAction;
import org.confluence.mod.common.entity.ai.bt.leaf.MoveToTargetAction;
import org.confluence.mod.common.entity.ai.bt.leaf.RandomStrollAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.mod.common.init.ModSoundEvents;
import org.jetbrains.annotations.Nullable;

/**
 * 拥有同步外观和属性变体的泰拉瑞亚僵尸。
 *
 * <p>服务端在自然生成时选择变体，并将枚举序号通过实体数据同步给客户端；存档使用稳定的
 * 字符串编解码，避免枚举顺序改变后把旧索引解释为另一种外观。变体同时决定基础生命、攻击、
 * 护甲、渲染色与缩放，加载存档或接收生成数据后都通过 {@link #setVariant(Variant)} 统一应用。</p>
 *
 * <p>当前十种变体共用已迁移的血腥僵尸模型、纹理和动画，客户端差异由颜色与缩放表达。
 * 资源方法返回的是 GeckoLib 可直接读取的完整文件路径，后续补入独立素材时只需修改枚举映射。</p>
 */
public class Zombie extends BaseHumanoidMonster implements VariantHolder<Zombie.Variant> {
    public static final String VARIANT_KEY = "Variant";
    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.INT);

    public Zombie(EntityType<? extends Zombie> type, Level level) {
        this(type, level, Variant.NORMAL);
    }

    public Zombie(EntityType<? extends Zombie> type, Level level, Variant variant) {
        super(type, level);
        setVariant(variant);
    }

    public static boolean checkZombieSpawnRules(EntityType<Zombie> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
        if (LibDateUtils.isDay(level) || pos.getY() < OverworldUtils.getSurfaceY()) return false;
        return SpawnPlacementChecks.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    protected void applyVariantStats(Variant v) {
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(v.health);
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(v.damage);
        getAttribute(Attributes.ARMOR).setBaseValue(v.armor);
        setHealth(getMaxHealth());
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
        return Variant.byId(this.entityData.get(DATA_VARIANT));
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
                t -> tag.put(VARIANT_KEY, t));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (!tag.contains(VARIANT_KEY)) {
            setVariant(Variant.NORMAL);
            return;
        }
        PortDataResultExtension.ifSuccess(
                Variant.CODEC.parse(NbtOps.INSTANCE, tag.get(VARIANT_KEY)),
                this::setVariant);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.TR_ZOMBIE_FREE.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.TR_ZOMBIE_DEATH.get();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData data,
                                        @Nullable CompoundTag tag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, data, tag);
        if (tag == null || !tag.contains(VARIANT_KEY)) {
            setVariant(Variant.random(random));
        }
        return result;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide) {
            // 原版僵尸模型通过该同步标记决定是否前伸双臂；行为树不会自动维护它。
            setAggressive(getTarget() != null && getTarget().isAlive());
        }
        if (!level().isClientSide && !isPersistenceRequired()
                && level().isDay() && level().canSeeSky(blockPosition())
        ) {
            discard();
        }
    }

    public enum Variant implements StringRepresentable {
        NORMAL("normal", 20.0, 4.0, 2, 0xB7C7A5, 1.0F),
        ARMED("armed", 24.0, 6.0, 3, 0xC6B08A, 1.05F),
        SLIMED("slimed", 18.0, 3.5, 2, 0x79C96B, 0.95F),
        PINCUSHION("pincushion", 22.0, 5.0, 3, 0xB09A79, 1.0F),
        TWIGGY("twiggy", 20.0, 5.0, 1, 0x8E6E43, 1.05F),
        SWAMP("swamp", 20.0, 3.5, 3, 0x68875B, 1.0F),
        RAINCOAT("raincoat", 22.0, 4.5, 2, 0xE6C947, 1.0F),
        BLOOD("blood", 28.0, 6.0, 3, 0xC04A4A, 1.08F),
        ESKIMO("eskimo", 24.0, 5.0, 4, 0xBBD3DF, 1.05F),
        BALD("bald", 18.0, 4.5, 1, 0xA7A58D, 0.98F);

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        private final String name;
        public final double health;
        public final double damage;
        public final int armor;
        private final int tint;
        private final float scale;

        Variant(String name, double health, double damage, int armor, int tint, float scale) {
            this.name = name;
            this.health = health;
            this.damage = damage;
            this.armor = armor;
            this.tint = tint;
            this.scale = scale;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public ResourceLocation modelPath() {
            return ResourceLocation.fromNamespaceAndPath(
                    Confluence.MODID, "geo/entity/blood_zombie.geo.json");
        }

        public ResourceLocation texturePath() {
            return ResourceLocation.fromNamespaceAndPath(
                    Confluence.MODID, "textures/entity/blood_zombie.png");
        }

        public ResourceLocation animationPath() {
            return ResourceLocation.fromNamespaceAndPath(
                    Confluence.MODID, "animations/entity/blood_zombie.animation.json");
        }

        public int tint() {
            return tint;
        }

        public float scale() {
            return scale;
        }

        public static Variant byId(int id) {
            Variant[] variants = values();
            return id >= 0 && id < variants.length ? variants[id] : NORMAL;
        }

        public static Variant random(RandomSource random) {
            return values()[random.nextInt(values().length)];
        }
    }
}
