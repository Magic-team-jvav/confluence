package org.confluence.terraentity.data.gen.npc;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Blocks;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.api.npc.trade.ITrade;
import org.confluence.terraentity.api.npc.trade.ITradeGenerator;
import org.confluence.terraentity.data.enchantment.TEEnchantments;
import org.confluence.terraentity.data.gen.AbstractExistCodecProvider;
import org.confluence.terraentity.data.gen.loot.TENPCLoot;
import org.confluence.terraentity.entity.npc.trade.NPCTradeManager;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.init.entity.TENpcEntities;
import org.confluence.terraentity.init.item.*;
import org.confluence.terraentity.registries.npc_trade.TradeProperties;
import org.confluence.terraentity.registries.npc_trade.variant.ItemTradeHealth;
import org.confluence.terraentity.registries.npc_trade.variant.ItemTradeItemList;
import org.confluence.terraentity.registries.npc_trade.variant.ItemTradeLootTable;
import org.confluence.terraentity.registries.npc_trade.variant.TradeTask;
import org.confluence.terraentity.registries.npc_trade_list.variant.WeightMapGenerator;
import org.confluence.terraentity.registries.npc_trade_lock.variant.KillEntityLock;
import org.confluence.terraentity.registries.npc_trade_task.variant.DynamicAnglerTradeTask;
import org.confluence.terraentity.registries.npc_trade_task.variant.ProgressTradeTask;
import org.confluence.terraentity.registries.npc_trade_task.variant.RandomTradeTask;
import org.confluence.terraentity.utils.TEItemUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 生成单个NPC单个配方
 *
 * @see ITrade
 */
public class TENPCShopProvider extends AbstractExistCodecProvider<NPCTradeManager> {

    private final PackOutput.PathProvider npcShopPathProvider;

