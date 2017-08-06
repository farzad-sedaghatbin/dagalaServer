'use strict';

describe('Controller Tests', function() {

    describe('ProductStatusHistory Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockProductStatusHistory, MockProduct, MockProductStatus;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockProductStatusHistory = jasmine.createSpy('MockProductStatusHistory');
            MockProduct = jasmine.createSpy('MockProduct');
            MockProductStatus = jasmine.createSpy('MockProductStatus');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'ProductStatusHistory': MockProductStatusHistory,
                'Product': MockProduct,
                'ProductStatus': MockProductStatus
            };
            createController = function() {
                $injector.get('$controller')("ProductStatusHistoryDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:productStatusHistoryUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
