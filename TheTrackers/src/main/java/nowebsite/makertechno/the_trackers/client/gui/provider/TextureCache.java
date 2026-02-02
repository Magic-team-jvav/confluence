package nowebsite.makertechno.the_trackers.client.gui.provider;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nowebsite.makertechno.the_trackers.TheTrackers;
import nowebsite.makertechno.the_trackers.client.gui.components.Icon;
import nowebsite.makertechno.the_trackers.core.tool.TextureBuildTool;
import org.jetbrains.annotations.Contract;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class TextureCache {
    private static final Map<String, Icon> ICON_CACHE = new HashMap<>();

    static {
        initIcons(
                /* entity icons */
                "ender_dragon_head",
                "wither_head",
                "king_slime",
                "eye_of_cthulhu",
                "brain_of_cthulhu",
                "queen_bee",
                "eater_of_worlds",
                "skeletron",


                /* pointers */
                "normal_white",
                "normal",
                "normal_green",
                "point_x"
        );
    }

    public static Icon getIcon(String regName) {
        return ICON_CACHE.computeIfAbsent(regName, key -> {
            ResourceLocation location = getResourceFromModLocale(key);
            return TextureBuildTool.initIcon(key, location, Icon::new).orElse(Icon.NONE);
        });
    }
    public static void initIcons(String ...strings) {
        for (String key : strings) {
            try {
                ICON_CACHE.put(key, TextureBuildTool.initIcon(key, getResourceFromModLocale(key), Icon::new).orElseThrow());
            } catch (NoSuchElementException e) {
                warnUnload(key);
            }
        }
    }

    private static void warnUnload(String key) {
        TheTrackers.LOGGER.warn("Failed to load image named {}, check if it was not in The Trackers' resource with a valid path or in invalid config file.", key);
    }

    @Contract("_ -> new")
    private static ResourceLocation getResourceFromModLocale(String name) {
        return ResourceLocation.fromNamespaceAndPath(TheTrackers.MOD_ID, "textures/icons/"+name+".png");
    }
}
