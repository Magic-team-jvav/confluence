package org.confluence.mod.common.entity.boss;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.projectile.AncientLightProjectile;
import org.confluence.mod.common.entity.projectile.CultistProjectile;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.ModEntities;

/// 拜月教邪教徒——传送+弹幕+召唤幻影龙。
public class LunaticCultist extends BaseBoss {
    static final int CLONE_COUNT = 2;
    private static final int TELEPORT_TICKS = 60;
    private static final int SPELL_COOLDOWN = 50;
    private static final int ANCIENT_LIGHT_COOLDOWN = 180;
    static final int ANCIENT_LIGHT_COUNT = 5;
    private static final String TELEPORT_TIMER_TAG = "TeleportTimer";
    private static final String SPELL_TIMER_TAG = "SpellTimer";
    private static final String ANCIENT_LIGHT_TIMER_TAG = "AncientLightTimer";
    private static final String ATTACK_CYCLE_TAG = "AttackCycle";
    private static final String SPELL_PATTERN_TAG = "SpellPattern";

    private CombatState combatState = CombatState.CASTING;
    private int stateTicks;
    private int teleportTimer = TELEPORT_TICKS;
    private int spellTimer = SPELL_COOLDOWN / 2;
    private int ancientLightTimer = ANCIENT_LIGHT_COOLDOWN / 2;
    private int attackCycle = 0;
    private int spellPattern;

    public LunaticCultist(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        setNoGravity(true);
        this.xpReward = 5000;
    }

