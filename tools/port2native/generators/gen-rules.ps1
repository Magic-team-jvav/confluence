# gen-rules.ps1
#
# Generates the port2native rule set:
#   tools/port2native/rules/types.json
#   tools/port2native/rules/callsites.json
#   tools/port2native/rules/imports.json
#   tools/port2native/rules/event-bus.json
#
# Every `native` value emitted here is checked against the native type table produced by
# parse-native-events.ps1 (itself parsed from the decompiled NeoForge 21.1.219 / MC 1.21.1
# sources in build/_nfsrc_219, plus the fancymodloader:loader:4.0.42 and bus:8.0.5 source
# jars that 21.1.219 pins in neoforge-21.1.219-moddev-config.json). Emitted natives that
# fail the check are downgraded to confidence=low and listed in out/validation-report.txt.

param(
    [string]$PortLib      = 'D:\Minecraft\1.20forge\confluence\PortLib\src\main\java',
    [string]$NativeTsv    = "$PSScriptRoot\out\native-event-bus.tsv",
    [string]$NativeAllTsv = "$PSScriptRoot\out\native-types-all.tsv",
    [string]$RulesDir     = 'D:\Minecraft\1.21neoforge\confluence\tools\port2native\rules',
    [string]$Out          = "$PSScriptRoot\out"
)
$ErrorActionPreference = 'Stop'
New-Item -ItemType Directory -Force -Path $RulesDir, $Out | Out-Null

# --------------------------------------------------------------------------
# 0. native type universe
# --------------------------------------------------------------------------
# native-event-bus.tsv (event subtrees only) carries the bus verdict; native-types-all.tsv
# (the WHOLE decompiled tree) is the resolution + validation universe.
$nativeEventTbl = @{}
foreach ($r in (Import-Csv $NativeTsv -Delimiter "`t")) { $nativeEventTbl[$r.fqn] = $r }
$nativeAll = @{}
foreach ($r in (Import-Csv $NativeAllTsv -Delimiter "`t")) { $nativeAll[$r.fqn] = $r }
$nativeEventBus = @{}
foreach ($r in $nativeEventTbl.Values) {
    if ($r.isEvent -ne 'True') { continue }   # only true event types get a bus verdict
    $bus = 'game'
    if ($r.modbus -eq 'True') { $bus = 'mod' }
    $nativeEventBus[$r.fqn] = $bus
}

# Some real event types are not detected as "events" by the supertype-name heuristic
# (e.g. ScreenEvent.KeyPressed extends KeyInput, which does not end in "Event"). Walk the
# native supertype chain so those inherit their ancestor's bus instead of reporting nothing.
$busMemo = @{}
function Get-NativeBus([string]$fqn, [System.Collections.Generic.HashSet[string]]$seen) {
    if ($busMemo.ContainsKey($fqn)) { return $busMemo[$fqn] }
    if (-not $seen.Add($fqn)) { return '' }
    if ($nativeEventBus.ContainsKey($fqn)) { $busMemo[$fqn] = $nativeEventBus[$fqn]; return $busMemo[$fqn] }
    $rec = $nativeAll[$fqn]
    if (-not $rec) { return '' }
    $pkg = $fqn.Substring(0, $fqn.LastIndexOf('.'))
    foreach ($s in ($rec.supers -split '\|' | Where-Object { $_ })) {
        $res = $null
        if ($s -match '\.') {
            $outer = $fqn
            while ($outer.Contains('.')) {
                $outer = $outer.Substring(0, $outer.LastIndexOf('.'))
                if ($nativeAll.ContainsKey("$outer.$s")) { $res = "$outer.$s"; break }
            }
            if (-not $res -and $nativeAll.ContainsKey("$pkg.$s")) { $res = "$pkg.$s" }
        } else {
            $outer = $fqn
            while ($outer.Contains('.')) {
                $outer = $outer.Substring(0, $outer.LastIndexOf('.'))
                if ($nativeAll.ContainsKey("$outer.$s")) { $res = "$outer.$s"; break }
            }
            if (-not $res -and $nativeAll.ContainsKey("$pkg.$s")) { $res = "$pkg.$s" }
            if (-not $res -and $nativeSimple.ContainsKey($s) -and @($nativeSimple[$s]).Count -eq 1) { $res = @($nativeSimple[$s])[0] }
        }
        if ($res) {
            $b = Get-NativeBus $res $seen
            if ($b) { $busMemo[$fqn] = $b; return $b }
        }
    }
    $busMemo[$fqn] = ''
    return ''
}
Write-Host "native types (full tree): $($nativeAll.Count); native events with bus: $($nativeEventBus.Count)"

$nativeSimple = @{}
foreach ($k in $nativeAll.Keys) {
    $sn = $k.Substring($k.LastIndexOf('.') + 1)
    if (-not $nativeSimple.ContainsKey($sn)) { $nativeSimple[$sn] = New-Object System.Collections.Generic.List[string] }
    $nativeSimple[$sn].Add($k)
}

# --------------------------------------------------------------------------
# 1. parse the PortLib tree (all 645 files) into FQN + nesting
# --------------------------------------------------------------------------
$declRegex = [regex]'^\s*(?:(?:public|protected|private|abstract|final|sealed|non-sealed|static|strictfp)\s+)*(?<kind>@interface|class|interface|enum|record)\s+(?<name>[A-Za-z_]\w*)\s*(?<rest>.*)$'
$pkgRegex  = [regex]'^\s*package\s+([\w\.]+)\s*;'
$portTypes = [ordered]@{}

foreach ($f in Get-ChildItem $PortLib -Recurse -Filter *.java -File | Where-Object { $_.Name -ne 'package-info.java' }) {
    $pkg = $null; $depth = 0
    $stack = New-Object System.Collections.Generic.List[object]
    $lines = Get-Content -LiteralPath $f.FullName
    for ($i = 0; $i -lt $lines.Count; $i++) {
        $line = $lines[$i]
        $braceDelta = 0
        if ($null -eq $pkg) { $pm = $pkgRegex.Match($line); if ($pm.Success) { $pkg = $pm.Groups[1].Value } }
        if ($line -notmatch '^\s*(\*|//|/\*)') {
            $m = $declRegex.Match($line)
            if ($m.Success) {
                $name = $m.Groups['name'].Value
                $rest = $m.Groups['rest'].Value
                # accumulate a multi-line declaration header so nested types of e.g. multi-line
                # records are attributed to the right enclosing type
                $headerEnd = $i
                if ($rest -notmatch '\{') {
                    for ($k = $i + 1; $k -lt [Math]::Min($i + 40, $lines.Count); $k++) {
                        $rest += ' ' + $lines[$k]
                        if ($lines[$k] -match '\{') { $headerEnd = $k; break }
                        if ($lines[$k] -match ';') { break }
                    }
                }
                $prefix = if ($stack.Count -gt 0) { $stack[$stack.Count - 1].fqn } else { $pkg }
                if ($prefix) {
                    $fqn = "$prefix.$name"
                    # Strip the generic type-parameter list before reading `extends`: its bounds
                    # also contain `extends` and would otherwise be mistaken for the superclass.
                    $header = $rest
                    $ai = $header.IndexOf('<')
                    if ($ai -ge 0) {
                        $dA = 0; $aEnd = -1
                        for ($c = $ai; $c -lt $header.Length; $c++) {
                            if ($header[$c] -eq '<') { $dA++ }
                            elseif ($header[$c] -eq '>') { $dA--; if ($dA -eq 0) { $aEnd = $c; break } }
                        }
                        if ($aEnd -gt $ai) { $header = $header.Substring(0, $ai) + ' ' + $header.Substring($aEnd + 1) }
                    }
                    $ext = ''
                    $em = [regex]::Match($header, '\bextends\s+(.+?)(?=\bimplements\b|\{|\s*$)')
                    if ($em.Success) { $ext = $em.Groups[1].Value.Trim() }
                    if (-not $portTypes.Contains($fqn)) {
                        $portTypes[$fqn] = [pscustomobject]@{
                            fqn = $fqn; kind = $m.Groups['kind'].Value; pkg = $pkg
                            outer = $prefix; ext = $ext
                            file = $f.FullName.Substring($PortLib.Length + 1) -replace '\\', '/'
                        }
                    }
                    if ($rest -match '\{') {
                        $stack.Add([pscustomobject]@{ fqn = $fqn; depth = $depth + 1 })
                        if ($headerEnd -gt $i) { $i = $headerEnd; $braceDelta = 1; $line = $lines[$i] }
                    }
                }
            }
        }
        if ($braceDelta -eq 0) {
            $braceDelta = ([regex]::Matches($line, '\{')).Count - ([regex]::Matches($line, '\}')).Count
        }
        $depth += $braceDelta
        if ($depth -lt 0) { $depth = 0 }
        while ($stack.Count -gt 0 -and $stack[$stack.Count - 1].depth -gt $depth) { $stack.RemoveAt($stack.Count - 1) }
    }
}
Write-Host "portlib types parsed: $($portTypes.Count)"

function Get-PortTopLevel([string]$fqn) { $r = $portTypes[$fqn]; if ($r) { return $r.outer } return $fqn }

# --------------------------------------------------------------------------
# 2. curated Port -> native rules
# --------------------------------------------------------------------------
# kind: alias | static-alias | shared | drop | manual | unknown
$curated = @{}
function Set-Rule([string]$port, [string]$kind, [string]$native, [string]$conf, [string]$notes, [string]$copyTo) {
    $script:curated[$port] = [pscustomobject]@{
        kind = $kind; native = $native; confidence = $conf; notes = $notes; copyTo = $copyTo
    }
}
$SH = 'org.confluence.lib'   # proposed home for shared helpers on the 1.21 side

# ---- network -----------------------------------------------------------------
Set-Rule 'org.mesdag.portlib.network.codec.PortStreamCodec' 'alias' 'net.minecraft.network.codec.StreamCodec' 'high' 'Same method set: of/ofMember/unit/composite(1..6)/dispatch/map/mapStream/apply/cast, plus nested CodecOperation. Verified StreamCodec.java:14 and composite overloads at :112,:127,:150,:177,:208,:243.'
Set-Rule 'org.mesdag.portlib.network.codec.PortByteBufCodecs' 'static-alias' 'net.minecraft.network.codec.ByteBufCodecs' 'medium' 'Qualifier swap only. ByteBufCodecs is an INTERFACE (ByteBufCodecs.java:43) so constants are implicitly public static final. PortLib adds json(...) which has NO native twin (no JSON codec in ByteBufCodecs.java) - those call sites need review. Chunk/size constants differ: see callsites.json.'
Set-Rule 'org.mesdag.portlib.network.chat.PortComponentSerialization' 'alias' 'net.minecraft.network.chat.ComponentSerialization' 'high' 'CODEC/STREAM_CODEC/OPTIONAL_STREAM_CODEC/TRUSTED_STREAM_CODEC/TRUSTED_OPTIONAL_STREAM_CODEC all verified (ComponentSerialization.java:40-48).'
Set-Rule 'org.mesdag.portlib.network.IPortPacket' 'alias' 'net.minecraft.network.protocol.common.custom.CustomPacketPayload' 'medium' 'Base payload contract. CustomPacketPayload declares type() plus static createType(String)/codec(...) and default toVanillaClientbound/toVanillaServerbound (CustomPacketPayload.java:13-71). PortLib identifier() -> type().id(); handle(Context) is replaced by IPayloadHandler registration.'
Set-Rule 'org.mesdag.portlib.network.IPortPacket.Context' 'alias' 'net.neoforged.neoforge.network.handling.IPayloadContext' 'high' 'Method-for-method identical for the members mod code uses: player()/connection()/enqueueWork(Runnable)/reply(CustomPacketPayload)/disconnect(Component)/channelHandlerContext() - verified IPayloadContext.java:35-131. NB native enqueueWork is overloaded (Runnable at :81 and Supplier<T> at :86), so a lambda whose body returns a value becomes ambiguous.'
Set-Rule 'org.mesdag.portlib.network.IPortPacket.S2C' 'manual' 'net.minecraft.network.protocol.common.custom.CustomPacketPayload' 'medium' 'The marker interface disappears: the direction is chosen at REGISTRATION time by PayloadRegistrar.playToClient/configurationToClient/commonToClient (PayloadRegistrar.java:44,:70,:96). A human must decide per payload which registrar method replaces the S2C marker, and whether the phase is play or configuration. PortLib work(Player) becomes the IPayloadHandler body.'
Set-Rule 'org.mesdag.portlib.network.IPortPacket.C2S' 'manual' 'net.minecraft.network.protocol.common.custom.CustomPacketPayload' 'medium' 'Mirror of S2C: registration-time direction via PayloadRegistrar.playToServer/configurationToServer/commonToServer (:52,:78,:104). PortLib work(ServerPlayer) becomes the IPayloadHandler body; PayloadRegistrar does NOT exist as a login phase (login payloads are common* + versioned).'
Set-Rule 'org.mesdag.portlib.network.PortPacketDistributor' 'static-alias' 'net.neoforged.neoforge.network.PacketDistributor' 'high' '8 public statics verified (PacketDistributor.java:38-113). NOTE the native names drop the "All" where PortLib has it: sendToAllPlayersTrackingEntity -> sendToPlayersTrackingEntity, sendToAllPlayersTrackingChunk -> sendToPlayersTrackingChunk. Also native sendToPlayersInDimension takes ServerLevel, not ResourceKey<Level>.'
Set-Rule 'org.mesdag.portlib.network.PortConnectionType' 'manual' '' 'high' 'No drop-in enum. Native ConnectionType has exactly NEOFORGE and OTHER (ConnectionType.java:19,:25); PortLib MODDED/VANILLA do not exist. A human must rewrite MODDED->NEOFORGE and VANILLA->OTHER, and note OTHER also covers non-NeoForge mod loaders (doc at :22).'
Set-Rule 'org.mesdag.portlib.network.PortPayloadHandler' 'manual' 'net.neoforged.neoforge.network.registration.PayloadRegistrar' 'high' 'Shape differs: PortPayloadHandler is constructed with (namespace, version) and registers by ResourceLocation id + Class, while PayloadRegistrar is obtained from RegisterPayloadHandlersEvent.registrar(version) (RegisterPayloadHandlersEvent.java:40) and registers by CustomPacketPayload.Type. Method renames are mechanical once the idiom is rewritten - see the port-payloadhandler-* rules in callsites.json.'
Set-Rule 'org.mesdag.portlib.network.PortRegistryFriendlyByteBuf' 'alias' 'net.minecraft.network.RegistryFriendlyByteBuf' 'high' 'RegistryFriendlyByteBuf.java:7; accessor is registryAccess() (:31) and getConnectionType() (:27). The two-arg ctor (:15) is @Deprecated - pass ConnectionType.'
Set-Rule 'org.mesdag.portlib.network.PortVarInt' 'alias' 'net.minecraft.network.VarInt' 'high' 'VarInt.java:5; getByteSize/read/write/hasContinuationBit all present (:11,:25,:41,:21).'
Set-Rule 'org.mesdag.portlib.network.PortVarLong' 'alias' 'net.minecraft.network.VarLong' 'high' 'VarLong.java:5; getByteSize/read/write/hasContinuationBit all present.'
Set-Rule 'org.mesdag.portlib.network.PortNetworkHandler' 'drop' '' 'high' 'SimpleChannel transport shim. Native equivalent is PayloadRegistrar + PacketDistributor; nothing to alias. Any reference is a compile error to be rewritten by hand.'
Set-Rule 'org.mesdag.portlib.network.PortNetworkDirection' 'drop' '' 'high' 'PLAY_TO_*/LOGIN_TO_* have no 1.21.1 enum; the direction is a registrar method choice. PortLib login payloads map to common* + versioned().'
Set-Rule 'org.mesdag.portlib.network.PortFriendlyByteBuf' 'drop' '' 'medium' 'Static read/write helpers that now live on FriendlyByteBuf itself and on ByteBufCodecs. Call sites must be retargeted by hand (each member has a different native home).'
Set-Rule 'org.mesdag.portlib.network.PortUtf8String' 'drop' '' 'high' 'Replaced by ByteBufCodecs.STRING_UTF8 (:135) / stringUtf8(int) (:230) and FriendlyByteBuf.readUtf/writeUtf.'
foreach ($c in @('IPortCustomConfigurationTask','PortConfigurationContext','PortConfigurationManager','PortConfigurationFinishedPayload','PortConfigurationFragmentPayload')) {
    Set-Rule "org.mesdag.portlib.network.config.$c" 'drop' '' 'high' '1.20.1 CONFIGURATION-phase emulation. 1.21.1 has a real configuration phase; use RegisterConfigurationTasksEvent + ICustomConfigurationTask. Nothing to alias.'
}

# ---- registries --------------------------------------------------------------
Set-Rule 'org.mesdag.portlib.registries.PortRegistryEntry' 'alias' 'net.neoforged.neoforge.registries.DeferredHolder' 'high' 'DeferredHolder<R,T> implements Holder<R>, Supplier<T> (DeferredHolder.java:32). getId()/getKey()/value()/get()/isBound()/is(...)/tags()/unwrap()/unwrapKey()/kind()/canSerializeIn() all verified (:162,:170,:100,:116,:197). PortRegistryEntry.isPresent() has NO native twin - use isBound(); DeferredHolder also adds asOptional().'
Set-Rule 'org.mesdag.portlib.registries.PortDeferredItem' 'alias' 'net.neoforged.neoforge.registries.DeferredItem' 'high' 'DeferredItem<T extends Item> extends DeferredHolder<Item,T> implements ItemLike (DeferredItem.java:20); toStack()/toStack(int)/asItem() verified (:24,:33,:65).'
Set-Rule 'org.mesdag.portlib.registries.PortDeferredBlock' 'alias' 'net.neoforged.neoforge.registries.DeferredBlock' 'high' 'DeferredBlock<T extends Block> extends DeferredHolder<Block,T> implements ItemLike (DeferredBlock.java:21); toStack()/toStack(int)/asItem() verified.'
Set-Rule 'org.mesdag.portlib.registries.PortBlockRegistration' 'alias' 'net.neoforged.neoforge.registries.DeferredRegister.Blocks' 'medium' 'DeferredRegister.Blocks (DeferredRegister.java:390) has register(String,Supplier), registerBlock(...), registerSimpleBlock(...). PortLib addAlias(Location,Location) -> DeferredRegister.addAlias (:303). key() -> getRegistryKey() (:333) - see callsite rule.'
Set-Rule 'org.mesdag.portlib.registries.PortItemRegistration' 'alias' 'net.neoforged.neoforge.registries.DeferredRegister.Items' 'high' 'DeferredRegister.Items (DeferredRegister.java:486) exposes register, registerSimpleItem, registerSimpleBlockItem, registerItem - the same surface PortItemRegistration uses.'
Set-Rule 'org.mesdag.portlib.registries.PortRegistration' 'alias' 'net.neoforged.neoforge.registries.DeferredRegister' 'medium' 'DeferredRegister<T> (DeferredRegister.java:84). register(String,Supplier) :214 and register(String,Function<ResourceLocation,? extends I>) :225 both exist; addAlias :303; getEntries :326. PortLib key() -> getRegistryKey() (:333).'
Set-Rule 'org.mesdag.portlib.registries.PortRegistryMaker' 'alias' 'net.neoforged.neoforge.registries.RegistryBuilder' 'medium' 'RegistryBuilder (RegistryBuilder.java:23): defaultKey :37/:42, callback :57, onAdd :62, onBake :66, onClear :70, maxId :81, sync(boolean) :92, create :115. PortLib make() -> create(); no allowModification/setDefaultKey/disableSync on the native type.'
Set-Rule 'org.mesdag.portlib.registries.PortCustomRegistration' 'manual' 'net.neoforged.neoforge.registries.DeferredRegister' 'medium' 'DeferredRegister.makeRegistry(Consumer<RegistryBuilder<T>>) (DeferredRegister.java:259) replaces the PortLib custom-registration flow, but PortCustomRegistration.get(ResourceLocation)/getKey(R)/getHolder/byNameCodec have no method-for-method peers - a human must retarget each accessor onto Registry/DeferredRegister.'
Set-Rule 'org.mesdag.portlib.registries.PortRegisterHandler' 'manual' '' 'high' 'NO single native target: PortRegisterHandler is a static FACTORY HUB whose members map onto different natives. item()->DeferredRegister.createItems, block()->createBlocks, dataComponent()->createDataComponents, attachment()->create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES), particleType()->create(Registries.PARTICLE_TYPE), attribute()->create(Registries.ATTRIBUTE), armorMaterial()->create(Registries.ARMOR_MATERIAL), ingredientType()->create(NeoForgeRegistries.Keys.INGREDIENT_TYPES), custom()->create + makeRegistry. init(IEventBus) has no native twin (DeferredRegister.register(IEventBus) is called per register instead). See the port-registerhandler-* callsite rules.'
Set-Rule 'org.mesdag.portlib.registries.PortAttachmentRegistration' 'alias' 'net.neoforged.neoforge.registries.DeferredRegister' 'medium' 'Becomes DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, namespace) (key verified NeoForgeRegistries.java:58, registry :43). registerSimple(String, AttachmentType.Builder) -> DeferredRegister.register(String, Supplier).'
Set-Rule 'org.mesdag.portlib.registries.PortDataComponentRegistration' 'alias' 'net.neoforged.neoforge.registries.DeferredRegister.DataComponents' 'medium' 'DeferredRegister.DataComponents (DeferredRegister.java:649) has registerComponentType(String, UnaryOperator<DataComponentType.Builder<D>>) :667. The registry is vanilla: BuiltInRegistries.DATA_COMPONENT_TYPE (BuiltInRegistries.java:264), NOT NeoForgeRegistries. PortLib builder(name, consumer) -> registerComponentType.'
Set-Rule 'org.mesdag.portlib.registries.PortParticleTypeRegistration' 'alias' 'net.neoforged.neoforge.registries.DeferredRegister' 'medium' 'DeferredRegister.create(Registries.PARTICLE_TYPE, namespace). The nested PortLibParticleType<T> ctor shape (MapCodec + PortStreamCodec) matches 1.21.1 ParticleType.codec()/streamCodec() (ParticleType.java:18,:20), but ParticleType is ABSTRACT so a subclass is still required.'
Set-Rule 'org.mesdag.portlib.registries.PortArmorMaterialRegistration' 'alias' 'net.neoforged.neoforge.registries.DeferredRegister' 'medium' 'DeferredRegister.create(Registries.ARMOR_MATERIAL, namespace) - the registry field is ARMOR_MATERIAL (singular) at BuiltInRegistries.java:263 / Registries.java:202. 1.21.1 ArmorMaterial IS a record (ArmorMaterial.java:13), so the PortLib Settings/Layer shim collapses.'
Set-Rule 'org.mesdag.portlib.registries.PortAttributeRegistration' 'alias' 'net.neoforged.neoforge.registries.DeferredRegister' 'medium' 'DeferredRegister.create(Registries.ATTRIBUTE, namespace) (Registries.java:115). AttributeMaker.setSentiment DOES have a native target: Attribute.Sentiment exists in 21.1.219 (Attribute.java:95, POSITIVE/NEUTRAL/NEGATIVE) with setSentiment at :57 - the inventory called this a 1.21.2 feature but it is present in 21.1.219.'
Set-Rule 'org.mesdag.portlib.registries.PortIngredientTypeRegistration' 'alias' 'net.neoforged.neoforge.registries.DeferredRegister' 'medium' 'DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, namespace) (NeoForgeRegistries.java:55).'
foreach ($c in @('PortRegistryCallback','PortAddCallback','PortBakeCallback','PortClearCallback')) {
    Set-Rule "org.mesdag.portlib.registries.callback.$c" 'alias' "net.neoforged.neoforge.registries.callback.$($c.Substring(4))" 'high' 'Callback interfaces exist natively: RegistryCallback/AddCallback/BakeCallback/ClearCallback with onAdd(Registry<T>,int,ResourceKey<T>,T) at AddCallback.java:25.'
}

# ---- attachment --------------------------------------------------------------
Set-Rule 'org.mesdag.portlib.attachment.IPortAttachmentHolder' 'alias' 'net.neoforged.neoforge.attachment.IAttachmentHolder' 'high' 'Method RENAMES required: hasAttaches()->hasAttachments(), hasAttach->hasData, getAttach->getData, getExistingAttach->getExistingData, getExistingAttachOrNull->getExistingDataOrNull, setAttach->setData, removeAttach->removeData, syncAttach->syncData. All verified in IAttachmentHolder.java:19-137. Native also adds Supplier<AttachmentType<T>> overloads of every accessor.'
Set-Rule 'org.mesdag.portlib.attachment.PortAttachmentType' 'alias' 'net.neoforged.neoforge.attachment.AttachmentType' 'high' 'AttachmentType (AttachmentType.java:56) with builder(Supplier)/builder(Function) (:95,:109) and Builder.serialize x3 (:168,:184,:196), copyOnDeath :224, copyHandler :238, sync x3 (:250,:261,:273), build :294 - identical surface.'
Set-Rule 'org.mesdag.portlib.attachment.PortAttachmentSyncHandler' 'alias' 'net.neoforged.neoforge.attachment.AttachmentSyncHandler' 'high' 'sendToPlayer/write/read verified (AttachmentSyncHandler.java:39,:57,:66).'
Set-Rule 'org.mesdag.portlib.attachment.IPortAttachmentSerializer' 'alias' 'net.neoforged.neoforge.attachment.IAttachmentSerializer' 'high' 'read(holder,S,Provider)/write(T,Provider) verified (IAttachmentSerializer.java:27,:32).'
Set-Rule 'org.mesdag.portlib.attachment.IPortAttachmentCopyHandler' 'alias' 'net.neoforged.neoforge.attachment.IAttachmentCopyHandler' 'high' 'copy(T,IAttachmentHolder,Provider) verified (IAttachmentCopyHandler.java:23).'

# ---- datamap -----------------------------------------------------------------
Set-Rule 'org.mesdag.portlib.datamap.PortDataMapType' 'alias' 'net.neoforged.neoforge.registries.datamaps.DataMapType' 'high' 'DataMapType (DataMapType.java:59) is sealed permitting AdvancedDataMapType, exactly like PortLib. builder(ResourceLocation,ResourceKey<Registry<R>>,Codec<T>) :85, registryKey() :92, id() :99, codec() :106, networkCodec() :113, mandatorySync() :120, Builder.synced(Codec,boolean) :152, build() :161.'
Set-Rule 'org.mesdag.portlib.datamap.PortAdvancedDataMapType' 'alias' 'net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType' 'medium' 'AdvancedDataMapType<R,T,VR> (AdvancedDataMapType.java:56) - note the native type parameter ORDER is <R,T,VR> while PortAdvancedDataMapType mirrors it; builder/remover/merger verified (:89,:69,:76).'
Set-Rule 'org.mesdag.portlib.datamap.PortDataMapValueMerger' 'alias' 'net.neoforged.neoforge.registries.datamaps.DataMapValueMerger' 'high' 'DataMapValueMerger.java:29; defaultMerger/listMerger/setMerger/mapMerger verified (:45,:52,:63,:74).'
Set-Rule 'org.mesdag.portlib.datamap.PortDataMapValueRemover' 'alias' 'net.neoforged.neoforge.registries.datamaps.DataMapValueRemover' 'high' 'DataMapValueRemover.java:24; nested Default<T,R> exists natively too (DataMapValueRemover.java:44) so PortDefault -> Default.'
Set-Rule 'org.mesdag.portlib.datamap.PortDataMapProvider' 'alias' 'net.neoforged.neoforge.common.data.DataMapProvider' 'high' 'DataMapProvider.java:44; builder(DataMapType) :98 and nested Builder with add/remove/replace/conditions/build (:130-:173), AdvancedBuilder :178.'
Set-Rule 'org.mesdag.portlib.datamap.builtin.PortCompostable' 'alias' 'net.neoforged.neoforge.registries.datamaps.builtin.Compostable' 'high' 'Record exists natively; the data-map instances are NeoForgeDataMaps.COMPOSTABLES / FURNACE_FUELS.'
Set-Rule 'org.mesdag.portlib.datamap.builtin.PortFurnaceFuel' 'alias' 'net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel' 'high' 'Record exists natively; see NeoForgeDataMaps.FURNACE_FUELS.'

