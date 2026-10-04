#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""列出 1.20 有、1.21 没有的 java 文件（按 FQN），每行一个，供 dep_subset.py 的 --seed 用。

用法：
  python tools/port2native/missing_files.py --src120 <1.20 侧 src/main/java 根> \
      --root121 <1.21 侧源码根> [--root121 ...] [--sub common/entity/monster] [--failed]
"""
from __future__ import annotations

import argparse
import os
import sys


def main() -> int:
    try:
        sys.stdout.reconfigure(encoding='utf-8', errors='replace')
    except Exception:
        pass
    ap = argparse.ArgumentParser()
    ap.add_argument('--src120', required=True, help='1.20 侧 src/main/java 根')
    ap.add_argument('--root121', action='append', default=[], help='1.21 侧源码根（可重复）')
    ap.add_argument('--sub', default='', help="限定子路径（如 common/entity/monster）")
    args = ap.parse_args()

    def index(root: str) -> set[str]:
        prefix = root
        for marker in ('src/main/java', 'src/client/java'):
            cand = os.path.join(root, *marker.split('/'))
            if os.path.isdir(cand):
                prefix = cand
                break
        out = set()
        for dirpath, _d, files in os.walk(prefix):
            for f in files:
                if f.endswith('.java') and f != 'package-info.java':
                    rel = os.path.relpath(os.path.join(dirpath, f), prefix)
                    out.add(rel.replace('\\', '/')[:-5].replace('/', '.'))
        return out

    a = index(args.src120)
    b: set[str] = set()
    for r in args.root121:
        b |= index(r)
    prefix = args.sub.replace('/', '.').strip('.')
    for fqn in sorted(a - b):
        # `--sub` 是**片段**匹配：FQN 形如 org.confluence.mod.common.entity.monster.X，
        # 而调用方给的是 common/entity/monster，所以按「.片段.」或「.片段结尾」判断。
        if prefix and f'.{prefix}.' not in fqn + '.':
            continue
        print(fqn)
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
