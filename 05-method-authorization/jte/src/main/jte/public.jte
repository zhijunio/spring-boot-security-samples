@param String userName

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Spring Boot Tutorials</title>
</head>
<body>
	<h1 style="text-align: center;">Spring Boot Tutorial</h1>
	<h2 style="text-align: center;">Public Page!</h2>
	@if(userName != "Anonymous")
		<p style="text-align: center;">Username: 
			<span>${userName}</span>
		</p>
	@else
		<p style="text-align: center;">Username: Anonymous</p>
	@endif
	<p style="text-align: center;"><a href='/'>Home</a></p>
</body>
</html>