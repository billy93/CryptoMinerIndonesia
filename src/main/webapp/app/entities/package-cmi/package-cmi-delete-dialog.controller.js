(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('PackageCmiDeleteController',PackageCmiDeleteController);

    PackageCmiDeleteController.$inject = ['$uibModalInstance', 'entity', 'PackageCmi'];

    function PackageCmiDeleteController($uibModalInstance, entity, PackageCmi) {
        var vm = this;

        vm.packageCmi = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            PackageCmi.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
