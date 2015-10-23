var jsoptimizer = angular.module('jsoptimizer', []);

jsoptimizer.controller('ServiceController', ['$scope', '$http', 
  function($scope, $http){
	$scope.requestJSFile = function(files) {
	
		
		if(files == "")
			files = '["https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.js","https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.js"]';
		
		$http.post('/rest/getjs', {"msg":JSON.parse(files)}).
			then(function(response) {
				console.log(JSON.stringify(response));
				alert(response)
			}, function(response){
				console.log(JSON.stringify(response));
			});
		
	}
	
	
	$scope.saveFile = function(file) {
		
		console.log(file);
		
		$http.post('/rest/loadscript', {"url":file, "combinedKey": null}).
		then(function(response) {
			console.log(JSON.stringify(response));
			alert(response)
		}, function(response){
			console.log(JSON.stringify(response));
		});
		
	}
	
  }]);