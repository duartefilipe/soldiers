import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './context/AuthContext';
import { PrivateRoute } from './components/PrivateRoute';
import { Layout } from './components/Layout';
import { Home } from './pages/Home';
import { Login } from './pages/Login';
import { Dashboard } from './pages/Dashboard';
import { Products } from './pages/Products';
import { Games } from './pages/Games';
import { NewSale } from './pages/NewSale';
import { GameSchedule } from './pages/GameSchedule';
import { GameSales } from './pages/GameSales';
import { History } from './pages/History';
import { Users } from './pages/Users';
import { News } from './pages/News';
import { Budget } from './pages/Budget';
import { Trips } from './pages/Trips';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Toaster position="top-right" />
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route
              path="/dashboard"
              element={
                <PrivateRoute>
                  <Layout>
                    <Dashboard />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/products"
              element={
                <PrivateRoute>
                  <Layout>
                    <Products />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/games"
              element={
                <PrivateRoute>
                  <Layout>
                    <Games />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/sales"
              element={
                <PrivateRoute>
                  <Layout>
                    <NewSale />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/schedule"
              element={
                <PrivateRoute>
                  <Layout>
                    <GameSchedule />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/game/:gameId/sales"
              element={
                <PrivateRoute>
                  <Layout>
                    <GameSales />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/history"
              element={
                <PrivateRoute>
                  <Layout>
                    <History />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/budget"
              element={
                <PrivateRoute>
                  <Layout>
                    <Budget />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/trips"
              element={
                <PrivateRoute>
                  <Layout>
                    <Trips />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/users"
              element={
                <PrivateRoute requireAdmin={true}>
                  <Layout>
                    <Users />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/news"
              element={
                <PrivateRoute requireAdmin={true}>
                  <Layout>
                    <News />
                  </Layout>
                </PrivateRoute>
              }
            />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App; 