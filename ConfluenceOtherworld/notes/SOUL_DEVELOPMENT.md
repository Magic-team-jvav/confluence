# 魂师开发与 AI 扩展指南

更新日期：2026-10-16。适用模块：ConfluenceOtherworld；Minecraft 1.21.1、NeoForge 21.1.235、Java 21。

本文以当前源码为准。数值、模式、施法、技能状态、标签、收集与恢复、已给出技能和物品均已接入；隐匿者盔甲、具体仪式结构与材料、未指定的副武器和法器没有虚构实现。完整机制接入与本次魂师命名、创造栏图标和透支标签调整均已通过两个模块的 IDEA Data 及其编译前置；本次两个 Data 日志均为 BUILD SUCCESSFUL。未做游戏实测。生成记录、日志与范围见第 9 节。

## 1. 接手前必须遵守

- 魂师是独特的法师变体。灵魂钓手、噬魂怪、亡魂射弹及其配方、掉落、标签不属于本系统。
- 魂师职业的包段、界面资源目录与职业 ID 使用 soulcerer；灵魂值、灵魂伤害、灵魂代偿和具体灵魂名称继续使用 soul。
- 先读取用户开发规范、当前项目规范及相关专项 Skill。通过 IntelliJ IDEA / IntelliJ Index MCP 查询、编辑和诊断，不绕过可用 IDE。
- 保留现有 CRLF、缩进、排序、用户修改；现有文件局部补丁，新文件新增，不整文件覆盖。没有明确要求时不运行 Data、构建、测试、同步、重载或游戏。
- 通用魂师操作直接放静态 PlayerUtils，不新建 SoulHandler 或 PlayerUtils 内嵌魂师子类。附件通过 SoulStorage.of / SoulSkillStorage.of 获取，不增加 getSoulStorage 等包装。
- ClientSoulHandler 是纯静态客户端入口。common 不调用 Minecraft、HUD、renderer 等纯客户端类。
- SoulSkillStack 不允许 null。空技能使用 SoulSkillStack.Empty；不额外加入 Objects.requireNonNull、空值适配层或未要求的检查。
- 技能效果写在具体技能类中，不在注册参数中传入效果回调。
- Confluence.SOUL_SKILLS 当前仍沿用项目的 LibUtils.isDev() 开发开关，继续约束施法、资源切换等机制。魂师 HUD 注册、样式配置、总览/快捷面板输入不再检查开发环境；仍保留手持主武器等原有显示条件。完整机制的正式启用属于独立决策。

## 2. 文件职责与入口

以下源码路径均相对 ConfluenceOtherworld/src/main/java/org/confluence/mod。

