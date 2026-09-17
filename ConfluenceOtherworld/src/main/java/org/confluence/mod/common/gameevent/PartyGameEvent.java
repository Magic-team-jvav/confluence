package org.confluence.mod.common.gameevent;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.entity.npc.BaseNPC;
import org.confluence.mod.common.init.entity.NpcEntities;
import org.confluence.mod.util.OverworldUtils;

public enum PartyGameEvent implements GameEvent {
    INSTANCE;

    public static final ResourceKey<PartyGameEvent> KEY = GameEvent.createKey(Confluence.asResource("party"));
    private ServerLevel level;
    private boolean active;
    private boolean natural;
    private boolean requested;
    private boolean ending;
    private long checkedDay = -1;
    private int cooldownDays;

    @Override
    public void open(MinecraftServer server) {
        level = OverworldUtils.getLevel(server);
        requested = false;
        ending = false;
    }

    @Override
    public void close(MinecraftServer server) {
        level = null;
        requested = false;
    }

    @Override
    public void tick() {}

    @Override
    public boolean canStart() {
        if (level == null) return false;
        if (requested) return true;
        long day = Math.floorDiv(level.getDayTime(), 24000);
        if (!level.isDay() || Math.floorMod(level.getDayTime(), 24000) > 100 || checkedDay == day)
            return false;
        checkedDay = day;
        if (cooldownDays > 0) {
            cooldownDays--;
            return false;
        }
        int residents = 0;
        boolean partyGirl = false;
        for (var entity : level.getAllEntities()) {
            if (entity instanceof BaseNPC npc && npc.isAlive() && !npc.isTownPet()
                    && npc.getType() != NpcEntities.OLD_MAN.get() && npc.getType() != NpcEntities.SKELETON_MERCHANT.get()) {
                residents++;
                partyGirl |= npc.getType() == NpcEntities.PARTY_GIRL.get();
            }
        }
        return partyGirl && residents >= 5 && level.random.nextInt(10) == 0;
    }

    @Override
    public boolean canEnd() {
        return ending || level == null || !level.isDay();
    }

    @Override
    public void onStart() {
        natural = !requested;
        active = true;
        requested = false;
        ending = false;
        if (natural) cooldownDays = 5 + level.random.nextInt(6);
        level.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("message.confluence.party.started"), false);
    }

    @Override
    public void onEnd() {
        active = false;
        natural = false;
        ending = false;
        if (level != null)
            level.getServer().getPlayerList().broadcastSystemMessage(Component.translatable("message.confluence.party.ended"), false);
    }

    public boolean isNatural() {
        return active && natural;
    }

    @Override
    public boolean started() {
        return active;
    }

    @Override
    public boolean forceStart() {
        if (active || level == null) return false;
        requested = true;
        return true;
    }

    @Override
    public void forceEnd() {
        requested = false;
        ending = true;
    }

    @Override
    public void decode(CompoundTag tag) {
        active = tag.getBoolean("Started");
        natural = tag.getBoolean("Natural");
        checkedDay = tag.contains("CheckedDay") ? tag.getLong("CheckedDay") : -1;
        cooldownDays = Math.max(0, tag.getInt("CooldownDays"));
    }

    @Override
    public void encode(CompoundTag tag) {
        tag.putBoolean("Started", active);
        tag.putBoolean("Natural", natural);
        tag.putLong("CheckedDay", checkedDay);
        tag.putInt("CooldownDays", cooldownDays);
    }

    @Override
    public ResourceKey<PartyGameEvent> key() {
        return KEY;
    }

    @Override
    public boolean isNonEnvEvent() {
        return false;
    }
}
