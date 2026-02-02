package org.confluence.terraentity.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.confluence.terraentity.api.entity.IPetMob;

public class PetItem<T extends Mob & IPetMob> extends SummonItem<T> {

    public PetItem(Properties properties, DeferredHolder<EntityType<?>, EntityType<T>> entityType) {
        super(properties, entityType, 0, 0);
    }

    @Override
    protected boolean canDiscard(Entity entity, Player player){
        return entity instanceof IPetMob petMobMob && petMobMob.summon_getOwner() == player;
    }
}
