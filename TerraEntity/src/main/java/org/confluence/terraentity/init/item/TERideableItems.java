package org.confluence.terraentity.init.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terraentity.entity.rideable.RideableBee;
import org.confluence.terraentity.entity.rideable.RideableSlime;
import org.confluence.terraentity.init.entity.TERideableEntities;
import org.confluence.terraentity.item.RideableItem;

import static org.confluence.terraentity.TerraEntity.MODID;

public class TERideableItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredItem<RideableItem<RideableSlime>> SLIMY_SADDLE = ITEMS.register("slimy_saddle",()->new RideableItem<>(new Item.Properties(), TERideableEntities.RIDEABLE_SLIME));
    public static final DeferredItem<RideableItem<RideableBee>> HONEYED_GOGGLES = ITEMS.register("honeyed_goggles",()->new RideableItem<>(new Item.Properties(), TERideableEntities.RIDEABLE_BEE));


}
