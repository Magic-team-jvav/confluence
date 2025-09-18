package org.confluence.mod.common.data.gen;

import it.unimi.dsi.fastutil.chars.Char2CharOpenHashMap;
import net.minecraft.data.PackOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModEnUdProvider extends ModEnglishProvider {
    private static final Char2CharOpenHashMap UD = new Char2CharOpenHashMap(
            new char[]{
                    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
                    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
                    '.', '!', ',', '"', '\'', '(', ')',
                    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
            },
            new char[]{
                    'ɐ', 'q', 'ɔ', 'p', 'ǝ', 'ɟ', 'ᵷ', 'ɥ', 'ᴉ', 'ɾ', 'ʞ', 'ꞁ', 'ɯ', 'u', 'o', 'd', 'b', 'ɹ', 's', 'ʇ', 'n', 'ʌ', 'ʍ', 'x', 'ʎ', 'z',
                    'Ɐ', 'ᗺ', 'Ɔ', 'ᗡ', 'Ǝ', 'Ⅎ', '⅁', 'H', 'I', 'Ր', 'Ʞ', 'Ꞁ', 'W', 'N', 'O', 'Ԁ', 'Ꝺ', 'ᴚ', 'S', '⟘', 'n', 'Ʌ', 'M', 'X', '⅄', 'Z',
                    '˙', '¡', '\'', '„', ',', ')', '(',
                    '0', '⥝', 'ᘔ', 'Ɛ', '߈', 'ϛ', '9', 'ㄥ', '8', '6'
            }
    );
    private static final Pattern PARAS = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    public ModEnUdProvider(PackOutput output) {
        super(output, "en_ud");
    }

    @Override
    public void add(String key, String value) {
        Matcher matcher = PARAS.matcher(value);
        if (matcher.find()) {
            int lastIndex = value.length();
            List<Match> matches = new ArrayList<>();
            do {
                matches.add(new Match(matcher.group(), matcher.start(), matcher.end()));
            } while (matcher.find());
            StringBuilder builder = new StringBuilder();
            for (int j = matches.size() - 1; j >= 0; j--) {
                Match match = matches.get(j);
                if (match.end < lastIndex) {
                    String afterText = value.substring(match.end, lastIndex);
                    builder.append(reverseAndConvert(afterText));
                }
                builder.append(match.text);
                lastIndex = match.start;
            }
            if (lastIndex > 0) {
                String beforeText = value.substring(0, lastIndex);
                builder.append(reverseAndConvert(beforeText));
            }
            super.add(key, builder.toString());
        } else {
            super.add(key, reverseAndConvert(value));
        }
    }

    private String reverseAndConvert(String text) {
        if (text.length() == 1) return text;
        StringBuilder builder = new StringBuilder(text.length());
        for (int i = text.length() - 1; i >= 0; i--) {
            char c = text.charAt(i);
            builder.append(UD.getOrDefault(c, c));
        }
        return builder.toString();
    }

    private record Match(String text, int start, int end) {}
}
