(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('WalletBtcTransactionDeleteController',WalletBtcTransactionDeleteController);

    WalletBtcTransactionDeleteController.$inject = ['$uibModalInstance', 'entity', 'WalletBtcTransaction'];

    function WalletBtcTransactionDeleteController($uibModalInstance, entity, WalletBtcTransaction) {
        var vm = this;

        vm.walletBtcTransaction = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            WalletBtcTransaction.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
