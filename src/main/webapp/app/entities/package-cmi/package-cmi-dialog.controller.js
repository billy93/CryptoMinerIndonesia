(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('PackageCmiDialogController', PackageCmiDialogController);

    PackageCmiDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'PackageCmi', 'Principal'];

    function PackageCmiDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, PackageCmi, Principal) {
        var vm = this;

        vm.packageCmi = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.getAccount = getAccount;
        
        if (vm.packageCmi.id == null) {
	        vm.packageCmi.startDate = new Date();
	        vm.packageCmi.startDate.setDate(vm.packageCmi.startDate.getDate() + 1);
	        vm.packageCmi.endDate = new Date();
	        vm.packageCmi.endDate.setDate(vm.packageCmi.endDate.getDate() + 1);
	        vm.packageCmi.endDate.setFullYear(vm.packageCmi.endDate.getFullYear() + 3);
        }
        
        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                console.log(vm.account);
            });
        }
        
        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
        	if(vm.packageCmi.amount > vm.account.usdAmount){
        		alert("Insufficient Amount");
        	}
        	else if(vm.packageCmi.amount < 100){
        		alert("Amount can't be less than 100");
        	}
        	else if(vm.packageCmi.amount % 100 != 0){
        		alert("Amount must be a multiple of 100");
        	}
        	
        	else{
        		console.log(vm.packageCmi);
	            vm.isSaving = true;
	            if (vm.packageCmi.id !== null) {
	                PackageCmi.update(vm.packageCmi, onSaveSuccess, onSaveError);
	            } else {
	                PackageCmi.save(vm.packageCmi, onSaveSuccess, onSaveError);
	            }
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
