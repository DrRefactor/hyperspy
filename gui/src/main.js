(function() {
  const React = require('react')
  // needed for React Developer Tools
  window.React = React

  const container = document.getElementById('root')
  const App = require('./App').default

  const ReactDOM = require('react-dom')
  return ReactDOM.render(<App />, container)
})();
