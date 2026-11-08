package org.confluence.mod.client.character;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.neoforged.fml.loading.FMLPaths;
import org.confluence.mod.Confluence;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/// 泰拉式角色列表，存在本机 config/confluence/characters.json（与世界无关，像泰拉的角色文件）。
/// 进入世界时把选中的角色（名字、难度）发给服务端；背包仍按世界保存（Minecraft 的规则）。
public final class CharacterStore {
    private static final Path FILE = FMLPaths.CONFIGDIR.get().resolve("confluence").resolve("characters.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Data data;

    /// 一个角色
    public static final class TerraCharacter {
        public String id = UUID.randomUUID().toString();
        public String name = "";
        /// TerraCharacterRules.SOFTCORE / MEDIUMCORE / HARDCORE
        public byte difficulty;
        /// 外观；旧角色没有（null）时用启动器皮肤
        public CharacterAppearance appearance;
        /// 硬核角色死后为 true，不能再用
        public boolean dead;
        public long created = System.currentTimeMillis();
        public long lastPlayed;
    }

    private static final class Data {
        String selected = "";
        List<TerraCharacter> characters = new ArrayList<>();
    }

    private CharacterStore() {}

    private static Data data() {
        if (data == null) {
            data = new Data();
            if (Files.exists(FILE)) {
                try (Reader reader = Files.newBufferedReader(FILE)) {
                    Data loaded = GSON.fromJson(reader, new TypeToken<Data>() {}.getType());
                    if (loaded != null && loaded.characters != null) data = loaded;
                } catch (IOException | RuntimeException e) {
                    Confluence.LOGGER.warn("Failed to read {}", FILE, e);
                }
            }
        }
        return data;
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) {
                GSON.toJson(data(), writer);
            }
        } catch (IOException e) {
            Confluence.LOGGER.warn("Failed to write {}", FILE, e);
        }
    }

    public static List<TerraCharacter> characters() {
        return data().characters;
    }

    public static TerraCharacter selected() {
        String id = data().selected;
        for (TerraCharacter character : characters()) if (character.id.equals(id)) return character;
        return null;
    }

    public static void select(TerraCharacter character) {
        data().selected = character == null ? "" : character.id;
        if (character != null) character.lastPlayed = System.currentTimeMillis();
        save();
    }

    public static void add(TerraCharacter character) {
        characters().addFirst(character);
        save();
    }

    public static void remove(TerraCharacter character) {
        characters().remove(character);
        if (character.id.equals(data().selected)) data().selected = "";
        save();
    }

    public static void markDead(String id) {
        for (TerraCharacter character : characters()) {
            if (character.id.equals(id) && !character.dead) {
                character.dead = true;
                save();
            }
        }
    }
}
