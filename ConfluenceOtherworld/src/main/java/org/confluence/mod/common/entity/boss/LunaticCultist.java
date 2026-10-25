package org.confluence.mod.common.entity.boss;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
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
    static final int MAX_CLONE_COUNT = 6;
    private int ritualCloneCount;
    /// 两次瞬移之间的基础间隔，单位 tick。
    private static final int TELEPORT_TICKS = 60;
    /// 常规法术攻击的基础冷却，单位 tick。
    private static final int SPELL_COOLDOWN = 50;
    /// 召唤远古光明弹的基础冷却，单位 tick。
    private static final int ANCIENT_LIGHT_COOLDOWN = 180;

    /// 远古光束技能冷却的额外随机等待范围，单位 tick。
    private static final int ANCIENT_LIGHT_COOLDOWN_VARIATION = 60;
    /// 法术连发序列相邻弹幕的间隔，单位 tick。
    private static final int SPELL_SEQUENCE_STEP_TICKS = 5;
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
                    if (stateTicks % SPELL_SEQUENCE_STEP_TICKS == 0)
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
                if (isFtw()) teleportTimer = Math.max(1, teleportTimer * 2 / 3);
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
                    if (isFtw()) spellTimer = Math.max(1, spellTimer * 2 / 3);
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
                    ancientLightTimer = ANCIENT_LIGHT_COOLDOWN + random.nextInt(ANCIENT_LIGHT_COOLDOWN_VARIATION);
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
        if (!(level() instanceof ServerLevel serverLevel)) return;
        if (hasLivingDragon()) {
            AncientVision vision = BossEntities.ANCIENT_VISION.get().create(level());
            if (vision == null) return;
            vision.setPos(position().add(0.0, 3.0, 0.0));
            vision.setMaster(this);
            if (!serverLevel.addFreshEntity(vision)) vision.discard();
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
        ritualCloneCount = Math.min(MAX_CLONE_COUNT, ritualCloneCount + CLONE_COUNT);
        for (int index = 0; index < ritualCloneCount; index++) {
            LunaticCultistClone clone = BossEntities.LUNATIC_CULTIST_CLONE.get().create(level());
            if (clone == null) continue;
            double angle = index * Mth.TWO_PI / ritualCloneCount + random.nextDouble() * 0.4;
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

        int pattern = spellPattern;
        spellPattern = (spellPattern + 1) % 3;
        float health = getHealth() / getMaxHealth();
        if (isExpert() && health < 0.5F && random.nextInt(health < 0.25F ? 3 : health < 0.33F ? 4 : 6) == 0
                && spawnAncientDooms(serverLevel)) return true;
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

    private boolean spawnAncientDooms(ServerLevel serverLevel) {
        if (getTarget() == null) return false;
        boolean spawned = false;
        Vec3 center = getTarget().getBoundingBox().getCenter();
        for (int index = 0; index < 3; index++) {
            AncientDoom doom = BossEntities.ANCIENT_DOOM.get().create(level());
            if (doom == null) continue;
            double angle = random.nextDouble() * Math.PI * 2.0;
            Vec3 origin = center.add(Math.cos(angle) * 7.0, random.nextDouble() * 4.0 - 2.0, Math.sin(angle) * 7.0);
            doom.setPos(origin);
            if (!level().hasChunkAt(doom.blockPosition()) || !level().noCollision(doom, doom.getBoundingBox()))
                continue;
            doom.setMaster(this);
            if (serverLevel.addFreshEntity(doom)) spawned = true;
            else doom.discard();
        }
        return spawned;
    }

    void onCloneHit(LunaticCultistClone clone) {
        if (clone.getMaster() != this || level().isClientSide || !isPerformingRitual()) return;
        spawnDragon();
        ritualCloneCount = Math.max(0, ritualCloneCount - 1);
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
            if (isExpert()) {
                int removed = 0;
                for (Entity entity : getSubEntities()) {
                    if (entity instanceof LunaticCultistClone clone && removed < 3) {
                        clone.discard();
                        removed++;
                    }
                }
                ritualCloneCount = Math.max(0, ritualCloneCount - removed);
            } else {
                clearClones();
                ritualCloneCount = 0;
            }
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
        tag.putInt("RitualCloneCount", ritualCloneCount);
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
        spellPattern = Math.floorMod(tag.getInt(SPELL_PATTERN_TAG), 3);
        ritualCloneCount = Mth.clamp(tag.getInt("RitualCloneCount"), 0, MAX_CLONE_COUNT);
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
