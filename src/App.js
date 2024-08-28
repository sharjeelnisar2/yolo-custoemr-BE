import React from 'react';
import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import Cookies from 'js-cookie';
import Home from './pages/Auth/Home/Home';
import Login from './pages/Auth/login';
import SignUp from './pages/Auth/signup';
import Dashboard from './pages/dashboard/Dashboard';
import CreateAccounts from './pages/dashboard/accounts';
import ViewTransactions from './pages/dashboard/viewTransactions';
import AdminDashboard from './component/admin/AdminDashboard';
import ViewAccounts from './component/admin/viewAccounts/ViewAccounts';

// Helper function to check if user is authenticated
const isAuthenticated = () => {
  return !!Cookies.get('token'); // Check for token in cookies
};

// PrivateRoute component for handling protected routes
const PrivateRoute = ({ element: Component, ...rest }) => {
  return isAuthenticated() ? (
    <Component {...rest} />
  ) : (
    <Navigate to="/home" replace />
  );
};

function App() {
  const location = useLocation();

  // Redirect to /home if on the root path
  if (location.pathname === '/') {
    return <Navigate to="/home" />;
  }

  return (
    <Routes>
      <Route path="/home" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/signup" element={<SignUp />} />
      <Route path="/dashboard" element={<PrivateRoute element={Dashboard} />}>
        <Route path="createAccounts" element={<PrivateRoute element={CreateAccounts} />} />
        <Route path="viewTransactions" element={<PrivateRoute element={ViewTransactions} />} />
      </Route>
      <Route path="/admin" element={<PrivateRoute element={AdminDashboard} />}>
        <Route path="viewAccounts" element={<PrivateRoute element={ViewAccounts} />} />
      </Route>
    </Routes>
  );
}

export default App;
