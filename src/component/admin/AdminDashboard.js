import React, { useState } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import {
  Box,
  Button,
  Heading,
  Container,
  Flex,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalCloseButton,
  useDisclosure,
  VStack,
  Divider,
  FormControl,
  FormLabel,
  Input,
  Select,
  FormErrorMessage
} from '@chakra-ui/react';
import axios from 'axios';
import Swal from 'sweetalert2';
import Cookies from 'js-cookie';

function Dashboard() {
  const [newUser, setNewUser] = useState({
    username: '',
    email: '',
    address: '',
    password: '',
  });
  const [newAccount, setNewAccount] = useState({
    fullname: '',
    accountType: '',
  });
  const [userID, setUserID] = useState(null);
  const [isRegisteringUser, setIsRegisteringUser] = useState(true);

  const [errors, setErrors] = useState({
    username: '',
    email: '',
    address: '',
    password: '',
  });

  // eslint-disable-next-line
  const { isOpen: isAccountsOpen, onOpen: onOpenAccounts, onClose: onCloseAccounts } = useDisclosure();
  const { isOpen: isCreateAccountOpen, onOpen: onOpenCreateAccount, onClose: onCloseCreateAccount } = useDisclosure();
  const { isOpen: isDepositOpen, onClose: onCloseDeposit } = useDisclosure();
  const navigate = useNavigate(); 

  const handleUserChange = (e) => {
    const { name, value } = e.target;
    setNewUser({ ...newUser, [name]: value });
    if (name === 'username') {
      if (value.length > 8) {
        setErrors(prev => ({ ...prev, username: 'Username must be less that 8 characters long.' }));
      } else {
        setErrors(prev => ({ ...prev, username: '' }));
      }
    } else if (name === 'email') {
      if (!/\S+@\S+\.\S+/.test(value)) {
        setErrors(prev => ({ ...prev, email: 'Email must be a valid email address.' }));
      } else {
        setErrors(prev => ({ ...prev, email: '' }));
      }
    } else if (name === 'address') {
      if (value.length > 30) {
        setErrors(prev => ({ ...prev, address: 'Address must be less than 30 characters.' }));
      } else {
        setErrors(prev => ({ ...prev, address: '' }));
      }
    } else if (name === 'password') {
      if (!/^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{8,}$/.test(value)) {
        setErrors(prev => ({ ...prev, password: 'Password must be at least 8 characters long and include both letters and numbers.' }));
      } else {
        setErrors(prev => ({ ...prev, password: '' }));
      }
    }
  };

  const handleAccountChange = (e) => {
    setNewAccount({ ...newAccount, [e.target.name]: e.target.value });
  };

  const handleRegisterUser = async () => {
    if (Object.values(errors).some(error => error !== '')) {
      Swal.fire({
        title: 'Validation Error',
        text: 'Please correct the errors in the form.',
        icon: 'error',
        confirmButtonText: 'OK'
      });
      return;
    }
    
    try {
      const response = await axios.post('http://localhost:8080/api/v1/auth/register', newUser);
      const userID = response.data.userID;
      console.log("user created with userID: ", userID)
      setUserID(userID);
      setNewAccount(prev => ({ ...prev, fullname: newUser.username })); // Set fullname as username
      setIsRegisteringUser(false); // Switch to account creation form
    } catch (error) {
      console.error('Error registering user:', error);
    }
  };

  const handleCreateAccount = async () => {
    try {
      const token = Cookies.get('token');

      console.log("Cookie received token in create user by Admin: ", token )
      await axios.post('http://localhost:8080/api/v1/accounts', {
        ...newAccount,
        user: {
          userID: userID,
        },
      }, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      onCloseCreateAccount();
      setIsRegisteringUser(true); 

      
      Swal.fire({
        title: 'Account Created Successfully',
        text: `Account for ${newAccount.fullname} has been created.`,
        icon: 'success',
        confirmButtonText: 'OK'
      });
    } catch (error) {
      console.error('Error creating account:', error);
      Swal.fire({
        title: 'Error',
        text: 'There was an issue creating the account. Please try again.',
        icon: 'error',
        confirmButtonText: 'OK'
      });
    }
  };

  const handleLogout = () => {
    Cookies.remove('token');
    Cookies.remove('userId');
    Cookies.remove('accountId');
    Cookies.remove('username');
    
    console.log("Removed Cookies")
    navigate('/home');
  };

  return (
    <Flex minH="100vh">
      <Box
        w={{ base: 'full', md: '20%' }}
        p={4}
        bg="gray.100"
        borderRightWidth="1px"
        height="200vh"
        position={{ base: 'relative', md: 'fixed' }}
        top="0"
        left="0"
        zIndex="1"
      >
        <Divider />
        <Heading size="lg" mb={8} textAlign="center">Admin Dashboard</Heading>
        <Divider />
        <VStack spacing={4} align="start">
          <Button w="full" onClick={onOpenAccounts}>View Accounts</Button>
          <Button w="full" onClick={onOpenCreateAccount}>Create Account</Button>
          <Button w="full" colorScheme="red" onClick={handleLogout}>Logout</Button>
        </VStack>
      </Box>

      <Box
        ml={{ base: '0', md: '20%' }}
        p={4}
        w={{ base: 'full', md: '80%' }}
        bg="white"
      >
        <Container maxW="container.xl" py={5}>
          <Flex wrap="wrap" gap={4}>
            <Box w="full" lg="48%" p={4} borderWidth={1} borderRadius="lg" boxShadow="lg">
              
              <Heading size="lg" mb={4} textAlign="center" color="gray.700">
                <i className="fa-solid fa-money-bill-1"></i> Welcome to the Admin Dashboard
              </Heading>
              <Divider />
            </Box>
          </Flex>
        </Container>

        <Outlet />
      </Box>

      <Modal isOpen={isCreateAccountOpen} onClose={onCloseCreateAccount} size="xl">
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>{isRegisteringUser ? 'Register New User first' : 'Create New Account'}</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            {isRegisteringUser ? (
              <>
                <FormControl mb={4} isInvalid={errors.username !== ''}>
                  <FormLabel>Username</FormLabel>
                  <Input type="text" name="username" placeholder="Username" value={newUser.username} onChange={handleUserChange} />
                  <FormErrorMessage>{errors.username}</FormErrorMessage>
                </FormControl>
                <FormControl mb={4} isInvalid={errors.email !== ''}>
                  <FormLabel>Email</FormLabel>
                  <Input type="email" name="email" placeholder="abc@gmail.com" value={newUser.email} onChange={handleUserChange} />
                  <FormErrorMessage>{errors.email}</FormErrorMessage>
                </FormControl>
                <FormControl mb={4} isInvalid={errors.address !== ''}>
                  <FormLabel>Address</FormLabel>
                  <Input type="text" name="address" placeholder="Address" value={newUser.address} onChange={handleUserChange} />
                  <FormErrorMessage>{errors.address}</FormErrorMessage>
                </FormControl>
                <FormControl mb={4} isInvalid={errors.password !== ''}>
                  <FormLabel>Password</FormLabel>
                  <Input type="password" name="password" placeholder="****" value={newUser.password} onChange={handleUserChange} />
                  <FormErrorMessage>{errors.password}</FormErrorMessage>
                </FormControl>
                <Button colorScheme="teal" onClick={handleRegisterUser}>Register User</Button>
              </>
            ) : (
              <>
                <FormControl mb={4}>
                  <FormLabel>Full Name</FormLabel>
                  <Input type="text" name="fullname" value={newAccount.fullname} readOnly />
                </FormControl>
                <FormControl mb={4}>
                  <FormLabel>Account Type</FormLabel>
                  <Select name="accountType" value={newAccount.accountType} onChange={handleAccountChange}>
                    <option value="CHECKING">CHECKING</option>
                    <option value="SAVING">SAVING</option>
                  </Select>
                </FormControl>
                <Button colorScheme="teal" onClick={handleCreateAccount}>Create Account</Button>
              </>
            )}
          </ModalBody>
        </ModalContent>
      </Modal>

      <Modal isOpen={isDepositOpen} onClose={onCloseDeposit} size="xl">
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Create Account</ModalHeader>
          <ModalCloseButton />
        </ModalContent>
      </Modal>
    </Flex>
  );
}

export default Dashboard;
