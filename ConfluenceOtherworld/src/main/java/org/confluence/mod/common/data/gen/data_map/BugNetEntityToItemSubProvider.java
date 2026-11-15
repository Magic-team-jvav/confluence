package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.common.extensions.IHolderExtension;
import net.neoforged.neoforge.registries.DeferredItem;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.BugNetEntityToItem;
import org.confluence.mod.common.entity.IVariant;
import org.confluence.mod.common.entity.animal.Butterfly;
import org.confluence.mod.common.entity.animal.Dragonfly;
import org.confluence.mod.common.entity.animal.Grasshopper;
import org.confluence.mod.common.entity.animal.Ladybug;
import org.confluence.mod.common.entity.animal.Scorpion;
import org.confluence.mod.common.entity.animal.Worm;
import org.confluence.mod.common.init.ModAdvancements;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.item.BaitItems;
import org.confluence.mod.common.init.entity.CritterEntities;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
                .add(CritterEntities.BUGGY, BaitItems.BUGGY)
                .add(CritterEntities.STINKBUG, BaitItems.STINKBUG)
                .add(CritterEntities.SNAIL, BaitItems.SNAIL)
                .add(CritterEntities.TRUFFLE_WORM, BaitItems.TRUFFLE_WORM)
                .add(CritterEntities.FIREFLY, BaitItems.FIREFLY)
                .add(CritterEntities.LIGHTNING_BUG, BaitItems.LIGHTNING_BUG)
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
                        variant(Worm.Variant.NIGHTCRAWLER, BaitItems.ENCHANTED_NIGHTCRAWLER),
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

    /// 注意：这里必须按**变体名**匹配，不能按枚举序号。变体枚举的声明顺序与 1.20.1 不同
    /// （例如 `Worm.Variant` 是 `NORMAL, GOLD, NIGHTCRAWLER`），写死序号会串味。
    private static Tuple<EntityPredicate, ItemStack> variant(IVariant variant, DeferredItem<?> item) {
        return new Tuple<>(EntityPredicate.Builder.entity()
                .subPredicate(ModAdvancements.EntitySubPredicatez.VARIANT_NAME.createPredicate(variant.getSerializedName()))
                .build(), item.toStack());
    }

    public static class Builder extends DataMapProvider.Builder<BugNetEntityToItem, EntityType<?>> {
        public Builder() {
            super(ModDataMaps.BUG_NET_ENTITY_TO_ITEM);
        }

        public Builder add(IHolderExtension<EntityType<?>> holder, DeferredItem<?> item) {
            super.add(Objects.requireNonNull(holder.getKey()), new BugNetEntityToItem(Collections.singletonList(new Tuple<>(BugNetEntityToItem.EMPTY_PREDICATE, item.toStack()))), false);
            return this;
        }

        public Builder add(IHolderExtension<EntityType<?>> holder, List<Tuple<EntityPredicate, ItemStack>> list) {
            super.add(Objects.requireNonNull(holder.getKey()), new BugNetEntityToItem(list), false);
            return this;
        }
    }
}
