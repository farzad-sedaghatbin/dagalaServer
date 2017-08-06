'use strict';

describe('Controller Tests', function() {

    describe('Shop Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockShop, MockState, MockShopPropertyValue, MockUser, MockShopType;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockShop = jasmine.createSpy('MockShop');
            MockState = jasmine.createSpy('MockState');
            MockShopPropertyValue = jasmine.createSpy('MockShopPropertyValue');
            MockUser = jasmine.createSpy('MockUser');
            MockShopType = jasmine.createSpy('MockShopType');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Shop': MockShop,
                'State': MockState,
                'ShopPropertyValue': MockShopPropertyValue,
                'User': MockUser,
                'ShopType': MockShopType
            };
            createController = function() {
                $injector.get('$controller')("ShopDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:shopUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
