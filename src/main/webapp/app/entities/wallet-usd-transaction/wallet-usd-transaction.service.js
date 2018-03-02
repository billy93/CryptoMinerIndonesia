(function() {
    'use strict';
    angular
        .module('cryptoMinerIndonesiaApp')
        .factory('WalletUsdTransaction', WalletUsdTransaction);

    WalletUsdTransaction.$inject = ['$resource'];

    function WalletUsdTransaction ($resource) {
        var resourceUrl =  'api/wallet-usd-transactions/:id';

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
