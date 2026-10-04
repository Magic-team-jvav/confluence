#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""跑 `:ConfluenceOtherworld:compileJava` 并把 javac 的错误解析成可读清单。

为什么要专门写一个：javac 在 Windows 上会**把长路径折行**，直接 grep 会得到
`nster.java:428: error: ...` 这种残缺文件名。这里按「新起一行且以盘符开头」判断真正的
文件行，其余非缩进行当作续行拼回去。

用法：
  python tools/port2native/build_errors.py                 # 编译 + 解析
  python tools/port2native/build_errors.py --log <file>    # 只解析已有日志
  python tools/port2native/build_errors.py --module :Confluence-Magic-Lib
  python tools/port2native/build_errors.py --maxerrs 2000  # 抬高 javac 的报错上限（见下）

⚠️ **javac 默认只报前 100 条错误**（`-Xmaxerrs` 默认值），超出部分既不打印也不计数 ——
于是「100 错误 / 46 文件」这种整数看着像巧合，实际是**被截断的下界**：批次 25 实测
真实规模是 **150 错误 / 65 文件**，前 4 轮都在按残缺清单排优先级。
大批次（几十个文件以上）请一律带 `--maxerrs 2000`，否则会漏掉后面几十个文件。
"""

from __future__ import annotations

import argparse
import collections
import os
import re
import subprocess
import sys

DRIVE = re.compile(r"^[A-Za-z]:[\\/]")
# 注意盘符里的冒号：文件段不能写成 [^:]+
ERR = re.compile(r"^(?P<file>[A-Za-z]:[\\/].*?\.java):(?P<line>\d+): (?:error|warning): (?P<msg>.*)$")
ERR_NODRIVE = re.compile(r"^(?P<file>.*?\.java):(?P<line>\d+): (?:error|warning): (?P<msg>.*)$")

# 抬高报错上限用的 Gradle init 脚本（写到临时目录，不进仓库、不改 build.gradle）。
_MAXERRS_INIT = """\
allprojects {
    tasks.withType(JavaCompile).configureEach {
        options.compilerArgs << '-Xmaxerrs' << '%d'
    }
}
"""


def write_maxerrs_init(maxerrs: int) -> str:
    """写出抬高 `-Xmaxerrs` 的 init 脚本，返回路径（供 `gradlew -I` 使用）。"""
    path = os.path.join(os.environ.get("TEMP", "."), "port2native", "maxerrs.init.gradle")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as fh:
        fh.write(_MAXERRS_INIT % maxerrs)
    return path



def run_gradle(module: str, extra: list[str], cwd: str, log_path: str) -> str:
    env = dict(os.environ)
    env["JAVA_TOOL_OPTIONS"] = "-Duser.language=en -Duser.country=US"
    args = ["gradlew.bat" if os.name == "nt" else "./gradlew", "--offline",
            f"{module}:compileJava", *extra]
    proc = subprocess.run(args, cwd=cwd, env=env, capture_output=True)
    text = proc.stdout.decode("utf-8", "replace") + proc.stderr.decode("utf-8", "replace")
    os.makedirs(os.path.dirname(os.path.abspath(log_path)), exist_ok=True)
    with open(log_path, "w", encoding="utf-8", newline="\n") as fh:
        fh.write(text)
    print(f"[build] exit={proc.returncode} log={log_path}", file=sys.stderr)
    return text


def join_wrapped(text: str) -> list[str]:
    """把被折行的 `path:line: error:` 拼回一行。"""
    out: list[str] = []
    pending: str | None = None
    for raw in text.splitlines():
        ln = raw.rstrip()
        if DRIVE.match(ln):
            if pending is not None:
                out.append(pending)
            pending = ln
            continue
        if pending is not None and ln[:1] not in ("", " ", "\t"):
            cand = pending + ln
            if ".java:" in cand and not re.search(r": (error|warning):", pending):
                pending = cand
                continue
        if pending is not None:
            out.append(pending)
            pending = None
        out.append(ln)
    if pending is not None:
        out.append(pending)
    return out


def main() -> int:
    try:
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    except Exception:
        pass
    ap = argparse.ArgumentParser()
    ap.add_argument("--module", default=":ConfluenceOtherworld")
    ap.add_argument("--log", default="")
    ap.add_argument("--repo", default=".")
    ap.add_argument("--maxerrs", type=int, default=0,
                    help="抬高 javac 的报错上限（默认 0 = 用 javac 自带的 100，会截断清单）")
    ap.add_argument("--keep", nargs="*", default=[],
                    help="只显示路径里含这些子串的错误（默认为空=全部）")
    args = ap.parse_args()

    log_path = args.log or os.path.join(os.environ.get("TEMP", "."), "port2native",
                                        "build", "compileJava.log")
    if args.log and os.path.isfile(args.log):
        with open(args.log, "r", encoding="utf-8", errors="replace") as fh:
            text = fh.read()
    else:
        extra: list[str] = []
        if args.maxerrs > 0:
            extra = ["-I", write_maxerrs_init(args.maxerrs)]
        text = run_gradle(args.module, extra, os.path.abspath(args.repo), log_path)

    lines = join_wrapped(text)
    entries: list[tuple[str, int, str]] = []
    for ln in lines:
        m = ERR.match(ln) or ERR_NODRIVE.match(ln)
        if not m:
            continue
        f = m.group("file")
        if f == "错误" or f.startswith("Note"):
            continue
        entries.append((f, int(m.group("line")), m.group("msg").strip()))

    byfile: dict[str, list[tuple[int, str]]] = collections.OrderedDict()
    for f, ln, msg in entries:
        byfile.setdefault(f, []).append((ln, msg))

    print(f"总错误数: {len(entries)}，涉及 {len(byfile)} 个文件\n")
    for f, items in sorted(byfile.items(), key=lambda kv: -len(kv[1])):
        rel = f.replace("\\", "/")
        if args.keep and not any(k in rel for k in args.keep):
            continue
        short = rel.split("/src/main/java/")[-1] if "/src/main/java/" in rel else rel
        print(f"## {short}  ({len(items)} 处)")
        uniq: dict[str, list[int]] = collections.OrderedDict()
        for ln, msg in items:
            uniq.setdefault(msg, []).append(ln)
        for msg, lns in sorted(uniq.items(), key=lambda kv: -len(kv[1])):
            head = ",".join(str(x) for x in lns[:8])
            more = f" (+{len(lns) - 8})" if len(lns) > 8 else ""
            print(f"  - [{head}{more}] {msg[:200]}")
        print()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
