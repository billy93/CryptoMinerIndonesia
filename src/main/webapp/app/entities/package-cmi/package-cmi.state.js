(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('package-cmi', {
            parent: 'entity',
            url: '/package-cmi?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'PackageCmis'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/package-cmi/package-cmis.html',
                    controller: 'PackageCmiController',
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
        .state('package-cmi-detail', {
            parent: 'package-cmi',
            url: '/package-cmi/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'PackageCmi'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/package-cmi/package-cmi-detail.html',
                    controller: 'PackageCmiDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'PackageCmi', function($stateParams, PackageCmi) {
                    return PackageCmi.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'package-cmi',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('package-cmi-detail.edit', {
            parent: 'package-cmi-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/package-cmi/package-cmi-dialog.html',
                    controller: 'PackageCmiDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['PackageCmi', function(PackageCmi) {
                            return PackageCmi.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('package-cmi.new', {
            parent: 'package-cmi',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/package-cmi/package-cmi-dialog.html',
                    controller: 'PackageCmiDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                username: null,
                                amount: null,
                                startDate: null,
                                endDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('package-cmi', null, { reload: 'package-cmi' });
                }, function() {
                    $state.go('package-cmi');
                });
            }]
        })
        .state('package-cmi.edit', {
            parent: 'package-cmi',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/package-cmi/package-cmi-dialog.html',
                    controller: 'PackageCmiDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['PackageCmi', function(PackageCmi) {
                            return PackageCmi.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('package-cmi', null, { reload: 'package-cmi' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('package-cmi.delete', {
            parent: 'package-cmi',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/package-cmi/package-cmi-delete-dialog.html',
                    controller: 'PackageCmiDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['PackageCmi', function(PackageCmi) {
                            return PackageCmi.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('package-cmi', null, { reload: 'package-cmi' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
