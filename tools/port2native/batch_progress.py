#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""批次进度：每个 1.20 提交的欠账文件现在还剩多少没搬。

为什么需要
----------
台账（PORT-LEDGER）是「逐提交判定」，但实际执行按工作包做，一个工作包会横跨很多提交
（编译依赖不允许按提交顺序切）。于是需要一个机器算的口径回答：
**这个提交碰过的文件里，有多少已经和 1.21 一致了？**

口径：把该提交改动的 java 文件（`git log --numstat`，含子模块净改动）逐个查当前
`file-lag.json` 的 verdict，按**新增行数加权**汇总：
- `done` = IN-SYNC（有效行重叠 ≥85%）
- `part` = PARTIAL（50~85%）
- `left` = LAGGING + MISSING（<50% / 1.21 没有）
并给出 `done/(done+part+left)` 作为该提交的完成度。完成度 = 100% 的行可以直接记 `PORTED`，
`0%` 的是没动的，中间的是做了一半。

用法：
    python batch_progress.py --repo . --numstat <tmp>/numstat-all.txt --out notes
"""

from __future__ import annotations

import argparse
import json
import pathlib
import re
import sys
from collections import Counter, defaultdict

HEADER_RE = re.compile(r"^C\|([0-9a-f]{7,40})")
NUMSTAT_RE = re.compile(r"^(\d+|-)\t(\d+|-)\t(.*)$")


def read_text_any(path: pathlib.Path) -> str:
    raw = path.read_bytes()
    for enc in ("utf-8-sig", "utf-16", "utf-8"):     # BOM 必须先于 utf-8，否则丢首行
        try:
            t = raw.decode(enc)
            if "\x00" not in t[:400]:
                return t
        except UnicodeDecodeError:
            continue
    return raw.decode("utf-8", errors="ignore")


def parse_numstat(path: pathlib.Path) -> dict[str, dict[str, int]]:
    commits: dict[str, dict[str, int]] = {}
    cur = None
    for line in read_text_any(path).splitlines():
        m = HEADER_RE.match(line)
        if m:
            cur = m.group(1)
            commits.setdefault(cur, {})
            continue
        if cur is None:
            continue
        nm = NUMSTAT_RE.match(line)
        if nm:
            commits[cur][nm.group(3)] = 0 if nm.group(1) == "-" else int(nm.group(1))
    return commits


DONE = {"IN-SYNC"}
PART = {"PARTIAL"}
LEFT = {"LAGGING", "MISSING"}


def main(argv: list[str] | None = None) -> int:
    ap = argparse.ArgumentParser(description="逐提交搬运进度（按 file-lag 当前状态加权）")
    ap.add_argument("--repo", required=True)
    ap.add_argument("--numstat", required=True)
    ap.add_argument("--out", required=True)
    args = ap.parse_args(argv)

    repo = pathlib.Path(args.repo).resolve()
    load = lambda n: json.loads((repo / "notes" / n).read_text(encoding="utf-8-sig"))
    ledger = load("port-ledger.json")["commits"]
    status = load("port-ledger-status.json")["commits"]
    lag = {r["path"]: r for r in load("file-lag.json")["rows"]}
    numstat = parse_numstat(pathlib.Path(args.numstat))

    rows = []
    for c in ledger:
        h = c["hash"]
        paths = {p: a for p, a in numstat.get(h, {}).items() if p.endswith(".java")}
        if not paths:
            continue
        bucket: Counter = Counter()
        unknown = 0
        for p, a in paths.items():
            r = lag.get(p)
            w = max(a, 1)
            if r is None:
                unknown += w
                continue
            v = r["verdict"]
            if v in DONE or v == "TINY":
                bucket["done"] += w
            elif v in PART:
                bucket["part"] += w
            elif v in LEFT:
                bucket["left"] += w
            else:
                unknown += w
        total = bucket["done"] + bucket["part"] + bucket["left"]
        pct = (bucket["done"] / total) if total else 0.0
        rows.append({"hash": h, "date": c["date"], "subject": c["subject"],
                     "status": status.get(h, {}).get("status", "TODO"),
                     "files": len(paths),
                     "done": bucket["done"], "part": bucket["part"], "left": bucket["left"],
                     "total": total, "unknown": unknown, "pct": round(pct, 3)})

    c = Counter()
    for r in rows:
        if r["total"] == 0:
            c["无可计权文件"] += 1
        elif r["pct"] >= 0.999:
            c["100% 已搬完"] += 1
        elif r["pct"] >= 0.5:
            c["≥50%"] += 1
        elif r["pct"] > 0:
            c["<50%"] += 1
        else:
            c["0%（未动）"] += 1
    print(json.dumps(dict(c), ensure_ascii=False))

    md = ["# 逐提交搬运进度（按 file-lag 当前状态加权）", "",
          "- 权重 = 该提交对每个文件的新增行数；`done`=IN-SYNC，`part`=PARTIAL，`left`=LAGGING+MISSING",
          f"- 参与统计的提交 **{len(rows)}** 个",
          f"- 分布：" + " / ".join(f"{k} {v}" for k, v in c.most_common()), "",
          "## 已搬完（完成度 100%，可据以把台账记为 PORTED）", "",
          "| 提交 | 日期 | 说明 | 文件数 | 权重 |", "|---|---|---|---|---|"]
    done = sorted([r for r in rows if r["total"] and r["pct"] >= 0.999], key=lambda r: -r["total"])
    for r in done:
        md.append(f"| `{r['hash'][:9]}` | {r['date']} | {r['subject'][:44]} | {r['files']} | {r['total']} |")

    md += ["", "## 进行中（0% < 完成度 < 100%）", "",
           "| 提交 | 日期 | 说明 | done | part | left | 完成度 |", "|---|---|---|---|---|---|---|"]
    mid = sorted([r for r in rows if r["total"] and 0 < r["pct"] < 0.999], key=lambda r: r["pct"])
    for r in mid[:80]:
        md.append(f"| `{r['hash'][:9]}` | {r['date']} | {r['subject'][:44]} | {r['done']} | {r['part']} | "
                  f"{r['left']} | {r['pct']:.0%} |")
    if len(mid) > 80:
        md.append(f"| … | | 另有 {len(mid)-80} 个 | | | | |")

    out = pathlib.Path(args.out).resolve()
    (out / "BATCH-PROGRESS.md").write_text("\n".join(md), encoding="utf-8")
    (out / "batch-progress.json").write_text(
        json.dumps({"counts": dict(c), "commits": rows}, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"报告: {out / 'BATCH-PROGRESS.md'}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
