"""Independent 1.20 -> 1.21 entity-type tag delta report (read-only).

Parses tag(...) statements (with chained .add/.addTag/.addOptionalTag) from both repos'
ModEntityTypeTagsProvider and compares tag sets + members.
1.21 side is read from the worktree (may include uncommitted work); 1.20 side from HEAD.
"""
import re
import subprocess
import sys

P = "ConfluenceOtherworld/src/main/java/org/confluence/mod/common/data/gen/tag/ModEntityTypeTagsProvider.java"
R20 = r"D:\Minecraft\1.20forge\confluence"
R21 = r"D:\Minecraft\1.21neoforge\confluence"

TAG_CALL = re.compile(r"(?<![A-Za-z0-9_])tag\s*\(")
CHAIN = re.compile(r"\.(add|addTag|addOptional|addOptionalTag|addAll)\s*\(")

PREFIXES = ("PortTags.EntityTypes.", "ModTags.EntityTypes.", "LibTags.EntityTypes.",
            "Tags.EntityTypes.", "EntityTypeTags.", "TCTags.", "ModEntityTypeTags.")


def read_head(repo):
    r = subprocess.run(["git", "-C", repo, "show", f"HEAD:{P}"], capture_output=True)
    if r.returncode != 0:
        raise SystemExit(r.stderr.decode("utf-8", "replace"))
    return r.stdout.decode("utf-8", "replace")


def read_worktree(repo):
    return open(rf"{repo}/{P}", encoding="utf-8", errors="replace").read()


def scan_paren(s, i):
    """i = index just after an opening paren; return index just after the matching close."""
    depth = 1
    while i < len(s) and depth:
        if s[i] == "(":
            depth += 1
        elif s[i] == ")":
            depth -= 1
        i += 1
    return i


def split_args(s):
    out, depth, cur = [], 0, ""
    for ch in s:
        if ch in "([{":
            depth += 1
        elif ch in ")]}":
            depth -= 1
        if ch == "," and depth == 0:
            out.append(cur)
            cur = ""
        else:
            cur += ch
    if cur.strip():
        out.append(cur)
    return [x.strip() for x in out if x.strip()]


def norm_symbol(tok):
    t = re.sub(r"\s+", "", tok)
    t = t.replace(".get()", "").replace(".value()", "")
    return t


def norm_tag(t):
    t = norm_symbol(t)
    for pre in PREFIXES:
        if t.startswith(pre):
            return pre.rstrip(".").split(".")[-1] + ":" + t[len(pre):]
    return "RAW:" + t


def parse(src):
    tags = {}
    for m in TAG_CALL.finditer(src):
        k = scan_paren(src, m.end())
        expr = norm_tag(src[m.end():k - 1])
        semi = src.find(";", k)
        chain = src[k:semi if semi >= 0 else len(src)]
        members = tags.setdefault(expr, [])
        for cm in CHAIN.finditer(chain):
            k2 = scan_paren(chain, cm.end())
            for a in split_args(chain[cm.end():k2 - 1]):
                members.append((cm.group(1), norm_symbol(a)))
    return tags


def main():
    t20 = parse(read_head(R20))
    t21 = parse(read_worktree(R21))
    out = [f"1.20 tag blocks: {len(t20)}   1.21 tag blocks: {len(t21)}", ""]

    out.append("== 1.21 blocks ==")
    for k in sorted(t21):
        out.append(f"  {k:<40} {len(t21[k])} entries")

    out.append("")
    out.append("== 1.20-only blocks (absent from 1.21) ==")
    for k in sorted(set(t20) - set(t21)):
        ms = t20[k]
        kinds = sorted({kind for kind, _ in ms})
        out.append(f"  {k:<40} {len(ms)} entries  kinds={kinds}")
        for kind, sym in ms:
            out.append(f"        {kind}: {sym}")

    out.append("")
    for k in sorted(set(t20) & set(t21)):
        m20 = [s for _, s in t20[k]]
        m21 = [s for _, s in t21[k]]
        o20 = [x for x in m20 if x not in m21]
        o21 = [x for x in m21 if x not in m20]
        if o20 or o21:
            out.append(f"== {k} ==")
            out.append(f"   only-1.20 ({len(o20)}): {o20}")
            out.append(f"   only-1.21 ({len(o21)}): {o21}")
    print("\n".join(out))


if __name__ == "__main__":
    sys.exit(main())
