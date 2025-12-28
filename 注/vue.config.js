const { defineConfig } = require('@vue/cli-service');

module.exports = defineConfig({
  transpileDependencies: true,

  devServer: {
    // 配置跨域
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // 本地 API 地址
        ws: false,                      // 禁用 WebSocket
        changeOrigin: true,             // 允许跨域
        pathRewrite: {
          '^/api': ''                   // 重写路径
        }
      },
      '/socket': {
        target: 'ws://localhost:8080',  // 本地 WebSocket 地址
        changeOrigin: true,             // 允许跨域
        pathRewrite: {
          '^/socket': ''                // 重写路径
        },
        ws: true                         // 启用 WebSocket
      }
    }
  }
});