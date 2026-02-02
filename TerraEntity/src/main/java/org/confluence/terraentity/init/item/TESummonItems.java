package org.confluence.terraentity.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.TESounds;
import org.confluence.terraentity.init.entity.TESummonEntities;
import org.confluence.terraentity.item.SummonItem;


public class TESummonItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TerraEntity.MODID);

    public static final DeferredItem<Item> FINCH_STAFF = ITEMS.register("finch_staff", () -> new SummonItem<>(new Item.Properties(), TESummonEntities.SUMMON_FINCH, 1, 2));
    public static final DeferredItem<Item> IRON_GOLEM_STAFF = ITEMS.register("iron_golem_staff", () -> new SummonItem<>(new Item.Properties(), TESummonEntities.SUMMON_IRON_GOLEM, 1, 8));
    public static final DeferredItem<Item> SLIME_STAFF = ITEMS.register("slime_staff", () -> new SummonItem<>(new Item.Properties(), TESummonEntities.SUMMON_SLIME, 1, 5));
    public static final DeferredItem<Item> HORNET_STAFF = ITEMS.register("hornet_staff", () -> new SummonItem<>(new Item.Properties(), TESummonEntities.SUMMON_HORNET, 1, 8).setSound(TESounds.SUMMON_HORNET));
    public static final DeferredItem<Item> SCULK_WISP_STAFF = ITEMS.register("sculk_wisp_staff", () -> new SummonItem<>(new Item.Properties(), TESummonEntities.SCULK_WISP, 1, 7));
    public static final DeferredItem<Item> IMP_STAFF = ITEMS.register("imp_staff", () -> new SummonItem<>(new Item.Properties(), TESummonEntities.IMP, 1, 14).setSound(TESounds.SUMMON_IMP));
    public static final DeferredItem<Item> SNOW_FLINX_STAFF = ITEMS.register("snow_flinx_staff", () -> new SummonItem<>(new Item.Properties(), TESummonEntities.SUMMON_SNOW_FLINX, 1, 7));


    //棱镜系列
    public static final DeferredItem<Item> SUMMON_WOODEN_SWORD_STAFF = ITEMS.register("summon_wooden_sword_staff", () -> new SummonItem<>(new Item.Properties(), TESummonEntities.SUMMON_WOODEN_SWORD, 1, 2));
    public static final DeferredItem<Item> SUMMON_STONE_SWORD_STAFF = ITEMS.register("summon_stone_sword_staff", () -> new SummonItem<>(new Item.Properties(), TESummonEntities.SUMMON_STONE_SWORD, 1, 3));
    public static final DeferredItem<Item> SUMMON_IRON_SWORD_STAFF = ITEMS.register("summon_iron_sword_staff", () -> new SummonItem<>(new Item.Properties(), TESummonEntities.SUMMON_IRON_SWORD, 1, 4));
    public static final DeferredItem<Item> SUMMON_GOLDEN_SWORD_STAFF = ITEMS.register("summon_golden_sword_staff", () -> new SummonItem<>(new Item.Properties(), TESummonEntities.SUMMON_GOLDEN_SWORD, 1, 5));
    public static final DeferredItem<Item> SUMMON_DIAMOND_SWORD_STAFF = ITEMS.register("summon_diamond_sword_staff", () -> new SummonItem<>(new Item.Properties(), TESummonEntities.SUMMON_DIAMOND_SWORD, 1, 6));
    public static final DeferredItem<Item> SUMMON_NETHERITE_SWORD_STAFF = ITEMS.register("summon_netherite_sword_staff", () -> new SummonItem<>(new Item.Properties(), TESummonEntities.SUMMON_NETHERITE_SWORD, 1, 7));
    public static final DeferredItem<Item> TERRAPRISMA = ITEMS.register("terraprisma", () -> new SummonItem<>(new Item.Properties(), TESummonEntities.TERRAPRISMA, 1, 18));


}
