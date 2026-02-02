package org.confluence.terra_curio.common.attachment;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.api.primitive.PrimitiveValue;
import org.confluence.terra_curio.api.primitive.ValueType;
import org.confluence.terra_curio.util.TCUtils;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AccessoriesValueCommand {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> INFOS = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, TerraCurio.MODID);

    static {
        INFOS.register("value_type", () -> ArgumentTypeInfos.registerByClass(ValueTypeArgument.class, SingletonArgumentInfo.contextFree(ValueTypeArgument::type)));
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("terra_curio").requires(sourceStack -> sourceStack.hasPermission(2))
                .then(Commands.argument("type", ValueTypeArgument.type()).executes(context -> {
                    ResourceLocation id = ValueTypeArgument.getId(context, "type");
                    ValueType<?, ? extends PrimitiveValue<?>> type = ValueType.TYPES.get(id);
                    CommandSourceStack source = context.getSource();
                    if (type == null) {
                        source.sendFailure(Component.translatable("argument.terra_curio.unknown_type", id));
                        return 0;
                    } else if (source.getEntity() instanceof LivingEntity living) {
                        PrimitiveValue<?> value = TCUtils.getPrimitiveValue(living, type);
                        if (type.defaultValue() == Unit.INSTANCE) {
                            source.sendSystemMessage(Component.literal("Contains: " + (value != null)));
                        } else if (value == null) {
                            source.sendSystemMessage(Component.literal("NONE"));
                        } else {
                            for (String description : value.getDescription()) {
                                source.sendSystemMessage(Component.literal(description));
                            }
                        }
                        return 1;
                    }
                    return 0;
                }))
        );
    }

    public static class ValueTypeArgument implements ArgumentType<ResourceLocation> {
        private static final DynamicCommandExceptionType UNKNOWN_TYPE = new DynamicCommandExceptionType(type -> Component.translatable("argument.terra_curio.unknown_type", type));
        private static final List<String> EXAMPLES = List.of("terra_curio:auto_attack");
        private static List<String> AVAILABLE;

        @Override
        public ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
            try {
                ResourceLocation id = ResourceLocation.read(reader);
                if (ValueType.TYPES.containsKey(id)) {
                    return id;
                } else {
                    throw UNKNOWN_TYPE.create(id);
                }
            } catch (Exception e) {
                throw UNKNOWN_TYPE.create(e);
            }
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            if (AVAILABLE == null) {
                AVAILABLE = ValueType.TYPES.keySet().stream().map(ResourceLocation::toString).sorted().toList();
            }
            return SharedSuggestionProvider.suggest(AVAILABLE, builder);
        }

        @Override
        public Collection<String> getExamples() {
            return EXAMPLES;
        }

        public static ValueTypeArgument type() {
            return new ValueTypeArgument();
        }

        public static ResourceLocation getId(CommandContext<CommandSourceStack> context, String name) {
            return context.getArgument(name, ResourceLocation.class);
        }
    }
}
