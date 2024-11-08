//const path = require('path');
config.devServer.allowedHosts= ['.dndhelper.com']
module.exports = {
  //...
  devServer: {
    allowedHosts: [
      'dndhelper.com',
      'subdomain.host.com',
      'subdomain2.host.com',
      'host2.com',
    ],
  },
};
