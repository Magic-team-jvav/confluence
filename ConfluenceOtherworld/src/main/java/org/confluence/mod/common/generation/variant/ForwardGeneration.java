package org.confluence.mod.common.generation.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.confluence.terraentity.api.entity.IGeneration;
import org.confluence.terraentity.registries.generation.GenerationProvider;
import org.confluence.terraentity.registries.generation.GenerationProviderTypes;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * <h1>直线弹幕</h1>
 */
public record ForwardGeneration(float offsetY, float inaccuracy) implements IGeneration {

    public static MapCodec<ForwardGeneration> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.FLOAT.fieldOf("offsetY").forGetter(ForwardGeneration::offsetY),
            Codec.FLOAT.fieldOf("inaccuracy").forGetter(ForwardGeneration::inaccuracy)
    ).apply(instance, ForwardGeneration::new));

    public static ForwardGeneration of(float offsetY, float inaccuracy) {
        return new ForwardGeneration(offsetY, inaccuracy);
    }

    @Override
    public void genProjectile(LivingEntity owner, @Nullable ItemStack weapon, float velocity, Supplier<? extends @Nullable Projectile> proj) {
        Projectile projectile = proj.get();
        if (projectile == null) return;
        projectile.setOwner(owner);
        // todo 计算yaw
        projectile.setPos(owner.getX(), owner.getY() + owner.getEyeHeight() + offsetY, owner.getZ());
        projectile.shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F, velocity, inaccuracy);
        owner.level().addFreshEntity(projectile);
    }

    @Override
    public GenerationProvider getCodec() {
        return GenerationProviderTypes.FORWARD_GENERATION.get();
    }


}
