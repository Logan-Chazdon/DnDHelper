const fs = require('fs');

module.exports = {
    devServer: {
        allowedHosts: [
            'dndhelper.com*',
        ],
        port: 8081,
        host: 'dndhelper.com',
        hostname: 'dndhelper.com',
        static: false,
        disableHostCheck: true,
        public: 'store-client-nestroia1.c9users.io',
        server: {
            type: 'https',
            options: {
                key: fs.readFileSync('../../../../_wildcard.dndhelper.com+3-key.pem'),
                cert: fs.readFileSync('../../../../_wildcard.dndhelper.com+3.pem'),
                requestCert: true,
            },
        },
    },
};
