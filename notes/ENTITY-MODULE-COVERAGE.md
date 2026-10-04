# Functional coverage gap: inlining `TerraEntity` + `TerraGuns` into the 1.21 `ConfluenceOtherworld`

**Scope.** Decision already taken: the 1.21 branch (`neoforge-dev/1.21.1`) will be regenerated from the
1.20 tree, entities/guns will be inlined into `ConfluenceOtherworld`, and the `TerraEntity` / `TerraGuns`
modules will no longer be used by the Confluence 1.21 build. This document answers: *what is lost?*

**Repos inspected (read-only, nothing modified, nothing committed, no checkout performed).**

| Label | Path | Branch | HEAD | Notes |
|---|---|---|---|---|
| **A** (harvest source) | `D:\Minecraft\1.20forge\confluence` | `forge-dev/1.20.1` | `a75bda140` (2026-09-25) | MC 1.20.1 / Forge 47.4.20. Entity+guns inlined into `ConfluenceOtherworld`. **No `TerraEntity`/`TerraGuns` module.** 2,496 java files in `ConfluenceOtherworld`, 266 in `Confluence-Magic-Lib`. |
| **B** (what is being dropped) | `D:\Minecraft\1.21neoforge\confluence` | `neoforge-dev/1.21.1` | `c387b9010` (2026-09-22) | MC 1.21.1 / NeoForge 21.1.219. `TerraEntity` @ `62984cf3` (860 java), `TerraGuns` @ `ea14bb6` (99 java) as submodules. |

Also inspected for repo A: `Confluence-Magic-Lib\src` (266 java), `PortLib\src`, `TerraCurio\src`,
`TerraFurniture\src` — because a class "missing from `ConfluenceOtherworld`" may live in one of those.

**Method.** (1) Enumerated every `.java` under B's `TerraEntity\src\main\java` and `TerraGuns\src\main\java`.
(2) Exact-basename match against A's `ConfluenceOtherworld\src` (+ the other four A modules).
(3) Full-identifier scan (678 unresolved names, word-boundary regex) of A to catch renamed/referenced twins.
(4) For every unresolved name, a package/feature-level semantic verdict by reading the A-side package.
(5) **Critically:** because A's git history matters here, every "missing" verdict was re-checked against A's
*history* — see the warning below.
(6) Submodule pointer/commit analysis with `git log`, `git show --name-status`, `git ls-tree`.

### ⚠ The single most important finding about repo A's history

Repo A **already inlined TerraEntity once and then deliberately deleted it**:

| Commit | Date | What happened |
|---|---|---|
| `f6e114cdb` "part21" | 2026-06-24 | TerraEntity framework inlined into `ConfluenceOtherworld` as `org.confluence.mod.common.entity.*` (189 files, +8,115) |
| `2569be361` "part22" | 2026-06-27 | Same tree relocated `common/entity/…` → `util/entity/…` (217 files, +13,218) |
| `7f83b379a` "part23" | 2026-06-27 | continuation |
| `b20c0cefd` "remove all entity part" | 2026-06-27 | **Deleted the whole inlined tree: 250 files, −28,744 lines** |
| 2026-08-01 → `a75bda140` | 2026-09-25 | 295 commits rebuilding entities/AI/NPCs/guns **natively and independently**; the framework was never restored |

Consequences:

1. **`2569be361` (parent of `b20c0cefd`) is the cheapest harvest source for the whole AI/animation framework.**
   At that commit A holds 244 deleted java files (242 unique basenames), of which **162 basenames also exist in
   `TerraEntity`** — already mechanically inlined *and already package-renamed to `org.confluence.mod.*`*.
   Sampled file `git show 2569be361:ConfluenceOtherworld/src/main/java/org/confluence/mod/util/entity/ai/keyframe/Keyframe.java`
   imports only `com.mojang.serialization.*` — platform-neutral, directly reusable.
2. A's **current** entity/NPC/monster/boss tree at HEAD is an *independent rewrite*, not the inlined copy.
   Measured at A HEAD: `common\entity\**` = **552 java files**, of which `monster\**` 117, `projectile\**` 156,
   `npc\**` 66, `boss\**` 47, `animal\**` 41, plus `ai\**`, `mount\**`, `storage\**`, `hook\**`, `flail\**`,
   `fishing\**`, `minecart\**`, `yoyo\**`, `model\**`; `common\summoner\**` = 92 files; `mixin\**` = 135;
   `network\**` = 61; `client\entity\{model,renderer}\**` = 79. Minions are a custom "attachment entity"
   system, NPC AI is vanilla-Goal based plus a self-authored behaviour tree. So "A has no class named X" does
   **not** mean "X's feature is absent" — but it also does not mean the feature was preserved.
3. Since A HEAD is 295 commits ahead of the June inline and is itself actively porting 1.21 content
   (e.g. `dfcc5c041` 2026-08-15 "对齐 1.21 内容与运行时行为", `ce1ca67f8` 2026-08-22 "拉通 1.21 战斗、召唤与实体体系",
   `9bc04295b` 2026-08-19, `90dfd7804` 2026-08-18 "重构剑类、剑气与枪械系统"), several TerraEntity/TerraGuns features
   were re-implemented by hand in A — and, verified below, **most content already is**.

---

## Section 1 — Headline counts

### 1.1 `TerraEntity` — 860 java files (package `org.confluence.terraentity`, plus 6 files of a vendored `com.github.edg_thexu.cafelib`)

| Classification | Files | Meaning |
|---|---:|---|
| `INLINED-SAME` (at A HEAD) | **182** | A class with the same basename exists in A's working tree. 176 are true functional twins; 6 are name collisions (see §2.11). |
| `INLINED-SAME (history-only)` | **162** | Same basename exists in A's git history at `2569be361` (the deleted inline). The mechanical 1.20 port is already done there; needs re-introduction + a diff against B. |
| `INLINED-RENAMED` | **≈300** | No same-named twin, but an equivalent exists at A HEAD under another name/package (verified per feature area below). |
| `GENUINELY-MISSING` | **≈200** | No HEAD twin, no history twin, no verified rename. **This is the harvest TODO.** Explicit list in §3. |
| `LIBRARY-ONLY` | **≈16** | Infrastructure that only makes sense in a standalone jar. |

> The `INLINED-RENAMED` / `GENUINELY-MISSING` / `LIBRARY-ONLY` split is a per-package judgement
> (the exact `INLINED-SAME` / `history-only` numbers are exact basename matches and are reproducible).
> Files I could not resolve with confidence are listed as `UNVERIFIED` in §3.2 rather than silently bucketed.

### 1.2 `TerraGuns` — 99 java files (package `org.confluence.terra_guns`)

