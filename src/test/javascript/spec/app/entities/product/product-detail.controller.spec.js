'use strict';

describe('Controller Tests', function() {

    describe('Product Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockProduct, MockShop, MockProductPropertyValue, MockProductContent, MockProductStatus, MockProductType;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockProduct = jasmine.createSpy('MockProduct');
            MockShop = jasmine.createSpy('MockShop');
            MockProductPropertyValue = jasmine.createSpy('MockProductPropertyValue');
            MockProductContent = jasmine.createSpy('MockProductContent');
            MockProductStatus = jasmine.createSpy('MockProductStatus');
            MockProductType = jasmine.createSpy('MockProductType');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Product': MockProduct,
                'Shop': MockShop,
                'ProductPropertyValue': MockProductPropertyValue,
                'ProductContent': MockProductContent,
                'ProductStatus': MockProductStatus,
                'ProductType': MockProductType
            };
            createController = function() {
                $injector.get('$controller')("ProductDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:productUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
