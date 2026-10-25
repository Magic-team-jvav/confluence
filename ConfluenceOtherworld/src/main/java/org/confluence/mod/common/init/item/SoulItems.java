package org.confluence.mod.common.init.item;

import net.minecraft.network.chat.Component;
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
import net.minecraftforge.eventbus.api.IEventBus;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModSoulSkills;
import org.confluence.mod.common.init.ModTiers;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.item.soulcerer.*;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.confluence.mod.util.PlayerUtils;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.confluence.mod.Confluence.LANGUAGE_PROVIDERS;

public class SoulItems {
    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);
    public static final PortDeferredItem<FallenSoulCoreItem> FALLEN_SOUL_CORE = registerItem("fallen_soul_core", "堕魂星核", () -> new FallenSoulCoreItem(new Item.Properties(), ModRarity.BLUE));
    public static final PortDeferredItem<SoulSkillBookItem> SOUL_SKILL_BOOK = registerItemWithEnglishName("soulcerer_skill_book", "灵魂技能书", "Soul Skill Book", () -> new SoulSkillBookItem(new Item.Properties(), ModRarity.WHITE));
    public static final PortDeferredItem<SoulWeaponItem> BROKEN_GLIMMER = registerItem("broken_glimmer", "残破微光", () -> new SoulWeaponItem(new Item.Properties().stacksTo(1), ModRarity.WHITE, SoulSkillParameters.BROKEN_GLIMMER_DAMAGE, List.<ResourceLocation>of(Confluence.asResource("survival_instinct")), SoulWeaponItem.Trait.HEAL_ON_SOUL_GAIN));
    public static final PortDeferredItem<SoulLanternItem> SOUL_CONDUIT = registerItem("soul_conduit", "领魂之灯", () -> new SoulLanternItem(new Item.Properties().stacksTo(1), ModRarity.WHITE, 5.0F, 0.04F));
    public static final PortDeferredItem<SoulLanternItem> PILLAGING_FLAME = registerItem("pillaging_flame", "劫掠之火", () -> new SoulLanternItem(new Item.Properties().stacksTo(1), ModRarity.BLUE, 7.0F, 0.04F, List.<ResourceLocation>of(ModSoulSkills.SOUL_PLUNDER.get().getId())));
    public static final PortDeferredItem<SoulLanternItem> TRUFFLE_LANTERN = registerItem("truffle_lantern", "松露灯", () -> new SoulLanternItem(new Item.Properties().stacksTo(1), ModRarity.GREEN, 7.0F, 0.04F, List.<ResourceLocation>of(ModSoulSkills.CONFUSE_SPORES.get().getId())));
    public static final PortDeferredItem<SoulLanternItem> SOUL_CHANT_GUIDE = registerItem("soul_chant_guide", "颂魂指引", () -> new SoulLanternItem(new Item.Properties().stacksTo(1), ModRarity.GREEN, 9.0F, 0.08F, List.<ResourceLocation>of(), SoulWeaponItem.Trait.LURE_PIERCE));
    public static final PortDeferredItem<SoulLanternItem> LIFE_SPLENDOR = registerItem("life_splendor", "生机灿烂", () -> new SoulLanternItem(new Item.Properties().stacksTo(1), ModRarity.ORANGE, 11.0F, 0.04F, List.<ResourceLocation>of(ModSoulSkills.NATURES_WRATH.get().getId()), SoulWeaponItem.Trait.POISON_ON_SKILL_HIT));
    public static final PortDeferredItem<SoulLanternItem> STAR_SPIRIT = registerItem("star_spirit", "星魄", () -> new SoulLanternItem(new Item.Properties().stacksTo(1), ModRarity.ORANGE, 11.0F, 0.04F, List.<ResourceLocation>of(ModSoulSkills.STAR_CALL.get().getId()), SoulWeaponItem.Trait.STAR_ON_SKILL_HIT));
    public static final PortDeferredItem<SoulLanternItem> DAY_AND_NIGHT_ALIKE = registerItemWithEnglishName("day_and_night_alike", "无分昼夜", "Day and Night Alike", () -> new SoulLanternItem(new Item.Properties().stacksTo(1), ModRarity.LIGHT_RED, 23.0F, 0.04F, List.<ResourceLocation>of(ModSoulSkills.KARMA_FLAME.get().getId(), ModSoulSkills.PROFANE_SOUL.get().getId()), SoulWeaponItem.Trait.SHADOWFLAME_ON_SKILL_HIT));
    public static final PortDeferredItem<SoulLanternItem> SENIOR_PATHFINDER_PRO = registerItem("senior_pathfinder_pro", "带路的老前辈", () -> new SoulLanternItem(new Item.Properties().stacksTo(1), ModRarity.YELLOW, 16.0F, 0.04F, List.<ResourceLocation>of(), SoulWeaponItem.Trait.LURE_HOMING));
    /// 副武器：战士侧武器，只吃近战伤害加成（6 点伤害 / 1.6 攻速 / 4% 基础暴击 / +0.4 击退）。
    public static final PortDeferredItem<SoulKnifeItem> SPIRITWARD_BLADE = registerItem("spiritward_blade", "祈灵之刃", () -> new SoulKnifeItem(
            new Item.Properties().stacksTo(1).attributes(ModItems.createAttributes(ModTiers.UNBREAKABLE, 4.0F, -2.4F, ModItems.attributes(0, 0.4F))),
            ModRarity.BLUE));

    /// 首次获得即永久学会的技能：物品 → 技能 ID。新增首得解锁只在此登记，判定入口为 PlayerUtils.learnSoulSkillsOnFirstObtain。
    public static final Map<PortDeferredItem<? extends Item>, List<ResourceLocation>> FIRST_OBTAIN_SOUL_SKILLS = Map.of(
            LIFE_SPLENDOR, List.of(ModSoulSkills.LAW_OF_NATURE.get().getId())
    );

    /// 生成绑定指定书籍的技能书，供创造栏、战利品与交易共用。
    public static ItemStack soulBook(ResourceLocation bookId) {
        ItemStack stack = SOUL_SKILL_BOOK.toStack();
        stack.set(ModDataComponentTypes.SOUL_SKILL.get(), bookId);
        stack.set(ConfluenceMagicLib.MOD_RARITY, SoulBooks.rarity(bookId));
        return stack;
    }

    /// 注册魂师物品，不登记尚未定义的具体法器。
    public static void register(IEventBus eventBus) {
    }

    /// 玻璃瓶装魂：右键玻璃瓶，消耗 {@link SoulSkillParameters#SOUL_BOTTLE_SOUL_COST} 点灵魂得到魂瓶。
    ///
    /// 只要**身上有足够灵魂**就能装（灵魂值本身在两种资源条之间共享保留，不再要求先切到灵魂条），
    /// 创造模式不扣灵魂，方便取用与测试。只有模组的 `confluence:bottle` 能装魂，原版玻璃瓶不参与。
    /// 客户端只做可用性判断并回成功，实际扣除与替换只在服务端执行，避免两端状态分叉。
    public static boolean canFillSoulBottle(Player player) {
        if (player == null) return false;
        if (player.hasInfiniteMaterials()) return true;
        return PlayerUtils.getSoul(player) >= SoulSkillParameters.SOUL_BOTTLE_SOUL_COST;
    }

    /// 手上的物品能不能当装魂的玻璃瓶用。
    public static boolean isSoulBottleItem(ItemStack stack) {
        return stack.is(PotionItems.BOTTLE);
    }

    /// 尝试把手中的玻璃瓶装成魂瓶；条件不满足返回 false 并（在服务端）用动作栏提示原因。
    ///
    /// 调用方（[BottleItem]）已经先处理过接水，这里就不再重复判断水源。
    /// 客户端与服务端返回同一结果，只有服务端真正扣魂与替换物品。
    public static boolean tryFillSoulBottle(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!isSoulBottleItem(stack)) return false;
        if (!canFillSoulBottle(player)) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.displayClientMessage(Component.translatable(
                        "message.confluence.soul_bottle.not_enough_soul",
                        String.format("%.0f", SoulSkillParameters.SOUL_BOTTLE_SOUL_COST),
                        String.format("%.0f", PlayerUtils.getSoul(player))
                ), true);
            }
            return false;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            fillSoulBottle(serverPlayer, hand);
        }
        return true;
    }

    public static void fillSoulBottle(ServerPlayer player, InteractionHand hand) {
        if (!canFillSoulBottle(player)) {
            return;
        }
        ItemStack inHand = player.getItemInHand(hand);
        if (!isSoulBottleItem(inHand)) return;
        /// 创造模式不扣灵魂；生存模式直接扣当前灵魂值，不走技能与透支结算。
        if (!player.hasInfiniteMaterials() && !PlayerUtils.consumeStoredSoul(player, SoulSkillParameters.SOUL_BOTTLE_SOUL_COST)) {
            return;
        }
        Level level = player.level();
        player.setItemInHand(hand, ItemUtils.createFilledResult(inHand, player, FunctionalBlocks.SOUL_BOTTLE.toStack()));
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PICKUP, player.blockPosition());
        player.awardStat(Stats.ITEM_USED.get(PotionItems.BOTTLE.get()));
    }

    private static <T extends Item> PortDeferredItem<T> registerItem(String registryId, String chineseName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), chineseName));
        return entry;
    }

    private static <T extends Item> PortDeferredItem<T> registerItemWithEnglishName(String registryId, String chineseName, String englishName, Supplier<T> factory) {
        PortDeferredItem<T> entry = ITEMS.register(registryId, factory::get);
        LANGUAGE_PROVIDERS.add(language -> language.add(entry.get(), englishName, chineseName));
        return entry;
    }
}
