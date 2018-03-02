(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('TestingDetailController', TestingDetailController);

    TestingDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Testing'];

    function TestingDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Testing) {
        var vm = this;

        vm.testing = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('cryptoMinerIndonesiaApp:testingUpdate', function(event, result) {
            vm.testing = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
