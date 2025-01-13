package org.confluence.mod.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.common.BaseChestBlock;
import org.confluence.mod.common.block.functional.DeathChestBlock;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.terra_curio.common.init.TCItems;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Confluence.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BUILDING_BLOCKS = TABS.register("building_blocks",
            () -> CreativeModeTab.builder().icon(() -> IconItems.BLOCKS_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.building_blocks"))
                    .displayItems((parameters, output) -> {
                        LogBlockSet.acceptBuilding(output);
                        DecorativeBlocks.BLOCKS.getEntries().forEach(block -> output.accept(block.get()));
                        StatueBlocks.BLOCKS.getEntries().forEach(block -> output.accept(block.get()));
                    }).build()
    );
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> NATURAL_BLOCKS = TABS.register("natural_blocks",
            () -> CreativeModeTab.builder().icon(() -> IconItems.NATURE_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.natural_blocks"))
                    .displayItems((parameters, output) -> {
                        LogBlockSet.acceptNature(output);
                        OreBlocks.BLOCKS.getEntries().forEach(block -> output.accept(block.get()));
                        NatureBlocks.BLOCKS.getEntries().forEach(block -> {
                            Item item = block.get().asItem();
                            if (item != Items.AIR) output.accept(item);
                        });
                        PotBlocks.BLOCKS.getEntries().forEach(block -> output.accept(block.get()));
                    }).build()
    );
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MISC = TABS.register("misc",
            () -> CreativeModeTab.builder().icon(() -> IconItems.PRECIOUS_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.misc"))
                    .displayItems((parameters, output) -> {
                        Consumer<Supplier<? extends ItemLike>> action = item -> output.accept(item.get());
                        ModItems.ITEMS.getEntries().forEach(action);
                        ConsumableItems.ITEMS.getEntries().forEach(action);
                        BaitItems.ITEMS.getEntries().forEach(action);
                        QuestedFishes.ITEMS.getEntries().forEach(action);
                        CrateBlocks.BLOCKS.getEntries().forEach(action);
                        PaintItems.ITEMS.getEntries().forEach(action);
                    }).build()
    );
    // 材料
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MATERIALS = TABS.register("materials",
            () -> CreativeModeTab.builder().icon(() -> IconItems.MATERIAL_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.materials"))
                    .displayItems((parameters, output) -> {
                        output.accept(ConsumableItems.MANA_CRYSTAL.get());
                        output.accept(ConsumableItems.LIFE_CRYSTAL.get());
                        output.accept(ConsumableItems.LIFE_FRUIT.get());
                        MaterialItems.ITEMS.getEntries().forEach(item -> output.accept(item.get().getDefaultInstance()));
                        output.accept(ModBlocks.POO.get());
                    }).build());
    // 工具
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TOOLS = TABS.register("tools",
            () -> CreativeModeTab.builder().icon(() -> IconItems.TOOLS_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.tools"))
                    .displayItems((parameters, output) -> {
                        ToolItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        output.accept(TCItems.MAGIC_MIRROR.get());
                        output.accept(TCItems.CELL_PHONE.get());
                        output.accept(TCItems.DIVING_HELMET.get());
                        AxeItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        PickaxeItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        PickaxeAxeItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        DrillItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        HammerItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        HookItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        MinecartItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        FishingPoleItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                    }).build());
    // 战士武器
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> WARRIORS = TABS.register("warriors",
            () -> CreativeModeTab.builder().icon(() -> IconItems.MELEE_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.warriors"))
                    .displayItems((parameters, output) -> {
                        SwordItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        BoomerangItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                    }).build());
    // 射手武器
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SHOOTERS = TABS.register("shooters",
            () -> CreativeModeTab.builder().icon(() -> IconItems.REMOTE_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.rangers"))
                    .displayItems((parameters, output) -> {
                        BowItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        ArrowItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                    }).build());
    // 法师武器
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAGES = TABS.register("mages",
            () -> CreativeModeTab.builder().icon(() -> IconItems.MAGIC_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.mages"))
                    .displayItems((parameters, output) -> {
                        ManaStaffItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                    }).build());
    // 召唤武器
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SUMMONERS = TABS.register("summoners",
            () -> CreativeModeTab.builder().icon(() -> IconItems.SUMMON_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.summoners"))
                    .displayItems((parameters, output) -> {
                        // todo
                    }).build());
    // 开发者物品
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DEVELOPER = TABS.register("developer",
            () -> CreativeModeTab.builder().icon(() -> IconItems.DEVELOPER_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.developer"))
                    .displayItems((parameters, output) -> {
                        // todo
                    }).build());
    // 食物与药水
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> FOOD_AND_POTIONS = TABS.register("food_and_potions",
            () -> CreativeModeTab.builder().icon(() -> IconItems.POTION_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.food_and_potions"))
                    .displayItems((parameters, output) -> {
                        PotionItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        FoodItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                    }).build());
    // 盔甲
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ARMORS = TABS.register("armors",
            () -> CreativeModeTab.builder().icon(() -> IconItems.ARMOR_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.armors"))
                    .displayItems((parameters, output) -> {
                        output.accept(TCItems.DIVING_HELMET.get());
                        ArmorItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        VanityArmorItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                    }).build());
    // 器械
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MECHANICAL = TABS.register("mechanical",
            () -> CreativeModeTab.builder().icon(() -> IconItems.MECHANICAL_ICON.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.confluence.mechanical"))
                    .displayItems((parameters, output) -> {
                        output.accept(ToolItems.RED_WRENCH.get());
                        output.accept(ToolItems.GREEN_WRENCH.get());
                        output.accept(ToolItems.BLUE_WRENCH.get());
                        output.accept(ToolItems.YELLOW_WRENCH.get());
                        output.accept(ToolItems.WIRE_CUTTER.get());
                        Item base = FunctionalBlocks.BASE_CHEST_BLOCK.get().asItem();
                        Item death = FunctionalBlocks.DEATH_CHEST_BLOCK.get().asItem();
                        for (BaseChestBlock.Variant variant : BaseChestBlock.Variant.values()) {
                            output.accept(BaseChestBlock.setData(base.getDefaultInstance(), variant));
                            if (variant.getSerializedName().startsWith("unlocked")) { // 只放解锁的
                                output.accept(DeathChestBlock.setData(death.getDefaultInstance(), variant));
                            }
                        }
                        FunctionalBlocks.BLOCKS.getEntries().forEach(block -> output.accept(block.get()));
                    }).build());
}
