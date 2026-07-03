package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.advancement.IntVariantEntitySubPredicate;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.BugNetEntityToItem;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.entity.CritterEntities;
import org.confluence.mod.common.init.item.BaitItems;
import org.mesdag.portlib.datamap.PortDataMapProvider;
import org.mesdag.portlib.registries.PortDeferredItem;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class BugNetEntityToItemSubProvider {
    public static void gather(ModDataMapProvider.Appender<Builder> appender) {
        appender.create()
                .add(CritterEntities.GLOWING_SNAIL, BaitItems.GLOWING_SNAIL)
                .add(CritterEntities.GRUBBY, BaitItems.GRUBBY)
                .add(CritterEntities.MAGGOT, BaitItems.MAGGOT)
                .add(CritterEntities.MAGMA_SNAIL, BaitItems.MAGMA_SNAIL)
                .add(CritterEntities.HELL_BUTTERFLY, BaitItems.HELL_BUTTERFLY)
                .add(CritterEntities.PRISMATIC_LACEWING, BaitItems.PRISMATIC_LACEWING)
                .add(CritterEntities.SLUGGY, BaitItems.SLUGGY)
                .add(CritterEntities.SNAIL, BaitItems.SNAIL)
                .add(CritterEntities.BUTTERFLY, List.of(
                        intVariant(0, BaitItems.GOLD_BUTTERFLY),
                        intVariant(1, BaitItems.JULIA_BUTTERFLY),
                        intVariant(2, BaitItems.MONARCH_BUTTERFLY),
                        intVariant(3, BaitItems.PURPLE_EMPEROR_BUTTERFLY),
                        intVariant(4, BaitItems.RED_ADMIRAL_BUTTERFLY),
                        intVariant(5, BaitItems.SULPHUR_BUTTERFLY),
                        intVariant(6, BaitItems.TREE_NYMPH_BUTTERFLY),
                        intVariant(7, BaitItems.ULYSSES_BUTTERFLY),
                        intVariant(8, BaitItems.ZEBRA_SWALLOWTAIL_BUTTERFLY)
                ))
                .add(CritterEntities.DRAGONFLY, List.of(
                        intVariant(0, BaitItems.BLACK_DRAGONFLY),
                        intVariant(1, BaitItems.BLUE_DRAGONFLY),
                        intVariant(2, BaitItems.GOLD_DRAGONFLY),
                        intVariant(3, BaitItems.GREEN_DRAGONFLY),
                        intVariant(4, BaitItems.ORANGE_DRAGONFLY),
                        intVariant(5, BaitItems.RED_DRAGONFLY),
                        intVariant(6, BaitItems.YELLOW_DRAGONFLY)
                ))
                .add(CritterEntities.LADYBUG, List.of(
                        intVariant(0, BaitItems.GOLD_LADYBUG),
                        intVariant(1, BaitItems.LADYBUG)
                ))
                .add(CritterEntities.WORM, List.of(
                        intVariant(0, BaitItems.NIGHTCRAWLER),
                        intVariant(1, BaitItems.GOLD_WORM),
                        intVariant(2, BaitItems.WORM)
                ))
                .add(CritterEntities.SCORPION, List.of(
                        intVariant(0, BaitItems.BLACK_SCORPION),
                        intVariant(1, BaitItems.SCORPION)
                ))
                .add(CritterEntities.GRASSHOPPER, List.of(
                        intVariant(0, BaitItems.GOLD_GRASSHOPPER),
                        intVariant(1, BaitItems.GRASSHOPPER)
                ))
        ;
    }

    private static Tuple<EntityPredicate, ItemStack> intVariant(int variant, PortDeferredItem<?> item) {
        return new Tuple<>(EntityPredicate.Builder.entity().subPredicate(new IntVariantEntitySubPredicate(variant)).build(), item.toStack());
    }

    public static class Builder extends PortDataMapProvider.Builder<BugNetEntityToItem, EntityType<?>> {
        public Builder() {
            super(ModDataMaps.BUG_NET_ENTITY_TO_ITEM);
        }

        public Builder add(Supplier<? extends EntityType<?>> holder, PortDeferredItem<?> item) {
            super.add(Objects.requireNonNull(holder.get().builtInRegistryHolder().getKey()), new BugNetEntityToItem(Collections.singletonList(new Tuple<>(BugNetEntityToItem.EMPTY_PREDICATE, item.toStack()))), false);
            return this;
        }

        public Builder add(Supplier<? extends EntityType<?>> holder, List<Tuple<EntityPredicate, ItemStack>> list) {
            super.add(Objects.requireNonNull(holder.get().builtInRegistryHolder().getKey()), new BugNetEntityToItem(list), false);
            return this;
        }
    }
}
