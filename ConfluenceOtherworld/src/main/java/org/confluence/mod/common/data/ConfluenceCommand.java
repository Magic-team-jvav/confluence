package org.confluence.mod.common.data;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.command.EnumArgument;
import org.confluence.mod.common.data.saved.*;
import org.confluence.mod.common.init.ModAttachmentTypes;
import org.confluence.mod.common.init.item.PaintItems;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

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
                            String gamePhase = KillBoard.INSTANCE.getGamePhase().getSerializedName();
                            context.getSource().sendSystemMessage(Component.literal("GamePhase: " + gamePhase));
                            return 1;
                        }))
                        .then(Commands.literal("set").then(Commands.argument("value", EnumArgument.enumArgument(GamePhase.class)).executes(context -> {
                            GamePhase gamePhase = context.getArgument("value", GamePhase.class);
                            ServerLevel serverLevel = context.getSource().getLevel();
                            KillBoard.INSTANCE.setGamePhase(serverLevel.getServer(), gamePhase);
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
                .then(Commands.literal("meteorite")
                        .then(Commands.literal("random").then(Commands.argument("tickUntilLanding", IntegerArgumentType.integer(0)).executes(context -> {
                            int tickUntilLanding = IntegerArgumentType.getInteger(context, "tickUntilLanding");
                            MeteoriteTracker.INSTANCE.generateLandingDetail(context.getSource().getLevel(), tickUntilLanding);
                            return 1;
                        })))
                        .then(Commands.argument("location", BlockPosArgument.blockPos()).then(Commands.argument("tickUntilLanding", IntegerArgumentType.integer(0)).executes(context -> {
                            BlockPos location = BlockPosArgument.getBlockPos(context, "location");
                            int tickUntilLanding = IntegerArgumentType.getInteger(context, "tickUntilLanding");
                            ConfluenceData.get(context.getSource().getLevel()).setMeteorite(location, tickUntilLanding);
                            return 1;
                        })))
                )
                .then(Commands.literal("paint")
                        .then(Commands.argument("start", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos())
                                .then(Commands.literal("brush")
                                        .then(Commands.argument("face", EnumArgument.enumArgument(Direction.class)).then(Commands.argument("color", IntegerArgumentType.integer()).executes(context -> {
                                            Direction face = context.getArgument("face", Direction.class);
                                            int color = IntegerArgumentType.getInteger(context, "color");
                                            int[] list = BrushData.createColor(BrushData.EMPTY_COLOR);
                                            list[face.get3DDataValue()] = color;
                                            return fillPaints(context, list);
                                        })))
                                        .then(Commands.argument("color", IntegerArgumentType.integer()).executes(context -> {
                                            int color = IntegerArgumentType.getInteger(context, "color");
                                            int[] list = BrushData.createColor(color);
                                            return fillPaints(context, list);
                                        }))
                                )
                                .then(Commands.literal("scraper")
                                        .then(Commands.argument("face", EnumArgument.enumArgument(Direction.class)).executes(context -> {
                                            Direction face = context.getArgument("face", Direction.class);
                                            int[] list = BrushData.createColor(BrushData.EMPTY_COLOR);
                                            list[face.get3DDataValue()] = BrushData.CLEAR_COLOR;
                                            return fillPaints(context, list);
                                        }))
                                        .executes(context -> fillPaints(context, BrushingColorPacketS2C.CLEAR_COLORS))
                                )
                        ))
                        .then(Commands.literal("item").then(Commands.argument("color", IntegerArgumentType.integer()).executes(context -> {
                            if (context.getSource().getEntityOrException() instanceof Player player) {
                                int color = IntegerArgumentType.getInteger(context, "color");
                                ItemStack itemStack = PaintItems.PAINT.get().getDefaultInstance();
                                itemStack.set(DataComponents.DYED_COLOR, new DyedItemColor(color, true));
                                player.getInventory().add(itemStack);
                                return 1;
                            }
                            return 0;
                        })))
                )
                .then(Commands.literal("reload")
                        .then(Commands.argument("resource", EnumArgument.enumArgument(ReloadResource.class)).executes(context -> {

                            ReloadResource.execute(context.getArgument("resource", ReloadResource.class));
                            return 1;
                        }))
                )
        );
    }

    private static int fillPaints(CommandContext<CommandSourceStack> context, int[] list) throws CommandSyntaxException {
        BlockPos start = BlockPosArgument.getLoadedBlockPos(context, "start");
        BlockPos end = BlockPosArgument.getLoadedBlockPos(context, "end");
        Map<ChunkPos, Map<BlockPos, int[]>> chunkPosMap = new HashMap<>();
        for (BlockPos blockPos : BlockPos.betweenClosed(start, end)) {
            chunkPosMap.computeIfAbsent(new ChunkPos(blockPos), pos -> new HashMap<>()).put(blockPos.immutable(), list);
        }
        ServerLevel level = context.getSource().getLevel();
        Map<ChunkPos, BrushData> dataMap = level.getData(ModAttachmentTypes.CHUNK_BRUSH_DATA).getDataMap();
        for (Map.Entry<ChunkPos, Map<BlockPos, int[]>> entry : chunkPosMap.entrySet()) {
            BrushData brushData = new BrushData(entry.getValue());
            ChunkPos chunkPos = entry.getKey();
            dataMap.computeIfAbsent(chunkPos, pos -> new BrushData(new Hashtable<>())).merge(brushData);
            PacketDistributor.sendToPlayersTrackingChunk(level, chunkPos, new BrushingColorPacketS2C(chunkPos, brushData));
        }
        return 1;
    }
}
