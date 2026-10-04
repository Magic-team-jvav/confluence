# port2native rule set — notes, schema, counts, corrections

**Scope.** Machine-readable rules that let a source-to-source converter turn 1.20.1-Forge
"Port vocabulary" source (`org.mesdag.portlib.*`) into NeoForge 21.1.219 / Minecraft 1.21.1
native source. Read-only with respect to both mod branches: the only files written are under
`tools/port2native/` and `notes/`.

**Ground truth used (nothing guessed):**

| Source | What it is |
|---|---|
| `D:\Minecraft\1.20forge\confluence\PortLib\src\main\java` | The PortLib sources, 645 `.java` files. The `Port` side of every rule was read from here, and every `org.mesdag.portlib.**` type was enumerated (946 declared types including nested ones). |
| `D:\Minecraft\1.21neoforge\confluence\build\_nfsrc_219\` | Decompiled NeoForge 21.1.219 + MC 1.21.1. **Contains only `net/minecraft/**` and `net/neoforged/neoforge/**`** (6 315 `.java`; 8 806 declared types incl. nested). |
| `notes\_tmp\fml-src\` | Extracted `net.neoforged.fancymodloader:loader:4.0.42` sources. `_nfsrc_219` has **no** `net/neoforged/fml/**` at all, so FML claims cannot be checked there. |
| `notes\_tmp\bus-src\` | Extracted `net.neoforged:bus:8.0.5` sources. Likewise no `net/neoforged/bus/**` in `_nfsrc_219`. |
| `%USERPROFILE%\.gradle\caches\...\neoforge\21.1.219\...\neoforge-21.1.219-moddev-config.json` | Pins the two library versions above (`"net.neoforged.fancymodloader:loader:4.0.42"`, `"net.neoforged:bus:8.0.5"`), which is why those two source jars are the correct evidence. |
| `D:\Minecraft\1.20forge\confluence\{ConfluenceOtherworld,Confluence-Magic-Lib,TerraCurio,TerraFurniture}\src` | 3 083 `.java`; **162 unique `org.mesdag.portlib.*` import lines** (159 type imports + 3 static). |
| `notes\PORTLIB_API_INVENTORY.md` | The prior prose inventory. Used as a starting hypothesis, then re-verified; §7 below lists where it was wrong. |

---

## 1. Deliverables

| File | Contents |
|---|---|
| `tools/port2native/rules/types.json` | `{generatedFrom, types[]}` — one entry per `org.mesdag.portlib.**` (and `PortLib.extensions.**`) type: `port`, `kind`, `native`, `confidence`, `notes`, plus `copyTo` when `kind == "shared"`. |
| `tools/port2native/rules/callsites.json` | `{generatedFrom, kinds, rules[]}` — textual rewrite rules: `id`, `pattern` (regex), `replace`, `kind` (`safe`/`review`), `typeImports[]`, `addImports` (bool), `notes`. |
| `tools/port2native/rules/event-bus.json` | `{generatedFrom, busRule, events[]}` — `port`, `native`, `bus` (`mod`/`game`/`""`), `notes`. |
| `tools/port2native/rules/imports.json` | `{generatedFrom, removePrefixes, conventions, staticImports[], addImports[], map[]}` — the import-addition/removal table. |
| `tools/port2native/rules/forge-to-neoforge.json` | `{generatedFrom, scope, types[]}` — rules for source that imports **Forge directly** instead of going through PortLib. Same schema as `types.json`; `port` holds the Forge FQN and each entry carries an extra `occurrences` count. 161 entries covering all 158 distinct Forge import targets (665 import lines). |
| `tools/port2native/generators/*.ps1` | The generators that produce the five rule files (see §8). |
| `tools/port2native/generators/out/*.tsv` | The parsed native type tables the generators emit and validate against. |

---

## 2. Schema

### 2.1 `types.json`

```json
{
  "port": "org.mesdag.portlib.network.codec.PortStreamCodec",
  "kind": "alias",
  "native": "net.minecraft.network.codec.StreamCodec",
  "confidence": "high",
  "notes": "Same method set: of/ofMember/unit/composite(1..6)/dispatch/map/... "
}
```

`kind` semantics as specified:

* **`alias`** — replace references *and imports* of `port` with `native` (a fully-qualified
  class or nested class).
* **`static-alias`** — `port` is a holder of statics/constants; replace only the **qualifier**.
  `native` is still given. Used for `PortByteBufCodecs`→`ByteBufCodecs`,
  `PortItemAbilities`→`ItemAbilities`, `PortEffectCures`→`EffectCures`,
  `PortSoundActions`→`SoundActions`, `PortSpawnPlacementTypes`→`SpawnPlacementTypes`,
  `PortRegistries`→`NeoForgeRegistries`.
* **`shared`** — loader-independent; must be **copied** into the 1.21 tree. `native` is `""`
  and `copyTo` names the proposed destination.
* **`drop`** — nothing PortLib-shaped replaces it; the import is removed and the call sites are
  flagged for manual work. (`native` is `""`; where a *vanilla* class now provides the feature
  the class is named in `notes`, which is the case for the backported tuff/copper-bulb content
  and the backported sounds.)
* **`manual`** — a human decision is required; `notes` says exactly what.
* **`unknown`** — could not be verified.

`confidence` is `high` (read from source and hand-checked), `medium` (mechanically resolved,
structure not audited member by member), or `low` (auto-resolved by name or explicitly
unverifiable). **Treat every `low` as "do not apply without review".**

### 2.2 `callsites.json`

`pattern` is a .NET/PCRE regex applied to source text; `replace` uses `\1`-style group
references. **An empty `replace` is meaningful: it means "delete the matched text"** (16 rules
do this — e.g. `port-eventhandler-wrapevent` removes the call, `port-attachment-cast` removes an
unnecessary cast, `port-sprite` deletes a type reference that must be split by hand). Do not
treat a blank `replace` as a missing value. `kind == "safe"` means mechanical and
signature-compatible; `kind == "review"` means the rewrite is a best guess that a human must
check, and the converter should also emit it into the TODO report. `typeImports` lists the FQNs
the replacement text needs; `addImports == true` means the converter must add any of them that
are not already imported.

**Rule ordering matters.** Apply the specific rules (e.g. `port-bytebufcodecs-UUID`,
`port-bytebufcodecs-json`) *before* the blanket qualifier rules
(`port-bytebufcodecs-qualifier`, `port-configspec-qualifier`), otherwise the blanket rule wins
and the review case is lost.

### 2.3 `event-bus.json`

`bus` is `"mod"` when the **native** event class implements
`net.neoforged.fml.event.IModBusEvent` — directly or through any supertype — and `"game"`
otherwise. `bus == ""` means the entry is not an event class at all (a nested `enum`/`record`
/functional interface, one of the four bus-adapter base types, or a PortLib adapter that
disappears), so no bus applies.

* game bus = `net.neoforged.neoforge.common.NeoForge.EVENT_BUS` (`NeoForge.java:17`,
  the only member of that class).
* mod bus = `ModContainer#getEventBus()` (`notes\_tmp\fml-src\net\neoforged\fml\ModContainer.java:145`
  — note it is `@Nullable` **and** `abstract`); in practice the `IEventBus` injected into the
  `@Mod` constructor.

### 2.4 `imports.json`

`map[]` gives one row per PortLib import line: `action` is `replace` (swap for `native`),
`move` (the class is copied to `native` = a package), `remove` (delete the line) or `manual`
(leave it and emit a TODO). `addImports[]` groups the FQNs each callsite rule needs.
`staticImports[]` covers the one PortLib static import the mod actually uses.

---

## 3. Counts

### `types.json` — 954 entries

| `kind` | count |
|---|---|
| `alias` | 611 |
| `drop` | 195 |
| `shared` | 76 |
| `manual` | 63 |
| `static-alias` | 7 |
| `unknown` | 2 |
| **total** | **954** |

The 7 `static-alias` entries are `diff.PortRegistries`, `network.codec.PortByteBufCodecs`,
`network.PortPacketDistributor`, `wrapper.common.PortEffectCures`, `wrapper.common.PortItemAbilities`,
`wrapper.common.PortSoundActions` and `wrapper.world.entity.PortSpawnPlacementTypes`.

| `confidence` | count |
|---|---|
| `high` | 776 |
| `medium` | 166 |
| `low` | 12 |

The 12 `low` entries are: `diff.PortCommonHooks` (unknown), `wrapper.serialization.PortJavaOps`
(unknown), the four correctly-name-resolved but never hand-audited aliases
`diff.attachment.PortSyncAttachmentsPayload`, `network.codec.PortStreamDecoder`,
`PortStreamEncoder`, `PortStreamMemberEncoder`, and the six PortLib-only / unverifiable manual
entries `wrapper.fluids.PortFluidType` plus the five `IPort*Extension` interfaces
(`IPortChunkMapExtension`, `IPortConfigValueExtension`, `IPortForgeRegistryExtension`,
`IPortTextureAtlasExtension`, `IPortTimerExtension`).

### `callsites.json` — 208 rules (129 `safe`, 79 `review`)

### `event-bus.json` — 426 entries

| `bus` | count |
|---|---|
| `game` | 333 |
| `mod` | 57 |
| *(none — not an event class)* | 36 |

### `imports.json` — `map` 954 rows, `addImports` 194 groups, 1 `staticImports` row

### `forge-to-neoforge.json` — 161 entries

| `kind` | count | | `confidence` | count |
|---|---|---|---|---|
| `alias` | 131 | | `high` | 87 |
| `manual` | 28 | | `medium` | 74 |
| `drop` | 2 | | `low` | 0 |
| `unknown` | 0 | | | |

Covering all 158 distinct Forge import targets (665 import lines) — 153 real types plus 5 wildcard
packages. See §10 for the per-module counts and the `manual`/`medium` lists.

### Coverage of the actually-used set

The 1.20 mod source imports **162** distinct `org.mesdag.portlib.*` lines (159 types + 3 static).
All 159 appear in `types.json`. Wildcard imports were computed, not assumed — the complete set is:

```
2  import org.mesdag.portlib.registries.*;
2  import org.mesdag.portlib.event.client.*;
1  import org.mesdag.portlib.event.entity.player.*;
1  import org.mesdag.portlib.event.entity.living.*;
1  import static org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier.Operation.*;
```

Note: the task brief states the 1.20 code uses `import org.mesdag.portlib.wrapper.common.extensions.*;`.
**It does not** — that wildcard appears nowhere in the four source trees. The 21
`IPort*Extension` interfaces are imported individually. All 78 files in that package are still
covered in `types.json` as a precaution.

---

## 4. Every `manual` entry (62) and `unknown` entry (2)

Each row is `port` — what a human must decide.

### 4.1 Networking / payloads

| port | decision |
|---|---|
| `network.IPortPacket.S2C` | The direction marker disappears; the direction is chosen at **registration** time by `PayloadRegistrar.playToClient` (`PayloadRegistrar.java:44`), `configurationToClient` (`:70`) or `commonToClient` (`:96`). Decide per payload which registrar method replaces the marker and whether the phase is play or configuration; `work(Player)` becomes the `IPayloadHandler` body. |
| `network.IPortPacket.C2S` | Mirror: `playToServer` (`:52`) / `configurationToServer` (`:78`) / `commonToServer` (`:104`). There is **no** login phase — login-era payloads are `common*` + `versioned()`. |
| `network.PortPayloadHandler` | Shape differs: PortLib is constructed `(namespace, version)` and registers by `(Class, ResourceLocation)`; `PayloadRegistrar` comes from `RegisterPayloadHandlersEvent.registrar(version)` (`RegisterPayloadHandlersEvent.java:40`) and registers by `CustomPacketPayload.Type`. Method renames are mechanical once the idiom is rewritten (see the `port-payloadhandler-*` rules). |
| `network.PortConnectionType` | No drop-in enum: native `ConnectionType` has exactly `NEOFORGE`/`OTHER` (`ConnectionType.java:19,:25`). Rewrite `MODDED`→`NEOFORGE`, `VANILLA`→`OTHER`; note `OTHER` also covers non-NeoForge loaders (doc at `:22`). |
| `network.PortNetworkHandler.C` | Private/unused nested helper of the SimpleChannel shim; disappears with it. |

### 4.2 Registration

| port | decision |
|---|---|
| `registries.PortRegisterHandler` | **No single native target** — it is a static factory hub. `item()`→`DeferredRegister.createItems`, `block()`→`createBlocks`, `dataComponent()`→`createDataComponents`, `attachment()`→`create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, …)`, `particleType()`→`create(Registries.PARTICLE_TYPE, …)`, `attribute()`→`create(Registries.ATTRIBUTE, …)`, `armorMaterial()`→`create(Registries.ARMOR_MATERIAL, …)`, `ingredientType()`→`create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, …)`, `custom()`→`create` + `makeRegistry`. `init(IEventBus)` has no twin (`DeferredRegister.register(IEventBus)` is called per register). See the `port-registerhandler-*` rules. |
| `registries.PortCustomRegistration` | `DeferredRegister.makeRegistry(Consumer<RegistryBuilder<T>>)` (`DeferredRegister.java:259`) replaces the flow, but `get(ResourceLocation)`/`getKey(R)`/`getHolder`/`byNameCodec` have no method-for-method peers — retarget each accessor onto `Registry`/`DeferredRegister`. |
| `registries.PortAttributeRegistration.AttributeMaker` | The maker collapses: `Attribute` exposes `setSyncable(boolean)` (`Attribute.java:52`) and `setSentiment(Sentiment)` (`:57`) directly — but each fluent chain must be re-authored. |
| `registries.PortParticleTypeRegistration.PortLibParticleType` | A project-local `ParticleType` subclass is still required because `ParticleType` is abstract with `codec()` (`ParticleType.java:18`) and `streamCodec()` (`:20`). Author it. |
| `registries.PortRegistryEntry.Memoized` | PortLib-only lazy holder; `DeferredHolder` has no peer (`value()`/`get()` are already lazy). |
| `registries.callback.PortAddCallback.Vanilla` | No native peer; native callbacks are `RegistryCallback`/`AddCallback`/`BakeCallback`/`ClearCallback` (`AddCallback.java:16,:25`). |
| `diff.IPortMappedRegistry` | Alias surface (`portlib$addAlias`/`getAliaes`/`resolve`, `confluence$onAdd`). **`Registry#addAlias` does not exist in 1.21.1**; `DeferredRegister.addAlias(ResourceLocation,ResourceLocation)` does (`DeferredRegister.java:303`). Move alias registration there and delete the mixin contract. |
| `diff.IPortAttribute` | `Attribute.Sentiment` **does** exist in 21.1.219 (`Attribute.java:95`, POSITIVE/NEUTRAL/NEGATIVE; `setSentiment` `:57`, `getStyle` `:76`), so the contract is almost entirely redundant — fold each call onto plain `Attribute`. Only the tooltip re-styling helper needs a decision. |
| `diff.datamap.PortRegistryDataMapNegotiation` | Client/server negotiation NeoForge now performs internally. Decide per call site: delete, or replace with a `RegistryManager`/`DataMapsUpdatedEvent` hook. |

### 4.3 Configuration / client GUI

| port | decision |
|---|---|
| `config.PortConfigSpec.ListValue` | **No native type.** `defineList`/`defineListAllowEmpty` return `ConfigValue<List<? extends T>>`; only `ConfigValue`/`BooleanValue`/`IntValue`/`LongValue`/`DoubleValue`/`EnumValue` exist (`ModConfigSpec.java:1186`–`:1352`). Re-type the field. |
| `config.PortConfigSpec.NumberValue` | No native type. Pick `IntValue` (`:1303`), `LongValue` (`:1319`) or `DoubleValue` (`:1335`) per primitive. |
| `config.PortConfigSpec.StringValue` | No native type. A string value is `ConfigValue<String>` (`:1186`). |
| `client.gui.components.PortSprite` | No native peer (it bundles a `ResourceLocation` with width/height). Split each site: keep the location, pass the dimensions to `GuiGraphics.blitSprite(ResourceLocation,int,int,int,int)` (`GuiGraphics.java:677`) or the 9-arg overload (`:706`). |
| `client.gui.PortConfigurationScreen.Change`, `.ConfigList`, `.ConfigRow`, `.ConfigValuesScreen`, `.ConfigValuesScreen.IntegerSlider`, `.EditHistory`, `.InvalidValue` | PortLib config-screen internals only. NeoForge ships `ConfigurationScreen` (`ConfigurationScreen.java:110`) plus `ConfigurationSectionScreen`; re-author the screen code. |
| `client.PortGuiLayer.Delegate` | No native peer; `LayeredDraw.Layer` (`LayeredDraw.java:42`) is the only native contract. |

### 4.4 Wrapper / extension vocabulary

| port | decision |
|---|---|
| `wrapper.common.PortTags` **and nested** `PortTags.{Blocks,Items,Fluids,DamageTypes,EntityTypes,Biomes}` | 514 lines of `TagKey` constants, many in the `forge:` namespace. Every constant needs an individual decision: vanilla `ItemTags`/`BlockTags`, NeoForge `Tags.Items` (`Tags.java:312`)/`Tags.Blocks` (`:30`), or delete. Not mechanisable. |
| `wrapper.common.PortPercentageAttribute` | No native percentage attribute. Keep a small local `RangedAttribute` subclass (`RangedAttribute.java:8`, ctor `:18`) or drop the percent formatting. |
| `wrapper.common.PortSimpleTier` | `Tier` is still an interface, but the 1.21 shape differs (it added `getIncorrectBlocksForDrops()`); re-derive rather than substitute. |
| `wrapper.core.particles.PortParticleOptions` | Restructure into a `ParticleType` subclass implementing `codec()`/`streamCodec()`; `ParticleType` is abstract with a protected ctor (`ParticleType.java:10`). |
| `wrapper.fluids.PortFluidType` (and `.DripstoneDripInfo`) | Forge-era `FluidType.DripstoneDripInfo`; the 21.1.219 dripstone-drip shape could not be confirmed. Check each member against `net.neoforged.neoforge.fluids.FluidType`. |
| `wrapper.resource.PortContextAwareReloadListener` | `PreparableReloadListener` exists, but PortLib's condition-context/registry plumbing is an invention layered on `ConditionalOps` (`retrieveContext()` at `ConditionalOps.java:37`). Decide how much survives. |
| `wrapper.serialization.PortJavaOps.FixedMapBuilder` | Internal helper; disappears with `PortJavaOps`. |
| `wrapper.world.{effect.MobEffectHolder, entity.ai.attributes.AttributeHolder, item.alchemy.PotionHolder, item.enchantment.EnchantmentHolder, level.block.BlockHolder, level.gameevent.GameEventHolder}` | No native class. Every use becomes a `net.minecraft.core.Holder<X>` with a **fixed** type argument (`MobEffect`, `Attribute`, `Potion`, `Enchantment`, `Block`, `GameEvent`) — which a type alias cannot express. Prefer `DeferredHolder<X,Y>` when the value comes from a `DeferredRegister`. |
| `wrapper.common.extensions.IPortForgeRegistryExtension` | `IForgeRegistry` has no 1.21.1 counterpart; data maps live on `Registry`. Delete the interface, retarget each member. |
| `wrapper.common.extensions.IPortConfigValueExtension` | `ModConfigSpec.ConfigValue.get()` already exposes the raw value (`:1220`), so the extra accessor is redundant — but check each site. |
| `wrapper.common.extensions.IPortChunkMapExtension` | Reaches into the internal `ChunkMap` tracker map; 21.1.219 shapes differ. Re-derive each access. |
| `wrapper.common.extensions.IPortTextureAtlasExtension` | `TextureAtlas` internals changed (sprite lookup via `TextureAtlas#getSprite`). Decide per member. |
| `wrapper.common.extensions.IPortTimerExtension` | Tick-timer shim; 1.21.1 exposes `DeltaTracker`. Decide per member. |

### 4.5 Items / recipes / armour

| port | decision |
|---|---|
| `wrapper.world.item.PortArmorItem.PortType` | **No 1.21.1 native**: `net.minecraft.world.item.equipment.ArmorType` is 1.21.2+ (`ArmorType` has **0** occurrences anywhere in `_nfsrc_219`). In 1.21.1 use the nested `net.minecraft.world.item.ArmorItem.Type`. |
| `wrapper.world.item.PortArmorMaterial.Settings` | `ArmorMaterial` is a **record** (`ArmorMaterial.java:13`), so the builder disappears; each call becomes a record construction. |
| `wrapper.world.item.PortItem.TooltipContext` | Not a `PortItem` member at all; the native type is `net.minecraft.world.item.Item.TooltipContext`. |
| `wrapper.world.item.crafting.PortShapedRecipePattern.Data` | No native nested type; `ShapedRecipePattern` (`:22`) is a final class built via `of(…)`/`ofPositioned(…)`. Re-author the call site. |
| `wrapper.world.item.crafting.PortAbstractCookingRecipe.Factory` | The native serializer mechanism differs (`RecipeSerializer`/`MapCodec`). Re-author. |
| `wrapper.world.item.enchantment.PortEnchantmentHelper.EnchantmentSlotVisitor` | *(alias, listed here for the name trap)* native is `EnchantmentInSlotVisitor`, not `EnchantmentSlotVisitor`. |

### 4.6 Events that could not be resolved (5)

| port | decision |
|---|---|
| `event.PortEventHandler`, `event.PortEventHooks`, `event.PortBus` | The PortLib bus adapters. They have no native type: every `addListener`/`postEvent` call site is rewritten by the `port-eventhandler-*` callsite rules, with the bus taken per event from `event-bus.json`. |
| `event.client.PortEntityRenderersEvent.AddLayers.PortModel` | No native nested type. The `AddLayers` registration takes vanilla `PlayerSkin.Model`. Decide whether to delete the registration or retarget it. |
| `event.entity.living.PortLivingChangeTargetEvent.IPortLivingTargetType.Delegate`, `event.registries.PortRegisterEvent.PortRegisterHelper.Delegate` | PortLib-only nested `Delegate` helpers with no native counterpart (`ILivingTargetType`/`RegisterHelper` are functional interfaces natively). Delete. |

### 4.7 `unknown` (2)

| port | decision |
|---|---|
| `diff.PortCommonHooks` | Dev-only `validateComponent(Object)` reflection check. No `CommonHooks#validateComponent` could be confirmed in `_nfsrc_219`. Keep a local copy or delete the validation. |
| `wrapper.serialization.PortJavaOps` | 419-line `DynamicOps<Object>` over Java maps/lists/primitives with no 21.1.219 equivalent. Decide: carry a loader-independent copy, or migrate the affected codecs to `NbtOps`/`JsonOps`. |

---

## 5. Verification method

1. `generators\parse-native-events.ps1` parses `.java` **as text** (a line-based brace/depth
   walker) into a type table: FQN (including nested types), `extends`/`implements` clause, file.
   * `-AllTrees` parses all 6 315 native files → 8 806 declared types (`out\native-types-all.tsv`).
   * Without it, only event-bearing subtrees → 1 927 types, 407 of them events
     (`out\native-event-bus.tsv`).
2. `IModBusEvent` membership is resolved **transitively** over that table, so
   `FMLCommonSetupEvent → ParallelDispatchEvent → ModLifecycleEvent → IModBusEvent` and
   `EntityRenderersEvent.AddLayers → EntityRenderersEvent → IModBusEvent` are both classified
   correctly without special cases.
3. `generators\gen-rules.ps1` parses the PortLib tree the same way (946 types), applies the
   curated rules, resolves the remainder, and then **validates every emitted `native` value and
   every `callsites.json` `typeImports` FQN against the full 8 806-type table**.
   Result: **0 unverified natives** (`generators\out\validation-report.txt`).
4. Every claim about a *member* (method name, arity, line number) was read from the file and is
   quoted in the `notes` fields — e.g. `StreamCodec.composite` arities from
   `net/minecraft/network/codec/StreamCodec.java:112,:127,:150,:177,:208,:243`, and the eight
   `IEventBus.addListener` overloads from `bus-8.0.5-sources.jar!/net/neoforged/bus/api/IEventBus.java:57,69,78,91,101,115,124,137`.

Two parser bugs were found and fixed during this work; both are worth knowing because they
silently corrupt a name table:

* **Generic bounds read as superclasses.** `class Foo<T extends LivingEntity> extends Event`
  yielded superclass `LivingEntity`. Fixed by stripping the first balanced `<…>` before reading
  `extends`. This had mis-classified `RenderLivingEvent` (and any event with a bounded type
  parameter) as a non-event.
* **Multi-line declarations dropped their nested types.** `public record ArmorMaterial(…\n) {`
  pushed the enclosing type and then immediately popped it, so `ArmorMaterial.Layer` was
  recorded as `net.minecraft.world.item.Layer`. Fixed by consuming the header lines in the same
  iteration. This is why `armorMaterial.Layer`, `FoodProperties.PossibleEffect` and
  `DimensionTransition.PostDimensionTransition` initially looked absent.

---

## 6. Non-obvious traps the rules encode

These are the places where a naive "strip `Port` and rename" converter silently produces
compiling-but-wrong code:

1. **`event-bus.json` exists because the bus is not derivable from the Port class.** PortLib's
   `PortEventHandler.postEvent` picks the bus at runtime from `IModBusEvent`
   (`PortEventHandler.java:44-50`). On 1.21.1 that choice must be baked in per call site: a
   wrong choice **compiles and never fires**.
2. **Do not infer the bus from PortLib's `IPortModBusEvent` marker.** Verified: `PortEvent` only declares
   `extends Event` (`PortEvent.java:11`) and does *not* implement `IPortModBusEvent`; of the 62 client
   event files only **5** mention the marker at all, and only **3** carry it on their top-level class
   (`PortRegisterColorHandlersEvent`, `PortRegisterGuiLayersEvent`, `PortRenderLevelStageEvent` — the
   other two, `PortEntityRenderersEvent` and `PortModelEvent`, mention it only on nested classes).
   The inventory's "16 of the 62" figure does not reproduce; its 16 names are actually the set of
   *natives* that are mod-bus. Always use `event-bus.json`, which is derived from the native classes.
3. **`ModelEvent` is the exception to the "abstract base is the event" pattern.** Native
   `ModelEvent` does **not** implement `IModBusEvent` (`ModelEvent.java:29`); only its four
   nested subclasses do (`:48,:96,:139,:167`). PortLib mirrors this, so the bus verdict is
   per-nested-type.
4. **`DataMapsUpdatedEvent` is a *game*-bus event** (`DataMapsUpdatedEvent.java:26` declares
   `extends Event` with no `IModBusEvent`), unlike the other six registry events.
5. **`ScreenEvent` nesting moved.** PortLib nests `PortResult` inside the abstract
   `MouseButtonPressed`/`MouseButtonReleased` (`PortScreenEvent.java:205,:288`); native nests
   `Result` one level deeper inside `MouseButtonPressed.Post`/`.Post`
   (`ScreenEvent.java:452,:565`). The constants are identical, so only the *path* breaks.
6. **`StreamCodec.dispatch`/`map`/`apply`/`mapStream`/`cast` are default *instance* methods
   natively**, while PortLib exposes them as statics taking the codec first
   (`StreamCodec.java:59,:63,:77,:91,:298`). A literal name substitution produces
   uncompilable code; the receiver must move to the front. Marked `review`.
7. **`IEventBus` has eight `addListener` overloads, not four** (`IEventBus.java:57,69,78,91`
   plus the `receiveCanceled` family at `:101,115,124,137`). A stray `boolean` as the first
   argument silently selects `addListener(boolean, Consumer)`.
8. **`enqueueWork` is overloaded** on `IPayloadContext` (`Runnable` at `:81`,
   `Supplier<T>` at `:86`), so a lambda with a value-returning body changes its own return type.
9. **`PortByteBufCodecs.UUID`/`VECTOR4F`/`BLOCK_POS`/`UNBOUNDED_BYTE_ARRAY` have no home in
   `ByteBufCodecs`.** The natives are `UUIDUtil.STREAM_CODEC` (`UUIDUtil.java:43`),
   `BlockPos.STREAM_CODEC` (`BlockPos.java:40`) and
   `NeoForgeStreamCodecs.UNBOUNDED_BYTE_ARRAY` (`NeoForgeStreamCodecs.java:20`); `VECTOR4F` has
   none. `PortByteBufCodecs.json(…)` has no native twin at all — the string `json`/`Json` does
   not occur anywhere in `ByteBufCodecs.java`.
10. **`PacketDistributor` method names drop the `All`.** `sendToPlayersTrackingEntity` (`:89`),
    `sendToPlayersTrackingEntityAndSelf` (`:101`), `sendToPlayersTrackingChunk` (`:113`),
    `sendToPlayersNear` (`:65`). `sendToAllPlayersTrackingBlock` and `sendToNearbyPlayers` do
    not exist. `sendToPlayersInDimension`/`sendToPlayersNear` take `ServerLevel`, not
    `ResourceKey<Level>` → signature break.
11. **`PayloadRegistrar` has no static `create(...)`** and none of `registerInGame*`/`registerLogin*`
    exist anywhere. Its `play*` methods take
    `StreamCodec<? super RegistryFriendlyByteBuf,T>` while `configuration*`/`common*` take
    `StreamCodec<? super FriendlyByteBuf,T>` — the codec bound changes with the phase.
12. **`CustomPacketPayload` has no `write(...)`, and `Type` has no `create`**; the factory is
    `CustomPacketPayload.createType(String)` (`CustomPacketPayload.java:20`).
13. **`PortAttributeModifier.Operation` already uses the 1.21 names** (`ADD_VALUE`,
    `ADD_MULTIPLIED_BASE`, `ADD_MULTIPLIED_TOTAL` — PortLib unwraps to the 1.20 Forge spelling
    internally, `PortAttributeModifier.java:80-92`), so the mod source is already native and the
    static-import swap is `safe`. The *legacy* names are given `review` rules.
14. **`Event`/`EventPriority`/`ICancellableEvent`/`IModBusEvent` live outside `_nfsrc_219`** —
    they are in `bus:8.0.5` and `fancymodloader:loader:4.0.42`.
15. **`HOE_TILL` registers under the string `"till"`** (`ItemAbilities.java:116`), so
    string-keyed `ItemAbility.get(…)` lookups must not assume `"hoe_till"`.
16. **The singular wrappers declare no constants.** `EffectCure`, `ItemAbility`, `SoundAction`
    and (in NeoForge) `TriState` carry zero constants; everything is on `EffectCures`,
    `ItemAbilities`, `SoundActions`. So `PortSoundEvents`→`SoundActions` is a singular→plural
    qualifier swap.
17. **`PortRegistryEntry.isPresent()` has no native target**: `DeferredHolder` implements
    `Holder` and `Supplier`, neither of which declares it. Use `isBound()`
    (`DeferredHolder.java:197`) or `asOptional()` (`:125`).
18. **`IAttachmentHolder`'s `Supplier` overloads are asymmetric**: `hasData` takes
    `AttachmentType<?>` (`:24`) while its overload takes `Supplier<AttachmentType<T>>` (`:29`);
    the other accessors pair `AttachmentType<T>` with `Supplier<AttachmentType<T>>`
    (`:39/:46`, `:55/:64`, `:71/:81`, `:90/:97`, `:106/:113`, `:125/:137`). `DeferredHolder`
    implements `Supplier`, so a `PortRegistryEntry` argument still compiles — but it is not an
    `AttachmentType`.
19. **`ItemStack` does not declare `get`/`has`/`getOrDefault`.** They are inherited defaults from
    `net.minecraft.core.component.DataComponentHolder` (`:9,:13,:17`); `ItemStack` itself only
    declares `set` (`:716`), `remove` (`:732`) and two `update(...)` (`:721,:726`).
20. **`ARMOR_MATERIAL` is singular** (`Registries.java:202`), and `DATA_COMPONENT_TYPE` is
    *vanilla* `BuiltInRegistries.DATA_COMPONENT_TYPE` (`BuiltInRegistries.java:264`), not on
    `NeoForgeRegistries`.
21. **`QuadLighter` is abstract with a protected ctor** (`QuadLighter.java:28,:51`) and
    `Projectile#hitTargetOrDeflectSelf` is `protected` (`:177`) — neither is a drop-in for a
    constructor/public call.
22. **`BundleContents`, `ChargedProjectiles`, `ItemEnchantments` and `ShapedRecipePattern` are
    classes, not records** (`BundleContents.java:20`, `ChargedProjectiles.java:12`,
    `ItemEnchantments.java:31`, `ShapedRecipePattern.java:22`), whereas PortLib declares the
    corresponding `Port*` types as records. Record-shaped call sites (`new PortTool(...)`,
    destructuring) need adjusting.

---

## 7. Corrections to `notes\PORTLIB_API_INVENTORY.md`

Every row below was checked against `_nfsrc_219` (or the two pinned source jars where
`_nfsrc_219` has no such package). **What the inventory said → what is actually there.**

| # | Inventory claim (section) | Finding |
|---|---|---|
| 1 | `PortAddCarversBiomeModifier` → `NeoForgeBiomeModifiers`, and `NeoForgeBiomeModifiers.ADD_CARVERS` (§1.5.2) | **No `NeoForgeBiomeModifiers` class exists.** The record is nested: `net.neoforged.neoforge.common.world.BiomeModifiers.AddCarversBiomeModifier` (`BiomeModifiers.java:226`), and the registered codec is `NeoForgeMod.ADD_CARVERS_BIOME_MODIFIER_TYPE` (`NeoForgeMod.java:276`). Its components are `(HolderSet<Biome>, HolderSet<ConfiguredWorldCarver<?>>, GenerationStep.Carving)`, which no longer matches the PortLib shape. |
| 2 | `IPortEntityWithComplexSpawn` → vanilla `IEntityWithComplexSpawn` (§1.5.4) | **Wrong package.** It is `net.neoforged.neoforge.entity.IEntityWithComplexSpawn` (`:6`, decl `:14`). Its methods take `RegistryFriendlyByteBuf` (`:21,:29`), not `FriendlyByteBuf` — a silent signature break. |
| 3 | `IPortAttributeExtension.Sentiment` / `IPortAttribute` / `PortAttributeRegistration` are `UNKNOWN` because `Attribute.Sentiment` is a 1.21.2 feature (§1.2, §1.6.1, R9) | **`Attribute.Sentiment` exists in 21.1.219**: `Attribute.java:95`, constants `POSITIVE/NEUTRAL/NEGATIVE` (`:96-:98`), `setSentiment` `:57`, `getStyle` `:76`. So this is not a version-drift item. |
| 4 | "`PortRegistration` → `DeferredRegister` + `Registry#addAlias`" (§1.2, R5) | **`Registry#addAlias` does not exist.** `grep -i alias net/minecraft/core/Registry.java` → 0 matches. Registry aliases are the 1.21.2+ API. Only `DeferredRegister.addAlias(ResourceLocation,ResourceLocation)` exists (`DeferredRegister.java:303`). |
| 5 | "`MappedRegistry#onAdd` (both verified)" (§1.5.3) | **Not a method on `MappedRegistry`.** The declaration is `AddCallback.onAdd(Registry<T>,int,ResourceKey<T>,T)` (`AddCallback.java:25`); the builder form is `RegistryBuilder.onAdd(AddCallback)` (`:62`). `MappedRegistry.java:163` merely *invokes* a callback. |
| 6 | `IPortBlockEntityTypeExtension` → native `IBlockEntityTypeExtension` (§1.5.3) | **No such native interface.** The package was enumerated: `IBlockEntityExtension` exists, `IBlockEntityTypeExtension` does not. `BlockEntityType#getBlockStates()` is a plain method on the vanilla class. |
| 7 | `IPortEntityTypeExtension` / `IPortEntityTypeBuilderExtension` treated as wrapper twins (§1.5.3 group b) | **No `IEntityTypeExtension` and no `IEntityTypeBuilderExtension`** in `net/neoforged/neoforge/common/extensions/`. Both are `shared`, not `alias`. |
| 8 | `IPortItemPropertiesExtension` → "vanilla `Item.Properties#component(...)`" (§1.5.3) | The native interface is **`IItemPropertiesExtensions`** — note the plural `Extensions`. Recorded as an `alias` to that name. |
| 9 | `PortRegistryEntry` FACADE to `DeferredHolder` (§1.2) | True, but **`DeferredHolder` has no `isPresent()`** (it implements `Holder` + `Supplier`). Use `isBound()` (`:197`) or `asOptional()` (`:125`). `getId()`/`getKey()`/`value()`/`get()` all do exist (`:162,:170,:100,:116`). |
| 10 | All six registry events grouped as FACADE mod-bus (§1.3) | **`DataMapsUpdatedEvent` does NOT implement `IModBusEvent`** (`DataMapsUpdatedEvent.java:26`) → it is a **game**-bus event. The other six are mod-bus. |
| 11 | `PortModelEvent` → `ModelEvent` listed as a mod-bus registration event (§1.4) | Native **`ModelEvent` itself is not mod-bus** (`ModelEvent.java:29`); only its four nested subclasses implement `IModBusEvent` (`:48,:96,:139,:167`). Bus is per-nested-type. |
| 12 | "55 of the 71 named-type wrappers are exactly `"Port" + nativeName`" + `PortResult` listed as a nested rename (R2) | The rename is right but the **nesting path also moves** for `PortScreenEvent.{MouseButtonPressed,MouseButtonReleased}.PortResult` → `ScreenEvent.{MouseButtonPressed,MouseButtonReleased}.Post.Result` (`ScreenEvent.java:452,:565`). Path-transparent stripping fails here. |
| 13 | `IModBusEvent`, `IEventBus`, `ModContainer#getEventBus` "verified in the 21.1.219 sources" (R4, §1.3) | **Not verifiable in `_nfsrc_219`** — that tree has no `net/neoforged/fml/**` or `net/neoforged/bus/**` at all. Verified instead from `fancymodloader:loader:4.0.42` and `bus:8.0.5`, the versions 21.1.219 pins. |
| 14 | "the four overloads map 1:1 onto `IEventBus#addListener`" (R4) | True but incomplete: there are **eight** overloads (`IEventBus.java:57,69,78,91,101,115,124,137`). The `receiveCanceled` family means a stray `boolean` picks a different overload. |
| 15 | `PortStreamCodec` → `StreamCodec`, "same method names" (§1.1) | Names match, but **`dispatch`/`map`/`apply`/`mapStream`/`cast` are default instance methods natively** (`StreamCodec.java:59,:63,:77,:91,:298`) while PortLib exposes them as statics taking the codec first. Also `composite` parameter counts are 3/5/7/9/11/13. |
| 16 | `PortByteBufCodecs` → "`ByteBufCodecs` + `NeoForgeStreamCodecs`" (§1.1) | Correct in outline; the mapping is *member-by-member*, not per-class: `UNBOUNDED_BYTE_ARRAY`→`NeoForgeStreamCodecs.UNBOUNDED_BYTE_ARRAY` (`:20`), `UUID`→`UUIDUtil.STREAM_CODEC` (`UUIDUtil.java:43`), `BLOCK_POS`→`BlockPos.STREAM_CODEC` (`BlockPos.java:40`), `VECTOR4F`→nothing, `json(...)`→nothing. |
| 17 | `PortPacketDistributor` → `PacketDistributor` (§1.1) | Names are **not** the PortLib spellings: `sendToPlayersTrackingEntity` (`:89`), `sendToPlayersTrackingEntityAndSelf` (`:101`), `sendToPlayersTrackingChunk` (`:113`), `sendToPlayersNear` (`:65`). `sendToAllPlayersTrackingBlock` does not exist. Two methods take `ServerLevel` instead of `ResourceKey<Level>`. |
| 18 | `IPortPacket` → `CustomPacketPayload`; `Context` → `IPayloadContext` (§1.1) | `CustomPacketPayload` has **no `write(...)`**, and **`Type` has no `create`** — use the interface-level `CustomPacketPayload.createType(String)` (`:20`). `handle(Context)` is replaced by an `IPayloadHandler` supplied at registration. |
| 19 | `PortPayloadHandler` → "PayloadRegistrar — same method set" (§1.1) | Methods map, but **`PayloadRegistrar` has no static `create(...)`** and is obtained from `RegisterPayloadHandlersEvent.registrar(version)` (`:40`); registration is keyed by `CustomPacketPayload.Type`, not `(Class, ResourceLocation)`. Classified `manual`. |
| 20 | `PortLanguageProvider` → `LanguageProvider` FACADE, no caveat (§1.6.2) | **No 3-arg `add(String,String,String)`**, and `addBiome`/`add(Biome,String)` are **commented out** (`LanguageProvider.java:89-97`). Those call sites are hard failures. |
| 21 | `BooleanAttribute` FACADE, "`NeoForgeMod.CREATIVE_FLIGHT` uses it" (§1.5.2) | Correct, but the class is minimal: ctor `(String,boolean)` `:32`, `sanitizeValue` `:37`, `toValueComponent` `:45`, `toComponent` `:58`. **No `create`, no `toValue`, no `getModifier`.** |
| 22 | `PortQuadLighter` → `QuadLighter` (§1.5.4) | Exists, but it is **abstract** with a **protected** ctor (`:28,:51`) — not instantiable. |
| 23 | "`PortArmorItem.PortType` has no 1.21.1 native" (R9) | Confirmed: `ArmorType` has **0** occurrences tree-wide; `net/minecraft/world/item/equipment/` does not exist. |
| 24 | `PortSoundEvents` "26 backported sounds" (§1.5.4) | Confirmed vanilla (e.g. `TUFF_BRICKS_BREAK` `:1410`, `COPPER_BULB_TURN_ON` `:355`, `WET_SPONGE_BREAK` `:1529`), but note **`SoundEvent` has no public ctor** (`:48` private) — use `createVariableRangeEvent` (`:40`)/`createFixedRangeEvent` (`:44`). |
| 25 | "the `forge:` namespace is gone" (§1.5.2) | Mostly true — `net/neoforged/neoforge/common/Tags.java` uses only `"neoforge"` (`:284,:923`) and the only `"forge:` strings are DataFixer field names (`MobEffectIdFix.java:92,:111`) and a comment (`GameRenderer.java:900`). **But ~120 `ResourceLocation.fromNamespaceAndPath("forge", …)` calls survive** in `net/neoforged/neoforge/common/data/internal/*TagsProvider.java` as optional back-compat aliases. So `forge:` is gone as a *primary* namespace but not absent. |
| 26 | `PortConfigSpec` value classes "already match", incl. `ListValue` (§1.6.3) | **There is no `ModConfigSpec.ListValue`.** Only `ConfigValue` (`:1186`), `BooleanValue` (`:1284`), `IntValue` (`:1303`), `LongValue` (`:1319`), `DoubleValue` (`:1335`), `EnumValue` (`:1352`). `defineList` returns `ConfigValue<List<? extends T>>`. |
| 27 | `PortRegistries` → "`NeoForgeRegistries` … + `Keys.ARMOR_MATERIALS`" (§1.6.1) | `ATTACHMENT_TYPES` (`:43`) and `INGREDIENT_TYPES` (`:40`) are right. **`ARMOR_MATERIALS` does not exist** — it is vanilla `Registries.ARMOR_MATERIAL` (`:202`), singular. `DATA_COMPONENT_TYPE` is vanilla `BuiltInRegistries.DATA_COMPONENT_TYPE` (`:264`). |
| 28 | `DeferredRegister` as the registration target (§1.2) | Several members the inventory implies do **not** exist: `createAttachments` (0 hits), `registerSimple` on `DeferredRegister` itself (only on `.Blocks`/`.Items`), `DeferredHolder.isPresent`. |
| 29 | `RegistryBuilder` builder surface (§1.2) | No `allowModification`, `setDefaultKey`, `disableSync` or `make`. The real names are `defaultKey` (`:37,:42`), `sync(boolean)` (`:92`), `create` (`:115`), plus `callback`/`onAdd`/`onBake`/`onClear`/`maxId`. |
| 30 | `PortCustomIngredient` → "NeoForge `ICustomIngredient`; a vanilla `CustomIngredient` does not exist" (§1.5.2, R8) | **Confirmed correct**: `glob **/CustomIngredient.java` → 0 files; `Ingredient` wires `ICustomIngredient` at `Ingredient.java:89,:193`. |
| 31 | `PortTriState` → NeoForge `TriState`, "not vanilla, which is 1.21.2+" (R8) | Confirmed (`TriState.java:11`). No `net.minecraft.util.TriState` anywhere. |
| 32 | `PortDeltaTicker` → `DeltaTracker`, "`RenderTickCounter` does not exist in 1.21.1" (R8) | Confirmed: `DeltaTracker.java:8`; `RenderTickCounter` → **0** matches tree-wide. |
| 33 | `ItemStack` data-component accessors "`:710-760`" on `IPortItemStackExtension` (§1.5.3) | The Port side is right, but on the native side **`ItemStack` does not declare `get`/`has`/`getOrDefault`** — they come from `DataComponentHolder` (`:9,:13,:17`). `ItemStack` declares `set` (`:716`), `remove` (`:732`), `update` (`:721,:726`). |
| 34 | `PortLivingEvent.LivingTickEvent` (task brief, §1.3) | **Neither side has it.** PortLib's `PortLivingEvent` declares only `LivingJumpEvent` (`:22`) and `LivingVisibilityEvent` (`:36`); `LivingTickEvent` has **0** occurrences in the native tree. |
| 35 | "the 1.20 code does use wildcard imports such as `org.mesdag.portlib.wrapper.common.extensions.*`" (task brief) | **That wildcard is not used.** The complete wildcard set is `registries.*` (2), `event.client.*` (2), `event.entity.player.*` (1), `event.entity.living.*` (1) and `static …PortAttributeModifier.Operation.*` (1). The 21 extension interfaces are imported individually. |
| 36 | `ResourceKey.STREAM_CODEC` (implied by R5's "ResourceKey-first" discussion) | **There is no `ResourceKey.STREAM_CODEC` field** — it is the static method `ResourceKey.streamCodec(ResourceKey)` (`:35`); `ResourceKey.codec` is at `:31`. |
| 37 | `AbstractArrow` / `MobEffect` / etc. wrappers (§1.5.4) | `AbstractArrow.Pickup` uses the legacy constants `DISALLOWED`/`ALLOWED`/`CREATIVE_ONLY` (`:721-:723`) and the legacy method is `byOrdinal` (`:725`), not `byId`. `BundleContents`/`ChargedProjectiles`/`ItemEnchantments`/`ShapedRecipePattern` are **classes, not records**. |
| 38 | `ISimpleContainerExtension`, `IPortRecipeExtension`, etc. (§1.5.3) | Not individually contradicted, but the whole 78-file package was re-derived from the directory listing rather than trusting the grouped rows; 16 have a confirmed native twin, 3 were claimed to have one that does not exist (rows 6/7 above), and the rest are `shared`. |
| 39 | "16 of the 62 [`event.client`] do not implement `IPortModBusEvent` although their natives implement `IModBusEvent`" (§1.4) | **The figure does not reproduce, and the marker tracks nothing.** `PortEvent` declares only `extends Event` (`PortEvent.java:11`) — no marker. Of the 62 client event files, **57 never mention `IPortModBusEvent` at all**; only 5 mention it, and only 3 of those on the top-level class (`PortRegisterColorHandlersEvent`, `PortRegisterGuiLayersEvent`, `PortRenderLevelStageEvent`). The inventory's 16 names are in fact the set of *natives* that are mod-bus. The practical consequence is unchanged and is why `event-bus.json` exists: derive the bus from the native class, never from the PortLib wrapper. |
| 40 | "`PortSoundEvents` + `SoundEventHolder` … 26 backported sounds" (§1.5.4) treated as a single classification | Confirmed vanilla, but the replacement is not uniform: `SoundEvent` has **no public constructor** (`SoundEvent.java:48` is private), so a call site that *constructs* one must use `createVariableRangeEvent` (`:40`) / `createFixedRangeEvent` (`:44`), whereas a call site that only *references* a constant maps onto plain `SoundEvents.*`. The rule set currently covers the reference case (`port-backported-sounds`); a construction site needs a hand fix. |

---

## 8. Known limitations

1. **`net.neoforged.fml.**` and `net.neoforged.bus.**` are absent from `_nfsrc_219`.** All FML/bus
   claims rest on the two source jars extracted to `notes\_tmp\fml-src\` and `notes\_tmp\bus-src\`,
   which is why those directories must stay in place for the generators to be re-runnable.
2. **The parser is a line-oriented brace walker, not a Java parser.** It ignores braces inside
   string literals and does not understand comments beyond whole-line `//`/`*`. It is accurate
   enough for the decompiler output here (every nested type was cross-checked by spot-reading
   files) but should not be trusted on hand-written source with braces in strings.
