#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""一次算完**每个种子各自的**依赖闭包，用来切批次边界。

**为什么需要它**：`dep_subset.py` 是「一批种子 → 一个合并闭包」，用来定某个批次的边界；
但切边界之前要先知道**每个候选种子单独搬需要带多少东西**。把一堆种子塞给
`dep_subset.py` 量出来的是并集，会互相污染：一个种子把公共底座拉进来，
另一个看起来很轻的种子就被算成很重 —— 实测 `slime/*` 10 个类合量是 10 个文件，
但 `MotherSlime` 单点就带 `BaseSlime`+`slime` 全族。

做法：把 `dep_subset.py` 的扩张规则（**只往「1.21 侧还没有的类型」里扩张**）
在同一张图上实现一次，然后对每个种子做可达性 BFS。
一张图、一趟解析、N 个种子各得各的闭包 —— 与逐种子跑 `dep_subset.py` 的结果等价，
但不会互相污染，也不必把 1.20 源码树解析 N 遍。

用法：
  # 量 common/entity/monster 下全部 52 个缺失文件的单独闭包
  python tools/port2native/seed_closures.py `
      --src120  D:\\Minecraft\\1.20forge\\confluence\\ConfluenceOtherworld\\src\\main\\java `
      --root121 ConfluenceOtherworld/src/main/java `
      --root121 Confluence-Magic-Lib/src/main/java `
      --seed-dir common/entity/monster `
      --alias org.confluence.mod.common.init.entity.ModEntities=org.confluence.mod.common.init.entity.ModEntities `
      --out notes/WP2G-CLOSURES.md

  # 只看某一个种子的闭包明细
  python tools/port2native/seed_closures.py ... --seed common/entity/monster/Harpy.java --detail
