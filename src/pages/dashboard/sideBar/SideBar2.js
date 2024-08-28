import React, { useState } from "react";
import { FaUserAlt, FaMoneyBillAlt } from "react-icons/fa";
import {
  Box,
  VStack,
  HStack,
  IconButton,
  Text,
  useColorModeValue,
  Collapse,
} from "@chakra-ui/react";
import {
  FiArrowLeftCircle,
  FiArrowRightCircle,
} from "react-icons/fi";
import { MdDashboard } from "react-icons/md";
import { Link } from "react-router-dom";

function SideBar2({ rtl }) {
  const [menuCollapse, setMenuCollapse] = useState(false);

  const menuIconClick = () => {
    setMenuCollapse(!menuCollapse);
  };

  const bgColor = useColorModeValue("white", "gray.800");
  const headerBgColor = useColorModeValue("gray.100", "gray.700");

  return (
    <Box className="SideBar2" w={menuCollapse ? "20" : "64"} pos="fixed" h="full" bg={bgColor}>
      <Box p="4" bg={headerBgColor} className="shadow">
        <HStack justifyContent="space-between" alignItems="center">
          {!menuCollapse && <Text fontSize="lg" fontWeight="bold">Dashboard</Text>}
          <IconButton
            aria-label="Toggle Menu"
            icon={menuCollapse ? <FiArrowRightCircle /> : <FiArrowLeftCircle />}
            onClick={menuIconClick}
          />
        </HStack>
      </Box>

      <VStack spacing={4} align="stretch" p={4}>
        <Collapse in={!menuCollapse} animateOpacity>
          <Box
            p={2}
            display="flex"
            alignItems="center"
            _hover={{ bg: "gray.200", cursor: "pointer" }}
            borderRadius="md"
          >
            <IconButton
              aria-label="Dashboard"
              icon={<MdDashboard />}
              colorScheme="teal"
              variant="ghost"
            />
            <Text ml={4}>
              <Link to="/dashboard">Dashboard</Link>
            </Text>
          </Box>

          <Box
            p={2}
            display="flex"
            alignItems="center"
            _hover={{ bg: "gray.200", cursor: "pointer" }}
            borderRadius="md"
          >
            <IconButton
              aria-label="Accounts"
              icon={<FaUserAlt />}
              colorScheme="teal"
              variant="ghost"
            />
            <Text ml={4}>
              <Link to="/dashboard/viewAccounts">Accounts</Link>
            </Text>
          </Box>

          <Box
            p={2}
            display="flex"
            alignItems="center"
            _hover={{ bg: "gray.200", cursor: "pointer" }}
            borderRadius="md"
          >
            <IconButton
              aria-label="Transactions"
              icon={<FaMoneyBillAlt />}
              colorScheme="teal"
              variant="ghost"
            />
            <Text ml={4}>
              <Link to="/dashboard/viewTransactions">Transactions</Link>
            </Text>
          </Box>
        </Collapse>
      </VStack>
    </Box>
  );
}

export default SideBar2;
