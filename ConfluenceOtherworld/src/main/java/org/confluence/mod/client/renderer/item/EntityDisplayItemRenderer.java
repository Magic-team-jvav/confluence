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
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class EntityDisplayItemRenderer extends BlockEntityWithoutLevelRenderer {
    private final Map<UUID, Entity> entityMap = new Hashtable<>();
    private final Function<ClientLevel, RemotePlayer> magicHarp2333 = new Function<>() {
        private RemotePlayer cache;

        @Override
        public RemotePlayer apply(ClientLevel level) {
            if (cache == null) {
                this.cache = new RemotePlayer(level, new GameProfile(UUID.randomUUID(), "MagicHarp2333")) { // Our Leader
                    private PlayerSkin playerSkin;

                    @Override
                    public PlayerSkin getSkin() {
                        if (playerSkin == null) {
                            getEntityData().set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 127); // 显示双层模型
                            this.playerSkin = new PlayerSkin(Confluence.asResource("textures/magic_harp_2333.png"), null, null, null, PlayerSkin.Model.WIDE, false);
                        }
                        return playerSkin;
                    }
                };
            }
            return cache;
        }
    };

    public EntityDisplayItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        CompoundTag tag = LibUtils.getItemStackNbtIfPresent(stack);
        Entity entity;
        if (tag == null) {
            entity = magicHarp2333.apply(level);
        } else {
            if (level.getGameTime() % 24000L == 0) entityMap.clear(); // 每天清一次缓存
            entity = entityMap.computeIfAbsent(tag.getUUID(Entity.UUID_TAG), itemStack -> {
                Entity loaded = EntityType.loadEntityRecursive(tag, level, Function.identity());
                return loaded == null ? new Pig(EntityType.PIG, level) : loaded;
            });
        }

        poseStack.pushPose();
        EntityRenderDispatcher erd = Minecraft.getInstance().getEntityRenderDispatcher();
        MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
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
}
