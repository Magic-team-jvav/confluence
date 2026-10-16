package org.confluence.mod.common.data.gen.language;

/// 生成模组联动的中英文翻译
public final class IntegrationLanguageSubProvider {
    private IntegrationLanguageSubProvider() {}

    public static void addTranslations(TranslationConsumer language) {
        language.add("task.confluence.use_life_crystal", "Use Life Crystal", "生命水晶");
        language.add("task.confluence.use_life_crystal.condition.has_life_crystal", "Mainhand holds life crystal", "主手持有生命水晶");
        language.add("task.confluence.use_life_crystal.desc", "Mail will use life crystal to heal herself", "女仆会主动使用生命水晶增加最大生命值");
    }
}
