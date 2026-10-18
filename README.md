# Project Zomboid Web Port

A browser port of Project Zomboid. The original Java game runs unmodified-in-spirit inside a JavaScript JVM
([Doppio](https://github.com/plasma-umass/doppio)), rendering through a WebGL fixed-function emulation layer and
reading input from DOM events. There is no server component: everything runs client-side from static files.

> **Rights note:** this repository contains tooling and glue code. Project Zomboid and its assets belong to
> The Indie Stone. Make sure you have the right to use and host whatever game files you place in `pz-web.jar`.

## How it works

```
 index.html ── boots BrowserFS, downloads the game jar, creates the JVM, wires DOM events
     │
     ├── host/doppio.js    JVM implemented in JavaScript (interpreter + trace JIT), with PZ-specific intrinsics
     ├── host/pzcore.js    PZ host layer: PNG decoder, WebGL renderer, WebAudio, input, JVM native bindings
     ├── package/dist/browserfs.js   virtual filesystem (the JVM's /sys, /tmp, /game)
     └── pz-web.jar        the game, plus a small pz.shim.* package replacing LWJGL/AWT with native calls
```

**Boot sequence**

1. `index.html` starts downloading `pz-web.jar` immediately (cached in Cache Storage, keyed by `JAR_VERSION`),
   in parallel with BrowserFS and JVM setup.
2. BrowserFS mounts three filesystems: `/sys` (the Java class library, fetched lazily over XHR from
   `work/doppio/`), `/tmp` (in memory) and `/game` (in memory, where the jar is written).
3. A Doppio JVM is created with a trimmed bootstrap classpath (only the JRE jars the game needs), the game jar
   as the application classpath, and `zombie.GameWindow` as the main class.
4. `PZ.makeNatives()` registers the `pz/shim/N` native methods that connect Java to the browser.

**Java ↔ browser bridge (`pz/shim/N`)**

| Native | Purpose |
|---|---|
| `gl([FI)V` | Executes a batched command buffer of OpenGL calls (the Java side records calls into a `float[]`) |
| `texImage`, `texSub`, `texRead` | Texture upload, partial upload, and cached read-back |
| `decode`, `pixels`, `pixelsPadded` | Synchronous PNG decoding (pako) with bulk copies into Java arrays |
| `input([I)V` | Called once per frame: presents the finished frame, then hands mouse/keyboard state to Java |
| `display(II)V` | Reports the logical display size; the canvas is letterboxed to fit the window |
| `al*`, `audioDecode`, `audioPcm` | OpenAL replacement on WebAudio (opt-in) |
| `log([B)V` | Console logging |

**Renderer (`pzcore.js`)** emulates the fixed-function subset the game uses: matrix stacks, immediate-mode quads
and triangles, blending, texturing, render-to-texture and viewport. Vertices are transformed on the CPU at
`glVertex` time into a 20-byte packed format, batched, and drawn with a single small shader. Frames are rendered
to an offscreen texture and copied to the canvas only when the game finishes a frame, so half-drawn frames are
never shown. The module also loads under Node with a stub renderer for headless tests.

## Running it

Serve the folder with any static HTTP server (the page uses `fetch`/XHR, so `file://` will not work):

```
python3 -m http.server 8080
# then open http://localhost:8080/index.html
```

Expected layout:

```
index.html
favicon.ico
pz-web.jar
host/doppio.js
host/pzcore.js
package/dist/browserfs.js
work/doppio/listings.json
work/doppio/vendor/java_home/lib/rt.jar   (and the other JRE jars listed in KEEP_JARS)
work/doppio/classes/                      (doppio.security.DoppioProvider etc.)
```

`pako` is loaded from cdnjs; vendor it locally if you need to run offline.

### URL parameters

| Parameter | Effect |
|---|---|
| `?audio=1` | Enable WebAudio sound (experimental; starts on first click/keypress) |
| `?resp=<ms>` | JVM time-slice length before yielding to the browser (default `12`) |
| `?nocache=1` | Bypass the cached game jar |
| `?fulljre=1` | Load every JRE jar instead of the trimmed set (use if you hit `NoClassDefFoundError`) |
| `?prof=1` | Count Java method invocations (see *Profiling*) |

Press **F12** to toggle the dev overlay (FPS, frame time, draw calls, vertices, JS heap, canvas size).

When you change `pz-web.jar`, bump `JAR_VERSION` in `index.html` so returning visitors get the new copy.

## Performance design

A JVM running inside a JVM-in-JavaScript is inherently slow, so most of the engineering here is about keeping
per-call and per-frame overhead low. Typical results are around 20 fps on the development machine
(hardware dependent). The main pieces:

**Rendering and host layer**
- Vertex transform at `glVertex` time, quads emitted straight into the batch, packed 4-byte vertex colour.
- Redundant GL state changes (texture bind, blend, viewport, texture parameters) are skipped.
- Pooled, in-place matrix stack with an affine fast path (no per-vertex perspective divide).
- `present()` is skipped when nothing was drawn; `readPixels` results are cached until the texture changes.
- PNG decode fast paths for the common 8-bit formats; bulk typed-array copies into Java arrays.
- Input state is copied into the Java side with native bulk operations.

**Game jar**
- A fused texcoord+vertex call (`GL11.tv`) replaces two calls per vertex.
- Per-tile `ArrayList.get` hoisting and inlined small getters/enum/math helpers in hot loops.

**Doppio (interpreter / JIT)**
- No per-call bookkeeping: the interpreter's assertions and their eagerly built message strings are removed from
  the call/return path, frame constructors and invoke opcodes.
- Trace JIT triggers after 30 entries (down from 200) and the compiled-code lookup uses dense arrays that the
  frame loop reads directly.
- `scheduleResume` is a table lookup instead of a multi-case switch.
- Short JVM time slices (`?resp`, default 12 ms) keep the page responsive: the browser can paint and deliver
  input between slices instead of waiting up to a second.
- **Intrinsics:** tiny, very hot methods are answered directly from the method trampoline instead of pushing a
  full bytecode frame. They apply only when the caller is a bytecode frame and no callback is supplied; any
  unusual case (out-of-range index, a `Vector` lock held by another thread, a native or reflective caller) falls
  through to the real Java method, so exceptions and locking behave normally.

  | Target | Notes |
  |---|---|
  | `ArrayList.size/get`, `Vector.size/get` | `Vector` only when its monitor is free or owned by the current thread |
  | `Enum.ordinal`, `String.length/charAt`, `Math.min/max(int)`, `Math.sqrt`, `Object.<init>` | |
  | `GL11.tv(FFII)V` | Writes the six floats into the vertex buffer; falls through when a flush is needed |
  | Game-specific: `zombie.f.e.a.a`, `zombie.f.f.e.n/g`, `zombie.f.f.g.a`, `zombie.a.a.c.b`, `zombie.l.u.b`, `zombie.d.b.a.a` | Bitmask test on a `long`, int getters, texture-bound check, float distance/length |

  Intrinsics live in the table `window.__PZI` at the end of `host/doppio.js` and are injected when a class's
  trampolines are generated. The game-specific entries are keyed on the jar's obfuscated names: if the jar is
  rebuilt with different names they silently stop matching (nothing breaks, you just lose the speed-up).

### Invariants to preserve when editing Doppio

Because an intrinsic delivers its result immediately rather than via a later return, every call path must have
already consumed the call's operands before the call is made:

- Interpreter invoke opcodes pop the receiver and arguments **before** invoking.
- JIT-emitted call sites flush any pending stack temporaries **before** the call, so a result pushed by an
  intrinsic lands on top of them.

Break either rule and the operand stack is corrupted in ways that surface far from the cause (for example
`NegativeArraySizeException` during boot, or a wrong argument to `System.arraycopy`).

### Adding an intrinsic

1. Find candidates with the profiler (below); the best ones are tiny, leaf-like and called tens of millions of times.
2. Read the bytecode (`javap -p -c`) so the JS reproduces it exactly, including Java float (`Math.fround`),
   int and boolean (`0`/`1`) semantics.
3. Add an entry to `window.__PZI`, keyed `"<owner descriptor><name><descriptor>"` (for example
   `Ljava/util/ArrayList;size()I`). Leave `r` undefined to fall through to the real method; use `null` for a
   legitimate Java `null` result.
4. Static fields live on the class constructor under `"pkg/Class/field"`; instance fields on the object under the
   same key form. Doubles and longs occupy two stack words (value then `null`).
5. Unit-test the snippet against a reference implementation before shipping it.

## Profiling

- Load `index.html?prof=1`, play for a while, then run `pzprof(60)` in the console to list the most-invoked Java
  methods (`pzprofReset()` clears the counters). Counting has a cost, so FPS is lower in this mode.
- Record a Chrome DevTools Performance trace of normal play (without `?prof=1`) to see which interpreter
  functions dominate. Self-time per function is the most useful view.

## Known limitations

- Audio is experimental (OpenAL emulated on WebAudio) and off by default.
- Only the PNG variants and OpenGL subset the game uses are implemented; lines and points are not drawn.
- Performance is bounded by the JavaScript JVM. Further gains mostly come from doing less work per frame in the
  game code, or from more intrinsics for newly identified hot methods.
