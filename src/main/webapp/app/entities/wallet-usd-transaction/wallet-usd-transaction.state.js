(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('wallet-usd-transaction', {
            parent: 'entity',
            url: '/wallet-usd-transaction?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'WalletUsdTransactions'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/wallet-usd-transaction/wallet-usd-transactions.html',
                    controller: 'WalletUsdTransactionController',
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
        .state('wallet-usd-transaction-detail', {
            parent: 'wallet-usd-transaction',
            url: '/wallet-usd-transaction/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'WalletUsdTransaction'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/wallet-usd-transaction/wallet-usd-transaction-detail.html',
                    controller: 'WalletUsdTransactionDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'WalletUsdTransaction', function($stateParams, WalletUsdTransaction) {
                    return WalletUsdTransaction.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'wallet-usd-transaction',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('wallet-usd-transaction-detail.edit', {
            parent: 'wallet-usd-transaction-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/wallet-usd-transaction/wallet-usd-transaction-dialog.html',
                    controller: 'WalletUsdTransactionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['WalletUsdTransaction', function(WalletUsdTransaction) {
                            return WalletUsdTransaction.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('wallet-usd-transaction.new', {
            parent: 'wallet-usd-transaction',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/wallet-usd-transaction/wallet-usd-transaction-dialog.html',
                    controller: 'WalletUsdTransactionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                username: null,
                                amount: null,
                                type: null,
                                fromUsername: null,
                                txid: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('wallet-usd-transaction', null, { reload: 'wallet-usd-transaction' });
                }, function() {
                    $state.go('wallet-usd-transaction');
                });
            }]
        })
        .state('wallet-usd-transaction.edit', {
            parent: 'wallet-usd-transaction',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/wallet-usd-transaction/wallet-usd-transaction-dialog.html',
                    controller: 'WalletUsdTransactionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['WalletUsdTransaction', function(WalletUsdTransaction) {
                            return WalletUsdTransaction.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('wallet-usd-transaction', null, { reload: 'wallet-usd-transaction' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('wallet-usd-transaction.delete', {
            parent: 'wallet-usd-transaction',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/wallet-usd-transaction/wallet-usd-transaction-delete-dialog.html',
                    controller: 'WalletUsdTransactionDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['WalletUsdTransaction', function(WalletUsdTransaction) {
                            return WalletUsdTransaction.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('wallet-usd-transaction', null, { reload: 'wallet-usd-transaction' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
