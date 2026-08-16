package org.confluence.mod.api;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import org.confluence.lib.api.projectile.ProjectileLaunch;
import org.confluence.mod.common.init.ModCustomRegistries;
import org.confluence.mod.util.generation.GenerationProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/// # 发射方式
public interface IGeneration {
    /// 创建一次完整布局。
    ///
    /// @param owner             服务端所有者
    /// @param velocity          已解析的实际速度，仅供需要预判的布局计算方向
    /// @param projectileFactory 每次调用都应返回一个新的、尚未加入世界的实体
    /// @return 尚未提交到世界的布局描述
    List<ProjectileLaunch> createLaunches(
            LivingEntity owner,
            float velocity,
            Supplier<? extends @Nullable Projectile> projectileFactory
    );

    /// 返回本布局的多态编解码器注册项。
    GenerationProvider getCodec();

    Codec<IGeneration> TYPED_CODEC = ModCustomRegistries.GENERATION_PROVIDERS.byNameCodec()
            .dispatch(IGeneration::getCodec, provider -> provider.codec().codec());
}
