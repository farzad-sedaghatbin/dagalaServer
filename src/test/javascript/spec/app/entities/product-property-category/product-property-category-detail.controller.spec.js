'use strict';

describe('Controller Tests', function() {

    describe('ProductPropertyCategory Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockProductPropertyCategory;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockProductPropertyCategory = jasmine.createSpy('MockProductPropertyCategory');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'ProductPropertyCategory': MockProductPropertyCategory
            };
            createController = function() {
                $injector.get('$controller')("ProductPropertyCategoryDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:productPropertyCategoryUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
