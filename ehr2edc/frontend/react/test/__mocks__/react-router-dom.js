import React from 'react';

const rrd = require('react-router-dom');
rrd.BrowserRouter = ({children}) => <div>{children}</div>;
rrd.withRouter = f => f;

module.exports = rrd;