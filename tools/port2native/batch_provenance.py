#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""给每个 1.21 提交反查「它覆盖了哪些 1.20 提交」——恢复逐提交溯源。

`notes/COMMIT-LAG.md` 是「以 1.20 提交为行」的台账；本工具补上反方向的那张表：
**每个 1.21 提交的改动文件，对应到 1.20 侧哪些文件，而这些文件又被区间内哪些 1.20 提交改过。**

做法：
1. 从 1.21 仓库读每个提交的改动文件（`git log --numstat`，只取 src/main/java 下的 .java）。
2. 映射到 1.20 路径：先试**同相对路径**；再试**同模块同文件名**；再试**跨模块同文件名**
   （1.20 把 TerraEntity/TerraGuns 内联进主模组，跨模块命中是正常情况）。
3. 用 `numstat-all.txt`（1.20 侧区间内的逐提交 numstat）反查该 1.20 文件被哪些提交改过。

用法：
  python tools/port2native/batch_provenance.py --repo . --root121 ConfluenceOtherworld/src/main/java \
      --src120 <1.20 src/main/java> --log "$env:TEMP\port-ledger\numstat-all.txt" \
      --range 795ac9ccc..18221c338 --since 14fee792a --out notes/BATCH-PROVENANCE.md
"""

from __future__ import annotations

import argparse
import collections
import os
import re
import subprocess
import sys


def git(repo: str, *args: str) -> str:
    return subprocess.run(['git', '-C', repo, *args], capture_output=True, text=True,
                          encoding='utf-8', errors='replace').stdout


def norm120(p: str) -> str:
    """把 numstat 里的仓库根相对路径归一成「相对 src/main/java」的形式。

    两个必须处理的形态：
    1. 前缀：`ConfluenceOtherworld/src/main/java/org/...` → `org/...`（主模组）
    2. 重命名语法：`.../common/{item/yoyo => attachment}/YoyoSession.java` → `.../common/attachment/YoyoSession.java`
    """
    p = p.replace('\\', '/')
    p = re.sub(r'\{([^{}]*?) => ([^{}]*?)\}', lambda m: m.group(2) or '', p)
    idx = p.find('src/main/java/')
    if idx >= 0:
        p = p[idx + len('src/main/java/'):]
    return p


def load_numstat(path: str) -> dict[str, list[tuple[str, str, int]]]:
    """{1.20 相对路径: [(commit, subject, added), ...]}"""
    out: dict[str, list[tuple[str, str, int]]] = collections.defaultdict(list)
    commit = subject = ''
    with open(path, encoding='utf-8-sig', errors='replace') as fh:
        for line in fh:
            line = line.rstrip('\n')
            if line.startswith('C|'):
                # 台账头部是 `C|<hash>`（没有 subject；主题见 notes/PORT-LEDGER.md）
                parts = line.split('|')
                commit = parts[1] if len(parts) > 1 else ''
                subject = parts[2].strip() if len(parts) > 2 else ''
                continue
            parts = line.split('\t')
            if len(parts) != 3 or not commit:
                continue
            added, _deleted, p = parts
            if not p.endswith('.java'):
                continue
            try:
                n = int(added)
            except ValueError:
                n = 0
            out[norm120(p)].append((commit, subject, n))
    return out


def main() -> int:
    try:
        sys.stdout.reconfigure(encoding='utf-8', errors='replace')
    except Exception:
        pass
    ap = argparse.ArgumentParser()
    ap.add_argument('--repo', required=True)
    ap.add_argument('--root121', required=True, help='1.21 侧源码根（相对仓库）')
    ap.add_argument('--src120', required=True, help='1.20 侧 src/main/java')
    ap.add_argument('--log', required=True, help='numstat-all.txt')
    ap.add_argument('--range', required=True)
    ap.add_argument('--since', required=True, help='从这个 1.21 提交（不含）之后开始列')
    ap.add_argument('--out', default='')
    args = ap.parse_args()

    src120 = os.path.abspath(args.src120)
    rel120 = set()
    by_base: dict[str, list[str]] = collections.defaultdict(list)
    for dirpath, _d, files in os.walk(src120):
        for f in files:
            if not f.endswith('.java'):
                continue
            rel = os.path.relpath(os.path.join(dirpath, f), src120).replace('\\', '/')
            rel120.add(rel)
            by_base[f].append(rel)

    numstat = load_numstat(args.log)

    def map_to_120(rel121: str) -> str | None:
        if rel121 in rel120:
            return rel121
        base = os.path.basename(rel121)
        cand = by_base.get(base, [])
        # 同包尾段优先（例如 common/entity/monster/Foo.java 命中 terraentity 侧的同名文件时排后）
        tail = '/'.join(rel121.split('/')[-3:])
        for c in cand:
            if c.endswith(tail):
                return c
        return cand[0] if cand else None

    log = git(args.repo, 'log', f'{args.since}..HEAD', '--no-merges', '--numstat',
              '--format=C|%H|%s')
    commits: list[tuple[str, str, list[str]]] = []
    cur_hash = cur_subject = ''
    files: list[str] = []
    for line in log.splitlines():
        if line.startswith('C|'):
            if cur_hash:
                commits.append((cur_hash, cur_subject, files))
            _, cur_hash, cur_subject = (line.split('|', 2) + ['', ''])[:3]
            files = []
            continue
        parts = line.split('\t')
        if len(parts) == 3 and cur_hash:
            p = parts[2]
            if p.endswith('.java') and p.startswith(args.root121):
                files.append(p[len(args.root121):].lstrip('/'))
    if cur_hash:
        commits.append((cur_hash, cur_subject, files))

    L: list[str] = []
    L.append('# 批次 → 来源提交 映射（逐提交溯源的补表）')
    L.append('')
    L.append(f'- 区间 `{args.range}`；1.21 侧从 `{args.since}` 之后逐提交列')
    L.append('- 读法：左列是 1.21 提交，右列是它动过的文件在 1.20 侧对应文件上**改过的 1.20 提交**')
    L.append('  （一个工作包覆盖多个 1.20 提交是常态，这正是「判定逐提交、执行按工作包」的产物）')
    L.append('- ⚠️ 粒度是**文件级**：某 1.20 提交出现在这里，只说明它改过这些文件，')
    L.append('  **不等于**本批次移植了那个提交的改动（例如 `dfcc5c041` 是既定的 DO-NOT-PORT 污染提交，')
    L.append('  它碰过的文件很多，本仓库从不移植它）。逐提交的判定仍以 `notes/COMMIT-LAG.md` 为准。')
    L.append('- 台账头部是 `C|<hash>`（不含主题），故右列只列 hash；主题查 `notes/PORT-LEDGER.md`')
    L.append('')
    for h, subj, files in commits:
        hits: dict[str, int] = collections.Counter()
        subj_of: dict[str, str] = {}
        mapped = unmapped = 0
        for rel in files:
            p120 = map_to_120(rel)
            if not p120:
                unmapped += 1
                continue
            mapped += 1
            for c, s, n in numstat.get(p120, []):
                hits[c] += 1
                subj_of[c] = s
        L.append(f'## `{h[:9]}` {subj}')
        L.append('')
        L.append(f'- 改动 java 文件 {len(files)} 个（映射到 1.20 {mapped}，无对应 {unmapped}）')
        if hits:
            L.append('- 覆盖的 1.20 提交（按命中文件数排序）：')
            for c, n in hits.most_common(20):
                subj = subj_of.get(c) or '（主题见 notes/PORT-LEDGER.md）'
                L.append(f'  - `{c[:9]}` ×{n} —— {subj[:80]}')
            if len(hits) > 20:
                L.append(f'  - …另 {len(hits) - 20} 个')
        else:
            L.append('- 未命中区间内的 1.20 提交（可能是新增文件或纯 1.21 侧修复）')
        L.append('')

    md = '\n'.join(L)
    if args.out:
        with open(args.out, 'w', encoding='utf-8', newline='\r\n') as fh:
            fh.write(md)
        print(f'写出 {args.out}（{len(commits)} 个 1.21 提交）')
    else:
        sys.stdout.write(md)
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
