package org.confluence.mod.common.generation.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.AimUtils;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.api.IGeneration;
import org.confluence.mod.common.generation.GenerationProvider;
import org.confluence.mod.common.init.ModGenerationProviderTypes;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/// 星怒弹幕发射方式
///
/// @param maxAngle   索敌最大角度
/// @param range      索敌范围
/// @param predict    预判量
/// @param inAccuracy 不精准度
/// @param offsetV    发射时的高度偏移
/// @param offsetH    发射时的xy偏移
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
    public void genProjectile(LivingEntity owner, @Nullable ItemStack weapon, float speed, Supplier<? extends @Nullable Projectile> proj) {
        var projectile = proj.get();
        if (projectile == null) return;
        Vec3 eye = owner.getEyePosition();
        LivingEntity target = LibEntityUtils.getAABBAngleTarget(eye, eye.add(owner.getForward().normalize().scale(range)), owner.level(), owner, range, maxAngle, e -> LibEntityUtils.canHitEntity(e, projectile.getOwner()));
        float actualInaccuracy;
        Vec3 firePos, projVel;
        Vec3 fireLocOffset = new Vec3(
                owner.getRandom().nextDouble() * offsetH * 2 - offsetH,
                offsetV,
                owner.getRandom().nextDouble() * offsetH * 2 - offsetH
        );

        if (target != null) {
            // 周围有目标 预判
            firePos = target.getEyePosition().add(fireLocOffset);
            // 不准确性已在AimUtils中处理
            actualInaccuracy = 0;
            AimUtils.AimHelperOptions aimHelperOptions = new AimUtils.AimHelperOptions(projectile)
                    .setProjectileSpeed(speed)
                    .setRandomOffsetRadius(inAccuracy);
            projVel = AimUtils.helperAimEntity(firePos, target, aimHelperOptions);
        } else {
            // 周围无目标 获取视线指向点
            Vec3 ori = owner.getEyePosition().add(0, 1, 0);
            Vec3 end = ori.add(owner.getForward().normalize().scale(range));
            BlockHitResult blockHitResult = owner.level().clip(new ClipContext(ori, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, owner));
            firePos = blockHitResult.getLocation().add(fireLocOffset);
            // 取中值
            actualInaccuracy = inAccuracy / 2;
            // 速度是开火位置的反方向
            projVel = fireLocOffset.scale(-1);
        }

        projectile.setOwner(owner);
        projectile.setPos(firePos);
        projectile.shoot(projVel.get(Direction.Axis.X), projVel.get(Direction.Axis.Y), projVel.get(Direction.Axis.Z), speed, actualInaccuracy);
        owner.level().addFreshEntity(projectile);
    }


    @Override
    public GenerationProvider getCodec() {
        return ModGenerationProviderTypes.ABOVE_FALLEN.get();
    }
}
