package org.confluence.mod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import nowebsite.makertechno.the_trackers.TheTrackers;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.Confluence;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.mod.TerraGuns;
import org.confluence.terraentity.TerraEntity;
import org.mesdag.particlestorm.ParticleStorm;
import org.mesdag.thr_dim_particle.TDP;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MergedConfigurationScreen extends Screen {
    private final Screen parent;
    private final String[] modids;

    MergedConfigurationScreen(Screen parent, String... modids) {
        super(Component.translatable("title.confluence.merged_configuration"));
        this.parent = parent;
        this.modids = modids;
    }

    @Override
    protected void init() {
        int i = 0;
        List<Button> buttons = new ArrayList<>();
        for (String modid : modids) {
            Optional<? extends ModContainer> optionalContainer = ModList.get().getModContainerById(modid);
            if (optionalContainer.isEmpty()) continue;
            ModContainer container = optionalContainer.get();
            IConfigScreenFactory factory;
            if (Confluence.MODID.equals(modid)) {
                factory = ConfigurationScreen::new;
            } else {
                Optional<IConfigScreenFactory> optionalFactory = IConfigScreenFactory.getForMod(container.getModInfo());
                if (optionalFactory.isEmpty()) continue;
                factory = optionalFactory.get();
            }

            buttons.add(addRenderableWidget(Button.builder(Component.translatable("modid.name." + modid), button -> {
                assert minecraft != null;
                minecraft.setScreen(factory.createScreen(container, this));
            }).bounds(
                    (width - Button.DEFAULT_WIDTH) / 2,
                    i++ * Button.DEFAULT_HEIGHT,
                    Button.DEFAULT_WIDTH,
                    Button.DEFAULT_HEIGHT
            ).build()));
        }
        int y = (height - i * Button.DEFAULT_HEIGHT) / 2;
        for (Button button : buttons) {
            button.setY(button.getY() + y);
        }

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> onClose()).bounds(
                (width - Button.SMALL_WIDTH) / 2,
                height - Button.DEFAULT_HEIGHT - 6,
                Button.SMALL_WIDTH,
                Button.DEFAULT_HEIGHT
        ).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        assert minecraft != null;
        guiGraphics.drawCenteredString(minecraft.font, title, width / 2, 6, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        assert minecraft != null;
        minecraft.setScreen(parent);
    }

    public static Screen factory(ModContainer container, Screen parent) {
        return new MergedConfigurationScreen(parent,
                Confluence.MODID,
                ConfluenceMagicLib.LIB_ID,
                TerraCurio.MODID,
                TerraEntity.MODID,
                TerraFurniture.MODID,
                TerraGuns.MODID,
                TheTrackers.MOD_ID,
                ParticleStorm.MODID,
                TDP.MODID
        );
    }
}
