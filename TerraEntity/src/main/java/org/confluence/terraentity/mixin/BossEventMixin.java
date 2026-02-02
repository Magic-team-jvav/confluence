package org.confluence.terraentity.mixin;

import net.minecraft.world.BossEvent;
import org.confluence.terraentity.mixed.IBossEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BossEvent.class)
public class BossEventMixin implements IBossEvent {

    @Unique
    float terra_entity$bossHealth;
    @Unique
    float terra_entity$bossMaxHealth;

    @Override
    public float terra_enity$getBossHealth() {
        return terra_entity$bossHealth;
    }

    @Override
    public void terra_enity$setBossHealth(float bossHealth) {
        terra_entity$bossHealth = bossHealth;
    }

    @Override
    public float terra_enity$getBossMaxHealth() {
        return terra_entity$bossMaxHealth;
    }

    @Override
    public void terra_enity$setBossMaxHealth(float bossHealth) {
        terra_entity$bossMaxHealth = bossHealth;
    }
}
