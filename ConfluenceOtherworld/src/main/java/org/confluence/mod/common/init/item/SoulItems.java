package org.confluence.mod.common.init.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModSoulSkills;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.item.soulcerer.FallenSoulCoreItem;
import org.confluence.mod.common.item.soulcerer.SoulBooks;
import org.confluence.mod.common.item.soulcerer.SoulKnifeItem;
import org.confluence.mod.common.item.soulcerer.SoulLanternItem;
import org.confluence.mod.common.item.soulcerer.SoulSkillBookItem;
import org.confluence.mod.common.item.soulcerer.SoulWeaponItem;
import org.confluence.mod.common.item.soulcerer.SoulWeaponProperties;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.confluence.mod.util.PlayerUtils;

import java.util.List;
import java.util.Map;

public class SoulItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Confluence.MODID);
    public static final DeferredItem<FallenSoulCoreItem> FALLEN_SOUL_CORE = ITEMS.register("fallen_soul_core", () -> new FallenSoulCoreItem(new Item.Properties(), ModRarity.BLUE));
    public static final DeferredItem<SoulSkillBookItem> SOUL_SKILL_BOOK = ITEMS.register("soulcerer_skill_book", () -> new SoulSkillBookItem(new Item.Properties(), ModRarity.WHITE));

    public static final DeferredItem<SoulWeaponItem> BROKEN_GLIMMER = ITEMS.register("broken_glimmer", () -> new SoulWeaponItem(
            new Item.Properties(), ModRarity.WHITE,
            SoulWeaponProperties.builder()
                    .damage(SoulSkillParameters.BROKEN_GLIMMER_DAMAGE)
                    .soulSkill(Confluence.asResource("survival_instinct"))
                    .trait(SoulWeaponItem.Trait.HEAL_ON_SOUL_GAIN)
                    .build()));
    public static final DeferredItem<SoulLanternItem> SOUL_CONDUIT = ITEMS.register("soul_conduit", () -> new SoulLanternItem(
            new Item.Properties(), ModRarity.WHITE,
            SoulWeaponProperties.builder()
                    .damage(5.0F)
                    .criticalChance(0.04F)
                    .build()));
    public static final DeferredItem<SoulLanternItem> PILLAGING_FLAME = ITEMS.register("pillaging_flame", () -> new SoulLanternItem(
            new Item.Properties(), ModRarity.BLUE,
            SoulWeaponProperties.builder()
                    .damage(7.0F)
                    .criticalChance(0.04F)
                    .soulSkill(ModSoulSkills.SOUL_PLUNDER.get().getId())
                    .build()));
    public static final DeferredItem<SoulLanternItem> TRUFFLE_LANTERN = ITEMS.register("truffle_lantern", () -> new SoulLanternItem(
            new Item.Properties(), ModRarity.GREEN,
            SoulWeaponProperties.builder()
                    .damage(8.0F)
                    .criticalChance(0.04F)
                    .soulSkill(ModSoulSkills.CONFUSE_SPORES.get().getId())
                    .build()));
    public static final DeferredItem<SoulLanternItem> SOUL_CHANT_GUIDE = ITEMS.register("soul_chant_guide", () -> new SoulLanternItem(
            new Item.Properties(), ModRarity.GREEN,
            SoulWeaponProperties.builder()
                    .damage(9.0F)
                    .criticalChance(0.08F)
                    .trait(SoulWeaponItem.Trait.LURE_PIERCE)
                    .build()));
    public static final DeferredItem<SoulLanternItem> LIFE_SPLENDOR = ITEMS.register("life_splendor", () -> new SoulLanternItem(
            new Item.Properties(), ModRarity.ORANGE,
            SoulWeaponProperties.builder()
                    .damage(11.0F)
                    .criticalChance(0.04F)
                    .soulSkill(ModSoulSkills.NATURES_WRATH.get().getId())
                    .trait(SoulWeaponItem.Trait.POISON_ON_SKILL_HIT)
                    .build()));
    public static final DeferredItem<SoulLanternItem> STAR_SPIRIT = ITEMS.register("star_spirit", () -> new SoulLanternItem(
            new Item.Properties(), ModRarity.ORANGE,
            SoulWeaponProperties.builder()
                    .damage(11.0F)
                    .criticalChance(0.04F)
                    .soulSkill(ModSoulSkills.STAR_CALL.get().getId())
                    .trait(SoulWeaponItem.Trait.STAR_ON_SKILL_HIT)
                    .build()));
    public static final DeferredItem<SoulLanternItem> DAY_AND_NIGHT_ALIKE = ITEMS.register("day_and_night_alike", () -> new SoulLanternItem(
            new Item.Properties(), ModRarity.LIGHT_RED,
            SoulWeaponProperties.builder()
                    .damage(23.0F)
                    .criticalChance(0.04F)
                    .soulSkills(List.of(ModSoulSkills.KARMA_FLAME.get().getId(), ModSoulSkills.PROFANE_SOUL.get().getId()))
                    .trait(SoulWeaponItem.Trait.SHADOWFLAME_ON_SKILL_HIT)
                    .build()));
    public static final DeferredItem<SoulLanternItem> SENIOR_PATHFINDER_PRO = ITEMS.register("senior_pathfinder_pro", () -> new SoulLanternItem(
            new Item.Properties(), ModRarity.YELLOW,
            SoulWeaponProperties.builder()
                    .damage(16.0F)
                    .criticalChance(0.04F)
                    .trait(SoulWeaponItem.Trait.LURE_HOMING)
                    .build()));

    /// 副武器：战士侧武器，只吃近战伤害加成（6 点伤害 / 1.6 攻速 / 4% 基础暴击）。
    public static final DeferredItem<SoulKnifeItem> SPIRITWARD_BLADE = ITEMS.register("spiritward_blade", () -> new SoulKnifeItem(
            new Item.Properties().component(DataComponents.ATTRIBUTE_MODIFIERS,
                    ModItems.createAttributes(ModTiers.UNBREAKABLE, 4.0F, -2.4F, ModItems.attributes(0, 0.4F))),
            ModRarity.BLUE));

    /// 首次获得即永久学会的技能：物品 → 技能 ID。新增首得解锁只在此登记，判定入口为 PlayerUtils.learnSoulSkillsOnFirstObtain。
    public static final Map<DeferredItem<? extends Item>, List<ResourceLocation>> FIRST_OBTAIN_SOUL_SKILLS = Map.of(
            LIFE_SPLENDOR, List.of(ModSoulSkills.LAW_OF_NATURE.get().getId())
    );

    /// 注册魂师物品，不登记尚未定义的具体法器。
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    /// 生成绑定指定书籍的通用技能书，供创造栏、战利品与交易共用。
    public static ItemStack soulBook(ResourceLocation bookId) {
        ItemStack stack = SOUL_SKILL_BOOK.toStack();
        stack.set(ModDataComponentTypes.SOUL_SKILL.get(), bookId);
        stack.set(ConfluenceMagicLib.MOD_RARITY.get(), SoulBooks.rarity(bookId));
        return stack;
    }

    /// 玻璃瓶装魂：灵魂条状态下右键玻璃瓶，消耗 {@link SoulSkillParameters#SOUL_BOTTLE_SOUL_COST} 点灵魂得到魂瓶。
    ///
    /// 客户端只做可用性判断并回成功，实际扣除与替换只在服务端执行，避免两端状态分叉。
    public static boolean canFillSoulBottle(Player player) {
        return player != null
                && PlayerUtils.isSoulMode(player)
                && PlayerUtils.getSoul(player) >= SoulSkillParameters.SOUL_BOTTLE_SOUL_COST;
    }

    public static void fillSoulBottle(ServerPlayer player, InteractionHand hand) {
        if (!canFillSoulBottle(player) || !PlayerUtils.consumeSoul(player, SoulSkillParameters.SOUL_BOTTLE_SOUL_COST)) {
            return;
        }
        Level level = player.level();
        ItemStack inHand = player.getItemInHand(hand);
        player.setItemInHand(hand, ItemUtils.createFilledResult(inHand, player, FunctionalBlocks.SOUL_BOTTLE.toStack()));
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PICKUP, player.blockPosition());
        player.awardStat(Stats.ITEM_USED.get(PotionItems.BOTTLE.get()));
    }
}
