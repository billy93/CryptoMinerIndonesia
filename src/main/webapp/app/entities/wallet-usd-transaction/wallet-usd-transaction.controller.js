(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('WalletUsdTransactionController', WalletUsdTransactionController);

    WalletUsdTransactionController.$inject = ['$state', 'WalletUsdTransaction', 'Withdraw', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function WalletUsdTransactionController($state, WalletUsdTransaction, Withdraw, ParseLinks, AlertService, paginationConstants, pagingParams) {

        var vm = this;

        vm.walletUsdTransaction = {};
        vm.withdraw = {};
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;

        loadAll();

        vm.transfer = function(){
        		WalletUsdTransaction.transfer(vm.walletUsdTransaction, function(result){
        			alert('Transfer Success');
        		}, function(error){
        			
        		});
        };
        
        vm.withdrawData = function(){
        		vm.withdraw.type = "USD";
	    		Withdraw.save(vm.withdraw, function(result){
	    			alert('Request Withdraw sukses, kami akan memberikan notifikasi apabila proses telah selesai dilakukan');
	    		}, function(error){
	    			
	    		});        	
        }
        vm.changeAmount = function(){
        		//console.log(vm.walletUsdTransaction.amount);
         	vm.walletUsdTransaction.fee = parseInt(vm.walletUsdTransaction.amount) * 5 / 100;
        }
        vm.changeWithdrawAmount = function(){
	    		//console.log(vm.walletUsdTransaction.amount);
	     	vm.withdraw.fee = parseInt(vm.withdraw.amount) * 5 / 100;
	    }
        
        function loadAll () {
            WalletUsdTransaction.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.walletUsdTransactions = data;
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }
    }
})();
