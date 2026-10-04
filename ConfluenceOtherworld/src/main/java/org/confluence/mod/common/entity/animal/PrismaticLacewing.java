package org.confluence.mod.common.entity.animal;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;

public class PrismaticLacewing extends BaseFlyingCritter {
    private static final String DAMAGED_BY_PLAYER_KEY = "DamagedByPlayer";
    private boolean damagedByPlayer;

    public PrismaticLacewing(EntityType<? extends PrismaticLacewing> type, Level level) {
        super(type, level);
    }

    @Override
    public ResourceLocation getModelPath() {
        return Confluence.asResource("animal/butterfly");
    }

    @Override
    public ResourceLocation getTexturePath() {
        return Confluence.asResource("textures/entity/animal/butterfly/prismatic_lacewing.png");
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return withPassivePanic(createEnemyAvoidingFlyingRoutine(), 1.25D);
            }
        };
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide && tickCount % 3 == 0) {
            level().addParticle(ParticleTypes.END_ROD, getRandomX(0.8), getRandomY(), getRandomZ(0.8), 0.0, 0.005, 0.0);
        }
    }

    /// 七彩草蛉在玩家首次造成伤害前不受其他实体或环境伤害。
    /// 玩家命中后保留该状态，此后陷阱或其他弹幕造成的伤害也能正常结算。
    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if (attacker instanceof Player) damagedByPlayer = true;
        if (!damagedByPlayer && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean(DAMAGED_BY_PLAYER_KEY, damagedByPlayer);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        damagedByPlayer = tag.getBoolean(DAMAGED_BY_PLAYER_KEY);
    }

    @Override
    public boolean isFullBright() {
        return true;
    }
}
