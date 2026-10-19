# 魂师技能总览（C 键）鼠标滚轮缩放

给 `SoulOverviewScreen` 加了滚轮缩放，两版（1.21.1 NeoForge / 1.20.1 Forge）同名同改。

## 0. 验证

| 侧 | compileJava |
| --- | --- |
| 1.21.1 | `BUILD SUCCESSFUL` |
| 1.20.1 | `BUILD SUCCESSFUL` |

本轮只改客户端界面，无数据生成改动。

## 1. 操作

| 操作 | 效果 |
| --- | --- |
| 滚轮上滚 | 放大（每格 ×1.1） |
| 滚轮下滚 | 缩小（每格 ÷1.1） |
| 按住左键拖动 | 平移画布（缩放后仍与鼠标 1:1 跟随） |

- 缩放范围 **0.5 × ～ 2.0 ×**，超出即夹紧；
- **以鼠标位置为锚点**：缩放前后，鼠标下面的那个技能节点停在原处，不会"跑掉"；
- 缩放会同时改变节点间距与节点尺寸，以及它们之间的连线。

## 2. 坐标模型

节点被摆放到的**中心点**统一为：

```
center = 屏幕中心 + (orig + scroll) * zoom
```

- `orig` 是节点在"以屏幕中心为原点"的技能树坐标（节点现有布局，未改动）；
- `scroll` 仍是**节点空间**的平移量；
- `zoom` 是缩放倍率。

只有两处需要自己换算：

1. `positionAllNodes()` —— 节点定位，并把 `zoom` 同步给节点（`node.setUiScale`）；
2. `calcScrollBounds()` —— 滚动边界按 `1/zoom` 收缩
   （`minScrollX = (width - margin - centerX) / zoom - maxRight`），
   保证放大后仍能把角落拖进视野、缩小后也不会让内容飘出屏幕。

**连线端点不再自己推算**：直接取 `OverviewNode#getScreenCenter()`（控件位置的屏幕坐标），
细节见 §2.4。

### 2.1 锚点补偿

令锚点处的技能树坐标不变即可解出偏移补偿：

```
scroll += (mouse - center) * (1 / newZoom - 1 / oldZoom)
```

### 2.2 拖动补偿

`scroll` 是节点空间量，而鼠标位移是屏幕像素，所以拖动时除以 `zoom`：

```java
scrollX = dragStartScrollX + (mouseX - dragStartMouseX) / zoom;
```

否则放大到 2× 时拖动手感会快一倍。

### 2.3 连线线宽补偿

`drawGradientLine` 的粗细是**屏幕像素**，不跟着坐标走；只缩放端点会让节点变大、连线还是老粗细。
因此在 `renderConnections` 里把两个宽度也乘上 `zoom`：

```java
private static final float CONNECTION_THICKNESS = 2.0F;        // 芯线
private static final float CONNECTION_LIGHT_THICKNESS = 5.0F;  // 两侧辉光
...
float thickness = CONNECTION_THICKNESS * scale;
float lightThickness = CONNECTION_LIGHT_THICKNESS * scale;
drawGradientLine(guiGraphics, sx, sy, tx, ty, thickness, lightThickness, ...);
```

0.5× 时为 1 / 2.5 像素（仍可见），2× 时为 4 / 10 像素，与节点尺寸保持同一比例。

> 背景装饰（法阵、生命之树、星空）里的 `drawGradientLine` **没有**跟着缩放：
> 它们属于不缩放的背景层，用 `scroll * 各自系数` 的视差，改粗反而会和背景脱节。

### 2.4 连线端点改为「框中心 → 框中心」

原来 `renderConnections` 自己按 `orig` 推算端点：

```java
// 旧写法（错的）
Vec2 src = node.getCenterPos();                      // = new Vec2(origX + width/2, origY + height/2)
float sx = cx + (src.x + scrollX) * scale;
```

