package org.confluence.mod.common.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.Confluence;

public class ShimmerTransmutationTrigger extends SimpleCriterionTrigger<ShimmerTransmutationTrigger.TriggerInstance> {
    public static final ResourceLocation ID = Confluence.asResource("shimmer_transmutation");

    public void trigger(ServerPlayer pPlayer, Entity entity) {
        trigger(pPlayer, instance -> instance.matches(pPlayer, entity));
    }

    @Override
    protected TriggerInstance createInstance(JsonObject json, ContextAwarePredicate predicate, DeserializationContext deserializationContext) {
        return new TriggerInstance(predicate, EntityPredicate.fromJson(json.get("entity")));
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final EntityPredicate entity;

        public TriggerInstance(ContextAwarePredicate player, EntityPredicate entity) {
            super(ID, player);
            this.entity = entity;
        }

        public boolean matches(ServerPlayer serverPlayer, Entity itemEntity) {
            return entity.matches(serverPlayer, itemEntity);
        }
    }
}
