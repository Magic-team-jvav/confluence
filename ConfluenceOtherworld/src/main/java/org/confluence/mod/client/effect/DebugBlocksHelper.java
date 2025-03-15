package org.confluence.mod.client.effect;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static org.confluence.mod.client.effect.RenderUtil.renderDebugBlock;

/**
 * 用于显示Debug方块的帮助类
 */
public class DebugBlocksHelper extends AbstractBufferManager{
    List<BlockPos> blocks = new java.util.ArrayList<>();
    private final int continueTick;
    private Long lastTime;
    private static DebugBlocksHelper instance;

    public void addDebugBlock(BlockPos pos) {
        blocks.add(pos);
        lastTime = System.currentTimeMillis();
    }

    public void addDebugBlock(List<BlockPos> pos) {
        blocks.addAll(pos);
        lastTime = System.currentTimeMillis();
    }

    public void clear(BlockPos pos) {
        blocks.remove(pos);
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
        if(lastTime!= null && lastTime + continueTick * 20L < System.currentTimeMillis()) {
            blocks.clear();
            lastTime = null;
        }
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
        for(BlockPos pos : blocks) {
            float progress = 1 - (float) (System.currentTimeMillis() - lastTime) / (continueTick * 21L);
            renderDebugBlock(buffer, pos,1.0F,255,255,255, (int) (255 * progress));
        }
    }
}
