package org.confluence.mod.common.event;

import org.confluence.mod.Confluence;
import org.confluence.mod.network.AskForSoftcorePacket;
import org.confluence.mod.network.TeamPacket;
import org.confluence.mod.network.c2s.*;
import org.confluence.mod.network.s2c.*;
import org.confluence.mod.network.task.ReplyAchievementsPacketC2S;
import org.confluence.mod.network.task.RequestAchievementsPacketS2C;
import org.mesdag.portlib.network.PortNetworkHandler;

public final class NetworkEvents {
    public static void init() {
        PortNetworkHandler handler = Confluence.NETWORK_HANDLER;

        // C2S
        handler.registerInGameC2S(ApplySelectionPacketC2S.class, ApplySelectionPacketC2S.ID, ApplySelectionPacketC2S.STREAM_CODEC);
        handler.registerInGameC2S(DyeMixPacketC2S.class, DyeMixPacketC2S.ID, DyeMixPacketC2S.STREAM_CODEC);
        handler.registerInGameC2S(EmptyTargetSweepPacketC2S.class, EmptyTargetSweepPacketC2S.ID, EmptyTargetSweepPacketC2S.STREAM_CODEC);
        handler.registerInGameC2S(FlailControlPacketC2S.class, FlailControlPacketC2S.ID, FlailControlPacketC2S.STREAM_CODEC);
        handler.registerInGameC2S(GiveBannerPacketC2S.class, GiveBannerPacketC2S.ID, GiveBannerPacketC2S.STREAM_CODEC);
        handler.registerInGameC2S(HookThrowingPacketC2S.class, HookThrowingPacketC2S.ID, HookThrowingPacketC2S.STREAM_CODEC);
        handler.registerInGameC2S(HouseSelectPacketC2S.class, HouseSelectPacketC2S.ID, HouseSelectPacketC2S.STREAM_CODEC);
        handler.registerInGameC2S(KeyRequestPacketC2S.class, KeyRequestPacketC2S.ID, KeyRequestPacketC2S.STREAM_CODEC);
        handler.registerInGameC2S(OpenMenuPacketC2S.class, OpenMenuPacketC2S.ID, OpenMenuPacketC2S.STREAM_CODEC);
        handler.registerInGameC2S(SellTradePacketC2S.class, SellTradePacketC2S.ID, SellTradePacketC2S.STREAM_CODEC);
        handler.registerInGameC2S(SpearAttackPacketC2S.class, SpearAttackPacketC2S.ID, SpearAttackPacketC2S.STREAM_CODEC);
        handler.registerInGameC2S(SwitchEffectEnabledPackedC2S.class, SwitchEffectEnabledPackedC2S.ID, SwitchEffectEnabledPackedC2S.STREAM_CODEC);
        handler.registerInGameC2S(SwordProjectilePacketC2S.class, SwordProjectilePacketC2S.ID, SwordProjectilePacketC2S.STREAM_CODEC);
        handler.registerInGameC2S(WormholeToPlayerPacketC2S.class, WormholeToPlayerPacketC2S.ID, WormholeToPlayerPacketC2S.STREAM_CODEC);
        handler.registerInGameC2S(ShootPacketC2S.class, ShootPacketC2S.ID, ShootPacketC2S.STREAM_CODEC);

        // S2C
        handler.registerInGameS2C(AchievementOffsetSyncPacketS2C.class, AchievementOffsetSyncPacketS2C.ID, AchievementOffsetSyncPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(AchievementsDataSyncPacketS2C.class, AchievementsDataSyncPacketS2C.ID, AchievementsDataSyncPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(AvailableHouseSelectPacketS2C.class, AvailableHouseSelectPacketS2C.ID, AvailableHouseSelectPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(BestiarySyncPacketS2C.class, BestiarySyncPacketS2C.ID, BestiarySyncPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(BrushingColorPacketS2C.class, BrushingColorPacketS2C.ID, BrushingColorPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(CompatibilitySyncPacketS2c.class, CompatibilitySyncPacketS2c.ID, CompatibilitySyncPacketS2c.STREAM_CODEC);
        handler.registerInGameS2C(DeathMotionPacketS2C.class, DeathMotionPacketS2C.ID, DeathMotionPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(DragonChargePlayerConfigPacketS2C.class, DragonChargePlayerConfigPacketS2C.ID, DragonChargePlayerConfigPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(DropletsSyncPacketS2C.class, DropletsSyncPacketS2C.ID, DropletsSyncPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(ExtraInventoryStackPacketS2C.class, ExtraInventoryStackPacketS2C.ID, ExtraInventoryStackPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(ExtraInventorySyncPacketS2C.class, ExtraInventorySyncPacketS2C.ID, ExtraInventorySyncPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(FishingPowerInfoPacketS2C.class, FishingPowerInfoPacketS2C.ID, FishingPowerInfoPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(FlushArmorSetBonusPacketS2C.class, FlushArmorSetBonusPacketS2C.ID, FlushArmorSetBonusPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(GameEventSyncPacketS2C.class, GameEventSyncPacketS2C.ID, GameEventSyncPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(GlobalCloakSyncPacketS2C.class, GlobalCloakSyncPacketS2C.ID, GlobalCloakSyncPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(GoblinArmyProgressPacketS2C.class, GoblinArmyProgressPacketS2C.ID, GoblinArmyProgressPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(KillBoardSyncPacketS2C.class, KillBoardSyncPacketS2C.ID, KillBoardSyncPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(LucyTheAxeDialogPacketS2C.class, LucyTheAxeDialogPacketS2C.ID, LucyTheAxeDialogPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(ManaPacketS2C.class, ManaPacketS2C.ID, ManaPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(MeteoriteLocationPacketS2C.class, MeteoriteLocationPacketS2C.ID, MeteoriteLocationPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(OpenSelectionsScreenPacketS2C.class, OpenSelectionsScreenPacketS2C.ID, OpenSelectionsScreenPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(PiggyBankTotalMoneyPacket.class, PiggyBankTotalMoneyPacket.ID, PiggyBankTotalMoneyPacket.STREAM_CODEC);
        handler.registerInGameS2C(PlayerDeathInfoPacketS2C.class, PlayerDeathInfoPacketS2C.ID, PlayerDeathInfoPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(RepeaterShootingPayloadS2C.class, RepeaterShootingPayloadS2C.ID, RepeaterShootingPayloadS2C.STREAM_CODEC);
        handler.registerInGameS2C(SecretFlagSyncPacketS2C.class, SecretFlagSyncPacketS2C.ID, SecretFlagSyncPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(StarPhasesPacketS2C.class, StarPhasesPacketS2C.ID, StarPhasesPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(SyncEnemyBannerEntriesPacketS2C.class, SyncEnemyBannerEntriesPacketS2C.ID, SyncEnemyBannerEntriesPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(TerraStyleExplosionPacketS2C.class, TerraStyleExplosionPacketS2C.ID, TerraStyleExplosionPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(VisibilityPacketS2C.class, VisibilityPacketS2C.ID, VisibilityPacketS2C.STREAM_CODEC);
        handler.registerInGameS2C(WindSpeedPacketS2C.class, WindSpeedPacketS2C.ID, WindSpeedPacketS2C.STREAM_CODEC);

        // Bidirectional
        handler.registerInGameBidirectional(TeamPacket.class, TeamPacket.ID, TeamPacket.STREAM_CODEC);
        handler.registerInGameBidirectional(AskForSoftcorePacket.class, AskForSoftcorePacket.ID, AskForSoftcorePacket.STREAM_CODEC);

        // Configuration
        handler.registerConfigurationTask(RequestAchievementsPacketS2C.class, RequestAchievementsPacketS2C.ID, RequestAchievementsPacketS2C.STREAM_CODEC);
        handler.registerConfigurationTask(ReplyAchievementsPacketC2S.class, ReplyAchievementsPacketC2S.ID, ReplyAchievementsPacketC2S.STREAM_CODEC);
    }
}
