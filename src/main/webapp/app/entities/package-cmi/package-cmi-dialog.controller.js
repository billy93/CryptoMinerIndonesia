(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('PackageCmiDialogController', PackageCmiDialogController);

    PackageCmiDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'PackageCmi'];

    function PackageCmiDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, PackageCmi) {
        var vm = this;

        vm.packageCmi = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.packageCmi.id !== null) {
                PackageCmi.update(vm.packageCmi, onSaveSuccess, onSaveError);
            } else {
                PackageCmi.save(vm.packageCmi, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cryptoMinerIndonesiaApp:packageCmiUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.startDate = false;
        vm.datePickerOpenStatus.endDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
