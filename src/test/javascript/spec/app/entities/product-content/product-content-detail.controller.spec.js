'use strict';

describe('Controller Tests', function() {

    describe('ProductContent Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockProductContent, MockProduct;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockProductContent = jasmine.createSpy('MockProductContent');
            MockProduct = jasmine.createSpy('MockProduct');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'ProductContent': MockProductContent,
                'Product': MockProduct
            };
            createController = function() {
                $injector.get('$controller')("ProductContentDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:productContentUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
