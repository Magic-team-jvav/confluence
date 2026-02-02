package org.confluence.lib;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforgespi.language.IModFileInfo;
import org.apache.commons.codec.digest.Md5Crypt;
import org.confluence.lib.util.LibUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class StartupConfig {
    private static ModConfigSpec.LongValue modifyTime;
    private static ModConfigSpec.ConfigValue<String> version;
    private static ModConfigSpec.ConfigValue<String> messageDigest;
    private static ModConfigSpec.IntValue alarmTimes;
    private static ModConfigSpec spec;

    static void register(ModContainer container) {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        modifyTime = builder.defineInRange("modifyTime", -1, Long.MIN_VALUE, Long.MAX_VALUE);
        version = builder.define("version", "");
        messageDigest = builder.define("messageDigest", "");
        alarmTimes = builder.defineInRange("alarmTimes", -1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        spec = builder.build();
        container.registerConfig(ModConfig.Type.STARTUP, spec);
    }

    private static boolean shouldAlarmInThisJVM = true;

    public static void checkIfSomeoneHasViolatedEULA(Player player) {
        if (!shouldAlarmInThisJVM || LibUtils.isDev() || LibUtils.isPhysicalServer()) return;
        IModFileInfo modFileInfo = ModList.get().getModFileById(ConfluenceMagicLib.CONFLUENCE_ID);
        if (modFileInfo == null) return;
        boolean shouldSave = false;
        Path filePath = modFileInfo.getFile().getFilePath();

        try {
            String currentVersion = modFileInfo.versionString();
            String lastVersion = version.get();
            String currentPath = Md5Crypt.md5Crypt(filePath.toString().getBytes(StandardCharsets.UTF_8), "$1$tur3m1an");
            String lastPath = messageDigest.get();
            long currentModifyTime = Files.getLastModifiedTime(filePath).toMillis();
            long lastModifyTime = modifyTime.getAsLong();
            int lastAlarmTimes = alarmTimes.getAsInt();

            if ((lastVersion.isEmpty() || lastPath.isEmpty() || lastModifyTime == -1 || lastAlarmTimes == -1) || // initialize
                    (!currentVersion.equals(lastVersion) && currentPath.equals(lastPath)) // reset while version changed and path not changes
            ) {
                shouldSave = true;
                modifyTime.set(currentModifyTime);
                version.set(currentVersion);
                messageDigest.set(currentPath);
                alarmTimes.set(3);
            }

            if (lastModifyTime == currentModifyTime && !lastPath.equals(currentPath)) { // if modified time not changes && path changed
                shouldSave = true;
                modifyTime.set(currentModifyTime);
                if (lastAlarmTimes > 0) {
                    alarmTimes.set(lastAlarmTimes - 1);
                    player.sendSystemMessage(Component.translatable("alarm.confluence_magic_lib.this_is_free_mod"));
                }
            }
        } catch (Exception e) {
            ConfluenceMagicLib.LOGGER.debug(e.getMessage());
        } finally {
            if (shouldSave) {
                spec.save();
            }
            shouldAlarmInThisJVM = false;
        }
    }
}
