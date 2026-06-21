"""Convert Port*Extension static calls to direct instance method calls in ConfluenceOtherworld."""
import os
import re
import sys

ROOT = r"D:\Minecraft\1.20forge\confluence"
SRC_DIR = os.path.join(ROOT, "ConfluenceOtherworld", "src")

# Map: (extension_class, static_method) -> None means "NOT thiz method, don't convert"
# Items kept as static calls (no thiz parameter, or no IPort*Extension):
# - PortListExtension.* -> java.util.List can't be injected
# - PortCodecExtension.* -> user said skip
# - PortDataResultExtension.* -> no IPortDataResultExtension
# - PortInventoryScreenExtension.* -> no IPortInventoryScreenExtension
# - PortModelResourceLocationExtension.* -> no IPortModelResourceLocationExtension
# - PortAdvancementProgressExtension.* -> no IPortAdvancementProgressExtension
# - PortItemStackExtension.codec/singleItemCodec/strictCodec/.../isSameItemSameComponents/hash*/listMatches -> not thiz

SKIP_WHOLE = {
    "PortListExtension",
    "PortCodecExtension",
    "PortDataResultExtension",
    "PortInventoryScreenExtension",
    "PortModelResourceLocationExtension",
    "PortAdvancementProgressExtension",
    "PortEnchantmentHelperExtension",
    "PortProjectileUtilExtension",
    "PortParticleUtilsExtension",
    "PortTagParserExtension",
    "PortContainersExtension",
    "PortAttributesExtension",
    "PortFluidStackExtension",
    "PortRecipeExtension",
    "PortSoundTypeExtension",
    "PortBlockSetTypeExtension",
    "PortStatePropertiesPredicateExtension",
    "PortItemPredicateExtension",
    "PortNbtPredicateExtension",
    "PortEntityPredicateExtension",
    "PortIConditionExtension",
    "PortUnitExtension",
    "PortEitherExtension",
}

# Specific static methods to skip (no thiz param)
SKIP_METHODS = {
    ("PortItemStackExtension", "codec"),
    ("PortItemStackExtension", "singleItemCodec"),
    ("PortItemStackExtension", "strictCodec"),
    ("PortItemStackExtension", "strictSingleItemCodec"),
    ("PortItemStackExtension", "optionalCodec"),
    ("PortItemStackExtension", "simpleItemCodec"),
    ("PortItemStackExtension", "optionalStreamCodec"),
    ("PortItemStackExtension", "streamCodec"),
    ("PortItemStackExtension", "optionalListStreamCodec"),
    ("PortItemStackExtension", "listStreamCodec"),
    ("PortItemStackExtension", "itemNonAirCodec"),
    ("PortItemStackExtension", "hashItemAndComponents"),
    ("PortItemStackExtension", "hashStackList"),
    ("PortItemStackExtension", "listMatches"),
    ("PortItemStackExtension", "isSameItemSameComponents"),
    ("PortItemStackExtension", "validateStrict"),
    ("PortLivingEntityExtension", "getSlotForHand"),
}

# Pattern: PortXxxExtension.methodName(PARAM1, rest...)
# We want: PARAM1.methodName(rest...)
STATIC_CALL_RE = re.compile(r'(\bPort\w+Extension)\.(\w+)\s*\(')

def find_import_line(lines, class_name):
    """Find line number of import for given class."""
    import_pattern = re.compile(rf'^import\s+.*\.{re.escape(class_name)}\s*;')
    for i, line in enumerate(lines):
        if import_pattern.match(line):
            return i
    return None

def is_extension_import(line):
    """Check if line is an import for any Port*Extension class."""
    return bool(re.match(r'^import\s+PortLib\.extensions\..*\.Port\w+Extension\s*;', line))

def get_used_extensions(text):
    """Get set of extension classes still used in text."""
    used = set()
    for m in re.finditer(r'(\bPort\w+Extension)\.', text):
        used.add(m.group(1))
    return used

def convert_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        original = f.read()

    text = original
    changes = 0

    # Find all static calls
    for m in STATIC_CALL_RE.finditer(text):
        ext_class = m.group(1)
        method = m.group(2)

        if ext_class in SKIP_WHOLE:
            continue
        if (ext_class, method) in SKIP_METHODS:
            continue

        # Find the full call starting from this position
        pos = m.start()
        # Extract the first argument (the thiz parameter)
        start = m.end()
        depth = 1
        i = start
        first_arg_end = None
        while i < len(text) and depth > 0:
            c = text[i]
            if c == '(': depth += 1
            elif c == ')': depth -= 1
            elif c == ',' and depth == 1:
                first_arg_end = i
                break
            elif depth == 0:
                break
            i += 1

        if first_arg_end is None:
            # Could be a no-arg call or couldn't parse
            continue

        first_arg = text[start:first_arg_end].strip()
        # Check if it's a valid variable/expression (not a literal)
        if not first_arg or first_arg[0].isdigit() or first_arg.startswith('"') or first_arg.startswith('new ') or first_arg.startswith('Codec.') or first_arg.startswith('MapCodec.'):
            continue

        # Build replacement: first_arg.method(
        replacement = f"{first_arg}.{method}("
        old_text = f"{ext_class}.{method}({first_arg}, "
        new_text = f"{first_arg}.{method}("

        if old_text in text:
            text = text.replace(old_text, new_text)
            changes += 1
        else:
            # Try with different spacing
            alt_old = f"{ext_class}.{method}({first_arg},"
            alt_new = f"{first_arg}.{method}("
            if alt_old in text:
                text = text.replace(alt_old, alt_new)
                changes += 1

    # Handle single-arg thiz calls: PortXxx.method(thiz) -> thiz.method()
    for ext_class in set(m.group(1) for m in STATIC_CALL_RE.finditer(original)):
        if ext_class in SKIP_WHOLE:
            continue
        # Pattern: PortXxx.method(THIZ)
        pattern = re.compile(rf'{re.escape(ext_class)}\.(\w+)\((\w+)\)\s*;')
        for m in pattern.finditer(text):
            method = m.group(1)
            thiz = m.group(2)
            if (ext_class, method) in SKIP_METHODS:
                continue
            if thiz[0].isdigit() or thiz.startswith('"') or thiz.startswith('new '):
                continue
            old = f"{ext_class}.{method}({thiz});"
            new = f"{thiz}.{method}();"
            text = text.replace(old, new)
            changes += 1

    if changes == 0:
        return False

    # Remove unused extension imports
    used = get_used_extensions(text)
    lines = text.split('\n')
    new_lines = []
    for line in lines:
        if is_extension_import(line):
            # Check which class this import is for
            m = re.match(r'^import\s+.*\.(\w+)\s*;', line)
            if m and m.group(1) not in used:
                continue  # skip this import
        new_lines.append(line)

    text = '\n'.join(new_lines)

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(text)

    return True

def main():
    count = 0
    for root, dirs, files in os.walk(SRC_DIR):
        for f in files:
            if f.endswith('.java'):
                filepath = os.path.join(root, f)
                if convert_file(filepath):
                    count += 1
                    relpath = os.path.relpath(filepath, ROOT)
                    print(f"Converted: {relpath}")

    print(f"\nTotal files converted: {count}")

if __name__ == '__main__':
    main()