3. **`isEvent` is a heuristic** — "has a supertype whose simple name ends in `Event`". It
   under-detects event types that extend an intermediate non-`Event`-suffixed class
   (`ScreenEvent.KeyPressed extends KeyInput`). The generator compensates by walking the native
   supertype chain when resolving a bus, which is why the final table has a bus for every real
   event class even though the raw `isEvent` flag is `False` for a handful of them.
4. **Member-level verification is sampled, not exhaustive.** Every type name and every *call
   site rule's* target member was checked, but the individual members of the 78 `IPort*Extension`
   interfaces were not enumerated one by one; those are classified `shared`/`medium` for exactly
   that reason.
5. **`diff\mixin` (112 files) and `PortLib.extensions.**` were classified wholesale** as `drop`
   from the inventory's structural finding (each mixin exists to *create* a fire point that 1.21.1
   has natively). That was not verified file by file.
6. **`tools\port2native\rules\_pilot.json` is gone** (deleted after `types.json` landed); it is
   superseded by `types.json` + `callsites.json`. But **`tools\port2native\rules\local-extras.json`
   is still present and is NOT valid JSON** — it will break any loader that globs `*.json` in the
   rules directory. See §10.4 for the diagnosis; its rules are already folded into
   `callsites.json`, so it can simply be deleted.