    public TENPCShopProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
        this.npcShopPathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, NPCTradeManager.Loader.KEY);

    }

    @Override
    protected void run(HolderLookup.Provider lookupProvider) {

        HolderLookup.RegistryLookup<Enchantment> enchantmentLookup = lookupProvider.lookupOrThrow(Registries.ENCHANTMENT);


        shop(TENpcEntities.DEMOLITIONIST.getId(),builder()
                .add(ItemTradeItemList.builder().addCost(Items.GUNPOWDER,2).addCost(Items.IRON_INGOT,1).addResult(Items.TNT, 1).build())
                .add(ItemTradeItemList.builder().addCost(Items.GUNPOWDER,1).addResult(TEItemUtil.make(Items.FIREWORK_ROCKET, 1, stack -> stack.set(DataComponents.FIREWORKS, new Fireworks(1, List.of())))).build())
                .add(ItemTradeItemList.builder().addCost(Items.GUNPOWDER,2).addResult(TEItemUtil.make(Items.FIREWORK_ROCKET, 1, stack -> stack.set(DataComponents.FIREWORKS, new Fireworks(2, List.of())))).build())
                .add(ItemTradeItemList.builder().addCost(Items.GUNPOWDER,2).addCost(Items.COAL, 1).addResult(TEItemUtil.make(Items.FIREWORK_ROCKET, 1, stack -> stack.set(DataComponents.FIREWORKS, new Fireworks(3, List.of())))).build())
                .build());

        shop(TENpcEntities.NURSE.getId(),builder()
                .add(new ItemTradeHealth(List.of(new AmountIngredient(Ingredient.of(Items.EMERALD.getDefaultInstance()),1 )), 10, null))
                .add(ItemTradeItemList.builder().addCost(Items.RED_MUSHROOM,1).addCost(Items.BROWN_MUSHROOM,1).addResult(TEItemUtil.make(Items.POTION, 1, stack -> stack.set(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withPotion(Potions.HEALING)))).build())
                .add(ItemTradeItemList.builder().addCost(Items.RED_MUSHROOM,1).addCost(Items.BROWN_MUSHROOM,1).addCost(Items.WARPED_FUNGUS, 1).addResult(TEItemUtil.make(Items.POTION, 1, stack -> stack.set(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withPotion(Potions.STRONG_HEALING)))).build())
                .build());

        shop(TENpcEntities.ANGLER.getId(),builder()
                .add(TradeTask.create(
                        DynamicAnglerTradeTask.builder(
                                        ItemTradeLootTable.builder()
                                                .addCost(Items.COD, 1)
                                                .setLootTable(TENPCLoot.Angler.location())
                                                .setSprite(TerraEntity.space("random_gift"))
                                                .build(),
                                        List.of(Items.COD.getDefaultInstance(), Items.PUFFERFISH.getDefaultInstance(), Items.TROPICAL_FISH.getDefaultInstance(), Items.SALMON.getDefaultInstance(),Items.INK_SAC.getDefaultInstance(), Items.GLOW_INK_SAC.getDefaultInstance() )
                                )
                                .addResult(1, List.of(Items.FISHING_ROD.getDefaultInstance()))
                                .addResult(5, List.of(Items.BUCKET.getDefaultInstance()))
                                .addResult(10, List.of(TEItemUtil.make(Items.ENCHANTED_BOOK, 1, stack-> stack.enchant(enchantmentLookup.getOrThrow(Enchantments.LURE),1)), TEItemUtil.make(Items.ENCHANTED_BOOK, 1, stack-> stack.enchant(enchantmentLookup.getOrThrow(Enchantments.LUCK_OF_THE_SEA),1))))
                                .addResult(15, List.of(TEItemUtil.make(Items.IRON_INGOT,20)))
                                .addResult(20, List.of(TEItemUtil.make(Items.ENCHANTED_BOOK, 1, stack-> stack.enchant(enchantmentLookup.getOrThrow(Enchantments.LURE),2)),TEItemUtil.make(Items.ENCHANTED_BOOK, 1, stack-> stack.enchant(enchantmentLookup.getOrThrow(Enchantments.LUCK_OF_THE_SEA),2))))
                                .addResult(25, List.of(TEItemUtil.make(Items.GOLD_INGOT,20)))
                                .addResult(30, List.of(TEItemUtil.make(Items.ENCHANTED_BOOK, 1, stack-> stack.enchant(enchantmentLookup.getOrThrow(Enchantments.LURE),3)),TEItemUtil.make(Items.ENCHANTED_BOOK, 1, stack-> stack.enchant(enchantmentLookup.getOrThrow(Enchantments.LUCK_OF_THE_SEA),3))))
                                .addResult(35, List.of(TEItemUtil.make(Items.DIAMOND,20)))
                                .addResult(40, List.of(TEItemUtil.make(Items.ENCHANTED_BOOK, 1, stack-> stack.enchant(enchantmentLookup.getOrThrow(Enchantments.MENDING),1))))
                                .addResult(45, List.of(TEItemUtil.make(Items.EMERALD,20)))
                                .addResult(50, List.of(TEItemUtil.make(Items.NETHERITE_INGOT,5), TEItemUtil.make(TEBoomerangItems.FLAMARANG.get(), 1, stack->stack.enchant(enchantmentLookup.getOrThrow(TEEnchantments.MULTI_BOOMERANG),1))))
                                .addResult(55, List.of(TEItemUtil.make(Items.NETHERITE_INGOT,5)))
//                                .setTitle("title.terra_entity.npc_trade.task.fishman")
                                .build()
                ))
                .build());

        shop(TENpcEntities.DRYAD.getId(),builder()
                .add(ItemTradeItemList.builder().addCost(Items.OAK_SAPLING, 2).addCost(Items.COAL, 1).addResult(Items.EMERALD, 1).build())
                .add(ItemTradeItemList.builder().addCost(Items.SPRUCE_SAPLING, 2).addCost(Items.COAL, 1).addResult(Items.EMERALD, 1).build())
                .add(ItemTradeItemList.builder().addCost(Items.BIRCH_SAPLING, 2).addCost(Items.COAL, 1).addResult(Items.EMERALD, 1).build())
                .add(ItemTradeItemList.builder().addCost(Items.JUNGLE_SAPLING, 2).addCost(Items.COAL, 1).addResult(Items.EMERALD, 1).build())
                .add(ItemTradeItemList.builder().addCost(Items.ACACIA_SAPLING, 2).addCost(Items.COAL, 1).addResult(Items.EMERALD, 1).build())
                .add(ItemTradeItemList.builder().addCost(Items.DARK_OAK_SAPLING, 2).addCost(Items.COAL, 1).addResult(Items.EMERALD, 1).build())
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD, 24).addResult(TESummonItems.SUMMON_WOODEN_SWORD_STAFF).build())
                .build());

        shop(TENpcEntities.MERCHANT.getId(),builder()
                .add(ItemTradeItemList.builder().addCost(Items.IRON_INGOT, 1).addResult(Items.COAL, 2).build())
                .add(ItemTradeItemList.builder().addCost(Items.GOLD_INGOT, 1).addResult(Items.IRON_INGOT, 2).build())
                .add(ItemTradeItemList.builder().addCost(Items.DIAMOND, 1).addResult(Items.GOLD_INGOT, 2).build())
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD, 1).addResult(Items.DIAMOND, 2).build())
                .add(ItemTradeItemList.builder().addCost(Items.NETHERITE_INGOT, 1).addResult(Items.EMERALD, 4).build())

                .add(ItemTradeItemList.builder().addCost(Items.COAL, 3).addResult(Items.IRON_INGOT, 1).build())
                .add(ItemTradeItemList.builder().addCost(Items.IRON_INGOT, 3).addResult(Items.GOLD_INGOT, 1).build())
                .add(ItemTradeItemList.builder().addCost(Items.GOLD_INGOT, 3).addResult(Items.DIAMOND, 1).build())
                .add(ItemTradeItemList.builder().addCost(Items.DIAMOND, 3).addResult(Items.EMERALD, 1).build())
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD, 5).addResult(Items.NETHERITE_INGOT, 1).build())

                .add(ItemTradeItemList.builder().addCost(Items.DIAMOND, 5).addCost(Items.EGG, 1).addResult(TEBossSummonsItems.KING_SLIME_SUMMONS.get().getDefaultInstance()).build())
                .add(ItemTradeItemList.builder().addCost(Items.REDSTONE, 20).addCost(Items.EGG, 1).addResult(TEBossSummonsItems.EYE_OF_CTHULHU_SUMMONS.get().getDefaultInstance()).build())

                .build());

        shop(TENpcEntities.ARMS_DEALER.getId(),builder()
                .add(ItemTradeItemList.builder().addCost(Items.ROTTEN_FLESH, 1).addCost(Items.STRING, 2).addResult(Items.BOW.getDefaultInstance()).build())
                .add(ItemTradeItemList.builder().addCost(Items.BONE, 3).addCost(Items.STRING, 2).addResult(Items.CROSSBOW.getDefaultInstance()).build())
                .add(ItemTradeItemList.builder().addCost(Items.ARROW, 4).addCost(Items.SPIDER_EYE, 1).addResult(TEItemUtil.make(Items.TIPPED_ARROW, 4, stack -> stack.set(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withPotion(Potions.POISON)))).build())
                .add(ItemTradeItemList.builder().addCost(Items.ARROW, 4).addCost(Items.SPIDER_EYE, 1).addResult(TEItemUtil.make(Items.TIPPED_ARROW, 4, stack -> stack.set(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withPotion(Potions.SLOWNESS)))).build())
                .add(ItemTradeItemList.builder().addCost(Items.ARROW, 4).addCost(Items.PHANTOM_MEMBRANE, 1).addResult(TEItemUtil.make(Items.TIPPED_ARROW, 4, stack -> stack.set(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withPotion(Potions.SLOW_FALLING)))).build())
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD, 48).addResult(TESummonItems.SUMMON_STONE_SWORD_STAFF).build())
                .build());




        int wool2dye = 2;
        int dye2money = 3;
        Item to = Items.EMERALD;

        shop(TENpcEntities.PAINTER.getId(),builder()
                .add(TradeTask.create(new RandomTradeTask(List.of(
                        ItemTradeItemList.builder().addCost(Items.RED_DYE,dye2money).addResult(to,1).build(),
                        ItemTradeItemList.builder().addCost(Items.LIGHT_BLUE_DYE,dye2money).addResult(to,1).build(),
                        ItemTradeItemList.builder().addCost(Items.MAGENTA_DYE,dye2money).addResult(to,1).build(),
                        ItemTradeItemList.builder().addCost(Items.ORANGE_DYE,dye2money).addResult(to,1).build(),
                        ItemTradeItemList.builder().addCost(Items.BLUE_DYE,dye2money).addResult(to,1).build(),
                        ItemTradeItemList.builder().addCost(Items.BROWN_DYE,dye2money).addResult(to,1).build(),
                        ItemTradeItemList.builder().addCost(Items.BLACK_DYE,dye2money).addResult(to,1).build(),
                        ItemTradeItemList.builder().addCost(Items.WHITE_DYE,dye2money).addResult(to,1).build(),
                        ItemTradeItemList.builder().addCost(Items.GREEN_DYE,dye2money).addResult(to,1).build(),
                        ItemTradeItemList.builder().addCost(Items.PURPLE_DYE,dye2money).addResult(to,1).build(),
                        ItemTradeItemList.builder().addCost(Items.CYAN_DYE,dye2money).addResult(to,1).build(),
                        ItemTradeItemList.builder().addCost(Items.LIGHT_GRAY_DYE,dye2money).addResult(to,1).build(),
                        ItemTradeItemList.builder().addCost(Items.GRAY_DYE,dye2money).addResult(to,1).build(),
                        ItemTradeItemList.builder().addCost(Items.PINK_DYE,dye2money).addResult(to,1).build(),
                        ItemTradeItemList.builder().addCost(Items.LIME_DYE,dye2money).addResult(to,1).build(),
                        ItemTradeItemList.builder().addCost(Items.YELLOW_DYE,dye2money).addResult(to,1).build()
                ))))

                .build());

        dye2money = 5;
        shop(TENpcEntities.DYE_TRADER.getId(),builder()
                .add(ItemTradeItemList.builder().addCost(Items.RED_DYE,dye2money).addResult(to,1).build())
                .add(ItemTradeItemList.builder().addCost(Items.LIGHT_BLUE_DYE,dye2money).addResult(to,1).build())
                .add(ItemTradeItemList.builder().addCost(Items.MAGENTA_DYE,dye2money).addResult(to,1).build())
                .add(ItemTradeItemList.builder().addCost(Items.ORANGE_DYE,dye2money).addResult(to,1).build())
                .add(ItemTradeItemList.builder().addCost(Items.BLUE_DYE,dye2money).addResult(to,1).build())
                .add(ItemTradeItemList.builder().addCost(Items.BROWN_DYE,dye2money).addResult(to,1).build())
                .add(ItemTradeItemList.builder().addCost(Items.BLACK_DYE,dye2money).addResult(to,1).build())
                .add(ItemTradeItemList.builder().addCost(Items.WHITE_DYE,dye2money).addResult(to,1).build())
                .add(ItemTradeItemList.builder().addCost(Items.GREEN_DYE,dye2money).addResult(to,1).build())
                .add(ItemTradeItemList.builder().addCost(Items.PURPLE_DYE,dye2money).addResult(to,1).build())
                .add(ItemTradeItemList.builder().addCost(Items.CYAN_DYE,dye2money).addResult(to,1).build())
                .add(ItemTradeItemList.builder().addCost(Items.LIGHT_GRAY_DYE,dye2money).addResult(to,1).build())
                .add(ItemTradeItemList.builder().addCost(Items.GRAY_DYE,dye2money).addResult(to,1).build())
                .add(ItemTradeItemList.builder().addCost(Items.PINK_DYE,dye2money).addResult(to,1).build())
                .add(ItemTradeItemList.builder().addCost(Items.LIME_DYE,dye2money).addResult(to,1).build())
                .add(ItemTradeItemList.builder().addCost(Items.YELLOW_DYE,dye2money).addResult(to,1).build())

                .add(ItemTradeItemList.builder().addCost(ItemTags.WOOL,1).addResult(Items.RED_DYE,wool2dye).build())
                .add(ItemTradeItemList.builder().addCost(ItemTags.WOOL,1).addResult(Items.LIGHT_BLUE_DYE,wool2dye).build())
                .add(ItemTradeItemList.builder().addCost(ItemTags.WOOL,1).addResult(Items.MAGENTA_DYE,wool2dye).build())
                .add(ItemTradeItemList.builder().addCost(ItemTags.WOOL,1).addResult(Items.ORANGE_DYE,wool2dye).build())
                .add(ItemTradeItemList.builder().addCost(ItemTags.WOOL,1).addResult(Items.BLUE_DYE,wool2dye).build())
                .add(ItemTradeItemList.builder().addCost(ItemTags.WOOL,1).addResult(Items.BROWN_DYE,wool2dye).build())
                .add(ItemTradeItemList.builder().addCost(ItemTags.WOOL,1).addResult(Items.BLACK_DYE,wool2dye).build())
                .add(ItemTradeItemList.builder().addCost(ItemTags.WOOL,1).addResult(Items.WHITE_DYE,wool2dye).build())
                .add(ItemTradeItemList.builder().addCost(ItemTags.WOOL,1).addResult(Items.GREEN_DYE,wool2dye).build())
                .add(ItemTradeItemList.builder().addCost(ItemTags.WOOL,1).addResult(Items.PURPLE_DYE,wool2dye).build())
                .add(ItemTradeItemList.builder().addCost(ItemTags.WOOL,1).addResult(Items.CYAN_DYE,wool2dye).build())
                .add(ItemTradeItemList.builder().addCost(ItemTags.WOOL,1).addResult(Items.LIGHT_GRAY_DYE,wool2dye).build())
                .add(ItemTradeItemList.builder().addCost(ItemTags.WOOL,1).addResult(Items.GRAY_DYE,wool2dye).build())
                .add(ItemTradeItemList.builder().addCost(ItemTags.WOOL,1).addResult(Items.PINK_DYE,wool2dye).build())
                .add(ItemTradeItemList.builder().addCost(ItemTags.WOOL,1).addResult(Items.LIME_DYE,wool2dye).build())
                .add(ItemTradeItemList.builder().addCost(ItemTags.WOOL,1).addResult(Items.YELLOW_DYE,wool2dye).build())
                .build());

        shop(TENpcEntities.MECHANIC.getId(),builder().build());

        shop(TENpcEntities.GUIDE.getId(),builder()
                .add(TradeTask.create(new ProgressTradeTask(List.of(
                        ItemTradeItemList.builder().addCost(ItemTags.PLANKS, 4).addResult(Items.CRAFTING_TABLE).build(),
                        ItemTradeItemList.builder().addCost(Items.COAL, 10).addResult(Items.TORCH, 64).build(),
                        ItemTradeItemList.builder().addCost(Items.DIAMOND, 5).addCost(Items.EGG, 1).addResult(TEBossSummonsItems.KING_SLIME_SUMMONS.get()).build(),
                        ItemTradeItemList.builder().addCost(Items.ENDER_EYE, 5).addCost(Items.REDSTONE, 20).addCost(Items.EGG, 1).addResult(TEBossSummonsItems.EYE_OF_CTHULHU_SUMMONS.get()).build(),
                        ItemTradeItemList.builder().addCost(Items.OBSIDIAN, 10).addCost(Items.EGG, 1).addResult(TEBossSummonsItems.EATER_OF_WORLDS_SUMMONS.get()).build(),
                        ItemTradeItemList.builder().addCost(Items.SPIDER_EYE, 5).addCost(Items.ROTTEN_FLESH,5).addCost(Items.EGG, 1).addResult(TEBossSummonsItems.BRAIN_OF_CTHULHU_SUMMONS.get()).build(),
                        ItemTradeItemList.builder().addCost(Items.HONEY_BOTTLE, 5).addCost(Items.HONEYCOMB,5).addCost(Items.EGG, 1).addResult(TEBossSummonsItems.QUEEN_BEE_SUMMONS.get()).build(),
                        ItemTradeItemList.builder().addCost(Items.BONE, 8).addCost(Items.EGG, 1).addResult(TEBossSummonsItems.SKELETRON_SUMMONS.get()).build()

                ))))
                .add(ItemTradeItemList.builder().addCost(Items.DIAMOND, 8).addCost(Items.EGG, 1).addResult(TEBossSummonsItems.KING_SLIME_SUMMONS.get()).build())
                .add(ItemTradeItemList.builder().addCost(Items.ENDER_EYE, 8).addCost(Items.REDSTONE, 20).addCost(Items.EGG, 1).addResult(TEBossSummonsItems.EYE_OF_CTHULHU_SUMMONS.get()).setProperties(TradeProperties.builder().setLock(new KillEntityLock(TEBossEntities.KING_SLIME.get())).build()).build())
                .add(ItemTradeItemList.builder().addCost(Items.OBSIDIAN, 15).addCost(Items.EGG, 1).addResult(TEBossSummonsItems.EATER_OF_WORLDS_SUMMONS.get()).setProperties(TradeProperties.builder().setLock(new KillEntityLock(TEBossEntities.EYE_OF_CTHULHU.get())).build()).build())
                .add(ItemTradeItemList.builder().addCost(Items.SPIDER_EYE, 8).addCost(Items.ROTTEN_FLESH,8).addCost(Items.EGG, 1).addResult(TEBossSummonsItems.BRAIN_OF_CTHULHU_SUMMONS.get()).setProperties(TradeProperties.builder().setLock(new KillEntityLock(TEBossEntities.EATER_OF_WORLDS.get())).build()).build())
                .add(ItemTradeItemList.builder().addCost(Items.HONEY_BOTTLE, 8).addCost(Items.HONEYCOMB,8).addCost(Items.EGG, 1).addResult(TEBossSummonsItems.QUEEN_BEE_SUMMONS.get()).setProperties(TradeProperties.builder().setLock(new KillEntityLock(TEBossEntities.BRAIN_OF_CTHULHU.get())).build()).build())
                .add(ItemTradeItemList.builder().addCost(Items.BONE, 10).addCost(Items.EGG, 1).addResult(TEBossSummonsItems.SKELETRON_SUMMONS.get()).setProperties(TradeProperties.builder().setLock(new KillEntityLock(TEBossEntities.BRAIN_OF_CTHULHU.get())).build()).build())

                .add(TradeTask.create(new ProgressTradeTask(List.of(
                        ItemTradeItemList.builder().addCost(ItemTags.PLANKS, 2).addResult(Items.WOODEN_SWORD).build(),
                        ItemTradeItemList.builder().addCost(Items.WOODEN_SWORD).addCost(Items.COBBLESTONE).addResult(Items.STONE_SWORD).build(),
                        ItemTradeItemList.builder().addCost(Items.STONE_SWORD).addCost(Items.IRON_INGOT).addResult(Items.IRON_SWORD).build(),
                        ItemTradeItemList.builder().addCost(Items.IRON_SWORD).addCost(Items.GOLD_INGOT).addResult(Items.GOLDEN_SWORD).build(),
                        ItemTradeItemList.builder().addCost(Items.GOLDEN_SWORD).addCost(Items.DIAMOND).addResult(Items.DIAMOND_SWORD).build(),
                        ItemTradeItemList.builder().addCost(Items.DIAMOND_SWORD).addCost(Items.NETHERITE_INGOT).addResult(Items.NETHERITE_SWORD).build()
                ))))
                .add(TradeTask.create(new ProgressTradeTask(List.of(
                        ItemTradeItemList.builder().addCost(ItemTags.PLANKS, 3).addResult(Items.WOODEN_AXE).build(),
                        ItemTradeItemList.builder().addCost(Items.WOODEN_AXE).addCost(Items.COBBLESTONE).addResult(Items.STONE_AXE).build(),
                        ItemTradeItemList.builder().addCost(Items.STONE_AXE).addCost(Items.IRON_INGOT).addResult(Items.IRON_AXE).build(),
                        ItemTradeItemList.builder().addCost(Items.IRON_AXE).addCost(Items.GOLD_INGOT).addResult(Items.GOLDEN_AXE).build(),
                        ItemTradeItemList.builder().addCost(Items.GOLDEN_AXE).addCost(Items.DIAMOND).addResult(Items.DIAMOND_AXE).build(),
                        ItemTradeItemList.builder().addCost(Items.DIAMOND_AXE).addCost(Items.NETHERITE_INGOT).addResult(Items.NETHERITE_AXE).build()
                ))))
                .add(TradeTask.create(new ProgressTradeTask(List.of(
                        ItemTradeItemList.builder().addCost(ItemTags.PLANKS, 3).addResult(Items.WOODEN_PICKAXE).build(),
                        ItemTradeItemList.builder().addCost(Items.WOODEN_PICKAXE).addCost(Items.COBBLESTONE).addResult(Items.STONE_PICKAXE).build(),
                        ItemTradeItemList.builder().addCost(Items.STONE_PICKAXE).addCost(Items.IRON_INGOT).addResult(Items.IRON_PICKAXE).build(),
                        ItemTradeItemList.builder().addCost(Items.IRON_PICKAXE).addCost(Items.GOLD_INGOT).addResult(Items.GOLDEN_PICKAXE).build(),
                        ItemTradeItemList.builder().addCost(Items.GOLDEN_PICKAXE).addCost(Items.DIAMOND).addResult(Items.DIAMOND_PICKAXE).build(),
                        ItemTradeItemList.builder().addCost(Items.DIAMOND_PICKAXE).addCost(Items.NETHERITE_INGOT).addResult(Items.NETHERITE_PICKAXE).build()
                ))))
                .add(TradeTask.create(new ProgressTradeTask(List.of(
                        ItemTradeItemList.builder().addCost(Items.LEATHER, 3).addResult(Items.LEATHER_HELMET).build(),
                        ItemTradeItemList.builder().addCost(Items.LEATHER_HELMET).addCost(Items.IRON_INGOT, 2).addResult(Items.IRON_HELMET).build(),
                        ItemTradeItemList.builder().addCost(Items.IRON_HELMET).addCost(Items.GOLD_INGOT, 2).addResult(Items.GOLDEN_HELMET).build(),
                        ItemTradeItemList.builder().addCost(Items.GOLDEN_HELMET).addCost(Items.DIAMOND, 2).addResult(Items.DIAMOND_HELMET).build(),
                        ItemTradeItemList.builder().addCost(Items.DIAMOND_HELMET).addCost(Items.NETHERITE_INGOT).addResult(Items.NETHERITE_HELMET).build()
                ))))
                .add(TradeTask.create(new ProgressTradeTask(List.of(
                        ItemTradeItemList.builder().addCost(Items.LEATHER, 6).addResult(Items.LEATHER_CHESTPLATE).build(),
                        ItemTradeItemList.builder().addCost(Items.LEATHER_CHESTPLATE).addCost(Items.IRON_INGOT, 4).addResult(Items.IRON_CHESTPLATE).build(),
                        ItemTradeItemList.builder().addCost(Items.IRON_CHESTPLATE).addCost(Items.GOLD_INGOT, 4).addResult(Items.GOLDEN_CHESTPLATE).build(),
                        ItemTradeItemList.builder().addCost(Items.GOLDEN_CHESTPLATE).addCost(Items.DIAMOND, 4).addResult(Items.DIAMOND_CHESTPLATE).build(),
                        ItemTradeItemList.builder().addCost(Items.DIAMOND_CHESTPLATE).addCost(Items.NETHERITE_INGOT).addResult(Items.NETHERITE_CHESTPLATE).build()
                ))))
                .add(TradeTask.create(new ProgressTradeTask(List.of(
                        ItemTradeItemList.builder().addCost(Items.LEATHER, 5).addResult(Items.LEATHER_LEGGINGS).build(),
                        ItemTradeItemList.builder().addCost(Items.LEATHER_LEGGINGS).addCost(Items.IRON_INGOT, 3).addResult(Items.IRON_LEGGINGS).build(),
                        ItemTradeItemList.builder().addCost(Items.IRON_LEGGINGS).addCost(Items.GOLD_INGOT, 3).addResult(Items.GOLDEN_LEGGINGS).build(),
                        ItemTradeItemList.builder().addCost(Items.GOLDEN_LEGGINGS).addCost(Items.DIAMOND, 3).addResult(Items.DIAMOND_LEGGINGS).build(),
                        ItemTradeItemList.builder().addCost(Items.DIAMOND_LEGGINGS).addCost(Items.NETHERITE_INGOT).addResult(Items.NETHERITE_LEGGINGS).build()
                ))))
                .add(TradeTask.create(new ProgressTradeTask(List.of(
                        ItemTradeItemList.builder().addCost(Items.LEATHER, 2).addResult(Items.LEATHER_BOOTS).build(),
                        ItemTradeItemList.builder().addCost(Items.LEATHER_BOOTS).addCost(Items.IRON_INGOT, 2).addResult(Items.IRON_BOOTS).build(),
                        ItemTradeItemList.builder().addCost(Items.IRON_BOOTS).addCost(Items.GOLD_INGOT, 2).addResult(Items.GOLDEN_BOOTS).build(),
                        ItemTradeItemList.builder().addCost(Items.GOLDEN_BOOTS).addCost(Items.DIAMOND, 2).addResult(Items.DIAMOND_BOOTS).build(),
                        ItemTradeItemList.builder().addCost(Items.DIAMOND_BOOTS).addCost(Items.NETHERITE_INGOT).addResult(Items.NETHERITE_BOOTS).build()
                ))))
                .build());

        shop(TENpcEntities.TRAVELING_MERCHANT.getId(), new ComplexBuilder(
                WeightMapGenerator.builder(6)
                        .addTrade(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Items.LEATHER,10).build(), 50)
                        .addTrade(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Items.COAL,10).build(), 50)
                        .addTrade(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Items.COPPER_INGOT,10).build(), 50)
                        .addTrade(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Items.IRON_INGOT,8).build(), 40)
                        .addTrade(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Items.GOLD_INGOT,4).build(), 30)
                        .addTrade(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Items.DIAMOND,2).build(), 10)
                        .addTrade(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Items.REDSTONE,10).build(), 30)
                        .addTrade(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Items.LAPIS_LAZULI,10).build(), 30)
                        .addTrade(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Items.AMETHYST_CLUSTER, 4).build(), 30)
                        .addTrade(ItemTradeItemList.builder().addCost(Items.EMERALD, 64).addResult(TESummonItems.SUMMON_GOLDEN_SWORD_STAFF).build(), 5)
                        .addTrade(ItemTradeItemList.builder().addCost(Items.EMERALD, 10).addResult(TEPetItems.CHESTER_STAFF).build(), 5)
                        .addTrade(ItemTradeItemList.builder().addCost(Items.EMERALD, 10).addResult(TEPetItems.WALLET).build(), 5)
                        .addTrade(ItemTradeItemList.builder().addCost(Items.EMERALD, 10).addResult(TEYoyosItems.CODE_1).setProperties(TradeProperties.builder().setLock(KillEntityLock.create(TEBossEntities.EYE_OF_CTHULHU.get())).build()).build(), 30)
                        .build()
        ).build());

        shop(TENpcEntities.MECHANIC.getId(), builder()
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Items.REDSTONE,10).build())
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Blocks.REPEATER,5).build())
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Blocks.COMPARATOR,5).build())
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Blocks.REDSTONE_LAMP,5).build())
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Blocks.REDSTONE_TORCH,8).build())
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD, 20).addResult(TEBoomerangItems.COMBAT_WRENCH).build())
                .build());

        shop(TENpcEntities.TRUFFLE.getId(), builder()
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD, 10).addResult(TEBoomerangItems.SHROOMERANG).build())
                .add(ItemTradeItemList.builder().addCost(Items.BROWN_MUSHROOM, 10).addResult(Items.EMERALD).build())
                .add(ItemTradeItemList.builder().addCost(Items.RED_MUSHROOM, 10).addResult(Items.EMERALD).build())
                .build());

        shop(TENpcEntities.CLOTHIER.getId(), builder()
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Items.IRON_HELMET).build())
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Items.IRON_CHESTPLATE).build())
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Items.IRON_LEGGINGS).build())
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Items.IRON_BOOTS).build())
                .build());

        shop(TENpcEntities.WITCH_DOCTOR.getId(), builder()
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(Items.HONEY_BOTTLE,8).build())
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(TEItemUtil.make(Items.SPLASH_POTION, 1, stack -> stack.set(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withPotion(Potions.HEALING)))).build())
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(TEItemUtil.make(Items.SPLASH_POTION, 1, stack -> stack.set(DataComponents.POTION_CONTENTS, PotionContents.EMPTY.withPotion(Potions.HARMING)))).build())
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD, 64).addResult(TESummonItems.SUMMON_IRON_SWORD_STAFF).build())
                .build());

        shop(TENpcEntities.PARTY_GIRL.getId(), builder()
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(TEItemUtil.make(Items.FIREWORK_ROCKET, 3, stack -> stack.set(DataComponents.FIREWORKS, new Fireworks(1, List.of(new FireworkExplosion(FireworkExplosion.Shape.STAR, IntList.of(255,0,0),IntList.of(255,255,255),true,false)))))).build())
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(TEItemUtil.make(Items.FIREWORK_ROCKET, 3, stack -> stack.set(DataComponents.FIREWORKS, new Fireworks(1, List.of(new FireworkExplosion(FireworkExplosion.Shape.CREEPER, IntList.of(255,255,0),IntList.of(255,255,255),true,false)))))).build())
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD).addResult(TEItemUtil.make(Items.FIREWORK_ROCKET, 3, stack -> stack.set(DataComponents.FIREWORKS, new Fireworks(1, List.of(new FireworkExplosion(FireworkExplosion.Shape.BURST, IntList.of(255,0,255),IntList.of(255,255,255),true,false)))))).build())
                .add(ItemTradeItemList.builder().addCost(Items.EMERALD, 64).addCost(Items.EMERALD, 32).addResult(TESummonItems.SUMMON_DIAMOND_SWORD_STAFF).build())

                .build());

    }

    @Override
    protected Codec<NPCTradeManager> getCodec() {
        return NPCTradeManager.CODEC;
    }

    @Override
    public @NotNull String getName() {
        return "NPC Shop";
    }


//    protected Appender<NPCTradeManager> shop(ResourceLocation id) {
//        return recipe(NPCTradeManager.CODEC, pathProvider().json(id));
//    }

    protected void shop(ResourceLocation id, NPCTradeManager manager) {
        this.gen(id.withPrefix("npc/shop/"), manager);
    }
    protected Builder builder() {
        return new Builder();
    }



    public static class Builder {
        private final List<ITrade> trades;

        public Builder() {
            this.trades = new ArrayList<>();
        }

        public Builder add(ITrade trade) {
            trades.add(trade);
            return this;
        }

        public NPCTradeManager build() {
            return new NPCTradeManager(trades);
        }
    }

    /**
     * 生成未初始化的NPCTradeManager，用于生成随机的交易表
     */
    public static class ComplexBuilder {

        ITradeGenerator list;
        public ComplexBuilder(ITradeGenerator list) {
            this.list = list;
        }

        public NPCTradeManager build() {
            return new NPCTradeManager(list);
        }
    }

//    @Override
    protected PackOutput.PathProvider pathProvider() {
        return npcShopPathProvider;
    }

}