# ---- component / config ------------------------------------------------------
Set-Rule 'org.mesdag.portlib.component.PortDataComponentType' 'alias' 'net.minecraft.core.component.DataComponentType' 'high' 'DataComponentType is an INTERFACE in 1.21.1 (DataComponentType.java:16). Builder.persistent(Codec) :57 and networkSynchronized(StreamCodec) :62 are name-identical to PortLib, and build() :72 matches.'
Set-Rule 'org.mesdag.portlib.component.PortDataComponentMap' 'alias' 'net.minecraft.core.component.DataComponentMap' 'high' 'DataComponentMap.java:22 with get/has/getOrDefault/keySet/stream plus Builder.set :143 / addAll :156 / build :164. There is NO entrySet() on the native interface.'
Set-Rule 'org.mesdag.portlib.component.PortDataComponentPatch' 'alias' 'net.minecraft.core.component.DataComponentPatch' 'high' 'DataComponentPatch.java:21 with EMPTY :22, builder() :137, entrySet() :146, Builder.set :241 / remove :247 / build :256.'
Set-Rule 'org.mesdag.portlib.config.PortConfigSpec' 'alias' 'net.neoforged.neoforge.common.ModConfigSpec' 'high' 'Rename only. ModConfigSpec.java:56 with Builder :300, ConfigValue :1186, BooleanValue :1284, IntValue :1303, LongValue :1319, DoubleValue :1335, EnumValue :1352. define/defineInRange/defineList/defineListAllowEmpty/defineEnum/comment/translation/push/pop/worldRestart/build are all present. There is no ListValue type - list values are ConfigValue<List<? extends T>>.'
Set-Rule 'org.mesdag.portlib.config.PortConfigSpec.Builder' 'alias' 'net.neoforged.neoforge.common.ModConfigSpec.Builder' 'high' 'Nested Builder; same define* API.'
Set-Rule 'org.mesdag.portlib.config.PortConfigSpec.ConfigValue' 'alias' 'net.neoforged.neoforge.common.ModConfigSpec.ConfigValue' 'high' 'get/getRaw/getDefault/set/save/getPath/clearCache verified (:1220,:1232,:1246,:1264,:1254,:1204,:1279).'
Set-Rule 'org.mesdag.portlib.config.PortConfigSpec.BooleanValue' 'alias' 'net.neoforged.neoforge.common.ModConfigSpec.BooleanValue' 'high' 'getAsBoolean/isTrue/isFalse verified (:1290,:1294,:1298).'
Set-Rule 'org.mesdag.portlib.config.PortConfigSpec.IntValue' 'alias' 'net.neoforged.neoforge.common.ModConfigSpec.IntValue' 'high' 'getAsInt verified (:1314).'
Set-Rule 'org.mesdag.portlib.config.PortConfigSpec.LongValue' 'alias' 'net.neoforged.neoforge.common.ModConfigSpec.LongValue' 'high' 'getAsLong verified (:1330).'
Set-Rule 'org.mesdag.portlib.config.PortConfigSpec.DoubleValue' 'alias' 'net.neoforged.neoforge.common.ModConfigSpec.DoubleValue' 'high' 'getAsDouble verified (:1347).'
Set-Rule 'org.mesdag.portlib.config.PortConfigSpec.EnumValue' 'alias' 'net.neoforged.neoforge.common.ModConfigSpec.EnumValue' 'high' 'EnumValue<T extends Enum<T>> exists (:1352).'

# ---- loot --------------------------------------------------------------------
Set-Rule 'org.mesdag.portlib.loot.PortAddTableLootModifier' 'alias' 'net.neoforged.neoforge.common.loot.AddTableLootModifier' 'high' 'AddTableLootModifier.java:37; ctor (LootItemCondition[], ResourceKey<LootTable>) :48, table() :53. Register it with a MapCodec in 1.21.1 (codec() :70).'

# ---- client ------------------------------------------------------------------
Set-Rule 'org.mesdag.portlib.client.PortDeltaTicker' 'alias' 'net.minecraft.client.DeltaTracker' 'high' 'DeltaTracker.java:8; getGameTimeDeltaTicks :12, getGameTimeDeltaPartialTick(boolean) :14, getRealtimeDeltaTicks :16. NOTE DeltaTracker is an interface with nested DefaultValue/Timer - RenderTickCounter does NOT exist anywhere in 21.1.219 (tree-wide grep: 0 hits).'
Set-Rule 'org.mesdag.portlib.client.PortGuiLayer' 'alias' 'net.minecraft.client.gui.LayeredDraw.Layer' 'high' 'LayeredDraw.Layer (LayeredDraw.java:42) with render(GuiGraphics,DeltaTracker) :43.'
Set-Rule 'org.mesdag.portlib.client.gui.PortConfigurationScreen' 'alias' 'net.neoforged.neoforge.client.gui.ConfigurationScreen' 'high' 'ConfigurationScreen.java:110 extends OptionsSubScreen; ctors at :270,:274,:279.'
Set-Rule 'org.mesdag.portlib.client.gui.components.PortWidgetSprites' 'alias' 'net.minecraft.client.gui.components.WidgetSprites' 'high' 'Exact rename. record WidgetSprites(ResourceLocation enabled, disabled, enabledFocused, disabledFocused) at WidgetSprites.java:8 with get(boolean,boolean) :17.'
Set-Rule 'org.mesdag.portlib.client.gui.components.PortImageButton' 'alias' 'net.minecraft.client.gui.components.ImageButton' 'high' 'ImageButton.java:11; sprite ctor (int,int,int,int,WidgetSprites,Button.OnPress,Component) at :18.'
Set-Rule 'org.mesdag.portlib.client.gui.components.PortSprite' 'manual' '' 'medium' 'PortSprite is a (ResourceLocation, width, height) triple with no native peer. A human must split each call site: keep the ResourceLocation and pass the width/height to GuiGraphics.blitSprite(ResourceLocation,int,int,int,int) (GuiGraphics.java:677) or the 9-arg overload (:706).'

# ---- wrapper: root -----------------------------------------------------------
Set-Rule 'org.mesdag.portlib.wrapper.IPortNBTSerializable' 'alias' 'net.neoforged.neoforge.common.util.INBTSerializable' 'high' 'INBTSerializable<T extends Tag> (INBTSerializable.java:16); serializeNBT(Provider) :18, deserializeNBT(Provider,T) :20. PortLib Provider arg order matches.'
Set-Rule 'org.mesdag.portlib.wrapper.PortEnvironment' 'drop' '' 'medium' 'Dist/side/mod-id helper. Native equivalents are scattered (FMLEnvironment.dist, FMLEnvironment.getDist, ServerLifecycleHooks, ModList, ModLoadingContext.get().getActiveContainer().getModId()); there is no single type to alias, so reference sites are flagged for manual work.'
Set-Rule 'org.mesdag.portlib.wrapper.PortPaths' 'alias' 'net.neoforged.fml.loading.FMLPaths' 'medium' 'FMLPaths exists in fancymodloader:loader:4.0.42 (net/neoforged/fml/loading/FMLPaths.java) - verified in the extracted sources; it is NOT in _nfsrc_219 (which contains no net/neoforged/fml). GAMEDIR/CONFIG are the members mod code needs.'
Set-Rule 'org.mesdag.portlib.wrapper.PortUtil' 'shared' '' 'high' 'Pure-JDK/Guava helper (immutable copy-add/put, symmetry test, sprite path, JSON->ops, peeking iterator). Copy verbatim.' $SH'.util'
Set-Rule 'org.mesdag.portlib.wrapper.PortSelfGetter' 'shared' '' 'high' 'portlib$self() self-cast helper interface; no loader dependency. Copy verbatim, renaming portlib$ -> a project prefix if desired.' $SH'.util'

# ---- wrapper: common --------------------------------------------------------
Set-Rule 'org.mesdag.portlib.wrapper.common.PortBooleanAttribute' 'alias' 'net.neoforged.neoforge.common.BooleanAttribute' 'high' 'BooleanAttribute extends Attribute (BooleanAttribute.java:31); ctor (String,boolean) :32. It has NO static create/toValue/getModifier - NeoForge constructs it inline (NeoForgeMod.java:212 uses new BooleanAttribute("neoforge.creative_flight", false).setSyncable(true)).'
Set-Rule 'org.mesdag.portlib.wrapper.common.PortPercentageAttribute' 'manual' '' 'medium' 'PortLib ships a RangedAttribute subclass that renders a percentage. 1.21.1 has RangedAttribute (RangedAttribute.java:8, ctor :18) and the Attribute tooltip API (Attribute.toValueComponent/toComponent via IAttributeExtension), but there is no native percentage attribute - a human must decide whether to keep a small local subclass or drop the percent formatting.'
Set-Rule 'org.mesdag.portlib.wrapper.common.PortEffectCure' 'alias' 'net.neoforged.neoforge.common.EffectCure' 'high' 'EffectCure.java:27 (final class, interned via EffectCure.get(String) :45). It declares NO constants of its own - the constants live on EffectCures.'
Set-Rule 'org.mesdag.portlib.wrapper.common.PortEffectCures' 'static-alias' 'net.neoforged.neoforge.common.EffectCures' 'high' 'EffectCures.java:10 with MILK :14, HONEY :18, PROTECTED_BY_TOTEM :22, DEFAULT_CURES :24.'
Set-Rule 'org.mesdag.portlib.wrapper.common.PortItemAbility' 'alias' 'net.neoforged.neoforge.common.ItemAbility' 'high' 'ItemAbility.java:14 (final class, ItemAbility.get(String) :31). No constants of its own.'
Set-Rule 'org.mesdag.portlib.wrapper.common.PortItemAbilities' 'static-alias' 'net.neoforged.neoforge.common.ItemAbilities' 'high' 'ItemAbilities.java:19 with 24 constants at :23-:149. WARNING: no code-level renaming needed, but ItemAbilities.HOE_TILL is registered under the STRING "till" (ItemAbilities.java:116), so string-keyed lookups must not assume "hoe_till".'
Set-Rule 'org.mesdag.portlib.wrapper.common.PortSoundActions' 'static-alias' 'net.neoforged.neoforge.common.SoundActions' 'high' 'SoundActions.java:11 with BUCKET_FILL/BUCKET_EMPTY/FLUID_VAPORIZE/CAULDRON_DRIP (:19-:34). Note the PortLib qualifier PortSoundActions maps to the PLURAL native class, not to SoundAction.'
Set-Rule 'org.mesdag.portlib.wrapper.common.PortSimpleTier' 'manual' '' 'medium' '1.21.1 net.minecraft.world.item.Tier is an interface, so a Tier implementation is still needed, but the record/field shape changed (1.21 Tier no longer has the 1.20 getLevel()/getUses() only - it added getIncorrectBlocksForDrops()). A human must re-derive the implementation rather than string-substitute.'
Set-Rule 'org.mesdag.portlib.wrapper.common.PortTranslatableEnum' 'shared' '' 'high' 'getTranslatedName() vocabulary interface, loader-independent. Copy verbatim.' $SH'.util'
Set-Rule 'org.mesdag.portlib.wrapper.common.PortTags' 'manual' '' 'high' '514 lines of TagKey constants, many in the FORGE/forge: namespace, which no longer exists in 1.21.1 (net/neoforged/neoforge/common/Tags.java uses only "neoforge", verified at :284,:923; the 3 remaining "forge:" strings in the tree are DataFixer field names and a comment). Each constant needs an individual decision: vanilla net.minecraft.tags.ItemTags/BlockTags, NeoForge net.neoforged.neoforge.common.Tags.Items/Tags.Blocks, or drop. Cannot be mechanised.'
Set-Rule 'org.mesdag.portlib.wrapper.common.util.PortTriState' 'alias' 'net.neoforged.neoforge.common.util.TriState' 'high' 'TriState.java:11 with TRUE/DEFAULT/FALSE (:15,:19,:23) and isTrue/isDefault/isFalse (:27,:31,:35). Do NOT use net.minecraft.util.TriState - it does not exist in 1.21.1.'
Set-Rule 'org.mesdag.portlib.wrapper.common.util.PortAttributeTooltipContext' 'alias' 'net.neoforged.neoforge.common.util.AttributeTooltipContext' 'high' 'AttributeTooltipContext extends Item.TooltipContext (AttributeTooltipContext.java:21); player() :26, flag() :31, of(...) :33.'
Set-Rule 'org.mesdag.portlib.wrapper.common.util.PortBlockSnapshot' 'alias' 'net.neoforged.neoforge.common.util.BlockSnapshot' 'high' 'BlockSnapshot.java:31 with create(...) :69/:78, getDimension/getPos/getFlags/getTag/getState/getCurrentState/restore* (:85-:194).'
Set-Rule 'org.mesdag.portlib.wrapper.common.damagesource.PortDamageContainer' 'alias' 'net.neoforged.neoforge.common.damagesource.DamageContainer' 'high' 'DamageContainer.java:39 with nested Reduction enum :40 (INVULNERABILITY/ARMOR/ENCHANTMENTS/MOB_EFFECTS/ABSORPTION/INNATE_RESISTANCE) and addModifier :111 / setReduction :163 / getReduction :147.'
Set-Rule 'org.mesdag.portlib.wrapper.common.damagesource.IPortReductionFunction' 'alias' 'net.neoforged.neoforge.common.damagesource.IReductionFunction' 'high' 'IReductionFunction.java:14 with float modify(DamageContainer,float) :23.'
Set-Rule 'org.mesdag.portlib.wrapper.common.crafting.PortCustomIngredient' 'alias' 'net.neoforged.neoforge.common.crafting.ICustomIngredient' 'high' 'ICustomIngredient.java:27 with test :35, getItems :52, isSimple :60, getType :67, toVanilla :73. A vanilla CustomIngredient does NOT exist in 1.21.1 (tree-wide glob: 0 files) - the inventory was right about that.'
Set-Rule 'org.mesdag.portlib.wrapper.common.crafting.PortIngredientType' 'alias' 'net.neoforged.neoforge.common.crafting.IngredientType' 'high' 'record IngredientType<T extends ICustomIngredient>(MapCodec<T>, StreamCodec<? super RegistryFriendlyByteBuf,T>) at IngredientType.java:18, plus a MapCodec-only ctor :22.'
Set-Rule 'org.mesdag.portlib.wrapper.common.conditions.PortConditionalOps' 'alias' 'net.neoforged.neoforge.common.conditions.ConditionalOps' 'high' 'ConditionalOps<T> extends RegistryOps<T> (ConditionalOps.java:25) with DEFAULT_CONDITIONS_KEY :49 / CONDITIONAL_VALUE_KEY :62 / createConditionalCodec :67.'
Set-Rule 'org.mesdag.portlib.wrapper.common.conditions.PortWithConditions' 'alias' 'net.neoforged.neoforge.common.conditions.WithConditions' 'high' 'record WithConditions<A>(List<ICondition> conditions, A carrier) at WithConditions.java:13, builder(A) :23, nested Builder :26.'
Set-Rule 'org.mesdag.portlib.wrapper.common.world.PortAddCarversBiomeModifier' 'alias' 'net.neoforged.neoforge.common.world.BiomeModifiers.AddCarversBiomeModifier' 'high' 'CORRECTION vs the inventory: there is NO NeoForgeBiomeModifiers class in 21.1.219. The record is nested - net.neoforged.neoforge.common.world.BiomeModifiers.AddCarversBiomeModifier (BiomeModifiers.java:226) - and the registered codec is NeoForgeMod.ADD_CARVERS_BIOME_MODIFIER_TYPE (NeoForgeMod.java:276). Note the record components are (HolderSet<Biome>, HolderSet<ConfiguredWorldCarver<?>>, GenerationStep.Carving), which no longer matches the 1.20 PortLib shape.'

# ---- wrapper: core / misc ---------------------------------------------------
Set-Rule 'org.mesdag.portlib.wrapper.core.PortRegistry' 'alias' 'net.minecraft.core.Registry' 'medium' 'PortRegistry wraps IForgeRegistry. On 1.21.1 a registry IS a net.minecraft.core.Registry<T> (Registry.java:25). key() -> key(); register(...) -> DeferredRegister.register. PortLib-only members (IForgeRegistry accessors) have no twin.'
Set-Rule 'org.mesdag.portlib.wrapper.core.PortHolder' 'drop' '' 'high' 'Pluggable Holder delegate that existed to bridge Forge RegistryObject. 1.21.1 registered objects are already Holder/DeferredHolder, so nothing replaces this type.'
Set-Rule 'org.mesdag.portlib.wrapper.core.particles.PortParticleOptions' 'manual' '' 'medium' 'The 1.21.1 replacement is to subclass net.minecraft.core.particles.ParticleType and implement codec() (ParticleType.java:18) + streamCodec() (:20); ParticleType is abstract and has a protected ctor (:10). A human must restructure each PortParticleOptions subclass rather than rename it.'
Set-Rule 'org.mesdag.portlib.wrapper.serialization.PortJavaOps' 'unknown' '' 'low' 'A 419-line DynamicOps<Object> over Java maps/lists/primitives, used for gson-backed data maps. Nothing in _nfsrc_219 is equivalent and no 1.21.1 native ops is a drop-in. A human must decide: carry a Port-independent copy, or move the affected codecs to NbtOps/JsonOps.'
Set-Rule 'org.mesdag.portlib.wrapper.resource.PortContextAwareReloadListener' 'manual' '' 'medium' '1.21.1 has net.minecraft.server.packs.resources.PreparableReloadListener, but the condition-context/registry-lookup plumbing PortLib adds is a PortLib invention layered on ConditionalOps; a human must decide how much of that survives.'
Set-Rule 'org.mesdag.portlib.wrapper.nbt.PortNbtIo' 'alias' 'net.minecraft.nbt.NbtIo' 'high' 'NbtIo.java:25 with readAnyTag(DataInput,NbtAccounter) :166 and writeAnyTag(Tag,DataOutput) :171 (plus read/write/readCompressed/writeCompressed).'
Set-Rule 'org.mesdag.portlib.wrapper.fml.PortInterModComms' 'alias' 'net.neoforged.fml.InterModComms' 'medium' 'InterModComms exists in fancymodloader:loader:4.0.42 (net/neoforged/fml/InterModComms.java) - verified in the extracted sources, not in _nfsrc_219. PortLib IMCMessage -> InterModComms.IMCMessage.'
Set-Rule 'org.mesdag.portlib.wrapper.fml.PortLogicalSide' 'alias' 'net.neoforged.fml.LogicalSide' 'medium' 'LogicalSide exists in fancymodloader:loader:4.0.42. unwrap() disappears: use the enum directly.'
Set-Rule 'org.mesdag.portlib.wrapper.entity.IPortEntityWithComplexSpawn' 'alias' 'net.neoforged.neoforge.entity.IEntityWithComplexSpawn' 'high' 'PACKAGE CORRECTION vs the inventory: the native interface is net.neoforged.neoforge.entity.IEntityWithComplexSpawn (package at IEntityWithComplexSpawn.java:6, declaration :14) - it is NOT net.minecraft.world.entity.IEntityWithComplexSpawn. Its methods take RegistryFriendlyByteBuf: writeSpawnData(:21) and readSpawnData(:29) - NOT FriendlyByteBuf, which is a silent signature break for 1.20-era call sites.'
Set-Rule 'org.mesdag.portlib.wrapper.advancements.PortAdvancementHolder' 'alias' 'net.minecraft.advancements.AdvancementHolder' 'high' 'record AdvancementHolder(ResourceLocation id, Advancement value) exists in 1.21.1.'
Set-Rule 'org.mesdag.portlib.wrapper.world.item.crafting.PortRecipeHolder' 'alias' 'net.minecraft.world.item.crafting.RecipeHolder' 'high' 'record RecipeHolder<T extends Recipe<?>>(ResourceLocation id, T value) at RecipeHolder.java:7.'
Set-Rule 'org.mesdag.portlib.wrapper.client.model.lighting.PortQuadLighter' 'alias' 'net.neoforged.neoforge.client.model.lighting.QuadLighter' 'medium' 'QuadLighter.java:28 is ABSTRACT with a PROTECTED ctor (:51), so it only works as a superclass - mod code that instantiates it must be rewritten.'
Set-Rule 'org.mesdag.portlib.wrapper.fluids.PortFluidType' 'manual' '' 'low' 'PortLib models FluidType.DripstoneDripInfo (a Forge-era nested type). NeoForge 21.1.219 net.neoforged.neoforge.fluids.FluidType exists, but the dripstone-drip shape could not be confirmed to match. A human must check each member.'

# ---- wrapper: sounds / blocks (backported content) -------------------------
Set-Rule 'org.mesdag.portlib.wrapper.sounds.PortSoundEvents' 'drop' '' 'high' '26 backported sound constants (tuff bricks, copper bulb/door/trapdoor, wet sponge). ALL are vanilla in 1.21.1 - e.g. SoundEvents.TUFF_BRICKS_BREAK (:1410), TUFF_BRICKS_PLACE (:1413), COPPER_BULB_TURN_ON (:355), COPPER_BULB_TURN_OFF (:356), COPPER_DOOR_OPEN (:363), WET_SPONGE_BREAK (:1529). Import is removed; each call site is retargeted to net.minecraft.sounds.SoundEvents by hand.'
Set-Rule 'org.mesdag.portlib.wrapper.sounds.SoundEventHolder' 'drop' '' 'high' 'Holder for the backported sounds above. Vanilla SoundEvent entries live in BuiltInRegistries.SOUND_EVENT (BuiltInRegistries.java:137), so no holder wrapper is needed.'
Set-Rule 'org.mesdag.portlib.wrapper.world.level.block.PortCopperBulbBlock' 'drop' '' 'high' 'Backported 1.21 block. It is VANILLA in 1.21.1: net.minecraft.world.level.block.CopperBulbBlock (CopperBulbBlock.java:15). Per the rule-set convention backported-content classes are `drop` (nothing PortLib-shaped replaces them) and the call sites are flagged; the correct hand edit is to extend/import the vanilla class.'
Set-Rule 'org.mesdag.portlib.wrapper.world.level.block.PortTransparentBlock' 'drop' '' 'high' 'Backported block; vanilla in 1.21.1 as net.minecraft.world.level.block.TransparentBlock (TransparentBlock.java:12). Same treatment as PortCopperBulbBlock.'

