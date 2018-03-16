(function() {
    'use strict';

    angular
        .module('cryptoMinerIndonesiaApp')
        .controller('ReferralController', ReferralController);

    ReferralController.$inject = ['$scope', 'DataUtils', 'entity', 'User'];

    function ReferralController($scope, DataUtils, entity, User) {

        var vm = this;
        vm.dataForTheTree = entity;
        $scope.treeOptions = {
        	    nodeChildren: "children",
        	    dirSelectable: true,
        	    injectClasses: {
        	        ul: "a1",
        	        li: "a2",
        	        liSelected: "a7",
        	        iExpanded: "a3",
        	        iCollapsed: "a4",
        	        iLeaf: "a5",
        	        label: "a6",
        	        labelSelected: "a8"
        	    }
        	}
        	$scope.dataForTheTree = vm.dataForTheTree;
//        	[
//        		{ "name" : "admin", "age" : "21", "children" : [
//        			{ "name" : "Smith", "age" : "42", "children" : [] },
//        			{ "name" : "Gary", "age" : "21", "children" : [
//        				{ "name" : "Jenifer", "age" : "23", "children" : [
//        					{ "name" : "Dani", "age" : "32", "children" : [] },
//        					{ "name" : "Max", "age" : "34", "children" : [] }
//        				]}
//        			]}
//        		]}
//        	];
        
        $scope.showSelected = function(sel) {
            $scope.selectedNode = sel;
            if($scope.selectedNode.children.length == 0){
            		User.getReferral({username:$scope.selectedNode.name}).$promise.then(function(result){
            			for(var x=0;x<result.length;x++){
	                		$scope.selectedNode.children.push({
	                			"name": result[x].name,
	                			"children":[]
	                		});            			
            			}
            		});
            		
            }
            console.log($scope.selectedNode);
        };
        
    }
})();
