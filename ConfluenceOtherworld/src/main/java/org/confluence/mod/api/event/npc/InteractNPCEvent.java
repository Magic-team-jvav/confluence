package org.confluence.mod.api.event.npc;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.confluence.mod.common.entity.npc.BaseNPC;

import java.util.function.BiConsumer;

/// NPC 完成装备交换等固定交互后、执行商店或特殊动作前发布的事件。
/// 取消事件会阻止默认动作；设置 replacement 可以替换默认动作而不改变公共交互顺序。
@Cancelable
public final class InteractNPCEvent extends Event {
    private final BaseNPC npc;
    private final ServerPlayer player;
    private BiConsumer<BaseNPC, ServerPlayer> replacement;
    private InteractionResult result = InteractionResult.PASS;

    public InteractNPCEvent(BaseNPC npc, ServerPlayer player) {
        this.npc = npc;
        this.player = player;
    }

    public BaseNPC getNPC() {
        return npc;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public void setReplacement(BiConsumer<BaseNPC, ServerPlayer> replacement) {
        this.replacement = replacement;
    }

    public void execute(Runnable defaultAction) {
        if (replacement == null) defaultAction.run();
        else replacement.accept(npc, player);
    }

    public InteractionResult getInteractionResult() {
        return result;
    }

    public void setInteractionResult(InteractionResult result) {
        this.result = result;
    }
}
