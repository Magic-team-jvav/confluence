package org.confluence.mod.common.entity.boss;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.projectile.CultistProjectile;
import org.confluence.mod.common.init.entity.ModEntities;

/// 可以提前摧毁的蓄力球；存活七秒后向四个水平方向释放预言之末。
public final class AncientDoom extends BaseLivingBossPart<LunaticCultist> {
    public AncientDoom(EntityType<? extends Monster> type, Level level) {super(type, level);}

    public void setMaster(LunaticCultist master) {bindTo(master);}

    @Override
    protected Class<LunaticCultist> getOwnerType() {return LunaticCultist.class;}

    @Override
    protected void tickPart(LunaticCultist master) {
        if (level().isClientSide) {
            double radius = Math.min(1.0, tickCount / 40.0);
            for (int i = 0; i < 8; i++) {
                double angle = tickCount * 0.15 + i * Math.PI / 4.0;
                level().addParticle(ParticleTypes.DRAGON_BREATH,
                        getX() + Math.cos(angle) * radius, getY() + 0.5 + Math.sin(angle * 2.0) * radius * 0.5,
                        getZ() + Math.sin(angle) * radius, 0.0, 0.0, 0.0);
            }
            return;
        }
        if (tickCount < 140) return;
        for (int i = 0; i < 4; i++) {
            CultistProjectile bolt = ModEntities.PROPHECYS_END.get().create(level());
            if (bolt == null) continue;
            double angle = i * Math.PI * 0.5;
            bolt.configure(master, getBoundingBox().getCenter(), new Vec3(Math.cos(angle), 0.0, Math.sin(angle)), 20.0F, 100);
            if (!level().addFreshEntity(bolt)) bolt.discard();
        }
        discard();
    }
}
