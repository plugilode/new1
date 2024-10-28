import { useState, useEffect } from 'react';

interface User {
  id: string;
  name: string;
  email: string;
  password: string;
  company?: string;
  createdAt: string;
}

interface DatabaseState {
  users: User[];
}

const DB_KEY = 'plugilo_db';

const useDatabase = () => {
  const [db, setDb] = useState<DatabaseState>({ users: [] });

  // Load database on mount
  useEffect(() => {
    const savedDb = localStorage.getItem(DB_KEY);
    if (savedDb) {
      setDb(JSON.parse(savedDb));
    }
  }, []);

  // Save database on changes
  useEffect(() => {
    localStorage.setItem(DB_KEY, JSON.stringify(db));
  }, [db]);

  const createUser = (userData: Omit<User, 'id' | 'createdAt'>) => {
    const newUser = {
      ...userData,
      id: crypto.randomUUID(),
      createdAt: new Date().toISOString(),
    };

    setDb(prev => ({
      ...prev,
      users: [...prev.users, newUser],
    }));
    return newUser;
  };

  const findUserByEmail = (email: string) => {
    return db.users.find(user => user.email === email);
  };

  const validateCredentials = (email: string, password: string) => {
    const user = findUserByEmail(email);
    if (!user) return null;
    return user.password === password ? user : null;
  };

  return {
    users: db.users,
    createUser,
    findUserByEmail,
    validateCredentials,
  };
};

export type { User };
export default useDatabase;