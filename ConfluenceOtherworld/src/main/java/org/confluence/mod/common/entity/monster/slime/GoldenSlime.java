package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.mod.common.init.item.ModItems;
import org.joml.Vector3f;

/// 金色史莱姆 —— 高血量、快速跳跃、掉落金币，稀有。
public class GoldenSlime extends BaseSlime {
    private static final DustParticleOptions GOLD_DUST = new DustParticleOptions(new Vector3f(1.0F, 0.666F, 0.0F), 1.0F);

    public GoldenSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, false);
    }

    @Override
    public void setSlimeSize(int size) {
        super.setSlimeSize(2);
    }

    /// 金史莱姆锁定为八刻基础跳跃间隔；追击目标时由移动控制器取其三分之一。
    @Override
    protected int getJumpDelay() {
        return 8;
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount % 22 == 0 && level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(GOLD_DUST, getX(), getY(), getZ(), 12, random.nextFloat(), random.nextFloat(), random.nextFloat(), 0.01);
        }
    }

    /// 受击时只显示散落的钱币，不生成可拾取物，避免视觉反馈改变实际掉落收益。
    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean accepted = super.hurt(source, amount);
        if (accepted && level() instanceof ServerLevel serverLevel) {
            ItemStack coin = new ItemStack(random.nextInt(4) == 0 ? ModItems.SILVER_COIN.get() : ModItems.COPPER_COIN.get());
            serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, coin), getX(), getY() + getBbHeight() * 0.5, getZ(),
                    4 + random.nextInt(4), getBbWidth() * 0.35, getBbHeight() * 0.25, getBbWidth() * 0.35, 0.12);
        }
        return accepted;
    }
}
