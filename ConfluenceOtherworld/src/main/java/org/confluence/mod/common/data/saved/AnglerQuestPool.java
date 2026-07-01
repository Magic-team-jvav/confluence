package org.confluence.mod.common.data.saved;

import org.confluence.mod.common.data.gen.angler.AnglerQuestProvider;

import java.util.List;

public enum AnglerQuestPool {
    INSTANCE;

    private volatile List<AnglerQuestEntry> entries;

    public List<AnglerQuestEntry> getEntries() {
        if (entries == null) {
            synchronized (this) {
                if (entries == null) {
                    entries = List.copyOf(AnglerQuestProvider.buildEntries());
                }
            }
        }
        return entries;
    }
}
