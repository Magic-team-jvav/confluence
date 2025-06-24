package org.confluence.mod.integration.xaero;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import xaero.map.server.radar.tracker.ISyncedPlayerTrackerSystem;

public class ConfluenceSyncedPlayerTrackerSystem implements ISyncedPlayerTrackerSystem {
    @Override
    public int getTrackingLevel(Player tracker, Player tracked) {
        PlayerTeam trackerTeam = tracker.getTeam();
        PlayerTeam trackedTeam = tracked.getTeam();
        if (trackerTeam == trackedTeam) return 2;
        if (trackerTeam == null || trackedTeam == null) return 0;
        return 1;
    }

    @Override
    public boolean isPartySystem() {
        return true;
    }
}
