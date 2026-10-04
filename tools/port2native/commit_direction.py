#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""提交方向分类：把 1.20.1 分叉点之后的提交分成「Port 化」与「内容」两类。

为什么需要
----------
1.20 侧有大量 `part N` 提交，做的是**把原生写法换成 PortLib 写法**
（例：`player.getInBlockState()` → `PortEntityExtension.getInBlockState(player)`、
`ByteBufCodecs.idMapper` → `PortByteBufCodecs.idMapper`）。
对 1.21 而言这类改动是**方向相反的**——1.21 要的就是原生写法，所以逐提交移植时它们是
`SKIP-PLATFORM`。真正需要动手的是"内容/逻辑"提交。

本工具按"新增行里有多少是 Port 引用"来判断方向：

| 判据 | 结论 | 1.21 侧动作 |
|---|---|---|
| 新增行以 Port 引用为主 | `port-ing` | SKIP-PLATFORM |
| 删除行以 Port 引用为主 | `de-port` | SKIP/COVERED（1.21 本来就是原生） |
| 新增行基本不含 Port 引用 | `content` | **要移植** |
| 两者都有且都不占多数 | `mixed` | 人工看 |

输入是 `git log -p` 的补丁文件（只含 *.java，避免资源文件把统计冲淡）：

    git -C <1.20仓库> log --reverse --no-merges --unified=0 --no-color \
        --format="C|%H|%ad|%s" --date=short 795ac9ccc..HEAD -- "*.java" > patches.txt
    python commit_direction.py --log patches.txt --out <notes目录>
