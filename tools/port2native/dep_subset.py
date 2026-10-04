#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""「最大可编译子集」计算器 —— 每一批移植开工的第一步。

标准工作循环第 1 步：从种子文件出发，把 `org.confluence.*` 依赖（含 import 与正文里的 FQN）
做成不动点；依赖在 1.21 侧已有同名文件、或也在保留集里，才算满足，否则连同依赖者一起剔除。

做法分两段：

1. **扩张**：种子 + 它们直接/间接的 `org.confluence.*` 依赖，全部拉进候选集（不动点）。
2. **收缩**：`--defer` 指定本批**不搬**的类型（比如别的批次认领的、或依赖物种注册层的）；
   候选集里凡是**能（直接或间接）到达某个 defer 根**的，连同依赖者一起剔除，
   并给出最短依赖链 —— 这条链写进提交信息就是「推迟了什么、为什么」。
   若 `--defer` 里给的类型根本没被候选集引用，工具会报出来（说明这个 defer 是多余的）。

嵌套类按最长匹配截断到外层类（`a.b.C.Nested` → `a.b.C`）。
同时报出候选集里**1.21 侧已有同名文件**的类型 —— 那些**不得覆盖**，必须 diff 后人工合并。

用法：
  python tools/port2native/dep_subset.py `
      --src120  D:\\Minecraft\\1.20forge\\confluence\\ConfluenceOtherworld\\src\\main\\java `
      --root121 ConfluenceOtherworld/src/main/java `
      --root121 Confluence-Magic-Lib/src/main/java `
      --seed    common/entity/boss/BaseBossPart.java `
      --seed    common/entity/boss/BaseLivingBossPart.java `
      --defer   org.confluence.mod.common.init.entity.BossEntities `
      --out     notes/1c-1-SUBSET.md
"""

from __future__ import annotations

import argparse
import collections
import json
import os
import re
import sys

PKG_RE = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.M)
IMP_RE = re.compile(r"^\s*import\s+(static\s+)?([\w$.*]+)\s*;", re.M)
FQN_RE = re.compile(r"\borg\.confluence(?:\.\w+)+")


def strip_java(code: str) -> str:
    """去掉注释与字符串/字符/文本块字面量（空格占位）。"""
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


def index_root(root: str) -> dict[str, str]:
    """源码根 → {FQN: 绝对路径}。"""
    prefix = root
    for marker in ("src/main/java", "src/client/java"):
        cand = os.path.join(root, *marker.split("/"))
        if os.path.isdir(cand):
            prefix = cand
            break
    out: dict[str, str] = {}
    for dirpath, _dirs, files in os.walk(prefix):
        for f in files:
            if not f.endswith(".java") or f == "package-info.java":
                continue
            full = os.path.join(dirpath, f)
            rel = os.path.relpath(full, prefix).replace("\\", "/")
            out[rel[:-5].replace("/", ".")] = full
    return out


def truncate_nested(fqn: str, known: set[str]) -> str | None:
    """把 a.b.C.Nested 截断到最长的已知外层类。"""
    parts = fqn.split(".")
    for k in range(len(parts), 1, -1):
        cand = ".".join(parts[:k])
        if cand in known:
            return cand
    return None


def refs_of(path: str, known: set[str]) -> set[str]:
    """一个 1.20 文件引用的 org.confluence 类型集合（按最长匹配截断到外层类）。

    三类来源都要算，漏掉任何一类都会**低估闭包**：
      1. 显式 import（`import a.b.C;`）
      2. 正文里的全限定名（`a.b.C.D`，可能是嵌套类）
      3. 简名引用：同包，以及 `import a.b.*;` 通配包里的成员。
         `common/init/entity/BossEntities.java` 就是用 `import ...entity.boss.*;` 引 Boss 物种的，
         早期版本漏了第 3 类的通配部分，导致 BossEntities 的闭包被算成只有 3 个文件。
    """
    with open(path, "r", encoding="utf-8", errors="replace") as fh:
        code = strip_java(fh.read())
    pkg_m = PKG_RE.search(code)
    package = pkg_m.group(1) if pkg_m else ""
    out: set[str] = set()
    wild: list[str] = []
    for m in IMP_RE.finditer(code):
        if m.group(1):
            continue
        full = m.group(2)
        if not full.startswith("org.confluence"):
            continue
        if full.endswith(".*"):
            wild.append(full[:-2])
            continue
        t = truncate_nested(full, known)
        if t:
            out.add(t)
    for m in FQN_RE.finditer(code):
        t = truncate_nested(m.group(0), known)
        if t:
            out.add(t)
    # 简名引用：同包 + 通配包
    if package or wild:
        for m in re.finditer(r"\b([A-Z]\w*)\b", code):
            simple = m.group(1)
            for cand in ([f"{package}.{simple}"] if package else []) + [f"{w}.{simple}" for w in wild]:
                if cand in known:
                    out.add(cand)
    return out


def shortest_chain(start: str, targets: set[str], deps: dict[str, list[str]],
                   limit: int = 12) -> list[str]:
    """从 start 出发、按依赖方向 BFS，返回到达任一 target 的最短链。"""
    seen = {start}
    queue: collections.deque[list[str]] = collections.deque([[start]])
    while queue:
        path = queue.popleft()
        if len(path) > limit:
            return []
        for nxt in deps.get(path[-1], ()):
            if nxt in targets:
                return path + [nxt]
            if nxt not in seen:
                seen.add(nxt)
                queue.append(path + [nxt])
    return []


def main() -> int:
    try:                                   # Windows 控制台默认 GBK，报告里有中文与符号
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    except Exception:
        pass
    ap = argparse.ArgumentParser()
    ap.add_argument("--src120", required=True, help="1.20 侧 src/main/java 根")
    ap.add_argument("--root121", action="append", default=[], help="1.21 侧源码根（可重复）")
    ap.add_argument("--seed", action="append", required=True,
                    help="种子文件（相对 1.20 src/main/java 的路径）")
    ap.add_argument("--defer", action="append", default=[],
                    help="本批不搬的类型 FQN（可重复）；候选集里能到达它的都会被剔除")
    ap.add_argument("--alias", action="append", default=[],
                    help="`1.20的FQN=1.21的FQN`：跨分支改过包路径/类名的等价关系（可重复）。"
                         "1.21 侧目标存在时，该边视作已满足，不再顺着 1.20 那份扩张 —— "
                         "否则纯路径搬迁（如 init.entity.ModEntities -> init.ModEntities）"
                         "会把整个注册层错算成新增。")
    ap.add_argument("--out", default="")
    ap.add_argument("--json", dest="json_out", default="")
    args = ap.parse_args()

    r120 = index_root(os.path.abspath(args.src120))
    known120 = set(r120)
    r121: dict[str, str] = {}
    for root in args.root121:
        r121.update(index_root(os.path.abspath(root)))
    known121 = set(r121)

    seeds: list[str] = []
    for s in args.seed:
        fqn = s.replace("/", ".").removesuffix(".java")
        if fqn not in known120:
            print(f"[!] 种子不在 1.20 源码根里: {s}", file=sys.stderr)
            return 2
        seeds.append(fqn)

    # ---- 扩张 ----
    # 只往「1.21 侧还没有的类型」里扩张：1.21 已有的同名文件说明这条边已经满足，
    # 再顺着 1.20 那份继续走会把整棵 1.20 依赖树拉进来（实测 BaseBossPart 单点能从
    # 十几个候选涨到 1054）。已满足的直接依赖记在 satisfied 里，报告里列出来。
    # `--alias` 处理跨分支改过包路径/类名的等价关系（纯路径搬迁不算新增）。
    alias: dict[str, str] = {}
    for a in args.alias:
        src, _, dst = a.partition("=")
        if src and dst:
            alias[src.strip()] = dst.strip()

    def mapped(d: str) -> str:
        return alias.get(d, d)

    cand: set[str] = set()
    deps: dict[str, list[str]] = {}
    satisfied: dict[str, list[str]] = {}
    queue = list(seeds)
    while queue:
        cur = queue.pop()
        if cur in cand:
            continue
        cand.add(cur)
        ds = sorted(refs_of(r120[cur], known120 | known121) - {cur})
        deps[cur] = ds
        satisfied[cur] = [d for d in ds if mapped(d) in known121]
        for d in ds:
            if d in known120 and mapped(d) not in known121 and d not in cand:
                queue.append(d)

    # ---- 收缩：--defer 的传递闭包 ----
    deferred = list(dict.fromkeys(args.defer))
    used = [d for d in deferred if d in deps or d in cand]
    unused = [d for d in deferred if d not in cand]
    roots = set(used)
    removed: dict[str, str] = {}
    for c in sorted(cand):
        if c in roots:
            removed[c] = "本批显式 `--defer`"
            continue
        chain = shortest_chain(c, roots, deps)
        if chain:
            removed[c] = " -> ".join(x.rsplit(".", 1)[-1] for x in chain)
    kept = sorted(c for c in cand if c not in removed)
    already = sorted(c for c in cand if c in known121)
    new_files = [c for c in kept if c not in known121]

    out = {"seeds": seeds, "candidates": len(cand), "kept": kept, "new_files": new_files,
           "removed": removed, "already_in_121": already, "unused_defer": unused,
           "aliases": alias}
    L: list[str] = []
    L.append("# 最大可编译子集（`tools/port2native/dep_subset.py` 输出）")
    L.append("")
    L.append(f"- 种子 {len(seeds)} 个：" + ", ".join("`" + s.rsplit(".", 1)[-1] + "`" for s in seeds))
    L.append(f"- 扩张后候选 **{len(cand)}** 个 `org.confluence.*` 类型")
    L.append(f"- 本批保留 **{len(kept)}** 个（其中 1.21 侧**新增** {len(new_files)} 个），"
             f"因 `--defer` 剔除 **{len(removed)}** 个")
    if alias:
        L.append(f"- `--alias` {len(alias)} 条（跨分支改过包路径的等价关系，不算新增）："
                 + ", ".join(f"`{k}` → `{v}`" for k, v in sorted(alias.items())))
    if unused:
        L.append(f"- ℹ️ `--defer` 里这些没被候选集引用（多余，可去掉）：" + ", ".join(f"`{u}`" for u in unused))
    L.append("")
    if already:
        L.append("## ⚠️ 1.21 侧已有同名文件 —— **不得覆盖，必须 diff 后人工合并**")
        L.append("")
        for a in already:
            L.append(f"- `{a}`")
        L.append("")
    L.append("## 本批新增（直接拷贝即可）")
    L.append("")
    for k in new_files:
        L.append(f"- `{k}`")
    L.append("")
    L.append("## 被 `--defer` 剔除（连依赖链一起还给后续批次）")
    L.append("")
    if not removed:
        L.append("_无_")
    for k in sorted(removed):
        L.append(f"- `{k}` —— {removed[k]}")
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
            json.dump(out, fh, ensure_ascii=False, indent=2)
    print(json.dumps({"candidates": len(cand), "kept": len(kept),
                      "new": len(new_files), "removed": len(removed),
                      "already": len(already), "unused_defer": len(unused)}, ensure_ascii=False))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
