package org.confluence.mod.network.s2c;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.confluence.lib.network.IPacketS2C;
import org.confluence.mod.Confluence;
import org.confluence.mod.client.gui.screen.AnglerDialogScreen;

///
public record OpenAnglerDialogPacketS2C(
        int entityId,
        byte state,
        Item questFish,
        String levelName
) implements IPacketS2C {
    public static final byte COMPLETED = 0;
    public static final byte NO_QUEST = 1;
    public static final byte SHOW_HINT = 2;
    public static final byte WAKE_UP = 3;
    // CAN_SUBMIT is handled server-side by AnglerNPC, never reaches dialog

    public static final Type<OpenAnglerDialogPacketS2C> TYPE = Confluence.createType("open_angler_dialog");
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenAnglerDialogPacketS2C> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, OpenAnglerDialogPacketS2C::entityId,
                    ByteBufCodecs.BYTE, OpenAnglerDialogPacketS2C::state,
                    ByteBufCodecs.registry(Registries.ITEM), OpenAnglerDialogPacketS2C::questFish,
                    ByteBufCodecs.STRING_UTF8, OpenAnglerDialogPacketS2C::levelName,
                    OpenAnglerDialogPacketS2C::new
            );

    @Override
    public void work(Player player) {
        AnglerDialogScreen.State s = switch (state) {
            case COMPLETED -> AnglerDialogScreen.State.COMPLETED;
            case NO_QUEST -> AnglerDialogScreen.State.NO_QUEST;
            case WAKE_UP -> AnglerDialogScreen.State.WAKE_UP;
            default -> AnglerDialogScreen.State.SHOW_HINT;
        };
        AnglerDialogScreen.open(entityId, s, questFish, levelName);
    }

    @Override
    public Type<OpenAnglerDialogPacketS2C> type() {
        return TYPE;
    }
}
