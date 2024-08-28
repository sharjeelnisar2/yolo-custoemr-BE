// PrivateRoute.js
import React from 'react';
import { Route, Navigate } from 'react-router-dom';
import Cookies from 'js-cookie';

const PrivateRoute = ({ element: Component, ...rest }) => {
  const token = Cookies.get('token'); // Retrieve token from cookies

  return (
    <Route
      {...rest}
      element={token ? <Component /> : <Navigate to="/home" />}
    />
  );
};

export default PrivateRoute;
