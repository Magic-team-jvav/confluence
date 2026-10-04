#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""PortLib -> NeoForge 1.21.1 原生源码转换器（计划 Phase 2 的核心工具）。

把 1.20.1（forge-dev/1.20.1）里用 `org.mesdag.portlib.*` 词汇写的源码，转换成
1.21.1（neoforge-dev/1.21.1）上的原生 NeoForge / Minecraft 代码。

设计原则
--------
1. **规则数据驱动**：所有"某个 Port 类换成什么"都来自 rules 目录下的 JSON，
   不在代码里硬编码映射，便于逐条 review 与增补。
2. **三层输出**（这是验收口径）：
   - 转换后的源码树（--out）
   - `uncovered.json`：**没有规则可依的类型/导入**——必须为空才能进入编译阶段
   - `manual-todo.md`：`manual` / `drop` / `review` 类规则命中的位置，必须人工裁决
   - `leftovers.txt`：转换后仍残留 `org.mesdag.portlib` 或 `Port*`/`IPort*` 标识符的位置
3. **不猜**：`manual` / `drop` / `unknown` 的类型**不改写方法体**，只记录；
   宁可留下编译错误让人类看见，也不要静默改写成语义不同的代码。
4. 保留原文件的换行风格；只在代码行做替换，纯注释行（`//` `///` `*` `/**`）不动。

规则目录约定（同目录下所有 `*.json` 都会被加载并合并）
------------------------------------------------------
- 含 `"types": [...]` 的文件：类型级规则
- 含 `"rules": [...]` 的文件：调用点级规则（正则）
- 含 `"events": [...]` 的文件：Port 事件 -> 原生事件 + 总线归属

用法
----
    python port2native.py --source <1.20 模块的 src 根> --out <输出目录> \
        --rules tools/port2native/rules [--only <路径子串>] [--limit N]

例：
    python port2native.py \
        --source D:/Minecraft/1.20forge/confluence/TerraFurniture/src \
        --out D:/temp/port2native-out/TerraFurniture \
        --rules tools/port2native/rules
