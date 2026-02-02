package org.confluence.terraentity.entity.ai.goal.behavior.webviewer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.minecraft.world.entity.Mob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.confluence.terraentity.config.ServerConfig;
import org.confluence.terraentity.entity.ai.goal.behavior.BTNode;
import org.confluence.terraentity.entity.ai.goal.behavior.BTRoot;
import org.confluence.terraentity.entity.ai.goal.behavior.composite.CompositeNode;
import org.confluence.terraentity.entity.ai.goal.behavior.composite.SelectorNode;
import org.confluence.terraentity.entity.ai.goal.behavior.composite.SequenceNode;
import org.confluence.terraentity.entity.ai.goal.behavior.decoration.DecorationNode;
import org.confluence.terraentity.entity.ai.goal.behavior.decoration.RepeaterNode;
import org.confluence.terraentity.entity.ai.goal.behavior.leaf.WaitAction;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class BTServer {
    private static final AtomicReference<String> behaviorTreeJson = new AtomicReference<>("{}");
    private static final Gson gson = new Gson();
    private static HttpServer server;
    private static ExecutorService executor;
    private static ExecutorService updateExecutor;
    private static final Logger log = LogManager.getLogger(BTServer.class);

    // 用于停止服务器的信号量
    private static volatile boolean isRunning = false;

    public static Mob updateMob = null;

    public static void startServer(byte[] html) throws Exception {
        if (isRunning) {
            log.info("服务器已经在运行中");
            return;
        }

        // 创建服务器，设置较大的队列长度
        server = HttpServer.create(new InetSocketAddress(ServerConfig.BEHAVIOR_TREE_WEB_VIEWER_SERVER_PORT.getAsInt()), 100);

        // 创建线程池，避免阻塞
        executor = Executors.newFixedThreadPool(5);
        updateExecutor = Executors.newSingleThreadExecutor();
        server.setExecutor(executor);

        // 行为树API - 使用高效的handler
        server.createContext("/behaviorTree", new FastJsonHandler());

        // 主页
        server.createContext("/", exchange -> {
            try {
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, html.length);
                exchange.getResponseBody().write(html);
            } finally {
                exchange.close();
            }
        });

        // 健康检查
        server.createContext("/health", exchange -> {
            try (exchange) {
                String response = "{\"status\":\"ok\",\"timestamp\":" + System.currentTimeMillis() + "}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);
            }
        });

        // 停止服务器端点（可选）
        server.createContext("/stop", exchange -> {
            try (exchange) {
                String response = "{\"message\":\"正在停止服务器...\"}";
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(200, bytes.length);
                exchange.getResponseBody().write(bytes);

                // 在新线程中停止服务器，避免阻塞响应
                new Thread(() -> {
                    try {
                        Thread.sleep(100); // 给响应一点时间
                        stopServer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

            }
        });

        server.start();
        isRunning = true;
        log.info("Behavior Tree Web Viewer 服务器启动: http://localhost:" + ServerConfig.BEHAVIOR_TREE_WEB_VIEWER_SERVER_PORT.getAsInt());
    }

    /**
     * 停止服务器
     */
    public static void stopServer() {
        if (!isRunning) {
            log.info("服务器未在运行");
            return;
        }

        log.info("正在停止服务器...");

        // 关闭更新执行器
        if (updateExecutor != null) {
            updateExecutor.shutdown();
            try {
                if (!updateExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    updateExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                updateExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        // 关闭HTTP服务器
        if (server != null) {
            server.stop(0); // 0表示立即停止
        }

        // 关闭线程池
        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        isRunning = false;
        log.info("服务器已停止");
    }

    /**
     * 优雅停止服务器
     * @param delaySeconds 延迟停止的秒数
     */
    public static void stopServerGracefully(int delaySeconds) {
        if (!isRunning) {
            log.info("服务器未在运行");
            return;
        }

        log.info("将在 {} 秒后停止服务器...", delaySeconds);

        new Thread(() -> {
            try {
                Thread.sleep(delaySeconds * 1000L);
                stopServer();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /**
     * 重启服务器
     */
    public static void restartServer(byte[] html) throws Exception {
        stopServer();
        Thread.sleep(1000); // 等待1秒确保完全停止
        startServer(html);
    }

    /**
     * 获取服务器状态
     */
    public static boolean isServerRunning() {
        return isRunning;
    }

    // 专门处理JSON请求的Handler
    static class FastJsonHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 检查服务器是否正在运行
            if (!isRunning) {
                try {
                    String response = "{\"error\":\"服务器已停止\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(503, bytes.length); // 503 Service Unavailable
                    exchange.getResponseBody().write(bytes);
                } finally {
                    exchange.close();
                }
                return;
            }

            // 立即设置响应头，准备发送数据
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Cache-Control", "no-cache");

            // 获取当前JSON数据（原子操作，线程安全）
            String json = behaviorTreeJson.get();
            byte[] responseBytes = json.getBytes(StandardCharsets.UTF_8);

            // 立即发送响应
            exchange.sendResponseHeaders(200, responseBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }

            exchange.close();

            // 记录日志
//            log.info("请求处理完成 - {}", System.currentTimeMillis());
        }
    }

    public static void updateBehaviorTree(BTNode node) {
        if (!isRunning) {
            log.info("服务器已停止，忽略更新");
            return;
        }

        // 在后台线程中序列化
        updateExecutor.submit(() -> {
            try {
                String json = BTServer.serializeForDisplay(node);
                behaviorTreeJson.set(json);
//                log.info("行为树更新完成 - {}", System.currentTimeMillis());
            } catch (Exception e) {
                log.error("更新行为树失败: {}", e.getMessage());
            }
        });
    }

    // 简化的序列化方法，只生成前端需要的数据
    public static String serializeForDisplay(BTNode node) {
        if (node == null) return "null";

        try {
            JsonObject json = new JsonObject();

            // 基本属性
            json.addProperty("_type", getSimpleTypeName(node));
            json.addProperty("status", node.getStatus().name());
            if(node.getDesc() != null) {
                json.addProperty("desc", node.getDesc());
            }

            // 特定属性
            addNodeSpecificProperties(json, node);

            // 子节点
            addChildren(json, node);

            return gson.toJson(json);

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\":\"序列化失败\"}";
        }
    }

    private static String getSimpleTypeName(BTNode node) {
        // 直接使用类名的简单版本
        String className = node.getClass().getSimpleName();
        if (className.endsWith("Node")) {
            return className.substring(0, className.length() - 4);
        }
        return className;
    }

    private static void addNodeSpecificProperties(JsonObject json, BTNode node) {
        if (node instanceof WaitAction wait) {
            json.addProperty("waitTicks", wait.getWaitTicks());
            json.addProperty("currentTicks", wait.getCurrentTicks());
        } else if (node instanceof RepeaterNode repeater) {
            json.addProperty("repeatCount", repeater.getRepeatCount());
            json.addProperty("currentCount", repeater.getCurrentCount());
            json.addProperty("infinite", repeater.isInfinite());
        } else if (node instanceof SequenceNode seq) {
            json.addProperty("currentIndex", seq.getCurrentIndex());
        } else if (node instanceof SelectorNode sel) {
            json.addProperty("currentIndex", sel.getCurrentIndex());
        }
        // 可以添加更多节点类型的特定属性
    }

    private static void addChildren(JsonObject json, BTNode node) {
        if (node instanceof DecorationNode decorationNode) {
            if (decorationNode.getChild() != null) {
                json.add("child", gson.fromJson(serializeForDisplay(decorationNode.getChild()), JsonElement.class));
            }
        } else if (node instanceof CompositeNode compositeNode) {
            if (compositeNode.getChildren() != null && !compositeNode.getChildren().isEmpty()) {
                JsonArray children = new JsonArray();
                for (BTNode child : compositeNode.getChildren()) {
                    children.add(gson.fromJson(serializeForDisplay(child), JsonElement.class));
                }
                json.add("children", children);
            }
        } else if (node instanceof BTRoot rootNode) {
            if (rootNode.getChild() != null) {
                json.add("child", gson.fromJson(serializeForDisplay(rootNode.getChild()), JsonElement.class));
            }
        }
        // 添加其他类型的子节点处理...
    }

    /**
     * 清理资源（JVM退出时调用）
     */
    public static void cleanup() {
        log.info("清理服务器资源...");
        stopServer();
    }

    /**
     * 注册JVM关闭钩子
     */
    public static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("JVM正在关闭，清理服务器资源...");
            cleanup();
        }));
    }
}
