package org.confluence.mod.common.summoner.attachment;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.ConfluenceMagicLib;
import org.confluence.mod.common.summoner.LyraStreamCodecs;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntity;
import org.confluence.mod.common.summoner.attachmentEntity.AttachmentEntityType;
import org.confluence.mod.common.summoner.attachmentEntity.PathNode;
import org.confluence.mod.common.summoner.minion.ICarryMinion;
import org.confluence.mod.common.summoner.minion.Minion;
import org.confluence.mod.common.summoner.minion.MinionSlotType;
import org.confluence.mod.common.summoner.register.SummonerAttachmentTypes;
import org.confluence.mod.common.summoner.register.SummonerRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mesdag.portlib.attachment.IPortAttachmentHolder;
import org.mesdag.portlib.attachment.PortAttachmentSyncHandler;
import org.mesdag.portlib.network.PortConnectionType;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 统一的世界级附件实体数据附件。
 * <p>
 * 使用 AttachmentEntityType 分组存储实体。
 * 移除通过 setRemove() 标记完成，添加通过延迟队列在 tick 后统一处理。
 * </p>
 */
public class AttachmentEntityData {

    private final Map<AttachmentEntityType<?>, List<AttachmentEntity>> pendingAdd = new HashMap<>();
    private final Map<AttachmentEntityType<?>, List<AttachmentEntity>> groups = new HashMap<>();
    private final List<AttachmentEntity> renderCache = new ArrayList<>();
    private final AtomicReference<List<byte[]>> pendingPayloads = new AtomicReference<>(List.of());
    private final Player owner;
    private Level level = null;
    private boolean changed = false;
    private boolean hasCarryMinion = false;

    public AttachmentEntityData(IPortAttachmentHolder owner) {
        if (owner instanceof Player player) {
            this.owner = player;
        } else {
            throw new IllegalArgumentException(owner + " is not a valid AttachmentEntityData");
        }
    }

    public void tick(Player player) {
        if (isClientSide()) {
            applyPendingSync();
            renderCache.clear();
            if (isRunning()) {
                for (List<AttachmentEntity> list : groups.values()) {
                    for (AttachmentEntity entity : list) {
                        entity.tickCurrentPathNode();
                        renderCache.add(entity);
                    }
                }
            }
        } else {
            if (isRunning()) {
                hasCarryMinion = false;
                boolean levelChange = level != null && level != player.level();
                level = player.level();
                Map<Long, List<Minion>> sameCache = new HashMap<>();
                // tick实体
                for (List<AttachmentEntity> list : groups.values()) {
                    for (AttachmentEntity entity : list) {
                        if (entity instanceof Minion minion) {
                            List<Minion> minionList = sameCache.computeIfAbsent(minion.getSameHash(), key -> new ArrayList<>());
                            minion.setOrder(minionList.size());
                            minionList.add(minion);
                            for (Minion minion1 : minionList) {
                                minion1.setSameSize(list.size());
                            }
                        }
                        if (levelChange) {
                            entity.onLevelChange();
                        }
                        entity.tick();
                        entity.tickCurrentPathNode();
                    }
                }
                // 将待添加队列合并到主分组
                if (!pendingAdd.isEmpty()) {
                    for (Map.Entry<AttachmentEntityType<?>, List<AttachmentEntity>> entry : pendingAdd.entrySet()) {
                        List<AttachmentEntity> entities = groups.computeIfAbsent(entry.getKey(), key -> new ArrayList<>());
                        for (AttachmentEntity attachmentEntity : entry.getValue()) {
                            if (!attachmentEntity.getCurrentPathNode().pos().equals(Vec3.ZERO)) {
                                attachmentEntity.setOwner(player);
                                entities.add(attachmentEntity);
                            }
                        }
                    }
                    pendingAdd.clear();
                    changed = true;
                }
                // 检查槽位溢出
                Map<MinionSlotType, Integer> limits = new HashMap<>();
                for (Minion minion : get(Minion.class)) {
                    Player owner = minion.getOwner();
                    int limit = limits.computeIfAbsent(minion.getSlotType(), slotType -> {
                        AttributeInstance instance = switch (slotType) {
                            case Minion -> owner.getAttribute(ConfluenceMagicLib.MINION_CAPACITY);
                            case Sentry -> owner.getAttribute(ConfluenceMagicLib.SENTRY_CAPACITY);
                            case None -> null;
                        };
                        if (instance != null) {
                            return (int) instance.getValue();
                        }
                        return 0;
                    });
                    int slotCost = minion.getSlotCost();
                    if (slotCost > 0) {
                        if (limit >= slotCost) {
                            limits.put(minion.getSlotType(), limit - slotCost);
                        } else {
                            minion.setRemove();
                        }
                    }
                }
                // 清理所有分组中标记移除的实体
                groups.values().removeIf(list -> {
                    while (true) {
                        list.removeIf(entity -> {
                            if (!entity.isAlive()) {
                                entity.setRemove();
                            }
                            if (entity.isRemove()) {
                                entity.onRemove();
                                changed = true;
                                return true;
                            }
                            return false;
                        });
                        if (list.isEmpty()) {
                            break;
                        }
                        if (list.stream().noneMatch(AttachmentEntity::isRemove)) {
                            break;
                        }
                    }
                    return list.isEmpty();
                });
                if (!groups.isEmpty() || changed) {
                    changed = false;
                    player.syncData(SummonerAttachmentTypes.ENTITY_DATA.get());
                }
            }
        }
    }

    public Level getLevel() {
        return owner.level();
    }

