package org.confluence.mod.common.data.saved;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.server.command.EnumArgument;

import static org.confluence.mod.common.data.saved.ConfluenceData.STAR_PHASES_SIZE;

public class ConfluenceCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("confluence").requires(sourceStack -> sourceStack.hasPermission(2))
                .then(Commands.literal("starPhases")
                        .then(Commands.literal("get").then(Commands.argument("index", IntegerArgumentType.integer(0, STAR_PHASES_SIZE - 1)).executes(context -> {
                            int index = IntegerArgumentType.getInteger(context, "index");
                            StarPhase starPhase = ConfluenceData.get(context.getSource().getLevel()).getStarPhase(index);
                            if (starPhase == null) {
                                context.getSource().sendFailure(Component.literal("No such StarPhase!"));
                                return 0;
                            }
                            context.getSource().sendSystemMessage(Component.literal(starPhase.toString()));
                            return 1;
                        })))
                        .then(Commands.literal("set").then(Commands.argument("index", IntegerArgumentType.integer(0, STAR_PHASES_SIZE - 1)).then(Commands.argument("timeOffset", IntegerArgumentType.integer()).then(Commands.argument("radius", FloatArgumentType.floatArg()).then(Commands.argument("angle", FloatArgumentType.floatArg()).executes(context -> {
                            int index = IntegerArgumentType.getInteger(context, "index");
                            int timeOffset = IntegerArgumentType.getInteger(context, "timeOffset");
                            float radius = FloatArgumentType.getFloat(context, "radius");
                            float angle = FloatArgumentType.getFloat(context, "angle");
                            ConfluenceData data = ConfluenceData.get(context.getSource().getLevel());
                            if (!data.setStarPhase(index, timeOffset, radius, angle)) {
                                context.getSource().sendFailure(Component.literal("Can not set StarPhase!"));
                                return 0;
                            }
                            context.getSource().sendSuccess(() -> Component.literal("Has been set to " + data.getStarPhase(index).toString()), true);
                            return 1;
                        }))))))
                )
                .then(Commands.literal("gamePhase")
                        .then(Commands.literal("get").executes(context -> {
                            String gamePhase = ConfluenceData.get(context.getSource().getLevel()).getGamePhase().getSerializedName();
                            context.getSource().sendSystemMessage(Component.literal("GamePhase: " + gamePhase));
                            return 1;
                        }))
                        .then(Commands.literal("set").then(Commands.argument("value", EnumArgument.enumArgument(GamePhase.class)).executes(context -> {
                            GamePhase gamePhase = context.getArgument("value", GamePhase.class);
                            ConfluenceData.get(context.getSource().getLevel()).setGamePhase(gamePhase);
                            context.getSource().sendSuccess(() -> Component.literal("Has been set to " + gamePhase.getSerializedName()), true);
                            return 1;
                        })))
                )
                .then(Commands.literal("windSpeed")
                        .then(Commands.literal("get").executes(context -> {
                            ConfluenceData data = ConfluenceData.get(context.getSource().getLevel());
                            context.getSource().sendSystemMessage(Component.literal("Wind speed: [" + data.getWindSpeedX() + ", " + data.getWindSpeedZ() + "]"));
                            return 1;
                        }))
                        .then(Commands.literal("set").then(Commands.argument("x", FloatArgumentType.floatArg()).then(Commands.argument("z", FloatArgumentType.floatArg()).executes(context -> {
                            float x = FloatArgumentType.getFloat(context, "x");
                            float z = FloatArgumentType.getFloat(context, "z");
                            ConfluenceData.get(context.getSource().getLevel()).setWindSpeed(x, z);
                            context.getSource().sendSystemMessage(Component.literal("Has been set to [" + x + ", " + z + "]"));
                            return 1;
                        }))))
                )
                .then(Commands.literal("meteorite").then(Commands.argument("location", BlockPosArgument.blockPos()).then(Commands.argument("tickUntilLanding", IntegerArgumentType.integer(0)).executes(context -> {
                    BlockPos location = BlockPosArgument.getBlockPos(context, "location");
                    int tickUntilLanding = IntegerArgumentType.getInteger(context, "tickUntilLanding");
                    ConfluenceData.get(context.getSource().getLevel()).setMeteorite(location, tickUntilLanding);
                    return 1;
                }))))
        );
    }
}
