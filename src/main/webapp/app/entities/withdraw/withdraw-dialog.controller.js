(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('WithdrawDialogController', WithdrawDialogController);

    WithdrawDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Withdraw'];

    function WithdrawDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Withdraw) {
        var vm = this;

        vm.withdraw = entity;
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
