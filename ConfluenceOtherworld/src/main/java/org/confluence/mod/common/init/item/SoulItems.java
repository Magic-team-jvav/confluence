package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.item.soulcerer.FallenSoulCoreItem;
import org.confluence.mod.common.item.soulcerer.SoulSkillBookItem;
import org.confluence.mod.common.item.soulcerer.SoulWeaponItem;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;

import java.util.List;

public class SoulItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    public static final DeferredItem<FallenSoulCoreItem> FALLEN_SOUL_CORE = ITEMS.register("fallen_soul_core", () -> new FallenSoulCoreItem(new Item.Properties(), ModRarity.BLUE));
    public static final DeferredItem<SoulSkillBookItem> SOUL_SKILL_BOOK = ITEMS.register("soulcerer_skill_book", () -> new SoulSkillBookItem(new Item.Properties(), ModRarity.WHITE));
    public static final DeferredItem<SoulWeaponItem> BROKEN_GLIMMER = ITEMS.register("broken_glimmer", () -> new SoulWeaponItem(new Item.Properties().stacksTo(1), ModRarity.WHITE, SoulSkillParameters.BROKEN_GLIMMER_DAMAGE, List.of(Confluence.asResource("survival_instinct"))));

    /// 注册魂师物品，不登记尚未定义的具体法器。
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
