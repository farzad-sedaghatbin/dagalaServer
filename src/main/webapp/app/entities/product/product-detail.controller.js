(function() {
    'use strict';

    angular
        .module('baseApp')
        .controller('ProductDetailController', ProductDetailController);

    ProductDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Product', 'Status'];

    function ProductDetailController($scope, $rootScope, $stateParams, previousState, entity, Product, Status) {
        var vm = this;

        vm.product = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('baseApp:productUpdate', function(event, result) {
            vm.product = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
