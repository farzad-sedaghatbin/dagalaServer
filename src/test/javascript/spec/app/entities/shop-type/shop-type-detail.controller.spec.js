'use strict';

describe('Controller Tests', function() {

    describe('ShopType Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockShopType, MockShopProperty, MockShopTypeCategory;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockShopType = jasmine.createSpy('MockShopType');
            MockShopProperty = jasmine.createSpy('MockShopProperty');
            MockShopTypeCategory = jasmine.createSpy('MockShopTypeCategory');


            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'ShopType': MockShopType,
                'ShopProperty': MockShopProperty,
                'ShopTypeCategory': MockShopTypeCategory
            };
            createController = function() {
                $injector.get('$controller')("ShopTypeDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:shopTypeUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
