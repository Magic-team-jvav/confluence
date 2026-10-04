"""按文件比对 1.20 HEAD 与 1.21 HEAD 的标识符残差。

用法: python tools/port2native/residual_file.py <relpath> [<relpath> ...]

判定: 取 1.20 blob 中出现的标识符(去掉字符串/注释/纯数字), 减去 1.21 全仓标识符集合,
      剩下的即"1.20 有而 1.21 没有"的标识符(残差)。残差里的每个词再给出出现位置,
      供人工判断是平台差异还是真缺口。
"""
import re
import subprocess
import sys
from pathlib import Path

REPO_121 = Path(r"D:\Minecraft\1.21neoforge\confluence")
REPO_120 = Path(r"D:\Minecraft\1.20forge\confluence")

TOKEN = re.compile(r"[A-Za-z_$][A-Za-z0-9_$]*")
LINE_COMMENT = re.compile(r"//[^\n]*")
BLOCK_COMMENT = re.compile(r"/\*.*?\*/", re.S)
STRING_LIT = re.compile(r'"(?:\\.|[^"\\])*"')
JAVA_KEYWORDS = {
    "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
    "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
    "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
    "new", "package", "private", "protected", "public", "record", "return", "short", "static",
    "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try",
    "var", "void", "volatile", "while", "true", "false", "null", "yield", "sealed", "permits",
}


def git(repo: Path, *args: str) -> subprocess.CompletedProcess:
    return subprocess.run(["git", *args], cwd=repo, capture_output=True, text=False)


def blob(repo: Path, rev: str, relpath: str):
    proc = git(repo, "show", f"{rev}:{relpath}")
    if proc.returncode != 0:
        return None
    return proc.stdout.decode("utf-8", "replace")


def strip_noise(text: str) -> str:
    return TOKEN.findall(STRING_LIT.sub(" ", BLOCK_COMMENT.sub(" ", LINE_COMMENT.sub(" ", text))))


def repo_tokens(repo: Path, rev: str, exts: tuple[str, ...]) -> set[str]:
    out = git(repo, "grep", "-h", "-o", "-E", "[A-Za-z_$][A-Za-z0-9_$]*", rev, "--", *[f"*{e}" for e in exts])
    words = out.stdout.decode("utf-8", "replace").split("\n")
    return {w.split(":", 1)[-1].strip() for w in words if w.strip()}


def main(argv: list[str]) -> int:
    if not argv:
        print(__doc__)
        return 2
    universe = repo_tokens(REPO_121, "HEAD", (".java", ".json"))
    print(f"1.21 全仓标识符集合: {len(universe)}")
    exit_code = 0
    for relpath in argv:
        src = blob(REPO_120, "HEAD", relpath)
        dst = blob(REPO_121, "HEAD", relpath)
        if src is None:
            print(f"\n[{relpath}] 1.20 HEAD 无此文件")
            exit_code = 1
            continue
        if dst is None:
            print(f"\n[{relpath}] 1.20 行数={src.count(chr(10)) + 1}  1.21 **缺文件**")
            exit_code = 1
            continue
        src_tokens = strip_noise(src)
        residual = sorted({t for t in src_tokens if t not in universe and t not in JAVA_KEYWORDS and not t.isdigit()})
        ratio = 100.0 * (1 - len(residual) / max(1, len(set(src_tokens))))
        print(f"\n[{relpath}] 1.20 行数={src.count(chr(10)) + 1} 1.21 行数={dst.count(chr(10)) + 1} "
              f"1.20 独立标识符={len(set(src_tokens))} 残差={len(residual)} 覆盖={ratio:.1f}%")
        if residual:
            lines = src.split("\n")
            for token in residual:
                hits = [i + 1 for i, line in enumerate(lines) if re.search(rf"\b{re.escape(token)}\b", line)]
                print(f"  - {token}  (1.20 行 {hits[:6]}{' ...' if len(hits) > 6 else ''})")
            exit_code = 1
    return exit_code


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
