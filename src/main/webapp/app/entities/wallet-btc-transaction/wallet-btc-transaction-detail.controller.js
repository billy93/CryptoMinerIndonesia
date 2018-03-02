(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('WalletBtcTransactionDetailController', WalletBtcTransactionDetailController);

    WalletBtcTransactionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'WalletBtcTransaction'];

    function WalletBtcTransactionDetailController($scope, $rootScope, $stateParams, previousState, entity, WalletBtcTransaction) {
        var vm = this;

        vm.walletBtcTransaction = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cryptoMinerIndonesiaApp:walletBtcTransactionUpdate', function(event, result) {
            vm.walletBtcTransaction = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
