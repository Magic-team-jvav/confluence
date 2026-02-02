package org.confluence.terraentity.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.init.entity.TEBossEntities;
import org.confluence.terraentity.item.BossSummonsItem;

public class TEBossSummonsItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TerraEntity.MODID);

    public static DeferredItem<BossSummonsItem<?>> KING_SLIME_SUMMONS = ITEMS.register("slime_crown",
            () -> new BossSummonsItem<>(new Item.Properties(), TEBossEntities.KING_SLIME));

    public static DeferredItem<BossSummonsItem<?>> EYE_OF_CTHULHU_SUMMONS = ITEMS.register("suspicious_looking_eye",
            () -> new BossSummonsItem<>(new Item.Properties(), TEBossEntities.EYE_OF_CTHULHU));

    public static DeferredItem<BossSummonsItem<?>> EATER_OF_WORLDS_SUMMONS = ITEMS.register("worm_food",
            () -> new BossSummonsItem<>(new Item.Properties(), TEBossEntities.EATER_OF_WORLDS));

    public static DeferredItem<BossSummonsItem<?>> BRAIN_OF_CTHULHU_SUMMONS = ITEMS.register("bloody_spine",
            () -> new BossSummonsItem<>(new Item.Properties(), TEBossEntities.BRAIN_OF_CTHULHU));

    public static DeferredItem<BossSummonsItem<?>> QUEEN_BEE_SUMMONS = ITEMS.register("abeemination",
            () -> new BossSummonsItem<>(new Item.Properties(), TEBossEntities.QUEEN_BEE));

    public static DeferredItem<BossSummonsItem<?>> SKELETRON_SUMMONS = ITEMS.register("clothier_voodoo_doll",
            () -> new BossSummonsItem<>(new Item.Properties(), TEBossEntities.SKELETRON));

    public static DeferredItem<BossSummonsItem<?>> WALL_OF_FLESH_SUMMONS = ITEMS.register("guide_voodoo_doll_wall",
            () -> new BossSummonsItem<>(new Item.Properties(), TEBossEntities.WALL_OF_FLESH).setMaxSummonRange(100, 80));

    public static DeferredItem<BossSummonsItem<?>> HILL_OF_FLESH_SUMMONS = ITEMS.register("guide_voodoo_doll_hill",
            () -> new BossSummonsItem<>(new Item.Properties(), TEBossEntities.HILL_OF_FLESH).setMaxSummonRange(40, 15));



}
