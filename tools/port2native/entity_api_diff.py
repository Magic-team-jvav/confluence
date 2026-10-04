#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""1c-a：1.20.1 → 1.21.1 实体基座/部件的 `@Override` 覆写点对照表。

口径 v2（修正 notes/WP1C-API-DIFF.md 首版的三处缺陷）

  1. **参数只比类型，丢掉形参名。**
     首版按「类型+形参名」整体文本比较，而两侧形参名来自不同映射（1.20 是 Mojang 名，
     1.21 反编译源码里常是 `p_xxxxx_`），于是 `remove(RemovalReason reason)`、
     `readAdditionalSaveData(CompoundTag tag)` 这类共 26 行全部误报「参数变了」。
  2. **只认类体层级（大括号深度 1）的成员。**
     首版把匿名内部类里的方法也算成了目标类的覆写点 —— `BaseMonster` 的 `createTree()` /
     `execute()` 实际写在 `new BTNode(){...}` 里。
  3. **在「继承闭包」里找方法，而不是只查原版类。**
     索引同时编入 1.21 工程源码（ConfluenceOtherworld / Confluence-Magic-Lib /
     TerraEntity / TerraGuns / TerraCurio）与第三方库源码（geckolib sources jar）。
     因此 `PartHitTarget`、`org.confluence.lib.api.entity.Boss`、geckolib `GeoEntity`
     声明的接口方法会被正确识别；首版只查 `net.minecraft.*`，把它们一律误判成
     「1.21 无此方法」（21 行里 10 行是这么来的）。

判定取值

  OK           继承闭包里存在同名**且参数类型表一致**的方法 —— 覆写点成立，无需改动
  SIG_CHANGED  闭包里有同名方法，但参数类型表对不上 —— 需要改签名/调用
  REMOVED      闭包完整，但整条链上都没有这个方法名 —— 1.21 删除或改名
  UNRESOLVED   闭包不完整（某个父类型源码找不到），结论不可信，不参与统计

用法

  python tools/port2native/entity_api_diff.py \\
      --src120   ../1.20forge/confluence/ConfluenceOtherworld/src/main/java \\
      --root121  ConfluenceOtherworld/src/main/java \\
      --root121  Confluence-Magic-Lib/src/main/java \\
      --jar121   <neoforge-21.1.219-sources.jar> \\
      --lib      <geckolib-neoforge-1.21.1-4.8.4-sources.jar> \\
      --out      notes/WP1C-API-DIFF.md
"""

from __future__ import annotations

import argparse
import json
import os
import re
import sys
import zipfile
from dataclasses import dataclass, field

# ---------------------------------------------------------------------------
# 词法预处理
# ---------------------------------------------------------------------------


def strip_java(code: str) -> str:
    """去掉注释与字符串/字符/文本块字面量（空格占位，保持长度与换行）。"""
    out: list[str] = []
    i, n = 0, len(code)
    while i < n:
        c = code[i]
        if c == "/" and i + 1 < n and code[i + 1] == "/":
            j = code.find("\n", i)
            j = n if j < 0 else j
            out.append(" " * (j - i))
            i = j
        elif c == "/" and i + 1 < n and code[i + 1] == "*":
            j = code.find("*/", i + 2)
            j = n if j < 0 else j + 2
            out.append("".join(ch if ch == "\n" else " " for ch in code[i:j]))
            i = j
        elif code.startswith('"""', i):
            j = code.find('"""', i + 3)
            j = n if j < 0 else j + 3
            out.append("".join(ch if ch == "\n" else " " for ch in code[i:j]))
            i = j
        elif c == '"' or c == "'":
            q, j = c, i + 1
            while j < n:
                if code[j] == "\\":
                    j += 2
                    continue
                if code[j] == q:
                    j += 1
                    break
                if code[j] == "\n":
                    break
                j += 1
            out.append("".join(ch if ch == "\n" else " " for ch in code[i:j]))
            i = j
        else:
            out.append(c)
            i += 1
    return "".join(out)


def brace_pairs(code: str) -> tuple[dict[int, int], dict[int, int]]:
    """一次扫描得到 (开括号->闭括号, 位置->该位置之前的深度)。"""
    pairs: dict[int, int] = {}
    depth_at: dict[int, int] = {}
    stack: list[int] = []
    for i, ch in enumerate(code):
        if ch == "}":
            depth_at[i] = max(0, len(stack) - 1)
        else:
            depth_at[i] = len(stack)
        if ch == "{":
            stack.append(i)
        elif ch == "}":
            if stack:
                pairs[stack.pop()] = i
    return pairs, depth_at


_IDENT = re.compile(r"[\w$.]+")
_ANNOT = re.compile(r"@[\w.]+")


def strip_annotations(text: str) -> str:
    """去掉 `@Foo` 与 `@Foo(...)`（括号配对）。"""
    out: list[str] = []
    i = 0
    while i < len(text):
        m = _ANNOT.match(text, i)
        if m and (i == 0 or not (text[i - 1].isalnum() or text[i - 1] in "_.")):
            i = m.end()
            j = i
            while j < len(text) and text[j].isspace():
                j += 1
            if j < len(text) and text[j] == "(":
                depth = 0
                while j < len(text):
                    if text[j] == "(":
                        depth += 1
                    elif text[j] == ")":
                        depth -= 1
                        if depth == 0:
                            j += 1
                            break
                    j += 1
                i = j
            continue
        out.append(text[i])
        i += 1
    return "".join(out)


