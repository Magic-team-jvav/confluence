package org.confluence.mod.mixed;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.neoforged.fml.ModLoader;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.CustomWorldIconRegisterEvent;
import org.confluence.mod.common.init.ModSecretSeeds;

import java.util.Optional;

public interface IWorldOptions {
    /**
     * 能获取到服务器的情况下尽量使用如下方法
     *
     * @see IMinecraftServer#confluence$updateSecretFlag(long)
     */
    void confluence$withSecretFlag(long flag);

    long confluence$getSecretFlag();

    void confluence$setLegacyCustomOptions(Optional<String> legacyCustomOptions);

    WorldOptions confluence$copyWithoutSecretFlag();

    long THE_CORRUPTION = 0b0001;
    long TR_CRIMSON = 0b0010;
    long DOUBLE_EVIL = 0b0011;
    long HARDMODE = 0b0100;
    long GRADUATED = 0b1100;

    long DW_MASK = ModSecretSeeds.DRUNK_WORLD.getFlag();
    long NTB_MASK = ModSecretSeeds.NOT_THE_BEES.getFlag();
    long FTW_MASK = ModSecretSeeds.FOR_THE_WORTHY.getFlag();
    long C10_MASK = ModSecretSeeds.CELEBRATIONMK10.getFlag();
    long TC_MASK = ModSecretSeeds.THE_CONSTANT.getFlag();
    long NT_MASK = ModSecretSeeds.NO_TRAPS.getFlag();
    long DDU_MASK = ModSecretSeeds.DONT_DIG_UP.getFlag();
    long GFB_MASK = ModSecretSeeds.GET_FIXED_BOI.getFlag();
    long BW_MASK = ModSecretSeeds.BOULDER_WORLD.getFlag();

    ResourceLocation UNKNOWN_WORLD_ICON = Confluence.asResource("textures/gui/world_icon/unknown.png");
    Long2ObjectMap<ResourceLocation> WORLD_ICON = Util.make(new Long2ObjectOpenHashMap<>(), map -> {
        registerWorldIcon(map, 0, "normal");
        map.put(DW_MASK | DOUBLE_EVIL, Confluence.asResource("textures/gui/world_icon/drunk_world.png"));
        map.put(DW_MASK | DOUBLE_EVIL | HARDMODE, Confluence.asResource("textures/gui/world_icon/drunk_world_hardmode.png"));
        map.put(DW_MASK | DOUBLE_EVIL | GRADUATED, Confluence.asResource("textures/gui/world_icon/drunk_world_graduated.png"));
        registerWorldIcon(map, NTB_MASK, "not_the_bees");
        registerWorldIcon(map, FTW_MASK, "for_the_worthy");
        registerWorldIcon(map, C10_MASK, "celebrationmk10");
        registerWorldIcon(map, TC_MASK, "the_constant");
        registerWorldIcon(map, NT_MASK, "no_traps");
        registerWorldIcon(map, DDU_MASK, "dont_dig_up");
        registerWorldIcon(map, GFB_MASK, "get_fixed_boi");
        registerWorldIcon(map, BW_MASK, "boulder_world");

        CustomWorldIconRegisterEvent event = new CustomWorldIconRegisterEvent(map);
        ModLoader.postEvent(event);
        map.putAll(event.getToAdd());
    });

    static void registerWorldIcon(Long2ObjectMap<ResourceLocation> map, long flag, String base) {
        map.put(flag | THE_CORRUPTION, Confluence.asResource("textures/gui/world_icon/" + base + "_corruption.png"));
        map.put(flag | THE_CORRUPTION | HARDMODE, Confluence.asResource("textures/gui/world_icon/" + base + "_corruption_hardmode.png"));
        map.put(flag | THE_CORRUPTION | GRADUATED, Confluence.asResource("textures/gui/world_icon/" + base + "_corruption_graduated.png"));
        map.put(flag | TR_CRIMSON, Confluence.asResource("textures/gui/world_icon/" + base + "_crimson.png"));
        map.put(flag | TR_CRIMSON | HARDMODE, Confluence.asResource("textures/gui/world_icon/" + base + "_crimson_hardmode.png"));
        map.put(flag | TR_CRIMSON | GRADUATED, Confluence.asResource("textures/gui/world_icon/" + base + "_crimson_graduated.png"));
    }

    static ResourceLocation getWorldIcon(long flag) {
        return WORLD_ICON.getOrDefault(flag, UNKNOWN_WORLD_ICON);
    }
}
