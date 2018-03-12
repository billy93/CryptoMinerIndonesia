(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('WithdrawDeleteController',WithdrawDeleteController);

    WithdrawDeleteController.$inject = ['$uibModalInstance', 'entity', 'Withdraw'];

    function WithdrawDeleteController($uibModalInstance, entity, Withdraw) {
        var vm = this;

        vm.withdraw = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Withdraw.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
