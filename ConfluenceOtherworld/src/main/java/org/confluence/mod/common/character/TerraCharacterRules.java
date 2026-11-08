package org.confluence.mod.common.character;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import org.confluence.mod.network.s2c.CharacterDiedPacketS2C;
import org.confluence.mod.network.s2c.CharacterNamePacketS2C;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/// 泰拉式“角色”在服务端的部分：客户端进入世界时发来所选角色（名字、难度），存在玩家的 PlayerPersisted 里。
/// - 名字：头顶、聊天、玩家列表显示角色名（账号名照旧用于权限、存档、指令）。
/// - 难度：软核死亡只掉一部分钱、保留物品；中核掉落物品；硬核死亡后永久死亡（在这个世界变成旁观者，客户端把角色标为已死）。
///   实现方式：玩家死亡和复活期间，keepInventory 规则对这个玩家按难度取值（GameRulesCharacterMixin），
///   这样原版、Curios 和本模组自己的掉落逻辑都会照难度处理。
/// 没有选角色（原版界面模式、直接连接等）的玩家一切照旧。
public final class TerraCharacterRules {
    public static final byte NONE = -1;
    public static final byte SOFTCORE = 0;
    public static final byte MEDIUMCORE = 1;
    public static final byte HARDCORE = 2;
    public static final int MAX_NAME_LENGTH = 24;
    private static final String KEY = "confluence_character";
    /// 正在死亡/复活结算的玩家的难度（只在服务端线程上）
    private static final ThreadLocal<Byte> SCOPE = ThreadLocal.withInitial(() -> NONE);
    /// 客户端：其他玩家（和自己）的角色名
    public static final Map<UUID, String> CLIENT_NAMES = new ConcurrentHashMap<>();

    private TerraCharacterRules() {}

    // ---------- 数据 ----------
    private static CompoundTag data(Player player) {
        CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);
        CompoundTag tag = persisted.getCompound(KEY);
        persisted.put(KEY, tag);
        return tag;
    }

    public static boolean hasCharacter(Player player) {
        return !data(player).getString("id").isEmpty();
    }

    public static byte difficulty(Player player) {
        CompoundTag tag = data(player);
        return tag.getString("id").isEmpty() ? NONE : tag.getByte("difficulty");
    }

    public static String name(Player player) {
        String name = data(player).getString("name");
        return name.isEmpty() ? null : name;
    }

    /// 名字只留可见字符，去掉格式代码
    public static String sanitize(String name) {
        String clean = name.replace("§", "").strip();
        StringBuilder builder = new StringBuilder();
        clean.codePoints().filter(c -> !Character.isISOControl(c)).forEach(builder::appendCodePoint);
        String result = builder.toString();
        return result.length() > MAX_NAME_LENGTH ? result.substring(0, MAX_NAME_LENGTH) : result;
    }

    /// 客户端发来所选角色
    public static void apply(ServerPlayer player, String id, String rawName, byte difficulty) {
        String name = sanitize(rawName);
        if (id.isEmpty() || id.length() > 64 || name.isEmpty() || difficulty < SOFTCORE || difficulty > HARDCORE) return;
        CompoundTag tag = data(player);
        // 在这个世界死去的硬核角色换成了另一个角色：恢复到默认游戏模式
        if (tag.getBoolean("dead") && !id.equals(tag.getString("id")) && player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
            player.setGameMode(player.server.getDefaultGameType());
            tag.remove("dead");
        }
        if (!id.equals(tag.getString("id"))) tag.remove("dead");
        tag.putString("id", id);
        tag.putString("name", name);
        tag.putByte("difficulty", difficulty);
        refreshName(player);
        // 硬核角色在这个世界已经死过：仍是旁观者
        if (tag.getBoolean("dead")) CharacterDiedPacketS2C.send(player, id);
    }

    private static void refreshName(ServerPlayer player) {
        player.refreshDisplayName();
        player.refreshTabListName();
        CharacterNamePacketS2C.sendToAll(player.server, player.getUUID(), name(player));
    }

    /// 新玩家进来：告诉他在线玩家的角色名
    public static void onLoggedIn(ServerPlayer player) {
        for (ServerPlayer other : player.server.getPlayerList().getPlayers()) {
            String name = name(other);
            if (name != null) CharacterNamePacketS2C.send(player, other.getUUID(), name);
        }
    }

    public static Component displayName(Player player) {
        String name = player.level().isClientSide ? CLIENT_NAMES.get(player.getUUID()) : name(player);
        return name == null ? null : Component.literal(name);
    }

    // ---------- 死亡与复活 ----------
    public static void enter(Player player) {
        SCOPE.set(difficulty(player));
    }

    public static void exit() {
        SCOPE.set(NONE);
    }

    /// 结算期间 keepInventory 的取值：软核保留物品，中核/硬核掉落；没有角色时不改
    public static Boolean keepInventoryOverride() {
        byte difficulty = SCOPE.get();
        if (difficulty == NONE) return null;
        return difficulty == SOFTCORE;
    }

    /// 死亡结算之后（未被取消）：硬核角色永久死亡
    public static void onDeath(ServerPlayer player) {
        if (difficulty(player) != HARDCORE) return;
        CompoundTag tag = data(player);
        tag.putBoolean("dead", true);
        CharacterDiedPacketS2C.send(player, tag.getString("id"));
    }

    public static void onRespawn(ServerPlayer player) {
        if (difficulty(player) == HARDCORE && data(player).getBoolean("dead")) {
            player.setGameMode(GameType.SPECTATOR);
        }
    }
}
