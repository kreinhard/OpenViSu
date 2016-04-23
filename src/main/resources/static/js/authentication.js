(function () {
    'use strict';

    var application = angular.module('application');

    application.register = function(username) {
        $rootScope.authenticated = true;
        $rootScope.username = username;
    }

    application.unregister = function() {
        $rootScope.authenticated = false;
        $rootScope.username = undefined;
    }

    application.isAuthenticated = function() {
        return $rootScope.authenticated == true;
    }

    application.getUsername = function() {
        return $rootScope.username;
    }

    // Checks via rest server call whether an user is returned or not and calls the given callback afterwards.
    application.authenticate = function (callback) {

        $http.get('user').then(function (response) {
            if (response.data.name) {
                register(response.data.name);
            } else {
                unregister();
            }
            callback && callback();
        }, function () {
            unregister();
            callback && callback();
        });

    }

    application.login = function () {
        $http.post('login', $.param(self.credentials), {
            headers: {
                "content-type": "application/x-www-form-urlencoded"
            }
        }).then(function () {
            authenticate(function () {
                if (isAuthenticated()) {
                    console.log("Login succeeded")
                    $location.path("/");
                    self.error = false;
                } else {
                    console.log("Login failed with redirect")
                    $location.path("/login");
                    self.error = true;
                }
            });
        }, function () {
            console.log("Login failed")
            $location.path("/login");
            self.error = true;
            unregister();
        })
    };

    application.logout = function () {
        $http.post('logout', {}).finally(function () {
            unregister();
            $location.path("/");
        });
    }

})();
