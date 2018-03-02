(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('WalletBtcTransactionDialogController', WalletBtcTransactionDialogController);

    WalletBtcTransactionDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'WalletBtcTransaction'];

    function WalletBtcTransactionDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, WalletBtcTransaction) {
        var vm = this;

        vm.walletBtcTransaction = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.walletBtcTransaction.id !== null) {
                WalletBtcTransaction.update(vm.walletBtcTransaction, onSaveSuccess, onSaveError);
            } else {
                WalletBtcTransaction.save(vm.walletBtcTransaction, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cryptoMinerIndonesiaApp:walletBtcTransactionUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