7. `types.json`/`event-bus.json` include **every** declared PortLib type (946, nested included),
   not only reachable ones. That is deliberate (the brief asked for completeness of the reachable
   set and the wildcard set), but it means the `drop`/`shared` bulk rows dominate the counts.

---

## 9. How to extend

**Add or correct a rule.** Edit the curated table in
`tools\port2native\generators\gen-rules.ps1` and re-run it — never hand-edit the generated JSON,
because the next run overwrites it.

* New type rule: add `Set-Rule '<port FQN>' '<kind>' '<native FQN or "">' '<high|medium|low>' '<notes>' ['<copyTo>']`.
  Placement matters: the curated block runs before the mechanical defaults, so an explicit
  `Set-Rule` always wins. There is a second block (`$manualNested`, `$aliasFix`, `$holderWrappers`)
  specifically for cases where the automatic resolver picked a wrong or absent target.
* New callsite rule: add
  `Add-Call '<id>' '<regex pattern>' '<replacement>' '<safe|review>' @('<import FQN>', …) '<notes>' <true|false>`.
* New event: nothing to do — the event table is derived. Only add an override to `$evtTopOverride`
  or `$evtNestedOverride` when the native name/nesting genuinely differs (the file already lists
  the ~30 known cases).
* New Forge→NeoForge rule: add `F '<forge FQN>' '<kind>' '<native FQN or "">' '<high|medium|low>' '<notes>'`
  to the `$forgeExplicit` block. Everything **not** listed there is resolved automatically by the
  `$forgePrefixMap` namespace substitution and must exist in the native table to get `alias`;
  entries for types that live outside the scanned trees go into `$forgeExtraVerified` with their
  evidence. The import inventory itself is re-derived from the four `src` trees at every run, so a
  new `import net.minecraftforge...` line is picked up automatically and would surface as an
  `unknown` wildcard rule or an auto-resolved `alias`.

