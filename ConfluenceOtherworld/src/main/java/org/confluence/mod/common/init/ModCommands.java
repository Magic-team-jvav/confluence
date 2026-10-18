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
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.server.command.EnumArgument;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.worldgen.biome.DynamicBiomeUtils;
import org.confluence.lib.common.worldgen.biome.MiniBiome;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.attachment.ChunkBrushData;
import org.confluence.mod.common.attachment.ManaStorage;
import org.confluence.mod.common.component.prefix.ModPrefix;
import org.confluence.mod.common.component.prefix.PrefixComponent;
import org.confluence.mod.common.component.prefix.PrefixType;
import org.confluence.mod.common.data.BrushData;
import org.confluence.mod.common.data.GamePhase;
import org.confluence.mod.common.data.StarPhase;
import org.confluence.mod.common.data.command.GameEventArgument;
import org.confluence.mod.common.data.command.PrefixArgument;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.data.saved.KillBoard;
import org.confluence.mod.common.data.saved.MeteoriteTracker;
import org.confluence.mod.common.gameevent.GameEvent;
import org.confluence.mod.common.gameevent.GameEventSystem;
import org.confluence.mod.common.init.item.PaintItems;
import org.confluence.mod.common.wind.WindField;
import org.confluence.mod.common.wind.WindMath;
import org.confluence.mod.common.wind.WindState;
import org.confluence.mod.network.s2c.BrushingColorPacketS2C;
import org.confluence.mod.util.OverworldUtils;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.mod.common.soulcerer.skill.SoulSkillParameters;
import org.confluence.mod.util.PrefixUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.mesdag.portlib.network.PortPacketDistributor;

import java.util.*;

import static org.confluence.mod.common.data.saved.ConfluenceData.STAR_PHASES_SIZE;

