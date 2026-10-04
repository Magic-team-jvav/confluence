#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""台账第二轮分诊：用 commit-lag（Port 免疫的文件级欠账）给 TODO 行出结论。

第一轮分诊（TRIAGE-PASS1.md）用的 commit_coverage.py 有两个盲区，导致 124 行「无覆盖率条目」——
它们恰恰是 1.20 采用 PortLib 之后的新增内容。本轮改用 file_lag.py 的提交级汇总。

规则（只对**还没有状态**的行出结论，已判定的一律不动）：
  A1a SKIP-PORTLIB        PORTLIB-ONLY：该提交改的全是 PortLib 里的 java 文件
                        （PortLib 是 1.20 专有的 1.21.1 API 模拟层，1.21 侧不需要）
  A1b SKIP-1.20-REVERTED  DEAD-ONLY：该提交改过的 java 文件在 1.20 HEAD 已全部不存在
                        （1.20 自己后来删掉/替换了）→ 没有可移植物
  A2 DEFER-ASSETS         NO-JAVA：该提交没有 java 改动（资源 / gradle / 文档）
  A3 COVERED              commit-lag COVERED：1.21 侧该提交涉及的文件基本已同步
  A4 TODO                 commit-lag NEEDS-PORT / PARTIAL：进工作队列，附缺失/滞后文件证据
  A5 REVIEW               两个工具结论严重冲突：coverage NEEDS-PORT×lag COVERED
                        或 coverage COVERED×lag NEEDS-PORT → 人工抽查

用法：
    python triage_pass2.py --repo <1.21仓库> --out notes
