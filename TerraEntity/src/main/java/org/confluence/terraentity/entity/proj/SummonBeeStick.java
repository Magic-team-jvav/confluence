package org.confluence.terraentity.entity.proj;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.api.entity.ISummonMob;
import org.confluence.terraentity.init.TETags;

public class SummonBeeStick extends LineProj{

    public SummonBeeStick(EntityType<? extends LineProj> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public DamageSource getDamageSource(LivingEntity hurter){
        if(getOwner() instanceof ISummonMob mob)
            return TETags.DamageTypes.of(level(), TETags.DamageTypes.SUMMONER, mob.summon_getOwner());
        return super.getDamageSource(hurter);
    }
}