def split_top_level(text: str, sep: str = ",") -> list[str]:
    parts, buf, depth = [], [], 0
    for ch in text:
        if ch in "<([{":
            depth += 1
        elif ch in ">)]}":
            depth -= 1
        if ch == sep and depth == 0:
            parts.append("".join(buf))
            buf = []
        else:
            buf.append(ch)
    parts.append("".join(buf))
    return parts


def param_type(param: str) -> str:
    """把单个形参声明压成类型字符串（丢掉形参名、注解、final、空白）。"""
    p = strip_annotations(param).strip()
    if not p:
        return ""
    p = re.sub(r"\bfinal\b", " ", p)
    p = p.replace("...", "[]")
    toks = p.split()
    if len(toks) >= 2 and re.fullmatch(r"[\w$]+(\[\])*", toks[-1]):
        toks = toks[:-1]
    return re.sub(r"\s+", "", "".join(toks))


def norm_type(t: str) -> str:
    t = t.replace(" ", "")
    t = t.replace("?extends", "").replace("?super", "")
    return t.replace("...", "[]")


def is_type_var(t: str) -> bool:
    return bool(re.fullmatch(r"[A-Z]\d*(\[\])*", t))


def nested_prefix_compatible(a: str, b: str) -> bool:
    """`RemovalReason` 与 `Entity.RemovalReason` 是同一个类型：1.20 源码 import 了嵌套类，
    1.21 反编译源码写成限定名。只在一侧带限定前缀且末段同名时判等。"""
    sa, sb = a.split("<")[0], b.split("<")[0]
    if "." in sa and "." not in sb:
        return sa.rsplit(".", 1)[-1] == sb
    if "." in sb and "." not in sa:
        return sb.rsplit(".", 1)[-1] == sa
    return False


def sig_match(a: list[str], b: list[str]) -> bool:
    """参数类型表比较：长度相同、逐项同型；类型变量、泛型实参与嵌套类限定前缀视作放宽。"""
    if len(a) != len(b):
        return False
    for x, y in zip(a, b):
        nx, ny = norm_type(x), norm_type(y)
        if nx == ny or is_type_var(nx) or is_type_var(ny) or nested_prefix_compatible(nx, ny):
            continue
        return False
    return True


# ---------------------------------------------------------------------------
# 源码解析
# ---------------------------------------------------------------------------

TYPE_DECL = re.compile(r"\b(class|interface|record|enum)\s+([\w$]+)")
PKG_RE = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.M)
IMP_RE = re.compile(r"^\s*import\s+(static\s+)?([\w$.]+)\s*;", re.M)


@dataclass
class Method:
    name: str
    params: list[str]

    @property
    def key(self) -> str:
        return self.name + "(" + ",".join(norm_type(p) for p in self.params) + ")"


@dataclass
class ClassInfo:
    fqn: str
    package: str
    simple: str
    kind: str
    origin: str
    supers: list[str] = field(default_factory=list)          # 源码里写的父类型名（未解析）
    methods: list[Method] = field(default_factory=list)      # 类体层级全部方法
    overrides: list[Method] = field(default_factory=list)    # 其中带 @Override 的

    def by_name(self, name: str) -> list[Method]:
        seen, out = set(), []
        for m in self.methods:
            if m.name != name or m.key in seen:
                continue
            seen.add(m.key)
            out.append(m)
        return out


def split_type_params(header: str) -> str:
    """剥掉类名后面的 `<T extends ...>` 类型参数段。"""
    h = header.strip()
    if not h.startswith("<"):
        return h
    d = 0
    for k, ch in enumerate(h):
        if ch == "<":
            d += 1
        elif ch == ">":
            d -= 1
            if d == 0:
                return h[k + 1:].strip()
    return h


def parse_supers(header: str) -> list[str]:
    """从类名之后到 `{` 之前的文本里取父类型名。注意 `extends X implements A, B`
    里 extends 的捕获必须在 implements 处截断，否则会把整段 implements 当成第一个父类型。"""
    h = split_type_params(header)
    raw: list[str] = []
    mi = re.search(r"\bimplements\b", h)
    if mi:
        raw += split_top_level(h[mi.end():])
        h = h[: mi.start()]
    me = re.search(r"\bextends\b", h)
    if me:
        raw += split_top_level(h[me.end():])
    out = []
    for s in raw:
        s = re.sub(r"<.*", "", strip_annotations(s)).strip()
        if s and s not in out:
            out.append(s)
    return out


