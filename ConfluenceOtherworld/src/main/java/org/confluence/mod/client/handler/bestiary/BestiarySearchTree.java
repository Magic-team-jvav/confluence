package org.confluence.mod.client.handler.bestiary;

import net.minecraft.client.searchtree.SearchTree;

import java.util.List;

public class BestiarySearchTree implements SearchTree<ClientBestiaryEntry> {
    @Override
    public List<ClientBestiaryEntry> search(String query) {
        return List.of();
    }
}
