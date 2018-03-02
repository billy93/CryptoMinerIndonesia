(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('TestingDeleteController',TestingDeleteController);

    TestingDeleteController.$inject = ['$uibModalInstance', 'entity', 'Testing'];

    function TestingDeleteController($uibModalInstance, entity, Testing) {
        var vm = this;

        vm.testing = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Testing.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
