游戏时间用LibDateUtils
节日时间用DateUtils
高度、群系、维度用OverworldUtils，且高度范围尽量用返回boolean的方法而不是返回int的方法
游戏阶段用Killboard.INSTANCE.isDefeated衍生而不是Killboard.INSTANCE.getGamePhase衍生
服务端游戏事件用GameEventSystem，客户端游戏事件用ClientGameEventSystem
少用forge事件判断模组内实体，改用实体的类中判断
困难模式用ModUtils.isHardmode判断
游戏难度用LibUtils

# 对于相同事件，在一个逻辑端内不要有重复的事件订阅
- 已经有一个death(LivingDeathEvent event)了，就不许出现deathSoul(LivingDeathEvent event)

# 不要有单独的事件注册总线，全部集中到一个类里订阅
- 方法名必须满足：fooBar$Post(FooBarEvent.Post event)
- 如果注册的事件用了EventPriority，那就写成fooBar\$Post\$\<EventPriority in BigCamelCase\>(FooBarEvent.Post event)
- 如果类名里有Living，就不用写livingDeath可以写death

事件订阅类里不要写非事件订阅方法，如各类工具方法
事件的订阅位置要看该事件继承自什么类，如果是PlayerEvent，就必须在PlayerEvents订阅

套装奖励见ModArmorBonus，判断玩家有没有套装奖励见PlayerSpecialData.of(player).getArmorSetBonusKey()
对于已存在的旧列表式代码，不允许把列表式的新增代码插到列表头
自定义RenderType写到ModRenderTypes中

# 新增生物与敌怪数值

新生物默认体型按 1 设计。缺少的战利品、对应的物品解封图鉴条目及图鉴翻译，应在对应实现处注明。

以 Terraria Wiki 专家模式为普通难度基准。生命、攻击和普通敌怪的特殊攻击取原值的
26%，向上取整后作为微调基础。护甲取专家防御的 45% 向上取整，击退抗性采用专家模式数据。

| 难度        | 普通敌怪生命 | 普通攻击 | 普通敌怪特殊攻击 | Boss 生命 | Boss 攻击 | 全敌怪护甲 |    全敌怪护甲穿透 |        全敌怪韧性 |
|-------------|-------------:|---------:|-----------------:|----------:|----------:|-----------:|------------------:|------------------:|
| 经典 / 简单 |        ×0.50 |    ×0.50 |            ×0.50 |     ×0.50 |     ×0.50 |         ×1 | 基础值 −1，保留 1 | 基础值 −2，保留 1 |
| 专家 / 普通 |           ×1 |       ×1 |               ×1 |        ×1 |        ×1 |         ×1 |            基础值 |            基础值 |
| 大师 / 困难 |        ×1.50 |    ×1.50 |            ×1.50 |    ×1.275 |     ×1.50 |         ×1 |         基础值 +1 |         基础值 +2 |
| 传奇        |        ×2.00 |   ×2.667 |            ×1.50 |    ×1.700 |    ×2.667 |         ×1 |         基础值 +2 |         基础值 +2 |

护甲穿透初值为转换后的伤害乘以 35% 再向上取整。基础值取初值减 2，初值为 1 时保留 1，上限为
12。韧性基础值取专家防御的 20% 向下取整，上限为 8。
