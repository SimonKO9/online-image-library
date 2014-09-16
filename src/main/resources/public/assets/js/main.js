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
            templateUrl: '/public/partials/home.html',
            controller: 'HomeController',
            resolve: {
                user: loggedInUser
            }
        })
        .when('/login', {
            templateUrl: '/public/partials/login.html',
            controller: 'LoginController'
        })
        .when('/resetPassword', {
            templateUrl: '/public/partials/reset-password.html',
            controller: 'ResetPasswordController'
        })
        .when('/register', {
            templateUrl: '/public/partials/register.html',
            controller: 'RegisterController'
        })
        .otherwise({
            redirectTo: '/home'
        })
});

module.service('AuthService', function ($http) {
    var _loggedIn = false;
    var _user = {};

    return {
        isLoggedIn: function () {
            return _loggedIn;
        },
        getUser: function () {
            return _user;
        },
        login: function (username, password) {
            $http.defaults.headers.common['Authorization'] = 'Basic ' + btoa(username + ":" + password);
            return $http({
                method: 'GET',
                url: '/api/user/' + username
            }).success(function (userResponse, status) {
                if (status == 200) {
                    _user = userResponse;
                    _loggedIn = true;
                }
            }).error(function () {
                delete $http.defaults.headers.common['Authorization'];
            });
        }
    };
});

module.service('RegisterService', function($http) {
    return {
        register: function(data) {
            return $http({
                method: 'POST',
                url: '/api/user',
                data: data
            });
        }
    }
});

module.controller('HomeController', function ($scope, user) {
    $scope.user = user;
    console.log(user);
});

module.controller('LoginController', function ($scope, AuthService, $location) {
    $scope.credentials = {
        username: '',
        password: ''
    };

    $scope.signIn = function () {
        var promise = AuthService.login($scope.credentials.username, $scope.credentials.password);
        promise.success(function () {
            if (AuthService.isLoggedIn()) {
                $location.path('/home');
            }
        });
    };

    $scope.reset = function () {
        $location.path('/resetPassword');
    };

    $scope.register = function () {
        $location.path('/register');
    }

});

module.controller('RegisterController', function ($scope, RegisterService, $location) {
    $scope.formData = {
        username: '',
        password: '',
        repeatPassword: '',
        email: ''
    };

    $scope.register = function() {
        RegisterService.register($scope.formData)
            .success(function(data) {
                console.log('registered', data);
            }).error(function(data, statusCode) {
                if(statusCode == 400) {
                    console.log('failed 400');
                }
            });
    };

    $scope.isUsernameFree = function() {
        return true;
    };

    $scope.isEmailFree = function() {
        return true;
    };

    $scope.passwordsMatch = function() {
        return $scope.formData.password == $scope.formData.repeatPassword;
    }
});

module.run(function ($rootScope, $location) {
    $rootScope.$on('$routeChangeError', function () {
        $location.path('/login');
    });
});