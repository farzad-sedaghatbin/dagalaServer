'use strict';

describe('Controller Tests', function() {

    describe('ProductTypeCategory Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockProductTypeCategory, MockProductTypeStatus;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockProductTypeCategory = jasmine.createSpy('MockProductTypeCategory');
            MockProductTypeStatus = jasmine.createSpy('MockProductTypeStatus');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'ProductTypeCategory': MockProductTypeCategory,
                'ProductTypeStatus': MockProductTypeStatus
            };
            createController = function() {
                $injector.get('$controller')("ProductTypeCategoryDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:productTypeCategoryUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
