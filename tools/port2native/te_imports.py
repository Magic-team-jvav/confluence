#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""列出主模组引用的全部 TE 全限定名（import 形态），供 te_repoint.py 的映射表使用。"""
import os
import re
import sys
from collections import Counter

ROOT = os.path.join("ConfluenceOtherworld", "src", "main", "java")
RE = re.compile(r"^\s*import\s+(static\s+)?(org\.confluence\.terraentity\.[\w\.]*\*?)\s*;")


def main():
    root = sys.argv[1] if len(sys.argv) > 1 else ROOT
    c = Counter()
    for dp, _dn, fn in os.walk(root):
        for f in fn:
            if not f.endswith(".java"):
                continue
            p = os.path.join(dp, f)
            with open(p, encoding="utf-8", errors="replace") as fh:
                for ln in fh:
                    m = RE.match(ln)
                    if m:
                        c[(m.group(2), bool(m.group(1)))] += 1
    for (fqn, is_static), n in sorted(c.items()):
        print("%3d  %s%s" % (n, "static " if is_static else "", fqn))
    print("--- 共 %d 个不同 FQN" % len(c))
    return 0


if __name__ == "__main__":
    sys.exit(main())
