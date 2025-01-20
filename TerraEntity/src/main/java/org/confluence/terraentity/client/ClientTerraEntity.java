package org.confluence.terraentity.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import static org.confluence.terraentity.TerraEntity.MODID;

@Mod(value = MODID, dist = Dist.CLIENT)
public class ClientTerraEntity {

    public ClientTerraEntity(IEventBus modEventBus, ModContainer container) {

        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
