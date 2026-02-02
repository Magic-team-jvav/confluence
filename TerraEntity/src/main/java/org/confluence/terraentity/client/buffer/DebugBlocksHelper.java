package org.confluence.terraentity.client.buffer;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.confluence.terraentity.client.util.ShaderUtil.renderDebugBlock;

/**
 * 用于显示Debug方块的帮助类
 */
public class DebugBlocksHelper extends AbstractBufferManager{

    private final Map<BlockPos, DebugInfo> debugInfoMap = new HashMap<>();
    private final int continueTick;
    private static DebugBlocksHelper instance;

    public void addDebugBlock(BlockPos pos) {
        debugInfoMap.put(pos, new DebugInfo(255,255,255, continueTick));
    }

    public void addDebugBlock(List<BlockPos> pos) {
        for(BlockPos p : pos) {
            debugInfoMap.put(p, new DebugInfo(255,255,255, continueTick));
        }
    }

    public void addDebugBlock(BlockPos pos, DebugInfo debugInfo) {
        debugInfoMap.put(pos, debugInfo);
    }

    public void addDebugBlock(List<BlockPos> pos, DebugInfo debugInfo) {
        for(BlockPos p : pos) {
            debugInfoMap.put(p, debugInfo);
        }
    }

    @Override
    protected boolean shouldRefresh() {
        return super.shouldRefresh() && !debugInfoMap.isEmpty();
    }

    public void clear(BlockPos pos) {
//        blocks.remove(pos);
        debugInfoMap.remove(pos);
    }

    public static DebugBlocksHelper Singleton() {
        if (instance == null) {
            instance = new DebugBlocksHelper(50,100);
        }
        return instance;
    }

    /**
     * @param refreshTime 刷新时间 单位：毫秒
     * @param continueTick 持续时间 单位：tick
     */
    DebugBlocksHelper(int refreshTime, int continueTick) {
        super(refreshTime);
        this.continueTick = continueTick;
    }

    public void refresh(){
        super.refresh();
    }

    @Override
    protected void beforeRender() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    @Override
    protected void afterRender(PoseStack poseStack) {
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    @Override
    protected void buildBuffer(BufferBuilder buffer) {
        if(debugInfoMap.isEmpty()){
            return;
        }
        List<BlockPos> removeList = new ArrayList<>();
        for(Map.Entry<BlockPos, DebugInfo> entry : debugInfoMap.entrySet()) {
            DebugInfo debugInfo = entry.getValue();
            long time = System.currentTimeMillis() - debugInfo.startTime;
            int continueTick = (int) (debugInfo.continueTick * 21L);
            if(time > continueTick) {
                removeList.add(entry.getKey());
                continue;
            }

            float progress = 1 - (float) (time) / continueTick;
            renderDebugBlock(buffer, entry.getKey(),1.0F,debugInfo.r,debugInfo.g,debugInfo.b, (int) (255 * progress));
        }
        for(BlockPos pos : removeList) {
            debugInfoMap.remove(pos);
        }
    }

    public record DebugInfo(int r, int g, int b, int continueTick, long startTime){
        public DebugInfo(int r, int g, int b, int continueTick) {
            this(r, g, b, continueTick, System.currentTimeMillis());
        }
    }
}
