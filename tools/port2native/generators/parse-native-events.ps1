# parse-native-events.ps1  (fast, line-based)
# Builds the native event-class -> event-bus table for NeoForge 21.1.219 / MC 1.21.1.
# Bus = "mod" when the type (transitively) implements net.neoforged.fml.event.IModBusEvent,
#       "game" otherwise.
#
# Sources:
#   * _nfsrc_219  (decompiled MC 1.21.1 + NeoForge 21.1.219: only net/minecraft/**, net/neoforged/neoforge/**)
#   * fml-src     (net.neoforged.fancymodloader:loader:4.0.42 sources - pinned by
#                  neoforge-21.1.219-moddev-config.json)
#   * bus-src     (net.neoforged:bus:8.0.5 sources - ditto)
#
# Output: out/native-event-bus.tsv   columns: fqn, kind, modbus, file

param(
    [string]$Nfsrc  = 'D:\Minecraft\1.21neoforge\confluence\build\_nfsrc_219',
    [string]$FmlSrc = 'D:\Minecraft\1.21neoforge\confluence\notes\_tmp\fml-src',
    [string]$BusSrc = 'D:\Minecraft\1.21neoforge\confluence\notes\_tmp\bus-src',
    [string]$Out    = "$PSScriptRoot\out",
    [switch]$AllTrees
)
$ErrorActionPreference = 'Stop'
New-Item -ItemType Directory -Force -Path $Out | Out-Null

# --- which subtrees can hold event classes -------------------------------
$subtreeFilters = @(
    '\\event\\', '\\capabilities\\', '\\registries\\', '\\extensions\\', '\\network\\', '\\data\\',
    '\\common\\'
)
if ($AllTrees) { $subtreeFilters = @('\\') }   # whole tree: used to build the validation universe

$declRegex = [regex]'^\s*(?:(?:public|protected|private|abstract|final|sealed|non-sealed|static|strictfp)\s+)*(?<kind>@interface|class|interface|enum|record)\s+(?<name>[A-Za-z_]\w*)\s*(?<rest>.*)$'
$pkgRegex  = [regex]'^\s*package\s+([\w\.]+)\s*;'

# --- collect files -------------------------------------------------------
$files = New-Object System.Collections.Generic.List[object]
foreach ($f in Get-ChildItem $Nfsrc -Recurse -Filter *.java -File) {
    $rel = $f.FullName.Substring($Nfsrc.Length + 1)
    $keep = $false
    foreach ($sf in $subtreeFilters) { if ($rel -match $sf) { $keep = $true; break } }
    if ($keep) { $files.Add([pscustomobject]@{ path = $f.FullName; rel = "nfsrc:$($rel -replace '\\','/')" }) }
}
foreach ($root in @($FmlSrc, $BusSrc)) {
    if (-not (Test-Path $root)) { continue }
    foreach ($f in Get-ChildItem $root -Recurse -Filter *.java -File) {
        $rel = $f.FullName.Substring($root.Length + 1)
        $tag = if ($root -eq $FmlSrc) { 'fml' } else { 'bus' }
        $files.Add([pscustomobject]@{ path = $f.FullName; rel = "$($tag):$($rel -replace '\\','/')" })
    }
}
Write-Host "scanning $($files.Count) candidate files"

# --- parse: fqn -> record -----------------------------------------------
$types = @{}
$order = New-Object System.Collections.Generic.List[string]

foreach ($f in $files) {
    $pkg = $null
    $stack = New-Object System.Collections.Generic.List[object]  # @{fqn; depth}
    $depth = 0
    $lines = Get-Content -LiteralPath $f.path
    for ($i = 0; $i -lt $lines.Count; $i++) {
        $line = $lines[$i]
        $braceDelta = 0
        if ($null -eq $pkg) {
            $pm = $pkgRegex.Match($line)
            if ($pm.Success) { $pkg = $pm.Groups[1].Value }
        }
        if ($line -notmatch '^\s*(\*|//|/\*)') {
            $m = $declRegex.Match($line)
            if ($m.Success) {
                $name = $m.Groups['name'].Value
                $kind = $m.Groups['kind'].Value
                $rest = $m.Groups['rest'].Value
                # A declaration whose header spans several lines (multi-line record / extends
                # lists) has no '{' on its own line. Accumulate forward until the body opens.
                $opensBody = $rest -match '\{'
                $headerEnd = $i
                if (-not $opensBody) {
                    for ($k = $i + 1; $k -lt [Math]::Min($i + 40, $lines.Count); $k++) {
                        $rest += ' ' + $lines[$k]
                        if ($lines[$k] -match '\{') { $opensBody = $true; $headerEnd = $k; break }
                        if ($lines[$k] -match ';') { break }
                    }
                }
                $prefix = if ($stack.Count -gt 0) { $stack[$stack.Count - 1].fqn } else { $pkg }
                if ($prefix) {
                    $fqn = "$prefix.$name"
                    # Strip the generic type-parameter list first: its bounds also use `extends`,
                    # and matching those instead of the real superclass is a silent corruption
                    # (e.g. "class Foo<T extends LivingEntity> extends Event" -> superclass
                    # "LivingEntity" instead of "Event").
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
                    $impl = ''
                    $em = [regex]::Match($header, '\bextends\s+(.+?)(?=\bimplements\b|\{|\s*$)')
                    if ($em.Success) { $ext = $em.Groups[1].Value }
                    $im = [regex]::Match($header, '\bimplements\s+(.+?)(?=\{|\s*$)')
                    if ($im.Success) { $impl = $im.Groups[1].Value }
                    $supers = New-Object System.Collections.Generic.List[string]
                    foreach ($chunk in @($ext, $impl)) {
                        if (-not $chunk) { continue }
                        foreach ($p in ($chunk -split ',')) {
                            $t = [regex]::Replace($p, '<.*$', '').Trim()
                            $t = $t -replace '^\s+|\s+$', ''
                            if ($t -and $t -notmatch '[\s<>]') { $supers.Add($t) }
                        }
                    }
                    if (-not $types.ContainsKey($fqn)) {
                        $types[$fqn] = [pscustomobject]@{
                            fqn = $fqn; kind = $kind; supers = ($supers -join '|'); file = $f.rel
                        }
                        $order.Add($fqn)
                    }
                    if ($opensBody) {
                        $stack.Add([pscustomobject]@{ fqn = $fqn; depth = $depth + 1 })
                        # When the body opener sat on a LATER line, consume those lines here so the
                        # enclosing-type entry survives the depth-based cleanup below.
                        if ($headerEnd -gt $i) { $i = $headerEnd; $braceDelta = 1; $line = $lines[$i] }
                    }
                }
            }
        }
        # update brace depth (cheap: count braces, ignore those in string literals is overkill here)
        if ($braceDelta -eq 0) {
            $braceDelta = ([regex]::Matches($line, '\{')).Count - ([regex]::Matches($line, '\}')).Count
        }
        $depth += $braceDelta
        if ($depth -lt 0) { $depth = 0 }
        while ($stack.Count -gt 0 -and $stack[$stack.Count - 1].depth -gt $depth) {
            $stack.RemoveAt($stack.Count - 1)
        }
    }
}
Write-Host "parsed types: $($types.Count)"

