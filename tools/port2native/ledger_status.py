"""回填 / 查询 PORT-LEDGER.md 的最后一列（状态）。

用法:
  python tools/port2native/ledger_status.py --list [起 止]      # 列出行的当前状态
  python tools/port2native/ledger_status.py --check             # 只做结构校验，不写盘
  python tools/port2native/ledger_status.py 111=COVERED 112=SKIP-1.20-REVERTED ...
                                                                # 回填（默认拒绝覆盖已有非 TODO 状态）
  python tools/port2native/ledger_status.py --force 68=COVERED  # 允许覆盖

状态取值（与台账 §概况 头部一致）:
  TODO / PORTED / COVERED / SKIP-PLATFORM / SKIP-1.21-KEEPS / SKIP-PORTLIB /
  SKIP-1.20-REVERTED / MOVED / DEFER-ASSETS / DEFER-ARCH / DO-NOT-PORT / LOST?
"""
import io
import json
import re
import sys
from pathlib import Path

LEDGER = Path("notes/PORT-LEDGER.md")
LEDGER_JSON = Path("notes/port-ledger.json")
STATUS_JSON = Path("notes/port-ledger-status.json")
ROW_RE = re.compile(r"^\|\s*(\d+)\s*\|")
VALID = {
    "TODO", "PORTED", "COVERED", "SKIP-PLATFORM", "SKIP-1.21-KEEPS", "SKIP-PORTLIB",
    "SKIP-1.20-REVERTED", "MOVED", "DEFER-ASSETS", "DEFER-ARCH", "DO-NOT-PORT", "LOST?",
    "REVERSE-ALIGNED",
}
NOTE = ("移植状态；key = 1.20 侧提交全 hash。status: TODO/PORTED/COVERED/SKIP-PLATFORM/SKIP-1.21-KEEPS/"
        "SKIP-PORTLIB/SKIP-1.20-REVERTED/DEFER-ASSETS/DO-NOT-PORT/REVERSE-ALIGNED。缺 KEY = TODO。判定依据见 "
        "notes/PORT-LEDGER.md、TRIAGE-PASS1.md、TRIAGE-PASS2.md、FILE-LAG.md")
TARGETS = {
    "PORTED": "已按 1.21 原生改写并提交（见 notes/PORT-LANDING-RECORD.md）",
    "COVERED": "（1.21 已有等价实现）",
    "SKIP-PLATFORM": "（不动 1.21）",
    "SKIP-1.20-REVERTED": "（不动 1.21）",
    "DO-NOT-PORT": "（永不移植）",
    "DEFER-ASSETS": "（随所属功能提交处理）",
    "DEFER-ARCH": "（架构级，Phase 1/3）",
    "SKIP-PORTLIB": "（不动 1.21）",
    "SKIP-1.21-KEEPS": "（不动 1.21）",
    "MOVED": "（对应 1.21 另一路径）",
    "LOST?": "（待复核）",
    "REVERSE-ALIGNED": "（1.20 侧反向对齐 1.21 的提交，不作移植源）",
}


def load():
    raw = LEDGER.read_bytes()
    text = raw.decode("utf-8")
    crlf = raw.count(b"\r\n") > 0
    return text.replace("\r\n", "\n").split("\n"), crlf


def save(lines, crlf):
    text = "\n".join(lines)
    data = (text.replace("\n", "\r\n") if crlf else text).encode("utf-8")
    LEDGER.write_bytes(data)


def split_row(line):
    """返回 (行号, 单元格列表, 前缀, 后缀) —— 单元格已 strip，前缀/后缀保留原格式。"""
    parts = line.split("|")
    return parts


def status_of(line):
    parts = line.split("|")
    for cell in reversed(parts):
        token = cell.strip()
        if token:
            return token
    return ""


def main(argv):
    force = "--force" in argv
    argv = [a for a in argv if a != "--force"]
    lines, crlf = load()
    index = {}
    bad = []
    for i, line in enumerate(lines):
        m = ROW_RE.match(line)
        if not m:
            continue
        row = int(m.group(1))
        index[row] = i
        st = status_of(line)
        if st not in VALID:
            bad.append((row, st))
    print(f"台账行数: {len(index)}（{min(index)}–{max(index)}）" + (f"，异常状态 {bad}" if bad else "，状态列全部合法"))

    if not argv or argv[0] in ("--list", "--check"):
        start, end = 1, 10 ** 9
        if argv[0] == "--list" and len(argv) >= 3:
            start, end = int(argv[1]), int(argv[2])
        if argv[0] == "--check":
            return 1 if bad else 0
        for row in sorted(index):
            if start <= row <= end:
                print(f"  {row:>4}  {status_of(lines[index[row]])}")
        return 0

    # 行号 → 1.20 提交全 hash（json 的 commits 列表与台账行同序，行 N = commits[N-1]）
    full = {}
    if LEDGER_JSON.exists():
        data = json.loads(LEDGER_JSON.read_text(encoding="utf-8"))
        seq = data.get("commits", [])
        for row in index:
            if 1 <= row <= len(seq):
                full[row] = seq[row - 1].get("hash", "")

    changes = []
    json_sync = []
    for spec in argv:
        if "=" not in spec:
            print(f"忽略非法参数: {spec}")
            return 2
        row_s, new = spec.split("=", 1)
        row, new = int(row_s), new.strip()
        if new not in VALID:
            print(f"非法状态: {new}（合法: {sorted(VALID)}）")
            return 2
        if row not in index:
            print(f"台账无此行: {row}")
            return 2
        line = lines[index[row]]
        old = status_of(line)
        if old == new:
            json_sync.append((row, new))
            changes.append((row, old, new, "已是该状态（只同步 JSON）"))
            continue
        if old != "TODO" and not force:
            changes.append((row, old, new, "拒绝覆盖（需 --force）"))
            continue
        parts = line.split("|")
        for j in range(len(parts) - 1, -1, -1):
            if parts[j].strip():
                parts[j] = parts[j].replace(parts[j].strip(), new)
                break
        lines[index[row]] = "|".join(parts)
        changes.append((row, old, new, "已写"))
        json_sync.append((row, new))

    for row, old, new, note in changes:
        print(f"  {row:>4}  {old} -> {new}  ({note})")
    written = [(row, new) for row, _old, new, note in changes if note == "已写"]
    if written:
        save(lines, crlf)
        print("PORT-LEDGER.md 已保存")
    if json_sync:
        if full:
            status = json.loads(STATUS_JSON.read_text(encoding="utf-8")) if STATUS_JSON.exists() else {"$note": NOTE, "commits": {}}
            commits = status.setdefault("commits", {})
            for row, new in json_sync:
                h = full.get(row)
                if not h:
                    print(f"  ! 行 {row} 未在 port-ledger.json 找到全 hash，状态 JSON 未写")
                    continue
                entry = commits.get(h)
                if isinstance(entry, dict):
                    # 保留既有 target/note 等元数据，只改 status
                    entry["status"] = new
                else:
                    commits[h] = {
                        "status": new,
                        "target": TARGETS.get(new, ""),
                        "note": f"台账行 {row}；依据见 notes/PORT-LANDING-RECORD.md §十二/§十三",
                    }
            STATUS_JSON.write_text(json.dumps(status, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
            print(f"port-ledger-status.json 已保存（共 {len(commits)} 条状态，顺序与既有元数据保持原样）")
        else:
            print("  ! 未找到 port-ledger.json，仅更新了表格")
    return 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
