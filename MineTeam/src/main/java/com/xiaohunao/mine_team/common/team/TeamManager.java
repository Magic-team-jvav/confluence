package com.xiaohunao.mine_team.common.team;

import com.google.common.collect.Maps;
import com.xiaohunao.mine_team.MineTeam;
import com.xiaohunao.mine_team.common.attachment.TeamAttachment;
import com.xiaohunao.mine_team.common.init.MTAttachmentTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

public class TeamManager extends SavedData {
    public static final StreamCodec<FriendlyByteBuf, TeamManager> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public TeamManager decode(FriendlyByteBuf byteBuf) {
            TeamManager manager = new TeamManager();
            int size = byteBuf.readVarInt();
            for (int i = 0; i < size; i++) {
                manager.taems.put(byteBuf.readUUID(), Team.STREAM_CODEC.decode(byteBuf));
            }
            size = byteBuf.readVarInt();
            for (int i = 0; i < size; i++) {
                DyeColor dyeColor = DyeColor.STREAM_CODEC.decode(byteBuf);
                Team team = Team.STREAM_CODEC.decode(byteBuf);
                manager.dyeColorTeam.put(dyeColor, team);
                manager.teamDyeColor.put(team, dyeColor);
            }
            return manager;
        }

        @Override
        public void encode(FriendlyByteBuf byteBuf, TeamManager manager) {
            byteBuf.writeVarInt(manager.taems.size());
            for (Map.Entry<UUID, Team> entry : manager.taems.entrySet()) {
                byteBuf.writeUUID(entry.getKey());
                Team.STREAM_CODEC.encode(byteBuf, entry.getValue());
            }
            byteBuf.writeVarInt(manager.dyeColorTeam.size());
            for (Map.Entry<DyeColor, Team> entry : manager.dyeColorTeam.entrySet()) {
                DyeColor.STREAM_CODEC.encode(byteBuf, entry.getKey());
                Team.STREAM_CODEC.encode(byteBuf, entry.getValue());
            }
        }
    };
    private static final String NAME = MineTeam.MODID + "_manager";
    private static final Logger LOGGING = LoggerFactory.getLogger(TeamManager.class);
    private static final TeamManager clientMonger = new TeamManager();

    private final Map<UUID, Team> taems = Maps.newHashMap();
    private final Map<DyeColor, Team> dyeColorTeam = Maps.newEnumMap(DyeColor.class);
    private final Map<Team, DyeColor> teamDyeColor = Maps.newHashMap(); // bi hash map 无法正常工作

    public static TeamManager of(Level level) {
        if (level.isClientSide) return clientMonger;
        TeamManager manager = ((ServerLevel) (level.dimension() == Level.OVERWORLD ? level : level.getServer().overworld()))
                .getDataStorage().computeIfAbsent(new Factory<>(TeamManager::new, (tag, provider) -> {
                    TeamManager manager1 = new TeamManager();
                    manager1.deserializeNBT(tag);
                    return manager1;
                }), NAME);
        if (manager.isTeamEmpty()) {
            for (DyeColor color : DyeColor.values()) {
                manager.createTeam(UUID.randomUUID(), color);
            }
            manager.setDirty();
        }
        return manager;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        CompoundTag taems = new CompoundTag();
        for (Map.Entry<UUID, Team> entry : this.taems.entrySet()) {
            taems.put(entry.getKey().toString(), Team.CODEC.encodeStart(NbtOps.INSTANCE, entry.getValue()).result().orElseGet(CompoundTag::new));
        }
        tag.put("taems", taems);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        CompoundTag taems = tag.getCompound("taems");
        for (String uid : taems.getAllKeys()) {
            UUID uuid = UUID.fromString(uid);
            Team team = Team.CODEC.parse(NbtOps.INSTANCE, taems.get(uid)).result().orElseGet(Team::new);
            this.taems.put(uuid, team);

            DyeColor dyeColor = DyeColor.byFireworkColor(team.getRGB());
            if (dyeColor != null) {
                this.dyeColorTeam.put(dyeColor, team);
                this.teamDyeColor.put(team, dyeColor);
            }
        }
    }

    public void copyFrom(TeamManager manager) {
        taems.putAll(manager.taems);
        dyeColorTeam.putAll(manager.dyeColorTeam);
        teamDyeColor.putAll(manager.teamDyeColor);
    }

    public boolean isTeamEmpty() {
        return taems.isEmpty();
    }

    public Team createTeam(UUID uuid, int textureDiffuseColor) {
        if (taems.containsKey(uuid)) {
            LOGGING.warn("Team with UUID {} already exists", uuid);
            return null;
        }
        Team team = new Team(uuid, textureDiffuseColor);
        setDirty();
        taems.put(uuid, team);
        return team;
    }

    public Team createTeam(UUID uuid, DyeColor dyeColor) {
        Team team = createTeam(uuid, dyeColor.getFireworkColor());
        dyeColorTeam.put(dyeColor, team);
        setDirty();
        return team;
    }

    public Team getTeam(UUID uuid) {
        return taems.get(uuid);
    }

    public Team getTeam(DyeColor dyeColor) {
        return dyeColorTeam.get(dyeColor);
    }

    public DyeColor getDyeColor(Team team) {
        return teamDyeColor.get(team);
    }

    public static Team getTeam(Entity entity) {
        if (entity.hasData(MTAttachmentTypes.TEAM)) {
            TeamManager manager = TeamManager.of(entity.level());
            return manager.taems.get(TeamAttachment.of(entity).getTeamUid());
        }
        return null;
    }
}
