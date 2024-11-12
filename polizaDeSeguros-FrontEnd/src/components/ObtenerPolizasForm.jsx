import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Container, Typography, AppBar, Toolbar, IconButton, Box, Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, Snackbar, Alert, CircularProgress, Backdrop, TextField } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ExitToAppIcon from '@mui/icons-material/ExitToApp';
import { DataGrid } from '@mui/x-data-grid';
import api from '../services/api';
import AuthService from '../services/AuthService';

const ListPolizas = () => {
  const [polizas, setPolizas] = useState([]);
  const [filteredPolizas, setFilteredPolizas] = useState([]);
  const [openConfirmDialog, setOpenConfirmDialog] = useState(false);
  const [polizaToDelete, setPolizaToDelete] = useState(null);
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [snackbarSeverity, setSnackbarSeverity] = useState('success');
  const [loading, setLoading] = useState(false);
  const [searchParams, setSearchParams] = useState({
    codigo: '',
    estado: '',
    descripcion: '',
    documentoCliente: '',
  });
  const navigate = useNavigate();
  const token = AuthService.getToken();
  const username = AuthService.getNombre() + ' ' + AuthService.getApellido();

  const handleLogout = () => {
    localStorage.removeItem('username');
    localStorage.removeItem('token');
    navigate('/');
  };

  useEffect(() => {
    if (!AuthService.isAuthenticated()) {
      navigate('/'); 
    }
    obtenerPolizas();
  }, [token]);

  useEffect(() => {
    const filtered = polizas.filter((poliza) => {
      return (
        (searchParams.codigo === '' || poliza.codigo.toString().includes(searchParams.codigo)) &&
        (searchParams.estado === '' || poliza.estado.includes(searchParams.estado)) &&
        (searchParams.descripcion === '' || poliza.descripcion.toLowerCase().includes(searchParams.descripcion.toLowerCase())) &&
        (searchParams.documentoCliente === '' || poliza.nroDocumentoCliente.includes(searchParams.documentoCliente))
      );
    });
    setFilteredPolizas(filtered);
  }, [searchParams, polizas]);

  const obtenerPolizas = async () => {
    try {
      const response = await api.get('/polizas/obtenerPolizas', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      setPolizas(response.data);
      setFilteredPolizas(response.data);
    } catch (error) {
      console.error('Error al obtener las pólizas:', error);
    }
  };

  const handleDeleteClick = (polizaId) => {
    setPolizaToDelete(polizaId);
    setOpenConfirmDialog(true);
  };

  const handleConfirmDelete = async () => {
    setOpenConfirmDialog(false);
    setLoading(true);
    try {
      await api.delete(`/polizas/${polizaToDelete}/eliminarPoliza`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      setSnackbarMessage('Póliza eliminada con éxito!');
      setOpenSnackbar(true);
      obtenerPolizas();
    } catch (error) {
      setSnackbarMessage('Error eliminando la póliza. Intenta nuevamente.');
      setSnackbarSeverity('error');
      setOpenSnackbar(true);
    } finally {
      setLoading(false);
    }
  };

  const handleCloseDialog = () => {
    setOpenConfirmDialog(false);
  };

  const handleCloseSnackbar = () => {
    setOpenSnackbar(false);
  };

  const handleEditClick = (polizaId) => {
    navigate(`/crearPoliza/${polizaId}`);
  };

  const actualizarVencimientos = async (polizaId) => {
    setLoading(true);
    try {
      const response = await api.put(`/polizas/${polizaId}/actualizarEstadoPoliza`, {}, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      setSnackbarMessage(response.data);
      setSnackbarSeverity('success');
      setOpenSnackbar(true);
      obtenerPolizas();
    } catch (error) {
      setSnackbarMessage('Error actualizando vencimiento de la póliza.');
      setSnackbarSeverity('error');
      setOpenSnackbar(true);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchChange = (e) => {
    const { name, value } = e.target;
    setSearchParams(prevState => ({
      ...prevState,
      [name]: value,
    }));
  };

  const columns = [
    { field: 'codigo', headerName: 'Código', width: 150 },
    { field: 'descripcion', headerName: 'Descripción', width: 200 },
    { field: 'montoAsegurado', headerName: 'Monto Asegurado', width: 180 },
    { field: 'fechaEmision', headerName: 'Fecha de Emisión', width: 180},
    { field: 'fechaVencimiento', headerName: 'Fecha de Vencimiento', width: 180},
    { field: 'estado', headerName: 'Estado', width: 120 },
    { field: 'nombreCliente', headerName: 'Cliente', width: 200, 
      renderCell: (params) => {
        const { nombreCliente, apellidoCliente } = params.row; 
        return `${nombreCliente} ${apellidoCliente}`;
      }
    },    
    { field: 'emailCliente', headerName: 'Email Cliente', width: 200 },
    { field: 'nroDocumentoCliente', headerName: 'Documento Cliente', width: 180 },
    { field: 'estadoCliente', headerName: 'Estado Cliente', width: 180 },
    { field: 'descripcionTipoSeguro', headerName: 'Tipo de Seguro', width: 180 },
    {
      field: 'acciones',
      headerName: 'Acciones',
      width: 500,
      renderCell: (params) => (
        <Box>
          <Button variant="contained" color="secondary" onClick={() => handleEditClick(params.id)} disabled={params.row.estado === 'VENCIDA' || params.row.estado === 'CANCELADA'}>
            Editar
          </Button>
          <Button variant="contained" color="secondary" onClick={() => handleDeleteClick(params.id)} disabled={params.row.estado === 'VENCIDA' || params.row.estado === 'CANCELADA'} sx={{ ml: 1 }}>
            Eliminar
          </Button>
          <Button variant="contained" color="success" onClick={() => actualizarVencimientos(params.id)} disabled={params.row.estado === 'VENCIDA' || params.row.estado === 'CANCELADA'} sx={{ ml: 1 }}>
            Actualizar Vencimiento
          </Button>
        </Box>
      )
    }
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
        <Typography variant="h4" gutterBottom>
          Lista de Pólizas
        </Typography>

        <Box mb={3}>
          <TextField label="Código" name="codigo" value={searchParams.codigo} onChange={handleSearchChange} sx={{ mr: 2 }} />
          <TextField label="Estado" name="estado" value={searchParams.estado.toUpperCase()} onChange={handleSearchChange} sx={{ mr: 2 }}/>
          <TextField label="Descripción" name="descripcion" value={searchParams.descripcion} onChange={handleSearchChange} sx={{ mr: 2 }} />
          <TextField label="Documento Cliente" name="documentoCliente" value={searchParams.documentoCliente} onChange={handleSearchChange} sx={{ mr: 2 }} />
        </Box>

        <div style={{ height: 400, width: '100%', overflowX: 'auto' }}>
          <DataGrid
            rows={filteredPolizas}
            columns={columns}
            pageSize={5}
            disableSelectionOnClick
            columnPinning={{ left: ['codigo'] }}
          />
        </div>

        <Dialog open={openConfirmDialog} onClose={handleCloseDialog}>
          <DialogTitle>Confirmación</DialogTitle>
          <DialogContent>
            <DialogContentText>
              ¿Está seguro de que desea eliminar esta póliza?
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseDialog} color="primary">
              Cancelar
            </Button>
            <Button onClick={handleConfirmDelete} color="secondary">
              Eliminar
            </Button>
          </DialogActions>
        </Dialog>

        <Backdrop sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }} open={loading}>
          <CircularProgress color="inherit" />
        </Backdrop>

        <Snackbar open={openSnackbar} autoHideDuration={6000} onClose={handleCloseSnackbar}>
          <Alert onClose={handleCloseSnackbar} severity={snackbarSeverity} sx={{ width: '100%' }}>
            {snackbarMessage}
          </Alert>
        </Snackbar>
      </Container>
    </Box>
  );
};

export default ListPolizas;