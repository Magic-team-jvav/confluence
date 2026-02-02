package org.confluence.terraentity.data.format;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum OutputFormat implements Function<String, String> {

    /**
     * 不做任何格式化
     */
    ORIGINAL(Function.identity()),
    /**
     * 压缩JSON字符串中的数字数组，使其显示在一行
     */
    COMPRESS_NUMBER_ARRAYS(s->compressArrays(s, ArrayPattern.NUMBER_ARRAY)),

    /**
     * 压缩JSON字符串中的数组，使其显示在一行，包括数字数组和非数字数组
     */
    COMPRESS_ALL_ARRAYS(s->compressArrays(s, ArrayPattern.ALL_ARRAY)),

    /**
     * 修复JSON字符串中的浮点数精度，不过貌似没有必要
     */
    FIX_FLOAT_PRECISION(s->fixFloatPrecisionInJson(s, DefaultPrecisionStrategy.INSTANCE))

    ;


    final Function<String, String> format;

    OutputFormat(Function<String, String> format) {
        this.format = format;
    }

    @Override
    public String apply(String s) {
        return format.apply(s);
    }

    private enum ArrayPattern {
        NUMBER_ARRAY(Pattern.compile(
                "\\[\\s*-?\\d+(?:\\.\\d+)?(?:\\s*,\\s*-?\\d+(?:\\.\\d+)?)*\\s*]",
                Pattern.DOTALL
        )),
        ALL_ARRAY(Pattern.compile(
                "\\[(?:[^\\[\\]]++|\\[(?:[^\\[\\]]++|\\[(?:[^\\[\\]]++|\\[.*?])*])*])*]",
                Pattern.DOTALL
        ))
        ;
        final Pattern pattern;
        ArrayPattern(Pattern pattern) {
            this.pattern = pattern;
        }
    }

    /**
     * 压缩JSON字符串中的数组，使其显示在一行
     *
     * @param json 格式化的JSON字符串
     * @return 数组压缩后的JSON字符串
     */
    private static String compressArrays(String json, ArrayPattern arrayPattern) {
        Matcher matcher = arrayPattern.pattern.matcher(json);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            // 找到数组并移除其中的换行和多余空格
            String array = matcher.group();
            String compressed = spaceEnterSpace
                    .matcher(array)
                    .replaceAll(" ")
                    .replaceAll(additionalSpace.pattern(), " ");
            matcher.appendReplacement(result, compressed);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static final Pattern spaceEnterSpace = Pattern.compile("\\s*\\n\\s*");
    private static final Pattern additionalSpace = Pattern.compile("\\s+");

    // 匹配浮点数的正则表达式模式
    private static final Pattern FLOAT_PATTERN = Pattern.compile(
            "-?\\d+\\.\\d+(?:[eE][-+]?\\d+)?"
    );



    /**
     * 修复 JSON 字符串中的浮点数精度，支持自定义精度策略
     * @param jsonString JSON 字符串
     * @param precisionStrategy 精度策略
     * @return 修复后的 JSON 字符串
     */
    public static String fixFloatPrecisionInJson(String jsonString, PrecisionStrategy precisionStrategy) {
        Matcher matcher = FLOAT_PATTERN.matcher(jsonString);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String floatString = matcher.group();
            String fixedFloat = fixFloatPrecision(floatString, precisionStrategy);

            // 只有在值发生变化时才替换
            if (!floatString.equals(fixedFloat)) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(fixedFloat));
            } else {
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group()));
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * 修复单个浮点数字符串的精度
     * @param floatString 浮点数字符串
     * @param precisionStrategy 精度策略
     * @return 修复后的字符串
     */
    private static String fixFloatPrecision(String floatString, PrecisionStrategy precisionStrategy) {
        try {
            if(!floatString.contains(".")){
                return floatString;
            }
            BigDecimal bd = new BigDecimal(floatString);
            int precision = precisionStrategy.determinePrecision(bd);

            // 设置精度
            bd = bd.setScale(precision, java.math.RoundingMode.HALF_UP)
                    .stripTrailingZeros();

            // 如果没有0，补充1位0
            String plainString = bd.toPlainString();
            if(plainString.contains(".")){
                return plainString;
            }
            return plainString + ".0";
        } catch (NumberFormatException e) {
            return floatString;
        }
    }

    /**
     * 精度策略接口
     */
    public interface PrecisionStrategy {
        int determinePrecision(BigDecimal value);
    }

    /**
     * 默认精度策略
     */
    public enum DefaultPrecisionStrategy implements PrecisionStrategy {
        INSTANCE;

        @Override
        public int determinePrecision(BigDecimal value) {
            return 6;  // 保留6位小数
        }
    }

    /**
     * 固定精度策略
     */
    public static class FixedPrecisionStrategy implements PrecisionStrategy {
        private final int precision;

        public FixedPrecisionStrategy(int precision) {
            this.precision = precision;
        }

        @Override
        public int determinePrecision(BigDecimal value) {
            return precision;
        }
    }

    /**
     * 自适应精度策略
     */
    public enum  AdaptivePrecisionStrategy implements PrecisionStrategy {
        INSTANCE;

        @Override
        public int determinePrecision(BigDecimal value) {
            // 获取原始精度
            int originalScale = value.scale();

            // 对于接近整数的值，使用较小精度
            BigDecimal fractionalPart = value.remainder(BigDecimal.ONE).abs();
            if (fractionalPart.compareTo(new BigDecimal("0.000001")) < 0) {
                return Math.min(6, originalScale);
            }

            // 对于常见的小数值，使用特定精度
            String plainString = value.toPlainString();
            if (plainString.matches("0\\.0+[1-9]")) {
                Matcher matcher = Pattern.compile("0\\.(0+)[1-9]").matcher(plainString);
                if (matcher.find()) {
                    int zeros = matcher.group(1).length();
                    return zeros + 2; // 保留所有零和两位有效数字
                }
            }

            // 默认情况下，使用较小精度但至少保留6位小数
            return Math.min(originalScale, Math.max(6, originalScale));
        }
    }
}
