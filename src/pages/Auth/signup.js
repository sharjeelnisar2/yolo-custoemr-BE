import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  Box,
  Button,
  FormControl,
  FormLabel,
  Input,
  Stack,
  Text,
  Heading,
  FormErrorMessage
} from '@chakra-ui/react';
import axios from 'axios';
import Swal from "sweetalert2";

function SignUp() {
  const [userDetails, setUserDetails] = useState({
    username: '',
    email: '',
    address: '',
    password: '',
  });
  const [errors, setErrors] = useState({
    username: '',
    email: '',
    address: '',
    password: '',
  });
  const navigate = useNavigate();

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setUserDetails({
      ...userDetails,
      [name]: value
    });

    // Validate input fields
    if (name === 'username') {
      setErrors(prev => ({
        ...prev,
        username: value.length > 8 ? 'Username must be less than 8 characters.' : ''
      }));
    } else if (name === 'email') {
      setErrors(prev => ({
        ...prev,
        email: !/\S+@\S+\.\S+/.test(value) ? 'Email must be a valid email address.' : ''
      }));
    } else if (name === 'address') {
      setErrors(prev => ({
        ...prev,
        address: value.length > 30 ? 'Address must be less than 30 characters.' : ''
      }));
    } else if (name === 'password') {
      setErrors(prev => ({
        ...prev,
        password: !/^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{8,}$/.test(value)
          ? 'Password must be at least 8 characters long and include both letters and numbers.'
          : ''
      }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Final validation check before submission
    if (Object.values(errors).some(error => error !== '') || 
        Object.values(userDetails).some(value => value.trim() === '')) {
      return;
    }

    try {
      const response = await axios.post("http://localhost:8080/api/v1/auth/register", userDetails);

      if (response.status === 201 || response.status === 200) {
        Swal.fire({
          title: 'User registered',
          text: 'You can now log in.',
          icon: 'success',
          confirmButtonText: 'OK'
        });
        navigate("/home");
      }
    } catch (error) {
      Swal.fire({
        title: 'Error',
        text: 'User is already registered.',
        icon: 'error',
        confirmButtonText: 'OK'
      });
    } finally {
      // Clear the form fields regardless of the result
      setUserDetails({
        username: "",
        email: "",
        address: "",
        password: "",
      });
    }
  };

  return (
    <Box bg="gray.50" minH="60vh" display="flex" alignItems="center" justifyContent="center" p={6}>
      <Box bg="white" p={8} borderRadius="lg" shadow="md" maxW="md" w="full">
        <Heading as="h1" size="lg" mb={4} textAlign="center">
          Sign Up
        </Heading>
        <form onSubmit={handleSubmit}>
          <Stack spacing={4}>
            <FormControl id="username" isRequired isInvalid={errors.username !== ''}>
              <FormLabel>Username</FormLabel>
              <Input
                type="text"
                name="username"
                value={userDetails.username}
                onChange={handleInputChange}
                placeholder="username"
              />
              <FormErrorMessage>{errors.username}</FormErrorMessage>
            </FormControl>
            <FormControl id="email" isRequired isInvalid={errors.email !== ''}>
              <FormLabel>Email Address</FormLabel>
              <Input
                type="email"
                name="email"
                value={userDetails.email}
                onChange={handleInputChange}
                placeholder="abcd@gmail.com"
              />
              <FormErrorMessage>{errors.email}</FormErrorMessage>
            </FormControl>
            <FormControl id="address" isInvalid={errors.address !== ''}>
              <FormLabel>Address</FormLabel>
              <Input
                type="text"
                name="address"
                value={userDetails.address}
                onChange={handleInputChange}
                placeholder="Address"
              />
              <FormErrorMessage>{errors.address}</FormErrorMessage>
            </FormControl>
            <FormControl id="password" isRequired isInvalid={errors.password !== ''}>
              <FormLabel>Password</FormLabel>
              <Input
                type="password"
                name="password"
                value={userDetails.password}
                onChange={handleInputChange}
                placeholder="••••••••"
              />
              <FormErrorMessage>{errors.password}</FormErrorMessage>
            </FormControl>
            <Stack spacing={3}>
              <Button colorScheme="blue" type="submit">
                Sign Up
              </Button>
              <Text textAlign="center">
                Already have an account? <Link to="/home">Login</Link>
              </Text>
            </Stack>
          </Stack>
        </form>
      </Box>
    </Box>
  );
}

export default SignUp;
