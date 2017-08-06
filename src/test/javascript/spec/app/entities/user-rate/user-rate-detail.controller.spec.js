'use strict';

describe('Controller Tests', function() {

    describe('UserRate Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockUserRate, MockUser, MockProduct, MockShop;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockUserRate = jasmine.createSpy('MockUserRate');
            MockUser = jasmine.createSpy('MockUser');
            MockProduct = jasmine.createSpy('MockProduct');
            MockShop = jasmine.createSpy('MockShop');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'UserRate': MockUserRate,
                'User': MockUser,
                'Product': MockProduct,
                'Shop': MockShop
            };
            createController = function() {
                $injector.get('$controller')("UserRateDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:userRateUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
