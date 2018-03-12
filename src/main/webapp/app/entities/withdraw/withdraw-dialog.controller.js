(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('WithdrawDialogController', WithdrawDialogController);

    WithdrawDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Withdraw', 'WalletUsdTransaction', 'WalletBtcTransaction'];

    function WithdrawDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Withdraw, WalletUsdTransaction, WalletBtcTransaction) {
        var vm = this;

        vm.withdraw = entity;
        vm.clear = clear;
        vm.save = save;
        vm.walletusdtransactions = WalletUsdTransaction.query({filter: 'withdraw-is-null'});
        vm.walletbtctransactions = WalletBtcTransaction.query({filter: 'withdraw-is-null'});
        
        $q.all([vm.withdraw.$promise, vm.walletusdtransactions.$promise]).then(function() {
            if (!vm.withdraw.walletUsdTransaction || !vm.withdraw.walletUsdTransaction.id) {
                return $q.reject();
            }
            return WalletUsdTransaction.get({id : vm.withdraw.walletUsdTransaction.id}).$promise;
        }).then(function(walletUsdTransaction) {
            vm.walletusdtransactions.push(walletUsdTransaction);
        });

        $q.all([vm.withdraw.$promise, vm.walletbtctransactions.$promise]).then(function() {
            if (!vm.withdraw.walletBtcTransaction || !vm.withdraw.walletBtcTransaction.id) {
                return $q.reject();
            }
            return WalletBtcTransaction.get({id : vm.withdraw.walletBtcTransaction.id}).$promise;
        }).then(function(walletBtcTransaction) {
            vm.walletbtctransactions.push(walletBtcTransaction);
        });
        
        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.withdraw.id !== null) {
                Withdraw.update(vm.withdraw, onSaveSuccess, onSaveError);
            } else {
                Withdraw.save(vm.withdraw, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cryptoMinerIndonesiaApp:withdrawUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
