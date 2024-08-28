import React, { createContext, useState, useEffect } from 'react';

export const AuthenticatedContext = createContext();

function AuthenticatedContextProvider(props) {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoader, setIsLoader] = useState(true);
  const [userId, setUserId] = useState('');
  const [user, setUser] = useState({
    

  });


  useEffect(() => {
    const checkAuth = () => {
      const storedUserId = localStorage.getItem('userId');
      if (storedUserId) {
        setIsAuthenticated(true);
        setUser({ id: storedUserId, name: 'Ahmad Irtza' });
      } else {
        setIsAuthenticated(false);
        setUser({});
      }
      setIsLoader(false);
    };

    checkAuth();
  }, []);

  return (
    <AuthenticatedContext.Provider
      value={{ isAuthenticated, isLoader, setIsAuthenticated, user, userId, setUserId }}
    >
      {props.children}
    </AuthenticatedContext.Provider>
  );
}

export default AuthenticatedContextProvider;
