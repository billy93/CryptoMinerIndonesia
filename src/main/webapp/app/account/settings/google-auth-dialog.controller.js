(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('GoogleAuthDialogController', GoogleAuthDialogController);

    GoogleAuthDialogController.$inject = ['Principal', 'Auth', 'User', '$uibModalInstance'];

    function GoogleAuthDialogController (Principal, Auth, User, $uibModalInstance) {
        var vm = this;

        vm.error = null;
        vm.save = save;
        vm.settingsAccount = null;
        vm.success = null;
        vm.gauth = {};
    	
        vm.clear = clear;
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }
        
        vm.disableGauth = function(){
        	vm.gauth.type = 'disable';
        	User.gauth(vm.gauth, function(result){
        	    alert('Success disabled');
                
        	})
        }
        
        vm.enableGauth = function(){
        	vm.gauth.type = 'enable';
        	User.gauth(vm.gauth, function(result){
        	    alert('Success enabled');
                
        	})
        }
        /**
         * Store the "settings account" in a separate variable, and not in the shared "account" variable.
         */
        var copyAccount = function (account) {
            return {
                activated: account.activated,
                email: account.email,
                firstName: account.firstName,
                langKey: account.langKey,
                lastName: account.lastName,
                login: account.login,
                enabled: account.enabled,
                secret: account.secret
            };
        };

        Principal.identity().then(function(account) {
            vm.settingsAccount = copyAccount(account);
        });

        User.getOtp({}, function(result){
        	vm.otp = result.code;
        });
        function save () {
            Auth.updateAccount(vm.settingsAccount).then(function() {
                vm.error = null;
                vm.success = 'OK';
                Principal.identity(true).then(function(account) {
                    vm.settingsAccount = copyAccount(account);
                });
            }).catch(function() {
                vm.success = null;
                vm.error = 'ERROR';
            });
        }
    }
})();
