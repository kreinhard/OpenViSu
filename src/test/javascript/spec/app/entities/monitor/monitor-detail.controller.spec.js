'use strict';

describe('Controller Tests', function() {

    describe('Monitor Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockMonitor;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockMonitor = jasmine.createSpy('MockMonitor');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Monitor': MockMonitor
            };
            createController = function() {
                $injector.get('$controller')("MonitorDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'openViSuApp:monitorUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
