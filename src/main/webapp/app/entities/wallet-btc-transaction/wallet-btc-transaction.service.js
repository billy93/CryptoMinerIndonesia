(function() {
    'use strict';
    angular
        .module('cryptoMinerIndonesiaApp')
        .factory('WalletBtcTransaction', WalletBtcTransaction);

    WalletBtcTransaction.$inject = ['$resource'];

    function WalletBtcTransaction ($resource) {
        var resourceUrl =  'api/wallet-btc-transactions/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
