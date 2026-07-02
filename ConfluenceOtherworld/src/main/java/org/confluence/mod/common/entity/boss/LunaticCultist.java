package org.confluence.mod.common.entity.boss;

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
import net.minecraft.world.level.levelgen.Heightmap;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTRoot;
import org.confluence.mod.common.entity.ai.bt.composite.SelectorNode;
import org.confluence.mod.common.entity.ai.bt.composite.SequenceNode;
import org.confluence.mod.common.entity.ai.bt.condition.HasTargetCondition;
import org.confluence.mod.common.entity.ai.bt.leaf.DashAction;
import org.confluence.mod.common.entity.ai.bt.leaf.FlyWanderAction;
import org.confluence.mod.common.entity.ai.bt.leaf.ShootAction;
import org.confluence.mod.common.entity.ai.bt.leaf.WaitAction;
import org.confluence.mod.common.init.entity.BossEntities;

/**
 * 拜月教邪教徒——传送+弹幕+召唤幻影龙。
 */
public class LunaticCultist extends BaseBoss {
    private static final int TELEPORT_TICKS = 60;
    private static final int DRAGON_COOLDOWN = 400;
    private int teleportTimer = TELEPORT_TICKS;
    private int dragonTimer = DRAGON_COOLDOWN / 2;
    private int attackCycle = 0;

    public LunaticCultist(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 10, false);
        setNoGravity(true);
        this.xpReward = 5000;
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
                return SelectorNode.of(
                        SequenceNode.of(new HasTargetCondition(LunaticCultist.this),
                                new ShootAction(LunaticCultist.this, 8.0f, 0.2f),
                                new WaitAction(5)),
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

        if (!level().isClientSide) {
            if (getTarget() == null && tickCount % 10 == 0) {
                Player nearest = level().getNearestPlayer(this, 64);
                if (nearest != null) setTarget(nearest);
            }

            // Teleport cycle
            teleportTimer--;
            if (teleportTimer <= 0) {
                teleportTimer = TELEPORT_TICKS + random.nextInt(40);
                doTeleport();
                attackCycle++;
            }

            // Summon phantom dragon
            if (getTarget() != null) {
                dragonTimer--;
                if (dragonTimer <= 0) {
                    dragonTimer = DRAGON_COOLDOWN + random.nextInt(120);
                    spawnDragon();
                }
            }
        }
    }

    private void doTeleport() {
        if (getTarget() == null || !(level() instanceof ServerLevel serverLevel)) return;
        float angle = random.nextFloat() * Mth.TWO_PI;
        double dist = 4 + random.nextFloat() * 6;
        double tx = getTarget().getX() + Math.cos(angle) * dist;
        double tz = getTarget().getZ() + Math.sin(angle) * dist;
        int ty = serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                new net.minecraft.core.BlockPos((int) tx, 0, (int) tz)).getY();
        teleportTo(tx, ty + 5 + random.nextFloat() * 4, tz);
    }

    private void spawnDragon() {
        if (!(level() instanceof ServerLevel serverLevel)) return;
        PhantasmDragon dragon = BossEntities.PHANTASM_DRAGON.get().create(level());
        if (dragon != null) {
            dragon.setPos(position().add(0, 3, 0));
            if (getTarget() != null) dragon.setTarget(getTarget());
            serverLevel.addFreshEntity(dragon);
        }
    }

    @Override public boolean causeFallDamage(float f, float m, DamageSource s) { return false; }
    @Override public boolean isPushable() { return false; }
    @Override protected boolean shouldDiscardWhenNoTarget() { return true; }
}
