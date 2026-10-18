// Headless Node test: boots the patched game jar in the Doppio JVM with a stub renderer.
const path = require('path');
const D = path.resolve(__dirname, '../work/doppio');
const javaCLI = require(D + '/dist/release-cli/src/java_cli').default;
const enums = require(D + '/dist/release-cli/src/enums');
const pako = require(D + '/node_modules/pako');
const PZ = require('./pzcore.js');
let frames = 0, glCalls = 0, last = Date.now(), t0 = Date.now(), cmds = 0;
const env = { renderer: { exec(c, n) { glCalls++; cmds += n / 5; }, texImage() { env.tex = (env.tex || 0) + 1; }, texSub() {}, texRead() {} },
  audio: null, inflate: pako.inflate, ASYNC: enums.ThreadStatus.ASYNC_WAITING,
  log: (m) => console.log('[game]', m), onDisplay: (w, h) => console.log('[display]', w, h) };
const jar = process.argv[2] || path.resolve(__dirname, '../build/pz-web.jar');
process.on('uncaughtException', (e) => { console.log('UNCAUGHT', e && e.stack || e); process.exit(2); });
javaCLI(['-classpath', jar, 'zombie.GameWindow'], { doppioHomePath: D, nativeClasspath: [], launcherName: 'x', intMode: false, dumpJITStats: false, tmpDir: require('os').tmpdir() },
  (s) => { console.log('JVM exit', s); process.exit(s); },
  (jvm) => {
    jvm.registerNatives(PZ.makeNatives(env));
    setInterval(() => console.log(`[t=${((Date.now() - t0) / 1000).toFixed(0)}s] gl() calls=${glCalls} cmds=${cmds | 0} textures=${env.tex || 0}`), 5000);
    setTimeout(() => { console.log('timeout reached'); process.exit(0); }, (+process.argv[3] || 120) * 1000);
  });
