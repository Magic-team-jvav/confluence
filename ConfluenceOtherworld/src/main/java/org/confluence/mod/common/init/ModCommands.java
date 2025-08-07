package org.confluence.mod.common.init;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.server.command.EnumArgument;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.component.prefix.ModPrefix;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.data.PrefixArgument;
import org.confluence.mod.common.data.saved.*;
import org.confluence.mod.common.init.item.PaintItems;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;
import org.confluence.mod.util.DynamicBiomeUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.util.PrefixUtils;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static org.confluence.mod.common.data.saved.ConfluenceData.STAR_PHASES_SIZE;

@SuppressWarnings("unused")
public final class ModCommands {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPE_INFOS = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Confluence.MODID);

    public static final Holder<ArgumentTypeInfo<?, ?>> PREFIX_ARGUMENT_TYPE = ARGUMENT_TYPE_INFOS.register("prefix", () -> ArgumentTypeInfos.registerByClass(PrefixArgument.class, new PrefixArgument.Info()));

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
                            context.getSource().sendSuccess(() -> Component.literal("Has been set to " + data.getStarPhase(index)), true);
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
                .then(Commands.literal("reload").then(Commands.argument("resource", EnumArgument.enumArgument(ReloadResource.class)).executes(context -> {
                    ReloadResource.execute(context.getArgument("resource", ReloadResource.class));
                    return 1;
                })))
                .then(Commands.literal("judgeBiome").executes(context -> {
                    Level level;
                    BlockPos pos;
                    if (context.getSource().source instanceof Entity entity) {
                        level = entity.level();
                        pos = entity.blockPosition();
                    } else if (context.getSource().source instanceof BaseCommandBlock commandBlock) {
                        level = commandBlock.getLevel();
                        Vec3 position = commandBlock.getPosition();
                        pos = new BlockPos((int) position.x, (int) position.y, (int) position.z);
                    } else {
                        return 1;
                    }
                    LevelChunk chunk = level.getChunkAt(pos);
                    LevelChunkSection section = chunk.getSection(chunk.getSectionIndex(pos.getY()));
                    Holder<Biome> result = DynamicBiomeUtils.judgeSection(section);
                    context.getSource().sendSuccess(() -> {
                        if (result == null) {
                            return Component.literal(pos + " pure");
                        } else {
                            return Component.literal(pos + " " + result);
                        }
                    }, true);
                    return 0;
                }))
                .then(Commands.literal("mana")
                        .then(Commands.literal("receive").then(Commands.argument("value", FloatArgumentType.floatArg(0.0F, Float.MAX_VALUE)).executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player == null) return 0;
                            boolean value = ManaStorage.of(player).receiveMana(() -> FloatArgumentType.getFloat(context, "value"));
                            PlayerUtils.syncMana2Client(player);
                            return value ? 1 : 0;
                        })))
                        .then(Commands.literal("extract").then(Commands.argument("value", FloatArgumentType.floatArg(0.0F, Float.MAX_VALUE)).executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player == null) return 0;
                            boolean value = ManaStorage.of(player).extractMana(() -> FloatArgumentType.getFloat(context, "value"), player);
                            PlayerUtils.syncMana2Client(player);
                            return value ? 1 : 0;
                        })))
                        .then(Commands.literal("clearStars").executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            if (player == null) return 0;
                            ManaStorage.of(player).clearStars();
                            PlayerUtils.syncMana2Client(player);
                            return 1;
                        }))
                )
                .then(Commands.literal("reforge")
                        .then(Commands.literal("random").executes(context -> {
                            CommandSourceStack source = context.getSource();
                            ServerPlayer player = source.getPlayer();
                            if (cannotBeReforged(source, player)) return 0;
                            ItemStack itemStack = player.getMainHandItem();
                            PrefixComponent prefix = PrefixUtils.random(player.getRandom(), itemStack);
                            if (unknownPrefixType(source, prefix)) return 0;
                            source.sendSuccess(() -> Component.translatable("commands.confluence.reforge.success", ModRarity.withColor(itemStack, prefix.getName())), false);
                            return 1;
                        }))
                        .then(Commands.literal("best").executes(context -> {
                            CommandSourceStack source = context.getSource();
                            ServerPlayer player = source.getPlayer();
                            if (cannotBeReforged(source, player)) return 0;
                            ItemStack itemStack = player.getMainHandItem();
                            PrefixComponent prefix = PrefixUtils.best(player.getRandom(), itemStack);
                            if (unknownPrefixType(source, prefix)) return 0;
                            source.sendSuccess(() -> Component.translatable("commands.confluence.reforge.success", ModRarity.withColor(itemStack, prefix.getName())), false);
                            return 1;
                        }))
                        .then(Commands.literal("clear").executes(context -> {
                            CommandSourceStack source = context.getSource();
                            ServerPlayer player = source.getPlayer();
                            if (cannotBeReforged(source, player)) return 0;
                            ItemStack itemStack = player.getMainHandItem();
                            PrefixUtils.unknown(itemStack);
                            LibUtils.resetDataComponent(itemStack, ConfluenceMagicLib.MOD_RARITY.get());
                            LibUtils.resetDataComponent(itemStack, ModDataComponentTypes.VALUE.get());
                            source.sendSuccess(() -> Component.translatable("commands.confluence.reforge.clear.success"), false);
                            return 1;
                        }))
                        .then(setPrefixBuilder())
                )
        );
    }

    private static LiteralArgumentBuilder<CommandSourceStack> setPrefixBuilder() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("set");
        for (String group : ModPrefix.GROUPS.keySet()) {
            builder.then(Commands.literal(group).then(Commands.argument("group", new PrefixArgument(group)).executes(context -> {
                CommandSourceStack source = context.getSource();
                ServerPlayer player = source.getPlayer();
                if (cannotBeReforged(source, player)) return 0;
                ItemStack itemStack = player.getMainHandItem();
                PrefixType type = PrefixUtils.getPrefixType(itemStack);
                if (!type.isGroupAvailable(group)) {
                    source.sendFailure(Component.translatable("commands.confluence.reforge.set.unavailable_group").withStyle(ChatFormatting.RED));
                    return 0;
                }
                PrefixComponent prefix = PrefixUtils.setAndUpdate(itemStack, type, context.getArgument("group", ModPrefix.class));
                if (unknownPrefixType(source, prefix)) return 0;
                source.sendSuccess(() -> Component.translatable("commands.confluence.reforge.success", ModRarity.withColor(itemStack, prefix.getName())), false);
                return 1;
            })));
        }
        return builder;
    }

    private static boolean cannotBeReforged(CommandSourceStack source, ServerPlayer player) {
        if (player == null) return true;
        if (!PrefixUtils.couldReforge(player.getMainHandItem())) {
            source.sendFailure(Component.translatable("commands.confluence.reforge.cannot_be_reforged").withStyle(ChatFormatting.RED));
            return true;
        }
        return false;
    }

    @Contract("_, null -> true")
    private static boolean unknownPrefixType(CommandSourceStack source, PrefixComponent prefix) {
        if (prefix == null) {
            source.sendFailure(Component.translatable("commands.confluence.reforge.unknown_prefix_type").withStyle(ChatFormatting.RED));
            return true;
        }
        return false;
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
