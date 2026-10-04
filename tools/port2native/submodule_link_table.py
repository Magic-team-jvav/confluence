#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""子模块链接表：把每个主仓库提交映射到「子模块 + 旧SHA..新SHA + 子模块侧提交」。

为什么必须要有它
----------------
1.20 分支上有一类提交，主仓库这侧**只有一行 `M <子模块>`**（gitlink 跳变），真正的
代码改动在子模块仓库里。实测 `795ac9ccc..a75bda140`：
  - `submodule-only` 52 个提交（主仓库零代码改动）
  - `content+submodule` 115 个提交（两边都有）
而且"把类搬进 lib 模块"这类搬迁，在主仓库里看不出 rename：git 只报告
「删掉主仓库的文件 + 子模块指针跳变」，另一半是子模块仓库里的一次新增提交。
例子：主仓库 `1c012ccb1`「将饰品的药水效果转移至lib」只改了调用点 + 跳 Magic-Lib 指针；
真正的搬迁是 Magic-Lib 的 `0718c59`（新增 `GravitationEffect`/`HoneyEffect`/`LibEffects`/
`GravitationHandler`/`ILibEntity` + 若干 mixin，并把 `LibEffects`、`LibGameEvents` 拆位改名）。

本工具输出
----------
1. `SUBMODULE-LINKS.md`：每个子模块一节，逐行列出
   `主仓库提交 → 指针 old..new（短 SHA）→ 子模块侧提交列表（hash + 说明）`
2. `submodule-links.json`：同样的结构化数据，供其他工具消费

用法
----
    python submodule_link_table.py --super D:/Minecraft/1.20forge/confluence \
        --base 795ac9ccc --head a75bda140 --out D:/Minecraft/1.21neoforge/confluence/notes
