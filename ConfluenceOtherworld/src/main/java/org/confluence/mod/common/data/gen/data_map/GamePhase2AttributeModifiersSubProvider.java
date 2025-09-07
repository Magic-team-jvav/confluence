package org.confluence.mod.common.data.gen.data_map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.common.extensions.IHolderExtension;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.gen.ModDataMapProvider;
import org.confluence.mod.common.data.map.GamePhase2AttributeModifiers;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.terra_curio.api.primitive.AttributeModifiersValue;
import org.confluence.terraentity.init.entity.TEMonsterEntities;

import java.util.Map;
import java.util.Objects;

public final class GamePhase2AttributeModifiersSubProvider {
    private static final ResourceLocation id = Confluence.asResource("game_phase_modifier");

    public static void gather(ModDataMapProvider.Appender<Builder> appender) {
        appender.create()
                // 初始数值基础，目标阶段数值除初始数值-1 = 填参值
                .add(EntityType.ZOMBIE.builtInRegistryHolder(), Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.ATTACK_DAMAGE, id, 1.7, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 2.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.ATTACK_DAMAGE, id, 2.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.MAX_HEALTH, id, 3.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ))
                .add(TEMonsterEntities.BLOOD_ZOMBIE, Map.of(
                        GamePhase.WALL_OF_FLESH, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 0.09, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ATTACK_DAMAGE, id, -0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build(),
                        GamePhase.PLANTERA, AttributeModifiersValue.builder()
                                .add(Attributes.MAX_HEALTH, id, 1.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ATTACK_DAMAGE, id, 0.8, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .add(Attributes.ARMOR, id, 1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                                .build()
                ));
    }

    public static class Builder extends DataMapProvider.Builder<GamePhase2AttributeModifiers, EntityType<?>> {
        public Builder() {
            super(ModDataMaps.GAME_PHASE_2_ATTRIBUTE_MODIFIERS);
        }

        public final Builder add(IHolderExtension<EntityType<?>> holder, Map<GamePhase, AttributeModifiersValue> map) {
            super.add(Objects.requireNonNull(holder.getKey()), new GamePhase2AttributeModifiers(map), false);
            return this;
        }

        public final Builder add(TagKey<EntityType<?>> tagKey, Map<GamePhase, AttributeModifiersValue> map) {
            super.add(tagKey, new GamePhase2AttributeModifiers(map), false);
            return this;
        }
    }
}
