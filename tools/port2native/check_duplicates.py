#!/usr/bin/env python
"""移植前审计：待落地的 1.20 文件，在 1.21 侧**是否只是「被移动过」的同一个类**。

## 为什么需要这个工具（真实事故，2026-09-27）

「1.20 把类从 A 包移到 B 包」这种情况，`dep_subset.py` 只看**类型级**依赖：
它按 1.20 的 FQN 去 1.21 里找，找不到就判「缺失」，于是**把一次包移动报成了新类**。
于是移植的人（我）在 B 包新建了一份，而 A 包的原类与其全部引用原封不动 ——
树里出现**两个同名类、两套实现**，新那份还是死代码。

实例（都已确认）：

| 类 | 1.20 位置 | 1.21 原有位置 | 事故 |
|---|---|---|---|
| `BrushData` / `DateStamp` / `StarPhase` | `common/data/` | `common/data/saved/` | **本次会话 WP7B 造了重复**（已撤） |
| `MoonPhase` | `common/data/` | `common/data/saved/` | **WP2 批次 12（f4d075f04）造了重复，至今仍在树里** |
| `GunStats` 等 8 个枪械词汇 | `common/combat/gun/`、`common/item/gun/definition/`、`common/component/` | `TerraGuns` 子模块 `org.confluence.terra_guns.*` | 属既定的「TerraGuns 退役、内联进主模组」过渡态，**但必须写明**，不能当「全新增」 |

`GamePhase` 是同一族里**唯一**被项目用 `--alias` 正式登记的等价关系
（`org.confluence.mod.common.data.GamePhase=org.confluence.mod.common.data.saved.GamePhase`），
这也说明：**团队选择的处置方式是「登记等价」而不是「整体包迁移」**。

## 用法

    python tools/port2native/check_duplicates.py \
        --src120 D:\\Minecraft\\1.20forge\\confluence\\ConfluenceOtherworld\\src\\main\\java \
        --ref    D:\\Minecraft\\1.21neoforge\\confluence \
        --file org/confluence/mod/common/data/BrushData.java \
        --file ...                       # 或 --all（扫描全部同名文件，较慢）

判定：

  SAME   同一份（连 package 都一样）      -> 纯重复，别落
  MOVE   只差 package 声明（内容等价）    -> **是移动**：要么登记 alias，要么整批迁移（移动+改引用）
  NEAR   归一化后相似度 >= 85%            -> 疑似同一类的不同改写版，人工 diff
  DIFF   相似度 < 85%                     -> 不同实现（不同包/不同模块），确认是否既定的「先加后删」

输出同时给出**建议的 alias 行**，可直接粘进工具的调用参数或 `notes/` 的别名表。
"""
from __future__ import annotations

import argparse
import difflib
import os
import re
import sys
from pathlib import Path

SKIP_DIRS = {"build", ".git", "run", "runData", "logs", ".gradle", "out"}


def iter_java(root: Path):
    for dirpath, dirnames, filenames in os.walk(root):
        dirnames[:] = [d for d in dirnames if d not in SKIP_DIRS]
        for name in filenames:
            if name.endswith(".java"):
                yield Path(dirpath) / name


def normalize(text: str) -> str:
    """去掉 package 声明与空白，用于「是否只是换了包」的判定。"""
    lines = [ln.strip() for ln in text.splitlines()]
    lines = [ln for ln in lines if ln and not ln.startswith("package ")]
    return "\n".join(lines)


def fqn_of(path: Path, text: str) -> str:
    m = re.search(r"^package\s+([\w.]+)\s*;", text, re.M)
    return f"{m.group(1)}.{path.stem}" if m else path.stem


def main() -> int:
    try:
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    except Exception:
        pass
    ap = argparse.ArgumentParser()
    ap.add_argument("--src120", required=True, help="1.20 侧 src/main/java 根")
    ap.add_argument("--ref", required=True, help="1.21 侧仓库根（含子模块，子模块目录会被递归扫）")
    ap.add_argument("--file", action="append", default=[], help="1.20 相对 src120 的路径（正斜杠）")
    ap.add_argument("--all", action="store_true", help="扫描 src120 下所有 java（慢）")
    ap.add_argument("--min-ratio", type=float, default=0.85, help="NEAR 判定阈值（默认 0.85）")
    args = ap.parse_args()

    src120 = Path(args.src120)
    ref = Path(args.ref)
    if args.all:
        targets = list(iter_java(src120))
    else:
        targets = [src120 / f for f in args.file]
        for t in targets:
            if not t.exists():
                print(f"[!] 1.20 侧找不到: {t}", file=sys.stderr)
                return 2

    # 1.21 侧按 basename 建索引（含子模块）
    index: dict[str, list[Path]] = {}
    for p in iter_java(ref):
        index.setdefault(p.name, []).append(p)

    problems = 0
    for t in targets:
        text20 = t.read_text(encoding="utf-8", errors="replace")
        hits = [p for p in index.get(t.name, []) if p.resolve() != t.resolve()]
        if not hits:
            continue
        rel = t.relative_to(src120).as_posix()
        n20 = normalize(text20)
        fqn20 = fqn_of(t, text20)
        print(f"\n### {rel}   (1.20 FQN: {fqn20})")
        for h in hits:
            try:
                text21 = h.read_text(encoding="utf-8", errors="replace")
            except OSError:
                continue
            n21 = normalize(text21)
            fqn21 = fqn_of(h, text21)
            relh = h.relative_to(ref).as_posix()
            if fqn20 == fqn21:
                # 同 FQN：1.21 侧同一路径上的那份就是**移植目标本身**，不是重复 —— 直接跳过，
                # 否则每个已落地的文件都会被误报成「疑似重复」（实测踩过）。
                print(f"  [SELF]  {relh}  （同 FQN：移植目标本身，跳过）")
                continue
            if text20 == text21:
                verdict = "SAME"
            elif n20 == n21:
                verdict = "MOVE"
            else:
                ratio = difflib.SequenceMatcher(None, n20, n21).ratio()
                verdict = "NEAR" if ratio >= args.min_ratio else "DIFF"
            print(f"  [{verdict}] {relh}   (1.21 FQN: {fqn21})")
            if verdict in ("SAME", "MOVE", "NEAR"):
                problems += 1
                print(f"          -> 不要新建副本；按等价关系处置（alias 或整批迁移）")
                print(f"          -> 建议 alias: {fqn20}={fqn21}")
            else:
                print(f"          -> 不同实现：确认是否既定的「先加后删」（模块退役/架构对齐）")

    print(f"\n共 {len(targets)} 个待查文件，发现 {problems} 处「疑似移动/重复」")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
