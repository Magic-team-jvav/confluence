package org.confluence.terraentity.item;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.function.TriFunction;
import org.confluence.terraentity.TerraEntity;
import org.confluence.terraentity.client.gui.DebugScreen;
import org.confluence.terraentity.config.ServerConfig;
import org.confluence.terraentity.entity.ai.goal.behavior.BTRoot;
import org.confluence.terraentity.entity.ai.goal.behavior.webviewer.BTServer;
import org.confluence.terraentity.init.TEAttachments;
import org.confluence.terraentity.utils.TEUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;


public class DebugItem extends Item {
    public DebugItem(Properties properties) {
        super(properties);
    }

    public enum DebugMode implements TriFunction<Level, Player, InteractionHand, InteractionResultHolder<ItemStack>> {
        /// 行为树Web查看工具：右键带有行为树的生物会启动web服务器，打开web端口实时预览行为树。再次右键空白位置关闭服务器。右键另一个生物会绑定到新的生物。
        BT_WEB_VIEWER {
            @Override
            public InteractionResultHolder<ItemStack> apply(Level level, Player player, InteractionHand interactionHand) {
                if (!level.isClientSide) {
                    var ray = TEUtils.getEyeTraceHitResult(player, 100);
                    if (ray != null && ray.getEntity() instanceof Mob mob) {
                        for (var goal : mob.goalSelector.getAvailableGoals()) {
                            if (goal.getGoal() instanceof BTRoot root) {
                                if (!BTServer.isServerRunning()) {
                                    if (ServerLifecycleHooks.getCurrentServer() != null) {
                                        Optional<Resource> opt = ServerLifecycleHooks.getCurrentServer().getResourceManager()
                                                .getResource(TerraEntity.space("behaviorviewer/behavior_tree_viewer.html"));
                                        opt.ifPresent(r -> {
                                            try (InputStream inputStream = r.open()) {

                                                byte[] bytes = new byte[inputStream.available()];
                                                inputStream.read(bytes);
                                                BTServer.startServer(bytes);

                                                // for debug
//                                                BTServer.startServer(Files.readAllBytes(Paths.get("D:\\programingFiles\\java\\untitled3\\src\\main\\resources\\behavior_tree_viewer.html")));

                                                BTServer.updateBehaviorTree(root);
                                                BTServer.updateMob = mob;
                                                String text = "http://localhost:" + ServerConfig.BEHAVIOR_TREE_WEB_VIEWER_SERVER_PORT.getAsInt();
                                                player.sendSystemMessage(Component.literal("Behavior Tree Web Viewer Server Started at ")
                                                        .append(Component.literal(text).withStyle(style -> style
                                                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, text))
                                                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to jump"))))));

                                            } catch (IOException e) {
                                                TerraEntity.LOGGER.error("Error reading behavior tree viewer html file");
                                            } catch (Exception e) {
                                                TerraEntity.LOGGER.error("Error starting behavior tree viewer server");
                                            }
                                        });
                                    }

                                } else {
                                    BTServer.updateBehaviorTree(root);
                                    BTServer.updateMob = mob;
                                    player.sendSystemMessage(Component.literal("Behavior Tree Updated"));
                                }


                                return InteractionResultHolder.success(player.getItemInHand(interactionHand));
                            }
                        }
                    } else {
                        if (BTServer.isServerRunning()) {
                            player.sendSystemMessage(Component.literal("Behavior Tree Web Viewer Server Stopped"));
                            BTServer.stopServer();
                        } else {
                            System.out.println("Behavior Tree Web Viewer Server is not running");
                        }

                    }
                }
                return InteractionResultHolder.pass(player.getItemInHand(interactionHand));
            }
        },
        /// 热重载保留功能
        TEMP {
            @Override
            public InteractionResultHolder<ItemStack> apply(Level level, Player player, InteractionHand interactionHand) {
                return InteractionResultHolder.pass(player.getItemInHand(interactionHand));
            }
        }

    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!player.isShiftKeyDown()) {
            return player.getData(TEAttachments.UNSYNC).getDebugMode().apply(level, player, usedHand);
        }
        if (player.level().isClientSide && player.isShiftKeyDown()) {
            DebugScreen.setScreen();
        }
        return super.use(level, player, usedHand);
    }
}
