/** @type {import('tailwindcss').Config} */
module.exports = {
    content: ["./src/**/*.clj"],
    theme: {
	extend: {
	    colors: {
		"solblue": "#268bd2"
	    },
	    fontFamily: {
		'sans': ["DotGothic16", "mono"],
	    },
	    typography: theme => ({
		DEFAULT: {
		    css: {
			h1: {color: theme('colors.red.700')},
			h2: {color: theme('colors.cyan.700')},
			h3: {color: theme('colors.cyan.700')},
			h4: {color: theme('colors.pink.800')},
			h5: {color: theme('colors.cyan.800')},
			h6: {color: theme('colors.cyan.900')},
			a: {color: theme('colors.solblue')},
			'a:hover': {color: theme('colors.blue.500')}
		    }
		},
		invert: {}
	    })
	}
    },
    plugins: [
	require('@tailwindcss/typography'),
	require("daisyui")
    ],
    daisyui: {
        themes: ["retro", "cupcake", "synthwave"],
    }
}

