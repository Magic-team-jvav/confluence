package org.confluence.terraentity.entity.monster.slime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.entity.ai.goal.behavior.BTFactory;
import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;
import org.confluence.terraentity.entity.ai.goal.behavior.composite.WeightNode;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.LookAtTargetAction;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.ShootAction;
import org.confluence.terraentity.entity.proj.SlimeSpikeProjectile;
import org.confluence.terraentity.init.entity.TEProjectileEntities;

import java.util.function.Supplier;

/**
 * 高级的尖刺史莱姆，可以远程攻击
 */
public class SpikedJungleSlime extends SpikedSlime {


    public SpikedJungleSlime(EntityType<? extends Slime> slime, Level level, int size) {
        super(slime, level, size);
    }

    public static SpikedJungleSlime createSpikedJungleSlime(EntityType<? extends Slime> slime, Level level, int size) {
        return new SpikedJungleSlime(slime, level, size);
    }

    public static SpikedJungleSlime createSpikedIceSlime(EntityType<? extends Slime> slime, Level level, int size) {
        return new SpikedJungleSlime(slime, level, size) {
            @Override
            protected Supplier<EntityType<SlimeSpikeProjectile>> getSpikeType() {
                return TEProjectileEntities.ICE_SPIKE;
            }
        };
    }


    private static class JungleSpikedSlimeShootAction extends ShootAction<SpikedJungleSlime> {

        public JungleSpikedSlimeShootAction(SpikedJungleSlime mob) {
            super(mob);
        }

        @Override
        protected void shoot(LivingEntity target) {
            SlimeSpikeProjectile entity = this.mob.getSpikeType().get().create(mob.level());
            if(entity != null) {
                entity.setHasGravity(false);
                entity.shootFromRotation(mob, mob.getXRot(), mob.getYRot(), 0, 0.3f, 1.0f);
                entity.setPos(mob.getBoundingBox().getCenter().offsetRandom(mob.getRandom(), 0.2f));
                entity.setOwner(mob);
                entity.setDamage(mob.getAttackDamage());
                mob.level().addFreshEntity(entity);
            }
        }
    }

    @Override
    protected BTNode createFarAwayFromTargetWaitingBehavior() {
        return BTFactory.sequence()
                .addChild(BTFactory.withTimer(20, new LookAtTargetAction(this)))
                .addChild(new WeightNode()
                        .addChild(1, new JungleSpikedSlimeShootAction(this))
                        .addChild(2, BTFactory.wait(5))
                )
        ;
    }

    @Override
    protected Supplier<EntityType<SlimeSpikeProjectile>> getSpikeType(){
        return TEProjectileEntities.JUNGLE_SPIKE;
    }
}
