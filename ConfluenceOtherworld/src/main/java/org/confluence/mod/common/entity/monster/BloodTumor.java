package org.confluence.mod.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.entity.MonsterEntities;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;

/**
 * 血腥孢子爆裂后生成的静止肿瘤。
 *
 * <p>肿瘤不会寻路或主动近战，而是在短暂孵化后随机转化为血爬虫、脸怪或猩红喀迈拉。
 * 倒计时会保存到实体数据中，区块卸载与重载不会把孵化进度重置。</p>
 */
public final class BloodTumor extends BaseMonster {
    private static final String TRANSFORMATION_TICKS_TAG =
            "TransformationTicks";
    private static final RawAnimation IDLE =
            RawAnimation.begin().thenLoop("misc.idle");
    private int transformationTicks = -1;

    public BloodTumor(
            EntityType<? extends BloodTumor> type,
            Level level) {
        super(type, level);
    }

    @Override
    public void onAddedToWorld() {
        if (transformationTicks < 0) {
            transformationTicks = 60 + Math.floorMod(getId(), 40);
        }
        super.onAddedToWorld();
    }

    @Override
    public void tick() {
        super.tick();
        setDeltaMovement(Vec3.ZERO);
        if (!level().isClientSide
                && isAlive()
                && transformationTicks-- <= 0) {
            transform();
        }
    }

    private void transform() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        List<EntityType<? extends Entity>> outcomes = List.of(
                MonsterEntities.BLOOD_CRAWLER.get(),
                MonsterEntities.FACE_MONSTER.get(),
                MonsterEntities.CRIMERA.get());
        Entity replacement = outcomes.get(
                random.nextInt(outcomes.size())).create(serverLevel);
        if (replacement == null) {
            transformationTicks = 20;
            return;
        }
        replacement.moveTo(
                getX(), getY(), getZ(), getYRot(), getXRot());
        replacement.setDeltaMovement(0.0, 0.4, 0.0);
        if (serverLevel.addFreshEntity(replacement)) {
            discard();
        } else {
            transformationTicks = 20;
        }
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new WaitAction(20);
            }
        };
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(TRANSFORMATION_TICKS_TAG, transformationTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        transformationTicks = Math.max(
                0, tag.getInt(TRANSFORMATION_TICKS_TAG));
    }

    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(
                this,
                "idle",
                0,
                state -> state.setAndContinue(IDLE)));
    }
}
