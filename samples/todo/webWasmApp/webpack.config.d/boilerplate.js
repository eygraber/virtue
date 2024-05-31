if(config.devServer) {
  config.devServer.historyApiFallback = {
    rewrites: [
      { from: /.*todo-sample-wasm.wasm/, to: '/todo-sample-wasm.wasm' },
    ]
  }
}
