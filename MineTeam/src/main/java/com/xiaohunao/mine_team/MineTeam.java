package com.xiaohunao.mine_team;

import com.mojang.logging.LogUtils;
import com.xiaohunao.mine_team.common.config.MineTeamConfig;
import com.xiaohunao.mine_team.common.init.MTAttachmentTypes;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(MineTeam.MODID)
public class MineTeam {
    public static final String MODID = "mine_team";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final boolean IS_CONFLUENCE_LOADED = ModList.get().isLoaded("confluence");
    public MineTeam(IEventBus modEventBus, ModContainer modContainer) {
        MTAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);

//        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::onFMLCommonSetup);
        modContainer.registerConfig(ModConfig.Type.COMMON, MineTeamConfig.CONFIG, "mine_team.toml");
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    @SubscribeEvent
    public void onFMLCommonSetup(FMLCommonSetupEvent event) {
        MineTeamConfig.loadTamingMaterials();
    }
}
