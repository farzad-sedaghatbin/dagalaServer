'use strict';

describe('Controller Tests', function() {

    describe('ShopProductCategory Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockShopProductCategory, MockShop, MockProductTypeCategory;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockShopProductCategory = jasmine.createSpy('MockShopProductCategory');
            MockShop = jasmine.createSpy('MockShop');
            MockProductTypeCategory = jasmine.createSpy('MockProductTypeCategory');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'ShopProductCategory': MockShopProductCategory,
                'Shop': MockShop,
                'ProductTypeCategory': MockProductTypeCategory
            };
            createController = function() {
                $injector.get('$controller')("ShopProductCategoryDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:shopProductCategoryUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
