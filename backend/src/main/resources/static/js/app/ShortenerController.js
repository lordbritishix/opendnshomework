'use strict'

var module = angular.module('urlshortener.controllers', []);

module.controller("ShortenerController", ["$scope", "ShortenerService",
    function ($scope, ShortenerService) {
        $scope.url = null;
        $scope.shortenError = null;
        $scope.shortenSuccess = null;

        $scope.shorten = function () {
            ShortenerService.shorten($scope.url).then(function(response) {
                switch(response.status) {
                    case 200:
                        $scope.shortenSuccess = response.data.shortenedUrl
                        break;
                }
            }).catch(function(response) {
                $scope.shortenError = response.data.message
            })
        }

        $scope.clearState = function() {
            $scope.shortenError = null;
            $scope.shortenSuccess = null;
        }
    }
]);