| 文件 / 包 | 职责 |
| --- | --- |
| common/attachment/SoulStorage.java | 基本数值、成长份数、恢复比例、资源模式、NBT 和原生附件同步；上限读取实体额外容量属性。 |
| common/attachment/SoulSkillStorage.java | 装备槽、当前索引、永久学习、临时授予、当前可用技能、按技能 ID 共享的冷却。 |
| util/PlayerUtils.java | 资源切换、施法判定和结算、收魂/恢复、技能可用性、法器限制。 |
| common/soulcerer/skill/SoulSkill.java | 技能定义、消耗/冷却读取、条件与双端执行接口、技能 Tag 查询。 |
| common/soulcerer/skill/SoulSkillStack.java | 技能实例及冷却显示数据；Empty 是共享空实例。 |
| common/soulcerer/skill/SoulSkillParameters.java | 已确认内容的集中测试参数。冷却使用 tick。 |
| common/init/ModSoulSkills.java | 正式技能注册表与现有界面分类；保留缓存原型的安全初始化。 |
| common/init/ModSoulSkillTags.java | SOUL、BLOOD_NATURE、STAR_LURE 标签键。标签与界面分类用途不同。 |
| common/item/soulcerer、common/init/item/SoulItems.java | 主武器、副武器、法器、技能书、星核及具体物品注册。 |
| common/item/soulcerer/SoulSecondaryWeaponItem.java | 副武器类型与构造基类；效果和伤害规则由具体职业定义，灵魂资源复用 PlayerUtils。 |
| common/entity/soulcerer、common/effect/soulcerer | 引魂弹幕、灵魂激荡冲击波、强力附魂效果。 |
| client/handler/soulcerer、client/gui/**/soulcerer、client/renderer/entity/soulcerer | 客户端输入、界面、HUD、成功通知表现和实体渲染。 |
| api/event/*Soul*Event.java | 消耗、独立生命代偿、恢复、收集、技能基础总伤害的扩展事件；AdditionalSoulEvent 不再参与容量计算。 |
| common/data/gen/tag/ModSoulSkillTagsProvider.java | 技能 Tag 生成源。 |
| common/data/gen/language/SoulLanguageSubProvider.java | 新增魂师中英文键；原有技能名仍由 InterfaceLanguageSubProvider 生成。 |

独立伤害和属性位于 Confluence-Magic-Lib：ConfluenceMagicLib.SOUL_DAMAGE、ConfluenceMagicLib.ADDITIONAL_SOUL、LibAttributes、LibDamageTypes、LibGameEvents、LibModEvents、LibLanguageProvider。

## 3. 数值、模式与生命周期

- 默认魔力模式，当前灵魂为 0；初始永久灵魂上限 20。
- soulsCount 是成长份数，每份 20，范围 1～20，永久上限因此为 400。
- 总上限 = 永久成长上限 + 实体 ADDITIONAL_SOUL 属性；额外容量默认 0，范围 0～Integer.MAX_VALUE，小数部分向下取整。400 不限制装备、饰品等额外容量；派生上限实时读取，不缓存。
- SoulStorage.addSoul(1/-1) 调整成长份数，即上限增加/减少 20。创造星核调的是上限，不直接填满当前灵魂。
- 切入灵魂模式时魔力归零，切回魔力时灵魂归零。普通切换不补目标资源。
- PlayerUtils.switchSoulMode(player, mode, targetResource) 可在切换后补指定目标资源，供未来技能使用；这不是魔力与灵魂一比一兑换。
- convertManaToSoul / convertSoulToMana 仍是未定义比例的预留入口，不实现或推断兑换公式。
- ManaStorage 底层接收和提取都检查模式；PlayerUtils 的创造免消耗、主动/自然回蓝、自动药水入口同样遵守互斥。
- 构造与反序列化不互相读取另一种附件，避免加载循环。登录、复活、换维同步入口负责规范非活动资源。
- 附件保留 copyOnDeath。当前死亡不重置成长、模式、当前灵魂或技能冷却；onDeath / onRespawn 是明确的生命周期入口。
- 用户已确认系统从未实际启用，本轮没有旧存档迁移规则或迁移版本字段。
- Iron 与 Ars 仅在既有共享魔力配置启用时受模式限制。进入灵魂模式取消 Iron 尚未完成的读条；已经释放的弹幕和效果不被追溯取消。

## 4. 服务端判定与双端执行

主武器右键通过原版物品交互包到达服务器，服务端调用 PlayerUtils.tryUseSoulSkill。不要为同一次右键再次发送 SoulSkillUsePacketC2S，否则会重复请求。ClientSoulHandler.requestSoulSkillUse 留给未来独立输入。

成功流程：

1. 检查开发开关、灵魂模式、主手主武器和当前非空技能。
2. 刷新永久/临时来源与当前可用性；检查按技能 ID 的冷却及 canUse。
3. 发布 SoulConsumeEvent，扣除已有灵魂，计算不足部分的生命代偿。
4. 设置共享冷却、同步技能状态，复制这次施法的技能栈。
5. 服务端调用 skill.use(player, stack)。
6. 发送成功通知给追踪施法者的客户端及本人，使用 sendToPlayersTrackingEntityAndSelf 一次，不另发本人。
7. 客户端根据包中的施法者实体 ID、实际技能 ID 和栈冷却值重建临时栈，然后调用同一 use。
8. 以 SOUL_OVERDRAFT 来源进入 NeoForge 伤害流程，由伤害事件和标签处理代偿与死亡。

canUse(ServerPlayer, SoulSkillStack) 只在服务端判定。isAvailable(Player) 表示解锁来源存在后该技能的持续可用条件。use(Player, SoulSkillStack) 两端分别执行一次；客户端不提前预测、不再次付款或判定。

具体技能必须区分逻辑侧：服务端生成实体、造成伤害和施加 Buff；客户端生成本地表现。实体已通过原版跟踪同步时，客户端不可再生成第二个实体，也不可重复播放已由服务端粒子协议发出的同一效果。

失败时不发成功事件；若本次判定更新了临时可用性，拒绝路径仍发送一次状态校正。成功通知不修改当前索引、装备、永久学习或远端玩家附件。新追踪者通过实体状态看到持续效果，不重放已经结束的瞬时施法。

## 5. 同步协议

| 通道 | 内容与接收者 |
| --- | --- |
| SoulStorage 原生附件同步 | 成长份数、当前灵魂、恢复比例与模式，读取时绑定所属实体；额外容量走原生实体属性同步。常规变化更新仅本人，初次同步沿 NeoForge 框架规则，也可能发给追踪者。 |
| SoulSkillSyncPacketS2C | 技能数据快照，不包含当前索引；仅本人。临时/可用集合只用于网络，不作为永久学习保存。 |
| SoulSkillIndexRequestPacketC2S / SoulSkillIndexSyncPacketS2C | 当前技能索引，独立 VAR_INT，不携带技能附件。 |
| SoulSkillUsePacketC2S | 空请求，服务端读取自己的当前选择。 |
| SoulSkillUsePacketS2C | 本次成功的 entityId、skillId、cd、maxCd；发施法者及追踪者。 |
| ManaPacketS2C | 复用原有魔力数值缓存协议。 |

生命周期初始化依次发送技能数据和索引。普通状态包只还原状态、刷新界面，绝不调用 use。当前技能数据同步按类别与索引分离，不是通用的逐字段增量协议。

冷却按技能 ID 保存在 skillCooldowns，装备栈 CD 是镜像。卸下、换槽、重复装备不能重置冷却；不同槽的同一技能共享冷却。服务端推进真值，客户端仅本地倒计时显示；开始、结束、可用性变化及生命周期初始化发送校正，不每 tick 发整份快照。

装备、替换请求必须检查服务端可用性，客户端传来的 CD 不能覆盖权威冷却。客户端不能调整服务端槽容量；SET_SLOT_COUNT 请求仅触发认可容量回传。永久学习不通过旧 UNLOCK_ALL 原型请求。

## 6. 伤害、代价、收集与恢复

灵魂伤害是独立 DamageType，SOUL_DAMAGE 属性默认倍率 1。基础总伤害为技能伤害加主武器伤害，先经过 SoulSkillDamageEvent，再由 MagicLib 的灵魂分支乘一次属性。该来源不进入魔法或远程伤害标签，避免叠乘其他职业属性；通用伤害流程仍可继续处理。

副武器基类为 SoulSecondaryWeaponItem，继承 CustomRarityItem；只提供类型与构造骨架，灵魂资源复用 PlayerUtils，效果和伤害来源、倍率由具体职业定义。技能面板仍按 SoulWeaponItem 判定。

血量代偿默认每缺少 1 点灵魂扣除 4 点生命。先使用剩余灵魂，再对不足部分结算生命代偿；SoulConsumeEvent 可修改消耗和比例或取消整个消耗。效果和成功通知之后，以独立 SOUL_OVERDRAFT 来源调用 hurt，进入 NeoForge 伤害流程；LivingIncomingDamageEvent 根据 confluence_magic_lib:soul_overdraft 标签转发 SoulOverdraftEvent，允许修改或取消本次代偿，取消不回滚已提交的灵魂消耗、技能效果和冷却。分类标签加入 BYPASSES_ARMOR、BYPASSES_SHIELD、BYPASSES_INVULNERABILITY、BYPASSES_COOLDOWN、BYPASSES_EFFECTS、BYPASSES_RESISTANCE、BYPASSES_ENCHANTMENTS 和 NO_KNOCKBACK。吸收生命通过 NeoForge 的 ABSORPTION 减伤回调归零，代偿保留已有受伤无敌时间且不额外增加；闪避、微光与饰品无敌入口遵守 BYPASSES_INVULNERABILITY。生命变化、战斗记录、死亡归因、文本和掉落由游戏伤害流程处理。

收魂仅在灵魂模式发生，复用玩家攻击归属：

- 普通敌怪收益 = (最大生命 × 0.8 + 攻击属性 × 0.1 + 护甲 × 0.1) / 20，基础收益至少 1。
- 普通公式可保留正小数；不把百分比恢复的向上取整扩展到所有收魂。
- SoulCollectionEvent 可以修改普通收益或取消；事件可把收益设为 0。
- Boss 本体直接回满当前灵魂；Boss 部件不单独收魂。双子管理本体通过眼球死亡前记录的最后击杀玩家归属结算。
- 使用最大生命，避免在死亡时读取已经为 0 的当前生命。

每秒恢复一次：存储恢复比例 + 基础环境比例，经 SoulRecoveryEvent 修改后，最后计算 ceil(总上限 × 最终比例)。灵雾墓地与灵魂沙峡谷任一满足时提供 5%，同时满足不重复叠加。墓地判定复用服务端 ModBlockCounters，不读取客户端雾效配置。特殊方块、附魔、套装和饰品通过事件接入，不把具体判断塞进附件。

## 7. 学习、临时授予与具体内容

永久学习和当前可用性是两个概念。初始永久学会 soul_lure 和 enhanced_soul，不自动装备。书、仪式或被动使用 learnSoulSkill；已有永久记录时返回 false，不消费技能书。

主武器 getGrantedSoulSkills 提供临时 ID 集合。永久记录与临时来源合并后，再用 isAvailable 检查持续条件。来源或条件失效时关闭可用状态，保留装备与冷却；恢复条件后重新可用。不要通过删除永久学习记录实现暂时锁定。

| ID / 类型 | 当前实现与测试值 |
| --- | --- |
| soul_lure / 引魂 | 技能伤害5，消耗1，CD20tick；远程单体弹幕，速度5格/tick、最大距离5格、最长100tick。 |
| enhanced_soul / 强力附魂 | 消耗1，CD20tick；Buff持续100tick，灵魂属性总倍率×5，不是增加5%。 |
| spirit_surge / 灵魂激荡 | 技能伤害5，消耗1，CD20tick；水平半径5、竖向范围5，扩散100tick，每圈5粒子，每目标一次。 |
| survival_instinct / 求生欲 | 主动使用条件为血量低于40且已有耐药性；消耗10，CD20tick，成功时耐药剩余时长减半一次，不每tick自动减半。 |
| broken_glimmer / 残破微光 | 主武器基础灵魂伤害5，白色稀有度、不可堆叠；手持临时授予求生欲。 |
| fallen_soul_core / 堕魂星核 | 普通使用切模式，成功后同类型1秒CD；非创造消耗，创造保留。创造潜行右键增加上限一份，左键减少一份。 |
| soul_skill_book / 通用技能书 | SOUL_SKILL ResourceLocation组件绑定具体技能；保留灵魂激荡与求生欲永久学会书。 |

求生欲缺独立图标，当前复用引魂图标而保留自己的技能 ID。强力附魂 mob_effect 图标由现有技能图标原字节复制，不引入新美术。其他既有技能仍为原型，具体效果待规则确认。

创造栏“汇流来世 | 魂师”位于法师武器与召唤师武器之间，图标使用 IconItems.SOULCERER_ICON，堕魂星核置于首位。其后依次为主武器、副武器、法器、技能书四组：残破微光放主武器组；副武器、法器保留空组；技能书组保留未绑定书、灵魂激荡书和求生欲书。关闭 itemGroups 时按相同顺序平铺。星核同时保留在杂项的增益组首位，其他魂师物品已从杂项迁出。分组及内容由 ModTabs.SOUL 与 acceptSoulGroup 管理。

法器复用原版 ItemCooldowns，同物品类型共享 CD。物品栏有3格法器正在冷却时不能再用更多法器；格数不是堆叠个数。快捷栏1/2/3通过实际数字键选槽后调用原版 useItem，尊重按键重绑定，不改 Shift。离开物品栏不重置原有计时，但该格不再占用3格限制；原版物品冷却不提供离线持久化。技能冷却与法器原生冷却不要混为一套数据。

## 8. 扩展操作顺序

### 新技能

1. 在 common/soulcerer/skill 新建类，继承 SoulSkill，构造配置基础伤害、消耗与 tick 冷却。
2. 持续解锁条件覆写 isAvailable，单次使用条件覆写 canUse，效果覆写 use。
3. ModSoulSkills 添加稳定英文 ID 与缓存原型注册，沿现有返回供应器方式；不要在静态初始化提前调用尚未完成注册的 DeferredHolder.get。
4. 为技能分配 Tag；需要数据包调整时修改 Tag 生成源，运行 Data 需另获明确授权。
5. 用学习 API、主武器临时授予或条件来源接入；新增条件可用性由服务端重算。
6. 新增图标、活跃语言定义和确有必要的总览节点，不移动原节点布局。
7. 共享 Buff 可监听 SoulSkillDamageEvent 并查询 event.getSkillStack().getSoulSkill().is(tag)，再修改基础总伤害。不要再次乘全局 SOUL_DAMAGE。

### 新主武器、副武器与法器

- 主武器继承 SoulWeaponItem；getSoulDamage 返回未乘属性的基础值，临时技能通过 getGrantedSoulSkills 提供。
- 新副武器可继承 SoulSecondaryWeaponItem，在具体类中实现所属职业的效果和伤害，只在服务端调用灵魂资源 API。已有职业武器可直接复用资源 API。
- 法器继承 SoulArtifactItem，配置消耗和 tick 冷却，实现 canUse / useArtifact；不要绕过 tryUseSoulArtifact 的3格校验和支付。
- SOUL_ARTIFACT_QUICK_SLOT_COUNT 与 MAX_COOLING_SOUL_ARTIFACT_SLOTS 是独立规则，不把快捷槽数量作为冷却上限来源。

### 容量、恢复、收集与仪式

- 装备额外上限使用 ConfluenceMagicLib.ADDITIONAL_SOUL 的实体属性修饰符，直接增加容量使用 ADD_VALUE；不要每tick调用 addSoul 当作临时容量。
- 仪式完成且材料/条件验证成功后，再调用 SoulStorage.of(player).addSoul(份数) 改永久成长。
- 盔甲、饰品、附魔或特殊方块恢复监听 SoulRecoveryEvent；收集增益监听 SoulCollectionEvent。
- 隐匿者的属性、套装效果、仪式结构、材料、次数及完成条件仍待提供，不自行填写或登记空壳成品。

## 9. 数据生成与获取途径

- 活跃入口是 ModLanguageProvider。新增魂师键写 SoulLanguageSubProvider；原有技能名沿用 InterfaceLanguageSubProvider，避免重复添加。旧 ModChineseProvider / ModEnglishProvider 不再是入口。
- 技能 Tag 在 ModSoulSkillTagsProvider，由 ModDataGenerator 挂接。
- 残破微光加入原版 minecraft:chests/simple_dungeon 的现有宝箱追加生成链，不再新建一套 GLM。
- 灵魂激荡书加入世吞/克脑现有普通、专家、大师六张奖励表，不加 Boss 部件掉落。
- 沿已确认测试值：独立池5%概率、命中数量5、条目权重5。权重不是概率。
- 当前没有提供这些内容的合成配方，因此不虚构材料。后续指定配方后写现有 recipe Provider。
- 本轮承接用户的数据生成授权，依次通过 IDEA 的 Confluence-Magic-Lib - Data 和 ConfluenceOtherworld - Data 执行，最终均为 BUILD SUCCESSFUL。透支伤害类型 JSON 已生成；三个技能 Tag、中英文提示、技能书模型与奖励输出已读取。
- Data 的编译前置步骤发现并修正了本轮快捷法器局部玩家类型；语言生成发现并删除了新入口中重复的 22 个原技能名。最终源码诊断无错误，仍有未使用预留接口等警告。
- Data 按整个模块生成，实际改动 18 个非缓存生成文件及 13 个缓存文件；其中也刷新了工作区已有暗影蜡烛、墓碑标签、城镇小动物标签、饰品数据映射和 DataTip 输出，对应业务源码未由本轮修改。
- 仍有 EMILoot 的既有战利品警告：minecraft:entities/slime 引用 confluence:gameplay/slime_carry 未通过该 Provider 的校验。本轮未修正此非魂师问题，Data 仍正常结束。
- 成功日志备份：[Magic Lib Data](../../notes/_tmp_soul_complete_backup_20261016_1792136225642/data-logs/lib-data-success.log)、[主模块 Data](../../notes/_tmp_soul_complete_backup_20261016_1792136225642/data-logs/main-data-success.log)。
- 魂师创造栏图标、soulcerer 命名对应的物品模型、技能标签和中英文键，以及灵魂透支分类与绕过标签均已通过 IDEA Data 生成。旧魂师技能标签文件和技能书模型已由 Data 清理。
- 本次两个模块合计 29 项非缓存生成路径变化（含新增、修改、删除）及 10 个缓存文件变化；其中六张世吞/克脑奖励表同步更新技能书物品 ID，未改变奖励规则。
- 魂师命名与图标 Data 成功日志：[Magic Lib Data](../../notes/_tmp_soulcerer_naming_backup_20261016_212106_9347/generated-before-data/logs/magic-lib-data-success.log)、[主模块 Data](../../notes/_tmp_soulcerer_naming_backup_20261016_212106_9347/generated-before-data/logs/main-data-success.log)。
- 本次运行前的 10,197 个生成文件已备份：[生成备份](../../notes/_tmp_soulcerer_naming_backup_20261016_212106_9347/generated-before-data)、[差异清单](../../notes/_tmp_soulcerer_naming_backup_20261016_212106_9347/generated-before-data/generated-diff.json)。
- 后续再次运行 Data、构建或游戏仍需用户明确要求。

## 10. 后续检查与交接

先读源码和对应 IDEA 诊断。以下是需要游戏验证的行为清单，不代表本轮已经执行：

- 普通/创造切换、死亡/复活/换维后模式和资源保留；非活动资源所有增减入口保持0。
- 主武器右键只施放一次；无效条件或取消支付不产生成功表现。
- 本人及旁观者各收到一次表现，切换技能后不播放错误技能。
- 缺少灵魂时4:1基础代偿、减伤影响、事件取消、致死前完成施法。
- 多槽同技能、卸下重装、重登后的技能冷却；客户端暂停与服务端校正。
- 灵雾、魂沙谷、双环境重合、低上限和额外容量时的百分比向上取整。
- 普通敌怪、Boss本体、部件、双子最终击杀归属与回满。
- 临时授予失效仍装备、重新满足条件、永久书学会后不丢记录。
- 法器3格限制、同类型共享CD、前三槽快捷使用及普通物品不误触发。
- Ars/Iron 未安装、共享配置关闭、创造/零成本技能、切模式取消读条。
- 新 Data 输出与中英文提示、图标、创造物品栏、Boss奖励及地下宝箱。

修改前源码与原计划、Data 前的 10,191 个生成文件均已备份到仓库根 notes/_tmp_soul_complete_backup_20261016_1792136225642。Git 提交和推送本轮未执行。

## 11. 本轮修改文件索引

Java 源码共 68 个文件（新增 25 个）；均保留 CRLF。以下链接供后续 AI 定位本轮实现。

### Confluence-Magic-Lib

- [common/data/gen/LibLanguageProvider.java](../../Confluence-Magic-Lib/src/main/java/org/confluence/lib/common/data/gen/LibLanguageProvider.java)
- [common/event/LibGameEvents.java](../../Confluence-Magic-Lib/src/main/java/org/confluence/lib/common/event/LibGameEvents.java)
- [common/event/LibModEvents.java](../../Confluence-Magic-Lib/src/main/java/org/confluence/lib/common/event/LibModEvents.java)
- [common/LibAttributes.java](../../Confluence-Magic-Lib/src/main/java/org/confluence/lib/common/LibAttributes.java)
- [common/LibDamageTypes.java](../../Confluence-Magic-Lib/src/main/java/org/confluence/lib/common/LibDamageTypes.java)
- [ConfluenceMagicLib.java](../../Confluence-Magic-Lib/src/main/java/org/confluence/lib/ConfluenceMagicLib.java)

### ConfluenceOtherworld

- [api/event/AdditionalSoulEvent.java](../src/main/java/org/confluence/mod/api/event/AdditionalSoulEvent.java)
- [api/event/SoulCollectionEvent.java](../src/main/java/org/confluence/mod/api/event/SoulCollectionEvent.java)
- [api/event/SoulConsumeEvent.java](../src/main/java/org/confluence/mod/api/event/SoulConsumeEvent.java)
- [api/event/SoulRecoveryEvent.java](../src/main/java/org/confluence/mod/api/event/SoulRecoveryEvent.java)
- [api/event/SoulSkillDamageEvent.java](../src/main/java/org/confluence/mod/api/event/SoulSkillDamageEvent.java)
- [client/ClientConfigs.java](../src/main/java/org/confluence/mod/client/ClientConfigs.java)
- [client/ModKeyBindings.java](../src/main/java/org/confluence/mod/client/ModKeyBindings.java)
- [client/event/GameClientEvents.java](../src/main/java/org/confluence/mod/client/event/GameClientEvents.java)
- [client/event/ModClientEvents.java](../src/main/java/org/confluence/mod/client/event/ModClientEvents.java)
- [client/gui/container/soulcerer/SoulOverviewScreen.java](../src/main/java/org/confluence/mod/client/gui/container/soulcerer/SoulOverviewScreen.java)
- [client/gui/hud/soulcerer/CurrentSelectedSkillHud.java](../src/main/java/org/confluence/mod/client/gui/hud/soulcerer/CurrentSelectedSkillHud.java)
- [client/gui/hud/soulcerer/quick_skill/BasicSoulQuickSkillHud.java](../src/main/java/org/confluence/mod/client/gui/hud/soulcerer/quick_skill/BasicSoulQuickSkillHud.java)
- [client/gui/hud/TerraStyleManaHud.java](../src/main/java/org/confluence/mod/client/gui/hud/TerraStyleManaHud.java)
- [client/gui/widget/soulcerer/skill/overview/HotbarWidget.java](../src/main/java/org/confluence/mod/client/gui/widget/soulcerer/skill/overview/HotbarWidget.java)
- [client/gui/widget/soulcerer/skill/overview/OverviewNode.java](../src/main/java/org/confluence/mod/client/gui/widget/soulcerer/skill/overview/OverviewNode.java)
- [client/gui/widget/soulcerer/skill/SoulSkillBox.java](../src/main/java/org/confluence/mod/client/gui/widget/soulcerer/skill/SoulSkillBox.java)
- [client/handler/soulcerer/ClientSoulHandler.java](../src/main/java/org/confluence/mod/client/handler/soulcerer/ClientSoulHandler.java)
- [client/renderer/entity/soulcerer/SoulLureRenderer.java](../src/main/java/org/confluence/mod/client/renderer/entity/soulcerer/SoulLureRenderer.java)
- [common/attachment/ManaStorage.java](../src/main/java/org/confluence/mod/common/attachment/ManaStorage.java)
- [common/attachment/SoulSkillStorage.java](../src/main/java/org/confluence/mod/common/attachment/SoulSkillStorage.java)
- [common/attachment/SoulStorage.java](../src/main/java/org/confluence/mod/common/attachment/SoulStorage.java)
- [common/data/gen/language/SoulLanguageSubProvider.java](../src/main/java/org/confluence/mod/common/data/gen/language/SoulLanguageSubProvider.java)
- [common/data/gen/loot/GiftSubProvider.java](../src/main/java/org/confluence/mod/common/data/gen/loot/GiftSubProvider.java)
- [common/data/gen/loot/modifiers/AddChestLootConfluenceSubProvider.java](../src/main/java/org/confluence/mod/common/data/gen/loot/modifiers/AddChestLootConfluenceSubProvider.java)
- [common/data/gen/ModDataGenerator.java](../src/main/java/org/confluence/mod/common/data/gen/ModDataGenerator.java)
- [common/data/gen/ModItemModelProvider.java](../src/main/java/org/confluence/mod/common/data/gen/ModItemModelProvider.java)
- [common/data/gen/ModLanguageProvider.java](../src/main/java/org/confluence/mod/common/data/gen/ModLanguageProvider.java)
- [common/data/gen/tag/ModSoulSkillTagsProvider.java](../src/main/java/org/confluence/mod/common/data/gen/tag/ModSoulSkillTagsProvider.java)
- [common/effect/soulcerer/EnhancedSoulEffect.java](../src/main/java/org/confluence/mod/common/effect/soulcerer/EnhancedSoulEffect.java)
- [common/entity/boss/AbstractTwinEye.java](../src/main/java/org/confluence/mod/common/entity/boss/AbstractTwinEye.java)
- [common/entity/soulcerer/SoulLureProjectile.java](../src/main/java/org/confluence/mod/common/entity/soulcerer/SoulLureProjectile.java)
- [common/entity/soulcerer/SpiritSurgeEntity.java](../src/main/java/org/confluence/mod/common/entity/soulcerer/SpiritSurgeEntity.java)
- [common/event/game/entity/LivingEntityEvents.java](../src/main/java/org/confluence/mod/common/event/game/entity/LivingEntityEvents.java)
- [common/event/game/entity/PlayerEvents.java](../src/main/java/org/confluence/mod/common/event/game/entity/PlayerEvents.java)
- [common/event/game/TickEvents.java](../src/main/java/org/confluence/mod/common/event/game/TickEvents.java)
- [common/init/entity/ModEntities.java](../src/main/java/org/confluence/mod/common/init/entity/ModEntities.java)
- [common/init/item/SoulItems.java](../src/main/java/org/confluence/mod/common/init/item/SoulItems.java)
- [common/init/ModAttachmentTypes.java](../src/main/java/org/confluence/mod/common/init/ModAttachmentTypes.java)
- [common/init/ModDataComponentTypes.java](../src/main/java/org/confluence/mod/common/init/ModDataComponentTypes.java)
- [common/init/ModEffects.java](../src/main/java/org/confluence/mod/common/init/ModEffects.java)
- [common/init/ModSoulSkills.java](../src/main/java/org/confluence/mod/common/init/ModSoulSkills.java)
- [common/init/ModSoulSkillTags.java](../src/main/java/org/confluence/mod/common/init/ModSoulSkillTags.java)
- [common/init/ModTabs.java](../src/main/java/org/confluence/mod/common/init/ModTabs.java)
- [common/item/soulcerer/FallenSoulCoreItem.java](../src/main/java/org/confluence/mod/common/item/soulcerer/FallenSoulCoreItem.java)
- [common/item/soulcerer/SoulArtifactItem.java](../src/main/java/org/confluence/mod/common/item/soulcerer/SoulArtifactItem.java)
- [common/item/soulcerer/SoulSkillBookItem.java](../src/main/java/org/confluence/mod/common/item/soulcerer/SoulSkillBookItem.java)
- [common/item/soulcerer/SoulSecondaryWeaponItem.java](../src/main/java/org/confluence/mod/common/item/soulcerer/SoulSecondaryWeaponItem.java)
- [common/item/soulcerer/SoulWeaponItem.java](../src/main/java/org/confluence/mod/common/item/soulcerer/SoulWeaponItem.java)
- [common/soulcerer/skill/EnhancedSoulSkill.java](../src/main/java/org/confluence/mod/common/soulcerer/skill/EnhancedSoulSkill.java)
- [common/soulcerer/skill/SoulLureSkill.java](../src/main/java/org/confluence/mod/common/soulcerer/skill/SoulLureSkill.java)
- [common/soulcerer/skill/SoulSkill.java](../src/main/java/org/confluence/mod/common/soulcerer/skill/SoulSkill.java)
- [common/soulcerer/skill/SoulSkillParameters.java](../src/main/java/org/confluence/mod/common/soulcerer/skill/SoulSkillParameters.java)
- [common/soulcerer/skill/SpiritSurgeSkill.java](../src/main/java/org/confluence/mod/common/soulcerer/skill/SpiritSurgeSkill.java)
- [common/soulcerer/skill/SurvivalInstinctSkill.java](../src/main/java/org/confluence/mod/common/soulcerer/skill/SurvivalInstinctSkill.java)
- [Confluence.java](../src/main/java/org/confluence/mod/Confluence.java)
- [integration/ars_nouveau/ArsNouveauHelper.java](../src/main/java/org/confluence/mod/integration/ars_nouveau/ArsNouveauHelper.java)
- [integration/irons_spell/IronSpellHelper.java](../src/main/java/org/confluence/mod/integration/irons_spell/IronSpellHelper.java)
- [mixin/client/MinecraftMixin.java](../src/main/java/org/confluence/mod/mixin/client/MinecraftMixin.java)
- [mixin/integration/ars_nouveau/SpellCrossbowMixin.java](../src/main/java/org/confluence/mod/mixin/integration/ars_nouveau/SpellCrossbowMixin.java)
- [mixin/integration/ars_nouveau/SpellResolverMixin.java](../src/main/java/org/confluence/mod/mixin/integration/ars_nouveau/SpellResolverMixin.java)
- [network/c2s/SoulSkillRequestPacketC2S.java](../src/main/java/org/confluence/mod/network/c2s/SoulSkillRequestPacketC2S.java)
- [util/PlayerUtils.java](../src/main/java/org/confluence/mod/util/PlayerUtils.java)

其他源码资源与文档：

- [Ars Mixin 登记](../src/main/resources/confluence.mixins.json)
- [强力附魂效果图标](../src/main/resources/assets/confluence/textures/mob_effect/enhanced_soul.png)
- [已确认内容计划](SOUL_PLAN.md)
- 本指南 SOUL_DEVELOPMENT.md。

Data 实际变化的非缓存文件（含模块内已有内容的重新生成）：

- [Confluence-Magic-Lib/assets/confluence_magic_lib/lang/en_us.json](../../Confluence-Magic-Lib/src/generated/resources/assets/confluence_magic_lib/lang/en_us.json)
- [Confluence-Magic-Lib/assets/confluence_magic_lib/lang/zh_cn.json](../../Confluence-Magic-Lib/src/generated/resources/assets/confluence_magic_lib/lang/zh_cn.json)
- [Confluence-Magic-Lib/data/confluence_magic_lib/damage_type/soul_overdraft.json](../../Confluence-Magic-Lib/src/generated/resources/data/confluence_magic_lib/damage_type/soul_overdraft.json)（新生成）
- [ConfluenceOtherworld/assets/confluence/datatip/functional_block_descriptions.json](../src/generated/resources/assets/confluence/datatip/functional_block_descriptions.json)
- [ConfluenceOtherworld/assets/confluence/lang/en_ud.json](../src/generated/resources/assets/confluence/lang/en_ud.json)
- [ConfluenceOtherworld/assets/confluence/lang/en_us.json](../src/generated/resources/assets/confluence/lang/en_us.json)
- [ConfluenceOtherworld/assets/confluence/lang/zh_cn.json](../src/generated/resources/assets/confluence/lang/zh_cn.json)
- [ConfluenceOtherworld/assets/confluence/models/item/shadow_candle.json](../src/generated/resources/assets/confluence/models/item/shadow_candle.json)（新生成）
- [ConfluenceOtherworld/assets/confluence/models/item/soulcerer_skill_book.json](../src/generated/resources/assets/confluence/models/item/soulcerer_skill_book.json)
- [ConfluenceOtherworld/data/c/tags/block/tombstone.json](../src/generated/resources/data/c/tags/block/tombstone.json)
- [ConfluenceOtherworld/data/confluence/loot_table/blocks/shadow_candle.json](../src/generated/resources/data/confluence/loot_table/blocks/shadow_candle.json)（新生成）
- [ConfluenceOtherworld/data/confluence/loot_table/treasure_bag/eater_of_worlds/classic.json](../src/generated/resources/data/confluence/loot_table/treasure_bag/eater_of_worlds/classic.json)
- [ConfluenceOtherworld/data/confluence/loot_table/treasure_bag/eater_of_worlds/expert.json](../src/generated/resources/data/confluence/loot_table/treasure_bag/eater_of_worlds/expert.json)
- [ConfluenceOtherworld/data/confluence/loot_table/treasure_bag/eater_of_worlds/master.json](../src/generated/resources/data/confluence/loot_table/treasure_bag/eater_of_worlds/master.json)
- [ConfluenceOtherworld/data/confluence/recipe/heavy_work_bench/shadow_candle.json](../src/generated/resources/data/confluence/recipe/heavy_work_bench/shadow_candle.json)（新生成）
- [ConfluenceOtherworld/data/confluence/tags/entity_type/town_critters.json](../src/generated/resources/data/confluence/tags/entity_type/town_critters.json)（新生成）
- [ConfluenceOtherworld/data/confluence/tags/entity_type/town_suppressed_critters.json](../src/generated/resources/data/confluence/tags/entity_type/town_suppressed_critters.json)（新生成）
- [ConfluenceOtherworld/data/terra_curio/data_maps/item/accessories.json](../src/generated/resources/data/terra_curio/data_maps/item/accessories.json)