**If a new native target is not found.** The resolver searches
`generators\out\native-types-all.tsv`, which is produced from `_nfsrc_219`. If the type lives in
FML or the event bus, it will never be there — extract the matching source jar into
`notes\_tmp\fml-src` / `notes\_tmp\bus-src` (versions are pinned in
`neoforge-21.1.219-moddev-config.json`: `fancymodloader:loader:4.0.42`, `bus:8.0.5`) and re-run.

**Re-run order.**

```powershell
# 1. native type tables (event-bearing subtrees, then the whole tree)
powershell -NoProfile -ExecutionPolicy Bypass -File tools\port2native\generators\parse-native-events.ps1
powershell -NoProfile -ExecutionPolicy Bypass -File tools\port2native\generators\parse-native-events.ps1 -AllTrees
# 2. the five rule files + validation reports
powershell -NoProfile -ExecutionPolicy Bypass -File tools\port2native\generators\gen-rules.ps1
```

Step 2 prints the counts and writes `generators\out\validation-report.txt` (PortLib rules) and
`generators\out\forge-validation-report.txt` (Forge rules). **The build is only good if both
reports say 0 unverified** — they list any emitted `native` (from `types.json` or
`forge-to-neoforge.json`) or `typeImports` FQN (from `callsites.json`) that is absent from the
8 806-type table. (Execution policy is bypassed explicitly because script execution is disabled by
policy on this machine.)

