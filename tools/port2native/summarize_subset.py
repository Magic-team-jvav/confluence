#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""把 dep_subset.py 的报告读成「按子系统分组的体量清单」，用于决定批次边界。

用法：
  python tools/port2native/summarize_subset.py --report <dep_subset 输出 md> \
      --src120 <1.20 侧 src/main/java 根> [--top 40]
"""
from __future__ import annotations

import argparse
import collections
import os
import re
import sys


def main() -> int:
    try:
        sys.stdout.reconfigure(encoding='utf-8', errors='replace')
    except Exception:
        pass
    ap = argparse.ArgumentParser()
    ap.add_argument('--report', required=True)
    ap.add_argument('--src120', required=True)
    ap.add_argument('--top', type=int, default=40)
    ap.add_argument('--group', type=int, default=5, help='按 FQN 前 N 段分组')
    args = ap.parse_args()

    text = open(args.report, encoding='utf-8').read().split('\n')

    def find(prefix: str) -> int:
        # 报告的标题带后缀（如「## 被 `--defer` 剔除（连依赖链一起还给后续批次）」），
        # list.index 要求整行相等，必须按前缀找。
        for k, line in enumerate(text):
            if line.startswith(prefix):
                return k
        return -1

    i = find('## 本批新增')
    if i < 0:
        print('报告里找不到「本批新增」小节', file=sys.stderr)
        return 2
    j = find('## 被 `--defer` 剔除')
    if j < 0:
        j = len(text)
    items = [l[3:-1] for l in text[i + 1:j] if l.startswith('- `')]

    def lines(fqn: str) -> int:
        p = os.path.join(args.src120, *fqn.split('.')) + '.java'
        if not os.path.isfile(p):
            return 0
        return len([x for x in open(p, encoding='utf-8', errors='replace').read().split('\n') if x.strip()])

    groups: dict[str, list[int]] = collections.defaultdict(lambda: [0, 0])
    rows = []
    for fqn in items:
        n = lines(fqn)
        rows.append((fqn, n))
        key = '.'.join(fqn.split('.')[:args.group])
        groups[key][0] += 1
        groups[key][1] += n
    print(f'保留 {len(items)} 个 / {sum(n for _f, n in rows)} 非空行')
    print('--- 分组 ---')
    for k in sorted(groups, key=lambda x: -groups[x][1]):
        print('  %-44s %3d 个 %6d' % (k, groups[k][0], groups[k][1]))
    print('--- 最大的 %d 个 ---' % args.top)
    for fqn, n in sorted(rows, key=lambda r: -r[1])[:args.top]:
        print('  %-64s %5d' % (fqn.replace('org.confluence.mod.', ''), n))
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
