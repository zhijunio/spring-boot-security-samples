@param String userName
@param String roles

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Spring Boot Tutorials</title>
</head>
<body>
	<h1 style="text-align: center;">Spring Boot Tutorial</h1>
	<h2 style="text-align: center;">Home Page!</h2>
	@if(userName != "Anonymous")
		<p style="text-align: center;">Username: 
			<span>${userName}</span>
		</p>
		<p style="text-align: center;"><a href='/public'>Public</a></p>
		<p>&nbsp;</p>
		<p style="text-align: center;"><a href='/logout'>Logout</a></p>
	@else
		<p style="text-align: center;">Username: Anonymous</p>
		<p style="text-align: center;"><a href='/public'>Public</a></p>
		<p>&nbsp;</p>
		<p style="text-align: center;"><a href='/login'>Login</a></p>
	@endif
	@if(userName != "Anonymous")
		<p>&nbsp;</p>
		@if(roles.contains("ROLE_USER"))
			<p style="text-align: center;"><a href='/user'>Private for User</a></p>
		@endif
		@if(roles.contains("ROLE_ADMIN"))
			<p style="text-align: center;"><a href='/admin'>Private for Admin</a></p>
		@endif
	@endif
</body>
</html>