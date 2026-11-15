package org.confluence.mod.client.handler;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.common.entity.npc.dialog.NPCDialog;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/// 对话界面只使用当前服务器下发的对话表，与本地资源重载和集成服务器数据隔离。
public final class ClientNPCDialogs {
    private static Map<EntityType<?>, NPCDialog> dialogs = Map.of();

    private ClientNPCDialogs() {}

    public static void update(Map<EntityType<?>, NPCDialog> value) {
        dialogs = Map.copyOf(value);
    }

    public static void clear() {
        dialogs = Map.of();
    }

    public static @Nullable String getRandomDialogKey(RandomSource random, EntityType<?> type) {
        NPCDialog dialog = dialogs.get(type);
        return dialog == null || dialog.isEmpty() ? null : dialog.randomKey(random);
    }
}
