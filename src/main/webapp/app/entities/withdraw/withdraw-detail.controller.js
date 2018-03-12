(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('WithdrawDetailController', WithdrawDetailController);

    WithdrawDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Withdraw'];

    function WithdrawDetailController($scope, $rootScope, $stateParams, previousState, entity, Withdraw) {
        var vm = this;

        vm.withdraw = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cryptoMinerIndonesiaApp:withdrawUpdate', function(event, result) {
            vm.withdraw = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
