<#assign 
	username="Anonymous"
	isAuthenticated = false
	isUser = false
	isAdmin = false>
<#assign security=Session.SPRING_SECURITY_CONTEXT??>
<#if security>
	<#if Session.SPRING_SECURITY_CONTEXT.authentication.principal.roles()?seq_contains("ROLE_USER")>
		<#assign isUser = true>
	</#if>
	<#if Session.SPRING_SECURITY_CONTEXT.authentication.principal.roles()?seq_contains("ROLE_ADMIN")>
		<#assign isAdmin = true>
	</#if>
   	<#assign 
   		username = Session.SPRING_SECURITY_CONTEXT.authentication.principal.username()
   		isAuthenticated = Session.SPRING_SECURITY_CONTEXT.authentication.isAuthenticated()>
</#if>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Spring Boot Tutorials</title>
</head>
<body>
	<h1 style="text-align: center;">Spring Boot Tutorial</h1>
	<h2 style="text-align: center;">Home Page!</h2>
	<p style="text-align: center;">Username: ${username}</p>
	<p style="text-align: center;"><a href='/public'>Public</a></p>
	<p>&nbsp;</p>
	<#if isAuthenticated>
		<p style="text-align: center;"><a href='/logout'>Logout</a></p>
	<#else>
		<p style="text-align: center;"><a href='/login'>Login</a></p>
	</#if>
	<#if isUser>
		<p style="text-align: center;"><a href='/user'>Private for User</a></p>
	</#if>
	<#if isAdmin>
		<p style="text-align: center;"><a href='/admin'>Private for Admin</a></p>
	</#if>	
</body>
</html>