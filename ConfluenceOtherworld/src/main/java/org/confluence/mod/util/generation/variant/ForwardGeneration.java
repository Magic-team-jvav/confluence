package org.confluence.mod.util.generation.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileLaunch;
import org.confluence.mod.api.IGeneration;
import org.confluence.mod.common.init.ModGenerationProviderTypes;
import org.confluence.mod.util.generation.GenerationProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/// # 直线弹幕
public record ForwardGeneration(float offsetY, float inaccuracy) implements IGeneration {
    public static final MapCodec<ForwardGeneration> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.FLOAT.fieldOf("offsetY").forGetter(ForwardGeneration::offsetY),
            Codec.FLOAT.fieldOf("inaccuracy").forGetter(ForwardGeneration::inaccuracy)
    ).apply(instance, ForwardGeneration::new));

    public static ForwardGeneration of(float offsetY, float inaccuracy) {
        return new ForwardGeneration(offsetY, inaccuracy);
    }

    @Override
    public List<ProjectileLaunch> createLaunches(
            LivingEntity owner,
            float velocity,
            Supplier<? extends @Nullable Projectile> projectileFactory
    ) {
        Projectile projectile = projectileFactory.get();
        if (projectile == null) {
            return List.of();
        }
        projectile.setOwner(owner);
        // 调用实体自己的发射钩子，保留草剑等子类在发射瞬间初始化的运动参数。
        projectile.shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F, velocity, inaccuracy);
        Vec3 direction = projectile.getDeltaMovement();
        Vec3 position = new Vec3(owner.getX(), owner.getY() + owner.getEyeHeight() + offsetY, owner.getZ());
        return List.of(new ProjectileLaunch(projectile, position, direction));
    }

    @Override
    public GenerationProvider getCodec() {
        return ModGenerationProviderTypes.FORWARD_GENERATION.get();
    }
}
