package org.confluence.mod.common.init.item;

import net.minecraft.world.item.Item;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.summon.SummonItem;
import org.confluence.mod.common.summon.SummonType;
import org.confluence.mod.common.summon.SummonTypes;
import org.confluence.mod.common.summon.dragon.StardustDragonSummon;
import org.confluence.mod.common.summon.flying.FinchSummon;
import org.confluence.mod.common.summon.flying.HornetSummon;
import org.confluence.mod.common.summon.flying.ImpSummon;
import org.confluence.mod.common.summon.flying.SculkWispSummon;
import org.confluence.mod.common.summon.ground.IronGolemSummon;
import org.confluence.mod.common.summon.ground.SnowFlinxSummon;
import org.confluence.mod.common.summon.slime.SlimeSummon;
import org.confluence.mod.common.summon.terraprisma.TerraprismaSummon;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;

public class SummonItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<SummonItem> FINCH_STAFF = ITEMS.register("finch_staff",
            () -> new SummonItem(new Item.Properties(), SummonTypes.FINCH, FinchSummon.SLOT_COST, FinchSummon.BASE_DAMAGE));
    public static final PortDeferredItem<SummonItem> IRON_GOLEM_STAFF = ITEMS.register("iron_golem_staff",
            () -> new SummonItem(new Item.Properties(), SummonTypes.IRON_GOLEM, IronGolemSummon.SLOT_COST, IronGolemSummon.BASE_DAMAGE));
    public static final PortDeferredItem<SummonItem> SLIME_STAFF = ITEMS.register("slime_staff",
            () -> new SummonItem(new Item.Properties(), SummonTypes.SLIME, SlimeSummon.SLOT_COST, SlimeSummon.BASE_DAMAGE));
    public static final PortDeferredItem<SummonItem> HORNET_STAFF = ITEMS.register("hornet_staff",
            () -> new SummonItem(new Item.Properties(), SummonTypes.HORNET, HornetSummon.SLOT_COST, HornetSummon.BASE_DAMAGE).setSound(ModSoundEvents.SUMMON_HORNET));
    public static final PortDeferredItem<SummonItem> SCULK_WISP_STAFF = ITEMS.register("sculk_wisp_staff",
            () -> new SummonItem(new Item.Properties(), SummonTypes.SCULK_WISP, SculkWispSummon.SLOT_COST, SculkWispSummon.BASE_DAMAGE));
    public static final PortDeferredItem<SummonItem> IMP_STAFF = ITEMS.register("imp_staff",
            () -> new SummonItem(new Item.Properties(), SummonTypes.IMP, ImpSummon.SLOT_COST, ImpSummon.BASE_DAMAGE).setSound(ModSoundEvents.SUMMON_IMP));
    public static final PortDeferredItem<SummonItem> SNOW_FLINX_STAFF = ITEMS.register("snow_flinx_staff",
            () -> new SummonItem(new Item.Properties(), SummonTypes.SNOW_FLINX, SnowFlinxSummon.SLOT_COST, SnowFlinxSummon.BASE_DAMAGE));
    public static final PortDeferredItem<SummonItem> SUMMON_WOODEN_SWORD_STAFF = registerSword("summon_wooden_sword_staff", 2, SummonTypes.SUMMON_WOODEN_SWORD);
    public static final PortDeferredItem<SummonItem> SUMMON_STONE_SWORD_STAFF = registerSword("summon_stone_sword_staff", 3, SummonTypes.SUMMON_STONE_SWORD);
    public static final PortDeferredItem<SummonItem> SUMMON_IRON_SWORD_STAFF = registerSword("summon_iron_sword_staff", 4, SummonTypes.SUMMON_IRON_SWORD);
    public static final PortDeferredItem<SummonItem> SUMMON_GOLDEN_SWORD_STAFF = registerSword("summon_golden_sword_staff", 5, SummonTypes.SUMMON_GOLDEN_SWORD);
    public static final PortDeferredItem<SummonItem> SUMMON_DIAMOND_SWORD_STAFF = registerSword("summon_diamond_sword_staff", 6, SummonTypes.SUMMON_DIAMOND_SWORD);
    public static final PortDeferredItem<SummonItem> SUMMON_NETHERITE_SWORD_STAFF = registerSword("summon_netherite_sword_staff", 7, SummonTypes.SUMMON_NETHERITE_SWORD);
    public static final PortDeferredItem<SummonItem> TERRAPRISMA = ITEMS.register("terraprisma",
            () -> new SummonItem(new Item.Properties(), SummonTypes.TERRAPRISMA, TerraprismaSummon.SLOT_COST, TerraprismaSummon.BASE_DAMAGE));
    public static final PortDeferredItem<SummonItem> STARDUST_DRAGON_STAFF = ITEMS.register("stardust_dragon_staff",
            () -> new SummonItem(new Item.Properties(), SummonTypes.STARDUST_DRAGON, StardustDragonSummon.SLOT_COST, StardustDragonSummon.BASE_DAMAGE));

    private static PortDeferredItem<SummonItem> registerSword(String name, float baseDamage, SummonType type) {
        return ITEMS.register(name, () -> new SummonItem(new Item.Properties(), type, 1, baseDamage));
    }
}
