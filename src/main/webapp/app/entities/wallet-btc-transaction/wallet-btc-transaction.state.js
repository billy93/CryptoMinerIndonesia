(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('wallet-btc-transaction', {
            parent: 'entity',
            url: '/wallet-btc-transaction?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'WalletBtcTransactions'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/wallet-btc-transaction/wallet-btc-transactions.html',
                    controller: 'WalletBtcTransactionController',
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
        .state('wallet-btc-transaction-detail', {
            parent: 'wallet-btc-transaction',
            url: '/wallet-btc-transaction/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'WalletBtcTransaction'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/wallet-btc-transaction/wallet-btc-transaction-detail.html',
                    controller: 'WalletBtcTransactionDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'WalletBtcTransaction', function($stateParams, WalletBtcTransaction) {
                    return WalletBtcTransaction.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'wallet-btc-transaction',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('wallet-btc-transaction-detail.edit', {
            parent: 'wallet-btc-transaction-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/wallet-btc-transaction/wallet-btc-transaction-dialog.html',
                    controller: 'WalletBtcTransactionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['WalletBtcTransaction', function(WalletBtcTransaction) {
                            return WalletBtcTransaction.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('wallet-btc-transaction.new', {
            parent: 'wallet-btc-transaction',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/wallet-btc-transaction/wallet-btc-transaction-dialog.html',
                    controller: 'WalletBtcTransactionDialogController',
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
                    $state.go('wallet-btc-transaction', null, { reload: 'wallet-btc-transaction' });
                }, function() {
                    $state.go('wallet-btc-transaction');
                });
            }]
        })
        .state('wallet-btc-transaction.edit', {
            parent: 'wallet-btc-transaction',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/wallet-btc-transaction/wallet-btc-transaction-dialog.html',
                    controller: 'WalletBtcTransactionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['WalletBtcTransaction', function(WalletBtcTransaction) {
                            return WalletBtcTransaction.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('wallet-btc-transaction', null, { reload: 'wallet-btc-transaction' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('wallet-btc-transaction.delete', {
            parent: 'wallet-btc-transaction',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/wallet-btc-transaction/wallet-btc-transaction-delete-dialog.html',
                    controller: 'WalletBtcTransactionDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['WalletBtcTransaction', function(WalletBtcTransaction) {
                            return WalletBtcTransaction.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('wallet-btc-transaction', null, { reload: 'wallet-btc-transaction' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
