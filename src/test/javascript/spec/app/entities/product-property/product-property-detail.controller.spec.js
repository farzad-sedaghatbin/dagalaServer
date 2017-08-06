'use strict';

describe('Controller Tests', function() {

    describe('ProductProperty Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockProductProperty, MockProductPropertyCategory, MockProductType;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockProductProperty = jasmine.createSpy('MockProductProperty');
            MockProductPropertyCategory = jasmine.createSpy('MockProductPropertyCategory');
            MockProductType = jasmine.createSpy('MockProductType');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'ProductProperty': MockProductProperty,
                'ProductPropertyCategory': MockProductPropertyCategory,
                'ProductType': MockProductType
            };
            createController = function() {
                $injector.get('$controller')("ProductPropertyDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:productPropertyUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