# --- simple-name index --------------------------------------------------
$bySimple = @{}
foreach ($k in $types.Keys) {
    $sn = $k.Substring($k.LastIndexOf('.') + 1)
    if (-not $bySimple.ContainsKey($sn)) { $bySimple[$sn] = New-Object System.Collections.Generic.List[string] }
    $bySimple[$sn].Add($k)
}

function Resolve-Super([string]$name, [string]$fromFqn, [string]$pkg) {
    if ($types.ContainsKey($name)) { return $name }
    if ($name -match '\.') {
        $outer = $fromFqn
        while ($outer.Contains('.')) {
            $outer = $outer.Substring(0, $outer.LastIndexOf('.'))
            if ($types.ContainsKey("$outer.$name")) { return "$outer.$name" }
        }
        if ($types.ContainsKey("$pkg.$name")) { return "$pkg.$name" }
        return $null
    }
    $outer = $fromFqn
    while ($outer.Contains('.')) {
        $outer = $outer.Substring(0, $outer.LastIndexOf('.'))
        if ($types.ContainsKey("$outer.$name")) { return "$outer.$name" }
    }
    if ($types.ContainsKey("$pkg.$name")) { return "$pkg.$name" }
    if ($bySimple.ContainsKey($name)) {
        $c = $bySimple[$name]
        if ($c.Count -eq 1) { return $c[0] }
    }
    return $null
}

$memo = @{}
function Test-ModBus([string]$fqn, [System.Collections.Generic.HashSet[string]]$seen) {
    if ($memo.ContainsKey($fqn)) { return $memo[$fqn] }
    if (-not $seen.Add($fqn)) { return $false }
    $rec = $types[$fqn]
    if (-not $rec) { return $false }
    $pkg = $rec.fqn.Substring(0, $rec.fqn.LastIndexOf('.'))
    $result = $false
    foreach ($s in ($rec.supers -split '\|' | Where-Object { $_ })) {
        if ($s -eq 'IModBusEvent') { $result = $true; break }
        $r = Resolve-Super $s $fqn $pkg
        if ($r -and (Test-ModBus $r $seen)) { $result = $true; break }
    }
    $memo[$fqn] = $result
    return $result
}

$rows = New-Object System.Collections.Generic.List[object]
foreach ($k in $order) {
    $rec = $types[$k]
    $isEvent = $false
    foreach ($s in ($rec.supers -split '\|' | Where-Object { $_ })) {
        if ($s -match 'Event$') { $isEvent = $true; break }
    }
    $mod = Test-ModBus $k (New-Object 'System.Collections.Generic.HashSet[string]')
    $rows.Add([pscustomobject]@{
        fqn = $rec.fqn; kind = $rec.kind; isEvent = $isEvent; modbus = $mod
        supers = $rec.supers; file = $rec.file
    })
}
if ($AllTrees) {
    $rows | Sort-Object fqn | Export-Csv -NoTypeInformation -Encoding UTF8 "$Out\native-types-all.tsv" -Delimiter "`t"
    Write-Host "wrote $Out\native-types-all.tsv ($($rows.Count) types)"
} else {
    $rows | Sort-Object fqn | Export-Csv -NoTypeInformation -Encoding UTF8 "$Out\native-event-bus.tsv" -Delimiter "`t"
    $ev = $rows | Where-Object { $_.isEvent -eq 'True' }
    $modCount = ($ev | Where-Object { $_.modbus -eq 'True' }).Count
    Write-Host "all types: $($rows.Count); event types: $($ev.Count); mod-bus: $modCount; game-bus: $($ev.Count - $modCount)"
    Write-Host "wrote $Out\native-event-bus.tsv"
}
