"""Regenerate all 24 JavaJolt PNG sprites from the team's Aseprite sources.

Requirements: Python 3.9+ and Pillow (`python -m pip install pillow`).
The supplied source pack contains one frame, one visible 32-bit RGBA cel each.
No Aseprite program or CLI installation is required.
"""

from collections import deque
from pathlib import Path
import struct
import zlib
from PIL import Image

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / 'art-source' / 'characters'
OUT = ROOT / 'src' / 'edu' / 'batstateu' / 'javajolt' / 'ui' / 'swing' / 'assets' / 'characters'

# Order per entry: idle, correct (Happy), hurt (Ouch/damaged).
CHARACTERS = {
    'mascot': ('Main_Mascot/JavaJult.aseprite', 'Main_Mascot/JavaJult_Happy.aseprite', 'Main_Mascot/JavaJult_Ouch.aseprite'),
    'variables': ('Variables/Variables.aseprite', 'Variables/Variables_Happy.aseprite', 'Variables/Variables_Ouch.aseprite'),
    'operators': ('Operators/Operators.aseprite', 'Operators/Operators_Happy.aseprite', 'Operators/Operators_Ouch.aseprite'),
    'decisions': ('Decisions/Decisions.aseprite', 'Decisions/Decisions_Happy.aseprite', 'Decisions/Decisions_Ouch.aseprite'),
    'loops': ('Loops/Loops-idle.aseprite', 'Loops/Loops-correct.aseprite', 'Loops/Loops-damaged.aseprite'),
    'arrays': ('Arrays/Array_idle.aseprite', 'Arrays/Array_correct.aseprite', 'Arrays/Array_damaged.aseprite'),
    'exception': ('Exception Handling/Exeption_Handling_idle.aseprite', 'Exception Handling/Exeption_Handling_correct.aseprite', 'Exception Handling/Exeption_Handling_damaged.aseprite'),
    'oop': ('OOP/OOP_idle.aseprite', 'OOP/OOP_correct.aseprite', 'OOP/OOP_damaged.aseprite'),
}


def read_aseprite(path):
    data = path.read_bytes()
    file_size, magic, frame_count, width, height, depth = struct.unpack_from('<IHHHHH', data)
    if magic != 0xA5E0 or file_size != len(data) or frame_count != 1 or depth != 32:
        raise ValueError(f'{path}: expected a one-frame, 32-bit RGBA Aseprite image')
    frame_size, frame_magic, old_chunks, duration = struct.unpack_from('<IHHH', data, 128)
    if frame_magic != 0xF1FA:
        raise ValueError(f'{path}: invalid frame header')
    chunks = struct.unpack_from('<I', data, 140)[0] or old_chunks
    layers = []
    canvas = Image.new('RGBA', (width, height), (0, 0, 0, 0))
    offset = 144
    for _ in range(chunks):
        length, chunk_type = struct.unpack_from('<IH', data, offset)
        payload = data[offset + 6:offset + length]
        if chunk_type == 0x2004:
            flags, layer_type = struct.unpack_from('<HH', payload)
            layers.append((flags, layer_type))
        elif chunk_type == 0x2005:
            layer, x, y, opacity, cel_type = struct.unpack_from('<HhhBH', payload)
            if layer >= len(layers) or not (layers[layer][0] & 1):
                offset += length
                continue
            if cel_type != 2:
                raise ValueError(f'{path}: expected compressed RGBA cel')
            cel_width, cel_height = struct.unpack_from('<HH', payload, 16)
            pixels = zlib.decompress(payload[20:])
            cel = Image.frombytes('RGBA', (cel_width, cel_height), pixels)
            if opacity != 255:
                cel.putalpha(cel.getchannel('A').point(lambda value: value * opacity // 255))
            layer_image = Image.new('RGBA', canvas.size, (0, 0, 0, 0))
            layer_image.paste(cel, (x, y))
            canvas = Image.alpha_composite(canvas, layer_image)
        offset += length
    return canvas


def clear_external_white_background(image):
    """Remove only white pixels connected to the canvas edge, not suit details."""
    pixels = image.load()
    width, height = image.size
    queue = deque()
    for x in range(width):
        queue.extend(((x, 0), (x, height - 1)))
    for y in range(height):
        queue.extend(((0, y), (width - 1, y)))
    seen = set()
    while queue:
        x, y = queue.popleft()
        if (x, y) in seen:
            continue
        seen.add((x, y))
        red, green, blue, alpha = pixels[x, y]
        if min(red, green, blue) < 250:
            continue
        pixels[x, y] = (0, 0, 0, 0)
        for nx, ny in ((x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1)):
            if 0 <= nx < width and 0 <= ny < height and (nx, ny) not in seen:
                queue.append((nx, ny))
    return image


def main():
    count = 0
    for name, source_files in CHARACTERS.items():
        dest = OUT / ('mascot' if name == 'mascot' else f'enemies/{name}')
        dest.mkdir(parents=True, exist_ok=True)
        for emotion, source_file in zip(('idle', 'correct', 'hurt'), source_files):
            image = read_aseprite(SRC / source_file)
            if name == 'mascot':
                image = clear_external_white_background(image)
            image.save(dest / f'{emotion}.png', optimize=True)
            count += 1
    print(f'Exported {count} character sprites.')


if __name__ == '__main__':
    main()
