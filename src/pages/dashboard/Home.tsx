import React, { useState, useEffect } from 'react';
import Clock from 'react-clock';
import GaugeChart from 'react-gauge-chart';
import { Calendar, MessageSquare, Clock as ClockIcon, Activity, ListTodo, Rss } from 'lucide-react';
import { format } from 'date-fns';
import { useAuth } from '../../context/AuthContext';
import 'react-clock/dist/Clock.css';

const Home = () => {
  const { user } = useAuth();
  const [times, setTimes] = useState({
    berlin: new Date(),
    mexico: new Date(),
    vietnam: new Date(),
    newYork: new Date()
  });
  const [messages, setMessages] = useState([
    { id: 1, user: 'Sarah Miller', message: 'Team meeting at 3 PM', time: '10:30 AM' },
    { id: 2, user: 'John Davis', message: 'Project update completed', time: '11:15 AM' },
    { id: 3, user: 'Emma Wilson', message: 'New client presentation ready', time: '11:45 AM' }
  ]);
  const [tasks, setTasks] = useState([
    { id: 1, title: 'Review Q1 Reports', completed: false, priority: 'high' },
    { id: 2, title: 'Team Performance Reviews', completed: true, priority: 'medium' },
    { id: 3, title: 'Client Meeting Preparation', completed: false, priority: 'high' },
    { id: 4, title: 'Update Project Timeline', completed: false, priority: 'low' }
  ]);
  const [news, setNews] = useState([
    { id: 1, title: 'Tech Industry Shows Strong Growth in Q1', source: 'TechNews', time: '2h ago' },
    { id: 2, title: 'New AI Developments in Enterprise Software', source: 'AI Weekly', time: '3h ago' },
    { id: 3, title: 'Global Market Trends for 2024', source: 'Business Insider', time: '4h ago' }
  ]);

  useEffect(() => {
    const updateTimes = () => {
      const now = new Date();
      setTimes({
        berlin: new Date(now.toLocaleString('en-US', { timeZone: 'Europe/Berlin' })),
        mexico: new Date(now.toLocaleString('en-US', { timeZone: 'America/Mexico_City' })),
        vietnam: new Date(now.toLocaleString('en-US', { timeZone: 'Asia/Ho_Chi_Minh' })),
        newYork: new Date(now.toLocaleString('en-US', { timeZone: 'America/New_York' }))
      });
    };

    const interval = setInterval(updateTimes, 1000);
    return () => clearInterval(interval);
  }, []);

  const toggleTask = (id: number) => {
    setTasks(tasks.map(task => 
      task.id === id ? { ...task, completed: !task.completed } : task
    ));
  };

  return (
    <div className="space-y-6">
      {/* Welcome Section */}
      <div className="bg-gradient-to-r from-blue-600 to-purple-600 rounded-xl p-6 text-white">
        <h1 className="text-3xl font-bold mb-2">Welcome back, {user?.name}!</h1>
        <p className="text-blue-100">Today is {format(new Date(), 'EEEE, MMMM do, yyyy')}</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {/* World Clocks */}
        <div className="bg-gray-800 rounded-xl p-4 flex flex-col items-center">
          <div className="text-gray-400 mb-2">Berlin</div>
          <Clock value={times.berlin} size={100} />
        </div>
        <div className="bg-gray-800 rounded-xl p-4 flex flex-col items-center">
          <div className="text-gray-400 mb-2">Mexico City</div>
          <Clock value={times.mexico} size={100} />
        </div>
        <div className="bg-gray-800 rounded-xl p-4 flex flex-col items-center">
          <div className="text-gray-400 mb-2">Vietnam</div>
          <Clock value={times.vietnam} size={100} />
        </div>
        <div className="bg-gray-800 rounded-xl p-4 flex flex-col items-center">
          <div className="text-gray-400 mb-2">New York</div>
          <Clock value={times.newYork} size={100} />
        </div>
      </div>

      {/* System Status */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="bg-gray-800 rounded-xl p-6">
          <h2 className="text-xl font-semibold text-white mb-4 flex items-center">
            <Activity className="w-5 h-5 mr-2 text-green-500" />
            Europe Status
          </h2>
          <GaugeChart
            id="europe-gauge"
            nrOfLevels={20}
            percent={1.0}
            colors={['#FF5F6D', '#FFC371', '#2ECC71']}
          />
        </div>
        <div className="bg-gray-800 rounded-xl p-6">
          <h2 className="text-xl font-semibold text-white mb-4 flex items-center">
            <Activity className="w-5 h-5 mr-2 text-red-500" />
            USA Status
          </h2>
          <GaugeChart
            id="usa-gauge"
            nrOfLevels={20}
            percent={0.0}
            colors={['#FF5F6D', '#FFC371', '#2ECC71']}
          />
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Task Overview */}
        <div className="bg-gray-800 rounded-xl p-6">
          <h2 className="text-xl font-semibold text-white mb-4 flex items-center">
            <ListTodo className="w-5 h-5 mr-2 text-blue-500" />
            Task Overview
          </h2>
          <div className="space-y-3">
            {tasks.map(task => (
              <div
                key={task.id}
                className="flex items-center justify-between p-3 bg-gray-700/50 rounded-lg"
              >
                <div className="flex items-center">
                  <input
                    type="checkbox"
                    checked={task.completed}
                    onChange={() => toggleTask(task.id)}
                    className="w-4 h-4 mr-3 rounded border-gray-600"
                  />
                  <span className={`text-sm ${task.completed ? 'text-gray-400 line-through' : 'text-white'}`}>
                    {task.title}
                  </span>
                </div>
                <span className={`text-xs px-2 py-1 rounded ${
                  task.priority === 'high' ? 'bg-red-500/20 text-red-400' :
                  task.priority === 'medium' ? 'bg-yellow-500/20 text-yellow-400' :
                  'bg-green-500/20 text-green-400'
                }`}>
                  {task.priority}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* RSS Feeds */}
        <div className="bg-gray-800 rounded-xl p-6">
          <h2 className="text-xl font-semibold text-white mb-4 flex items-center">
            <Rss className="w-5 h-5 mr-2 text-orange-500" />
            Latest News
          </h2>
          <div className="space-y-4">
            {news.map(item => (
              <div key={item.id} className="border-b border-gray-700 pb-4 last:border-0">
                <h3 className="text-white text-sm font-medium mb-1">{item.title}</h3>
                <div className="flex items-center text-xs text-gray-400">
                  <span className="mr-2">{item.source}</span>
                  <span>•</span>
                  <span className="ml-2">{item.time}</span>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Chat Window */}
        <div className="bg-gray-800 rounded-xl p-6">
          <h2 className="text-xl font-semibold text-white mb-4 flex items-center">
            <MessageSquare className="w-5 h-5 mr-2 text-purple-500" />
            Team Chat
          </h2>
          <div className="space-y-4">
            {messages.map(message => (
              <div key={message.id} className="flex items-start space-x-3">
                <div className="w-8 h-8 rounded-full bg-gradient-to-r from-purple-500 to-blue-500 flex items-center justify-center text-white text-sm">
                  {message.user.charAt(0)}
                </div>
                <div className="flex-1">
                  <div className="flex items-baseline justify-between">
                    <h3 className="text-sm font-medium text-white">{message.user}</h3>
                    <span className="text-xs text-gray-400">{message.time}</span>
                  </div>
                  <p className="text-sm text-gray-300 mt-1">{message.message}</p>
                </div>
              </div>
            ))}
          </div>
          <div className="mt-4 flex">
            <input
              type="text"
              placeholder="Type your message..."
              className="flex-1 bg-gray-700 border border-gray-600 rounded-lg px-4 py-2 text-white focus:outline-none focus:border-blue-500"
            />
            <button className="ml-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
              Send
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;