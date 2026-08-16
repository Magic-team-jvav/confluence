package org.confluence.mod.util.generation.variant;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.api.IGeneration;
import org.confluence.mod.common.init.ModGenerationProviderTypes;
import org.confluence.mod.util.generation.GenerationProvider;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public record StillGeneration(Vec3 offset) implements IGeneration {
    public static final MapCodec<StillGeneration> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Vec3.CODEC.fieldOf("offset").forGetter(StillGeneration::offset)
    ).apply(instance, StillGeneration::new));

    public static StillGeneration of(Vec3 offset) {
        return new StillGeneration(offset);
    }

    @Override
    public void genProjectile(LivingEntity owner, @Nullable ItemStack weapon, float velocity, Supplier<? extends @Nullable Projectile> proj) {
        Projectile projectile = proj.get();
        if (projectile == null) return;
        projectile.setOwner(owner);
        // todo 计算yaw
        Vec3 pos = owner.position().add(0, 1, 0);
        if (owner instanceof Player player) {
            pos = pos.add(LibEntityUtils.getPlayerHandPos(player));
        }
        projectile.setPos(pos);
        owner.level().addFreshEntity(projectile);
    }

    @Override
    public GenerationProvider getCodec() {
        return ModGenerationProviderTypes.STILL.get();
    }
}
