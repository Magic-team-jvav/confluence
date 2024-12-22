package org.confluence.mod.client.gui.wiki;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.util.GuiUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupWikiScreen extends BaseWikiScreen{
    private static Map<String, List<DeferredRegister<?>>> wikiMap = new HashMap<>();
    private static Map<String, List<String>> nameMap = new HashMap<>();

    private ResourceLocation icon;
    private Component name;
    private ArrayList<TypeWikiScreen> wiki;
    private String id;
    private TypeWikiScreen.WikiRenderType type;
    private boolean isInTitleScreen;

    public GroupWikiScreen(ResourceLocation icon, Component name, String id,
                           TypeWikiScreen.WikiRenderType type, boolean isInTitleScreen) {
        super(Component.translatable("title.confluence.group"));
        this.icon = icon;
        this.name = name;
        this.id = id;
        this.type = type;
        this.isInTitleScreen = isInTitleScreen;
        wiki = new ArrayList<>();
        wikiMap.get(this.id).forEach((r) -> {
            wiki.add(new TypeWikiScreen(this, r, TypeWikiScreen.WikiRenderType.ITEM));
        });
    }

    public static void putWikiType(String id, List<DeferredRegister<?>> registries,
                                   List<String> names) {
        wikiMap.put(id, registries);
        nameMap.put(id, names);
    }

    public Component getName() {
        return name;
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        for (int i = 0; i < wiki.size(); i++) {
            if (type.equals(TypeWikiScreen.WikiRenderType.ITEM)) {
                renderItemButton(guiGraphics, i, wiki.get(i));
            }
        }
    }
    private void renderItemButton(GuiGraphics guiGraphics, int i,
                                  TypeWikiScreen wikiScreen){
        DeferredItem<?> d = (DeferredItem<?>) wikiScreen.getReg().getEntries().toArray()[0];
        Item item = d.asItem();
        int finalI = i;
        Button g = GuiUtils.buildItemButton(guiGraphics, item,
                31 + i * 30, height - 150, 20, 20, 16, 16,
                (e) -> this.minecraft.setScreen(wiki.get(finalI)),
                Component.translatable("wiki.confluence.type_" + nameMap.get(id).get(i)), minecraft);
        this.addRenderableWidget(g);
    }

    @Override
    public Screen getParentScreen() {
        if (isInTitleScreen){
            return new MainWikiScreen(true);
        } else {
            return new MainWikiScreen(false);
        }
    }
}
