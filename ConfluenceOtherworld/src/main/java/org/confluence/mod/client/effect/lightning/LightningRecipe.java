// Adapted from minecraft-lightning skill. See META-INF/arc-surge-lightning-NOTICES.md.
package org.confluence.mod.client.effect.lightning;

import org.confluence.mod.Confluence;

import com.google.gson.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Shared JSON contract used by the Forge demo and the HTML workbench. */
public final class LightningRecipe {
    public int schema = 1;
    public String mode = "orbit";
    public long seed = 1337L;
    public int strands = 16, samples = 48, orbitDirection = 1;
    public String orbitAnimation = "grow";
    public double objectRadius = 1.35, orbitRadius = 1.52, orbitHeight = 0,
        rodLength = 8.5, rodRadius = 0.15, emitterSpread = 0.10,
        orbitWobble = 0.28, orbitFlow = 1.8, orbitNoiseFrequency = 0.75, arcLength = 2.2,
        lengthRandomness = .65, pulseInterval = 6, throttle = .25, orbitExtendSpeed = 20,
        currentWidth = 0.014, glowWidth = 0.07, coreGain = 3.8, glowGain = 2.4,
        strikeHeight = 18, strikeWidth = .055, strikeDisplacement = 1.5, strikeRoughness = 0.53,
        branchProbability = 0.16, branchDecay = 0.5, branchLength = 0.3,
        branchAngle = 0.72, branchJitter = 0.18, progressionTicks = 1.2,
        durationTicks = 14.0, restrikeCount = 2, flicker = 0.22;
    public int strikeGenerations = 7, maxBranchDepth = 2, maxSegments = 1200;
    public int rainCount = 5, rainMaxBranchDepth = 1, rainMaxBranches = 1, branchLimit = 64;
    public double rainSpread = 12, rainDepthSpread = 4, rainStagger = 1.2, rainBranchProbability = .04;
    public String rainStyle = "spread";
    public boolean impactEnabled = true;
    public double impactRadius = 1.25, impactDuration = 12;
    public int impactRays = 9, impactSparks = 14;
    public boolean gradientEnabled = false;
    public double gradientRatio = 50;
    public String gradientTarget = "both", gradientCoreMode = "vivid";
    public String coreColor = "#ffffff", glowColor = "#87cfff", gradientStart = "#ffffff", gradientEnd = "#ffffff";

