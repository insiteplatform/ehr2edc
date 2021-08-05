/* Initialization of the i18next system (internationalization for javascript)
 * 
 * NOTE: translations are only available once the init() function has completed,
 * e.g. it might not yet be possible to use i18next.t() in $(document).ready(function()); of the particular page js.
 * use i18next.on("initialized", function(options) {}); if necessary
 */

i18next.use(i18nextXHRBackend)
i18next.use(window.i18nextBrowserLanguageDetector)
var options={
	debug: false,
	fallbackLng: 'en',
	ns: [
       "common", page_namespace
	],
	defaultNS: page_namespace,
	fallbackNS: "common",
	backend: {
      loadPath: loadPath
	},
	parseMissingKeyHandler: function(key) {
		return "JS Resource missing: " + key;
	}
}
if (enableInternationalization) {
	options.detection = {};
	options.detection.order = ["navigator"]; //Detect locale from navigator.languages javascript variable
}
else {
	options.lng = "en"; //Fixed language english, no detection is performed
}
i18next.init(options, function(err, t) {
	jqueryI18next.init(i18next, $);
});