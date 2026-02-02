package org.confluence.terraentity.entity.npc.house;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.confluence.lib.util.ComputerUtils;
import org.confluence.terraentity.init.TETags;

import java.util.List;

/**
 * 房屋检测信息
 *
 * @param min  左下角
 * @param max  右上角
 * @param list 包含范围
 * @param type 检测信息类型
 */
public record HouseDetectInfo(BlockPos min, BlockPos max, BlockPos center, List<BlockPos> list, DetectType type) implements IHouseDetector {
    public static int DetectRange = 20;

    public static HouseDetectInfo error(DetectType type) {
        return new HouseDetectInfo(BlockPos.ZERO, BlockPos.ZERO, BlockPos.ZERO, List.of(), type);
    }

    public static HouseDetectInfo detect(BlockPos start, Level level){
        // TODO: 房间内可包含方块
        List<BlockPos> list = ComputerUtils.zoomDetection(level, start, DetectRange, state->{
            return state.isAir() || state.is(TETags.Blocks.NPC_HOUSE_CONSTITUTE);
            }
        );

        if(list.isEmpty()){
            return error(DetectType.TOO_LARGE);
        }
        // 空间太小
        if(list.size() < 16){
            return error(DetectType.TOO_SMALL);
        }
        int minx = Integer.MAX_VALUE;
        int miny = Integer.MAX_VALUE;
        int minz = Integer.MAX_VALUE;
        int maxx = Integer.MIN_VALUE;
        int maxy = Integer.MIN_VALUE;
        int maxz = Integer.MIN_VALUE;

        boolean hasLight = false;
        boolean hasChair = false;
        boolean hasTable = false;

        for(BlockPos blockPos : list){
            BlockState state = level.getBlockState(blockPos);
            if(!hasLight && level.getLightEmission(blockPos) > 10) {
                hasLight = true;
            }
            if(!hasChair && state.is(TETags.Blocks.NPC_HOUSE_CHAIR)) {
                hasChair = true;
            }
            if(!hasTable && state.is(TETags.Blocks.NPC_HOUSE_TABLE)) {
                hasTable = true;
            }
            minx = Math.min(minx, blockPos.getX());
            miny = Math.min(miny, blockPos.getY());
            minz = Math.min(minz, blockPos.getZ());
            maxx = Math.max(maxx, blockPos.getX());
            maxy = Math.max(maxy, blockPos.getY());
            maxz = Math.max(maxz, blockPos.getZ());
        }
        // 空间xz单个方向最小值
        if(maxx - minx < 4 || maxz - minz < 4){
            return error(DetectType.TOO_SMALL);
        }
        if(!hasLight){
            return error(DetectType.NO_DYNAMIC_LIGHT);
        }
        if(!hasChair){
            return error(DetectType.NO_CHAIR);
        }
        if(!hasTable){
            return error(DetectType.NO_TABLE);
        }
        BlockPos min = new BlockPos(minx, miny, minz);
        BlockPos max = new BlockPos(maxx, maxy, maxz);

        return new HouseDetectInfo(min, max, start, list, DetectType.FOUND_HOUSE);
    }

    public boolean isError() {
        return type != DetectType.FOUND_HOUSE;
    }

    @Override
    public String message() {
        return type.getTranslationKey();
    }

    public enum DetectType {

        TOO_LARGE("tooltip.terra_entity.house_detect.message.too_large"),
        TOO_SMALL("tooltip.terra_entity.house_detect.message.too_small"),
        NO_DYNAMIC_LIGHT("tooltip.terra_entity.house_detect.message.no_dynamic_light"),
        NO_CHAIR("tooltip.terra_entity.house_detect.message.no_chair"),
        NO_TABLE("tooltip.terra_entity.house_detect.message.no_table"),
        FOUND_HOUSE("tooltip.terra_entity.house_detect.message.found_house");

        private final String translationKey;
        DetectType(String translationKey){
            this.translationKey = translationKey;
        }

        public String getTranslationKey() {
            return translationKey;
        }
    }
}
