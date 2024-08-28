// import React, { useState, useEffect } from 'react';
// import { useNavigate } from 'react-router-dom';
// import Swal from 'sweetalert2';
// import axios from 'axios';
// import {
//   FormControl,
//   FormLabel,
//   Input,
//   Select,
//   Button,
//   Spinner,
//   Box,
//   Heading,
//   VStack,
//   HStack,
//   Icon
// } from "@chakra-ui/react";
// import { FaUser, FaInfoCircle } from 'react-icons/fa';

// const initialState = {
//   fullName: "",
//   accountType: "",
// };

// function CreateAccounts({ onClose }) {
//   const [isLoading, setIsLoading] = useState(false);
//   const [state, setState] = useState(initialState);
//   const navigate = useNavigate();

//   useEffect(() => {
//     const username = localStorage.getItem('username');
//     setState(prevState => ({
//       ...prevState,
//       fullName: username || ""
//     }));
//   }, []);

//   const handleChange = e => {
//     setState(s => ({ ...s, [e.target.name]: e.target.value }));
//   }

//   const handleSubmit = async e => {
//     e.preventDefault();

//     if (state.fullName.trim() === "") {
//       Swal.fire('Error', 'Your Name field is empty and that is not acceptable.', 'error');
//       return;
//     }

//     const token = localStorage.getItem('token');
//     const userId = localStorage.getItem('userId');

//     if (!token || !userId) {
//       Swal.fire('Error', 'User not authenticated. Please log in.', 'error');
//       return;
//     }

//     const accountData = {
//       ...state,
//       user: {
//         userID: userId
//       }
//     };

//     try {
//       setIsLoading(true);
//       const response = await axios.post('http://localhost:8080/api/v1/accounts', accountData, {
//         headers: {
//           Authorization: `Bearer ${token}`
//         }
//       });
//       const { accountID } = response.data;
//       localStorage.setItem('accountID', accountID);
//       console.log("Account Id after creation: ", accountID)
//       Swal.fire('Success', `Dear ${state.fullName}, your account has been created.`, 'success');
//       navigate("/dashboard/viewAccounts");
//     } catch (error) {
//       Swal.fire('Error', 'An error occurred while creating your account.', 'error');
//     } finally {
//       setIsLoading(false);
//       if (onClose) onClose();
//     }
//     setState(initialState);
//   }

//   return (
//     <Box p={4}>
//       <Heading as="h2" size="lg" mb={4}>Create New Account</Heading>
//       <form onSubmit={handleSubmit}>
//         <VStack spacing={4}>
//           <HStack w="full">
//             <Icon as={FaUser} boxSize={6} />
//             <FormControl isRequired>
//               <FormLabel>Full Name</FormLabel>
//               <Input
//                 type="text"
//                 name="fullName"
//                 value={state.fullName}
//                 isReadOnly
//               />
//             </FormControl>
//           </HStack>
//           <HStack w="full">
//             <Icon as={FaInfoCircle} boxSize={6} />
//             <FormControl isRequired>
//               <FormLabel>Choose Account Type</FormLabel>
//               <Select
//                 name="accountType"
//                 value={state.accountType}
//                 onChange={handleChange}
//               >
//                 <option value="Saving">Saving</option>
//                 <option value="Current">Current</option>
//               </Select>
//             </FormControl>
//           </HStack>
//           <Box w="full" textAlign="end">
//             <Button type='submit' isLoading={isLoading} colorScheme="green">
//               { !isLoading ? "Create Account" : <Spinner size="sm" /> }
//             </Button>
//           </Box>
//         </VStack>
//       </form>
//     </Box>
//   );
// }

// export default CreateAccounts;
