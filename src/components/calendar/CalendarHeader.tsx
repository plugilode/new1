import React from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { format } from 'date-fns';

interface CalendarHeaderProps {
  currentDate: Date;
  onPrevMonth: () => void;
  onNextMonth: () => void;
  onToday: () => void;
}

const CalendarHeader: React.FC<CalendarHeaderProps> = ({
  currentDate,
  onPrevMonth,
  onNextMonth,
  onToday,
}) => {
  return (
    <div className="flex items-center justify-between mb-8">
      <div className="flex items-center space-x-4">
        <h1 className="text-3xl font-bold text-white">
          {format(currentDate, 'MMMM yyyy')}
        </h1>
        <button
          onClick={onToday}
          className="px-3 py-1 text-sm bg-gray-700 hover:bg-gray-600 text-white rounded-lg"
        >
          Today
        </button>
      </div>
      <div className="flex space-x-2">
        <button
          onClick={onPrevMonth}
          className="p-2 hover:bg-gray-700 rounded-lg text-gray-400 hover:text-white"
        >
          <ChevronLeft className="w-5 h-5" />
        </button>
        <button
          onClick={onNextMonth}
          className="p-2 hover:bg-gray-700 rounded-lg text-gray-400 hover:text-white"
        >
          <ChevronRight className="w-5 h-5" />
        </button>
      </div>
    </div>
  );
};

export default CalendarHeader;