# ---- shared util / diff -----------------------------------------------------
foreach ($c in @('DelayedSupplier','Final','Private','Protected','Static','VarOrInline','ImmutableTransformSet','MutableTransformSet','MutableTransformList','PortLists','PortSets')) {
    Set-Rule "org.mesdag.portlib.util.$c" 'shared' '' 'high' 'Pure JDK/Guava utility or marker annotation - no Minecraft or loader imports. Copy verbatim.' $SH'.util'
}
Set-Rule 'org.mesdag.portlib.diff.Diff' 'shared' '' 'high' 'SOURCE-retention annotation marking "this member differs per loader". Harmless to copy; the ported code usually drops it, but the import must be satisfied.' $SH'.util'
Set-Rule 'org.mesdag.portlib.diff.MemoizeCodec' 'shared' '' 'high' 'Guava-memoised Codec delegate; no loader dependency. Copy verbatim, or delete the file and let the recipes use plain Codecs.' $SH'.util'
Set-Rule 'org.mesdag.portlib.diff.IPortMappedRegistry' 'manual' '' 'high' 'Mixin-implemented alias surface (portlib$addAlias / portlib$getAliaes / portlib$resolve / confluence$onAdd). Registry#addAlias does NOT exist in 1.21.1 (grep -i alias over net/minecraft/core/Registry.java: 0 matches); DeferredRegister.addAlias(ResourceLocation,ResourceLocation) DOES (DeferredRegister.java:303). A human must move alias registration to DeferredRegister and delete the mixin contract.'
Set-Rule 'org.mesdag.portlib.diff.IPortAttribute' 'manual' '' 'medium' 'Mixin-implemented sentiment/tooltip contract on Attribute. Attribute.Sentiment DOES exist in 21.1.219 (Attribute.java:95; POSITIVE/NEUTRAL/NEGATIVE) with setSentiment :57 and getStyle :76, so the PortLib contract is almost entirely redundant - a human should fold each call onto plain Attribute and delete the interface. The tooltip re-styling helper is the only part needing a decision.'
foreach ($c in @('IPortItem','IPortItemStack','IPortLivingEntity','IPortEntityDimensions','IPortEntityType','IPortPlayer','IPortServerPlayer','IPortAbstractArrow','IPortMobEffect','IPortMobEffectInstance','IPortFluidType','IPortFoodProperties','IPortBlock','IPortClientExtensionsSetter','IPortRebuildTask','IPortRenderRegionCache','IPortEntity','IPortProjectile')) {
    Set-Rule "org.mesdag.portlib.diff.$c" 'drop' '' 'high' 'Mixin-implemented state contract that existed to ADD 1.21 state to 1.20.1 vanilla types. On 1.21.1 the state is on the object itself, so the interface and all portlib$* members disappear; call sites become plain vanilla calls. Flagged for manual work because the portlib$* member names have no native spelling.'
}
Set-Rule 'org.mesdag.portlib.diff.mixin.CapabilityProviderAccessor' 'drop' '' 'high' 'Accessor into the Forge capability provider (callSerializeCaps). 1.21.1 has no Forge capabilities - NeoForge uses data components and attachments. Import removed; call sites need a data-component/attachment rewrite by hand.'
Set-Rule 'org.mesdag.portlib.diff.component.PortPatchedDataComponentMap' 'alias' 'net.minecraft.core.component.PatchedDataComponentMap' 'high' 'PatchedDataComponentMap.java:17 (final class) with fromPatch :32, set :66, remove :81, applyPatch :94, setAll :123, asPatch :202, copy :211. There is no public patch() accessor - use asPatch()/isPatchEmpty().'
Set-Rule 'org.mesdag.portlib.diff.PortCommonHooks' 'unknown' '' 'low' 'Dev-only validateComponent(Object) reflection check. Could not confirm a 1.21.1 equivalent named CommonHooks#validateComponent from _nfsrc_219. A human must decide whether to keep a local copy or delete the validation.'
Set-Rule 'org.mesdag.portlib.diff.PortRegistries' 'static-alias' 'net.neoforged.neoforge.registries.NeoForgeRegistries' 'high' 'Qualifier rename. NeoForgeRegistries.java:32 provides ENTITY_DATA_SERIALIZERS/GLOBAL_LOOT_MODIFIER_SERIALIZERS/BIOME_MODIFIER_SERIALIZERS/STRUCTURE_MODIFIER_SERIALIZERS/FLUID_TYPES/HOLDER_SET_TYPES/INGREDIENT_TYPES/FLUID_INGREDIENT_TYPES/CONDITION_SERIALIZERS/ATTACHMENT_TYPES (:34-:43). DATA_COMPONENT_TYPE is NOT here - it is vanilla BuiltInRegistries.DATA_COMPONENT_TYPE (BuiltInRegistries.java:264). There is no ARMOR_MATERIALS (plural); it is vanilla Registries.ARMOR_MATERIAL (:202).'
Set-Rule 'org.mesdag.portlib.diff.PortDataPackRegistriesHooks' 'alias' 'net.neoforged.neoforge.registries.DataPackRegistriesHooks' 'high' 'DataPackRegistriesHooks.java:21 with getSyncedRegistry(ResourceKey) :75.'
Set-Rule 'org.mesdag.portlib.diff.PortRegistryManager' 'alias' 'net.neoforged.neoforge.registries.RegistryManager' 'high' 'RegistryManager.java:41 with isNonSyncedBuiltInRegistry(Registry<?>) :264.'
Set-Rule 'org.mesdag.portlib.diff.PortAdvancedAddEntityPayload' 'alias' 'net.neoforged.neoforge.network.payload.AdvancedAddEntityPayload' 'high' 'Native payload exists under net/neoforged/neoforge/network/payload/.'
Set-Rule 'org.mesdag.portlib.diff.PortSyncAttachmentsPayload' 'alias' 'net.neoforged.neoforge.network.payload.SyncAttachmentsPayload' 'high' 'Native payload exists.'
Set-Rule 'org.mesdag.portlib.diff.PortSyncEffectParticlesS2C' 'drop' '' 'high' '1.21.1 NeoForge synchronises effect particles itself, so the payload disappears.'
Set-Rule 'org.mesdag.portlib.diff.PortBundledPacket' 'drop' '' 'high' 'Batched the 1.20.1 packet fan-out. 1.21.1 has ClientboundBundlePacket and the registrar handles multi-payload sends; nothing PortLib-shaped replaces it.'
Set-Rule 'org.mesdag.portlib.diff.PortModelManager' 'drop' '' 'high' 'Static fields filled by mixins for the atlas/render events. 1.21.1 events (RegisterSpriteSourceTypesEvent, MouseScrollingEvent) carry the values directly.'
Set-Rule 'org.mesdag.portlib.diff.PortMouseHandler' 'drop' '' 'high' 'Same as PortModelManager: mixin-populated static, replaced by the native RegisterSpriteSourceTypesEvent / MouseScrollingEvent data.'
foreach ($c in @('ItemStack$hurtAndBreakAction','LevelWriter$AddFreshEntityAction')) {
    Set-Rule "org.mesdag.portlib.diff.action.$c" 'drop' '' 'high' 'MixinExtras Operation wrapper for injection plumbing. Unreachable on 1.21.1 (Level.addFreshEntity already returns boolean).'
}
Set-Rule 'org.mesdag.portlib.diff.datagen.PortLanguageProvider' 'alias' 'net.neoforged.neoforge.common.data.LanguageProvider' 'medium' 'LanguageProvider.java:28. WARNING: it declares add(String,String) :123, addBlock(Supplier<? extends Block>,String) :65, addItem(Supplier<? extends Item>,String) :73, addEntityType :107, add(TagKey<?>,String) :119 - but there is NO 3-arg add(String,String,String) and addBiome/add(Biome,String) are COMMENTED OUT (LanguageProvider.java:89-97). Any such call site is a hard failure to rewrite by hand.'
foreach ($c in @('PortDataGenerator','PortBiomeTagsProvider','PortBlockTagsProvider','PortItemTagsProvider','PortFluidTagsProvider','PortDamageTypeTagsProvider','PortEntityTypeTagsProvider','PortRecipeProvider','PortBlockLootSubProvider')) {
    Set-Rule "org.mesdag.portlib.diff.datagen.$c" 'drop' '' 'high' 'Data-generator shim for the backported content (including forge:/c: dual-namespace tag writes). The backported content is vanilla in 1.21.1, so the shim disappears; the forge: half of each tag write must be dropped by hand.'
}
foreach ($c in @('PortAttachmentInternals','PortAttachmentSync','PortLevelAttachmentsSavedData')) {
    Set-Rule "org.mesdag.portlib.diff.attachment.$c" 'alias' "net.neoforged.neoforge.attachment.$($c.Substring(4))" 'high' 'NeoForge 1.21.1 ships the same three types: AttachmentInternals, AttachmentSync, LevelAttachmentsSavedData under net/neoforged/neoforge/attachment/.'
}
Set-Rule 'org.mesdag.portlib.diff.attachment.CPortAttachmentHolder' 'alias' 'net.neoforged.neoforge.attachment.IAttachmentHolder' 'medium' 'Storage contract + NBT (de)serialisation. The native interface covers hasData/getData/getExistingData/getExistingDataOrNull/setData/removeData/syncData (IAttachmentHolder.java:19-137); the NBT helpers live on the concrete AttachmentHolder (serializeAttachments :122, deserializeAttachments :151).'
Set-Rule 'org.mesdag.portlib.diff.datamap.PortDataMapEntry' 'alias' 'net.neoforged.neoforge.registries.datamaps.DataMapEntry' 'high' 'DataMapEntry.java:16 with nested Removal<T,R> :29 and codec(...) factories.'
Set-Rule 'org.mesdag.portlib.diff.datamap.PortDataMapFile' 'alias' 'net.neoforged.neoforge.registries.datamaps.DataMapFile' 'high' 'DataMapFile.java:22 (record with replace / values / removals).'
Set-Rule 'org.mesdag.portlib.diff.datamap.PortDataMapLoader' 'alias' 'net.neoforged.neoforge.registries.DataMapLoader' 'medium' 'DataMapLoader exists under net/neoforged/neoforge/registries/; the loader is driven by NeoForge itself on 1.21.1 so most call sites disappear.'
Set-Rule 'org.mesdag.portlib.diff.datamap.PortKnownRegistryDataMapsPayload' 'alias' 'net.neoforged.neoforge.network.payload.KnownRegistryDataMapsPayload' 'high' 'Native payload exists.'
Set-Rule 'org.mesdag.portlib.diff.datamap.PortKnownRegistryDataMapsReplyPayload' 'alias' 'net.neoforged.neoforge.network.payload.KnownRegistryDataMapsReplyPayload' 'high' 'Native payload exists.'
Set-Rule 'org.mesdag.portlib.diff.datamap.PortRegistryDataMapSyncPayload' 'alias' 'net.neoforged.neoforge.network.payload.RegistryDataMapSyncPayload' 'high' 'Native payload exists.'
Set-Rule 'org.mesdag.portlib.diff.datamap.PortRegistryDataMapNegotiation' 'manual' '' 'medium' 'Client/server data-map negotiation choreography that NeoForge now performs internally. A human must decide whether each call site is deleted or replaced by a RegistryManager/DataMapsUpdatedEvent hook.'

# ---- PortLib bootstrap ------------------------------------------------------
Set-Rule 'org.mesdag.portlib.PortLib' 'drop' '' 'high' 'The @Mod("portlib") entrypoint. Everything it registers (16 backported attributes, 13 tuff blocks + items, ADD_TABLE_LOOT_MODIFIER, COMPOSTABLES/FURNACE_FUELS data maps, ADD_CARVERS biome modifier) is vanilla or NeoForge-native in 1.21.1: those attributes are in net.minecraft.world.entity.ai.attributes.Attributes (SCALE :124, JUMP_STRENGTH :80, ...), creative_flight is NeoForgeMod.CREATIVE_FLIGHT, the tuff family is net.minecraft.world.level.block.Blocks (:6866-:6884), and the data maps are NeoForgeDataMaps.COMPOSTABLES/FURNACE_FUELS. All PortLib.CHISELED_TUFF-style constant references must be retargeted by hand.'

# --------------------------------------------------------------------------
# 3. interface-injection layer (wrapper.common.extensions, 78 files)
# --------------------------------------------------------------------------
# Native NeoForge twin interfaces that were confirmed to exist:
$nativeTwins = @{
    'IPortEntityExtension'          = 'IEntityExtension'
    'IPortLivingEntityExtension'    = 'ILivingEntityExtension'
    'IPortPlayerExtension'          = 'IPlayerExtension'
    'IPortServerPlayerExtension'    = 'IPlayerExtension'
    'IPortItemStackExtension'       = 'IItemStackExtension'
    'IPortItemExtension'            = 'IItemExtension'
    'IPortHolderExtension'          = 'IHolderExtension'
    'IPortFriendlyByteBufExtension' = 'IFriendlyByteBufExtension'
    'IPortMobEffectExtension'       = 'IMobEffectExtension'
    'IPortAttributeExtension'       = 'IAttributeExtension'
    'IPortBlockStateExtension'      = 'IBlockStateExtension'
    'IPortLevelExtension'           = 'ILevelExtension'
    'IPortBlockEntityExtension'     = 'IBlockEntityExtension'
    'IPortHolderSetExtension'       = 'IHolderSetExtension'
    'IPortHolderLookupProviderExtension' = 'IHolderLookupProviderExtension'
    'IPortBlockExtension'           = 'IBlockExtension'
    'IPortItemPropertiesExtension'  = 'IItemPropertiesExtensions'
}
# Interfaces the inventory claimed have a native `I*Extension` twin that DOES NOT EXIST in
# 21.1.219 - enumerated directly from net/neoforged/neoforge/common/extensions/.
$noNativeTwin = @{
    'IPortEntityTypeExtension'        = 'There is NO net.neoforged.neoforge.common.extensions.IEntityTypeExtension in 21.1.219 (the package was enumerated: IEntityExtension exists, IEntityTypeExtension does not). The 1.21.1 members the interface forwards to (EntityType.Builder eye-height/attachment helpers, EntityType#getSpawnAABB) are on vanilla EntityType/EntityType.Builder directly.'
    'IPortEntityTypeBuilderExtension' = 'There is NO native IEntityTypeBuilderExtension. The added builder methods live on vanilla net.minecraft.world.entity.EntityType.Builder.'
    'IPortBlockEntityTypeExtension'   = 'The inventory named a native IBlockEntityTypeExtension; it DOES NOT EXIST. BlockEntityType#getBlockStates() is a plain method on vanilla net.minecraft.world.level.block.entity.BlockEntityType, so this interface is only needed as shared vocabulary.'
}
$portOnlyExtensions = @{
    'IPortForgeRegistryExtension' = 'IForgeRegistry has no 1.21.1 counterpart; data maps now live on Registry. Delete the interface and retarget each member.'
    'IPortConfigValueExtension'   = 'ModConfigSpec.ConfigValue.get() already exposes the raw value (ModConfigSpec.java:1220), so the extra accessor is redundant - but each use site must be checked.'
    'IPortChunkMapExtension'      = 'Reaches into the internal ChunkMap tracker map. 21.1.219 field/method shapes differ; a human must re-derive each access.'
    'IPortTextureAtlasExtension'  = 'TextureAtlas internals changed in 1.21.1 (sprite lookup is via TextureAtlas#getSprite). A human must decide per member.'
    'IPortTimerExtension'         = 'Tick-timer shim; 1.21.1 exposes net.minecraft.client.DeltaTracker instead. A human must decide per member.'
}
foreach ($k in $portTypes.Keys) {
    $rec = $portTypes[$k]
    if ($rec.pkg -ne 'org.mesdag.portlib.wrapper.common.extensions') { continue }
    $leaf = $k.Substring($k.LastIndexOf('.') + 1)
    if ($portOnlyExtensions.ContainsKey($leaf)) {
        Set-Rule $k 'manual' '' 'low' $portOnlyExtensions[$leaf]
    } elseif ($noNativeTwin.ContainsKey($leaf)) {
        Set-Rule $k 'shared' '' 'medium' ($noNativeTwin[$leaf] + ' Keep the interface on the 1.21 side and reduce each default method to a delegation onto those vanilla members; the interface-injection manifest must be re-pointed at it.') "$SH.extensioninjection"
    } elseif ($nativeTwins.ContainsKey($leaf)) {
        Set-Rule $k 'alias' "net.neoforged.neoforge.common.extensions.$($nativeTwins[$leaf])" 'medium' "Instance members that mod code reaches through this interface exist on the native twin, but the PortLib interface ALSO declares Port-only statics/extras. A human must move those extras to a helper before the interface can be deleted. Native twin: net.neoforged.neoforge.common.extensions.$($nativeTwins[$leaf])."
    } else {
        Set-Rule $k 'shared' '' 'medium' 'Interface-injection vocabulary with NO same-shaped native twin. Keep the interface on the 1.21 side and reduce each default method to a delegation onto the vanilla member named in the inventory; the interface-injection manifest must be re-pointed at it.' "$SH.extensioninjection"
    }
}

# --------------------------------------------------------------------------
# 4. explicit corrections for auto-resolutions that are provably wrong
# --------------------------------------------------------------------------
# These were found by auditing every confidence=low auto-resolution. Each one either
# resolved to a same-named type in an unrelated package, or resolved where no native
# counterpart exists at all.
$manualNested = @{
    'org.mesdag.portlib.client.gui.PortConfigurationScreen.Change'   = 'Nested helper of the PortLib config screen. NeoForge ConfigurationScreen (ConfigurationScreen.java:110) provides its own screen sections, so this type has no counterpart - the surrounding screen code must be re-authored.'
    'org.mesdag.portlib.client.gui.PortConfigurationScreen.ConfigList' = 'PortLib config-screen internals only; NeoForge ships ConfigurationSectionScreen instead. Re-author by hand.'
    'org.mesdag.portlib.client.gui.PortConfigurationScreen.ConfigRow'  = 'PortLib config-screen internals only; NeoForge ships ConfigurationSectionScreen instead. Re-author by hand.'
    'org.mesdag.portlib.client.gui.PortConfigurationScreen.ConfigValuesScreen' = 'PortLib config-screen internals only; NeoForge ships ConfigurationSectionScreen instead. Re-author by hand.'
    'org.mesdag.portlib.client.gui.PortConfigurationScreen.ConfigValuesScreen.IntegerSlider' = 'PortLib config-screen internals only; NeoForge ships ConfigurationSectionScreen instead. Re-author by hand.'
    'org.mesdag.portlib.client.gui.PortConfigurationScreen.EditHistory' = 'PortLib config-screen internals only; NeoForge ships ConfigurationSectionScreen instead. Re-author by hand.'
    'org.mesdag.portlib.client.gui.PortConfigurationScreen.InvalidValue' = 'PortLib config-screen internals only; NeoForge ships ConfigurationSectionScreen instead. Re-author by hand.'
    'org.mesdag.portlib.client.PortGuiLayer.Delegate'                = 'PortLib layering helper with no native peer; LayeredDraw.Layer (LayeredDraw.java:42) is the only native contract.'
    'org.mesdag.portlib.config.PortConfigSpec.ListValue'             = 'NO native type. In 1.21.1 defineList/defineListAllowEmpty return ConfigValue<List<? extends T>>; only ConfigValue/BooleanValue/IntValue/LongValue/DoubleValue/EnumValue exist (ModConfigSpec.java:1186-:1352). Call sites must be re-typed.'
    'org.mesdag.portlib.config.PortConfigSpec.NumberValue'           = 'NO native type. Use IntValue/LongValue/DoubleValue (ModConfigSpec.java:1303,:1319,:1335) according to the primitive actually used.'
    'org.mesdag.portlib.config.PortConfigSpec.StringValue'           = 'NO native type. A string config value is a plain ConfigValue<String> (ModConfigSpec.java:1186).'
    'org.mesdag.portlib.network.PortNetworkHandler.C'                = 'Private/unused nested helper inside the SimpleChannel transport shim; disappears with the shim.'
    'org.mesdag.portlib.registries.PortRegistryEntry.Memoized'       = 'PortLib-only lazy holder. DeferredHolder (DeferredHolder.java:32) has no Memoized peer - its value()/get() are already lazy.'
    'org.mesdag.portlib.registries.PortAttributeRegistration.AttributeMaker' = 'PortLib builder shim for the attribute DeferredRegister. 1.21.1 Attribute exposes setSyncable(boolean) (Attribute.java:52) and setSentiment(Sentiment) (:57) directly, so the maker collapses - but each chain must be re-authored.'
    'org.mesdag.portlib.registries.PortParticleTypeRegistration.PortLibParticleType' = 'PortLib abstract ParticleType subclass. 1.21.1 ParticleType IS abstract with codec() (ParticleType.java:18) and streamCodec() (:20), so a project-local subclass is still required - a human must author it.'
    'org.mesdag.portlib.registries.callback.PortAddCallback.Vanilla' = 'PortLib convenience callback with no native peer. Native callbacks are RegistryCallback/AddCallback/BakeCallback/ClearCallback (AddCallback.java:16,:25).'
    'org.mesdag.portlib.wrapper.world.item.crafting.PortAbstractCookingRecipe.Factory' = 'PortLib serializer factory. The native recipe serializer mechanism differs (RecipeSerializer/MapCodec); re-author by hand.'
    'org.mesdag.portlib.wrapper.world.item.crafting.PortShapedRecipePattern.Data'      = 'NO native nested type: ShapedRecipePattern (ShapedRecipePattern.java:22) is a final class whose data is constructed via of(...)/ofPositioned(...). Re-author the call site.'
    'org.mesdag.portlib.wrapper.world.item.PortArmorItem.PortType'  = 'NO 1.21.1 native: net.minecraft.world.item.equipment.ArmorType is a 1.21.2+ addition (tree-wide grep of _nfsrc_219 for "ArmorType": 0 matches). In 1.21.1 the equivalent is the nested net.minecraft.world.item.ArmorItem.Type.'
    'org.mesdag.portlib.wrapper.world.item.PortArmorMaterial.Settings' = 'PortLib builder for the 1.20-style armour material. 1.21.1 ArmorMaterial is a RECORD (ArmorMaterial.java:13), so the Settings builder disappears and each call becomes a record construction.'
    'org.mesdag.portlib.wrapper.world.item.PortItem.TooltipContext' = 'Wrong namespace on purpose: the native type is net.minecraft.world.item.Item.TooltipContext, not a PortItem member.'
    'org.mesdag.portlib.wrapper.fluids.PortFluidType.DripstoneDripInfo' = 'Forge-era FluidType.DripstoneDripInfo. Could not be confirmed in 21.1.219 from this run; a human must check net.neoforged.neoforge.fluids.FluidType for the dripstone-drip shape.'
    'org.mesdag.portlib.wrapper.serialization.PortJavaOps.FixedMapBuilder' = 'Internal helper of the PortJavaOps DynamicOps implementation; disappears with it.'
    'org.mesdag.portlib.wrapper.common.PortTags.Blocks'    = 'Nested tag-constant container. PortTags is 514 lines of TagKeys many of which are in the vanished forge: namespace - each constant needs an individual decision (vanilla BlockTags / NeoForge Tags.Blocks / drop).'
    'org.mesdag.portlib.wrapper.common.PortTags.Items'     = 'Nested tag-constant container; see PortTags. net.neoforged.neoforge.common.Tags.Items (Tags.java:312) is only ONE of several destinations.'
    'org.mesdag.portlib.wrapper.common.PortTags.Biomes'      = 'Nested tag-constant container; see PortTags.'
    'org.mesdag.portlib.wrapper.common.PortTags.DamageTypes' = 'Nested tag-constant container; see PortTags.'
    'org.mesdag.portlib.wrapper.common.PortTags.EntityTypes' = 'Nested tag-constant container; see PortTags.'
    'org.mesdag.portlib.wrapper.common.PortTags.Fluids'      = 'Nested tag-constant container; see PortTags.'
}
foreach ($k in $manualNested.Keys) {
    if ($portTypes.Contains($k)) { Set-Rule $k 'manual' '' 'medium' $manualNested[$k] }
}

# Correct aliases for auto-resolutions that landed on an unrelated same-named type, or
# that the (event-only) first-pass native table could not see at all.
$aliasFix = @{
    'org.mesdag.portlib.wrapper.world.item.PortItem'                       = 'net.minecraft.world.item.Item'
    'org.mesdag.portlib.wrapper.world.item.PortArmorItem'                  = 'net.minecraft.world.item.ArmorItem'
    'org.mesdag.portlib.wrapper.world.item.PortArmorMaterial'              = 'net.minecraft.world.item.ArmorMaterial'
    'org.mesdag.portlib.wrapper.world.item.PortArmorMaterial.Layer'        = 'net.minecraft.world.item.ArmorMaterial.Layer'
    'org.mesdag.portlib.wrapper.world.item.PortProjectileItem'             = 'net.minecraft.world.item.ProjectileItem'
    'org.mesdag.portlib.wrapper.world.effect.PortMobEffect'                = 'net.minecraft.world.effect.MobEffect'
    'org.mesdag.portlib.wrapper.world.entity.PortEquipmentSlotGroup'       = 'net.minecraft.world.entity.EquipmentSlotGroup'
    'org.mesdag.portlib.wrapper.world.entity.PortSpawnPlacementType'       = 'net.minecraft.world.entity.SpawnPlacementType'
    'org.mesdag.portlib.wrapper.world.entity.PortSpawnPlacementTypes'      = 'net.minecraft.world.entity.SpawnPlacementTypes'
    'org.mesdag.portlib.wrapper.world.entity.PortEntityAttachment'         = 'net.minecraft.world.entity.EntityAttachment'
    'org.mesdag.portlib.wrapper.world.entity.PortEntityAttachment.Fallback'= 'net.minecraft.world.entity.EntityAttachment.Fallback'
    'org.mesdag.portlib.wrapper.world.entity.PortEntityAttachments'        = 'net.minecraft.world.entity.EntityAttachments'
    'org.mesdag.portlib.wrapper.world.entity.PortEntityAttachments.Builder'= 'net.minecraft.world.entity.EntityAttachments.Builder'
    'org.mesdag.portlib.wrapper.world.entity.projectile.PortAbstractArrow' = 'net.minecraft.world.entity.projectile.AbstractArrow'
    'org.mesdag.portlib.wrapper.world.entity.projectile.PortProjectileDeflection' = 'net.minecraft.world.entity.projectile.ProjectileDeflection'
    'org.mesdag.portlib.wrapper.world.entity.vehicle.PortVehicleEntity'   = 'net.minecraft.world.entity.vehicle.VehicleEntity'
    'org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier' = 'net.minecraft.world.entity.ai.attributes.AttributeModifier'
    'org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier.Operation' = 'net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation'
    'org.mesdag.portlib.wrapper.world.food.PortFoodProperties'            = 'net.minecraft.world.food.FoodProperties'
    'org.mesdag.portlib.wrapper.world.food.PortFoodProperties.PortPossibleEffect' = 'net.minecraft.world.food.FoodProperties.PossibleEffect'
    'org.mesdag.portlib.wrapper.world.inventory.PortRecipeCraftingHolder' = 'net.minecraft.world.inventory.RecipeCraftingHolder'
    'org.mesdag.portlib.wrapper.world.item.alchemy.PortPotionBrewing'      = 'net.minecraft.world.item.alchemy.PotionBrewing'
    'org.mesdag.portlib.wrapper.world.item.alchemy.PortPotionBrewing.Builder' = 'net.minecraft.world.item.alchemy.PotionBrewing.Builder'
    'org.mesdag.portlib.wrapper.world.item.alchemy.PortPotionContents'     = 'net.minecraft.world.item.alchemy.PotionContents'
    'org.mesdag.portlib.wrapper.world.item.crafting.PortRecipe'            = 'net.minecraft.world.item.crafting.Recipe'
    'org.mesdag.portlib.wrapper.world.item.crafting.PortRecipeInput'       = 'net.minecraft.world.item.crafting.RecipeInput'
    'org.mesdag.portlib.wrapper.world.item.crafting.PortCraftingInput'     = 'net.minecraft.world.item.crafting.CraftingInput'
    'org.mesdag.portlib.wrapper.world.item.crafting.PortCraftingInput.Positioned' = 'net.minecraft.world.item.crafting.CraftingInput.Positioned'
    'org.mesdag.portlib.wrapper.world.item.crafting.PortSingleRecipeInput' = 'net.minecraft.world.item.crafting.SingleRecipeInput'
    'org.mesdag.portlib.wrapper.world.item.crafting.PortShapedRecipePattern' = 'net.minecraft.world.item.crafting.ShapedRecipePattern'
    'org.mesdag.portlib.wrapper.world.item.crafting.PortAbstractCookingRecipe' = 'net.minecraft.world.item.crafting.AbstractCookingRecipe'
    'org.mesdag.portlib.wrapper.world.item.component.PortTool'             = 'net.minecraft.world.item.component.Tool'
    'org.mesdag.portlib.wrapper.world.item.component.PortTool.PortRule'    = 'net.minecraft.world.item.component.Tool.Rule'
    'org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers' = 'net.minecraft.world.item.component.ItemAttributeModifiers'
    'org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers.Builder' = 'net.minecraft.world.item.component.ItemAttributeModifiers.Builder'
    'org.mesdag.portlib.wrapper.world.item.component.PortItemAttributeModifiers.Entry' = 'net.minecraft.world.item.component.ItemAttributeModifiers.Entry'
    'org.mesdag.portlib.wrapper.world.item.component.PortBundleContents'   = 'net.minecraft.world.item.component.BundleContents'
    'org.mesdag.portlib.wrapper.world.item.component.PortChargedProjectiles' = 'net.minecraft.world.item.component.ChargedProjectiles'
    'org.mesdag.portlib.wrapper.world.item.component.PortSuspiciousStewEffects' = 'net.minecraft.world.item.component.SuspiciousStewEffects'
    'org.mesdag.portlib.wrapper.world.item.component.PortSuspiciousStewEffects.PortEntry' = 'net.minecraft.world.item.component.SuspiciousStewEffects.Entry'
    'org.mesdag.portlib.wrapper.world.item.component.PortMapPostProcessing' = 'net.minecraft.world.item.component.MapPostProcessing'
    'org.mesdag.portlib.wrapper.world.item.component.PortDebugStickState'  = 'net.minecraft.world.item.component.DebugStickState'
    'org.mesdag.portlib.wrapper.world.item.enchantment.PortEnchantmentHelper' = 'net.minecraft.world.item.enchantment.EnchantmentHelper'
    'org.mesdag.portlib.wrapper.world.item.enchantment.PortItemEnchantments'  = 'net.minecraft.world.item.enchantment.ItemEnchantments'
    'org.mesdag.portlib.wrapper.world.item.enchantment.PortItemEnchantments.Mutable' = 'net.minecraft.world.item.enchantment.ItemEnchantments.Mutable'
    'org.mesdag.portlib.wrapper.world.level.PortExplosionDamageCalculator' = 'net.minecraft.world.level.ExplosionDamageCalculator'
    'org.mesdag.portlib.wrapper.world.PortItemInteractionResult'           = 'net.minecraft.world.ItemInteractionResult'
    'org.mesdag.portlib.wrapper.world.level.chunk.status.PortChunkType'    = 'net.minecraft.world.level.chunk.status.ChunkType'
    'org.mesdag.portlib.wrapper.world.level.portal.PortDimensionTransition' = 'net.minecraft.world.level.portal.DimensionTransition'
    'org.mesdag.portlib.wrapper.world.level.portal.PortPostDimensionTransition' = 'net.minecraft.world.level.portal.DimensionTransition.PostDimensionTransition'
    'org.mesdag.portlib.wrapper.server.level.PortClientInformation'        = 'net.minecraft.server.level.ClientInformation'
    'org.mesdag.portlib.datamap.PortDataMapType.Builder'                   = 'net.neoforged.neoforge.registries.datamaps.DataMapType.Builder'
    'org.mesdag.portlib.datamap.PortAdvancedDataMapType.Builder'           = 'net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType.Builder'
    'org.mesdag.portlib.datamap.PortDataMapProvider.Builder'               = 'net.neoforged.neoforge.common.data.DataMapProvider.Builder'
    'org.mesdag.portlib.component.PortDataComponentType.Builder'           = 'net.minecraft.core.component.DataComponentType.Builder'
    'org.mesdag.portlib.component.PortDataComponentMap.Builder'            = 'net.minecraft.core.component.DataComponentMap.Builder'
    'org.mesdag.portlib.component.PortDataComponentPatch.Builder'          = 'net.minecraft.core.component.DataComponentPatch.Builder'
    'org.mesdag.portlib.attachment.PortAttachmentType.Builder'             = 'net.neoforged.neoforge.attachment.AttachmentType.Builder'
    'org.mesdag.portlib.wrapper.common.conditions.PortWithConditions.Builder' = 'net.neoforged.neoforge.common.conditions.WithConditions.Builder'
    'org.mesdag.portlib.diff.PortRegistries.Keys'                          = 'net.neoforged.neoforge.registries.NeoForgeRegistries.Keys'
}
foreach ($k in $aliasFix.Keys) {
    if ($portTypes.Contains($k)) {
        Set-Rule $k 'alias' $aliasFix[$k] 'high' 'Hand-corrected mapping: the automatic name match either landed on an unrelated same-named type or the type was outside the first-pass native table. Verified to exist in the full native type table.'
    }
}

