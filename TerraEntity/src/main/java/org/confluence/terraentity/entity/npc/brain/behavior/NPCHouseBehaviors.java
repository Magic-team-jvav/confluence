package org.confluence.terraentity.entity.npc.brain.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import org.apache.commons.lang3.mutable.MutableLong;
import org.confluence.terraentity.entity.npc.AbstractTerraNPC;
import org.confluence.terraentity.entity.npc.house.House;
import org.confluence.terraentity.entity.npc.house.HouseManager;
import org.confluence.terraentity.entity.npc.house.IHouseDetector;

/**
 * NPC房屋行为
 */
public class NPCHouseBehaviors {
    public static int Detect_Interval = 511;

    public static BehaviorControl<AbstractTerraNPC> walkToHouse(float speedModifier) {

        MutableLong mutablelong = new MutableLong(0L);
        return BehaviorBuilder.create((instance) -> instance.group(
                instance.absent(MemoryModuleType.WALK_TARGET),
                instance.present(MemoryModuleType.HOME)
        ).apply(instance, (walk_target, home_pos) -> (serverLevel, entity, l) -> {
            if (serverLevel.getGameTime() - mutablelong.getValue() >= 20L) {
                entity.setHouse(HouseManager.getInstance().getHouse(entity.getUUID()));
                House house = entity.getHouse();
                if (!house.isEmpty()) {


                    // 如果已经有房屋，则回到房屋
                    GlobalPos globalpos = instance.get(home_pos);
                    BlockPos pos = globalpos.pos();
                    if(serverLevel.isNight() && entity.blockPosition().distSqr(pos ) > 500){
                        entity.teleportTo(pos.getX(), pos.getY(), pos.getZ());
                    }else {
                        walk_target.set(new WalkTarget(pos, speedModifier, 1));
                    }
//                    walk_target.set(new WalkTarget(entity.house.center(), speedModifier, 1));
                    HouseManager.getInstance().tryAddHouse(entity.getUUID(),
                            house.min(), house.max(), house.center());
                    // todo 设置为椅子坐标
//                    home_pos.set(GlobalPos.of(serverLevel.dimension(), entity.house.center()));
                    return true;
                }

            }
            return false;
        }));
    }



    public static BehaviorControl<AbstractTerraNPC> findHouse(MemoryModuleType<GlobalPos> poiPosMemory) {
        return BehaviorBuilder.create((instance) -> instance.group(
                instance.registered(poiPosMemory)
        ).apply(instance, (memoryAccessor) -> (serverLevel, entity, l) -> {

            boolean timeToRefresh = (entity.tickCount & Detect_Interval) == 0;
            if(timeToRefresh) {

                // 如果已有房屋，检查房屋是否合理
                House oldHouse = HouseManager.getInstance().getHouse(entity.getUUID());
                if(oldHouse == null){
                    entity.setHouse(House.EMPTY);
                }


                BlockPos blockpos;
                if(oldHouse == null || oldHouse.isEmpty()){
                    // 如果房屋为空，在当前位置检测
                    blockpos = entity.blockPosition();
                }else{
                    // 如果房屋不为空，检查已有的房屋是否合理
                    blockpos = oldHouse.center();
                }

                if (!serverLevel.isLoaded(blockpos)) return true;

                HouseManager.getInstance().removeHouse(entity.getUUID());
                IHouseDetector info = IHouseDetector.detect(blockpos, serverLevel);
                if (info.isError()) {
                    // 检测失败
                    entity.setHouse(House.EMPTY);
                    return false;
                }

                // 成功检测到房屋
                House house = info.getHouse(entity.getUUID());

                if(HouseManager.getInstance().tryAddHouse(house)){
                    // 成功添加房屋
                    entity.setHouse(house);
                    memoryAccessor.set(GlobalPos.of(serverLevel.dimension(), house.center()));
                    return true;
                }
                // 房屋已存在
                entity.setHouse(House.EMPTY);
                return false;
            }


            return true;

        }));
    }


}
