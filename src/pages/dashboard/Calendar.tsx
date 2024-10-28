import React, { useState } from 'react';
import CalendarHeader from '../../components/calendar/CalendarHeader';
import CalendarGrid from '../../components/calendar/CalendarGrid';
import AppointmentModal from '../../components/calendar/AppointmentModal';
import useCalendar from '../../hooks/useCalendar';

const Calendar = () => {
  const {
    currentDate,
    days,
    addAppointment,
    nextMonth,
    prevMonth,
    today
  } = useCalendar();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);

  const handleDateSelect = (date: Date) => {
    setSelectedDate(date);
    setIsModalOpen(true);
  };

  return (
    <div className="space-y-6">
      <CalendarHeader
        currentDate={currentDate}
        onPrevMonth={prevMonth}
        onNextMonth={nextMonth}
        onToday={today}
      />

      <CalendarGrid
        days={days}
        onSelectDate={handleDateSelect}
      />

      {selectedDate && (
        <AppointmentModal
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          onSave={addAppointment}
          date={selectedDate}
        />
      )}
    </div>
  );
};

export default Calendar;