package org.confluence.mod.common.data.map;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import PortLib.extensions.java.util.List.PortListExtension;
import PortLib.extensions.net.minecraft.advancements.critereon.EntityPredicate.PortEntityPredicateExtension;
import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModDataMaps;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public record BugNetEntityToItem(List<Tuple<EntityPredicate, ItemStack>> list) {
    public static final EntityPredicate EMPTY_PREDICATE = EntityPredicate.Builder.entity().build();
    public static final Codec<BugNetEntityToItem> CODEC = PortCodecExtension.lazyInitialized(() -> {
        Codec<Tuple<EntityPredicate, ItemStack>> codec = RecordCodecBuilder.create(instance -> instance.group(
                PortCodecExtension.lenientOptionalFieldOf(PortEntityPredicateExtension.codec(), "predicate", EMPTY_PREDICATE).forGetter(Tuple::getA),
                PortItemStackExtension.codec().fieldOf("result").forGetter(Tuple::getB)
        ).apply(instance, Tuple::new));
        return Codec.either(codec.listOf(), codec).xmap(
                either -> either.map(Function.identity(), List::of),
                tuples -> tuples.size() == 1 ? Either.right(PortListExtension.getFirst(tuples)) : Either.left(tuples)
        ).xmap(BugNetEntityToItem::new, BugNetEntityToItem::list);
    });

    public static @Nullable ItemStack getItem(ServerPlayer player, LivingEntity living) {
        BugNetEntityToItem data = ModDataMaps.getEntityData(ModDataMaps.BUG_NET_ENTITY_TO_ITEM, living);
        if (data != null) {
            for (Tuple<EntityPredicate, ItemStack> tuple : data.list) {
                if (tuple.getA().matches(player, living)) {
                    return tuple.getB().copy();
                }
            }
        }
        return null;
    }
}
