#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""把 javac 日志解析成「文件:行 + 出错源码行」的清单，便于逐个修。

用法：
  python tools/port2native/error_detail.py [--log <compileJava.log>] [--filter <子串>]
"""
from __future__ import annotations

import argparse
import os
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from build_errors import ERR, ERR_NODRIVE, join_wrapped  # noqa: E402


def main() -> int:
    try:
        sys.stdout.reconfigure(encoding='utf-8', errors='replace')
    except Exception:
        pass
    ap = argparse.ArgumentParser()
    ap.add_argument('--log', default=os.path.join(os.environ.get('TEMP', '.'), 'port2native',
                                                  'build', 'compileJava.log'))
    ap.add_argument('--filter', default='', help='只看路径含该子串的条目')
    args = ap.parse_args()

    text = open(args.log, encoding='utf-8', errors='replace').read()
    for ln in join_wrapped(text):
        m = ERR.match(ln) or ERR_NODRIVE.match(ln)
        if not m:
            continue
        f, n, msg = m.group('file'), int(m.group('line')), m.group('msg')
        if args.filter and args.filter not in f.replace('\\', '/'):
            continue
        src = open(f, encoding='utf-8', errors='replace').read().split('\n')
        code = src[n - 1].strip() if n - 1 < len(src) else ''
        print('%-28s %5d  %s' % (os.path.basename(f), n, msg[:95]))
        print('        >>> %s' % code[:135])
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
