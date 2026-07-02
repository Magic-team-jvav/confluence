package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.confluence.lib.api.entity.Boss;
import org.confluence.mod.common.entity.ai.bt.Blackboard;
import org.confluence.mod.common.entity.monster.BaseMonster;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseBoss extends BaseMonster implements Boss {
    protected final ServerBossEvent bossEvent;
    protected final Blackboard blackboard = new Blackboard();
    protected final List<Entity> subEntities = new ArrayList<>();
    protected float ironGolemResistance = 0.4f;
    protected float explosionResistance = 0.5f;
    protected int noTargetTicks = 0;
    protected static final int DISCARD_TICKS = 200;

    public BaseBoss(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.bossEvent = (ServerBossEvent) new ServerBossEvent(getDisplayName(), getBossBarColor(), BossEvent.BossBarOverlay.PROGRESS)
                .setDarkenScreen(true).setPlayBossMusic(true);
    }

    // === Boss bar ===

    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.RED;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        bossEvent.setName(getDisplayName());
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        bossEvent.setProgress(getHealth() / getMaxHealth());
    }

    // === Boss interface ===

    @Override
    public boolean shouldShowMessage() { return isMainBody(); }

    @Override
    public boolean isMainBody() { return true; }

    @Override
    public boolean shouldEnhanceMultiplayer() { return true; }

    // === Multi-part ===

    public void addSubEntity(Entity part) {
        subEntities.add(part);
    }

    public void removeSubEntity(Entity part) {
        subEntities.remove(part);
    }

    public List<Entity> getSubEntities() {
        return subEntities;
    }

    @Override
    public void remove(RemovalReason reason) {
        for (Entity part : subEntities) {
            if (part.isAlive()) part.remove(reason);
        }
        subEntities.clear();
        super.remove(reason);
    }

    // === Difficulty ===

    protected boolean isExpert() {
        return level().getDifficulty() == Difficulty.HARD;
    }

    protected boolean isMaster() {
        return false;
    }

    // === Damage ===

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof IronGolem) {
            amount *= ironGolemResistance;
        }
        if (source.is(DamageTypes.EXPLOSION)) {
            amount *= explosionResistance;
        }
        return super.hurt(source, amount);
    }

    // === Immunity ===

    @Override
    public boolean isInWall() {
        return false;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (source.is(DamageTypes.LAVA)) return true;
        return super.isInvulnerableTo(source);
    }

    @Override
    public void lavaHurt() {
        if (!fireImmune()) {
            float multiplier = isExpert() ? 0.05f : 0.25f;
            igniteForSeconds(15.0F * multiplier);
            if (hurt(damageSources().lava(), 4.0F * multiplier)) {
                playSound(SoundEvents.GENERIC_BURN, 0.4F, 2.0F + random.nextFloat() * 0.4F);
            }
        }
    }

    // === Tick ===

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            if (getTarget() == null || !getTarget().isAlive()) {
                noTargetTicks++;
                if (noTargetTicks > DISCARD_TICKS && shouldDiscardWhenNoTarget()) {
                    Boss.sendBossDeathMessage(this);
                    discard();
                    return;
                }
            } else {
                noTargetTicks = 0;
            }
        }
    }

    protected boolean shouldDiscardWhenNoTarget() {
        return true;
    }

    // === Save ===

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (hasCustomName()) bossEvent.setName(getDisplayName());
    }

    // === Attributes ===

    public static AttributeSupplier.Builder createBossAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 500.0)
                .add(Attributes.ATTACK_DAMAGE, 15.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.FLYING_SPEED, 0.4);
    }

    // === Death ===

    @Override
    public void die(DamageSource source) {
        Boss.sendBossDeathMessage(this);
        super.die(source);
    }
}
