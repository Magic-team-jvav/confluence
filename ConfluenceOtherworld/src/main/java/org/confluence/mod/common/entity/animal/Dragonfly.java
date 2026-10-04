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
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.BTStatus;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.leaf.VanillaGoalAction;
import org.confluence.mod.util.OverworldUtils;



public class Dragonfly extends BaseFlyingCritter implements VariantHolder<Dragonfly.Variant> {
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(Dragonfly.class, EntityDataSerializers.INT);
    public static final String VARIANT_KEY = "Variant";
    private static final VariantSpawnProfile<Variant> FOREST_VARIANTS = VariantSpawnProfile.<Variant>builder()
            .add(Variant.BLUE, 399).add(Variant.GREEN, 399).add(Variant.RED, 399).add(Variant.GOLD, 3)
            .build();
    private static final VariantSpawnProfile<Variant> DESERT_VARIANTS = VariantSpawnProfile.<Variant>builder()
            .add(Variant.BLACK, 399).add(Variant.ORANGE, 399).add(Variant.YELLOW, 399).add(Variant.GOLD, 3)
            .build();

    public Dragonfly(EntityType<? extends Dragonfly> type, Level level) {
        super(type, level);
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return SelectorNode.of(
                        new VanillaGoalAction(new AvoidEntityGoal<>(Dragonfly.this, Player.class, 5.0F, 1.0D, 1.35D)),
                        new DragonflyBurstAction(Dragonfly.this)
                );
            }
        };
    }

    /// 蜻蜓按短促冲飞与悬停交替移动；玩家靠近时仍由更高优先级的逃逸分支立即抢占。
    private static final class DragonflyBurstAction extends BTNode {
        private final Dragonfly dragonfly;
        private Vec3 destination;
        private int burstTicks;
        private int restTicks;

        private DragonflyBurstAction(Dragonfly dragonfly) {
            this.dragonfly = dragonfly;
        }

        @Override
        public void start() {
            if (burstTicks <= 0 && restTicks <= 0) beginRest();
        }

        @Override
        public BTStatus execute() {
            if (restTicks > 0) {
                --restTicks;
                dragonfly.setDeltaMovement(dragonfly.getDeltaMovement().scale(0.72));
                if (restTicks == 0) beginBurst();
                return BTStatus.RUNNING;
            }
            if (destination == null || --burstTicks <= 0 || dragonfly.position().closerThan(destination, 0.75)) {
                beginRest();
                return BTStatus.RUNNING;
            }
            Vec3 direction = destination.subtract(dragonfly.position());
            if (direction.lengthSqr() > 1.0E-6) {
                Vec3 movement = dragonfly.getDeltaMovement().scale(0.82).add(direction.normalize().scale(0.12));
                if (movement.lengthSqr() > 0.2025) movement = movement.normalize().scale(0.45);
                dragonfly.setDeltaMovement(movement);
                Vec3 lookTarget = dragonfly.position().add(movement);
                dragonfly.getLookControl().setLookAt(lookTarget.x, lookTarget.y, lookTarget.z);
                dragonfly.hasImpulse = true;
            }
            return BTStatus.RUNNING;
        }

        private void beginBurst() {
            destination = dragonfly.position().add(
                    dragonfly.random.nextDouble() * 8.0 - 4.0,
                    dragonfly.random.nextDouble() * 4.0 - 2.0,
                    dragonfly.random.nextDouble() * 8.0 - 4.0
            );
            burstTicks = 8 + dragonfly.random.nextInt(9);
        }

        private void beginRest() {
            destination = null;
            burstTicks = 0;
            restTicks = 8 + dragonfly.random.nextInt(18);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, Variant.BLUE.ordinal());
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
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (!tag.contains(VARIANT_KEY)) {
            setVariant(Variant.BLUE);
            return;
        }
        Variant.CODEC.parse(NbtOps.INSTANCE, tag.get(VARIANT_KEY)).ifSuccess(this::setVariant);
    }

    @Override
    protected String variantSaveKey() {
        return VARIANT_KEY;
    }

    @Override
    protected void initializeSpawnVariant() {
        VariantSpawnProfile<Variant> profile = OverworldUtils.isDesert(level().getBiome(blockPosition()))
                ? DESERT_VARIANTS
                : FOREST_VARIANTS;
        setVariant(profile.select(random));
    }

    @Override
    public ResourceLocation getModelPath() {return getVariant().modelPath();}

    @Override
    public ResourceLocation getTexturePath() {return getVariant().texturePath();}

    public enum Variant implements IVariant {
        BLUE, GREEN, RED, YELLOW, BLACK, ORANGE, GOLD;

        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);
        private static final IntFunction<Variant> BY_ID = ByIdMap.sparse(Variant::ordinal, values(), BLUE);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public ResourceLocation modelPath() {
            return IVariant.resource("animal/dragonfly");
        }

        @Override
        public ResourceLocation texturePath() {
            return IVariant.resource("textures/entity/animal/dragonfly/" + getSerializedName() + "_dragonfly.png");
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
