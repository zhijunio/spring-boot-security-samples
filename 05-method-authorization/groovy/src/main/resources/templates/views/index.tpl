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
    	h2(class:'center', "Home Page!")
		p(class:'center', "Username: " + name)
		div(class: 'center') {
			p(a(href: '/public', 'Public'))
			if (isAuthenticated) {
				p(a(href: '/logout', 'Logout'))
			} else {
				p(a(href: '/login', 'Login'))
			}	
			if (isUser) {
				p(a(href: '/user', 'Private for User'))
			}
			if (isAdmin) {
				p(a(href: '/admin', 'Private for Admin'))
			}
		}
	}
}