package org.confluence.mod.common.block.functional.network;

import it.unimi.dsi.fastutil.ints.IntArraySet;

import java.util.Set;

public class IntAllocator {
    private final Set<Integer> table;

    public IntAllocator() {
        this.table = new IntArraySet();
    }

    public int insert() {
        int size = table.size();
        for (int i = 0; i < size; i++) {
            if (!table.contains(i)) {
                table.add(i);
                return i;
            }
        }
        table.add(size);
        return size;
    }

    public boolean forceAdd(int id) {
        return table.add(id);
    }

    public void remove(int id) {
        table.remove(id);
    }

    public void clear() {
        table.clear();
    }
}
