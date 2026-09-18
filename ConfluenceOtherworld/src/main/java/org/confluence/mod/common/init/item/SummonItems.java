package org.confluence.mod.common.init.item;

import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.common.item.SummonerWeaponItem;
import org.confluence.mod.common.item.summon.SummonItem;
import org.confluence.mod.common.summon.SummonTypes;
import org.confluence.mod.common.summon.dragon.StardustDragonSummon;
import org.confluence.mod.common.summon.flying.*;
import org.confluence.mod.common.summon.ground.*;
import org.confluence.mod.common.summon.slime.SlimeSummon;
import org.confluence.mod.common.summon.terraprisma.TerraprismaSummon;
import org.confluence.mod.common.summoner.minion.HornetMinion;
import org.confluence.mod.common.summoner.minion.MinionSlotType;
import org.confluence.mod.common.summoner.register.SummonerAttachmentEntityTypes;
import org.confluence.mod.common.summoner.register.SummonerSoundEvents;
import org.mesdag.portlib.registries.PortDeferredItem;
import org.mesdag.portlib.registries.PortItemRegistration;
import org.mesdag.portlib.registries.PortRegisterHandler;
import net.minecraft.world.item.Item;

public class SummonItems {
    public static void init() {}

    public static final PortItemRegistration ITEMS = PortRegisterHandler.item(Confluence.MODID);

    public static final PortDeferredItem<SummonItem> FINCH_STAFF = ITEMS.register("finch_staff",
            () -> new SummonItem(ModRarity.BLUE, SummonTypes.FINCH, FinchSummon.SLOT_COST, FinchSummon.BASE_DAMAGE));
    public static final PortDeferredItem<SummonItem> IRON_GOLEM_STAFF = ITEMS.register("iron_golem_staff",
            () -> new SummonItem(ModRarity.BLUE, SummonTypes.IRON_GOLEM, IronGolemSummon.SLOT_COST, IronGolemSummon.BASE_DAMAGE));
    public static final PortDeferredItem<SummonItem> SLIME_STAFF = ITEMS.register("slime_staff",
            () -> new SummonItem(ModRarity.LIGHT_RED, SummonTypes.SLIME, SlimeSummon.SLOT_COST, SlimeSummon.BASE_DAMAGE));
    public static final PortDeferredItem<SummonItem> SCULK_WISP_STAFF = ITEMS.register("sculk_wisp_staff",
            () -> new SummonItem(ModRarity.ORANGE, SummonTypes.SCULK_WISP, SculkWispSummon.SLOT_COST, SculkWispSummon.BASE_DAMAGE));
    public static final PortDeferredItem<SummonItem> IMP_STAFF = ITEMS.register("imp_staff",
            () -> new SummonItem(ModRarity.ORANGE, SummonTypes.IMP, ImpSummon.SLOT_COST, ImpSummon.BASE_DAMAGE).setSound(ModSoundEvents.SUMMON_IMP));
    public static final PortDeferredItem<SummonItem> SNOW_FLINX_STAFF = ITEMS.register("snow_flinx_staff",
            () -> new SummonItem(ModRarity.ORANGE, SummonTypes.SNOW_FLINX, SnowFlinxSummon.SLOT_COST, SnowFlinxSummon.BASE_DAMAGE));
    public static final PortDeferredItem<SummonItem> VAMPIRE_FROG_STAFF = ITEMS.register("vampire_frog_staff",
            () -> new SummonItem(ModRarity.ORANGE, SummonTypes.VAMPIRE_FROG, VampireFrogSummon.SLOT_COST, VampireFrogSummon.BASE_DAMAGE));
    public static final PortDeferredItem<SummonItem> DEADLY_SPHERE_STAFF = ITEMS.register("deadly_sphere_staff",
            () -> new SummonItem(ModRarity.YELLOW, SummonTypes.DEADLY_SPHERE, DeadlySphereSummon.SLOT_COST, DeadlySphereSummon.BASE_DAMAGE));
    public static final PortDeferredItem<SummonItem> SANGUINE_STAFF = ITEMS.register("sanguine_staff",
            () -> new SummonItem(ModRarity.LIGHT_RED, SummonTypes.VAMPIRE_BAT, VampireBatSummon.SLOT_COST, VampireBatSummon.BASE_DAMAGE));
    public static final PortDeferredItem<SummonItem> SPIDER_STAFF = ITEMS.register("spider_staff",
            () -> new SummonItem(ModRarity.LIGHT_RED, SummonTypes.SPIDER, SpiderSummon.SLOT_COST, SpiderSummon.BASE_DAMAGE));
    public static final PortDeferredItem<SummonItem> DESERT_TIGER_STAFF = ITEMS.register("desert_tiger_staff",
            () -> new SummonItem(ModRarity.YELLOW, SummonTypes.DESERT_TIGER, DesertTigerSummon.SLOT_COST, DesertTigerSummon.BASE_DAMAGE));
    public static final PortDeferredItem<SummonItem> TERRAPRISMA = ITEMS.register("terraprisma",
            () -> new SummonItem(ModRarity.PINK, SummonTypes.TERRAPRISMA, TerraprismaSummon.SLOT_COST, TerraprismaSummon.BASE_DAMAGE));
    public static final PortDeferredItem<SummonItem> STARDUST_DRAGON_STAFF = ITEMS.register("stardust_dragon_staff",
            () -> new SummonItem(ModRarity.RED, SummonTypes.STARDUST_DRAGON, StardustDragonSummon.SLOT_COST, StardustDragonSummon.BASE_DAMAGE));
    public static final PortDeferredItem<SummonerWeaponItem<HornetMinion>> NEW_HORNET_STAFF = ITEMS.register("new_hornet_staff",
            () -> new SummonerWeaponItem<>(
                    new Item.Properties().stacksTo(1).component(ConfluenceMagicLib.MOD_RARITY, ModRarity.ORANGE),
                    SummonerAttachmentEntityTypes.HORNET,
                    MinionSlotType.Minion,
                    8.0F,
                    0.2F,
                    0.0F,
                    SummonerSoundEvents.USE_MINION_WEAPON,
                    null,
                    null
            ));

}
