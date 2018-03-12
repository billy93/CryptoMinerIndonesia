(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('withdraw', {
            parent: 'entity',
            url: '/withdraw?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Withdraws'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/withdraw/withdraws.html',
                    controller: 'WithdrawController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
            }
        })
        .state('withdraw-detail', {
            parent: 'withdraw',
            url: '/withdraw/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Withdraw'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/withdraw/withdraw-detail.html',
                    controller: 'WithdrawDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Withdraw', function($stateParams, Withdraw) {
                    return Withdraw.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'withdraw',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('withdraw-detail.edit', {
            parent: 'withdraw-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/withdraw/withdraw-dialog.html',
                    controller: 'WithdrawDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Withdraw', function(Withdraw) {
                            return Withdraw.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('withdraw.new', {
            parent: 'withdraw',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/withdraw/withdraw-dialog.html',
                    controller: 'WithdrawDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                username: null,
                                amount: null,
                                status: null,
                                fee: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('withdraw', null, { reload: 'withdraw' });
                }, function() {
                    $state.go('withdraw');
                });
            }]
        })
        .state('withdraw.edit', {
            parent: 'withdraw',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/withdraw/withdraw-dialog.html',
                    controller: 'WithdrawDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Withdraw', function(Withdraw) {
                            return Withdraw.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('withdraw', null, { reload: 'withdraw' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('withdraw.delete', {
            parent: 'withdraw',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/withdraw/withdraw-delete-dialog.html',
                    controller: 'WithdrawDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Withdraw', function(Withdraw) {
                            return Withdraw.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('withdraw', null, { reload: 'withdraw' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
