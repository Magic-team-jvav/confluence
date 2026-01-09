package org.confluence.mod.client.handler;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.confluence.mod.client.renderer.item.LucyTheAxeDialogRenderer;
import org.confluence.mod.common.data.LucyTheAxeDialogCategory;
import org.confluence.mod.common.init.ModSoundEvents;
import org.confluence.mod.network.s2c.LucyTheAxeDialogPacketS2C;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

public final class LucyTheAxeHandler {
    private static final Object2IntMap<ResourceLocation> cycleMap = new Object2IntOpenHashMap<>();
    private static final Queue<Stack> queue = new ArrayDeque<>();
    private static int cooldown;

    public static void handlePacket(LucyTheAxeDialogPacketS2C packet, Player player) {
        if (cooldown > 0) return;
        Entity entity = player.level().getEntity(packet.senderId());
        if (entity == null) return;
        ResourceLocation key = packet.categoryKey();
        LucyTheAxeDialogCategory category = packet.category();
        int index;
        if (category.cycle()) {
            index = cycleMap.getInt(key);
            if (index == category.entries()) {
                index = 0;
            }
            cycleMap.put(key, index + 1);
        } else {
            index = player.getRandom().nextInt(0, category.entries());
        }

        Component dialog = Component.translatable("dialogs." + key.getNamespace() + ".lucy_the_axe." + key.getPath() + "." + index);
        if (entity == player) {
            LucyTheAxeDialogRenderer.dialog = dialog;
            player.level().playSound(player, player.getX(), player.getEyeY(), player.getZ(), ModSoundEvents.LUCYAXE_TALK.get(), SoundSource.VOICE, 1, 1);
        } else {
            entity.playSound(ModSoundEvents.LUCYAXE_TALK.get());
        }
        float x = (float) entity.getX();
        float y = (float) (entity.getY() + entity.getBbHeight() + 0.5);
        float z = (float) entity.getZ();
        if (LucyTheAxeDialogCategory.THROWN_ON_THE_GROUND.equals(key)) {
            x += (float) entity.getForward().x * 3;
            z += (float) entity.getForward().z * 3;
        }
        queue.add(new Stack(packet.senderId(), dialog, x, y, z));
        cooldown = 60;
    }

    public static void reset() {
        queue.clear();
        LucyTheAxeDialogRenderer.reset();
    }

    public static void handle(int id) {
        if (cooldown > 0) --cooldown;
        if (queue.isEmpty()) return;
        Iterator<Stack> iterator = queue.iterator();
        while (iterator.hasNext()) {
            Stack stack = iterator.next();
            if (stack.remainTime <= 0) {
                if (stack.senderId == id) {
                    LucyTheAxeDialogRenderer.reset();
                }
                iterator.remove();
            } else {
                --stack.remainTime;
            }
        }
    }

    public static Queue<Stack> getStacks() {
        return queue;
    }

    public static class Stack {
        final int senderId;
        public final Component dialog;
        public final float x;
        public final float y;
        public final float z;
        int remainTime = 60;

        Stack(int senderId, Component dialog, float x, float y, float z) {
            this.senderId = senderId;
            this.dialog = dialog;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
