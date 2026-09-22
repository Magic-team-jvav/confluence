package org.confluence.mod.common.item.yoyo;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.common.entity.yoyo.ChikCrystalProjectile;
import org.confluence.mod.common.entity.yoyo.YoyoEntity;
import org.confluence.mod.common.init.entity.ModEntities;

public final class ChikYoyoItem extends YoyoItem {
    private final float specialDamageMultiplier;
    private final int hitInterval;
    private final int shardCount;

    public ChikYoyoItem(ModRarity rarity, float damage, float range, int lifetimeTicks, float knockback, int hitInterval, int shardCount, float specialDamageMultiplier) {
        super(new Properties().unbreakable(), rarity, damage, range, lifetimeTicks, knockback);
        this.specialDamageMultiplier = specialDamageMultiplier;
        this.hitInterval = hitInterval;
        this.shardCount = shardCount;
    }

    @Override
    protected void onHitTarget(YoyoEntity yoyo, ServerPlayer owner, LivingEntity target) {
        YoyoSession session = YoyoSession.of(owner);
        if (session.isSpecialHit(hitInterval)) {
            for (int i = 0; i < shardCount; i++) {
                Vec3 direction = new Vec3(owner.getRandom().nextGaussian(), owner.getRandom().nextGaussian() * 0.4, owner.getRandom().nextGaussian());
                new ChikCrystalProjectile(ModEntities.CHIK_CRYSTAL.get(), yoyo.level()).shoot(yoyo, direction, target);
            }
        }
        session.countSpecialHit();
    }

    @Override
    public float hitMultiplier(ServerPlayer owner) {return YoyoSession.of(owner).isSpecialHit(hitInterval) ? specialDamageMultiplier : 1;}

    @Override
    public boolean fullBright() {return true;}

    @Override
    protected Component effectTooltip() {return Component.translatable("tooltip.confluence.yoyo.crystals", hitInterval, shardCount);}
}
