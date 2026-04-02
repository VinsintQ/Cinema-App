package com.Cinema.App.service;

import com.Cinema.App.exception.InformationExistException;
import com.Cinema.App.exception.InformationNotFoundException;
import com.Cinema.App.model.Hall;
import com.Cinema.App.model.Seat;
import com.Cinema.App.repository.HallRepository;
import com.Cinema.App.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class HallService {

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private SeatRepository seatRepository;

    public List<Hall> getAllHalls() {
        return hallRepository.findAll();
    }

    public Hall getHallById(Long id) {
        return hallRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException("Hall not found with id: " + id));
    }

    public List<Seat> getSeatsForHall(Long hallId) {
        getHallById(hallId); // validates existence
        return seatRepository.findByHallId(hallId);
    }

    @Transactional
    public Hall createHall(Hall hall) {
        if (hallRepository.existsByNameIgnoreCase(hall.getName())) {
            throw new InformationExistException("Hall with name '" + hall.getName() + "' already exists");
        }
        Hall saved = hallRepository.save(hall);
        generateSeats(saved);
        return saved;
    }

    @Transactional
    public Hall updateHall(Long id, Hall updated) {
        Hall hall = getHallById(id);
        boolean seatsChanged = hall.getRows() != updated.getRows()
                || hall.getSeatsPerRow() != updated.getSeatsPerRow();

        hall.setName(updated.getName());
        hall.setRows(updated.getRows());
        hall.setSeatsPerRow(updated.getSeatsPerRow());
        Hall saved = hallRepository.save(hall);

        if (seatsChanged) {
            seatRepository.deleteByHallId(id);
            generateSeats(saved);
        }
        return saved;
    }

    @Transactional
    public void deleteHall(Long id) {
        Hall hall = getHallById(id);
        seatRepository.deleteByHallId(id);
        hallRepository.delete(hall);
    }

    private void generateSeats(Hall hall) {
        List<Seat> seats = new ArrayList<>();
        for (int row = 1; row <= hall.getRows(); row++) {
            for (int seat = 1; seat <= hall.getSeatsPerRow(); seat++) {
                seats.add(Seat.builder()
                        .hall(hall)
                        .rowNumber(row)
                        .seatNumber(seat)
                        .label(String.valueOf((char) ('A' + row - 1)) + seat)
                        .build());
            }
        }
        seatRepository.saveAll(seats);
    }
}
