package org.confluence.mod.common.data.gen;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.item.*;
import org.confluence.terraentity.init.item.TEBoomerangItems;

import java.util.*;

import static org.confluence.mod.Confluence.MODID;

public class ModItemModelProvider extends ItemModelProvider {
    private static final ResourceLocation MISSING_ITEM = Confluence.asResource("item/item_icon");
    private static final ResourceLocation MISSING_BLOCK = Confluence.asResource("item/blocks_icon");
    private final ModelFile itemGenerated = new ModelFile.UncheckedModelFile(ResourceLocation.withDefaultNamespace("item/generated"));
    private final Set<DeferredHolder<Item, ? extends Item>> skip = new HashSet<>();

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ModelFile.UncheckedModelFile templateReverse24x = new ModelFile.UncheckedModelFile(Confluence.asResource("item/template_reverse24x"));
        ModelFile.UncheckedModelFile templateNormal24x = new ModelFile.UncheckedModelFile(Confluence.asResource("item/template_normal24x"));

        separateModel(SwordItems.BEE_KEEPER, templateReverse24x, "sword/");
        separateModel(SwordItems.ICE_BLADE, templateReverse24x, "sword/");
        separateModel(SwordItems.MURAMASA, templateReverse24x, "sword/");
        separateModel(SwordItems.LIGHTS_BANE, templateReverse24x, "sword/");
        separateModel(SwordItems.BLADE_OF_GRASS, templateReverse24x, "sword/");
        separateModel(SwordItems.BONE_SWORD, templateReverse24x, "sword/");
        separateModel(SwordItems.BLOOD_BUTCHERER, templateReverse24x, "sword/");
        separateModel(SwordItems.PURPLE_CLUBBERFISH, templateReverse24x, "sword/");
        separateModel(SwordItems.CANDY_CANE_SWORD, templateReverse24x, "sword/");
        separateModel(SwordItems.BAT_BAT, templateReverse24x, "sword/");
        separateModel(SwordItems.KATANA, templateReverse24x, "sword/");
        separateModel(HammerItems.THE_BREAKER, templateReverse24x, "hammer/");
        separateModel(HammerItems.FLESH_GRINDER, templateNormal24x, "hammer/");
        separateModel(HammerItems.ROCKFISH, templateNormal24x, "hammer/");
        separateModel(HamaxeItems.METEOR_HAMAXE, templateNormal24x, "hamaxe/");
        separateModel(HamaxeItems.MOLTEN_HAMAXE, templateNormal24x, "hamaxe/");
        separateModel(AxeItems.WAR_AXE_OF_THE_NIGHT, templateReverse24x, "axe/");
        separateModel(AxeItems.BLOOD_LUST_CLUSTER, templateNormal24x, "axe/");
        separateModel(AxeItems.AXE_OF_REGROWTH, templateNormal24x, "axe/");
        separateModel(AxeItems.STAFF_OF_REGROWTH, templateNormal24x, "axe/");
        separateModel(PickaxeItems.REAVER_SHARK_PICKAXE, templateReverse24x, "pickaxe/");

        getBuilder(SwordItems.NIGHTS_EDGE.getId().getPath()).parent(templateReverse24x).texture("layer0", SwordItems.NIGHTS_EDGE.getId().withPrefix("item/sword/"));
        skip.add(SwordItems.NIGHTS_EDGE);

        ResourceLocation templateDye = Confluence.asResource("item/template_dye");
        for (DeferredHolder<Item, ? extends Item> item : VanityArmorItems.DYE_ITEMS) {
            withExistingParent(item.getId().getPath(), templateDye);
            skip.add(item);
        }