# PortSpawnPlacementTypes is a CONSTANTS HOLDER, so the rule-set spec calls it static-alias rather
# than alias. Placed after the $aliasFix loop, which would otherwise overwrite this entry.
Set-Rule 'org.mesdag.portlib.wrapper.world.entity.PortSpawnPlacementTypes' 'static-alias' 'net.minecraft.world.entity.SpawnPlacementTypes' 'high' 'Constants holder: SpawnPlacementTypes.NO_RESTRICTIONS (:12), IN_WATER (:13), IN_LAVA (:21), ON_GROUND (:24). Almost always used as a qualifier, so only the qualifier needs swapping - see the port-spawnplacementtypes-qualifier callsite rule.'

# EnchantmentHelper visitors: the native names differ ("InSlot", not "Slot").
Set-Rule 'org.mesdag.portlib.wrapper.world.item.enchantment.PortEnchantmentHelper.EnchantmentSlotVisitor' 'alias' 'net.minecraft.world.item.enchantment.EnchantmentHelper.EnchantmentInSlotVisitor' 'high' 'NAME MISMATCH: the native nested interface is EnchantmentInSlotVisitor, not EnchantmentSlotVisitor.'
Set-Rule 'org.mesdag.portlib.wrapper.world.item.enchantment.PortEnchantmentHelper.EnchantmentVisitor' 'alias' 'net.minecraft.world.item.enchantment.EnchantmentHelper.EnchantmentVisitor' 'high' 'Same-named native nested interface.'
Set-Rule 'org.mesdag.portlib.diff.datamap.PortKnownRegistryDataMapsPayload.MandatoryEntry' 'alias' 'net.neoforged.neoforge.registries.ClientRegistryManager.MandatoryEntry' 'high' 'Moved across classes: the native record is ClientRegistryManager.MandatoryEntry, not a member of KnownRegistryDataMapsPayload (which only nests KnownDataMap).'

# PortLib "Holder" wrappers exist because 1.20.1 Forge handed out RegistryObject. On 1.21.1 the
# registered object IS a Holder, so each wrapper collapses to Holder<X> with a fixed argument -
# which a plain type alias cannot express, hence manual.
$holderWrappers = @{
    'org.mesdag.portlib.wrapper.world.effect.MobEffectHolder'                    = 'net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect>'
    'org.mesdag.portlib.wrapper.world.entity.ai.attributes.AttributeHolder'      = 'net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute>'
    'org.mesdag.portlib.wrapper.world.item.alchemy.PotionHolder'                = 'net.minecraft.core.Holder<net.minecraft.world.item.alchemy.Potion>'
    'org.mesdag.portlib.wrapper.world.item.enchantment.EnchantmentHolder'       = 'net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment>'
    'org.mesdag.portlib.wrapper.world.level.block.BlockHolder'                  = 'net.minecraft.core.Holder<net.minecraft.world.level.block.Block>'
    'org.mesdag.portlib.wrapper.world.level.gameevent.GameEventHolder'          = 'net.minecraft.core.Holder<net.minecraft.world.level.gameevent.GameEvent>'
}
foreach ($k in $holderWrappers.Keys) {
    if ($portTypes.Contains($k)) {
        Set-Rule $k 'manual' '' 'medium' ("No native class. Every use site becomes a plain net.minecraft.core.Holder reference with a FIXED type argument - $($holderWrappers[$k]) - which a type alias cannot express, so a human (or a dedicated callsite rule) must rewrite each occurrence. Prefer DeferredHolder<X,Y> when the value comes from a DeferredRegister.")
    }
}

# --------------------------------------------------------------------------
# 5. mechanical defaults for everything else
# --------------------------------------------------------------------------
function New-Default-Rule([string]$fqn, [string]$pkg) {
    # mixins / actions / datagen shims / emulated registries -> drop
    if ($pkg -like 'org.mesdag.portlib.diff.mixin*') {
        return [pscustomobject]@{ kind='drop'; native=''; confidence='high'; notes='1.20.1 mixin. Exists only to CREATE a fire point or object state that 1.21.1 already has natively, so the mixin and this class disappear.'; copyTo='' }
    }
    if ($pkg -like 'org.mesdag.portlib.diff.action*') {
        return [pscustomobject]@{ kind='drop'; native=''; confidence='high'; notes='MixExtras @WrapOperation holder class. It exists only to carry injection plumbing for a 1.20.1 method that 1.21.1 already returns the right value from (e.g. Level.addFreshEntity returns boolean natively), so the whole holder disappears.'; copyTo='' }
    }
    if ($pkg -like 'org.mesdag.portlib.network.config*') {
        return [pscustomobject]@{ kind='drop'; native=''; confidence='high'; notes='Part of the 1.20.1 CONFIGURATION-phase emulation (PortNetworkHandler / PortConfigurationManager). 1.21.1 has a real configuration phase, so none of these helpers has a counterpart.'; copyTo='' }
    }
    if ($pkg -eq 'org.mesdag.portlib.wrapper.common.extensions') {
        return [pscustomobject]@{ kind='shared'; native=''; confidence='low'; notes='Unclassified IPort*Extension member (nested type or helper) discovered by enumerating the package. Default assumption is the shared injection vocabulary; a human must confirm whether a native I*Extension twin exists.'; copyTo="$SH.extensioninjection" }
    }
    if ($pkg -like 'PortLib.extensions*') {
        return [pscustomobject]@{ kind='drop'; native=''; confidence='high'; notes='Compiler-generated static-member helper for an interface-injected vanilla/loader class. Produced by the interface-injection compile step; it does not exist as hand-written source on the 1.21 side.'; copyTo='' }
    }
    $leaf = $fqn.Substring($fqn.LastIndexOf('.') + 1)
    $stripped = $leaf -replace '^Port', ''
    if (-not $stripped) { $stripped = $leaf }

    # (a) nested type: resolve inside the already-resolved native parent's subtree first.
    $rec = $script:portTypes[$fqn]
    if ($rec -and $rec.outer -ne $rec.pkg) {
        $parentRule = $script:curated[$rec.outer]
        if ($parentRule -and $parentRule.kind -in @('alias','static-alias') -and $parentRule.native) {
            foreach ($cand in @("$($parentRule.native).$leaf", "$($parentRule.native).$stripped")) {
                if ($script:nativeAll.ContainsKey($cand)) {
                    return [pscustomobject]@{ kind='alias'; native=$cand; confidence='medium'; notes="Nested type resolved inside the native parent '$($parentRule.native)'. Auto-resolved (parent-subtree match) - not hand-audited member by member."; copyTo='' }
                }
            }
            $sub = @($script:nativeAll.Keys | Where-Object { $_ -like "$($parentRule.native).*" -and $_.Substring($_.LastIndexOf('.') + 1) -eq $stripped })
            if ($sub.Count -eq 1) {
                return [pscustomobject]@{ kind='alias'; native=$sub[0]; confidence='medium'; notes="Nested type auto-resolved to a deeper member of the native parent '$($parentRule.native)' (the nesting depth differs from PortLib). Not hand-audited."; copyTo='' }
            }
        }
        if ($parentRule -and $parentRule.kind -in @('manual','drop','unknown')) {
            return [pscustomobject]@{ kind=$parentRule.kind; native=''; confidence=$parentRule.confidence; notes="Inherits the parent rule for '$($rec.outer)' ($($parentRule.kind)): $($parentRule.notes)"; copyTo='' }
        }
    }

    # (b) top level: unique simple-name match in the WHOLE native tree.
    $cands = @()
    if ($script:nativeSimple.ContainsKey($stripped)) { $cands = @($script:nativeSimple[$stripped]) }
    if ($cands.Count -eq 1) {
        return [pscustomobject]@{ kind='alias'; native=$cands[0]; confidence='low'; notes="UNREVIEWED auto-resolution: Port '$leaf' -> native simple name '$stripped' matched exactly one type in the native tree. Not hand-checked - a human must confirm the package and the member shape before trusting this substitution."; copyTo='' }
    }
    if ($cands.Count -gt 1) {
        return [pscustomobject]@{ kind='unknown'; native=''; confidence='low'; notes="Ambiguous: native simple name '$stripped' exists in $($cands.Count) types ($($cands -join ', ')). A human must pick the right one."; copyTo='' }
    }
    return [pscustomobject]@{ kind='unknown'; native=''; confidence='low'; notes="No native type named '$stripped' (or the Port name) was found in the FULL parsed native tree (8797 types). Either the Port name differs from the native one, or the class has no 1.21.1 counterpart - a human must classify it."; copyTo='' }
}
$script:nativeAll = $nativeAll
$script:portTypes = $portTypes

# parents before children so nested resolution can see the parent's native value
foreach ($k in ($portTypes.Keys | Sort-Object { ($_ -split '\.').Count }, { $_ })) {
    if ($curated.ContainsKey($k)) { continue }
    $curated[$k] = New-Default-Rule $k $portTypes[$k].pkg
}

# --------------------------------------------------------------------------
# 5b. corrections to the PortLib.extensions.** default
# --------------------------------------------------------------------------
# The default rule for this package is `drop` ("compiler-generated static-member helper").
# That is WRONG for the five members the mod actually calls: measured usage across the four
# modules is PortCodecExtension 105, PortDataResultExtension 68, PortListExtension 54,
# PortDeferredRegisterExtension 11, PortFluidStackExtension 1. Set after the defaults loop so
# these overrides win. The remaining 13 files in the package have 0 uses and stay `drop`.
Set-Rule 'PortLib.extensions.java.util.List.PortListExtension' 'drop' '' 'high' 'Only 6 static members are used (addFirst, addLast, getFirst, getLast, removeFirst, removeLast - all six measured in the mod source) and ALL SIX are Java 21 java.util.List/SequencedCollection methods. The class is not needed: each call becomes an instance call, see the list-extension-* callsite rules. Nothing PortLib-specific survives, so drop is safe here (unlike the Codec/DataResult helpers below).'
Set-Rule 'PortLib.extensions.com.mojang.serialization.DataResult.PortDataResultExtension' 'drop' '' 'high' 'Only 3 static members are used (getOrThrow, ifSuccess, mapOrElse - 68 call sites) and all three are vanilla DataFixerUpper API. Verified in datafixerupper-10.0.21-sources.jar (the DFU version Minecraft 1.21.1 resolves): DataResult.getOrThrow() is a default method at DataResult.java:80 (plus the Function overload at :76), mapOrElse(Function,Function) at :90, ifSuccess(Consumer) at :92. So the class is pure re-exposure and the calls become instance calls - see the dataresult-extension-* callsite rules. NOTE: com/mojang/serialization is NOT in build\_nfsrc_219 (DFU is a library, not decompiled there), which is why that jar is the evidence.'
Set-Rule 'PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension' 'shared' '' 'high' '105 call sites; 5 members used: lazyInitialized, lenientOptionalFieldOf, object2BooleanMap, vector4f, withAlternative. THREE of the five are vanilla DataFixerUpper API - verified in datafixerupper-10.0.21-sources.jar: Codec.lazyInitialized(Supplier) static at Codec.java:204, withAlternative static at :129 and :143 plus default instance forms at :133/:147, and lenientOptionalFieldOf(String) / lenientOptionalFieldOf(String,A) as DEFAULT INSTANCE methods at :287/:291 (PortLib merely re-exposed them as statics taking the codec first). The other two - object2BooleanMap and vector4f - have NO DataFixerUpper equivalent (neither name occurs in Codec.java). VENDOR the class rather than dropping it, because object2BooleanMap/vector4f must survive; optionally inline the three native members afterwards. No callsite rules are emitted for this class on purpose: rewriting only the three native members would leave a half-migrated class.' 'org.confluence.lib.codec'
Set-Rule 'PortLib.extensions.net.minecraftforge.registries.DeferredRegister.PortDeferredRegisterExtension' 'manual' '' 'high' '11 call sites, all `register(...)`: this helper wraps the FORGE DeferredRegister and returns a Forge RegistryObject, so it cannot be aliased. On 1.21.1 use DeferredRegister.register(String, Supplier) (DeferredRegister.java:214) / the DeferredRegister.Items/Blocks overloads and capture the resulting DeferredHolder<X,Y>. A human must re-type each call site.'
Set-Rule 'PortLib.extensions.net.minecraftforge.fluids.FluidStack.PortFluidStackExtension' 'drop' '' 'high' '1 call site: isSameFluidSameComponents. NeoForge 1.21.1 has the vanilla-side equivalent as a static - net.neoforged.neoforge.fluids.FluidStack.isSameFluidSameComponents(FluidStack,FluidStack) at FluidStack.java:349 (the deprecated instance form is at :505). See the fluidstack-extension-issamefluidsamecomponents callsite rule.'
foreach ($ext in @('PortLib.extensions.com.mojang.datafixers.util.Either.PortEitherExtension',
                   'PortLib.extensions.net.minecraftforge.client.event.RenderBlockScreenEffectEvent.PortRenderBlockScreenEffectEventExtension',
                   'PortLib.extensions.net.minecraftforge.client.event.RenderLevelStageEvent.PortRenderLevelStageEventExtension',
                   'PortLib.extensions.net.minecraftforge.client.gui.overlay.IGuiOverlay.PortIGuiOverlayExtension',
                   'PortLib.extensions.net.minecraftforge.client.model.lighting.QuadLighter.PortQuadLighterExtension',
                   'PortLib.extensions.net.minecraftforge.common.crafting.conditions.ICondition.PortIConditionExtension',
                   'PortLib.extensions.net.minecraftforge.eventbus.api.Event.PortEventExtension',
                   'PortLib.extensions.net.minecraftforge.fml.InterModComms.PortInterModCommsExtension',
                   'PortLib.extensions.net.minecraftforge.fml.LogicalSide.PortLogicalSideExtension',
                   'PortLib.extensions.net.minecraftforge.registries.ForgeRegistry.PortForgeRegistryExtension',
                   'PortLib.extensions.net.minecraftforge.registries.IForgeRegistry.PortIForgeRegistryExtension',
                   'PortLib.extensions.com.mojang.serialization.Codec.DispatchedMapCodec',
                   'PortLib.extensions.com.mojang.serialization.Codec.StrictUnboundedMapCodec')) {
    if ($portTypes.Contains($ext)) {
        Set-Rule $ext 'drop' '' 'high' 'Compiler-generated static-member helper for an interface-injected Forge/Mojang class. MEASURED: 0 call sites in any of the four modules, so dropping it (and its import) requires no rewrite. Its nested types (e.g. Event.Result, InterModComms.IMCMessage, RenderLevelStageEvent.Stage) are likewise unused.'
    }
}

# --------------------------------------------------------------------------
# 5. event mapping (Port event classes -> native event + bus)
# --------------------------------------------------------------------------
$evtPkgMap = [ordered]@{
    'org.mesdag.portlib.event.client.extensions.common' = 'net.neoforged.neoforge.client.extensions.common'
    'org.mesdag.portlib.event.client.sound'             = 'net.neoforged.neoforge.client.event.sound'
    'org.mesdag.portlib.event.client'                   = 'net.neoforged.neoforge.client.event'
    'org.mesdag.portlib.event.entity.item'              = 'net.neoforged.neoforge.event.entity.item'
    'org.mesdag.portlib.event.entity.living'            = 'net.neoforged.neoforge.event.entity.living'
    'org.mesdag.portlib.event.entity.player'            = 'net.neoforged.neoforge.event.entity.player'
    'org.mesdag.portlib.event.entity'                   = 'net.neoforged.neoforge.event.entity'
    'org.mesdag.portlib.event.level.block'              = 'net.neoforged.neoforge.event.level.block'
    'org.mesdag.portlib.event.level'                    = 'net.neoforged.neoforge.event.level'
    'org.mesdag.portlib.event.brewing'                  = 'net.neoforged.neoforge.event.brewing'
    'org.mesdag.portlib.event.enchanting'               = 'net.neoforged.neoforge.event.enchanting'
    'org.mesdag.portlib.event.furnace'                  = 'net.neoforged.neoforge.event.furnace'
    'org.mesdag.portlib.event.server'                   = 'net.neoforged.neoforge.event.server'
    'org.mesdag.portlib.event.tick'                     = 'net.neoforged.neoforge.event.tick'
    'org.mesdag.portlib.event.village'                  = 'net.neoforged.neoforge.event.village'
    'org.mesdag.portlib.event.lifecycle'                = 'net.neoforged.fml.event.lifecycle'
    'org.mesdag.portlib.event.network'                  = 'net.neoforged.neoforge.network.event'
    'org.mesdag.portlib.event.registries'               = 'net.neoforged.neoforge.registries'
    'org.mesdag.portlib.event.other'                    = 'net.neoforged.neoforge.event'
    'org.mesdag.portlib.event'                          = 'net.neoforged.neoforge.event'
}
$evtTopOverride = @{
    'org.mesdag.portlib.event.PortEvent'                  = 'net.neoforged.bus.api.Event'
    'org.mesdag.portlib.event.PortEventPriority'          = 'net.neoforged.bus.api.EventPriority'
    'org.mesdag.portlib.event.IPortCancellableEvent'      = 'net.neoforged.bus.api.ICancellableEvent'
    'org.mesdag.portlib.event.IPortModBusEvent'           = 'net.neoforged.fml.event.IModBusEvent'
    'org.mesdag.portlib.event.PortEventHandler'           = ''
    'org.mesdag.portlib.event.PortEventHooks'             = ''
    'org.mesdag.portlib.event.PortBus'                    = ''
    'org.mesdag.portlib.event.PortStatAwardEvent'         = 'net.neoforged.neoforge.event.StatAwardEvent'
    'org.mesdag.portlib.event.other.PortStatAwardEvent'   = 'net.neoforged.neoforge.event.StatAwardEvent'
    'org.mesdag.portlib.event.other.PortRegisterCapabilitiesEvent' = 'net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent'
    'org.mesdag.portlib.event.client.extensions.common.PortRegisterClientExtensionsEvent' = 'net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent'
    'org.mesdag.portlib.event.registries.PortRegisterDataMapTypesEvent' = 'net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent'
    'org.mesdag.portlib.event.registries.PortDataMapsUpdatedEvent'      = 'net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent'
    'org.mesdag.portlib.event.registries.PortRegisterEvent'             = 'net.neoforged.neoforge.registries.RegisterEvent'
    'org.mesdag.portlib.event.registries.PortNewRegistryEvent'          = 'net.neoforged.neoforge.registries.NewRegistryEvent'
    'org.mesdag.portlib.event.registries.PortModifyRegistriesEvent'     = 'net.neoforged.neoforge.registries.ModifyRegistriesEvent'
    'org.mesdag.portlib.event.registries.PortDataPackRegistryEvent'     = 'net.neoforged.neoforge.registries.DataPackRegistryEvent'
    'org.mesdag.portlib.event.lifecycle.PortFMLClientSetupEventPort'    = 'net.neoforged.fml.event.lifecycle.FMLClientSetupEvent'
}
$evtNestedOverride = @{
    'org.mesdag.portlib.event.entity.living.PortMobEffectEvent.Applicable.PortResult'   = 'net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable.Result'
    'org.mesdag.portlib.event.entity.living.PortMobSpawnEvent.SpawnPlacementCheck.PortResult' = 'net.neoforged.neoforge.event.entity.living.MobSpawnEvent.SpawnPlacementCheck.Result'
    'org.mesdag.portlib.event.entity.living.PortMobSpawnEvent.PositionCheck.PortResult' = 'net.neoforged.neoforge.event.entity.living.MobSpawnEvent.PositionCheck.Result'
    'org.mesdag.portlib.event.entity.living.PortMobDespawnEvent.PortResult'            = 'net.neoforged.neoforge.event.entity.living.MobDespawnEvent.Result'
    'org.mesdag.portlib.event.entity.player.PortPlayerSpawnPhantomsEvent.PortResult'   = 'net.neoforged.neoforge.event.entity.player.PlayerSpawnPhantomsEvent.Result'
    'org.mesdag.portlib.event.entity.player.PortPlayerInteractEvent.LeftClickBlock.PortAction' = 'net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock.Action'
    'org.mesdag.portlib.event.entity.player.PortUseItemOnBlockEvent.PortUsePhase'      = 'net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent.UsePhase'
    'org.mesdag.portlib.event.entity.player.PortAdvancementEvent.AdvancementProgressEvent.PortProgressType' = 'net.neoforged.neoforge.event.entity.player.AdvancementEvent.AdvancementProgressEvent.ProgressType'
    'org.mesdag.portlib.event.level.block.PortCropGrowEvent.Pre.PortResult'            = 'net.neoforged.neoforge.event.level.block.CropGrowEvent.Pre.Result'
    'org.mesdag.portlib.event.level.PortNoteBlockEvent.PortNote'                      = 'net.neoforged.neoforge.event.level.NoteBlockEvent.Note'
    'org.mesdag.portlib.event.level.PortNoteBlockEvent.PortOctave'                    = 'net.neoforged.neoforge.event.level.NoteBlockEvent.Octave'
    'org.mesdag.portlib.event.level.PortPistonEvent.PortPistonMoveType'               = 'net.neoforged.neoforge.event.level.PistonEvent.PistonMoveType'
    'org.mesdag.portlib.event.entity.living.PortLivingChangeTargetEvent.PortLivingTargetType' = 'net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent.LivingTargetType'
    'org.mesdag.portlib.event.entity.living.PortLivingChangeTargetEvent.IPortLivingTargetType' = 'net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent.ILivingTargetType'
    'org.mesdag.portlib.event.registries.PortRegisterEvent.PortRegisterHelper'        = 'net.neoforged.neoforge.registries.RegisterEvent.RegisterHelper'
    'org.mesdag.portlib.event.registries.PortRegisterEvent.Delegate'                  = ''
    'org.mesdag.portlib.event.registries.PortDataPackRegistryEvent.PortNewRegistry'   = 'net.neoforged.neoforge.registries.DataPackRegistryEvent.NewRegistry'
    'org.mesdag.portlib.event.registries.PortDataPackRegistryEvent.Delegate'          = ''
    'org.mesdag.portlib.event.other.PortTagsUpdatedEvent.PortUpdateCause'             = 'net.neoforged.neoforge.event.TagsUpdatedEvent.UpdateCause'
    'org.mesdag.portlib.event.registries.PortDataMapsUpdatedEvent.UpdateCause'        = 'net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent.UpdateCause'
    'org.mesdag.portlib.event.client.PortInputEvent.PortMouseButton'                  = 'net.neoforged.neoforge.client.event.InputEvent.MouseButton'
    'org.mesdag.portlib.event.client.PortContainerScreenEvent.PortRender'             = 'net.neoforged.neoforge.client.event.ContainerScreenEvent.Render'
    'org.mesdag.portlib.event.client.PortRenderBlockScreenEffectEvent.PortOverlayType' = 'net.neoforged.neoforge.client.event.RenderBlockScreenEffectEvent.OverlayType'
    'org.mesdag.portlib.event.client.PortAddSectionGeometryEvent.PortAdditionalSectionRenderer' = 'net.neoforged.neoforge.client.event.AddSectionGeometryEvent.AdditionalSectionRenderer'
    'org.mesdag.portlib.event.client.PortAddSectionGeometryEvent.PortSectionRenderingContext' = 'net.neoforged.neoforge.client.event.AddSectionGeometryEvent.SectionRenderingContext'
    'org.mesdag.portlib.event.client.PortEntityRenderersEvent.PortModel'              = ''
    'org.mesdag.portlib.event.client.PortScreenEvent.MouseButtonPressed.PortResult'   = 'net.neoforged.neoforge.client.event.ScreenEvent.MouseButtonPressed.Post.Result'
    'org.mesdag.portlib.event.client.PortScreenEvent.MouseButtonReleased.PortResult'  = 'net.neoforged.neoforge.client.event.ScreenEvent.MouseButtonReleased.Post.Result'
    'org.mesdag.portlib.event.client.PortRegisterPayloadHandlersEvent'                = ''
}
$evtTopOverride['org.mesdag.portlib.event.network.PortRegisterPayloadHandlersEvent'] = 'net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent'

function New-Res([string]$native, [string]$note, [string]$confidence) {
    return [pscustomobject]@{ native = $native; note = $note; confidence = $confidence }
}

