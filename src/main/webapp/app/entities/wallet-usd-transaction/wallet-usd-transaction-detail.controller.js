(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('WalletUsdTransactionDetailController', WalletUsdTransactionDetailController);

    WalletUsdTransactionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'WalletUsdTransaction'];

    function WalletUsdTransactionDetailController($scope, $rootScope, $stateParams, previousState, entity, WalletUsdTransaction) {
        var vm = this;

        vm.walletUsdTransaction = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cryptoMinerIndonesiaApp:walletUsdTransactionUpdate', function(event, result) {
            vm.walletUsdTransaction = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
