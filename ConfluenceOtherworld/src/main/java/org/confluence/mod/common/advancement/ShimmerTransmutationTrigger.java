package org.confluence.mod.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ShimmerTransmutationTrigger extends SimpleCriterionTrigger<ShimmerTransmutationTrigger.TriggerInstance> {
    public void trigger(ServerPlayer pPlayer, Entity entity) {
        trigger(pPlayer, instance -> instance.matches(pPlayer, entity));
    }

    @Override
    public @NotNull Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player,
                                  Optional<EntityPredicate> entity) implements SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                EntityPredicate.CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::entity)
        ).apply(instance, TriggerInstance::new));

        public boolean matches(ServerPlayer serverPlayer, Entity itemEntity) {
            return entity.isEmpty() || entity.get().matches(serverPlayer, itemEntity);
        }

        @Override
        public @NotNull Optional<ContextAwarePredicate> player() {
            return player;
        }
    }
}
