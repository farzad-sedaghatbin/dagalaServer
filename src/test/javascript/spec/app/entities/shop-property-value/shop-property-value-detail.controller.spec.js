'use strict';

describe('Controller Tests', function() {

    describe('ShopPropertyValue Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockShopPropertyValue, MockShop, MockShopProperty;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockShopPropertyValue = jasmine.createSpy('MockShopPropertyValue');
            MockShop = jasmine.createSpy('MockShop');
            MockShopProperty = jasmine.createSpy('MockShopProperty');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'ShopPropertyValue': MockShopPropertyValue,
                'Shop': MockShop,
                'ShopProperty': MockShopProperty
            };
            createController = function() {
                $injector.get('$controller')("ShopPropertyValueDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:shopPropertyValueUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
