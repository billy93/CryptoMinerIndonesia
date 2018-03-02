(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('testing', {
            parent: 'entity',
            url: '/testing',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Testings'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/testing/testings.html',
                    controller: 'TestingController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('testing-detail', {
            parent: 'testing',
            url: '/testing/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Testing'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/testing/testing-detail.html',
                    controller: 'TestingDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Testing', function($stateParams, Testing) {
                    return Testing.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'testing',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('testing-detail.edit', {
            parent: 'testing-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/testing/testing-dialog.html',
                    controller: 'TestingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Testing', function(Testing) {
                            return Testing.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('testing.new', {
            parent: 'testing',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/testing/testing-dialog.html',
                    controller: 'TestingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                image: null,
                                imageContentType: null,
                                agreement: null,
                                file: null,
                                fileContentType: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('testing', null, { reload: 'testing' });
                }, function() {
                    $state.go('testing');
                });
            }]
        })
        .state('testing.edit', {
            parent: 'testing',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/testing/testing-dialog.html',
                    controller: 'TestingDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Testing', function(Testing) {
                            return Testing.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('testing', null, { reload: 'testing' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('testing.delete', {
            parent: 'testing',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/testing/testing-delete-dialog.html',
                    controller: 'TestingDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Testing', function(Testing) {
                            return Testing.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('testing', null, { reload: 'testing' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
