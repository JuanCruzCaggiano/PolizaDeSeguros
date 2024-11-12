import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import LoginPage from './pages/Login';
import Home from './pages/Home';
import CrearPoliza from './components/CrearOEditarPolizaForm'
import ObtenerPolizas from './components/ObtenerPolizasForm'
import ListarClientes from './components/ListarClientes'

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/home" element={<Home />} />
        <Route path="/crearPoliza" element={<CrearPoliza />} />
        <Route path="/crearPoliza/:id" element={<CrearPoliza />} />
        <Route path="/obtenerPolizas" element={<ObtenerPolizas/>} />
        <Route path="/listarClientes" element={<ListarClientes/>} />
      </Routes>
    </Router>
  );
}

export default App;