def decl_to_method(text: str) -> tuple[Method, bool] | None:
    """把「类体层级的一段声明文本」解析成 (方法, 是否带 @Override)；不是方法则 None。"""
    if not text.strip():
        return None
    has_override = "@Override" in text
    decl = strip_annotations(text)

    # 顶层 `=` 说明是字段声明（含初始化 / 匿名类）
    d = 0
    for ch in decl:
        if ch in "<([":
            d += 1
        elif ch in ">)]":
            d -= 1
        elif ch == "=" and d == 0:
            return None

    # 第一个角度深度为 0 的 `(`
    d, cut = 0, None
    for k, ch in enumerate(decl):
        if ch == "<":
            d += 1
        elif ch == ">":
            d -= 1
        elif ch == "(" and d == 0:
            cut = k
            break
    if cut is None:
        return None

    head = decl[:cut]
    # 枚举常量表 `A(1), B(2)` 之类
    if "," in head:
        return None
    mm = None
    for x in _IDENT.finditer(head):
        mm = x
    if mm is None:
        return None
    name = mm.group(0).rsplit(".", 1)[-1]
    pre = head[: mm.start()].rstrip()
    if not pre or pre.endswith("."):
        return None

    tail = decl[cut:]
    d, close = 0, None
    for k, ch in enumerate(tail):
        if ch == "(":
            d += 1
        elif ch == ")":
            d -= 1
            if d == 0:
                close = k
                break
    if close is None:
        return None
    raw = tail[1:close]
    params = [param_type(p) for p in split_top_level(raw)] if raw.strip() else []
    return Method(name, [p for p in params if p]), has_override


def parse_members(code: str, body_open: int, pairs: dict[int, int], body_end: int):
    """取类体（深度 1）的成员。嵌套块整体跳过，因此不会误收内部类/匿名类的方法。"""
    all_methods: list[Method] = []
    overrides: list[Method] = []
    i = body_open + 1
    seg_start = i
    while i < body_end:
        ch = code[i]
        if ch == "{":
            parsed = decl_to_method(code[seg_start:i])
            if parsed:
                all_methods.append(parsed[0])
                if parsed[1]:
                    overrides.append(parsed[0])
            nxt = pairs.get(i)
            if nxt is None:
                break
            i = nxt + 1
            seg_start = i
            continue
        if ch == ";":
            parsed = decl_to_method(code[seg_start:i])
            if parsed:
                all_methods.append(parsed[0])
                if parsed[1]:
                    overrides.append(parsed[0])
            seg_start = i + 1
        i += 1
    return all_methods, overrides


def imports_of_text(text: str) -> tuple[dict[str, str], str]:
    """从源码文本取 (simple名 -> FQN, package)。"""
    code = strip_java(text)
    pkg = PKG_RE.search(code)
    imports: dict[str, str] = {}
    for mm in IMP_RE.finditer(code):
        if mm.group(1):
            continue
        full = mm.group(2)
        imports[full.rsplit(".", 1)[-1]] = full
    return imports, pkg.group(1) if pkg else ""


def parse_java(text: str, fqn: str, origin: str) -> ClassInfo | None:
    code = strip_java(text)
    pkg_m = PKG_RE.search(code)
    package = pkg_m.group(1) if pkg_m else ""
    pairs, depth_at = brace_pairs(code)
    want = fqn.rsplit(".", 1)[-1]
    decl = next((m for m in TYPE_DECL.finditer(code)
                 if m.group(2) == want and depth_at[m.start()] == 0), None)
    if decl is None:
        return None
    brace = code.find("{", decl.end())
    if brace < 0:
        return None
    end = pairs.get(brace, len(code) - 1)
    header = code[decl.end():brace]
    supers = parse_supers(header)

    methods, overrides = parse_members(code, brace, pairs, end)
    return ClassInfo(fqn=fqn, package=package, simple=want, kind=decl.group(1),
                     origin=origin, supers=supers, methods=methods, overrides=overrides)


# ---------------------------------------------------------------------------
# 源码索引
# ---------------------------------------------------------------------------


