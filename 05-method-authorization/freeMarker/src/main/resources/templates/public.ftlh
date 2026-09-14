<#assign 
	username="Anonymous">
<#assign security=Session.SPRING_SECURITY_CONTEXT??>
<#if security>
   	<#assign 
   		username = Session.SPRING_SECURITY_CONTEXT.authentication.principal.username()>
</#if>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Spring Boot Tutorials</title>
</head>
<body>
	<h1 style="text-align: center;">Spring Boot Tutorial</h1>
	<h2 style="text-align: center;">Public Page!</h2>
	<p style="text-align: center;">Username: ${username}</p>
	<p style="text-align: center;"><a href='/'>Home</a></p>
</body>
</html>