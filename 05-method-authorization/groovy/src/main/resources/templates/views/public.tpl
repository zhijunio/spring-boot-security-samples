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
		h2(class:'center', "Public Page!")
		p(class:'center', "Username: " + name)
		div(class: 'center') {
			p(a(href: '/', 'Home'))
		}
	}
}