package org.confluence.mod.client.renderer.item;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

public class EntityDisplayItemRenderer extends BlockEntityWithoutLevelRenderer {
    private Map<UUID, Entity> entityMap = new Hashtable<>();
    private ClientLevel currentLevel;
    private RemotePlayer magicHarpPlayer;
    private ClientLevel magicHarpLevel;

    private RemotePlayer getMagicHarp(ClientLevel level) {
        if (magicHarpPlayer == null || magicHarpLevel != level) {
            magicHarpLevel = level;
            magicHarpPlayer = new RemotePlayer(level, new GameProfile(Mth.createInsecureUUID(level.random), "MagicHarp2333")) { // Our Leader
                private ResourceLocation playerSkin;

                @Override
                public ResourceLocation getSkinTextureLocation() {
                    if (playerSkin == null) {
                        getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 127); // 显示双层模型
                        this.playerSkin = Confluence.asResource("textures/magic_harp_2333.png");
                    }
                    return playerSkin;
                }
            };
        }
        return magicHarpPlayer;
    }

    public EntityDisplayItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) return;
        // 按 level 实例而不是 dimension 判断：同一维度的不同存档也不能复用旧实体。
        if (level != currentLevel) {
            this.currentLevel = level;
            this.entityMap = new HashMap<>();
        }
        CompoundTag tag = LibUtils.getItemStackNbtIfPresent(stack);
        Entity entity;
        if (tag == null) {
            entity = getMagicHarp(level);
        } else {
            try {
                entity = entityMap.computeIfAbsent(tag.getUUID(Entity.UUID_TAG), uuid -> {
                    Entity loaded = EntityType.loadEntityRecursive(tag, level, java.util.function.Function.identity());
                    return loaded == null ? new Pig(EntityType.PIG, level) : loaded;
                });
            } catch (Exception e) {
                entity = getMagicHarp(level);
            }
        }

        poseStack.pushPose();
        EntityRenderDispatcher erd = minecraft.getEntityRenderDispatcher();
        MultiBufferSource.BufferSource immediate = minecraft.renderBuffers().bufferSource();
        erd.setRenderShadow(false);
        poseStack.translate(0.5F, 0.25F, 0.5F);
        float rotation = 0.0F;
        if (displayContext == ItemDisplayContext.GUI) {
            rotation = Mth.HALF_PI * -0.5F;
        } else if (displayContext == ItemDisplayContext.FIXED) {
            rotation = Mth.PI * 0.75F;
        }
        poseStack.mulPose(Axis.YP.rotation(rotation));
        erd.render(entity, 0, 0, 0, 0, 1, poseStack, immediate, packedLight);
        erd.setRenderShadow(true);
        immediate.endBatch();
        poseStack.popPose();
    }

    /// 世界卸载时释放缓存实体与假玩家，避免旧 ClientLevel 被渲染器静态实例持有。
    public void clear() {
        this.entityMap.clear();
        this.currentLevel = null;
        this.magicHarpPlayer = null;
        this.magicHarpLevel = null;
    }
}
