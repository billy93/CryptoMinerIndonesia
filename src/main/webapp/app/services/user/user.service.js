(function () {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .factory('User', User);

    User.$inject = ['$resource'];

    function User ($resource) {
        var service = $resource('api/users/:login', {}, {
            'query': {method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'referral': {
                method: 'GET',
                url: 'api/users/referral',
                isArray: true
            },     
            'getReferral': {
            	method: 'GET',
                url: 'api/users/referral/:username',
                isArray: true
            },
            'gauth': {
            	url: 'api/users/gauth',
            	method: 'POST'
            },
            'getOtp': {
            	url: 'api/users/getOtp',
            	method: 'GET'
            },
            'save': { method:'POST' },
            'update': { method:'PUT' },
            'delete':{ method:'DELETE'}
        });

        return service;
    }
})();
