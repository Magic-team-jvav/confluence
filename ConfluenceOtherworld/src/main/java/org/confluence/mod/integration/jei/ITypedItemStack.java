package org.confluence.mod.integration.jei;

public interface ITypedItemStack {
    void confluence$setChance(float chance);

    void confluence$setBounds(int min, int max);

    float confluence$grtChance();

    int confluence$getMin();

    int confluence$getMax();
}
