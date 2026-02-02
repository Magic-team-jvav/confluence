package org.confluence.lib.util.lang;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.util.GsonHelper;
import org.confluence.lib.util.LibUtils;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class LangDistinctor {
    public static void main(String[] args) {
        if (!LibUtils.isDev() || args.length == 0) return;
        Path dir = Paths.get(args[0]).resolve("i18n");
        Path en_us = dir.resolve("en_us.json");
        Path zh_cn = dir.resolve("zh_cn.json");
        distinct("en_us_remaining.json", zh_cn, en_us, dir);
        distinct("zh_cn_remaining.json", en_us, zh_cn, dir);
    }

    private static void distinct(String file, Path a, Path b, Path dir) {
        try (Reader reader0 = new FileReader(a.toFile()); Reader reader1 = new FileReader(b.toFile())) {
            JsonObject mergedResult = new JsonObject();
            JsonObject parsed0 = GsonHelper.parse(reader0);
            Set<String> allKeys = new HashSet<>(parsed0.keySet());
            JsonObject parsed1 = GsonHelper.parse(reader1);
            allKeys.removeAll(parsed1.keySet());
            for (String key : allKeys) {
                mergedResult.add(key, parsed0.get(key));
            }
            Path resultPath = dir.resolve(file);
            Files.deleteIfExists(resultPath);
            DataProvider.saveStable(CachedOutput.NO_CACHE, mergedResult, resultPath).join();
        } catch (Exception ignored) {}
    }
}
