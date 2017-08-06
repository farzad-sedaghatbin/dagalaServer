'use strict';

describe('Controller Tests', function() {

    describe('UserNewPlace Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockUserNewPlace, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockUserNewPlace = jasmine.createSpy('MockUserNewPlace');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'UserNewPlace': MockUserNewPlace,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("UserNewPlaceDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:userNewPlaceUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