"""

from __future__ import annotations

import argparse
import json
import pathlib
import re
import sys
from collections import defaultdict

HEADER_RE = re.compile(r"^C\|([0-9a-f]{7,40})\|(\d{4}-\d{2}-\d{2})\|(.*)$")
# 一行里出现这些就算"Port 引用"
PORT_REF_RE = re.compile(r"org\.mesdag\.portlib|\bPortLib\.extensions|\b(?:IPort|Port)[A-Z]\w*")

# integration 路径按 Q12 不移植
IGNORE_HINTS = ("/integration/",)


def read_text_any(path: pathlib.Path) -> str:
    raw = path.read_bytes()
    for enc in ("utf-8-sig", "utf-16", "utf-8"):
        try:
            text = raw.decode(enc)
            if "\x00" not in text[:400]:
                return text
        except UnicodeDecodeError:
            continue
    return raw.decode("utf-8", errors="ignore")


def parse(path: pathlib.Path) -> list[dict]:
    commits: list[dict] = []
    cur: dict | None = None
    in_ignored_file = False
    for line in read_text_any(path).splitlines():
        m = HEADER_RE.match(line)
        if m:
            cur = {"hash": m.group(1), "date": m.group(2), "subject": m.group(3),
                   "addedPort": 0, "removedPort": 0, "addedCode": 0, "removedCode": 0,
                   "addedFiles": 0, "files": []}
            commits.append(cur)
            continue
        if cur is None:
            continue
        if line.startswith("diff --git "):
            parts = line.split(" b/")
            fname = parts[-1] if len(parts) > 1 else line
            in_ignored_file = any(h in fname for h in IGNORE_HINTS)
            cur["addedFiles"] += 1
            cur["files"].append(fname)
            continue
        if line.startswith("+++") or line.startswith("---") or in_ignored_file:
            continue
        if line.startswith("+") and not line.startswith("+++"):
            if PORT_REF_RE.search(line):
                cur["addedPort"] += 1
            else:
                cur["addedCode"] += 1
        elif line.startswith("-") and not line.startswith("---"):
            if PORT_REF_RE.search(line):
                cur["removedPort"] += 1
            else:
                cur["removedCode"] += 1
    return [c for c in commits if c["addedPort"] or c["removedPort"] or c["addedCode"] or c["removedCode"]]


def classify(c: dict) -> str:
    ap, rp, ac, rc = c["addedPort"], c["removedPort"], c["addedCode"], c["removedCode"]
    added = ap + ac
    total_port = ap + rp
    if added == 0 and total_port == 0:
        return "no-code"
    if added > 0 and ap / added >= 0.6:
        return "port-ing"          # 把原生换成 Port 写法 -> 1.21 跳过
    if total_port > 0 and rp > ap and (ac + rc) < max(3, total_port * 0.5):
        return "de-port"           # 反过来去 Port 化 -> 1.21 更不需要动
    if added > 0 and ap / added < 0.2:
        return "content"           # 基本是纯逻辑/内容 -> 要移植
    return "mixed"


def main(argv: list[str] | None = None) -> int:
    ap = argparse.ArgumentParser(description="提交方向分类（Port 化 vs 内容）")
    ap.add_argument("--log", required=True, help="git log -p 的补丁文件")
    ap.add_argument("--out", required=True, help="输出目录")
    ap.add_argument("--base", default="")
    ap.add_argument("--head", default="")
    args = ap.parse_args(argv)

    commits = parse(pathlib.Path(args.log).resolve())
    for c in commits:
        c["direction"] = classify(c)

    kinds = defaultdict(int)
    for c in commits:
        kinds[c["direction"]] += 1

    port_ing = [c for c in commits if c["direction"] == "port-ing"]
    content = [c for c in commits if c["direction"] == "content"]
    mixed = [c for c in commits if c["direction"] == "mixed"]

    out_dir = pathlib.Path(args.out).resolve()
    out_dir.mkdir(parents=True, exist_ok=True)

    md = ["# 提交方向分类（Port 化 vs 内容）", "",
          f"- 范围：`{args.base or '<base>'}..{args.head or '<head>'}`，含代码改动的提交 **{len(commits)}** 个",
          "- 判据：新增行里 Port 引用占比 ≥60% 判为 `port-ing`（1.21 跳过）；<20% 判为 `content`（要移植）。",
          f"- **`content`（要移植）{len(content)} 个；`port-ing`（跳过）{len(port_ing)} 个；"
          f"`mixed` {len(mixed)} 个；其余 {len(commits) - len(content) - len(port_ing) - len(mixed)} 个**",
          "",
          "| 方向 | 提交数 |", "|---|---|"]
    for k, v in sorted(kinds.items(), key=lambda kv: -kv[1]):
        md.append(f"| {k} | {v} |")

    def table(title: str, rows: list[dict], limit: int = 0) -> None:
        md.extend(["", f"## {title}（{len(rows)}）", "",
                   "| 提交 | 日期 | 说明 | 文件 | +Port/-Port | +代码/-代码 |", "|---|---|---|---|---|---|"])
        show = rows[:limit] if limit else rows
        for c in show:
            subject = c["subject"].replace("|", "\\|")
            md.append(f"| `{c['hash'][:9]}` | {c['date']} | {subject} | {c['addedFiles']} | "
                      f"+{c['addedPort']}/-{c['removedPort']} | +{c['addedCode']}/-{c['removedCode']} |")
        if limit and len(rows) > limit:
            md.append(f"| … | | 另有 {len(rows) - limit} 个 | | | |")

    table("要移植的（content）", content)
    table("需人工判定（mixed）", mixed, limit=60)
    table("可跳过（port-ing：把原生换成 Port 写法）", port_ing, limit=40)

    (out_dir / "COMMIT-DIRECTIONS.md").write_text("\n".join(md), encoding="utf-8")
    (out_dir / "commit-directions.json").write_text(
        json.dumps({"range": {"base": args.base, "head": args.head},
                    "kinds": dict(kinds), "commits": commits}, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )
    print(json.dumps(dict(kinds), ensure_ascii=False, indent=2))
    print(f"要移植(content): {len(content)} 个；报告: {out_dir / 'COMMIT-DIRECTIONS.md'}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
