import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import {Container,Typography,AppBar,Toolbar,Tooltip,IconButton,Box,Button,Dialog,DialogActions,DialogContent,DialogContentText,DialogTitle,Snackbar,Alert,CircularProgress,Backdrop,TextField} from "@mui/material";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import ExitToAppIcon from "@mui/icons-material/ExitToApp";
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import UpdateIcon from '@mui/icons-material/Update';
import { DataGrid } from "@mui/x-data-grid";
import api from "../services/api";
import AuthService from "../services/AuthService";

const ListPolizas = () => {
  const [polizas, setPolizas] = useState([]);
  const [filteredPolizas, setFilteredPolizas] = useState([]);
  const [openConfirmDialog, setOpenConfirmDialog] = useState(false);
  const [polizaToDelete, setPolizaToDelete] = useState(null);
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState("");
  const [snackbarSeverity, setSnackbarSeverity] = useState("success");
  const [loading, setLoading] = useState(false);
  const [searchParams, setSearchParams] = useState({
    codigo: "",
    estado: "",
    descripcion: "",
    documentoCliente: "",
  });
  const navigate = useNavigate();
  const token = AuthService.getToken();
  const username = AuthService.getNombre() + " " + AuthService.getApellido();

  const handleLogout = () => {
    localStorage.removeItem("username");
    localStorage.removeItem("token");
    navigate("/");
  };

  useEffect(() => {
    if (!AuthService.isAuthenticated()) {
      navigate("/");
    }
    getPolizas();
  }, [token]);

  useEffect(() => {
    const filtered = polizas.filter((poliza) => {
      return (
        (searchParams.codigo === "" ||
          poliza.codigo.toString().includes(searchParams.codigo)) &&
        (searchParams.estado === "" ||
          poliza.estado.includes(searchParams.estado)) &&
        (searchParams.descripcion === "" ||
          poliza.descripcion
            .toLowerCase()
            .includes(searchParams.descripcion.toLowerCase())) &&
        (searchParams.documentoCliente === "" ||
          poliza.nroDocumentoCliente.includes(searchParams.documentoCliente))
      );
    });
    setFilteredPolizas(filtered);
  }, [searchParams, polizas]);

  const getPolizas = async () => {
    try {
      const response = await api.get("/polizas/obtenerPolizas", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setPolizas(response.data);
      setFilteredPolizas(response.data);
    } catch (error) {
      console.error("Error al obtener las pólizas:", error);
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
          Authorization: `Bearer ${token}`,
        },
      });
      setSnackbarMessage("Póliza eliminada con éxito!");
      setOpenSnackbar(true);
      getPolizas();
    } catch (error) {
      setSnackbarMessage("Error eliminando la póliza. Intenta nuevamente.");
      setSnackbarSeverity("error");
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

  const updateDates = async (polizaId) => {
    setLoading(true);
    try {
      const response = await api.put(
        `/polizas/${polizaId}/actualizarEstadoPoliza`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setSnackbarMessage(response.data);
      setSnackbarSeverity("success");
      setOpenSnackbar(true);
      getPolizas();
    } catch (error) {
      setSnackbarMessage("Error actualizando vencimiento de la póliza.");
      setSnackbarSeverity("error");
      setOpenSnackbar(true);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchChange = (e) => {
    const { name, value } = e.target;
    setSearchParams((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  const columns = [
    { field: "acciones", headerName: "Acciones", width: 160, pinned: "left",
      renderCell: (params) => (
        <Box>
          <Tooltip title="Editar" arrow>
            <IconButton
              variant="contained"
              color="secondary"
              onClick={() => handleEditClick(params.id)}
              disabled={
                params.row.estado === "VENCIDA" ||
                params.row.estado === "CANCELADA"
              }
            >
              <EditIcon />
            </IconButton>
          </Tooltip>
    
          <Tooltip title="Eliminar" arrow>
            <IconButton
              variant="contained"
              color="secondary"
              onClick={() => handleDeleteClick(params.id)}
              disabled={
                params.row.estado === "VENCIDA" ||
                params.row.estado === "CANCELADA"
              }
              sx={{ ml: 1 }}
            >
              <DeleteIcon />
            </IconButton>
          </Tooltip>
    
          <Tooltip title="Actualizar Vencimiento" arrow>
            <IconButton
              variant="contained"
              color="success"
              onClick={() => updateDates(params.id)}
              disabled={
                params.row.estado === "VENCIDA" ||
                params.row.estado === "CANCELADA"
              }
              sx={{ ml: 1 }}
            >
              <UpdateIcon />
            </IconButton>
          </Tooltip>
        </Box>
      ),
    },
    { field: "codigo", headerName: "Código", width: 100 },
    { field: "descripcion", headerName: "Descripción", width: 200 },
    { field: "montoAsegurado", headerName: "Monto Asegurado", width: 180 },
    { field: "fechaEmision", headerName: "Fecha de Emisión", width: 180,
      renderCell: (params) => {
        const fecha = params.value;
        const partesFecha = fecha.split('-');
        return `${partesFecha[2]}/${partesFecha[1]}/${partesFecha[0]}`;
      } 
    },
    { field: "fechaVencimiento", headerName: "Fecha de Vencimiento", width: 180,
      renderCell: (params) => {
        const fecha = params.value;
        const partesFecha = fecha.split('-');
        return `${partesFecha[2]}/${partesFecha[1]}/${partesFecha[0]}`;
      } 
     },
    { field: "estado", headerName: "Estado", width: 120 },
    { field: "nombreCliente", headerName: "Cliente", width: 200,
      renderCell: (params) => {
        const { nombreCliente, apellidoCliente } = params.row;
        return `${nombreCliente} ${apellidoCliente}`;
      },
    },
    { field: "emailCliente", headerName: "Email Cliente", width: 250 },
    { field: "nroDocumentoCliente", headerName: "Documento Cliente", width: 180 },
    { field: "estadoCliente", headerName: "Estado Cliente", width: 180 },
    { field: "descripcionTipoSeguro", headerName: "Tipo de Seguro", width: 180 },
  ];

  // Traducciones personalizadas para DataGrid
  const localeText = {
    noRowsLabel: 'No hay datos para mostrar',
    columnMenuUnsort: 'Quitar orden',
    columnMenuSortAsc: 'Ordenar ascendente',
    columnMenuSortDesc: 'Ordenar descendente',
    columnMenuFilter: 'Filtrar',
    columnMenuHideColumn: 'Ocultar columna',
    columnMenuShowColumns: 'Mostrar columnas',
    rowsPerPage: 'Filas por página',
    footerRowSelected: (count) => count !== 1 ? `${count.toLocaleString()} filas seleccionadas` : `${count.toLocaleString()} fila seleccionada`,
    MuiTablePagination: {
      labelRowsPerPage: 'Filas por página',
    },
  };

  return (
    <div style={{ backgroundColor: "#dcdcdc", minHeight: "97vh"}}>
    <Box>
      <AppBar position="static" sx={{ backgroundColor: "#1976d2" }}>
        <Toolbar>
          <IconButton
            edge="start"
            color="inherit"
            onClick={() => (window.location.href = "/home")}
          >
            <ArrowBackIcon />
          </IconButton>
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            Volver
          </Typography>
          <Typography variant="body1">{username}</Typography>
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
          <TextField
            label="Código"
            name="codigo"
            value={searchParams.codigo}
            onChange={handleSearchChange}
            sx={{ mr: 2, backgroundColor: "white" }}
          />
          <TextField
            label="Estado"
            name="estado"
            value={searchParams.estado.toUpperCase()}
            onChange={handleSearchChange}
            sx={{ mr: 2, backgroundColor: "white" }}
          />
          <TextField
            label="Descripción"
            name="descripcion"
            value={searchParams.descripcion}
            onChange={handleSearchChange}
            sx={{ mr: 2, backgroundColor: "white" }}
          />
          <TextField
            label="Documento Cliente"
            name="documentoCliente"
            value={searchParams.documentoCliente}
            onChange={handleSearchChange}
            sx={{ mr: 2, backgroundColor: "white" }}
          />
        </Box>

        <div style={{ height: 400, width: "100%", overflowX: "auto" }}>
          <DataGrid
            rows={filteredPolizas}
            columns={columns}
            pageSize={5}
            sx={{
              backgroundColor: "#ffffff",
              "& .MuiDataGrid-columnHeaderTitle": {
                fontWeight: "bold",
              },
            }}
            disableSelectionOnClick
            localeText={localeText}
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

        <Backdrop
          sx={{ color: "#fff", zIndex: (theme) => theme.zIndex.drawer + 1 }}
          open={loading}
        >
          <CircularProgress color="inherit" />
        </Backdrop>

        <Snackbar
          open={openSnackbar}
          autoHideDuration={6000}
          onClose={handleCloseSnackbar}
        >
          <Alert
            onClose={handleCloseSnackbar}
            severity={snackbarSeverity}
            sx={{ width: "100%" }}
          >
            {snackbarMessage}
          </Alert>
        </Snackbar>
      </Container>
    </Box>
    </div>
  );
};

export default ListPolizas;