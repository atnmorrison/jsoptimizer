<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html ng-app="jsoptimizer">
	<head>
		<script type="text/javascript" src="/js/angular.min.js"></script>
		<script type="text/javascript" src="/js/jsoptimizer.js"></script>
	</head>
	<body>
		<div ng-controller="ServiceController">
			<form>
				<div><textarea ng-model="scripts"></textarea></div>
				<div><button ng-click="requestJSFile(scripts)">Save Resources</button></div>
			</form>
			
			
			<form>
				<div><input ng-model="script" type="text" size="30" /></div>
				<div><button ng-click="saveFile(script)">Save Single File</button></div>
			</form>
			
		</div>
		

		
	</body>
</html>