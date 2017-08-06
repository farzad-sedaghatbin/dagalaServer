'use strict';

describe('Controller Tests', function() {

    describe('ProductTrust Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockProductTrust, MockProduct, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockProductTrust = jasmine.createSpy('MockProductTrust');
            MockProduct = jasmine.createSpy('MockProduct');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'ProductTrust': MockProductTrust,
                'Product': MockProduct,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("ProductTrustDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'productManagementApp:productTrustUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
