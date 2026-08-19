package org.confluence.mod.common.entity.monster.slime;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

/// 金色史莱姆 —— 高血量、快速跳跃、掉落金币，稀有。
public class GoldenSlime extends BaseSlime {
    private static final DustParticleOptions GOLD_DUST = new DustParticleOptions(new Vector3f(1.0F, 0.666F, 0.0F), 1.0F);

    public GoldenSlime(EntityType<? extends BaseSlime> type, Level level) {
        super(type, level, 0xFCF8BD, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSlimeAttributes(5.0f, 2, 97.0f);
    }

    @Override
    protected void setSlimeSize(int size) {
        super.setSlimeSize(2);
    }

    /// 金史莱姆在 1.21 侧锁定为八刻基础跳跃间隔；追击目标时由移动控制器取其三分之一。
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
}
