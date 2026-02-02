package org.confluence.terraentity.entity.boss.hillofflesh;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.confluence.terraentity.entity.monster.HillHungry;
import org.confluence.terraentity.entity.monster.prefab.AbstractPrefab;
import org.confluence.terraentity.entity.monster.slime.FleshSlime;
import org.confluence.terraentity.init.TESounds;
import org.confluence.terraentity.init.TETags;
import org.confluence.terraentity.init.entity.TEMonsterEntities;
import org.confluence.terraentity.utils.TEUtils;

public class HillOfFleshMouth extends HillOfFleshPart {

    int summonHungryInterval = 256;
    HillHungry hungry;

    public HillOfFleshMouth(HillOfFlesh parentMob, String name, float width, float height) {
        super(parentMob, name, width, height);

    }

    @Override
    protected void tickPart(double offsetX, double offsetY, double offsetZ, int index) {
        if(!this.level().isClientSide && (this.hungry == null || !this.hungry.isAlive())
                && this.parentMob.tickCount % summonHungryInterval == this.getId() % summonHungryInterval){
            this.hungry = this.summonHungry(this.position(), index);
            if (this.hungry != null) {
                this.getParent().setTarget(index, this.hungry, false);
            }
        }
        if(this.target != null && level().isClientSide){
            if(this.target instanceof HillHungry hun){
                this.hungry = hun;
                hungry.setClientInitPos(this.getBoundingBox().getCenter().toVector3f());
            }
        }
    }

    private HillHungry summonHungry(Vec3 hungryPos, int index) {
        HillHungry hungry = TEUtils.spawnEntity(() -> new HillHungry(TEMonsterEntities.HILL_HUNGRY.get(), level(),
                new AbstractPrefab().getPrefab()) {
            @Override
            protected boolean shouldDropLoot() {
                return false;
            }

            @Override
            protected Vec3 initDirection(LivingEntity owner){
                if(HillOfFleshMouth.this.name.endsWith("0")){
                    return new Vec3(0,0,-1);
                }
                return HillOfFleshMouth.this.modelOffset.add(0,-8,0).normalize().scale(1);
            }
            @Override
            public boolean canAttack(LivingEntity entity) {
                return !(entity.getType().is(TETags.EntityTypes.FLESH_ALLIANCE)) &&  super.canAttack(entity);
            }
            @Override
            public float getMaxDis() {
                return super.getMaxDis() * HillOfFleshMouth.this.parentMob.currentScale;
            }
        }, (ServerLevel) level(), hungryPos);

        if (hungry != null) {
            hungry.minion_setOwner(this.getParent());
            hungry.setYRot(this.getYRot());
            hungry.setInitPos(this.position().add(0,1,0).toVector3f());
            hungry.index = index;
            hungry.level().playSound(null, hungry.blockPosition(), TESounds.WALL_OF_FLESH_SUMMON.get(), SoundSource.HOSTILE, 1, 1);
            return hungry;
        }
        return null;
    }

    public void onSummonFleshSlime(FleshSlime fleshSlime){
        fleshSlime.setDeltaMovement(this.modelOffset.normalize().scale(3f));
    }

}
