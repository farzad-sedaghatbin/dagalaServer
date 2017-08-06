'use strict';

describe('Controller Tests', function() {

    describe('ProductTypeStatus Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockProductTypeStatus;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockProductTypeStatus = jasmine.createSpy('MockProductTypeStatus');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'ProductTypeStatus': MockProductTypeStatus
            };
            createController = function() {
                $injector.get('$controller')("ProductTypeStatusDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:productTypeStatusUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
