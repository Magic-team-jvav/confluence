package org.confluence.mod.mixed;

import net.minecraft.client.resources.sounds.SoundInstance;
import org.jetbrains.annotations.Nullable;

public interface IMusicManager {
    @Nullable SoundInstance confluence$getCurrentMusic();

    void confluence$setMusicBoxOccupied(boolean occupied);

    boolean confluence$isMusicBoxOccupied();
}
