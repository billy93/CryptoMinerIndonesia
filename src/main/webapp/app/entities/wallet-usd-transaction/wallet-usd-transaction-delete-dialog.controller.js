(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('WalletUsdTransactionDeleteController',WalletUsdTransactionDeleteController);

    WalletUsdTransactionDeleteController.$inject = ['$uibModalInstance', 'entity', 'WalletUsdTransaction'];

    function WalletUsdTransactionDeleteController($uibModalInstance, entity, WalletUsdTransaction) {
        var vm = this;

        vm.walletUsdTransaction = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            WalletUsdTransaction.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
