import React, { useContext, useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Box, Button, Container, Flex, Heading, Text, IconButton, Spinner } from '@chakra-ui/react';
import { BsFacebook, BsGithub, BsTwitter } from 'react-icons/bs';
import dayjs from 'dayjs';
import { AuthenticatedContext } from '../../Context/AuthContext';
import { motion } from 'framer-motion';

const MotionBox = motion(Box);

function LandingPage() {
    const [time, setTime] = useState("");
    const { isAuthenticated, setIsAuthenticated } = useContext(AuthenticatedContext);

    useEffect(() => {
        const intervalId = setInterval(() => {
            setTime(dayjs().format("DD/MM/YYYY, hh:mm:ss A"));
        }, 1000);

        return () => clearInterval(intervalId);
    }, []);

    const handleLogout = () => {
        setIsAuthenticated(false);
    };

    return (
        <Flex direction="column" minHeight="100vh">
            {/* TopBar Component */}
            <Box bg="blue.800" color="white" py={2}>
                <Flex justify="space-between" align="center" maxW="container.xl" mx="auto" px={4}>
                    <Text fontSize="sm">{time}</Text>
                    <Flex>
                        <IconButton
                            aria-label="Facebook"
                            icon={<BsFacebook />}
                            as="a"
                            href="https://www.facebook.com/ahmad-981/"
                            target="_blank"
                            variant="link"
                            color="white"
                            mr={2}
                        />
                        <IconButton
                            aria-label="Github"
                            icon={<BsGithub />}
                            as="a"
                            href="https://github.com/Ahmad-981"
                            target="_blank"
                            variant="link"
                            color="white"
                            mr={2}
                        />
                        <IconButton
                            aria-label="Twitter"
                            icon={<BsTwitter />}
                            as="a"
                            href="https://twitter.com/Ahmad-981"
                            target="_blank"
                            variant="link"
                            color="white"
                            mr={2}
                        />
                    </Flex>
                </Flex>
            </Box>
            <MotionBox
                bg="blue.800"
                color="white"
                py={4}
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.5 }}
            >
                <Container maxW="container.xl">
                    <Flex align="center" justify="space-between">
                        <Heading size="lg">
                            <Link to="/">EasyWay Bank</Link>
                        </Heading>
                        <Flex>
                            {!isAuthenticated ? (
                                <Link to="/home">
                                    <Button colorScheme="teal" mr={4}>
                                        Home
                                    </Button>
                                </Link>
                            ) : (
                                <>
                                    <Link to="/dashboard">
                                        <Button colorScheme="teal" mr={4}>
                                            Dashboard
                                        </Button>
                                    </Link>
                                    <Button colorScheme="red" onClick={handleLogout}>
                                        Logout
                                    </Button>
                                </>
                            )}
                        </Flex>
                    </Flex>
                </Container>
            </MotionBox>

            {/* Main Landing Page Content */}
            <Flex 
                flex="1" 
                bgImage="https://img.freepik.com/free-vector/bank-service-concept-landing-page-template_23-2150470764.jpg?size=626&ext=jpg&ga=GA1.1.1287491362.1717396977&semt=ais_user" 
                bgSize="cover" 
                bgPosition="center" 
                color="white" 
                py={16}
            >
                <Container maxW="container.lg">
                    <Box bg="blue.700" p={6} rounded="lg" shadow="lg" mx="auto" maxW="3xl">
                        <Heading as="h3" size="lg" mb={4}>
                            Welcome to Your Financial Future
                        </Heading>
                        <Text mb={6}>
                            Join us today and experience the best in online banking. Whether you’re saving for your future or looking for smart investment options, we have you covered with unparalleled services.
                        </Text>
                        <Button colorScheme="teal" variant="solid">
                            Explore Our Services
                        </Button>
                    </Box>
                </Container>
            </Flex>

            {/* Footer Component */}
            <Box bg="blue.800" color="white" py={4}>
                <Container maxW="container.xl">
                    <Text textAlign="center" fontSize="sm">
                        &copy; {new Date().getFullYear()} Bank. Crafted with <span style={{ color: '#e53e3e' }}>❤</span> by{' '}
                        <Link 
                            href="https://github.com/Ahmad-981" 
                            isExternal
                            color="teal.300" 
                            fontWeight="bold" 
                            textDecoration="none"
                        >
                            Ahmad Irtza
                        </Link>
                    </Text>
                    <Box textAlign="center" mt={2}>
                        <Spinner color="teal.300" size="md" />
                    </Box>
                </Container>
            </Box>
        </Flex>
    );
}

export default LandingPage;
