package org.confluence.mod.common.data.gen.language;

import org.confluence.mod.common.data.TranslationConsumer;
import org.confluence.mod.common.init.item.SoulItems;

public class SoulLanguageSubProvider {
    /// 生成魂师技能、物品、界面和使用提示的中英文翻译。
    public static void addTranslations(TranslationConsumer language) {
        // 魂师涉及的按键设置
        language.add("key.confluence.soulcerer.overview", "Soulcerer Overview", "魂师技能总览");
        language.add("key.confluence.soulcerer.skill_switching", "Switch Soul Skill", "切换灵魂技能");
        language.add("creativetab.confluence.soulcerer", "Confluence | Soul Mage", "汇流来世 | 魂师");
        language.add("itemGroup.confluence.soulcerer_main_weapons", "Primary Weapons", "主武器");
        language.add("itemGroup.confluence.soulcerer_secondary_weapons", "Secondary Weapons", "副武器");
        language.add("itemGroup.confluence.soulcerer_artifacts", "Artifacts", "法器");
        language.add("itemGroup.confluence.soulcerer_skill_books", "Skill Books", "技能书");
        language.add("confluence.soulcerer_skill.survival_instinct.name", "Survival Instinct", "求生欲");
        language.add("item.confluence.fallen_soul_core", "Fallen Soul Core", "堕魂星核");
        language.add("item.confluence.broken_glimmer", "Broken Glimmer", "残破微光");
        language.add("item.confluence.soulcerer_skill_book", "Soul Skill Book", "灵魂技能书");
        language.add("confluence.soul_book.psychopomp.name", "Psychopomp", "引渡者");
        language.add("confluence.soul_book.raiders_path.name", "Raiders' Path", "劫掠之道");
        language.add("confluence.soul_book.soul_surge.name", "Soul Surge", "灵魂激荡");
        language.add("confluence.soul_book.harvester.name", "Harvester", "收割者");
        language.add("confluence.soul_book.firmament_reversed.name", "Firmament Reversed", "天穹倒转");
        language.add("confluence.soul_book.blood_world_wrath.name", "Blood and World's Wrath", "血与世界之怒");
        language.add("confluence.soul_book.survival_instinct.name", "Survival Instinct", "求生欲");
        language.add(SoulItems.SOUL_CONDUIT.get(), "Soul Conduit", "领魂之灯");
        language.add(SoulItems.PILLAGING_FLAME.get(), "Pillaging Flame", "劫掠之火");
        language.add(SoulItems.SOUL_CHANT_GUIDE.get(), "Soul Chant Guide", "颂魂指引");
        language.add(SoulItems.TRUFFLE_LANTERN.get(), "Truffle Lantern", "松露灯");
        language.add(SoulItems.LIFE_SPLENDOR.get(), "Life Splendor", "生机灿烂");
        language.add(SoulItems.STAR_SPIRIT.get(), "Star Spirit", "星魄");
        language.add(SoulItems.DAY_AND_NIGHT_ALIKE.get(), "Day and Night Alike", "无分昼夜");
        language.add(SoulItems.SENIOR_PATHFINDER_PRO.get(), "Senior Pathfinder Pro", "带路的老前辈");
        language.add(SoulItems.SPIRITWARD_BLADE.get(), "Spiritward Blade", "祈灵之刃");
        language.add("message.confluence.soul_mode.magic", "Traverse the astral morning stars", "流转天外晨星");
        language.add("message.confluence.soul_mode.soul", "Rouse the souls of fallen quietude", "唤起坠寂之魂");
        language.add("message.confluence.soulcerer_skill.learned", "By reading the dust-laden tome, you have acquired %s!", "通过阅读尘封的书籍，你获得了%s！");
        language.add("message.confluence.soulcerer_skill.first_obtain", "By obtaining %s, you have acquired %s!", "获得%s，你习得了%s！");
        language.add("tooltip.confluence.soulcerer_skill_book.unbound", "No soul skill is bound to this book", "此书尚未绑定灵魂技能");
        language.add("message.confluence.soulcerer_skill.review", "A familiar read: soul damage increased", "温故知新：灵魂伤害提高");
        language.add("message.confluence.soul_book.harvester", "Soul lure skills have grown mightier!", "引魂系列技能的威力增强了！");
        language.add("tooltip.confluence.soulcerer_skill_book.required", "Requires permanently learning %s first", "需要先永久学会%s");
        language.add("tooltip.confluence.soulcerer_skill_book.psychopomp", "Soul lure skills deal 2 more damage and their projectiles bounce once more", "引魂系列技能伤害+2，且引魂弹幕额外弹射一次");
        language.add("tooltip.confluence.soulcerer_skill_book.invalid", "The bound soul skill is unavailable", "绑定的灵魂技能不存在");
        language.add("confluence.soulcerer_skill.state.learned", "Permanently unlocked", "已永久解锁");
        language.add("confluence.soulcerer_skill.state.temporary", "Temporarily available", "临时可用");
        language.add("confluence.soulcerer_skill.state.locked", "Locked", "未解锁");
        language.add("confluence.soulcerer_skill.state.equipped", "Equipped", "已装备");
        language.add("confluence.soulcerer_skill.state.unavailable", "Temporarily unavailable", "暂不可用");
        language.add("confluence.screen.soulcerer_overview", "Soul Skill Overview", "魂师技能总览");
        language.add("confluence.screen.soulcerer_overview.background_toggle", "Background rendering: on / off (off keeps only the base colour and the skills)", "背景渲染开关：关掉后只保留底色与技能，其余背景层全部停画");
        language.add("confluence.screen.soulcerer_overview.tab.empty", "Empty", "空");
        language.add("confluence.screen.soulcerer_overview.tab.soul", "Soul", "灵魂");
        language.add("confluence.screen.soulcerer_overview.tab.blood_nature", "Blood and Nature", "血与自然");
        language.add("confluence.screen.soulcerer_overview.tab.star_lure", "Star Lure", "星魂");
    }
}
