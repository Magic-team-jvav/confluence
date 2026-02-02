package org.confluence.terra_curio.client.handler;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;
import org.confluence.lib.client.DPSMeter;
import org.confluence.lib.util.LibUtils;
import org.confluence.terra_curio.client.TCKeyBindings;
import org.confluence.terra_curio.common.init.TCCommonConfigs;
import org.confluence.terra_curio.network.s2c.InfoCurioCheckPacketS2C;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.LongFunction;

public final class InformationHandler {
    public static final int WATCH = 0;
    public static final int WEATHER_RADIO = 1;
    public static final int SEXTANT = 2;
    public static final int FISHERMANS_POCKET_GUIDE = 3;
    public static final int METAL_DETECTOR = 4;
    public static final int LIFE_FORM_ANALYZER = 5;
    public static final int RADAR = 6;
    public static final int TALLY_COUNTER = 7;
    public static final int DPS_METER = 8;
    public static final int STOPWATCH = 9;
    public static final int COMPASS = 10;
    public static final int DEPTH_METER = 11;
    public static final int MECHANICAL_LENS = 12;

    public static final boolean[] DISABLE = new boolean[InfoCurioCheckPacketS2C.ARRAY_LENGTH];

    private static Int2ObjectMap<Component> INFORMATION = new Int2ObjectArrayMap<>();
    private static final byte[] INFO_DATA = new byte[InfoCurioCheckPacketS2C.ARRAY_LENGTH];
    private static final Int2ObjectOpenHashMap<byte[]> REMOTE_DATA = new Int2ObjectOpenHashMap<>();

    private static @Nullable LongFunction<Component> timeInfo = null;
    private static Component weatherRadioInfo = Component.translatable("info.terra_curio.weather_radio.clear", "0.00");
    private static boolean detectorPressed = false;
    private static Component metalDetectorInfo = Component.translatable("info.terra_curio.metal_detector.none");
    private static Component lifeFormAnalyzerInfo = Component.translatable("info.terra_curio.life_form_analyzer.none");
    private static Component radarInfo = Component.translatable("info.terra_curio.radar", 0);
    private static Component tallyCounterInfo = Component.translatable("info.terra_curio.tally_counter.unknown");

