import React, { useState } from 'react';
import { CircuitBoard, Apple, Mail } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import SignupModal from './SignupModal';
import { useAuth } from '../context/AuthContext';

const LoginForm = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [isSignupOpen, setIsSignupOpen] = useState(false);
  const [loginData, setLoginData] = useState({
    email: '',
    password: '',
  });
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);
    
    const success = await login(loginData.email, loginData.password);
    if (success) {
      document.body.classList.add('login-transition');
      setTimeout(() => {
        navigate('/dashboard');
      }, 1000);
    } else {
      setError('Invalid email or password');
      setIsLoading(false);
    }
  };

  const handleSignupSuccess = () => {
    setIsSignupOpen(false);
  };

  return (
    <>
      <div className={`relative w-full max-w-md p-8 bg-black/80 backdrop-blur-sm rounded-2xl shadow-2xl transition-all duration-500 ${isLoading ? 'scale-105 brightness-200' : ''}`}>
        <div className="flex justify-center mb-8">
          <CircuitBoard className={`w-12 h-12 text-blue-500 transition-all duration-500 ${isLoading ? 'rotate-180 scale-150' : ''}`} />
        </div>
        
        <h2 className="text-3xl font-bold text-center text-white mb-2">Welcome Back</h2>
        <p className="text-gray-400 text-center mb-8">Sign in to your Plugilo AI account</p>
        
        {error && (
          <div className="mb-4 p-3 bg-red-500/10 border border-red-500/20 rounded-lg text-red-500 text-sm text-center">
            {error}
          </div>
        )}
        
        <form onSubmit={handleLogin} className="space-y-6">
          <div>
            <input
              type="email"
              placeholder="Email address"
              value={loginData.email}
              onChange={(e) => setLoginData({ ...loginData, email: e.target.value })}
              className="w-full px-4 py-3 bg-gray-900/50 border border-gray-700 rounded-lg focus:outline-none focus:border-blue-500 text-white"
              disabled={isLoading}
            />
          </div>
          <div>
            <input
              type="password"
              placeholder="Password"
              value={loginData.password}
              onChange={(e) => setLoginData({ ...loginData, password: e.target.value })}
              className="w-full px-4 py-3 bg-gray-900/50 border border-gray-700 rounded-lg focus:outline-none focus:border-blue-500 text-white"
              disabled={isLoading}
            />
          </div>
          <div className="flex items-center justify-between">
            <label className="flex items-center">
              <input type="checkbox" className="w-4 h-4 bg-gray-900 border-gray-700 rounded" disabled={isLoading} />
              <span className="ml-2 text-sm text-gray-400">Remember me</span>
            </label>
            <a href="#" className="text-sm text-blue-500 hover:text-blue-400">Forgot password?</a>
          </div>
          <button
            type="submit"
            disabled={isLoading}
            className={`w-full py-3 px-4 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-all duration-500 ${isLoading ? 'bg-white text-black scale-105' : ''}`}
          >
            {isLoading ? 'Initializing...' : 'Sign In'}
          </button>
        </form>
        
        <div className="relative my-8">
          <div className="absolute inset-0 flex items-center">
            <div className="w-full border-t border-gray-700"></div>
          </div>
          <div className="relative flex justify-center text-sm">
            <span className="px-2 bg-black/80 text-gray-400">Or continue with</span>
          </div>
        </div>
        
        <div className="grid grid-cols-2 gap-4">
          <button disabled={isLoading} className="flex items-center justify-center px-4 py-3 border border-gray-700 rounded-lg hover:bg-gray-900/50 transition-colors">
            <Mail className="w-5 h-5 text-white mr-2" />
            <span className="text-white">Google</span>
          </button>
          <button disabled={isLoading} className="flex items-center justify-center px-4 py-3 border border-gray-700 rounded-lg hover:bg-gray-900/50 transition-colors">
            <Apple className="w-5 h-5 text-white mr-2" />
            <span className="text-white">Apple</span>
          </button>
        </div>
        
        <p className="mt-8 text-center text-gray-400">
          Don't have an account?{' '}
          <button
            onClick={() => setIsSignupOpen(true)}
            disabled={isLoading}
            className="text-blue-500 hover:text-blue-400"
          >
            Sign up
          </button>
        </p>
      </div>

      <SignupModal
        isOpen={isSignupOpen}
        onClose={() => setIsSignupOpen(false)}
        onSignupSuccess={handleSignupSuccess}
      />
    </>
  );
};

export default LoginForm;