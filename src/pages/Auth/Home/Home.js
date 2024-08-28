import React from 'react';
import { Container, Box, Text, Tabs, Tab, TabList, TabPanels, TabPanel } from '@chakra-ui/react';
import Login from '../login';
import SignUp from '../signup';


function Home() {
  return (

    <Container maxW="xl" centerContent >
      <Box
        textAlign="center"
        display="flex"
        justifyContent="center"
        p={3}
        bg="white"
        w="full"
        m="40px 0 15px 0"
        borderRadius="lg"
        borderWidth="1px"
        boxShadow="lg"
      >
        <Text fontSize="4xl" fontFamily="work sans" color="grey.500">Digital Banking</Text>
      </Box>
      <Box
        bg="white"
        w="full"
        p={4}
        color="black"
        borderRadius="lg"
        borderWidth="1px"
        boxShadow="lg"
      >
        <Tabs
          variant="soft-rounded"
          colorScheme="green"
          isFitted
        >
          <TabList>
            <Tab>Login</Tab>
            <Tab>Register</Tab>
          </TabList>
          <TabPanels>
            <TabPanel>
              <Login />
            </TabPanel>
            <TabPanel>
              <SignUp />
            </TabPanel>
          </TabPanels>
        </Tabs>
      </Box>
    </Container>
  );
}

export default Home;
