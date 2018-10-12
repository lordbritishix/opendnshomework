'use strict'

var module = angular.module('urlshortener.controllers', []);

module.controller("ShortenerController", ["$scope", "ShortenerService",
    function ($scope, ShortenerService) {
        $scope.url = null;

        // ShortenerService.shorten($scope.url).then(function() {
        //     console.log("works");
        // })

        $scope.shorten = function () {
            ShortenerService.shorten($scope.url).then(
                console.log("abc")
            )
        }
    }
]);
