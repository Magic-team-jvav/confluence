package nowebsite.makertechno.the_trackers.core.track;

import net.minecraft.world.entity.Entity;
import nowebsite.makertechno.the_trackers.client.gui.TGui;
import nowebsite.makertechno.the_trackers.core.config.TConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public class TrackersMonitor {
    private static WorldSingletonTracker tracker;
    /** 更新时间跳刻计数 */
    private static int count = 0;
    /** 触发一次更新，注意这不代表触发一次计算，具体跳过时间刻由配置决定 */
    public static void trigger() {
        if (tracker == null) {
            tracker = WorldSingletonTracker.getInstance();
            TGui.setExtendTracker(tracker);
        }

        if (++count >= TConfig.interval) {
            count = 0;
            Map<UUID, Entity> cached = EntityTracker.process();

            if (cached != null) tracker.trigger(cached);
        }
    }
    public static void quit() {
        TGui.setExtendTracker(null);
        if (tracker != null) tracker.close();
        tracker = null;
    }

    public static @Nullable WorldSingletonTracker getTracker() {
        return tracker;
    }
}