class SourceIndex:
    """按需解析的 java 源码索引：目录根 + sources jar。"""

    def __init__(self, sources: list[tuple[str, str]]):
        self.files: dict[str, str] = {}
        self.origins: dict[str, str] = {}
        self.by_simple: dict[str, list[str]] = {}
        self._readers: dict[str, zipfile.ZipFile] = {}
        self._cache: dict[str, ClassInfo | None] = {}
        self.virtual: dict[str, ClassInfo] = {}                 # 1.20 同批待移植类（闭包用）
        self.virtual_imports: dict[str, tuple[dict[str, str], str]] = {}
        for label, path in sources:
            self._add_source(label, path)
        for fqn in self.files:
            self.by_simple.setdefault(fqn.rsplit(".", 1)[-1], []).append(fqn)

    def add_virtual(self, fqn: str, text: str, origin: str = "120") -> None:
        """把 1.20 侧「同批要移植」的类挂进索引，好让 BaseBoss 这类子类的继承闭包
        能穿过尚未移植的 BaseMonster 继续往上走到 Monster/Entity。"""
        ci = parse_java(text, fqn, origin)
        if ci is None:
            return
        self.virtual[fqn] = ci
        self.virtual_imports[fqn] = imports_of_text(text)
        self.origins.setdefault(fqn, origin)
        self.by_simple.setdefault(fqn.rsplit(".", 1)[-1], []).append(fqn)

    def _add_source(self, label: str, path: str) -> None:
        if os.path.isfile(path) and path.endswith(".jar"):
            try:
                zf = zipfile.ZipFile(path)
            except Exception:
                return
            self._readers[path] = zf
            for name in zf.namelist():
                if not name.endswith(".java"):
                    continue
                fqn = name[:-5].replace("/", ".")
                if fqn.endswith("package-info") or fqn.endswith("module-info"):
                    continue
                self.files.setdefault(fqn, path)
                self.origins.setdefault(fqn, label)
        elif os.path.isdir(path):
            prefix = path
            for marker in ("src/main/java", "src/client/java"):
                cand = os.path.join(path, *marker.split("/"))
                if os.path.isdir(cand):
                    prefix = cand
                    break
            for dirpath, _dirs, files in os.walk(prefix):
                for f in files:
                    if not f.endswith(".java"):
                        continue
                    full = os.path.join(dirpath, f)
                    fqn = os.path.relpath(full, prefix).replace("\\", "/")[:-5].replace("/", ".")
                    if fqn.endswith("package-info"):
                        continue
                    self.files.setdefault(fqn, full)
                    self.origins.setdefault(fqn, label)

    def _read(self, fqn: str) -> str | None:
        path = self.files.get(fqn)
        if path is None:
            return None
        zf = self._readers.get(path)
        if zf is not None:
            try:
                return zf.read(fqn.replace(".", "/") + ".java").decode("utf-8", "replace")
            except KeyError:
                return None
        try:
            with open(path, "r", encoding="utf-8", errors="replace") as fh:
                return fh.read()
        except OSError:
            return None

    def imports_of(self, fqn: str) -> tuple[dict[str, str], str]:
        if fqn in self.virtual_imports:
            return self.virtual_imports[fqn]
        text = self._read(fqn)
        if text is None:
            return {}, ""
        return imports_of_text(text)

    def info(self, fqn: str) -> ClassInfo | None:
        if fqn in self._cache:
            return self._cache[fqn]
        self._cache[fqn] = None  # 防环
        text = self._read(fqn)
        if text is not None:
            ci = parse_java(text, fqn, self.origins.get(fqn, "?"))
            self._cache[fqn] = ci
            return ci
        # 嵌套类型 pkg.Outer.Inner
        parts = fqn.split(".")
        for k in range(len(parts) - 1, 0, -1):
            outer = ".".join(parts[:k])
            if outer not in self.files:
                continue
            node = self.info(outer)
            for seg in parts[k:]:
                if node is None:
                    break
                node = self._nested_of(node.fqn, seg)
            self._cache[fqn] = node
            return node
        if fqn in self.virtual:
            self._cache[fqn] = self.virtual[fqn]
            return self.virtual[fqn]
        return None

    def _nested_of(self, outer_fqn: str, simple: str) -> ClassInfo | None:
        text = self._read(outer_fqn)
        if text is None:
            return None
        code = strip_java(text)
        pairs, depth_at = brace_pairs(code)
        for m in TYPE_DECL.finditer(code):
            if m.group(2) != simple:
                continue
            brace = code.find("{", m.end())
            if brace < 0:
                continue
            end = pairs.get(brace, len(code) - 1)
            supers = parse_supers(code[m.end():brace])
            methods, overrides = parse_members(code, brace, pairs, end)
            return ClassInfo(fqn=outer_fqn + "." + simple,
                             package=outer_fqn.rsplit(".", 1)[0], simple=simple,
                             kind=m.group(1), origin=self.origins.get(outer_fqn, "?"),
                             supers=supers, methods=methods,
                             overrides=overrides)
        return None

    def _known(self, fqn: str) -> bool:
        return fqn in self.files or fqn in self.virtual

    def resolve(self, raw: str, imports: dict[str, str], package: str) -> str | None:
        raw = re.sub(r"<.*", "", raw).strip().replace("...", "").strip()
        if not raw or is_type_var(raw):
            return None
        if "." in raw:
            head, tail = raw.split(".", 1)
            if head[:1].islower() and head not in imports:
                return raw if self._known(raw) else None
            base = imports.get(head)
            full = f"{base}.{tail}" if base else raw
            if self._known(full) or self.info(full):
                return full
            return raw if self._known(raw) else None
        if raw in imports and self._known(imports[raw]):
            return imports[raw]
        cand = f"{package}.{raw}" if package else raw
        if self._known(cand):
            return cand
        options = self.by_simple.get(raw, [])
        if len(options) == 1:
            return options[0]
        if self._known(raw):
            return raw
        for o in options:                      # geckolib 包布局差异：core.** -> 重组后的包
            if o.startswith("software.bernie.geckolib."):
                return o
        return options[0] if options else None

    def hierarchy(self, fqn: str) -> tuple[list[ClassInfo], list[str]]:
        seen: dict[str, ClassInfo] = {}
        missing: list[str] = []
        queue = [fqn]
        while queue:
            cur = queue.pop(0)
            if cur in seen:
                continue
            ci = self.info(cur)
            if ci is None:
                continue
            seen[cur] = ci
            imports, pkg = self.imports_of(cur)
            for raw in ci.supers:
                r = self.resolve(raw, imports, pkg)
                if r is None:
                    missing.append(f"{cur.rsplit('.', 1)[-1]} -> {raw}")
                elif r not in seen:
                    queue.append(r)
        return list(seen.values()), missing

    def hierarchy_of_supers(self, supers: list[str], imports: dict[str, str],
                            package: str) -> tuple[list[ClassInfo], list[str]]:
        """目标类本身在 1.21 侧不存在（这正是要移植的原因），所以闭包从它的父类型开始。"""
        seen: dict[str, ClassInfo] = {}
        missing: list[str] = []
        queue: list[str] = []
        for raw in supers:
            r = self.resolve(raw, imports, package)
            if r is None:
                missing.append(raw)
            else:
                queue.append(r)
        while queue:
            cur = queue.pop(0)
            if cur in seen:
                continue
            ci = self.info(cur)
            if ci is None:
                continue
            seen[cur] = ci
            imp2, pkg2 = self.imports_of(cur)
            for raw in ci.supers:
                r = self.resolve(raw, imp2, pkg2)
                if r is None:
                    missing.append(f"{cur.rsplit('.', 1)[-1]} -> {raw}")
                elif r not in seen:
                    queue.append(r)
        return list(seen.values()), missing


