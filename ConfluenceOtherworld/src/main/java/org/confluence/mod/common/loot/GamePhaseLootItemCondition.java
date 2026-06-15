package org.confluence.mod.common.loot;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.init.ModLootTables;

import java.util.Objects;

public record GamePhaseLootItemCondition(
        GamePhase from,
        boolean fromInclusive,
        GamePhase to,
        boolean toInclusive
) implements LootItemCondition {
    public static final MapCodec<GamePhaseLootItemCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            GamePhase.CODEC.fieldOf("from").forGetter(GamePhaseLootItemCondition::from),
            PortCodecExtension.lenientOptionalFieldOf(Codec.BOOL, "from_inclusive", true).forGetter(GamePhaseLootItemCondition::fromInclusive),
            GamePhase.CODEC.fieldOf("to").forGetter(GamePhaseLootItemCondition::to),
            PortCodecExtension.lenientOptionalFieldOf(Codec.BOOL, "to_inclusive", true).forGetter(GamePhaseLootItemCondition::toInclusive)
    ).apply(instance, GamePhaseLootItemCondition::new));

    @Override
    public LootItemConditionType getType() {
        return ModLootTables.ItemConditions.GAME_PHASE.get();
    }

    @Override
    public boolean test(LootContext context) {
        GamePhase gamePhase = KillBoard.INSTANCE.getGamePhase();
        if (fromInclusive ? gamePhase.isAtLeast(from) : gamePhase.isAboveThan(from)) {
            return !(toInclusive ? gamePhase.isAboveThan(to) : gamePhase.isAtLeast(to));
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || ((o instanceof GamePhaseLootItemCondition c) &&
                from == c.from && fromInclusive == c.fromInclusive && to == c.to && toInclusive == c.toInclusive);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(from);
        result = 31 * result + Boolean.hashCode(fromInclusive);
        result = 31 * result + Objects.hashCode(to);
        result = 31 * result + Boolean.hashCode(toInclusive);
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements LootItemCondition.Builder {
        private GamePhase from = GamePhase.BEFORE_SKELETRON;
        private boolean fromInclusive = true;
        private GamePhase to = GamePhase.MOON_LORD;
        private boolean toInclusive = true;

        public Builder from(GamePhase from) {
            this.from = from;
            return this;
        }

        public Builder from(GamePhase from, boolean inclusive) {
            this.from = from;
            this.fromInclusive = inclusive;
            return this;
        }

        public Builder to(GamePhase to) {
            this.to = to;
            return this;
        }

        public Builder to(GamePhase to, boolean inclusive) {
            this.to = to;
            this.toInclusive = inclusive;
            return this;
        }

        @Override
        public GamePhaseLootItemCondition build() {
            return new GamePhaseLootItemCondition(from, fromInclusive, to, toInclusive);
        }
    }
}
