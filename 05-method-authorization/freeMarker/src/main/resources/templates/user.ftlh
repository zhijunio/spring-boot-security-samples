<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Spring Boot Tutorials</title>
</head>
<body>
	<h1 style="text-align: center;">Spring Boot Tutorial</h1>
	<h2 style="text-align: center;">Private Page for User!</h2>
	<p style="text-align: center;">Username: 
		<span><#if user??>${user.username()}<#else>Anonymous</#if></span>
	</p>
	<p style="text-align: center;">Name: <span><#if user??>${user.name()}<#else> </#if></span></p>
	<p style="text-align: center;">E-mail: <span><#if user??>${user.email()}<#else> </#if></span></p>
	<p style="text-align: center;"><#if (user.roles()?size > 1)>Roles:<#else>Role:</#if> <span><#if user??>${user.roles()?join(", ")}<#else> </#if></span></p>
	<p style="text-align: center;"><a href='/'>Home</a></p>
	
</body>
</html>