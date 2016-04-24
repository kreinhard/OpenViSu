(function () {
    'use strict';

    var application = angular.module('application', ['ui.router']);

    application.config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('home', {
                url: '/home',
                templateUrl: 'home.html',
                controller: 'HomeController',
                data: {
                    requireLogin: false
                }
            })
            .state('login', {
                url: '/login',
                templateUrl: 'login.html',
                controller: 'NavigationController',
                data: {
                    requireLogin: false
                }
            })
            .state('app', {
                abstract: true,
                data: {
                    requireLogin: true // this property will apply to all children of 'app'
                }
            })

        // .state('app.playEvent', {
        // child state of `app`
        // requireLogin === true
        //});
    });

    application.config(function ($httpProvider) {

        $httpProvider.interceptors.push(function ($timeout, $q, $injector) {
            var loginModal, $http, $state;

            // this trick must be done so that we don't receive
            // `Uncaught Error: [$injector:cdep] Circular dependency found`
            $timeout(function () {
                loginModal = $injector.get('loginModal');
                $http = $injector.get('$http');
                $state = $injector.get('$state');
            });

            return {
                responseError: function (rejection) {
                    if (rejection.status !== 401) {
                        return $q.reject(rejection);
                    }

                    var deferred = $q.defer();

                    loginModal()
                        .then(function () {
                            deferred.resolve($http(rejection.config));
                        })
                        .catch(function () {
                            $state.go('welcome');
                            deferred.reject(rejection);
                        });

                    return deferred.promise;
                }
            };
        });

    });

    application.config(['$provide', '$stateProvider', '$urlRouterProvider', '$httpProvider',
        function ($provide, $stateProvider, $urlRouterProvider, $httpProvider) {
            /* Put this routeprovider for when'/' then go to dashboard page. */
            $urlRouterProvider
                .when('/', function ($state) {
                    $state.go('home');
                }).otherwise('/login');
        }
    ]);

    application.run(function ($rootScope, $state, loginModal) {


        $rootScope.$on('$stateChangeStart', function (event, toState, toParams) {
            var requireLogin = toState.data.requireLogin;

            if (requireLogin && typeof $rootScope.currentUser === 'undefined') {
                event.preventDefault();

                loginModal()
                    .then(function () {
                        return $state.go(toState.name, toParams);
                    })
                    .catch(function () {
                        return $state.go('home');
                    });
            }
        });

    });

    application.controller('HomeController', function ($http) {
        var self = this;
        $http.get('/resource/').then(function (response) {
            self.greeting = response.data;
        })
    });
})();
