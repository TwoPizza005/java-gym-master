package ru.yandex.practicum.gym;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class TimetableTest {

    // ---------- Тесты для getTrainingSessionsForDay ----------

    @Test
    void testGetTrainingSessionsForDaySingleSession() {
        Timetable timetable = new Timetable();

        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession session = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(session);

        TreeMap<TimeOfDay, List<TrainingSession>> monday = timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);
        Assertions.assertEquals(1, monday.size());
        Assertions.assertTrue(monday.containsKey(new TimeOfDay(13, 0)));
        Assertions.assertEquals(1, monday.get(new TimeOfDay(13, 0)).size());

        TreeMap<TimeOfDay, List<TrainingSession>> tuesday = timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY);
        Assertions.assertTrue(tuesday.isEmpty());
    }

    @Test
    void testGetTrainingSessionsForDayMultipleSessions() {
        Timetable timetable = new Timetable();

        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");

        Group groupAdult = new Group("Акробатика для взрослых", Age.ADULT, 90);
        TrainingSession thursdayAdult = new TrainingSession(groupAdult, coach,
                DayOfWeek.THURSDAY, new TimeOfDay(20, 0));

        Group groupChild = new Group("Акробатика для детей", Age.CHILD, 60);
        TrainingSession mondayChild = new TrainingSession(groupChild, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));
        TrainingSession thursdayChild1 = new TrainingSession(groupChild, coach,
                DayOfWeek.THURSDAY, new TimeOfDay(13, 0));
        TrainingSession thursdayChild2 = new TrainingSession(groupChild, coach,
                DayOfWeek.THURSDAY, new TimeOfDay(13, 0));
        TrainingSession saturdayChild = new TrainingSession(groupChild, coach,
                DayOfWeek.SATURDAY, new TimeOfDay(10, 0));

        timetable.addNewTrainingSession(thursdayAdult);
        timetable.addNewTrainingSession(mondayChild);
        timetable.addNewTrainingSession(thursdayChild1);
        timetable.addNewTrainingSession(thursdayChild2);
        timetable.addNewTrainingSession(saturdayChild);

        // Понедельник
        TreeMap<TimeOfDay, List<TrainingSession>> monday = timetable.getTrainingSessionsForDay(DayOfWeek.MONDAY);
        Assertions.assertEquals(1, monday.size());
        Assertions.assertEquals(1, monday.get(new TimeOfDay(13, 0)).size());

        // Четверг — 2 времени: 13:00 (2 занятия) и 20:00 (1 занятие)
        TreeMap<TimeOfDay, List<TrainingSession>> thursday = timetable.getTrainingSessionsForDay(DayOfWeek.THURSDAY);
        Assertions.assertEquals(2, thursday.size());
        Iterator<TimeOfDay> it = thursday.navigableKeySet().iterator();
        Assertions.assertEquals(new TimeOfDay(13, 0), it.next());
        Assertions.assertEquals(new TimeOfDay(20, 0), it.next());
        Assertions.assertEquals(2, thursday.get(new TimeOfDay(13, 0)).size());
        Assertions.assertEquals(1, thursday.get(new TimeOfDay(20, 0)).size());

        // Вторник
        Assertions.assertTrue(timetable.getTrainingSessionsForDay(DayOfWeek.TUESDAY).isEmpty());
    }

    // ---------- Тесты для getTrainingSessionsForDayAndTime ----------

    @Test
    void testGetTrainingSessionsForDayAndTime() {
        Timetable timetable = new Timetable();

        Group group = new Group("Акробатика для детей", Age.CHILD, 60);
        Coach coach = new Coach("Васильев", "Николай", "Сергеевич");
        TrainingSession session = new TrainingSession(group, coach,
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));

        timetable.addNewTrainingSession(session);

        List<TrainingSession> result = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(13, 0));
        Assertions.assertEquals(1, result.size());
        Assertions.assertSame(session, result.get(0));

        List<TrainingSession> empty = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.MONDAY, new TimeOfDay(14, 0));
        Assertions.assertTrue(empty.isEmpty());
    }

    @Test
    void testGetTrainingSessionsForDayAndTimeMultipleSessionsSameTime() {
        Timetable timetable = new Timetable();

        Coach coach = new Coach("Иванов", "Иван", "Иванович");
        Group group = new Group("Йога", Age.ADULT, 60);
        TrainingSession s1 = new TrainingSession(group, coach, DayOfWeek.WEDNESDAY, new TimeOfDay(10, 0));
        TrainingSession s2 = new TrainingSession(group, coach, DayOfWeek.WEDNESDAY, new TimeOfDay(10, 0));

        timetable.addNewTrainingSession(s1);
        timetable.addNewTrainingSession(s2);

        List<TrainingSession> result = timetable.getTrainingSessionsForDayAndTime(
                DayOfWeek.WEDNESDAY, new TimeOfDay(10, 0));
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(s1));
        Assertions.assertTrue(result.contains(s2));
    }

    // ---------- Тесты для getCountByCoaches (не менее трёх) ----------

    @Test
    void testGetCountByCoachesEmptySchedule() {
        Timetable timetable = new Timetable();
        List<Timetable.CounterOfTrainings> result = timetable.getCountByCoaches();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testGetCountByCoachesSingleCoachMultipleTrainings() {
        Timetable timetable = new Timetable();

        Coach coach = new Coach("Сидоров", "Петр", "Алексеевич");
        Group group = new Group("Акробатика", Age.CHILD, 60);

        for (int i = 0; i < 5; i++) {
            TrainingSession session = new TrainingSession(group, coach,
                    DayOfWeek.MONDAY, new TimeOfDay(10 + i, 0));
            timetable.addNewTrainingSession(session);
        }

        List<Timetable.CounterOfTrainings> result = timetable.getCountByCoaches();
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(5, result.get(0).getCount());
        Assertions.assertEquals(coach, result.get(0).getCoach());
    }

    @Test
    void testGetCountByCoachesMultipleCoachesSorted() {
        Timetable timetable = new Timetable();

        Coach coach1 = new Coach("Алексеев", "Алексей", "Алексеевич");
        Coach coach2 = new Coach("Борисов", "Борис", "Борисович");
        Coach coach3 = new Coach("Васильев", "Василий", "Васильевич");

        Group group = new Group("Фитнес", Age.ADULT, 45);

        for (int i = 0; i < 3; i++) {
            timetable.addNewTrainingSession(new TrainingSession(group, coach1,
                    DayOfWeek.MONDAY, new TimeOfDay(9 + i, 0)));
        }
        for (int i = 0; i < 5; i++) {
            timetable.addNewTrainingSession(new TrainingSession(group, coach2,
                    DayOfWeek.TUESDAY, new TimeOfDay(10 + i, 0)));
        }
        for (int i = 0; i < 2; i++) {
            timetable.addNewTrainingSession(new TrainingSession(group, coach3,
                    DayOfWeek.WEDNESDAY, new TimeOfDay(11 + i, 0)));
        }

        List<Timetable.CounterOfTrainings> result = timetable.getCountByCoaches();

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals(coach2, result.get(0).getCoach());
        Assertions.assertEquals(5, result.get(0).getCount());
        Assertions.assertEquals(coach1, result.get(1).getCoach());
        Assertions.assertEquals(3, result.get(1).getCount());
        Assertions.assertEquals(coach3, result.get(2).getCoach());
        Assertions.assertEquals(2, result.get(2).getCount());
    }

    @Test
    void testGetCountByCoachesSameCount() {
        Timetable timetable = new Timetable();

        Coach coachA = new Coach("Антонов", "Антон", "Антонович");
        Coach coachB = new Coach("Белов", "Борис", "Борисович");
        Group group = new Group("Йога", Age.ADULT, 60);

        timetable.addNewTrainingSession(new TrainingSession(group, coachA, DayOfWeek.MONDAY, new TimeOfDay(10, 0)));
        timetable.addNewTrainingSession(new TrainingSession(group, coachB, DayOfWeek.MONDAY, new TimeOfDay(11, 0)));

        List<Timetable.CounterOfTrainings> result = timetable.getCountByCoaches();
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.stream().anyMatch(c -> c.getCoach().equals(coachA)));
        Assertions.assertTrue(result.stream().anyMatch(c -> c.getCoach().equals(coachB)));
        Assertions.assertEquals(1, result.get(0).getCount());
        Assertions.assertEquals(1, result.get(1).getCount());
    }
}