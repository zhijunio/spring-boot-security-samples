@import willydekeyser.config.CustomUser

@param CustomUser user

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Spring Boot Tutorials</title>
</head>
<body>
	<h1 style="text-align: center;">Spring Boot Tutorial</h1>
	<h2 style="text-align: center;">Private Page for User!</h2>
	<p style="text-align: center;">UserName: ${user.username().toUpperCase()}</p>
	<p style="text-align: center;">Name: ${user.name()}</p>
	<p style="text-align: center;">E-mail: ${user.email()}</p>
	<p style="text-align: center;">
		@if(user.roles().size() > 1)
			Roles: 
		@else
			Role: 
		@endif
		${user.roles().toString()}
	</p>
	<p style="text-align: center;"><a href='/'>Home</a></p>
</body>
</html>