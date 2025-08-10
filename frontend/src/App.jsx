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
import { History } from './pages/History';
import { Users } from './pages/Users';
import { News } from './pages/News';
import { Budget } from './pages/Budget';
import { Trips } from './pages/Trips';
import { TripBudget } from './pages/TripBudget';
import { Team } from './pages/Team';
import Profiles from './pages/Profiles';

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
              path="/sales/:gameId"
              element={
                <PrivateRoute>
                  <Layout>
                    <NewSale />
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
              path="/trips/:tripId/budget"
              element={
                <PrivateRoute>
                  <Layout>
                    <TripBudget />
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
              path="/profiles"
              element={
                <PrivateRoute requireAdmin={true}>
                  <Layout>
                    <Profiles />
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
            <Route
              path="/team"
              element={
                <PrivateRoute requireAdmin={true}>
                  <Layout>
                    <Team />
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