package org.confluence.terraentity.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.confluence.terraentity.entity.npc.house.HouseDetectInfo;
import org.confluence.terraentity.entity.npc.house.IHouseDetector;


/**
 * 替换房屋检测
 */
public class HouseDetectEvent extends Event implements ICancellableEvent {
    private final BlockPos pos;
    private final Level level;
    private IHouseDetector detector;

    public HouseDetectEvent(BlockPos pos, Level level) {
        this.pos = pos;
        this.level = level;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Level getLevel() {
        return level;
    }

    public void replace(IHouseDetector detector){
        this.detector = detector;
    }

    public IHouseDetector getDetector(){
        if(detector != null){
            return detector;
        }
        return HouseDetectInfo.detect(pos, level);
    }
}
