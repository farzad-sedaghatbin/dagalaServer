'use strict';

describe('Controller Tests', function() {

    describe('ProductPropertyValue Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockProductPropertyValue, MockProduct, MockProductProperty;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockProductPropertyValue = jasmine.createSpy('MockProductPropertyValue');
            MockProduct = jasmine.createSpy('MockProduct');
            MockProductProperty = jasmine.createSpy('MockProductProperty');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'ProductPropertyValue': MockProductPropertyValue,
                'Product': MockProduct,
                'ProductProperty': MockProductProperty
            };
            createController = function() {
                $injector.get('$controller')("ProductPropertyValueDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:productPropertyValueUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
