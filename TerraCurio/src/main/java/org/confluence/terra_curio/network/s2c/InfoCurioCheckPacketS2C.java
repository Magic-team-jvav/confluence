package org.confluence.terra_curio.network.s2c;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Team;
import net.neoforged.neoforge.network.PacketDistributor;
import org.confluence.lib.common.item.IFunctionCouldEnable;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.api.primitive.TooltipComponentsValue;
import org.confluence.terra_curio.client.handler.InformationHandler;
import org.confluence.terra_curio.common.component.PrimitiveValueComponent;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.item.IMultiFunctionCouldEnable;
import org.confluence.terra_curio.util.CuriosUtils;
import org.confluence.terra_curio.util.TCUtils;

import java.util.ArrayList;
import java.util.Set;

public record InfoCurioCheckPacketS2C(int playerId, byte[] enabled) implements IPacketS2C {
    public static final Type<InfoCurioCheckPacketS2C> TYPE = new Type<>(TerraCurio.asResource("info_curio_check"));
    public static final StreamCodec<ByteBuf, InfoCurioCheckPacketS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, InfoCurioCheckPacketS2C::playerId,
            ByteBufCodecs.BYTE_ARRAY, InfoCurioCheckPacketS2C::enabled,
            InfoCurioCheckPacketS2C::new
    );
    public static final int ARRAY_LENGTH = 13;
    public static final double MAX_SHARE_DISTANCE_SQR = 1024.0;

    @Override
    public Type<InfoCurioCheckPacketS2C> type() {
        return TYPE;
    }

    @Override
    public void work(Player player) {
        InformationHandler.handlePacket(playerId, enabled, player);
    }

    private static byte checkEnabled(byte original, byte target, ItemStack itemStack, TooltipComponentsValue.Storage storage) {
        if (itemStack.getItem() instanceof IFunctionCouldEnable f) {
            if (f instanceof IMultiFunctionCouldEnable mf) {
                return mf.isEnabled(itemStack, storage) ? target : original;
            } else {
                return f.isEnabled(itemStack) ? target : original;
            }
        }
        return original;
    }

    public static void sendToClient(ServerPlayer serverPlayer, Inventory inventory) {
        ArrayList<ItemStack> itemStacks = CuriosUtils.getCurios(serverPlayer);
        itemStacks.addAll(inventory.items);
        byte watch = 0;
        byte weatherRadio = 0;
        byte sextant = 0;
        byte guide = 0;
        byte detector = 0;
        byte analyzer = 0;
        byte radar = 0;
        byte counter = 0;
        byte dpsMeter = 0;
        byte stopwatch = 0;
        byte compass = 0;
        byte depthMeter = 0;
        byte lens = 0;
        for (ItemStack stack : itemStacks) {
            PrimitiveValueComponent component = TCUtils.getAccessoriesComponent(stack);
            if (component == null) continue;
            TooltipComponentsValue value = component.get(TCItems.INFORMATION);
            if (value == null) continue;
            Set<TooltipComponentsValue.Storage> list = value.getSet();

            if (watch < 1 && list.contains(TCItems.HOUR$WATCH))
                watch = checkEnabled(watch, (byte) 1, stack, TCItems.HOUR$WATCH);
            else if (watch < 2 && list.contains(TCItems.HALF$HOUR$WATCH))
                watch = checkEnabled(watch, (byte) 2, stack, TCItems.HALF$HOUR$WATCH);
            else if (watch < 3 && list.contains(TCItems.MINUTE$WATCH))
                watch = checkEnabled(watch, (byte) 3, stack, TCItems.MINUTE$WATCH);
            if (weatherRadio == 0 && list.contains(TCItems.WEATHER$RADIO))
                weatherRadio = checkEnabled(weatherRadio, (byte) 1, stack, TCItems.WEATHER$RADIO);
            if (sextant == 0 && list.contains(TCItems.$SEXTANT))
                sextant = checkEnabled(sextant, (byte) 1, stack, TCItems.$SEXTANT);
            if (guide == 0 && list.contains(TCItems.FISHERMANS$POCKET$GUIDE))
                guide = checkEnabled(guide, (byte) 1, stack, TCItems.FISHERMANS$POCKET$GUIDE);
            if (detector == 0 && list.contains(TCItems.METAL$DETECTOR))
                detector = checkEnabled(detector, (byte) 1, stack, TCItems.METAL$DETECTOR);
            if (analyzer == 0 && list.contains(TCItems.LIFE$FORM$ANALYZER))
                analyzer = checkEnabled(analyzer, (byte) 1, stack, TCItems.LIFE$FORM$ANALYZER);
            if (radar == 0 && list.contains(TCItems.$RADAR))
                radar = checkEnabled(radar, (byte) 1, stack, TCItems.$RADAR);
            if (counter == 0 && list.contains(TCItems.TALLY$COUNTER))
                counter = checkEnabled(counter, (byte) 1, stack, TCItems.TALLY$COUNTER);
            if (dpsMeter == 0 && list.contains(TCItems.DPS$METER))
                dpsMeter = checkEnabled(dpsMeter, (byte) 1, stack, TCItems.DPS$METER);
            if (stopwatch == 0 && list.contains(TCItems.$STOPWATCH))
                stopwatch = checkEnabled(stopwatch, (byte) 1, stack, TCItems.$STOPWATCH);
            if (compass == 0 && list.contains(TCItems.$COMPASS))
                compass = checkEnabled(compass, (byte) 1, stack, TCItems.$COMPASS);
            if (depthMeter == 0 && list.contains(TCItems.DEPTH$METER))
                depthMeter = checkEnabled(depthMeter, (byte) 1, stack, TCItems.DEPTH$METER);
            if (lens == 0 && list.contains(TCItems.MECHANICAL$LENS))
                lens = checkEnabled(lens, (byte) 1, stack, TCItems.MECHANICAL$LENS);
        }
        PacketDistributor.sendToPlayer(serverPlayer, new InfoCurioCheckPacketS2C(serverPlayer.getId(), new byte[]{
                watch, weatherRadio, sextant, guide, detector, analyzer,
                radar, counter, dpsMeter, stopwatch, compass, depthMeter, lens
        }));
    }

    public static void sendToOthers(ServerPlayer serverPlayer) {
        ArrayList<ItemStack> itemStacks = CuriosUtils.getCurios(serverPlayer);
        itemStacks.addAll(serverPlayer.getInventory().items);
        byte watch = -125;
        byte weatherRadio = -128;
        byte sextant = -128;
        byte guide = -128;
        byte detector = -128;
        byte analyzer = -128;
        byte radar = -128;
        byte counter = -128;
        byte dpsMeter = -128;
        byte stopwatch = -128;
        byte compass = -128;
        byte depthMeter = -128;
        byte lens = -128;
        for (ItemStack stack : itemStacks) {
            PrimitiveValueComponent component = TCUtils.getAccessoriesComponent(stack);
            if (component == null) continue;
            TooltipComponentsValue value = component.get(TCItems.INFORMATION);
            if (value == null) continue;
            Set<TooltipComponentsValue.Storage> list = value.getSet();

            if (watch > -126 && list.contains(TCItems.HOUR$WATCH))
                watch = checkEnabled(watch, (byte) -126, stack, TCItems.HOUR$WATCH);
            else if (watch > -127 && list.contains(TCItems.HALF$HOUR$WATCH))
                watch = checkEnabled(watch, (byte) -127, stack, TCItems.HALF$HOUR$WATCH);
            else if (watch > -128 && list.contains(TCItems.MINUTE$WATCH))
                watch = checkEnabled(watch, (byte) -128, stack, TCItems.MINUTE$WATCH);
            if (weatherRadio == -128 && list.contains(TCItems.WEATHER$RADIO))
                weatherRadio = checkEnabled(weatherRadio, (byte) -1, stack, TCItems.WEATHER$RADIO);
            if (sextant == -128 && list.contains(TCItems.$SEXTANT))
                sextant = checkEnabled(sextant, (byte) -1, stack, TCItems.$SEXTANT);
            if (guide == -128 && list.contains(TCItems.FISHERMANS$POCKET$GUIDE))
                guide = checkEnabled(guide, (byte) -1, stack, TCItems.FISHERMANS$POCKET$GUIDE);
            if (detector == -128 && list.contains(TCItems.METAL$DETECTOR))
                detector = checkEnabled(detector, (byte) -1, stack, TCItems.METAL$DETECTOR);
            if (analyzer == -128 && list.contains(TCItems.LIFE$FORM$ANALYZER))
                analyzer = checkEnabled(analyzer, (byte) -1, stack, TCItems.LIFE$FORM$ANALYZER);
            if (radar == -128 && list.contains(TCItems.$RADAR))
                radar = checkEnabled(radar, (byte) -1, stack, TCItems.$RADAR);
            if (counter == -128 && list.contains(TCItems.TALLY$COUNTER))
                counter = checkEnabled(counter, (byte) -1, stack, TCItems.TALLY$COUNTER);
            if (dpsMeter == -128 && list.contains(TCItems.DPS$METER))
                dpsMeter = checkEnabled(dpsMeter, (byte) -1, stack, TCItems.DPS$METER);
            if (stopwatch == -128 && list.contains(TCItems.$STOPWATCH))
                stopwatch = checkEnabled(stopwatch, (byte) -1, stack, TCItems.$STOPWATCH);
            if (compass == -128 && list.contains(TCItems.$COMPASS))
                compass = checkEnabled(compass, (byte) -1, stack, TCItems.$COMPASS);
            if (depthMeter == -128 && list.contains(TCItems.DEPTH$METER))
                depthMeter = checkEnabled(depthMeter, (byte) -1, stack, TCItems.DEPTH$METER);
            if (lens == -128 && list.contains(TCItems.MECHANICAL$LENS))
                lens = checkEnabled(lens, (byte) -1, stack, TCItems.MECHANICAL$LENS);
        }
        boolean equals = watch == -125 && weatherRadio == -128 && sextant == -128 && guide == -128 && detector == -128 && analyzer == -128 &&
                radar == -128 && counter == -128 && dpsMeter == -128 && stopwatch == -128 && compass == -128 && depthMeter == -128 && lens == -128;
        if (equals) return; // 如果不需要发送, 则返回
        InfoCurioCheckPacketS2C packet = new InfoCurioCheckPacketS2C(serverPlayer.getId(), new byte[]{
                watch, weatherRadio, sextant, guide, detector, analyzer,
                radar, counter, dpsMeter, stopwatch, compass, depthMeter, lens
        });
        Team team = serverPlayer.getTeam();
        serverPlayer.serverLevel().players().forEach(player -> {
            if (player != serverPlayer && player.getTeam() == team && player.distanceToSqr(serverPlayer) < MAX_SHARE_DISTANCE_SQR) {
                PacketDistributor.sendToPlayer(player, packet);
            }
        });
    }
}