"""

from __future__ import annotations

import argparse
import collections
import json
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import dep_subset as ds  # noqa: E402


def blank_lines(path: str) -> int:
    try:
        with open(path, "r", encoding="utf-8", errors="replace") as fh:
            return sum(1 for x in fh.read().split("\n") if x.strip())
    except OSError:
        return 0


def main() -> int:
    try:
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    except Exception:
        pass
    ap = argparse.ArgumentParser()
    ap.add_argument("--src120", required=True)
    ap.add_argument("--root121", action="append", default=[])
    ap.add_argument("--seed", action="append", default=[],
                    help="种子（相对 1.20 src/main/java 的路径，或 FQN）")
    ap.add_argument("--seed-dir", action="append", default=[],
                    help="把这个目录（相对 1.20 src/main/java）下**1.21 侧缺失**的 java 全当种子")
    ap.add_argument("--alias", action="append", default=[])
    ap.add_argument("--assume-present", action="append", default=[], dest="assume",
                    help="假定这些 1.20 FQN（如注册层 org.confluence.mod.common.init.entity.MonsterEntities）"
                         "「本批不搬、但当作已满足」，不扩张也不计入闭包 —— "
                         "用于回答「注册层放行之后，这个物种还差多少」")
    ap.add_argument("--out", default="")
    ap.add_argument("--json", dest="json_out", default="")
    ap.add_argument("--detail", action="store_true", help="打印每个种子的完整闭包清单")
    ap.add_argument("--small", type=int, default=12, help="「轻量种子」阈值（闭包文件数，含自己）")
    args = ap.parse_args()

    r120 = ds.index_root(os.path.abspath(args.src120))
    known120 = set(r120)
    known121: set[str] = set()
    for root in args.root121:
        known121 |= set(ds.index_root(os.path.abspath(root)))
    assumed = set(args.assume)
    missing_assumed = sorted(a for a in assumed if a not in known120)
    if missing_assumed:
        print(f"[!] --assume-present 里有不在 1.20 源码根的类型: {', '.join(missing_assumed)}", file=sys.stderr)
        return 2
    known121 |= assumed

    alias: dict[str, str] = {}
    for a in args.alias:
        src, _, dst = a.partition("=")
        if src and dst:
            alias[src.strip()] = dst.strip()

    def mapped(d: str) -> str:
        return alias.get(d, d)

    # 只有「1.21 侧没有的类型」才需要出边：已有的类型是已满足的边，不扩张。
    expandable = [f for f in sorted(known120) if mapped(f) not in known121]
    deps: dict[str, list[str]] = {}
    for f in expandable:
        ds_refs = ds.refs_of(r120[f], known120 | known121) - {f}
        deps[f] = sorted(d for d in ds_refs if d in known120 and mapped(d) not in known121)

    def resolve(token: str) -> str | None:
        """把「FQN / 相对路径 / 简单类名」都解析成 FQN；歧义返回 None。"""
        t = token.replace("\\", "/").strip().removesuffix(".java")
        fqn = t.replace("/", ".")
        if fqn in known120:
            return fqn
        # 相对路径可能少了包前缀（`common/entity/monster/Harpy`），也可能只有类名
        hits = sorted(f for f in known120 if f.endswith("." + fqn))
        if len(hits) == 1:
            return hits[0]
        if len(hits) > 1:
            print(f"[!] 种子 `{token}` 有歧义，命中 {len(hits)} 个：" + ", ".join(hits[:6]), file=sys.stderr)
            return None
        return ""

    # 种子
    seeds: list[str] = []
    for s in args.seed:
        got = resolve(s)
        if got is None:
            return 2
        if not got:
            print(f"[!] 种子不在 1.20 源码根里: {s}", file=sys.stderr)
            return 2
        seeds.append(got)
    for d in args.seed_dir:
        rel = d.replace("\\", "/").strip("/").removesuffix(".java")
        dot = rel.replace("/", ".")
        # 目录可以给相对路径（`common/entity/monster`）或 FQN；**含子目录**
        # （`common/entity/monster` 要连 `monster/slime/*` 一起算，包边界用前后点卡住）
        for f in sorted(known120):
            if ("." + dot + ".") in f and mapped(f) not in known121:
                seeds.append(f)
    seeds = sorted(dict.fromkeys(seeds))
    if not seeds and args.seed_dir:
        print(f"[!] --seed-dir 一个种子都没命中（目录给成相对路径或 FQN？）：{args.seed_dir}", file=sys.stderr)
        return 2
    if not seeds:
        print("[!] 没有种子（--seed / --seed-dir 至少要给一个）", file=sys.stderr)
        return 2

    # 每个种子各算各的可达闭包
    closures: dict[str, list[str]] = {}
    for s in seeds:
        seen = {s}
        stack = [s]
        while stack:
            cur = stack.pop()
            for nxt in deps.get(cur, ()):
                if nxt not in seen:
                    seen.add(nxt)
                    stack.append(nxt)
        closures[s] = sorted(seen - {s})

    freq: collections.Counter[str] = collections.Counter()
    for s in seeds:
        freq.update(closures[s])

    def cost(fqns: list[str]) -> int:
        return sum(blank_lines(r120[f]) for f in fqns)

    rows = []
    for s in seeds:
        cl = closures[s]
        rows.append({
            "seed": s,
            "closure": len(cl) + 1,
            "new": len(cl),
            "lines": blank_lines(r120[s]) + cost(cl),
            "self_lines": blank_lines(r120[s]),
            "in_121": mapped(s) in known121,
            "files": cl,
        })
    rows.sort(key=lambda r: (r["new"], -r["lines"]))

    light = [r for r in rows if r["new"] + 1 <= args.small]
    heavy = [r for r in rows if r["new"] + 1 > args.small]

    L: list[str] = []
    L.append("# 逐种子依赖闭包（`tools/port2native/seed_closures.py` 输出）")
    L.append("")
    L.append(f"- 种子 **{len(seeds)}** 个；1.21 侧可扩张类型 {len(expandable)} 个")
    L.append(f"- 口径与 `dep_subset.py` 一致：只往「1.21 侧还没有的类型」里扩张，"
             f"已满足的边算 0 成本、不继续走")
    if alias:
        L.append(f"- `--alias` {len(alias)} 条：" + ", ".join(f"`{k}` → `{v}`" for k, v in sorted(alias.items())))
    L.append("")
    L.append(f"## 按单独闭包从小到大（轻量 ≤{args.small} 个文件：**{len(light)}** 个种子）")
    L.append("")
    L.append("| 种子 | 闭包文件 | 其中新增 | 非空行 | 种子自身行 | 1.21 已有同名 |")
    L.append("| --- | ---: | ---: | ---: | ---: | :--: |")
    for r in light:
        L.append("| `%s` | %d | %d | %d | %d | %s |"
                 % (r["seed"].rsplit(".", 1)[-1], r["closure"], r["new"], r["lines"], r["self_lines"],
                    "⚠️ 是" if r["in_121"] else ""))
    L.append("")
    L.append(f"## 重量种子（闭包 >{args.small} 个文件：**{len(heavy)}** 个）")
    L.append("")
    L.append("| 种子 | 闭包文件 | 其中新增 | 非空行 | 种子自身行 | 1.21 已有同名 |")
    L.append("| --- | ---: | ---: | ---: | ---: | :--: |")
    for r in heavy:
        L.append("| `%s` | %d | %d | %d | %d | %s |"
                 % (r["seed"].rsplit(".", 1)[-1], r["closure"], r["new"], r["lines"], r["self_lines"],
                    "⚠️ 是" if r["in_121"] else ""))
    L.append("")
    hubs = [(f, n) for f, n in freq.most_common() if n >= 2]
    L.append(f"## 公共底座（出现在 ≥2 个种子的闭包里：**{len(hubs)}** 个）")
    L.append("")
    L.append("**先搬这些，后面每个种子都会变轻。**")
    L.append("")
    L.append("| 类型 | 被几个种子需要 | 非空行 | 自己单独搬时的闭包 |")
    L.append("| --- | ---: | ---: | ---: |")
    for f, n in hubs:
        own = closures.get(f)
        L.append("| `%s` | %d | %d | %s |"
                 % (f.replace("org.confluence.mod.", ""), n, blank_lines(r120[f]),
                    ("%d" % (len(own) + 1)) if own is not None else "（不是种子）"))
    L.append("")
    if args.detail:
        L.append("## 逐种子闭包明细")
        L.append("")
        for r in rows:
            if not r["files"]:
                L.append(f"### `{r['seed']}` —— 无新增依赖（{r['self_lines']} 行）")
                L.append("")
                continue
            L.append(f"### `{r['seed']}` —— 新增 {r['new']} 个 / {r['lines']} 行")
            L.append("")
            for f in r["files"]:
                L.append(f"- `{f.replace('org.confluence.mod.', '')}` —— {blank_lines(r120[f])} 行")
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
            json.dump({"rows": rows, "hubs": hubs}, fh, ensure_ascii=False, indent=2)

    print(json.dumps({"seeds": len(seeds), "light": len(light), "heavy": len(heavy),
                      "hubs": len(hubs),
                      "min_new": rows[0]["new"] if rows else None,
                      "max_new": rows[-1]["new"] if rows else None}, ensure_ascii=False))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
