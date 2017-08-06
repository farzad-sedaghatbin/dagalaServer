'use strict';

describe('Controller Tests', function() {

    describe('ProductType Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockProductType, MockProductTypeStatus, MockProductProperty;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockProductType = jasmine.createSpy('MockProductType');
            MockProductTypeStatus = jasmine.createSpy('MockProductTypeStatus');
            MockProductProperty = jasmine.createSpy('MockProductProperty');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'ProductType': MockProductType,
                'ProductTypeStatus': MockProductTypeStatus,
                'ProductProperty': MockProductProperty
            };
            createController = function() {
                $injector.get('$controller')("ProductTypeDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:productTypeUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