**If a type genuinely moves in a later NeoForge version,** update
`$NativeAllTsv`/`_nfsrc_219` and re-run; the validation will flag every rule that broke, which is
the intended workflow — the rule set is meant to fail loudly rather than silently.

---

## 10. Forge → NeoForge rules (`forge-to-neoforge.json`)

Source that **bypasses PortLib and imports Forge directly** needs its own rule file. Measured with:

```powershell
$base='D:\Minecraft\1.20forge\confluence'
foreach($m in @('ConfluenceOtherworld','Confluence-Magic-Lib','TerraCurio','TerraFurniture')){
  $h = Get-ChildItem "$base\$m\src" -Recurse -Filter *.java -File |
       Select-String -Pattern '^import\s+(static\s+)?(net\.minecraftforge\.[\w.]+)'
  "$m occurrences=$($h.Count) unique=$(($h | ForEach-Object { $_.Matches[0].Groups[2].Value } | Sort-Object -Unique).Count)"
}
```

### 10.1 Counts

| Module | occurrences | distinct targets |
|---|---|---|
| `ConfluenceOtherworld` | 437 | 132 |
| `Confluence-Magic-Lib` | 80 | 51 |
| `TerraCurio` | 85 | 48 |
| `TerraFurniture` | 63 | 22 |
| **total** | **665** | **158** (union) |

