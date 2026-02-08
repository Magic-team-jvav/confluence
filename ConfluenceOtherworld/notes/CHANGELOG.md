# 更新日志（1.2.2）：

## 注意事项：

- 生命体分析仪的检测列表已更新，需要手动重置泰拉饰品的配置以刷新列表

## 修复：

- 修复了当同时装载"高级战利品信息显示Advanced Loot Info"时，创建世界会生成饿鬼的问题
- 修复肢解效果配置失效的问题
- 修复关闭机械棱镜还是能看见电线的问题
- 修复自动攻击对骑枪生效的问题
- 修复钓鱼任务邪恶世界区分失败的问题
- 修复微光炼药锅会消耗所有以太块的问题
- 修复史莱姆雨事件不触发的问题
- 抑制微光炼药锅的报错
- 修复泰拉生物配置中的"增强所有生物"导致的区块加载死锁的问题
- 修复地牢中的诅咒骷髅头敌怪无法被狩猎药水标记的问题
- 再次修复与AzureLib的兼容
- 修复汇流为原版战利品表添加奖励丢失的问题
- 修复图鉴崩溃的问题
- 修复重载资源包导致的图鉴数据丢失问题
- 修复clear指令失效问题
- 修复自然回血与手动回血的混淆
- 修复梯凳相关bug
- 修复错误的怪物图鉴键

## 优化：

- 增加悠悠球的平滑移动
- 汇流来世自定义窗口标题
- NPC不再生成在墙里或空中
- 18:00（即12000t）时会有五分之一的几率替换为特殊月亮
- 交叉骨模型偏移，尖球滚动滑移
- 打开匣子和升级钱币时，不再需要对着空气
- 重写队伍，且退出存档时自动关闭PvP（仅在多人模式有效）
- 补充更多图鉴条目
- 洞穴探险药水效果、危险感知药水效果、重力药水效果、金属探测器物品会在物品栏内显示其键位
- 增加蜜蜂枪射弹的灵敏度
- 支持MrCrayfish的家具作为NPC房屋条件。其中chair、sofa、stool系列为椅子；table、desk系列为桌子。

## 调整：

- 同步泰拉瑞亚1.4.5版本：
  - 洞穴冰雪箱战利品表中的可疑眼球替换为鹿华
  - 为史莱姆王宝藏袋战利品表添加史莱姆法杖（3.33%概率）
  - 需要解锁16个图鉴才能在动物学家处购买皮鞭
  - 无限袋合成所需材料从3996上调至9999个
  - 提升蝙蝠棍的攻速
  - 提升罗马短剑的攻速
  - 紫挥棒鱼能以50%概率造成困惑
  - 养蜂人能以100%概率造成困惑
  - 提升风暴长矛射弹射程以及伤害
  - 延长尖球持续时间33%
  - 提升燧发枪暴击率6%，射弹速度20%
  - 提升火枪暴击率1%
  - 提升战术霰弹枪伤害2点
  - 降低霹雳法杖魔力消耗1点
  - 提升太空枪增加能穿透的敌怪数量2个
  - 降低魔法飞刀伤害3点
  - 提升水晶风暴伤害2点
  - 提升脊柱骨鞭伤害2点
  - NPC不需要幸福条件出售晶塔
  - 猎鹰刃，异域弯刀攻击敌人会短暂提升12%伤害，最高可到达50%，伤害提升以每秒30%的速度衰减
  - 黑曜石匣，狱石匣中的矿物内容物替换为狱石，并且额外包含5%概率的地狱熔炉
  - 地牢匣，围栏匣增加50%几率获取地牢砖
  - 提升泰拉魔刃获取权重2点
- 重写敌怪旗，现在可以直接在怪物图鉴里获取
- 交叉骨攻击时带有4tick无敌帧
- 钛金碎片屏障的伤害源改为玩家
- 将后期饰品也作防御减半处理
- 梯凳的最大额外高度改为7
- 先触发翅膀，再触发飞毯
- 仅汇流来世的NPC可以无视玩家伤害

## 更新内容：

- 同步泰拉瑞亚1.4.5版本：
  - 占卜球
  - 将史莱姆王冠丢入微光可以召唤史莱姆雨
  - 巨石世界会在下雨和雷暴时下巨石雨
- 蜂蜜史莱姆：将蓝紫绿史莱姆浸泡到蜂蜜一段时间后转化，现在会掉落大量蜜凝糖
- 松树系列，pine地物有四分之一的概率替换为汇流来世的松树
- 仙灵木系列
- 现在发光蘑菇地上会生长超大型生命蘑菇
- 现在生命蘑菇可以被骨粉催熟至大型生命蘑菇

# Changelog (1.2.2):

## Important Notes:

- The detection list of the Lifeform Analyzer has been updated. You need to manually reset the
  configuration of Terra accessories to refresh the list.

## Fixes:

