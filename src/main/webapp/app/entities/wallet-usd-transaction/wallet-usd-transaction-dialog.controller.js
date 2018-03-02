(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('WalletUsdTransactionDialogController', WalletUsdTransactionDialogController);

    WalletUsdTransactionDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'WalletUsdTransaction'];

    function WalletUsdTransactionDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, WalletUsdTransaction) {
        var vm = this;

        vm.walletUsdTransaction = entity;
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
            if (vm.walletUsdTransaction.id !== null) {
                WalletUsdTransaction.update(vm.walletUsdTransaction, onSaveSuccess, onSaveError);
            } else {
                WalletUsdTransaction.save(vm.walletUsdTransaction, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cryptoMinerIndonesiaApp:walletUsdTransactionUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