The per-module numbers match the brief exactly; the **union is 158, not 199**. I could not
reproduce 199 under any variant I tried: 158 types + 5 wildcard packages = 163 import targets;
adding the 6 fully-qualified-only references (all already imported) stays at 158; the sum of
per-module distinct counts is 253; distinct packages + types is 214. If the 199 came from a
different revision of the 1.20 branch or a broader file set, the generator will pick the
difference up automatically on the next run — it re-derives the list from the four `src` trees
rather than hard-coding it, and it emits one rule per discovered target. **Coverage is complete
for the measured set: 158/158 targets, 665/665 import lines.**

Breakdown: 131 `alias`, 28 `manual`, 2 `drop`, 0 `unknown`; 87 `high` / 74 `medium` confidence.

### 10.2 The 28 `manual` + 2 `drop` entries (what a human must do)

`N x` is the number of import lines in the four modules.

| entries | decision |
|---|---|
| `registries.RegistryObject` (**53 x** — the single most frequent) | No native type. Field declarations change **arity**: `RegistryObject<X>` → `DeferredHolder<RegistryType, X>` (or `DeferredItem<X>` / `DeferredBlock<X>`). `.get()`/`.getId()`/`.getKey()` map 1:1 onto `DeferredHolder` (`:116,:162,:170`); `.isPresent()` → `isBound()` (`:197`) because `DeferredHolder` implements `Holder`+`Supplier` and neither declares `isPresent`; `.map(f)` → `.asOptional().map(f)`; passing one as an argument works where `Supplier<X>` or `Holder<RegistryType>` is expected, not where `RegistryObject<X>` is. `RegistryObject.create(...)` (3 uses) → `DeferredHolder.create(ResourceKey)` (`:62`) or `DeferredHolder.create(registryKey, valueName)` (`:41`). |
| `registries.ForgeRegistries` (**26 x**) | **Do not blanket-map to `NeoForgeRegistries`.** Verified usage is *entirely vanilla* registries: `Keys.BIOME_MODIFIERS` (7) and `Keys.FLUID_TYPES` (2) → `NeoForgeRegistries.Keys.*` (`:61,:53`); `ENTITY_TYPES` (7), `ITEMS` (7), `MOB_EFFECTS` (6), `BLOCKS` (4), `FLUIDS` (2), `SOUND_EVENTS` (1), `ATTRIBUTES` (1) → **`BuiltInRegistries`**, and five of those need a **singular** rename (`ENTITY_TYPE`, `ITEM`, `MOB_EFFECT`, `BLOCK`, `FLUID`, `SOUND_EVENT`, `ATTRIBUTE`). Only the 10 fields on `NeoForgeRegistries` (`:34`-`:43`) keep a NeoForge qualifier. |
| `eventbus.api.Cancelable` (12 x) | `@Cancelable` is gone — bus-8.0.5 has no `Cancelable`. Drop the annotation and declare `implements ICancellableEvent`. |
| `event.TickEvent` (8 x) | Genuine refactor: no native `TickEvent`, and `TickEvent.Phase` (15 uses) does not exist at all. `ClientTickEvent`→`client.event.ClientTickEvent`, `ServerTickEvent`→`event.tick.ServerTickEvent`, `LevelTickEvent`→`event.tick.LevelTickEvent`, `PlayerTickEvent`→`event.tick.PlayerTickEvent`, `RenderTickEvent`→`client.event.RenderFrameEvent`. The `if (event.phase != TickEvent.Phase.END) return;` guard is **deleted** and the handler moves to the `.Post` variant (`.Pre` for START); the mod already names its handlers `clientTick$Pre`/`clientTick$Post`, so the split is per-handler mechanical. |
| `common.ForgeMod` (8 x) | Renamed `NeoForgeMod`; 15 of Forge's attributes are **vanilla** `Attributes` in 1.21 and `CREATIVE_FLIGHT` is `NeoForgeMod.CREATIVE_FLIGHT` (`:212`). Check every member. |
| `event.ForgeEventFactory` (8 x) | Renamed `event.EventHooks`; 8 distinct methods used (`getMobGriefingEvent`, `onFinalizeSpawn`, `getExperienceDrop`, `onExplosionDetonate`, `onExplosionStart`, `onEntityStruckByLightning`, `onArrowLoose`, `onPlayerDestroyItem`) — verify each name/signature. |
| `network.NetworkHooks` (7 x) | Removed. `openScreen` → `ServerPlayer#openMenu`; `getEntitySpawningPacket` (6 x) → delete and implement `net.neoforged.neoforge.entity.IEntityWithComplexSpawn` (`:21,:29`, `RegistryFriendlyByteBuf`), NeoForge then ships `AdvancedAddEntityPayload`. |
| `common.ForgeHooks` (6 x) | Renamed `common.CommonHooks`; check each of the 6 members. |
| `common.ForgeConfigSpec` (4 x) + nested `Builder`/`BooleanValue`/`IntValue`/`EnumValue` | The **nested** types *are* clean aliases (→ `ModConfigSpec.*`, same names, `:300,:1284,:1303,:1352`), and the top-level type is effectively a rename to `ModConfigSpec` (`:56`). Kept `manual` because the other half is the **registration path**: `ModLoadingContext.get().registerConfig(...)` → `modContainer.registerConfig(...)` (fml-src `ModContainer.java:102,:119`). There is no `ModConfigSpec.ListValue`. |
| `common.ToolActions` (11 x) / `common.ToolAction` (10 x) | Renamed `ItemAbilities` / `ItemAbility` and **all constant names are identical** (`SHOVEL_FLATTEN` `:68`, `SWORD_SWEEP` `:80`, `AXE_STRIP` `:53`, `HOE_TILL` `:116`, `SHEARS_CARVE` `:99`, `DEFAULT_*_ACTIONS` `:152`-`:164`), so this is nearly a qualifier swap. `manual` because the **item-side hook moved**: `IForgeItem#getToolModifiedState(ToolAction)` no longer exists, so any handler reacting to a ToolAction needs re-authoring. Trap: `HOE_TILL` registers under the string `"till"` (`:116`). |
| `client.gui.overlay.VanillaGuiOverlay` (3 x) | The whole `client.gui.overlay` package is gone. Use `client.gui.VanillaGuiLayers` (`:17`), whose constants are `ResourceLocation`s — so **`.id()` is dropped**. `PLAYER_HEALTH`/`FOOD_LEVEL`/`ARMOR_LEVEL`/`CROSSHAIR`/`HOTBAR` keep their names (`:23,:25,:24,:19,:20`); `BOSS_EVENT_PROGRESS`→`BOSS_OVERLAY` (`:32`) and `SUBTITLES`→`SUBTITLE_OVERLAY` (`:41`) are **renamed**; `HELMET` (`TCModClientEvent.java:74`) has **no equivalent**. |
| `client.gui.overlay.ForgeGui` (4 x) | All four uses are `((ForgeGui) minecraft.gui).leftHeight` / `.rightHeight` (the `TerraStyle*Hud` classes). Those mutable layout cursors are gone — rewrite as explicit layer ordering (`RegisterGuiLayersEvent.registerAbove/registerBelow` with a `VanillaGuiLayers` id) and delete the stacking arithmetic. |
| `client.ConfigScreenHandler` (3 x) | `ConfigScreenFactory` (4 uses) → `client.gui.IConfigScreenFactory` (`:22`); `getScreenFactoryFor` → `IConfigScreenFactory.getForMod(IModInfo)` (`:28`). **Signature change**: `createScreen(ModContainer, Screen)` (`:26`) vs Forge's `(Minecraft, Screen)`. |
| `common.IForgeShearable` (3 x) | Vanilla absorbed it as `net.minecraft.world.entity.Shearable`; the method set differs, so re-author the implementation. |
| `common.MinecraftForge` (3 x) | → `common.NeoForge` (`:12`); `MinecraftForge.EVENT_BUS` → `NeoForge.EVENT_BUS` (`:17`) is mechanical. |
| `common.ForgeSpawnEggItem` (2 x) | → `common.DeferredSpawnEggItem`; the constructor takes deferred suppliers now. |
| `entity.IEntityAdditionalSpawnData` (2 x) | → `neoforge.entity.IEntityWithComplexSpawn` (**package moves into `net.neoforged.neoforge`**), and the methods take `RegistryFriendlyByteBuf`. |
| `common.util.LazyOptional` (1 x) | Removed; 1.21 uses nullability + `Optional` at API boundaries. `Lazy` itself survives. |
| `common.util.ForgeSoundType` (1 x) | → `common.util.DeferredSoundType`; constructor changed. |
| `common.capabilities.Capability` (1 x) | Forge capabilities are gone: `BlockCapability`/`ItemCapability`/`EntityCapability` + `RegisterCapabilitiesEvent` + `level.getCapability(...)`. |
| `common.capabilities.ForgeCapabilities` (1 x) | → `capabilities.Capabilities`, but field *shapes* changed (`Capability<IItemHandler>` → `ItemCapability`). |
| `common.extensions.IForgeAbstractMinecart` (1 x) | → `common.extensions.IAbstractMinecartExtension`; members renamed. |
| `common.extensions.IForgeMenuType` (1 x) | → `common.extensions.IMenuTypeExtension`; the static factory signature changed. |
| `registries.ForgeRegistry` (1 x) | `((ForgeRegistry<Block>) ForgeRegistries.BLOCKS).addAlias(from, to)` (`LibModEvents.java:64,69`) cannot be ported as a cast — **`Registry#addAlias` does not exist in 1.21.1** (aliases are 1.21.2+). Move alias registration to `DeferredRegister.addAlias` (`:303`). |
| `client.model.ForgeItemModelShaper` (1 x) | Removed; re-author against the 1.21 model-resolution path. |
| `common.extensions.IForgeItem` / `IForgeBlock` (0 x) | Included because the brief names them. Correct Forge FQNs are under `common.extensions.`; native twins are `IItemExtension`/`IBlockExtension`, which are no longer *implemented by* items/blocks — the members became default methods, so `((IForgeItem) item).getBurnTime(...)` collapses to `item.getBurnTime(...)`. |
| **`drop`**: `fml.javafmlmod.FMLJavaModLoadingContext` (9 x) | Not in loader 4.0.42 at all. Replace `FMLJavaModLoadingContext.get().getModEventBus()` with an `IEventBus` constructor parameter on the `@Mod` class. |
| **`drop`**: `network.PlayMessages` (1 x) | Removed; the custom spawn-packet path is `IEntityWithComplexSpawn` + `AdvancedAddEntityPayload`. |

