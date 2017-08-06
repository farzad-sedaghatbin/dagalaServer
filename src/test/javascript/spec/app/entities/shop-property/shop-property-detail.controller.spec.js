'use strict';

describe('Controller Tests', function() {

    describe('ShopProperty Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockShopProperty, MockShopPropertyCategory;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockShopProperty = jasmine.createSpy('MockShopProperty');
            MockShopPropertyCategory = jasmine.createSpy('MockShopPropertyCategory');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'ShopProperty': MockShopProperty,
                'ShopPropertyCategory': MockShopPropertyCategory
            };
            createController = function() {
                $injector.get('$controller')("ShopPropertyDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:shopPropertyUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
