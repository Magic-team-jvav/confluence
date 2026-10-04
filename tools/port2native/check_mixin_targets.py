#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Mixin 目标审计：把 1.20.1 侧的 mixin 目标（类与方法）拿去和 1.21.1 的源码对账。

为什么需要它
------------
Mixin 是**唯一不能靠 Port 词汇转换解决**的一层：它的 @Mixin/@Inject 目标直接指向
原版内部的类与方法，而 1.20.1 与 1.21.1 之间这些内部结构改动很大（方法改名/合并/删除、
lambda 重排、加载器类不同）。机械把 1.20 的 mixin 搬到 1.21 的结果是启动期
MixinApplyError 或注入点找不到。

本工具不修改任何文件，只回答三个问题：
  1. 1.20 的 mixin 目标类在 1.21 里还存在吗？（不存在 -> 该 mixin 必须重设计或删除）
  2. 目标类存在，但 @Inject/@Redirect/... 指定的方法名在 1.21 里还有吗？
  3. 目标是不是加载器类（net.minecraftforge.* / net.neoforged.*）？——这类天然是单版本专有。

用法
----
    python check_mixin_targets.py \
        --source  D:/Minecraft/1.20forge/confluence/ConfluenceOtherworld/src/main/java \
        --reference D:/Minecraft/1.21neoforge/confluence/build/_nfsrc_219 \
        --out  <报告目录>