    /// 拜月教邪教徒的空中站位和传送由技能状态机控制。
    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.YELLOW;
    }

    @Override
    protected BTRoot createBT() {
        return new BTRoot() {
            @Override
            protected BTNode createTree() {
                return new WaitAction(20);
            }
        };
    }

    @Override
    public void tick() {
        super.tick();
        if (!isAlive()) return;

        if (!level().isClientSide) {
            if (getTarget() == null && tickCount % 10 == 0) {
                Player replacement = findCombatPlayer();
                if (replacement != null) setTarget(replacement);
            }
            setSpecialState(CombatState.WOUNDED, getHealth() < getMaxHealth() * 0.5F);
            if (getTarget() == null) {
                setDeltaMovement(Vec3.ZERO);
                return;
            }
            if (combatState != CombatState.CASTING) {
                setDeltaMovement(Vec3.ZERO);
                if (combatState == CombatState.RITUAL && level() instanceof ServerLevel server) {
                    if (stateTicks % 5 == 0)
                        server.sendParticles(ParticleTypes.ENCHANT, getX(), getY() + 1, getZ(), 12, 3, 0.2, 3, 0.1);
                }
                if (++stateTicks >= stateParameters(combatState).duration()) {
                    if (combatState == CombatState.RITUAL) spawnDragon();
                    combatState = CombatState.CASTING;
                    stateTicks = 0;
                }
                return;
            }

            // 传送周期
            teleportTimer--;
            if (teleportTimer <= 0 && getTarget() != null) {
                teleportTimer = stateParameters(CombatState.RELOCATING).randomAttackInterval(random);
                doTeleport();
                combatState = CombatState.RELOCATING;
                stateTicks = 0;
                return;
            }

            // 弹幕与幻影龙召唤周期
            if (getTarget() != null) {
                spellTimer--;
                if (spellTimer <= 0) {
                    spellTimer = stateParameters(CombatState.CASTING).randomAttackInterval(random);
                    shootSpell();
                    if (++attackCycle >= stateParameters(CombatState.RITUAL).attackCount()) {
                        attackCycle = 0;
                        combatState = CombatState.RITUAL;
                        stateTicks = 0;
                        spawnClones();
                        return;
                    }
                }

                if (getHealth() < getMaxHealth() * 0.5F) ancientLightTimer--;
                if (ancientLightTimer <= 0) {
                    ancientLightTimer = ANCIENT_LIGHT_COOLDOWN + random.nextInt(60);
                    spawnAncientLights();
                }
            }
        }
    }

    private void doTeleport() {
        var target = getTarget();
        if (target == null) return;
        Vec3 destination = findFlyingTeleportPosition(target, 4.0, 10.0, 4.0, 16);
        if (destination != null) {
            teleportTo(destination.x, destination.y, destination.z);
        }
    }

    void spawnDragon() {
        if (!(level() instanceof ServerLevel serverLevel) || hasLivingDragon()) {
            return;
        }
        PhantasmDragon dragon = BossEntities.PHANTASM_DRAGON.get().create(level());
        if (dragon != null) {
            dragon.setPos(position().add(0, 3, 0));
            dragon.setMaster(this);
            if (getTarget() != null) dragon.setTarget(getTarget());
            if (!serverLevel.addFreshEntity(dragon)) {
                removeSubEntity(dragon);
                dragon.discard();
            }
        }
    }

    private boolean hasLivingDragon() {
        return getSubEntities().stream()
                .filter(PhantasmDragon.class::isInstance)
                .map(PhantasmDragon.class::cast)
                .anyMatch(dragon -> dragon.isAlive() && !dragon.isRemoved());
    }

    void spawnClones() {
        if (!(level() instanceof ServerLevel serverLevel) || getTarget() == null) return;
        clearClones();
        for (int index = 0; index < CLONE_COUNT; index++) {
            LunaticCultistClone clone = BossEntities.LUNATIC_CULTIST_CLONE.get().create(level());
            if (clone == null) continue;
            double angle = index * Mth.TWO_PI / CLONE_COUNT + random.nextDouble() * 0.4;
            clone.setPos(position().add(Math.cos(angle) * 3.0, 0.0, Math.sin(angle) * 3.0));
            clone.setMaster(this, index);
            clone.setTarget(getTarget());
            if (!serverLevel.addFreshEntity(clone)) removeSubEntity(clone);
        }
    }

    void spawnAncientLights() {
        if (!(level() instanceof ServerLevel serverLevel) || getTarget() == null) return;
        for (int index = 0; index < ANCIENT_LIGHT_COUNT; index++) {
            AncientLightProjectile light = ModEntities.ANCIENT_LIGHT.get().create(level());
            if (light == null) continue;
            double spread = (index - (ANCIENT_LIGHT_COUNT - 1) * 0.5) * 0.16;
            light.configure(this, getTarget(), spread);
            if (!serverLevel.addFreshEntity(light)) {
                light.discard();
            }
        }
    }

    /// 依次发射火球、冰雾与闪电球。
    ///
    /// 每次调用只创建一个真实碰撞实体，伤害不会在创建阶段直接结算。
    /// 三类弹幕都锁定发射瞬间的方向，玩家可以通过移动躲避。
    boolean shootSpell() {
        if (!(level() instanceof ServerLevel serverLevel) || getTarget() == null) {
            return false;
        }

        int pattern = spellPattern++ % 3;
        CultistProjectile projectile;
        float velocity;
        if (pattern == 0) {
            projectile = ModEntities.CULTIST_FIREBALL.get().create(level());
            velocity = 1.15F;
        } else if (pattern == 1) {
            projectile = ModEntities.CULTIST_ICE_MIST.get().create(level());
            velocity = 0.72F;
        } else {
            projectile = ModEntities.CULTIST_LIGHTNING_ORB.get().create(level());
            velocity = 0.88F;
        }
        if (projectile == null) {
            return false;
        }
        projectile.configure(this, getTarget(), (float) getAttributeValue(Attributes.ATTACK_DAMAGE), velocity);
        if (serverLevel.addFreshEntity(projectile)) {
            return true;
        }
        projectile.discard();
        return false;
    }

    void onCloneHit(LunaticCultistClone clone) {
        if (clone.getMaster() != this || level().isClientSide || !isPerformingRitual()) return;
        spawnDragon();
        combatState = CombatState.CASTING;
        stateTicks = 0;
    }

    boolean isPerformingRitual() {return combatState == CombatState.RITUAL;}

    private void clearClones() {
        for (LunaticCultistClone clone : getSubEntities().stream()
                .filter(LunaticCultistClone.class::isInstance)
                .map(LunaticCultistClone.class::cast)
                .toList()) {
            clone.discard();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (combatState == CombatState.RELOCATING && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
            return false;
        boolean hurt = super.hurt(source, amount);
        if (hurt && !level().isClientSide && isPerformingRitual()) {
            clearClones();
            combatState = CombatState.CASTING;
            stateTicks = 0;
        }
        return hurt;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(TELEPORT_TIMER_TAG, teleportTimer);
        tag.putInt(SPELL_TIMER_TAG, spellTimer);
        tag.putInt(ANCIENT_LIGHT_TIMER_TAG, ancientLightTimer);
        tag.putInt(ATTACK_CYCLE_TAG, attackCycle);
        tag.putInt(SPELL_PATTERN_TAG, spellPattern);
        tag.putString("CombatState", combatState.name());
        tag.putInt("StateTicks", stateTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        teleportTimer = Math.max(0, tag.getInt(TELEPORT_TIMER_TAG));
        spellTimer = Math.max(0, tag.getInt(SPELL_TIMER_TAG));
        ancientLightTimer = Math.max(0, tag.getInt(ANCIENT_LIGHT_TIMER_TAG));
        attackCycle = Math.max(0, tag.getInt(ATTACK_CYCLE_TAG));
        spellPattern = Math.max(0, tag.getInt(SPELL_PATTERN_TAG));
        try {combatState = CombatState.valueOf(tag.getString("CombatState"));} catch (
                IllegalArgumentException ignored) {combatState = CombatState.CASTING;}
        stateTicks = Math.max(0, tag.getInt("StateTicks"));
    }

    @Override public boolean causeFallDamage(float f, float m, DamageSource s) { return false; }

    @Override public boolean isPushable() { return false; }

    public enum CombatState {CASTING, RELOCATING, RITUAL, WOUNDED}

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.LUNATIC_CULTIST_FREE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.LUNATIC_CULTIST_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.LUNATIC_CULTIST_DEATH.get();
    }

}