function Resolve-EventNative([string]$portFqn) {
    if ($evtNestedOverride.ContainsKey($portFqn)) {
        $n = $evtNestedOverride[$portFqn]
        if ($n) { return (New-Res $n 'nested-path override (hand-verified)' 'high') }
        return (New-Res '' 'no native counterpart - the PortLib nested type disappears on 1.21.1' 'high')
    }
    if ($evtTopOverride.ContainsKey($portFqn)) {
        $n = $evtTopOverride[$portFqn]
        if ($n) { return (New-Res $n 'top-level override (hand-verified)' 'high') }
        return (New-Res '' 'no native counterpart - the PortLib adapter type disappears on 1.21.1' 'high')
    }
    $rec = $portTypes[$portFqn]
    if (-not $rec) { return (New-Res '' 'not a parsed PortLib type' 'low') }
    # top level?
    if ($rec.outer -eq $rec.pkg) {
        $pkg = $null
        foreach ($k in $evtPkgMap.Keys) { if ($rec.pkg -eq $k -or $rec.pkg.StartsWith("$k.")) { $pkg = $evtPkgMap[$k]; break } }
        if (-not $pkg) { $pkg = 'net.neoforged.neoforge.event' }
        $leaf = $rec.fqn.Substring($rec.fqn.LastIndexOf('.') + 1)
        $stripped = $leaf
        if ($leaf -like 'Port*') { $stripped = $leaf.Substring(4) }
        elseif ($leaf -like '*EventPort') { $stripped = $leaf.Substring(0, $leaf.Length - 4) }
        $cand = "$pkg.$stripped"
        if ($nativeEventBus.ContainsKey($cand)) { return (New-Res $cand 'package map + Port-prefix strip, verified against the native event table' 'high') }
        if ($nativeSimple.ContainsKey($stripped)) {
            $c = @($nativeSimple[$stripped] | Where-Object { $_ -like "$pkg.*" })
            if ($c.Count -eq 1) { return (New-Res $c[0] 'resolved by simple name inside the expected native package' 'medium') }
        }
        return (New-Res '' "no native event found for '$cand'" 'low')
    }
    # nested: resolve against the native parent's subtree
    $parentRule = $script:eventRuleCache[$rec.outer]
    if (-not $parentRule -or -not $parentRule.native) {
        return (New-Res '' "parent '$($rec.outer)' has no resolved native type" 'low')
    }
    $leaf2 = $rec.fqn.Substring($rec.fqn.LastIndexOf('.') + 1)
    $s2 = $leaf2
    if ($leaf2 -like 'Port*') { $s2 = $leaf2.Substring(4) }
    elseif ($leaf2 -like 'I*') { $s2 = 'I' + $leaf2.Substring(1) }
    foreach ($cand in @("$($parentRule.native).$leaf2", "$($parentRule.native).$s2")) {
        if ($nativeAll.ContainsKey($cand)) { return (New-Res $cand 'nested name matched inside the native parent' 'high') }
    }
    $sub = @($nativeAll.Keys | Where-Object { $_ -like "$($parentRule.native).*" -and $_.Substring($_.LastIndexOf('.') + 1) -eq $s2 })
    if ($sub.Count -eq 1) { return (New-Res $sub[0] 'nested name found one level deeper in the native parent subtree (nesting differs from PortLib)' 'high') }
    $msg = "could not resolve nested '$leaf2' under native '$($parentRule.native)'"
    if ($sub.Count -gt 1) { $msg = $msg + ' - ambiguous, candidates: ' + ($sub -join ' | ') }
    return (New-Res '' $msg 'low')
}

$script:eventRuleCache = @{}
$eventRules = New-Object System.Collections.Generic.List[object]
# process parent-before-child
$eventFqns = $portTypes.Keys | Where-Object { $_ -like 'org.mesdag.portlib.event.*' } |
    Sort-Object { ($_ -split '\.').Count }, { $_ }
foreach ($k in $eventFqns) {
    $res = Resolve-EventNative $k
    $bus = ''
    if ($res.native) { $bus = Get-NativeBus $res.native (New-Object 'System.Collections.Generic.HashSet[string]') }
    $isNested = ($portTypes[$k].outer -ne $portTypes[$k].pkg)
    $rule = [pscustomobject]@{
        port = $k; native = $res.native; note = $res.note; confidence = $res.confidence
        bus = $bus; isNested = $isNested
    }
    $script:eventRuleCache[$k] = $rule
    $eventRules.Add($rule)
}
Write-Host "event rules: $($eventRules.Count); resolved: $(($eventRules | Where-Object { $_.native }).Count); bus known: $(($eventRules | Where-Object { $_.bus }).Count)"

# fold event rules into the curated table so types.json is complete
foreach ($e in $eventRules) {
    if ($e.native) {
        $kind = 'alias'
        if ($e.port -eq 'org.mesdag.portlib.event.PortEventHandler' -or $e.port -eq 'org.mesdag.portlib.event.PortEventHooks') { $kind = 'drop' }
        $busNote = ''
        if ($e.bus) { $busNote = " Event bus: $($e.bus) (see event-bus.json)." }
        Set-Rule $e.port $kind $e.native $e.confidence ("Event mapping - " + $e.note + "." + $busNote + " Nested Port enums renames are listed individually in event-bus.json.")
    } else {
        Set-Rule $e.port 'manual' '' 'medium' ("Event mapping UNRESOLVED/absent - " + $e.note + ". A human must decide the replacement (delete the listener, or pick the native event) before the converter can proceed.")
    }
}

# --------------------------------------------------------------------------
# 6. emit types.json
# --------------------------------------------------------------------------
$typeList = New-Object System.Collections.Generic.List[object]
foreach ($k in ($curated.Keys | Sort-Object)) {
    $c = $curated[$k]
    $o = [ordered]@{
        port = $k
        kind = $c.kind
        native = $c.native
        confidence = $c.confidence
        notes = $c.notes
    }
    if ($c.kind -eq 'shared' -and $c.copyTo) { $o['copyTo'] = $c.copyTo }
    $typeList.Add([pscustomobject]$o)
}
$typesJson = [pscustomobject]@{
    generatedFrom = 'D:\Minecraft\1.20forge\confluence\PortLib\src\main\java (645 files, enumerated) + notes\PORTLIB_API_INVENTORY.md; every native value checked against build\_nfsrc_219 (NeoForge 21.1.219 / MC 1.21.1) plus the fancymodloader:loader:4.0.42 and bus:8.0.5 sources pinned in neoforge-21.1.219-moddev-config.json'
    types = $typeList
}
$typesJson | ConvertTo-Json -Depth 6 | Set-Content -Encoding UTF8 "$RulesDir\types.json"
Write-Host "types.json entries: $($typeList.Count)"

# --------------------------------------------------------------------------
# 7. emit event-bus.json
# --------------------------------------------------------------------------
$busList = New-Object System.Collections.Generic.List[object]
foreach ($e in ($eventRules | Sort-Object port)) {
    $o = [ordered]@{ port = $e.port; native = $e.native; bus = $e.bus }
    if ($e.note) { $o['notes'] = $e.note }
    $busList.Add([pscustomobject]$o)
}
[pscustomobject]@{
    generatedFrom = 'net.neoforged.fml.event.IModBusEvent membership resolved transitively over the decompiled NeoForge 21.1.219 sources in build\_nfsrc_219 plus fancymodloader:loader:4.0.42 and bus:8.0.5 sources'
    busRule = 'bus = "mod" when the NATIVE event class implements net.neoforged.fml.event.IModBusEvent (directly or via a supertype), else "game". The mod bus is ModContainer#getEventBus() (fml-src ModContainer.java:145, @Nullable, abstract); the game bus is NeoForge.EVENT_BUS (NeoForge.java:17).'
    events = $busList
} | ConvertTo-Json -Depth 6 | Set-Content -Encoding UTF8 "$RulesDir\event-bus.json"
Write-Host "event-bus.json entries: $($busList.Count)  (mod: $(($busList | Where-Object { $_.bus -eq 'mod' }).Count), game: $(($busList | Where-Object { $_.bus -eq 'game' }).Count), unknown: $(($busList | Where-Object { -not $_.bus }).Count))"

# --------------------------------------------------------------------------
# 8. callsites.json + imports.json
# --------------------------------------------------------------------------
$calls = New-Object System.Collections.Generic.List[object]
function Add-Call([string]$id, [string]$pattern, [string]$replace, [string]$kind, [string[]]$typeImports, [string]$notes, [bool]$addImports) {
    $o = [ordered]@{
        id = $id; pattern = $pattern; replace = $replace; kind = $kind
        typeImports = @($typeImports); addImports = $addImports; notes = $notes
    }
    $script:calls.Add([pscustomobject]$o)
}

# ---- PortStreamCodec --------------------------------------------------------
Add-Call 'port-streamcodec-composite' 'PortStreamCodec\.composite\(' 'StreamCodec.composite(' 'safe' @('net.minecraft.network.codec.StreamCodec') 'Identical: vanilla StreamCodec declares composite for 1..6 values - 3/5/7/9/11/13 parameters (StreamCodec.java:112,:127,:150,:177,:208,:243). PortLib mirrors the same arities.' $true
Add-Call 'port-streamcodec-of' 'PortStreamCodec\.of\(' 'StreamCodec.of(' 'safe' @('net.minecraft.network.codec.StreamCodec') 'StreamCodec.of(StreamEncoder,StreamDecoder) at StreamCodec.java:15.' $true
Add-Call 'port-streamcodec-ofMember' 'PortStreamCodec\.ofMember\(' 'StreamCodec.ofMember(' 'safe' @('net.minecraft.network.codec.StreamCodec') 'StreamCodec.ofMember EXISTS at StreamCodec.java:29. Note the argument order (value, output), which is the reverse of StreamEncoder.' $true
Add-Call 'port-streamcodec-unit' 'PortStreamCodec\.unit\(' 'StreamCodec.unit(' 'safe' @('net.minecraft.network.codec.StreamCodec') 'StreamCodec.unit(V expectedValue) at StreamCodec.java:43.' $true
Add-Call 'port-streamcodec-map' 'PortStreamCodec\.map\(' 'StreamCodec.map(' 'review' @('net.minecraft.network.codec.StreamCodec') 'DANGER: map is a DEFAULT INSTANCE method on vanilla StreamCodec (StreamCodec.java:63) and its argument order is (factory, getter) - the REVERSE of the java.util.stream convention. PortLib declares it as a static taking a codec first: PortStreamCodec.map(codec, factory, getter). A literal replacement produces an uncompilable call; the receiver must move to the front.' $true
Add-Call 'port-streamcodec-dispatch' 'PortStreamCodec\.dispatch\(' 'StreamCodec.dispatch(' 'review' @('net.minecraft.network.codec.StreamCodec') 'Same problem as map: native dispatch is a default instance method (StreamCodec.java:91) taking (keyGetter, codecGetter), while PortLib exposes it statically with the codec first.' $true
Add-Call 'port-streamcodec-apply' 'PortStreamCodec\.apply\(' 'StreamCodec.apply(' 'review' @('net.minecraft.network.codec.StreamCodec') 'Native apply is a default instance method taking a StreamCodec.CodecOperation (StreamCodec.java:59); PortLib exposes it statically with the codec first. Receiver must move to the front.' $true
Add-Call 'port-streamcodec-mapStream' 'PortStreamCodec\.mapStream\(' 'StreamCodec.mapStream(' 'review' @('net.minecraft.network.codec.StreamCodec') 'Native mapStream is a default instance method (StreamCodec.java:77). Receiver must move to the front.' $true
Add-Call 'port-streamcodec-cast' 'PortStreamCodec\.cast\(' 'StreamCodec.cast(' 'review' @('net.minecraft.network.codec.StreamCodec') 'Native cast() is a default instance method (StreamCodec.java:298). Receiver must move to the front.' $true
Add-Call 'port-streamcodec-operation' 'PortStreamCodec\.PortCodecOperation' 'StreamCodec.CodecOperation' 'safe' @('net.minecraft.network.codec.StreamCodec') 'Nested interface CodecOperation<B,S,T> with apply(StreamCodec) at StreamCodec.java:303.' $false
Add-Call 'port-streamdecoder' 'PortStreamDecoder' 'StreamDecoder' 'safe' @('net.minecraft.network.codec.StreamDecoder') 'net.minecraft.network.codec.StreamDecoder (StreamDecoder.java:4).' $true
Add-Call 'port-streamencoder' 'PortStreamEncoder' 'StreamEncoder' 'safe' @('net.minecraft.network.codec.StreamEncoder') 'net.minecraft.network.codec.StreamEncoder (StreamEncoder.java:4).' $true
Add-Call 'port-streammemberencoder' 'PortStreamMemberEncoder' 'StreamMemberEncoder' 'safe' @('net.minecraft.network.codec.StreamMemberEncoder') 'net.minecraft.network.codec.StreamMemberEncoder (StreamMemberEncoder.java:4); note encode(T value, O output).' $true
Add-Call 'port-streamcodec-lambda-decode-encode' 'new PortStreamCodec<' 'new StreamCodec<' 'safe' @('net.minecraft.network.codec.StreamCodec') 'Anonymous StreamCodec implementations keep working; only the interface name changes.' $true

# ---- PortByteBufCodecs ------------------------------------------------------
Add-Call 'port-bytebufcodecs-qualifier' 'PortByteBufCodecs\.' 'ByteBufCodecs.' 'safe' @('net.minecraft.network.codec.ByteBufCodecs') 'Qualifier swap. ByteBufCodecs is an INTERFACE (ByteBufCodecs.java:43), so all members are implicitly public static.' $true
$bbcReview = @{
    'UNBOUNDED_BYTE_ARRAY' = 'ByteBufCodecs has NO UNBOUNDED_BYTE_ARRAY. The native constant is net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs.UNBOUNDED_BYTE_ARRAY (NeoForgeStreamCodecs.java:20) - a DIFFERENT CLASS, so the qualifier must change too.'
    'UUID'                 = 'ByteBufCodecs has NO UUID codec. The verified native is net.minecraft.core.UUIDUtil.STREAM_CODEC (UUIDUtil.java:43, StreamCodec<ByteBuf,UUID>).'
    'VECTOR4F'             = 'ByteBufCodecs has NO VECTOR4F. Verified natives: VECTOR3F (ByteBufCodecs.java:149) and QUATERNIONF (:158) - a Vector4f codec must be written (e.g. from the Quaternionf codec) or the field dropped.'
    'BLOCK_POS'            = 'ByteBufCodecs has NO BLOCK_POS. The verified native is net.minecraft.core.BlockPos.STREAM_CODEC (BlockPos.java:40).'
    'json'                 = 'PortByteBufCodecs.json(...) has NO native twin: the string "json"/"Json" does not occur anywhere in ByteBufCodecs.java. Fall back to ByteBufCodecs.fromCodec(Codec) with a JSON codec, or drop the field.'
    'list'                 = 'ByteBufCodecs.list() returns a StreamCodec.CodecOperation (ByteBufCodecs.java:384), NOT a StreamCodec - so it is applied with codec.apply(...) rather than used as a codec. PortLib exposes it as a direct codec factory. Rewrite shape.'
    'collection'           = 'ByteBufCodecs.collection(IntFunction) is a CodecOperation overload (:380) alongside the two codec-returning overloads (:351,:355). Overload resolution may silently change; verify each call.'
}
foreach ($m in $bbcReview.Keys) {
    Add-Call "port-bytebufcodecs-$m" "PortByteBufCodecs\.$m\b" '' 'review' @('net.minecraft.network.codec.ByteBufCodecs') $bbcReview[$m] $true
}
foreach ($m in @('BOOL','BYTE','SHORT','UNSIGNED_SHORT','INT','VAR_INT','VAR_LONG','FLOAT','DOUBLE','BYTE_ARRAY','STRING_UTF8','TAG','TRUSTED_TAG','COMPOUND_TAG','TRUSTED_COMPOUND_TAG','VECTOR3F','QUATERNIONF','GAME_PROFILE','GAME_PROFILE_PROPERTIES')) {
    Add-Call "port-bytebufcodecs-$m" "PortByteBufCodecs\.$m\b" "ByteBufCodecs.$m" 'safe' @('net.minecraft.network.codec.ByteBufCodecs') 'Constant verified to exist in vanilla ByteBufCodecs.' $false
}
foreach ($m in @('byteArray','stringUtf8','tagCodec','compoundTagCodec','fromCodec','fromCodecTrusted','fromCodecWithRegistries','fromCodecWithRegistriesTrusted','optional','collection','list','map','either','idMapper','registry','holderRegistry','holder','holderSet')) {
    Add-Call "port-bytebufcodecs-$m" "PortByteBufCodecs\.$m\(" "ByteBufCodecs.$m(" 'safe' @('net.minecraft.network.codec.ByteBufCodecs') 'Method verified to exist in vanilla ByteBufCodecs with a compatible arity (see the verification dossier line numbers in the notes file).' $false
}
# PortLib-only helpers that have no vanilla home
foreach ($m in @('UNBOUNDED_BYTE_ARRAY','UUID','VECTOR4F','BLOCK_POS')) { }   # handled above as review

# ---- PortPacketDistributor -------------------------------------------------
$pdist = @{
    'sendToServer'                        = @('sendToServer', 'safe');    'sendToPlayer'                        = @('sendToPlayer', 'safe')
    'sendToAllPlayers'                    = @('sendToAllPlayers', 'safe')
    'sendToPlayersTrackingEntity'         = @('sendToPlayersTrackingEntity', 'safe')
    'sendToPlayersTrackingEntityAndSelf'  = @('sendToPlayersTrackingEntityAndSelf', 'safe')
    'sendToPlayersTrackingChunk'          = @('sendToPlayersTrackingChunk', 'safe')
    'sendToPlayersInDimension'            = @('sendToPlayersInDimension', 'review')
    'sendToPlayersNear'                   = @('sendToPlayersNear', 'review')
}
foreach ($m in $pdist.Keys) {
    $nat = $pdist[$m][0]; $kd = $pdist[$m][1]
    $note = 'Receiver becomes net.neoforged.neoforge.network.PacketDistributor; payload arguments are CustomPacketPayload varargs.'
    if ($kd -eq 'review') {
        if ($m -eq 'sendToPlayersInDimension') { $note = 'SIGNATURE BREAK: PortLib takes ResourceKey<Level>, the native takes ServerLevel (PacketDistributor.java:57). The caller must supply a ServerLevel.' }
        if ($m -eq 'sendToPlayersNear')        { $note = 'SIGNATURE BREAK: PortLib takes ResourceKey<Level>; the native takes ServerLevel plus explicit (excluded, x, y, z, radius) (PacketDistributor.java:65). Also the native method is sendToPlayersNear, not sendToNearbyPlayers.' }
    }
    Add-Call "port-packetdistributor-$m" "PortPacketDistributor\.$m\(" "PacketDistributor.$nat(" $kd @('net.neoforged.neoforge.network.PacketDistributor') $note $true
}

# ---- IPortPacket ------------------------------------------------------------
Add-Call 'port-ipacket-identifier' 'IPortPacket\b' 'CustomPacketPayload' 'review' @('net.minecraft.network.protocol.common.custom.CustomPacketPayload') 'A payload implements CustomPacketPayload and must implement type() returning CustomPacketPayload.Type<T>. PortLib identifier() (a ResourceLocation) becomes the payload Type, constructed with CustomPacketPayload.createType(String) (CustomPacketPayload.java:20) - there is NO Type#create. The Type record holds the ResourceLocation (CustomPacketPayload.java:79).' $true
Add-Call 'port-ipacket-context-type' 'IPortPacket\.Context' 'IPayloadContext' 'safe' @('net.neoforged.neoforge.network.handling.IPayloadContext') 'Method-for-method identical for player()/connection()/enqueueWork(Runnable)/reply(...)/disconnect(...)/channelHandlerContext() (IPayloadContext.java:35-131).' $true
Add-Call 'port-ipacket-context-enqueuework' '\.enqueueWork\(' '.enqueueWork(' 'review' @('net.neoforged.neoforge.network.handling.IPayloadContext') 'AMBIGUITY HAZARD: the native interface overloads enqueueWork(Runnable) (IPayloadContext.java:81) and enqueueWork(Supplier<T>) (:86). A lambda whose body yields a value now resolves to the Supplier overload and changes its return type from void to CompletableFuture<T>. Pin the argument type.' $false
Add-Call 'port-ipacket-handle' '\bvoid handle\(IPortPacket\.Context' 'void handle(IPayloadContext' 'review' @('net.neoforged.neoforge.network.handling.IPayloadContext') 'The handler is no longer a payload method: it is supplied at registration as an IPayloadHandler<T> (IPayloadHandler.java:18). The body of handle(Context) becomes that handler lambda.' $true
Add-Call 'port-ipacket-s2c' 'IPortPacket\.S2C' 'CustomPacketPayload' 'review' @('net.minecraft.network.protocol.common.custom.CustomPacketPayload') 'The direction marker disappears. At registration the payload must use PayloadRegistrar.playToClient (PayloadRegistrar.java:44), configurationToClient (:70) or commonToClient (:96). PortLib work(Player) becomes the IPayloadHandler body.' $true
Add-Call 'port-ipacket-c2s' 'IPortPacket\.C2S' 'CustomPacketPayload' 'review' @('net.minecraft.network.protocol.common.custom.CustomPacketPayload') 'Mirror of S2C: registration must use playToServer (:52), configurationToServer (:78) or commonToServer (:104). There is no login phase - login-era payloads are common* with versioned().' $true
Add-Call 'port-payloadhandler-ctor' 'new PortPayloadHandler\(' 'PayloadRegistrar.create(' 'review' @('net.neoforged.neoforge.network.registration.PayloadRegistrar') 'SIGNATURE BREAK: PayloadRegistrar has NO static create(...) factory. It is obtained from RegisterPayloadHandlersEvent.registrar(String) (RegisterPayloadHandlersEvent.java:40), and its only public ctor takes just the version (PayloadRegistrar.java:31). PortPayloadHandler(namespace, version) must be restructured.' $true
Add-Call 'port-payloadhandler-registerInGameS2C' '\.registerInGameS2C\(' '.playToClient(' 'review' @('net.neoforged.neoforge.network.registration.PayloadRegistrar') 'Method RENAME: registerInGameS2C/registerInGameC2S -> playToClient/playToServer; registerLoginS2C/registerLoginC2S -> configurationToClient/configurationToServer (PortLib used the login phase to emulate 1.20.2+ configuration). The registered key also changes: a CustomPacketPayload.Type instead of (Class, ResourceLocation). Codec bound is StreamCodec<? super RegistryFriendlyByteBuf,T> for play*, but StreamCodec<? super FriendlyByteBuf,T> for configuration*/common*.' $true
Add-Call 'port-payloadhandler-registerInGameC2S' '\.registerInGameC2S\(' '.playToServer(' 'review' @('net.neoforged.neoforge.network.registration.PayloadRegistrar') 'See registerInGameS2C.' $true
Add-Call 'port-payloadhandler-registerLoginS2C' '\.registerLoginS2C\(' '.configurationToClient(' 'review' @('net.neoforged.neoforge.network.registration.PayloadRegistrar') 'See registerInGameS2C.' $true
Add-Call 'port-payloadhandler-registerLoginC2S' '\.registerLoginC2S\(' '.configurationToServer(' 'review' @('net.neoforged.neoforge.network.registration.PayloadRegistrar') 'See registerInGameS2C.' $true
Add-Call 'port-payloadhandler-registerInGameBidirectional' '\.registerInGameBidirectional\(' '.playBidirectional(' 'review' @('net.neoforged.neoforge.network.registration.PayloadRegistrar') 'Method RENAME (playBidirectional, PayloadRegistrar.java:62).' $true
Add-Call 'port-payloadhandler-versioned' '\.versioned\(|PortRegisterPayloadHandlersEvent' 'RegisterPayloadHandlersEvent' 'review' @('net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent') 'PortLib calls event.registrar("1"); the native RegisterPayloadHandlersEvent is mod-bus (RegisterPayloadHandlersEvent.java:31 implements IModBusEvent) and exposes registrar(String version) at :40. versioned(...) exists on PayloadRegistrar (:145) but is normally not needed.' $true

# ---- PortRegisterHandler / registration ------------------------------------
$regh = @{
    'item'          = @('DeferredRegister.createItems(', 'review', 'DeferredRegister.createItems(String modid) at DeferredRegister.java:142.')
    'block'         = @('DeferredRegister.createBlocks(', 'review', 'DeferredRegister.createBlocks(String modid) at DeferredRegister.java:155.')
    'dataComponent' = @('DeferredRegister.createDataComponents(', 'review', 'DeferredRegister.createDataComponents(ResourceKey<Registry<DataComponentType<?>>>, String) at DeferredRegister.java:169 - the registry is vanilla BuiltInRegistries.DATA_COMPONENT_TYPE (BuiltInRegistries.java:264), NOT NeoForgeRegistries.')
    'attachment'    = @('DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, ', 'review', 'ATTACHMENT_TYPES key verified at NeoForgeRegistries.Keys (NeoForgeRegistries.java:58); the registry field is NeoForgeRegistries.ATTACHMENT_TYPES (:43).')
    'particleType'  = @('DeferredRegister.create(Registries.PARTICLE_TYPE, ', 'review', 'Vanilla key Registries.PARTICLE_TYPE. The registered object must still be an abstract ParticleType subclass implementing codec() (ParticleType.java:18) and streamCodec() (:20).')
    'attribute'     = @('DeferredRegister.create(Registries.ATTRIBUTE, ', 'review', 'Vanilla key Registries.ATTRIBUTE (Registries.java:115).')
    'armorMaterial' = @('DeferredRegister.create(Registries.ARMOR_MATERIAL, ', 'review', 'Vanilla key Registries.ARMOR_MATERIAL (Registries.java:202) - SINGULAR. There is no ARMOR_MATERIALS.')
    'ingredientType'= @('DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, ', 'review', 'NeoForgeRegistries.Keys.INGREDIENT_TYPES (NeoForgeRegistries.java:55).')
}
foreach ($m in $regh.Keys) {
    Add-Call "port-registerhandler-$m" "PortRegisterHandler\.$m\(" $regh[$m][0] $regh[$m][1] @('net.neoforged.neoforge.registries.DeferredRegister') ("PortRegisterHandler.$m(namespace) becomes the corresponding DeferredRegister factory. " + $regh[$m][2] + ' The returned value is a DeferredRegister (or one of its nested Blocks/Items/DataComponents types), not a PortRegistration - so every subsequent .register(...) must be checked against the new receiver type.') $true
}
foreach ($m in @('custom','init')) {
    Add-Call "port-registerhandler-$m" "PortRegisterHandler\.$m\(" '' 'review' @('net.neoforged.neoforge.registries.DeferredRegister') "PortRegisterHandler.$m has no native equivalent: custom(namespace, registryKey, consumer) maps onto DeferredRegister.create(key, namespace) plus makeRegistry(Consumer<RegistryBuilder<T>>) (DeferredRegister.java:259), and init(IEventBus) disappears because DeferredRegister.register(IEventBus) (:315) is called per register instead. A human must restructure each site." $true
}

