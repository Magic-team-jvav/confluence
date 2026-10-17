package org.confluence.mod.common.data.gen.language;

import org.confluence.mod.common.data.TranslationConsumer;

/// 生成方块名称的中英文翻译
public final class BlockLanguageSubProvider {
    private BlockLanguageSubProvider() {}

    public static void addTranslations(TranslationConsumer language) {
        language.add("block.confluence.failed_skull_wall", "Failed Skull Wall", "失败品头颅墙");
        language.add("block.confluence.test", "Test", "测试方块");
    }
}
