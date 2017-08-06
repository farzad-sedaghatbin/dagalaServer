'use strict';

describe('Controller Tests', function() {

    describe('ShopTypeCategory Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockShopTypeCategory;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockShopTypeCategory = jasmine.createSpy('MockShopTypeCategory');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'ShopTypeCategory': MockShopTypeCategory
            };
            createController = function() {
                $injector.get('$controller')("ShopTypeCategoryDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:shopTypeCategoryUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
