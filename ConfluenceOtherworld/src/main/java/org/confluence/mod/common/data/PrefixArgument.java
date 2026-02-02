package org.confluence.mod.common.data;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.server.command.CommandUtils;
import org.confluence.mod.common.component.prefix.ModPrefix;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public record PrefixArgument(String group) implements ArgumentType<ModPrefix> {
    private static final Dynamic2CommandExceptionType INVALID_PREFIX = new Dynamic2CommandExceptionType((found, constants) -> CommandUtils.makeTranslatableWithFallback("commands.confluence.arguments.prefix.invalid", constants, found));

    @Override
    public ModPrefix parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readUnquotedString();
        Map<String, ? extends ModPrefix> map = ModPrefix.GROUPS.get(group);
        if (map != null) {
            ModPrefix prefix = map.get(name);
            if (prefix != null) {
                return prefix;
            }
        }
        throw INVALID_PREFIX.createWithContext(reader, name, stream().toArray());
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
        return ModPrefix.GROUPS.getOrDefault(group, Map.of()).keySet().stream();
    }

    public static class Info implements ArgumentTypeInfo<PrefixArgument, Info.Template> {
        @Override
        public void serializeToNetwork(Template template, FriendlyByteBuf buffer) {
            buffer.writeUtf(template.group);
        }

        @Override
        public Template deserializeFromNetwork(FriendlyByteBuf buffer) {
            return new Template(buffer.readUtf());
        }

        @Override
        public void serializeToJson(Template template, JsonObject json) {
            json.addProperty("group", template.group);
        }

        @Override
        public Template unpack(PrefixArgument argument) {
            return new Template(argument.group);
        }

        public class Template implements ArgumentTypeInfo.Template<PrefixArgument> {
            private final String group;

            Template(String group) {
                this.group = group;
            }

            @Override
            public PrefixArgument instantiate(CommandBuildContext context) {
                return new PrefixArgument(group);
            }

            @Override
            public ArgumentTypeInfo<PrefixArgument, ?> type() {
                return Info.this;
            }
        }
    }
}
