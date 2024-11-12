import React, { useEffect, useState } from 'react';
import { DataGrid } from '@mui/x-data-grid';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ExitToAppIcon from '@mui/icons-material/ExitToApp';
import { Container, Typography, AppBar, Toolbar, IconButton, Box, Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Snackbar, Alert, CircularProgress, Backdrop, TextField } from '@mui/material';
import api from '../services/api';
import AuthService from '../services/AuthService';

const ClientesList = () => {
  const [clientes, setClientes] = useState([]);
  const token = AuthService.getToken();
  const username = AuthService.getNombre() + ' ' + AuthService.getApellido();

  const handleLogout = () => {
    localStorage.removeItem('username');
    localStorage.removeItem('token');
    navigate('/');
  };

  useEffect(() => {
    const obtenerClientes = async () => {
        try {
          const response = await api.get('/clientes/obtenerClientes', {
            headers: { 'Authorization': `Bearer ${token}` },
          });
          setClientes(response.data);
        } catch (error) {
          console.error('Error al obtener los clientes:', error);
        }
      };

      obtenerClientes();
  }, []);

  const columns = [
    { field: 'nombreCliente', headerName: 'Cliente', width: 200, 
      renderCell: (params) => {
        const { nombre, apellido } = params.row; 
        return `${nombre} ${apellido}`;
      }
    },  
    { field: 'nroDocumento', headerName: 'Nro. de Documento', width: 200 },
    { field: 'fechaNacimiento', headerName: 'Fecha de Nacimiento', width: 200 },
    { field: 'telefono', headerName: 'Teléfono', width: 150 },
    { field: 'email', headerName: 'Email', width: 250 },
    { field: 'direccion', headerName: 'Dirección', width: 150 },
    { field: 'estado', headerName: 'Estado', width: 150 },
  ];

  return (
    <Box>
      <AppBar position="static" sx={{ backgroundColor: '#1976d2' }}>
        <Toolbar>
          <IconButton edge="start" color="inherit" onClick={() => window.location.href = '/home'}>
            <ArrowBackIcon />
          </IconButton>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            Volver
          </Typography>
          <Typography variant="body1">
            {username}
          </Typography>
          <IconButton color="inherit" onClick={handleLogout}>
            <ExitToAppIcon />
          </IconButton>
        </Toolbar>
      </AppBar>

    <Container maxWidth="lg" sx={{ mt: 4 }}>
      <Typography variant="h4" gutterBottom>Lista de Clientes</Typography>
        <div style={{ height: 400, width: '100%' }}>
          <DataGrid
            rows={clientes}
            columns={columns}
            pageSize={5}
            rowsPerPageOptions={[5, 10, 20]}
            disableSelectionOnClick
          />
        </div>
    </Container>
    </Box>
  );
};

export default ClientesList;