public final class ModCommands {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPE_INFOS = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Confluence.MODID);

    static {
        ARGUMENT_TYPE_INFOS.register("prefix", () -> ArgumentTypeInfos.registerByClass(PrefixArgument.class, new PrefixArgument.Info()));
        ARGUMENT_TYPE_INFOS.register("game_event", () -> ArgumentTypeInfos.registerByClass(GameEventArgument.class, SingletonArgumentInfo.contextFree(GameEventArgument::new)));
    }

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
                .then(Commands.literal("wind")
                        .then(Commands.literal("get").executes(ModCommands::windGet))
                        .then(Commands.literal("set")
                                .then(Commands.argument("x", FloatArgumentType.floatArg(-MAX_WIND_SPEED, MAX_WIND_SPEED))
                                        .then(Commands.argument("z", FloatArgumentType.floatArg(-MAX_WIND_SPEED, MAX_WIND_SPEED))
                                                .executes(context -> windSet(context, Integer.MAX_VALUE))
                                                .then(Commands.argument("durationTicks", IntegerArgumentType.integer(1))
                                                        .executes(context -> windSet(context, IntegerArgumentType.getInteger(context, "durationTicks")))))))
                        .then(Commands.literal("stop").executes(context -> {
                            WindState.INSTANCE.clearOverride();
                            context.getSource().sendSuccess(() -> Component.literal("Wind override cleared"), true);
                            return 1;
                        }))
                        .then(Commands.literal("debug").executes(ModCommands::windDebug)))
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
                                ItemStack stack = PaintItems.PAINT.toStack();
                                stack.setDyedColor(color);
                                player.getInventory().add(stack);
                                return 1;
                            }
                            return 0;
                        })))
                )
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
                    Holder<Biome> result = DynamicBiomeUtils.judgeSection(section, level.registryAccess().lookupOrThrow(Registries.BIOME));
                    context.getSource().sendSuccess(() -> {
                        if (result == null) {
                            return Component.literal(pos + " pure");
                        } else {
                            return Component.literal(pos + " " + result);
                        }
                    }, true);
                    return 0;
                }))
                .then(Commands.literal("judgeMiniBiome").executes(context -> {
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
                    BlockPos finalPos = pos;
                    List<MiniBiome.Marker> markers = MiniBiome.markersAt(level, pos);
                    context.getSource().sendSuccess(() -> {
                        if (markers.isEmpty()) return Component.literal(finalPos + " no mini biome");
                        StringBuilder builder = new StringBuilder(finalPos + " mini biomes:");
                        for (MiniBiome.Marker marker : markers) {
                            builder.append("\n  ").append(marker.id())
                                    .append(" count=").append(marker.count())
                                    .append(" threshold=").append(marker.threshold())
                                    .append(" priority=").append(marker.priority())
                                    .append(" influence=").append(String.format("%.2f", marker.influence()));
                        }
                        return Component.literal(builder.toString());
                    }, false);
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
                            PrefixComponent prefix = PrefixUtils.random(player.getRandom1211(), itemStack);
                            if (unknownPrefixType(source, prefix)) return 0;
                            source.sendSuccess(() -> Component.translatable("commands.confluence.reforge.success", ModRarity.withColor(itemStack, prefix.getName())), false);
                            return 1;
                        }))
                        .then(Commands.literal("best").executes(context -> {
                            CommandSourceStack source = context.getSource();
                            ServerPlayer player = source.getPlayer();
                            if (cannotBeReforged(source, player)) return 0;
                            ItemStack itemStack = player.getMainHandItem();
                            PrefixComponent prefix = PrefixUtils.best(player.getRandom1211(), itemStack);
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
                            LibUtils.resetDataComponent(itemStack, ConfluenceMagicLib.MOD_RARITY);
                            LibUtils.resetDataComponent(itemStack, ModDataComponentTypes.VALUE);
                            source.sendSuccess(() -> Component.translatable("commands.confluence.reforge.clear.success"), false);
                            return 1;
                        }))
                        .then(setPrefixBuilder())
                )
                .then(Commands.literal("gameEvent")
                        .then(Commands.literal("start").then(Commands.argument("key", new GameEventArgument()).executes(context -> {
                            GameEvent event = context.getArgument("key", GameEvent.class);
                            if (!event.forceStart()) return 0;
                            return 1;
                        })))
                        .then(Commands.literal("end").then(Commands.argument("key", new GameEventArgument()).executes(context -> {
                            context.getArgument("key", GameEvent.class).forceEnd();
                            return 1;
                        })))
                        .then(Commands.literal("query").executes(context -> {
                            CommandSourceStack source = context.getSource();
                            source.sendSystemMessage(Component.literal("GameEvents:"));
                            for (Map.Entry<ResourceKey<? extends GameEvent>, GameEvent> entry : GameEventSystem.INSTANCE.getEvents().entrySet()) {
                                GameEvent event = entry.getValue();
                                boolean started = event.started();
                                boolean isEnvironment = !event.isNonEnvEvent();
                                source.sendSystemMessage(Component.literal("  " + entry.getKey().location())
                                        .append(": started=").append(Component.literal(String.valueOf(started)).withStyle(started ? ChatFormatting.GREEN : ChatFormatting.RED))
                                        .append(", isEnvironment=").append(Component.literal(String.valueOf(isEnvironment)).withStyle(isEnvironment ? ChatFormatting.GREEN : ChatFormatting.RED))
                                );
                            }
                            return 1;
                        }))
                )
                .then(Commands.literal("stopAskForSoftcore").executes(context -> {
                    ServerLevel overworld = context.getSource().getServer().getLevel(OverworldUtils.dimension());
                    if (overworld == null) return 0;
                    ConfluenceData.get(overworld).setStopAskForSoftcore(true);
                    return 1;
                }))
                .then(Commands.literal("soulSkill")
                        .then(Commands.literal("reset").executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            PlayerUtils.resetSoulSkillEquip(player);
                            context.getSource().sendSuccess(() -> Component.literal("Soul skill equipment has been reset"), true);
                            return 1;
                        }))
                        .then(Commands.literal("slots")
                                .then(Commands.argument("count", IntegerArgumentType.integer(3, 6)).executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    int count = IntegerArgumentType.getInteger(context, "count");
                                    PlayerUtils.setSoulSkillSlotCount(player, count);
                                    context.getSource().sendSuccess(() -> Component.literal("Soul skill slots set to " + count), true);
                                    return 1;
                                })))
                        .then(Commands.literal("ritual")
                                .then(Commands.argument("count", IntegerArgumentType.integer(0, SoulSkillParameters.MAX_RITUAL_COUNT)).executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    int count = IntegerArgumentType.getInteger(context, "count");
                                    PlayerUtils.setSoulRitualCount(player, count);
                                    context.getSource().sendSuccess(() -> Component.literal("Soul ritual count set to " + count), true);
                                    return 1;
                                })))
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
                ItemStack stack = player.getMainHandItem();
                PrefixType type = PrefixUtils.getPrefixType(stack);
                if (!type.isGroupAvailable(group)) {
                    source.sendFailure(Component.translatable("commands.confluence.reforge.set.unavailable_group").withStyle(ChatFormatting.RED));
                    return 0;
                }
                PrefixComponent prefix = PrefixUtils.setAndUpdate(stack, type, context.getArgument("group", ModPrefix.class));
                if (unknownPrefixType(source, prefix)) return 0;
                source.sendSuccess(() -> Component.translatable("commands.confluence.reforge.success", ModRarity.withColor(stack, prefix.getName())), false);
                return 1;
            })));
        }
        return builder;
    }

    private static boolean cannotBeReforged(CommandSourceStack source, @Nullable ServerPlayer player) {
        if (player == null) return true;
        if (!PrefixUtils.couldReforge(player.getMainHandItem())) {
            source.sendFailure(Component.translatable("commands.confluence.reforge.cannot_be_reforged").withStyle(ChatFormatting.RED));
            return true;
        }
        return false;
    }

    @Contract("_, null -> true")
    private static boolean unknownPrefixType(CommandSourceStack source, @Nullable PrefixComponent prefix) {
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
        Map<ChunkPos, BrushData> dataMap = ChunkBrushData.of(level).getDataMap();
        for (Map.Entry<ChunkPos, Map<BlockPos, int[]>> entry : chunkPosMap.entrySet()) {
            BrushData brushData = new BrushData(entry.getValue());
            ChunkPos chunkPos = entry.getKey();
            dataMap.computeIfAbsent(chunkPos, pos -> new BrushData(new Hashtable<>())).merge(brushData);
            PortPacketDistributor.sendToPlayersTrackingChunk(level, chunkPos, new BrushingColorPacketS2C(chunkPos, brushData));
        }
        return 1;
    }

    // ===================== 风 =====================

    /// `wind set` 的风速分量上限（m/s）。与 {@link WindState#MAX_FORCED_SPEED} 一致。
    private static final float MAX_WIND_SPEED = WindState.MAX_FORCED_SPEED;

    private static int windGet(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        WindState state = WindState.INSTANCE;
        Component message = Component.literal(
                "Wind: x=%.2f, z=%.2f (%.2f m/s, %.1f mph), from the %s, %s, gust=%.2f%s".formatted(
                        state.dirX() * state.speed(),
                        state.dirZ() * state.speed(),
                        state.speed(),
                        WindMath.toMph(state.speed()),
                        Component.translatable(WindMath.compassKey(state.angle())).getString(),
                        Component.translatable("info.confluence.wind.tier." + state.tier().name().toLowerCase(Locale.ROOT)).getString(),
                        WindMath.gust(level.getGameTime(), state.gustPhase()),
                        state.isOverridden() ? " [overridden]" : ""));
        context.getSource().sendSuccess(() -> message, false);
        return 1;
    }

    /// `/confluence wind set <x> <z> [durationTicks]` —— x / z 是风速在 **+X（东）/ +Z（南）** 上的分量，单位 m/s。
    ///
    /// 用分量而不是"大小 + 角度"：这里本来就是二维风向场，分量对负值和斜风都更自然
    /// （负值天然合法，不必再约定"负风速 = 反向"）。`set 0 0` 就是强制静风。
    ///
    /// @param ticks 强制风持续的 **tick** 数（20 tick = 1 秒）。传 {@link Integer#MAX_VALUE} 表示本局一直有效，
    ///              由 `/confluence wind stop` 解除。参数名带 `Ticks` 是为了让 Brigadier 的用法提示自己说明单位。
    private static int windSet(CommandContext<CommandSourceStack> context, int ticks) {
        float x = FloatArgumentType.getFloat(context, "x");
        float z = FloatArgumentType.getFloat(context, "z");
        float speed = Mth.sqrt(x * x + z * z);
        // 方向由分量比例给出；WindState.override 只夹取大小上限，所以方向不会被夹偏。
        // (0, 0) 视为静风，此时方向无意义。
        float angle = speed < 1.0E-4F ? 0.0F : (float) Mth.atan2(z, x);
        WindState.INSTANCE.override(speed, angle, ticks);

        String duration = ticks == Integer.MAX_VALUE
                ? "until /confluence wind stop"
                : "%d ticks (%.1f s)".formatted(ticks, ticks / 20.0F);
        // 这里刻意不给方位名：`<x> <z>` 是"吹向"的分量，而气象台显示的是"来向"（相差 180°），
        // 两个方向词并排出现只会让人怀疑哪个是哪个。分量本身就是方向。
        context.getSource().sendSuccess(() -> Component.literal(
                "Wind set to (x=%.2f, z=%.2f) = %.2f m/s, holding for %s".formatted(x, z, speed, duration)), true);
        return 1;
    }

    /// 打印该命令来源所在位置的风向场细节，用于核对温度噪声造成的方向偏差。
    private static int windDebug(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        WindState state = WindState.INSTANCE;
        Vec3 pos = context.getSource().getPosition();
        Vector2f dir = WindField.direction(level, state, pos.x, pos.z);
        BlockPos blockPos = BlockPos.containing(pos);
        context.getSource().sendSuccess(() -> Component.literal(
                "speed=%.2f m/s instant=%.2f tier=%s blend=%.2f\n".formatted(
                        state.speed(), state.instantSpeed(level.getGameTime()), state.tier(), state.directionBlend())
                        + "globalAngle=%.1f deg localAngle=%.1f deg (delta=%.1f deg) cell=(%d, %d)\n".formatted(
                        state.angle() * Mth.RAD_TO_DEG,
                        (float) Mth.atan2(dir.y, dir.x) * Mth.RAD_TO_DEG,
                        ((float) Mth.atan2(dir.y, dir.x) - state.angle()) * Mth.RAD_TO_DEG,
                        Mth.floor(pos.x / WindField.CELL), Mth.floor(pos.z / WindField.CELL))
                        + "exposure=%.2f windyDay=%s".formatted(
                        WindMath.exposure(level, blockPos), state.isWindyDay())), false);
        return 1;
    }
}