- Fixed an issue where creating a world would generate Hungry (Devourer segments) when "Advanced
  Loot Info" mod is also loaded.
- Fixed the Lethargic effect configuration not working.
- Fixed being able to see wires even when the Mechanical Prism is turned off.
- Fixed auto-attack affecting lances.
- Fixed fishing quests failing to distinguish between evil biomes.
- Fixed the Shimmer Transmutation Pot consuming all Aether Blocks.
- Fixed the Slime Rain event not triggering.
- Suppressed error messages from the Shimmer Transmutation Pot.
- Fixed a chunk loading deadlock caused by the "Buff All Creatures" option in Terra Creature
  Configuration.
- Fixed Cursed Skull enemies in the Dungeon not being highlighted by Hunter Potion.
- Fixed compatibility with AzureLib (again).
- Fixed missing bonuses added by Confluence to vanilla loot tables.
- Fixed a Bestiary crash.
- Fixed Bestiary data loss when reloading resource packs.
- Fixed the clear command not working.
- Fixed confusion between natural life regeneration and manual healing.
- Fixed various bugs related to Step Stools.
- Fixed incorrect Bestiary keys.

## Optimizations:

- Added smoother movement for yoyos.
- Confluence custom window title.
- NPCs no longer spawn inside walls or in mid-air.
- At 18:00 (tick 12000), there's a 20% chance for the moon to be replaced by a special version.
- Adjusted Crossbone model offset and improved Spiky Ball rolling/sliding.
- Opening crates and upgrading coins no longer requires pointing at empty space.
- Rewrote the team system. PvP is now automatically turned off when exiting a world (Multiplayer
  only).
- Added more Bestiary entries.
- Spelunker Potion effect, Dangersense Potion effect, Gravitation Potion effect, and the Metal
  Detector item now display their hotkey in the inventory.
- Increased Bee Gun projectile sensitivity.
- Added support for MrCrayfish's Furniture Mod items as valid NPC housing furniture. chair, sofa,
  and stool series count as chairs; table and desk series count as tables.

## Adjustments:

- Synchronized with Terraria 1.4.5:
  - Replaced Suspicious Looking Eye with Deer Thing in the Frozen Chest loot table.
  - Added Slime Staff (3.33% chance) to King Slime's Treasure Bag loot table.
  - Unlocking 16 Bestiary entries is now required to buy the Leather Whip from the Zoologist.
  - Increased materials required for crafting Bottomless Bags from 3,996 to 9,999.
  - Increased Bat Bat attack speed.
  - Increased Gladius attack speed.
  - Purple Clubberfish now has a 50% chance to inflict Confused.
  - The Beekeeper now has a 100% chance to inflict Confused.
  - Increased Storm Spear projectile range and damage.
  - Increased Spiky Ball duration by 33%.
  - Increased Flintlock Pistol critical strike chance by 6% and projectile speed by 20%.
  - Increased Musket critical strike chance by 1%.
  - Increased Tactical Shotgun damage by 2.
  - Decreased Thunder Zapper mana cost by 1.
  - Increased Space Gun's enemy penetration limit by 2.
  - Decreased Magic Dagger damage by 3.
  - Increased Crystal Storm damage by 2.
  - Increased Spinal Tap damage by 2.
  - NPCs no longer require happiness conditions to sell Pylons.
  - Falcon Blade and Exotic Scimitar gain a temporary 12% damage increase on hit, stacking up to
    50%. The bonus decays at 30% per second.
  - Replaced mineral contents in Obsidian Lock Boxes and Hellstone Lock Boxes with Hellstone, and
    added a 5% chance for a Hellforge.
  - Increased chance to get Dungeon Brick from Dungeon and Locked Golden Chests by 50%.
  - Increased Terra Blade acquisition weight by 2.
- Rewrote Enemy Banners. They can now be obtained directly from the Bestiary.
- Crossbone gains 4 ticks of invincibility frames when attacking.
- Titanium Shard barrier damage source is now changed to the player.
- Defense-halving now also applies to late-game accessories.
- Maximum additional height for Step Stools changed to 7.
- Wings activate before Flying Carpet.
- Only Confluence NPCs are immune to player damage.

## New Content:

- Synchronized with Terraria 1.4.5:
  - Scrying Orb.
  - Throwing a Slime Crown into Shimmer now triggers a Slime Rain.
  - Boulder World now experiences Boulder Rain during Rain and Thunderstorms.
- Honey Slime: Created by soaking Blue, Purple, or Green Slimes in Honey for a period. Now drops
  large amounts of Nectar.
- Pine series: Pine surface features have a 25% chance to be replaced with Confluence Pine trees.
- Added Faewood series.
- Giant Life Mushrooms now grow in surface Glowing Mushroom biomes.
- Life Mushrooms can now be grown into Giant Life Mushrooms using Bone Meal.
