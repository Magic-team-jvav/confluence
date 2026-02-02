package org.confluence.terraentity.entity.npc.chat;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gui.Font;
import net.minecraft.world.phys.Vec2;
import org.confluence.terraentity.api.npc.chat.IChatElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 聊天元素排版器。只需要传入聊天元素列表和字体，自动转换统一的相对格式
 */
public class ChatArranger {

    private int initialX = 12;
    private int initialY = 12;

    public Map<IChatElement, Vec2> elementPositions;
    public int width;
    public int height;
    public int startTick = 0;

    public ChatArranger(List<IChatElement> chatElements, Font font){
        this.elementPositions = new Object2ObjectOpenHashMap<>();
        this.arrange(chatElements, font);
    }

    public Vec2 getElementPosition(IChatElement element){
        return elementPositions.get(element);
    }

    public void arrange(List<IChatElement> chatElements, Font font){
        int x = 0;
        int y = 0;

        List<IChatElement> lineElements = new ArrayList<>();
        for(IChatElement element : chatElements){
            int cacheX = x;
            x = element.wrapWidth(x, font);

            this.width = Math.max(this.width, x);
            y = element.warpHeight(y, font);
            this.height = Math.max(this.height, y);
            lineElements.add(element);

            if(x == 0){ // 此时换行
                for(IChatElement lineElement : lineElements){
                    elementPositions.computeIfPresent(lineElement, (k, v) -> new Vec2(v.x - cacheX * 0.5f, v.y));
                }

                lineElements.clear();
            }else{
                Vec2 pos = new Vec2(cacheX, y);
                elementPositions.put(element, pos);
            }

        }
        for(IChatElement lineElement : lineElements){
            Vec2 pos = elementPositions.get(lineElement);
            int finalX1 = x;
            elementPositions.computeIfPresent(lineElement, (k, v) -> new Vec2(v.x - finalX1 * 0.5f, v.y));
        }

        elementPositions.entrySet().stream()
                .map(m-> Map.entry(m.getKey(), new Vec2(m.getValue().x, m.getValue().y - this.height * 0.5f)))
                .forEach(m-> elementPositions.put(m.getKey(), m.getValue()));

    }

    public float getBubbleWidth(){
        _w = Math.max(this.width * 0.5f, 25) +25;
        return _w;
    }

    public float getBubbleHeight() {
        _h = Math.max(this.height * 0.5f, 20) + 25;
        return _h;
    }

    float _w;
    float _h;

    public float getBubbleScale(){
        if(_w == 0 || _h == 0){
            getBubbleWidth();
            getBubbleHeight();
        }
        return Math.max(_w / 50, _h / 50);
    }

}
