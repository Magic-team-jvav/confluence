package org.confluence.mod.client.summoner.model.bbmodel;

import java.util.Locale;

final class BBModelEasing {

    private BBModelEasing() {
    }

    static double apply(String easingName, double t) {
        String name = easingName == null ? "linear" : easingName.toLowerCase(Locale.ROOT);
        return switch (name) {
            case "none", "linear" -> t;
            case "step" -> t <= 0 ? 0 : 1;
            case "easeinsine" -> easeIn(t, BBModelEasing::sine);
            case "easeoutsine" -> easeOut(t, BBModelEasing::sine);
            case "easeinoutsine" -> easeInOut(t, BBModelEasing::sine);
            case "easeinquad" -> easeIn(t, BBModelEasing::quad);
            case "easeoutquad" -> easeOut(t, BBModelEasing::quad);
            case "easeinoutquad" -> easeInOut(t, BBModelEasing::quad);
            case "easeincubic" -> easeIn(t, BBModelEasing::cubic);
            case "easeoutcubic" -> easeOut(t, BBModelEasing::cubic);
            case "easeinoutcubic" -> easeInOut(t, BBModelEasing::cubic);
            case "easeinquart" -> easeIn(t, value -> pow(value, 4));
            case "easeoutquart" -> easeOut(t, value -> pow(value, 4));
            case "easeinoutquart" -> easeInOut(t, value -> pow(value, 4));
            case "easeinquint" -> easeIn(t, value -> pow(value, 5));
            case "easeoutquint" -> easeOut(t, value -> pow(value, 5));
            case "easeinoutquint" -> easeInOut(t, value -> pow(value, 5));
            case "easeinexpo" -> easeIn(t, BBModelEasing::expo);
            case "easeoutexpo" -> easeOut(t, BBModelEasing::expo);
            case "easeinoutexpo" -> easeInOut(t, BBModelEasing::expo);
            case "easeincirc" -> easeIn(t, BBModelEasing::circ);
            case "easeoutcirc" -> easeOut(t, BBModelEasing::circ);
            case "easeinoutcirc" -> easeInOut(t, BBModelEasing::circ);
            case "easeinback" -> easeIn(t, BBModelEasing::back);
            case "easeoutback" -> easeOut(t, BBModelEasing::back);
            case "easeinoutback" -> easeInOut(t, BBModelEasing::back);
            case "easeinelastic" -> easeIn(t, BBModelEasing::elastic);
            case "easeoutelastic" -> easeOut(t, BBModelEasing::elastic);
            case "easeinoutelastic" -> easeInOut(t, BBModelEasing::elastic);
            case "easeinbounce" -> easeIn(t, BBModelEasing::bounce);
            case "easeoutbounce" -> easeOut(t, BBModelEasing::bounce);
            case "easeinoutbounce" -> easeInOut(t, BBModelEasing::bounce);
            default -> t;
        };
    }

    private static double easeIn(double t, Function function) {
        return function.apply(t);
    }

    private static double easeOut(double t, Function function) {
        return 1 - function.apply(1 - t);
    }

    private static double easeInOut(double t, Function function) {
        if (t < 0.5) {
            return function.apply(t * 2) / 2;
        }
        return 1 - function.apply((1 - t) * 2) / 2;
    }

    private static double sine(double t) {
        return 1 - Math.cos(t * Math.PI / 2);
    }

    private static double quad(double t) {
        return t * t;
    }

    private static double cubic(double t) {
        return t * t * t;
    }

    private static double pow(double t, double power) {
        return Math.pow(t, power);
    }

    private static double expo(double t) {
        return Math.pow(2, 10 * (t - 1));
    }

    private static double circ(double t) {
        return 1 - Math.sqrt(1 - t * t);
    }

    private static double back(double t) {
        double c = 1.70158;
        return t * t * ((c + 1) * t - c);
    }

    private static double elastic(double t) {
        return 1 - Math.pow(Math.cos(t * Math.PI / 2), 3) * Math.cos(t * Math.PI);
    }

    private static double bounce(double t) {
        double n = 0.5;
        return Math.min(Math.min(b1(t), b2(t, n)), Math.min(b3(t, n), b4(t, n)));
    }

    private static double b1(double x) {
        return 121d / 16d * x * x;
    }

    private static double b2(double x, double n) {
        return 121d / 4d * n * Math.pow(x - 6d / 11d, 2) + 1 - n;
    }

    private static double b3(double x, double n) {
        return 121 * n * n * Math.pow(x - 9d / 11d, 2) + 1 - n * n;
    }

    private static double b4(double x, double n) {
        return 484 * n * n * n * Math.pow(x - 10.5d / 11d, 2) + 1 - n * n * n;
    }

    @FunctionalInterface
    private interface Function {
        double apply(double t);
    }
}