| Classification | Files |
|---|---:|
| `INLINED-SAME` (at A HEAD) | **63** raw basename matches — 62 real (1 collision: B's `client\event\GameEvent` matched A's unrelated `common\gameevent\GameEvent`) |
| `INLINED-RENAMED` | **48** (per the gun deep-dive's 99-file verdict table) |
| `GENUINELY-MISSING` | **4** |
| `LIBRARY-ONLY` | **1** (`common\init\TGAttributes` — an empty `DeferredRegister`, no gun attributes exist in either repo) |
| `UNVERIFIED` | **0** at class level |

`TerraGuns` content (27 item ids: guns, bullets, ammo, tags, models, lang) is **100 % covered** by A —
`blowgun, boomstick, chlorophyte_bullet, crystal_bullet, cursed_bullet, endless_musket_pouch, exploding_bullet,
flintlock_pistol, golden_bullet, hand_gun, high_velocity_bullet, ichor_bullet, luminite_bullet, meteor_shot,
minishark, musket, musket_bullet, nano_bullet, party_bullet, phoenix_blaster, shotgun, silver_bullet,
snowball_cannon, tactical_shotgun, the_undertaker, tungsten_bullet, venom_bullet` — every id has an identical
`item.confluence.<id>` key. Neither repo stores gun stats in JSON; stats are Java literals in
`common\item\gun\definition\*` (A) vs `common\definition\*` (B).

### 1.3 Content-level diff (the strongest evidence that content is *not* the problem)

Language-key diffs between B's generated `en_us.json` and A's generated `en_us.json`:

| Domain | A keys | B keys | B ids with no identical A id | Real content gaps |
|---|---:|---:|---:|---|
| entities | 559 | 241 | 42 | of which 1 real: `stardust_dragon` (rest are internal projectile/part/summon helper ids that A registers differently) |
| items | 1,910 | 232 | 20 | of which real: `stardust_dragon_staff`, `summon_{wooden,stone,iron,golden,diamond,netherite}_sword_staff` (6 cosmetic staffs), `wallet`, `house_detector` |
| effects | 81 | 8 | 2 | none (`crimson_storm`, `scared`, `summoning` all present) |
| enchantments | 11 | 3 | 0 | none (but see gun enchantments — those live under `terra_guns`, not `terra_entity`) |

Beyond content, A registers **more** than B in most domains: 117 monster files / ~90 registered monster types
vs B's ~50, 66 NPC files / 20+ registered NPCs, 41 critter files, 47 boss files / ~20 registered bosses
(A adds `LunaticCultist`, `LunaticCultistClone`, `PhantasmDragon`, `ServantOfCthulhu`), 17 minions vs 9,
5 mounts vs 3, and entire families B lacks (bombs 18, boulders 14, arrows 14, hooks 7, fishing hooks 5,
flails 7, minecarts, yoyos 7, spears 7, mana projectiles 24, swords 14).

---

## Section 2 — Class/feature verdict table

Legend — verdicts: `SAME` = same basename at A HEAD · `HIST` = same basename at `2569be361` only ·
`RENAMED` = equivalent under another name at A HEAD · **`MISSING`** = harvest gap · `LIB` = library-only.
A paths are relative to `ConfluenceOtherworld\src\main\java\org\confluence\mod\` unless prefixed
`lib:` (= `Confluence-Magic-Lib\src\main\java\org\confluence\lib\`). B paths relative to
`TerraEntity\src\main\java\org\confluence\terraentity\`.

### 2.1 Animation engine — keyframe / baker / interpolator / motion curve

Verified defectively: a word-boundary token scan for `Keyframe, IKeyframeAnimation, IKeyframeBaker,
IInterpolator, IStateMachine, IUseItemAnimatable, PiecewiseBezier, LinearBaker, BakerEnum, InterpolatorEnum,
SplineKeyframeDynamicCurve, UniformedCurve, Bezier3Curve, Bezier4Curve, InverseKinematics` across
**all five A modules + all A refs + `git rev-list --all --objects` (8,204 historical java paths)** returns
**zero hits at HEAD** and hits only at `2569be361` in history.

| B class/feature | Verdict | A counterpart / reason |
|---|---|---|
| `api\entity\animation\**` (6: Curve, IInterpolator, IKeyframeAnimation, IKeyframeBaker, IStateMachine, IUseItemAnimatable) | `HIST` | `api\entity\animation\*` @`2569be361`. Note `IInterpolator` drifted a lot (A 20 ln vs B 72 ln — B added interpolator kinds). |
| `entity\ai\keyframe\**` (13) | `HIST` | `util\entity\ai\keyframe\**` @`2569be361`. Only package line + small deltas. `Vec3KeyframeAnimation` A=165/B=178 ln (69 diff lines — diff before reuse). |
| `entity\ai\keyframe\baker\**` (4) | `HIST` | `util\entity\ai\keyframe\baker\**` |
| `entity\ai\keyframe\interpolator\**` (3) | `HIST` | `util\entity\ai\keyframe\interpolator\**` |
| `entity\ai\motion\curve\**` (3) | `HIST` | `util\entity\ai\motion\curve\**` (Bezier3 4 diff lines, Bezier4 2) |
| `entity\ai\motion\DashComponent`, `DragonMovement` | `HIST` | `util\entity\ai\motion\*` (identical line counts) |
| `entity\ai\ik\InverseKinematics3D` | `HIST` | `util\entity\ai\ik\InverseKinematics3D` — **A=126/B=126 ln but 82 diff lines: biggest drift in the set.** |
| `entity\animation\**` (7: Abstract/Bone/MultiBone state machines, BoneStates, ModelPositionTable, HillOfFleshModelAnimationTable) | `HIST` | `util\entity\animation\**` |
| **`client\animation\**` (11 real files)** | **`MISSING`** | Never existed in A on any ref. Includes `AbstractAnimator`, `BoneAnimator`, `AnimatorContext`, `BoneState`, `GeoBoneAnimator`, `GeoBoneState`, `Left/RightHandGeoBoneAnimator`, `MultiBoneAnimator`, `MultiBoneState`, `SkeletronAnimator`. Note `MultiBone`/`MultiBoneStateMachine` (the **data** layer) *are* `HIST` — only the client animator layer is gone. |
| `client\animation\LashAnimation` | `HIST` | `client\animation\LashAnimation` @ history (later deleted by `02e826b33`) |
| `client\animation\api\context\IAnimatorContext` | `LIB` | Empty marker interface; no payload. |
| `client\entity\model\AnimatorModel`, `client\entity\renderer\AnimatorRenderer` | **`MISSING`** | A HEAD has their superclasses (`GeoNormalModel`, `GeoNormalRenderer`) but not the interpolating subclasses. |
| `client\util\DefaultBoneBoundIdents` | **`MISSING`** | A HEAD hard-codes the same bone strings inline (`ContactHumanoidGeoModel<>(…,"RightArm","LeftArm")`, `NPCHumanoidGeoModel.getBone("LeftArm")`, `VanillaHumanoidGeoModel` `case "LeftArm"`); no constants holder. |
| `client\util\ShaderUtil` | **`MISSING`** | No `ShaderUtil` on any A ref. Partial, differently-shaped overlap in `client\effect\AbstractBufferManager.setShader()`. |
| `utils\Easing` | **`MISSING`** | Never existed in A. A delegates to GeckoLib `software.bernie.geckolib.core.animation.EasingType` in 15 HEAD files. Harvest only if the `Codec`-serialisable enum is needed. |
| `utils\SmoothFloat` | **`MISSING`** | Never existed in A on any ref; no `Smooth*` equivalent. |
| `entity\util\KeyframeAnimationCounter` | **`MISSING`** | Never existed in A. (Codec/StreamCodec wrapper pushing a `KeyframeAnimation` into a synced counter.) |
| `entity\util\DeathAnimOptions` | `RENAMED` *(partial)* | A HEAD `util\DeathAnimUtils.java` still **references** it, but only in commented-out code. The real interface (`getBloodColor()`) existed on older A lines and was deleted → re-harvest. |

**Animation-library dependency:** *not* a library swap — both branches pin **GeckoLib 4.8.4**
(A `geckolib-forge-1.20.1`, B `geckolib-neoforge-1.21.1`, which B jarJars). B additionally ships the custom
framework above; A HEAD uses raw GeckoLib (`AnimationController` in 106 files, `AnimatableManager` in 142,
`RawAnimation` in 100). AzureLib is absent from A and is not a real dependency of B (only commented-out
compat lines). Practical consequence: re-introducing the subsystem means restoring ~35 files from A's own
history (`2569be361`) plus harvesting the ~38 files that never existed in A, and reconciling the
Forge-1.20.1 ↔ NeoForge-1.21.1 API gaps already visible in the `(hist)` diffs.

### 2.2 Motion blur — whole subsystem absent

| B class/feature | Verdict | A counterpart / reason |
|---|---|---|
| `entity\blur\**` (4: MotionBlurManager, PosRotMotionBlurContext, PosRotMotionBlurManager, PosRotMotionBlurRenderer) | **`MISSING`** | `MotionBlur`/`IMotionBlur`/`motionBlur` = 0 hits on any A ref. A HEAD has only `client\effect\AfterimageHelper`+`AfterimageStyle` (player-only ninja-armour ghost afterimages, 3-step alpha ramp) — a different feature, not a substitute. |
| `client\entity\renderer\GeoMotionBlurRenderer` | **`MISSING`** | same |
| `api\entity\blur\**` (4 seams) | `LIB` | Library-only generic seams. Add to the harvest list if the API surface must be kept. |
| corresponding shader programs | **`MISSING`** | B: `assets\terra_entity\shaders\core\{color_blit, dissolve_blit, dissolve_blit_lager, float_bar, float_fire, mix_add, pixel_style_dissolve}.{fsh,vsh,json}`. A: `assets\confluence\shaders\core\boss_bar_flow.*`, `post\the_constant.json`, `program\the_constant.*`, `include\noise.glsl` only. |

### 2.3 FSM / boss-skill framework

| B class/feature | Verdict | A counterpart / reason |
|---|---|---|
| `api\entity\ai\{IBossFSM, IFSMGeoMob, ISkill, ISkillManager}` (4) | `HIST` | `api\entity\ai\*` @`2569be361` |
| `entity\ai\fsm\**` (5: AbstractMobSkill, CircleMobSkills, DetailMobSkill, EmptyMobSkill, MobSkill) | `HIST` | `util\entity\ai\fsm\**` |
| `entity\ai\goal\FSMGoal`, `entity\ai\goal\skill\SkillCooldownManager` | `HIST` | `util\entity\ai\goal\FSMGoal`, `…\skill\SkillCooldownManager` |
| `data\mappeddata\BossSkillMapDatas` (+ B's generated `mapped_data\boss_skill_params.json.json`) | **`MISSING`** | A has no per-boss skill-parameter data. Bosses themselves are all covered (§2.7). |
| **A's replacement mechanism** | `RENAMED` | A rewrote boss/monster AI as a self-authored behaviour tree `common\entity\ai\bt\**` + `common\entity\ai\goal\**`, with `common\entity\boss\{BaseBoss, BaseBossPart, BaseLivingBossPart, BaseWormBoss, BossWormPart, AbstractTwinEye, BossOwnedEntity, BossOwnerTracker, BossChildDeathLedger, BossMultiplayerEnhancement, MechanicalMayhemTracker, BossChunkTicket}`. Functionally superset in places (multiplayer boss ownership, chunk tickets, child death ledger) but **the data-driven skill-parameter tuning is lost**. |

### 2.4 Vanilla-brain AI + sensors

| B class/feature | Verdict | A counterpart / reason |
|---|---|---|
| `entity\ai\brain\behavior\**` (8) | `HIST` | `util\entity\ai\brain\behavior\**` |
| `entity\ai\brain\sensor\**` (5: NPCHostilesSensor, NPCNearbyOthersSensor, NPCNearestVisibleAllianceSensor, NPCNurseTargetSensor, TENearestVisibleLivingEntitySensor) | `HIST` | `util\entity\ai\brain\sensor\**` — but **A HEAD does not use them**: `BaseNPC.SENSOR_TYPES` are vanilla `NEAREST_LIVING_ENTITIES/NEAREST_PLAYERS/HURT_BY` only; there is no brain-Behaviour/Sensor NPC AI. Restoring the files is cheap; restoring the *behaviour* means re-porting `NPCAi`. |
| `entity\npc\brain\**` (NPCAi, ArmDealerNPCAi, DemolitionistNPCAi, NonAttackableNPCAi, NurseAi, OldManAi + 11 behaviours) | `RENAMED` | A: `common\entity\npc\BaseNPC.registerGoals()` + `common\entity\npc\ai\{NPCDefenseGoal, NPCHealGoal, NPCHurtRetreatGoal, NPCReturnHomeGoal, NPCCombatActions, NPCCombatProfile, NPCCombatProgression}`. Vanilla-Goal based, not brain-based; no strafing variant. |
| `CrossBowAttackOnCooldownBrain` | **`MISSING`** | No crossbow NPC attack/charge anywhere in A (greps for `CrossbowAttackMob|performCrossbowAttack|isChargingCrossbow`; `NPCCombatActions` only references `SoundEvents.CROSSBOW_SHOOT`). |

### 2.5 Behaviour-tree / goal machinery

| B class/feature | Verdict | A counterpart / reason |
|---|---|---|
| `BTNode`, `BTRoot`, `Blackboard`, `composite\{SelectorNode, SequenceNode}`, `condition\Condition`, `leaf\{DashAction, JumpAttackAction, MoveToTargetAction, RandomStrollAction, WaitAction}` (11) | `SAME` | `common\entity\ai\bt\*` |
| `entity\ai\goal\behavior\**` remainder — decoration (8), composites (3), conditions (9), blackboard seams (2), `BTFactory`/`BTCommonRoot`/`BTBossTwoStageRoot` (3), leaves (19), `webviewer\BTServer` (1) | `HIST` | `util\entity\ai\goal\behavior\**` @`2569be361` — **the entire goal/BT tree was inlined then deleted** in `b20c0cefd`, so all of it is recoverable. A HEAD's live BT is a **different, smaller** tree (see next row). |
| A HEAD's live BT | `RENAMED` | `common\entity\ai\bt\**`: `BTNode/BTRoot/BTStatus/Blackboard`, `composite\{SelectorNode, SequenceNode, ConditionalSwitchNode, RoundRobinSelectorNode}`, `condition\{Condition, HasTargetCondition, PlayerCloseCondition, TargetWithinRangeCondition}`, `leaf\**` (~40 actions: MeleeAttackAction, BowCombatAction, CasterCycleAction, ChargeAttackAction, CircleAroundTargetAction, DirectFloatingPursuitAction, FlyingPursuitAction, FlyingVolleyCombatAction, MaintainRangedDistanceAction, PanicFleeAction, PhasedFlyingPursuitAction, RangedWindupAction, ShootSpikesAction, SlimeHopAction, SpawnArrowAction, SpawnProjectileAction, SteeringDashAction, TeleportNearTargetAction, VanillaGoalAction, WormMovementAction, …). **A HEAD's tree has no `decoration/` package at all.** |
| `entity\ai\goal\**` top-level goals (17 non-SAME: AccelerateOnSeeing, CdGoal, ComeAndBackDash, Dash, DestroyHouse, FactorFloat, FloatAi, FlyRangeAttack, JumpAttack, JumpOverBlock, LookForwardWanderFly, MeleeAttackNoLook, MutableRangeNearestAttackableTarget, TERangedAttack, WormRandomWander, SummonGoal, …) | `HIST` | `util\entity\ai\goal\*` @`2569be361` (all of them) |
| `entity\ai\goal\summon\**` (7) | `HIST` | Same. A HEAD's summon AI is a different system (`common\summoner\minion\goal\**`, 27 goal classes, one pair per minion). |
| A's own dev tools: `webviewer\BTServer` + `assets`/`data\terra_entity\behaviorviewer` + config `behavior_tree_web_viewer_server_port` | **`MISSING`** (as a tool) | A HEAD has no BT web viewer. The file itself is `HIST`. |

### 2.6 Summon system

| B class/feature | Verdict | A counterpart / reason |
|---|---|---|
| `entity\summon\AbstractSummonMob`, `FlyRangeAttackSummonMob`, `SummonFinch`, `SummonHornet`, `SummonIronGolem`, `SummonSlime`, `SummonSnowFlinx`, `SculkWisp`, `Terraprisma`, `SummonSword` | `RENAMED` | A: **custom attachment-entity minion system** — `common\summoner\{SummonerHelper, SummonerEvents, attachment\*, attachmentEntity\{AttachmentEntity, AttachmentEntityType, AttachmentEntityGoal, MomentumAttachmentEntity, OBB, PathNode, PlannedPath, SyncFieldDispatcher, …}, summonMark\{SummonMarkType, SummonMarkInstance}, register\{SummonerRegistries, SummonerAttachmentEntityTypes, SummonerAttachmentTypes, SummonerModels, SummonerParticleTypes, SummonerSoundEvents, SummonerSummonMarks}, particle\*\}` with minions `Finch, Hornet, IronGolem, Slime, SnowFlinx, SculkWisp, Terraprisma, Imp, DeadlySphere, DesertTiger, EyeLaserTurret, RuinRelic, SanguineBat, Spider, VampireFrog` + `GroundMinion, MomentumMinion, ICarryMinion, MinionSlotType`. A's summon system uses its **own custom registries** (`summoner_attachment_entity_types`, `summon_mark`) that B's TerraEntity has no equivalent of. Net: A ⊃ B. |
| `entity\summon\StardustDragon`, `StardustDragonSegment` (+ `stardust_dragon_staff` item) | **`MISSING`** | A has no Stardust Dragon entity or staff (verified in entity/item lang keys and by grep). **Real content gap.** |
| `entity\summon\Chester`, `PiggyBank` | `RENAMED` | A: `common\entity\storage\{ChesterEntity, FlyingPiggyBankEntity, StorageCompanionEntity}`, `common\attachment\{PlayerSafeContainer, PlayerPiggyBankContainer}`, `item` `chester_staff`/`money_trough`, `s2c\PiggyBankTotalMoneyPacket` |
| 6 × `summon_{wooden,stone,iron,golden,diamond,netherite}_sword_staff` | **`MISSING`** (content) | No A item ids. A has only `terraprisma` (+ `TerraprismaMinion`). Cosmetic/vanity set. |
| `api\entity\IMinion`, `ISummonMob` | `HIST` / `RENAMED` | `IMinion` was inlined at `2569be361` (as `api\entity\IMinion`); A HEAD uses `api\summon\OwnedSummon` + `AttachmentEntity`. |
| `effect\harmful\SummonFocusEffect` | **`MISSING`** | A HEAD has `ModEffects.SUMMONING` + the summon-mark system, but no `summon_focus` effect class. |

### 2.7 Bosses — **no boss gap**

| B boss | A | B boss | A |
|---|---|---|---|
| BrainOfCthulhu | `common\entity\boss\BrainOfCthulhu` | Plantera (+Hook/Tentacle) | `Plantera`, `PlanteraHook`, `PlanteraTentacle` |
| Deerclops | `DeerClops` (capital C) | PrimeEnderDragon (+Part) | `PrimeEnderDragon`, `PrimeEnderDragonPart` |
| DungeonGuardian | `DungeonGuardian` | QueenBee | `QueenBee` |
| EaterOfWorlds (+Segment) | `EaterOfWorlds` (+`BossWormPart`) | Skeletron / SkeletronHand | `Skeletron`, `SkeletronHand` |
| EyeOfCthulhu | `EyeOfCthulhu` | SkeletronPrime (+Part) | `SkeletronPrime`, `SkeletronPrimeArm` |
| HillOfFlesh (+Eye/Mouth/Part) | `HillOfFlesh`(+Eye/Mouth), `BaseLivingBossPart` | TheDestroyer (+Part/Probe) | `TheDestroyer`, `TheDestroyerProbe` |
| KingSlime | `KingSlime` | TheTwins / Retinazer / Spazmatism | `TheTwins`, `Retinazer`, `Spazmatism` (+`AbstractTwinEye`) |
| WallOfFlesh (+Eye/Mouth/Part) | `WallOfFlesh`(+Eye/Mouth/Part) | BrainFake | `BrainFake` |
| — | A **additionally** has `LunaticCultist`, `LunaticCultistClone`, `PhantasmDragon`, `ServantOfCthulhu` | | |
| `client\boss\renderer\**` (14) | `client\entity\renderer\*Renderer` (8 `SAME` by basename; rest renamed, e.g. `SkeletronBossRenderer`, `SkeletronPrimeBossRenderer`, `BossWormPartRenderer`, `BossGeoRenderer`, `WormHeadRenderer`) | `client\boss\model\**` (3) | `client\entity\model\*` + `client\model\entity\hook\SkeletronHandModel` |

**Verdict: bosses are fully covered, and A has more of them.** The only loss is the FSM/skill framework and its
`boss_skill_params` tuning data (§2.3).

### 2.8 Monsters / animals / rideables / projectiles

| B area | Verdict | A counterpart / reason |
|---|---|---|
| `entity\monster\**` (~50 in B) | `RENAMED` | A `common\entity\monster\**` = **117 files**. Named twins: 32 `SAME`. Renamed/absorbed: `AbstractMonster`→`BaseMonster`, `AbstractFSMMonster`→`BaseMonster`+BT, `BaseBat`→`CaveBat`, `BaseWorm`→`BaseWormMonster`/`SimpleWormMonster`, `HumanoidMonster`→`BaseHumanoidMonster`+`HumanoidWarriorMonster`, `JumpAttackMonster`→`ChargingMonster`/`JumpingWarriorMonster`, `RangeShooter`→`RangedMonster`, `RangeSkeleton`→`MeleeSkeleton.BehaviorProfile`+`RangedMonster`, `FireImpEntity`→`FireImp`, `CrimsonMimic`→`BaseMimic` (`crimson_mimic` id present), `BoneSerpent`→`SimpleWormMonster.Anatomy.BONE_SERPENT` (`bone_serpent` + `wither_bone_serpent` ids present), slimes → `slime\{CorruptSlime, FleshSlime, GoldenSlime, LavaSlime, LuminousSlime, MotherSlime, SpikedSlime, SweetSlime, TropicSlime}`. **A additionally has** Antlion/AntlionCharger, Arapaima, ChaosElemental, Corruptor, DarkCaster, Derpling, DesertSpirit, Drippler, DungeonSpirit, EaterOfSouls, EnchantedSword, EvilPenguin, FlyingFish, FrostFighter, Gastropod, GiantTortoise, Gnome, Goblin*, GraniteGolem, Hoplite, IceElemental, MartianProbe, Paladin, Pirate*, RedDevil, RockGolem, SandShark, ShadowflameApparition, Slimer, SpittingPlant, SporeZombie, Unicorn, WaterBoltMimic, Werewolf, WindyBalloon, ZombieMerman, Zombie… **No monster gap found.** |
| `entity\monster\prefab\**` (5: AbstractPrefab, AttributeBuilder, FlyMonsterPrefab, LandMonsterPrefab, IAttributeHolder) | `HIST` / `RENAMED` | Inlined at `2569be361` as `common\entity\monster\prefab\**`; A HEAD uses `common\entity\monster\CreatureAttributeBuilder` + `MonsterAttributeScaling` + `ModDataMaps.CREATURE_DEFINITION` data map instead. |
| `entity\monster\demoneye\**` (5) | `SAME`/`HIST` | `DemonEye` is `SAME`; the 3 goals + `DemonEyeVariant` were inlined then deleted and re-appear as `common\entity\ai\bt\leaf\DemonEye{Leave,Surround,Wander}Action`. |
| `entity\animal\**` (16) | `RENAMED` | A `common\entity\animal\**` = **41 files** vs B's 16. Named twins 8 (`Bird, Bunny, Crab, Duck, Fairy, JewelBunny, JewelSquirrel, Squirrel`). Renamed: `SimpleAnimal`→`SimpleCritter`/`BaseCritter`, `SimpleVariantAnimal`→`VariantSpawnProfile`, `WeightedVariantAnimal`→`VariantSpawnProfile`, `JumpableAnimal`→`BaseCritter`, `VariantsTextureMaps`→`VariantTextureGeoModel`, `BoomBunny`→`ExplosiveBunny`/`HostileBunny`. **A additionally has** BlueJay, Butterfly, Cardinal, CloudSheep, Cluckshroom, CritterCorruption, Dragonfly, Fealing, GlowBug, GlowingMooshroom, Goldfish, Grasshopper, HellButterfly, Ladybug, MysticFrog, Penguin, PrismaticLacewing, RedSquirrel, Scorpion, Sluggy, Snail, Stinkbug, TruffleWorm, Worm. **No animal gap.** |
| `entity\rideable\**` (3: AbstractRideableEntity, RideableBee, RideableSlime) | `RENAMED` | A `common\entity\mount\**`: `AbstractMountEntity`, `RideableBeeMountEntity`, `RideableSlimeMountEntity` + **extra** `RideableLavaSharkMountEntity`, `RideableUnicornMountEntity`; `common\init\item\MountItems` + `c2s\{MountInputPacketC2S, MountTogglePacketC2S}`. A ⊃ B. |
| `entity\proj\**` (22) | `RENAMED` | A `common\entity\projectile\**` = **156 files** across `arrow\`(14), `bomb\`(18), `boulder\`(14), `flail\`(4), `mana\`(24), `spear\`(7), `strip\`(4), `sword\`(14), `whip\`(2) plus ~50 top-level. Named twins 2 (`BoomerangProjectile`, `mana\SkullProjectile`). Mapping: `BeeProj`→`BeeArrowEntity`/`BeeGunBullet`, `DemonScytheProj`→`mana\DemonScytheProjectile`+`HostileDemonScytheProjectile`, `IcePillar`→`DeerclopsIcePillarProjectile`, `LavaPillar`→`HillLavaPillarProjectile`, `SlimeSpikeProjectile`→`SlimeSpikeEntity`, `SpikeBallProjectile`→`SpikyBallProjectile`/`SuperSpikyBallProjectile`+`SpikyBallRuntime`, `SporeProjectile`→`PlanteraProjectile`/`SporeCloudProjectile`, `ThrownIceProjectile`→`DeerclopsThrownIceProjectile`, `ShadowHandProjectile`→`DeerclopsShadowHandProjectile`, `WhipEntity`→`whip\WhipAttackEntity`+`WhipCollisionGeometry`, `YoyosEntity`→`common\entity\yoyo\**` (7 files), `TrailProjectile`/`TrailSwordProj`→`sword\SwordProjectile`+`Geo/Forward/Grass/IceBlade/NightEdge/Phaseblade/StarFury` + `sword\SwordProjectileVisualBridge`, `SeedProjectile`→`DandelionSeed`, `ThrowableProj`→`ThrowableDropSelfProjectile`, `LineProj`/`ParticleLineProj`→`HostileParticleProjectile`, `SummonBeeStick`→(bee arrow path). No vfx gap. |
| `entity\util\{SpawnPlacementChecks, OBB, …}` | `SAME`/`HIST` | `SpawnPlacementChecks` is `SAME` (`common\entity\SpawnPlacementChecks`); `OBB` is `SAME` (`common\summoner\attachmentEntity\OBB`); `AttBuilder`→`CreatureAttributeBuilder`; `DifficultSelector`→A config (`CommonConfigs`); `DeathAnimOptions`→`util\DeathAnimOptions` (maybe hist only — see §2.1); `SharedFlagController` is `HIST` (`api\entity\SharedFlagController`). |
| `entity\proj\package-info`, other `package-info` | n/a | excluded |

### 2.9 NPC framework

Full detail in the NPC deep-dive; headline verdicts:

| B area | Verdict | A counterpart / reason |
|---|---|---|
| `entity\npc\AbstractTerraNPC` | `RENAMED` | `common\entity\npc\BaseNPC` (+ A extras: Goal AI, house/spawn tracking, `NPCCombatProfile`; A lacks `ITradeHolder`, POI sleeping, `CrossbowAttackMob`) |
| `AnglerNPC`, `MechanicNPC`, `SimpleNPC`, `TravelingMerchantNPC` | `SAME` | `common\entity\npc\{AnglerNPC, MechanicNPC, SimpleNPC, TravelingMerchantNPC}` (A also has `FEMALE_ANGLER`) |
| `chat\{ChatManager, NPCChat}` | `SAME` | `common\entity\npc\chat\{ChatManager, NPCChat}` |
| `chat\ChatHolder` | `RENAMED` | `common\entity\npc\chat\ChatLine` (chat + `TradeCondition` + randomized cooldown) |
| `chat\{ChatArranger, IToOtherChat, ToTypeChat}` | **`MISSING`** | No composable chat arrangement, no NPC↔NPC chat (`chat_map`/`talkingTarget`). A's `ChatManager` only targets the nearest player and takes the first passing line. |
| `house\House` | `SAME` | `common\entity\npc\house\House` |
| `house\{HouseDetectInfo, HouseManager, IHouseDetector}` | `RENAMED` | `common\entity\npc\house\HouseValidater` (BFS + light/chair/table/size), `common\data\saved\HouseHandler` (dimension→region→uuid, town pets), A lacks the `HouseDetectEvent` extension point |
| `misc\NPCDialogs` | `RENAMED` | `common\entity\npc\dialog\{NPCDialog, NPCDialogLoader}` (identical `{"dialogs":[…]}` schema, A loads `confluence:npc/dialogs.json`) |
| `misc\NPCNames`, `mood\Mood`, `mood\NPCMood` | `SAME` | `common\entity\npc\NPCNames`, `mood\{Mood, NPCMood}`; `mood\MoodInfo`→`mood\MoodData`. **A's mood model is richer** (biome, solitude, crowding, homeless/far-from-home, evil biome + buy/sell multipliers); B adds a per-NPC `MoodSetting` value table and a synced mood-id list for tooltip dialogs. |
| `entity\ai\goal\NPCTradeGoal` | `SAME` | `common\entity\npc\NPCTradeGoal` |
| `entity\ai\goal\DestroyHouseGoal` | `LIB` | B's class is an **empty 6-line stub** — nothing to port |
| `entity\npc\model\CrownOfKingSlimeModelEntity`, `client\...\NPCRenderer` | `SAME`/`RENAMED` | `common\entity\model\CrownOfKingSlimeModelEntity`; `client\entity\renderer\NPCEntityRenderer` (+`NPCHumanoidGeoModel`) |
| `entity\npc\trade\{NPCTradeManager, TradeElement}` | `RENAMED` | `common\entity\npc\trade\{NPCTradeList, NPCTradeOffer, NPCTradeMenu}` |
| `entity\npc\trade\{BitMask, TradeParams, TradeModifiers}` | **`MISSING`** | No per-slot enable mask, no trade-level/progress params, no trade modifiers |
| `api\npc\chat\**` (5) | `LIB` | A renders text/sprite/item directly in `NPCEntityRenderer`; the composable-element API surface is not needed once inlined (feature loss: composition/animation) |
| `api\npc\trade\**` (13) | **`MISSING`** (as a type system) | Every *behaviour* exists concretely in A (`BaseNPC`, `NPCTradeOffer`, `AnglerNPC.getMilestoneReward`, `NurseNPC.healPlayer`, `NPCTradeMenu.consumeCosts`), but there is no SPI: no `ITrade`, `ITradeLock`, `ITradeTask`, `ITradeGenerator`, `ITradeModifier`, `ITradeLootTable`, `ITradeHolder`, `ITradeHealth`, `IIngredientTrade`, `ITradeItemList`, `IDynamicTask`, `TradeLockRecipeDrawer`. |
| `registries\npc_trade_lock\variant\**` (9 locks + `TrueLock`) | `RENAMED` | `common\entity\npc\trade\conditions\**` — **A's set is a superset (27 vs 9)**: `AlwaysTrue, And, Or, Not, Biome, KillEntity, Mood, NPCNearby, Time, Hardmode, AnyBossDefeated, BossDefeated, Bestiary, Date, Dimension, Fluid, GameEvent, Graveyard, MoonPhase, PositionHeight, AttackTarget, NPCItemInHand, Weather, WorldFlag, ArtisanLoafUnused` in `common\init\ModTradeConditions` (registry key `confluence:trade_conditions`) |
| `registries\npc_trade_lock\{TradeLockProvider, TradeLockProviderTypes}` | **`MISSING`** | A has no lock-provider registry (conditions are a *codec* registry, so datapack-extensible, but with no client-side drawer concept) |
| `registries\npc_trade\{TradeProvider, TradeProviderTypes, TradeProperties}`, `variant\TradeTask` | **`MISSING`** | No trade-kind registry |
| `registries\npc_trade_list\**` (4) | **`MISSING`** (weighted generation) | `WeightMapGenerator` + generator registry absent; A's `TravelingMerchantNPC.selectTradeOffers()` is a uniform shuffle (`SimpleGenerator` ≈ `RENAMED`) |
| `registries\npc_trade_modify\**` (4: TradeItemModifier, TradeListModifier + providers) | **`MISSING`** | No trade modifiers at all (`TradeModifier|trade_modif` = 0 hits) |
| `registries\npc_trade_task\**` (7: DynamicAngler, DynamicPool, FixedMap, Progress, Random + providers) | **`MISSING`** (only DynamicAngler ≈ `RENAMED`) | A's angler is one hardcoded daily quest: `common\entity\npc\AnglerNPC` + `common\data\saved\AnglerData` + `common\data\AnglerQuestLoader` + `ModLootTables.QUESTS_0/10/75`; Progress/Random/FixedMap/DynamicPool tasks have no counterpart |
| `npc\trade\drawer\**` (8 recipe drawers) | **`MISSING`** | A's `NPCTradeScreen` draws offers + a coin column only; no lock/condition recipe drawing in GUI or JEI |
| `menu\{TETradesMenu, SimpleTradeMenu}` | `RENAMED` | `common\entity\npc\trade\NPCTradeMenu` (server-authoritative buy/sell/buyback/paging, coin currency) |
| `registries\chat\**` (7) + `registries\chat_condition\**` (8) | **`MISSING`** (registries) / `RENAMED` (variants) | A reuses the single `TradeCondition` registry for chat. Variants renamed: `ItemChatElement`→`NPCChat.item`, `StringChatElement`→`NPCChat.text`, `SpriteChatElement`→`NPCChat.emoji`, `ItemInHandChatCondition`→`NPCItemInHandCondition`, `WeatherChatCondition`→`WeatherCondition`, `NotChatCondition`→`NotCondition`. Truly absent: element/condition **provider** registries, `RandomElement`, `SeparatorElement`, `ChatVanillaCondition` (vanilla `ICondition` bridge), `MemoryStateCondition`, `RandomCondition` (B's is an unregistered stub) |
| `registries\chester\**` (4) | **`MISSING`** (registry) / `RENAMED` (feature) | A: `common\entity\storage\ChesterEntity`, `common\attachment\PlayerSafeContainer`, `chester_staff`, `tooltip.*chester.*` — feature present, datapack-configurability gone |
| `client\gui\container\{AnglerDialogScreen}` | `SAME` | `client\gui\screen\AnglerDialogScreen` |
| `client\gui\container\{DialogScreen, SimpleTradeScreen, TETradeScreen}` | `RENAMED` | `client\gui\screen\{NPCDialogScreen, GoblinTinkererDialogScreen}`, `client\gui\container\npc_screen\{NPCTradeScreen, NPCTradeScreens, NPCTradePortrait, NPCReforgeScreen}` |
| `client\gui\renderer\chat\bubble\**` (3) + `element\**` (3) + `client\buffer\NPCChatBubbleBuffer` | **`MISSING`** | A hardcodes `textures/gui/chat_bubble.png` with private helpers in `NPCEntityRenderer`; no bubble styles, no element renderers. (Config `npc_chat_bubble_style` is also absent from A.) |
| `network\c2s\NPCShopPacket` | `RENAMED` | Handled server-side in `NPCTradeMenu.clicked()` — no packet needed |
| `network\s2c\{UpdateNPCTradePacket, SyncNPCTradesPacketS2C}` | **`MISSING`** | A rebuilds the shop from a datapack revision (`NPCTradeList.getRevision()`) and ships offers in menu slots; no live per-slot sync |
| `network\s2c\SetAnglerDialogPacketS2C` | `RENAMED` | `network\s2c\OpenAnglerDialogPacketS2C` |
| `network\c2s\ServerBoundHousePacket` | `RENAMED` | `network\c2s\HouseSelectPacketC2S` + `s2c\AvailableHouseSelectPacketS2C` + `client\gui\hud\HouseSelectHud` |
| `network\s2c\UpdateBlackboardPacket` | `UNVERIFIED` | A has `common\entity\ai\bt\Blackboard`, but no sync packet was found; not traced whether A syncs BT state another way |
| `network\{c2s,s2c}\EventPacket*` | `RENAMED` | A uses purpose-built packets (`OpenMenuPacketC2S`, `OpenNPCServicePacketC2S`, `NPCDialogSessionPacketC2S`, `LucyTheAxeDialogPacketS2C`, `s2c\OpenNPCDialogPacketS2C`) |
| `data\gen\npc\{NPCChatProvider, NPCMoodProvider, NPCNameProvider}` | `SAME` | `common\data\gen\*` |
| `data\gen\npc\TENPCShopProvider` | `RENAMED` | `common\data\gen\NPCShopProvider` — **A writes a simpler schema**: `data\confluence\npc\trades\<npc>.json` = `{"offers":[{item, costs?, condition?}]}` (19 files) vs B's lock/task/modifier form (17 files in TerraEntity + 18 in the 1.21 main mod, incl. `maid_shop.json`) |
| `data\gen\recipe\TENPCShopModifierProvider` | **`MISSING`** | No modifier datagen |
| `data\saved_data\HouseStoreSaver` | `RENAMED` | `common\data\saved\HouseHandler` |
| `data\mappeddata\NPCMappedDatas` | `RENAMED` (partial) | `common\data\map\CreatureDefinition` + `NPCCombatProfile.weapon()`; B's **weighted multi-weapon table** (`npc_initial_weapon`) is not present — A fixes one weapon per NPC |
| `block\FigureBlock` + `client\block\renderer\FigureBlockRenderer` + `init\block\TEFigureBlocks` | **`MISSING`** | `Figure` = 0 hits in A. A has `common\block\common\StatueBlock` + `functional\BehaviourStatueBlock` (decorative/functional statues, not entity-figure displays) |
| `item\HouseDetectItem` | **`MISSING`** (as an item) | Capability present via `HouseSelectPacketC2S` (CHECK/ADD/DELETE) + `HouseSelectHud`, opened from `ExtraInventoryScreen`; no item tool |
| `init\entity\TENpcEntities`, `init\item\TESpawnEggItems`, `init\TEMenus` | `RENAMED` | `common\init\entity\NpcEntities` (**A registers more**: golfer, tax collector, steampunker, cyborg, skeleton merchant, 8 town slimes, dye trader, stylist, truffle, wizard, party girl, clothier, painter, witch doctor…), `common\init\item\SpawnEggItems`, `common\init\ModMenuTypes` |
| `integration\jei\**` (3) | **`MISSING`** (NPC category) | A has `integration\jei\ModJeiPlugin` + 18 categories, none NPC. Watch for a class-name clash when merging. |
| `integration\jade\**` (2) | **`MISSING`** | `snownee.jade` = 0 hits in A. (B's provider is Terraprisma-hiding, not NPC.) |
| `integration\touhou_little_maid\**` (6) | **`MISSING`** | A has only a **commented-out** `MAID_TRADE_MENU` in `network\c2s\OpenMenuPacketC2S`. Note B's main mod generates `data\confluence\npc\shop\maid_shop.json`. |
| `integration\curios\**` (2) | `RENAMED` | A uses `common\attachment\ExtraInventory` + `client\gui\container\ExtraInventoryScreen`; A's Curios mixins live in `mixin\integration\curios` |
| `api\event\NPCEvent` (Interact / InitTrade / TravelingMerchantGenerateTrade / NPCTradeEvent.Pre·Post / NPCBrainCollector / NPCDialogEvent) | **`MISSING`** | Repo-wide grep: only a **commented-out** `GameClientEvents.java:455` |
| `api\event\HouseDetectEvent` | **`MISSING`** | A's detection is not replaceable by third parties |
| `api\event\LoadResourceEvent` | **`MISSING`** | A loads names/dialogs/moods via `SimpleJsonResourceReloadListener` only |
| `api\event\IReDirectable` | **`MISSING`** | — |
| `api\event\RedirectBTEvent` | `HIST` | inlined at `2569be361` as `api\event\RedirectBTEvent` |
| Datapack JSON: `npc\dialogs.json`, `npc\names.json`, `npc\chat\guide.json` | `SAME` | `data\confluence\npc\dialogs.json`, `…\names.json`, `…\chat\guide.json` (A's chat schema = `chat{text|emoji|item}` + `condition` + `cooldown_ticks`) |
| Datapack JSON: `npc\moods.json` (single file) | `RENAMED` | A: `npc\moods\<npc>.json` (22 files) |

### 2.10 Registry / config / attachment / network / mixin infrastructure

| B class/feature | Verdict | A counterpart / reason |
|---|---|---|
| `registries\TERegistries` — **14 custom datapack registries** | `RENAMED` (much smaller) | A `common\init\ModCustomRegistries` defines only **3**: `track_type_provider`, `generation_provider`, `trade_conditions` (+ `common\summoner\register\SummonerRegistries` adds `summoner_attachment_entity_types`, `summon_mark`). **Absent in A:** `effect_strategy_provider`, `effect_strategy`, `trade_provider`, `trade_task_provider`, `trade_lock_provider`, `trade_generator_provider`, `chester_type`, `chester_conditional_type`, `trade_modifier_provider`, `chat_element`, `chat_condition`, `mapped_data_type`. |
| `registries\mappeddata\**` (7) + `data\mappeddata\**` (6) + `data\gen\MappedDataProvider` + `data\gen\TEDataMapProvider` + `data\init\loot\TELootParams` + `variant\{VariantCondition, VariantProvider}` + `init\TEDataMaps` (19) | **`MISSING`** | A HEAD instead uses **vanilla-style data maps**: `common\init\ModDataMaps` (13 types) + `common\data\map\**` (12 classes: `ValueComponent`, `ExtractinatorData`, `DiggingPower`, `TreasureBagDrop`, `CreatureDefinition`, `ImmunityDataMap`, `BugNetEntityToItem`, `LivingInvulnerableEffects`, `AttackEffects`, `GamePhase2AttributeModifiers`, `PresetBestiaryEntry`, `BlockBreakSpawns`) + `common\data\spawner\**`. This covers monster params and effect-ish data but **not** boss-skill params, NPC params, weapon params, whip data/paths, nor the `VariantCondition`/`VariantProvider` loot context. |
| `registries\hit_effect\**` (7) + `init\TEEffectStrategies` (8) | **`MISSING`** (registry) / `RENAMED` (concept) | A has only `api\EffectStrategyComponent` (an 8-line functional interface) + `ModDataMaps.ATTACK_EFFECTS`; there is no effect-strategy registry or provider-type dispatch. B's `effect.strategy.*` lang keys (24) have no A equivalent. |
| `init\**` (16) + `init\item\**` (11) + `init\entity\**` (7) | `RENAMED` | A: `common\init\Mod{AttachmentTypes, Biomes, BlockCounters, BoatTypes, Carvers, ChunkGenerators, Commands, CustomRegistries, DataComponentTypes, DataMaps, DensityFunctionTypes, DynamicBiomes, Effects, Enchantments, Features, Fluids, GenerationProviderTypes, GunProperties, LootTables, MenuTypes, MiniBiomes, ParticleTypes, Recipes, SecretSeeds, SoulSkills, SoundEvents, Structures, Tabs, Tags, Tiers, TrackTypeProviderTypes, TradeConditions, Villagers}`, `common\init\{armor, block, entity, gun, item}\**`, `common\summoner\register\**` |
| `attachment\**` (4: ItemInHandTrailAttachment, SummonerAttachment, UnSyncableAttachment, WeaponStorage) | `RENAMED`/`MISSING` | A `common\attachment\**`: `ExtraInventory`, `LeftClickState`, `ManaStorage`, `PlayerSafeContainer`, `PlayerPiggyBankContainer`, `PlayerSpecialData`, `EverBeneficial`, `ChunkBrushData`, `ChunkDropletsData`; `common\init\ModAttachmentTypes`. `ItemInHandTrailAttachment` is **`MISSING`** (nothing to attach it to without trails). |
| `network\**` (20) | `RENAMED` | A `network\**` = **61 files** (24 c2s, 35 s2c + 2 top-level) covering the same plumbing with different names; A uses a central payload list in `common\event\NetworkEvents`. Missing specifically: `SyncCameraShakePacket`, `SyncNPCTradesPacketS2C`, `UpdateNPCTradePacket`, `UpdateBlackboardPacket`. |
| `mixin\**` (37) | `RENAMED` | A `mixin\**` = **135 files**. Targets matched: `AbstractContainerMenuMixin`, `ChunkMapMixin`, `EntityMixin`, `PlayerMixin`, `ZombieMixin`, `EntityAccessor`, `GameRendererMixin`, `ItemInHandLayerMixin`, `ItemRendererMixin`, `LevelRendererMixin`, `LocalPlayerMixin`, `RecipeManagerMixin`, `FileLoaderMixin`, `RegistryDataLoader$RegistryDataMixin`, `MobMixin`, `VillagerMixin`, `CrossbowItemMixin`, `BlockBehaviourMixin` (=B's `BlockStateBaseMixin`). **Likely missing:** `BossEventMixin`/`ServerBossEventMixin` (A uses `client\gui\hud\CustomBossBarRenderer` + `BossBarSyncPacketS2C` instead), `NaturalSpawnerMixin`/`MobSpawnSettingsMixin`/`MobSpawnSettingsBuilderMixin`/`ModifiableBiomeInfoMixin` (A has its own `common\data\spawner\**` + `BiomeSourceMixin`), `ShaderInstanceMixin`, `LootDataManagerMixin`, `ReloadableServerResourcesMixin`, `ServerEntityMixin`, `client\{BossHealthOverlayMixin, EntityRenderDispatcherMixin}`, and `mixin\accessor\{CameraAccessor, GameRendererAccessor, GeoEntityRendererAccessor, GeoRendererAccessor, LevelRendererAccessor, MobAccessor, ProjectileWeaponAccessor, SlimeAccessor}`. `mixin\azurelib\AzurelibFileLoaderMixin` is `LIB` (AzureLib is not used by either branch). |
| `config\**` (4: AbstractJsonConfig, ClientConfig, ServerConfig, TEAttributeModifierConfig) | `RENAMED` (partial) / `LIB` | A: `common\CommonConfigs`, `client\ClientConfigs`, `StartupConfigs`, `client\gui\MergedConfigurationScreen`, `common\data\gen\language\ConfigurationLanguageSubProvider`. A has the monster/boss attribute multipliers and "enhance all monster"; **absent from A:** `enable_entity_motion_blur`, `npc_chat_bubble_style`, `behavior_tree_web_viewer_server_port`, `spawn_without_light`, `enemy_spawn_chance(_apply_all)`, `chance_to_spawn_slime_on_zombie_head`, `boss_bar_style/number offsets`, `enableNonSpiderModel`, `display_summon_items`, `boss_clear_when_no_target`/`boss_no_physics`/`boss_leave_on_day`/`boss_keep_wandering`, `generate_projectile_particle`. `TEAttributeModifierConfig` (JSON-serialisable attribute modifier) has no A twin. |
| `runtime\{TERuntime, dev\**}` (4) | `LIB` | `DevOnly`/`DevOnlyInterceptor`/`IDevWrapper` runtime dev instrumentation — only meaningful in a standalone library; A instead uses `common\init\entity\DevelopmentSpawnPolicy`. |
| `data\format\{JsonSorter, OutputFormat}` | `LIB` | Datagen JSON formatting helpers for the submodule's own generated directory. |
| `data\security\**` (8) | `SAME` (package moved) | A: `client\security\{HuffmanCodingWithEmbeddedTree, IWithKeySecurity, KeyGenerator, SecurityFace, SecurityKey, SecurityKeys, SecurityProcessor, VigenereCipher}` |
| `utils\**` (13 + `DriveAwaySystem\` 5) | `RENAMED` / **`MISSING`** | `AimUtils`→`lib:util\AimUtils`; `TEUtils`/`TEItemUtil`→`util\ModUtils`; `RecipeDrawerUtils`→(partial); `OBB`→`common\summoner\attachmentEntity\OBB`. **`MISSING`:** `CameraShakeManager`, `CameraShakeData`, `AdapterUtils`, `CircularArrayBuffer`, `EfficientCylinderDestruction`, `WorldChunksManager`, and the whole `DriveAwaySystem\**` (5 files) — A has only `common\effect\harmful\DriveAwayController` + `DriveAwayEffect`. |
| `client\post\**` | mixed | `TongueRenderer`→`client\renderer\entity\TongueRenderer` (`SAME`, but implementations have diverged almost completely); `BossSpawnCameraManager`, `BrainTranslucent`, `PlayerSwordTrailRenderer` **`MISSING`**. `BrainDissolveTexture` in A is a texture-space dissolve, **not** a substitute for `BrainTranslucent`. |
| `client\buffer\{AbstractBufferManager, DebugBlocksHelper}` | `RENAMED` | `client\effect\{AbstractBufferManager, DebugBlocksHelper}` (A HEAD only kept `AbstractBufferManager`; `DebugBlocksHelper` is history-only) |
| `client\particle\{BiomeColorParticle, SpitParticle}` | `SAME` | `client\particle\*` |
| `client\gui\{CustomizeBossHealthBar, DebugScreen}` | `RENAMED`/`MISSING` | `client\gui\hud\CustomBossBarRenderer`; no `DebugScreen` equivalent. |
| `client\init\model\**` (4) | `SAME`/`RENAMED` | `WhipModelRegister` is `SAME`; `AbstractModelRegister`/`AdditionalItemRegister`/`EntityBlockModelRegister` → A registers inline in `client\event\ModClientEvents` |
| `client\item\model\BaseArmorItemRenderer`, `client\entity\layer\**` (3) | `RENAMED` | A `client\renderer\entity\*`, `mixin\client\renderer\entity\layers\{HumanoidArmorLayerMixin, ItemInHandLayerMixin}` |
| `client\entity\renderer\**` (~45) | `RENAMED` | A `client\entity\renderer\**` = **55 files** + `client\summoner\renderer\minion\**` (17) + `client\renderer\entity\{hook,projectile,bestiary,fishing}\**` |
| `client\entity\model\**` (~19) | `RENAMED` | A `client\entity\model\**` (24) + `client\model\entity\{projectile,summon,hook,bomb}\**`. Named twins: `CrownOfKingSlimeModel`, `GeoNormalModel`, `NymphModel`, `HarpyFeatherProjectileModel`, `TerraprismaModel`. Renamed/absent-but-unneeded: `AnimatorModel`(→**MISSING**), `BeeProjModel`(→`BeeProjectileModel`), `CabbageProjModel`, `DemonScytheModel`(→`DemonScytheProjectileModel`), `DuckModel`, `GeoHumanoidModel`(→`VanillaHumanoidGeoModel`/`NPCHumanoidGeoModel`/`ContactHumanoidGeoModel`), `GeoModelTextureDecoration`, `IceSpikeProjectileModel`, `JungleSpikedProjectlieModel`, `SlimeSpikedProjectlieModel`, `Stinger`(→`HornetStingerProjectileModel`), `VariantTexModel`(→`VariantTextureGeoModel`). |
| `misc\HotSwap`, `mixed\{IBossEvent, IBossHealthOverlay, IPlayer, IShaderInstance, IZombie}` | `RENAMED`/`MISSING` | A `mixed\**` has 28 interfaces incl. `IPlayer` (`SAME`); `IBossEvent`/`IBossHealthOverlay`/`IShaderInstance`/`IZombie`/`HotSwap` **`MISSING`** (A's boss bar uses `network\s2c\BossBarSyncPacketS2C` + `client\gui\hud\CustomBossBarRenderer`). |
| `data\biome\{TEBiomes, ExtendedAddSpawnsBiomeModifier}`, `data\gen\biome\TEBiomeModifier` | `RENAMED` | `common\init\{ModBiomes, ModMiniBiomes, ModDynamicBiomes}`, `common\data\spawner\**`, `neoforge\biome_modifier` datagen |
| `data\gen\**` (30) | `RENAMED` | A `common\data\gen\**` (much larger: `ModDataGenerator`, `Mod{English,Chinese}Provider`, `ModItemModelProvider`, `tag\**`, `loot\**`, `recipe\**`, `language\**`, `data_map\**`, `angler\**`) |
| `data\codec\TECodecs`, `data\component\{ResourceLocationComponent, SingleBooleanComponent}`, `data\enchantment\**` (2) | `RENAMED` | `common\init\ModDataComponentTypes`, `common\init\ModEnchantments`, `lib:util\LibCodecUtils` |
| `api\event\**` (9) | `RENAMED`/`MISSING` | A `api\event\**` (33 events). `TEBossEvent`→`api\event\gameevent\**`; `WhipRegisterModifyEvent`, `YoyosThrowingEvent`, `SummonEvent`→`lib:api\event\*` / whip API. **`MISSING`:** `NPCEvent`, `HouseDetectEvent`, `LoadResourceEvent`, `IReDirectable`. `RedirectBTEvent` is `HIST`. |
| `api\entity\**` (23) | `SAME`/`HIST` | `IGeneration`, `ITrackType`, `IVariant`, `ILeftClickStateItem` are `SAME`; `IMinion`, `IWorm`, `IWormSegment`, `IHeightControlMob`, `IStateChangeableMob`, `ICollisionAttackEntity`, `ISharedFlagControllerHolder`, `SharedFlagController` are `HIST` (`api\entity\*` @`2569be361`). A additionally has `IAngryMob`-equivalents inline. |
| `item\**` (12: SummonItem, PetItem, RideableItem, SentryItem, BossSummonsItem, ChesterSummonItem, HouseDetectItem, DebugItem, Boomerang, YoyosItem, BaseArmorItem, BaseWhipItem) | `RENAMED`/`MISSING` | `common\init\item\{SummonItems, PetItems, LightPetItems, MountItems, TreasureBagItems, BoomerangItems, YoyoItems, ArmorItems, WhipItems, SpawnEggItems, …}` + `common\item\{armor\BaseArmorItem, whip\BaseWhipItem, spear\*, gun\*, …}`. `HouseDetectItem` + `DebugItem` **`MISSING`**; A's sentry carry is `api\summon\OwnedSummon` + commit `116bef809` "添加哨兵携带接口" (no `SentryItem` class). |
| `block\FigureBlock`, `client\block\renderer\FigureBlockRenderer`, `init\block\TEFigureBlocks` | **`MISSING`** | See §2.9 |
| `integration\**` (23) | mixed | See §2.9 NPC rows + `integration\ModChecker`/`ModLoadPair`→A has `LoadingModList` checks inline; `integration\ItemComponentModify`→`common\init\ModDataComponentTypes`; `integration\{curios,iris,iron_spell,sodium_dynamic_light,sodiumextras,veil}\**`→A has Curios/Iris/sodium integrations under different names, **no Veil**. |

---

## Section 3 — `GENUINELY-MISSING` — the harvest TODO

Every entry below has **no same-named class in A HEAD and no same-named class at `2569be361`**, and no
verified rename. Where a rename exists I say so and it is *not* in this list.

### 3.1 Confirmed harvest list

**A. Motion blur (9 files + 7 shader programs)** — the whole subsystem, absent from every A ref
1. `api\entity\blur\IMotionBlurContext.java`
2. `api\entity\blur\IMotionBlurHolder.java`
3. `api\entity\blur\IMotionBlurManager.java`
4. `api\entity\blur\IMotionBlurRenderer.java`
5. `entity\blur\MotionBlurManager.java`
6. `entity\blur\PosRotMotionBlurContext.java`
7. `entity\blur\PosRotMotionBlurManager.java`
8. `entity\blur\PosRotMotionBlurRenderer.java`
9. `client\entity\renderer\GeoMotionBlurRenderer.java`
- shaders: `assets\terra_entity\shaders\core\{color_blit, dissolve_blit, dissolve_blit_lager, float_bar, float_fire, mix_add, pixel_style_dissolve}\.*`

**B. Client rig/animator layer (10 files)** — absent from every A ref
10. `client\animation\api\animator\AbstractAnimator.java`
11. `client\animation\api\animator\BoneAnimator.java`
12. `client\animation\api\context\AnimatorContext.java`
13. `client\animation\api\state\BoneState.java`
14. `client\animation\bone\GeoBoneAnimator.java`
15. `client\animation\bone\GeoBoneState.java`
16. `client\animation\bone\animator\humanoid\LeftHandGeoBoneAnimator.java`
17. `client\animation\bone\animator\humanoid\RightHandGeoBoneAnimator.java`
18. `client\animation\multi_bone\MultiBoneAnimator.java`
19. `client\animation\multi_bone\MultiBoneState.java`
20. `client\animation\multi_bone\animator\SkeletronAnimator.java`

**C. Model/renderer/util layer (4)**
21. `client\entity\model\AnimatorModel.java`
22. `client\entity\renderer\AnimatorRenderer.java`
23. `client\util\DefaultBoneBoundIdents.java`
24. `client\util\ShaderUtil.java`

**D. Post-processing (3)**
25. `client\post\BossSpawnCameraManager.java`
26. `client\post\BrainTranslucent.java`
27. `client\post\PlayerSwordTrailRenderer.java`

**E. Buffer/debug plumbing (1)**
28. `client\buffer\DebugEntityHelper.java`  (`NPCChatBubbleBuffer` is listed under K)

**F. Utils / counters (3)**
29. `utils\Easing.java`
30. `utils\SmoothFloat.java`
31. `entity\util\KeyframeAnimationCounter.java`

**G. Trail framework (10)**
32. `api\entity\trail\ITrail.java`
33. `entity\util\trail\BoomerangTrail.java`
34. `entity\util\trail\PositionPoseProperties.java`
35. `entity\util\trail\PositionPoseTrail.java`
36. `entity\util\trail\SummonSwordTrail.java`
37. `entity\util\trail\SwordTrail.java`
38. `entity\util\trail\TrailProperties.java`
39. `entity\util\trail\player\ColorfulItemInHandTrail.java`
40. `entity\util\trail\player\ItemInHandTail.java`
41. `attachment\ItemInHandTrailAttachment.java`

**H. Camera shake / boss-spawn camera (3)**
42. `utils\CameraShakeManager.java`
43. `utils\CameraShakeData.java`
44. `network\s2c\SyncCameraShakePacket.java`

**I. Mapped-data system (19)**
45. `registries\mappeddata\DeferredMappedType.java`
46. `registries\mappeddata\IAutoReloadable.java`
47. `registries\mappeddata\MappedData.java`
48. `registries\mappeddata\MappedDataLoader.java`
49. `registries\mappeddata\MappedDataType.java`
50. `registries\mappeddata\MappedDataTypes.java`
51. `registries\mappeddata\MappedKey.java`
52. `data\mappeddata\BossSkillMapDatas.java`
53. `data\mappeddata\MonsterMappedDatas.java`
54. `data\mappeddata\NPCMappedDatas.java`
55. `data\mappeddata\WeaponMappedDatas.java`
56. `data\mappeddata\data\WhipData.java`
57. `data\mappeddata\data\WhipPathManager.java`
58. `data\gen\MappedDataProvider.java`
59. `data\gen\TEDataMapProvider.java`
60. `data\init\loot\TELootParams.java`
61. `data\init\loot\conditioin\VariantCondition.java`
62. `data\init\loot\number\VariantProvider.java`
63. `init\TEDataMaps.java`
- plus B's generated `data\terra_entity\mapped_data\{boss_skill_params, monster_params, npc_params, weapon_params}.json.json`

**J. Effect-strategy registry (8)**
64. `registries\hit_effect\EffectStrategy.java`
65. `registries\hit_effect\EffectStrategyProvider.java`
66. `registries\hit_effect\EffectStrategyProviderTypes.java`
67. `registries\hit_effect\IEffectStrategy.java`
68. `registries\hit_effect\variant\PrefabEffect.java`
69. `registries\hit_effect\variant\RandomWeightEffect.java`
70. `registries\hit_effect\variant\TimePossibilityAmplifierEffect.java`
71. `init\TEEffectStrategies.java`

**K. Chat composition + bubble rendering (25)**
72. `entity\npc\chat\ChatArranger.java`
73. `entity\npc\chat\ChatHolder.java` *(≈`ChatLine`, rename candidate — verify)*
74. `entity\npc\chat\IToOtherChat.java`
75. `entity\npc\chat\ToTypeChat.java`
76. `registries\chat\ChatElementProvider.java`
77. `registries\chat\ChatProviderTypes.java`
78. `registries\chat\variant\RandomElement.java`
79. `registries\chat\variant\SeparatorElement.java`
80. `registries\chat_condition\ChatConditionProvider.java`
81. `registries\chat_condition\ChatConditionProviderTypes.java`
82. `registries\chat_condition\variant\ChatVanillaCondition.java`
83. `registries\chat_condition\variant\MemoryStateCondition.java`
84. `registries\chat_condition\variant\RandomCondition.java`
85. `client\gui\renderer\chat\bubble\BubbleConfig.java`
86. `client\gui\renderer\chat\bubble\CloudBubble.java`
87. `client\gui\renderer\chat\bubble\RectBubble.java`
88. `client\gui\renderer\chat\element\ChatComponentRenderer.java`
89. `client\gui\renderer\chat\element\ChatItemRenderer.java`
90. `client\gui\renderer\chat\element\SpriteRenderer.java`
91. `client\buffer\NPCChatBubbleBuffer.java`
- also the 5 `api\npc\chat\**` seam interfaces (judged `LIB`; add if the API must survive)

**L. Chester registry (4)**
92. `registries\chester\ChesterConditionalType.java`
93. `registries\chester\ChesterConditionalTypes.java`
94. `registries\chester\ChesterType.java`
95. `registries\chester\ChesterTypes.java`

**M. Trade SPI — registries, providers, tasks, modifiers, generators, drawers (46)**
96–98. `registries\npc_trade\{TradeProperties, TradeProvider, TradeProviderTypes}.java`
99. `registries\npc_trade\variant\TradeTask.java`
100–102. `registries\npc_trade_list\{TradeGeneratorProvider, TradeGeneratorProviderTypes}.java`, `variant\WeightMapGenerator.java`
103–104. `registries\npc_trade_lock\{TradeLockProvider, TradeLockProviderTypes}.java`
105–108. `registries\npc_trade_modify\{TradeModifierProvider, TradeModifierProviderTypes}.java`, `variant\{TradeItemModifier, TradeListModifier}.java`
109–115. `registries\npc_trade_task\{TradeTaskProvider, TradeTaskProviderTypes}.java`, `variant\{DynamicPoolTradeTask, FixedMapTradeTask, ProgressTradeTask, RandomTradeTask}.java` (+`DynamicAnglerTradeTask` ≈ renamed)
116–128. `api\npc\trade\**` 13 files: `IDynamicTask, IIngredientTrade, ITrade, ITradeGenerator, ITradeHealth, ITradeHolder, ITradeItem, ITradeItemList, ITradeLock, ITradeLootTable, ITradeModifier, ITradeTask, TradeLockRecipeDrawer`
129–136. `npc\trade\drawer\**` 8 files: `AndLockRecipeDrawer, BiomeLockRecipeDrawer, KillEntityLockRecipeDrawer, MoodLockRecipeDrawer, NotLockRecipeDrawer, NPCExistLockRecipeDrawer, OrLockRecipeDrawer, TimeLockRecipeDrawer`
137–138. `menu\{SimpleTradeMenu, TETradesMenu}.java`
139–141. `entity\npc\trade\{BitMask, TradeParams, TradeModifiers}.java`
- plus network `NPCShopPacket`, `UpdateNPCTradePacket`, `SyncNPCTradesPacketS2C`; datagen `data\gen\recipe\TENPCShopModifierProvider`; B's lock/task/modifier JSON schemas

**N. Content gaps (7 classes + 8 item ids)**
142. `entity\summon\StardustDragon.java`
143. `entity\summon\StardustDragonSegment.java`
144. `effect\harmful\SummonFocusEffect.java`
145. `item\HouseDetectItem.java`
146. `block\FigureBlock.java`
147. `client\block\renderer\FigureBlockRenderer.java`
148. `init\block\TEFigureBlocks.java`
- items with no A id: `stardust_dragon_staff`, `summon_{wooden,stone,iron,golden,diamond,netherite}_sword_staff`, `wallet`
  (B's `house_detector` capability exists in A but as a HUD flow, not an item)

**O. AI/event API (4)**
149. `api\event\NPCEvent.java` (+ its ~7 nested event types)
150. `api\event\HouseDetectEvent.java`
151. `api\event\LoadResourceEvent.java`
152. `api\event\IReDirectable.java`

**P. Behaviour-tree extras that never existed in A (the ones the `HIST` set does *not* cover)**
- `entity\ai\goal\behavior\webviewer\BTServer.java` **is** `HIST`; the **web viewer tool + config port** is `MISSING`.
- `entity\ai\brain\behavior\range\RangeAttackStrafingBrain` **is** `HIST`.
- `CrossBowAttackOnCooldownBrain` and any crossbow-NPC attack behaviour: **`MISSING`** (§2.4).

**Q. Mode/misc utils (7)**
153. `utils\AdapterUtils.java`
154. `utils\CircularArrayBuffer.java`
155. `utils\EfficientCylinderDestruction.java`
156. `utils\WorldChunksManager.java`
157. `utils\TEItemUtil.java` *(rename candidate → `util\ModUtils`)*
158. `utils\TEUtils.java` *(rename candidate → `util\ModUtils`)*
159. `utils\RecipeDrawerUtils.java` *(only referenced from A's `ModEnglishProvider`)*
160–164. `utils\DriveAwaySystem\{DriveAwayArrowIntegration, DriveAwayAttachment, DriveAwayDataAttachment, DriveAwayExecutor, DriveAwayMath}.java`
- `entity\util\DifficultSelector.java`, `entity\util\AttBuilder.java` (rename candidates)

**R. Config keys (feature-level, no class)**
`enable_entity_motion_blur`, `npc_chat_bubble_style`, `behavior_tree_web_viewer_server_port`,
`spawn_without_light`, `enemy_spawn_chance`(+`_apply_all`), `chance_to_spawn_slime_on_zombie_head`,
`boss_bar_style`/`boss_bar_number_offset_x|y`, `enableNonSpiderModel`, `display_summon_items`,
`boss_clear_when_no_target`, `boss_no_physics`, `boss_leave_on_day`, `boss_keep_wandering`,
`generate_projectile_particle`, `enhance_all_monster` (A has this one), `boss_attributes_multiplier_*`
(A has monster equivalents; boss-specific bounds differ: B `0.0625–10`, A `0.0625–100`),
`TEAttributeModifierConfig` (JSON attribute-modifier codec — no A twin).

**S. Mixins / accessors with no A equivalent (verify individually)**
`BossEventMixin`, `ServerBossEventMixin`, `ShaderInstanceMixin`, `LootDataManagerMixin`,
`ReloadableServerResourcesMixin`, `NaturalSpawnerMixin`, `MobSpawnSettingsMixin`,
`MobSpawnSettingsBuilderMixin`, `ModifiableBiomeInfoMixin`, `ServerEntityMixin`,
`client\BossHealthOverlayMixin`, `client\EntityRenderDispatcherMixin`,
`accessor\{CameraAccessor, GameRendererAccessor, GeoEntityRendererAccessor, GeoRendererAccessor,
LevelRendererAccessor, MobAccessor, ProjectileWeaponAccessor, SlimeAccessor}`

**T. `TerraGuns` — the entire confirmed missing set (4 + data)**
165. `client\renderer\item\TaczAnimationConstraint.java` (197 ln; TACZ ICA constraint bone math; called at B `GunRenderer.java:128`). Greps for `constraint|Constraint|readIca|BoneSnapshot` across **all five A modules** → only pose-reset snapshot calls. **No ICA logic anywhere in A.**
166. `common\enchantment\GunEnchantmentService.java` (155 ln)
167. `common\init\TGEnchantments.java`
168. `network\c2s\EmergencyMeleePacketC2S.java`
- **data:** `data\terra_guns\enchantment\{compressed_tactics, emergency_melee, temporary_reserve}.json`,
  `data\terra_guns\tags\item\enchantable\gun.json`, `TGTags.GUN_ENCHANTABLE`,
  `TGDataComponents.EMERGENCY_MELEE_COOLDOWN_END`
- **consumer deltas:** `fireRadial`, `spawnRadial`, `getAmmoForShot`, `hasEnoughAmmo`, `consumeAmmo`,
  `setTemporaryReserveLevel`, the `temporaryReserve` handler, `HandAnimationAction.EJECT_SHELL` +
  the `shell_action` channel, `isPutAwayAnimationPlaying` (A has the helper but **no callers**)
- **mixin behaviour:** `mixin\client\ItemInHandRendererMixin` put-away hold (pins `mainHandHeight >= 0.6F`
  while `PUT_AWAY` plays). A has two same-named mixins for unrelated purposes (`mixin\client\gun\*` cancels
  `applyItemArmTransform` for the GUN tag; `mixin\client\renderer\*` handles bow/whip/yoyo/mirror) — neither
  touches `mainHandHeight`/`PUT_AWAY`.
- **tests:** `TerraGuns\src\test\java\...\{TrailPathSmootherTest, BallisticsResolverTest, HomingControllerTest}.java` —
  **A has no `src\test` directory at all.**

### 3.2 `UNVERIFIED` — could not confirm either way (treat as missing until checked)

1. `network\s2c\UpdateBlackboardPacket` — A has `common\entity\ai\bt\Blackboard`; not traced whether A syncs
   blackboard state by another route.
2. Whether A's `NPCCombatProfile` single fixed weapon covers B's weighted `InitialWeapons` table semantics.
3. `entity\util\DeathAnimOptions` — A HEAD references it only in commented-out code; the interface may or may
   not exist on a line not fetched by this clone.
4. Behavioural parity of the ~35 `HIST` animation files vs B: line counts and diff-line counts were compared
   (byte identity impossible — the package line differs) but **neither tree was compiled or run**.
   `InverseKinematics3D` (82 diff lines) and `Vec3KeyframeAnimation` (69) warrant a manual diff before reuse.
5. Anything on A's remotes that this clone has not fetched. Local refs inspected:
   `forge-dev/1.20.1`, `neoforge-dev/1.21.1`, `backup/rebased-line-c63f84eaa-20260920`,
   `origin/{forge-dev/1.20.1, neoforge-dev/1.21.1, neoforge/1.21.1, forge/1.20.1}`, plus
   `git rev-list --all --objects` (8,204 historical java paths).
6. Whether A HEAD *intends* to re-inline the framework: `b20c0cefd` "remove all entity part" reads like a
   deliberate hand-off to the `TerraEntity` submodule — but the same branch then dropped the submodule too.
7. The eight `api\entity\blur\**`/`client\animation\api\context\IAnimatorContext` seam files: judged `LIB`,
   but if the public API surface must be preserved they belong in §3.1.
8. `client\gui\DebugScreen`, `misc\HotSwap`, `mixed\{IBossEvent, IBossHealthOverlay, IShaderInstance, IZombie}`
   — no A twin found, but each is small and may be subsumed; individually unverified.
9. Whether repo A's live BT is *feature-complete* relative to B's `HIST` goal tree for any specific boss.
   A's BT has no `decoration/` package; whether any boss actually needed those nodes was not verified.

---

## Section 4 — The 43 `integration\terra_entity` and 58 `mixin\integration` files in the 1.21 main mod

Paths relative to `ConfluenceOtherworld\src\main\java\org\confluence\mod\`.

### 4.1 `integration\terra_entity\**` — 43 files

**Disappears entirely (TerraEntity-module plumbing, no A counterpart needed) — 6 files**

| File | Why it disappears |
|---|---|
| `TEEvents.java` (103 ln) | Registers TE's own event handlers/mixin hooks onto B's mod bus |
| `TEGameEvents.java` (142 ln) | TE game-event bridging (e.g. game-event sync into TE) |
| `TEHelper.java` (46 ln) | TE-specific helpers: strips `terra_entity` recipes, rewrites TE loot-table namespace to `confluence:entities/terra_entity/…`, TE worm spawn rules |
| `AttributeRegistration.java` (21 ln) | Pushes TE's attributes into the main mod's `AttributeEvent`; redundant once attributes are inlined |
| `IAbstractTerraNPC.java` (12 ln) | Interface-injection seam into TE's `AbstractTerraNPC` |
| `brain\{ConfluenceArmDealerNPCAi, ConfluenceDemolitionistNPCAi}.java` (21+44 ln) | **Borderline:** these are TE-subclass AI plug-ins. They *do* carry content (Arms Dealer / Demolitionist combat) and **must survive in some form** — A already expresses both as `common\entity\npc\ai\NPCCombatActions.{ARMS_DEALER, DEMOLITIONIST}` + `NpcEntities.*` |

**Real content that must survive — 37 files**

| Group | Files | Verdict / where it must land |
|---|---|---|
| Trade-lock variants (12) | `npc_trade_lock\{AnyBossDefeatedLock, BestiaryUnlockedCountLock, ConditionsLock, DateLock, DimensionLock, EnvironmentLock, FishingHookInFluidLock, GameEventLock, MoonPhaseLock, PositionLock, QuestedFishPrecheckLock, SecretFlagLock}.java` (21–68 ln) | **All 12 have A HEAD equivalents as `common\entity\npc\trade\conditions\*`** (`AnyBossDefeatedCondition, BestiaryCondition, And/Or/NotCondition, DateCondition, DimensionCondition, Biome/Fluid/Graveyard/PositionHeightCondition, GameEventCondition, MoonPhaseCondition, PositionHeightCondition, …, WorldFlagCondition`). The one genuinely unique one is **`FishingHookInFluidLock` (46 ln)** — a fishing-hook-in-fluid predicate; A's nearest is `FluidCondition`; **verify**. Also `QuestedFishPrecheckLock` (37 ln) — A has `common\init\item\QuestedFishes` + `AnglerData`; **verify**. |
| Trade-lock recipe drawers (11 + 1 pkg-info) | `npc_trade_lock\drawer\{AnyBossDefeated, BestiaryUnlockedCount, Conditions, Date, Dimension, Environment, FishingHookInFluid, GameEvent, MoonPhase, Position, SecretFlag}LockRecipeDrawer.java` (13–105 ln) | **`GENUINELY-MISSING`** — A has no JEI/recipe drawer concept at all (`RecipeDrawer` only appears as a lang string in `ModEnglishProvider`). |
| Trade content (7) | `npc_trade\{IMoneyTrade, MoneyTradeItem, MoneyTradeHealth, MoneyTradeHealthFull, DeferredMoneyTradeItem, SellTrade}.java` (58–172 ln) | **`RENAMED`/absorbed** — A's `common\entity\npc\trade\NPCTradeOffer` + `NPCTradeMenu` (coin currency, buy/sell/buyback) + `util\Coins` + `common\entity\MoneyDropSource` cover the behaviour; A's `NurseNPC.healPlayer()` covers `MoneyTradeHealth`. `gen`-style deferred items and `SellTrade`'s pricing rules need a line-by-line check. |
| Registry registration (4) | `init\{AdditionalChesterTypes, ModEffectStrategies, ModTradeLockProviderTypes, ModTradeProviders}.java` (20–160 ln) | **`GENUINELY-MISSING` plumbing** — these exist only because TE exposes provider registries. Once inlined, either **keep TE's registries** (§2.10 row 1) or rewrite all 4 + their 19 registered types. **This is the pivotal architectural decision of the whole inline.** |
| Trail (1) | `trail\TerraSwordTrail.java` (189 ln) | **`GENUINELY-MISSING`** — the only consumer of the missing `SwordTrail`/`ITrail`/`ItemInHandTrailAttachment` stack (§3.1-G). Must be harvested along with it. |
| Item components (1) | `TEItemComponentModify.java` (19 ln) | `RENAMED` — A has `common\init\ModDataComponentTypes` + `common\component\*` |

> **Cross-check:** B's main mod also generates `data\confluence\npc\shop\*.json` (18 files, incl. a
> `maid_shop.json`) written against TE's shop SPI. A generates `data\confluence\npc\trades\*.json`
> (19 files) against its own flat schema. **Whichever schema wins, the other side's datagen must be rewritten.**

### 4.2 `mixin\integration\**` — 58 java files

- **44 files are unrelated to TerraEntity/TerraGuns** (they integrate other mods): `advancement_plaque`(1),
  `apothic_attributes`(2), `ars_nouveau`(2), `betteradvancements`(2), `carryon`(1), `create`(2),
  `curios`(3), `ftbchunks`(3), `geckolib`(2), `irons_spell`(2), `jade`(1), `jei`(3), `journeymap`(1),
  `lootbeamsrefork`(1), `lootr`(1), `magiclib`(1), `sodium`(5), `terracurio`(8), `terrafurniture`(1),
  `touhoulittlemaid`(2). **These are unaffected by the decision** — they mix into third-party classes, not
  into TerraEntity. (A has its own equivalents for most: `mixin\integration\{curios, geckolib, jei, magiclib,
  terracurio, terrafurniture, sodium}\*`.)
  Note `mixin\integration\geckolib\{GeoCubeMixin, InternalUtilMixin}` and
  `mixin\integration\jade\HarvestToolProviderMixin` are the only two of the 44 with a plausible
  TerraEntity-adjacent purpose (geo-cube/keyframe plumbing and a Jade provider base) — recheck after inlining.
- **14 files are TerraEntity plumbing and disappear — 12 of them, with 2 carrying real content:**

| File | Lines | Verdict |
|---|---:|---|
| `terraentity\AbstractTerraNPCMixin.java` | 69 | **Disappears** (mixins into TE's `AbstractTerraNPC`) — but its injected members must be *moved into* A's `BaseNPC`. |
| `terraentity\AnglerNPCMixin.java` | 35 | **Content to preserve:** Angler-specific dialog/session behaviour (A has `AnglerNPC` natively) |
| `terraentity\MechanicNPCMixin.java` | 24 | Content to preserve (A has `MechanicNPC` natively) |
| `terraentity\TravelingMerchantNPCMixin.java` | 20 | Content to preserve (A has `TravelingMerchantNPC` natively) |
| `terraentity\DemonEyeMixin.java` | 19 | Content to preserve (A: `DemonEye` + `DemonEyeGeoModel`) |
| `terraentity\DungeonGuardianMixin.java` | 21 | Content to preserve (A: `DungeonGuardian`) |
| `terraentity\GoldenSlimeMixin.java` | 28 | Content to preserve (A: `GoldenSlime` — check the golden-slime drop rule) |
| `terraentity\SkeletronMixin.java` | 25 | Content to preserve (A: `Skeletron`) |
| `terraentity\TEAnimalsMixin.java` | 15 | Content to preserve (A: `common\entity\animal\**` — likely variant spawn weights) |
| `terraentity\SpawnPlacementChecksMixin.java` | 18 | Content to preserve (A has `common\entity\SpawnPlacementChecks` natively) |
| `terraentity\DynamicAnglerTradeTaskMixin.java` | 41 | **Content to preserve — and it necessarily disappears**, because its target `DynamicAnglerTradeTask` is part of TE's trade-task SPI (§3.1-M). A's angler quest must be extended instead. |
| `terraentity\ServerBoundEventPacketMixin.java` | 15 | **Disappears** (TE packet plumbing) |
| `terraentity\TEKeyBindingsMixin.java` | 13 | **Disappears** (TE keybind plumbing; A: `client\ModKeyBindings`) |
| `terraentity\TEUtilsMixin.java` | 15 | **Disappears** (TE util plumbing; A: `util\ModUtils`) |

---

## Section 5 — Submodule commit analysis (work after the fork that would be lost)

### 5.1 Fork baselines

`795ac9ccc` "微调配置界面" (2026-05-31) is the last common ancestor present in **both** branches
(`git branch -a --contains 795ac9ccc` → `neoforge-dev/1.21.1`, `origin/forge-dev/1.20.1`, `origin/neoforge*`).
Repo A's `.gitmodules` shows `git log --oneline 795ac9ccc..HEAD -- TerraEntity TerraGuns` on the 1.20 side =
40 commits, the last pointer bumps being:

| Submodule | Last 1.20-side pointer | Date | Removed by |
|---|---|---|---|
| `TerraEntity` | `5d2c165945a0760716c22c9b7fca25bc06cdb975` "part7" | 2026-06-08 | `f4b42537c` "part8" 2026-06-10 (`.gitmodules` entry deleted, 93 files/+3,985/−3,779) |
| `TerraGuns` | `e201abd57f5a4e9e88c63f07454e29d9d576f361` "part7" | 2026-06-08 | `57d824a9d` 2026-06-12 (`.gitmodules` entry) + `d194600b9` 2026-06-12 |

Then `e7b826680` "part9" (2026-06-10), `f6e114cdb` "part21" (2026-06-24), `2569be361`/`7f83b379a` "part22/23"
(2026-06-27), `b20c0cefd` "remove all entity part" (2026-06-27).

### 5.2 `TerraEntity` — 20 commits after the last used pointer (`5d2c1659..HEAD`), 2026-06-02 → 2026-09-22

| Commit | Date | Subject | java | other | What |
|---|---|---|---:|---:|---|
| `47396cf5` | 2026-06-02 | 修交易界面无法双击物品收集同种物品 | 1 | 0 | 1-line `client\gui\container\TETradeScreen` fix (double-click same-item collection) → **UI bugfix worth harvesting into A's `NPCTradeScreen`** |
| `3a9116c3` | 2026-06-17 | 血蛭换模 | 0 | 6 | Leech re-model (assets) |
| `3f553afd` | 2026-06-19 | 更新贴图，一些杂活 | 1 | 1 | `init\entity\TEMonsterEntities` + texture |
| `ac19c47a` | 2026-06-19 | 更新黄蜂外观 | 0 | 2 | Hornet appearance (95/−163 lines of model?) |
| `96b8a8ec` | 2026-06-21 | 更新下恶魔眼 | 0 | 9 | Demon Eye art (9 files) |
| `b50eccc3` | 2026-06-23 | 发光蘑菇苔藓，更新滴滴怪材质 | 0 | 4 | Assets |
| `1c123f9d` | 2026-07-03 | 先传点 | 0 | 3 | Assets |
| `1045119f` | 2026-07-09 | 小砂岩堆和蚁狮卵 | 0 | 4 | Small sandstone heap + antlion egg (assets) |
| `b12d1205` | 2026-07-24 | 补污染小堆，补污染冰锥，俩套时装 | 0 | 1 | Assets |
| `7b8b39df` | 2026-07-24 | (same) | 0 | 1 | Assets |
| `12376416` | 2026-07-27 | something | 1 | 0 | 4-line deletion in `event\GameEntityEvent` |
| `752a6e83` | 2026-08-16 | 升级粒子 | 0 | 2 | Particle upgrade |
| `170a2c3d` | 2026-08-16 | mat4x3 | 0 | 1 | Shader/asset |
| `322eb649` | 2026-08-16 | 优化import | 26 | 0 | Import cleanup only |
| `0c2b12bf` | 2026-08-23 | Improve message when switch Chester connected container | 4 | 0 | **`item\ChesterSummonItem`, `network\s2c\ChesterAttachmentPacketS2C`, `data\gen\{TEChinese,TEEnglish}Provider`** — real UX improvement to Chester |
| `c1102a4f` | 2026-08-26 | 粒子修复 | 0 | 1 | Particle fix |
| `45b883ce` | 2026-09-15 | 晶塔占位符 | 0 | 1 | Pylon placeholder |
| `82077df8` | 2026-09-21 | IdentityHashMap改成Reference2ObjectOpenHashMap 修复暴击率的问题 | 3 | 0 | **`client\boss\renderer\WallOfFleshRenderer`, `entity\npc\misc\NPCDialogs`, `entity\npc\misc\NPCNames`** — crit-rate fix (fastutil map swap) |
| `1be5db6b` | 2026-09-22 | 修复BOSS召唤问题 | 1 | 0 | **`init\item\TESpawnEggItems`** (1 line) — boss-summon fix |
| `62984cf3` | 2026-09-22 | 升级粒子 | 0 | 1 | Particle version bump (HEAD) |

**Net:** post-fork TerraEntity work is small and mostly **art/asset churn**. The only code worth harvesting is
**6 commits / 35 java lines**: `47396cf5` (trade-screen double-click), `0c2b12bf` (Chester message),
`82077df8` (map-swap crit fix), `1be5db6b` (boss summon), `752a6e83`/`c1102a4f`/`62984cf3` (particles),
`12376416` (GameEntityEvent cleanup). **No new entity, boss, NPC, AI or animation feature landed in
TerraEntity after the fork.**

> Caveat: because A's post-June tree is an independent rewrite, "not harvested" cannot be inferred from
> basenames for the *feature-level* items. The four code commits above must each be re-checked against A:
> e.g. A has no `TESpawnEggItems` but its `common\init\item\SpawnEggItems` may or may not carry the fix.

### 5.3 `TerraGuns` — 15 commits after the last used pointer (`e201abd5..HEAD`), 2026-08-04 → 2026-09-10

| Commit | Date | Subject | java | other | What |
|---|---|---|---:|---:|---|
| `ba9feda` | 2026-08-04 | **重写枪械 完成全部特殊子弹代码** | **82** | 92 | **The big one.** Full gun-system rewrite: adds the whole `common\definition\**` + `common\definition\behavior\**` tree (19 files incl. 13 behaviors), `common\combat\{Ballistics, BallisticsResolver, GunStats, AmmoStats, ShotContext, GunFiringService, GunProjectileFactory, ShootingService, HomingController}`, `common\enchantment\GunEnchantmentService`, tests. 174 files / +3,817 / −2,202. |
| `2646be1` | 2026-08-04 | 一些改动 | 0 | 1 | misc |
| `76219e7` | 2026-08-05 | 手枪模型手部动画 移除无用gradle和gen | 28 | 32 | **Hand animation clips for the pistol model** — the `api\client\animation\**` + `client\animation\GunCameraAnimation` layer, `data\...\animations\item\gun\*`, bone snapshot plumbing |
| `ae6943a` | 2026-08-05 | (merge) | 0 | 1 | merge |
| `d845e2a` | 2026-08-05 | 优化动画 | 5 | 1 | **`mixin\client\ItemInHandRendererMixin` put-away hold** (+38 ln), animation tuning |
| `79099c5` | 2026-08-07 | 修复枪械更新bug | 6 | 1 | gun update bugfix |
| `31050e0` | 2026-08-08 | **大改数据生成** | 6 | 3 | datagen rework; `TGDamageTypes` + damage-type JSON changes |
| `0fd7c2d` | 2026-08-16 | 升级粒子 | 0 | 1 | particle |
| `f98fc93` | 2026-08-16 | 优化import | 11 | 0 | imports only |
| `58f140f` | 2026-08-21 | 同步一些1.4.5.7的数值更改 | 1 | 0 | **1-line balance sync (Terraria 1.4.5.7 values)** |
| `2314209` | 2026-09-10 | 换贴图 | 9 | 9 | textures |
| `3251d02` | 2026-09-10 | **三个附魔** | **17** | 3 | **The 3 gun enchantments**: `compressed_tactics`, `emergency_melee`, `temporary_reserve` + `data\terra_guns\enchantment\*.json`, `enchantable/gun` tag, `EmergencyMeleePacketC2S`, `EMERGENCY_MELEE_COOLDOWN_END` component, `fireRadial`/`spawnRadial`/`getAmmoForShot`/`hasEnoughAmmo`/`consumeAmmo`/`setTemporaryReserveLevel` |
| `ec312d4`/`e81fc5b` | 2026-09-10 | merges | 13 | 0 | merges (carry `f98fc93`-class import changes) |
| `ea14bb6` | 2026-09-10 | update | 0 | 1 | `build.gradle` (HEAD) |

**Net:** unlike TerraEntity, TerraGuns post-fork work is **substantial: ~150 java file touches**. But the
gun deep-dive showed A's inlined gun tree **already contains the equivalent of `ba9feda`'s structural
output** (`common\item\gun\definition\**` 19 files, `common\combat\gun\**` 9 files, all 13 behaviors) — A
rebuilt it independently in `dfcc5c041` (2026-08-15), `90dfd7804` (2026-08-18), `9bc04295b` (2026-08-19),
`ce1ca67f8` (2026-08-22). What A does **not** have is:

1. `TaczAnimationConstraint` (ICA constraint math) — from `ba9feda`
2. the 3 gun enchantments + service + packet + tag + component — from `3251d02`
3. the put-away-hold mixin — from `d845e2a`
4. `EJECT_SHELL` / `shell_action` hand-animation channel — from `76219e7`
5. the three unit tests — from `ba9feda` (A has no `src\test` at all)
6. per-field fidelity of the thinner A versions: measured similarity A↔B is
   `AmmoStats` ~11 %, `Ballistics` ~13 %, `GunStats` ~11 %, `GunFiringService` ~26 %,
   `ShootingService` ~34 %, `ShotContext` ~43 %, `BulletTrailStyles` ~34 %, `BulletDefinition` ~46 %,
   `GunDefinition` ~58 %, `BaseBulletEntity` ~50 % (A 494 ln vs B 699 ln), `BaseGun` ~55 %,
   `GunRenderer` ~70 %. Behaviour-only additions (radial fire, ammo-count gating, temporary reserve)
   are the causes.
7. `58f140f` 1.4.5.7 balance numbers (1 line — trivially checkable)

### 5.4 Tier-list of post-fork work to harvest

| Priority | Item | Source | Est. cost |
|---|---|---|---|
| P0 | 3 gun enchantments + `GunEnchantmentService` + `EmergencyMeleePacketC2S` + `enchantable/gun` tag + component | TerraGuns `3251d02` (+ `ba9feda` consumer deltas) | **High** — B's are 1.21 *data-driven* enchantments (`data/<ns>/enchantment/*.json` + `ResourceKey<Enchantment>`); A's enchantments are code-registered `Enchantment` subclasses with no `enchantable/*` tag concept, so all three must be **re-authored as 1.20.1 Java enchantments** |
| P0 | Animation/keyframe/rig framework re-introduction | A history `2569be361` (162 basenames) + TerraEntity HEAD for the 38 never-inlined files | **High** — mechanical restore + Forge↔NeoForge reconciliation; largest diffs `InverseKinematics3D`, `Vec3KeyframeAnimation`, `IInterpolator` |
| P1 | Trade/chat/chester/mapped-data/effect-strategy **registries** (the 12 missing custom registries) | TerraEntity HEAD | **High** — this is the single biggest architectural decision: keep TE's SPI or rewrite the 43 `integration\terra_entity` classes + 18 shop JSONs |
| P1 | `TaczAnimationConstraint` (197 ln) | TerraGuns `ba9feda` | Low–Medium — self-contained math; GeckoLib 4 naming only |
| P1 | Gun-core field fidelity (`Ballistics`, `GunStats`, `AmmoStats`, `GunFiringService`, `ShootingService`, `BaseBulletEntity` +160 ln) | TerraGuns `ba9feda` + `58f140f` | Medium |
| P2 | Motion blur (9 files + 7 shaders), camera shake (3), trails (10), post effects (3), `Easing`/`SmoothFloat` | TerraEntity HEAD | Medium |
| P2 | Stardust Dragon (+segment+staff), 6 summon-sword staffs, `wallet`, `FigureBlock`, `HouseDetectItem`, `SummonFocusEffect` | TerraEntity HEAD | Low (content, assets needed) |
| P2 | Behaviour-tree decoration nodes + advanced composites/conditions + BT web viewer | A history `2569be361` + TE config | Low–Medium |
| P3 | `ItemInHandRendererMixin` put-away hold, `EJECT_SHELL`/`shell_action` channel, 3 gun tests | TerraGuns `d845e2a`, `76219e7`, `ba9feda` | Low |
| P3 | TerraEntity code fixes: trade-screen double-click `47396cf5`, Chester message `0c2b12bf`, crit-rate map swap `82077df8`, boss-summon `1be5db6b` | TerraEntity commits | Low (35 java lines total) |
| P3 | Config keys + `TEAttributeModifierConfig` | TerraEntity `config\**` | Low |

---

## Section 6 — Caveats: what I could not verify

1. **No compilation, no runtime, no tests were run.** Every verdict is static. A has **no `src\test` at all**;
   B ships 3 gun tests. No claim here about behavioural equivalence is backed by execution.
2. **`(hist)` parity is unverified.** The ~162 history-only classes were compared by line count and
   diff-line count against B (byte identity is impossible because the package declaration differs). The two
   largest drifts — `InverseKinematics3D` (82 diff lines) and `Vec3KeyframeAnimation` (69) — must be diffed
   by hand before reuse.
3. **`INLINED-RENAMED` relies on semantic reading, not exhaustive proof.** A functionally-equivalent class
   under a completely unrelated name could have escaped the token search. I mitigated this from the content
   side (entity/item/effect/enchantment language-key diffs, 27 gun item ids, 20 bosses, 90 monsters, 40
   critters) rather than proving it class by class.
4. **Untouched remotes.** I inspected only local refs of the 1.20 checkout:
   `forge-dev/1.20.1`, `neoforge-dev/1.21.1`, `backup/rebased-line-c63f84eaa-20260920`,
   `origin/{forge-dev/1.20.1, neoforge-dev/1.21.1, neoforge/1.21.1, forge/1.20.1}`, plus
   `git rev-list --all --objects` (8,204 historical java paths) and the five A modules on disk. An
   un-fetched colleague branch could still hold something.
5. **`UNVERIFIED` items in §3.2 stand as listed** — in particular `UpdateBlackboardPacket`,
   `DeathAnimOptions`, `FishingHookInFluidLock`/`QuestedFishPrecheckLock` mapping to A conditions, the
   weighted `InitialWeapons` semantics, and whether A's live behaviour tree is feature-complete relative to
   B's `HIST` goal tree for any specific boss.
6. **The 43/44 counts in the task description.** I measured **43** java files under
   `integration\terra_entity\**` (matches), and **58** java files under `mixin\integration\**`, of which
   **14** are under `mixin\integration\terraentity\**` and **44** are unrelated mod integrations — the last
   figure matches the task's "44", so I have treated the 44 as the non-TerraEntity set.
7. **The pointer-bump command.** `git -C D:\Minecraft\1.21neoforge\confluence log --oneline 795ac9ccc..HEAD
   -- TerraEntity TerraGuns` in the *1.21* repo lists 34 commits, but those are pointer bumps on the 1.21
   branch, not the 1.20 branch's history. I used the 1.20 checkout (`.gitmodules` history: `f4b42537c`
   2026-06-10 TerraEntity, `57d824a9d`/`d194600b9` 2026-06-12 TerraGuns) plus the actual submodule logs
   (`5d2c1659..HEAD` = 20 commits, `e201abd5..HEAD` = 15 commits). The 1.20 branch shared history lives in
   `origin/forge-dev/1.20.1` inside the 1.21 clone; `b20c0cefd`/`7f83b379a` are contained only in
   `origin/forge-dev/1.20.1`, not in `neoforge-dev/1.21.1`.
8. **`TerraEntity` shadowing.** `D:\Minecraft\1.21neoforge\confluence\TerraEntity\` also contains a
   vendored `com\github\edg_thexu\cafelib\**` (6 files: `CafeLib`, `AddPreloadResourceEvent`,
   `LivingSpawnForbidden`, `PreReloader`, `IBiomeInfo`, `data\pack\resources\*`) — a third-party data-pack
   preloader. No `cafelib` counterpart exists in A. Treated here as `LIBRARY-ONLY` (a standalone library
   helper), but flagged because it appears in 1 of the 860 files' packages.
9. **Assets were out of scope.** B's `TerraEntity` ships **1,103 files** under `src\main\resources\**` (geo
   models, animations, textures, sounds, particle definitions, shader programs, xaero icons) plus **521** under
   `src\generated\resources\**`; `TerraGuns` adds 102 + generated. A ships **10,420** main-resource files and
   **10,016** generated — i.e. an order of magnitude more. I compared only code and datapack/language keys.
   A systematic asset diff (geo/animation/texture/sound for the 20 bosses, ~90 monsters, 41 critters) was
   **not** performed and could hide a real visual gap — though A does ship `client\entity\model\**` (24) and
   `client\entity\renderer\**` (55) for its entities, so the models exist in some form.
