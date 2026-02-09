package org.confluence.mod.mixed;

import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;

public interface IPlayerAdvancements {
    void confluence$load(ServerAdvancementManager manager, PlayerAdvancements.Data data);

    static IPlayerAdvancements of(PlayerAdvancements advancements) {
        return (IPlayerAdvancements) advancements;
    }
}
