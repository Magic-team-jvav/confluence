package org.confluence.mod.common.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.confluence.mod.Confluence;
import org.confluence.mod.integration.jei.RecipeTransferPacketC2S;
import org.confluence.mod.network.AskForSoftcorePacket;
import org.confluence.mod.network.TeamPacket;
import org.confluence.mod.network.c2s.*;
import org.confluence.mod.network.s2c.*;
import org.confluence.mod.network.task.ReplyAchievementsPacketC2S;
import org.confluence.mod.network.task.RequestAchievementsPacketS2C;

@EventBusSubscriber(modid = Confluence.MODID)
public final class NetworkEvents {
    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToClient(BrushingColorPacketS2C.TYPE, BrushingColorPacketS2C.STREAM_CODEC, BrushingColorPacketS2C::handle)
                .playToClient(DeathMotionPacketS2C.TYPE, DeathMotionPacketS2C.STREAM_CODEC, DeathMotionPacketS2C::handle)
                .playToClient(VisibilityPacketS2C.TYPE, VisibilityPacketS2C.STREAM_CODEC, VisibilityPacketS2C::handle)
                .playToClient(ExtraInventoryStackPacketS2C.TYPE, ExtraInventoryStackPacketS2C.STREAM_CODEC, ExtraInventoryStackPacketS2C::handle)
                .playToClient(ExtraInventorySyncPacketS2C.TYPE, ExtraInventorySyncPacketS2C.STREAM_CODEC, ExtraInventorySyncPacketS2C::handle)
                .playToClient(FishingPowerInfoPacketS2C.TYPE, FishingPowerInfoPacketS2C.STREAM_CODEC, FishingPowerInfoPacketS2C::handle)
                .playToClient(KillBoardSyncPacketS2C.TYPE, KillBoardSyncPacketS2C.STREAM_CODEC, KillBoardSyncPacketS2C::handle)
                .playToClient(ManaPacketS2C.TYPE, ManaPacketS2C.STREAM_CODEC, ManaPacketS2C::handle)
//                .playToClient(SoulPacketS2C.TYPE, SoulPacketS2C.STREAM_CODEC, SoulPacketS2C::handle)
                .playToClient(MeteoriteLocationPacketS2C.TYPE, MeteoriteLocationPacketS2C.STREAM_CODEC, MeteoriteLocationPacketS2C::handle)
                .playToClient(OpenSelectionsScreenPacketS2C.TYPE, OpenSelectionsScreenPacketS2C.STREAM_CODEC, OpenSelectionsScreenPacketS2C::handle)
                .playToClient(PlayerDeathInfoPacketS2C.TYPE, PlayerDeathInfoPacketS2C.STREAM_CODEC, PlayerDeathInfoPacketS2C::handle)
                .playToClient(SecretFlagSyncPacketS2C.TYPE, SecretFlagSyncPacketS2C.STREAM_CODEC, SecretFlagSyncPacketS2C::handle)
                .playToClient(StarPhasesPacketS2C.TYPE, StarPhasesPacketS2C.STREAM_CODEC, StarPhasesPacketS2C::handle)
                .playToClient(WindSpeedPacketS2C.TYPE, WindSpeedPacketS2C.STREAM_CODEC, WindSpeedPacketS2C::handle)
                .playToClient(AchievementOffsetSyncPacketS2C.TYPE, AchievementOffsetSyncPacketS2C.STREAM_CODEC, AchievementOffsetSyncPacketS2C::handle)
                .playToClient(RepeaterShootingPayloadS2C.TYPE, RepeaterShootingPayloadS2C.STREAM_CODEC, RepeaterShootingPayloadS2C::handle)
                .playToClient(CompatibilitySyncPacketS2c.TYPE, CompatibilitySyncPacketS2c.STREAM_CODEC, CompatibilitySyncPacketS2c::handle)
                .playToClient(PiggyBankTotalMoneyPacket.TYPE, PiggyBankTotalMoneyPacket.STREAM_CODEC, PiggyBankTotalMoneyPacket::handle)
                .playToClient(DropletsSyncPacketS2C.TYPE, DropletsSyncPacketS2C.STREAM_CODEC, DropletsSyncPacketS2C::handle)
                .playToClient(BestiarySyncPacketS2C.TYPE, BestiarySyncPacketS2C.STREAM_CODEC, BestiarySyncPacketS2C::handle)
                .playToClient(AvailableHouseSelectPacketS2C.TYPE, AvailableHouseSelectPacketS2C.STREAM_CODEC, AvailableHouseSelectPacketS2C::handle)
                .playToClient(TerraStyleExplosionPacketS2C.TYPE, TerraStyleExplosionPacketS2C.STREAM_CODEC, TerraStyleExplosionPacketS2C::handle)
                .playToClient(FlushArmorSetBonusPacketS2C.TYPE, FlushArmorSetBonusPacketS2C.STREAM_CODEC, FlushArmorSetBonusPacketS2C::handle)
                .playToClient(GlobalCloakSyncPacketS2C.TYPE, GlobalCloakSyncPacketS2C.STREAM_CODEC, GlobalCloakSyncPacketS2C::handle)
                .playToClient(LucyTheAxeDialogPacketS2C.TYPE, LucyTheAxeDialogPacketS2C.STREAM_CODEC, LucyTheAxeDialogPacketS2C::handle)
                .playToClient(GameEventSyncPacketS2C.TYPE, GameEventSyncPacketS2C.STREAM_CODEC, GameEventSyncPacketS2C::handle)
                .playToClient(GoblinArmyProgressPacketS2C.TYPE, GoblinArmyProgressPacketS2C.STREAM_CODEC, GoblinArmyProgressPacketS2C::handle)
                .playToClient(SyncEnemyBannerEntriesPacketS2C.TYPE, SyncEnemyBannerEntriesPacketS2C.STREAM_CODEC, SyncEnemyBannerEntriesPacketS2C::handle)
                .playToClient(AchievementsDataSyncPacketS2C.TYPE, AchievementsDataSyncPacketS2C.STREAM_CODEC, AchievementsDataSyncPacketS2C::handle)
                .playToClient(DragonChargePlayerConfigPacketS2C.TYPE, DragonChargePlayerConfigPacketS2C.STREAM_CODEC, DragonChargePlayerConfigPacketS2C::handle)