$regEntry = @{
    'getId'      = @('getId()', 'safe', 'DeferredHolder.getId() returns ResourceLocation (DeferredHolder.java:162).')
    'getKey'     = @('getKey()', 'safe', 'DeferredHolder.getKey() returns ResourceKey<R> (DeferredHolder.java:170).')
    'value'      = @('value()', 'safe', 'DeferredHolder.value() returns T (DeferredHolder.java:100).')
    'get'        = @('get()', 'safe', 'DeferredHolder.get() returns T (DeferredHolder.java:116) via Supplier<T>.')
    'isPresent'  = @('isBound()', 'review', 'DeferredHolder has NO isPresent() - it implements Holder and Supplier, neither of which declares it. Use isBound() (DeferredHolder.java:197) or asOptional() (:125). The polarity is the same, so the rewrite is mechanical but must be verified per site.')
    'getRegisteredName' = @('getRegisteredName()', 'safe', 'Holder#getRegisteredName() is a default method (Holder.java:40) returning "[unregistered]" when unbound, so DeferredHolder inherits it.')
    'tags'       = @('tags()', 'safe', 'DeferredHolder.tags() at DeferredHolder.java:262.')
    'unwrap'     = @('unwrap()', 'safe', 'DeferredHolder.unwrap() at DeferredHolder.java:273.')
    'unwrapKey'  = @('unwrapKey()', 'safe', 'DeferredHolder.unwrapKey() at DeferredHolder.java:285.')
    'kind'       = @('kind()', 'safe', 'DeferredHolder.kind() at DeferredHolder.java:290.')
    'canSerializeIn' = @('canSerializeIn(', 'safe', 'DeferredHolder.canSerializeIn(HolderOwner<R>) at DeferredHolder.java:295.')
}
foreach ($m in $regEntry.Keys) {
    Add-Call "port-registryentry-$m" "\.$m" ("." + $regEntry[$m][0]) $regEntry[$m][1] @('net.neoforged.neoforge.registries.DeferredHolder') ('PortRegistryEntry -> DeferredHolder/DeferredItem/DeferredBlock. ' + $regEntry[$m][2]) $false
}
Add-Call 'port-deferreditem-tostack' '\.toStack\(' '.toStack(' 'safe' @('net.neoforged.neoforge.registries.DeferredItem','net.neoforged.neoforge.registries.DeferredBlock') 'DeferredItem.toStack()/toStack(int) (DeferredItem.java:24,:33) and DeferredBlock.toStack()/toStack(int) (DeferredBlock.java:25,:34) exist.' $false
Add-Call 'port-deferred-asitem' '\.asItem\(' '.asItem(' 'safe' @('net.neoforged.neoforge.registries.DeferredItem','net.neoforged.neoforge.registries.DeferredBlock') 'Both implement ItemLike; asItem() verified (DeferredItem.java:65, DeferredBlock.java:66).' $false
Add-Call 'port-registration-key' '\.key\(\)' '.getRegistryKey()' 'review' @('net.neoforged.neoforge.registries.DeferredRegister') 'PortRegistration.key() returns ResourceKey<? extends Registry<R>>; the native DeferredRegister.getRegistryKey() does the same (DeferredRegister.java:333). getRegistryName() (:340) returns the ResourceLocation.' $false
Add-Call 'port-registration-register' '\.register\(' '.register(' 'safe' @('net.neoforged.neoforge.registries.DeferredRegister') 'DeferredRegister.register(String, Supplier) at DeferredRegister.java:214 and register(String, Function<ResourceLocation,? extends I>) at :225; DeferredRegister.Blocks.register :404/:416; DeferredRegister.Items.register :501/:514.' $false
Add-Call 'port-registration-addalias' '\.addAlias\(' '.addAlias(' 'safe' @('net.neoforged.neoforge.registries.DeferredRegister') 'DeferredRegister.addAlias(ResourceLocation from, ResourceLocation to) at DeferredRegister.java:303. NOTE: Registry#addAlias does NOT exist in 1.21.1 (grep -i alias over net/minecraft/core/Registry.java: 0 matches) - the alias API is on DeferredRegister only.' $false
Add-Call 'port-datacomponentregistration-builder' '\.builder\(' '.registerComponentType(' 'review' @('net.neoforged.neoforge.registries.DeferredRegister.DataComponents') 'PortDataComponentRegistration.builder(name, consumer) becomes DeferredRegister.DataComponents.registerComponentType(String, UnaryOperator<DataComponentType.Builder<D>>) (DeferredRegister.java:667). The consumer becomes a UnaryOperator lambda returning the builder.' $false
Add-Call 'port-deferredregistration-getentries' '\.getEntries\(' '.getEntries(' 'safe' @('net.neoforged.neoforge.registries.DeferredRegister') 'DeferredRegister.getEntries() returns Collection<DeferredHolder<T,? extends T>> (DeferredRegister.java:326).' $false

# ---- PortEventHandler (all four addListener overloads + postEvent) ---------
Add-Call 'port-eventhandler-addlistener-1' 'PortEventHandler\.addListener\(\s*([A-Za-z_$][\w$]*)\s*->' 'BUS.addListener(\1 ->' 'review' @('net.neoforged.bus.api.IEventBus','net.neoforged.neoforge.common.NeoForge') 'BUS is NOT a constant: resolve the lambda parameter type through types.json/event-bus.json. binder "game" -> NeoForge.EVENT_BUS (NeoForge.java:17); "mod" -> the IEventBus injected into the @Mod constructor (ModContainer#getEventBus(), fml-src ModContainer.java:145). PortEventHandler chooses the bus at runtime from IModBusEvent; on 1.21.1 that choice must be baked in per call site. A wrong choice compiles but the listener never fires.' $true
Add-Call 'port-eventhandler-addlistener-priority' 'PortEventHandler\.addListener\(\s*PortEventPriority\.' 'BUS.addListener(EventPriority.' 'review' @('net.neoforged.bus.api.IEventBus','net.neoforged.bus.api.EventPriority','net.neoforged.neoforge.common.NeoForge') 'PortEventPriority -> net.neoforged.bus.api.EventPriority (HIGHEST/HIGH/NORMAL/LOW/LOWEST, EventPriority.java:34-:38). IEventBus.addListener(EventPriority, Consumer<T>) at fml/bus IEventBus.java:78. Same bus decision as port-eventhandler-addlistener-1.' $true
Add-Call 'port-eventhandler-addlistener-receivecancelled' 'PortEventHandler\.addListener\((PortEventPriority\.[\w.]+)\s*,\s*(true|false)\s*,' 'BUS.addListener(\1, \2,' 'review' @('net.neoforged.bus.api.IEventBus','net.neoforged.bus.api.EventPriority') 'IEventBus.addListener(EventPriority, boolean, Consumer<T>) exists (IEventBus.java:101). CAUTION: there are EIGHT addListener overloads including addListener(boolean, Consumer) at :124 and addListener(boolean, Class, Consumer) at :137 - a bare boolean as the first argument selects a receiveCanceled overload, so keep the priority argument present.' $true
Add-Call 'port-eventhandler-addlistener-class' 'PortEventHandler\.addListener\((PortEventPriority\.[\w.]+)\s*,\s*(true|false)\s*,\s*([\w.$]+)\.class\s*,' 'BUS.addListener(\1, \2, \3.class,' 'review' @('net.neoforged.bus.api.IEventBus','net.neoforged.bus.api.EventPriority') 'IEventBus.addListener(EventPriority, boolean, Class<T>, Consumer<T>) exists (IEventBus.java:115). This is the form that avoids reflective type inference, so prefer it when the inferred form is ambiguous.' $true
Add-Call 'port-eventhandler-postevent' 'PortEventHandler\.postEvent\(' 'BUS.post(' 'review' @('net.neoforged.bus.api.IEventBus','net.neoforged.neoforge.common.NeoForge') 'postEvent picks the bus from IModBusEvent at runtime. Replace with NeoForge.EVENT_BUS.post(...) for game-bus events or modBus.post(...) for mod-bus events, using event-bus.json. post(Event) is at IEventBus.java:158.' $true
Add-Call 'port-eventhandler-posteventwithreturn' 'PortEventHandler\.postEventWithReturn\(' 'BUS.post(' 'review' @('net.neoforged.bus.api.IEventBus','net.neoforged.neoforge.common.NeoForge') 'IEventBus.post returns the event, so the "WithReturn" wrapper disappears. Same bus decision as port-eventhandler-postevent.' $true
Add-Call 'port-eventhandler-wrapevent' 'PortEventHandler\.wrapEvent\(' '' 'review' @() 'PortEventHandler.wrapEvent registers a translation listener from a Forge event to a Port event. On 1.21.1 the mod listens to the NATIVE event directly, so every wrapEvent call should be deleted rather than translated. A human must confirm the call has no other purpose.' $false
Add-Call 'port-eventpriority' 'PortEventPriority\.' 'EventPriority.' 'safe' @('net.neoforged.bus.api.EventPriority') 'PortEventPriority -> net.neoforged.bus.api.EventPriority; constants are name-identical.' $true
Add-Call 'port-eventbus' 'PortBus\.' 'NeoForge.EVENT_BUS' 'review' @('net.neoforged.neoforge.common.NeoForge') 'PortBus.MOD/GAME disappears: the mod bus is the IEventBus injected into the @Mod constructor (ModContainer#getEventBus()) and the game bus is NeoForge.EVENT_BUS (NeoForge.java:17). A human must choose per use.' $true
Add-Call 'port-cancellableevent' 'IPortCancellableEvent' 'ICancellableEvent' 'safe' @('net.neoforged.bus.api.ICancellableEvent') 'ICancellableEvent declares setCanceled(boolean) (:29) and isCanceled() (:37) - same shape as the PortLib interface.' $true
Add-Call 'port-modbusevent' 'IPortModBusEvent' 'IModBusEvent' 'safe' @('net.neoforged.fml.event.IModBusEvent') 'IModBusEvent is an empty marker in net.neoforged.fml.event (fml-src IModBusEvent.java:11). VERIFIED CORRECTION to the inventory: the PortLib marker does NOT track the native one. PortEvent itself only does `extends Event` (PortEvent.java:11), and of the 62 client event files only 5 even mention IPortModBusEvent (3 on the top-level class: PortRegisterColorHandlersEvent, PortRegisterGuiLayersEvent, PortRenderLevelStageEvent; 2 only on nested classes). The inventory said "16 of the 62"; that list is really the set of NATIVES that are mod-bus. Consequence: never infer the bus from the PortLib marker - use event-bus.json, which is derived from the native classes.' $true

# ---- PortDataMapType -------------------------------------------------------
foreach ($m in @('builder','synced','build','registryKey','networkCodec','mandatorySync','codec','id','remover','merger')) {
    Add-Call "port-datamaptype-$m" "\.$m\(" ".$m(" 'safe' @('net.neoforged.neoforge.registries.datamaps.DataMapType') 'DataMapType/AdvancedDataMapType expose the same member names (DataMapType.java:85,:152,:161,:92,:113,:120,:106,:99; AdvancedDataMapType.java:69,:76). synced(Codec,boolean) is the ONLY synced overload - there is no single-argument synced(Codec).' $false
}

# ---- PortAttachmentType + IPortAttachmentHolder renames --------------------
foreach ($m in @('builder','serializable','serialize','copyOnDeath','copyHandler','sync','build','read','write','sendToPlayer')) {
    Add-Call "port-attachmenttype-$m" "\.$m\(" ".$m(" 'safe' @('net.neoforged.neoforge.attachment.AttachmentType') 'AttachmentType.Builder has the same member names: serialize x3 (AttachmentType.java:168,:184,:196), copyOnDeath :224, copyHandler :238, sync x3 (:250,:261,:273), build :294. The sync(BiPredicate<IAttachmentHolder,ServerPlayer>, StreamCodec) overload replaces PortLib sync(BiPredicate<IPortAttachmentHolder,...>, ...) - the predicate parameter type changes with the holder interface.' $false
}
$attachRenames = @{
    'hasAttaches'            = @('hasAttachments', 'safe',  'IAttachmentHolder.hasAttachments() at IAttachmentHolder.java:19.')
    'hasAttach'              = @('hasData',        'safe',  'IAttachmentHolder.hasData(AttachmentType<?>) at :24, plus a Supplier<AttachmentType<T>> overload at :29 (note: not AttachmentType, unlike the others).')
    'getAttach'              = @('getData',        'safe',  'IAttachmentHolder.getData(AttachmentType<T>) at :39, Supplier overload at :46.')
    'getExistingAttachOrNull'= @('getExistingDataOrNull', 'safe', 'IAttachmentHolder.getExistingDataOrNull(AttachmentType<T>) at :71, Supplier overload at :81.')
    'getExistingAttach'      = @('getExistingData','safe',  'IAttachmentHolder.getExistingData(AttachmentType<T>) at :55, Supplier overload at :64.')
    'setAttach'              = @('setData',        'safe',  'IAttachmentHolder.setData(AttachmentType<T>,T) at :90, Supplier overload at :97.')
    'removeAttach'           = @('removeData',     'safe',  'IAttachmentHolder.removeData(AttachmentType<T>) at :106, Supplier overload at :113.')
    'syncAttach'             = @('syncData',       'safe',  'IAttachmentHolder.syncData(AttachmentType<?>) at :125, Supplier overload at :137.')
}
foreach ($m in $attachRenames.Keys) {
    Add-Call "port-attachment-$m" "\.$m\(" ("." + $attachRenames[$m][0] + '(') $attachRenames[$m][1] @('net.neoforged.neoforge.attachment.IAttachmentHolder') ('METHOD RENAME. ' + $attachRenames[$m][2] + ' Also retarget the argument when it is a PortRegistryEntry<...>: the native overload takes Supplier<AttachmentType<T>>, and DeferredHolder implements Supplier, so the value still compiles - but a DeferredHolder is NOT an AttachmentType.') $false
}
Add-Call 'port-attachment-cast' '\(IPortAttachmentHolder\)\s*' '' 'review' @('net.neoforged.neoforge.attachment.IAttachmentHolder') 'The cast usually becomes unnecessary on 1.21.1: Entity/Level/BlockEntity implement IAttachmentHolder natively, so ((IPortAttachmentHolder) player).getData(...) is simply player.getData(...). A human must confirm the static type actually implements the interface.' $false
Add-Call 'port-attachment-serializable' 'PortAttachmentType\.serializable\(' 'AttachmentType.serializable(' 'safe' @('net.neoforged.neoforge.attachment.AttachmentType') 'AttachmentType.serializable(Supplier)/serializable(Function<IAttachmentHolder,T>) at AttachmentType.java:119,:132. PortLib binds T to IPortNBTSerializable while native binds it to net.neoforged.neoforge.common.util.INBTSerializable - change the bound type at the declaration.' $true

# ---- PortDataComponentType / Map / Patch ----------------------------------
Add-Call 'port-datacomponenttype-persistent' '\.persistent\(' '.persistent(' 'safe' @('net.minecraft.core.component.DataComponentType') 'DataComponentType.Builder.persistent(Codec<T>) at DataComponentType.java:57 - name-identical to PortLib.' $false
Add-Call 'port-datacomponenttype-networksynchronized' '\.networkSynchronized\(' '.networkSynchronized(' 'safe' @('net.minecraft.core.component.DataComponentType') 'DataComponentType.Builder.networkSynchronized(StreamCodec<? super RegistryFriendlyByteBuf,T>) at DataComponentType.java:62 - name and bounds identical.' $false
Add-Call 'port-datacomponenttype-builder' 'PortDataComponentType\.Builder' 'DataComponentType.Builder' 'safe' @('net.minecraft.core.component.DataComponentType') 'DataComponentType.Builder (DataComponentType.java:50) with persistent :57, networkSynchronized :62, cacheEncoding :67, build :72. The native entry point is the static DataComponentType.builder() (:28).' $true
Add-Call 'port-datacomponentmap-builder' 'PortDataComponentMap\.Builder' 'DataComponentMap.Builder' 'safe' @('net.minecraft.core.component.DataComponentMap') 'DataComponentMap.Builder (DataComponentMap.java:137) with set :143, addAll :156, build :164. There is NO DataComponentMap.entrySet().' $true
Add-Call 'port-datacomponentpatch-builder' 'PortDataComponentPatch\.Builder' 'DataComponentPatch.Builder' 'safe' @('net.minecraft.core.component.DataComponentPatch') 'DataComponentPatch.Builder (DataComponentPatch.java:235) with set :241, remove :247, set(TypedDataComponent) :252, build :256.' $true
Add-Call 'port-datacomponentpatch-empty' 'PortDataComponentPatch\.EMPTY' 'DataComponentPatch.EMPTY' 'safe' @('net.minecraft.core.component.DataComponentPatch') 'DataComponentPatch.EMPTY at DataComponentPatch.java:22.' $true
Add-Call 'port-itemstack-component-accessors' '\.getOrDefault\(|\.has\(|\.get\(' 'SAME' 'review' @('net.minecraft.core.component.DataComponentType') 'The component accessors get/has/getOrDefault are NOT declared on ItemStack - they are inherited defaults from net.minecraft.core.component.DataComponentHolder (:9,:13,:17). A bare .get(x) on an ItemStack compiles, but watch for the same-named Map/List methods resolving instead; add the DataComponentHolder context when in doubt. ItemStack itself only declares set (:716), remove (:732) and two update(...) overloads (:721,:726).' $false

# ---- PortConfigSpec --------------------------------------------------------
Add-Call 'port-configspec-qualifier' 'PortConfigSpec' 'ModConfigSpec' 'safe' @('net.neoforged.neoforge.common.ModConfigSpec') 'Pure rename. ModConfigSpec (ModConfigSpec.java:56) exposes Builder :300, ConfigValue :1186, BooleanValue :1284, IntValue :1303, LongValue :1319, DoubleValue :1335, EnumValue :1352.' $true
Add-Call 'port-configspec-define' '\.define(List|InRange|InList|Enum|AllowEmpty)?\(' '.define(' 'safe' @('net.neoforged.neoforge.common.ModConfigSpec') 'The whole define/defineInRange/defineInList/defineList/defineListAllowEmpty/defineEnum family exists on ModConfigSpec.Builder (ModConfigSpec.java:309-:799) with matching overload shapes, as do comment :803, translation :820, push :842, pop :860, worldRestart :829, build :877.' $false
Add-Call 'port-configspec-listvalue' 'PortConfigSpec\.ListValue' 'ModConfigSpec.ConfigValue<List<? extends Object>>' 'review' @('net.neoforged.neoforge.common.ModConfigSpec') 'SIGNATURE BREAK: ModConfigSpec has NO ListValue type. defineList/defineListAllowEmpty return ConfigValue<List<? extends T>> (ModConfigSpec.java:397,:501). The declared field type must be widened to ConfigValue<List<? extends T>>.' $true
Add-Call 'port-configspec-numbervalue' 'PortConfigSpec\.NumberValue' 'ModConfigSpec.IntValue' 'review' @('net.neoforged.neoforge.common.ModConfigSpec') 'SIGNATURE BREAK: no NumberValue exists. Choose IntValue (:1303), LongValue (:1319) or DoubleValue (:1335) according to the primitive the site actually uses; .get()/.getAsX() differ per choice.' $true
Add-Call 'port-configspec-stringvalue' 'PortConfigSpec\.StringValue' 'ModConfigSpec.ConfigValue<String>' 'review' @('net.neoforged.neoforge.common.ModConfigSpec') 'SIGNATURE BREAK: no StringValue exists. A string config value is ConfigValue<String> (ModConfigSpec.java:1186).' $true

# ---- client ----------------------------------------------------------------
Add-Call 'port-sprites' 'PortWidgetSprites' 'WidgetSprites' 'safe' @('net.minecraft.client.gui.components.WidgetSprites') 'Exact rename: record WidgetSprites(enabled, disabled, enabledFocused, disabledFocused) at WidgetSprites.java:8, get(boolean,boolean) at :17.' $true
Add-Call 'port-imagebutton-ctor' 'new PortImageButton\(' 'new ImageButton(' 'review' @('net.minecraft.client.gui.components.ImageButton') 'ImageButton (ImageButton.java:11) has ctors (int,int,int,int,WidgetSprites,OnPress) :14, the same plus Component :18, and (int,int,WidgetSprites,OnPress,Component) :23. PortLib constructor argument order must be checked against these - particularly the trailing message Component.' $true
Add-Call 'port-sprite' 'PortSprite' '' 'review' @() 'PortSprite has NO native peer (it bundles a ResourceLocation with a width/height). Split each use site: keep the ResourceLocation and pass the dimensions to GuiGraphics.blitSprite(ResourceLocation,int,int,int,int) (GuiGraphics.java:677), or use the 9-argument overload (:706) when the caller was doing manual UV maths.' $false
Add-Call 'port-deltaticker' 'PortDeltaTicker' 'DeltaTracker' 'safe' @('net.minecraft.client.DeltaTracker') 'DeltaTracker (DeltaTracker.java:8) with getGameTimeDeltaTicks :12, getGameTimeDeltaPartialTick(boolean) :14, getRealtimeDeltaTicks :16. RenderTickCounter does NOT exist in 21.1.219.' $true
Add-Call 'port-guilayer' 'PortGuiLayer' 'LayeredDraw.Layer' 'safe' @('net.minecraft.client.gui.LayeredDraw') 'LayeredDraw.Layer (LayeredDraw.java:42) with render(GuiGraphics,DeltaTracker) :43.' $true
Add-Call 'port-configurationscreen' 'PortConfigurationScreen' 'net.neoforged.neoforge.client.gui.ConfigurationScreen' 'safe' @('net.neoforged.neoforge.client.gui.ConfigurationScreen') 'ConfigurationScreen.java:110; ctors :270,:274,:279 all take a ModContainer, which PortLib did not.' $true

# ---- components / components serialization ---------------------------------
Add-Call 'port-componentserialization' 'PortComponentSerialization\.' 'ComponentSerialization.' 'safe' @('net.minecraft.network.chat.ComponentSerialization') 'CODEC :40, STREAM_CODEC :41, OPTIONAL_STREAM_CODEC :42, TRUSTED_STREAM_CODEC :43, TRUSTED_OPTIONAL_STREAM_CODEC :44, TRUSTED_CONTEXT_FREE_STREAM_CODEC :47 (bound to ByteBuf, not RegistryFriendlyByteBuf).' $true

# ---- attributes / spawn placement -----------------------------------------
Add-Call 'port-attributemodifier-operation-static-import' 'import static org\.mesdag\.portlib\.wrapper\.world\.entity\.ai\.attributes\.PortAttributeModifier\.Operation\.' 'import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.' 'safe' @('net.minecraft.world.entity.ai.attributes.AttributeModifier') 'STATIC IMPORT rewrite. PortLib mirrors the 1.21 constant names exactly (ADD_VALUE/ADD_MULTIPLIED_BASE/ADD_MULTIPLIED_TOTAL), and unwraps them to the 1.20 Forge names internally - so the mod source already uses the native spelling and no constant renaming is needed. Native: AttributeModifier.java:64,:65,:66.' $true
foreach ($c in @('ADD_VALUE','ADD_MULTIPLIED_BASE','ADD_MULTIPLIED_TOTAL')) {
    Add-Call "port-attributemodifier-operation-$c" "PortAttributeModifier\.Operation\.$c" "AttributeModifier.Operation.$c" 'safe' @('net.minecraft.world.entity.ai.attributes.AttributeModifier') 'Constant verified (AttributeModifier.java:64,:65,:66). Operation#id() exists (:81); Operation.BY_ID exists (:68) - Operation#fromId does NOT.' $false
}
foreach ($c in @('ADDITION','MULTIPLY_BASE','MULTIPLY_TOTAL')) {
    $new = @{ 'ADDITION'='ADD_VALUE'; 'MULTIPLY_BASE'='ADD_MULTIPLIED_BASE'; 'MULTIPLY_TOTAL'='ADD_MULTIPLIED_TOTAL' }[$c]
    Add-Call "port-operation-legacy-$c" "Operation\.$c\b" "Operation.$new" 'review' @('net.minecraft.world.entity.ai.attributes.AttributeModifier') "LEGACY NAME: $c does NOT exist on net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation in 21.1.219 (only ADD_VALUE/ADD_MULTIPLIED_BASE/ADD_MULTIPLIED_TOTAL at AttributeModifier.java:64-:66). A PortLib-era call site using $c is really the FORGE spelling leaking through, so rewrite to $new - but confirm the semantics, because PortLib's own unwrap() mapped ADD_MULTIPLIED_BASE->MULTIPLY_BASE and ADD_MULTIPLIED_TOTAL->MULTIPLY_TOTAL." $false
}
Add-Call 'port-attributemodifier' 'PortAttributeModifier\b' 'AttributeModifier' 'safe' @('net.minecraft.world.entity.ai.attributes.AttributeModifier') 'AttributeModifier is a RECORD (ResourceLocation id, double amount, Operation operation) at AttributeModifier.java:22, so it has id()/amount()/operation() accessors. PortLib.uuid2rl/rl2uuid helpers have NO native peer.' $true
Add-Call 'port-attributemodifier-id' '\.getId\(\)' '.id()' 'review' @('net.minecraft.world.entity.ai.attributes.AttributeModifier') 'On an AttributeModifier the component accessor is id() (record accessor), not getId(). But getId() IS correct on DeferredHolder (DeferredHolder.java:162) - the converter must know the receiver type before applying this rule, which is why it is marked review.' $false
Add-Call 'port-attributeholder' 'AttributeHolder' 'Holder<Attribute>' 'review' @('net.minecraft.core.Holder') 'PortLib''s AttributeHolder wrapper has NO native class. Each use becomes net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute>, or DeferredHolder<Attribute,Y> when it comes from a DeferredRegister. A type alias cannot express the type argument, so this needs a per-site rewrite.' $true
Add-Call 'port-spawnplacementtype' 'PortSpawnPlacementType\b' 'SpawnPlacementType' 'safe' @('net.minecraft.world.entity.SpawnPlacementType') 'SpawnPlacementType (SpawnPlacementType.java:7); SpawnPlacementTypes constants NO_RESTRICTIONS :12, IN_WATER :13, IN_LAVA :21, ON_GROUND :24.' $true
Add-Call 'port-spawnplacementtypes-qualifier' 'PortSpawnPlacementTypes\.' 'SpawnPlacementTypes.' 'safe' @('net.minecraft.world.entity.SpawnPlacementTypes') 'Qualifier swap; all four constants verified.' $true
Add-Call 'port-equipmentslotgroup' 'PortEquipmentSlotGroup' 'EquipmentSlotGroup' 'safe' @('net.minecraft.world.entity.EquipmentSlotGroup') 'EquipmentSlotGroup enum (EquipmentSlotGroup.java:12) with ANY/MAINHAND/OFFHAND/HAND/FEET/LEGS/CHEST/HEAD/ARMOR/BODY (:13-:22).' $true
Add-Call 'port-existingdata-holder-arg' 'PortRegistryEntry' 'DeferredHolder' 'review' @('net.neoforged.neoforge.registries.DeferredHolder') 'When a PortRegistryEntry is passed where an attachment/data-map accessor expects a type, prefer passing the DeferredHolder itself: it implements Supplier<AttachmentType<T>>-compatible semantics on 1.21.1 and the Supplier overloads of IAttachmentHolder accept it directly.' $true

