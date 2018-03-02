(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('PackageCmiDetailController', PackageCmiDetailController);

    PackageCmiDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'PackageCmi'];

    function PackageCmiDetailController($scope, $rootScope, $stateParams, previousState, entity, PackageCmi) {
        var vm = this;

        vm.packageCmi = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cryptoMinerIndonesiaApp:packageCmiUpdate', function(event, result) {
            vm.packageCmi = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
