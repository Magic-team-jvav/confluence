package org.confluence.mod.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import org.confluence.mod.integration.terra_entity.TERemoval;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.io.Reader;
import java.util.Map;

@Mixin(SimpleJsonResourceReloadListener.class)
public class SimpleJsonResourceReloadListenerMixin {

    @ModifyArg(method = "scanDirectory", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/GsonHelper;fromJson(Lcom/google/gson/Gson;Ljava/io/Reader;Ljava/lang/Class;)Ljava/lang/Object;"), index = 1)
    private static Reader onScanDirectory2(Reader reader, @Local Map.Entry<ResourceLocation, Resource> entry, @Local(argsOnly = true) ResourceManager resourceManager){
        return TERemoval.processLootTables(reader, resourceManager, entry);
    }
}
