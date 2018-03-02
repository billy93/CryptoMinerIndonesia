(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('TestingController', TestingController);

    TestingController.$inject = ['DataUtils', 'Testing'];

    function TestingController(DataUtils, Testing) {

        var vm = this;

        vm.testings = [];
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;

        loadAll();

        function loadAll() {
            Testing.query(function(result) {
                vm.testings = result;
                vm.searchQuery = null;
            });
        }
    }
})();
