#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""注释口径核对：把 1.21 某批新文件的注释行与 1.20 原文逐行比，抓出「多写的」和「漏抄的」。

口径（用户 2026-09-27 明确）：1.20 原文有注释就照抄，1.20 没有就不加；不写溯源/说明性注释。
  * 只在 1.21 出现、1.20 没有的注释行 = 疑似加料（人工看一眼）
  * 只在 1.20 出现、1.21 没有的注释行 = 疑似漏抄
比较按「归一化文本 + 出现次数」多元集做，剔除空白与注释前缀差异。

用法（输出重定向到文件再看，PowerShell 控制台 GBK 会把中文打乱）:
  cmd /c "python -X utf8 tools\\port2native\\comment_diff.py --files a/b.java c/d.java > %TEMP%\\cd.txt 2>&1"
  python tools/port2native/comment_diff.py --list %TEMP%\\batch_files.txt
"""
import argparse
import os
import re
import sys
from collections import Counter

DEFAULT_120 = r"D:\Minecraft\1.20forge\confluence\ConfluenceOtherworld\src\main\java"
REL_121 = os.path.join("ConfluenceOtherworld", "src", "main", "java")
INLINE = re.compile(r"//\s*(.*)$")


def read_lines(path):
    with open(path, "r", encoding="utf-8", errors="replace") as fh:
        return fh.read().split("\n")


def norm(s):
    s = s.strip()
    s = re.sub(r"^[/*]+\s*", "", s)
    return re.sub(r"\s+", " ", s)


def comments_of(lines):
    out = Counter()
    for ln in lines:
        st = ln.strip()
        if not st or st.startswith("import ") or st.startswith("package "):
            continue
        if st.startswith("//") or st.startswith("/*") or st.startswith("*"):
            n = norm(st)
            if n:
                out[n] += 1
            continue
        m = INLINE.search(st)
        if m:
            n = norm("//" + m.group(1))
            if len(n) > 3:
                out[n] += 1
    return out


def find_120(src120, rel):
    cand = os.path.join(src120, rel.replace("/", os.sep))
    if os.path.isfile(cand):
        return cand
    base = os.path.basename(rel)
    for root, _d, files in os.walk(src120):
        if base in files:
            return os.path.join(root, base)
    return None


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--repo", default=".")
    ap.add_argument("--src120", default=DEFAULT_120)
    ap.add_argument("--files", nargs="*", default=[])
    ap.add_argument("--list", default=None)
    args = ap.parse_args()

    repo = os.path.abspath(args.repo)
    root121 = os.path.join(repo, REL_121)
    files = list(args.files)
    if args.list:
        with open(args.list, encoding="utf-8-sig") as fh:
            files += [l.strip() for l in fh if l.strip()]

    added_total = missing_total = 0
    for rel in files:
        p121 = os.path.join(root121, rel.replace("/", os.sep))
        if not os.path.isfile(p121):
            print("[跳过] 1.21 无此文件: " + rel)
            continue
        p120 = find_120(args.src120, rel)
        if p120 is None:
            print("[注意] 1.20 无同名文件（新文件？）: " + rel)
            continue
        c121, c120 = comments_of(read_lines(p121)), comments_of(read_lines(p120))
        added, missing = c121 - c120, c120 - c121
        if not added and not missing:
            continue
        print("=" * 78)
        print(rel)
        if added:
            print("  [1.21 多出 %d 处（疑似加料）]" % sum(added.values()))
            for k, v in added.items():
                print("     %s%s" % (k, "" if v == 1 else "   ×%d" % v))
            added_total += sum(added.values())
        if missing:
            print("  [1.21 少了 %d 处（疑似漏抄）]" % sum(missing.values()))
            for k, v in missing.items():
                print("     %s%s" % (k, "" if v == 1 else "   ×%d" % v))
            missing_total += sum(missing.values())
    print("=" * 78)
    print("合计：多出 %d 处，少了 %d 处（共 %d 个文件）" % (added_total, missing_total, len(files)))
    return 0


if __name__ == "__main__":
    sys.exit(main())
