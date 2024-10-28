import { useState, useEffect } from 'react';
import {
  startOfMonth,
  endOfMonth,
  startOfWeek,
  endOfWeek,
  eachDayOfInterval,
  isSameMonth,
  isToday,
  addMonths,
  subMonths
} from 'date-fns';
import { CalendarDay, Appointment } from '../types/calendar';

const STORAGE_KEY = 'calendar_appointments';

export const useCalendar = () => {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [appointments, setAppointments] = useState<Appointment[]>(() => {
    const saved = localStorage.getItem(STORAGE_KEY);
    return saved ? JSON.parse(saved) : [];
  });

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(appointments));
  }, [appointments]);

  const getDaysInMonth = (date: Date): CalendarDay[] => {
    const start = startOfWeek(startOfMonth(date));
    const end = endOfWeek(endOfMonth(date));

    return eachDayOfInterval({ start, end }).map(day => ({
      date: day,
      isCurrentMonth: isSameMonth(day, date),
      isToday: isToday(day),
      appointments: appointments.filter(apt => 
        new Date(apt.startTime).toDateString() === day.toDateString()
      )
    }));
  };

  const addAppointment = (appointment: Omit<Appointment, 'id'>) => {
    const newAppointment = {
      ...appointment,
      id: crypto.randomUUID()
    };
    setAppointments(prev => [...prev, newAppointment]);
  };

  const updateAppointment = (id: string, appointment: Omit<Appointment, 'id'>) => {
    setAppointments(prev =>
      prev.map(apt => (apt.id === id ? { ...appointment, id } : apt))
    );
  };

  const deleteAppointment = (id: string) => {
    setAppointments(prev => prev.filter(apt => apt.id !== id));
  };

  const nextMonth = () => setCurrentDate(prev => addMonths(prev, 1));
  const prevMonth = () => setCurrentDate(prev => subMonths(prev, 1));
  const today = () => setCurrentDate(new Date());

  return {
    currentDate,
    days: getDaysInMonth(currentDate),
    appointments,
    addAppointment,
    updateAppointment,
    deleteAppointment,
    nextMonth,
    prevMonth,
    today
  };
};

export default useCalendar;