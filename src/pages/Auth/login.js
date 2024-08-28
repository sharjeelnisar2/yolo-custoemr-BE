import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Box, Button, FormControl, FormLabel, Input, Stack, Text, Heading } from "@chakra-ui/react";
import Swal from "sweetalert2";
import axios from "axios";
import Cookies from 'js-cookie';

function Login() {
  const [credentials, setCredentials] = useState({ username: "", password: "" });
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setCredentials({ ...credentials, [name]: value });
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);

    if (!credentials.username || !credentials.password) {
      Swal.fire({
        title: 'Error',
        text: 'Please fill in all fields.',
        icon: 'error',
        confirmButtonText: 'OK'
      });
      setLoading(false);
      return;
    }

    try {
      const response = await axios.post("http://localhost:8080/api/v1/auth/login", credentials);
      const { accountId, role, userId, token, username } = response.data;

      // Set cookies instead of localStorage
      Cookies.set('token', token, { expires: 1 }); // expires in 1 day
      Cookies.set('userId', userId, { expires: 1 });
      Cookies.set('accountId', accountId, { expires: 1 });
      Cookies.set('username', username, { expires: 1 });

      // Log cookies
      console.log("Cookie Token:", Cookies.get('token'));
      console.log("UserId:", Cookies.get('userId'));
      console.log("Username:", Cookies.get('username'));
      console.log("AccountId:", Cookies.get('accountId'));
      console.log("Role:", role);

      Swal.fire({
        title: 'Login successful',
        icon: 'success',
        confirmButtonText: 'OK'
      });

      if (role === 'ADMIN') {
        navigate("/admin/viewAccounts");
      } else {
        navigate("/dashboard");
      }
    } catch (error) {
      console.error("Login Error:", error);
      Swal.fire({
        title: 'Login failed',
        text: 'Incorrect username or password.',
        icon: 'error',
        confirmButtonText: 'OK'
      });
    } finally {
      setCredentials({ username: "", password: "" });
      setLoading(false);
    }
  };

  return (
    <Box bg="gray.50" minH="50vh" d="flex" alignItems="center" justifyContent="center" p={6}>
      <Box bg="white" p={8} borderRadius="lg" shadow="md" maxW="md" w="full">
        <Heading as="h1" size="lg" mb={4} textAlign="center">
          Login
        </Heading>
        <form onSubmit={handleLogin}>
          <Stack spacing={4}>
            <FormControl id="username" isRequired>
              <FormLabel>Username</FormLabel>
              <Input
                type="text"
                name="username"
                value={credentials.username}
                onChange={handleInputChange}
                placeholder="Username"
              />
            </FormControl>
            <FormControl id="password" isRequired>
              <FormLabel>Password</FormLabel>
              <Input
                type="password"
                name="password"
                value={credentials.password}
                onChange={handleInputChange}
                placeholder="•••••"
              />
            </FormControl>
            <Stack spacing={3}>
              <Button colorScheme="blue" type="submit" isLoading={loading}>
                Login
              </Button>
              <Text textAlign="center">
                Not registered? <Link to="/home">Create an account</Link>
              </Text>
            </Stack>
          </Stack>
        </form>
      </Box>
    </Box>
  );
}

export default Login;















