package com.xiaohunao.enemybanner.payloads;

import com.mojang.logging.LogUtils;
import com.xiaohunao.enemybanner.AttachmentTypeRegister;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class ClientPayloadHandler{
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void handle(@NotNull PlayerBannerCountPayload data, @NotNull IPayloadContext context){
        //接受服务端数据，在客户端处理
        context.player().setData(AttachmentTypeRegister.PLAYER_BANNER_COUNT, data.playerBannerCount());
    }
}
