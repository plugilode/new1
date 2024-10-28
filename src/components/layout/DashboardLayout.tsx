import React, { useEffect, useState } from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';

const DashboardLayout = () => {
  const [loading, setLoading] = useState(true);
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setProgress(prev => {
        if (prev >= 100) {
          clearInterval(interval);
          setTimeout(() => setLoading(false), 500);
          return 100;
        }
        return prev + 5;
      });
    }, 100);

    return () => clearInterval(interval);
  }, []);

  if (loading) {
    return (
      <div className="flex h-screen bg-gray-900 items-center justify-center">
        <div className="w-96 space-y-4">
          <div className="text-center text-white mb-8">
            <h2 className="text-2xl font-bold mb-2">Initializing Dashboard</h2>
            <p className="text-gray-400">Loading components: {progress}%</p>
          </div>
          <div className="h-2 bg-gray-800 rounded-full overflow-hidden">
            <div 
              className="h-full bg-blue-500 transition-all duration-300 ease-out"
              style={{ width: `${progress}%` }}
            />
          </div>
          <div className="text-gray-500 text-sm text-center animate-pulse">
            {progress < 30 && "Loading core components..."}
            {progress >= 30 && progress < 60 && "Initializing modules..."}
            {progress >= 60 && progress < 90 && "Preparing workspace..."}
            {progress >= 90 && "Almost ready..."}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="flex h-screen bg-gray-900">
      <div className="dashboard-item" style={{ animationDelay: '0.2s' }}>
        <Sidebar />
      </div>
      <main className="flex-1 overflow-auto">
        <div className="container mx-auto px-6 py-8 dashboard-item" style={{ animationDelay: '0.4s' }}>
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default DashboardLayout;