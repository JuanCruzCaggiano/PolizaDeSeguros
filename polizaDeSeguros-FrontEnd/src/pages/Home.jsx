import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import { Container, Typography, Box } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/AuthService';

const Home = () => {
  const [username, setUsername] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    if (!AuthService.isAuthenticated()) {
      navigate('/'); 
    } else {
      const user = AuthService.getNombre() + ' ' + AuthService.getApellido();
      setUsername(user);
    }
  }, [navigate]);

  return (
    <div>
      <Navbar username={username} />
      <Container>
        <Box textAlign="center" mt={4}>
          <Typography variant="h4">Bienvenido a la gestión de pólizas</Typography>
          <Typography variant="subtitle1" mt={2}>
            Selecciona una opción en el menú para empezar
          </Typography>
        </Box>
      </Container>
    </div>
  );
};

export default Home;
