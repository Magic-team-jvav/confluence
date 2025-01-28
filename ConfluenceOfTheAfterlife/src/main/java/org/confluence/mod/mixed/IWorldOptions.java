package org.confluence.mod.mixed;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.ModLoader;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.CustomWorldIconRegisterEvent;
import org.confluence.mod.common.init.ModSecretSeeds;

import java.util.Optional;

public interface IWorldOptions {
    /**
     * 能获取到服务器的情况下尽量使用如下方法
     * @see IMinecraftServer#confluence$updateSecretFlag(long)
     */
    void confluence$withSecretFlag(long flag);

    long confluence$getSecretFlag();

    void confluence$setLegacyCustomOptions(Optional<String> legacyCustomOptions);

    static long getSecretFlag(MinecraftServer server) {
        return ((IWorldOptions) server.getWorldData().worldGenOptions()).confluence$getSecretFlag();
    }

    long THE_CORRUPTION = 0b001;
    long TR_CRIMSON = 0b010;
    long DOUBLE_EVIL = THE_CORRUPTION | TR_CRIMSON;
    long HARDMODE = 0b100;

    long DW_MASK = ModSecretSeeds.DRUNK_WORLD.getFlag();
    long NTB_MASK = ModSecretSeeds.NOT_THE_BEES.getFlag();
    long FTW_MASK = ModSecretSeeds.FOR_THE_WORTHY.getFlag();
    long C10_MASK = ModSecretSeeds.CELEBRATIONMK10.getFlag();
    long TC_MASK = ModSecretSeeds.THE_CONSTANT.getFlag();
    long NT_MASK = ModSecretSeeds.NO_TRAPS.getFlag();
    long DDU_MASK = ModSecretSeeds.DONT_DIG_UP.getFlag();
    long GFB_MASK = ModSecretSeeds.GET_FIXED_BOI.getFlag();

    ResourceLocation UNKNOWN_WORLD_ICON = Confluence.asResource("missing");
    Long2ObjectMap<ResourceLocation> WORLD_ICON = Util.make(new Long2ObjectOpenHashMap<>(), map -> {
        map.put(THE_CORRUPTION, Confluence.asResource("missing"));
        map.put(THE_CORRUPTION | HARDMODE, Confluence.asResource("missing"));

        map.put(TR_CRIMSON, Confluence.asResource("textures/gui/world_icon/crimson.png"));
        map.put(TR_CRIMSON | HARDMODE, Confluence.asResource("missing"));

        map.put(DOUBLE_EVIL, Confluence.asResource("missing"));
        map.put(DOUBLE_EVIL | HARDMODE, Confluence.asResource("missing"));

        map.put(DW_MASK | DOUBLE_EVIL, Confluence.asResource("missing"));
        map.put(DW_MASK | DOUBLE_EVIL | HARDMODE, Confluence.asResource("missing"));

        map.put(NTB_MASK | THE_CORRUPTION, Confluence.asResource("missing"));
        map.put(NTB_MASK | THE_CORRUPTION | HARDMODE, Confluence.asResource("missing"));
        map.put(NTB_MASK | TR_CRIMSON, Confluence.asResource("missing"));
        map.put(NTB_MASK | TR_CRIMSON | HARDMODE, Confluence.asResource("missing"));

        map.put(FTW_MASK | THE_CORRUPTION, Confluence.asResource("missing"));
        map.put(FTW_MASK | THE_CORRUPTION | HARDMODE, Confluence.asResource("missing"));
        map.put(FTW_MASK | TR_CRIMSON, Confluence.asResource("missing"));
        map.put(FTW_MASK | TR_CRIMSON | HARDMODE, Confluence.asResource("missing"));

        map.put(C10_MASK | THE_CORRUPTION, Confluence.asResource("missing"));
        map.put(C10_MASK | THE_CORRUPTION | HARDMODE, Confluence.asResource("missing"));
        map.put(C10_MASK | TR_CRIMSON, Confluence.asResource("missing"));
        map.put(C10_MASK | TR_CRIMSON | HARDMODE, Confluence.asResource("missing"));

        map.put(TC_MASK | THE_CORRUPTION, Confluence.asResource("missing"));
        map.put(TC_MASK | THE_CORRUPTION | HARDMODE, Confluence.asResource("missing"));
        map.put(TC_MASK | TR_CRIMSON, Confluence.asResource("missing"));
        map.put(TC_MASK | TR_CRIMSON | HARDMODE, Confluence.asResource("missing"));

        map.put(NT_MASK | THE_CORRUPTION, Confluence.asResource("missing"));
        map.put(NT_MASK | THE_CORRUPTION | HARDMODE, Confluence.asResource("missing"));
        map.put(NT_MASK | TR_CRIMSON, Confluence.asResource("missing"));
        map.put(NT_MASK | TR_CRIMSON | HARDMODE, Confluence.asResource("missing"));

        map.put(DDU_MASK | THE_CORRUPTION, Confluence.asResource("missing"));
        map.put(DDU_MASK | THE_CORRUPTION | HARDMODE, Confluence.asResource("missing"));
        map.put(DDU_MASK | TR_CRIMSON, Confluence.asResource("missing"));
        map.put(DDU_MASK | TR_CRIMSON | HARDMODE, Confluence.asResource("missing"));

        map.put(GFB_MASK | THE_CORRUPTION, Confluence.asResource("missing"));
        map.put(GFB_MASK | THE_CORRUPTION | HARDMODE, Confluence.asResource("missing"));
        map.put(GFB_MASK | TR_CRIMSON, Confluence.asResource("missing"));
        map.put(GFB_MASK | TR_CRIMSON | HARDMODE, Confluence.asResource("missing"));

        CustomWorldIconRegisterEvent event = new CustomWorldIconRegisterEvent(map);
        ModLoader.postEvent(event);
        map.putAll(event.getToAdd());
    });

    static ResourceLocation getWorldIcon(long flag) {
        return WORLD_ICON.getOrDefault(flag, UNKNOWN_WORLD_ICON);
    }
}
