package com.xiaohunao.enemybanner.gui;

import com.mojang.logging.LogUtils;
import com.xiaohunao.enemybanner.AttachmentTypeRegister;
import com.xiaohunao.enemybanner.BannerConfig;
import com.xiaohunao.enemybanner.BannerParameters;
import com.xiaohunao.enemybanner.items.ItemRegister;
import com.xiaohunao.enemybanner.items.SilksItem;
import com.xiaohunao.enemybanner.payloads.PlayerBannerCountPayload;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class BannerBoxMenu extends ItemCombinerMenu {
    public static final Logger LOGGER = LogUtils.getLogger();

    private Object2IntOpenHashMap<String> playerBannerCount;

    private Inventory playerInv;

    private String selectedKey;

    public BannerBoxMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        this(containerId, playerInv, ContainerLevelAccess.NULL);
    }

    public BannerBoxMenu(int containerId, Inventory playerInv, ContainerLevelAccess access) {
        super(Menus.BANNER_BOX_MENU.get(), containerId, playerInv, access);
        this.playerInv = playerInv;
        playerBannerCount = new Object2IntOpenHashMap<>();
        loadBannerMap();
    }

    @Override
    protected boolean mayPickup(@NotNull Player player, boolean hasStack) {
        return hasStack;
    }

    @Override
    protected void onTake(@NotNull Player player, @NotNull ItemStack itemStack) {
        ItemStack bannerInput = this.inputSlots.getItem(0);
        ItemStack silks = this.inputSlots.getItem(1);
        bannerInput.setCount(bannerInput.getCount() - 1);
        if (!silks.isEmpty()) {
            silks.setCount(silks.getCount() - 1);
        }
        BannerConfig.Banner banner = BannerConfig.getBanner(selectedKey);
        if (banner != null && playerBannerCount.getInt(banner.monsterId) >= banner.basicKills) {
            Object2IntOpenHashMap<String> tmpMap = new Object2IntOpenHashMap<>(playerBannerCount);
            tmpMap.put(banner.monsterId, playerBannerCount.getInt(banner.monsterId) - banner.basicKills);
            player.setData(AttachmentTypeRegister.PLAYER_BANNER_COUNT, tmpMap);
            playerBannerCount = tmpMap;
        }
        updateBannerMap();
        loadBannerMap();
        setSelected(selectedKey);
    }

    @Override
    protected boolean isValidBlock(@NotNull BlockState blockState) {
        return true;
    }


    @Override
    public void createResult() {
        if (selectedKey != null && getSlot(0).hasItem() && playerBannerCount.containsKey(selectedKey) && playerBannerCount.getInt(selectedKey) >= BannerConfig.getBanner(selectedKey).basicKills) {
            ItemStack stack = ItemRegister.ENEMY_BANNER.toStack();
            stack.set(BannerParameters.BANNER_DATA_COMPONENT, new BannerParameters(selectedKey, DyeColor.WHITE.getId(), getSilksId()));
            this.resultSlots.setItem(0, stack);
            this.broadcastChanges();
        } else {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        }
    }

    public void setSelected(String key) {
        BannerConfig.Banner banner = BannerConfig.getBanner(key);
        if (banner != null && playerBannerCount.getInt(banner.monsterId) >= banner.basicKills)
            this.selectedKey = key;
        else
            this.selectedKey = "";
        createResult();
    }

    public String getSilksId() {
        return (getSlot(1).hasItem() ? getSlot(1).getItem().getItem().toString() : ItemRegister.BASIC_SILKS.getRegisteredName()).split(":")[1];
    }

    @Override
    protected @NotNull ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(0, 11, 16, itemStack -> itemStack.getItem() instanceof BannerItem)
                .withSlot(1, 11, 54, itemStack -> itemStack.getItem() instanceof SilksItem)
                .withResultSlot(2, 149, 35)
                .build();
    }

    private void loadBannerMap() {
        playerBannerCount = player.getData(AttachmentTypeRegister.PLAYER_BANNER_COUNT);

        if (!player.level().isClientSide) {
            ServerPlayer serverPlayer = player.getServer().getPlayerList().getPlayer(player.getUUID());
            PacketDistributor.sendToPlayer(serverPlayer, new PlayerBannerCountPayload(playerBannerCount, ""));
        }
    }

    private void updateBannerMap() {
        if (player.level().isClientSide) {
            PacketDistributor.sendToServer(new PlayerBannerCountPayload(playerBannerCount, selectedKey));
        }
        playerBannerCount = player.getData(AttachmentTypeRegister.PLAYER_BANNER_COUNT);
    }

    public Object2IntOpenHashMap<String> getPlayerBannerCount() {
        return this.playerBannerCount;
    }
}
