if(config.devServer) {
  config.devServer.historyApiFallback = {
    rewrites: [
      { from: /.*auth-sample-wasm.wasm/, to: '/auth-sample-wasm.wasm' },
    ]
  }
}
