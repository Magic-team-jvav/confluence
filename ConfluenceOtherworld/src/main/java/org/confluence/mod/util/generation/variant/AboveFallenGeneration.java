package org.confluence.mod.util.generation.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileLaunch;
import org.confluence.lib.util.AimUtils;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.api.IGeneration;
import org.confluence.mod.common.init.ModGenerationProviderTypes;
import org.confluence.mod.util.generation.GenerationProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/// 星怒这一类“从上方坠落”的剑气布局。
///
/// @param maxAngle   索敌允许的最大夹角
/// @param range      索敌和视线落点的搜索范围
/// @param predict    预判量，保留给后续更精确的目标预测
/// @param inAccuracy 弹幕散布误差
/// @param offsetV    生成点相对目标/视线落点的垂直偏移
/// @param offsetH    生成点相对目标/视线落点的水平随机偏移
public record AboveFallenGeneration(
        float maxAngle,
        float range,
        float predict,
        float inAccuracy,
        float offsetV,
        float offsetH
) implements IGeneration {
    public static final MapCodec<AboveFallenGeneration> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.FLOAT.fieldOf("max_angle").forGetter(AboveFallenGeneration::maxAngle),
            Codec.FLOAT.fieldOf("range").forGetter(AboveFallenGeneration::range),
            Codec.FLOAT.fieldOf("predict").forGetter(AboveFallenGeneration::predict),
            Codec.FLOAT.fieldOf("in_accuracy").forGetter(AboveFallenGeneration::inAccuracy),
            Codec.FLOAT.fieldOf("offset_v").forGetter(AboveFallenGeneration::offsetV),
            Codec.FLOAT.fieldOf("offset_h").forGetter(AboveFallenGeneration::offsetH)
    ).apply(instance, AboveFallenGeneration::new));

    @Override
    public List<ProjectileLaunch> createLaunches(
            LivingEntity owner,
            float speed,
            Supplier<? extends @Nullable Projectile> projectileFactory
    ) {
        Projectile projectile = projectileFactory.get();
        if (projectile == null) {
            return List.of();
        }
        projectile.setOwner(owner);
        Vec3 eye = owner.getEyePosition();
        LivingEntity target = LibEntityUtils.getAABBAngleTarget(
                eye,
                eye.add(owner.getForward().normalize().scale(range)),
                owner.level(),
                owner,
                range,
                maxAngle,
                entity -> LibEntityUtils.canHitEntity(entity, owner));
        float actualInaccuracy;
        Vec3 firePos, projVel;
        Vec3 fireLocOffset = new Vec3(
                owner.getRandom().nextDouble() * offsetH * 2 - offsetH,
                offsetV,
                owner.getRandom().nextDouble() * offsetH * 2 - offsetH
        );

        if (target != null) {
            // 有有效目标时，从目标上方生成，并让 AimUtils 负责弹道修正与散布。
            firePos = target.getEyePosition().add(fireLocOffset);
            actualInaccuracy = 0;
            AimUtils.AimHelperOptions aimHelperOptions = new AimUtils.AimHelperOptions(projectile)
                    .setProjectileSpeed(speed)
                    .setRandomOffsetRadius(inAccuracy);
            projVel = AimUtils.helperAimEntity(firePos, target, aimHelperOptions);
        } else {
            // 没有有效目标时，使用玩家视线命中的位置作为落点，让弹幕从上方随机偏移处下落。
            Vec3 ori = owner.getEyePosition().add(0, 1, 0);
            Vec3 end = ori.add(owner.getForward().normalize().scale(range));
            BlockHitResult blockHitResult = owner.level().clip(new ClipContext(ori, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, owner));
            firePos = blockHitResult.getLocation().add(fireLocOffset);
            actualInaccuracy = inAccuracy / 2;
            projVel = fireLocOffset.scale(-1);
        }

        projectile.shoot(
                projVel.get(Direction.Axis.X),
                projVel.get(Direction.Axis.Y),
                projVel.get(Direction.Axis.Z),
                speed,
                actualInaccuracy);
        return List.of(new ProjectileLaunch(projectile, firePos, projectile.getDeltaMovement()));
    }


    @Override
    public GenerationProvider getCodec() {
        return ModGenerationProviderTypes.ABOVE_FALLEN.get();
    }
}
