package org.confluence.terraentity.client.post;

import com.mojang.blaze3d.pipeline.TextureTarget;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.client.ModRenderTypes;
import org.confluence.terraentity.client.util.ShaderUtil;
import org.confluence.terraentity.entity.boss.BrainOfCthulhu;
import org.confluence.terraentity.mixed.IShaderInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrainTranslucent {
    public static class tuple{
        public TextureTarget target;
        public int light;
        public tuple(TextureTarget target, int light){
            this.target = target;
            this.light = light;
        }
    }

    public static Map<BrainOfCthulhu, tuple> entityMap = new HashMap<>();

    static TextureTarget temp;
    static TextureTarget temp2;
    public static void render(RenderLevelStageEvent event){

        if(entityMap.isEmpty()) return;

        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        List<BrainOfCthulhu> shouldBeRemoved = new ArrayList<>();
        if(temp == null || temp.width != Minecraft.getInstance().getMainRenderTarget().width || temp.height != Minecraft.getInstance().getMainRenderTarget().height) {
            temp = new TextureTarget(Minecraft.getInstance().getMainRenderTarget().width, Minecraft.getInstance().getMainRenderTarget().height, false, true);
            temp2 = new TextureTarget(Minecraft.getInstance().getMainRenderTarget().width, Minecraft.getInstance().getMainRenderTarget().height, false, true);
        }
        temp.setClearColor(0, 0, 0, 0);
        temp.clear(true);
        temp2.setClearColor(0, 0, 0, 0);
        temp2.clear(true);

        temp.bindWrite(true);
        Minecraft.getInstance().getMainRenderTarget().blitToScreen(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        temp2.bindWrite(true);
        Minecraft.getInstance().getMainRenderTarget().blitToScreen(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());

        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        //将maintarget拷贝到
        temp.bindWrite(true);

        TextureTarget out = null;
        TextureTarget in;
        int c = 0;
        for(BrainOfCthulhu brain : entityMap.keySet()){
            if(brain!= null && brain.isAlive()){
                // 对每个BOSS本体虚影渲染
                c++;
                out = (c & 1) == 0? temp: temp2;
                in = (c & 1) == 0? temp2: temp;
                out.bindWrite(true);
                tuple tuple = entityMap.get(brain);
                float alpha = Math.clamp(brain.getFadeProgress(), 0, 1);
                if(alpha < 0.99f){
                    TextureTarget target = tuple.target;
                    TextureTarget finalIn = in;

//                    ShaderUtil.blitScreen(ModRenderTypes.Shaders.colorBlitShader, shader->{
//                        shader.COLOR_MODULATOR.set(1f, 1f, 1f, alpha);
//                        shader.setSampler("Sampler0", finalIn);
//                        shader.setSampler("Sampler1", target);
//                    });

                    float p = Math.clamp(brain.getDissolveProgress(), 0, 1);
                    ((IShaderInstance)ModRenderTypes.Shaders.dissolveBlitShader).getTerra_entity$Progress().set(1-p);
                    ((IShaderInstance)ModRenderTypes.Shaders.dissolveBlitShader).getTerra_entity$Distance().set(Minecraft.getInstance().player.distanceTo(brain));
                    ShaderUtil.blitScreen(ModRenderTypes.Shaders.dissolveBlitShader, shader->{
                        shader.COLOR_MODULATOR.set(1f, 0f, 1f, alpha);
                        shader.setSampler("Sampler0", finalIn);
                        shader.setSampler("Sampler1", target);
                        shader.setSampler("Sampler2",Minecraft.getInstance().getTextureManager().getTexture(TerraEntity.space("textures/gui/noise.png")) );
                    });
                }else{
                    shouldBeRemoved.add(brain);
                }
            }else{
                shouldBeRemoved.add(brain);
            }
        }
        for(BrainOfCthulhu brain : shouldBeRemoved){
            entityMap.remove(brain);
        }

        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);

        if(out != null) {
            out.blitToScreen(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        }
        temp.clear(true);
        for(tuple t : entityMap.values()){
            t.target.clear(true);
        }
    }
}
