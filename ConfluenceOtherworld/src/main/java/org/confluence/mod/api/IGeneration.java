package org.confluence.mod.api;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModCustomRegistries;
import org.confluence.mod.util.generation.GenerationProvider;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/// # 发射方式
public interface IGeneration {
    /// 生成弹道实体
    void genProjectile(LivingEntity owner, @Nullable ItemStack weapon, float velocity, Supplier<? extends @Nullable Projectile> proj);

    /// 获取编解码器
    ///
    /// @return 编解码器
    GenerationProvider getCodec();

    Codec<IGeneration> TYPED_CODEC = ModCustomRegistries.GENERATION_PROVIERS.byNameCodec().dispatch(IGeneration::getCodec, p -> p.codec().codec());
}
