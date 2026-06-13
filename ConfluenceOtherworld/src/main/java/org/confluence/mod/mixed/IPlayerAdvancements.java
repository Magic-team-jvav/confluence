package org.confluence.mod.mixed;

import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import org.confluence.lib.mixed.SelfGetter;

import java.util.Map;

public interface IPlayerAdvancements extends SelfGetter<PlayerAdvancements> {
    void confluence$load(ServerAdvancementManager manager, Map<ResourceLocation, AdvancementProgress> data);

    static IPlayerAdvancements of(PlayerAdvancements advancements) {
        return (IPlayerAdvancements) advancements;
    }
}
