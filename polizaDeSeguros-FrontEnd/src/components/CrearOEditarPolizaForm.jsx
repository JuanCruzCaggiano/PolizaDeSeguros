import React, { useState, useEffect } from "react";
import {
  AppBar,
  Toolbar,
  Typography,
  TextField,
  Button,
  Container,
  Box,
  MenuItem,
  Select,
  InputLabel,
  FormControl,
  Snackbar,
  Alert,
  CircularProgress,
  Backdrop,
  IconButton,
} from "@mui/material";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import ExitToAppIcon from "@mui/icons-material/ExitToApp";
import api from "../services/api";
import { NumericFormat } from "react-number-format";
import { useNavigate, useParams } from "react-router-dom";
import AuthService from "../services/AuthService";

const CreateOrEditPoliza = () => {
  const { id } = useParams();
  const [codigo, setCodigo] = useState("");
  const [descripcion, setDescripcion] = useState("");
  const [montoAsegurado, setMontoAsegurado] = useState("");
  const [fechaEmision, setFechaEmision] = useState("");
  const [fechaVencimiento, setFechaVencimiento] = useState("");
  const [estado, setEstado] = useState("");
  const [idTipoSeguro, setIdTipoSeguro] = useState("");
  const [idCliente, setIdCliente] = useState("");
  const [clientes, setClientes] = useState([]);
  const [tiposSeguro, setTiposSeguro] = useState([]);
  const [openSnackbar, setOpenSnackbar] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState("");
  const [snackbarSeverity, setSnackbarSeverity] = useState("success");
  const [loading, setLoading] = useState(false);
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

    const getClientes = async () => {
      try {
        const response = await api.get("/clientes/obtenerClientes", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setClientes(response.data);
      } catch (error) {
        console.error("Error al obtener los clientes:", error);
      }
    };

    const getTiposSeguros = async () => {
      try {
        const response = await api.get("/tipoSeguro/obtenerTiposSeguros", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setTiposSeguro(response.data);
      } catch (error) {
        console.error("Error al obtener los tipos de seguro:", error);
      }
    };

    getClientes();
    getTiposSeguros();

    if (id) {
      cargarPoliza(id);
    }
  }, [id, navigate]);

  const cargarPoliza = async (idPoliza) => {
    if (!token) {
      setSnackbarMessage(
        "No se encontró el token. Por favor inicie sesión nuevamente."
      );
      setSnackbarSeverity("error");
      setOpenSnackbar(true);
      return;
    }

    try {
      const response = await api.get(`/polizas/${idPoliza}/obtenerPoliza`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const poliza = response.data;

      const formatearFecha = (fecha) => {
        const date = new Date(fecha);
        return date.toISOString().split("T")[0];
      };

      setCodigo(poliza.codigo);
      setDescripcion(poliza.descripcion);
      setMontoAsegurado(poliza.montoAsegurado);
      setFechaEmision(formatearFecha(poliza.fechaEmision));
      setFechaVencimiento(formatearFecha(poliza.fechaVencimiento));
      setEstado(poliza.estado);
      setIdTipoSeguro(poliza.idTipoSeguro);
      setIdCliente(poliza.idCliente);
    } catch (error) {
      console.error("Error al cargar la póliza:", error);
      setSnackbarMessage("Error al cargar la póliza.");
      setSnackbarSeverity("error");
      setOpenSnackbar(true);
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);

    if (!token) {
      setSnackbarMessage(
        "No se encontró el token. Por favor inicie sesión nuevamente."
      );
      setSnackbarSeverity("error");
      setOpenSnackbar(true);
      setLoading(false);
      return;
    }

    const polizaData = {
      codigo,
      descripcion,
      montoAsegurado: parseFloat(montoAsegurado),
      fechaEmision,
      fechaVencimiento,
      estado,
      idTipoSeguro: parseInt(idTipoSeguro),
      idCliente: parseInt(idCliente),
    };

    try {
      if (id) {
        await api.put(`/polizas/${id}/actualizarPoliza`, polizaData, {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });
        setSnackbarMessage("Póliza actualizada con éxito!");
        setTimeout(() => {
          navigate("/obtenerPolizas");
        }, 2000);
      } else {
        await api.post("/polizas/crearPoliza", polizaData, {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });
        setSnackbarMessage("Póliza creada con éxito!");
      }
      setSnackbarSeverity("success");
      setOpenSnackbar(true);
      resetFormulario();
    } catch (error) {
      setSnackbarMessage("Error al procesar la póliza. Intenta nuevamente.");
      setSnackbarSeverity("error");
      setOpenSnackbar(true);
    } finally {
      setLoading(false);
    }
  };

  const resetFormulario = () => {
    setCodigo("");
    setDescripcion("");
    setMontoAsegurado("");
    setFechaEmision("");
    setFechaVencimiento("");
    setEstado("");
    setIdTipoSeguro("");
    setIdCliente("");
  };

  return (
    <div style={{ backgroundColor: "#dcdcdc", minHeight: "97vh" }}>
      <AppBar position="static" color="primary">
        <Toolbar>
          <IconButton
            edge="start"
            color="inherit"
            onClick={() => navigate(id ? "/obtenerPolizas" : "/home")}
            aria-label="volver"
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

      <Container maxWidth="sm" sx={{ mt: 4 }}>
        <Box
          sx={{
            backgroundColor: "#ffffff",
            padding: 3,
            borderRadius: 2,
            boxShadow: 3,
          }}
        >
          <Typography variant="h4" gutterBottom>
            {id ? "Editar Póliza" : "Crear Póliza"}
          </Typography>
          <Box
            component="form"
            onSubmit={handleSubmit}
            sx={{ display: "flex", flexDirection: "column", gap: 3 }}
          >
            <Box sx={{ display: "flex", flexWrap: "wrap", gap: 3 }}>
              <Box sx={{ flex: 1 }}>
                <NumericFormat
                  label="Código de Póliza"
                  value={codigo}
                  onValueChange={(values) => setCodigo(values.value)}
                  decimalScale={0}
                  allowNegative={false}
                  customInput={TextField}
                  fullWidth
                  required
                  InputProps={{ readOnly: !!id, disabled: !!id }}
                />
              </Box>
              <Box sx={{ flex: 1 }}>
                <TextField
                  label="Descripción"
                  value={descripcion}
                  onChange={(e) => setDescripcion(e.target.value)}
                  fullWidth
                  required
                />
              </Box>
            </Box>
            <Box sx={{ display: "flex", flexWrap: "wrap", gap: 3 }}>
              <Box sx={{ flex: 1 }}>
                <NumericFormat
                  label="Monto Asegurado"
                  value={montoAsegurado}
                  onValueChange={(values) => setMontoAsegurado(values.value)}
                  thousandSeparator="."
                  decimalSeparator=","
                  decimalScale={2}
                  fixedDecimalScale
                  allowNegative={false}
                  customInput={TextField}
                  fullWidth
                  required
                />
              </Box>
              <Box sx={{ flex: 1 }}>
                <TextField
                  label="Fecha de Emisión"
                  type="date"
                  value={fechaEmision}
                  onChange={(e) => setFechaEmision(e.target.value)}
                  fullWidth
                  InputLabelProps={{ shrink: true }}
                  required
                />
              </Box>
            </Box>
            <Box sx={{ display: "flex", flexWrap: "wrap", gap: 3 }}>
              <Box sx={{ flex: 1 }}>
                <TextField
                  label="Fecha de Vencimiento"
                  type="date"
                  value={fechaVencimiento}
                  onChange={(e) => setFechaVencimiento(e.target.value)}
                  fullWidth
                  InputLabelProps={{ shrink: true }}
                  required
                />
              </Box>
              <Box sx={{ flex: 1 }}>
                <FormControl fullWidth required>
                  <InputLabel>Estado</InputLabel>
                  <Select
                    label="Estado"
                    value={estado}
                    onChange={(e) => setEstado(e.target.value)}
                  >
                    <MenuItem value="VIGENTE">VIGENTE</MenuItem>
                    <MenuItem value="CANCELADA">CANCELADA</MenuItem>
                  </Select>
                </FormControl>
              </Box>
            </Box>
            <Box sx={{ display: "flex", flexWrap: "wrap", gap: 3 }}>
              <Box sx={{ flex: 1 }}>
                <FormControl fullWidth required>
                  <InputLabel>Tipo de Seguro</InputLabel>
                  <Select
                    label="Tipo de Seguro"
                    value={idTipoSeguro}
                    onChange={(e) => setIdTipoSeguro(e.target.value)}
                  >
                    {tiposSeguro.map((tipo) => (
                      <MenuItem key={tipo.id} value={tipo.id}>
                        {tipo.descripcion}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Box>
              <Box sx={{ flex: 1 }}>
                <FormControl fullWidth required>
                  <InputLabel>Cliente</InputLabel>
                  <Select
                    label="Cliente"
                    value={idCliente}
                    onChange={(e) => setIdCliente(e.target.value)}
                  >
                    {clientes.map((cliente) => (
                      <MenuItem key={cliente.id} value={cliente.id}>
                        {cliente.nombre} {cliente.apellido}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Box>
            </Box>
            <Box sx={{ display: "flex", justifyContent: "center" }}>
              <Button
                variant="contained"
                color="secondary"
                type="submit"
                sx={{ width: "100%" }}
              >
                {id ? "Actualizar Póliza" : "Crear Póliza"}
              </Button>
            </Box>
          </Box>
        </Box>
      </Container>

      <Snackbar
        open={openSnackbar}
        autoHideDuration={6000}
        onClose={() => setOpenSnackbar(false)}
      >
        <Alert
          onClose={() => setOpenSnackbar(false)}
          severity={snackbarSeverity}
          sx={{ width: "100%" }}
        >
          {snackbarMessage}
        </Alert>
      </Snackbar>

      <Backdrop
        sx={{ color: "#fff", zIndex: (theme) => theme.zIndex.drawer + 1 }}
        open={loading}
      >
        <CircularProgress color="inherit" />
      </Backdrop>
    </div>
  );
};

export default CreateOrEditPoliza;
