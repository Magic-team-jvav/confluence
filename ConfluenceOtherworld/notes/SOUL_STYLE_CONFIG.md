# HUD 设置补「灵魂值样式」

灵魂条此前一直借用「魔力值样式」的开关（`manaStyle`），HUD 设置里没有独立项。本轮补上。

## 1. 新增配置项

`ClientConfigs`：

```java
public static TerraStyleManaHud.Soul soulStyle = TerraStyleManaHud.Soul.OVERLAY;
...
SOUL_STYLE = builder.defineEnum("soulStyle", TerraStyleManaHud.Soul.OVERLAY);   // HUD → Soulcerer → Soul
```

配置路径：`HUD.Soulcerer.soulStyle`，与「快速技能样式」同一分组。

翻译键（`ConfigurationLanguageSubProvider`）：

```
confluence.configuration.soulStyle          = 灵魂值样式
confluence.configuration.soulStyle.legacy   = 灵魂值样式:精致
confluence.configuration.soulStyle.overlay  = 灵魂值样式:叠加
confluence.configuration.soulStyle.tooltip  = 灵魂值样式
```

> 这四个键**语言提供器里本来就写好了**（含 `soulOffsetX/Y`），只是配置项一直没注册，
> 所以之前 HUD 设置里看不到。本轮只补配置项，不重复加键（第一次加重复键，`runData` 直接报
> `Duplicate translation key confluence.configuration.soulStyle`，已回退）。
> 顺手修了两处错译：
> - `soulStyle.legacy` 的中文原本是「灵魂值样式:叠加」→ 改为「灵魂值样式:精致」
> - `soulStyle.tooltip` 的中文原本是「灵魂样式」→ 统一为「灵魂值样式」

配置界面是 NeoForge 原生的 `ConfigurationScreen`（经 `MergedConfigurationScreen`），
`defineEnum` 一注册，枚举项与下拉就会自动出现，不需要另写界面。

## 2. 为什么新增枚举而不是复用 `Mana`

`TranslatableEnum#getTranslatedName()` 是枚举实例方法，`Mana` 固定拼
`confluence.configuration.manaStyle.*`。直接复用会导致灵魂条的下拉显示成「魔力值样式:精致」。

所以新增 `TerraStyleManaHud.Soul`，**只做委托**，画法完全复用：

```java
public enum Soul implements TranslatableEnum {
    LEGACY(Mana.LEGACY),
    OVERLAY(Mana.OVERLAY);
    ...
    public void render(GuiGraphics g, Minecraft mc) { style.render(g, mc); }
    @Override public Component getTranslatedName() {
        return Component.translatable("confluence.configuration.soulStyle." + name().toLowerCase(Locale.ROOT));
    }
}
```

好处：两种样式的画法只有一份实现，加新样式时两边同步得到，不会出现「灵魂条和魔力条画法分叉」。

## 3. 渲染分流

`TerraStyleManaHud#render` 按当前资源模式选开关：

```java
if (PlayerUtils.isSoulMode(minecraft.player)) {
    ClientConfigs.soulStyle.render(guiGraphics, minecraft);
} else {
    ClientConfigs.manaStyle.render(guiGraphics, minecraft);
}
```

同一时间只会画一个条，所以两个开关不会互相干扰；
把「魔力值样式」设成精致、「灵魂值样式」设成叠加这种混搭也能正常工作。

## 4. 两版的差异

- 1.21.1：`Soulcerer` 配置分组是无条件注册的，`soulStyle` 直接放在里面；
- 1.20.1：该分组被 `if (Confluence.SOUL_SKILLS)` 包着（`SOUL_SKILLS = LibUtils.isDev()`），
  所以 `soulStyle` 也放在里面，并在读取时同样加了 `if (Confluence.SOUL_SKILLS)` 守卫，
  避免开关关闭时 `EnumValue` 为 null 导致 NPE（此时灵魂模式本来也不会触发，字段保持默认值）。

## 5. 没做的

- 灵魂条仍然复用**魔力条的偏移设置**（`manaOffsetX/Y`），没有单独的 X/Y 偏移项；
  需要的话可以照同样方式再加 `soulOffsetX/Y`。
- 两版都没有进游戏实测：配置项是否出现在 HUD → Soulcerer 下、切换后是否即时生效（配置页改动会触发 reload）。
