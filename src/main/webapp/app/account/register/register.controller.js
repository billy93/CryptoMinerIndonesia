(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('RegisterController', RegisterController);


    RegisterController.$inject = [ '$timeout', '$scope', 'Auth', 'LoginService', 'errorConstants', 'DataUtils'];

    function RegisterController ($timeout, $scope, Auth, LoginService, errorConstants, DataUtils) {
        var vm = this;

        vm.doNotMatch = null;
        vm.error = null;
        vm.errorUserExists = null;
        vm.login = LoginService.open;
        vm.register = register;
        vm.registerAccount = {};
        vm.success = null;
        vm.pattern = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$@$!%*#?&])[A-Za-z\d$@$!%*#?&]/;
        
        $timeout(function (){angular.element('#login').focus();});

        function register () {
            if (vm.registerAccount.password !== vm.confirmPassword) {
                vm.doNotMatch = 'ERROR';
            } else {
                vm.registerAccount.langKey =  'en' ;
                vm.doNotMatch = null;
                vm.error = null;
                vm.errorUserExists = null;
                vm.errorEmailExists = null;

                Auth.createAccount(vm.registerAccount).then(function () {
                    vm.success = 'OK';
                }).catch(function (response) {
                    vm.success = null;
                    if (response.status === 400 && angular.fromJson(response.data).type === errorConstants.LOGIN_ALREADY_USED_TYPE) {
                        vm.errorUserExists = 'ERROR';
                    } else if (response.status === 400 && angular.fromJson(response.data).type === errorConstants.EMAIL_ALREADY_USED_TYPE) {
                        vm.errorEmailExists = 'ERROR';
                    } else {
                        vm.error = 'ERROR';
                    }
                });
            }
        }
        
        vm.setImage = function ($file, registerAccount, type) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                    	     if(type=='ktp'){
                    	          registerAccount.ktp = base64Data;
                              registerAccount.ktpContentType = $file.type;
                    	     }
                    	     else if(type=='photo'){
                    	    	      registerAccount.photo = base64Data;
                               registerAccount.photoContentType = $file.type;
                    	     }
                    });
                });
            }
        };
    }
})();
