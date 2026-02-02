package org.confluence.terraentity.mixed;

import net.minecraft.world.entity.monster.Zombie;

public interface IZombie {
    void terra_entity$setSlimeZombie();

    boolean terra_entity$isSlimeZombie();

    static IZombie of(Zombie zombie) {
        return (IZombie) zombie;
    }
}