# ---------------------------------------------------------------------------
# 主流程
# ---------------------------------------------------------------------------

TARGETS = [
    "org.confluence.mod.common.entity.boss.BaseBossPart",
    "org.confluence.mod.common.entity.boss.BaseLivingBossPart",
    "org.confluence.mod.common.entity.boss.BossChildDeathLedger",
    "org.confluence.mod.common.entity.boss.MechanicalMayhemTracker",
    "org.confluence.mod.common.entity.boss.BaseBoss",
    "org.confluence.mod.common.entity.boss.BossOwnedEntity",
    "org.confluence.mod.common.entity.boss.BossChunkTicket",
    "org.confluence.mod.common.entity.EnemyTargeting",
    "org.confluence.mod.common.entity.monster.BaseMonster",
]

LABELS = {"vanilla": "原版/NeoForge", "project": "工程源码", "lib": "第三方库",
          "120": "1.20 同批待移植类", "?": "未知"}
ORIGIN_RANK = {"vanilla": 0, "project": 1, "lib": 2, "120": 3, "?": 9}


def read_120(src120: str, fqn: str) -> str | None:
    path = os.path.join(src120, *fqn.split(".")) + ".java"
    if not os.path.isfile(path):
        return None
    with open(path, "r", encoding="utf-8", errors="replace") as fh:
        return fh.read()


def analyse(index: SourceIndex, src120: str, fqn: str) -> dict:
    text = read_120(src120, fqn)
    if text is None:
        return {"fqn": fqn, "missing": True, "rows": []}
    own = parse_java(text, fqn, "120")
    overrides = own.overrides if own else []
    imports, package = imports_of_text(text)
    # 目标类在 1.21 侧不存在（正是要移植的原因），因此从它的父类型开始解析闭包；
    # 若 1.21 侧已有同名文件，则用 1.21 的声明（更可信）。
    base_supers, base_imports, base_pkg = own.supers if own else [], imports, package
    if fqn in index.files:
        ci121 = index.info(fqn)
        if ci121 is not None:
            base_supers = ci121.supers
            base_imports, base_pkg = index.imports_of(fqn)
    closure, missing = index.hierarchy_of_supers(base_supers, base_imports, base_pkg)

    rows = []
    for m in overrides:
        exact, same_name = [], []
        for ci in closure:
            for cand in ci.by_name(m.name):
                same_name.append((ci.origin, ci.fqn, cand))
                if sig_match(m.params, cand.params):
                    exact.append((ci.origin, ci.fqn, cand))
        if exact:
            exact.sort(key=lambda x: ORIGIN_RANK.get(x[0], 9))
            origin, decl, cand = exact[0]
            rows.append({"method": m.name, "p120": m.params, "p121": cand.params,
                         "verdict": "OK", "declared_in": decl, "source": origin,
                         "note": "" if norm_type(",".join(m.params)) == norm_type(",".join(cand.params))
                                 else "类型放宽后一致（嵌套类限定前缀 / 类型变量 / 泛型实参）"})
        elif same_name:
            same_name.sort(key=lambda x: ORIGIN_RANK.get(x[0], 9))
            prim = [x for x in same_name if x[0] != "120"] or same_name
            origins = sorted({x[0] for x in same_name})
            rows.append({"method": m.name, "p120": m.params, "p121": prim[0][2].params,
                         "verdict": "SIG_CHANGED", "declared_in": prim[0][1],
                         "source": ",".join(origins),
                         "note": "闭包内候选: " + " ; ".join(
                             f"{f.rsplit('.', 1)[-1]}({', '.join(c.params) or '—'})"
                             for _o, f, c in same_name[:4])})
        else:
            rows.append({"method": m.name, "p120": m.params, "p121": [],
                         "verdict": "UNRESOLVED" if missing else "REMOVED",
                         "declared_in": "", "source": "",
                         "note": ("闭包缺: " + "; ".join(missing[:3])) if missing else ""})
    return {"fqn": fqn, "missing": False, "rows": rows,
            "closure_size": len(closure), "unresolved": missing}


