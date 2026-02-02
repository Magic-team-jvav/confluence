package org.confluence.terraentity.entity.npc.brain;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.brain.behavior.NPCRangeAttackBrain;

import java.util.Optional;

/**
 * 爆破专家的AI，放置TNT
 */
public class DemolitionistNPCAi extends NPCAi {

    public DemolitionistNPCAi(AbstractTerraNPC npc) {
        super(npc);

    }
    protected void init(){
        npc.setAttackRange(5);
        npc.setCanPerformerAttackTest(e->true); // 不需要手持TNT
    }

    protected NPCRangeAttackBrain<? super AbstractTerraNPC> getRangeAttackBrain() {
        return new NPCRangeAttackBrain<>(10, npc.getAttackRange()){

            @Override
            protected boolean customDoAttack(ServerLevel level, AbstractTerraNPC owner, LivingEntity target){
                var tnt = new PrimedTnt(EntityType.TNT, level){
                    @Override
                    protected void explode() {
                        float f = 4.0F;
                        this.level().explode(this, Explosion.getDefaultDamageSource(this.level(), this), new ExplosionDamageCalculator() {
                            public boolean shouldBlockExplode(Explosion p_353087_, BlockGetter p_353096_, BlockPos p_353092_, BlockState p_353086_, float p_353094_) {
                                return false;
                            }

                            public Optional<Float> getBlockExplosionResistance(Explosion p_353090_, BlockGetter p_353088_, BlockPos p_353091_, BlockState p_353093_, FluidState p_353095_) {

                                return p_353093_.is(Blocks.NETHER_PORTAL) ? Optional.empty() : super.getBlockExplosionResistance(p_353090_, p_353088_, p_353091_, p_353093_, p_353095_);
                            }

                            public float getEntityDamageAmount(Explosion explosion, Entity entity) {
                                // 如果不是敌人，且不是npc的目标，则不受伤害
                                if(!(entity instanceof Enemy)){
                                    if(npc.getTarget() != entity)
                                        return 0;
                                }
                                return super.getEntityDamageAmount(explosion, entity);
                            }
                        }, this.getX(), this.getY(0.0625), this.getZ(), 4.0F, false, Level.ExplosionInteraction.TNT);
                    }
                };

                double distance = owner.distanceTo(target);
                Vec3 placePosition;
                if(distance < 5){
                    placePosition = target.getEyePosition();
                }
                else{
                    placePosition = target.position().subtract(owner.position()).normalize().scale(5).add(owner.getEyePosition());
                }
                tnt.setPos(placePosition);
                level.addFreshEntity(tnt);
                return true;
            }
        };
    }
}
