package com.xiaohunao.enemybanner.payloads;

import com.mojang.logging.LogUtils;
import com.xiaohunao.enemybanner.AttachmentTypeRegister;
import com.xiaohunao.enemybanner.gui.BannerBoxMenu;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class ServerPayloadHandler{
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void handle(@NotNull PlayerBannerCountPayload data, @NotNull IPayloadContext context){
        context.player().setData(AttachmentTypeRegister.PLAYER_BANNER_COUNT, data.playerBannerCount());
        if (context.player().containerMenu instanceof BannerBoxMenu menu) {
            menu.setSelected(data.monsterId());
        }
    }
}
