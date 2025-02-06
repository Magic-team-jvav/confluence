package org.confluence.mod.integration.patchouli;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.mod.Confluence;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PatchouliHelper {
    public static final boolean IS_LOADED = ModList.get().isLoaded("patchouli");
    public static final Set<ResourceLocation> UNLOCKED_ENTITY_ENTRIES = new HashSet<>();
    public static final String ENTITY = "entity/";

    public static boolean isBookFromConfluence(ResourceLocation id) {
        return IS_LOADED && id.getNamespace().equals(Confluence.MODID);
    }

    public static boolean isEntityPageUnlocked(ResourceLocation id) {
        if (isBookFromConfluence(id) && id.getPath().startsWith(ENTITY)) {
            return UNLOCKED_ENTITY_ENTRIES.contains(id);
        }
        return true;
    }

    public static void entityEntry(ServerPlayer serverPlayer, boolean unlock, String entry) {
        if (IS_LOADED) {
            PacketDistributor.sendToPlayer(serverPlayer, new PatchouliEntityEntriesPacketS2C(unlock, Collections.singleton(entry)));
        }
    }

    public static void entityEntries(ServerPlayer serverPlayer, boolean unlock, Set<String> entries) {
        if (IS_LOADED) {
            PacketDistributor.sendToPlayer(serverPlayer, new PatchouliEntityEntriesPacketS2C(unlock, entries));
        }
    }
}