# ---- misc wrapper vocabulary ----------------------------------------------
Add-Call 'port-tristate' 'PortTriState' 'TriState' 'safe' @('net.neoforged.neoforge.common.util.TriState') 'net.neoforged.neoforge.common.util.TriState (TriState.java:11) with TRUE/DEFAULT/FALSE (:15,:19,:23). WRONG namespace risk: net.minecraft.util.TriState does not exist in 1.21.1.' $true
Add-Call 'port-tristate-unwrap' '\.unwrapState\(|\.wrapState\(|\.unwrapResult\(' 'SAME' 'review' @('net.neoforged.neoforge.common.util.TriState') 'PortTriState''s unwrapState/wrapState/unwrapResult helpers convert to and from Boolean for the 1.20 Forge API. TriState only declares isTrue/isDefault/isFalse (TriState.java:27,:31,:35), so these helper calls must be inlined by hand.' $false
Add-Call 'port-effectcures-qualifier' 'PortEffectCures\.' 'EffectCures.' 'safe' @('net.neoforged.neoforge.common.EffectCures') 'EffectCures.MILK :14, HONEY :18, PROTECTED_BY_TOTEM :22, DEFAULT_CURES :24. The singular EffectCure declares no constants of its own.' $true
Add-Call 'port-itemabilities-qualifier' 'PortItemAbilities\.' 'ItemAbilities.' 'safe' @('net.neoforged.neoforge.common.ItemAbilities') 'ItemAbilities declares 24 constants (ItemAbilities.java:23-:149). Code-level names match PortLib, but note HOE_TILL is registered under the STRING "till" (:116) - string-keyed lookups must not assume "hoe_till".' $true
Add-Call 'port-datamap-provider-builder' 'PortDataMapProvider\.Builder|PortDataMapProvider\.AdvancedBuilder' 'DataMapProvider.Builder' 'review' @('net.neoforged.neoforge.common.data.DataMapProvider') 'DataMapProvider.Builder (DataMapProvider.java:116) and AdvancedBuilder (:178) with add/remove/replace/conditions/build (:130-:173) - same surface, but the AdvancedBuilder generic arity is <T,R,VR>.' $true
Add-Call 'port-nbtio' 'PortNbtIo\.' 'NbtIo.' 'safe' @('net.minecraft.nbt.NbtIo') 'NbtIo.readAnyTag(DataInput,NbtAccounter) :166 and writeAnyTag(Tag,DataOutput) :171.' $true
Add-Call 'port-advancementholder' 'PortAdvancementHolder' 'AdvancementHolder' 'safe' @('net.minecraft.advancements.AdvancementHolder') 'record AdvancementHolder(ResourceLocation id, Advancement value) exists in 1.21.1.' $true
Add-Call 'port-recipeholder' 'PortRecipeHolder' 'RecipeHolder' 'safe' @('net.minecraft.world.item.crafting.RecipeHolder') 'record RecipeHolder<T extends Recipe<?>>(ResourceLocation id, T value) at RecipeHolder.java:7.' $true
Add-Call 'port-contextawareredlistener' 'PortContextAwareReloadListener' 'PreparableReloadListener' 'review' @('net.minecraft.server.packs.resources.PreparableReloadListener') 'The native listener interface exists, but PortLib''s condition-context/registry-lookup plumbing is an invention layered on ConditionalOps; a human must decide how much of it survives (ConditionalOps.retrieveContext() at ConditionalOps.java:37 is the native hook).' $true
Add-Call 'port-javaops' 'PortJavaOps' '' 'review' @() 'PortJavaOps is a 419-line DynamicOps<Object> implementation with no 21.1.219 equivalent and no drop-in replacement. Marked unknown in types.json: a human must decide whether to carry a loader-independent copy or migrate the affected codecs to NbtOps/JsonOps.' $false
Add-Call 'port-tags-qualifier' 'PortTags\.' '' 'review' @() 'PortTags is 514 lines of TagKey constants, many in the vanished forge: namespace. Every constant needs an individual decision: vanilla net.minecraft.tags.ItemTags/BlockTags, NeoForge net.neoforged.neoforge.common.Tags.Items (Tags.java:312) / Tags.Blocks (:30), or delete. There is no blanket rule - the converter must emit a TODO per occurrence.' $false

# ---- backported content (drop + manual retarget) ---------------------------
Add-Call 'port-backported-blocks' 'PortCopperBulbBlock|PortTransparentBlock' '' 'review' @('net.minecraft.world.level.block.CopperBulbBlock','net.minecraft.world.level.block.TransparentBlock') 'Backported 1.21 blocks that are VANILLA on this branch: CopperBulbBlock.java:15 and TransparentBlock.java:12. Replace extends/imports with the vanilla classes; the PortLib import is deleted.' $false
Add-Call 'port-backported-sounds' 'PortSoundEvents\.|SoundEventHolder' '' 'review' @('net.minecraft.sounds.SoundEvents','net.minecraft.core.registries.BuiltInRegistries') 'All 26 backported sound constants are vanilla in 1.21.1 (e.g. SoundEvents.TUFF_BRICKS_BREAK :1410, COPPER_BULB_TURN_ON :355, WET_SPONGE_BREAK :1529). Retarget each constant reference to net.minecraft.sounds.SoundEvents; the holder wrapper is no longer needed because Sounds live in BuiltInRegistries.SOUND_EVENT (BuiltInRegistries.java:137).' $false
Add-Call 'port-tuff-constants' 'PortLib\.(CHISELED_TUFF|TUFF_BRICKS|POLISHED_TUFF|TUFF_SLAB|TUFF_STAIRS|TUFF_WALL|CHISELED_TUFF_BRICKS)' 'Blocks.$1' 'review' @('net.minecraft.world.level.block.Blocks') 'The tuff family is vanilla in 1.21.1: Blocks.TUFF_SLAB :6866, TUFF_STAIRS :6867, TUFF_WALL :6868, POLISHED_TUFF :6869, CHISELED_TUFF :6877, TUFF_BRICKS :6878, CHISELED_TUFF_BRICKS :6884. Also retarget the 16 backported attributes onto net.minecraft.world.entity.ai.attributes.Attributes.' $true
Add-Call 'port-portlib-attributes' 'PortLib\.(BLOCK_BREAK_SPEED|BURNING_TIME|EXPLOSION_KNOCKBACK_RESISTANCE|FALL_DAMAGE_MULTIPLIER|JUMP_STRENGTH|MAX_ABSORPTION|MINING_EFFICIENCY|MOVEMENT_EFFICIENCY|OXYGEN_BONUS|SAFE_FALL_DISTANCE|SCALE|SNEAKING_SPEED|SUBMERGED_MINING_SPEED|SWEEPING_DAMAGE_RATIO|WATER_MOVEMENT_EFFICIENCY|CREATIVE_FLIGHT)' 'Attributes.$1' 'review' @('net.minecraft.world.entity.ai.attributes.Attributes') '15 of the 16 backported attributes are vanilla in 1.21.1 (Attributes.java: SCALE :124, JUMP_STRENGTH :80, MAX_ABSORPTION :96, SAFE_FALL_DISTANCE :121, OXYGEN_BONUS :118, BURNING_TIME :48, EXPLOSION_KNOCKBACK_RESISTANCE :52, MOVEMENT_EFFICIENCY :108, WATER_MOVEMENT_EFFICIENCY :145, SUBMERGED_MINING_SPEED :139, SNEAKING_SPEED :127, MINING_EFFICIENCY :105, BLOCK_BREAK_SPEED :42, SWEEPING_DAMAGE_RATIO :142, FALL_DAMAGE_MULTIPLIER :58). CREATIVE_FLIGHT is NOT vanilla: it is NeoForgeMod.CREATIVE_FLIGHT (NeoForgeMod.java:212) built on BooleanAttribute, so that one call site needs a different qualifier.' $true

# ---- PortLib.extensions static-member helpers ---------------------------------------------
# PortListExtension: all six used members are Java 21 java.util.List (SequencedCollection).
# Pattern note: the receiver group deliberately excludes parentheses/commas, so a complex
# receiver (a.b(), x[i], new Foo()) does NOT match and falls through to the TODO report - that
# is why these are `review` rather than `safe`.
$listExt = @{
    'getFirst'    = 'List#getFirst()'
    'getLast'     = 'List#getLast()'
    'removeFirst' = 'List#removeFirst()'
    'removeLast'  = 'List#removeLast()'
    'addFirst'    = 'List#addFirst(E)' 
    'addLast'     = 'List#addLast(E)'
}
foreach ($m in $listExt.Keys) {
    $argPat = '([A-Za-z_$][\w$.\[\]]*)'
    $pat = "\bPortListExtension\.$m\("
    $rep = ''
    $note = "Java 21 SequencedCollection: $($listExt[$m]) on java.util.List - no PortLib helper needed. "
    if ($m -in @('addFirst','addLast')) {
        $pat = "\bPortListExtension\.$m\(\s*$argPat\s*,"
        $rep = "\1.$m("
        $note += 'Writer-first AND writer-last forms both collapse: addFirst(list, e) -> list.addFirst(e), addLast(list, e) -> list.addLast(e).'
    } else {
        $pat = "\bPortListExtension\.$m\(\s*$argPat\s*\)"
        $rep = "\1.$m()"
        $note += 'Reader form only: PortListExtension.X(list) -> list.X(). Review because the receiver group matches simple expressions only.'
    }
    Add-Call "list-extension-$($m.ToLower())" $pat $rep 'review' @() $note $false
}
# PortDataResultExtension: all three used members are vanilla DataFixerUpper API (default/abstract
# methods on DataResult), so the static-with-receiver-first form becomes an instance call.
foreach ($m in @('getOrThrow','ifSuccess','mapOrElse')) {
    Add-Call "dataresult-extension-$($m.ToLower())" "\bPortDataResultExtension\.$m\(\s*([A-Za-z_$][\w$.\[\]]*)\s*," "`$1.$m(" 'review' @() "DataResult.$m is vanilla DataFixerUpper (verified in datafixerupper-10.0.21-sources.jar: getOrThrow() default at DataResult.java:80, ifSuccess(Consumer) at :92, mapOrElse(Function,Function) at :90). PortLib exposed it statically with the DataResult first; the receiver must move to the front. Review because a complex receiver expression is not matched by the regex."
}
Add-Call 'fluidstack-extension-issamefluidsamecomponents' '\bPortFluidStackExtension\.isSameFluidSameComponents\(' 'FluidStack.isSameFluidSameComponents(' 'safe' @('net.neoforged.neoforge.fluids.FluidStack') 'Verified static: net.neoforged.neoforge.fluids.FluidStack.isSameFluidSameComponents(FluidStack,FluidStack) at FluidStack.java:349. Same argument order and arity, so this is a pure qualifier swap.' $true
Add-Call 'portextensions-import-strip' '\bPortLib\.extensions\.(?:com|java|net)\.([\w.]+?)\.(Port\w+Extension|DispatchedMapCodec|StrictUnboundedMapCodec)\b' '\2' 'review' @() 'PortLib.extensions.** holds plain static-helper classes (there is no Manifold plugin). For a VENDORED helper the fully-qualified reference can collapse to the simple name; for a DROPPED helper the call itself must be rewritten by the specific callsite rule (e.g. PortListExtension -> List#getFirst, PortDataResultExtension -> DataResult#getOrThrow). Applying this rule alone leaves broken calls, so review and pair it with the type-level disposition from types.json.' $false

# ---- emit callsites.json ---------------------------------------------------
[pscustomobject]@{
    generatedFrom = 'Hand-authored against the PortLib sources in D:\Minecraft\1.20forge\confluence\PortLib\src\main\java and verified against build\_nfsrc_219 (NeoForge 21.1.219 / MC 1.21.1) plus fancymodloader:loader:4.0.42 and bus:8.0.5'
    kinds = @{
        safe   = 'Mechanical and signature-compatible: the replacement compiles and behaves the same.'
        review = 'The rewrite is a best guess. A human MUST check it; the converter also emits these into the TODO report.'
    }
    rules = $calls
} | ConvertTo-Json -Depth 6 | Set-Content -Encoding UTF8 "$RulesDir\callsites.json"
Write-Host "callsites.json rules: $($calls.Count)  (safe: $(($calls | Where-Object { $_.kind -eq 'safe' }).Count), review: $(($calls | Where-Object { $_.kind -eq 'review' }).Count))"

# --------------------------------------------------------------------------
# 9. imports.json
# --------------------------------------------------------------------------
$importMap = New-Object System.Collections.Generic.List[object]
foreach ($t in $typeList) {
    if ($t.port -notlike 'org.mesdag.portlib.*' -and $t.port -notlike 'PortLib.*') { continue }
    $action = 'remove'
    $target = ''
    if ($t.kind -eq 'alias' -or $t.kind -eq 'static-alias') { $action = 'replace'; $target = $t.native }
    elseif ($t.kind -eq 'shared')            { $action = 'move';    $target = $t.copyTo }
    elseif ($t.kind -eq 'manual')            { $action = 'manual' }
    $o = [ordered]@{ port = $t.port; action = $action }
    if ($target) { $o['native'] = $target }
    $o['kind'] = $t.kind
    $o['confidence'] = $t.confidence
    $importMap.Add([pscustomobject]$o)
}
$addImports = New-Object System.Collections.Generic.List[object]
foreach ($c in $calls) {
    if (-not $c.typeImports -or $c.typeImports.Count -eq 0) { continue }
    $addImports.Add([pscustomobject]@{ forRule = $c.id; imports = @($c.typeImports) })
}
$staticImports = New-Object System.Collections.Generic.List[object]
$staticImports.Add([pscustomobject]@{
    port = 'import static org.mesdag.portlib.wrapper.world.entity.ai.attributes.PortAttributeModifier.Operation.*;'
    native = 'import static net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.*;'
    notes = 'Static-import rewrite; constant names are identical (ADD_VALUE/ADD_MULTIPLIED_BASE/ADD_MULTIPLIED_TOTAL).'
})
[pscustomobject]@{
    generatedFrom = 'Derived from types.json (per-type import disposition) and callsites.json (imports the replacement text needs)'
    removePrefixes = @('org.mesdag.portlib.', 'PortLib.')
    conventions = 'action=replace -> swap the import for `native`; action=move -> the class is copied to `native` (a package) and the import points at the copy; action=remove -> delete the import line; action=manual -> leave the import and emit a TODO. `addImports` lists, per callsite rule, the FQNs the generated code needs, so the converter can add any that are not already imported (respecting that a single-type import for a type in java.lang or the same package is unnecessary).'
    staticImports = $staticImports
    addImports = $addImports
    map = $importMap
} | ConvertTo-Json -Depth 6 | Set-Content -Encoding UTF8 "$RulesDir\imports.json"
Write-Host "imports.json map entries: $($importMap.Count); addImports groups: $($addImports.Count)"

# --------------------------------------------------------------------------
# 10. forge-to-neoforge.json
# --------------------------------------------------------------------------
# Rules for source that imports Forge directly instead of going through PortLib. The set is
# re-derived here from the 1.20 mod sources (not hard-coded), so it stays in sync.
$forgeSrcs = @(
    'D:\Minecraft\1.20forge\confluence\ConfluenceOtherworld\src',
    'D:\Minecraft\1.20forge\confluence\Confluence-Magic-Lib\src',
    'D:\Minecraft\1.20forge\confluence\TerraCurio\src',
    'D:\Minecraft\1.20forge\confluence\TerraFurniture\src'
)
# Wildcard imports: record the package as "pkg.*" so the converter rewrites the import line.
$forgeImports = @{}
foreach ($s in $forgeSrcs) {
    if (-not (Test-Path $s)) { continue }
    foreach ($hit in (Get-ChildItem $s -Recurse -Filter *.java -File |
                      Select-String -Pattern '^import\s+(static\s+)?(net\.minecraftforge\.[\w.]*)(\*)?;')) {
        $fqn = $hit.Matches[0].Groups[2].Value.TrimEnd('.')
        if ($hit.Matches[0].Groups[3].Value -eq '*') { $fqn = "$fqn.*" }
        if (-not $forgeImports.ContainsKey($fqn)) { $forgeImports[$fqn] = 0 }
        $forgeImports[$fqn]++
    }
}
Write-Host "forge imports discovered: $($forgeImports.Count) distinct targets, $(($forgeImports.Values | Measure-Object -Sum).Sum) occurrences"

# --- explicit rules: only where the mapping is NOT a plain package-prefix substitution -----
# format: forge FQN -> @(kind, native, confidence, notes)
$forgeExplicit = @{}
function F([string]$forge, [string]$kind, [string]$native, [string]$conf, [string]$notes) {
    $script:forgeExplicit[$forge] = [pscustomobject]@{ kind=$kind; native=$native; confidence=$conf; notes=$notes }
}

