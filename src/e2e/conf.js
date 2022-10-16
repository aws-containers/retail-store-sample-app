var { SpecReporter } = require('jasmine-spec-reporter');

exports.config = {
  framework: 'jasmine',
  specs: ['*.spec.js'],
  params: {
    baseUrl: process.env.ENDPOINT || 'http://localhost'
  },
  capabilities: {
    browserName: 'chrome',
    chromeOptions: {
      args: ['--headless', '--disable-gpu', '--window-size=1024,768', '--no-sandbox'] // Need minimum windows size or the responsive layout breaks navigation assumptions
    }
  },
  directConnect: true,
  onPrepare: function () {
    jasmine.getEnv().addReporter(new SpecReporter({
      displayStacktrace: 'all',       // display stacktrace for each failed assertion, values: (all|specs|summary|none) 
      displaySuccessesSummary: false, // display summary of all successes after execution 
      displayFailuresSummary: true,   // display summary of all failures after execution 
      displayPendingSummary: true,    // display summary of all pending specs after execution 
      displaySuccessfulSpec: true,    // display each successful spec 
      displayFailedSpec: true,        // display each failed spec 
      displayPendingSpec: false,      // display each pending spec 
      displaySpecDuration: false,     // display each spec duration 
      displaySuiteNumber: false,      // display each suite number (hierarchical) 
      colors: {
        success: 'green',
        failure: 'red',
        pending: 'yellow'
      },
      prefixes: {
        success: '✓ ',
        failure: '✗ ',
        pending: '* '
      },
      customProcessors: []
    }));
  }
}