package org.confluence.mod.common.entity.boss.thetwins;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.LibAttributes;
import org.confluence.terraentity.data.mappeddata.BossSkillMapDatas;
import org.confluence.terraentity.entity.ai.goal.behavior.BTBossTwoStageRoot;
import org.confluence.terraentity.entity.ai.goal.behavior.BTFactory;
import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;
import org.confluence.terraentity.entity.ai.goal.behavior.BTRoot;
import org.confluence.terraentity.entity.ai.goal.behavior.composite.ParallelNode;
import org.confluence.terraentity.entity.ai.goal.behavior.composite.SequenceNode;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.Condition;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.DistanceLowerThanCondition;
import org.confluence.terraentity.entity.ai.goal.behavior.condition.HealthLowerThanCondition;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.*;
import org.confluence.terraentity.init.entity.TEProjectileEntities;
import org.confluence.terraentity.registries.mappeddata.MappedDataTypes;

public class Retinazer extends Spazmatism {
    private final SkillParams skillParams;

    public Retinazer(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.skillParams = MappedDataTypes.BOSS_SKILL_MAP_DATAS.get().getData(BossSkillMapDatas.THE_TWINS_PARAMS).retinazerParams();
        this.xpReward = skillParams.xpReward;

    }

    public record SkillParams(int xpReward, float moveSpeed1, float moveSpeed2,
                              int shootCount1, int shootCount2, int shootInterval1, int shootInterval2,
                              int dashCount1, int dashInterval1, int dashDuration1, float dashSpeed1,
                              float followDistance, float rangeDamageFactor) {

        public static Codec<SkillParams> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("xp_reward").forGetter(SkillParams::xpReward),
                Codec.FLOAT.fieldOf("move_speed_1").forGetter(SkillParams::moveSpeed1),
                Codec.FLOAT.fieldOf("move_speed_2").forGetter(SkillParams::moveSpeed2),
                Codec.INT.fieldOf("shoot_count_1").forGetter(SkillParams::shootCount1),
                Codec.INT.fieldOf("shoot_count_2").forGetter(SkillParams::shootCount2),
                Codec.INT.fieldOf("shoot_interval_1").forGetter(SkillParams::shootInterval1),
                Codec.INT.fieldOf("shoot_interval_2").forGetter(SkillParams::shootInterval2),
                Codec.INT.fieldOf("dash_count_1").forGetter(SkillParams::dashCount1),
                Codec.INT.fieldOf("dash_interval_1").forGetter(SkillParams::dashInterval1),
                Codec.INT.fieldOf("dash_duration_1").forGetter(SkillParams::dashDuration1),
                Codec.FLOAT.fieldOf("dash_speed_1").forGetter(SkillParams::dashSpeed1),
                Codec.FLOAT.fieldOf("follow_distance").forGetter(SkillParams::followDistance),
                Codec.FLOAT.fieldOf("range_damage_factor").forGetter(SkillParams::rangeDamageFactor)
        ).apply(instance, SkillParams::new));

        public static SkillParams getDefaultParams() {
            return new SkillParams(1500, 1f, 1f,
                    5, 6, 20, 20,
                    5, 10, +5,1.5f,
                    7, 1f);
        }
    }

    protected static class RetinazerBT extends BTBossTwoStageRoot<Retinazer> {

        public RetinazerBT(Retinazer mob) {
            super(mob);
        }

        @Override
        protected Condition createStageCondition() {
            return new HealthLowerThanCondition(this.mob, 0.5f);
        }

        @Override
        protected SequenceNode switchPre(SequenceNode sequence) {
            return sequence
                    .addChild(new AnimCtrlAction<>(mob, "Controller", "switching", mob.switch_Flag, true))
                    .addChild(BTFactory.wait(10))
                    ;
        }

        @Override
        protected SequenceNode switchPost(SequenceNode sequence) {
            return sequence
                    .addChild(new AnimCtrlAction<>(mob, "Controller", "switching", mob.switch_Flag, false))
                    .addChild(BTFactory.wait(20))
                    ;
        }

        @Override
        protected BTNode createStageOneAttack() {
            return BTFactory.sequence()
                    // 间隔
                    .addChild(BTFactory.withTimer(50)
                            .addChild(new ParallelMoveAction(mob, this.mob.skillParams.followDistance(), this.mob.skillParams.moveSpeed1(), 5f))
                            .addChild(new LookAtTargetAction(mob))
                    )
                    // 平行射击
                    .addChild(BTFactory.parallel(ParallelNode.Policy.REQUIRE_ONE, ParallelNode.Policy.REQUIRE_ONE)
                            .addChild(new ParallelMoveAction(mob, this.mob.skillParams.followDistance(), this.mob.skillParams.moveSpeed1(), 5f)) // 悬浮位置在斜上方
                            .addChild(new LookAtTargetAction(mob))
                            .addChild(BTFactory.repeater(this.mob.skillParams.shootCount1(), BTFactory.sequence()
                                    .addChild(BTFactory.wait(this.mob.skillParams.shootInterval1()))
                                    .addChild(BTFactory.condition(new DistanceLowerThanCondition(this.mob, 20), new IntervalShootAction(mob, 30f))) // 离太远不能发射激光
                            ))
                    )
                    .addChild(new AnimCtrlAction<>(mob, "Controller", "run1", mob.run_1_Flag, true))
                    // 冲刺
                    .addChild(BTFactory.repeater(this.mob.skillParams.dashCount1(), BTFactory.sequence()
                            .addChild(BTFactory.parallel(ParallelNode.Policy.REQUIRE_ONE, ParallelNode.Policy.REQUIRE_ONE)
                                    .addChild(BTFactory.wait(this.mob.skillParams.dashInterval1()))
                                    .addChild(new LookAtTargetAction(mob))
                            )
                            .addChild(BTFactory.withTimer(this.mob.skillParams.dashDuration1(), new DashAction(mob, this.mob.skillParams.dashSpeed1())))
                    ))
                    .addChild(new AnimCtrlAction<>(mob, "Controller", "run1", mob.run_1_Flag, false))
            ;
        }

        @Override
        protected BTNode createStageTwoAttack() {
            return BTFactory.sequence()
                    .addChild(BTFactory.withTimer(50)
                            .addChild(new ParallelMoveAction(mob, this.mob.skillParams.followDistance(), this.mob.skillParams.moveSpeed2(), 0f))
                            .addChild(new LookAtTargetAction(mob))
                    )

                    // 头顶射击
                    .addChild(BTFactory.parallel(ParallelNode.Policy.REQUIRE_ONE, ParallelNode.Policy.REQUIRE_ONE)
                            .addChild(new ParallelMoveAction(mob, 0, this.mob.skillParams.moveSpeed2(), 5f))
                            .addChild(new LookAtTargetAction(mob))
                            .addChild(BTFactory.repeater(this.mob.skillParams.shootCount2(), BTFactory.sequence()
                                    .addChild(BTFactory.wait(this.mob.skillParams.shootInterval2()))
                                    .addChild(new IntervalShootAction(mob, 10f))
                            ))
                    )

                    // 平行射击
                    .addChild(BTFactory.parallel(ParallelNode.Policy.REQUIRE_ONE, ParallelNode.Policy.REQUIRE_ONE)
                            .addChild(new ParallelMoveAction(mob, this.mob.skillParams.followDistance(), this.mob.skillParams.moveSpeed2(), 0f))
                            .addChild(new LookAtTargetAction(mob))
                            .addChild(BTFactory.repeater(this.mob.skillParams.shootCount2() / 2, BTFactory.sequence()
                                    .addChild(BTFactory.wait(this.mob.skillParams.shootInterval2() / 2))
                                    .addChild(new IntervalShootAction(mob, 10f))
                            ))
                    )

                    // 平行快速射击
                    .addChild(new AnimCtrlAction<>(mob, "Controller", "run2", mob.run_2_Flag, true))
                    .addChild(BTFactory.parallel(ParallelNode.Policy.REQUIRE_ONE, ParallelNode.Policy.REQUIRE_ONE)
                            .addChild(new ParallelMoveAction(mob, this.mob.skillParams.followDistance(), this.mob.skillParams.moveSpeed2(), 0f))
                            .addChild(new LookAtTargetAction(mob))
                            .addChild(BTFactory.repeater(this.mob.skillParams.shootCount2() * 2, BTFactory.sequence()
                                    .addChild(BTFactory.wait(this.mob.skillParams.shootInterval2() / 4))
                                    .addChild(new IntervalShootAction(mob, 10f))
                            ))
                    )
                    .addChild(new AnimCtrlAction<>(mob, "Controller", "run2", mob.run_2_Flag, false))

            ;
        }

        protected static class IntervalShootAction extends ShootAction<Retinazer> {
            final float inaccuracy;
            public IntervalShootAction(Retinazer mob, float inaccuracy) {
                super(mob);
                this.inaccuracy = inaccuracy;
            }

            @Override
            protected void shoot(LivingEntity target) {
                var entity = TEProjectileEntities.TRAIL_PROJECTILE.get().create(mob.level());
                if (entity != null) {
                    entity.shootFromRotation(mob, mob.getXRot(), mob.getYRot(), 0.0f, 1.5f, inaccuracy);
                    entity.setOwner(mob);
                    entity.setPos(mob.getX(), mob.getY() + mob.getBbHeight() * 0.5f, mob.getZ());
                    entity.setDamage((float) mob.getAttributeValue(LibAttributes.getAttackDamage()) * mob.skillParams.rangeDamageFactor);
                    mob.level().addFreshEntity(entity);
                }
            }
        }
    }

    @Override
    protected BTRoot createBT(){
        return new RetinazerBT(this);
    }

}
