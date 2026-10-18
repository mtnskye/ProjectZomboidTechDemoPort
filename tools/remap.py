#!/usr/bin/env python3
"""Rewrite constant pools of the game's classes so AWT/Swing/ImageIO/JavaSound references point at pz.shim.* classes."""
import re, struct, sys, zipfile, os

CLASS_MAP = {
 'java/awt/Canvas':'pz/shim/Canvas','java/applet/Applet':'pz/shim/Applet','java/awt/Frame':'pz/shim/Frame',
 'java/awt/Cursor':'pz/shim/Cursor','java/awt/Dimension':'pz/shim/Dimension','java/awt/Rectangle':'pz/shim/Rectangle',
 'java/awt/Component':'pz/shim/Component','java/awt/Image':'pz/shim/Image','java/awt/Graphics2D':'pz/shim/Graphics2D',
 'java/awt/Graphics':'pz/shim/Graphics','java/awt/image/ImageObserver':'pz/shim/ImageObserver',
 'java/awt/image/ColorModel':'pz/shim/ColorModel','java/awt/image/WritableRaster':'pz/shim/WritableRaster',
 'java/awt/image/BufferedImage':'pz/shim/BufferedImage','javax/swing/JOptionPane':'pz/shim/JOptionPane',
 'javax/imageio/ImageIO':'pz/shim/ImageIO','javax/sound/sampled/AudioSystem':'pz/shim/AudioSystem',
 'javax/sound/sampled/AudioInputStream':'pz/shim/AudioInputStream','javax/sound/sampled/AudioFormat$Encoding':'pz/shim/AudioFormat$Encoding',
 'javax/sound/sampled/AudioFormat':'pz/shim/AudioFormat',
}
def fix(s):
    if s in CLASS_MAP: return CLASS_MAP[s]
    if 'java' not in s: return s
    for k, v in CLASS_MAP.items():
        s = s.replace('L' + k + ';', 'L' + v + ';')
    return s

def remap(data):
    if data[:4] != b'\xca\xfe\xba\xbe': return data
    count = struct.unpack('>H', data[8:10])[0]
    pos = 10; ents = [None]  # index 0 unused
    i = 1
    while i < count:
        tag = data[pos]
        if tag == 1:
            ln = struct.unpack('>H', data[pos+1:pos+3])[0]
            ents.append(['u', data[pos+3:pos+3+ln].decode('latin-1')]); pos += 3 + ln
        elif tag in (3, 4): ents.append(['raw', data[pos:pos+5]]); pos += 5
        elif tag in (5, 6): ents.append(['raw', data[pos:pos+9]]); ents.append(None); pos += 9; i += 1
        elif tag in (7, 8, 16, 19, 20): ents.append(['raw', data[pos:pos+3]]); pos += 3
        elif tag in (9, 10, 11, 12, 17, 18): ents.append(['raw', data[pos:pos+5]]); pos += 5
        elif tag == 15: ents.append(['raw', data[pos:pos+4]]); pos += 4
        else: raise Exception('bad tag %d' % tag)
        i += 1
    rest = data[pos:]
    changed = False
    for e in ents:
        if e and e[0] == 'u':
            n = fix(e[1])
            if n != e[1]: e[1] = n; changed = True
    # redirect ByteBuffer.allocateDirect -> pz/shim/Util.allocateDirect
    def utf(idx): return ents[idx][1]
    newcls = None
    for idx, e in enumerate(ents):
        if e and e[0] == 'raw' and e[1][0] == 10:
            ci, nt = struct.unpack('>HH', e[1][1:5])
            cu = struct.unpack('>H', ents[ci][1][1:3])[0]
            if utf(cu) != 'java/nio/ByteBuffer': continue
            ni = struct.unpack('>H', ents[nt][1][1:3])[0]
            if utf(ni) != 'allocateDirect': continue
            if newcls is None:
                ents.append(['u', 'pz/shim/Util']); u = len(ents) - 1
                ents.append(['raw', b'\x07' + struct.pack('>H', u)]); newcls = len(ents) - 1
            ents[idx][1] = b'\x0a' + struct.pack('>HH', newcls, nt); changed = True
    if not changed: return data
    out = bytearray(data[:8]) + struct.pack('>H', len(ents))
    for e in ents[1:]:
        if e is None: continue
        if e[0] == 'u':
            b = e[1].encode('latin-1'); out += b'\x01' + struct.pack('>H', len(b)) + b
        else: out += e[1]
    return bytes(out) + rest

if __name__ == '__main__':
    src, dst = sys.argv[1], sys.argv[2]
    extra = sys.argv[3:]  # directories to merge in (classes)
    n = 0
    with zipfile.ZipFile(src) as zi, zipfile.ZipFile(dst, 'w', zipfile.ZIP_STORED) as zo:
        for it in zi.infolist():
            if it.is_dir() or it.filename.startswith('META-INF/'): continue
            d = zi.read(it.filename)
            if it.filename.endswith('.class'):
                d2 = remap(d); n += d2 != d; d = d2
            zo.writestr(it.filename, d)
        for ed in extra:
            for root, _, files in os.walk(ed):
                for f in files:
                    full = os.path.join(root, f); arc = os.path.relpath(full, ed)
                    zo.write(full, arc)
    print('remapped classes:', n)
