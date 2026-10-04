#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""把「主仓库提交 → 子模块净改动」也写成 numstat 格式，供 file_lag.py 的提交级汇总使用。

为什么需要
----------
`git log --numstat` 在主仓库里对子模块只显示一行 gitlink（`-  -  Confluence-Magic-Lib`），
子模块内部改了哪些文件完全看不到。于是 `21b060ec6 饰品能力全改为datamap`（子模块 15 文件
+804/-510）这种**纯子模块内容提交**会被 file_lag 误判成 `NO-JAVA`（看起来像资源改动）。

做法：读主仓库 gitlink 变化（`--raw`，mode 160000），对每个 (old, new) 用
`git -C <主仓库>/<子模块> diff --numstat old new` 取出净改动，路径前缀加上子模块名，
输出与 `git log --numstat` 同形的文本，直接和主仓库的 numstat 文件拼接即可。

不存在的 SHA（贡献者 fork 里的提交、已删除的子模块）会记进 skipped 统计，不报错。

用法：
    python submodule_numstat.py --super <1.20仓库> --range 795ac9ccc..18221c338 --out sub-numstat.txt
"""

from __future__ import annotations

import argparse
import pathlib
import re
import subprocess
import sys
from collections import Counter

HEADER_RE = re.compile(r"^C\|([0-9a-f]{7,40})")
RAW_RE = re.compile(r"^:(\d{6}) (\d{6}) ([0-9a-f]{7,40}) ([0-9a-f]{7,40}) ([A-Z])\t(.*)$")
NULL = "0" * 40


def run_git(cwd: pathlib.Path, args: list[str]) -> tuple[int, str]:
    p = subprocess.run(["git", "-C", str(cwd)] + args, capture_output=True)
    return p.returncode, p.stdout.decode("utf-8", "ignore")


def main(argv: list[str] | None = None) -> int:
    ap = argparse.ArgumentParser(description="子模块净改动 -> numstat 文本")
    ap.add_argument("--super", required=True, help="1.20 主仓库根")
    ap.add_argument("--range", required=True)
    ap.add_argument("--out", required=True)
    ap.add_argument("--modules", default="", help="只看这些子模块（逗号分隔），留空=全部")
    args = ap.parse_args(argv)

    sup = pathlib.Path(args.super).resolve()
    only = {m.strip() for m in args.modules.split(",") if m.strip()}
    if not only:
        # 未指定时从 .gitmodules 取；gitlink 模式（160000）仍然强制校验
        gm = sup / ".gitmodules"
        if gm.is_file():
            tex = gm.read_text(encoding="utf-8", errors="ignore")
            only = set(re.findall(r"^\s*path\s*=\s*(\S+)\s*$", tex, re.M))
    print(f"子模块: {sorted(only)}")

    rc, raw = run_git(sup, ["log", "--no-merges", "--raw", "--format=C|%H", args.range])
    if rc != 0:
        raise SystemExit("git log 失败")

    cur: str | None = None
    pairs: list[tuple[str, str, str, str]] = []      # commit, module, old, new
    for line in raw.splitlines():
        m = HEADER_RE.match(line)
        if m:
            cur = m.group(1)
            continue
        rm = RAW_RE.match(line)
        if not rm or cur is None:
            continue
        old, new, status, path = rm.group(3), rm.group(4), rm.group(5), rm.group(6)
        if rm.group(1) != "160000" or rm.group(2) != "160000":
            continue                                  # 只有 gitlink 才是子模块指针
        if only and path not in only:
            continue
        if old == NULL or new == NULL:
            continue                                  # 新增/删除 gitlink：没有可比区间
        pairs.append((cur, path, old, new))

    stats: Counter = Counter()
    cache: dict[tuple[str, str, str], list[str]] = {}
    out_lines: list[str] = []
    last_commit = None
    for commit, mod, old, new in pairs:
        key = (mod, old, new)
        if key not in cache:
            sub = sup / mod
            if not (sub / ".git").exists():
                cache[key] = []
                stats["子模块工作树不存在"] += 1
            else:
                rc, diff = run_git(sub, ["diff", "--numstat", old, new])
                if rc != 0:
                    cache[key] = []
                    stats["SHA 不在本地"] += 1
                else:
                    cache[key] = [l for l in diff.splitlines() if l.strip()]
        lines = cache[key]
        if not lines:
            continue
        if commit != last_commit:
            out_lines.append(f"C|{commit}")
            last_commit = commit
        for l in lines:
            parts = l.split("\t")
            if len(parts) != 3:
                continue
            a, d, path = parts
            if not path.endswith(".java"):
                continue
            out_lines.append(f"{a}\t{d}\t{mod}/{path}")
            stats["文件条目"] += 1

    pathlib.Path(args.out).write_text("\n".join(out_lines) + "\n", encoding="utf-8")
    print(f"输出 {args.out}: {len(out_lines)} 行; 统计 {dict(stats)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
