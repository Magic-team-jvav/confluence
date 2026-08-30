package org.confluence.mod.common.data.gen.data_map;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.EntityVariantPredicate;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.BugNetEntityToItem;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.entity.animal.*;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.entity.CritterEntities;
import org.confluence.mod.common.init.item.BaitItems;
import org.mesdag.portlib.datamap.PortDataMapProvider;
import org.mesdag.portlib.registries.PortDeferredItem;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
                        variant(Butterfly.Variant.GOLD, BaitItems.GOLD_BUTTERFLY),
                        variant(Butterfly.Variant.JULIA, BaitItems.JULIA_BUTTERFLY),
                        variant(Butterfly.Variant.MONARCH, BaitItems.MONARCH_BUTTERFLY),
                        variant(Butterfly.Variant.PURPLE_EMPEROR, BaitItems.PURPLE_EMPEROR_BUTTERFLY),
                        variant(Butterfly.Variant.RED_ADMIRAL, BaitItems.RED_ADMIRAL_BUTTERFLY),
                        variant(Butterfly.Variant.SULPHUR, BaitItems.SULPHUR_BUTTERFLY),
                        variant(Butterfly.Variant.TREE_NYMPH, BaitItems.TREE_NYMPH_BUTTERFLY),
                        variant(Butterfly.Variant.ULYSSES, BaitItems.ULYSSES_BUTTERFLY),
                        variant(Butterfly.Variant.ZEBRA_SWALLOWTAIL, BaitItems.ZEBRA_SWALLOWTAIL_BUTTERFLY)
                ))
                .add(CritterEntities.DRAGONFLY, List.of(
                        variant(Dragonfly.Variant.BLACK, BaitItems.BLACK_DRAGONFLY),
                        variant(Dragonfly.Variant.BLUE, BaitItems.BLUE_DRAGONFLY),
                        variant(Dragonfly.Variant.GOLD, BaitItems.GOLD_DRAGONFLY),
                        variant(Dragonfly.Variant.GREEN, BaitItems.GREEN_DRAGONFLY),
                        variant(Dragonfly.Variant.ORANGE, BaitItems.ORANGE_DRAGONFLY),
                        variant(Dragonfly.Variant.RED, BaitItems.RED_DRAGONFLY),
                        variant(Dragonfly.Variant.YELLOW, BaitItems.YELLOW_DRAGONFLY)
                ))
                .add(CritterEntities.LADYBUG, List.of(
                        variant(Ladybug.Variant.GOLD, BaitItems.GOLD_LADYBUG),
                        variant(Ladybug.Variant.RED, BaitItems.LADYBUG)
                ))
                .add(CritterEntities.WORM, List.of(
                        variant(Worm.Variant.NIGHTCRAWLER, BaitItems.NIGHTCRAWLER),
                        variant(Worm.Variant.GOLD, BaitItems.GOLD_WORM),
                        variant(Worm.Variant.NORMAL, BaitItems.WORM)
                ))
                .add(CritterEntities.SCORPION, List.of(
                        variant(Scorpion.Variant.BLACK, BaitItems.BLACK_SCORPION),
                        variant(Scorpion.Variant.NORMAL, BaitItems.SCORPION)
                ))
                .add(CritterEntities.GRASSHOPPER, List.of(
                        variant(Grasshopper.Variant.GOLD, BaitItems.GOLD_GRASSHOPPER),
                        variant(Grasshopper.Variant.GREEN, BaitItems.GRASSHOPPER)
                ))
        ;
    }

    @SuppressWarnings("unchecked")
    private static Tuple<EntityPredicate, ItemStack> variant(IVariant variant, PortDeferredItem<?> item) {
        EntitySubPredicate predicate = EntityVariantPredicate.create((Codec<? super IVariant>) variant.codec(), entity -> {
            if (entity instanceof VariantHolder<?> holder && holder.getVariant() instanceof IVariant v) {
                return Optional.of(v);
            }
            return Optional.empty();
        }).createPredicate(variant);
        return new Tuple<>(EntityPredicate.Builder.entity().subPredicate(predicate).build(), item.toStack());
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
