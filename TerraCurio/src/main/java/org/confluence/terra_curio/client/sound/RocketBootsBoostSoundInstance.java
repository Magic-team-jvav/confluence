package org.confluence.terra_curio.client.sound;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import org.confluence.terra_curio.client.handler.PlayerJumpHandler;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.init.TCSoundEvents;

public class RocketBootsBoostSoundInstance extends AbstractTickableSoundInstance {
    private final LocalPlayer player;

    public RocketBootsBoostSoundInstance(LocalPlayer player) {
        super(TCSoundEvents.ROCKET_BOOTS_BOOST.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = player;
        this.volume = 0.4F;
    }

    @Override
    public void tick() {
        if (!player.isRemoved() && PlayerJumpHandler.getCurrentFlight() == TCItems.ROCKET_BOOTS.getKey()) {
            this.x = player.getX();
            this.y = player.getY();
            this.z = player.getZ();
        } else {
            stop();
        }
    }
}
