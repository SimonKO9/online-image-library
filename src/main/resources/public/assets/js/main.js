var module = angular.module('imageLibrary', ['ngRoute']);

function loggedInUser(AuthService) {
    if (!AuthService.isLoggedIn()) {
        throw "User is not logged in"
    } else {
        return AuthService.getUser();
    }
}

module.config(function ($routeProvider) {
    $routeProvider
        .when('/home', {
            templateUrl: 'public/partials/home.html',
            controller: 'HomeController',
            resolve: {
                user: loggedInUser
            }
        })
        .when('/login', {
            templateUrl: 'public/partials/login.html',
            controller: 'LoginController'
        })
        .otherwise({
            redirectTo: '/home'
        })
});

module.factory('AuthService', function ($http) {
    var loggedIn = false;

    return {
        isLoggedIn: function () {
            return loggedIn;
        },
        getUser: function () {
            return {}; // TODO
        },
        login: function(username, password) {
            $http.defaults.headers.common['Authorization'] = 'Basic ' + btoa(username + ":" + password);
            $http({
                method: 'GET',
                url: '/api/user/' + username
            }).success(function(d) {
                console.log('success', d);
            }).error(function(d) {
                console.log('error', d);
            })
        }
    };
});

module.controller('HomeController', function ($scope) {
});

module.controller('LoginController', function ($scope, AuthService) {
    $scope.credentials = {
        username: '',
        password: ''
    };

    $scope.signIn = function() {
        AuthService.login($scope.credentials.username, $scope.credentials.password);
    }
});

module.run(function ($rootScope, $location) {
    $rootScope.$on('$routeChangeError', function () {
        $location.path('/login');
    });
});