        ResourceLocation handheldRod = ResourceLocation.withDefaultNamespace("item/handheld_rod");
        ResourceLocation pulling = ResourceLocation.withDefaultNamespace("pulling");
        ResourceLocation pull = ResourceLocation.withDefaultNamespace("pull");
        for (DeferredHolder<Item, ? extends Item> item : BowItems.ITEMS.getEntries()) {
            String path = item.getId().getPath();
            ResourceLocation texture = Confluence.asResource("item/bow/" + path);
            ItemModelBuilder builder = withExistingParent(path, handheldRod).texture("layer0", texture)
                    .transforms()
                    .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(-80, 260, -40).translation(-1, -2, 2.5F).scale(0.9F, 0.9F, 0.9F).end()
                    .transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).rotation(-80, -280, 40).translation(-1, -2, 2.5F).scale(0.9F, 0.9F, 0.9F).end()
                    .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0, -90, 25).translation(1.13F, 3.2F, 1.13F).scale(0.68F, 0.68F, 0.68F).end()
                    .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(0, 90, -25).translation(1.13F, 3.2F, 1.13F).scale(0.68F, 0.68F, 0.68F).end()
                    .end()
                    .override().predicate(pulling, 1).model(new ModelFile.UncheckedModelFile(Confluence.asResource("item/" + path + "_pulling_0"))).end();
            ResourceLocation parent = Confluence.asResource("item/" + path);
            withExistingParent(path + "_pulling_0", parent).texture("layer0", texture.withSuffix("_pulling_0"));
            if (!path.endsWith("short_bow")) {
                builder
                        .override().predicate(pulling, 1).predicate(pull, 0.65F).model(new ModelFile.UncheckedModelFile(Confluence.asResource("item/" + path + "_pulling_1"))).end()
                        .override().predicate(pulling, 1).predicate(pull, 0.9F).model(new ModelFile.UncheckedModelFile(Confluence.asResource("item/" + path + "_pulling_2"))).end();
                withExistingParent(path + "_pulling_1", parent).texture("layer0", texture.withSuffix("_pulling_1"));
                withExistingParent(path + "_pulling_2", parent).texture("layer0", texture.withSuffix("_pulling_2"));
            }
            skip.add(item);
        }

        // 一般物品
        // tip：MATERIALS的贴图分多个文件夹 "materials/","gem/","ingot/","ore/"，物品多的文件夹放前面提高速度
        List<Map<DeferredRegister.Items, String[]>> customModels = new ArrayList<>();
        customModels.add(createDir(AccessoryItems.ITEMS, "accessory/"));
        customModels.add(createDir(ArmorItems.ITEMS, "armor_item/"));
        customModels.add(createDir(ArrowItems.ITEMS, "arrow/"));
        customModels.add(createDir(BaitItems.ITEMS, "bait/"));
        customModels.add(createDir(ConsumableItems.ITEMS, "consumable/"));
        customModels.add(createDir(FoodItems.ITEMS, "food/", "seed/"));
        customModels.add(createDir(HookItems.ITEMS, "hook/"));
        customModels.add(createDir(IconItems.ITEMS, "icon/"));
        customModels.add(createDir(LightPetItems.ITEMS, "light_pet/"));
        customModels.add(createDir(MaterialItems.ITEMS, "materials/", "ingot/", "ore/"));
        customModels.add(createDir(MinecartItems.ITEMS, "minecart/"));
        customModels.add(createDir(ModItems.ITEMS, "misc/"));
        customModels.add(createDir(ModItems.HIDDEN, "misc/"));
        customModels.add(createDir(PaintItems.ITEMS, "paint/"));
        customModels.add(createDir(PotionItems.ITEMS, "potion/"));
        customModels.add(createDir(QuestedFishes.ITEMS, "quested_fish/"));
        customModels.add(createDir(ToolItems.ITEMS, "tool/"));
        customModels.add(createDir(TreasureBagItems.ITEMS, "treasure_bag/"));
        customModels.add(createDir(VanityArmorItems.ITEMS, "vanity_armor/"));

        genModels(customModels, "item/generated");

        // 手持物品
        List<Map<DeferredRegister.Items, String[]>> handheld = new ArrayList<>();
        handheld.add(createDir(SwordItems.ITEMS, "sword/"));
        handheld.add(createDir(AxeItems.ITEMS, "axe/"));
        handheld.add(createDir(HammerItems.ITEMS, "hammer/"));
        handheld.add(createDir(TEBoomerangItems.ITEMS, "boomerang/"));
        handheld.add(createDir(PickaxeItems.ITEMS, "pickaxe/"));
        handheld.add(createDir(PickaxeAxeItems.ITEMS, "pickaxe_axe/"));
        handheld.add(createDir(ManaWeaponItems.ITEMS, "mana_staff/"));
        handheld.add(createDir(HoeItems.ITEMS, "hoe/"));
        handheld.add(createDir(ShovelItems.ITEMS, "shovel/"));
        handheld.add(createDir(HamaxeItems.ITEMS, "hamaxe/"));
        handheld.add(createDir(HoeShovelItems.ITEMS, "hoe_shovel/"));

        genModels(handheld, "item/handheld");

        // 方块物品
        for (DeferredHolder<Item, ? extends Item> item : ModItems.BLOCK_ITEMS.getEntries()) {
            Item item1 = item.get();
            String path = item.getId().getPath();
            try {
                if (item1 instanceof BlockItem item2) {
                    Block block = item2.getBlock();
                    if (block instanceof DoorBlock) {
                        withExistingParent(path, "item/generated").texture("layer0", Confluence.asResource("item/" + path));
                    } else if (block instanceof TrapDoorBlock) {
                        withExistingParent(path, Confluence.asResource("block/" + path + "_bottom"));
                    } else {
                        withExistingParent(path, Confluence.asResource("block/" + path + (hasInventory(block) ? "_inventory" : "")));
                    }
                }
            } catch (Exception ignored) {
                withExistingParent(path, MISSING_BLOCK);
            }
        }
    }

    /**
     * 手持高清但是物品栏16x
     */
    private void separateModel(DeferredItem<?> deferredItem, ModelFile parentModel, String parentPath) {
        String path = deferredItem.getId().getPath();
        getBuilder(path).guiLight(BlockModel.GuiLight.FRONT).customLoader((builder, helper) -> {
            ResourceLocation texture = Confluence.asResource("item/" + parentPath + path);
            ItemModelBuilder standaloneModel = new ItemModelBuilder(Confluence.asResource("item/" + path + "_inventory"), helper)
                    .parent(itemGenerated)
                    .texture("layer0", texture.withSuffix("_inventory"));
            return SeparateTransformsModelBuilder.begin(builder, helper)
                    .base(new ItemModelBuilder(Confluence.asResource("item/" + path + "_base"), helper)
                            .parent(parentModel)
                            .texture("layer0", texture))
                    .perspective(ItemDisplayContext.HEAD, standaloneModel)
                    .perspective(ItemDisplayContext.GUI, standaloneModel)
                    .perspective(ItemDisplayContext.GROUND, standaloneModel)
                    .perspective(ItemDisplayContext.FIXED, standaloneModel);
        });
        skip.add(deferredItem);
    }

    private static boolean hasInventory(Block block) {
        return block instanceof ButtonBlock || block instanceof FenceBlock;
    }

    private Map<DeferredRegister.Items, String[]> createDir(DeferredRegister.Items reg, String... packPaths) {
        return Map.of(reg, packPaths);
    }

    private void genModels(List<Map<DeferredRegister.Items, String[]>> list, String parent) {
        for (Map<DeferredRegister.Items, String[]> map : list) {
            for (Map.Entry<DeferredRegister.Items, String[]> entry : map.entrySet()) {
                for (DeferredHolder<Item, ? extends Item> item : entry.getKey().getEntries()) {
                    if (skip.contains(item)) continue;
                    String path = item.getId().getPath();
                    boolean exist = false;
                    for (String packPath : entry.getValue()) {
                        try {
                            withExistingParent(path, parent).texture("layer0", Confluence.asResource("item/" + packPath + path));
                            exist = true;
                            break;
                        } catch (Exception ignored) {}
                    }
                    if (!exist) withExistingParent(path, MISSING_ITEM);
                }
            }
        }
    }
}
