package org.confluence.mod.util.generation.variant;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileLaunch;
import org.confluence.lib.util.LibEntityUtils;
import org.confluence.mod.api.IGeneration;
import org.confluence.mod.common.init.ModGenerationProviderTypes;
import org.confluence.mod.util.generation.GenerationProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/// 在所有者手边创建弹幕的纯布局。
///
/// <p>“静止”表示生成点跟随手部偏移，并把本枚弹幕速度倍率降为 0。需要持续控制的实体应在自身
/// tick 状态机中接管后续运动。</p>
public record StillGeneration(Vec3 offset) implements IGeneration {
    public static final MapCodec<StillGeneration> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Vec3.CODEC.fieldOf("offset").forGetter(StillGeneration::offset)
    ).apply(instance, StillGeneration::new));

    public static StillGeneration of(Vec3 offset) {
        return new StillGeneration(offset);
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
        Vec3 position = owner.position().add(0, 1, 0).add(offset);
        if (owner instanceof Player player) {
            position = position.add(LibEntityUtils.getPlayerHandPos(player));
        }
        return List.of(new ProjectileLaunch(projectile, position, owner.getLookAngle(), 0.0F));
    }

    @Override
    public GenerationProvider getCodec() {
        return ModGenerationProviderTypes.STILL.get();
    }
}
