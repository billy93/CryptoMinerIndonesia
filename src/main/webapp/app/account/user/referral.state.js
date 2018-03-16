(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('referral', {
            parent: 'account',
            url: '/referral',
            data: {
                authorities: [],
                pageTitle: 'Referral'
            },
            views: {
                'content@': {
                    templateUrl: 'app/account/user/referral.html',
                    controller: 'ReferralController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'User', function($stateParams, User) {
                    return User.referral({}).$promise;
                }]
            }
        });
    }
})();
