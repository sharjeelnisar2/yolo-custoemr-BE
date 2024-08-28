import React, { useState } from 'react';
import {
  Box,
  Button,
  FormControl,
  FormLabel,
  Input,
  VStack,
} from '@chakra-ui/react';
import axios from 'axios';

function CreateAccountForm({ formData, onCreateAccount }) {
  const [accountType, setAccountType] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token'); 
      await axios.post('http://localhost:8080/api/v1/accounts', {
        fullName: formData.fullName,
        accountType: accountType,
        userID: localStorage.getItem('userID'), 
      }, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      onCreateAccount(); 
    } catch (error) {
      console.error('Error creating account:', error);
    }
  };

  return (
    <Box>
      <form onSubmit={handleSubmit}>
        <VStack spacing={4}>
          <FormControl>
            <FormLabel>Full Name</FormLabel>
            <Input type="text" value={formData.fullName} readOnly />
          </FormControl>
          <FormControl>
            <FormLabel>Account Type</FormLabel>
            <Input
              type="text"
              value={accountType}
              onChange={(e) => setAccountType(e.target.value)}
            />
          </FormControl>
          <Button type="submit" colorScheme="teal">Create Account</Button>
        </VStack>
      </form>
    </Box>
  );
}

export default CreateAccountForm;
