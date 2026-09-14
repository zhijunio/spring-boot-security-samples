yieldUnescaped '<!DOCTYPE html>'
html {
	head {
		title('Spring Boot Tutorials')
		style(type:'text/css') { yieldUnescaped """
            .center {
                text-align:center;
            }
			"""  }
	}
	body {
		h1(class:'center', "Spring Boot Tutorial")
		h2(class:'center', "Private Page for User!")
		div(class: 'center') {
			p("Username: " + customUser.username.toUpperCase())
			p("Name: " + customUser.name)
			p("E-mail: " + customUser.email)
			if (customUser.roles.size() > 1) {
				p("Roles: " + customUser.roles)
			} else {
				p("Role: " + customUser.roles)
			}
			p(a(href: '/', 'Home'))
		}
	}
}