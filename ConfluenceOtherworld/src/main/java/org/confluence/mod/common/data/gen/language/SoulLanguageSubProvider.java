package org.confluence.mod.common.data.gen.language;

public class SoulLanguageSubProvider {
    /// 生成魂师技能、物品、界面和使用提示的中英文翻译。
    public static void addTranslations(TranslationConsumer language) {
        // TODO 补充灵魂总览，灵魂快捷技能切换按键翻译
        language.add("key.confluence.soulcerer.overview", "****", "****");
        language.add("key.confluence.soulcerer.quick_skill_switching", "****", "****");
        language.add("creativetab.confluence.soulcerer", "Confluence | Soul Mage", "汇流来世 | 魂师");
        language.add("itemGroup.confluence.soulcerer_main_weapons", "Primary Weapons", "主武器");
        language.add("itemGroup.confluence.soulcerer_secondary_weapons", "Secondary Weapons", "副武器");
        language.add("itemGroup.confluence.soulcerer_artifacts", "Artifacts", "法器");
        language.add("itemGroup.confluence.soulcerer_skill_books", "Skill Books", "技能书");
        language.add("confluence.soulcerer_skill.survival_instinct.name", "Survival Instinct", "求生欲");
        language.add("item.confluence.fallen_soul_core", "Fallen Soul Core", "堕魂星核");
        language.add("item.confluence.broken_glimmer", "Broken Glimmer", "残破微光");
        language.add("item.confluence.soulcerer_skill_book", "Soul Skill Book", "灵魂技能书");
        language.add("message.confluence.soul_mode.magic", "Traverse the astral morning stars", "流转天外晨星");
        language.add("message.confluence.soul_mode.soul", "Rouse the souls of fallen quietude", "唤起坠寂之魂");
        language.add("message.confluence.soulcerer_skill.learned", "By reading the dust-laden tome, you have acquired %s!", "通过阅读尘封的书籍，你获得了%s！");
        language.add("tooltip.confluence.soulcerer_skill_book.unbound", "No soul skill is bound to this book", "此书尚未绑定灵魂技能");
        language.add("tooltip.confluence.soulcerer_skill_book.invalid", "The bound soul skill is unavailable", "绑定的灵魂技能不存在");
        language.add("confluence.soulcerer_skill.state.learned", "Permanently learned", "永久学会");
        language.add("confluence.soulcerer_skill.state.temporary", "Temporarily available", "临时可用");
        language.add("confluence.soulcerer_skill.state.locked", "Locked", "未解锁");
        language.add("confluence.soulcerer_skill.state.equipped", "Equipped", "已装备");
        language.add("confluence.soulcerer_skill.state.unavailable", "Temporarily unavailable", "暂不可用");
        language.add("confluence.screen.soulcerer_overview", "Soul Skill Overview", "魂师技能总览");
        language.add("confluence.screen.soulcerer_overview.tab.empty", "Empty", "空");
        language.add("confluence.screen.soulcerer_overview.tab.soul", "Soul", "灵魂");
        language.add("confluence.screen.soulcerer_overview.tab.blood_nature", "Blood and Nature", "血与自然");
        language.add("confluence.screen.soulcerer_overview.tab.star_lure", "Star Lure", "星魂");
    }
}
