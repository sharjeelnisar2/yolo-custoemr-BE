import React from 'react';
import { Routes, Route, Link as RouterLink } from 'react-router-dom';
import {
  Box, Flex, Link, Container, useDisclosure, IconButton,
  Drawer, DrawerBody, DrawerCloseButton, DrawerContent,
  DrawerHeader, DrawerOverlay, Heading
} from '@chakra-ui/react';
import { HamburgerIcon } from '@chakra-ui/icons';
import Dashboard from './Dashboard';
import ViewAccounts from './viewAccounts/ViewAccounts';
import CreateAccounts from "./createAccounts/CreateAccounts";
import ViewTransactions from './viewTransactions/ViewTransactions';

function Index() {
  const { isOpen, onOpen, onClose } = useDisclosure();

  return (
    <>
      <Box as="header" className="bg-blue-600 text-white">
        <Flex
          as="nav"
          align="center"
          justify="space-between"
          wrap="wrap"
          p={4}
          maxW="container.xl"
          mx="auto"
        >
          <Flex align="center">
            <RouterLink to="/">
              <Heading size="lg" className="flex items-center">
                <i className="fa-solid fa-house"></i>
              </Heading>
            </RouterLink>
          </Flex>
          <IconButton
            aria-label="Toggle Navigation"
            icon={<HamburgerIcon />}
            variant="outline"
            colorScheme="white"
            onClick={onOpen}
            display={{ base: 'block', lg: 'none' }}
          />
          <Drawer
            isOpen={isOpen}
            placement="right"
            onClose={onClose}
          >
            <DrawerOverlay>
              <DrawerContent>
                <DrawerCloseButton />
                <DrawerHeader>Menu</DrawerHeader>
                <DrawerBody>
                  <Flex direction="column" align="start" gap={4}>
                    <Link as={RouterLink} to="/dashboard" onClick={onClose}>
                      Dashboard
                    </Link>
                    <Link as={RouterLink} to="/dashboard/viewAccounts" onClick={onClose}>
                      Accounts
                    </Link>
                    <Link as={RouterLink} to="/dashboard/viewTransactions" onClick={onClose}>
                      Transactions
                    </Link>
                  </Flex>
                </DrawerBody>
              </DrawerContent>
            </DrawerOverlay>
          </Drawer>
          <Flex
            display={{ base: 'none', lg: 'flex' }}
            gap={4}
            align="center"
          >
            <Link as={RouterLink} to="/dashboard">
              Dashboard
            </Link>
            <Link as={RouterLink} to="/dashboard/viewAccounts">
              Accounts
            </Link>
            <Link as={RouterLink} to="/dashboard/viewTransactions">
              Transactions
            </Link>
          </Flex>
        </Flex>
      </Box>
      <Box className="contentArea p-4">
        <Container maxW="container.xl">
          <Routes>
            <Route path="dashboard" element={<Dashboard />}>
              <Route path="viewTransactions" element={<ViewTransactions />} />
              <Route path="createAccounts" element={<CreateAccounts />} />
              <Route path="viewAccounts" element={<ViewAccounts />} />
            </Route>
          </Routes>
        </Container>
      </Box>
    </>
  );
}

export default Index;
