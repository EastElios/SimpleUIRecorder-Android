from PIL import Image, ImageDraw, ImageFont
import os

# 定义图标尺寸
sizes = {
    'mipmap-mdpi': 48,
    'mipmap-hdpi': 72,
    'mipmap-xhdpi': 96,
    'mipmap-xxhdpi': 144,
    'mipmap-xxxhdpi': 192
}

base_dir = os.path.expanduser('~/Desktop/HomeAutomation/app/src/main/res')

for folder, size in sizes.items():
    # 创建目录
    folder_path = os.path.join(base_dir, folder)
    os.makedirs(folder_path, exist_ok=True)

    # 创建图片
    img = Image.new('RGBA', (size, size), (102, 126, 234, 255))
    draw = ImageDraw.Draw(img)

    # 绘制简单的房子图标
    margin = size // 6
    draw_size = size - 2 * margin

    # 房子主体
    body_left = margin
    body_right = size - margin
    body_top = margin + draw_size // 2
    body_bottom = size - margin
    draw.rectangle([body_left, body_top, body_right, body_bottom],
                   fill=(255, 255, 255, 255), outline=(255, 255, 255, 255), width=2)

    # 屋顶
    roof_top = margin + draw_size // 4
    roof_bottom = body_top
    draw.polygon([
        (margin + draw_size // 2, roof_top),
        (margin, roof_bottom),
        (size - margin, roof_bottom)
    ], fill=(255, 235, 59, 255), outline=(255, 255, 255, 255), width=2)

    # 门
    door_width = draw_size // 5
    door_height = draw_size // 3
    door_left = size // 2 - door_width // 2
    door_top = body_bottom - door_height
    draw.rectangle([door_left, door_top, door_left + door_width, body_bottom],
                   fill=(255, 152, 0, 255))

    # 保存
    img.save(os.path.join(folder_path, 'ic_launcher.png'))
    img.save(os.path.join(folder_path, 'ic_launcher_round.png'))

print("图标创建完成！")