"""

from __future__ import annotations

import argparse
import json
import pathlib
from collections import Counter, defaultdict

PREFIX = "ConfluenceOtherworld/src/main/java/org/confluence/mod/"


def short(path: str) -> str:
    return path.replace(PREFIX, "CO:")


def main(argv: list[str] | None = None) -> int:
    ap = argparse.ArgumentParser(description="台账第二轮分诊（file-lag 驱动）")
    ap.add_argument("--repo", required=True)
    ap.add_argument("--out", required=True)
    args = ap.parse_args(argv)

    repo = pathlib.Path(args.repo).resolve()
    out_dir = pathlib.Path(args.out).resolve()
    load = lambda n: json.loads((repo / "notes" / n).read_text(encoding="utf-8-sig"))

    status = load("port-ledger-status.json")["commits"]
    ledger = load("port-ledger.json")["commits"]
    cov = {r["hash"]: r for r in load("commit-coverage.json")["results"]}
    lag = {c["hash"]: c for c in load("commit-lag.json")["commits"]}
    fl = {r["path"]: r for r in load("file-lag.json")["rows"]}
    numstat = load("commit-lag.json")  # 权重明细在 commits[].weights

    props: list[dict] = []
    already: list[dict] = []
    for c in ledger:
        h = c["hash"]
        if h in status:
            already.append({"hash": h, "date": c["date"], "subject": c["subject"],
                            "status": status[h].get("status", "?"),
                            "note": status[h].get("note", "")})
            continue
        e = lag.get(h)
        cv = cov.get(h)
        covv = cv["verdict"] if cv else "NO-COV"
        if e is None:
            rule, verdict = "A0", "REVIEW"
        elif e["verdict"] == "PORTLIB-ONLY":
            rule, verdict = "A1a", "SKIP-PORTLIB"
        elif e["verdict"] == "DEAD-ONLY":
            rule, verdict = "A1b", "SKIP-1.20-REVERTED"
        elif e["verdict"] == "NO-JAVA":
            rule, verdict = "A2", "DEFER-ASSETS"
        elif e["verdict"] == "COVERED":
            rule, verdict = ("A5", "REVIEW") if covv == "NEEDS-PORT" else ("A3", "COVERED")
        else:
            rule, verdict = ("A5", "REVIEW") if covv == "COVERED" else ("A4", "TODO")
        w = e["weights"] if e else {}
        bad = w.get("LAGGING", 0) + w.get("MISSING", 0)
        props.append({"hash": h, "date": c["date"], "subject": c["subject"],
                      "rule": rule, "proposal": verdict, "coverage": covv,
                      "lag": e["verdict"] if e else "NO-ROW", "weights": w,
                      "badWeight": bad, "totalWeight": e["total"] if e else 0,
                      "deadPaths": e["deadPaths"] if e else 0})

    counts = Counter(p["proposal"] for p in props)
    print(json.dumps(dict(counts), ensure_ascii=False))
    md = ["# 台账第二轮分诊（file-lag 驱动，Port 免疫）", "",
          f"- 参与分诊：**{len(props)}** 行还没有状态的行（已完成判定的 {len(already)} 行见文末「已落账」）",
          "- 度量：`commit-lag` = 该提交改动文件的有效行欠账加权；有效行剔除 import/package、"
          "Port 引用、平台 API 引用（详见 `FILE-LAG.md`）",
          "- 规则 A1~A5 见 `tools/port2native/triage_pass2.py` 文件头",
          f"- 结果：**" + " / ".join(f"{k} {v}" for k, v in counts.most_common()) + "**", ""]

    def table(rows: list[dict], title: str, limit: int = 300, with_files: bool = False) -> None:
        md.append(f"## {title}（{len(rows)}）")
        md.append("")
        md.append("| 提交 | 日期 | 说明 | coverage | lag | 欠账权重 | 全权重 | 幽灵文件 | 规则 |")
        md.append("|---|---|---|---|---|---|---|---|---|")
        for p in rows[:limit]:
            md.append(f"| `{p['hash'][:9]}` | {p['date']} | {p['subject'][:40]} | {p['coverage']} | "
                      f"{p['lag']} | {p['badWeight']} | {p['totalWeight']} | {p['deadPaths']} | {p['rule']} |")
        if len(rows) > limit:
            md.append(f"| … | | 另有 {len(rows) - limit} 个 | | | | | | |")
        md.append("")

    for verdict in ("SKIP-PORTLIB", "SKIP-1.20-REVERTED", "DEFER-ASSETS", "COVERED", "REVIEW"):
        rows = sorted([p for p in props if p["proposal"] == verdict],
                      key=lambda p: -(p["totalWeight"] + p["deadPaths"]))
        table(rows, verdict)

    todo = sorted([p for p in props if p["proposal"] == "TODO"], key=lambda p: -p["badWeight"])
    table(todo, "TODO（工作队列，按欠账权重降序）", limit=250)

    # --- 工作队列概览 ---
    by_date = Counter(p["date"][:7] for p in todo)
    bad_total = sum(p["badWeight"] for p in todo)
    md += ["## 工作队列概览", "",
           f"- TODO 行数 **{len(todo)}**，欠账权重合计 **{bad_total}**（权重 = 该提交认领文件的**新增行数**和）",
           f"- 欠账权重 ≥1000 的行：**{sum(1 for p in todo if p['badWeight'] >= 1000)}**；"
           f"≥100 的行：**{sum(1 for p in todo if 100 <= p['badWeight'] < 1000)}**；"
           f"<100 的行：**{sum(1 for p in todo if p['badWeight'] < 100)}**",
           "",
           "| 月份 | TODO 行数 |", "|---|---|"]
    for k in sorted(by_date):
        md.append(f"| {k} | {by_date[k]} |")
    md.append("")

    # --- 已落账 ---
    md += ["## 已落账（`port-ledger-status.json` 里的全部判定）", "",
           f"- 共 **{len(already)}** 条；本轮 A1a/A1b/A2/A3 写入了其中 24 条，"
           f"其余 {len(todo)} 行保持「无状态 = TODO」",
           "- `notes/PORT-RESIDUALS.md` 另记行级复核抠出的 2 条微量残留",
           "",
           "| 提交 | 日期 | 状态 | 说明 |", "|---|---|---|---|"]
    st_order = ["SKIP-PORTLIB", "SKIP-1.20-REVERTED", "DEFER-ASSETS", "COVERED"]
    for a in sorted(already, key=lambda a: (st_order.index(a["status"]) if a["status"] in st_order else 99,
                                            a["hash"])):
        md.append(f"| `{a['hash'][:9]}` | {a['date']} | {a['status']} | {a['subject'][:44]} |")
    md.append("")

    (out_dir / "TRIAGE-PASS2.md").write_text("\n".join(md), encoding="utf-8")
    (out_dir / "triage-pass2.json").write_text(
        json.dumps({"counts": dict(counts), "proposals": props}, ensure_ascii=False, indent=2),
        encoding="utf-8")
    print(f"报告: {out_dir / 'TRIAGE-PASS2.md'}")
    return 0


if __name__ == "__main__":
    import sys
    sys.exit(main())
