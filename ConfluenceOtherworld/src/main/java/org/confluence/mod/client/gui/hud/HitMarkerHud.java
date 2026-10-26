package org.confluence.mod.client.gui.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import org.confluence.lib.common.LibDamageTypes;
import org.confluence.lib.common.LibTags;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.OwnedSummon;
import org.confluence.mod.client.ClientConfigs;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.client.PortDeltaTicker;
import org.mesdag.portlib.client.PortGuiLayer;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class HitMarkerHud implements PortGuiLayer {
    private static final ResourceLocation TEXTURE = Confluence.asResource("textures/gui/sprites/hud/hit_marker.png");
    private static final int TEXTURE_SIZE = 15;
    private static final long GROW_DURATION_MS = 150;
    private static final long FADE_DURATION_MS = 50;
    private static final float SCALE_INCREASE = 0.3f;
    private static final float FULL_ROTATION_DEGREES = 360f;
    private static final List<Hit> HITS = new ArrayList<>();

    private record Hit(long startTime, float angle) {}

    public static void onDamage(LivingEntity victim, DamageSource source) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean localDamage = minecraft.player != null
                && (isLocalDamage(source.getEntity(), minecraft.player) || isLocalDamage(source.getDirectEntity(), minecraft.player));
        if (!ClientConfigs.hitMarker || minecraft.player == null || victim == minecraft.player || !localDamage) {
            return;
        }

        // 1.20.1 的客户端伤害来源不提供武器快照，未知武器无法按物品 ID 排除。
        if (!isCategoryEnabled(source)) {
            return;
        }

        long now = Util.getMillis();
        HITS.removeIf(hit -> now - hit.startTime() >= GROW_DURATION_MS + FADE_DURATION_MS);
        HITS.add(new Hit(now, ThreadLocalRandom.current().nextFloat() * FULL_ROTATION_DEGREES));
    }

    private static boolean isLocalDamage(@Nullable Entity entity, LocalPlayer player) {
        if (entity == player) {
            return true;
        }
        if (entity instanceof OwnedSummon summon) {
            return player.getUUID().equals(summon.getSummonOwnerId());
        }
        if (entity instanceof Projectile projectile) {
            Entity owner = projectile.getOwner();
            return owner == player || owner instanceof OwnedSummon summon && player.getUUID().equals(summon.getSummonOwnerId());
        }
        return false;
    }

    private static boolean isSummon(@Nullable Entity entity) {
        return entity instanceof OwnedSummon || entity instanceof Projectile projectile && projectile.getOwner() instanceof OwnedSummon;
    }

    private static boolean isCategoryEnabled(DamageSource source) {
        if (source.is(LibDamageTypes.SUMMONER) || isSummon(source.getEntity()) || isSummon(source.getDirectEntity())) {
            return ClientConfigs.hitMarkerSummon;
        }
        if (source.is(PortTags.DamageTypes.IS_MAGIC)) {
            return ClientConfigs.hitMarkerMagic;
        }
        if (source.is(LibTags.DamageTypes.AS_MELEE_ATTACK)) {
            return ClientConfigs.hitMarkerMelee;
        }
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            return ClientConfigs.hitMarkerRanged;
        }
        return ClientConfigs.hitMarkerOther;
    }

    public static void clear() {
        HITS.clear();
    }

    @Override
    public void render(GuiGraphics guiGraphics, PortDeltaTicker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!ClientConfigs.hitMarker || minecraft.player == null || minecraft.level == null) {
            clear();
            return;
        }

        long now = Util.getMillis();
        HITS.removeIf(hit -> now - hit.startTime() >= GROW_DURATION_MS + FADE_DURATION_MS);
        if (HITS.isEmpty() || !minecraft.options.getCameraType().isFirstPerson()
                || minecraft.screen != null || minecraft.options.hideGui) {
            return;
        }

        int x = (guiGraphics.guiWidth() - TEXTURE_SIZE) / 2;
        int y = (guiGraphics.guiHeight() - TEXTURE_SIZE) / 2;
        float centerX = x + TEXTURE_SIZE / 2f;
        float centerY = y + TEXTURE_SIZE / 2f;
        PoseStack pose = guiGraphics.pose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        try {
            for (Hit hit : HITS) {
                long age = now - hit.startTime();
                float scale = 1f + SCALE_INCREASE * Math.min(1f, (float) age / GROW_DURATION_MS);
                float alpha = age <= GROW_DURATION_MS ? 1f : 1f - (float) (age - GROW_DURATION_MS) / FADE_DURATION_MS;
                pose.pushPose();
                try {
                    pose.translate(centerX, centerY, 0);
                    pose.mulPose(Axis.ZP.rotationDegrees(hit.angle()));
                    pose.scale(scale, scale, 1);
                    pose.translate(-centerX, -centerY, 0);
                    guiGraphics.setColor(1, 1, 1, alpha);
                    guiGraphics.blit(TEXTURE, x, y, 0f, 0f, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE, TEXTURE_SIZE);
                } finally {
                    pose.popPose();
                }
            }
        } finally {
            guiGraphics.setColor(1, 1, 1, 1);
            RenderSystem.disableBlend();
        }
    }
}
