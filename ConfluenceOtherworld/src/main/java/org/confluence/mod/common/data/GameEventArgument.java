package org.confluence.mod.common.data;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.confluence.mod.common.gameevent.GameEvent;
import org.confluence.mod.common.gameevent.GameEventSystem;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class GameEventArgument implements ArgumentType<GameEvent> {
    private static final SimpleCommandExceptionType UNKNOWN_KEY = new SimpleCommandExceptionType(Component.translatable("commands.confluence.arguments.game_event.unknown"));

    @Override
    public GameEvent parse(StringReader reader) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.read(reader);
        GameEvent event = GameEventSystem.INSTANCE.getEventInstance(GameEvent.createKey(id));
        if (event == null) {
            throw UNKNOWN_KEY.createWithContext(reader);
        }
        return event;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(stream(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return stream().toList();
    }

    private Stream<String> stream() {
        return GameEventSystem.INSTANCE.getEvents().keySet().stream().map(ResourceKey::location).map(ResourceLocation::toString);
    }
}
