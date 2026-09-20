package org.confluence.mod.api.event.bestiary;

import net.minecraftforge.eventbus.api.Event;
import org.confluence.mod.client.handler.bestiary.ClientBestiary;
import org.confluence.mod.common.init.ModTags;

public class CheckBannerAvailableEvent extends Event {
    private final ClientBestiary.Entry entry;
    private final boolean originalAvailalbe;
    private boolean available;

    public CheckBannerAvailableEvent(ClientBestiary.Entry entry) {
        this.entry = entry;
        this.originalAvailalbe = this.available = entry.isCompleted() &&
                !entry.type.is(ModTags.EntityTypes.ENEMY_BANNER_BLACKLIST) &&
                entry.type.is(ModTags.EntityTypes.ENEMY_BANNER_WHITELIST);
    }

    public ClientBestiary.Entry getEntry() {
        return entry;
    }

    public boolean isOriginalAvailable() {
        return originalAvailalbe;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
