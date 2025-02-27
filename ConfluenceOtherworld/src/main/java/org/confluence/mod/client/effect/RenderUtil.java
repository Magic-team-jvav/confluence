package org.confluence.mod.client.effect;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.core.BlockPos;

public class RenderUtil {
    public static void renderDebugBlock(BufferBuilder buffer, BlockPos pos, float size, int r, int g, int b, int a){
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        buffer.addVertex(x, y + size, z).setColor(r,g,b,a);
        buffer.addVertex(x + size, y + size, z).setColor(r,g,b,a);
        buffer.addVertex(x + size, y + size, z).setColor(r,g,b,a);
        buffer.addVertex(x + size, y + size, z + size).setColor(r,g,b,a);
        buffer.addVertex(x + size, y + size, z + size).setColor(r,g,b,a);
        buffer.addVertex(x, y + size, z + size).setColor(r,g,b,a);
        buffer.addVertex(x, y + size, z + size).setColor(r,g,b,a);
        buffer.addVertex(x, y + size, z).setColor(r,g,b,a);

        // BOTTaddVertex()
        buffer.addVertex(x + size, y, z).setColor(r,g,b,a);
        buffer.addVertex(x + size, y, z + size).setColor(r,g,b,a);
        buffer.addVertex(x + size, y, z + size).setColor(r,g,b,a);
        buffer.addVertex(x, y, z + size).setColor(r,g,b,a);
        buffer.addVertex(x, y, z + size).setColor(r,g,b,a);
        buffer.addVertex(x, y, z).setColor(r,g,b,a);
        buffer.addVertex(x, y, z).setColor(r,g,b,a);
        buffer.addVertex(x + size, y, z).setColor(r,g,b,a);

        // EdgeaddVertex()
        buffer.addVertex(x + size, y, z + size).setColor(r,g,b,a);
        buffer.addVertex(x + size, y + size, z + size).setColor(r,g,b,a);

        // EdgeaddVertex()
        buffer.addVertex(x + size, y, z).setColor(r,g,b,a);
        buffer.addVertex(x + size, y + size, z).setColor(r,g,b,a);

        // EdgeaddVertex()
        buffer.addVertex(x, y, z + size).setColor(r,g,b,a);
        buffer.addVertex(x, y + size, z + size).setColor(r,g,b,a);

        // EdgeaddVertex()
        buffer.addVertex(x, y, z).setColor(r,g,b,a);
        buffer.addVertex(x, y + size, z).setColor(r,g,b,a);
    }
}
