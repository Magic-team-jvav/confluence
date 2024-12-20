package org.confluence.mod.mixin.client;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import org.confluence.mod.mixed.IMusicManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(MusicManager.class)
public abstract class MusicManagerMixin implements IMusicManager {
    @Shadow
    @Nullable
    private SoundInstance currentMusic;
    @Unique
    private boolean confluence$musicBoxOccupied = false;

    @Override
    public @Nullable SoundInstance confluence$getCurrentMusic() {
        return currentMusic;
    }

    @Override
    public void confluence$setMusicBoxOccupied(boolean occupied) {
        this.confluence$musicBoxOccupied = occupied;
    }

    @Override
    public boolean confluence$isMusicBoxOccupied() {
        return confluence$musicBoxOccupied;
    }
}
