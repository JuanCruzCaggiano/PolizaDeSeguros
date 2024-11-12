import React from 'react';
import { AppBar, Toolbar, Typography, Button, Box, IconButton } from '@mui/material';
import ExitToAppIcon from '@mui/icons-material/ExitToApp';
import { Link } from 'react-router-dom';
import { useNavigate } from 'react-router-dom';

const Navbar = ({ username }) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('username');
    localStorage.removeItem('token');
    
    navigate('/');
  };

  return (
    <AppBar position="static">
      <Toolbar>
        <Box display="flex" flexGrow={1}>
          <Button color="inherit" component={Link} to="/crearPoliza">Crear Póliza</Button>
          <Button color="inherit" component={Link} to="/obtenerPolizas">Listar Pólizas</Button>
          <Button color="inherit" component={Link} to="/listarClientes">Listar Clientes</Button>
        </Box>
        <Typography variant="body1">{username}</Typography>
        <IconButton color="inherit" onClick={handleLogout}>
          <ExitToAppIcon />
        </IconButton>
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;
