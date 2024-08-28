import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Table, Thead, Tbody, Tr, Th, Td } from 'react-super-responsive-table';
import 'react-super-responsive-table/dist/SuperResponsiveTableStyle.css';
import { Rings } from 'react-loader-spinner';
import { Button, Modal, ModalOverlay, ModalContent, ModalHeader, ModalBody, ModalFooter, useDisclosure } from '@chakra-ui/react';
import Swal from 'sweetalert2';
import axios from 'axios';
import './customTableStyles.css'; 
import Cookies from 'js-cookie';


function ViewTransactions() {
  const [transactions, setTransactions] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedTransaction, setSelectedTransaction] = useState({});
  const { isOpen, onOpen, onClose } = useDisclosure();

  const currentAccountId = Cookies.get('accountId');
  const token = Cookies.get('token');

  const fetchTransactions = async () => {

    try {
      //console.log("Fetching transactions for Account Id: ", currentAccountId);
      if(!currentAccountId){
        Swal.fire({
          title: 'Error',
          text: 'Create an Account first.',
          icon: 'error',
          confirmButtonText: 'Close',
        });
      }
      const response = await axios.get(`http://localhost:8080/api/transaction/by-account?id=${currentAccountId}`, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      setTransactions(response.data);
    } catch (error) {
      console.error('Error fetching transactions:', error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchTransactions();
  }, []);

  const handleRowClick = (transaction) => {
    setSelectedTransaction(transaction);
    onOpen();
  };

  return (
    <div className='p-4'>
      <div className="container mx-auto" >
        <div className="bg-white shadow-md rounded-lg p-4">
          <div className="mb-4">
            <Link to="/dashboard">
            </Link>
          </div>
          {isLoading
            ? <div className="flex justify-center items-center h-64">
              <Rings />
            </div>
            : <>
              {transactions.length < 1
                ? <div className='text-center my-5'>
                  <h5 className="text-xl font-semibold"><i className="fa-solid fa-money-bill-1"></i> No Transactions Yet</h5>
                </div>
                : <div className="overflow-x-auto">
                  <Table className="customTable" >
                    <Thead>
                      <Tr>
                        <Th>Transaction ID</Th>

                        <Th>Receiver ID</Th>
                        <Th>Receiver</Th>
                        <Th>Sender</Th>
                        <Th>Date</Th>
                        <Th>Amount</Th>
                        <Th>Indicator</Th>
                      </Tr>
                    </Thead>
                    <Tbody>
                      {
                        transactions.map((transaction) => {
                          // Convert accountId to string for comparison
                          const fromAccountId = transaction.fromAccount.accountID.toString();
                          const indicator = fromAccountId === currentAccountId ? "DB" : "CR";
                          //console.log(`Transaction ID: ${transaction.transactionID} - FromAccountID: ${fromAccountId}, CurrentAccountID: ${currentAccountId}, Indicator: ${indicator}`);
                          return (
                            <Tr key={transaction.transactionID}>
                              <Td>
                                <Button variant='link' onClick={() => handleRowClick(transaction)}>
                                  {transaction.transactionID}
                                </Button>
                              </Td>
                              <Td>{transaction.toAccount.accountID}</Td>
                              <Td>{transaction.toAccount.user.username}</Td>
                              <Td>{transaction.fromAccount.user.username}</Td>
                              <Td>{new Date(transaction.date).toLocaleDateString('en-US')}</Td>
                              <Td>{transaction.amount}</Td>
                              <Td>{indicator}</Td>
                            </Tr>
                          );
                        })
                      }
                    </Tbody>
                  </Table>
                </div>
              }
              <Modal isOpen={isOpen} onClose={onClose}>
                <ModalOverlay />
                <ModalContent>
                  <ModalHeader>Transaction Details</ModalHeader>
                  <ModalBody>
                    <div className='space-y-4'>
                      <div><strong>Account ID#:</strong> {selectedTransaction.toAccount?.accountID}</div>
                      <div><strong>Account Holder Name#:</strong> {selectedTransaction.toAccount?.user?.username || 'N/A'}</div>
                      <div><strong>Transaction Date:</strong> {new Date(selectedTransaction.date).toDateString()}</div>
                      <div><strong>Transaction Time:</strong> {new Date(selectedTransaction.date).toLocaleTimeString('en-US')}</div>
                      <div><strong>Transaction Type:</strong> {selectedTransaction.indicator}</div>
                      <div><strong>Amount:</strong> {selectedTransaction.amount}</div>
                    </div>
                  </ModalBody>
                  <ModalFooter>
                    <Button colorScheme='red' mr={3} onClick={onClose}>
                      Close
                    </Button>
                  </ModalFooter>
                </ModalContent>
              </Modal>
            </>
          }
        </div>
      </div>
    </div>
  );
}

export default ViewTransactions;


