(function() {
    'use strict';
    angular
        .module('cryptoMinerIndonesiaApp')
        .factory('Withdraw', Withdraw);

    Withdraw.$inject = ['$resource'];

    function Withdraw ($resource) {
        var resourceUrl =  'api/withdraws/:id';

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