"""

from __future__ import annotations

import argparse
import json
import pathlib
import subprocess
import sys
from collections import Counter
from dataclasses import dataclass, field

SUBMODULES = ("PortLib", "Confluence-Magic-Lib", "TerraCurio", "TerraFurniture", "TerraEntity", "TerraGuns")


@dataclass
class Range:
    commit: str = ""
    date: str = ""
    subject: str = ""
    old: str = ""
    new: str = ""
    sub_commits: list[dict] = field(default_factory=list)
    error: str = ""
    # 指针区间的**净改动**：区间里可能有"加了又删/改名"的提交，逐提交看会误判，净改动才是要移植的东西
    net_files: int = 0
    net_add: int = 0
    net_del: int = 0


def git(repo: pathlib.Path, *args: str) -> str:
    """跑 git 并返回 stdout；失败抛 RuntimeError（带命令与 stderr）。"""
    proc = subprocess.run(
        ["git", "-C", str(repo), *args],
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
    )
    if proc.returncode != 0:
        raise RuntimeError(f"git {' '.join(args)} @ {repo}\n{proc.stderr.strip()}")
    return proc.stdout


def try_git(repo: pathlib.Path, *args: str) -> str | None:
    try:
        return git(repo, *args)
    except (RuntimeError, OSError):
        return None


def build_for_submodule(super_repo: pathlib.Path, sub: str, base: str, head: str) -> list[Range]:
    sub_path = super_repo / sub
    # 主仓库里所有改动过该子模块指针的提交（时间正序）
    log = git(super_repo, "log", "--reverse", "--format=%H%x09%ad%x09%s", "--date=short",
              f"{base}..{head}", "--", sub)
    ranges: list[Range] = []
    for line in log.splitlines():
        if not line.strip():
            continue
        parts = line.split("\t")
        if len(parts) < 3:
            continue
        commit, date, subject = parts[0], parts[1], parts[2]
        item = Range(commit=commit, date=date, subject=subject)
        item.old = (try_git(super_repo, "rev-parse", f"{commit}^:{sub}") or "").strip()
        item.new = (try_git(super_repo, "rev-parse", f"{commit}:{sub}") or "").strip()
        if item.old and item.new and item.old != item.new and sub_path.is_dir():
            out = try_git(sub_path, "log", "--reverse", "--no-merges",
                          "--format=%H%x09%ad%x09%s", "--date=short", f"{item.old}..{item.new}")
            if out is None:
                item.error = "子模块对象缺失（需要 git submodule update / fetch）"
            else:
                for sline in out.splitlines():
                    if not sline.strip():
                        continue
                    sp = sline.split("\t")
                    if len(sp) >= 3:
                        item.sub_commits.append({"hash": sp[0], "date": sp[1], "subject": sp[2]})
                # 区间净改动
                numstat = try_git(sub_path, "diff", "--numstat", "-M", item.old, item.new)
                if numstat is not None:
                    for nline in numstat.splitlines():
                        cols = nline.split("\t")
                        if len(cols) >= 3:
                            item.net_files += 1
                            if cols[0].isdigit():
                                item.net_add += int(cols[0])
                            if cols[1].isdigit():
                                item.net_del += int(cols[1])
        elif not sub_path.is_dir():
            item.error = "子模块目录不存在（可能已被移除）"
        ranges.append(item)
    return ranges


def main(argv: list[str] | None = None) -> int:
    ap = argparse.ArgumentParser(description="生成子模块链接表")
    ap.add_argument("--super", required=True, help="主仓库路径")
    ap.add_argument("--base", required=True)
    ap.add_argument("--head", required=True)
    ap.add_argument("--out", required=True, help="输出目录")
    ap.add_argument("--submodule", action="append", default=None,
                    help="只处理指定子模块（可重复），默认全部")
    args = ap.parse_args(argv)

    super_repo = pathlib.Path(args.super).resolve()
    out_dir = pathlib.Path(args.out).resolve()
    out_dir.mkdir(parents=True, exist_ok=True)
    subs = args.submodule or list(SUBMODULES)

    data: dict[str, list[Range]] = {}
    for sub in subs:
        data[sub] = build_for_submodule(super_repo, sub, args.base, args.head)

    md = ["# 子模块链接表（主仓库提交 ↔ 子模块提交）", "",
          f"- 主仓库：`{super_repo}`，范围 `{args.base}..{args.head}`",
          "- 用途：主仓库只看到 `M <子模块>`（指针跳变）时，到右侧子模块提交里找真正的改动。",
          "- 移植时：把这些子模块提交的改动落到 1.21 侧同一模块（模块已退役的按 Q3 落到主模组内联路径）。",
          ""]
    summary = {}
    for sub, ranges in data.items():
        with_subs = [r for r in ranges if r.sub_commits]
        total_sub = sum(len(r.sub_commits) for r in ranges)
        summary[sub] = {"pointerBumps": len(ranges), "bumpsWithSubCommits": len(with_subs),
                        "subCommits": total_sub,
                        "errors": len([r for r in ranges if r.error])}
        md += [f"## {sub}", "",
               f"- 指针跳变提交：**{len(ranges)}** 次；其中有子模块提交可查的 {len(with_subs)} 次；",
               f"  共 **{total_sub}** 个子模块提交；读取失败 {summary[sub]['errors']} 次。", ""]
        md += ["| 主仓库提交 | 日期 | 说明 | 指针 old..new | 区间净改动 | 子模块提交 |", "|---|---|---|---|---|---|"]
        for r in ranges:
            span = f"`{r.old[:9]}..{r.new[:9]}`" if r.old and r.new else "?"
            net = f"{r.net_files} 文件 +{r.net_add}/-{r.net_del}" if r.net_files else "（无净改动）"
            if r.error:
                cell = f"⚠️ {r.error}"
                if "对象缺失" in r.error:
                    cell += f"<br>补救：`git -C {sub} fetch --all && git -C {sub} log {r.old[:9]}..{r.new[:9]}`"
            elif not r.sub_commits:
                cell = "（无新提交）"
            else:
                cell = "<br>".join(f"`{s['hash'][:9]}` {s['subject']}" for s in r.sub_commits[:6])
                if len(r.sub_commits) > 6:
                    cell += f"<br>…另有 {len(r.sub_commits) - 6} 个"
            md.append(f"| `{r.commit[:9]}` | {r.date} | {r.subject} | {span} | {net} | {cell} |")
        md.append("")

    (out_dir / "SUBMODULE-LINKS.md").write_text("\n".join(md), encoding="utf-8")
    (out_dir / "submodule-links.json").write_text(
        json.dumps({"range": {"base": args.base, "head": args.head}, "summary": summary,
                    "links": {k: [vars(r) for r in v] for k, v in data.items()}},
                   ensure_ascii=False, indent=2),
        encoding="utf-8",
    )

    print(json.dumps(summary, ensure_ascii=False, indent=2))
    print(f"链接表: {out_dir / 'SUBMODULE-LINKS.md'}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