                .playToServer(ApplySelectionPacketC2S.TYPE, ApplySelectionPacketC2S.STREAM_CODEC, ApplySelectionPacketC2S::handle)
                .playToServer(HookThrowingPacketC2S.TYPE, HookThrowingPacketC2S.STREAM_CODEC, HookThrowingPacketC2S::handle)
                .playToServer(KeyRequestPacketC2S.TYPE, KeyRequestPacketC2S.STREAM_CODEC, KeyRequestPacketC2S::handle)
                .playToServer(OpenMenuPacketC2S.TYPE, OpenMenuPacketC2S.STREAM_CODEC, OpenMenuPacketC2S::handle)
                .playToServer(WormholeToPlayerPacketC2S.TYPE, WormholeToPlayerPacketC2S.STREAM_CODEC, WormholeToPlayerPacketC2S::handle)
                .playToServer(SellTradePacketC2S.TYPE, SellTradePacketC2S.STREAM_CODEC, SellTradePacketC2S::handle)
                .playToServer(RecipeTransferPacketC2S.TYPE, RecipeTransferPacketC2S.STREAM_CODEC, RecipeTransferPacketC2S::handle)
                .playToServer(SpearAttackPacketC2S.TYPE, SpearAttackPacketC2S.STREAM_CODEC, SpearAttackPacketC2S::handle)
                .playToServer(SwitchEffectEnabledPackedC2S.TYPE, SwitchEffectEnabledPackedC2S.STREAM_CODEC, SwitchEffectEnabledPackedC2S::handle)
                .playToServer(DyeMixPacketC2S.TYPE, DyeMixPacketC2S.STREAM_CODEC, DyeMixPacketC2S::handle)
                .playToServer(HouseSelectPacketC2S.TYPE, HouseSelectPacketC2S.STREAM_CODEC, HouseSelectPacketC2S::handle)
                .playToServer(EmptyTargetSweepPacketC2S.TYPE, EmptyTargetSweepPacketC2S.STREAM_CODEC, EmptyTargetSweepPacketC2S::handle)
                .playToServer(SwordProjectilePacketC2S.TYPE, SwordProjectilePacketC2S.STREAM_CODEC, SwordProjectilePacketC2S::handle)
                .playToServer(FlailControlPacketC2S.TYPE, FlailControlPacketC2S.STREAM_CODEC, FlailControlPacketC2S::handle)
                .playToServer(GiveBannerPacketC2S.TYPE, GiveBannerPacketC2S.STREAM_CODEC, GiveBannerPacketC2S::handle)

                .playBidirectional(TeamPacket.TYPE, TeamPacket.STREAM_CODEC, TeamPacket::handle)
                .playBidirectional(AskForSoftcorePacket.TYPE, AskForSoftcorePacket.STREAM_CODEC, AskForSoftcorePacket::handle)

                .configurationToClient(RequestAchievementsPacketS2C.TYPE, RequestAchievementsPacketS2C.STREAM_CODEC, RequestAchievementsPacketS2C::handle)
                .configurationToServer(ReplyAchievementsPacketC2S.TYPE, ReplyAchievementsPacketC2S.STREAM_CODEC, ReplyAchievementsPacketC2S::handle)
        ;
    }
}