"""

from __future__ import annotations

import argparse
import json
import pathlib
import re
import sys
from collections import defaultdict

PORT_PKG = "org.mesdag.portlib"
# 需要转换的命名空间：PortLib 本体、它借用 Manifold 目录风格的静态工具类（PortLib.extensions.**），
# 以及**不经 PortLib 的直接 Forge 引用**（forge-to-neoforge.json 负责映射）
PORT_PREFIXES = (PORT_PKG + ".", "PortLib.")
PORT_FQN_RE = re.compile(r"(?:org\.mesdag\.portlib|PortLib|net\.minecraftforge)\.[\w.]+")


def is_port_fqn(fqn: str) -> bool:
    return fqn.startswith(PORT_PREFIXES)

# kind 取值：alias / static-alias / shared / drop / manual / unknown
REWRITABLE_KINDS = {"alias", "static-alias"}
# shared 类需要被复制进 1.21 树（本工具只报告，不做复制）


# --------------------------------------------------------------------------
# 规则加载
# --------------------------------------------------------------------------
class Rules:
    def __init__(self) -> None:
        self.types: dict[str, dict] = {}
        self.by_package: dict[str, list[dict]] = defaultdict(list)
        self.callsites: list[dict] = []
        self.events: dict[str, dict] = {}
        self.imports: dict[str, list[str]] = {}
        self.sources: dict[str, str] = {}
        self.conflicts: list[str] = []
        self.disabled: set[str] = set()
        self.bus_aware: set[str] = set()
        self.event_index: dict[str, dict] = {}      # 事件简单名 -> {native, bus}
        self.governance: list[str] = []             # 治理动作记录，写进报告
        self._auto_res: list | None = None          # 惰性缓存：可自动应用的调用点规则（已编译）

    def apply_overrides(self, data: dict) -> None:
        """overrides.json / 任意含 disable|promote|busAware|extraRules 的规则文件。"""
        dis = data.get("disable") or []
        self.disabled |= {str(x) for x in dis}
        promote = {str(x) for x in (data.get("promote") or [])}
        bus = {str(x) for x in (data.get("busAware") or [])}
        self.bus_aware |= bus
        # 先把 extraRules 追加进来，再统一打标记/提升 —— 否则本文件里新增的规则会被漏掉
        # （踩过：新增的 bus-aware 规则没被打上标记，replace 里的 BUS 被原样写进输出）
        extra = data.get("extraRules") or []
        if extra:
            self.callsites.extend(extra)
            self.governance.append(f"extraRules: 追加 {len(extra)} 条人工核实规则")
        for rule in self.callsites:
            rid = str(rule.get("id") or "")
            if rid in promote and rule.get("kind") != "safe":
                self.governance.append(f"promote: {rid}（{rule.get('kind')} → safe）")
                rule["kind"] = "safe"
            if rid in bus:
                rule["_busAware"] = True

    def build_event_index(self) -> None:
        for entry in self.events.values():
            native = entry.get("native") or ""
            if not native:
                continue
            simple = native.rsplit(".", 1)[-1]
            bus = entry.get("bus", "")
            prev = self.event_index.get(simple)
            if prev and prev.get("bus") != bus:
                self.event_index[simple] = {"native": native, "bus": "*"}   # 同名歧义
            else:
                self.event_index[simple] = {"native": native, "bus": bus}

    def auto_rules(self) -> list[tuple[dict, re.Pattern]]:
        """**可自动应用**的调用点规则（已通过安全闸）及其编译好的正则，惰性缓存。

        两处用到：① 判断「没有可转换 import 的文件是否也该处理」；
        ② 非 PortLib import 的包布局改名（rename_import）。
        只统计非总线感知、非禁用、且 rule_applicability 通过的规则，因此规则表变胖
        不会悄悄扩大改写范围。
        """
        if self._auto_res is None:
            res: list[tuple[dict, re.Pattern]] = []
            for rule in self.callsites:
                rid = str(rule.get("id") or "")
                if rid in self.disabled or rule.get("_busAware"):
                    continue
                ok, _ = rule_applicability(rule)
                if not ok:
                    continue
                pattern = rule.get("pattern")
                if not pattern:
                    continue
                try:
                    res.append((rule, re.compile(pattern)))
                except re.error:
                    continue
            self._auto_res = res
        return self._auto_res

    def auto_callsite_hits(self, text: str) -> bool:
        """有没有可自动应用的调用点规则命中这段文本（用于放行无 import 可改的文件）。"""
        return any(cre.search(text) for _, cre in self.auto_rules())

    def load_dir(self, rules_dir: pathlib.Path) -> None:
        if not rules_dir.is_dir():
            raise SystemExit(f"规则目录不存在: {rules_dir}")
        for path in sorted(rules_dir.glob("*.json")):
            try:
                # 规则文件常由 PowerShell 生成，带 UTF-8 BOM —— 必须用 utf-8-sig 读
                data = json.loads(path.read_text(encoding="utf-8-sig"))
            except Exception as exc:  # noqa: BLE001
                raise SystemExit(f"规则文件解析失败 {path}: {exc}") from exc
            self._load_one(path.name, data)

    def _load_one(self, name: str, data: dict) -> None:
        for entry in data.get("types", []):
            port = entry.get("port")
            if not port:
                continue
            if port in self.types and self.types[port].get("kind") != entry.get("kind"):
                self.conflicts.append(
                    f"{port}: {self.sources.get(port)} 与 {name} 冲突"
                    f"({self.types[port].get('kind')} vs {entry.get('kind')})，以先加载者为准"
                )
                continue
            self.types[port] = entry
            self.sources.setdefault(port, name)
            pkg = port.rsplit(".", 1)[0]
            self.by_package[pkg].append(entry)
        self.callsites.extend(data.get("rules", []))
        for entry in data.get("events", []):
            port = entry.get("port")
            if port:
                self.events[port] = entry
        for fqn, imps in (data.get("imports") or {}).items():
            self.imports.setdefault(fqn, [])
            self.imports[fqn].extend(imps)
        self.apply_overrides(data)


# --------------------------------------------------------------------------
# 工具函数
# --------------------------------------------------------------------------
IMPORT_RE = re.compile(r"^\s*import\s+(static\s+)?([\w.$]+)\s*;\s*$")
COMMENT_LINE_RE = re.compile(r"^\s*(///?|\*|/\*)")
IDENT_RE_CACHE: dict[str, re.Pattern[str]] = {}


def ident_re(name: str) -> re.Pattern[str]:
    if name not in IDENT_RE_CACHE:
        IDENT_RE_CACHE[name] = re.compile(r"\b" + re.escape(name) + r"\b")
    return IDENT_RE_CACHE[name]


def simple_name(fqn: str) -> str:
    return fqn.rsplit(".", 1)[-1]


def rule_native(rule: dict) -> str:
    """该规则应该把引用改写成哪个 FQN；返回空串表示"不改写、只报告"。

    - alias / static-alias：用规则里的 native
    - shared：类本身要 vendor 到 1.21 侧（copyTo），引用同时改指到新位置
    - drop / manual / unknown：不改写（宁可留编译错误也要让人看见）
    """
    kind = rule.get("kind")
    if kind in REWRITABLE_KINDS:
        return rule.get("native") or ""
    if kind == "shared" and rule.get("copyTo"):
        return f"{rule['copyTo']}.{simple_name(rule['port'])}"
    return ""


def find_rule_for_import(rules: Rules, fqn: str) -> dict | None:
    """把一个 portlib import 解析成类型规则。

    import 的可能是顶层类、嵌套类（a.b.C.D）或静态成员，这里按"最长匹配"解析。
    """
    if fqn in rules.types:
        return rules.types[fqn]
    # 嵌套类：org.mesdag.portlib.x.PortEvent.Inner -> 试 PortEvent.Inner、PortEvent
    parts = fqn.split(".")
    for cut in range(len(parts), len(PORT_PKG.split(".")), -1):
        cand = ".".join(parts[:cut])
        if cand in rules.types:
            rule = dict(rules.types[cand])
            rule["_nested_suffix"] = ".".join(parts[cut:])
            return rule
    return None


FQN_SHAPE_RE = re.compile(r"^[A-Za-z_][\w$]*(?:\.[A-Za-z_][\w$]*)+$")


def rename_import(rules: Rules, fqn: str) -> tuple[str, str] | None:
    """用「可自动应用的调用点规则」给非 PortLib 的 import 改名（包布局迁移用）。

    只认三种情况，避免误伤：
      - 规则 pattern 能匹配到该 FQN；
      - 替换后确实与原来不同；
      - 替换结果仍是合法 FQN（否则说明这条规则不是干这个的）。
    返回 (新 FQN, 规则 id)。
    """
    for rule, cre in rules.auto_rules():
        if not cre.search(fqn):
            continue
        new_fqn = cre.sub(str(rule.get("replace", "")), fqn)
        if new_fqn != fqn and FQN_SHAPE_RE.match(new_fqn):
            return new_fqn, str(rule.get("id") or rule.get("pattern"))
    return None


# --------------------------------------------------------------------------
# 规则安全闸
# --------------------------------------------------------------------------
# 实测教训：全量规则里存在**不能机械应用**的条目，例如
#   pattern=r"\.get"                      replace=".get()"   -> 把 .getKey() 改成 .get()Key()
#   pattern=r"\.getOrDefault\(|\.has\(|\.get\("  replace="SAME" -> 把 .getName() 改成 SAME)Name()
# 前者过宽、后者是占位符。二者被应用都会静默毁掉代码，所以这里做三道闸：
#   1. replace 是占位符（SAME/TODO/…）或为空且非删除语义 -> 不应用，只报告
#   2. kind == review -> 不自动应用（审查类规则往往需要结合上下文改，例如 static→instance 形式）
#   3. pattern 里最长的一段"字面量"太短（如 "get"）-> 视为过宽，不应用
PLACEHOLDER_REPLACES = {"same", "todo", "fixme", "n/a", "na", "?", "-", "none", "null", ""}
MIN_LITERAL_RUN = 8          # pattern 中最长字面量至少这么长，才认为是"锚定到具体 API"
METACHARS = set("\\.^$*+?()[]{}|")


def longest_literal_run(pattern: str) -> int:
    best = cur = 0
    i = 0
    while i < len(pattern):
        ch = pattern[i]
        if ch == "\\" and i + 1 < len(pattern):
            i += 2                       # 转义序列算作字面量的一部分？对 \. 这类算 1 个字符
            cur += 1
            continue
        if ch in METACHARS:
            best, cur = max(best, cur), 0
        else:
            cur += 1
        i += 1
    return max(best, cur)


def rule_applicability(rule: dict) -> tuple[bool, str]:
    """返回 (能否机械应用, 不能的原因)。"""
    pattern = rule.get("pattern") or ""
    replace = rule.get("replace")
    if rule.get("kind") == "review":
        return False, "review 规则：需结合上下文人工改写，不自动应用"
    if replace is None:
        return False, "规则没有 replace 字段"
    if str(replace).strip().lower() in PLACEHOLDER_REPLACES and not rule.get("delete"):
        return False, f"replace 是占位符/空（{replace!r}），不是具体改写"
    run = longest_literal_run(pattern)
    if run < MIN_LITERAL_RUN:
        return False, f"pattern 过宽（最长字面量仅 {run} 字符：{pattern!r}），可能误伤"
    return True, ""


def resolve_bus(window: str, event_index: dict[str, dict]) -> tuple[str, str]:
    """从调用点附近的文本里判定事件总线，返回 (总线表达式, 原因)。

    规则 replace 里的 `BUS` 必须换成具体表达式：
      - 全部命中**游戏总线**事件 -> `NeoForge.EVENT_BUS`（可安全自动改写）
      - 命中**模组总线**事件 -> 不能自动改写：1.21 的模组总线是 `@Mod` 构造器参数
        （`public Confluence(IEventBus eventBus, ModContainer container)`），必须把注册挪到能拿到
        该参数的地方；写成 `NeoForge.EVENT_BUS` 会静默不触发。
      - 判定不出/歧义 -> 人工
    """
    if not event_index:
        return "", "没有事件总线表，无法判定（需人工）"
    matched = []
    for simple, info in event_index.items():
        if re.search(r"\b" + re.escape(simple) + r"\b", window):
            matched.append((simple, info))
    if not matched:
        return "", "调用点附近找不到可识别的事件类型（可能是方法引用/未知事件），需人工判定总线"
    buses = {info.get("bus") for _, info in matched}
    names = ", ".join(sorted({s for s, _ in matched})[:4])
    if buses == {"game"}:
        return "NeoForge.EVENT_BUS", f"仅命中游戏总线事件（{names}）"
    if "mod" in buses or "*" in buses:
        mod_names = ", ".join(sorted({s for s, i in matched if i.get("bus") in ("mod", "*")})[:4])
        return "", (f"含模组总线事件（{mod_names}）：必须注册到 @Mod 构造器拿到的 IEventBus，"
                    f"不能写 NeoForge.EVENT_BUS（写错能编译但静默不触发）")
    return "", f"事件类型不明确（{names}），需人工判定总线"


BLOCK_COMMENT_RE = re.compile(r"/\*.*?\*/", re.S)
LINE_COMMENT_RE = re.compile(r"//[^\n]*")
STRING_LIT_RE2 = re.compile(r'"(?:\\.|[^"\\])*"')
CHAR_LIT_RE = re.compile(r"'(?:\\.|[^'\\])'")
TEXT_BLOCK_RE = re.compile(r'"""(?:.|\n)*?"""')


def balanced(text: str) -> bool:
    """粗粒度括号配平自检 —— 用来兜住"改写把代码剪坏了"。

    先剥掉文本块 / 字符串 / 字符字面量 / 注释，再对 () {} [] 做栈式配平。
    踩过的坑：早期版本逐字符处理 `'`，一遇到含撇号的文本或中文注释就失准，
    把改好的文件误判成"括号不配平"并拒写（实测 HangingPotBlock 其实是 252/252 配平）。
    """
    cleaned = TEXT_BLOCK_RE.sub('""', text)
    cleaned = STRING_LIT_RE2.sub('""', cleaned)
    cleaned = CHAR_LIT_RE.sub("''", cleaned)
    cleaned = BLOCK_COMMENT_RE.sub("", cleaned)
    cleaned = LINE_COMMENT_RE.sub("", cleaned)
    pairs = {")": "(", "}": "{", "]": "["}
    stack: list[str] = []
    for ch in cleaned:
        if ch in "({[":
            stack.append(ch)
        elif ch in ")}]":
            if not stack or stack.pop() != pairs[ch]:
                return False
    return not stack


# --------------------------------------------------------------------------
# 单文件转换
# --------------------------------------------------------------------------
class FileResult:
    def __init__(self, rel: str) -> None:
        self.rel = rel
        self.had_portlib = False
        self.rewrites: dict[str, int] = defaultdict(int)
        self.uncovered: list[dict] = []
        self.manual: list[dict] = []
        self.leftovers: list[str] = []
        self.skipped_rules: dict[str, str] = {}
        self.damaged = ""
        self.text: str = ""
        self.changed = False


def convert_file(path: pathlib.Path, rel: str, rules: Rules, report_root: pathlib.Path, allow_damaged: bool = False) -> FileResult:
    res = FileResult(rel)
    raw = path.read_text(encoding="utf-8")
    lines = raw.splitlines(keepends=True)

    # ---- 1. 解析 import 区 ----
    port_import_idx: list[int] = []
    wildcard_pkgs: list[str] = []
    direct_imports: list[tuple[str, dict]] = []   # (simple name, rule)
    static_port_imports: list[str] = []
    orig_imports: list[str] = []                  # 非 portlib 的原样保留

    for idx, line in enumerate(lines):
        m = IMPORT_RE.match(line)
        if not m:
            continue
        is_static, fqn = bool(m.group(1)), m.group(2)
        # 「可转换」的判据是"有规则"，而不是"命名空间是 PortLib"——这样
        # forge-to-neoforge.json（net.minecraftforge.* → net.neoforged.*）也自动生效
        preset_rule = None if is_static else find_rule_for_import(rules, fqn)
        if not is_port_fqn(fqn) and preset_rule is None:
            # 非 PortLib 的 import 也可以被规则改名：geckolib 在两个 MC 分支上用了
            # 同一版本号但**不同包布局**（1.20 是 software.bernie.geckolib.core.**，
            # 1.21 是重组后的 software.bernie.geckolib.**），这类映射是用 callsite
            # 正则表达的（包前缀规则）。这里做一次「import 全文替换」尝试：
            # 只有替换后确实变了、且结果仍像合法 FQN 才采用，否则原样保留。
            renamed = rename_import(rules, fqn)
            if renamed is not None:
                new_fqn, rid = renamed
                res.had_portlib = True                     # 让文件走完整流程（含正文重写）
                res.rewrites[f"import:{rid}"] += 1
                orig_imports.append(new_fqn)
                continue
            orig_imports.append(fqn)
            continue

        res.had_portlib = True
        port_import_idx.append(idx)
        if is_static:
            static_port_imports.append(fqn)
            res.uncovered.append({"kind": "static-import", "detail": fqn})
            continue
        if fqn.endswith(".*"):
            wildcard_pkgs.append(fqn[:-2])
            continue
        rule = preset_rule
        if rule is None:
            res.uncovered.append({"kind": "no-rule", "detail": fqn})
            continue
        direct_imports.append((fqn, rule))

    if not res.had_portlib:
        # 没引用 PortLib 的文件默认原样返回。**但**这条捷径有个漏洞：原版 API 的
        # 1.20.1→1.21.1 更名规则（rules/vanilla-api-1.20-to-1.21.json）打的正是
        # 这类文件——它们一个 Port 引用都没有（例如 getRandom1211 垫片、
        # BlockPathTypes→PathType），却必须改写。所以先问一句"有没有**可自动应用**的
        # 调用点规则真的能命中"；命中就继续走完整流程，否则还是原样返回。
        # 判据只算 rule_applicability 通过的规则（review 类与过宽规则不算），
        # 因此不会因为规则表变胖而误伤本来无需处理的文件。
        if not rules.auto_callsite_hits(raw):
            res.text = raw
            return res

    # ---- 2. 建立重命名表 ----
    rename: dict[str, dict] = {}   # portlib 简单名 -> rule
    for fqn, rule in direct_imports:
        rename[simple_name(fqn)] = rule

    body_for_scan = "".join(
        line for i, line in enumerate(lines) if i not in set(port_import_idx)
    )
    for pkg in wildcard_pkgs:
        for rule in rules.by_package.get(pkg, []):
            name = simple_name(rule["port"])
            if ident_re(name).search(body_for_scan):
                rename[name] = rule
        # 通配包里有类被用到但没有规则 —— 无法知道是哪些，交给 leftovers 兜底
        res.uncovered.append({"kind": "wildcard-package", "detail": pkg})

    needed_imports: set[str] = set()
    manual_rewrite_names: set[str] = set()

    for name, rule in rename.items():
        kind = rule.get("kind")
        native = rule_native(rule)
        if native:
            needed_imports.add(native)
            res.rewrites[f"type:{kind}"] += 1
            if kind == "shared":
                res.manual.append(
                    {
                        "rule": "shared",
                        "port": rule["port"],
                        "action": f"**一次性**把该类原样 vendor 到 1.21 侧 {rule.get('copyTo')}"
                        f"（引用已自动改指）；此外注意该类内部是否还依赖 PortLib 其它类",
                    }
                )
            elif rule.get("confidence", "high") != "high":
                # 非 high 置信度的映射：改写照做，但必须进人工裁决清单
                res.manual.append(
                    {
                        "rule": f"type:{kind}({rule.get('confidence', '?')})",
                        "port": rule["port"],
                        "action": rule.get("notes", "低置信度映射，需人工确认"),
                    }
                )
        elif kind in REWRITABLE_KINDS:
            res.uncovered.append({"kind": "missing-native", "detail": rule["port"]})
        else:  # drop / manual / unknown （或 shared 缺 copyTo）
            manual_rewrite_names.add(name)
            res.manual.append(
                {
                    "rule": kind or "unknown",
                    "port": rule["port"],
                    "action": rule.get("notes", "需人工判定"),
                }
            )

    # ---- 3. 改写 ----
    ordered_names = sorted(rename.keys(), key=len, reverse=True)
    out_lines: list[str] = []
    first_import_out_idx: int | None = None   # 新 import 块要插到 out_lines 的这个下标处
    port_import_set = set(port_import_idx)
    for idx, line in enumerate(lines):
        if IMPORT_RE.match(line):
            # 所有 import 行都从原位置摘掉，稍后统一重建（避免"保留 + 重排"导致重复 import）
            if first_import_out_idx is None:
                first_import_out_idx = len(out_lines)
            continue
        if idx in port_import_set:  # 理论上已被上面的分支覆盖，兜底
            continue
        if COMMENT_LINE_RE.match(line):
            out_lines.append(line)
            continue

        new_line = line
        # 3a. 调用点规则**先**跑：它们比"简单名替换"更具体。
        #     顺序反了就会出问题：IPortItemStackExtension.foo( 会先被简单名替换成
        #     IItemStackExtension.foo(，更具体的调用点规则再也匹配不到。
        for rule in rules.callsites:
            pattern = rule.get("pattern")
            if not pattern:
                continue
            rid = str(rule.get("id") or "")
            if rid in rules.disabled:
                continue                      # 治理：明确禁用的规则（无效/过宽/已被别名取代）
            # 总线感知规则：replace 里的 BUS 必须按事件类型解析成 NeoForge.EVENT_BUS 或模组总线
            if rule.get("_busAware"):
                try:
                    hit = re.search(pattern, new_line) is not None
                except re.error:
                    hit = False
                if hit:
                    window = "".join(lines[idx: idx + 12])
                    bus_expr, reason = resolve_bus(window, rules.event_index)
                    if bus_expr:
                        new_line = re.sub(pattern, str(rule.get("replace", "")).replace("BUS", bus_expr), new_line)
                        res.rewrites[f"call:{rid}"] += 1
                        needed_imports.add("net.neoforged.neoforge.common.NeoForge")
                    else:
                        res.manual.append({"rule": f"bus:{rid}", "port": pattern, "action": reason})
                continue
            ok, reason = rule_applicability(rule)
            if not ok:
                try:
                    hit = re.search(pattern, new_line) is not None
                except re.error:
                    hit = True
                if hit:
                    res.skipped_rules.setdefault(rid or pattern, reason)
                    res.manual.append(
                        {
                            "rule": f"skipped:{rid}",
                            "port": pattern,
                            "action": f"规则未自动应用（{reason}），需人工按 1.21 原生 API 改写",
                        }
                    )
                continue
            try:
                new_new = re.sub(pattern, str(rule.get("replace", "")), new_line)
            except re.error as exc:  # 规则写错要立刻暴露
                raise SystemExit(f"调用点规则 {rule.get('id')} 正则非法: {exc}") from exc
            if new_new != new_line:
                res.rewrites[f"call:{rule.get('id')}"] += 1
                for imp in rule.get("typeImports", []) or []:
                    needed_imports.add(imp)
                new_line = new_new
        # 3b. 全限定引用
        for m in sorted(set(PORT_FQN_RE.findall(new_line)), key=len, reverse=True):
            rule = find_rule_for_import(rules, m)
            replacement = rule_native(rule) if rule else ""
            if replacement:
                new_line = new_line.replace(m, replacement)
                res.rewrites["fqn"] += 1
        # 3c. 简单名（最后做，粒度最粗）
        for name in ordered_names:
            replacement = rule_native(rename[name])
            if not replacement:
                continue
            if not ident_re(name).search(new_line):
                continue
            new_line = ident_re(name).sub(replacement.split(".")[-1], new_line)
            res.rewrites["simple-name"] += 1
        out_lines.append(new_line)

    # ---- 4. 重建 import 区 ----
    existing: dict[str, str] = {}   # 简单名 -> fqn（非 portlib 原有 import）
    for fqn in orig_imports:
        existing.setdefault(fqn.rsplit(".", 1)[-1], fqn)

    add_plain: list[str] = []
    for fqn in sorted(needed_imports):
        if not fqn or fqn.startswith("java.lang."):
            continue
        tail = fqn.rsplit(".", 1)[-1]
        if tail in existing and existing[tail] != fqn:
            res.uncovered.append(
                {"kind": "import-collision", "detail": f"{fqn} 与已导入的 {existing[tail]} 同名"}
            )
            continue
        existing[tail] = fqn
        add_plain.append(fqn)

    all_plain = sorted({*orig_imports, *add_plain})
    new_import_block = [f"import {fqn};\n" for fqn in all_plain]

    # 插入位置：第一条原 import 所在的位置；若该文件原本没有 import，则放在 package 行之后
    if first_import_out_idx is None:
        first_import_out_idx = 0
        for i, line in enumerate(out_lines):
            if line.lstrip().startswith("package "):
                first_import_out_idx = i + 1
                break

    text = (
        "".join(out_lines[:first_import_out_idx])
        + "".join(new_import_block)
        + "".join(out_lines[first_import_out_idx:])
    )

    res.text = text
    res.changed = text != raw

    # ---- 4.5 损伤自检：改完之后括号必须配平、且不能残留占位符，否则拒绝写出 ----
    if res.changed and not balanced(text):
        res.damaged = "改写后括号不配平，已放弃写出该文件（请检查命中的规则）"
        if not allow_damaged:
            res.text = raw
            res.changed = False
    elif res.changed and re.search(r"\bBUS\b", text):
        # 总线感知规则的占位符没被解析就写进去了 —— 这是能编译但静默不触发的坏代码，必须拦下
        res.damaged = "改写后仍残留占位符 BUS（总线未解析），已放弃写出该文件"
        if not allow_damaged:
            res.text = raw
            res.changed = False

    # ---- 5. 残留扫描 ----
    for i, line in enumerate(text.splitlines(), start=1):
        if COMMENT_LINE_RE.match(line):
            continue
        if PORT_PKG in line or re.search(r"\b(?:IPort|Port)[A-Z]\w*", line):
            res.leftovers.append(f"{rel}:{i}: {line.strip()[:160]}")
    return res


# --------------------------------------------------------------------------
# 主流程
# --------------------------------------------------------------------------
def main(argv: list[str] | None = None) -> int:
    ap = argparse.ArgumentParser(description="PortLib -> 1.21.1 原生源码转换器")
    ap.add_argument("--source", required=True, help="1.20 侧源码根目录（含 java 的 src 目录或其子目录）")
    ap.add_argument("--out", required=True, help="输出目录（转换后的源码树）")
    ap.add_argument("--rules", required=True, help="规则目录（*.json）")
    ap.add_argument("--report", default=None, help="报告目录，默认 <out>/../_report")
    ap.add_argument("--only", default=None, help="只处理相对路径含该子串的文件")
    ap.add_argument("--limit", type=int, default=0, help="只处理前 N 个文件（调试用）")
    ap.add_argument("--allow-damaged", action="store_true",
                    help="调试用：损伤自检命中时仍然写出文件（用于定位是哪条规则剪坏了代码）")
    args = ap.parse_args(argv)

    src_root = pathlib.Path(args.source).resolve()
    out_root = pathlib.Path(args.out).resolve()
    rules_dir = pathlib.Path(args.rules).resolve()
    report_root = pathlib.Path(args.report).resolve() if args.report else out_root.parent / "_report"
    report_root.mkdir(parents=True, exist_ok=True)

    rules = Rules()
    rules.load_dir(rules_dir)
    rules.build_event_index()      # 总线感知规则要用：事件简单名 -> {native, bus}

    files = sorted(p for p in src_root.rglob("*.java") if p.is_file())
    if args.only:
        files = [p for p in files if args.only in p.as_posix()]
    if args.limit:
        files = files[: args.limit]

    stats = {
        "rulesFiles": sorted(p.name for p in rules_dir.glob("*.json")),
        "rulesTypes": len(rules.types),
        "rulesCallsites": len(rules.callsites),
        "filesScanned": len(files),
        "filesWithPortLib": 0,
        "filesChanged": 0,
        "rewrites": defaultdict(int),
    }
    uncovered: list[dict] = []
    skipped_rules: dict[str, str] = {}
    damaged_files: list[str] = []
    manual: list[dict] = []
    leftovers: list[str] = []

    for path in files:
        rel = path.relative_to(src_root).as_posix()
        res = convert_file(path, rel, rules, report_root, args.allow_damaged)
        if res.had_portlib:
            stats["filesWithPortLib"] += 1
        if res.changed:
            stats["filesChanged"] += 1
            dst = out_root / rel
            dst.parent.mkdir(parents=True, exist_ok=True)
            # 两个仓库都是 core.autocrlf=true，**工作树里所有源码都是 CRLF**
            # （1.21 侧 5813 个文件是 i/lf + w/crlf）。原来写的是 newline=""，
            # 会把 CRLF 原样落成 LF，于是每批新文件都变成 w/lf、和仓库其余文件不一致。
            # 内存里的文本已被 read_text 做过通用换行（只含 \n），因此 newline="\r\n" 安全，
            # 不会写出 \r\r\n。
            dst.write_text(res.text, encoding="utf-8", newline="\r\n")
        for entry in res.uncovered:
            uncovered.append({"file": rel, **entry})
        for entry in res.manual:
            manual.append({"file": rel, **entry})
        leftovers.extend(res.leftovers)
        for key, value in res.skipped_rules.items():
            skipped_rules.setdefault(key, value)
        if res.damaged:
            damaged_files.append(f"{rel}：{res.damaged}")
        for key, value in res.rewrites.items():
            stats["rewrites"][key] += value

    stats["rewrites"] = dict(sorted(stats["rewrites"].items(), key=lambda kv: -kv[1]))

    (report_root / "stats.json").write_text(
        json.dumps(stats, ensure_ascii=False, indent=2), encoding="utf-8"
    )
    (report_root / "uncovered.json").write_text(
        json.dumps(uncovered, ensure_ascii=False, indent=2), encoding="utf-8"
    )
    (report_root / "leftovers.txt").write_text("\n".join(leftovers), encoding="utf-8")
    skipped_md = ["# 被安全闸拦下的规则（未自动应用，需人工逐处改写）", ""]
    if skipped_rules:
        skipped_md += [f"- `{k}`：{v}" for k, v in sorted(skipped_rules.items())]
    else:
        skipped_md.append("（无）")
    if damaged_files:
        skipped_md += ["", "## 损伤自检拦下的文件（未写出，保留原文）", ""]
        skipped_md += [f"- {d}" for d in damaged_files]
    (report_root / "skipped-rules.md").write_text("\n".join(skipped_md) + "\n", encoding="utf-8")

    grouped: dict[str, list[dict]] = defaultdict(list)
    for entry in manual:
        grouped[f"{entry['rule']} :: {entry['port']} :: {entry['action']}"].append(entry)
    md = ["# 需人工裁决清单（转换器产出）", ""]
    if rules.conflicts:
        md += ["## 规则冲突", ""] + [f"- {c}" for c in rules.conflicts] + [""]
    md += [f"共 {len(manual)} 处，按规则/类型分组：", ""]
    for key in sorted(grouped):
        items = grouped[key]
        md.append(f"## {key}")
        md.append("")
        md.append(f"命中 {len(items)} 处，示例文件：")
        for entry in items[:8]:
            md.append(f"- `{entry['file']}`")
        if len(items) > 8:
            md.append(f"- …另有 {len(items) - 8} 处")
        md.append("")
    (report_root / "manual-todo.md").write_text("\n".join(md), encoding="utf-8")

    print(json.dumps({k: v for k, v in stats.items() if k != "rewrites"}, ensure_ascii=False, indent=2))
    print(f"重写计数: {json.dumps(stats['rewrites'], ensure_ascii=False)}")
    print(f"未覆盖规则: {len(uncovered)} 条；需人工裁决: {len(manual)} 处；残留 Port 引用: {len(leftovers)} 行")
    print(f"报告目录: {report_root}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
