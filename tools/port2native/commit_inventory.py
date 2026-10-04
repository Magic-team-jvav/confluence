#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""把 1.20.1 分叉点之后的提交整理成"逐提交移植台账"。

为什么是逐提交而不是整树
------------------------
1.20 分支在分叉后做了大量**类的位置变动**：模块内搬（实测 588 处 rename，例如
`common/entity/**` → `util/entity/**`）、以及把类搬进 lib 模块（这种在主仓库里表现为
"删文件 + 子模块 gitlink 跳变"，git 认不出 rename，真正的另一半提交在子模块仓库里）。
整树路径对比会把这类变动当成"删除 + 新增"，直接丢掉对应关系；逐提交走 git，
`-M -C` 的改名检测能给出行级对应，搬进子模块的那部分也能靠 gitlink 跳变识别出来。

本工具做的事
------------
读取 `git log --reverse --no-merges --name-status -M -C --date=short
--pretty="C|%H|%ad|%s" <base>..<head>` 的输出，产出：

1. `port-ledger.json`：每个提交一条结构化记录（改动文件、增删改、改名对、gitlink 跳变、
   粗分类、移动族）。
2. `PORT-LEDGER.md`：**按时间正序**（= 应当的移植顺序）的可勾选台账，供逐提交推进时填写状态。
3. 控制台摘要：改名/移动族、疑似"搬进子模块"的提交、平台性 vs 内容性提交计数。

用法
----
    # 先取日志（在 1.20 仓库里）
    git -C D:/Minecraft/1.20forge/confluence log --reverse --no-merges \
        --name-status -M -C --date=short --pretty="C|%H|%ad|%s" <base>..<head> > gitlog.txt

    python commit_inventory.py --log gitlog.txt --out D:/Minecraft/1.21neoforge/confluence/notes
