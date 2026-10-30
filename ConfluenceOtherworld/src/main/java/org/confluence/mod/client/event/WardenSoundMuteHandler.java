package org.confluence.mod.client.event;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.ClientConfigs;

/// 「关闭监守者声音」配置：开启后在客户端声音引擎播放前拦下全部监守者音效。
///
/// 在客户端拦截可以同时覆盖服务端广播的音效（脚步、咆哮、音波等）与客户端本地生成的心跳，
/// 不需要改动服务端逻辑；关闭配置后立即恢复正常播放。
@EventBusSubscriber(modid = Confluence.MODID, value = Dist.CLIENT)
public final class WardenSoundMuteHandler {
    private static final String WARDEN_SOUND_PREFIX = "entity.warden";

    private WardenSoundMuteHandler() {}

    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        if (!ClientConfigs.isWardenSoundDisabled()) return;
        if (event.getSound() != null && isWardenSound(event.getSound().getLocation())) {
            event.setSound(null);
        }
    }

    private static boolean isWardenSound(ResourceLocation location) {
        return ResourceLocation.DEFAULT_NAMESPACE.equals(location.getNamespace()) && location.getPath().startsWith(WARDEN_SOUND_PREFIX);
    }
}
