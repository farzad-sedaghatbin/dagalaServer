'use strict';

describe('Controller Tests', function() {

    describe('ShopPropertyCategory Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockShopPropertyCategory;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockShopPropertyCategory = jasmine.createSpy('MockShopPropertyCategory');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'ShopPropertyCategory': MockShopPropertyCategory
            };
            createController = function() {
                $injector.get('$controller')("ShopPropertyCategoryDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:shopPropertyCategoryUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