def fmt_params(ps: list[str]) -> str:
    return ", ".join(f"`{p}`" for p in ps) if ps else "—"


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("--src120", required=True, help="1.20 侧 src/main/java 根")
    ap.add_argument("--root121", action="append", default=[], help="1.21 工程源码根（可重复）")
    ap.add_argument("--jar121", action="append", default=[], help="1.21 vanilla/NeoForge sources jar")
    ap.add_argument("--lib", action="append", default=[], help="第三方库源码（jar 或目录）")
    ap.add_argument("--target", action="append", default=[], help="目标类 FQN（默认内置 1c 九个）")
    ap.add_argument("--out", default="")
    ap.add_argument("--json", dest="json_out", default="")
    args = ap.parse_args()

    sources: list[tuple[str, str]] = []
    for j in args.jar121:
        sources.append(("vanilla", j))
    for r in args.root121:
        sources.append(("project", r))
    for l in args.lib:
        sources.append(("lib", l))
    index = SourceIndex(sources)

    targets = args.target or TARGETS
    # 先把 1.20 目标类挂成「虚拟类」，让批内父子关系（BaseBoss -> BaseMonster -> Monster）
    # 的继承闭包能连通；1.21 侧已有同名文件的类不被虚拟类覆盖。
    for t in targets:
        t120 = read_120(args.src120, t)
        if t120 is not None and t not in index.files:
            index.add_virtual(t, t120)
    results = [analyse(index, args.src120, t) for t in targets]

    counts: dict[str, int] = {}
    for r in results:
        for row in r["rows"]:
            counts[row["verdict"]] = counts.get(row["verdict"], 0) + 1
    total = sum(counts.values())

    L: list[str] = []
    L.append("# 1c-a · 1.21 实体 API 差异对照表（v2：只比参数类型 + 继承闭包）")
    L.append("")
    L.append("由 `tools/port2native/entity_api_diff.py` 生成，可重跑（命令见文末）。")
    L.append("")
    L.append("## 口径：v2 相对首版修正的三处缺陷")
    L.append("")
    L.append("| # | 首版口径 | 后果 | v2 修正 |")
    L.append("|---|---|---|---|")
    L.append("| 1 | 参数按「类型+形参名」整体文本比较 | 1.21 反编译源码的形参名常是 `p_xxxxx_`，"
             "`remove(RemovalReason reason)` / `readAdditionalSaveData(CompoundTag tag)` 等 26 行误报「参数变了」 | "
             "**只比参数类型**，丢掉形参名 |")
    L.append("| 2 | 用正则抓 `@Override` 后的方法，不区分嵌套层级 | 匿名内部类里的方法被算成目标类的覆写点"
             "（`BaseMonster` 的 `createTree()` / `execute()` 其实在 `new BTNode(){...}` 里） | "
             "**只认类体层级（大括号深度 1）成员**，嵌套块整体跳过 |")
    L.append("| 3 | 只在 `net.minecraft.*` 反编译源码里找方法 | `PartHitTarget` / "
             "`org.confluence.lib.api.entity.Boss` / geckolib `GeoEntity` 声明的接口方法被误判成「1.21 无此方法」 | "
             "**在继承闭包里找**，闭包并入 1.21 工程源码 + geckolib sources |")
    L.append("")
    L.append("判定：`OK` 覆写点成立；`SIG_CHANGED` 同名但参数类型表对不上；`REMOVED` 闭包完整而整条链无此方法名；"
             "`UNRESOLVED` 闭包不完整、结论不可信。")
    L.append("")
    L.append("> ⚠️ **`REMOVED` 只表示「这个名字在 1.21 不存在」，不等于「被删除」** —— 也可能是**改名**。"
             "本表里的 `onAddedToWorld` 就是改名成 `onAddedToLevel`（`Entity.java:3733`），"
             "当初按「删除」处理，把逻辑挪进 `tick()` 的「只跑一次」守卫，"
             "结果丢掉了 `super` 里的 `isAddedToLevel = true`。**每个 `REMOVED` 都要先按「会不会是改名」查一遍。**")
    L.append("")
    L.append("`声明于` 列给出 1.21 侧命中的类，`来源` 列给出它所在层"
             "（原版/NeoForge、工程源码、第三方库）。")
    L.append("")
    L.append("## 汇总")
    L.append("")
    L.append("| 文件 | 覆写点 | OK | SIG_CHANGED | REMOVED | UNRESOLVED | 闭包类型数 |")
    L.append("|---|---|---|---|---|---|---|")
    for r in results:
        if r.get("missing"):
            L.append(f"| `{r['fqn'].rsplit('.', 1)[-1]}` | — | — | — | — | — | 1.20 侧无此文件 |")
            continue
        c = {}
        for row in r["rows"]:
            c[row["verdict"]] = c.get(row["verdict"], 0) + 1
        L.append("| `{}` | {} | {} | {} | {} | {} | {} |".format(
            r["fqn"].rsplit(".", 1)[-1], len(r["rows"]), c.get("OK", 0),
            c.get("SIG_CHANGED", 0), c.get("REMOVED", 0), c.get("UNRESOLVED", 0),
            r["closure_size"]))
    L.append("| **合计** | **{}** | **{}** | **{}** | **{}** | **{}** | — |".format(
        total, counts.get("OK", 0), counts.get("SIG_CHANGED", 0),
        counts.get("REMOVED", 0), counts.get("UNRESOLVED", 0)))
    L.append("")

    # ---- 首版（v1）对照：口径变化带来的收敛 ----
    L.append("## v1 → v2 收敛对照")
    L.append("")
    L.append("首版（提交 `69cee922c` 的本文旧版）把 86 个覆写点里的 44 个判为「需处理」。"
             "按上表逐条复核后，**44 里有 38 个是口径缺陷造成的假阳性，真实需要动手的只有 6 个**。")
    L.append("")
    L.append("> 附带记录：旧版正文写「需处理 44」时的分项是「`参数变了` 26、`1.21 无此方法` 18」，"
             "但用脚本数它自己的表，实际是 **23 + 21 = 44** —— 旧版的正文数字与旧版的表也对不上。")
    L.append("")
    L.append("### 旧判「参数变了」的 23 行")
    L.append("")
    L.append("| 归因 | 条数 | 明细 |")
    L.append("|---|---|---|")
    L.append("| 假阳性：只有形参名不同 | 15 | `readAdditionalSaveData`×3、`addAdditionalSaveData`×3、"
             "`startSeenByPlayer`、`stopSeenByPlayer`、`setDeltaMovement`、`handleEntityEvent`、`die`、"
             "`setTarget`、`getHurtSound`、`tickHeadTurn`×2 |")
    L.append("| 假阳性：嵌套类限定前缀 | 3 | `remove(RemovalReason)`×3 —— 1.21 反编译写成 `Entity.RemovalReason` |")
    L.append("| **真差异** | 5 | `defineSynchedData`×2、`getAddEntityPacket`、`lerpTo`、`save` |")
    L.append("")
    L.append("### 旧判「1.21 无此方法」的 21 行")
    L.append("")
    L.append("| 归因 | 条数 | 明细 |")
    L.append("|---|---|---|")
    L.append("| 工程接口声明，1.21 侧本来就有 | 11 | `PartHitTarget` 的 `damageRecipient`/`encounterOwner`/"
             "`dedupeIdentity`/`acceptsDirectHit`（两处各 4）+ `Boss` 的 `shouldShowMessage`/`isMainBody`/"
             "`shouldEnhanceMultiplayer`（`Confluence-Magic-Lib`） |")
    L.append("| 同批待移植的 `BaseMonster` 默认钩子 | 4 | `onCreatureDefinitionReload`、`hasEntityContactAttack`、"
             "`contactAttackInterval`、`contactAttackInflation` |")
    L.append("| 匿名内部类误收，本就不是该类的类体成员 | 3 | `createTree`、`execute`（在 `new BTNode(){...}` 里）、"
             "`createPathFinder`（在 `createNavigation` 返回的 `new GroundPathNavigation(...){...}` 里）|")
    L.append("| geckolib 接口 | 2 | `getAnimatableInstanceCache`、`registerControllers`（`GeoAnimatable`） |")
    L.append("| **改名**（不是删除） | 1 | `Entity#onAddedToWorld()` → `Entity#onAddedToLevel()` |")
    L.append("")
    L.append("覆写点总数 86 → 83，差额 3 正是上表第三类（匿名内部类误收）。")
    L.append("")
    L.append("### 被这一轮推翻的旧结论（含后来才查清的一条）")
    L.append("")
    L.append("1. **`lerpTo` 的方向反了。** 旧结论写「1.21 多一个插值参」，实测正相反："
             "**1.20.1 Forge 比 1.21.1 NeoForge 多一个布尔参**。已直接核对两侧 jar —— "
             "`forge-1.20.1-47.4.20-sources.jar` / `net/minecraft/world/entity/Entity.java:2115` 是 "
             "`lerpTo(double,double,double,float,float,int,boolean teleport)`（Forge 自己补的 `teleport`），"
             "而 `neoforge-21.1.219-sources.jar` 同文件 `:2202` 是 "
             "`lerpTo(double,double,double,float,float,int)`（NeoForge 没带这个补丁，它到 1.21.2 才回原版）。"
             "所以移植方向是**删**参数，不是加。")
    L.append("2. **`tickHeadTurn` 不是差异。** 旧结论写「参数表不同」，实测两侧都是 `(float, float)`，"
             "与 `Mob#tickHeadTurn` 一致，`BaseBoss` / `BaseLivingBossPart` 两处都无需改动。")
    L.append("3. **`onAddedToWorld` 不是「被删除」，是改名成 `onAddedToLevel`。** "
             "本表的 `REMOVED` 判定只说明「这个名字在 1.21 不存在」。当初据「1.21 的 "
             "`Entity.java` / `Level.java` 里 `onAddedToWorld` 出现 0 次」就下了删除的结论，"
             "把 4 个类的逻辑挪进 `tick()` 的「只跑一次」守卫；后来查清 1.21.1 是 "
             "`public void onAddedToLevel() { this.isAddedToLevel = true; }`（`Entity.java:3733`，"
             "调用点 `ServerLevel.java:933/:945`、`ClientLevel.java:355`、"
             "`PersistentEntitySectionManager.java:115/:122/:248`），"
             "**误判导致丢掉了 `super` 里置 `isAddedToLevel` 的副作用**。"
             "1.21 分支原本已有 6 个文件（`SwordProjectile`/`BeeKeeperProjectile`/`SporeCloudProjectile`/"
             "`SpearProjectile`/`NorthPoleSubProjectile`/`BaseArrowEntity`）正确地覆写它。"
             "已固化成规则 `vanilla-onaddedtoworld-rename`（1.20 侧还有 36 处使用）。")
    L.append("")

    # ---- 可执行清单 ----
    L.append("## 1c 的可执行改动清单（SIG_CHANGED + REMOVED 的全部）")
    L.append("")
    L.append("除下表之外，**其余覆写点在 1.21 侧签名一致，移植时不需要任何 API 手术**。")
    L.append("")
    L.append("| 文件 | 方法 | 1.20 参数 | 1.21 参数 | 处理 |")
    L.append("|---|---|---|---|---|")
    HOW = {
        "defineSynchedData": "改成 `protected void defineSynchedData(SynchedEntityData.Builder builder)`，"
                             "把 `entityData.define(...)` 换成 `builder.define(...)`，并调用 `super.defineSynchedData(builder)`",
        "getAddEntityPacket": "改成 `public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity)`，"
                              "`new ClientboundAddEntityPacket(this, entity)`",
        "lerpTo": "1.21.1 是六参（无 `boolean teleport`）；删掉第七个形参，把「是否瞬移」的判断改由调用方/`steps <= 0` 决定",
        "save": "改成 `public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries)`",
        "onAddedToWorld": "**是改名，不是删除**：1.21.1 叫 `onAddedToLevel()`"
                          "（`Entity.java:3733`，调用点 `ServerLevel.java:933/:945` 等），"
                          "整体改名即可，**必须保留 `super.onAddedToLevel()`** —— 它会置 `isAddedToLevel`。"
                          "已固化成规则 `vanilla-onaddedtoworld-rename`。"
                          "（反向教训：一度误判成「1.21 删了这个钩子」，把逻辑挪进 `tick()` 的「只跑一次」守卫里，"
                          "结果丢掉了 super 的副作用。）",
    }
    for r in results:
        if r.get("missing"):
            continue
        for row in r["rows"]:
            if row["verdict"] not in ("SIG_CHANGED", "REMOVED"):
                continue
            L.append("| `{}` | `{}` | {} | {} | {} |".format(
                r["fqn"].rsplit(".", 1)[-1], row["method"], fmt_params(row["p120"]),
                fmt_params(row["p121"]), HOW.get(row["method"], "按 1.21 签名改写")))
    L.append("")

    order = {"REMOVED": 0, "SIG_CHANGED": 1, "UNRESOLVED": 2, "OK": 3}
    for r in results:
        if r.get("missing"):
            continue
        L.append(f"## {r['fqn'].rsplit('.', 1)[-1]}")
        L.append("")
        for row in sorted(r["rows"], key=lambda x: (order[x["verdict"]], x["method"])):
            L.append("### `{}` — **{}**".format(row["method"], row["verdict"]))
            L.append("")
            L.append(f"- 1.20 参数：{fmt_params(row['p120'])}")
            if row["p121"]:
                L.append(f"- 1.21 参数：{fmt_params(row['p121'])}（`{row['declared_in']}`）")
            if row["source"]:
                L.append(f"- 来源：{LABELS.get(row['source'], row['source'])}")
            if row["note"]:
                L.append(f"- 备注：{row['note']}")
            L.append("")

    L.append("## 复现命令")
    L.append("")
    L.append("```powershell")
    L.append("python tools/port2native/entity_api_diff.py \\")
    L.append("  --src120  D:\\Minecraft\\1.20forge\\confluence\\ConfluenceOtherworld\\src\\main\\java \\")
    L.append("  --root121 ConfluenceOtherworld/src/main/java \\")
    L.append("  --root121 Confluence-Magic-Lib/src/main/java \\")
    L.append("  --root121 TerraEntity/src/main/java --root121 TerraGuns/src/main/java \\")
    L.append("  --jar121  ConfluenceOtherworld/build/moddev/artifacts/neoforge-21.1.219-sources.jar \\")
    L.append("  --lib     <geckolib-neoforge-1.21.1-4.8.4-sources.jar> \\")
    L.append("  --out notes/WP1C-API-DIFF.md")
    L.append("```")
    L.append("")

    md = "\n".join(L)
    if args.out:
        os.makedirs(os.path.dirname(os.path.abspath(args.out)), exist_ok=True)
        with open(args.out, "w", encoding="utf-8", newline="\r\n") as fh:
            fh.write(md)
    else:
        sys.stdout.write(md)
    if args.json_out:
        with open(args.json_out, "w", encoding="utf-8") as fh:
            json.dump({"counts": counts, "total": total, "results": results}, fh,
                      ensure_ascii=False, indent=2)
    print(json.dumps({"counts": counts, "total": total}, ensure_ascii=False))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