    public static void handle(LocalPlayer localPlayer) {
        INFORMATION = new Int2ObjectArrayMap<>();
        long gameTime = localPlayer.level().getGameTime();

        byte b = INFO_DATA[WATCH];
        if (!DISABLE[WATCH]) {
            if (b != 0 && timeInfo != null) {
                INFORMATION.put(WATCH, timeInfo.apply(localPlayer.level().dayTime()));
            }
        }

        long tenSec = gameTime % 200;
        if (!DISABLE[WEATHER_RADIO] && INFO_DATA[WEATHER_RADIO] != 0) {
            if (tenSec == WEATHER_RADIO) weatherRadioInfo = getWeatherInfo(localPlayer);
            INFORMATION.put(WEATHER_RADIO, weatherRadioInfo);
        }

        if (!DISABLE[SEXTANT] && INFO_DATA[SEXTANT] != 0) {
            INFORMATION.put(SEXTANT, Component.translatable("info.terra_curio.sextant." + localPlayer.level().getMoonPhase()));
        }

        if (!DISABLE[FISHERMANS_POCKET_GUIDE] && INFO_DATA[FISHERMANS_POCKET_GUIDE] != 0) {
            INFORMATION.put(FISHERMANS_POCKET_GUIDE, getFishingPowerInfo(localPlayer));
        }

        if (!DISABLE[METAL_DETECTOR]) {
            b = INFO_DATA[METAL_DETECTOR];
            if (TCKeyBindings.METAL_DETECTOR.get().isDown()) {
                if (!detectorPressed && b != 0) {
                    detectorPressed = true;
                    metalDetectorInfo = getMetalDetectorInfo(localPlayer);
                }
            } else detectorPressed = false;
            if (b != 0) {
                INFORMATION.put(METAL_DETECTOR, metalDetectorInfo);
            }
        }

        if (!DISABLE[LIFE_FORM_ANALYZER] && INFO_DATA[LIFE_FORM_ANALYZER] != 0) {
            if (tenSec == LIFE_FORM_ANALYZER) lifeFormAnalyzerInfo = getLifeFormAnalyzerInfo(localPlayer);
            INFORMATION.put(LIFE_FORM_ANALYZER, lifeFormAnalyzerInfo);
        }

        if (!DISABLE[RADAR] && INFO_DATA[RADAR] != 0) {
            if (tenSec == RADAR) radarInfo = Component.translatable(
                    "info.terra_curio.radar",
                    localPlayer.level().getEntities(localPlayer, new AABB(localPlayer.blockPosition()).inflate(63.5), entity -> entity instanceof Enemy).size()
            );
            INFORMATION.put(RADAR, radarInfo);
        }

        if (!DISABLE[TALLY_COUNTER] && INFO_DATA[TALLY_COUNTER] != 0) {
            INFORMATION.put(TALLY_COUNTER, tallyCounterInfo);
        }

        if (!DISABLE[DPS_METER] && INFO_DATA[DPS_METER] != 0) {
            INFORMATION.put(DPS_METER, Component.translatable("info.terra_curio.dps_meter", "%.2f".formatted(DPSMeter.getDPS(gameTime))));
        }

        if (!DISABLE[STOPWATCH] && INFO_DATA[STOPWATCH] != 0) {
            INFORMATION.put(STOPWATCH, Component.translatable(
                    "info.terra_curio.stopwatch",
                    "%.2f".formatted(Mth.length(
                            localPlayer.getX() - localPlayer.xOld,
                            localPlayer.getY() - localPlayer.yOld,
                            localPlayer.getZ() - localPlayer.zOld
                    ) * 20)
            ));
        }

        if (!DISABLE[COMPASS] && INFO_DATA[COMPASS] != 0) {
            INFORMATION.put(COMPASS, getCompassInfo(localPlayer));
        }

        if (!DISABLE[DEPTH_METER] && INFO_DATA[DEPTH_METER] != 0) {
            INFORMATION.put(DEPTH_METER, getDepthMeterInfo(localPlayer));
        }

        if (tenSec == 0) {
            for (int i = 0; i < INFO_DATA.length; i++) {
                if (INFO_DATA[i] >= 0) continue;
                boolean match = false;
                for (Player player : localPlayer.level().players()) {
                    if (player == localPlayer || player.distanceToSqr(localPlayer) > InfoCurioCheckPacketS2C.MAX_SHARE_DISTANCE_SQR) continue;
                    byte[] data = REMOTE_DATA.get(player.getId());
                    if (data == null) continue;
                    if (data[i] > -125) {
                        match = true;
                        break;
                    }
                }
                if (!match) INFO_DATA[i] = 0;
            }
        }
    }

    public static void reset() {
        if (!INFORMATION.isEmpty()) {
            INFORMATION = new Int2ObjectArrayMap<>();
            Arrays.fill(INFO_DATA, (byte) 0);
            REMOTE_DATA.clear();
        }
    }

    private static Component getWeatherInfo(Player player) {
        Level level = player.level();
        String weather = level.dimension() == Level.OVERWORLD ? "clear" : "cloudy";
        if (level.isRaining()) {
            if (level.getBiome(player.blockPosition()).is(Tags.Biomes.IS_COLD)) {
                weather = "snow";
            } else {
                weather = "rain";
            }
            if (level.isThundering()) {
                if ("snow".equals(weather)) {
                    weather = "thunder_snow";
                } else {
                    weather = "thunder";
                }
            }
        } else if (level.isThundering()) {
            weather = "thunder";
        }
        LibUtils.forMixin$Inject();
        return Component.translatable("info.terra_curio.weather_radio." + weather);
    }

    private static Component getFishingPowerInfo(Player player) {
        float fishingPower = LibUtils.forMixin$ModifyExpression(player.getLuck());
        return Component.translatable(
                "info.terra_curio.fishermans_pocket_guide",
                "%.2f".formatted(fishingPower)
        );
    }

    private static Component getMetalDetectorInfo(Player player) {
        AtomicReference<Component> atomic = new AtomicReference<>(Component.translatable("info.terra_curio.metal_detector.none"));
        player.level().getBlockStates(new AABB(player.blockPosition()).inflate(15.5)).distinct()
                .filter(TCCommonConfigs.rareBlocks::containsKey)
                .min(Comparator.comparingInt(TCCommonConfigs.rareBlocks::getInt))
                .ifPresent(blockState -> atomic.set(Component.translatable("info.terra_curio.metal_detector", blockState.getBlock().getName())));
        return atomic.get();
    }

