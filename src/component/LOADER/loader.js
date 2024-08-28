import React from 'react';
import { Box} from '@chakra-ui/react';
import { ThreeCircles } from 'react-loader-spinner';

export default function Loader() {
    return (
        <Box
            position="absolute"
            top="0"
            left="0"
            width="100%"
            height="100vh"
            display="flex"
            alignItems="center"
            justifyContent="center"
            backgroundColor="rgba(0,0,0,0.97)"
            zIndex="10000"
        >
            <ThreeCircles
                color="white"
                height={120}
                width={120}
                ariaLabel="three-circles-rotating"
            />
        </Box>
    );
}
