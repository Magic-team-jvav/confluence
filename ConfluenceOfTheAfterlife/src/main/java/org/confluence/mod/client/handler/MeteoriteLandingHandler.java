package org.confluence.mod.client.handler;

import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.mod.network.s2c.MeteoriteLocationPacketS2C;

public final class MeteoriteLandingHandler {
    private static BlockPos meteoriteLocation;
    private static int tickUntilLanding = -1;

    public static void handleMeteorite(MeteoriteLocationPacketS2C packet) {
        meteoriteLocation = packet.location();
        tickUntilLanding = packet.tickUntilLanding();
    }

    public static void render(RenderLevelStageEvent event) {

    }
}
