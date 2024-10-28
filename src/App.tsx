import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginForm from './components/LoginForm';
import CircuitAnimation from './components/CircuitAnimation';
import DashboardLayout from './components/layout/DashboardLayout';
import { AuthProvider } from './context/AuthContext';

// Dashboard Pages
import Home from './pages/dashboard/Home';
import CRM from './pages/dashboard/CRM';
import Calendar from './pages/dashboard/Calendar';
import Email from './pages/dashboard/Email';
import Database from './pages/dashboard/Database';
import OfficeTools from './pages/dashboard/OfficeTools';
import AIAgents from './pages/dashboard/AIAgents';
import AITools from './pages/dashboard/AITools';
import SocialMedia from './pages/dashboard/SocialMedia';
import DatabaseSearch from './pages/dashboard/DatabaseSearch';
import WebSearch from './pages/dashboard/WebSearch';
import ControlPanel from './pages/dashboard/ControlPanel';
import AdminControl from './pages/dashboard/AdminControl';
import UserRights from './pages/dashboard/UserRights';
import NetworkStatus from './pages/dashboard/NetworkStatus';
import HR from './pages/dashboard/HR';
import TimeSchedule from './pages/dashboard/TimeSchedule';
import TimeClock from './pages/dashboard/TimeClock';
import Finances from './pages/dashboard/Finances';
import GoogleChat from './pages/dashboard/GoogleChat';
import GoogleMeet from './pages/dashboard/GoogleMeet';
import GoogleDrive from './pages/dashboard/GoogleDrive';
import GoogleSheet from './pages/dashboard/GoogleSheet';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/" element={
            <div className="min-h-screen w-full bg-black flex items-center justify-center p-4">
              <CircuitAnimation />
              <LoginForm />
            </div>
          } />
          
          <Route path="/dashboard" element={<DashboardLayout />}>
            <Route index element={<Home />} />
            <Route path="crm" element={<CRM />} />
            <Route path="calendar" element={<Calendar />} />
            <Route path="email" element={<Email />} />
            <Route path="database" element={<Database />} />
            <Route path="office" element={<OfficeTools />} />
            <Route path="ai-agents" element={<AIAgents />} />
            <Route path="ai-tools" element={<AITools />} />
            <Route path="social" element={<SocialMedia />} />
            <Route path="db-search" element={<DatabaseSearch />} />
            <Route path="web-search" element={<WebSearch />} />
            <Route path="control" element={<ControlPanel />} />
            <Route path="admin" element={<AdminControl />} />
            <Route path="rights" element={<UserRights />} />
            <Route path="network" element={<NetworkStatus />} />
            <Route path="hr" element={<HR />} />
            <Route path="schedule" element={<TimeSchedule />} />
            <Route path="time-clock" element={<TimeClock />} />
            <Route path="finances" element={<Finances />} />
            <Route path="gchat" element={<GoogleChat />} />
            <Route path="gmeet" element={<GoogleMeet />} />
            <Route path="gdrive" element={<GoogleDrive />} />
            <Route path="gsheet" element={<GoogleSheet />} />
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;