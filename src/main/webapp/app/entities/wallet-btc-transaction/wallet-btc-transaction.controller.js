(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('WalletBtcTransactionController', WalletBtcTransactionController);

    WalletBtcTransactionController.$inject = ['$state', 'WalletBtcTransaction', 'Withdraw', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function WalletBtcTransactionController($state, WalletBtcTransaction, Withdraw, ParseLinks, AlertService, paginationConstants, pagingParams) {

        var vm = this;

        vm.withdraw = {};
        vm.withdraw.fee = "0.0005";
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.walletBtcTransaction = {};
        vm.walletBtcTransaction.fee = "0.0005";
        loadAll();

        vm.transfer = function(){
        		WalletBtcTransaction.transfer(vm.walletBtcTransaction, function(result){
	    			alert('Transfer Success');
	    		}, function(error){
	    			
	    		});
	    };
	    
	    vm.withdrawData = function(){
	    		vm.withdraw.type = "BTC";
	    		Withdraw.save(vm.withdraw, function(result){
	    			alert('Request Withdraw sukses, kami akan memberikan notifikasi apabila proses telah selesai dilakukan');
	    		}, function(error){
	    			
	    		});        	
	    }
	    
        function loadAll () {
            WalletBtcTransaction.query({
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
                vm.walletBtcTransactions = data;
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
