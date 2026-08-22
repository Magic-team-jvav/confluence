package org.confluence.mod.common.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.confluence.mod.Confluence;
import org.mesdag.portlib.diff.Diff;

public class ShimmerTransmutationTrigger extends SimpleCriterionTrigger<ShimmerTransmutationTrigger.TriggerInstance> {
    @Diff
    public static final ResourceLocation ID = Confluence.asResource("shimmer_transmutation");
    @Diff
    public static final ShimmerTransmutationTrigger INSTANCE = new ShimmerTransmutationTrigger();

    private ShimmerTransmutationTrigger() {}

    public void trigger(ServerPlayer player, Entity entity) {
        trigger(player, instance -> instance.matches(player, entity));
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
