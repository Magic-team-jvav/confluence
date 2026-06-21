"""Convert Port*Extension static calls in Confluence-Magic-Lib (submodule)."""
import os
import re

ROOT = r"D:\Minecraft\1.20forge\confluence\Confluence-Magic-Lib"
SRC = os.path.join(ROOT, "src")

# Exact string replacements
REPLACEMENTS = [
    # PortItemStackExtension
    ("PortItemStackExtension.getData(stack, ", "stack.getData("),
    ("PortItemStackExtension.getData(groupStack, ", "groupStack.getData("),
    ("PortItemStackExtension.getData(group, ", "group.getData("),
    ("PortItemStackExtension.getDataOrDefault(stack, ", "stack.getDataOrDefault("),
    ("PortItemStackExtension.getDataOrDefault(groupStack, ", "groupStack.getDataOrDefault("),
    ("PortItemStackExtension.getDataOrDefault(element, ", "element.getDataOrDefault("),
    ("PortItemStackExtension.setData(stack, ", "stack.setData("),
    ("PortItemStackExtension.setData(groupStack, ", "groupStack.setData("),
    ("PortItemStackExtension.setData(group, ", "group.setData("),
    ("PortItemStackExtension.getPrototypeData(stack)", "stack.getPrototypeData()"),
    ("PortItemStackExtension.getPortAttributeModifiers(stack)", "stack.getPortAttributeModifiers()"),
    ("PortItemStackExtension.setCustomName(stack, ", "stack.setCustomName("),

    # PortDataResultExtension -> KEEP (no IPortDataResultExtension)

    # PortEntityExtension attachment methods -> RENAMED
    ("PortEntityExtension.getExistingAttachOrNull(livingEntity, ", "livingEntity.getExistingAttachmentOrNull("),
    ("PortEntityExtension.getExistingAttachOrNull(entity, ", "entity.getExistingAttachmentOrNull("),
    ("PortEntityExtension.removeAttach(livingEntity, ", "livingEntity.removeAttachment("),
    ("PortEntityExtension.removeAttach(entity, ", "entity.removeAttachment("),

    # PortEntityExtension regular methods
    ("PortEntityExtension.getInBlockState(entity)", "entity.getInBlockState()"),
    ("PortEntityExtension.getRandom(entity)", "entity.getRandom()"),
    ("PortEntityExtension.registryAccess(entity)", "entity.registryAccess()"),
    ("PortEntityExtension.registryAccess(player)", "player.registryAccess()"),
    ("PortEntityExtension.igniteForTicks(entity, ", "entity.igniteForTicks("),
    ("PortEntityExtension.getKnownMovement(entity)", "entity.getKnownMovement()"),
    ("PortEntityExtension.getWeaponItem(entity)", "entity.getWeaponItem()"),
    ("PortEntityExtension.copyAttachmentsFrom(entity, ", "entity.copyAttachmentsFrom("),

    # PortMobEffectInstanceExtension
    ("PortMobEffectInstanceExtension.getPortCures(instance)", "instance.getPortCures()"),
    ("PortMobEffectInstanceExtension.is(instance, ", "instance.is("),

    # PortPlayerExtension
    ("PortPlayerExtension.hasInfiniteMaterials(player)", "player.hasInfiniteMaterials()"),
    ("PortPlayerExtension.blockInteractionRange(player)", "player.blockInteractionRange()"),
    ("PortPlayerExtension.entityInteractionRange(player)", "player.entityInteractionRange()"),
    ("PortPlayerExtension.hasCorrectToolForDrops(player, ", "player.hasCorrectToolForDrops("),

    # PortHolderExtension
    ("PortHolderExtension.getRegisteredName(holder)", "holder.getRegisteredName()"),
    ("PortHolderExtension.getKey(holder)", "holder.getKey()"),
    ("PortHolderExtension.is(holder, ", "holder.is("),

    # PortBlockExtension
    ("PortBlockExtension.isEmpty(block, ", "block.isEmpty("),

    # PortFoodPropertiesExtension
    ("PortFoodPropertiesExtension.getEatSeconds(food)", "food.getEatSeconds()"),
    ("PortFoodPropertiesExtension.usingConvertsTo(food)", "food.usingConvertsTo()"),
    ("PortFoodPropertiesExtension.effects(food)", "food.effects()"),

    # PortBlockPosExtension
    ("PortBlockPosExtension.getBottomCenter(pos)", "pos.getBottomCenter()"),
    ("PortBlockPosExtension.getBottomCenter(blockPos)", "blockPos.getBottomCenter()"),

    # PortAABBExtension
    ("PortAABBExtension.getMinPosition(aabb)", "aabb.getMinPosition()"),
    ("PortAABBExtension.getMaxPosition(aabb)", "aabb.getMaxPosition()"),


    # PortResourceKeyExtension
    ("PortResourceKeyExtension.registryKey(key)", "key.registryKey()"),

    # PortLivingEntityExtension
    ("PortLivingEntityExtension.getAttribute(livingEntity, ", "livingEntity.getAttribute("),
    ("PortLivingEntityExtension.getAttribute(entity, ", "entity.getAttribute("),
    ("PortLivingEntityExtension.removeEffectsCuredBy(livingEntity, ", "livingEntity.removeEffectsCuredBy("),
    ("PortLivingEntityExtension.getWeaponItem(livingEntity)", "livingEntity.getWeaponItem()"),
    ("PortLivingEntityExtension.hasInfiniteMaterials(livingEntity)", "livingEntity.hasInfiniteMaterials()"),

    # PortItemStackExtension - remaining thiz methods
    ("PortItemStackExtension.getPortEnchantmentsOrDefault(stack, ", "stack.getPortEnchantmentsOrDefault("),
    ("PortItemStackExtension.getPortEnchantments(stack)", "stack.getPortEnchantments()"),
    ("PortItemStackExtension.getEnchantmentLevel(stack, ", "stack.getEnchantmentLevel("),
    ("PortItemStackExtension.getAllPortEnchantments(stack, ", "stack.getAllPortEnchantments("),
    ("PortItemStackExtension.is(stack, ", "stack.is("),
    ("PortItemStackExtension.consume(stack, ", "stack.consume("),
    ("PortItemStackExtension.hurtAndBreak(stack, ", "stack.hurtAndBreak("),
    ("PortItemStackExtension.getFood(stack, ", "stack.getFood("),
    ("PortItemStackExtension.setFood(stack, ", "stack.setFood("),
    ("PortItemStackExtension.getTool(stack)", "stack.getTool()"),
    ("PortItemStackExtension.setTool(stack, ", "stack.setTool("),
    ("PortItemStackExtension.getUnbreakable(stack)", "stack.getUnbreakable()"),
    ("PortItemStackExtension.setCustomName(stack, ", "stack.setCustomName("),
    ("PortItemStackExtension.getCustomName(stack)", "stack.getCustomName()"),
    ("PortItemStackExtension.getItemName(stack)", "stack.getItemName()"),
    ("PortItemStackExtension.getLore(stack)", "stack.getLore()"),
    ("PortItemStackExtension.getShowEnchantmentsTooltip(stack)", "stack.getShowEnchantmentsTooltip()"),
    ("PortItemStackExtension.setShowEnchantmentsTooltip(stack, ", "stack.setShowEnchantmentsTooltip("),
    ("PortItemStackExtension.getCanPlaceOn(stack, ", "stack.getCanPlaceOn("),
    ("PortItemStackExtension.getCanBreak(stack, ", "stack.getCanBreak("),
    ("PortItemStackExtension.getCustomModelData(stack)", "stack.getCustomModelData()"),
    ("PortItemStackExtension.getRepaireCost(stack)", "stack.getRepaireCost()"),
    ("PortItemStackExtension.getCreativeSlotLock(stack)", "stack.getCreativeSlotLock()"),
    ("PortItemStackExtension.setCreativeSlotLock(stack, ", "stack.setCreativeSlotLock("),
    ("PortItemStackExtension.getEnchantmentGlintOverride(stack)", "stack.getEnchantmentGlintOverride()"),
    ("PortItemStackExtension.setEnchantmentGlintOverride(stack, ", "stack.setEnchantmentGlintOverride("),
    ("PortItemStackExtension.getIntangibleProjectile(stack)", "stack.getIntangibleProjectile()"),
    ("PortItemStackExtension.setIntangibleProjectile(stack, ", "stack.setIntangibleProjectile("),
    ("PortItemStackExtension.getFireResistant(stack)", "stack.getFireResistant()"),
    ("PortItemStackExtension.setFireResistant(stack, ", "stack.setFireResistant("),
    ("PortItemStackExtension.getStoredEnchantments(stack)", "stack.getStoredEnchantments()"),
    ("PortItemStackExtension.setStoredEnchantments(stack, ", "stack.setStoredEnchantments("),
    ("PortItemStackExtension.getDyedColor(stack)", "stack.getDyedColor()"),
    ("PortItemStackExtension.setDyedColor(stack, ", "stack.setDyedColor("),
    ("PortItemStackExtension.getMapColor(stack)", "stack.getMapColor()"),
    ("PortItemStackExtension.setMapColor(stack, ", "stack.setMapColor("),
    ("PortItemStackExtension.getMapId(stack)", "stack.getMapId()"),
    ("PortItemStackExtension.setMapId(stack, ", "stack.setMapId("),
    ("PortItemStackExtension.getMapPostProcessing(stack)", "stack.getMapPostProcessing()"),
    ("PortItemStackExtension.setMapPostProcessing(stack, ", "stack.setMapPostProcessing("),
    ("PortItemStackExtension.getChargedProjectiles(stack)", "stack.getChargedProjectiles()"),
    ("PortItemStackExtension.setChargedProjectiles(stack, ", "stack.setChargedProjectiles("),
    ("PortItemStackExtension.getBundleContents(stack)", "stack.getBundleContents()"),
    ("PortItemStackExtension.setBundleContents(stack, ", "stack.setBundleContents("),
    ("PortItemStackExtension.getPotionContents(stack)", "stack.getPotionContents()"),
    ("PortItemStackExtension.setPotionContents(stack, ", "stack.setPotionContents("),
    ("PortItemStackExtension.getSuspiciousStewEffects(stack)", "stack.getSuspiciousStewEffects()"),
    ("PortItemStackExtension.setSuspiciousStewEffects(stack, ", "stack.setSuspiciousStewEffects("),
    ("PortItemStackExtension.getTrim(stack)", "stack.getTrim()"),
    ("PortItemStackExtension.setTrim(stack, ", "stack.setTrim("),
    ("PortItemStackExtension.getDebugStickState(stack)", "stack.getDebugStickState()"),
    ("PortItemStackExtension.setDebugStickState(stack, ", "stack.setDebugStickState("),
    ("PortItemStackExtension.getEntityData(stack, ", "stack.getEntityData("),
    ("PortItemStackExtension.setEntityData(stack, ", "stack.setEntityData("),
    ("PortItemStackExtension.getBucketEntityData(stack, ", "stack.getBucketEntityData("),
    ("PortItemStackExtension.setBucketEntityData(stack, ", "stack.setBucketEntityData("),
    ("PortItemStackExtension.getBlockEntityData(stack, ", "stack.getBlockEntityData("),
    ("PortItemStackExtension.setBlockEntityData(stack, ", "stack.setBlockEntityData("),
    ("PortItemStackExtension.getInstrument(stack)", "stack.getInstrument()"),

    # PortFriendlyByteBufExtension
    ("PortFriendlyByteBufExtension.wrap(buffer)", "buffer.wrap()"),
    ("PortFriendlyByteBufExtension.readMap(buffer, ", "buffer.readMap("),
    ("PortFriendlyByteBufExtension.writeMap(buffer, ", "buffer.writeMap("),

    # Fix .of() patterns from earlier
    ("IPortEntityExtension.of(projectile).getGravity()", "projectile.getGravity()"),

    # Cleanup remaining
    ("PortItemStackExtension.removeData(stack, ", "stack.removeData("),
    ("PortHolderLookupExtension.Provider.holderOrThrow(", "level.registryAccess().holderOrThrow("),  # specific case
]

IMPORT_RE = re.compile(r'^import\s+PortLib\.extensions\..*\.(Port\w+Extension)\s*;')

def get_used_static_exts(text):
    return set(re.findall(r'\b(Port\w+Extension)\.', text))

def fix_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        text = f.read()

    original = text
    changed = False

    for old, new in REPLACEMENTS:
        if old in text:
            text = text.replace(old, new)
            changed = True

    if not changed:
        return False

    # Remove unused extension imports
    used = get_used_static_exts(text)
    lines = text.split('\n')
    new_lines = []
    for line in lines:
        m = IMPORT_RE.match(line)
        if m:
            if m.group(1) not in used:
                continue
        new_lines.append(line)

    text = '\n'.join(new_lines)

    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(text)

    return True

if __name__ == '__main__':
    count = 0
    for root, dirs, files in os.walk(SRC):
        for f in files:
            if f.endswith('.java'):
                fp = os.path.join(root, f)
                if fix_file(fp):
                    count += 1
                    print(f"Fixed: {os.path.relpath(fp, ROOT)}")
    print(f"\nTotal: {count}")
