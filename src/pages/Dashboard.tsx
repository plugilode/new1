import React from 'react';
import { CircuitBoard } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

const Dashboard = () => {
  const { user, logout } = useAuth();

  return (
    <div className="min-h-screen bg-gray-900">
      <nav className="bg-black/90 border-b border-gray-800 px-4 py-3">
        <div className="flex items-center justify-between max-w-7xl mx-auto">
          <div className="flex items-center space-x-3">
            <CircuitBoard className="w-8 h-8 text-blue-500" />
            <span className="text-xl font-bold text-white">Plugilo AI</span>
          </div>
          <div className="flex items-center space-x-4">
            <span className="text-gray-400">Welcome, {user?.name}</span>
            <button 
              onClick={logout}
              className="text-gray-400 hover:text-white transition-colors"
            >
              Sign Out
            </button>
          </div>
        </div>
      </nav>
      
      <main className="max-w-7xl mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold text-white mb-8">Welcome to Plugilo AI Dashboard</h1>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {/* Dashboard content will go here */}
        </div>
      </main>
    </div>
  );
};

export default Dashboard;