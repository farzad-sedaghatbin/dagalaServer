(function() {
    'use strict';

    angular
        .module('baseApp')
        .controller('StatusDetailController', StatusDetailController);

    StatusDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Status'];

    function StatusDetailController($scope, $rootScope, $stateParams, previousState, entity, Status) {
        var vm = this;

        vm.status = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('baseApp:statusUpdate', function(event, result) {
            vm.status = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
