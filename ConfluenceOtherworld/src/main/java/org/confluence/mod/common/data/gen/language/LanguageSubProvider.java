package org.confluence.mod.common.data.gen.language;

public interface LanguageSubProvider {
   default void addTranslations(boolean isEn) {
        if (isEn) {
            english();
        } else {
            chinese();
        }
    }

    void english();

    void chinese();

    void add(String key, String value);
}