"""

from __future__ import annotations

import argparse
import json
import pathlib
import re
import sys
from collections import Counter, defaultdict

IMPORT_RE = re.compile(r"^\s*import\s+(static\s+)?([\w.$]+)\s*;\s*$")
MIXIN_RE = re.compile(r"@Mixin\s*\(([^)]*)\)")
CLASS_REF_RE = re.compile(r"([A-Za-z_$][\w$]*(?:\.[A-Za-z_$][\w$]*)*)\.class")
# 注入注解 -> 是否取方法名（@Accessor/@Invoker 取字段/方法名）
INJECT_ANNOS = [
    "Inject", "Redirect", "WrapOperation", "WrapWithCondition", "ModifyVariable",
    "ModifyArg", "ModifyArgs", "ModifyConstant", "ModifyReturnValue", "WrapMethod",
    "Accessor", "Invoker", "Overwrite", "Shadow",
]
ANNO_RE = re.compile(r"@(" + "|".join(INJECT_ANNOS) + r")\s*\(([^)]*)\)")
METHOD_ATTR_RE = re.compile(r'method\s*=\s*"([^"]*)"')
VALUE_ATTR_RE = re.compile(r'(?:value|target)\s*=\s*"([^"]*)"')
LOADER_PKGS = ("net.minecraftforge.", "net.neoforged.", "net.minecraftforge.", "cpw.mods.")


def strip_descriptor(entry: str) -> str:
    """把 'foo(II)V' / 'Lnet/minecraft/x;bar' / 'lambda$x$0' 归一成方法名。"""
    entry = entry.strip().rstrip(";")
    if not entry:
        return ""
    if entry.startswith("L") and ";" in entry:       # 带描述符的混淆风格
        entry = entry.split(";")[-1] or entry
    name = entry.split("(")[0]
    name = name.split(":")[-1]                      # 去掉可能的类前缀
    return name.strip()


def load_reference_index(ref_root: pathlib.Path) -> set[str]:
    idx: set[str] = set()
    for p in ref_root.rglob("*.java"):
        idx.add(p.relative_to(ref_root).as_posix()[: -len(".java")].replace("/", "."))
    return idx


def ref_source(ref_root: pathlib.Path, fqn: str) -> str | None:
    cand = ref_root / (fqn.replace(".", "/") + ".java")
    if cand.is_file():
        return cand.read_text(encoding="utf-8", errors="ignore")
    # 嵌套类：a.b.C$D 或 a.b.C.D -> 找外层
    parts = fqn.split(".")
    for cut in range(len(parts) - 1, 0, -1):
        cand = ref_root / ("/".join(parts[:cut]) + ".java")
        if cand.is_file():
            return cand.read_text(encoding="utf-8", errors="ignore")
    return None


def strip_nested_at(args: str) -> str:
    """去掉注解参数里嵌套的 @At(...) / @Slice(...) 内容。

    否则 `@WrapOperation(method = "x", at = @At(value = "INVOKE", ...))` 里的
    `value = "INVOKE"` 会被误当成成员名（早期版本正是踩了这个坑，导致 METHOD-MISSING 虚高）。
    """
    out: list[str] = []
    i = 0
    while i < len(args):
        if args.startswith("@At", i) or args.startswith("@Slice", i):
            j = args.find("(", i)
            if j == -1:
                i += 4
                continue
            depth, k = 0, j
            while k < len(args):
                if args[k] == "(":
                    depth += 1
                elif args[k] == ")":
                    depth -= 1
                    if depth == 0:
                        break
                k += 1
            i = k + 1
            continue
        out.append(args[i])
        i += 1
    return "".join(out)


STRING_LIT_RE = re.compile(r'"([^"]*)"')


def audit_file(path: pathlib.Path, rel: str, search_roots: list[pathlib.Path], ref_index: set[str]) -> dict:
    text = path.read_text(encoding="utf-8", errors="ignore")
    imports: dict[str, str] = {}
    for line in text.splitlines():
        m = IMPORT_RE.match(line)
        if m and not m.group(1):
            imports[m.group(2).rsplit(".", 1)[-1]] = m.group(2)

    pkg = ""
    m = re.search(r"^\s*package\s+([\w.]+)\s*;", text, re.M)
    if m:
        pkg = m.group(1)

    result: dict = {"file": rel, "targets": [], "verdicts": Counter(), "methodChecks": []}

    for mixin_args in MIXIN_RE.findall(text):
        for raw in CLASS_REF_RE.findall(mixin_args):
            # `X.class` 与嵌套 `X.Y.class` 都要走 import 解析，否则嵌套目标会被误判成"目标类缺失"
            head, _, rest = raw.partition(".")
            base = imports.get(head)
            if rest:
                fqn = f"{base}.{rest}" if base else raw
            else:
                fqn = base or (f"{pkg}.{head}" if pkg else head)
            verdicts: list[str] = []
            if fqn.startswith(LOADER_PKGS):
                verdicts.append("LOADER-CLASS")           # 加载器类：天然单版本专有
            body = None
            for root in search_roots:
                body = ref_source(root, fqn)
                if body is not None:
                    break
            if body is None and fqn not in ref_index:
                verdicts.append("TARGET-MISSING")
            result["targets"].append({"raw": raw, "fqn": fqn, "verdicts": verdicts or ["TARGET-OK"]})
            for v in verdicts or ["TARGET-OK"]:
                result["verdicts"][v] += 1

    # 方法级检查：拿 @Inject/@Redirect... 的 method= 名去参照类里找
    single_target = len(result["targets"]) == 1
    target_fqn = result["targets"][0]["fqn"] if single_target else None
    body = None
    if target_fqn:
        for root in search_roots:
            body = ref_source(root, target_fqn)
            if body is not None:
                break

    for anno, raw_args in ANNO_RE.findall(text):
        clean = strip_nested_at(raw_args)
        names: list[str] = []
        if anno in ("Accessor", "Invoker"):
            # @Accessor("foo") / @Invoker("foo")：字符串字面量就是成员名
            names = [n for n in STRING_LIT_RE.findall(clean) if n]
        else:
            for attr in (METHOD_ATTR_RE,):
                for raw in attr.findall(clean):
                    names.extend(strip_descriptor(x) for x in raw.split(";") if strip_descriptor(x))
        if not names:
            continue
        for name in names:
            if name in ("<init>", "<clinit>"):
                state = "CTOR-OK"
            elif name.startswith("lambda$"):
                state = "LAMBDA-RISKY"          # lambda 编号在版本间不稳定
            elif not single_target:
                state = "MULTI-TARGET"          # 多目标混注：无法静态判定归属，交人工
            elif body is None:
                state = "UNKNOWN"
            elif re.search(r"\b" + re.escape(name) + r"\s*\(", body):
                state = "METHOD-OK"
            else:
                state = "METHOD-MISSING"
            result["methodChecks"].append({"file": rel, "anno": anno, "member": name, "state": state})
            result["verdicts"][state] += 1
    return result


def main(argv: list[str] | None = None) -> int:
    ap = argparse.ArgumentParser(description="Mixin 目标审计（1.20 mixin vs 1.21 源码）")
    ap.add_argument("--source", required=True, help="1.20 侧源码根（含 mixin 包的 java 根）")
    ap.add_argument("--reference", required=True, help="1.21 反编译源码根（_nfsrc_219）")
    ap.add_argument("--out", default=None, help="报告目录")
    ap.add_argument("--filter", default="mixin", help="只审计路径含该子串的文件（默认 mixin）")
    ap.add_argument("--project-ref", action="append", default=[],
                    help="额外参照源码根（可重复）：本工程自己的模块，用于判定 mixin 到自身/兄弟模块类的目标")
    args = ap.parse_args(argv)

    src_root = pathlib.Path(args.source).resolve()
    ref_root = pathlib.Path(args.reference).resolve()
    out_dir = pathlib.Path(args.out).resolve() if args.out else src_root / "_mixin-audit"
    out_dir.mkdir(parents=True, exist_ok=True)

    search_roots = [ref_root] + [pathlib.Path(x).resolve() for x in args.project_ref]
    ref_index: set[str] = set()
    for root in search_roots:
        ref_index |= load_reference_index(root)
    files = sorted(
        p for p in src_root.rglob("*.java")
        if args.filter in p.as_posix() and p.is_file()
    )

    results = [
        audit_file(p, p.relative_to(src_root).as_posix(), search_roots, ref_index)
        for p in files
    ]

    total = Counter()
    for r in results:
        total.update(r["verdicts"])

    md = ["# Mixin 目标审计报告", "",
          f"- 审计文件：{len(files)}（{src_root}）",
          f"- 参照源码：{ref_root}", "",
          "## 汇总", "", "| 结论 | 次数 |", "|---|---|"]
    for key, value in total.most_common():
        md.append(f"| {key} | {value} |")

    md += ["", "## 需要人工处理的文件", ""]
    bad_states = {"TARGET-MISSING", "METHOD-MISSING", "LAMBDA-RISKY", "LOADER-CLASS", "UNKNOWN"}
    for r in results:
        bad = [v for v in r["verdicts"] if v in bad_states]
        if not bad:
            continue
        md.append(f"### `{r['file']}`")
        for t in r["targets"]:
            if any(v in bad_states for v in t["verdicts"]):
                md.append(f"- 目标类 `{t['fqn']}` → {', '.join(t['verdicts'])}")
        for mc in r["methodChecks"]:
            if mc["state"] in bad_states:
                md.append(f"- `@{mc['anno']}` 成员 `{mc['member']}` → {mc['state']}")
        md.append("")

    (out_dir / "mixin-audit.md").write_text("\n".join(md), encoding="utf-8")
    (out_dir / "mixin-audit.json").write_text(
        json.dumps({"summary": dict(total), "files": results}, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )
    print(json.dumps({"files": len(files), **dict(total)}, ensure_ascii=False, indent=2))
    print(f"报告: {out_dir}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
