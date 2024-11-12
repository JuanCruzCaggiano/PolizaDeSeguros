import React, { useState } from 'react';
import { Container, TextField, Button, Typography, Box } from '@mui/material';
import api from '../services/api';
import AuthService from '../services/AuthService';

const LoginPage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await api.post('/auth/login', { username, password });
      AuthService.logout(); 
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('username', username);
      localStorage.setItem('nombre', response.data.nombre);
      localStorage.setItem('apellido', response.data.apellido);
      window.location.href = '/home'; 
    } catch (error) {
      console.error('Error al iniciar sesión:', error);
      setError('Credenciales incorrectas');
    }
  };

  return (
    <Container maxWidth="xs">
      <Box display="flex" flexDirection="column" alignItems="center" mt={8}>
        <Typography variant="h4" gutterBottom>Login</Typography>
        {error && <Typography color="error">{error}</Typography>}
        <form onSubmit={handleLogin} style={{ width: '100%' }}>
          <TextField
            label="Usuario"
            fullWidth
            margin="normal"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <TextField
            label="Contraseña"
            type="password"
            fullWidth
            margin="normal"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            color="secondary"
            sx={{ mt: 3 }}
          >
            Iniciar Sesión
          </Button>
        </form>
      </Box>
    </Container>
  );
};

export default LoginPage;