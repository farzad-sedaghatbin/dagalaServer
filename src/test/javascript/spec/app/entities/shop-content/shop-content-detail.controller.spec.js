'use strict';

describe('Controller Tests', function() {

    describe('ShopContent Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockShopContent, MockShop;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockShopContent = jasmine.createSpy('MockShopContent');
            MockShop = jasmine.createSpy('MockShop');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'ShopContent': MockShopContent,
                'Shop': MockShop
            };
            createController = function() {
                $injector.get('$controller')("ShopContentDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:shopContentUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
