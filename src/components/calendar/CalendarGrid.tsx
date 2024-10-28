import React from 'react';
import { format } from 'date-fns';
import { CalendarDay } from '../../types/calendar';

interface CalendarGridProps {
  days: CalendarDay[];
  onSelectDate: (date: Date) => void;
}

const CalendarGrid: React.FC<CalendarGridProps> = ({ days, onSelectDate }) => {
  const weekDays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

  return (
    <div className="grid grid-cols-7 gap-px bg-gray-700 rounded-lg overflow-hidden">
      {weekDays.map(day => (
        <div
          key={day}
          className="bg-gray-800 p-4 text-center text-sm font-medium text-gray-400"
        >
          {day}
        </div>
      ))}
      
      {days.map((day, idx) => (
        <div
          key={idx}
          onClick={() => onSelectDate(day.date)}
          className={`min-h-[120px] p-2 ${
            day.isCurrentMonth ? 'bg-gray-900' : 'bg-gray-800/50'
          } ${day.isToday ? 'ring-2 ring-blue-500 ring-inset' : ''} cursor-pointer hover:bg-gray-800 transition-colors`}
        >
          <div className="flex justify-between items-start">
            <span className={`text-sm font-medium ${
              day.isCurrentMonth ? 'text-white' : 'text-gray-500'
            }`}>
              {format(day.date, 'd')}
            </span>
            {day.appointments.length > 0 && (
              <span className="text-xs text-blue-400 font-medium">
                {day.appointments.length} events
              </span>
            )}
          </div>
          
          <div className="mt-2 space-y-1">
            {day.appointments.slice(0, 3).map(apt => (
              <div
                key={apt.id}
                className="text-xs px-2 py-1 rounded truncate"
                style={{ backgroundColor: `${apt.color}20`, color: apt.color }}
              >
                {format(new Date(apt.startTime), 'HH:mm')} - {apt.title}
              </div>
            ))}
            {day.appointments.length > 3 && (
              <div className="text-xs text-gray-400">
                +{day.appointments.length - 3} more
              </div>
            )}
          </div>
        </div>
      ))}
    </div>
  );
};

export default CalendarGrid;