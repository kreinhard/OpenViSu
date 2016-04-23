(function () {
    'use strict';

    var application = angular.module('application');

    application.service('loginModal', function ($modal, $rootScope) {

        function assignCurrentUser(user) {
            $rootScope.currentUser = user;
            return user;
        }

        return function () {
            var instance = $modal.open({
                templateUrl: 'login.html',
                controller: 'LoginModalController',
                controllerAs: 'LoginModalController'
            })

            return instance.result.then(assignCurrentUser);
        };

    });
})();
