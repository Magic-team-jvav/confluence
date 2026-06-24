"""Convert PortItem.PortProperties and PortItemExtension.Properties static calls to injected IPortItemPropertiesExtension."""
import os
import re

ROOT = r"D:\Minecraft\1.20forge\confluence"
SRC_DIRS = [
    os.path.join(ROOT, "ConfluenceOtherworld", "src"),
    os.path.join(ROOT, "Confluence-Magic-Lib", "src"),
    os.path.join(ROOT, "TerraCurio", "src"),
    os.path.join(ROOT, "TerraFurniture", "src"),
]

REPLACEMENTS = [
    # Replace PortItem.PortProperties with Item.Properties
    ("new PortItem.PortProperties()", "new Item.Properties()"),

    # PortItemExtension.Properties.xxx(properties, args) → properties.xxx(args)
    ("PortItemExtension.Properties.component(properties, ", "properties.component("),
    ("PortItemExtension.Properties.unbreakable(properties)", "properties.unbreakable()"),
    ("PortItemExtension.Properties.getAttributes(properties)", "properties.getAttributes()"),
    ("PortItemExtension.Properties.getComponent(properties, ", "properties.getComponent("),
    ("PortItemExtension.Properties.dyedColor(properties, ", "properties.dyedColor("),
    ("PortItemExtension.Properties.attributes(properties, ", "properties.attributes("),

    # PortItemExtension.Properties.xxx(new Item.Properties(), args) → new Item.Properties().xxx(args)
    ("PortItemExtension.Properties.component(new Item.Properties(), ", "new Item.Properties().component("),
]

IMPORT_PORTITEM = re.compile(r'^import\s+org\.mesdag\.portlib\.wrapper\.world\.item\.PortItem\s*;')
IMPORT_EXT = re.compile(r'^import\s+PortLib\.extensions\.net\.minecraft\.world\.item\.Item\.PortItemExtension\s*;')

def fix_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        text = f.read()

    changed = False
    for old, new in REPLACEMENTS:
        if old in text:
            text = text.replace(old, new)
            changed = True

    if not changed:
        return False

    # Remove unused imports
    lines = text.split('\n')
    new_lines = []
    for line in lines:
        if IMPORT_PORTITEM.match(line) and not re.search(r'\bPortItem\b', text):
            continue
        if IMPORT_EXT.match(line):
            # Only keep if still used as static (e.g., PortItemExtension.xxx without Properties)
            if 'PortItemExtension.Properties.' not in text and not re.search(r'\bPortItemExtension\.(?!Properties\.)', text):
                continue
        new_lines.append(line)

    text = '\n'.join(new_lines)

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(text)
    return True

if __name__ == '__main__':
    count = 0
    for src_dir in SRC_DIRS:
        if not os.path.isdir(src_dir):
            continue
        for root, dirs, files in os.walk(src_dir):
            for f in files:
                if f.endswith('.java'):
                    fp = os.path.join(root, f)
                    if fix_file(fp):
                        count += 1
                        print(f"Fixed: {os.path.relpath(fp, ROOT)}")
    print(f"\nTotal: {count}")
