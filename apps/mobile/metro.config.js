// // Learn more https://docs.expo.io/guides/customizing-metro
// const { getDefaultConfig } = require('expo/metro-config')

// /** @type {import('expo/metro-config').MetroConfig} */
// const config = getDefaultConfig(__dirname)

// // Add wasm asset support
// config.resolver.assetExts.push('wasm')

// // Add COEP and COOP headers to support SharedArrayBuffer
// config.server.enhanceMiddleware = (middleware) => {
//   return (req, res, next) => {
//     res.setHeader('Cross-Origin-Embedder-Policy', 'credentialless')
//     res.setHeader('Cross-Origin-Opener-Policy', 'same-origin')
//     middleware(req, res, next)
//   }
// }

// module.exports = config

// Learn more https://docs.expo.io/guides/customizing-metro
const { getDefaultConfig } = require('expo/metro-config');

/** @type {import('expo/metro-config').MetroConfig} */
const config = getDefaultConfig(__dirname);

// 1. Desactiva la resolución moderna de exports que causa el conflicto
config.resolver.unstable_enablePackageExports = false;

// 2. Mantén la prioridad de condiciones por si acaso
config.resolver.unstable_conditionNames = ['browser', 'require', 'react-native'];

config.resolver.assetExts.push('wasm');

config.server.enhanceMiddleware = (middleware) => {
  return (req, res, next) => {
    res.setHeader('Cross-Origin-Embedder-Policy', 'credentialless');
    res.setHeader('Cross-Origin-Opener-Policy', 'same-origin');
    middleware(req, res, next);
  };
};

module.exports = config;
