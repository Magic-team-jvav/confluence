package org.confluence.mod.common.entity.boss;

import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EaterOfWorlds extends BaseWormBoss {
    private static final int SEGMENT_COUNT = 12;

    public EaterOfWorlds(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 800;
    }

    @Override
    protected int getSegmentCount() {
        return SEGMENT_COUNT;
    }

    @Override
    protected BossEvent.BossBarColor getBossBarColor() {
        return BossEvent.BossBarColor.PURPLE;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createWormBossAttributes()
                .add(Attributes.MAX_HEALTH, 600.0)
                .add(Attributes.ATTACK_DAMAGE, 22.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.FOLLOW_RANGE, 64.0);
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
        if (!level().isClientSide && getTarget() == null && tickCount % 30 == 0) {
            Player nearest = level().getNearestPlayer(this, 64);
            if (nearest != null) setTarget(nearest);
        }
    }
}
