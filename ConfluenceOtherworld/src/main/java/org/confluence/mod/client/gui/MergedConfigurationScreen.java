package org.confluence.mod.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import nowebsite.makertechno.the_trackers.TheTrackers;
import org.confluence.mod.Confluence;
import org.confluence.terra_curio.TerraCurio;
import org.mesdag.opallight.OpalLight;
import org.mesdag.particlestorm.ParticleStorm;
import org.mesdag.portlib.client.gui.PortConfigurationScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

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
            BiFunction<ModContainer, Screen, Screen> factory = Confluence.MODID.equals(modid)
                    ? PortConfigurationScreen::new
                    : ConfigScreenHandler.getScreenFactoryFor(container.getModInfo())
                    .<BiFunction<ModContainer, Screen, Screen>>map(func -> (mod, parent) -> func.apply(minecraft, parent))
                    .orElseGet(() -> PortConfigurationScreen::new);

            String nameKey = "modid.name." + modid;
            Component name = I18n.exists(nameKey) ? Component.translatable(nameKey) : Component.literal(container.getModInfo().getDisplayName());
            buttons.add(addRenderableWidget(Button.builder(name, button -> {
                assert minecraft != null;
                minecraft.setScreen(factory.apply(container, this));
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
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        assert minecraft != null;
        guiGraphics.drawCenteredString(minecraft.font, title, width / 2, 6, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        assert minecraft != null;
        minecraft.setScreen(parent);
    }

    public static Screen factory(Minecraft minecraft, Screen parent) {
        return new MergedConfigurationScreen(parent,
                Confluence.MODID,
                TerraCurio.MODID,
                TheTrackers.MOD_ID,
                ParticleStorm.MODID,
                OpalLight.MODID
        );
    }
}
