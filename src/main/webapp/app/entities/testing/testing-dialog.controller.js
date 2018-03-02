(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('TestingDialogController', TestingDialogController);

    TestingDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Testing'];

    function TestingDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Testing) {
        var vm = this;

        vm.testing = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.testing.id !== null) {
                Testing.update(vm.testing, onSaveSuccess, onSaveError);
            } else {
                Testing.save(vm.testing, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cryptoMinerIndonesiaApp:testingUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


        vm.setImage = function ($file, testing) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        testing.image = base64Data;
                        testing.imageContentType = $file.type;
                    });
                });
            }
        };

        vm.setFile = function ($file, testing) {
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        testing.file = base64Data;
                        testing.fileContentType = $file.type;
                    });
                });
            }
        };

    }
})();
