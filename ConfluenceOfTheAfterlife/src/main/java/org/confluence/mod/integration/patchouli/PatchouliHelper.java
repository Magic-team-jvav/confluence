package org.confluence.mod.integration.patchouli;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import org.confluence.mod.Confluence;
import vazkii.patchouli.api.PatchouliAPI;

public class PatchouliHelper {
    public static final boolean LOADED = ModList.get().isLoaded("patchouli");
    public static final ResourceLocation CONFLUENCE_WIKI = Confluence.asResource("wiki");

    public static void openBookGUI(ServerPlayer serverPlayer, ResourceLocation bookId) {
        if (LOADED) {
            PatchouliAPI.get().openBookGUI(serverPlayer, bookId);
        } else {
            throw new RuntimeException("Patchouli is not installed!");
        }
    }

    public static ItemStack getBook(ResourceLocation bookId) {
        if (LOADED) {
            return PatchouliAPI.get().getBookStack(bookId);
        }
        throw new RuntimeException("Patchouli is not installed!");
    }
}
