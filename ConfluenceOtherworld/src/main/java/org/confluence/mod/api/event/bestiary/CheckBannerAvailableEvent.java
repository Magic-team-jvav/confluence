package org.confluence.mod.api.event.bestiary;

import net.neoforged.bus.api.Event;
import org.confluence.mod.client.handler.bestiary.ClientBestiaryEntry;
import org.confluence.mod.common.init.ModTags;

public class CheckBannerAvailableEvent extends Event {
    private final ClientBestiaryEntry entry;
    private final boolean originalAvailalbe;
    private boolean available;

    public CheckBannerAvailableEvent(ClientBestiaryEntry entry) {
        this.entry = entry;
        this.originalAvailalbe = this.available = entry.isCompleted() && !entry.type.is(ModTags.EntityTypes.BANNER_BLACKLIST);
    }

    public ClientBestiaryEntry getEntry() {
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
