package org.confluence.mod.common.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.RoundRobinSelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.DashAction;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.entity.projectile.AncientLightProjectile;
import org.confluence.mod.common.entity.projectile.CultistProjectile;
import org.confluence.mod.common.init.entity.BossEntities;
import org.confluence.mod.common.init.entity.ModEntities;

import java.util.List;

/// 拜月教邪教徒——传送+弹幕+召唤幻影龙。
public class LunaticCultist extends BaseBoss {
    static final int CLONE_COUNT = 2;
    private static final int TELEPORT_TICKS = 60;
    private static final int SPELL_COOLDOWN = 50;
    private static final int DRAGON_COOLDOWN = 400;
    private static final int ANCIENT_LIGHT_COOLDOWN = 180;
    static final int ANCIENT_LIGHT_COUNT = 5;
    private static final String TELEPORT_TIMER_TAG = "TeleportTimer";
    private static final String SPELL_TIMER_TAG = "SpellTimer";
    private static final String DRAGON_TIMER_TAG = "DragonTimer";
    private static final String ANCIENT_LIGHT_TIMER_TAG = "AncientLightTimer";
    private static final String ATTACK_CYCLE_TAG = "AttackCycle";
    private static final String SPELL_PATTERN_TAG = "SpellPattern";
    private int teleportTimer = TELEPORT_TICKS;
    private int spellTimer = SPELL_COOLDOWN / 2;
    private int dragonTimer = DRAGON_COOLDOWN / 2;
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

    public static AttributeSupplier.Builder createAttributes() {
        return createBossAttributes()
                .add(Attributes.MAX_HEALTH, 700.0)
                .add(Attributes.ATTACK_DAMAGE, 20.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.FOLLOW_RANGE, 64.0);
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
                return RoundRobinSelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(LunaticCultist.this),
                                new WaitAction(12)),
                        SequenceNode.of(new HasTargetCondition(LunaticCultist.this),
                                new DashAction(LunaticCultist.this, 0.9, 15)),
                        new FlyWanderAction(LunaticCultist.this, 0.3, 10)
                );
            }
        };
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void tick() {
        super.tick();
        if (isRemoved()) return;

        if (!level().isClientSide) {
            if (getTarget() == null && tickCount % 10 == 0) {
                Player replacement = findCombatPlayer();
                if (replacement != null) setTarget(replacement);
            }

            // Teleport cycle
            teleportTimer--;
            if (teleportTimer <= 0 && getTarget() != null) {
                teleportTimer = TELEPORT_TICKS + random.nextInt(40);
                doTeleport();
                if (++attackCycle % 3 == 0) spawnClones();
            }

            // Summon phantom dragon
            if (getTarget() != null) {
                spellTimer--;
                if (spellTimer <= 0) {
                    spellTimer = SPELL_COOLDOWN + random.nextInt(20);
                    shootSpell();
                }

                dragonTimer--;
                if (dragonTimer <= 0) {
                    dragonTimer = DRAGON_COOLDOWN + random.nextInt(120);
                    spawnDragon();
                }

                ancientLightTimer--;
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
        return subEntities.stream()
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
    /// <p>每次调用只创建一个真实碰撞实体，伤害不会在创建阶段直接结算。
    /// 三类弹幕都锁定发射瞬间的方向，玩家可以通过移动躲避。</p>
    boolean shootSpell() {
        if (!(level() instanceof ServerLevel serverLevel) || getTarget() == null) {
            return false;
        }

        int pattern = spellPattern++ % 3;
        CultistProjectile projectile;
        float damage;
        float velocity;
        if (pattern == 0) {
            projectile = ModEntities.CULTIST_FIREBALL.get().create(level());
            damage = 16.0F;
            velocity = 1.15F;
        } else if (pattern == 1) {
            projectile = ModEntities.CULTIST_ICE_MIST.get().create(level());
            damage = 12.0F;
            velocity = 0.72F;
        } else {
            projectile = ModEntities.CULTIST_LIGHTNING_ORB.get().create(level());
            damage = 14.0F;
            velocity = 0.88F;
        }
        if (projectile == null) {
            return false;
        }
        projectile.configure(this, getTarget(), damage, velocity);
        if (serverLevel.addFreshEntity(projectile)) {
            return true;
        }
        projectile.discard();
        return false;
    }

    void onCloneHit(LunaticCultistClone clone) {
        if (clone.getMaster() != this || level().isClientSide) return;
        dragonTimer = DRAGON_COOLDOWN;
        spawnDragon();
    }

    private void clearClones() {
        for (LunaticCultistClone clone : List.copyOf(subEntities).stream()
                .filter(LunaticCultistClone.class::isInstance)
                .map(LunaticCultistClone.class::cast)
                .toList()) {
            clone.discard();
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !level().isClientSide) clearClones();
        return hurt;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt(TELEPORT_TIMER_TAG, teleportTimer);
        tag.putInt(SPELL_TIMER_TAG, spellTimer);
        tag.putInt(DRAGON_TIMER_TAG, dragonTimer);
        tag.putInt(ANCIENT_LIGHT_TIMER_TAG, ancientLightTimer);
        tag.putInt(ATTACK_CYCLE_TAG, attackCycle);
        tag.putInt(SPELL_PATTERN_TAG, spellPattern);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        teleportTimer = Math.max(0, tag.getInt(TELEPORT_TIMER_TAG));
        spellTimer = Math.max(0, tag.getInt(SPELL_TIMER_TAG));
        dragonTimer = Math.max(0, tag.getInt(DRAGON_TIMER_TAG));
        ancientLightTimer = Math.max(0, tag.getInt(ANCIENT_LIGHT_TIMER_TAG));
        attackCycle = Math.max(0, tag.getInt(ATTACK_CYCLE_TAG));
        spellPattern = Math.max(0, tag.getInt(SPELL_PATTERN_TAG));
    }

    @Override public boolean causeFallDamage(float f, float m, DamageSource s) { return false; }
    @Override public boolean isPushable() { return false; }
    @Override protected boolean shouldDiscardWhenNoTarget() { return true; }
}
