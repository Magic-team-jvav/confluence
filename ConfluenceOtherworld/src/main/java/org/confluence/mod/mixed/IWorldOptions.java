package org.confluence.mod.mixed;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraftforge.fml.ModLoader;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.CustomWorldIconRegisterEvent;
import org.confluence.mod.common.init.ModSecretSeeds;

public interface IWorldOptions {
    void confluence$resetSecretFlag();

    /// 能获取到服务器的情况下尽量使用如下方法
    ///
    /// @see IMinecraftServer#confluence$updateSecretFlag(long)
    void confluence$withSecretFlag(long flag);

    long confluence$getSecretFlag();

    void confluence$setVersion(int version);

    int confluence$getVersion();

    WorldOptions confluence$copyWithoutSecretFlag();

    int CURRENT_VERSION = 1;

    long THE_CORRUPTION = 0b00000001;
    long THE_CRIMSON = 0b00000010;
    long DOUBLE_EVIL = 0b00000011;
    long HARDMODE = 0b00000100;
    long GRADUATED = 0b00001100;
    long SECRET_SEED = Long.MAX_VALUE >> ModSecretSeeds.RESERVE << ModSecretSeeds.RESERVE;

    long DW_MASK = ModSecretSeeds.DRUNK_WORLD.getFlag();
    long NTB_MASK = ModSecretSeeds.NOT_THE_BEES.getFlag();
    long FTW_MASK = ModSecretSeeds.FOR_THE_WORTHY.getFlag();
    long C10_MASK = ModSecretSeeds.CELEBRATIONMK10.getFlag();
    long TC_MASK = ModSecretSeeds.THE_CONSTANT.getFlag();
    long DDU_MASK = ModSecretSeeds.DONT_DIG_UP.getFlag();
    long NT_MASK = ModSecretSeeds.NO_TRAPS.getFlag();
    long GFB_MASK = ModSecretSeeds.GET_FIXED_BOI.getFlag();
    long SKYBLOCK_MASK = ModSecretSeeds.SKYBLOCK.getFlag();
    long BW_MASK = ModSecretSeeds.BOULDER_WORLD.getFlag();
    long REALLY_SMALL_MASK = ModSecretSeeds.REALLY_SMALL.getFlag();
    long TOO_EASY_MASK = ModSecretSeeds.TOO_EASY.getFlag();


    ResourceLocation UNKNOWN_WORLD_ICON = Confluence.asResource("world_icon/unknown");
    Long2ObjectMap<ResourceLocation> WORLD_ICON = Util.make(new Long2ObjectOpenHashMap<>(), map -> {
        registerWorldIcon(map, 0, "normal");
        map.put(DW_MASK | DOUBLE_EVIL, Confluence.asResource("world_icon/drunk_world"));
        map.put(DW_MASK | DOUBLE_EVIL | HARDMODE, Confluence.asResource("world_icon/drunk_world_hardmode"));
        map.put(DW_MASK | DOUBLE_EVIL | GRADUATED, Confluence.asResource("world_icon/drunk_world_graduated"));
        registerWorldIcon(map, NTB_MASK, "not_the_bees");
        registerWorldIcon(map, FTW_MASK, "for_the_worthy");
        registerWorldIcon(map, C10_MASK, "celebrationmk10");
        registerWorldIcon(map, TC_MASK, "the_constant");
        registerWorldIcon(map, NT_MASK, "no_traps");
        registerWorldIcon(map, DDU_MASK, "dont_dig_up");
        registerWorldIcon(map, GFB_MASK, "get_fixed_boi");
        registerWorldIcon(map, BW_MASK, "boulder_world");

        CustomWorldIconRegisterEvent event = new CustomWorldIconRegisterEvent(map);
        ModLoader.postEventWrapContainerInModOrder(event);
        map.putAll(event.getToAdd());
    });

    static void registerWorldIcon(Long2ObjectMap<ResourceLocation> map, long flag, String base) {
        map.put(flag | THE_CORRUPTION, Confluence.asResource("world_icon/" + base + "_corruption"));
        map.put(flag | THE_CORRUPTION | HARDMODE, Confluence.asResource("world_icon/" + base + "_corruption_hardmode"));
        map.put(flag | THE_CORRUPTION | GRADUATED, Confluence.asResource("world_icon/" + base + "_corruption_graduated"));
        map.put(flag | THE_CRIMSON, Confluence.asResource("world_icon/" + base + "_crimson"));
        map.put(flag | THE_CRIMSON | HARDMODE, Confluence.asResource("world_icon/" + base + "_crimson_hardmode"));
        map.put(flag | THE_CRIMSON | GRADUATED, Confluence.asResource("world_icon/" + base + "_crimson_graduated"));
    }

    static ResourceLocation getWorldIcon(long flag) {
        return WORLD_ICON.getOrDefault(flag, UNKNOWN_WORLD_ICON);
    }

    static IWorldOptions of(WorldOptions options) {
        return (IWorldOptions) options;
    }
}