    public boolean isClientSide() {
        return owner.level().isClientSide();
    }

    public boolean isRunning() {
        if (isClientSide()) {
            return !groups.isEmpty();
        } else {
            return !groups.isEmpty() || !pendingAdd.isEmpty() || changed;
        }
    }

    public void add(AttachmentEntity entity) {
        pendingAdd.computeIfAbsent(entity.getType(), key -> new ArrayList<>()).add(entity);
    }

    @SuppressWarnings("unchecked")
    public <T extends AttachmentEntity> List<T> get(AttachmentEntityType<T> attachmentEntityType) {
        List<T> result = new ArrayList<>();
        groups.getOrDefault(attachmentEntityType, new ArrayList<>())
                .stream()
                .filter(entity -> !entity.isRemove())
                .map(entity -> (T) entity)
                .forEach(result::add);
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T extends AttachmentEntity> List<T> get(Class<T> classType) {
        List<T> result = new ArrayList<>();
        groups.values().stream()
                .flatMap(Collection::stream)
                .filter(entity -> !entity.isRemove())
                .filter(classType::isInstance)
                .map(entity -> (T) entity)
                .forEach(result::add);
        return result;
    }

    public void remove(AttachmentEntityType<?> entityType) {
        getGroups().getOrDefault(entityType, new ArrayList<>()).forEach(AttachmentEntity::setRemove);
    }

    public Map<AttachmentEntityType<?>, List<AttachmentEntity>> getGroups() {
        return groups;
    }

    public List<AttachmentEntity> getRenderCache() {
        return renderCache;
    }

    public boolean isHasCarryMinion() {
        return hasCarryMinion;
    }

    public void setHasCarryMinion(boolean hasCarryMinion) {
        this.hasCarryMinion = hasCarryMinion;
    }

    /**
     * 网络包到达时只暂存载荷，在客户端 tick 起点执行真实解码。
     */
    private void applyPendingSync() {
        Level level = getLevel();
        List<byte[]> snapshot = pendingPayloads.getAndSet(List.of());
        for (byte[] payload : snapshot) {
            PortRegistryFriendlyByteBuf buf = new PortRegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload), level.registryAccess(), PortConnectionType.MODDED);
            // 保留现有实体的缓存引用
            Map<UUID, AttachmentEntity> existing = new HashMap<>();
            groups.values().forEach(list -> list.forEach(entity -> existing.put(entity.getUuid(), entity)));
            // 清空分组
            groups.clear();
            // 读取 AttachmentEntityType → 实体列表
            int typeCount = buf.readVarInt();
            for (int i = 0; i < typeCount; i++) {
                AttachmentEntityType<?> entityType = SummonerRegistries.ATTACHMENT_ENTITY_TYPES.get(buf.readResourceLocation());
                assert entityType != null;
                List<AttachmentEntity> list = groups.computeIfAbsent(entityType, k -> new ArrayList<>());
                int listSize = buf.readVarInt();
                for (int k = 0; k < listSize; k++) {
                    UUID uuid = buf.readUUID();
                    AttachmentEntity entity = existing.get(uuid);
                    if (entity == null) {
                        entity = entityType.factory().get();
                        entity.setUuid(uuid);
                    }
                    entity.setOwner(owner);
                    entity.getSyncFieldDispatcher().decode(buf, level);
                    if (!buf.readBoolean()) {
                        PathNode pathNode = LyraStreamCodecs.PATH_NODE.decode(buf);
                        ArrayList<PathNode> historyNodes = entity.getHistoryNodes();
                        historyNodes.clear();
                        historyNodes.add(pathNode);
                        historyNodes.add(pathNode);
                    }
                    list.add(entity);
                }
            }
        }
    }

    // ===================== 网络同步 =====================
    public static final class SyncHandler implements PortAttachmentSyncHandler<AttachmentEntityData> {

        @Override
        public void write(PortRegistryFriendlyByteBuf buf, AttachmentEntityData data, boolean initialSync) {
            // 写入 AttachmentEntityType → 实体列表的结构
            buf.writeVarInt(data.groups.size());
            for (Map.Entry<AttachmentEntityType<?>, List<AttachmentEntity>> entityEntry : data.groups.entrySet()) {
                buf.writeResourceLocation(entityEntry.getKey().location());
                List<AttachmentEntity> list = entityEntry.getValue();
                buf.writeVarInt(list.size());
                for (AttachmentEntity entity : list) {
                    buf.writeUUID(entity.getUuid());
                    entity.getSyncFieldDispatcher().encode(buf, entity.getLevel(), initialSync);
                    buf.writeBoolean(entity.isClientInit());
                    if (!entity.isClientInit()) {
                        entity.setClientInit(true);
                        LyraStreamCodecs.PATH_NODE.encode(buf, entity.getHistoryNodes().get(0));
                    }
                }
            }
        }

        @Override
        public AttachmentEntityData read(@NotNull IPortAttachmentHolder holder, @NotNull PortRegistryFriendlyByteBuf buf, @Nullable AttachmentEntityData oldData) {
            AttachmentEntityData data = oldData != null ? oldData : new AttachmentEntityData(holder);
            ByteBuf copy = buf.copy();
            byte[] payload = new byte[copy.readableBytes()];
            copy.readBytes(payload);
            if (payload.length == 0) {
                return data;
            }
            data.pendingPayloads.updateAndGet(payloads -> {
                List<byte[]> updated = new ArrayList<>(payloads.size() + 1);
                updated.addAll(payloads);
                updated.add(payload);
                return updated;
            });
            return data;
        }
    }
}