"""

from __future__ import annotations

import argparse
import json
import pathlib
import re
import sys
from collections import Counter, defaultdict

HEADER_RE = re.compile(r"^C\|([0-9a-f]{7,40})\|(\d{4}-\d{2}-\d{2})\|(.*)$")
STATUS_RE = re.compile(r"^([AMDRCT])(\d{0,3})\s+(.+)$")

MODULE_DIRS = (
    "ConfluenceOtherworld",
    "Confluence-Magic-Lib",
    "TerraCurio",
    "TerraFurniture",
    "TerraEntity",
    "TerraGuns",
    "PortLib",
)

GITLINK = "<submodule>"

# 粗分类：命中即可（用于给台账排序，不代替人工判断）
PLATFORM_HINTS = (
    "build.gradle", "gradle.properties", "settings.gradle", "gradlew",
    "mods.toml", "neoforge.mods.toml", "mixins.json", ".gitmodules", ".github/",
)
CONTENT_HINTS = (".java",)

# 刻意不移植的路径（决策 Q12）：1.20.1 **故意删掉了全部第三方集成**，而 1.21.1 要保留自己的集成。
# 因此凡是只动这些路径的提交，在 1.21 侧一律不动。
DEFAULT_IGNORE_PATHS = ("/integration/",)


def is_ignored(path: str, ignore_hints: tuple[str, ...]) -> bool:
    return any(h in path for h in ignore_hints)


def module_of(path: str) -> str:
    head = path.split("/", 1)[0]
    return head if head in MODULE_DIRS else "(repo-root)"


def classify(commit: dict, ignore_hints: tuple[str, ...] = DEFAULT_IGNORE_PATHS) -> str:
    files = commit["files"]
    if not files:
        return "empty"          # 只动子模块指针
    # 子模块指针变更在 --name-status 里就是"路径=子模块目录名"
    sub_paths = [f["path"] for f in files if f["path"] in MODULE_DIRS]
    real_files = [f for f in files if f["path"] not in MODULE_DIRS]
    ignored = [f for f in real_files if is_ignored(f["path"], ignore_hints)]
    kept = [f for f in real_files if not is_ignored(f["path"], ignore_hints)]
    suffix = "+integration" if ignored and kept else ""
    if kept and not sub_paths and all(
        any(h in f["path"] for h in PLATFORM_HINTS) for f in kept
    ):
        return "platform" + suffix
    if not kept and ignored:
        return "integration-only"          # Q12：1.21 保留集成，不动
    if not kept and sub_paths:
        return "submodule-only" + suffix
    java = [f for f in kept if f["status"] in "AMDRC" and f["path"].endswith(".java")]
    if kept:
        if sub_paths:
            return ("content+submodule" if java else "assets+submodule") + suffix
        return ("content" if java else "assets/other") + suffix
    return "other"


def read_json(path: pathlib.Path) -> dict:
    """读 JSON，容忍 PowerShell 写出的 BOM（utf-8-sig）与 UTF-16。"""
    try:
        return json.loads(path.read_text(encoding="utf-8-sig"))
    except (UnicodeDecodeError, json.JSONDecodeError):
        return json.loads(read_text_any(path))


def read_text_any(path: pathlib.Path) -> str:
    """PowerShell 的重定向可能是 UTF-16LE、也可能是带 BOM 的 UTF-8 —— 都试一遍。"""
    raw = path.read_bytes()
    for enc in ("utf-8-sig", "utf-16", "utf-8"):
        try:
            text = raw.decode(enc)
            if "\x00" not in text[:400]:
                return text
        except UnicodeDecodeError:
            continue
    return raw.decode("utf-8", errors="ignore")


def parse_log(path: pathlib.Path, ignore_hints: tuple[str, ...] = DEFAULT_IGNORE_PATHS) -> list[dict]:
    commits: list[dict] = []
    current: dict | None = None

    for raw in read_text_any(path).splitlines():
        line = raw.rstrip("\n")
        m = HEADER_RE.match(line)
        if m:
            current = {
                "hash": m.group(1),
                "date": m.group(2),
                "subject": m.group(3),
                "files": [],
                "renames": [],
                "gitlinkBumps": [],
            }
            commits.append(current)
            continue
        if current is None or not line.strip():
            continue
        sm = STATUS_RE.match(line)
        if not sm:
            continue
        status, score, rest = sm.group(1), sm.group(2), sm.group(3)
        if status in ("R", "C"):
            parts = rest.split("\t") if "\t" in rest else rest.split()
            if len(parts) >= 2:
                src, dst = parts[0], parts[-1]
                current["renames"].append({"from": src, "to": dst, "score": int(score or 0),
                                           "kind": "rename" if status == "R" else "copy"})
                if dst == GITLINK or src == GITLINK:
                    current["gitlinkBumps"].append(dst if dst != GITLINK else src)
                continue
        fpath = rest.strip()
        entry = {"status": status, "path": fpath}
        current["files"].append(entry)
        # git 把子模块指针变更报成"路径就是子模块目录名"（如 Confluence-Magic-Lib），而不是 <submodule>
        if fpath in MODULE_DIRS or fpath == GITLINK or "submodule" in fpath:
            current["gitlinkBumps"].append(fpath)

    for c in commits:
        c["modules"] = sorted({module_of(f["path"]) for f in c["files"]}
                              | {module_of(r["from"]) for r in c["renames"]}
                              | {module_of(r["to"]) for r in c["renames"]})
        c["classify"] = classify(c, ignore_hints)
    return commits


def move_families(commits: list[dict]) -> Counter:
    fam: Counter = Counter()
    for c in commits:
        for r in c["renames"]:
            src_dir = "/".join(r["from"].split("/")[:-1])
            dst_dir = "/".join(r["to"].split("/")[:-1])
            if src_dir != dst_dir:
                fam[f"{src_dir}  →  {dst_dir}"] += 1
    return fam


def suspicious_lib_moves(commits: list[dict]) -> list[dict]:
    """删了 java 文件、同时跳了某个子模块指针 —— 疑似"把类搬进 lib/子模块"。"""
    out = []
    for c in commits:
        deleted_java = [f["path"] for f in c["files"]
                        if f["status"] == "D" and f["path"].endswith(".java")]
        if deleted_java and c["gitlinkBumps"]:
            out.append({
                "hash": c["hash"], "date": c["date"], "subject": c["subject"],
                "deletedJava": len(deleted_java),
                "submodules": c["gitlinkBumps"],
                "sample": deleted_java[:6],
            })
    return out


def main(argv: list[str] | None = None) -> int:
    ap = argparse.ArgumentParser(description="生成逐提交移植台账")
    ap.add_argument("--log", required=True, help="git log --name-status 输出文件")
    ap.add_argument("--out", required=True, help="输出目录（写成 notes/PORT-LEDGER.md 与 port-ledger.json）")
    ap.add_argument("--submodule-links", default=None,
                    help="submodule-links.json（由 submodule_link_table.py 生成）：给出后，"
                         "每行会标注该提交对应的子模块侧提交，提示要不要去子模块看")
    ap.add_argument("--base", default="", help="范围起点（写进台账头部，便于复现）")
    ap.add_argument("--head", default="", help="范围终点")
    ap.add_argument("--ignore-path", action="append", default=None,
                    help="刻意不移植的路径片段（可重复）。默认 /integration/："
                         "1.20.1 故意删掉全部第三方集成，1.21.1 要保留自己的集成（决策 Q12）")
    ap.add_argument("--status", default=None,
                    help="移植状态文件（hash -> {status,target,note}）。台账的最后一列从这里读，"
                         "这样重新生成台账不会丢掉已填的状态。默认 <out>/port-ledger-status.json")
    ap.add_argument("--residue", default=None,
                    help="污染残留文件清单（如 notes/poison-residue-files.txt）：给出后，每行会标注"
                         "该提交触及的文件里有多少落在残留清单中（这些内容不得当成正确基线照抄）")
    args = ap.parse_args(argv)

    ignore_hints = tuple(args.ignore_path) if args.ignore_path else DEFAULT_IGNORE_PATHS
    residue: set[str] = set()
    if args.residue:
        residue_path = pathlib.Path(args.residue).resolve()
        if residue_path.is_file():
            residue = {l.strip() for l in read_text_any(residue_path).splitlines() if l.strip()}

    log_path = pathlib.Path(args.log).resolve()
    out_dir = pathlib.Path(args.out).resolve()
    out_dir.mkdir(parents=True, exist_ok=True)

    commits = parse_log(log_path, ignore_hints)
    fam = move_families(commits)

    # 状态文件：台账最后一列从外部 JSON 读，重新生成台账不会丢状态
    status_path = pathlib.Path(args.status).resolve() if args.status else out_dir / "port-ledger-status.json"
    statuses: dict[str, dict] = {}
    if status_path.is_file():
        statuses = read_json(status_path).get("commits", {})
    else:
        status_path.write_text(
            json.dumps({"$note": "移植状态；key = 1.20 侧提交全 hash。status: TODO/PORTED/COVERED/"
                                 "SKIP-PLATFORM/SKIP-1.21-KEEPS/SKIP-PORTLIB/SKIP-1.20-REVERTED/"
                                 "DEFER-ASSETS/DEFER-ARCH/DO-NOT-PORT/MOVED/LOST?；"
                                 "target = 1.21 侧提交或路径；note = 理由",
                        "commits": {}}, ensure_ascii=False, indent=2),
            encoding="utf-8")

    # 可选：把子模块链接表合进来，标注每个提交在子模块侧对应的提交数
    sub_links: dict[str, list[str]] = {}
    if args.submodule_links:
        links_path = pathlib.Path(args.submodule_links).resolve()
        links = read_json(links_path)
        for sub, entries in (links.get("links") or {}).items():
            for entry in entries:
                subs = [s["hash"][:9] for s in entry.get("sub_commits", [])]
                if subs:
                    sub_links.setdefault(entry["commit"], []).append(f"{sub}×{len(subs)}")
        for c in commits:
            c["submoduleSideCommits"] = sub_links.get(c["hash"], [])
    lib_moves = suspicious_lib_moves(commits)
    kinds = Counter(c["classify"] for c in commits)
    moved_commits = [c for c in commits if c["renames"]]

    md: list[str] = [
        "# 1.20.1 → 1.21.1 逐提交移植台账",
        "",
        f"- 范围：`{args.base or '<base>'}..{args.head or '<head>'}`（1.20 侧 `forge-dev/1.20.1`）",
        f"- 提交数：**{len(commits)}**（已排除 merge；含改名的提交 {len(moved_commits)} 个，改名/复制事件 "
        f"{sum(len(c['renames']) for c in commits)} 处）",
        "- 顺序即移植顺序（**从分叉点向新，时间正序**）；请从上往下推进，填最后一列状态。",
        "",
        "状态取值：`TODO` / `PORTED`（已按 1.21 原生改写并提交）/ `COVERED`（1.21 侧已有等价实现，含机器判定）/ "
        " `SKIP-PLATFORM`（1.21 已有的平台性改动）/ `SKIP-1.21-KEEPS`（integration：1.20 刻意删、1.21 保留）/ "
        " `SKIP-PORTLIB`（整提交只动 PortLib——1.20 专有的 1.21.1 API 模拟层，1.21 侧不需要）/ "
        " `SKIP-1.20-REVERTED`（改动文件在 1.20 HEAD 已不存在，1.20 自己后来删了）/ "
        " `MOVED`（对应到 1.21 侧的另一路径）/ `DEFER-ASSETS`（资源类，随所属功能提交一起处理）/ "
        " `DEFER-ARCH`（架构级搬迁，归 Phase 1 收割 / Phase 3 模块对齐）/ `DO-NOT-PORT`（污染提交，永不移植）/ "
        " `LOST?`（判定为无需移植但不确定，需复核）",
        "",
        "## 概况",
        "",
        "| 类别 | 提交数 |",
        "|---|---|",
    ]
    for k, v in kinds.most_common():
        md.append(f"| {k} | {v} |")

    ignored_only = sum(v for k, v in kinds.items() if k == "integration-only")
    with_ignored = sum(v for k, v in kinds.items() if k.endswith("+integration"))
    md += ["", "### 可直接跳过的行（Q12：integration 不移植）", "",
           f"- `integration-only`：**{ignored_only}** 个提交（整提交只动集成，1.21 侧一律不动）",
           f"- 含集成改动的提交：**{with_ignored}** 个（其余改动仍需按循环判定）",
           f"- 当前状态文件：`{status_path.name}`"
           f"（已填 {len(statuses)} 条；状态取值见本文件顶部说明）", ""]
    if statuses:
        st_kinds = Counter(v.get("status", "TODO") for v in statuses.values())
        md += ["| 已填状态 | 条数 |", "|---|---|"]
        for k, v in st_kinds.most_common():
            md.append(f"| {k} | {v} |")
        md.append("")

    if residue:
        rows_with = []
        for c in commits:
            n = len({f["path"] for f in c["files"]} & residue)
            if n:
                rows_with.append((c, n))
        md += ["", "### 污染残留警告（来源提交 `dfcc5c041`）", "",
               f"- 残留清单：{len(residue)} 个文件（`notes/poison-residue-files.txt`）",
               f"- **{len(rows_with)} 个提交触碰了残留文件**（表内「污染残留」列给出数量）",
               "- 这些提交的内容**不得照抄 1.20 的当前实现**：那批改动大部分是错的、部分被回退、部分残留仍在影响逻辑；",
               "  遇到可疑处优先以 **1.21 侧现有实现**为准（1.21 分支没经历过这次事故）。详见 `notes/POISON-dfcc5c041.md`。",
               "",
               "| 提交 | 日期 | 说明 | 残留文件数 |", "|---|---|---|---|"]
        for c, n in sorted(rows_with, key=lambda x: -x[1])[:15]:
            md.append(f"| `{c['hash'][:9]}` | {c['date']} | {c['subject'][:44]} | {n} |")
        md.append("")

    md += ["", "## 子模块指针跳变统计", "",
           "> 主仓库这侧只看到指针跳变时，**真正的代码改动在子模块仓库里**。",
           "> 移植这类提交必须去子模块仓库按 `旧SHA..新SHA` 找出对应的子模块提交。",
           "> PortLib 的跳变按 Q2（1.21 不引入 PortLib）**不移植**，属噪音。", "",
           "| 子模块 | 被跳变的提交数 |", "|---|---|"]
    sub_counts: Counter = Counter()
    for c in commits:
        for f in c["files"]:
            if f["path"] in MODULE_DIRS:
                sub_counts[f["path"]] += 1
    for k, v in sub_counts.most_common():
        md.append(f"| `{k}` | {v} |")

    md += ["", "## 类的位置变动（模块内）", "", "| 移动族 | 次数 |", "|---|---|"]
    for k, v in fam.most_common(25):
        md.append(f"| `{k}` | {v} |")

    md += ["", "## 疑似「搬进子模块/lib」的提交（删 java + 子模块指针跳变）", "",
           "> 这类在主仓库里看不出 rename：真正的另一半提交在子模块仓库（Confluence-Magic-Lib 等）里，",
           "> 移植时要到子模块仓库按同期提交去找对应的新增文件。", "",
           "| 提交 | 日期 | 说明 | 删掉的 java | 子模块 |", "|---|---|---|---|---|"]
    for item in lib_moves:
        md.append(f"| `{item['hash'][:9]}` | {item['date']} | {item['subject']} | "
                  f"{item['deletedJava']} | {', '.join(item['submodules'])} |")

    md += ["", "## 提交清单", "",
           "| # | 提交 | 日期 | 说明 | 模块 | 改动 | 移动 | 子模块侧提交 | 类别 | 1.21 侧对应 | 状态 |",
           "|---|---|---|---|---|---|---|---|---|---|---|"]
    for i, c in enumerate(commits, start=1):
        rename_note = ""
        if c["renames"]:
            first = c["renames"][0]
            rename_note = f"`{first['from'].split('/')[-1]}`→`{first['to'].split('/')[-1]}`"
            if len(c["renames"]) > 1:
                rename_note += f" 等{len(c['renames'])}处"
        changed = (f"+{sum(1 for f in c['files'] if f['status'] == 'A')}"
                   f" ~{sum(1 for f in c['files'] if f['status'] == 'M')}"
                   f" -{sum(1 for f in c['files'] if f['status'] == 'D')}")
        subject = c["subject"].replace("|", "\\|")
        sub_cell = ", ".join(c.get("submoduleSideCommits", [])) or ""
        # 污染残留：该提交触及的文件里有多少落在 dfcc5c041 的残留清单中
        overlap = len({f["path"] for f in c["files"]} & residue) if residue else 0
        residue_cell = f"⚠️ {overlap}" if overlap else ""
        st = statuses.get(c["hash"], {})
        status = st.get("status", "TODO")
        target_cell = st.get("target", "") or ""
        if st.get("note"):
            target_cell = (f"{target_cell} — {st['note']}").lstrip(" —")
        md.append(
            f"| {i} | `{c['hash'][:9]}` | {c['date']} | {subject} | "
            f"{', '.join(c['modules'])} | {changed} | {rename_note} | {sub_cell} | {residue_cell} | "
            f"{c['classify']} | {target_cell} | {status} |"
        )

    (out_dir / "PORT-LEDGER.md").write_text("\n".join(md), encoding="utf-8")
    (out_dir / "port-ledger.json").write_text(
        json.dumps({"range": {"base": args.base, "head": args.head},
                    "total": len(commits), "kinds": dict(kinds),
                    "moveFamilies": fam.most_common(200),
                    "suspiciousLibMoves": lib_moves,
                    "commits": commits}, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )

    print(json.dumps({
        "commits": len(commits),
        "withRenames": len(moved_commits),
        "renameEvents": sum(len(c["renames"]) for c in commits),
        "kinds": dict(kinds),
        "suspiciousLibMoves": len(lib_moves),
    }, ensure_ascii=False, indent=2))
    print("移动族 Top10:")
    for k, v in fam.most_common(10):
        print(f"  {v:4}x {k}")
    print(f"台账: {out_dir / 'PORT-LEDGER.md'}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
