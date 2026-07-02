package org.confluence.mod.common.entity.ai.bt;

import java.util.HashMap;
import java.util.Map;

/**
 * 行为树节点间共享状态。
 */
public class Blackboard {
    private final Map<String, Object> data = new HashMap<>();

    public void put(String key, Object value) { data.put(key, value); }
    @SuppressWarnings("unchecked")
    public <T> T get(String key) { return (T) data.get(key); }
    public boolean has(String key) { return data.containsKey(key); }
    public void remove(String key) { data.remove(key); }
    public void clear() { data.clear(); }

    public int getInt(String key) { return has(key) ? (int) get(key) : 0; }
    public float getFloat(String key) { return has(key) ? (float) get(key) : 0f; }
    public boolean getBoolean(String key) { return has(key) && (boolean) get(key); }
    public long getLong(String key) { return has(key) ? (long) get(key) : 0L; }
}
