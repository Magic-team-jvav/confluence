package org.confluence.lib.util.lang;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.util.GsonHelper;
import org.confluence.lib.util.LibUtils;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Map;

public class LangMerger {
    private static final JsonObject mergedResult = new JsonObject();
    private static final String[] locals = new String[]{"en_us.json", "zh_cn.json"};

    public static void main(String[] args) {
        if (!LibUtils.isDev() || args.length == 0) return;
        Path startPath = Paths.get(args[0]);
        for (String local : locals) {
            String s1 = startPath.toString();

            try {
                Files.walkFileTree(startPath, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        if (file.getFileName().toString().equals(local)) {
                            String s2 = file.toString();
                            if (s2.contains("resources") && !s2.contains("build") && !s2.contains("resourcepacks") && !s2.equals(s1)) {
                                try (Reader reader = new FileReader(file.toFile())) {
                                    for (Map.Entry<String, JsonElement> entry : GsonHelper.parse(reader).entrySet()) {
                                        mergedResult.add(entry.getKey(), entry.getValue());
                                    }
                                } catch (Exception ignored) {}
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) {
                        return FileVisitResult.CONTINUE;
                    }
                });

                Path resultPath = startPath.resolve("i18n").resolve(local);
                Files.deleteIfExists(resultPath);
                DataProvider.saveStable(CachedOutput.NO_CACHE, mergedResult, resultPath).join();
            } catch (IOException ignored) {}
        }
    }
}
