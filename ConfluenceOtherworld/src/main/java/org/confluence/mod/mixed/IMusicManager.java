package org.confluence.mod.mixed;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;
import org.jetbrains.annotations.Nullable;

public interface IMusicManager {
    @Nullable SoundInstance confluence$getCurrentMusic();

    void confluence$setMusicBoxOccupied(State occupied);

    State confluence$getMusicBoxOccupied();

    static void reset(MusicManager musicManager) {
        IMusicManager manager = (IMusicManager) musicManager;
        if (manager.confluence$getCurrentMusic() == null || manager.confluence$getMusicBoxOccupied().isAccessory()) {
            manager.confluence$setMusicBoxOccupied(State.NONE);
        }
    }

    enum State {
        ACCESSORY,
        BLOCK,
        NONE;

        public boolean isAccessory() {
            return this == ACCESSORY;
        }

        public boolean isBlock() {
            return this == BLOCK;
        }

        public boolean isNone() {
            return this == NONE;
        }
    }
}
