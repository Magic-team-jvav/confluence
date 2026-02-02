package com.xiaohunao.enemybanner.banner;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.slf4j.Logger;

public class BasicBannerBehavior implements BannerBehavior{
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final int DEFAULT_RANGE = 16;
    public static final double DEFAULT_DAMAGE = 0.5;
    public static final double DEFAULT_RESIST = 0.3;

    private int range;
    private double damage;
    private double resist;

    public BasicBannerBehavior(){
        this(DEFAULT_RANGE, DEFAULT_DAMAGE, DEFAULT_RANGE);
    }

    public BasicBannerBehavior(int range, double damage, double resist){
        this.range = range;
        this.damage = damage;
        this.resist = resist;
    }

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public void onTick(Level level, BlockPos blockPos, int tickCount) {

    }

    @Override
    public void onEntityDamage(DamageContainer damageContainer, Entity entity, Player player) {
        float originalDamage = damageContainer.getOriginalDamage();
        float newDamage = (float) (originalDamage * (1 + damage));
        damageContainer.setNewDamage(newDamage);
    }

    @Override
    public void onPlayerDamage(DamageContainer damageContainer, Entity entity, Player player) {
        float originalDamage = damageContainer.getOriginalDamage();
        float newDamage = (float) (originalDamage * (1 - resist));
        damageContainer.setNewDamage(newDamage);
    }

    @Override
    public void onPlayerKillLivingEntity(Player player, LivingEntity livingEntity) {

    }

    public void setRange(int range) {
        this.range = range;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getResist() {
        return resist;
    }

    public void setResist(double resist) {
        this.resist = resist;
    }
}