    public LightningRecipe validate() {
        if (schema != 1) throw new IllegalArgumentException("schema must be 1");
        if (seed < Integer.MIN_VALUE || seed > Integer.MAX_VALUE) throw new IllegalArgumentException("seed: signed 32-bit integer");
        if (!"spread".equals(rainStyle) && !"cluster".equals(rainStyle)) throw new IllegalArgumentException("rainStyle: spread/cluster");
        range("impactRadius",impactRadius,.1,6); range("impactDuration",impactDuration,2,40);
        if(impactRays<1||impactRays>24||impactSparks<0||impactSparks>32)throw new IllegalArgumentException("impact budget");
        if (!mode.equals("orbit") && !mode.equals("strike") && !mode.equals("rain")) throw new IllegalArgumentException("mode: orbit/strike/rain");
        if ((!orbitAnimation.equals("grow") && !orbitAnimation.equals("spektr") && !orbitAnimation.equals("ring")) || (orbitDirection != 1 && orbitDirection != -1)) throw new IllegalArgumentException("orbit animation/direction");
        if (strands < 1 || strands > 32 || samples < 16 || samples > 256) throw new IllegalArgumentException("strands/samples out of range");
        if (strikeGenerations < 3 || strikeGenerations > 9 || maxBranchDepth < 0 || maxBranchDepth > 4) throw new IllegalArgumentException("strike detail out of range");
        if (maxSegments < 64 || maxSegments > 10000) throw new IllegalArgumentException("maxSegments out of range");
        range("objectRadius", objectRadius, .1, 8); range("orbitRadius", orbitRadius, .1, 12);
        range("orbitHeight", orbitHeight, -8, 8); range("rodLength", rodLength, .5, 32); range("rodRadius", rodRadius, .01, 2); range("emitterSpread", emitterSpread, 0, 2);
        range("orbitWobble", orbitWobble, 0, 2);
        range("orbitNoiseFrequency", orbitNoiseFrequency, .05, 8);
        range("orbitFlow", orbitFlow, -12, 12); range("arcLength", arcLength, .1, 32); range("lengthRandomness", lengthRandomness, 0, .95);
        range("pulseInterval", pulseInterval, .5, 20); range("throttle", throttle, 0, 1); range("currentWidth", currentWidth, .001, .4); range("glowWidth", glowWidth, .01, 2);
        range("orbitExtendSpeed", orbitExtendSpeed, .1, 120);
        range("coreGain", coreGain, .1, 16); range("glowGain", glowGain, 0, 12);
        range("gradientRatio", gradientRatio, 0, 100);
        if (!java.util.Set.of("both", "core", "glow").contains(gradientTarget)) throw new IllegalArgumentException("gradientTarget: both/core/glow");
        if (!java.util.Set.of("vivid", "additive").contains(gradientCoreMode)) throw new IllegalArgumentException("gradientCoreMode: vivid/additive");
        range("strikeHeight", strikeHeight, 2, 1024); range("strikeWidth", strikeWidth, .004, .4); range("strikeDisplacement", strikeDisplacement, .05, 12);
        range("strikeRoughness", strikeRoughness, .35, .85); range("branchProbability", branchProbability, 0, 1);
        range("branchDecay", branchDecay, .1, .95); range("branchLength", branchLength, .05, 1); range("branchAngle", branchAngle, .1, 1.5);
        range("branchJitter", branchJitter, 0, 2); range("progressionTicks", progressionTicks, .1, 8);
        range("durationTicks", durationTicks, 1, 40); range("restrikeCount", restrikeCount, 0, 4); range("flicker", flicker, 0, 1);
        if (restrikeCount != Math.rint(restrikeCount)) throw new IllegalArgumentException("restrikeCount: integer");
        if (rainCount < 1 || rainCount > 24 || rainMaxBranchDepth < 0 || rainMaxBranchDepth > 1 || rainMaxBranches < 0 || rainMaxBranches > 2 || branchLimit < 0 || branchLimit > 128) throw new IllegalArgumentException("rain count/depth/branch limit out of range");
        if (durationTicks < progressionTicks + 2) throw new IllegalArgumentException("durationTicks must exceed progressionTicks by 2");
        range("rainSpread", rainSpread, 1, 64); range("rainDepthSpread", rainDepthSpread, 0, 32); range("rainStagger", rainStagger, 0, 12); range("rainBranchProbability", rainBranchProbability, 0, .12);
        color("coreColor", coreColor); color("glowColor", glowColor); color("gradientStart", gradientStart); color("gradientEnd", gradientEnd); return this;
    }
    private static void range(String n, double v, double lo, double hi) { if (!Double.isFinite(v) || v < lo || v > hi) throw new IllegalArgumentException(n + ": " + lo + ".." + hi); }
    private static void color(String n, String v) { if (v == null || !v.matches("#[0-9a-fA-F]{6}")) throw new IllegalArgumentException(n + ": #RRGGBB"); }
    public static LightningRecipe read(Path path) throws IOException {
        if (!Files.exists(path)) return new LightningRecipe();
        return parse(JsonParser.parseString(Files.readString(path)));
    }
    /** Skill JSON and old MOD string-value wrappers share one strict, unclamped parser. */
    public static LightningRecipe parse(JsonElement input) {
        if(input==null||!input.isJsonObject())throw new IllegalArgumentException("JSON 必须是参数对象");
        JsonObject root=input.getAsJsonObject();
        JsonObject values=root.has("values")?object(root.get("values")):root;
        LightningRecipe result=new LightningRecipe();
        for(var entry:values.entrySet()) {
            String key=entry.getKey();
            // The MOD's selection marker is metadata, not a skill renderer field.
            if(key.equals("preset"))continue;
            try {
                Field field=LightningRecipe.class.getField(key);
                if(Modifier.isStatic(field.getModifiers()))throw new NoSuchFieldException();
                JsonElement value=entry.getValue();
                if(value==null||!value.isJsonPrimitive())throw new IllegalArgumentException(key+" 必须是单个参数值");
                JsonPrimitive primitive=value.getAsJsonPrimitive();
                if(field.getType()==String.class){
                    if(!primitive.isString())throw new IllegalArgumentException(key+" 必须是字符串");
                    field.set(result,primitive.getAsString());
                }else if(field.getType()==boolean.class){
                    String text=primitive.getAsString();
                    if(!text.equals("true")&&!text.equals("false"))throw new IllegalArgumentException(key+" 必须为布尔值");
                    field.setBoolean(result,Boolean.parseBoolean(text));
                }else{
                    if(primitive.isBoolean())throw new IllegalArgumentException(key+" 必须是数值");
                    String numeric=primitive.getAsString();
                    double number=Double.parseDouble(numeric);
                    if(!Double.isFinite(number))throw new IllegalArgumentException(key+" 必须是有限数值");
                    if(field.getType()==int.class||field.getType()==long.class){
                        int integer;
                        try{integer=new BigDecimal(numeric).intValueExact();}
                        catch(ArithmeticException|NumberFormatException ex){throw new IllegalArgumentException(key+" 必须是 32 位整数");}
                        if(field.getType()==int.class)field.setInt(result,integer);else field.setLong(result,integer);
                    }else field.setDouble(result,number);
                }
            }catch(NoSuchFieldException ex){throw new IllegalArgumentException("未知闪电参数："+key);}
            catch(IllegalAccessException ex){throw new IllegalStateException(ex);}
        }
        return result.validate();
    }
    private static JsonObject object(JsonElement value){
        if(value==null||!value.isJsonObject())throw new IllegalArgumentException("values 必须是参数对象");
        return value.getAsJsonObject();
    }
}
