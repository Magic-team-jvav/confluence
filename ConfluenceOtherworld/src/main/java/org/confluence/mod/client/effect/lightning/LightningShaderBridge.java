// Adapted from minecraft-lightning skill. See META-INF/arc-surge-lightning-NOTICES.md.
package org.confluence.mod.client.effect.lightning;

import org.confluence.mod.Confluence;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

/** Preserve world projection/depth, then render after the shader pack's final composite. */
@Mod.EventBusSubscriber(modid=Confluence.MODID,value=Dist.CLIENT)
public final class LightningShaderBridge {
    private static Object api;
    private static java.lang.reflect.Method enabled;
    private static boolean searched;
    private static int depth=-1,width,height;
    private static Runnable pending;
    static boolean late;
    static Matrix4f projection=new Matrix4f(),modelView=new Matrix4f();
    static long draws;
    static boolean active(){
        if(Boolean.getBoolean("confluence.arcSurge.forceLightningLate"))return true;
        if(!searched){searched=true;for(String name:new String[]{"net.irisshaders.iris.api.v0.IrisApi","net.coderbot.iris.api.v0.IrisApi"})try{
            var cls=Class.forName(name);api=cls.getMethod("getInstance").invoke(null);enabled=cls.getMethod("isShaderPackInUse");break;
        }catch(ReflectiveOperationException|LinkageError ignored){}}
        if(enabled!=null)try{return (boolean)enabled.invoke(api);}catch(ReflectiveOperationException ignored){}
        return net.minecraftforge.fml.ModList.get().isLoaded("oculus")||net.minecraftforge.fml.ModList.get().isLoaded("iris");
    }
    static boolean prepare(Runnable draw){
        pending=null;
        int read=GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING),active=GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        GlStateManager._activeTexture(GL13.GL_TEXTURE0);int texture=GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        int[] vp=new int[4];GL11.glGetIntegerv(GL11.GL_VIEWPORT,vp);
        try{
            if(vp[0]!=0||vp[1]!=0||vp[2]<=0||vp[3]<=0)return false;
            int source=GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER,source);
            if(source!=0&&GL30.glGetFramebufferAttachmentParameteri(GL30.GL_READ_FRAMEBUFFER,GL30.GL_DEPTH_ATTACHMENT,GL30.GL_FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE)==GL11.GL_NONE)return false;
            if(depth<0)depth=GL11.glGenTextures();GlStateManager._bindTexture(depth);
            if(width!=vp[2]||height!=vp[3]){
                GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D,0,GL30.GL_DEPTH_COMPONENT32F,0,0,vp[2],vp[3],0);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MIN_FILTER,GL11.GL_NEAREST);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MAG_FILTER,GL11.GL_NEAREST);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_WRAP_S,GL12.GL_CLAMP_TO_EDGE);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_WRAP_T,GL12.GL_CLAMP_TO_EDGE);
            }else GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D,0,0,0,0,0,width,height);
            width=vp[2];height=vp[3];projection.set(RenderSystem.getProjectionMatrix());modelView.set(RenderSystem.getModelViewMatrix());pending=draw;return true;
        }finally{GlStateManager._bindTexture(texture);GlStateManager._activeTexture(active);GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER,read);}
    }
    static Matrix4f clip(Matrix4f pose){return new Matrix4f(late?projection:RenderSystem.getProjectionMatrix()).mul(late?modelView:RenderSystem.getModelViewMatrix()).mul(pose);}
    static void uniforms(ShaderInstance shader){
        shader.safeGetUniform("LateDepthEnabled").set(late?1f:0f);
        shader.safeGetUniform("WorldProjection").set(projection);shader.safeGetUniform("WorldModelView").set(modelView);
        var main=Minecraft.getInstance().getMainRenderTarget();shader.safeGetUniform("OutputSize").set((float)main.width,(float)main.height);
        if(late)shader.setSampler("DepthSnapshot",depth);
    }
    private static void flush(){
        Runnable draw=pending;pending=null;if(draw==null)return;
        var mc=Minecraft.getInstance();if(mc.level==null)return;
        try(var state=new LightningHalo.State()){
            mc.getMainRenderTarget().bindWrite(true);RenderSystem.disableScissor();late=true;
            try{draw.run();draws++;}finally{late=false;}
        }
    }
    @SubscribeEvent public static void gui(RenderGuiEvent.Pre event){if(pending!=null){event.getGuiGraphics().flush();flush();}}
    @SubscribeEvent(priority=net.minecraftforge.eventbus.api.EventPriority.HIGHEST) public static void frame(TickEvent.RenderTickEvent event){
        if(event.phase==TickEvent.Phase.START)pending=null;
        else if(Minecraft.getInstance().options.hideGui)flush();
    }
    static void release(){pending=null;if(depth>=0){RenderSystem.deleteTexture(depth);depth=-1;}width=height=0;}
    private LightningShaderBridge(){}
}
