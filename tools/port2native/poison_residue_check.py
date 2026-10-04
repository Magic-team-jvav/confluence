"""毒残留检查：判断某次改动/某个提交是否碰到 `dfcc5c041` 的残留文件。

为什么需要它
------------
《notes/POISON-dfcc5c041.md》第 2/3 条规定：落在残留清单里的文件**不能照抄 1.20 当前实现**，
要优先以 1.21 侧现有实现为准。2026-10 的一次真实翻车就出在这里：
移植 1.20 `placeCoins`（行 331，非毒）时，它调用的 `CoinItem.valueOf(Item)` 是**毒提交引入**的 helper
（1.21 从未有过），结果落地后编译报 `Cannot resolve method 'valueOf' in 'CoinItem'`，
而 `PlayerPiggyBankContainer.java` 正是残留清单里的文件。
⇒ 教训：**不仅看该行自己的文件，还要看被调方（callee）**；本工具先把"文件级"这一层自动化。

用法:
  python tools/port2native/poison_residue_check.py <路径...>          # 直接判给定文件
  python tools/port2native/poison_residue_check.py --commit <rev>     # 判某次提交改动的文件
  python tools/port2native/poison_residue_check.py --worktree         # 判工作树当前改动的文件
  python tools/port2native/poison_residue_check.py --symbol <名字>    # 判"某符号是否毒提交引入"
                                                                     # （在 1.20 仓跑 git log -S 取最早引入提交）
退出码：命中残留（或符号属毒）返回 1，否则 0。
"""
import argparse
import subprocess
import sys
from pathlib import Path

RESIDUE = Path("notes/poison-residue-files.txt")
POISON = {"dfcc5c041", "dc57ba5c2"}
REPO_120 = Path(r"D:\Minecraft\1.20forge\confluence")
REPO_121 = Path(r"D:\Minecraft\1.21neoforge\confluence")


def load_residue() -> set[str]:
    if not RESIDUE.exists():
        sys.exit(f"缺少 {RESIDUE}（毒残留清单）")
    return {l.strip() for l in RESIDUE.read_text(encoding="utf-8").splitlines() if l.strip()}


def git(repo: Path, *args: str) -> str:
    p = subprocess.run(["git", *args], cwd=repo, capture_output=True)
    return p.stdout.decode("utf-8", "replace")


def changed_files(args) -> list[str]:
    if args.commit:
        out = git(REPO_121, "show", "--name-only", "--pretty=format:", args.commit)
    else:
        out = git(REPO_121, "diff", "--name-only")
        out += git(REPO_121, "diff", "--cached", "--name-only")
        out += git(REPO_121, "ls-files", "--others", "--exclude-standard")
    return sorted({l.strip() for l in out.split("\n") if l.strip()})


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("paths", nargs="*")
    ap.add_argument("--commit")
    ap.add_argument("--worktree", action="store_true")
    ap.add_argument("--symbol")
    ap.add_argument("--path", help="符号模式用：把 -S 限定在该相对路径内（**落点级归属，权威口径**）")
    args = ap.parse_args()

    if args.symbol:
        cmd = [REPO_120, "log", "--format=%h %cI %s", "-S", args.symbol]
        if args.path:
            cmd += ["--", args.path]
        out = git(*cmd)
        lines = [l for l in out.split("\n") if l.strip()]
        scope = f"（路径级：{args.path}）" if args.path else "（**全仓级**）"
        if not lines:
            print(f"[symbol] {args.symbol}{scope}: 1.20 侧查不到引入提交")
            return 0
        first = lines[-1].split()[0]
        verdict = "毒提交引入 ⇒ 禁止移植/需改用 1.21 侧实现" if first in POISON else "非毒提交引入"
        print(f"[symbol] {args.symbol}{scope}: 最早引入 = {lines[-1]}")
        print(f"         判定：{verdict}")
        if not args.path:
            print("         注意：全仓级 -S 会被『共享史里别的文件的同名符号』带偏（实例："
                  "POWDER_SNOW_WALKABLE_MOBS 等 3 个 tag 在全仓级报非毒 4d02a62a6、"
                  "在路径级报毒 dfcc5c041，结论相反）。落地归属一律以路径级（--path）为准，"
                  "全仓级只用于『该符号是否在别处出现过』的粗筛。")
        return 1 if first in POISON else 0

    paths = args.paths or (changed_files(args) if (args.commit or args.worktree) else [])
    if not paths:
        print(__doc__)
        return 2

    residue = load_residue()
    hits = [p for p in paths if p.replace("\\", "/") in residue]
    print(f"检查 {len(paths)} 个路径；毒残留清单 {len(residue)} 条；命中 {len(hits)}")
    for p in hits:
        print(f"  [RESIDUE] {p}")
    if hits:
        print("\n⇒ 这些文件在 `dfcc5c041` 的残留清单里：按《POISON-dfcc5c041.md》第 3 条，"
              "**优先以 1.21 侧现有实现为准**，不要照抄 1.20 当前实现；并逐个核对被调用的方法/常量是否由毒提交引入"
              "（可用 `--symbol <名字>` 判定）。")
    return 1 if hits else 0


if __name__ == "__main__":
    sys.exit(main())