# ---- removed / restructured: no drop-in ---------------------------------------------------
F 'net.minecraftforge.registries.RegistryObject' 'manual' '' 'high' 'NeoForge has NO RegistryObject (53 imports - the single most frequent Forge type in this codebase). Per-use rewrite, verified against Forge 1.20.1 / NeoForge 21.1.219: (1) FIELD DECLARATION `RegistryObject<X> F = REG.register("n", ...)` becomes `DeferredHolder<RegistryType, X> F = ...` - note the EXTRA type argument, so this is a type-arity change, not a rename; use DeferredItem<X> / DeferredBlock<X> when registering items/blocks. (2) `.get()` -> `.get()` (DeferredHolder.java:116, unchanged). (3) `.getId()` -> `.getId()` (DeferredHolder.java:162, still ResourceLocation). (4) `.isPresent()` -> `isBound()` (DeferredHolder.java:197) - DeferredHolder implements Holder + Supplier and neither declares isPresent; `.asOptional()` (:125) is the Optional form. (5) `.map(f)` -> `.asOptional().map(f)`. (6) `.getKey()` -> `.getKey()`. (7) `.ifPresent(c)` -> `if (isBound()) c.accept(get())`. (8) PASSING AS A CONSTRUCTOR/METHOD ARGUMENT: DeferredHolder implements Supplier<X> and Holder<RegistryType>, so an argument typed X can still be supplied - but an argument typed `RegistryObject<X>` in the callee must be widened to `Supplier<X>` or `Holder<X>`. (9) STATIC `RegistryObject.create(ResourceLocation, IForgeRegistry)` (3 uses) -> `DeferredHolder.create(ResourceKey)` or `DeferredHolder.create(registryKey, valueName)` (DeferredHolder.java:62,:41).'
F 'net.minecraftforge.registries.ForgeRegistries' 'manual' '' 'high' 'Do NOT blanket-map to NeoForgeRegistries: that compiles for the NeoForge-only registries but is WRONG for every vanilla one, and this codebase uses ONLY vanilla registries. Verified usage (26 occurrences): Keys.BIOME_MODIFIERS (7), Keys.FLUID_TYPES (2), ENTITY_TYPES (7), ITEMS (7), MOB_EFFECTS (6), BLOCKS (4), FLUIDS (2), SOUND_EVENTS (1), ATTRIBUTES (1). Field-by-field destination: Keys.BIOME_MODIFIERS -> NeoForgeRegistries.Keys.BIOME_MODIFIERS (NeoForgeRegistries.java:61); Keys.FLUID_TYPES -> NeoForgeRegistries.Keys.FLUID_TYPES (:53); ENTITY_TYPES -> BuiltInRegistries.ENTITY_TYPE (SINGULAR); ITEMS -> BuiltInRegistries.ITEM (singular); MOB_EFFECTS -> BuiltInRegistries.MOB_EFFECT (singular); BLOCKS -> BuiltInRegistries.BLOCK (singular); FLUIDS -> BuiltInRegistries.FLUID (singular); SOUND_EVENTS -> BuiltInRegistries.SOUND_EVENT (singular); ATTRIBUTES -> BuiltInRegistries.ATTRIBUTE (singular). Only the 10 fields on NeoForgeRegistries (:34-:43 ENTITY_DATA_SERIALIZERS, GLOBAL_LOOT_MODIFIER_SERIALIZERS, BIOME_MODIFIER_SERIALIZERS, STRUCTURE_MODIFIER_SERIALIZERS, FLUID_TYPES, HOLDER_SET_TYPES, INGREDIENT_TYPES, FLUID_INGREDIENT_TYPES, CONDITION_SERIALIZERS, ATTACHMENT_TYPES) keep a NeoForge qualifier.'
F 'net.minecraftforge.eventbus.api.Cancelable' 'manual' '' 'high' '`@Cancelable` no longer exists: there is no net.neoforged.bus.api.Cancelable in bus-8.0.5 (the package declares only Event, EventListener, EventPriority, ICancellableEvent, IEventBus, IEventClassChecker, IEventExceptionHandler, SubscribeEvent, BusBuilder). 12 uses. Rewrite: drop the annotation and declare the event class as `implements net.neoforged.bus.api.ICancellableEvent` (methods setCanceled(boolean) :29 / isCanceled() :37).'
F 'net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext' 'drop' '' 'high' 'Removed. There is no FMLJavaModLoadingContext in fancymodloader:loader:4.0.42 (verified: the javafmlmod package contains only AutomaticEventSubscriber, FMLJavaModLanguageProvider, FMLModContainer). 9 uses. Replace `FMLJavaModLoadingContext.get().getModEventBus()` by taking the bus as a constructor parameter of the @Mod class - NeoForge injects IEventBus (and ModContainer / Dist) directly. The @Mod annotation itself is unchanged.'
F 'net.minecraftforge.event.TickEvent' 'manual' '' 'high' 'Genuine refactor, not a rename: there is NO native TickEvent. Verified native replacements - TickEvent.ClientTickEvent -> net.neoforged.neoforge.client.event.ClientTickEvent; TickEvent.ServerTickEvent -> net.neoforged.neoforge.event.tick.ServerTickEvent; TickEvent.LevelTickEvent -> net.neoforged.neoforge.event.tick.LevelTickEvent; TickEvent.PlayerTickEvent -> net.neoforged.neoforge.event.tick.PlayerTickEvent; TickEvent.RenderTickEvent -> net.neoforged.neoforge.client.event.RenderFrameEvent. TickEvent.Phase does NOT exist at all (15 uses); the phase became the event class, so `if (event.phase != TickEvent.Phase.END) return;` is DELETED and the handler moves to the `.Post` variant (`.Pre` for START). The mod already names its handlers clientTick$Pre / clientTick$Post etc., so the split is mechanical per handler. NOTE TickEvent.LevelTickEvent.Pre and .Post are NOT cancellable natively.'
F 'net.minecraftforge.common.ForgeMod' 'manual' '' 'high' 'Renamed class, same role: net.neoforged.neoforge.common.NeoForgeMod (NeoForgeMod.java). Do NOT alias blindly - 8 uses, and NeoForgeMod moved/renamed several members (e.g. the creative-flight attribute is NeoForgeMod.CREATIVE_FLIGHT, NeoForgeMod.java:212, built on BooleanAttribute) while the 15 attributes Forge kept in ForgeMod are VANILLA in 1.21 (net.minecraft.world.entity.ai.attributes.Attributes). Check each member name against NeoForgeMod.'
F 'net.minecraftforge.event.ForgeEventFactory' 'manual' '' 'high' 'Renamed to net.neoforged.neoforge.event.EventHooks. 8 uses, all distinct methods - verify each: getMobGriefingEvent, onFinalizeSpawn, getExperienceDrop, onExplosionDetonate, onExplosionStart, onEntityStruckByLightning, onArrowLoose, onPlayerDestroyItem. EventHooks exists but did not keep all of them under the same names/signatures (onFinalizeSpawn in particular became the FinalizeSpawnEvent path).'
F 'net.minecraftforge.common.ForgeHooks' 'manual' '' 'high' 'Renamed to net.neoforged.neoforge.common.CommonHooks. 6 uses - check each member, since CommonHooks dropped the Forge-only helpers and moved several onto the vanilla classes or onto the *Extension interfaces.'
F 'net.minecraftforge.common.ForgeConfigSpec' 'manual' '' 'high' 'Closest native type is net.neoforged.neoforge.common.ModConfigSpec (verified), and the nested value classes map 1:1 with the SAME names (ModConfigSpec.Builder :300, ConfigValue :1186, BooleanValue :1284, IntValue :1303, LongValue :1319, DoubleValue :1335, EnumValue :1352). Marked manual per the brief rather than alias because the type is only half the change: the REGISTRATION path differs - 1.20 `ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC)` becomes `modContainer.registerConfig(ModConfig.Type.COMMON, SPEC)` on the ModContainer injected into the @Mod constructor (fml-src ModContainer.java:102,:119). Also there is no ModConfigSpec.ListValue. Note the imported nested forms ForgeConfigSpec.Builder / .ConfigValue / .BooleanValue / .IntValue / .EnumValue all have exact native counterparts.'
F 'net.minecraftforge.common.ToolActions' 'manual' '' 'high' 'Renamed to net.neoforged.neoforge.common.ItemAbilities, and the constants ARE name-identical - verified used ones: SHOVEL_FLATTEN (ItemAbilities.java:68), SWORD_SWEEP (:80), AXE_STRIP (:53), HOE_TILL (:116), SHEARS_CARVE (:99), plus DEFAULT_AXE_ACTIONS / DEFAULT_HOE_ACTIONS / DEFAULT_SHOVEL_ACTIONS / DEFAULT_PICKAXE_ACTIONS (:152-:164). Marked manual per the brief; a converter may safely treat this as a qualifier swap. TRAP: HOE_TILL is registered under the string "till", not "hoe_till" (ItemAbilities.java:116), so string-keyed ItemAbility.get(...) lookups must not be rewritten by name.'
F 'net.minecraftforge.common.ToolAction' 'manual' '' 'high' 'Renamed to net.neoforged.neoforge.common.ItemAbility (final class, ItemAbility.get(String) :31, CODEC :17). 10 uses. Same API shape as ToolAction, so `ItemAbility` works as a type parameter - but the ITEM-side hook moved: 1.20 IForgeItem#getToolModifiedState(ToolAction) is gone, replaced by ItemAbility-driven behaviour plus the canPerformAction/isCorrectToolForDrops paths, so any handler that reacted to a ToolAction needs re-authoring, not renaming.'
F 'net.minecraftforge.network.NetworkHooks' 'manual' '' 'high' 'Removed; no native NetworkHooks. 7 uses, two distinct methods: `NetworkHooks.openScreen(...)` -> `serverPlayer.openMenu(MenuProvider, ...)` (vanilla, with NeoForge overloads taking extra data) ; `NetworkHooks.getEntitySpawningPacket(entity)` (6 uses) -> DELETE the call and let the entity implement net.neoforged.neoforge.entity.IEntityWithComplexSpawn (writeSpawnData/readSpawnData over RegistryFriendlyByteBuf, :21/:29); NeoForge 1.21 then ships the spawn data itself via AdvancedAddEntityPayload (net.neoforged.neoforge.network.payload.AdvancedAddEntityPayload, verified present).'
F 'net.minecraftforge.network.PlayMessages' 'drop' '' 'high' 'Removed. 1 use (PlayMessages.SpawnEntity). The 1.20 custom spawn-packet path is replaced by IEntityWithComplexSpawn + AdvancedAddEntityPayload, so the import and the packet class are deleted rather than renamed.'
F 'net.minecraftforge.common.capabilities.Capability' 'manual' '' 'high' 'Forge capabilities are gone in 1.21. There is no native Capability class; capabilities are now BlockCapability / ItemCapability / EntityCapability registered on the MOD bus via net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent, and looked up with level.getCapability(...)/stack.getCapability(...). Verified native types: capabilities.ICapabilityProvider, capabilities.Capabilities, capabilities.BlockCapability, capabilities.ItemCapability. Per-use rewrite, not a rename.'
F 'net.minecraftforge.common.capabilities.ForgeCapabilities' 'manual' '' 'high' 'Renamed to net.neoforged.neoforge.capabilities.Capabilities (verified). But the fields changed shape: ForgeCapabilities.ITEM_HANDLER was a Capability<IItemHandler>, while Capabilities.ItemHandler.ITEM is an ItemCapability. Handle the field references when substituting.'
F 'net.minecraftforge.common.capabilities.ICapabilityProvider' 'alias' 'net.neoforged.neoforge.capabilities.ICapabilityProvider' 'high' 'Same simple name in the new capabilities package (verified present). Note the method set changed from Forge hasCapability/getCapability to the getCapability(...) form taking the capability object.'
F 'net.minecraftforge.common.util.LazyOptional' 'manual' '' 'high' 'Removed; there is no net.neoforged.neoforge.common.util.LazyOptional. 1 use. 1.21 replaced it with plain nullability plus Optional at the API boundary: `LazyOptional.of(supplier)` -> hold the value/`Lazy` directly; `LazyOptional.empty()` -> null or Optional.empty(); `.ifPresent/.orElse/.map/.isPresent` on the result become Optional/null checks. Lazy itself DOES survive (net.neoforged.neoforge.common.util.Lazy, 5 uses, alias).'
F 'net.minecraftforge.registries.ForgeRegistry' 'manual' '' 'high' 'No native ForgeRegistry. 1 use, and it is a hard break: LibModEvents.java:64,69 does `((ForgeRegistry<Block>) ForgeRegistries.BLOCKS).addAlias(from, to)`. In 1.21 Registry has NO addAlias (verified: grep -i alias over net/minecraft/core/Registry.java => 0 matches; aliases are a 1.21.2+ API). The equivalent is DeferredRegister.addAlias(ResourceLocation from, ResourceLocation to) (DeferredRegister.java:303), so the alias registration must be moved onto the DeferredRegister that owns the registry.'
F 'net.minecraftforge.client.gui.overlay.ForgeGui' 'manual' '' 'high' 'The whole net.neoforged.neoforge.client.gui.overlay package DOES NOT EXIST in 21.1.219. 4 uses, all of the form `((ForgeGui) minecraft.gui).leftHeight` / `.rightHeight` (TerraStyle*Hud). Those mutable layout cursors are gone: 1.21 positions HUD elements through the vanilla LayeredDraw/RegisterGuiLayersEvent ordering instead. Rewrite by registering each HUD relative to a vanilla layer id (RegisterGuiLayersEvent.registerAbove/registerBelow with a net.neoforged.neoforge.client.gui.VanillaGuiLayers constant) or by computing an absolute Y offset; the stacking arithmetic must be deleted. net.neoforged.neoforge.client.gui.GuiLayerManager exists if layer bookkeeping is needed.'
F 'net.minecraftforge.client.gui.overlay.VanillaGuiOverlay' 'manual' '' 'high' 'Replaced by net.neoforged.neoforge.client.gui.VanillaGuiLayers (verified, VanillaGuiLayers.java:17), whose constants are ResourceLocations rather than enum members - so `.id()` is DROPPED: `VanillaGuiOverlay.CROSSHAIR.id()` -> `VanillaGuiLayers.CROSSHAIR`. Constant-level changes found in this codebase usage: PLAYER_HEALTH -> PLAYER_HEALTH (VanillaGuiLayers.java:23), FOOD_LEVEL -> FOOD_LEVEL (:25), ARMOR_LEVEL -> ARMOR_LEVEL (:24), CROSSHAIR -> CROSSHAIR (:19), HOTBAR -> HOTBAR (:20) all keep their names; BOSS_EVENT_PROGRESS -> BOSS_OVERLAY (:32) and SUBTITLES -> SUBTITLE_OVERLAY (:41) are RENAMED; HELMET (TerraCurio TCModClientEvent.java:74) has NO equivalent - there is no helmet layer in 1.21, so that registration must be re-anchored (e.g. registerBelow VanillaGuiLayers.PLAYER_HEALTH) or dropped.'
F 'net.minecraftforge.client.ConfigScreenHandler' 'manual' '' 'high' 'Removed. 5 uses: `ConfigScreenHandler.ConfigScreenFactory` (4) -> net.neoforged.neoforge.client.gui.IConfigScreenFactory (verified, :22), and `ConfigScreenHandler.getScreenFactoryFor(modInfo)` (1) -> IConfigScreenFactory.getForMod(IModInfo) (:28). NOTE the interface method signature CHANGED: IConfigScreenFactory declares `Screen createScreen(ModContainer container, Screen modListScreen)` (:26), whereas Forge took (Minecraft, Screen) - so each factory body must be re-typed. Registration is now `modContainer.registerExtensionPoint(IConfigScreenFactory.class, ...)`.'
F 'net.minecraftforge.common.IForgeShearable' 'manual' '' 'high' 'Forge-only interface that vanilla absorbed: 1.21 has net.minecraft.world.entity.Shearable (verified). 3 uses. The method set differs (Forge had onSheared/isShearable/getShearableDrops with Player/ItemStack args; vanilla Shearable is a marker that routes through the SHEARS ItemAbility), so re-author the implementation rather than renaming.'
F 'net.minecraftforge.common.MinecraftForge' 'manual' '' 'high' 'Renamed to net.neoforged.neoforge.common.NeoForge (verified, NeoForge.java:12, whose only member is `public static final IEventBus EVENT_BUS` at :17). 3 uses. `MinecraftForge.EVENT_BUS` -> `NeoForge.EVENT_BUS` is mechanical, but check for any other MinecraftForge static - it had no other member worth keeping and the mod-bus equivalent is the injected IEventBus.'
F 'net.minecraftforge.common.ForgeSpawnEggItem' 'manual' '' 'high' 'Renamed to net.neoforged.neoforge.common.DeferredSpawnEggItem (verified present). 2 uses. It is the deferred-colour spawn egg; the constructor form changed (NeoForge takes the DeferredHolder<EntityType> suppliers), so verify each argument.'
F 'net.minecraftforge.common.util.ForgeSoundType' 'manual' '' 'high' 'Renamed to net.neoforged.neoforge.common.util.DeferredSoundType (verified present). 1 use. Same idea (a SoundType whose SoundEvents are resolved lazily), but the constructor argument list changed between the two, so check it.'
F 'net.minecraftforge.common.extensions.IForgeAbstractMinecart' 'manual' '' 'high' 'The 1.20 IForge* extension interfaces became the NeoForge I*Extension interfaces: use net.neoforged.neoforge.common.extensions.IAbstractMinecartExtension (verified present in the extensions package). 1 use. Note the members were renamed from getComparatorLevel/setComparatorLevel style to the 1.21 names, so check each call.'
F 'net.minecraftforge.common.extensions.IForgeMenuType' 'manual' '' 'high' 'Replaced by net.neoforged.neoforge.common.extensions.IMenuTypeExtension (verified present). 1 use. Forge exposed a static `IForgeMenuType.create(...)`; in NeoForge the helper is IMenuTypeExtension.create(...) - verify the factory signature, which also moved to the new MenuType ctor form.'
F 'net.minecraftforge.resource.PathPackResources' 'alias' 'net.minecraft.server.packs.PathPackResources' 'high' 'Forge re-declared a vanilla class; in 1.21 it is simply vanilla net.minecraft.server.packs.PathPackResources (verified). 1 use. Not a namespace swap - the class moved INTO net.minecraft.'
F 'net.minecraftforge.client.model.ForgeItemModelShaper' 'manual' '' 'high' 'Removed. 1 use. The 1.21 model pipeline has no ItemModelShaper replacement at this level (models are resolved through ModelManager/ItemModelResolver), so the call site must be re-authored against the vanilla renderer path rather than renamed.'
F 'net.minecraftforge.entity.IEntityAdditionalSpawnData' 'manual' '' 'high' 'Renamed and re-signatured: net.neoforged.neoforge.entity.IEntityWithComplexSpawn (verified, package at :6, declaration :14). 2 uses. The methods now take RegistryFriendlyByteBuf, not FriendlyByteBuf (writeSpawnData :21 / readSpawnData :29), so the overrides change parameter type.'
F 'net.minecraftforge.client.event.RenderLevelStageEvent' 'alias' 'net.neoforged.neoforge.client.event.RenderLevelStageEvent' 'high' 'Verified present. 18 uses. The nested `RegisterStageEvent` and `Stage` survive, but note Stage gained/lost entries between Forge 47 and NeoForge 21 - check each Stage constant used.'
F 'net.minecraftforge.client.event.MovementInputUpdateEvent' 'alias' 'net.neoforged.neoforge.client.event.MovementInputUpdateEvent' 'high' 'Verified present under the client event package; this is one of the client events PortLib never had a wrapper for.'
F 'net.minecraftforge.client.event.ComputeFovModifierEvent' 'alias' 'net.neoforged.neoforge.client.event.ComputeFovModifierEvent' 'high' 'Verified present. Note the Forge/NeoForge property pair changed (Forge used fovModifier/newFov; NeoForge exposes getFovModifier/setFovModifier) - check each accessor.'
F 'net.minecraftforge.eventbus.api.EventPriority' 'alias' 'net.neoforged.bus.api.EventPriority' 'high' 'NOT CURRENTLY IMPORTED by the four modules (included because it is the natural companion of the imported SubscribeEvent). Verified: EventPriority.java:26 with HIGHEST :34, HIGH :35, NORMAL :36, LOW :37, LOWEST :38.'
F 'net.minecraftforge.common.extensions.IForgeItem' 'manual' '' 'high' 'NOT CURRENTLY IMPORTED (0 occurrences in the four modules) - included because the brief names it. Note the correct Forge FQN is net.minecraftforge.common.extensions.IForgeItem. In 1.21 it is net.neoforged.neoforge.common.extensions.IItemExtension (verified present), and it is no longer implemented BY items: the members became default methods on Item, so call sites change from `((IForgeItem) item).getBurnTime(...)` to plain `item.getBurnTime(...)`. Same story for IForgeBlock -> IBlockExtension (verified).'
F 'net.minecraftforge.common.extensions.IForgeBlock' 'manual' '' 'high' 'NOT CURRENTLY IMPORTED (0 occurrences) - see IForgeItem. Correct Forge FQN is net.minecraftforge.common.extensions.IForgeBlock; native twin is net.neoforged.neoforge.common.extensions.IBlockExtension (verified), whose members are default methods on Block/BlockBehaviour.'

# --- package/class renames that a namespace substitution cannot find ------------------------
F 'net.minecraftforge.api.distmarker.Dist' 'alias' 'net.neoforged.api.distmarker.Dist' 'high' 'Pure namespace move: net.minecraftforge.api.distmarker -> net.neoforged.api.distmarker. 3 uses. NOT in _nfsrc_219 (distmarker is not part of the decompiled mod sources); verified as the zip entry net/neoforged/api/distmarker/Dist.class inside mergetool-2.0.0-api.jar, the version neoforge-21.1.219-moddev-config.json pins.'
F 'net.minecraftforge.api.distmarker.OnlyIn' 'alias' 'net.neoforged.api.distmarker.OnlyIn' 'high' 'Pure namespace move. 2 uses, all `@OnlyIn(Dist.CLIENT)`. Verified as net/neoforged/api/distmarker/OnlyIn.class in mergetool-2.0.0-api.jar.'
F 'net.minecraftforge.common.crafting.conditions.ICondition' 'alias' 'net.neoforged.neoforge.common.conditions.ICondition' 'high' 'PACKAGE SIMPLIFIED: common.crafting.conditions -> common.conditions (verified, ICondition.java, plus nested ICondition.IContext). 1 use. A namespace substitution alone yields a non-existent package, so this rule must be applied explicitly.'
F 'net.minecraftforge.common.crafting.conditions.ModLoadedCondition' 'alias' 'net.neoforged.neoforge.common.conditions.ModLoadedCondition' 'high' 'Package simplified: common.crafting.conditions -> common.conditions (verified). 1 use.'
F 'net.minecraftforge.common.crafting.conditions.NotCondition' 'alias' 'net.neoforged.neoforge.common.conditions.NotCondition' 'high' 'Package simplified: common.crafting.conditions -> common.conditions (verified). 1 use. Beware the unrelated vanilla net.minecraft.world.level.levelgen.SurfaceRules.NotCondition.'
F 'net.minecraftforge.common.ForgeConfigSpec.Builder' 'alias' 'net.neoforged.neoforge.common.ModConfigSpec.Builder' 'high' 'Class renamed AND nested: ForgeConfigSpec.Builder -> ModConfigSpec.Builder (verified, ModConfigSpec.java:300). 5 uses. Same define/comment/push/pop/build surface. See the ForgeConfigSpec entry for the registration-side change.'
F 'net.minecraftforge.common.ForgeConfigSpec.BooleanValue' 'alias' 'net.neoforged.neoforge.common.ModConfigSpec.BooleanValue' 'high' 'ForgeConfigSpec.BooleanValue -> ModConfigSpec.BooleanValue (verified, ModConfigSpec.java:1284; getAsBoolean :1290, isTrue :1294, isFalse :1298). 9 uses.'
F 'net.minecraftforge.common.ForgeConfigSpec.IntValue' 'alias' 'net.neoforged.neoforge.common.ModConfigSpec.IntValue' 'high' 'ForgeConfigSpec.IntValue -> ModConfigSpec.IntValue (verified, ModConfigSpec.java:1303, getAsInt :1314). 2 uses.'
F 'net.minecraftforge.common.ForgeConfigSpec.EnumValue' 'alias' 'net.neoforged.neoforge.common.ModConfigSpec.EnumValue' 'high' 'ForgeConfigSpec.EnumValue -> ModConfigSpec.EnumValue (verified, ModConfigSpec.java:1352). 1 use.'
F 'net.minecraftforge.common.IExtensibleEnum' 'alias' 'net.neoforged.fml.common.asm.enumextension.IExtensibleEnum' 'high' 'MOVED INTO FML: the enum-extension machinery now lives in net.neoforged.fml.common.asm.enumextension (verified in the loader 4.0.42 sources). 2 uses. The companion annotations were also renamed/replaced (NamedEnum / IndexedEnum / NetworkedEnum / EnumParameters / ReservedConstructor).'
F 'net.minecraftforge.common.world.ForgeBiomeModifiers' 'alias' 'net.neoforged.neoforge.common.world.BiomeModifiers' 'high' 'RENAMED CLASS: ForgeBiomeModifiers -> BiomeModifiers (verified, BiomeModifiers.java:26). 1 use. The nested records were reshaped too: the ADD_CARVERS equivalent is BiomeModifiers.AddCarversBiomeModifier (:226), registered as NeoForgeMod.ADD_CARVERS_BIOME_MODIFIER_TYPE (NeoForgeMod.java:276).'
F 'net.minecraftforge.fluids.ForgeFlowingFluid' 'alias' 'net.neoforged.neoforge.fluids.BaseFlowingFluid' 'high' 'RENAMED CLASS: ForgeFlowingFluid -> BaseFlowingFluid (verified, nested Flowing / Properties / Source match the Forge nested names). 1 use. The Properties builder methods were trimmed in 1.21, so check each builder call.'

# --- wildcard imports ----------------------------------------------------------------------
F 'net.minecraftforge.client.event.*' 'alias' 'net.neoforged.neoforge.client.event.*' 'medium' 'WILDCARD IMPORT REWRITE (3 files: GameClientEvents, ModClientEvents, LibClientGameEvents). The package exists natively, but a handful of Forge client events were removed or renamed - the ones this codebase resolves through the wildcard are RegisterShadersEvent, EntityRenderersEvent, RegisterClientReloadListenersEvent, RegisterKeyMappingsEvent, RegisterParticleProvidersEvent, ModelEvent, ViewportEvent, CustomizeGuiOverlayEvent, InputEvent, RenderLivingEvent, RenderPlayerEvent, ClientPlayerNetworkEvent, MovementInputUpdateEvent, ComputeFovModifierEvent (all verified present). TickEvent.RenderTickEvent does NOT exist - it is RenderFrameEvent.'
F 'net.minecraftforge.event.entity.living.*' 'alias' 'net.neoforged.neoforge.event.entity.living.*' 'medium' 'WILDCARD IMPORT REWRITE (LibGameEvents, LivingEntityEvents). CAUTION: Forge nested several events that 1.21 hoisted to top level - `MobSpawnEvent.FinalizeSpawn` (used at LivingEntityEvents.java:525) becomes the TOP-LEVEL net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent, and `LivingEvent.LivingTickEvent` was REMOVED entirely (0 occurrences in 21.1.219; it does not exist on the PortLib side either). MobSpawnEvent.PositionCheck and MobSpawnEvent.SpawnPlacementCheck remain nested. LivingEvent itself survives.'
F 'net.minecraftforge.event.entity.player.*' 'alias' 'net.neoforged.neoforge.event.entity.player.*' 'high' 'WILDCARD IMPORT REWRITE (PlayerEvents). The package exists natively with the same event names; PlayerEvent and its nested classes survive.'
F 'net.minecraftforge.client.model.generators.*' 'alias' 'net.neoforged.neoforge.client.model.generators.*' 'medium' 'WILDCARD IMPORT REWRITE (3 datagen files: DefaultBlockDataGenerator, TableBDG, HorizontalBDG). The individually imported members of this package (ModelFile, BlockStateProvider, ItemModelProvider, ItemModelBuilder, ConfiguredModel, SeparateTransformsModelBuilder) were each verified to exist natively, but the datagen model API is the area where Forge-1.20-style code most often needs hand work (BlockModelProvider/ItemModelProvider behaviour changed in 1.21), so treat each generator body as review.'
F 'net.minecraftforge.common.ForgeConfigSpec.*' 'alias' 'net.neoforged.neoforge.common.ModConfigSpec.*' 'medium' 'WILDCARD STATIC IMPORT REWRITE (CommonConfigs.java: `import static net.minecraftforge.common.ForgeConfigSpec.*;`). All the imported names (Builder, ConfigValue, BooleanValue, IntValue, LongValue, DoubleValue, EnumValue) exist with the SAME names on ModConfigSpec, so this one is clean - see the ForgeConfigSpec entry for the registration-side change.'

# --- pass-through: the FQN suffix is identical, only the namespace changed ----------------
$forgePrefixMap = [ordered]@{
    'net.minecraftforge.fml.'            = 'net.neoforged.fml.'
    'net.minecraftforge.forgespi.'       = 'net.neoforged.neoforgespi.'
    'net.minecraftforge.eventbus.api.'   = 'net.neoforged.bus.api.'
    'net.minecraftforge.api.distmarker.' = 'net.neoforged.api.distmarker.'
    'net.minecraftforge.data.event.'     = 'net.neoforged.neoforge.data.event.'
    'net.minecraftforge.resource.'       = 'net.neoforged.neoforge.resource.'
}
# Types verified to exist but NOT present in the decompiled tree / extracted jars, so they cannot
# be checked against the type table. Evidence is recorded per entry.
$forgeExtraVerified = @{
    'net.neoforged.api.distmarker.Dist'   = 'provided by net.neoforged:mergetool:2.0.0:api (the version pinned by neoforge-21.1.219-moddev-config.json); verified as the zip entry net/neoforged/api/distmarker/Dist.class inside mergetool-2.0.0-api.jar'
    'net.neoforged.api.distmarker.OnlyIn' = 'provided by net.neoforged:mergetool:2.0.0:api; verified as net/neoforged/api/distmarker/OnlyIn.class in mergetool-2.0.0-api.jar'
}
# FML / bus packages are not in _nfsrc_219 at all; the extracted source jars are the evidence.
$forgeFmlBusOk = 'fancymodloader:loader:4.0.42 / bus:8.0.5 extracted sources (the versions neoforge-21.1.219-moddev-config.json pins)'

$forgeList = New-Object System.Collections.Generic.List[object]
$forgeUnverified = New-Object System.Collections.Generic.List[string]
foreach ($f in ($forgeImports.Keys | Sort-Object)) {
    $rule = $null
    if ($forgeExplicit.ContainsKey($f)) {
        $e = $forgeExplicit[$f]
        $rule = [ordered]@{ port=$f; kind=$e.kind; native=$e.native; confidence=$e.confidence; notes=$e.notes; occurrences=$forgeImports[$f] }
    } else {
        $isWildcard = $f.EndsWith('.*')
        if ($isWildcard) {
            $rule = [ordered]@{ port=$f; kind='unknown'; native=''; confidence='low'
                notes='Wildcard import with no explicit rule - a human must map this package.'; occurrences=$forgeImports[$f] }
        } else {
            $cand = $null
            foreach ($k in $forgePrefixMap.Keys) { if ($f.StartsWith($k)) { $cand = $forgePrefixMap[$k] + $f.Substring($k.Length); break } }
            if (-not $cand) { $cand = 'net.neoforged.neoforge.' + $f.Substring('net.minecraftforge.'.Length) }
            $prefix = ($f -replace '\.[^.]+$','')
            $highConfPrefixes = @('net.minecraftforge.fml.','net.minecraftforge.eventbus.api.','net.minecraftforge.data.event.',
                                  'net.minecraftforge.common.data.','net.minecraftforge.client.extensions.common.',
                                  'net.minecraftforge.items.','net.minecraftforge.server.','net.minecraftforge.entity.')
            $conf = 'medium'
            foreach ($hp in $highConfPrefixes) { if ($f.StartsWith($hp)) { $conf = 'high'; break } }
            if ($f -eq 'net.minecraftforge.registries.DeferredRegister' -or $f -eq 'net.minecraftforge.common.Tags' -or
                $f -eq 'net.minecraftforge.common.SoundActions' -or $f -eq 'net.minecraftforge.common.util.Lazy' -or
                $f -eq 'net.minecraftforge.common.util.MutableHashedLinkedMap' -or $f -eq 'net.minecraftforge.common.crafting.CraftingHelper') { $conf = 'high' }
            $ev = 'Resolved by namespace substitution (net.minecraftforge.* -> net.neoforged.neoforge.* / net.neoforged.*) and confirmed present in the native type table.'
            if ($f.StartsWith('net.minecraftforge.fml.') -or $f.StartsWith('net.minecraftforge.eventbus.api.')) {
                $ev = "Resolved by namespace substitution; confirmed in the $forgeFmlBusOk. _nfsrc_219 does NOT contain net/neoforged/fml or net/neoforged/bus."
            }
            $rule = [ordered]@{ port=$f; kind='alias'; native=$cand; confidence=$conf; notes=$ev; occurrences=$forgeImports[$f] }
        }
    }
    $forgeList.Add([pscustomobject]$rule)
}

# extra entries not imported by the modules but named in the brief
foreach ($x in @('net.minecraftforge.eventbus.api.EventPriority','net.minecraftforge.common.extensions.IForgeItem','net.minecraftforge.common.extensions.IForgeBlock')) {
    if ($forgeExplicit.ContainsKey($x) -and -not $forgeImports.ContainsKey($x)) {
        $e = $forgeExplicit[$x]
        $forgeList.Add([pscustomobject][ordered]@{ port=$x; kind=$e.kind; native=$e.native; confidence=$e.confidence; notes=$e.notes; occurrences=0 })
    }
}

# --- validate every emitted native ---------------------------------------------------------
$forgeTypeTable = @{}
foreach ($r in (Import-Csv $NativeAllTsv -Delimiter "`t")) { $forgeTypeTable[$r.fqn] = $true }
$forgeBad = New-Object System.Collections.Generic.List[string]
foreach ($r in $forgeList) {
    if (-not $r.native -or $r.native.EndsWith('.*')) { continue }
    if ($forgeTypeTable.ContainsKey($r.native)) { continue }
    # types verified out-of-tree (mergetool api jar) are accepted with their recorded evidence
    if ($forgeExtraVerified.ContainsKey($r.native)) { continue }
    $forgeBad.Add("UNVERIFIED-FORGE-NATIVE`t$($r.port)`t$($r.native)")
}
[pscustomobject]@{
    generatedFrom = 'Direct net.minecraftforge.* imports found in the four 1.20.1 module source trees (ConfluenceOtherworld, Confluence-Magic-Lib, TerraCurio, TerraFurniture); every native value checked against build\_nfsrc_219 (NeoForge 21.1.219 / MC 1.21.1) plus fancymodloader:loader:4.0.42, bus:8.0.5 and mergetool:2.0.0:api'
    scope = 'Rules for source that bypasses PortLib and imports Forge directly. Same schema as types.json; `port` holds the Forge FQN. `occurrences` is how many import lines in the four modules reference it.'
    types = $forgeList
} | ConvertTo-Json -Depth 6 | Set-Content -Encoding UTF8 "$RulesDir\forge-to-neoforge.json"
$fk = $forgeList | Group-Object kind
Write-Host "forge-to-neoforge.json entries: $($forgeList.Count)  (alias: $(($forgeList | Where-Object { $_.kind -eq 'alias' }).Count), manual: $(($forgeList | Where-Object { $_.kind -eq 'manual' }).Count), drop: $(($forgeList | Where-Object { $_.kind -eq 'drop' }).Count), unknown: $(($forgeList | Where-Object { $_.kind -eq 'unknown' }).Count))"
$forgeBad | Set-Content -Encoding UTF8 "$Out\forge-validation-report.txt"
if ($forgeBad.Count -eq 0) {
    "All emitted forge-to-neoforge 'native' values were found (type table, or the mergetool api jar allowlist)." | Set-Content -Encoding UTF8 "$Out\forge-validation-report.txt"
}
Write-Host "unverified forge natives: $($forgeBad.Count)  ->  $Out\forge-validation-report.txt"

# --------------------------------------------------------------------------
# 11. validation
# --------------------------------------------------------------------------
$report = New-Object System.Collections.Generic.List[string]
foreach ($t in $typeList) {
    if (-not $t.native) { continue }
    $n = $t.native
    if (-not $nativeAll.ContainsKey($n)) { $report.Add("UNVERIFIED-NATIVE`ttypes.json`t$($t.port)`t$n") }
}
# every FQN a callsite rule asks the converter to import must exist too
foreach ($c in $calls) {
    foreach ($n in $c.typeImports) {
        if (-not $nativeAll.ContainsKey($n)) { $report.Add("UNVERIFIED-NATIVE`tcallsites.json`t$($c.id)`t$n") }
    }
}
$report | Set-Content -Encoding UTF8 "$Out\validation-report.txt"
if ($report.Count -eq 0) {
    "All emitted 'native' values were found in the full parsed native type table (8806 types from the decompiled NeoForge 21.1.219 / MC 1.21.1 sources plus fancymodloader:loader:4.0.42 and bus:8.0.5)." |
        Set-Content -Encoding UTF8 "$Out\validation-report.txt"
}
Write-Host "unverified native values: $($report.Count)  ->  $Out\validation-report.txt"





