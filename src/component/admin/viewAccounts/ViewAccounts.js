import React, { useEffect, useState } from 'react';
import { Table, Thead, Tbody, Tr, Th, Td } from 'react-super-responsive-table';
import 'react-super-responsive-table/dist/SuperResponsiveTableStyle.css';
import { Rings } from "react-loader-spinner";
import Swal from 'sweetalert2';
import './customTableStyles.css';
import Cookies from 'js-cookie';
import {
  Box,
  Button,
  Container,
  Divider,
  VStack,
  Text,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalCloseButton,
  ModalBody,
  FormControl,
  FormLabel,
  Input,
  useDisclosure,
} from '@chakra-ui/react';

function ViewAccounts() {
  const [accounts, setAccounts] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedAccount, setSelectedAccount] = useState(null);
  const { isOpen, onOpen, onClose } = useDisclosure();

  const fetchAccounts = async () => {
    const userId = Cookies.get('userId');
    const token = Cookies.get('token');

    console.log("user id in fetch accounts: ", userId)
    console.log("token in fetch accounts: ", token)

    try {
      const response = await fetch('http://localhost:8080/api/v1/accounts', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      setAccounts(data);
    } catch (error) {
      console.error('Error fetching accounts:', error);
      Swal.fire('Error!', 'There was a problem fetching the accounts.', 'error');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchAccounts();
  }, []);

  const handleEditClick = (account) => {
    setSelectedAccount(account);
    onOpen();
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setSelectedAccount((prev) => ({
      ...prev,
      user: {
        ...prev.user,
        [name]: value,
      },
    }));
  };


  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = Cookies.get('token');
    try {
      const response = await fetch(`http://localhost:8080/api/v1/accounts/${selectedAccount.accountID}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          ...selectedAccount,
        })
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      Swal.fire('Success!', 'The account has been updated.', 'success');
      fetchAccounts();
      onClose(); 
    } catch (error) {
      console.error('Error updating account:', error);
      Swal.fire('Error!', 'There was a problem updating the account.', 'error');
    }
  };

  
  const handleDeleteClick = async (accountID) => {
    try {
      const result = await Swal.fire({
        title: 'Are you sure?',
        text: "You won't be able to revert this!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, delete it!',
      });
  
      if (result.isConfirmed) {
        const token = Cookies.get('token');
        const response = await fetch(`http://localhost:8080/api/v1/accounts/${accountID}`, {
          method: 'DELETE',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          }
        });
  
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
  
        Swal.fire('Deleted!', 'The account has been deleted.', 'success');
        fetchAccounts(); // Refresh the list
      }
    } catch (error) {
      console.error('Error deleting account:', error);
      Swal.fire('Error!', 'There was a problem deleting the account.', 'error');
    }
  };

  return (
    <Container maxW="container.xl" py={8} p={4} borderWidth="1px" borderRadius="md" boxShadow="md">

        <Button colorScheme="red" leftIcon={<i className="fa-solid fa-arrow-left"></i>}>
          Dashboard
        </Button>
      <VStack spacing={4} mt={4} align="start">
        <Text fontSize="2xl" fontWeight="bold">
          <i className="fa-solid fa-user me-1"></i> Accounts
        </Text>
        <Divider />
        {isLoading ? (
          <Box d="flex" justifyContent="center" alignItems="center" height="100px">
            <Rings />
          </Box>
        ) : accounts.length < 1 ? (
          <VStack spacing={4} align="center">
            <Text>No accounts available!</Text>
            {/* <Link to="/dashboard/createAccounts">
              <Button colorScheme="teal">Create Account</Button>
            </Link> */}
          </VStack>
        ) : (
          <>
            <Box overflowX="auto" width="100%">
              <Table className="customTable" id="customers" width="100%">
                <Thead>
                  <Tr>
                    <Th>Account ID</Th>
                    <Th>Account Number</Th>
                    <Th>Account Type</Th>
                    <Th>Username</Th>
                    <Th>Email</Th>
                    <Th>Address</Th>
                    <Th>Actions</Th>
                  </Tr>
                </Thead>
                <Tbody>
                  {accounts.map((account) => (
                    <Tr key={account.accountID}>
                      <Td>{account.accountID}</Td>
                      <Td>{account.accountNumber}</Td>
                      <Td>{account.accountType}</Td>
                      <Td>{account.user.username}</Td>
                      <Td>{account.user.email}</Td>
                      <Td>{account.user.address}</Td>
                      <Td>
                        <Button colorScheme="blue" onClick={() => handleEditClick(account)}>Edit</Button>
                        <Button colorScheme="red" ml={2} onClick={() => handleDeleteClick(account.accountID)}>Delete</Button>
                      </Td>
                    </Tr>
                  ))}
                </Tbody>
              </Table>
            </Box>
          </>
        )}
      </VStack>

      {selectedAccount && (
        <Modal isOpen={isOpen} onClose={onClose} size="xl">
          <ModalOverlay />
          <ModalContent>
            <ModalHeader>Edit Account</ModalHeader>
            <ModalCloseButton />
            <ModalBody>
              <form onSubmit={handleSubmit}>
                <FormControl mb={4}>
                  <FormLabel>Account ID</FormLabel>
                  <Input type="text" value={selectedAccount.accountID} isReadOnly />
                </FormControl>
                <FormControl mb={4}>
                  <FormLabel>Account Number</FormLabel>
                  <Input type="text" name="accountNumber" value={selectedAccount.accountNumber} onChange={handleInputChange} />
                </FormControl>
                <FormControl mb={4}>
                  <FormLabel>Account Type</FormLabel>
                  <Input type="text" name="accountType" value={selectedAccount.accountType} onChange={handleInputChange} />
                </FormControl>
                <FormControl mb={4}>
                  <FormLabel>Username</FormLabel>
                  <Input type="text" name="username" value={selectedAccount.user.username} onChange={handleInputChange} />
                </FormControl>
                <FormControl mb={4}>
                  <FormLabel>Email</FormLabel>
                  <Input type="email" name="email" value={selectedAccount.user.email} onChange={handleInputChange} />
                </FormControl>
                <FormControl mb={4}>
                  <FormLabel>Address</FormLabel>
                  <Input type="text" name="address" value={selectedAccount.user.address} onChange={handleInputChange} />
                </FormControl>
                <Button colorScheme="teal" type="submit">Save Changes</Button>
              </form>
            </ModalBody>
          </ModalContent>
        </Modal>
      )}
    </Container>
  );
}

export default ViewAccounts;
