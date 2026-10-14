package org.confluence.mod.common.item.common;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.confluence.lib.common.component.ModRarity;
import org.confluence.lib.common.item.CustomRarityItem;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class EverBeneficialItem extends CustomRarityItem {
//    public static final Beneficial FALLEN_SOUL_CORE = new Beneficial(
//            Confluence.asResource("fallen_soul_core"),
//            everBeneficial -> true,
//            (id, player, everBeneficial, isRespawn) -> {
//                if (!isRespawn) {
//                    everBeneficial.changeFallenSoulCore();
//                    PlayerSpecialData data = PlayerSpecialData.of(player);
//                    boolean active = everBeneficial.getFallenSoulCore();
//                    data.setFallenSoulCoreActive(active);
//                    SoulStorage soulStorage = SoulStorage.of(player);
//
//                    // 关键：同步到客户端
//                    if (player instanceof ServerPlayer serverPlayer) {
//                        Confluence.NETWORK_HANDLER.sendToPlayer(//                                new SoulPacketS2C(
//                                        soulStorage.getMaxSoul(),
//                                        soulStorage.getCurrentSoul(),
//                                        active
//                                ));
//                    }
//                }
//            }
//    );

    private final PermanentUpgrade upgrade;
    private final @Nullable Supplier<SoundEvent> sound;

    public EverBeneficialItem(Properties properties, ModRarity rarity, PermanentUpgrade upgrade, @Nullable Supplier<SoundEvent> sound) {
        super(properties, rarity);
        this.upgrade = upgrade;
        this.sound = sound;
    }

    public EverBeneficialItem(ModRarity rarity, PermanentUpgrade upgrade, @Nullable Supplier<SoundEvent> sound) {
        this(new Properties(), rarity, upgrade, sound);
    }

    public EverBeneficialItem(ModRarity rarity, PermanentUpgrade upgrade) {
        this(rarity, upgrade, null);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player instanceof ServerPlayer serverPlayer) {
            if (!upgrade.tryUse(serverPlayer)) return InteractionResultHolder.fail(stack);
            CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            if (!player.hasInfiniteMaterials()) stack.shrink(1);
            if (sound != null)
                level.playSound(null, player.getX(), player.getY(), player.getZ(), sound.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
