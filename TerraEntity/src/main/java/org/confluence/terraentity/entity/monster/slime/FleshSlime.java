package org.confluence.terraentity.entity.monster.slime;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import org.confluence.terraentity.init.TETags;

public class FleshSlime extends BaseSlime {

    public FleshSlime(EntityType<? extends Slime> slime, Level level, int color, int size) {
        super(slime, level, color, size);

    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if(source.is(DamageTypeTags.IS_FIRE) || source.is(DamageTypes.LAVA) || source.is(DamageTypeTags.IS_FALL)){
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        if(target.getType().is(TETags.EntityTypes.FLESH_ALLIANCE)){
            return false;
        }
        return super.canAttack(target);
    }
}