### 10.3 The 74 `medium`-confidence `alias` entries

These are namespace substitutions that were confirmed to *exist*, but whose members were not
audited one by one. Audit these first if something behaves oddly after conversion:

`client.ChunkRenderTypeSet`, `client.DimensionSpecialEffectsManager`, `client.IArmPoseTransformer`,
`client.IItemDecorator`, `client.model.BakedModelWrapper`, `client.model.data.ModelData`,
`client.model.data.ModelProperty`, `client.settings.KeyConflictContext`,
`client.model.generators.{ModelFile,BlockStateProvider,ItemModelProvider,ItemModelBuilder,ConfiguredModel,loaders.SeparateTransformsModelBuilder}`,
the wildcards `client.event.*`, `client.model.generators.*`, `event.entity.living.*`,
`common.ForgeConfigSpec.*`, and
`common.{BasicItemListing,brewing.IBrewingRecipe,loot.LootTableIdCondition,world.BiomeModifier}`,
`event.{AddPackFindersEvent,BuildCreativeModeTabContentsEvent,RegisterCommandsEvent,ItemStackedOnOtherEvent}`,
`event.entity.{EntityAttributeCreationEvent,EntityAttributeModificationEvent,EntityJoinLevelEvent,EntityLeaveLevelEvent,EntityMountEvent}`,
`event.entity.item.{ItemEvent,ItemTossEvent}`,
`event.entity.living.{LivingBreatheEvent,LivingDeathEvent,LivingEvent,LivingFallEvent,MobSpawnEvent}`,
`event.entity.player.{ItemTooltipEvent,PlayerEvent,PlayerInteractEvent}`,
`event.level.{BlockEvent,ChunkEvent,ChunkWatchEvent,ExplosionEvent,LevelEvent}`,
`event.server.{ServerAboutToStartEvent,ServerStartedEvent,ServerStartingEvent,ServerStoppedEvent,ServerStoppingEvent}`,
`event.village.VillagerTradesEvent`,
`fluids.{FluidInteractionRegistry,FluidStack,FluidType}`, `fluids.capability.IFluidHandlerItem`,
`forgespi.language.IModFileInfo`, `forgespi.locating.IModFile`, `network.PacketDistributor`,
`registries.RegisterEvent`, `registries.holdersets.{AndHolderSet,AnyHolderSet,NotHolderSet,OrHolderSet}`.