而节点实际是按「`origX` 就是中心」摆放的（`OverviewNode#setPosition` 会把入参减去半个框）：
`center = cx + (origX + scrollX) * zoom`。

两式一比就能看出端点恒偏 **`BOX_SIZE/2 * zoom` = 16 * zoom**（右下方向半个到一个框）；
1× 时被框盖住不明显，2× 时直接偏出一个整框，所以缩放后问题才暴露。

现在改为**直接用控件的屏幕几何**，不再重算坐标：

```java
/// 节点的屏幕坐标视觉中心：技能框、图标、缩放锚点与连线端点统一用这一点。
public Vec2 getScreenCenter() {
    return new Vec2(getX() + BOX_SIZE * 0.5F, getY() + BOX_SIZE * 0.5F - BOX_SPRITE_OFFSET_Y);
}
```

- `getX()/getY()` 已经包含 scroll 与 zoom 的结果，端点因此天然与节点同步，缩放/拖动/回绕都不用再改；
- `-2` 是因为技能框贴图从 `y - 2` 开始绘制（`SoulSkillBox.renderBox`），视觉中心比控件中心高 2 像素；
- 同一个点也用作 `renderWidget` 的 `pose.scale` 锚点与悬停判定中心，
  于是「贴图中心 = 缩放锚点 = 悬停中心 = 连线端点」四者严格一致，任何倍率下都不会再偏。

`drawGradientLine` 的绘制方法本身没有改动，只换了端点来源。

## 3. 节点自身的缩放（`OverviewNode`）

节点是 `AbstractWidget`，包围盒固定 `BOX_SIZE = 32`。为了既放大外观又保持提示文本可读：

- 新增 `uiScale` 字段 + `setUiScale(...)`，由 `positionAllNodes()` 每帧同步；
- `renderWidget(...)` 里以 `getScreenCenter()` 为锚点 `pose.scale(uiScale, uiScale, 1)`，只包住框、图标与冷却；
- **提示文本移到缩放之外绘制**：`renderTooltip(...)` 在 `popPose()` 之后调用，
  否则文字会被一起放大，并且因为坐标被缩放而偏离鼠标；
- 悬停/点击改用缩放后的实际范围：
  - `renderWidget` 开头用 `isMouseOverScaled(...)` 重设 `isHovered`
    （`AbstractWidget.render` 会先用未缩放的包围盒算一次，这里覆盖掉）；
  - 重写 `isMouseOver(double,double)` 与 `clicked(double,double)`，
    让鼠标点击命中缩放后的方框（`clicked` 是 `AbstractWidget.mouseClicked` 的实际判定入口）。

## 4. 刻意没做

1. **背景视差没跟着缩放**：`renderBackground` 里的星空/树/法阵用的是各自的 `scroll * 系数`，
   仍按原样铺满屏幕。跟着缩放会在 `zoom < 1` 时露出边缘空白，收益不大。
2. **导航按钮的跳转目标没改**：`navigateTo(target)` 里存的是节点空间的 scroll，
   在任何缩放下都能跳到大致区域，只是不再严格居中。
3. 缩放不做持久化：每次打开总览都从 1.0× 开始。

## 5. 文件索引

两版同名同改：

```
client/gui/container/soulcerer/SoulOverviewScreen.java  zoom 字段/常量、mouseScrolled、setZoom、
                                                        positionAllNodes、calcScrollBounds、renderConnections（端点取控件中心 + 线宽）、拖动补偿
client/gui/widget/soulcerer/skill/overview/OverviewNode.java  uiScale、getScreenCenter、缩放绘制、缩放后的悬停/点击判定
```

版本差异：

- 1.20.1 用 `Mth.clamp`（Java 17 没有 `Math.clamp`）；
- 1.20.1 的 `mouseScrolled` 只有 3 个参数（`scrollX` 分量是 1.20.5 之后才加的）。

## 6. 仍未做

两版都没有进游戏实测：滚轮缩放、锚点跟随、缩放后拖动与点击、0.5× / 2× 两端的表现。