    private static Component getLifeFormAnalyzerInfo(Player player) {
        AtomicReference<Component> atomic = new AtomicReference<>(Component.translatable("info.terra_curio.life_form_analyzer.none"));
        player.level().getEntities(player, new AABB(player.blockPosition()).inflate(47.5), entity -> TCCommonConfigs.rareCreatures.containsKey(entity.getType()))
                .stream().min(Comparator.comparingInt(entity -> TCCommonConfigs.rareCreatures.getInt(entity.getType())))
                .ifPresent(entity -> atomic.set(Component.translatable("info.terra_curio.life_form_analyzer", entity.getType().getDescription())));
        return atomic.get();
    }

    private static Component getCompassInfo(Player player) {
        double x = player.getX();
        double z = player.getZ();
        return Component.translatable("info.terra_curio.compass." + (x > 0 ? "east" : "west"), "%.2f".formatted(x))
                .append(Component.translatable("info.terra_curio.compass." + (z > 0 ? "south" : "north"), "%.2f".formatted(z)));
    }

    private static Component getDepthMeterInfo(Player player) {
        double y = player.getY();
        return Component.translatable("info.terra_curio.depth_meter." + (y > 63 ? "surface" : "underground"), "%.2f".formatted(y));
    }

    public static boolean hasMechanicalView() {
        return INFO_DATA[MECHANICAL_LENS] != 0; // confluence mixin here
    }

    public static Int2ObjectMap<Component> getInformation() {
        return INFORMATION;
    }

    public static void handlePacket(int playerId, byte[] enabled, Player player) {
        if (player != null && playerId != player.getId()) {
            // 存入远程玩家信息
            REMOTE_DATA.put(playerId, enabled);
        }
        byte b = enabled[WATCH];
        byte c = INFO_DATA[WATCH];
        // 玩家发给自己的信息 || 收到别人共享的信息
        if ((b >= 0 && c >= 0) || (b != -125 && c <= 0)) INFO_DATA[WATCH] = b;
        timeInfo = switch (INFO_DATA[WATCH]) {
            case 1, -126 -> InformationHandler::wrapHour;
            case 2, -127 -> InformationHandler::wrapHalfHour;
            case 3, -128 -> InformationHandler::wrapMinute;
            default -> null;
        };
        setInfoData(enabled, WEATHER_RADIO);
        setInfoData(enabled, SEXTANT);
        setInfoData(enabled, FISHERMANS_POCKET_GUIDE);
        setInfoData(enabled, METAL_DETECTOR);
        setInfoData(enabled, LIFE_FORM_ANALYZER);
        setInfoData(enabled, RADAR);
        setInfoData(enabled, TALLY_COUNTER);
        setInfoData(enabled, DPS_METER);
        setInfoData(enabled, STOPWATCH);
        setInfoData(enabled, COMPASS);
        setInfoData(enabled, DEPTH_METER);
        setInfoData(enabled, MECHANICAL_LENS);
    }

    private static Component wrapHour(long dayTime) {
        long hour = (dayTime % 24000) / 1000 + 6;
        if (hour > 23) hour -= 24;
        return Component.translatable("info.terra_curio.time", format(hour), "00");
    }

    private static Component wrapHalfHour(long dayTime) {
        dayTime = dayTime % 24000;
        long hour = dayTime / 1000 + 6;
        if (hour > 23) hour -= 24;
        String half = dayTime % 1000 > 499 ? "30" : "00";
        return Component.translatable("info.terra_curio.time", format(hour), half);
    }

    private static Component wrapMinute(long dayTime) {
        dayTime = dayTime % 24000;
        long hour = dayTime / 1000 + 6;
        if (hour > 23) hour -= 24;
        long minute = (long) ((dayTime % 1000) * 0.06F);
        return Component.translatable("info.terra_curio.time", format(hour), format(minute));
    }

    private static String format(long time) {
        return (time < 10 ? "0" : "") + time;
    }

    private static void setInfoData(byte[] enabled, int index) {
        byte b = enabled[index];
        byte c = INFO_DATA[index];
        // 玩家发给自己的信息 || 收到别人共享的信息
        if ((b >= 0 && c >= 0) || (b != -128 && c <= 0)) INFO_DATA[index] = b;
    }

    public static boolean hasInfoData(int index) {
        return INFO_DATA[index] != 0;
    }

    public static void handleEntityKilled(int amount, ResourceLocation entityType) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityType);
        tallyCounterInfo = Component.translatable("info.terra_curio.tally_counter").append(type.getDescription()).append("': " + (amount + 1));
    }
}