The 87 `high`-confidence ones cover `fml.*`, `eventbus.api.*`, `common.data.*`,
`client.extensions.common.*`, `items.*`, `server.*`, `entity.*`, `data.event.*`, plus
`DeferredRegister`, `Tags`, `SoundActions`, `Lazy`, `MutableHashedLinkedMap`, `CraftingHelper` and
the explicit `manual`/rename entries above. Every `high`/`medium` target was checked to exist in
the 8 806-type native table, except `net.neoforged.api.distmarker.{Dist,OnlyIn}`, which live in
`mergetool-2.0.0-api.jar` (verified as zip entries) and are allowlisted for that reason.

Two `alias` entries are worth calling out because the target is **not** in `net.neoforged`:

* `resource.PathPackResources` → `net.minecraft.server.packs.PathPackResources` (Forge re-declared a vanilla class; in 1.21 it is vanilla).
* `common.crafting.conditions.ICondition` / `.ModLoadedCondition` / `.NotCondition` → `common.conditions.*` — the package **dropped the `crafting.` segment**, so a plain namespace substitution produces a non-existent package.

### 10.4 A defect found in `rules\local-extras.json` (not written by me)

`local-extras.json` **is not valid JSON** and will break the converter's "load every `*.json` in
the rules directory" step:

```
Invalid object passed in, ':' or '}' expected. (229)
```

Root cause: every string value that ends with a CJK full stop has **lost its closing `"`** — the
file looks like it was written UTF-8 but read/written back through a GBK code page (`。` = `E3 80
82`, and the following `"` = `22` gets absorbed into a mis-decoded pair). The first failure is at
character 229, in the leading `_note` value. Fix: re-save the file as UTF-8 with the missing
quotes restored (or delete it).

Its four rules are sound and were **folded into `callsites.json`** so nothing is lost if the file
is deleted — and I extended them:

| `local-extras.json` rule | now in `callsites.json` as | change |
|---|---|---|
| `local-iportitemstack-issameitemsamecomponents` | `port-itemstack-component-accessors` group + the `iportitemstack-issameitemsamecomponents` claim (verified: `ItemStack.isSameItemSameComponents` **is** `public static`, `ItemStack.java:632`) | verified, kept |
| `local-list-extension-getfirst` | `list-extension-getfirst` | added the **other five** members |
| `local-list-extension-getlast` | `list-extension-getlast` | — |
| *(missing)* | `list-extension-addfirst`, `list-extension-addlast`, `list-extension-removefirst`, `list-extension-removelast` | **new** — measured usage is `getFirst`/`getLast` **and** `addFirst`/`addLast`/`removeFirst`/`removeLast` (e.g. `GunEvent.java:131,135`) |
| `local-portextensions-import-strip` | `portextensions-import-strip` | kept, `review` |
| *(missing)* | `fluidstack-extension-issamefluidsamecomponents` | **new** — `FluidStack.isSameFluidSameComponents` is static at `FluidStack.java:349` |
| *(missing)* | `dataresult-extension-{getorthrow,ifsuccess,maporelse}` | **new** — all three are vanilla DFU `DataResult` members |

### 10.5 A real defect in my own `types.json`, found while checking the above

My first pass classified the whole `PortLib.extensions.**` package as `drop` (from the inventory's
"compiler-generated static-member helper" note). That silently discarded **239 live call sites**.
Measured usage: `PortCodecExtension` **105**, `PortDataResultExtension` **68**,
`PortListExtension` **54**, `PortDeferredRegisterExtension` **11**, `PortFluidStackExtension`
**1**; the other 13 files in the package have **0** uses. Corrected dispositions:

| class | kind | why |
|---|---|---|
| `PortListExtension` | `drop` | All six used members are Java 21 `List` methods → 6 callsite rules, nothing PortLib-specific survives. |
| `PortDataResultExtension` | `drop` | All three used members (`getOrThrow`, `ifSuccess`, `mapOrElse`) are vanilla DFU — verified in `datafixerupper-10.0.21-sources.jar`: `DataResult.java:80,:92,:90`. |
| `PortCodecExtension` | `shared` → `org.confluence.lib.codec` | 3 of 5 used members are vanilla DFU (`Codec.lazyInitialized` `:204`, `withAlternative` `:129/:143`, `lenientOptionalFieldOf` `:287/:291` — the last is a **default instance** method), but `object2BooleanMap` and `vector4f` have **no** DFU equivalent, so the class must be vendored. No callsite rules are emitted, to avoid a half-migrated class. |
| `PortDeferredRegisterExtension` | `manual` | Wraps the **Forge** `DeferredRegister` and returns a Forge `RegistryObject`; each of the 11 sites must be re-typed onto `DeferredRegister.register`. |
| `PortFluidStackExtension` | `drop` | `FluidStack.isSameFluidSameComponents` is static at `:349`. |
| the other 13 | `drop` | 0 uses; dropping needs no rewrite (the note now records the measured 0). |

Evidence note: `com/mojang/serialization` is **not** in `_nfsrc_219` (DFU is a library, not
decompiled there), so those members were verified from
`datafixerupper-10.0.21-sources.jar` — the DFU version Minecraft 1.21.1 resolves — extracted to
`notes\_tmp\dfu-src\`. That directory must stay in place for re-verification, like `fml-src` and
`bus-src`.
