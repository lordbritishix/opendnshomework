'use strict'

angular.module('urlshortener.services', []).factory('ShortenerService', ["$http", function($http) {
    var service = {};

    service.shorten = function (url) {
        return $http.post("/shorten/" + btoa(url));
    };

    return service;
}]);
