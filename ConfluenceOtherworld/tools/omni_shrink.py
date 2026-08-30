import os
import glob
from PIL import Image

def process_image(image_path):
    """处理单张图片"""
    try:
        img = Image.open(image_path)
    except Exception as e:
        print(f"无法打开 {image_path}: {e}")
        return

    # 检查尺寸是否为 128x128
    if img.size != (128, 128):
        return

    # 1. 提取源矩形区域 (0,112) -> (48,128)
    src_box = (0, 112, 48, 128)   # (left, upper, right, lower)
    src_region = img.crop(src_box)

    # 2. 将提取的区域粘贴到目标位置 (64, 0)
    dst_pos = (64, 0)             # 粘贴区域的左上角
    img.paste(src_region, dst_pos)

    # 3. 裁剪左上角 112x112 区域
    cropped = img.crop((0, 0, 112, 112))

    # 4. 覆盖保存原文件（保持原格式）
    cropped.save(image_path)

def main(folder_path):
    """遍历文件夹中所有常见图片格式"""
    # 支持的扩展名（小写和大写）
    extensions = ('*.png', '*.jpg', '*.jpeg', '*.bmp', '*.tiff')
    for ext in extensions:
        for filepath in glob.glob(os.path.join(folder_path, ext)):
            process_image(filepath)
        for filepath in glob.glob(os.path.join(folder_path, ext.upper())):
            process_image(filepath)

if __name__ == "__main__":
    import sys
    if len(sys.argv) != 2:
        print("用法: python omni_shrink.py <文件夹路径>")
        sys.exit(1)
    main(sys.argv[1])
