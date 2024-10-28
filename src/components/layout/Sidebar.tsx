import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';
import {
  Home,
  Users,
  Calendar,
  Mail,
  Database,
  Briefcase,
  Bot,
  Wand2,
  Share2,
  Search,
  Globe,
  Settings,
  Shield,
  UserCog,
  Activity,
  ClipboardList,
  Clock,
  DollarSign,
  MessageSquare,
  Video,
  HardDrive,
  FileSpreadsheet,
  User,
  Sun,
  Moon,
  ChevronDown,
  ChevronRight,
} from 'lucide-react';
import { useAuth } from '../../context/AuthContext';

interface NavItem {
  icon: any;
  label: string;
  to?: string;
  children?: Omit<NavItem, 'children'>[];
}

const MenuItem = ({ icon: Icon, label, to, isChild = false }: { icon: any; label: string; to?: string; isChild?: boolean }) => {
  if (!to) return null;
  
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        `flex items-center space-x-3 px-4 py-3 rounded-lg transition-colors ${
          isChild ? 'ml-9' : ''
        } ${
          isActive
            ? 'bg-blue-600/10 text-blue-500'
            : 'text-gray-400 hover:bg-gray-800/50 hover:text-white'
        }`
      }
    >
      <Icon className="w-5 h-5" />
      <span>{label}</span>
    </NavLink>
  );
};

const MenuSection = ({ item, isExpanded, onToggle }: { 
  item: NavItem; 
  isExpanded: boolean;
  onToggle: () => void;
}) => {
  const hasChildren = item.children && item.children.length > 0;
  
  if (!hasChildren) {
    return <MenuItem {...item} />;
  }

  return (
    <div>
      <button
        onClick={onToggle}
        className={`w-full flex items-center justify-between px-4 py-3 rounded-lg transition-colors ${
          isExpanded ? 'bg-gray-800/50 text-white' : 'text-gray-400 hover:bg-gray-800/50 hover:text-white'
        }`}
      >
        <div className="flex items-center space-x-3">
          <item.icon className="w-5 h-5" />
          <span>{item.label}</span>
        </div>
        {isExpanded ? (
          <ChevronDown className="w-4 h-4" />
        ) : (
          <ChevronRight className="w-4 h-4" />
        )}
      </button>
      
      {isExpanded && item.children && (
        <div className="mt-1 space-y-1">
          {item.children.map((child, idx) => (
            <MenuItem key={idx} {...child} isChild />
          ))}
        </div>
      )}
    </div>
  );
};

const navigation: NavItem[] = [
  {
    icon: Home,
    label: 'Dashboard',
    to: '/dashboard'
  },
  {
    icon: Users,
    label: 'Customer Relations',
    children: [
      { icon: Users, label: 'CRM', to: '/dashboard/crm' },
      { icon: Calendar, label: 'Calendar', to: '/dashboard/calendar' },
      { icon: Mail, label: 'Email', to: '/dashboard/email' },
    ]
  },
  {
    icon: Database,
    label: 'Data Management',
    children: [
      { icon: Database, label: 'Database', to: '/dashboard/database' },
      { icon: Search, label: 'Database Search', to: '/dashboard/db-search' },
      { icon: Globe, label: 'Web Search', to: '/dashboard/web-search' },
    ]
  },
  {
    icon: Bot,
    label: 'AI & Automation',
    children: [
      { icon: Bot, label: 'AI Agents', to: '/dashboard/ai-agents' },
      { icon: Wand2, label: 'AI Tools', to: '/dashboard/ai-tools' },
      { icon: Share2, label: 'Social Media', to: '/dashboard/social' },
    ]
  },
  {
    icon: Settings,
    label: 'Administration',
    children: [
      { icon: Settings, label: 'Control Panel', to: '/dashboard/control' },
      { icon: Shield, label: 'Admin Control', to: '/dashboard/admin' },
      { icon: UserCog, label: 'User Rights', to: '/dashboard/rights' },
      { icon: Activity, label: 'Network Status', to: '/dashboard/network' },
    ]
  },
  {
    icon: ClipboardList,
    label: 'HR & Finance',
    children: [
      { icon: ClipboardList, label: 'HR Tools', to: '/dashboard/hr' },
      { icon: Clock, label: 'Time Schedule', to: '/dashboard/schedule' },
      { icon: Clock, label: 'Time Clock', to: '/dashboard/time-clock' },
      { icon: DollarSign, label: 'Finances', to: '/dashboard/finances' },
    ]
  },
  {
    icon: Briefcase,
    label: 'Workspace',
    children: [
      { icon: MessageSquare, label: 'Google Chat', to: '/dashboard/gchat' },
      { icon: Video, label: 'Google Meet', to: '/dashboard/gmeet' },
      { icon: HardDrive, label: 'Google Drive', to: '/dashboard/gdrive' },
      { icon: FileSpreadsheet, label: 'Google Sheet', to: '/dashboard/gsheet' },
    ]
  },
];

const Sidebar = () => {
  const { user, logout } = useAuth();
  const [isDarkMode, setIsDarkMode] = useState(true);
  const [expandedSections, setExpandedSections] = useState<string[]>([]);

  const toggleSection = (label: string) => {
    setExpandedSections(prev => 
      prev.includes(label)
        ? prev.filter(item => item !== label)
        : [...prev, label]
    );
  };

  return (
    <aside className="w-64 h-screen bg-gray-900 border-r border-gray-800 flex flex-col">
      <div className="p-4">
        <h1 className="text-2xl font-bold bg-gradient-to-r from-blue-500 to-purple-500 bg-clip-text text-transparent">
          Plugilo AI
        </h1>
      </div>

      <div className="flex-1 overflow-y-auto px-2 py-4 space-y-2">
        {navigation.map((item, idx) => (
          <MenuSection
            key={idx}
            item={item}
            isExpanded={expandedSections.includes(item.label)}
            onToggle={() => toggleSection(item.label)}
          />
        ))}
      </div>

      <div className="border-t border-gray-800 p-4">
        <div className="flex items-center justify-between mb-4">
          <button
            onClick={() => setIsDarkMode(!isDarkMode)}
            className="p-2 rounded-lg hover:bg-gray-800 text-gray-400 hover:text-white transition-colors"
          >
            {isDarkMode ? <Sun className="w-5 h-5" /> : <Moon className="w-5 h-5" />}
          </button>
        </div>

        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 rounded-full bg-gray-700 flex items-center justify-center">
            <User className="w-6 h-6 text-gray-300" />
          </div>
          <div className="flex-1">
            <p className="text-sm font-medium text-white">{user?.name}</p>
            <button
              onClick={logout}
              className="text-sm text-gray-400 hover:text-white transition-colors"
            >
              Sign out
            </button>
          </div>
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;