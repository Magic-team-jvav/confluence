package org.confluence.mod.integration.create.ponder;

import net.createmod.ponder.foundation.PonderIndex;
import org.confluence.lib.util.LibUtils;

import java.util.function.BiConsumer;

public class PonderHelper {
    public static final boolean IS_LOADED = LibUtils.isModLoaded("ponder");

    public static void registerPlugin() {
        if (IS_LOADED) {
            try {
                PonderIndex.addPlugin(ModPonderPlugin.class.getDeclaredConstructor().newInstance());
            } catch (Exception ignored) {}
        }
    }

    public static void addTranslateKeys(BiConsumer<String, String> consumer, boolean en) {
        addTranslateKey("using_altar", consumer, en, "Using Altar", "使用祭坛", new String[]{
                "Right click with item to store materials",
                "Right click while sneaking to take materials",
                "Left click to crafting once",
                "Left click while sneaking to quick crafting"
        }, new String[]{
                "手持物品右击以存入材料",
                "潜行右击以取出材料",
                "左击以合成一个",
                "潜行左击以快速合成"
        });
        addTranslateKey("connecting", consumer, en, "Connecting", "连接", new String[]{
                "Select first mechanical block",
                "Select second mechanical block"
        }, new String[]{
                "选择第一个机械方块",
                "选择第二个机械方块"
        });
    }

    private static void addTranslateKey(String sceneId, BiConsumer<String, String> consumer, boolean isEn, String enHeader, String zhHeader, String[] en, String[] zh) {
        if (isEn) {
            consumer.accept("confluence.ponder." + sceneId + ".header", enHeader);
        } else {
            consumer.accept("confluence.ponder." + sceneId + ".header", zhHeader);
        }
        for (int i = 0; i < en.length; i++) {
            if (isEn) {
                consumer.accept("confluence.ponder." + sceneId + ".text_" + (i + 1), en[i]);
            } else {
                consumer.accept("confluence.ponder." + sceneId + ".text_" + (i + 1), zh[i]);
            }
        }
    }
}
