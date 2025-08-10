import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { 
  BarChart3, 
  Package, 
  Users, 
  Calendar, 
  ShoppingCart, 
  History,
  LogOut,
  Menu,
  X,
  User,
  Newspaper,
  DollarSign,
  MapPin,
  Shield
} from 'lucide-react';

const menuItems = [
  { path: '/dashboard', icon: BarChart3, label: 'Dashboard' },
  { path: '/products', icon: Package, label: 'Produtos' },
  { path: '/games', icon: Calendar, label: 'Jogos' },
  { path: '/sales', icon: ShoppingCart, label: 'Vendas' },
  { path: '/history', icon: History, label: 'Histórico' },
  { path: '/budget', icon: DollarSign, label: 'Orçamento' },
  { path: '/trips', icon: MapPin, label: 'Viagens' },
  { path: '/team', icon: Shield, label: 'Time', adminOnly: true },
  { path: '/news', icon: Newspaper, label: 'Notícias', adminOnly: true },
  { path: '/users', icon: Users, label: 'Usuários', adminOnly: true },
];

export function Layout({ children }) {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const { user, signOut, isAdmin } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const handleSignOut = () => {
    signOut();
    navigate('/login');
  };

  const filteredMenuItems = menuItems.filter(item => 
    !item.adminOnly || isAdmin()
  );

  return (
    <div className="min-h-screen bg-gray-50 flex">
      {/* Sidebar */}
      <div className={`fixed inset-y-0 left-0 z-50 w-64 bg-white shadow-lg transform transition-transform duration-300 ease-in-out ${
        sidebarOpen ? 'translate-x-0' : '-translate-x-full'
      } lg:relative lg:translate-x-0 lg:inset-0`}>
        <div className="flex items-center justify-between h-16 px-6 border-b">
          <div className="flex items-center">
            <div className="w-8 h-8 bg-primary-600 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-sm">S</span>
            </div>
            <span className="ml-3 text-lg font-semibold text-gray-900">Soldiers</span>
          </div>
          <button
            onClick={() => setSidebarOpen(false)}
            className="lg:hidden p-2 rounded-md text-gray-400 hover:text-gray-600"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        <nav className="mt-6 px-3">
          <div className="space-y-1">
            {filteredMenuItems.map((item) => {
              const Icon = item.icon;
              const isActive = location.pathname === item.path;
              
              return (
                <Link
                  key={item.path}
                  to={item.path}
                  className={`flex items-center px-3 py-2 text-sm font-medium rounded-md transition-colors ${
                    isActive
                      ? 'bg-primary-100 text-primary-700'
                      : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                  }`}
                  onClick={() => setSidebarOpen(false)}
                >
                  <Icon className="mr-3 h-5 w-5" />
                  {item.label}
                </Link>
              );
            })}
          </div>
        </nav>

        {/* User info */}
        <div className="absolute bottom-0 left-0 right-0 p-4 border-t">
          <div className="flex items-center">
            <div className="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
              <User className="w-4 h-4 text-primary-600" />
            </div>
            <div className="ml-3 flex-1">
              <p className="text-sm font-medium text-gray-900">{user?.name}</p>
              <p className="text-xs text-gray-500">{user?.role}</p>
            </div>
            <button
              onClick={handleSignOut}
              className="p-2 text-gray-400 hover:text-gray-600"
              title="Sair"
            >
              <LogOut className="w-4 h-4" />
            </button>
          </div>
        </div>
      </div>

      {/* Main content */}
      <div className="flex-1 flex flex-col min-w-0">
        {/* Top bar */}
        <div className="sticky top-0 z-40 bg-white shadow-sm border-b">
          <div className="flex items-center justify-between h-16 px-4 sm:px-6 lg:px-8">
            <button
              onClick={() => setSidebarOpen(true)}
              className="lg:hidden p-2 rounded-md text-gray-400 hover:text-gray-600"
            >
              <Menu className="w-5 h-5" />
            </button>
            
            <div className="flex-1 lg:hidden"></div>
            
            <div className="flex items-center space-x-4">
              <span className="text-sm text-gray-500">
                Bem-vindo, {user?.name}
              </span>
            </div>
          </div>
        </div>

        {/* Page content */}
        <main className="flex-1 p-4 sm:p-6 lg:p-8">
          {children}
        </main>
      </div>

      {/* Overlay */}
      {sidebarOpen && (
        <div
          className="fixed inset-0 z-40 bg-gray-600 bg-opacity-75 lg:hidden"
          onClick={() => setSidebarOpen(false)}
        />
      )}
    </div>
  );
} 